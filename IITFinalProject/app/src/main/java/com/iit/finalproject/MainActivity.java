package com.iit.finalproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.iit.finalproject.Utility.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    SharedPreferences pref;

    ListView listview ;
    ListViewAdapter adapter;

    Thread mThread = null;
    JSONObject obj;

    Button btn_bulletin;
    Button btn_notice;
    Button btn_club;
    Button btn_dormitory;
    Button btn_cultural;
    Button btn_write;
    Button btn_logout;

    String board_type = "bulletin";
    TextView tv_actionbar_title;

    Button btn_menu;
    DrawerLayout drawer_layout;

    int user_id;
    int user_position;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new ListViewAdapter();
        listview = findViewById(R.id.listview1);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                String post_id = adapter.getItem(pos).getId();
                Intent intent = new Intent(MainActivity.this, PostActivity.class);
                intent.putExtra("post_id", post_id);
                startActivity(intent);
            }
        });

        btn_bulletin = findViewById(R.id.btn_bulletin);
        btn_notice = findViewById(R.id.btn_notice);
        btn_club = findViewById(R.id.btn_club);
        btn_dormitory = findViewById(R.id.btn_dormitory);
        btn_cultural = findViewById(R.id.btn_cultural);
        btn_write = findViewById(R.id.btn_write);
        btn_logout = findViewById(R.id.btn_logout);
        tv_actionbar_title = findViewById(R.id.tv_actionbar_title);
        btn_menu = findViewById(R.id.btn_menu);
        drawer_layout = findViewById(R.id.drawer_layout);

        btn_bulletin.setOnClickListener(this);
        btn_notice.setOnClickListener(this);
        btn_club.setOnClickListener(this);
        btn_dormitory.setOnClickListener(this);
        btn_cultural.setOnClickListener(this);
        btn_write.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        btn_menu.setOnClickListener(this);
    }

    public void get_board(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody req_body = new FormBody.Builder().build();

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
                    ArrayList<ListViewItem> ItemList = new ArrayList<>();
                    try {
                        JSONArray data = obj.getJSONArray("data");
                        for(int i=0;i<data.length();i++){
                            JSONObject temp = (JSONObject)data.get(i);
                            ItemList.add(new ListViewItem(temp.getString("id"), temp.getString("title"), temp.getString("author"), temp.getString("time")));
                        }
                        adapter.clearList();
                        adapter.addList(ItemList);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId()){
            case R.id.btn_bulletin:
                tv_actionbar_title.setText("BULLETIN BOARD");
                board_type = "bulletin";
                mThread = new Thread(new customRunnable(URL.GET_BULLETIN_BOARD));
                mThread.start();
                break;
            case R.id.btn_notice:
                tv_actionbar_title.setText("NOTICE BOARD");
                board_type = "notice";
                mThread = new Thread(new customRunnable(URL.GET_NOTICE_BOARD));
                mThread.start();
                break;
            case R.id.btn_club:
                tv_actionbar_title.setText("STUDENT CLUB BOARD");
                board_type = "club";
                mThread = new Thread(new customRunnable(URL.GET_CLUB_BOARD));
                mThread.start();
                break;
            case R.id.btn_dormitory:
                tv_actionbar_title.setText("DORMITORY BOARD");
                board_type = "dormitory";
                mThread = new Thread(new customRunnable(URL.GET_DORMITORY_BOARD));
                mThread.start();
                break;
            case R.id.btn_cultural:
                tv_actionbar_title.setText("CULTURAL LIFE BOARD");
                board_type = "cultural";
                mThread = new Thread(new customRunnable(URL.GET_CULTURAL_BOARD));
                mThread.start();
                break;
            case R.id.btn_write:
                pref = getSharedPreferences("pref", MODE_PRIVATE);
                int user_position = pref.getInt("user_position", 0);
                if(board_type=="notice" && user_position == 0){
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                } else {
                    intent = new Intent(this, WriteActivity.class);
                    intent.putExtra("board_type", board_type);
                    startActivity(intent);
                }
                break;
            case R.id.btn_logout:
                pref = getSharedPreferences("pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("login", false);
                editor.commit();
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_menu:
                drawer_layout.openDrawer(Gravity.LEFT);
                break;
            default:
                break;
        }
    }

    private class customRunnable implements Runnable {
        private String url;
        public customRunnable(String url){
            super();
            this.url=url;
        }
        @Override
        public void run() {
            try {
                get_board(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshView();
    }

    public void refreshView(){
        mThread = new Thread(new customRunnable(URL.MAIN_SERVER_URL + "/select/" + board_type));
        mThread.start();
    }
}
