package com.sportsfactory.sportforall;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Browser;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.sportsfactory.dialog.CustomProgressDialog;
import com.sportsfactory.info.SportForAll;
import com.sportsfactory.util.AndroidUtil;
import com.sportsfactory.util.ImageReScale;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import me.iwf.photopicker.PhotoPicker;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends CustomAppCompatActivity{

    public static WebView mWebView = null;
    private WebViewInterface mWebViewInterface;

    public long backKeyPressedTime = 0;

    public static final int PICK_FROM_ALBUM = 1;
    public static final int CROP_FROM_ALBUM = 3;
    public static final int PICK_FROM_MULTI_ALBUM = 4;
    public static final int ADDRESS_BOOK = 5;
    public static String club_code;
    public static String user_id;

    public static int is_fed;

    //OKHTTP 통신
    OkHttpClient client = new OkHttpClient();
    boolean result = true;

    //이미지 한개 선택 업로드 부분
    private AndroidUtil andUtil = new AndroidUtil();
    private String[] all_path = null;
    List<String> photos = null;

    //다중이미지 선택 업로드 부분
    public static CustomProgressDialog pd = null;
    private Thread mThread = null;
    public ByteArrayOutputStream[] bos = null;
    public Bitmap[] bm = null;

    private URL Url;
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";
    public String album_code;

    public String version = null;
    public int forced;

    public Uri photo_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //스플레시 이미지
        startActivity(new Intent(this,SplashActivity.class));
        processParsing(mThread, version_check);
        if(!checkNetworkState())
            finish();


        //웹뷰
        mWebView = (WebView)findViewById(R.id.mWebView);
        mWebView.loadUrl(com.sportsfactory.info.URL.MAIN_URL);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setSupportMultipleWindows(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAllowContentAccess(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);

        mWebView.setWebViewClient(new CustomWebViewClient(this));
        mWebView.setWebChromeClient(new CustomChromeClient(MainActivity.this));

        mWebViewInterface = new WebViewInterface(MainActivity.this, mWebView);  //JavascriptInterface 객체화
        mWebView.addJavascriptInterface(mWebViewInterface, "Android");          //웹뷰에 JavascriptInterface를 연결

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB)
            mWebView.getSettings().setDisplayZoomControls(false);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            mWebView.getSettings().setTextZoom(100);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            // Hide the zoom controls for HONEYCOMB+
            mWebView.getSettings().setDisplayZoomControls(false);
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                mWebView.getSettings().setTextZoom(100);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            mWebView.setWebContentsDebuggingEnabled(true);
        }
    }

    /*
        두번 누르면 액티비티 종료
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack() ){
            mWebView.loadUrl("javascript:is_back_btn()");
            mWebView.goBack();
            return true;
        }

        if ((keyCode == KeyEvent.KEYCODE_BACK) && (mWebView.canGoBack() == false)){
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                Toast.makeText(this, "한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
            } else if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                return super.onKeyDown(keyCode, event);
            }
        }
        return false;
    }

    public boolean checkNetworkState(){
        ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if(info==null){
            Toast.makeText(this, "인터넷 연결이 원활하지 않습니다.", Toast.LENGTH_LONG).show();
            return false;
        }


        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != RESULT_OK){
            return;
        }

        switch(requestCode){
            case PICK_FROM_MULTI_ALBUM:
                    if (data != null) {
                        photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                        all_path = new String[photos.size()];
                        for(int i=0;i<photos.size();i++)
                            all_path[i] = photos.get(i);

                        processParsing(mThread, multi_img);
                    }
                break;

            case PICK_FROM_ALBUM:
                if (data != null) {
                    photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    all_path = new String[photos.size()];
                    for(int i=0;i<photos.size();i++)
                        all_path[i] = photos.get(i);
                }

//                Uri photo_uri = data.getData();
//                String photo_path = andUtil.getRealPathFromURI(MainActivity.this, photo_uri);
                String photo_path = all_path[0];
                photo_uri = Uri.fromFile(new File(photo_path));

                String path = photo_path.substring(0, photo_path.lastIndexOf("/"));
                String file_name = System.currentTimeMillis()+".jpg";

                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(photo_uri, "image/*");
//                intent.putExtra("outputX", 644); // crop한 이미지의 x축 크기
//                intent.putExtra("outputY", 416); // crop한 이미지의 y축 크기
//                intent.putExtra("aspectX", 644); // crop 박스의 x축 비율
//                intent.putExtra("aspectY", 415); // crop 박스의 y축 비율
                intent.putExtra("scale", true);
                intent.putExtra("output", photo_uri);

                File f = new File(path, "/"+file_name);
                try {
                    f.createNewFile();
                } catch (IOException ex) {
                    Log.e("io", ex.getMessage());
                }
                photo_uri = Uri.fromFile(f);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photo_uri);
                startActivityForResult(intent, CROP_FROM_ALBUM);
                break;

            case CROP_FROM_ALBUM:
                if (data != null) {
                    Uri uri = data.getData();
                    if(uri==null){
                        uri = photo_uri;
                        Toast.makeText(this, "이미지 자르기를 지원하지 않는 기종이여서 원본이미지를 사용합니다.", Toast.LENGTH_SHORT).show();
                    }
                    new AsyncTask<Uri, Void, String>() {
                        @Override
                        protected String doInBackground(Uri... params) {
                            String mimeType = getMimeType(params[0]);
                            File file = uriToFile(params[0]);
                            String base64EncodedImage = fileToString(file);
                            return "javascript:selectedImage('" + mimeType + "', '" + base64EncodedImage + "');";
                        }

                        @Override
                        protected void onPostExecute(String result) {
                            mWebView.loadUrl(result);
                        }
                    }.execute(uri);
                }
                break;

            case ADDRESS_BOOK:
                if (data != null) {
                    String [] arrProjection = {
                            ContactsContract.Contacts.DISPLAY_NAME
                    };
                    Cursor cursor = getContentResolver().query(
                            data.getData(),
                            arrProjection
                            , null, null, null
                    );

                    cursor.moveToFirst();
                    String name = cursor.getString(0); // 0은 이름을 얻어옵니다.
                    cursor.close();
                    mWebView.loadUrl("javascript:call_address_return('"+ name + "')");
                }
                break;

            default :
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getMimeType(Uri uri) {
        ContentResolver cR = getContentResolver();
        String type = cR.getType(uri);
        return type;
    }

    private File uriToFile(Uri uri) {
        String filePath = "";
        final String[] imageColumns = {MediaStore.Images.Media.DATA };
        String scheme = uri.getScheme();
        if ( scheme.equalsIgnoreCase("content") ) {
            Cursor imageCursor = getContentResolver().query(uri, imageColumns, null, null, null);
            if (imageCursor.moveToFirst()) {
                filePath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
        } else {
            filePath = uri.getPath();
        }
        File file = new File( filePath );
        return file;
    }

    public String fileToString(File file) {
        String fileString = "";

        try {
            FileInputStream inputStream = new FileInputStream(file);
            ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();

            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = inputStream.read(buf)) != -1) {
                byteOutStream.write(buf, 0, len);
            }

            byte[] fileArray = byteOutStream.toByteArray();
            fileString = new String(Base64.encodeToString(fileArray, 0));
            fileString = fileString.replace("\n", "\\n");

            inputStream.close();
            byteOutStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileString;
    }

    private final Runnable version_check = new Runnable() {
        @Override
        public void run() {
            try {
                version_check(com.sportsfactory.info.URL.VERSION_CHECK);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
    private final Runnable multi_img = new Runnable() {
        @Override
        public void run() {
            multi_img();
        }
    };
    private final Runnable upload = new Runnable() {
        @Override
        public void run() {
            httpconn();
        }
    };
    private final Runnable makeAlbum = new Runnable() {
        @Override
        public void run() {
            try {
                makeAlbum(com.sportsfactory.info.URL.MAKE_ALBUM + "?club_code=" + club_code + "&user_id=" + user_id);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    // 리사이징 및 전환처리
    protected void multi_img() {
        // TODO Auto-generated method stub
        bos = new ByteArrayOutputStream[all_path.length];
        bm = new Bitmap[all_path.length];

        for (int i = 0; i < all_path.length; i++) {

            String photo_path = all_path[i];
            // 이미지 사이즈 화면 사이즈에 맞게 리스케일
            ImageReScale imgReScale = new ImageReScale();
            try {
                bm[i] = imgReScale.loadBackgroundBitmap(
                        getApplicationContext(), photo_path);
            } catch (OutOfMemoryError e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
            e.printStackTrace();
        }

            // 이미지를 상황에 맞게 회전시킨다
            if (bm[i] != null) {

                bm[i] = andUtil.bitmapRotate(photo_path, bm[i]);

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                bos[i] = new ByteArrayOutputStream();
                bm[i].compress(Bitmap.CompressFormat.JPEG, 100, bos[i]);

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

//        for (int i = 0; i < all_path.length; i++) {
//            String photo_path = all_path[i];
//
//            int degree = AndroidUtil.GetExifOrientation(photo_path);
//            Bitmap bitmap = AndroidUtil.GetRotatedBitmap(, degree);
//
//        }
        handler.sendEmptyMessage(1);
    }

    public void processParsing(Thread thread, Runnable runnable) {
        pd = new CustomProgressDialog(this);
        pd.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        pd.setTitle(null);
        pd.setCancelable(false);
        pd.show();

        thread = new Thread(runnable);
        thread.start();
    }

    public void version_check(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        String res = response.body().string();
        try {
            JSONObject obj = new JSONObject(res);
            JSONObject val = new JSONObject(obj.getString("value"));
            version = val.getString("version");
            forced = val.getInt("forced");
            handler.sendEmptyMessage(4);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void makeAlbum(String url) throws IOException {

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        String res = response.body().string();
        try {
            JSONObject obj = new JSONObject(res);
            album_code = obj.getString("value");
            handler.sendEmptyMessage(2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void httpconn(){
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try{
                    for(int i=0;i<bos.length;i++){

                        Url = new java.net.URL(com.sportsfactory.info.URL.UPLOAD_IMAGE);  // URL화 한다.
                        HttpURLConnection conn = (HttpURLConnection) Url.openConnection(); // URL을 연결한 객체 생성.
                        conn.setRequestMethod("POST"); // get방식 통신
                        conn.setDoOutput(true);       // 쓰기모드 지정
                        conn.setDoInput(true);        // 읽기모드 지정
                        conn.setUseCaches(false);     // 캐싱데이터를 받을지 안받을지
                        conn.setDefaultUseCaches(false); // 캐싱데이터 디폴트 값 설정
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("enctype", "multipart/form-data");
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                        FileInputStream mFileInputStream = new FileInputStream(all_path[i]);
                        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                        //text 전송
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"club_code\""
                                + lineEnd);
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(club_code + lineEnd);

                        //text 전송
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"album_code\""
                                + lineEnd);
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(album_code + lineEnd);

                        //Image 전송
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"img" + i + "\";filename=\"" + all_path[i]+"\"" + lineEnd);
                        dos.writeBytes(lineEnd);

//                        int bytesAvailable = mFileInputStream.available();
//                        int maxBufferSize = 1024;
//                        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
//
//                        byte[] buffer = new byte[bufferSize];
//                        int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
//
//                        // read image
//                        while (bytesRead > 0) {
//                            dos.write(buffer, 0, bufferSize);
//                            bytesAvailable = mFileInputStream.available();
//                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                            bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
//                        }

                        // Bitmap을 ByteBuffer로 전환
//                        byte[] pixels = new byte[bm[i].getWidth() * bm[i].getHeight()];
//                        for (int j = 0; j < bm[i].getWidth(); ++j) {
//                            for (int k = 0; k < bm[i].getHeight(); ++k) {
//                                //we're interested only in the MSB of the first byte,
//                                //since the other 3 bytes are identical for B&W images
//                                pixels[j + k] = (byte) ((bm[i].getPixel(j, k) & 0x80) >> 7);
//                            }
//                        }
//                        dos.write(pixels);

                        dos.write(bos[i].toByteArray());

                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                        mFileInputStream.close();
                        dos.flush();
                        conn.getInputStream();
                        dos.close();

                        int serverResponseCode = conn.getResponseCode();
                        String serverResponseMessage = conn.getResponseMessage();
                        if(serverResponseCode != HttpURLConnection.HTTP_OK){
                            result = false;
                            Log.e("서버연동에러", serverResponseMessage);
                        }

                    }

                }catch(MalformedURLException | ProtocolException exception) {
                    exception.printStackTrace();
                }catch(IOException io){
                    io.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                System.out.println(result);
                handler.sendEmptyMessage(3);
            }
        }.execute();
    }


    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {

            switch (msg.what) {
                case 1:
                    if (pd != null)
                        pd.cancel();

                    processParsing(mThread, makeAlbum);

                    break;
                case 2:
                    if (pd != null)
                        pd.cancel();

                    processParsing(mThread, upload);

                    break;

                case 3:
                    if (pd != null)
                        pd.cancel();

                    mWebView.loadUrl("javascript:title_update('"+ album_code +"')");

                    break;

                case 4:
                    if (pd != null)
                        pd.cancel();

                    if(!version.equalsIgnoreCase(SportForAll.VERSION)){
                        if(forced==0){
                            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                            alert.setTitle("업데이트");
                            alert.setMessage("새로운 버전이 나왔습니다! 업데이트 하시겠습니까?");
                            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse("market://details?id=" + getPackageName()));
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            alert.show();
                        } else {
                            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                            alert.setTitle("업데이트");
                            alert.setMessage("앱을 업데이트 후 다시 실행해 주세요!");
                            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse("market://details?id=" + getPackageName()));
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alert.show();
                        }

                    }

                    break;

                default:
                    break;
            }
        }
    };

    public class CustomChromeClient extends WebChromeClient {

        Activity mActivity;
        public CustomChromeClient(Activity mActivity){
            this.mActivity = mActivity;
        }

        @Override
        public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, android.os.Message resultMsg)
        {
            WebView newWebView = new WebView(mActivity);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(newWebView);
            resultMsg.sendToTarget();

            return true;
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("알림")
                    .setMessage(message)
                    .setPositiveButton("확인", new AlertDialog.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();

            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("알림")
                    .setMessage(message)
                    .setPositiveButton("확인", new AlertDialog.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    })
                    .setNegativeButton("취소", new AlertDialog.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.cancel();
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();

            return true;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
//            if(newProgress<100 && pd==null){
//                pd = new CustomProgressDialog(MainActivity.this);
//                pd.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//                pd.setTitle(null);
//                pd.setCancelable(false);
//                pd.show();
//            } else {
//                pd.dismiss();
//                pd.cancel();
//            }
//            if(newProgress>=100){
//                pd.dismiss();
//                pd.cancel();
//            }
            super.onProgressChanged(view, newProgress);
        }
    }

    public class CustomWebViewClient extends WebViewClient {

        public static final String INTENT_PROTOCOL_START = "intent:";
        public static final String INTENT_PROTOCOL_INTENT = "#Intent;";
        public static final String INTENT_PROTOCOL_END = ";end;";
        public static final String GOOGLE_PLAY_STORE_PREFIX = "market://details?id=";
        public static final String KAKAO_PROTOCOL_START = "kakaolink:";

        Context mContext;
        public CustomWebViewClient(Context mContext){
            this.mContext = mContext;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(pd==null){
                pd = new CustomProgressDialog(MainActivity.this);
                pd.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                pd.setTitle(null);
                pd.setCancelable(false);
                pd.show();
            }
            if (url.startsWith(INTENT_PROTOCOL_START)) {
                if(pd!=null){
                    pd.dismiss();
                    pd.cancel();
                    pd=null;
                }
                final int customUrlStartIndex = INTENT_PROTOCOL_START.length();
                final int customUrlEndIndex = url.indexOf(INTENT_PROTOCOL_INTENT);
                if (customUrlEndIndex < 0) {
                    return false;
                } else {
                    final String customUrl = url.substring(customUrlStartIndex, customUrlEndIndex);
                    try {
                        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(customUrl)));
                    } catch (ActivityNotFoundException e) {
                        final int packageStartIndex = customUrlEndIndex + INTENT_PROTOCOL_INTENT.length();
                        final int packageEndIndex = url.indexOf(INTENT_PROTOCOL_END);

                        final String packageName = url.substring(packageStartIndex, packageEndIndex < 0 ? url.length() : packageEndIndex);
                        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_STORE_PREFIX + packageName)));
                    }
                    return true;
                }
            } else if(url.startsWith(KAKAO_PROTOCOL_START)){
                if(pd!=null){
                    pd.dismiss();
                    pd.cancel();
                    pd=null;
                }
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.putExtra(Browser.EXTRA_APPLICATION_ID,mContext.getPackageName());
                    mContext.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=" + "com.kakao.talk"));
                    mContext.startActivity(intent);
                    Log.d("TAG", "카카오톡이 설치되어있지 않음 : " + e.toString());
                } catch (Exception e) {
                    Log.d("TAG", "기타에러 : " + e.toString());
                }

                return true;
            } else {
                return false;
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }

        @Override
        public void onPageFinished(WebView view, String url){
            if(pd!=null){
                pd.dismiss();
                pd.cancel();
                pd=null;
            }
            super.onPageFinished(view, url);
        }
    }

}
