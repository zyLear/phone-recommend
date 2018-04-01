package com.example.administrator.personalizationrecommendation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Preference extends AppCompatActivity {

    private String myIP = MainActivity.ip;
    private SeekBar brand;
    private SeekBar price;
    private SeekBar size;
    private SeekBar ram;
    private SeekBar pixel;
    private SeekBar rom;
    private SeekBar cpu;
    private Button savePreference;
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
                brand.setProgress(jsonObject.getInt("brand"));
                price.setProgress(jsonObject.getInt("price"));
                size.setProgress(jsonObject.getInt("size"));
                ram.setProgress(jsonObject.getInt("ram"));
                pixel.setProgress(jsonObject.getInt("pixel"));
                rom.setProgress(jsonObject.getInt("rom"));
                cpu.setProgress(jsonObject.getInt("cpu"));
            } catch (JSONException e) {
                e.printStackTrace();
                Preference.this.sendMessageToUI("出错了!");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemApplication.getInstance().addActivity(this);
        setContentView(R.layout.activity_preference);
        brand = (SeekBar) findViewById(R.id.brand);
        price = (SeekBar) findViewById(R.id.price);
        size = (SeekBar) findViewById(R.id.size);
        ram = (SeekBar) findViewById(R.id.ram);
        pixel = (SeekBar) findViewById(R.id.pixel);
        rom = (SeekBar) findViewById(R.id.rom);
        cpu = (SeekBar) findViewById(R.id.cpu);
        savePreference = (Button) findViewById(R.id.savePrefenrence);
        back = (Button) findViewById(R.id.back);

        back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP
                        && event.getX() > 0 && event.getX() < back.getWidth()
                        && event.getY() > 0 && event.getY() < back.getHeight()) {
                    Preference.this.finish();
                }
                return false;
            }
        });

        savePreference.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP
                        && event.getX() > 0 && event.getX() < savePreference.getWidth()
                        && event.getY() > 0 && event.getY() < savePreference.getHeight()) {
                    sureSavePreference();
                }
                return false;
            }
        });

        initPreference();

    }

    private void initPreference() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String currentAccount = getSharePreferencesValue("currentAccount");
                    URL url = new URL("http://" + myIP + "/Project/getPreference.do?account=" + currentAccount);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    int code = httpURLConnection.getResponseCode();
                    if (code == 200) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        String result = "";
                        result = bufferedReader.readLine();
                        bufferedReader.close();
                        if (!"error".equals(result)) {
                            System.out.println("setInfo");
                            showPreference(result);
                        } else {
                            Preference.this.sendMessageToUI("获取偏好失败!");
                        }
                    } else {
                        Preference.this.sendMessageToUI("连接服务器失败，请检查网络!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Preference.this.sendMessageToUI("错误!");
                }
            }
        }).start();
    }


    private void sureSavePreference() {

        final int brandText = brand.getProgress();
        final int priceText = price.getProgress();
        final int sizeText = size.getProgress();
        final int ramText = ram.getProgress();
        final int pixelText = pixel.getProgress();
        final int romText = rom.getProgress();
        final int cpuText = cpu.getProgress();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("account", Integer.parseInt(getSharePreferencesValue("currentAccount")));
                    jsonObject.put("brand", brandText);
                    jsonObject.put("price", priceText);
                    jsonObject.put("size", sizeText);
                    jsonObject.put("ram", ramText);
                    jsonObject.put("pixel", pixelText);
                    jsonObject.put("rom", romText);
                    jsonObject.put("cpu", cpuText);
                    jsonObject.put("total", brandText + priceText + sizeText + ramText + pixelText + romText + cpuText);


                    URL url = new URL("http://" + myIP + "/Project/changePreference.do");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream()));
                    bufferedWriter.write("preference=" + jsonObject);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    int code = httpURLConnection.getResponseCode();
                    if (code == 200) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        String result = "";
                        result = bufferedReader.readLine();
                        bufferedReader.close();
                        if ("ok".equals(result)) {
                            Preference.this.sendMessageToUI("保存偏好成功");
                        } else {
                            Preference.this.sendMessageToUI("修改失败!");
                        }
                    } else {
                        Preference.this.sendMessageToUI("连接服务器失败，请检查网络!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Preference.this.sendMessageToUI("错误!");
                }
            }
        }).start();
    }

    private void sendMessageToUI(String string) {
        Bundle bundle = new Bundle();
        bundle.putString("msg", string);
        Message message = new Message();
        message.setData(bundle);
        myHandler.sendMessage(message);
    }

    private String getSharePreferencesValue(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences("myAccount", MODE_PRIVATE);
        return sharedPreferences.getString(key, "none!");
    }

    private void showPreference(String string) {
        Bundle bundle = new Bundle();
        bundle.putString("msg", string);
        Message message = new Message();
        message.setData(bundle);
        textViewHandler.sendMessage(message);

    }

}
