package com.iit.finalproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.iit.finalproject.Utility.URL;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpdatePostActivity extends AppCompatActivity {

    Button btn_write_confirm;
    EditText edt_content;
    EditText edit_title;

    Thread mThread = null;
    JSONObject obj;

    String title;
    String content;
    String post_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_post);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        content = intent.getStringExtra("content");
        post_id = intent.getStringExtra("post_id");

        btn_write_confirm = findViewById(R.id.btn_update_confirm);
        edit_title = findViewById(R.id.edit_update_title);
        edit_title.setText(title);
        edt_content = findViewById(R.id.edt_update_content);
        edt_content.setText(content);

        btn_write_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            title = edit_title.getText().toString();
            content = edt_content.getText().toString();

            mThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        update_post(URL.UPDATE_POST);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            mThread.start();
            }
        });
    }

    public void update_post(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody req_body = new FormBody.Builder()
                .add("title", title)
                .add("content", content)
                .add("post_id", post_id)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(req_body)
                .build();

        Response response = client.newCall(request).execute();
        String res = response.body().string();
        Log.i("TAG", res);
        try {
            obj = new JSONObject(res);
            int result = obj.getInt("result");
            if(result == 0)
                handler.sendEmptyMessage(0);
            else
                handler.sendEmptyMessage(1);
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
                    Toast.makeText(UpdatePostActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 1:
                    Toast.makeText(UpdatePostActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
