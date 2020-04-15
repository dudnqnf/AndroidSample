package com.iit.finalproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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

public class CommentListViewAdapter extends BaseAdapter {

    private ArrayList<CommentListViewItem> itemList = new ArrayList<>() ;
    public CommentListViewAdapter() {}

    Thread mThread = null;
    JSONObject obj;
    int user_id = 0;

    @Override
    public int getCount() {
        return itemList.size() ;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.comment_listview_item, parent, false);
        }

        TextView content = convertView.findViewById(R.id.comment_content);
        TextView author = convertView.findViewById(R.id.comment_author);
        TextView time = convertView.findViewById(R.id.comment_time);
        Button btn_comment_delete = convertView.findViewById(R.id.btn_comment_delete);
        btn_comment_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            del_comment(URL.DELETE_COMMENT, itemList.get(position).getId());
                            delItem(position);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                mThread.start();
            }
        });

        CommentListViewItem listViewItem = itemList.get(position);
        content.setText(listViewItem.getContent());
        author.setText(listViewItem.getAuthor());
        time.setText(listViewItem.getTime());

        if(user_id != itemList.get(position).getUser_id())
            btn_comment_delete.setVisibility(View.INVISIBLE);

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public CommentListViewItem getItem(int position) {
        return itemList.get(position) ;
    }

    public void addItem(CommentListViewItem item) {
        this.itemList.add(item);
    }

    public void clearList(){
        itemList.clear();
    }

    public void delItem(int position){
        this.itemList.remove(position);
    }

    public void addList(ArrayList<CommentListViewItem> itemList){
        for(int i=0;i<itemList.size();i++)
            this.itemList.add(itemList.get(i));
    }

    public void del_comment(String url, int comment_id) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody req_body = new FormBody.Builder()
                .add("comment_id", ""+comment_id)
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
                    notifyDataSetChanged();
                    break;
                case 1:
                    break;
                default:
                    break;
            }
        }
    };

    public void setUserId(int user_id){
        this.user_id = user_id;
    }
}
