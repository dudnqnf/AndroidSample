package com.example.lee.adaptersample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Lee on 2016-09-17.
 */
public class SampleAdapter extends BaseAdapter{

    Context mContext;
    LayoutInflater Inflater;
    static ArrayList<data> arrlist = new ArrayList<data>();
    int mLayout;

    public SampleAdapter(Context context, int layout, ArrayList<data> list){
        mContext = context;
        Inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        arrlist = list;
        mLayout = layout;
    }

    @Override
    public int getCount() {
        return arrlist.size();
    }

    @Override
    public Object getItem(int position) {
        return arrlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = Inflater.inflate(mLayout, parent, false);
        }

        final String category_no = arrlist.get(position).a;

        TextView category = (TextView)convertView.findViewById(R.id.category);
        String name = arrlist.get(position).b+"";
        category.setText(name);

        return convertView;
    }
}
