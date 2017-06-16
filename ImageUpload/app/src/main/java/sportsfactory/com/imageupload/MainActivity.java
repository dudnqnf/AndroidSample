package sportsfactory.com.imageupload;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.yongbeam.y_photopicker.util.photopicker.PhotoPickerActivity;
import com.yongbeam.y_photopicker.util.photopicker.utils.YPhotoPickerIntent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import util.AndroidUtil;
import util.ImageReScale;

public class MainActivity extends AppCompatActivity {

    public static final int PICK_FROM_CAMERA = 0;
    public static final int PICK_FROM_ALBUM = 1;
    public static final int CROP_FROM_CAMERA = 2;
    public static final int CROP_FROM_ALBUM = 3;
    public static final int PICK_FROM_MULTI_ALBUM = 4;

    //OKHTTP 통신
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();
    String result = "default";

    //이미지 한개 선택 업로드 부분
    Uri selPhotoUri;
    private String selPhotoPath;
    private AndroidUtil andUtil = new AndroidUtil();
    String path= Environment.getExternalStorageDirectory()+"";
    String img_file_name="temp";
    private Uri uri;
    public static Bitmap selPhoto = null;
    private String[] all_path = null;
    List<String> photos = null;

    //다중이미지 선택 업로드 부분
    public static ProgressDialog pd = null;
    private Thread mThread = null;
    private ByteArrayOutputStream[] bos = null;
    private Bitmap[] bm = null;

    String strUrl = "http://192.168.0.54:8038/sportforall/club/upload_images.do";
    private URL Url;
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = (Button)findViewById(R.id.picbtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YPhotoPickerIntent intent = new YPhotoPickerIntent(MainActivity.this);
                intent.setMaxSelectCount(20);
                intent.setShowCamera(true);
                intent.setShowGif(true);
                intent.setSelectCheckBox(false);
                intent.setMaxGrideItemCount(3);
                startActivityForResult(intent, PICK_FROM_MULTI_ALBUM);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != RESULT_OK){
            return;
        }

        switch(requestCode){
            case PICK_FROM_MULTI_ALBUM:
                if (data != null) {
                    photos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
                    all_path = new String[photos.size()];
                    for(int i=0;i<photos.size();i++)
                        all_path[i] = photos.get(i);

                    Log.e("경로값들", ""+photos);
                    processParsing(mThread, multi_img);
                }
                break;

            default :
                break;

            case PICK_FROM_ALBUM:

                selPhotoUri = data.getData();
                selPhotoPath = andUtil.getRealPathFromURI(MainActivity.this, selPhotoUri);
                path = selPhotoPath.substring(0, selPhotoPath.lastIndexOf("/"));
                img_file_name = System.currentTimeMillis()+".jpg";
                Log.i("path", path);

                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(data.getData(), "image/*");
                intent.putExtra("outputX", 644); // crop한 이미지의 x축 크기
                intent.putExtra("outputY", 416); // crop한 이미지의 y축 크기
                intent.putExtra("aspectX", 644); // crop 박스의 x축 비율
                intent.putExtra("aspectY", 415); // crop 박스의 y축 비율
                intent.putExtra("scale", true);
                intent.putExtra("output", selPhotoUri);

                File f = new File(path, "/"+img_file_name);
                try {
                    f.createNewFile();
                } catch (IOException ex) {
                    Log.e("io", ex.getMessage());
                }

                uri = Uri.fromFile(f);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, CROP_FROM_ALBUM);

                break;

            case CROP_FROM_ALBUM:
                try {

                    String filePath = path + "/" + img_file_name;
                    Log.i("filePath", filePath);
                    selPhotoUri = Uri.parse(filePath);

                    selPhotoPath = andUtil.getRealPathFromURI(MainActivity.this, selPhotoUri);

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPurgeable = true;

                    if(selPhoto != null){
                        selPhoto.recycle();
                        selPhoto = null;
                    }

                    //이미지 사이즈 화면 사이즈에 맞게 리스케일
                    ImageReScale imgReScale2 = new ImageReScale();
                    selPhoto = imgReScale2.loadBackgroundBitmap(getApplicationContext(), selPhotoPath);

                    //작동구현
//                    setImage();

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void processParsing(Thread thread, Runnable runnable) {
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setTitle(null);
        pd.setCancelable(false);
        pd.setMessage("로드중...");
        pd.show();

        thread = new Thread(runnable);
        thread.start();
    }

    private final Runnable multi_img = new Runnable() {
        @Override
        public void run() {
            multi_img();
        }
    };

    private final Runnable upload = new Runnable() {
        @Override
        public void run() {
//          post(strUrl);
            httpconn();
        }
    };

    // 이미지 처리
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
                    System.out.println("#################");
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        handler.sendEmptyMessage(1);
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
                    for(int i=0;i<all_path.length;i++){
                        Url = new URL(strUrl);  // URL화 한다.
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
                        conn.setRequestProperty("img"+i, all_path[i]);

                        FileInputStream mFileInputStream = new FileInputStream(all_path[i]);
                        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"img" + i + "\";filename=\"" + all_path[i]+"\"" + lineEnd);
                        dos.writeBytes(lineEnd);

                        int bytesAvailable = mFileInputStream.available();
                        int maxBufferSize = 1024;
                        int bufferSize = Math.min(bytesAvailable, maxBufferSize);

                        byte[] buffer = new byte[bufferSize];
                        int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

                        // read image
                        while (bytesRead > 0) {
                            dos.write(buffer, 0, bufferSize);
                            bytesAvailable = mFileInputStream.available();
                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
                        }

                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                        mFileInputStream.close();
                        dos.flush();
                        conn.getInputStream();
                        dos.close();
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
                handler.sendEmptyMessage(2);
            }
        }.execute();
    }

    public void post(String url) throws IOException {
//        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/"+Bitmap.CompressFormat.JPEG);
//        RequestBody body = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .build();

//        RequestBody body = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url(url)
//                .post(body)
                .build();
        Response response = client.newCall(request).execute();

        String res = response.body().string();
        Log.i("TAG", res);
        try {
            JSONArray jarray = new JSONArray(res);
            for(int i=0;i<jarray.length();i++) {
                JSONObject jobject = jarray.getJSONObject(i);
                result = jobject.getString("result");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        handler.sendEmptyMessage(2);
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {

            switch (msg.what) {
                case 1:
                    if (pd != null)
                        pd.cancel();

                    processParsing(mThread, upload);

                    break;
                case 2:
                    if (pd != null)
                        pd.cancel();

                    Toast.makeText(MainActivity.this, "업로드까지 완료", Toast.LENGTH_SHORT).show();

                    break;
            }
        }
    };
}
