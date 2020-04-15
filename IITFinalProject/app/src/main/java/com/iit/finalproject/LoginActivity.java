package com.iit.finalproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.iit.finalproject.Utility.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    SharedPreferences pref;

    SignInButton Google_Login;
    private FirebaseAuth mAuth;
    GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;
    public static final int RC_SIGN_IN = 0;

    Thread mThread = null;
    JSONObject obj;

    String email;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref = getSharedPreferences("pref", MODE_PRIVATE);
        final boolean login_flag = pref.getBoolean("login", false);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
        mAuth = FirebaseAuth.getInstance();

        Google_Login = findViewById(R.id.Google_Login);
        Google_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent,RC_SIGN_IN);
            }
        });

        if(login_flag){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                // firebaseAuthWithGoogle(account);

                email = account.getEmail();
                name = account.getDisplayName();

                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            login(URL.LOGIN);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                mThread.start();
            }
            else{
                // Error
                Toast.makeText(this, "Google Login Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void login(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody req_body = new FormBody.Builder()
                .add("name", name)
                .add("email", email)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(req_body)
                .build();

        Response response = client.newCall(request).execute();
        String res = response.body().string();
        Log.i("TAG", res);
        try {
            obj = new JSONObject(res);
            int result = obj.getInt("result");
            if(result == 0)
                handler.sendEmptyMessage(0);
            else
                handler.sendEmptyMessage(1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(LoginActivity.this, "Welcome, " + name, Toast.LENGTH_SHORT).show();

                    JSONArray data = null;
                    int user_id = 0;
                    String user_name = "";
                    int user_position = 0;
                    try {
                        data = obj.getJSONArray("data");
                        JSONObject data_obj = data.getJSONObject(0);
                        user_id = data_obj.getInt("id");
                        user_position = data_obj.getInt("position");
                    } catch (JSONException e) {
                        Toast.makeText(LoginActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                        finish();
                        e.printStackTrace();
                    }

                    pref = getSharedPreferences("pref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("login", true);
                    editor.putInt("user_id", user_id);
                    editor.putString("user_name", user_name);
                    editor.putInt("user_position", user_position);
                    editor.commit();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case 1:
                    Toast.makeText(LoginActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

//    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
//        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
//        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if (task.isSuccessful()) {
//                    FirebaseUser user = mAuth.getCurrentUser();
//                } else {
//                    Toast.makeText(LoginActivity.this, "Firebase Error", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
