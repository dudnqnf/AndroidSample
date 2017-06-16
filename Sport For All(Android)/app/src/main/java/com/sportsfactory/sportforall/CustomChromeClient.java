//package com.sportsfactory.sportforall;
//
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.drawable.ColorDrawable;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.webkit.ValueCallback;
//import android.webkit.WebChromeClient;
//import android.webkit.WebView;
//
//import com.sportsfactory.dialog.CustomProgressDialog;
//
//import java.io.File;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//
//public class CustomChromeClient extends WebChromeClient {
//
//    Activity mActivity;
//    public CustomChromeClient(Activity mActivity){
//        this.mActivity = mActivity;
//    }
//
//    @Override
//    public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, android.os.Message resultMsg)
//    {
//        WebView newWebView = new WebView(mActivity);
//        WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
//        transport.setWebView(newWebView);
//        resultMsg.sendToTarget();
//
//        return true;
//    }
//
//    @Override
//    public void onProgressChanged(WebView view, int newProgress) {
//        CustomProgressDialog pd = MainActivity.pd;
//        if(newProgress==10){
//            pd = new CustomProgressDialog(mActivity);
//            pd.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//            pd.setTitle(null);
//            pd.setCancelable(false);
//            pd.show();
//        }
//
//        if(newProgress>=100){
//            pd.dismiss();
//            pd.cancel();
//        }
//        super.onProgressChanged(view, newProgress);
//    }
//
//}