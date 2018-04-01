package com.example.administrator.personalizationrecommendation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Login extends AppCompatActivity {

    private Button login;
    private Button register;
    private EditText account;
    private EditText password;
    private CheckBox autoLogin;
    private String myIP=MainActivity.ip;
    public Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message message) {
            Toast.makeText(getApplicationContext(), message.getData().getString("msg"), Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemApplication.getInstance().addActivity(this);
        setContentView(R.layout.activity_login);


        account = (EditText) findViewById(R.id.account);
        password = (EditText) findViewById(R.id.password);
        autoLogin = (CheckBox) findViewById(R.id.autoLogin);
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String accountText = account.getText().toString();
                final String passwordText = password.getText().toString();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int accountToInt = Integer.parseInt(accountText);
                            URL url = new URL("http://"+myIP+"/Project/login.do?account="+accountToInt+"&password="+passwordText);
                            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                            int code = httpURLConnection.getResponseCode();
                            if (code == 200) {
                                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                                String result="";
                                result=bufferedReader.readLine();
                                bufferedReader.close();
//                                System.out.println(result);
                                if ("ok".equals(result)) {
                                    if (autoLogin.isChecked()) {
                                        setSharedPreferencesValue(accountText,passwordText);
                                    }else{
                                        setSharedPreferencesValue("none!","none!");
                                    }
                                    setSharedPreferencesValue(accountText);
                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Login.this.sendMessageToUI("用户名或密码错误!");
                                }
                            } else {
                                Login.this.sendMessageToUI("连接服务器失败，请检查网络!");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Login.this.sendMessageToUI("错误!");
                        }
                    }
                }).start();

            }
        });
        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });





        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    private void sendMessageToUI(String string) {
        Bundle bundle=new Bundle();
        bundle.putString("msg",string);
        Message message=new Message();
        message.setData(bundle);
        myHandler.sendMessage(message);
    }

    private void setSharedPreferencesValue(String account,String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("myAccount", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("account",account);
        editor.putString("password", password);
        editor.commit();
    }

    private void setSharedPreferencesValue(String account) {
        SharedPreferences sharedPreferences = getSharedPreferences("myAccount", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("currentAccount",account);
        editor.commit();
    }

//    private String getSharePreferencesValue(String key) {
//        SharedPreferences sharedPreferences = getSharedPreferences("myAccount", MODE_PRIVATE);
//        return sharedPreferences.getString(key,"none!");
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }







    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Login Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
