package com.sportsfactory.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.sportsfactory.sportforall.R;

/**
 * Created by Lee on 2017-01-06.
 */

public class CustomProgressDialog extends Dialog {

    private Animation translateAni;
    private ImageView progress_img;

    public CustomProgressDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_custom_progress);

        translateAni = AnimationUtils.loadAnimation(context, R.anim.set);
        progress_img = (ImageView)findViewById(R.id.progress_img);
        progress_img.setAnimation(translateAni);
        progress_img.getBackground().setAlpha(0);
    }
}
