package com.programmers.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.programmers.todolist.util.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {

    private JSONObject response_object;
    private Thread mThread = null;
    private int id;
    private EditText edit_title, edit_deadline, edit_content;
    private Spinner edit_spinner_priority;
    private Button btn_confirm, btn_back;
    private DatePickerDialog.OnDateSetListener callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        btn_back = findViewById(R.id.edit_btn_back);
        btn_back.setOnClickListener(this);

        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);

        // Get data from server
        if(id != -1){
            mThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    getTodoList();
                }
            });
            mThread.start();
        }

        edit_title = findViewById(R.id.edit_edit_title);
        edit_deadline = findViewById(R.id.edit_edit_deadline);
        edit_spinner_priority = findViewById(R.id.edit_spinner_priority);
        edit_content = findViewById(R.id.edit_edit_content);
        btn_confirm = findViewById(R.id.edit_btn_confirm);

        edit_deadline.setOnClickListener(this);

        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(1);
        arrayList.add(2);
        arrayList.add(3);
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item, arrayList);
        edit_spinner_priority.setAdapter(arrayAdapter);

        btn_confirm.setOnClickListener(this);
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

    public void updateTodoList() {
        try {
            OkHttpClient client = new OkHttpClient();

            RequestBody req_body = new FormBody.Builder()
                    .add("id", ""+id)
                    .add("title", edit_title.getText().toString())
                    .add("deadline", edit_deadline.getText().toString())
                    .add("priority", edit_spinner_priority.getSelectedItem().toString())
                    .add("content", edit_content.getText().toString())
                    .build();

            Request request = new Request.Builder()
                    .url(URL.UPDATE_TODO_LIST)
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
                        int priority = data.getInt("priority");
                        String content = data.getString("content");

                        edit_title.setText(title);
                        edit_deadline.setText(deadline.substring(0, 10));
                        edit_spinner_priority.setSelection(priority - 1);
                        edit_content.setText(content);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case 1:
                    finish();
                    break;
                default:
                    Toast.makeText(EditActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_btn_back:
                finish();
                break;
            case R.id.edit_edit_deadline:
                callback = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        edit_deadline.setText(date);
                    }
                };

                DatePickerDialog dialog = new DatePickerDialog(EditActivity.this, callback, 2019, 8, 5);
                dialog.show();
                break;
            case R.id.edit_btn_confirm:
                if(edit_title.getText().toString().equals("")
                        || edit_deadline.getText().toString().equals("")
                        || edit_content.getText().toString().equals("")){
                    Toast.makeText(this, "Fill it all up", Toast.LENGTH_SHORT).show();
                    break;
                }

                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        updateTodoList();
                    }
                });
                mThread.start();
                break;
        }
    }
}
