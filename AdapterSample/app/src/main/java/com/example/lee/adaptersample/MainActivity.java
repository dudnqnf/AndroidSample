package com.example.lee.adaptersample;

        import android.os.AsyncTask;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.widget.ListView;
        import android.widget.Toast;

        import com.example.lee.util.HttpUtil;

        import java.net.URL;
        import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView lv;
    SampleAdapter mAdapter;
    public ArrayList<data> myArrlist;
    public ArrayList<String> exam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myArrlist = new ArrayList<data>();

        for(int j = 0; j < 5; j++){
            data d = new data("a"+j, "b"+(j+1));
            myArrlist.add(d);
        }

        lv = (ListView)findViewById(R.id.listView);
        mAdapter = new SampleAdapter(this, R.layout.listlayout, myArrlist);
        lv.setAdapter(mAdapter);


        new HttpUtil("http://hongikmagics.com/test/sample.html").execute();

    }

}
