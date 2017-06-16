package sportsfactory.com.httpsample;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    Context context = this;

    public static ProgressDialog pd = null;
    private Thread mThread = null;
    String response = null;

    String result="defualt";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        processParsing(mThread, sample);

    }

    private final Runnable sample = new Runnable() {
        @Override
        public void run() {
            try {
                post("http://hongikmagics.com/test/jsontest.json", "");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

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

    public void post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
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

        handler.sendEmptyMessage(1);
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {

            switch (msg.what) {
                case 1:
                    if (pd != null)
                        pd.cancel();

                    Toast.makeText(context, result, Toast.LENGTH_SHORT).show();

                    break;
                case 2:
                    if (pd != null)
                        pd.cancel();

                    break;
            }
        }
    };

}
