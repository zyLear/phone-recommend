package com.example.administrator.personalizationrecommendation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    static public String ip="192.168.253.1:12000";//"192.168.0.107:12000";
    private WebView allPhoneInfo;
    private WebView webRecommend;
    private WebView peopleLike;
    private RelativeLayout aboutMe;
    private ToggleButton all;
    private ToggleButton me;
    private ToggleButton recommend;
    private Button setting;

    private TextView account;
    private TextView age;
    private TextView status;
    private TextView gender;

    private String myIP=ip;
    public Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message message) {
            Toast.makeText(getApplicationContext(), message.getData().getString("msg"), Toast.LENGTH_SHORT).show();
        }
    };
    private Handler textViewHandler = new Handler(){
        @Override
        public void handleMessage(Message message) {
            try {
                JSONObject jsonObject =new JSONObject(message.getData().getString("msg"));
                account.setText(jsonObject.getString("account"));
                age.setText(jsonObject.getString("age"));
                switch (jsonObject.getString("gender")) {
                    case "male":gender.setText("男");break;
                    case "female":gender.setText("女");break;
                }
                switch (jsonObject.getString("status")) {
                    case "student":status.setText("学生");break;
                    case "working":status.setText("上班族");break;
                    case "other":status.setText("其他");break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"出错了！", Toast.LENGTH_SHORT).show();
            }
        }
    };



    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
//    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_main);
        allPhoneInfo = (WebView) findViewById(R.id.allPhoneInfo);
        webRecommend = (WebView) findViewById(R.id.webRecommend);
        peopleLike = (WebView) findViewById(R.id.webMe);
        all = (ToggleButton) findViewById(R.id.all);
        me = (ToggleButton) findViewById(R.id.me);
        recommend = (ToggleButton) findViewById(R.id.recommend);
        aboutMe = (RelativeLayout) findViewById(R.id.aboutMe);
        setting=(Button)findViewById(R.id.setting);

        account = (TextView) findViewById(R.id.showAccount);
        age = (TextView) findViewById(R.id.showAge);
        status = (TextView) findViewById(R.id.showStatus);
        gender = (TextView) findViewById(R.id.showGender);




        webViewInit();
        initButtonEvent();
    }




    private void webViewInit() {
        WebSettings settings = allPhoneInfo.getSettings();/**/
        //settings.setSupportZoom(true);          //支持缩放
        //settings.setBuiltInZoomControls(true);  //启用内置缩放装置
        settings.setJavaScriptEnabled(true);    //启用JS脚本
        allPhoneInfo.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                startActivity(intent);
//                return true;
//                view.loadUrl(url);
//                return true;
                if(url.startsWith("http://"+myIP) ) {
                    view.loadUrl(url);
                    return true;
                }else{
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //网页加载时调用
            }
        });
        allPhoneInfo.loadUrl("http://"+myIP+"/Project/show_phone_info.do?peopleId=" + Integer.parseInt(getSharePreferencesValue("currentAccount")));
        allPhoneInfo.setHorizontalScrollBarEnabled(false);
        allPhoneInfo.setWebChromeClient(new WebChromeClient());



        settings = webRecommend.getSettings();
        settings.setJavaScriptEnabled(true);
        webRecommend.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.startsWith("http://"+myIP) ) {
                    view.loadUrl(url);
                    return true;
                }else{
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //网页加载时调用
            }
        });
        try {
            webRecommend.loadUrl("http://"+myIP+"/Project/recommend.do?peopleId=" + Integer.parseInt(getSharePreferencesValue("currentAccount")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        webRecommend.setHorizontalScrollBarEnabled(false);


        settings = peopleLike.getSettings();
        settings.setJavaScriptEnabled(true);
        peopleLike.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.startsWith("http://"+myIP) ) {
                    view.loadUrl(url);
                    return true;
                }else{
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //网页加载时调用
            }
        });
        try {
            peopleLike.loadUrl("http://"+myIP+"/Project/people_like.do?peopleId=" + Integer.parseInt(getSharePreferencesValue("currentAccount")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        peopleLike.setHorizontalScrollBarEnabled(false);

    }

    private void initButtonEvent() {
        all.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP
                            && event.getX()> 0 &&event.getX()<all.getWidth()
                            && event.getY()> 0 &&event.getY()<all.getHeight()) {
                        allPhoneInfo.setVisibility(View.VISIBLE);
                        webRecommend.setVisibility(View.INVISIBLE);
                        aboutMe.setVisibility(View.INVISIBLE);
                        if (all.isChecked()) {
                            all.toggle();
                        }
                        recommend.setChecked(false);
                        me.setChecked(false);
                }
                return false;
            }
        });

        recommend.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP
                        && event.getX()> 0 &&event.getX()< recommend.getWidth()
                        && event.getY()> 0 &&event.getY()< recommend.getHeight()) {
                    allPhoneInfo.setVisibility(View.INVISIBLE);
                    webRecommend.setVisibility(View.VISIBLE);
                    aboutMe.setVisibility(View.INVISIBLE);
                    if (recommend.isChecked()) {
                        recommend.toggle();
                    }
                    all.setChecked(false);
                    me.setChecked(false);
                }
                return false;
            }
        });

        me.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP
                        && event.getX()> 0 &&event.getX()< me.getWidth()
                        && event.getY()> 0 &&event.getY()< me.getHeight()) {
                    allPhoneInfo.setVisibility(View.INVISIBLE);
                    webRecommend.setVisibility(View.INVISIBLE);
                    aboutMe.setVisibility(View.VISIBLE);
                    if (me.isChecked()) {
                        me.toggle();
                    }
                    all.setChecked(false);
                    recommend.setChecked(false);

                    try {
//                        webRecommend.loadUrl("http://"+myIP+"/Project/recommend.do?peopleId=" + Integer.parseInt(getSharePreferencesValue("currentAccount")));
                        peopleLike.loadUrl("http://"+myIP+"/Project/people_like.do?peopleId=" + Integer.parseInt(getSharePreferencesValue("currentAccount")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;

            }
        });

        setting.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP
                        && event.getX()> 0 &&event.getX()< setting.getWidth()
                        && event.getY()> 0 &&event.getY()< setting.getHeight()) {
                    Intent intent = new Intent(MainActivity.this, Setting.class);
                    startActivity(intent);
                }
                return false;
            }
        });

    }


//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK /*&& allPhoneInfo.canGoBack()*/) {
//            allPhoneInfo.goBack();// 返回前一个页面
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    protected void onResume() {
        super.onResume();
        getPersonalInfo();
       // Toast.makeText(this, "返回了返回了", Toast.LENGTH_LONG).show();
    }

    private String getSharePreferencesValue(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences("myAccount", MODE_PRIVATE);
        return sharedPreferences.getString(key,"none!");
    }

    private void setSharedPreferencesValue(String account,String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("myAccount", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("account",account);
        editor.putString("password", password);
        editor.commit();
    }


    private void sendMessageToUI(String string) {
        Bundle bundle=new Bundle();
        bundle.putString("msg",string);
        Message message=new Message();
        message.setData(bundle);
        myHandler.sendMessage(message);
    }

    private void showPersonalInfo(String string) {
        Bundle bundle=new Bundle();
        bundle.putString("msg",string);
        Message message=new Message();
        message.setData(bundle);
        textViewHandler.sendMessage(message);
    }


    private void getPersonalInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String currentAccount = getSharePreferencesValue("currentAccount");
                    URL url = new URL("http://"+myIP+"/Project/getPersonalInfo.do?account="+currentAccount);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    int code = httpURLConnection.getResponseCode();
                    if (code == 200) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                        String result="";
                        result=bufferedReader.readLine();
                        bufferedReader.close();
//                                System.out.println(result);
                        if (!"error".equals(result)) {
                            System.out.println("setInfo");
                            showPersonalInfo(result);
                        } else {
                            MainActivity.this.sendMessageToUI("获取个人信息失败!");
                        }
                    } else {
                        MainActivity.this.sendMessageToUI("连接服务器失败，请检查网络!");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.this.sendMessageToUI("错误!");
                }
            }
        }).start();
    }



    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
//    public Action getIndexApiAction() {
//        Thing object = new Thing.Builder()
//                .setName("Main Page") // TODO: Define a title for the content shown.
//                // TODO: Make sure this auto-generated URL is correct.
//                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
//                .build();
//        return new Action.Builder(Action.TYPE_VIEW)
//                .setObject(object)
//                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
//                .build();
//    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.connect();
//        AppIndex.AppIndexApi.start(client, getIndexApiAction());
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        AppIndex.AppIndexApi.end(client, getIndexApiAction());
//        client.disconnect();
//    }
}

