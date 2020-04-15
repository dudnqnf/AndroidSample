package com.programmers.todolist;

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

        ListViewItem item = itemList.get(position);
        TextView item_title = convertView.findViewById(R.id.item_title);
        item_title.setText(item.getTitle());

        TextView item_priority = convertView.findViewById(R.id.item_priority);
        item_priority.setText(""+item.getPriority());

        View item_check = convertView.findViewById(R.id.item_check);
        if(item.getStatus() == 1)
            item_check.setBackgroundResource(R.drawable.ic_checked);
        else
            item_check.setBackgroundResource(R.drawable.ic_unchecked);
        TextView item_deadline = convertView.findViewById(R.id.item_deadline);
        item_deadline.setText(item.getDeadline());

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

    public void addItem(int id, String title, int priority, int status, String deadline) {
        ListViewItem item = new ListViewItem(id, title, priority, status, deadline);
        itemList.add(item);
    }

    public void clearList(){
        itemList.clear();
    }
    public void addList(ArrayList<ListViewItem> itemList){
        this.itemList.addAll(itemList);
    }
}
