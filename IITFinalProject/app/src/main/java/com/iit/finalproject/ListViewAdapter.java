package com.iit.finalproject;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    private ArrayList<ListViewItem> itemList = new ArrayList<ListViewItem>() ;
    public ListViewAdapter() {}

    @Override
    public int getCount() {
        return itemList.size() ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        TextView title = convertView.findViewById(R.id.title);
        TextView author = convertView.findViewById(R.id.author);
        TextView time = convertView.findViewById(R.id.time);

        ListViewItem listViewItem = itemList.get(position);
        title.setText(listViewItem.getTitle());
        author.setText(listViewItem.getAuthor());
        time.setText(listViewItem.getTime());

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ListViewItem getItem(int position) {
        return itemList.get(position) ;
    }

    public void addItem(String notice, String subject) {
//        ListViewItem item = new ListViewItem();
//        item.setNotice(notice);
//        item.setSubject(subject);
//
//        itemList.add(item);
    }

    public void clearList(){
        itemList.clear();
    }

    public void addList(ArrayList<ListViewItem> itemList){
        for(int i=0;i<itemList.size();i++)
            this.itemList.add(itemList.get(i));
    }
}
