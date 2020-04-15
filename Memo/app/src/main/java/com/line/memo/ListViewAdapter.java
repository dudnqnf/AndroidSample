package com.line.memo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class ListViewAdapter extends BaseAdapter {

    private ArrayList<ListViewItem> itemList = new ArrayList<>();
    private ImageView imageView;

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        ListViewItem item = itemList.get(position);
        TextView item_title = convertView.findViewById(R.id.item_title);
        item_title.setText(item.getTitle());

        TextView item_content = convertView.findViewById(R.id.item_content);
        item_content.setText(item.getContent());

        imageView = convertView.findViewById(R.id.item_image);
        if (item.getOnline_thumbnail() != null)
            imageView.setImageBitmap(item.getOnline_thumbnail());
        else if (item.getThumbnail() != null) {
            imageView.setImageURI(Uri.parse(item.getThumbnail()));
        } else
            imageView.setImageResource(R.drawable.no_image);

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ListViewItem getItem(int position) {
        return itemList.get(position);
    }

    public void addItem(int id, String title, String content, String uri, Bitmap bitmap) {
        ListViewItem item = new ListViewItem(id, title, content, uri, bitmap);
        itemList.add(item);
    }

    public void clear() {
        itemList.clear();
    }
}
