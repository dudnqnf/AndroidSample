package com.sportsfactory.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.sportsfactory.sportforall.R;

public class AccessTermsDialog extends Dialog implements View.OnClickListener{

    public Context mContext;
    Activity mActivity;

    public AccessTermsDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_access_terms);
    }

    @Override
    public void onClick(View v) {
    }
}
