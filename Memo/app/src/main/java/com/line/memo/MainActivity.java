package com.line.memo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.line.memo.Database.SQLite;
import com.line.memo.Database.Tables;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final int PERMISSION_EXTERNAL_STORAGE = 0;

    private Button btn_add;
    private ListView list_view;
    private ListViewAdapter adapter;
    SQLiteDatabase db;

    private Thread mThread = null;
    private List<Bitmap> bitmapList;
    private int cursor_index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SQLite sqlite = new SQLite(this);
        db = sqlite.getDatabase();

        btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);

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
        bitmapList = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_EXTERNAL_STORAGE);
        }

        // Get all data from SQLite
        adapter.clear();
        bitmapList.clear();
        cursor_index = 0;
        String query = "SELECT m.*, u.uri FROM " + Tables.Memo.TABLE_NAME + " m LEFT JOIN (SELECT MAX(memo_id) memo_id, uri FROM " + Tables.Uri.TABLE_NAME + " GROUP BY memo_id) u ON m.id = u.memo_id;";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String content = cursor.getString(2);
            final String uri = cursor.getString(3);
            adapter.addItem(id, title, content, uri, null);
            bitmapList.add(null);
            cursor_index++;

            if (uri == null)
                continue;

            if (uri.contains("http")) {
                mThread = new Thread(new Runnable() {
                    int index = cursor_index - 1;

                    @Override
                    public void run() {
                        try {
                            URL url = new URL(uri);
                            URLConnection conn = url.openConnection();
                            conn.connect();

                            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                            bitmapList.set(index, BitmapFactory.decodeStream(bis));
                            handler.sendEmptyMessage(index);
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                mThread.start();
            }
        }
        adapter.notifyDataSetChanged();
        cursor.close();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            int index = msg.what;
            adapter.getItem(index).setOnline_thumbnail(bitmapList.get(index));
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_EXTERNAL_STORAGE:
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "데이터 접근에 허용해주셔야 합니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                Intent intent = new Intent(this, WriteActivity.class);
                startActivity(intent);
                break;
        }
    }
}
