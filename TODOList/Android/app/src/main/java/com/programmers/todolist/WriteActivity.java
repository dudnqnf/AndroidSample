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
import android.widget.ListView;
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

public class WriteActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText write_edit_title, write_edit_date, write_edit_content;
    private Spinner write_spinner_priority;
    private Button btn_back, write_btn_confirm;
    private JSONObject response_object;
    private Thread mThread = null;
    private DatePickerDialog.OnDateSetListener callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        btn_back = findViewById(R.id.write_btn_back);
        btn_back.setOnClickListener(this);

        write_edit_title = findViewById(R.id.write_edit_title);
        write_edit_date = findViewById(R.id.write_edit_date);
        write_edit_content = findViewById(R.id.write_edit_content);
        write_btn_confirm = findViewById(R.id.write_btn_confirm);
        write_spinner_priority = findViewById(R.id.write_spinner_priority);

        write_edit_date.setOnClickListener(this);

        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(1);
        arrayList.add(2);
        arrayList.add(3);
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item, arrayList);
        write_spinner_priority.setAdapter(arrayAdapter);

        write_btn_confirm.setOnClickListener(this);
    }

    public void writeTodoList() {
        try {
            OkHttpClient client = new OkHttpClient();

            RequestBody req_body = new FormBody.Builder()
                    .add("title", write_edit_title.getText().toString())
                    .add("deadline", write_edit_date.getText().toString())
                    .add("priority", write_spinner_priority.getSelectedItem().toString())
                    .add("content", write_edit_content.getText().toString())
                    .build();

            Request request = new Request.Builder()
                    .url(URL.INSERT_TODO_LIST)
                    .post(req_body)
                    .build();

            Response response = client.newCall(request).execute();
            String response_string = response.body().string();

            response_object = new JSONObject(response_string);
            handler.sendEmptyMessage(response_object.getInt("result"));
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
                    Toast.makeText(WriteActivity.this, "success", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                default:
                    Toast.makeText(WriteActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.write_btn_back:
                finish();
                break;
            case R.id.write_edit_date:
                callback = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        write_edit_date.setText(date);
                    }
                };

                DatePickerDialog dialog = new DatePickerDialog(WriteActivity.this, callback, 2019, 8, 5);
                dialog.show();
                break;
            case R.id.write_btn_confirm:
                if(write_edit_title.getText().toString().equals("") 
                        || write_edit_date.getText().toString().equals("")
                        || write_edit_content.getText().toString().equals("")){
                    Toast.makeText(this, "Fill it all up", Toast.LENGTH_SHORT).show();
                    break;
                }
                    

                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        writeTodoList();
                    }
                });
                mThread.start();
                break;
        }
    }
}
