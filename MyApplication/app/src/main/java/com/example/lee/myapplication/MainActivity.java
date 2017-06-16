package com.example.lee.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView text = (TextView)findViewById(R.id.textView);
        final EditText editbox = (EditText)findViewById(R.id.editbox);
        Button btn = (Button) findViewById(R.id.asdqwe);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              Intent i = new Intent(MainActivity.this, SampleActivity.class);
//              startActivity(i);
                String a = editbox.getText().toString();
                text.setText(a);;

            }
        });



    }
}
