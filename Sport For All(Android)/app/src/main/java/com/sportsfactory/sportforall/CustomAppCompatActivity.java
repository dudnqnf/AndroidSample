package com.sportsfactory.sportforall;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.tsengvn.typekit.TypekitContextWrapper;

//폰트적용
public class CustomAppCompatActivity extends AppCompatActivity{
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}
