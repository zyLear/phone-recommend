package com.example.administrator.personalizationrecommendation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.RadialGradient;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Setting extends AppCompatActivity {

    private String myIP = MainActivity.ip;
    private TextView account;
    private EditText oldPassword;
    private EditText newPassword;
    private RadioGroup gender;
    private RadioGroup status;
    private EditText age;
    private RadioButton student;
    private RadioButton working;
    private RadioButton other;
    private RadioButton male;
    private RadioButton female;
    private Button preferenceSetting;
    private Button logout;
    private Button sureChangePassword;
    private Button sureChangeInfo;
    private Button exit;
    private Button back;

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            Toast.makeText(getApplicationContext(), message.getData().getString("msg"), Toast.LENGTH_SHORT).show();
        }
    };

    private Handler textViewHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            try {
                JSONObject jsonObject = new JSONObject(message.getData().getString("msg"));
                account.setText(getSharePreferencesValue("currentAccount"));
                age.setText(jsonObject.getString("age"));
                switch (jsonObject.getString("gender")) {
                    case "male":
                        male.setChecked(true);
                        break;
                    case "female":
                        female.setChecked(true);
                        break;
                }
                switch (jsonObject.getString("status")) {
                    case "student":
                        student.setChecked(true);
                        break;
                    case "working":
                        working.setChecked(true);
                        break;
                    case "other":
                        other.setChecked(true);
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "出错了！", Toast.LENGTH_SHORT).show();
            }
        }
    };


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemApplication.getInstance().addActivity(this);
        setContentView(R.layout.activity_setting);

        account = (TextView) findViewById(R.id.settingAccount);
        oldPassword = (EditText) findViewById(R.id.settingOldPassword);
        newPassword = (EditText) findViewById(R.id.settingNewPassword);
        gender = (RadioGroup) findViewById(R.id.settingGender);
        status = (RadioGroup) findViewById(R.id.settingStatus);
        age = (EditText) findViewById(R.id.settingAge);
        student = (RadioButton) findViewById(R.id.settingStudent);
        working = (RadioButton) findViewById(R.id.settingWorking);
        other = (RadioButton) findViewById(R.id.settingOther);
        male = (RadioButton) findViewById(R.id.settingMale);
        female = (RadioButton) findViewById(R.id.settingFemale);
        preferenceSetting = (Button) findViewById(R.id.preferenceSetting);
        logout = (Button) findViewById(R.id.logout);
        sureChangeInfo = (Button) findViewById(R.id.sureChangeInfo);
        sureChangePassword = (Button) findViewById(R.id.sureChangePassword);
        exit = (Button) findViewById(R.id.exit);
        back = (Button) findViewById(R.id.back);

        initPersonalInfo();

        back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP
                        && event.getX() > 0 && event.getX() < back.getWidth()
                        && event.getY() > 0 && event.getY() < back.getHeight()) {
                    Setting.this.finish();
                }
                return false;
            }
        });

        preferenceSetting.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP
                        && event.getX() > 0 && event.getX() < preferenceSetting.getWidth()
                        && event.getY() > 0 && event.getY() < preferenceSetting.getHeight()) {
                    Intent intent = new Intent(Setting.this, Preference.class);
                    startActivity(intent);
                }
                return false;
            }
        });

        logout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP
                        && event.getX() > 0 && event.getX() < logout.getWidth()
                        && event.getY() > 0 && event.getY() < logout.getHeight()) {
                    setSharedPreferencesValue("none!", "none!");
                    Intent intent = new Intent(Setting.this, Login.class);
                    startActivity(intent);
                }
                return false;
            }
        });

        sureChangePassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP
                        && event.getX() > 0 && event.getX() < sureChangePassword.getWidth()
                        && event.getY() > 0 && event.getY() < sureChangePassword.getHeight()) {
                    changePassword();
                }
                return false;
            }
        });

        sureChangeInfo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP
                        && event.getX() > 0 && event.getX() < sureChangeInfo.getWidth()
                        && event.getY() > 0 && event.getY() < sureChangeInfo.getHeight()) {
                    changeInfo();
                }
                return false;
            }
        });

        exit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP
                        && event.getX() > 0 && event.getX() < exit.getWidth()
                        && event.getY() > 0 && event.getY() < exit.getHeight()) {
                    exitApplication();
                }
                return false;
            }
        });
    }


    private void exitApplication() {
        SystemApplication.getInstance().exit();
    }

    //修改密码函数
    private void changePassword() {
        final String oldPasswordText = oldPassword.getText().toString();
        final String newPasswordText = newPassword.getText().toString();

        if (oldPasswordText.matches("[a-zA-Z0-9]{6,16}") || newPasswordText.matches("[a-zA-Z0-9]{6,16}")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL("http://" + myIP + "/Project/changePassword.do?account="
                                + getSharePreferencesValue("currentAccount")
                                + "&oldPassword=" + oldPasswordText
                                + "&newPassword=" + newPasswordText);
                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                        int code = httpURLConnection.getResponseCode();
                        if (code == 200) {
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                            String result = "";
                            result = bufferedReader.readLine();
                            bufferedReader.close();
                            if ("ok".equals(result)) {
                                Setting.this.sendMessageToUI("修改密码成功");
                            } else {
                                Setting.this.sendMessageToUI("原密码错误，修改失败!");
                            }
                        } else {
                            Setting.this.sendMessageToUI("连接服务器失败，请检查网络!");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Setting.this.sendMessageToUI("错误!");
                    }
                }
            }).start();
        } else {
            Toast.makeText(this, "密码为六到十六位的数字或字母", Toast.LENGTH_SHORT).show();
        }

    }

    private void changeInfo() {
        final String ageText = age.getText().toString();
        if (ageText.matches("[0-9]{1,3}")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL("http://" + myIP + "/Project/changeInfo.do?account="
                                + getSharePreferencesValue("currentAccount")
                                + "&age=" + ageText
                                + "&gender=" + getGender()
                                + "&status=" + getStatus());
                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                        int code = httpURLConnection.getResponseCode();
                        if (code == 200) {
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                            String result = "";
                            result = bufferedReader.readLine();
                            bufferedReader.close();
                            if ("ok".equals(result)) {
                                Setting.this.sendMessageToUI("保存信息成功");
                            } else {
                                Setting.this.sendMessageToUI("原密码错误，修改失败!");
                            }
                        } else {
                            Setting.this.sendMessageToUI("连接服务器失败，请检查网络!");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Setting.this.sendMessageToUI("错误!");
                    }
                }
            }).start();
        } else {
            Toast.makeText(this, "请合理输入年龄", Toast.LENGTH_SHORT);
        }
    }

    private void showPersonalInfo(String string) {
        Bundle bundle = new Bundle();
        bundle.putString("msg", string);
        Message message = new Message();
        message.setData(bundle);
        textViewHandler.sendMessage(message);
    }

    private void initPersonalInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String currentAccount = getSharePreferencesValue("currentAccount");
                    URL url = new URL("http://" + myIP + "/Project/getPersonalInfo.do?account=" + currentAccount);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    int code = httpURLConnection.getResponseCode();
                    if (code == 200) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        String result = "";
                        result = bufferedReader.readLine();
                        bufferedReader.close();
                        if (!"error".equals(result)) {
                            System.out.println("setInfo");
                            showPersonalInfo(result);
                        } else {
                            Setting.this.sendMessageToUI("获取个人信息失败!");
                        }
                    } else {
                        Setting.this.sendMessageToUI("连接服务器失败，请检查网络!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Setting.this.sendMessageToUI("错误!");
                }
            }
        }).start();
    }

    private void setSharedPreferencesValue(String account, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("myAccount", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("account", account);
        editor.putString("password", password);
        editor.commit();
    }

    private String getSharePreferencesValue(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences("myAccount", MODE_PRIVATE);
        return sharedPreferences.getString(key, "none!");
    }

    private String getGender() {
        String result = "";
        switch (gender.getCheckedRadioButtonId()) {
            case R.id.settingMale:
                result = "male";
                break;
            case R.id.settingFemale:
                result = "female";
                break;
        }
        return result;
    }

    private String getStatus() {
        String result = "";
        switch (status.getCheckedRadioButtonId()) {
            case R.id.settingStudent:
                result = "student";
                break;
            case R.id.settingWorking:
                result = "working";
                break;
            case R.id.settingOther:
                result = "other";
                break;
        }
        return result;
    }


    private void sendMessageToUI(String string) {
        Bundle bundle = new Bundle();
        bundle.putString("msg", string);
        Message message = new Message();
        message.setData(bundle);
        myHandler.sendMessage(message);
    }

}
