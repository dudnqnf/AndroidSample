package com.sportsfactory.sportforall;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Lee on 2016-09-26.
 */
public class IntroActivity extends AppCompatActivity {

    Context context = this;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            startActivity(new Intent(this,Splash.class));

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_intro);

        Button loginbtn = (Button)findViewById(R.id.loginbtn);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, LoginActivity.class);
                startActivity(i);
            }
        });

        Button joinbtn = (Button)findViewById(R.id.joinbtn);
        joinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, JoinCertifyPhoneActivity.class);
                startActivity(i);
                finish();
            }
        });

    }
}