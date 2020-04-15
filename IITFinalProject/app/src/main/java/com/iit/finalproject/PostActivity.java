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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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

public class PostActivity extends AppCompatActivity {

    Thread mThread = null;
    JSONObject obj, obj2;

    String post_id;
    int user_id;

    TextView tv_title;
    TextView tv_author;
    TextView tv_date;
    TextView tv_content;
    Button btn_post_del;
    Button btn_post_upd;

    ListView listview ;
    CommentListViewAdapter adapter;

    Button btn_comment_submit;
    EditText edt_comment_content;

    String comment_content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Intent intent = getIntent();
        post_id = intent.getStringExtra("post_id");

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        user_id = pref.getInt("user_id", 0);

        tv_title = findViewById(R.id.tv_title);
        tv_author = findViewById(R.id.tv_author);
        tv_date = findViewById(R.id.tv_date);
        tv_content = findViewById(R.id.tv_content);

        btn_post_del = findViewById(R.id.btn_post_del);
        btn_post_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            delete_post(URL.DELETE_POST);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                mThread.start();
            }
        });

        btn_post_upd = findViewById(R.id.btn_post_upd);
        btn_post_upd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(PostActivity.this, UpdatePostActivity.class);
            intent.putExtra("title", tv_title.getText().toString());
            intent.putExtra("content", tv_content.getText().toString());
            intent.putExtra("post_id", post_id);
            startActivity(intent);
            }
        });

        adapter = new CommentListViewAdapter();
        adapter.setUserId(user_id);
        listview = findViewById(R.id.comment_listview);
        listview.setAdapter(adapter);

        btn_comment_submit = findViewById(R.id.btn_comment_submit);
        edt_comment_content = findViewById(R.id.edt_comment_content);
        btn_comment_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            insert_comment(URL.INSERT_COMMENT);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                mThread.start();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh_view();
    }

    public void refresh_view(){
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    get_board(URL.GET_POST);
                    get_comment(URL.GET_COMMENT);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        mThread.start();
    }

    public void get_board(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody req_body = new FormBody.Builder()
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

    public void delete_post(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody req_body = new FormBody.Builder()
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
                handler.sendEmptyMessage(2);
            else
                handler.sendEmptyMessage(3);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void get_comment(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody req_body = new FormBody.Builder()
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
            obj2 = new JSONObject(res);
            int result = obj2.getInt("result");
            if(result == 0)
                handler.sendEmptyMessage(4);
            else
                handler.sendEmptyMessage(5);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void insert_comment(String url) throws IOException {

        comment_content = edt_comment_content.getText().toString();

        OkHttpClient client = new OkHttpClient();
        RequestBody req_body = new FormBody.Builder()
                .add("board_id", post_id)
                .add("user_id", ""+user_id)
                .add("content", comment_content)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(req_body)
                .build();

        Response response = client.newCall(request).execute();
        String res = response.body().string();
        Log.i("TAG", res);
        try {
            obj2 = new JSONObject(res);
            int result = obj2.getInt("result");
            if(result == 0)
                handler.sendEmptyMessage(6);
            else
                handler.sendEmptyMessage(7);
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
                        JSONArray data = obj.getJSONArray("data");
                        JSONObject data_obj = data.getJSONObject(0);

                        tv_title.setText(data_obj.getString("title"));
                        tv_author.setText(data_obj.getString("name"));
                        tv_date.setText(data_obj.getString("date"));
                        tv_content.setText(data_obj.getString("content"));

                        if(user_id != data_obj.getInt("user_id")){
                            btn_post_del.setVisibility(View.INVISIBLE);
                            btn_post_upd.setVisibility(View.INVISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    Toast.makeText(PostActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(PostActivity.this, "DELETED", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 3:
                    Toast.makeText(PostActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                case 4:
                    ArrayList<CommentListViewItem> CommentItemList = new ArrayList<>();
                    try {
                        JSONArray data = obj2.getJSONArray("data");
                        for(int i=0;i<data.length();i++){
                            JSONObject temp = (JSONObject)data.get(i);
                            CommentItemList.add(new CommentListViewItem(temp.getInt("id"), temp.getString("content"), temp.getString("name"), temp.getInt("user_id"), temp.getString("date")));
                        }
                        adapter.clearList();
                        adapter.addList(CommentItemList);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 5:
                    Toast.makeText(PostActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                    break;
                case 6:
                    Toast.makeText(PostActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    JSONArray data2 = null;
                    try {
                        data2 = obj2.getJSONArray("data");
                        JSONObject data_obj = data2.getJSONObject(0);

                        int comment_id = data_obj.getInt("id");
                        String content = data_obj.getString("content");
                        String user_name = data_obj.getString("name");
                        String date = data_obj.getString("date");
                        int user_id = data_obj.getInt("user_id");

                        edt_comment_content.setText("");

                        adapter.addItem(new CommentListViewItem(comment_id, content, user_name, user_id, date));
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 7:
                    Toast.makeText(PostActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
}
