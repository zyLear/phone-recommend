package com.example.administrator.personalizationrecommendation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AutoLogin extends AppCompatActivity {

    private String myIP=MainActivity.ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemApplication.getInstance().addActivity(this);
        setContentView(R.layout.activity_auto_login);
        judge();
    }

    private void judge() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String accountText = (getSharePreferencesValue("account"));
                    int account = Integer.parseInt(accountText);
                    String password = getSharePreferencesValue("password");
                    URL url = new URL("http://"+myIP+"/Project/login.do?account="+account+"&password="+password);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    int code = httpURLConnection.getResponseCode();
                    if (code == 200) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        String result="";
                        result=bufferedReader.readLine();
                        bufferedReader.close();
                        if ("ok".equals(result)) {
                            setSharedPreferencesValue(accountText);
                            goToMain();
                        } else {
                            goToLogin();
                        }
                    } else {
                        goToLogin();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    goToLogin();
                }
            }
        }).start();

    }

    private String getSharePreferencesValue(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences("myAccount", MODE_PRIVATE);
        return sharedPreferences.getString(key,"none!");
    }

    private void goToLogin() {
        Intent intent = new Intent(AutoLogin.this, Login.class);
        startActivity(intent);
    }

    private void goToMain() {
        Intent intent = new Intent(AutoLogin.this, MainActivity.class);
        startActivity(intent);
    }

    private void setSharedPreferencesValue(String account) {
        SharedPreferences sharedPreferences = getSharedPreferences("myAccount", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("currentAccount",account);
        editor.commit();
    }
}
