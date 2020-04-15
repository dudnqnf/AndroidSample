package com.programmers.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.programmers.todolist.util.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_add, btn_notification;
    private Thread mThread;
    private JSONObject response_object;
    private ListView list_view;
    private ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);

        // Todo list
        adapter = new ListViewAdapter();
        list_view = findViewById(R.id.list_view);
        list_view.setAdapter(adapter);
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, ReadActivity.class);
                ListViewItem item = adapter.getItem(i);
                intent.putExtra("id", item.getId());
                startActivity(intent);
            }
        });

        // Notification status
        SharedPreferences preference = getSharedPreferences("preference", MODE_PRIVATE);
        Boolean notification_flag = preference.getBoolean("notification", false);
        btn_notification = findViewById(R.id.btn_notification);
        btn_notification.setOnClickListener(this);
        if (notification_flag)
            btn_notification.setBackgroundResource(R.drawable.ic_bell);
    }

    @Override
    protected void onResume() {
        // Get all todo_list from the server
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                getAllTodoList();
            }
        });
        mThread.start();
        super.onResume();
    }

    public void getAllTodoList() {
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody req_body = new FormBody.Builder()
                    .build();

            Request request = new Request.Builder()
                    .url(URL.GET_ALL_TODO_LIST)
                    .post(req_body)
                    .build();

            Response response = client.newCall(request).execute();
            String response_string = response.body().string();

            // get the result and send it to the handler
            response_object = new JSONObject(response_string);
            if(response_object.getInt("result") == 0)
                handler.sendEmptyMessage(0);
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

                    // ArrayList for notification
                    ArrayList<String> title_array = new ArrayList<>();

                    // Make list
                    try {
                        adapter.clearList();
                        JSONArray response_arr = response_object.getJSONArray("data");
                        for (int i = 0; i < response_arr.length(); i++) {
                            JSONObject data = response_arr.getJSONObject(i);
                            int id = data.getInt("id");
                            String title = data.getString("title");
                            int priority = data.getInt("priority");
                            int status = data.getInt("status");
                            String deadline = data.getString("deadline");

                            adapter.addItem(id, title, priority, status, deadline.substring(0, 10));

                            // notification
                            try {
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                String now = format.format(System.currentTimeMillis());
                                Date selected_date = format.parse(deadline);
                                Date now_date = format.parse(now);
                                long diff = selected_date.getTime() - now_date.getTime();

                                // If you missed the deadline and you haven't completed it yet
                                if (status == 0 && diff < 0)
                                    title_array.add(title);
                            } catch (ParseException e) {
                                Log.e("ERROR", e.toString());
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Notification
                    SharedPreferences preference = getSharedPreferences("preference", MODE_PRIVATE);
                    Boolean notification_flag = preference.getBoolean("notification", false);
                    if (notification_flag)
                        notification(title_array);

                    break;
                default:
                    Toast.makeText(MainActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public void notification(ArrayList<String> titles) {
        String text = "";
        for (int i = 0; i < titles.size(); i++) {
            text = text.concat(titles.get(i));
            if (i != titles.size() - 1)
                text += ", ";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("OUT OF DATE");
        builder.setContentText(text);

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }

        notificationManager.notify(1, builder.build());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                Intent intent = new Intent(this, WriteActivity.class);
                startActivity(intent);
                break;
            case R.id.list_view:
                break;
            case R.id.btn_notification:
                // notification toggle
                SharedPreferences preference = getSharedPreferences("preference", MODE_PRIVATE);
                Boolean notification_flag = preference.getBoolean("notification", false);
                SharedPreferences.Editor editor = preference.edit();
                if (notification_flag) {
                    // off
                    editor.putBoolean("notification", false);
                    editor.apply();
                    btn_notification.setBackgroundResource(R.drawable.ic_bell_off);
                    Toast.makeText(this, "Notification OFF", Toast.LENGTH_SHORT).show();
                } else {
                    // on
                    editor.putBoolean("notification", true);
                    editor.apply();
                    btn_notification.setBackgroundResource(R.drawable.ic_bell);
                    Toast.makeText(this, "Notification ON", Toast.LENGTH_SHORT).show();

                    // Notify
                    onResume();
                }

                break;
        }
    }
}
