package com.sportsfactory.sportforall;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class JoinProfileActivity extends AppCompatActivity {

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_profile);

        Button joinProfileNextBtn = (Button)findViewById(R.id.joinProfileNextBtn);
        joinProfileNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, JoinInterestActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
