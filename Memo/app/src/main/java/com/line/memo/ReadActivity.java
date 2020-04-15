package com.line.memo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.line.memo.Database.SQLite;
import com.line.memo.Database.Tables;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class ReadActivity extends AppCompatActivity implements View.OnClickListener {

    private int id;
    private TextView read_tv_title, read_tv_content;
    private Button btn_back, read_btn_edit, read_btn_delete;
    private SQLiteDatabase db;

    private LayoutInflater inflater;
    private LinearLayout read_scroll_view;
    private Button btn_delete;
    private List<Uri> picture_uris;

    private Thread mThread = null;
    private List<ImageView> imageViewList;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        db = new SQLite(this).getDatabase();

        btn_back = findViewById(R.id.read_btn_back);
        btn_back.setOnClickListener(this);

        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);

        read_tv_title = findViewById(R.id.read_tv_title);
        read_tv_content = findViewById(R.id.read_tv_content);
        read_btn_edit = findViewById(R.id.read_btn_edit);
        read_btn_delete = findViewById(R.id.read_btn_delete);
        read_scroll_view = findViewById(R.id.read_scroll_view);

        read_btn_edit.setOnClickListener(this);
        read_btn_delete.setOnClickListener(this);

        picture_uris = new ArrayList<>();
        imageViewList = new ArrayList<>();
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    public void add_view(final Uri uri) {
        final ConstraintLayout container = (ConstraintLayout) inflater.inflate(R.layout.scrollview_item, read_scroll_view, false);
        ImageView imageView = container.findViewById(R.id.scrollview_item_image);
        imageView.setImageURI(uri);
        imageViewList.add(imageView);
        btn_delete = container.findViewById(R.id.scrollview_item_btn_delete);
        btn_delete.setVisibility(View.GONE);
        read_scroll_view.addView(container);
    }

    public void add_view_online(final int index, final Uri uri) {
        final ConstraintLayout container = (ConstraintLayout) inflater.inflate(R.layout.scrollview_item, read_scroll_view, false);
        btn_delete = container.findViewById(R.id.scrollview_item_btn_delete);
        btn_delete.setVisibility(View.GONE);
        imageViewList.add((ImageView) container.findViewById(R.id.scrollview_item_image));
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(uri.toString());
                    URLConnection conn = url.openConnection();
                    conn.connect();

                    BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                    bitmap = BitmapFactory.decodeStream(bis);
                    handler.sendEmptyMessage(index);
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        mThread.start();
        read_scroll_view.addView(container);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            int index = msg.what;
            imageViewList.get(index).setImageBitmap(bitmap);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Cursor cursor = db.rawQuery("SELECT * FROM memo where id=" + id + ";", null);
        cursor.moveToNext();
        read_tv_title.setText(cursor.getString(1));
        read_tv_content.setText(cursor.getString(2));

        read_scroll_view.removeAllViews();
        picture_uris.clear();
        cursor = db.rawQuery("SELECT * FROM " + Tables.Uri.TABLE_NAME + " where memo_id=" + id + ";", null);
        while (cursor.moveToNext()) {
            String uri = cursor.getString(2);
            picture_uris.add(Uri.parse(uri));
        }

        imageViewList.clear();
        for (int i = 0; i < picture_uris.size(); i++) {
            Uri uri = picture_uris.get(i);
            if (uri.toString().contains("http"))
                add_view_online(i, uri);
            else
                add_view(uri);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.read_btn_back:
                finish();
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
                                db.delete("memo", "id=" + id, null);
                                finish();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
