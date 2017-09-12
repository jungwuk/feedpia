package com.pidpia.feedpia;

/**
 * Created by jenorain on 2017-02-10.
 */


import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jenorain on 2016-12-15.
 */

public class WebClient  extends Thread {

    String URL;
    String sendData;
    String recvData;

    Handler mHandler;
    int msg;
    public WebClient(String url, String data,Handler mHandler ,int msg ){
        this.URL=url;
        this.sendData= data;
        this.mHandler  = mHandler;
        this.msg = msg;
        this.start();
    }

    public WebClient(String url, Object data, Handler mHandler , int msg ){
        Gson gson = new Gson();
        this.URL=url;
        this.sendData= gson.toJson(data);
        this.mHandler  = mHandler;
        this.msg = msg;
        this.start();
    }


    public void run() {

        InputStream is = null;
        String result = "";
        JSONArray obj=null;

        try {
            URL urlCon = new URL(URL);
            HttpURLConnection httpCon = (HttpURLConnection) urlCon.openConnection();

            String json = sendData;

            // Set some headers to inform server about the type of the content
//            httpCon.setRequestMethod("POST");
//            httpCon.setRequestProperty("Accept", "application/json");
//            httpCon.setRequestProperty("Content-type", "application/json");
            httpCon.setRequestProperty("Cache-Control", "no-cache");
            httpCon.setRequestProperty("Content-Type", "application/json");
            httpCon.setRequestProperty("Accept", "application/json");
            // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
            httpCon.setDoOutput(true);
            // InputStream으로 서버로 부터 응답을 받겠다는 옵션.
            httpCon.setDoInput(true);

            OutputStream os = httpCon.getOutputStream();
            os.write(json.getBytes());
            os.flush();

            // receive response as inputStream
            try {
                is = httpCon.getInputStream();
                // convert inputstream to string
                if (is != null)
                    result = convertInputStreamToString(is);
                else
                    result = "Did not work!";

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("ERROR1", e.toString());
            } finally {
                Log.d("JSON", result);
                if(result != null) {
                    recvData=result;
                }else{
                    recvData="";
                }

                httpCon.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("ERROR2", e.toString());
        } catch (Exception e) {
            Log.d("ERROR3", e.toString());
        }

        mHandler.sendEmptyMessage(msg);

    }


    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }


}
