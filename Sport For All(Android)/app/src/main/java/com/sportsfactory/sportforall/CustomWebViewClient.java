//package com.sportsfactory.sportforall;
//
//import android.app.ProgressDialog;
//import android.content.ActivityNotFoundException;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.drawable.ColorDrawable;
//import android.net.Uri;
//import android.provider.Browser;
//import android.util.Log;
//import android.view.Window;
//import android.webkit.WebResourceError;
//import android.webkit.WebResourceRequest;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//import android.widget.Toast;
//
//import com.sportsfactory.dialog.CustomProgressDialog;
//
//import java.io.IOException;
//
//public class CustomWebViewClient extends WebViewClient {
//
//    public static final String INTENT_PROTOCOL_START = "intent:";
//    public static final String INTENT_PROTOCOL_INTENT = "#Intent;";
//    public static final String INTENT_PROTOCOL_END = ";end;";
//    public static final String GOOGLE_PLAY_STORE_PREFIX = "market://details?id=";
//    public static final String KAKAO_PROTOCOL_START = "kakaolink:";
//
//    Context mContext;
//    public CustomWebViewClient(Context mContext){
//        this.mContext = mContext;
//    }
//
////    @Override
////    public boolean shouldOverrideUrlLoading(WebView view, String url) {
////        view.loadUrl(url);
////        return true;
////    }
//
//    @Override
//    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//        if (url.startsWith(INTENT_PROTOCOL_START)) {
//            final int customUrlStartIndex = INTENT_PROTOCOL_START.length();
//            final int customUrlEndIndex = url.indexOf(INTENT_PROTOCOL_INTENT);
//            if (customUrlEndIndex < 0) {
//                return false;
//            } else {
//                final String customUrl = url.substring(customUrlStartIndex, customUrlEndIndex);
//                try {
//                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(customUrl)));
//                } catch (ActivityNotFoundException e) {
//                    final int packageStartIndex = customUrlEndIndex + INTENT_PROTOCOL_INTENT.length();
//                    final int packageEndIndex = url.indexOf(INTENT_PROTOCOL_END);
//
//                    final String packageName = url.substring(packageStartIndex, packageEndIndex < 0 ? url.length() : packageEndIndex);
//                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_STORE_PREFIX + packageName)));
//                }
//                return true;
//            }
//        } else if(url.startsWith(KAKAO_PROTOCOL_START)){
//            try {
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                intent.addCategory(Intent.CATEGORY_BROWSABLE);
//                intent.putExtra(Browser.EXTRA_APPLICATION_ID,mContext.getPackageName());
//                mContext.startActivity(intent);
//            } catch (ActivityNotFoundException e) {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("market://details?id=" + "com.kakao.talk"));
//                mContext.startActivity(intent);
//                Log.d("TAG", "카카오톡이 설치되어있지 않음 : " + e.toString());
//            } catch (Exception e) {
//                Log.d("TAG", "기타에러 : " + e.toString());
//            }
//            return true;
//        } else {
//            return false;
//        }
//    }
//
////    @Override
////    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
////        if(pd!=null){
////            pd.dismiss();
////            pd.cancel();
////        }
////        return super.shouldOverrideUrlLoading(view, request);
////    }
//
//    @Override
//    public void onPageStarted(WebView view, String url, Bitmap favicon) {
////        if(pd==null){
////            pd = new CustomProgressDialog(mContext);
////            pd.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
////            pd.setTitle(null);
////            pd.setCancelable(false);
////            pd.show();
////        }
////        Runnable time = new Runnable() {
////            @Override
////            public void run() {
////                mWebView.is
////                while(){
////                    try {
////                        Thread.sleep(1000);
////                    } catch (InterruptedException e) {
////                        e.printStackTrace();
////                    }
////                }
////                if(pd!=null){
////                    pd.dismiss();
////                    pd.cancel();
////                }
////            }
////        };
////        Thread thread = new Thread(time);
////        thread.start();
//        super.onPageStarted(view, url, favicon);
//    }
//
//    @Override
//    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//        super.onReceivedError(view, request, error);
//    }
//
//    @Override
//    public void onPageFinished(WebView view, String url){
//        super.onPageFinished(view, url);
//    }
//}