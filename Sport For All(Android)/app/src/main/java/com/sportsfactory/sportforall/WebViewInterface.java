package com.sportsfactory.sportforall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.sportsfactory.dialog.CustomProgressDialog;
import com.sportsfactory.dialog.IsClosedDialog;
import com.sportsfactory.info.SportForAll;

import me.iwf.photopicker.PhotoPicker;

public class WebViewInterface extends CustomAppCompatActivity{

    private WebView mWebView;
    private Context mContext;
    private Activity mActivity;
    public static CustomProgressDialog pd = null;

    public WebViewInterface(Activity mActivity, WebView view) {
        mWebView = view;
        this.mActivity = mActivity;
        this.mContext = mActivity.getApplicationContext();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    //메세징 테스트
    @JavascriptInterface
    public void toastLong (String message) {
//        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }

    //이미지 선택
    @JavascriptInterface
    public void selectImage (String club_code, String user_id) {
//        YPhotoPickerIntent intent = new YPhotoPickerIntent(mActivity);
//        intent.setMaxSelectCount(20);
//        intent.setShowCamera(true);
//        intent.setShowGif(true);
//        intent.setSelectCheckBox(false);
//        MainActivity.club_code = club_code;
//        MainActivity.user_id = user_id;
//        mActivity.startActivityForResult(intent, MainActivity.PICK_FROM_MULTI_ALBUM);
        MainActivity.club_code = club_code;
        MainActivity.user_id = user_id;
        PhotoPicker.builder()
                .setPhotoCount(10)
                .setShowCamera(true)
                .setShowGif(true)
                .setPreviewEnabled(false)
                .start(mActivity, MainActivity.PICK_FROM_MULTI_ALBUM);
    }

    //dialog
    @JavascriptInterface
    public void dialog() {
        IsClosedDialog mCustomDialog = new IsClosedDialog(mActivity, mWebView);
        mCustomDialog.show();
    }

    @JavascriptInterface
    public void call_address() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        mActivity.startActivityForResult(intent, MainActivity.ADDRESS_BOOK);
    }

    //이미지 한개 선택
    @JavascriptInterface
    public void selectOneImage () {
        PhotoPicker.builder()
                .setPhotoCount(1)
                .setShowCamera(true)
                .setShowGif(true)
                .setPreviewEnabled(false)
                .start(mActivity, MainActivity.PICK_FROM_ALBUM);

        /*Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image*//*");
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mActivity.startActivityForResult(intent, MainActivity.PICK_FROM_ALBUM);
        overridePendingTransition(0,0);*/
    }

    //뒤로가기 후 새로고침
    @JavascriptInterface
    public void BackAndReload() {
//        mWebView.goBack();
//        mWebView.reload();
    }

    @JavascriptInterface
    public void getCurrentVersion() {
        mWebView.loadUrl("javascript:current_version('"+ SportForAll.VERSION+"')");
    }

    @JavascriptInterface
    public void progressDialog() {
        if(pd==null){
            pd = new CustomProgressDialog(mActivity);
            pd.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            pd.setTitle(null);
            pd.setCancelable(false);
            pd.show();
        }
    }

    @JavascriptInterface
    public void progressDialogCancel() {
        if(pd!=null){
            pd.dismiss();
            pd.cancel();
        }
    }
}