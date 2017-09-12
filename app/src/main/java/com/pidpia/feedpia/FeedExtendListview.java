package com.pidpia.feedpia;

/**
 * Created by jenorain on 2017-02-11.
 */


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jenorain on 2016-12-16.
 */

public class FeedExtendListview {

    Context context;
    User user;
    ArrayList<DataOrder> order_data;
    OrderDataAdapter order_adpater;

    WebClient client;

    ListView mListView;

    boolean viewType;
    ImageLoader imgLoader;


    PopupWindow window;


    TextView status_msg, tab_name1, tab_name2;
    ProgressBar pb;
    ProgressDialog asyncDialog;

    int Section;


    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case 100:
                    order_data.clear();
                    try {
                        if (client.recvData != null) {
                            JSONObject obj = new JSONObject(client.recvData);


                            if (tab_name1 != null) tab_name1.setText(obj.getString("tab_name1"));
                            if (tab_name2 != null) tab_name2.setText(obj.getString("tab_name2"));


                            JSONArray ja = obj.getJSONArray("list");       // new JSONArray(client.recvData);


                            if (ja.length() == 0) {
                                if (status_msg != null) {
                                    status_msg.setText("조회된 목록이 없습니다.");
                                    status_msg.setVisibility(View.VISIBLE);
                                }
                            } else {
                                if (status_msg != null) status_msg.setVisibility(View.GONE);
                                for (int i = 0; i < ja.length(); i++) {
                                    final JSONObject data = ja.getJSONObject(i);
                                    order_data.add(new DataOrder(data.getString("idx"), data.getString("company_name"), data.getString("feed_name"), data.getString("feed_point"), data.getString("feed_section"), data.getString("feed_type"), data.getString("feed_sale"), data.getString("feed_sale_msg"), data.getString("feed_price"), data.getString("feed_image"), data.getString("feed_price_after")));
                                }
                            }

                            //                      mListView.setAdapter(order_adpater);
//                        mListView.notify();
                            order_adpater.notifyDataSetChanged();

                            asyncDialog.hide();

                        }

                    } catch (Exception e) {
                        e.printStackTrace();

                    }


                    break;


            }
        }
    };


    FeedExtendListview(Context _context, ListView listView, User _user) {
        context = _context;
        user = _user;

        mListView = listView;
        order_data = new ArrayList<DataOrder>();
        order_adpater = new OrderDataAdapter(context, order_data);
        mListView.setAdapter(order_adpater);

        viewType = false;

        JSONObject obj1 = new JSONObject();
        try {
            obj1.put("SSID", user.getData("SSID"));
            obj1.put("UID", user.getData("UID"));
            obj1.put("PID", user.getData("PID"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        imgLoader = new ImageLoader(context);

        String data = obj1.toString();
        client = new WebClient(user.URL_FEED_EXTEND, data, mHandler, 100);


    }


    FeedExtendListview(Context _context, ListView listView, User _user, PopupWindow window, TextView status_msg) {
        context = _context;
        user = _user;

        mListView = listView;
        order_data = new ArrayList<DataOrder>();
        order_adpater = new OrderDataAdapter(context, order_data);
        mListView.setAdapter(order_adpater);

        viewType = false;
        this.window = window;
        this.status_msg = status_msg;

        asyncDialog = new ProgressDialog(context);

        JSONObject obj1 = new JSONObject();
        try {
            obj1.put("SSID", user.getData("SSID"));
            obj1.put("UID", user.getData("UID"));
            obj1.put("PID", user.getData("PID"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        imgLoader = new ImageLoader(context);

        String data = obj1.toString();
        client = new WebClient(user.URL_FEED_EXTEND, data, mHandler, 100);

        Section=0;
    }


    FeedExtendListview(Context _context, ListView listView, User _user, PopupWindow window, TextView status_msg, TextView tab_name1, TextView tab_name2) {
        context = _context;
        user = _user;
        Section=0;

        mListView = listView;
        order_data = new ArrayList<DataOrder>();
        order_adpater = new OrderDataAdapter(context, order_data);
        mListView.setAdapter(order_adpater);

        viewType = false;
        this.window = window;
        this.status_msg = status_msg;

        asyncDialog = new ProgressDialog(context);
        this.tab_name1 = tab_name1;
        this.tab_name2 = tab_name2;


        JSONObject obj1 = new JSONObject();
        try {
            obj1.put("SSID", user.getData("SSID"));
            obj1.put("UID", user.getData("UID"));
            obj1.put("PID", user.getData("PID"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        imgLoader = new ImageLoader(context);

        String data = obj1.toString();
        client = new WebClient(user.URL_FEED_EXTEND, data, mHandler, 100);


    }


    FeedExtendListview(Context _context, ListView listView, User _user, boolean view) {
        context = _context;
        user = _user;

        mListView = listView;
        order_data = new ArrayList<DataOrder>();
        order_adpater = new OrderDataAdapter(context, order_data);
        mListView.setAdapter(order_adpater);

        viewType = view;

/*
        JSONObject obj1 = new JSONObject();
        try {
            obj1.put("SSID", user.getData("SSID"));
            obj1.put("UID", user.getData("UID"));

            obj1.put("UID", user.UID);
            obj1.put("SID", user.SID);
            obj1.put("CODE", user.CODE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String data = obj1.toString();
        client = new WebClient(user.PAGE_AFTERSCHOO_LIST, data, mHandler, 100);
        */

    }

    void Update(int type) {

//        CheckTypesTask task = new CheckTypesTask(context); task.execute();

        asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        asyncDialog.setMessage("로딩중입니다.");

        // show dialog
        asyncDialog.show();

        JSONObject obj1 = new JSONObject();
        try {
            obj1.put("SSID", user.getData("SSID"));
            obj1.put("UID", user.getData("UID"));
            obj1.put("PID", user.getData("PID"));
            obj1.put("TYPE", type);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        imgLoader = new ImageLoader(context);

        String data = obj1.toString();
        client = new WebClient(user.URL_FEED_EXTEND, data, mHandler, 100);

    }


    void UpdateSection(int section) {

//        CheckTypesTask task = new CheckTypesTask(context); task.execute();

        asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        asyncDialog.setMessage("로딩중입니다.");

        // show dialog
        asyncDialog.show();
         this.Section=section;
        JSONObject obj1 = new JSONObject();
        try {
            obj1.put("SSID", user.getData("SSID"));
            obj1.put("UID", user.getData("UID"));
            obj1.put("PID", user.getData("PID"));
            obj1.put("Section", section);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        imgLoader = new ImageLoader(context);

        String data = obj1.toString();
        client = new WebClient(user.URL_FEED_EXTEND, data, mHandler, 100);

    }

    private class OrderDataAdapter extends ArrayAdapter<DataOrder> {
        private LayoutInflater mInflater;
        private Context context;

        public OrderDataAdapter(Context _context, ArrayList<DataOrder> object) {
            super(_context, 0, object);
            mInflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            context = _context;
        }

        @Override
        public View getView(final int position, View v, ViewGroup parent) {
            View view = null;
            if (v == null) {
                view = mInflater.inflate(R.layout.custom_feed_listview, null);
            } else {
                view = v;
            }

            final DataOrder data = this.getItem(position);
            if (data != null) {


                FrameLayout feed_row;
                ImageView feed_image;
                TextView feed_company_name, feed_name, feed_section, feed_type, feed_sale, feed_sale_msg, feed_price_before, feed_price_after;


                feed_row = (FrameLayout) view.findViewById(R.id.feed_row);

                feed_image = (ImageView) view.findViewById(R.id.feed_image);

                feed_company_name = (TextView) view.findViewById(R.id.feed_company_name);
                feed_name = (TextView) view.findViewById(R.id.feed_name);
                feed_section = (TextView) view.findViewById(R.id.feed_section);
                feed_type = (TextView) view.findViewById(R.id.feed_type);
                feed_sale = (TextView) view.findViewById(R.id.feed_sale);
                feed_sale_msg = (TextView) view.findViewById(R.id.feed_sale_msg);
                feed_price_before = (TextView) view.findViewById(R.id.feed_price_before);
                feed_price_after = (TextView) view.findViewById(R.id.feed_price_after);

                feed_company_name.setText(data.company_name);
                feed_name.setText(data.feed_name);
                feed_section.setText(data.feed_section);
                feed_type.setText(data.feed_type);
                feed_sale.setText(data.feed_sale);
                feed_sale_msg.setText(data.feed_sale_msg);
                feed_price_before.setText(data.feed_price);
                feed_price_after.setText(data.feed_price_after);

                //   imgLoader.DisplayImage(data.feed_image,feed_image);

                new ImageLoad(context, feed_image, data.feed_image).start();

                feed_row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (window != null) window.dismiss();

                        if(Section>0){

                            ((MainActivity) context).setFeedAdd(data.idx, data.idx);
                        }else{
                            ((MainActivity) context).setFeedExtend(user.getData("PID"), data.idx);

                        }

                    }
                });
            }
            return view;
        }
    }

    public class DataOrder {
        String idx, company_name, feed_name, feed_point, feed_section, feed_type, feed_sale, feed_sale_msg, feed_price, feed_image, feed_price_after;

        DataOrder(String idx, String company_name, String feed_name, String feed_point, String feed_section, String feed_type, String feed_sale, String feed_sale_msg, String feed_price, String feed_image, String feed_price_after) {
            this.idx = idx;
            this.company_name = company_name;
            this.feed_name = feed_name;
            this.feed_point = feed_point;
            this.feed_section = feed_section;
            this.feed_type = feed_type;
            this.feed_sale = feed_sale;
            this.feed_sale_msg = feed_sale_msg;
            this.feed_price = feed_price;
            this.feed_image = feed_image;
            this.feed_price_after = feed_price_after;
        }
    }

}


