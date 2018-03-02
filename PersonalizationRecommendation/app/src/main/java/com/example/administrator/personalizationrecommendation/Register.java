package com.example.administrator.personalizationrecommendation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Register extends AppCompatActivity {

    private EditText account;
    private EditText password;
    private EditText surePassword;
    private EditText age;
    private RadioGroup gender;
    private RadioGroup status;
    private String genderText = "male";
    private String statusText = "student";
    private String myIP=MainActivity.ip;
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            Toast.makeText(getApplicationContext(), message.getData().getString("msg"), Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        account = (EditText) findViewById(R.id.registerAccount);
        password = (EditText) findViewById(R.id.settingOldPassword);
        surePassword = (EditText) findViewById(R.id.registerSurePassword);
        age = (EditText) findViewById(R.id.registerAge);
        gender = (RadioGroup) findViewById(R.id.gender);
        status = (RadioGroup) findViewById(R.id.status);


        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.male:
                        genderText = "male";
                        break;
                    case R.id.female:
                        genderText = "female";
                        break;
                    default:
                        break;
                }
                Toast.makeText(Register.this, genderText, Toast.LENGTH_SHORT).show();
            }
        });

        status.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.student:
                        statusText = "student";
                        break;
                    case R.id.working:
                        statusText = "working";
                        break;
                    case R.id.other:
                        statusText = "other";
                        break;
                    default:
                        break;
                }
                Toast.makeText(Register.this, statusText, Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.sureRegister).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    final String accountText = account.getText().toString();
                    final String passwordText = password.getText().toString();
                    final String ageText = age.getText().toString();
                    String surePasswordText = surePassword.getText().toString();
                    if (!accountText.matches("[0-9]{6,16}") || "".equals(surePasswordText)) {
                        Toast.makeText(Register.this, "账号为六到十六位数字", Toast.LENGTH_SHORT).show();
                    } else if (!passwordText.matches("[a-zA-Z0-9]{6,16}")) {
                        Toast.makeText(Register.this, "密码为六到十六位字母或数字", Toast.LENGTH_SHORT).show();
                    } else if (!ageText.matches("[0-9]{1,3}")) {
                        Toast.makeText(Register.this, "请合理输入年龄", Toast.LENGTH_SHORT).show();
                    } else if (!passwordText.equals(surePasswordText)) {
                        Toast.makeText(Register.this, "两次输入密码不一致", Toast.LENGTH_SHORT).show();
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    URL url = new URL("http://"+myIP+"/Project/register.do?account=" +
                                            accountText+"&password="+passwordText+"&age="+ageText+"&gender=" +
                                            genderText+"&status="+statusText);
                                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                                    int code = httpURLConnection.getResponseCode();
                                    if (code == 200) {
                                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                                        String result = "";
                                        result = bufferedReader.readLine();
                                        bufferedReader.close();
                                        if ("ok".equals(result)) {
                                            setSharedPreferencesValue(accountText, passwordText);
                                            setSharedPreferencesValue(accountText);
                                            Intent intent = new Intent(Register.this, MainActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Register.this.sendMessageToUI("注册不成功，请换个账号注册!");
                                        }
                                    } else {
                                        Register.this.sendMessageToUI("连接服务器失败，请检查网络!");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Register.this.sendMessageToUI("错误!");
                                }
                            }
                        }).start();
                    }
                }
                return false;
            }
        });
    }

    private void sendMessageToUI(String string) {
        Bundle bundle = new Bundle();
        bundle.putString("msg", string);
        Message message = new Message();
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
}
