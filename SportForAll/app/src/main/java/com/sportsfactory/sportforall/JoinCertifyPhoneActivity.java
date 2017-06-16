package com.sportsfactory.sportforall;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class JoinCertifyPhoneActivity extends AppCompatActivity {

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_certify_phone);

        Button certifyBtn = (Button)findViewById(R.id.certifyBtn);
        certifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, JoinBasicActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
