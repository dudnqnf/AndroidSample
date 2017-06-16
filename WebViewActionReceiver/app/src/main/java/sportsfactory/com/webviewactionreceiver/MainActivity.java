package sportsfactory.com.webviewactionreceiver;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final Handler handler = new Handler();
    private TextView text;

    private WebView mWebView = null;
    private WebViewInterface mWebViewInterface;

    Context mContext;

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (TextView)findViewById(R.id.text);

        final WebView mWebView = (WebView)findViewById(R.id.webview);

        //안드로이드에서 메세지 받기
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebViewInterface = new WebViewInterface(MainActivity.this, mWebView); //JavascriptInterface 객체화
        mWebView.addJavascriptInterface(mWebViewInterface, "Android"); //웹뷰에 JavascriptInterface를 연결

        mWebView.loadUrl("http://www.hongikmagics.com/test/test4.html");
//        mWebView.setWebViewClient(new MyWebClient());

        Button test_btn = (Button)findViewById(R.id.test_btn);
        test_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //안드로이드에서 메세지 보내기
                mWebView.loadUrl("javascript:setMessage('asdasdasd')");
            }
        });



    }

    class MyWebClient extends WebViewClient {
        @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    String url = request.getUrl().toString();
                    if(url.startsWith("app")) {
                        Toast.makeText(mContext, "호출됨", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(mContext.getApplicationContext(), Main2Activity.class);
                        startActivity(intent);
                        return true;
                    }
                    else {
                        view.loadUrl(url);
                return true;
            }
        }
    }
}

//웹쪽 코드
//
//<!DOCTYPE html>
//<html>
//<head>
//<meta charset="utf-8">
//<link rel="stylesheet" type="text/css" href="style.css" >
//<script type="text/javascript" src="../javascript/jquery.js"></script>
//</head>
//<body>
//<div id=category>
//</div>
//
//<hr/>
//<h2>WebView와의 통신</h2>
//<hr/>
//<br/>
//        받은 메시지 :
//<p id="textMessageFromApp" style="height:200px; overflow-y:auto;">
//</p>
//<hr/>
//<a href="app://application">Activity 호출</a>
//<input type="text" id="textMessageToApp"  value="App으로 전송"/>
//<input type="button" value="Send Message" onclick="sendMessage('호출됨')" style="left:50%;"/>
//
//<script language="JavaScript">
//        function setMessage(arg) {
//        document.getElementById('textMessageFromApp').innerHTML = arg;
//        }
//        function sendMessage(msg){
//        window.Android.startIntent(msg);
//        }
//</script>
//
//</body>
//</html>
