package com.sportsfactory.sportforall;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Lee on 2016-09-26.
 */
public class SplashActivity extends CustomAppCompatActivity {

    public static final int splashLength = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Handler hd = new Handler();
        hd.postDelayed(new Runnable() {

            @Override
            public void run() {
                finish();       // 3 초후 이미지를 닫아버림
            }
        }, splashLength);
    }
}