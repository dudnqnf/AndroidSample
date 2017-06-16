package com.sportsfactory.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.sportsfactory.sportforall.MainActivity;
import com.sportsfactory.sportforall.R;

//사용안함
public class IsClosedDialog extends Dialog implements View.OnClickListener{

    public Context mContext;
    public WebView mWebView;
    public int flag = 0;

    CheckBox open;
    CheckBox close;

    public IsClosedDialog(Context context, WebView webview) {
        super(context);
        mContext = context;
        mWebView = webview;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_is_closed);

        open = (CheckBox)findViewById(R.id.open);
        close = (CheckBox)findViewById(R.id.close);
        Button btn_cancel = (Button)findViewById(R.id.btn_cancel);
        Button btn_confirm = (Button)findViewById(R.id.btn_confirm);

        open.setOnClickListener(this);
        close.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open:
                if(close.isChecked()){
                    close.setChecked(false);
                    flag=0;
                }
                break;

            case R.id.close:
                if(open.isChecked()) {
                    open.setChecked(false);
                    flag=1;
                }
                break;

            case R.id.btn_confirm:
                mWebView.post(new Runnable() {
                    public void run() {
                        mWebView.loadUrl("javascript:android_result('"+ flag +"')");
                    }
                });
                break;

            case R.id.btn_cancel:
                IsClosedDialog.this.dismiss();
                break;

            default:
                break;
        }
    }
}