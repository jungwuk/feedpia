package com.pidpia.feedpia;

import android.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    EditText login_user_id, login_user_passwd;
    Button login_btn_login;

    User user;

    WebView login_webview;
    ImageView img_login, img_preview;
    PopupWindow popupWindow;
    Geocoder coder;
    LocationManager locManager;
    LocationListener locationListener;
    double latPoint, lngPoint;
    ProgressBar progressBar;
    File storageFile;
    ImageButton btn_tutorial;

    Boolean auto_login_flag;

    WebClient client;
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
                                user.setBoolean("auto_login", auto_login_flag);

                                user.setData("user_name", data.getString("user_name"));
                                user.setData("user_logo", data.getString("user_logo"));
                                user.setData("user_back", data.getString("user_back"));

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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

                case 200:
                    if(client.recvData != null) {
                        try {
                            JSONObject data = new JSONObject(client.recvData);

                            Toast.makeText(LoginActivity.this, data.getString("status")+" "+data.getString("msg"), Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
            }


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user = new User(this);

        FirebaseMessaging.getInstance().subscribeToTopic("news");
        FirebaseInstanceId.getInstance().getToken();

        btn_tutorial = (ImageButton)findViewById(R.id.btn_tutorial);
        login_user_id = (EditText) findViewById(R.id.login_user_id);
        login_user_passwd = (EditText) findViewById(R.id.login_user_passwd);
        login_btn_login = (Button) findViewById(R.id.login_btn_login);
        img_login = (ImageView) findViewById(R.id.btn_member_join);
        login_webview = (WebView) findViewById(R.id.login_webview);

        progressBar = (ProgressBar) this.findViewById(R.id.webview_progress);
        auto_login_flag = user.getBoolean("auto_login");


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

        //자동 로그인
        if (user.getBoolean("auto_login")) {

            if (user.getData("user_id").length() > 0 && user.getData("user_passwd").length() > 0 ) {

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

        }

        try {
            storageFile = File.createTempFile("photo", ".jpg", this.getCacheDir());
            storageFile.setWritable(true, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //배경화면 애니메이션
        //scaleAnimate();

        img_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupLogin();
            }
        });
        btn_tutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,Tutorial.class);
                startActivity(intent);
            }
        });

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            Log.d("[LOC]", "Faield...1");

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        } else {


            coder = new Geocoder(this, Locale.KOREA);
            locManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location locations) {
                    String location_address = "";
                    latPoint = locations.getLatitude();
                    lngPoint = locations.getLongitude();

                    Log.d("[LOC]:", latPoint + " " + lngPoint + " - " + location_address);

                    user.setData("now_lat", String.valueOf(latPoint));
                    user.setData("now_lng", String.valueOf(lngPoint));
                    Log.d("GPS_SET",String.valueOf(latPoint) + "   " + String.valueOf(lngPoint));

                    try {
                        if (coder != null) {
                            List<Address> address = coder.getFromLocation(latPoint, lngPoint, 1);
                            if (address != null && address.size() > 0) {
                                location_address = address.get(0).getAddressLine(0).toString();
                                //  order_input_address.setText(location_address);
                                //Toast.makeText(LoginActivity.this, location_address, Toast.LENGTH_SHORT).show();

                                //처음 페이지 떳을때 주소값 띄우는 부분
                                //login_webview.loadUrl("javascript:setAddress('" + location_address + "');");

                                if (ActivityCompat.checkSelfPermission(LoginActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LoginActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.


                                    Log.d("[LOC]", "Faield...2");
                                    return;
                                }
                                locManager.removeUpdates(locationListener);
                            } else {
                                Toast.makeText(getApplicationContext(), "Address null.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (IOException e) {
                        // order_input_address.setText("");
                        Toast.makeText(getApplicationContext(), "위치정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                        locManager.removeUpdates(locationListener);
                        Log.d("location: error", e.toString());
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            };


        }
        user.setBoolean("join_flag", false);
        Log.d("[LOC]", "Start ok");
        if (user.getBoolean("join_flag")) {

            user.setData("user_id", user.getData("user_id"));
            user.setData("user_passwd", user.getData("user_passwd"));

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //  gps functionality
            Log.d("[LOC]:", "get permission ok");
        }
    }


    void PopupLogin() {


        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LoginActivity.this.LAYOUT_INFLATER_SERVICE);
        final View layout = layoutInflater.inflate(R.layout.popup_intro_login, null);

        //   ((EditText) layout.findViewById(R.id.edt_id)).setText("KA5831828498");
        //   ((EditText) layout.findViewById(R.id.edt_pass)).setText("4321");


        popupWindow = new PopupWindow(LoginActivity.this);
        popupWindow.setContentView(layout);
        popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);

        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        //popupWindow.setFocusable(true);

        //Clear the default translucent background
        //popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //popupWindow.showAsDropDown(toolbar);
        popupWindow.showAtLocation(layout, Gravity.CENTER, 0, -100);

        CheckBox login_auto_login = (CheckBox) layout.findViewById(R.id.login_auto_login);

        TextView tv_find_pw = (TextView)layout.findViewById(R.id.tv_find_pw);

        login_auto_login.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                auto_login_flag = isChecked;
            }
        });

        layout.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        //일반 회원가입
        layout.findViewById(R.id.btn_join_buyer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //회원가입화면
                popupWindow.dismiss();
            }
        });

        tv_find_pw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Popupfindpw();
            }
        });

        layout.findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user_id = ((EditText) layout.findViewById(R.id.edt_id)).getText().toString().trim();
                String user_passwd = ((EditText) layout.findViewById(R.id.edt_pass)).getText().toString().trim();

                if (user_id.length() > 0 && user_passwd.length() > 0) {

                    user.setData("user_id", user_id);
                    user.setData("user_passwd", user_passwd);
                    JSONObject obj2 = new JSONObject();
                    try {
                        obj2.put("user_id", user_id);
                        obj2.put("user_passwd", user_passwd);
                        obj2.put("FCM", user.getData("FCM"));
                        client = new WebClient(user.URL_LOGIN, obj2.toString(), mHandler, 100);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    // startActivity(intent);


                } else {
                    user.Msg("아이디 또는 비밀번호를 입력하세요.");
                }
            }
        });

        //회원가입 버튼
        layout.findViewById(R.id.btn_join_buyer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                //웹뷰 셋팅
                login_webview.setVisibility(View.VISIBLE);


                WebSettings webSettings = login_webview.getSettings();
                webSettings.setJavaScriptEnabled(true);

                login_webview.setWebViewClient(new WebViewClient() {
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }

                    public void onPageStarted(WebView view, String url,
                                              android.graphics.Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        progressBar.setVisibility(View.VISIBLE);
                    }


                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        progressBar.setVisibility(View.INVISIBLE);
                    }


                    public void onReceivedError(WebView view, int errorCode,
                                                String description, String failingUrl) {
                        super.onReceivedError(view, errorCode, description, failingUrl);
                        Toast.makeText(LoginActivity.this, "로딩오류" + description,
                                Toast.LENGTH_SHORT).show();
                    }

                });

                login_webview.setWebChromeClient(new WebChromeClient() {

                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        progressBar.setProgress(newProgress);
                    }

                    public boolean onJsAlert(WebView view, String url,
                                             String message, final android.webkit.JsResult result) {
                        new AlertDialog.Builder(LoginActivity.this)
                                .setTitle("알림")
                                .setMessage(message)
                                .setPositiveButton(android.R.string.ok,
                                        new AlertDialog.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                result.confirm();
                                            }
                                        }).setCancelable(false).create().show();

                        return true;
                    }

                    ;

                    public boolean onJsConfirm(WebView view, String url,
                                               String message, final android.webkit.JsResult result) {
                        new AlertDialog.Builder(LoginActivity.this)
                                .setTitle("확인")
                                .setMessage(message)
                                .setPositiveButton(android.R.string.ok,
                                        new AlertDialog.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                result.confirm();
                                            }
                                        })
                                .setNegativeButton(android.R.string.cancel,
                                        new AlertDialog.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                result.cancel();
                                            }
                                        }).setCancelable(false).create().show();
                        return true;
                    }

                });
                login_webview.addJavascriptInterface(new AndroidBridge(), "Android");
                login_webview.loadUrl("http://110.10.189.232/Join.php");

            }
        });


    }

    void Popupfindpw() {

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LoginActivity.this.LAYOUT_INFLATER_SERVICE);
        final View layout = layoutInflater.inflate(R.layout.popup_find_pw, null);

        //   ((EditText) layout.findViewById(R.id.edt_id)).setText("KA5831828498");
        //   ((EditText) layout.findViewById(R.id.edt_pass)).setText("4321");


        popupWindow = new PopupWindow(LoginActivity.this);
        popupWindow.setContentView(layout);
        popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);

        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        //popupWindow.setFocusable(true);

        //Clear the default translucent background
        //popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //popupWindow.showAsDropDown(toolbar);
        popupWindow.showAtLocation(layout, Gravity.CENTER, 0, -100);

                Button img_find_pw = (Button) layout.findViewById(R.id.img_find_pw);

        img_find_pw.bringToFront();
        img_find_pw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String find_id = ((EditText)layout.findViewById(R.id.find_pw_id)).getText().toString().trim();
                final String find_email = ((EditText)layout.findViewById(R.id.find_pw_email)).getText().toString().trim();
                Toast.makeText(LoginActivity.this, "준비 중 입니다.", Toast.LENGTH_SHORT).show();
//                if (find_id.toString().length()  >0 && find_email.toString().length() > 0) {
//
//                    JSONObject obj2 = new JSONObject();
//                try {
//                    obj2.put("user_id", find_id);
//                    obj2.put("user_email", find_email);
//                    client = new WebClient(user.URL_FIND_PW, obj2.toString(), mHandler, 200);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            } else {
//                user.Msg("아이디 또는 이메일을 입력하세요.");
//            }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 100:
                //구글맵에서 선택 후의 주소값 설정
                login_webview.loadUrl("javascript:setAddressData('" + user.getData("map_addr") + "','" + user.getData("map_lng") + "','" + user.getData("map_lat") + "');");

               Log.d("result","javascript:setAddressData('" + user.getData("map_addr") + "','" + user.getData("map_lng") + "','" + user.getData("map_lat") + "');");

                break;

            default:
                break;
        }
    }

    private final Handler handler = new Handler();

    private class AndroidBridge {

        @JavascriptInterface
        public void showToast(String msg) {
            Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
        }


        @JavascriptInterface
        public void fileUpload() {

            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 1200);

        }

        @JavascriptInterface
        public void getGoordinate() { // must be final
            handler.post(new Runnable() {
                public void run() {

                    if (ActivityCompat.checkSelfPermission(LoginActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LoginActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        return;
                    }
                    locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 1, locationListener);
                    locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, locationListener);

                }
            });
        }


        @JavascriptInterface
        public void setJoinFlag(final String user_id, final String user_passwd) { // must be final
            handler.post(new Runnable() {
                public void run() {


                    user.setData("user_id", user_id);
                    user.setData("user_passwd", user_passwd);
                    user.setBoolean("auto_login", true);
                    login_webview.setVisibility(View.GONE);

                    JSONObject obj2 = new JSONObject();
                    try {
                        obj2.put("user_id", user.getData("user_id"));
                        obj2.put("user_passwd", user.getData("user_passwd"));
                        obj2.put("FCM", user.getData("FCM"));
                        client = new WebClient(user.URL_LOGIN, obj2.toString(), mHandler, 100);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                }
            });
        }

        @JavascriptInterface
        public void callGooglemap() {

            //지도로 이동
            Intent intent = new Intent(LoginActivity.this, MapActivity.class);
            startActivityForResult(intent, 100);

        }
    }

    /**
     * 배경화면의 확대 스케일 애니메이션
     */
    private void scaleAnimate() {
        ImageView imageView = (ImageView) findViewById(R.id.img_main_bg);

        //LinearLayout linearLayout  = (LinearLayout) findViewById(R.id.ll_main_bg);

        ScaleAnimation fade_in = new ScaleAnimation(1f, 1.2f, 1f, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        fade_in.setDuration(5000);     // animation duration in milliseconds
        fade_in.setFillAfter(true);    // If fillAfter is true, the transformation that this animation performed will persist when it is finished.
        imageView.startAnimation(fade_in);

    }


    @Override
    public void onBackPressed() {
        if (login_webview.canGoBack()) {
            login_webview.goBack();
        } else {
            if (login_webview.getVisibility() == View.VISIBLE) {
                login_webview.setVisibility(View.GONE);
            } else {
                super.onBackPressed();
            }
        }
    }


    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.

            Log.d("test", "onLocationChanged, location:" + location);
            double longitude = location.getLongitude(); //경도
            double latitude = location.getLatitude();   //위도
            double altitude = location.getAltitude();   //고도
            float accuracy = location.getAccuracy();    //정확도
            String provider = location.getProvider();   //위치제공자
            //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
            //Network 위치제공자에 의한 위치변화
            //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.
            user.Msg("위치정보 : " + provider + "\n위도 : " + longitude + "\n경도 : " + latitude
                    + "\n고도 : " + altitude + "\n정확도 : " + accuracy);
        }

        public void onProviderDisabled(String provider) {
            // Disabled시
            Log.d("test", "onProviderDisabled, provider:" + provider);
        }

        public void onProviderEnabled(String provider) {
            // Enabled시
            Log.d("test", "onProviderEnabled, provider:" + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };


}
