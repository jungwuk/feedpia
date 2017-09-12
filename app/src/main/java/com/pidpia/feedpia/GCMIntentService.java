package com.pidpia.feedpia;

/**
 * Created by jenorain on 2017-01-23.
 */


import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gcm.GCMBaseIntentService;


public class GCMIntentService extends GCMBaseIntentService {
    private static final String tag = "GCMIntentService";
    public static final String SEND_ID = "486801890898";

    User user;

    public GCMIntentService() {
        this(SEND_ID);
        // prefs = getSharedPreferences("ibss_kpu_bus", MODE_PRIVATE);
    }

    public GCMIntentService(String senderId) {
        super(senderId);
        //
    }

    /*
     * @Override protected void onMessage(Context context, Intent intent) {
     * Bundle b = intent.getExtras();
     *
     * Iterator<String> iterator = b.keySet().iterator();
     * while(iterator.hasNext()) { String key = iterator.next(); String value =
     * b.get(key).toString(); Log.d(tag, "onMessage. "+key+" : "+value); } }
     */
    Handler postHandler = new Handler();

    @Override
    protected void onMessage(final Context arg0, Intent arg1) {
        StringBuffer bufferMessageAll = new StringBuffer();
        Bundle bundle = arg1.getExtras();
        Set<String> setKey = bundle.keySet();
        Iterator<String> iterKey = setKey.iterator();

        user = new User(arg0);
        boolean status = user.getBoolean("app_status");


        while (iterKey.hasNext()) {
            String key = iterKey.next();
            String value = bundle.getString(key);
            Log.d("GCMIntentService", "onMessage. key = " + key + ", value = "
                    + value);
            String[] data = value.split("%div%");
            if (key.equals("notice_msg")) {
                setNotification(arg0, data[0], data[1]);
            }

        }
    }

    @Override
    protected void onError(Context context, String errorId) {
        Log.d(tag, "onError. errorId : " + errorId);
    }

    @Override
    protected void onRegistered(Context context, String regId) {
        Log.d(tag, "onRegistered. regId : " + regId);
    }

    @Override
    protected void onUnregistered(Context context, String regId) {
        Log.d(tag, "onUnregistered. regId : " + regId);
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        Log.d(tag, "onRecoverableError. errorId : " + errorId);
        return super.onRecoverableError(context, errorId);
    }

    public void showMessage(final Context context, final String message) {
        new Runnable() {
            @Override
            public void run() {
                Map<String, Object> map = new Hashtable<String, Object>();
                Message msg = new Message();
                msg.what = 0;
                map.put("message", message);
                map.put("context", context);
                msg.obj = map;
                handler.sendMessage(msg);
            }
        }.run();
    }

    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Map<String, Object> map = (Hashtable<String, Object>) msg.obj;
            String message = (String) map.get("message");
            Context context = (Context) map.get("context");
            Toast.makeText(context, "수신 메시지 : " + message, Toast.LENGTH_LONG)
                    .show();
        }
    };

    /*
    private void setNotification(Context context, String title, String message) {
        NotificationManager notificationManager = null;
        Notification notification = null;
        try {
            notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notification = new Notification(R.drawable.logo2, message,
                    System.currentTimeMillis());
            notification.defaults |= Notification.DEFAULT_SOUND;
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            // notification.flags|= Notification.FLAG_INSISTENT;
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
           notification.setLatestEventInfo(context, title, message, pi);
            notificationManager.notify(0, notification);

        } catch (Exception e) {
            Log.d("MSG : ", "[setNotification] Exception : " + e.getMessage());
        }
        // ((MainActivity)getApplicationContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        // | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        // | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        // | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        Vibrator vibes = (Vibrator) context
                .getSystemService(Context.VIBRATOR_SERVICE);
        vibes.vibrate(400);
        // vibes.wait();
        // vibes.vibrate(200);

    }
    */

    private void setNotification(Context context, String title, String message) {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("msg_title", title);
        intent.putExtra("msg_content", message);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        Notification.Builder mBuilder = new Notification.Builder(this);
        mBuilder.setSmallIcon(R.drawable.cast_ic_mini_controller_pause);
        mBuilder.setTicker(title);
        mBuilder.setWhen(System.currentTimeMillis());
        mBuilder.setNumber(10);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(message);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);

//        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);

//        nm.notify(111, mBuilder.build());

    }


    private boolean isActivityTop(Context context) {

        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> info;

        info = activityManager.getRunningTasks(1);

        if (info.get(0).topActivity.getClassName().equals(context.getClass().getName())) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean isRunningProcess(Context context, String packageName) {

        boolean isRunning = false;

        ActivityManager actMng = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> list = actMng.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo rap : list) {
            if (rap.processName.equals(packageName)) {
                isRunning = true;
                break;
            }
        }

        return isRunning;
    }
}


