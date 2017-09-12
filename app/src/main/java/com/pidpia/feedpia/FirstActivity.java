package com.pidpia.feedpia;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

public class FirstActivity extends AppCompatActivity {


    User user;
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case 100:
                    if (client.recvData != null) {
                        try {
                            JSONObject data = new JSONObject(client.recvData);
                            user.setData("FID", data.getString("FID"));
                            user.setData("UID", data.getString("UID"));
                            user.setData("SID", data.getString("SID"));
                            if (data.getBoolean("status")) {
                                user.setData("user_id", data.getString("user_id"));
                                user.setData("user_passwd", data.getString("user_passwd"));

                                user.setData("user_name", data.getString("user_name"));
                                user.setData("user_logo", data.getString("user_logo"));
                                user.setData("user_back", data.getString("user_back"));

                                Intent intent = new Intent(FirstActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                user.setData("user_id", "");
                                user.setData("user_passwd", "");
                                user.Msg(data.getString("msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    };

    WebClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        user = new User(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
            }
        }


            FirebaseMessaging.getInstance().subscribeToTopic("news");
            FirebaseInstanceId.getInstance().getToken();


            final String user_ids = user.getData("user_id");
            final String user_passwds = user.getData("user_passwd");


            Handler mHandler1 = new Handler() {
                public void handleMessage(Message msg) {


                    if (user_ids.length() > 0 && user_passwds.length() > 0) {

                        if (user.getData("user_id").length() > 0 && user.getData("user_passwd").length() > 0) {

                            JSONObject obj2 = new JSONObject();
                            try {
                                obj2.put("user_id", user.getData("user_id"));
                                obj2.put("user_passwd", user.getData("user_passwd"));
                                obj2.put("FCM", user.getData("FCM"));
                                client = new WebClient(user.URL_LOGIN, obj2.toString(), mHandler, 100);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            // startActivity(intent);
                        }


                    } else {
                        Intent intent = new Intent(FirstActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }
            };

            mHandler1.sendEmptyMessageDelayed(0, 2000);

        }





}
