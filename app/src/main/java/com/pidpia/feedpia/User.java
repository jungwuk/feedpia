package com.pidpia.feedpia;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.google.android.gcm.GCMRegistrar;
/**
 * Created by jenorain on 2016-12-07.
 */

public class User {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context mContext;
    public String UUID;
    public String SSID;
    public String TID;

    String GCM="";
    public static String URL_DOMAIN="http://110.10.189.232/";
    public static String URL_FEED_EXTEND = URL_DOMAIN+"App_Extend_List.php";
    public static String URL_DELIVERY_LIST = URL_DOMAIN+"App_Delivery_List.php";
    public static String URL_UPLOAD_PICTURE = URL_DOMAIN+"App_Picture_Upload.php";
    public static String URL_LOGIN = URL_DOMAIN+"App_Login.php";
    public static String URL_ORDER_CANCEL = URL_DOMAIN+"App_Order_Cancel.php";
    public static String URL_DELIVEY_STATE = URL_DOMAIN+"App_Delivery_State.php";
    public static String URL_DELIVEY_GPS = URL_DOMAIN+"App_Delivery_GPS.php";
    public static String URL_FIND_PW = URL_DOMAIN+"App_find_pw.php";



    public User(Context context){
        mContext=context;
        prefs = mContext.getSharedPreferences("feedpia", mContext.MODE_PRIVATE);
        editor = prefs.edit();



//        GCM = GCMRegistrar.getRegistrationId(mContext);
//
//        if ("".equals(GCM)) {
//            GCMRegistrar.register(mContext, GCMIntentService.SEND_ID);
//            GCM = GCMRegistrar.getRegistrationId(mContext);
//
//            if ("".equals(GCM)) {
//                GCM = GCMRegistrar.getRegistrationId(mContext);
//            }
//        }else{
//
//        }
//
//        Log.d("GCM",GCM);

        try {
            UUID = (String) Build.class.getField("SERIAL").get(null);
        } catch (IllegalAccessException e) {
            UUID="";
        } catch (NoSuchFieldException e) {
            UUID="";
        }

        try {
            TelephonyManager manager = (TelephonyManager) mContext.getSystemService(mContext.TELEPHONY_SERVICE);
            TID = manager.getLine1Number();
            if(TID==null)  TID="";
        } catch (Exception ee) {
            TID = "";
        }

        SSID=getData("SSID");
    }



    public void setData(String name,String data){
        editor.putString(name,data );
        editor.commit();
    }
    public  String getData(String name){
        return prefs.getString(name,"" );
    }


    public void setBoolean(String name,boolean data){
        editor.putBoolean(name, data);
        editor.commit();
    }
    public  boolean getBoolean(String name){
        return prefs.getBoolean(name, false);
    }
    boolean InternetCheck() {

        boolean status = false;
        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                status = true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                status = true;
            }
        } else {
            // not connected to the internet
            status = false;
            Toast.makeText(mContext, "네트워크 연결이 불안정합니다.", Toast.LENGTH_SHORT).show();
        }
        return status;
    }
    String SendJson(String url,  String json_data){

        InputStream is = null;
        String result = "";
        try {
            URL urlCon = new URL(url);
            HttpURLConnection httpCon = (HttpURLConnection)urlCon.openConnection();

            String json = json_data;

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
                if(is != null)
                    result = convertInputStreamToString(is);
                else
                    result = "Did not work!";

            }
            catch (IOException e) {
                e.printStackTrace();
                Log.d("ERROR1",e.toString());
            }
            finally {
                Log.d("JSON",result);
                if(result.length()>0){


                }
                httpCon.disconnect();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.d("ERROR2",e.toString());
        }
        catch (Exception e) {
            Log.d("ERROR3",e.toString());

//            Log.d("InputStream", e.toString());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public  void Msg( String msg){
        Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
    }
    public  void DMsg( String msg){
        Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
    }




    long getUDate(String date) {
//        String dateString = "Fri, 09 Nov 2012 23:40:18 GMT";
//        DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+9"));
        long unixTime = 0;
        try {
            Date dates = dateFormat.parse(date);

            unixTime = (long) dates.getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return unixTime;
    }

    long getUDate(String date, String format) {
//        String dateString = "Fri, 09 Nov 2012 23:40:18 GMT";
//        DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z");
        DateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        long unixTime = 0;
        try {
            Date dates = dateFormat.parse(date);
            unixTime = (long) dates.getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return unixTime;
    }

    long getUDateNow() {

        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date currentTime = new Date();
        String mTime = mSimpleDateFormat.format(currentTime);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long unixTime = 0;
        try {
            Date dates = dateFormat.parse(mTime);
            unixTime = (long) dates.getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return unixTime;
    }

    long getUDateDay() {

        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00", Locale.US);
        Date currentTime = new Date();
        String mTime = mSimpleDateFormat.format(currentTime);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long unixTime = 0;
        try {
            Date dates = dateFormat.parse(mTime);
            unixTime = (long) dates.getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return unixTime;
    }
}
