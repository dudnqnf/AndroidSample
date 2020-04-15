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

public class WriteActivity extends AppCompatActivity {

    Button btn_write_confirm;
    EditText edt_content;
    EditText edit_title;

    Thread mThread = null;
    JSONObject obj;

    String title;
    String board_type;
    String content;
    int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        Intent intent = getIntent();
        board_type = intent.getStringExtra("board_type");

        btn_write_confirm = findViewById(R.id.btn_write_confirm);
        edit_title = findViewById(R.id.edit_title);
        edt_content = findViewById(R.id.edt_content);
        btn_write_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = edit_title.getText().toString();
                content = edt_content.getText().toString();
                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                user_id = pref.getInt("user_id", 1);

                mThread = new Thread(new customRunnable(URL.INSERT_BOARD));
                mThread.start();
            }
        });
    }

    public void insert_board(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody req_body = new FormBody.Builder()
                .add("title", title)
                .add("type", board_type)
                .add("content", content)
                .add("user_id", ""+user_id)
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
            handler.sendEmptyMessage(result);
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
                    Toast.makeText(WriteActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 1:
                    Toast.makeText(WriteActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private class customRunnable implements Runnable {
        private String url;
        public customRunnable(String url){
            super();
            this.url=url;
        }
        @Override
        public void run() {
            try {
                insert_board(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
