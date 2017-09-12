package com.pidpia.feedpia;

/**
 * Created by jenorain on 2017-02-24.
 */


import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String token = FirebaseInstanceId.getInstance().getToken();
        //Log.d(TAG, "Refreshed token: " + token);

        Log.d("Token",token);

        // 생성등록된 토큰을 개인 앱서버에 보내 저장해 두었다가 추가 뭔가를 하고 싶으면 할 수 있도록 한다.
        sendRegistrationToServer(token);
    }

    public  String getData(String name){
        return prefs.getString(name,"" );
    }
    public void setData(String name,String data){
        editor.putString(name,data );
        editor.commit();
    }
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.

        prefs = getBaseContext().getSharedPreferences("feedpia", getBaseContext().MODE_PRIVATE);
        editor = prefs.edit();

        setData("FCM",token);
        Log.d("FCM",token);
//
//        OkHttpClient client = new OkHttpClient();
//        RequestBody body = new FormBody.Builder()
//                .add("Token", token)
//                .add("FID", getData("FID"))
//                .add("UID", getData("UID"))
//                .build();
//        //request
//        Request request = new Request.Builder().url("http://hm.ibss.co.kr/fcm.php").post(body) .build();

//        try {
//            client.newCall(request).execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}