package com.lee.crawler;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new ListViewAdapter();

        Thread m_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Document doc = Jsoup.connect(URL.google_intern).get();
                    Elements aa = doc.select("#search-results > li:nth-child(20) > a > div.gc-card__header > h2");
                    Log.e("HTML", aa.text());
                    adapter.addItem("asdfasdf");
                    adapter.notifyDataSetChanged();
                } catch(IOException e){
                    Log.e("ERROR", e.toString());
                }
            }
        });
        m_thread.start();




        mListView = findViewById(R.id.mListView);
        mListView.setAdapter(adapter);
    }


}