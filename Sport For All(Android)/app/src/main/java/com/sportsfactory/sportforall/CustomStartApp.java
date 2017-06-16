package com.sportsfactory.sportforall;

import android.app.Application;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tsengvn.typekit.Typekit;


public class CustomStartApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //폰트적용
        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "fonts/NanumSquareR.ttf"))
                .addBold(Typekit.createFromAsset(this, "fonts/NanumSquareB.ttf"))
                .addItalic(Typekit.createFromAsset(this, "fonts/NanumSquareL.ttf"));
    }
}
