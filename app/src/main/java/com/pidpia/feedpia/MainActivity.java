package com.pidpia.feedpia;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    WebView mainWebView;

    ProgressBar progressBar;

    TableRow menu_myfarm, menu_manager, menu_cscenter, menu_confing;
    LinearLayout main_logout,lv_activity_main_nav_list;


    User user;
    DrawerLayout menu_layout;
    PopupWindow popupWindowFeedExtend, popupWindowFeedAdd, popup_order_cancel;

    boolean popup_order_cancel_status;
    FeedExtendListview feedExtendListview;

    File storageFile, cameraFile;
    int Feed_Number;
    int Feed_ID;
    int Feed_Optipon;

    FrameLayout main_user_back;
    ImageView main_user_logo;
    TextView main_user_name;

    String img_target;

    File temp_info_file, temp_bank_file;
    TextView cancel_order_msg_target;

    int upload_msg;

    WebClient client;
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {


                case 100:
                    mainWebView.loadUrl("javascript:location.reload();");

                    user.Msg("취소되었습니다.");
//                    handler.post(new Runnable() {
//                        public void run() {
//                            if (popup_order_cancel != null) popup_order_cancel.dismiss();
//                            popup_order_cancel_status = false;
//                        }
//                    });


                    break;

                case 500:
                    //Log.d("UPLOAD","Upload complete;");


                    handler.post(new Runnable() {
                        public void run() {
                            mainWebView.loadUrl("javascript:callImageLogo('" + img_target + "');  ");
                        }

                    });


                    break;
                case 600:
                    JSONObject obj2 = new JSONObject();
                    try {
                        obj2.put("SSID", user.getData("SSID"));
                        obj2.put("UID", user.getData("UID"));
                        obj2.put("PID", user.getData("PID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    String data2 = obj2.toString();
//                    client = new WebClient(user.URL_DOMAIN + user.URL_ORDER_DETAIL, data2, mHandler, 100);
//                    user.Msg(upload_msg);
//                    popupWindowSign.dismiss();
                    break;


                case 1000:
                    if (client.recvData != null) {
                        try {
                            JSONObject obj = new JSONObject(client.recvData);
                            user.Msg(obj.getString("msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case 5000:
                case 5100:
                    handler.post(new Runnable() {
                        public void run() {
                            mainWebView.loadUrl("javascript:callImage('" + img_target + "','"+user.getData("file")+"');  ");
                        }

                    });
                    break;
            }
        }
    };


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = new User(this);
        mainWebView = (WebView) findViewById(R.id.mainWebView);
        main_logout = (LinearLayout) findViewById(R.id.main_logout);

        main_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setBoolean("join_flag", false);
                user.setData("user_id", "");
                user.setData("user_passwd", "");
                Toast.makeText(MainActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });
        lv_activity_main_nav_list = (LinearLayout)findViewById(R.id.lv_activity_main_nav_list);
        lv_activity_main_nav_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        progressBar = (ProgressBar) this.findViewById(R.id.webview_progress);

        //퍼미션 체크
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
            }
        }

        try {
            storageFile = File.createTempFile("photo", ".jpg", this.getCacheDir());
            storageFile.setWritable(true, false);
        } catch (IOException e) {
            e.printStackTrace();
        }


        Log.d("Uinfo", user.getData("UID") + "/" + user.getData("FID"));

        WebSettings webSettings = mainWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        menu_myfarm = (TableRow) findViewById(R.id.menu_myfarm);
        menu_manager = (TableRow) findViewById(R.id.menu_manager);
        menu_cscenter = (TableRow) findViewById(R.id.menu_cscenter);
        menu_confing = (TableRow) findViewById(R.id.menu_confing);





        menu_layout = (DrawerLayout) findViewById(R.id.dl_activity_main_drawer);


        main_user_back = (FrameLayout) findViewById(R.id.main_user_back);
        main_user_logo = (ImageView) findViewById(R.id.main_user_logo);
//        main_user_logo.setBackground(new ShapeDrawable(new OvalShape()));
//        main_user_logo.setClipToOutline(true);

        main_user_name = (TextView) findViewById(R.id.main_user_name);


        main_user_name .setText(user.getData("user_name"));
        new ImageLoad(MainActivity.this, main_user_logo, user.getData("user_logo")).start();
       // new ImageLoad(MainActivity.this, main_user_back, user.getData("user_back")).start();



        mainWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onPageStarted(WebView view, String url,
                                      Bitmap favicon) {
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
                Toast.makeText(MainActivity.this, "로딩오류" + description,
                        Toast.LENGTH_SHORT).show();
            }

        });

        mainWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
            }

            public boolean onJsAlert(WebView view, String url,
                                     String message, final JsResult result) {
                new AlertDialog.Builder(MainActivity.this)
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
                                       String message, final JsResult result) {
                new AlertDialog.Builder(MainActivity.this)
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
        mainWebView.addJavascriptInterface(new WebAppInterface(this, user), "Android");


        String param = "user_id=" + user.getData("user_id") + "&user_passwd=" + user.getData("user_passwd");
//        Toast.makeText(this, param, Toast.LENGTH_SHORT).show();
        mainWebView.postUrl("http://110.10.189.232/Login.php", param.getBytes());

        mainWebView.clearCache(true);
        mainWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        /*
        mainWebView.loadUrl("http://hm.ibss.co.kr/Login.php");
*/
        menu_myfarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainWebView.loadUrl("http://110.10.189.232/User_Farm.php");
                menu_layout.closeDrawer(Gravity.LEFT);
            }
        });
        menu_manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mainWebView.loadUrl("http://hm.ibss.co.kr/User_Manager.php");
                //menu_layout.closeDrawer(Gravity.LEFT);
                user.Msg("서비스 준비중입니다.");
            }
        });

        menu_cscenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainWebView.loadUrl("http://110.10.189.232/User_CsCenter.php");
                menu_layout.closeDrawer(Gravity.LEFT);
            }
        });

        menu_confing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainWebView.loadUrl("http://110.10.189.232/User_Config.php");
                menu_layout.closeDrawer(Gravity.LEFT);
            }
        });

        CheckTypesTask task = new CheckTypesTask(MainActivity.this);
        task.execute();


    }

    @Override
    public void onBackPressed() {
        if (mainWebView.canGoBack()) {
            if (popup_order_cancel_status) {
                if (popup_order_cancel != null) popup_order_cancel.dismiss();
                popup_order_cancel_status = false;
            } else {
                mainWebView.goBack();
            }
        } else {


            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setTitle("종료확인");
            alert.setMessage("어플리케이션을 종료하시겠습니까?");
            alert.setPositiveButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

            alert.setNegativeButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {

                    finish();
                }
            });
            alert.show();
            //super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera

            } else {
                // Your app will not have this permission. Turn off all functions
                // that require this permission or it will force close like your
                // original question
            }
        }
    }


    public class WebAppInterface {
        Context mContext;
        User user;
        int year, month, day, hour, minute, second;


        WebAppInterface(Context context, User user) {
            mContext = context;
            this.user = user;
//        ma.mainWebView.loadUrl("";);

            GregorianCalendar calendar = new GregorianCalendar();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            hour = 0;
            minute = 0;
            second = 0;


        }

        @JavascriptInterface
        public void delivery_state(String num) {

            Intent intent = new Intent(MainActivity.this, DeliverStatus.class);
            intent.putExtra("order_num",num);
            startActivity(intent);
        }

        @JavascriptInterface
        public void showToast(String msg) {
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void Tel(String tel) {
            startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
        }


        @JavascriptInterface
        public void setPID(String data) {
            user.setData("PID", data);
        }

        @JavascriptInterface
        public void ShowMenu() {
            handler.post(new Runnable() {
                public void run() {
                    if (menu_layout.isDrawerOpen(Gravity.LEFT)) {
                        menu_layout.closeDrawer(Gravity.LEFT);
                    } else {
                        menu_layout.openDrawer(Gravity.LEFT);
                    }
                }
            });
        }



        @JavascriptInterface
        public  void UploadLogo(){
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 5000);
        }


        @JavascriptInterface
        public  void UploadBack(){
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 5100);
        }


        boolean date_limit;

        @JavascriptInterface
        public void getDatepicker() {
            date_limit = true;
            new DatePickerDialog(mContext, dateSetListener1, year, month, day).show();
            date_target = "";
        }

        String date_target = "";

        @JavascriptInterface
        public void getDatepicker(String target) {
            date_limit = false;
            new DatePickerDialog(mContext, dateSetListener1, year, month, day).show();
            date_target = target;
        }


        public DatePickerDialog.OnDateSetListener dateSetListener1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year2, int monthOfYear2, int dayOfMonth2) {

                year = year2;
                month = monthOfYear2;
                day = dayOfMonth2;


                handler.post(new Runnable() {
                    public void run() {

                        if (date_limit) {
                            long date_sel = user.getUDate(year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", (day)) + " 00:00:00");
                            date_target = "order_datepicker";
                            if (date_sel < user.getUDateDay()) {
                                user.Msg("오늘 이후 주문이 가능힙니다.");
                            } else {
                                mainWebView.loadUrl("javascript:setDatepicker('" + date_target + "','" + year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", day) + "');");
                            }


                        } else {

                            mainWebView.loadUrl("javascript:setDatepicker('" + date_target + "','" + year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", day) + "');");

                        }


                    }
                });

            }
        };

        @JavascriptInterface
        public void getFeedExtendList(String target) {

            View layout;
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(MainActivity.this.LAYOUT_INFLATER_SERVICE);
            layout = layoutInflater.inflate(R.layout.popup_extend_feed_list, null);

            popupWindowFeedExtend = new PopupWindow(MainActivity.this);
            popupWindowFeedExtend.setContentView(layout);
            popupWindowFeedExtend.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
            popupWindowFeedExtend.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
            popupWindowFeedExtend.setFocusable(true);

            popupWindowFeedExtend.setAnimationStyle(-1);
            popupWindowFeedExtend.setBackgroundDrawable(new BitmapDrawable());
            popupWindowFeedExtend.setOutsideTouchable(true);
            popupWindowFeedExtend.showAtLocation(layout, Gravity.CENTER, 0, -100);

            final TextView popup_feed_tab_1, popup_feed_tab_2, popup_feed_listview_msg;
            ListView popup_feed_listview;
            Button popup_feed_ok, popup_feed_cancel;

            popup_feed_tab_1 = (TextView) layout.findViewById(R.id.popup_feed_tab_1);
            popup_feed_tab_2 = (TextView) layout.findViewById(R.id.popup_feed_tab_2);

            popup_feed_listview = (ListView) layout.findViewById(R.id.popup_feed_listview);
            popup_feed_listview_msg = (TextView) layout.findViewById(R.id.popup_feed_listview_msg);
            popup_feed_ok = (Button) layout.findViewById(R.id.popup_feed_ok);
            popup_feed_cancel = (Button) layout.findViewById(R.id.popup_feed_cancel);

            user.setData("PID", target);


            feedExtendListview = new FeedExtendListview(MainActivity.this, popup_feed_listview, user, popupWindowFeedExtend, popup_feed_listview_msg, popup_feed_tab_1, popup_feed_tab_2);
            feedExtendListview.Update(1);

            popup_feed_tab_1.setBackgroundColor(Color.parseColor("#0bd438"));
            popup_feed_tab_1.setTextColor(Color.parseColor("#FFFFFF"));
            popup_feed_tab_2.setBackgroundColor(Color.parseColor("#efefef"));
            popup_feed_tab_2.setTextColor(Color.parseColor("#bababa"));


            popup_feed_tab_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    feedExtendListview.Update(1);
                    popup_feed_tab_1.setBackgroundColor(Color.parseColor("#0bd438"));
                    popup_feed_tab_1.setTextColor(Color.parseColor("#FFFFFF"));
                    popup_feed_tab_2.setBackgroundColor(Color.parseColor("#efefef"));
                    popup_feed_tab_2.setTextColor(Color.parseColor("#bababa"));

                }
            });
            popup_feed_tab_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    feedExtendListview.Update(2);
                    popup_feed_tab_2.setBackgroundColor(Color.parseColor("#0bd438"));
                    popup_feed_tab_2.setTextColor(Color.parseColor("#FFFFFF"));
                    popup_feed_tab_1.setBackgroundColor(Color.parseColor("#efefef"));
                    popup_feed_tab_1.setTextColor(Color.parseColor("#bababa"));

                }
            });


            popup_feed_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            popup_feed_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindowFeedExtend.dismiss();
                }
            });
        }


        @JavascriptInterface
        public void ItemAdd() {


            View layout;
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(MainActivity.this.LAYOUT_INFLATER_SERVICE);
            layout = layoutInflater.inflate(R.layout.popup_feed_add, null);

            popupWindowFeedAdd = new PopupWindow(MainActivity.this);
            popupWindowFeedAdd.setContentView(layout);
            popupWindowFeedAdd.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
            popupWindowFeedAdd.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
            popupWindowFeedAdd.setFocusable(true);

            popupWindowFeedAdd.setAnimationStyle(-1);
            popupWindowFeedAdd.setBackgroundDrawable(new BitmapDrawable());
            popupWindowFeedAdd.setOutsideTouchable(true);
            popupWindowFeedAdd.showAtLocation(layout, Gravity.CENTER, 0, -100);


            Spinner popup_feed_add_category;
            TextView popup_feed_add_msg, popup_feed_add_cancel, popup_feed_add_ok;
            ListView popup_feed_add_listview;


            popup_feed_add_category = (Spinner) layout.findViewById(R.id.popup_feed_add_category);

            popup_feed_add_msg = (TextView) layout.findViewById(R.id.popup_feed_add_msg);
            popup_feed_add_cancel = (TextView) layout.findViewById(R.id.popup_feed_add_cancel);
            popup_feed_add_ok = (TextView) layout.findViewById(R.id.popup_feed_add_ok);

            popup_feed_add_listview = (ListView) layout.findViewById(R.id.popup_feed_add_listview);


            popup_feed_add_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    feedExtendListview.UpdateSection(position + 1);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            feedExtendListview = new FeedExtendListview(MainActivity.this, popup_feed_add_listview, user, popupWindowFeedAdd, popup_feed_add_msg);
            feedExtendListview.Update(1);
            popup_feed_add_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindowFeedAdd.dismiss();
                }
            });
            popup_feed_add_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindowFeedAdd.dismiss();
                }
            });

        }

        @JavascriptInterface
        public void getTimepicker() {
            new TimePickerDialog(MainActivity.this, timeSetListener, hour, minute, false).show();
        }


        public TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
                hour = hourOfDay;
                minute = minutes;

                handler.post(new Runnable() {
                    public void run() {
                        mainWebView.loadUrl("javascript:setTimepicker('" + String.format("%02d", hour) + ":" + String.format("%02d", minute) + ":00');");

                    }
                });
            }
        };

        @JavascriptInterface
        public void clearFlag() {
            user.setBoolean("join_flag", false);
        }

        @JavascriptInterface
        public void DeliveryList() {

            View layout;
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(MainActivity.this.LAYOUT_INFLATER_SERVICE);
            layout = layoutInflater.inflate(R.layout.popup_delivery_list, null);

            final PopupWindow popupWindow = new PopupWindow(MainActivity.this);
            popupWindow.setContentView(layout);
            popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
            popupWindow.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
            popupWindow.setFocusable(true);

            popupWindow.setAnimationStyle(-1);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setOutsideTouchable(true);
            popupWindow.showAtLocation(layout, Gravity.CENTER, 0, -100);


            TextView deliver_listview_msg;
            ListView deliver_listview;
            Button delivery_address_ok, delivery_address_cancel;

            deliver_listview_msg = (TextView) layout.findViewById(R.id.deliver_listview_msg);

            deliver_listview = (ListView) layout.findViewById(R.id.deliver_listview);

            delivery_address_ok = (Button) layout.findViewById(R.id.delivery_address_ok);
            delivery_address_cancel = (Button) layout.findViewById(R.id.delivery_address_cancel);

            new DeliverListView(MainActivity.this, deliver_listview, user, popupWindow, deliver_listview_msg);


            delivery_address_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });


        }


        //사료 수량입력 키패드
        @JavascriptInterface
        public void getNumberPicker(int number, int type) {

            Feed_ID = number;
            Feed_Optipon=type;
            Log.d("feed_id", number + "");

            final Dialog d = new Dialog(MainActivity.this);
            /*
            d.setTitle("수량입력");
            */
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            d.setContentView(R.layout.dialog_number_picker);
            Button dialog_number_picker_ok = (Button) d.findViewById(R.id.dialog_number_picker_ok);
            Button dialog_number_picker_close = (Button) d.findViewById(R.id.dialog_number_picker_close);
            final EditText dialog_keypad_input = (EditText) d.findViewById(R.id.dialog_keypad_input);

            dialog_keypad_input.setFocusableInTouchMode(false);

            Button dialog_keypad_1, dialog_keypad_2, dialog_keypad_3, dialog_keypad_4, dialog_keypad_5, dialog_keypad_6, dialog_keypad_7, dialog_keypad_8, dialog_keypad_9, dialog_keypad_0, dialog_keypad_del, dialog_keypad_reset;


            dialog_keypad_1 = (Button) d.findViewById(R.id.dialog_keypad_1);
            dialog_keypad_2 = (Button) d.findViewById(R.id.dialog_keypad_2);
            dialog_keypad_3 = (Button) d.findViewById(R.id.dialog_keypad_3);
            dialog_keypad_4 = (Button) d.findViewById(R.id.dialog_keypad_4);
            dialog_keypad_5 = (Button) d.findViewById(R.id.dialog_keypad_5);
            dialog_keypad_6 = (Button) d.findViewById(R.id.dialog_keypad_6);
            dialog_keypad_7 = (Button) d.findViewById(R.id.dialog_keypad_7);
            dialog_keypad_8 = (Button) d.findViewById(R.id.dialog_keypad_8);
            dialog_keypad_9 = (Button) d.findViewById(R.id.dialog_keypad_9);
            dialog_keypad_0 = (Button) d.findViewById(R.id.dialog_keypad_0);
            dialog_keypad_del = (Button) d.findViewById(R.id.dialog_keypad_del);
            dialog_keypad_reset = (Button) d.findViewById(R.id.dialog_keypad_reset);


            dialog_keypad_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog_keypad_input.setText(dialog_keypad_input.getText().toString() + "1");
                }
            });

            dialog_keypad_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog_keypad_input.setText(dialog_keypad_input.getText().toString() + "2");
                }
            });

            dialog_keypad_3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog_keypad_input.setText(dialog_keypad_input.getText().toString() + "3");
                }
            });


            dialog_keypad_4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog_keypad_input.setText(dialog_keypad_input.getText().toString() + "4");
                }
            });

            dialog_keypad_5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog_keypad_input.setText(dialog_keypad_input.getText().toString() + "5");
                }
            });

            dialog_keypad_6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog_keypad_input.setText(dialog_keypad_input.getText().toString() + "6");
                }
            });

            dialog_keypad_7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog_keypad_input.setText(dialog_keypad_input.getText().toString() + "7");
                }
            });

            dialog_keypad_8.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog_keypad_input.setText(dialog_keypad_input.getText().toString() + "8");
                }
            });


            dialog_keypad_9.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog_keypad_input.setText(dialog_keypad_input.getText().toString() + "9");
                }
            });


            dialog_keypad_0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog_keypad_input.setText(dialog_keypad_input.getText().toString() + "0");
                }
            });


            dialog_keypad_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String str = dialog_keypad_input.getText().toString();
                    if (str.length() > 0)
                        dialog_keypad_input.setText(str.substring(0, str.length() - 1));
                }
            });


            dialog_keypad_reset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog_keypad_input.setText("");
                }
            });


           /* final NumberPicker np = (NumberPicker) d.findViewById(R.id.dialog_number_picker);
            np.setMaxValue(9999);
            np.setMinValue(1);
            np.setWrapSelectorWheel(false);
            np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    //Feed_Number=newVal;
                    Log.d("number", newVal + "");
                }
            });

            dialog_number_picker_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Feed_Number = np.getValue();

                    Log.d("number2", Feed_Number + "");
                    handler.post(new Runnable() {
                        public void run() {
                            mainWebView.loadUrl("javascript:setNumberPicker('" + Feed_ID + "','" + Feed_Number + "');  ");
                            d.dismiss();
                        }

                    });
                }
            });
            */


           //수량 입력 후 확인 버튼
            dialog_number_picker_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Feed_Number = Integer.parseInt(dialog_keypad_input.getText().toString());
                    } catch (Exception ee) {
                        Feed_Number = 0;
                        user.Msg("잘못된 값이 입력되었습니다.");
                    }
                    Log.d("number2", Feed_Number + "");
                    handler.post(new Runnable() {
                        public void run() {
                            mainWebView.loadUrl("javascript:setNumberPicker('" + Feed_ID + "','"+Feed_Optipon+"','" + Feed_Number + "');  ");
                            d.dismiss();
                        }

                    });
                }
            });

            //취소 버튼
            dialog_number_picker_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.dismiss();
                }
            });
            d.show();
        }


        @JavascriptInterface
        public void fileUpload(String target) {

            img_target = target;

            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 1200);

        }



        @JavascriptInterface
        public void fileUpload(String target,int msg) {

            img_target = target;
            upload_msg=msg;

            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, upload_msg);

        }

        //지도 콜
        @JavascriptInterface
        public void callGooglemap() {


            Intent intent = new Intent(MainActivity.this, MapActivity.class);
//            startActivity(intent);
            startActivityForResult(intent, 100);
        }

        //주문 취소
        @JavascriptInterface
        public void cancelOrder(final int type, int status) {


            popup_order_cancel_status = true;

            popup_order_cancel = new PopupWindow(MainActivity.this.getCurrentFocus(), RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //팝업으로 띄울 커스텀뷰를 설정하고
            View views = inflater.inflate(R.layout.popup_cancel_order1, null);
            popup_order_cancel.setContentView(views);
            popup_order_cancel.setAnimationStyle(-1);
            popup_order_cancel.showAtLocation(views, Gravity.CENTER, 0, -100);


            TextView btn_no = (TextView) views.findViewById(R.id.btn_no);
            TextView btn_yes = (TextView) views.findViewById(R.id.btn_yes);


            final LinearLayout cancel_order_msg = (LinearLayout) views.findViewById(R.id.cancel_order_msg);
            final Spinner cancel_order_payment1_step2_account_name = (Spinner) views.findViewById(R.id.cancel_order_payment1_step2_account_name);
            final LinearLayout cancel_order_payment1_step1 = (LinearLayout) views.findViewById(R.id.cancel_order_payment1_step1);
            final LinearLayout cancel_order_payment1_step2 = (LinearLayout) views.findViewById(R.id.cancel_order_payment1_step2);
            final TextView cancel_order_payment1_step2_attach_info = (TextView) views.findViewById(R.id.cancel_order_payment1_step2_attach_info);
            final TextView cancel_order_payment1_step2_attach_bank = (TextView) views.findViewById(R.id.cancel_order_payment1_step2_attach_bank);

            final LinearLayout cancel_order_payment2_step1 = (LinearLayout) views.findViewById(R.id.cancel_order_payment2_step1);


            switch (type) {
                case 0:

                 //   cancel_order_payment1_step2.setVisibility(View.VISIBLE);
                //    cancel_order_payment2_step1.setVisibility(View.GONE);
                    break;
                case 1:

               //     cancel_order_payment1_step2.setVisibility(View.GONE);
              //      cancel_order_payment2_step1.setVisibility(View.VISIBLE);
                    break;
            }


            cancel_order_payment1_step2_attach_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (Build.VERSION.SDK_INT >= 23) {
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                            // Should we show an explanation?
                            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                            } else {
                                // No explanation needed, we can request the permission.
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2000);

                            }
                        } else {
                            cancel_order_msg_target = cancel_order_payment1_step2_attach_info;

                            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                            photoPickerIntent.setType("image/*");
                            startActivityForResult(photoPickerIntent, 2000);
                        }
                    }
                }
            });

            cancel_order_payment1_step2_attach_bank.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                            // Should we show an explanation?
                            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                            } else {
                                // No explanation needed, we can request the permission.
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2100);

                            }
                        } else {
                            cancel_order_msg_target = cancel_order_payment1_step2_attach_bank;

                            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                            photoPickerIntent.setType("image/*");
                            startActivityForResult(photoPickerIntent, 2100);
                        }
                    }
                }
            });
            btn_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popup_order_cancel.dismiss();
                }
            });

            btn_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    switch (type) {
                        case 0:

                            break;
                        case 1:

                            break;
                    }


                    JSONObject obj1 = new JSONObject();
                    try {
                        obj1.put("SSID", user.getData("SSID"));
                        obj1.put("UID", user.getData("UID"));
                        obj1.put("PID", user.getData("PID"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String data = obj1.toString();
                    client = new WebClient(user.URL_ORDER_CANCEL, data, mHandler, 100);
                    popup_order_cancel.dismiss();


                }
            });

            popup_order_cancel.setTouchable(true);
            popup_order_cancel.setFocusable(true);
            popup_order_cancel.showAsDropDown(MainActivity.this.getCurrentFocus());

        }
    }


    public void setFeedExtend(final String pid, final String idx) {

        handler.post(new Runnable() {
            public void run() {
//                mainWebView.loadUrl("javascript:orderDataSet('"+pid+"','option_fid','" + idx + "'); PageLoad('Order_Step1_Extend_Feed_List.php?PID="+pid+"','option_feed_list'); ");
                mainWebView.loadUrl("javascript:option_sel_attributes_select('"+pid+"','" + idx + "'); ");

            }

        });

    }

    public void setFeedAdd(final String pid, final String idx) {

        Log.d("FID", "add :" + pid);
        handler.post(new Runnable() {
            public void run() {
                mainWebView.loadUrl("javascript:DataSetSub('" + pid + "','fid','" + pid + "'); PageLoad('Order_Step1_Add_Feed_List.php?','feed_add_list'); ");
            }

        });

    }


    public void setDeliveryInfo(final String name, final String tel, final String address, final String lat, final String lng) {

        handler.post(new Runnable() {
            public void run() {
                mainWebView.loadUrl("javascript:setDeliveryInfo('" + name + "','" + tel + "','" + address + "','" + lat + "','" + lng + "');  ");
            }

        });

    }

    private final Handler handler = new Handler();


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 100) {

            mainWebView.loadUrl("javascript:setDeliveryAddressNew('" + user.getData("map_addr") + "','','','" + user.getData("map_lat") + "','" + user.getData("map_lng") + "');");

        }


        if (requestCode == 1200) {

            if (resultCode == RESULT_OK) {
                try {
                    final Uri imageUri = data.getData();
                    storageFile = new File(getPathFromUri(imageUri));
                    Log.d("FILE", getPathFromUri(imageUri));
                    // picture_type=2;
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    //card_picture_view.setImageBitmap(selectedImage);

                    new HttpAsyncTaskFile().execute(storageFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }


        if (requestCode == 2000) {

            if (resultCode == RESULT_OK) {
                try {

                    img_target = "INFO";
                    final Uri imageUri = data.getData();
                    temp_info_file = new File(getPathFromUri(imageUri));
                    cancel_order_msg_target.setText(temp_info_file.getName() + "파일을 선택했습니다.");

                    Log.d("FILE", getPathFromUri(imageUri));
                    // picture_type=2;
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    //card_picture_view.setImageBitmap(selectedImage);

                    new HttpAsyncTaskFile().execute(temp_info_file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }


        if (requestCode == 2100) {
            if (resultCode == RESULT_OK) {
                try {
                    img_target = "BANK";
                    final Uri imageUri = data.getData();
                    temp_bank_file = new File(getPathFromUri(imageUri));
                    Log.d("FILE", getPathFromUri(imageUri));
                    // picture_type=2;
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    cancel_order_msg_target.setText(temp_bank_file.getName() + "파일을 선택했습니다.");
                    new HttpAsyncTaskFile().execute(temp_bank_file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }






        if (requestCode == upload_msg) {

            if (resultCode == RESULT_OK) {
                try {
                    final Uri imageUri = data.getData();
                    storageFile = new File(getPathFromUri(imageUri));
                    Log.d("FILE", getPathFromUri(imageUri));
                    // picture_type=2;
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    //card_picture_view.setImageBitmap(selectedImage);

                    new HttpAsyncTaskFile().execute(storageFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }


    }


    public String getPathFromUri(Uri uri) {

        String[] projection = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    private class HttpAsyncTaskFile extends AsyncTask<File, Void, String> {
        @Override
        protected String doInBackground(File... files) {

            try {
                MultipartUtility multipart = null;
                multipart = new MultipartUtility(user.URL_UPLOAD_PICTURE, "utf8");
                multipart.addFormField("UID", user.getData("UID"));
                multipart.addFormField("FID", user.getData("FID"));
                multipart.addFormField("TYPE", img_target);

                multipart.addFormField("MSG", upload_msg+"");

                if (files[0].exists()) {
                    multipart.addFilePart("file_sign", new File(files[0].getAbsolutePath()));
                }
                List<String> response = multipart.finish();


                for (String line : response) {

                    Log.d("RSP", "Upload Files Response:::" + line);

                    try {
                        JSONObject json = new JSONObject(line);
                        if (json.getBoolean("status")) {
                            user.setData("file",json.getString("file"));
                            mHandler.sendEmptyMessage(upload_msg);
                        } else {
                            mHandler.sendEmptyMessage(upload_msg);
                        }


                    } catch (Exception e) {

                        e.printStackTrace();
                    }


                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";

        }


        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(context, "Data Sent!", Toast.LENGTH_LONG).show();
            //final upload
        }
    }

}
