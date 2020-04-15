package com.programmers.todolist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.programmers.todolist.util.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReadActivity extends AppCompatActivity implements View.OnClickListener {

    private JSONObject response_object;
    private Thread mThread = null;
    private int id;
    private TextView read_tv_title, read_tv_deadline, read_tv_priority, read_tv_content;
    private Button btn_back, read_btn_check, read_btn_edit, read_btn_delete;
    private int status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        btn_back = findViewById(R.id.read_btn_back);
        btn_back.setOnClickListener(this);

        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);

        read_tv_title = findViewById(R.id.read_tv_title);
        read_tv_deadline = findViewById(R.id.read_tv_deadline);
        read_tv_priority = findViewById(R.id.read_tv_priority);
        read_tv_content = findViewById(R.id.read_tv_content);

        read_btn_check = findViewById(R.id.read_btn_check);
        read_btn_edit = findViewById(R.id.read_btn_edit);
        read_btn_delete = findViewById(R.id.read_btn_delete);

        read_btn_check.setOnClickListener(this);
        read_btn_edit.setOnClickListener(this);
        read_btn_delete.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        if(id != -1){
            mThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    getTodoList();
                }
            });
            mThread.start();
        }

        super.onResume();
    }

    public void getTodoList() {
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody req_body = new FormBody.Builder()
                    .add("id", "" + id)
                    .build();

            Request request = new Request.Builder()
                    .url(URL.GET_TODO_LIST)
                    .post(req_body)
                    .build();

            Response response = client.newCall(request).execute();
            String response_string = response.body().string();

            response_object = new JSONObject(response_string);
            if(response_object.getInt("result") == 0)
                handler.sendEmptyMessage(0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateCheck() {
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody req_body = new FormBody.Builder()
                    .add("id", "" + id)
                    .add("status", ""+status)
                    .build();

            Request request = new Request.Builder()
                    .url(URL.UPDATE_CHECK)
                    .post(req_body)
                    .build();

            Response response = client.newCall(request).execute();
            String response_string = response.body().string();

            response_object = new JSONObject(response_string);
            if(response_object.getInt("result") == 0)
                handler.sendEmptyMessage(1);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void deleteTodoList() {
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody req_body = new FormBody.Builder()
                    .add("id", "" + id)
                    .build();

            Request request = new Request.Builder()
                    .url(URL.DELETE_TODO_LIST)
                    .post(req_body)
                    .build();

            Response response = client.newCall(request).execute();
            String response_string = response.body().string();

            response_object = new JSONObject(response_string);
            if(response_object.getInt("result") == 0)
                handler.sendEmptyMessage(2);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case 0:
                    try {
                        JSONArray response_arr = response_object.getJSONArray("data");
                        JSONObject data = response_arr.getJSONObject(0);
                        int id = data.getInt("id");
                        String title = data.getString("title");
                        String deadline = data.getString("deadline");
                        String priority = data.getString("priority");
                        String content = data.getString("content");
                        status = data.getInt("status");

                        read_tv_title.setText(title);
                        read_tv_deadline.setText(deadline.substring(0, 10));
                        read_tv_priority.setText(priority);
                        read_tv_content.setText(content);
                        if(status == 1)
                            read_btn_check.setBackgroundResource(R.drawable.ic_checked);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    break;
                case 2:
                    finish();
                    break;
                default:
                    Toast.makeText(ReadActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.read_btn_back:
                finish();
                break;
            case R.id.read_btn_check:
                // toggle
                if(status == 0){
                    read_btn_check.setBackgroundResource(R.drawable.ic_checked);
                    status = 1;
                }
                else{
                    read_btn_check.setBackgroundResource(R.drawable.ic_unchecked);
                    status = 0;
                }
                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        updateCheck();
                    }
                });
                mThread.start();

                break;
            case R.id.read_btn_edit:
                Intent intent = new Intent(this, EditActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
                break;
            case R.id.read_btn_delete:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder
                        .setTitle("Delete")
                        .setMessage("Do you really want to delete this?")
                        .setCancelable(true)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mThread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        deleteTodoList();
                                    }
                                });
                                mThread.start();
                            }
                        })
                        .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;
        }
    }
}
