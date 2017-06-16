package com.sportsfactory.sportforall;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sportsfactory.dialog.AccessTermsDialog;

import org.w3c.dom.Text;

public class JoinBasicActivity extends AppCompatActivity {

    Context context = this;
    AccessTermsDialog mAccessTermsDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_basic);

        TextView serviceDetail = (TextView)findViewById(R.id.serviceDetail);
        serviceDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAccessTermsDialog = new AccessTermsDialog(context); // 오른쪽 버튼 이벤트
                mAccessTermsDialog.show();
            }
        });

        TextView privateDetail = (TextView)findViewById(R.id.privateDetail);
        privateDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAccessTermsDialog = new AccessTermsDialog(context); // 오른쪽 버튼 이벤트
                mAccessTermsDialog.show();
            }
        });

        Button joinBasikNextBtn = (Button)findViewById(R.id.joinBasikNextBtn);
        joinBasikNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, JoinProfileActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
