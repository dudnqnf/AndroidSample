package com.lee.crawler;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    private ArrayList<ListViewItem> itemList = new ArrayList<>() ;
    public ListViewAdapter() {}

    @Override
    public int getCount() {
        return itemList.size() ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        TextView itemText = convertView.findViewById(R.id.item_text);
        ListViewItem listViewItem = itemList.get(position);
        itemText.setText(listViewItem.getText());

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

    public void addItem(String text) {
        ListViewItem item = new ListViewItem(text);
        itemList.add(item);
    }

    public void clearList(){
        itemList.clear();
    }

    public void addList(ArrayList<ListViewItem> itemList){
        for(int i=0;i<itemList.size();i++)
            this.itemList.add(itemList.get(i));
    }
}
