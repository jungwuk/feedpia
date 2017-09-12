package com.pidpia.feedpia;

/**
 * Created by jenorain on 2017-02-10.
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

public class DeliverListView {

    Context context;
    User user;
    ArrayList<DataOrder> order_data;
    OrderDataAdapter order_adpater;

    WebClient client;

    ListView mListView;
    PopupWindow window;
    TextView status_msg;

    boolean viewType;

    ProgressDialog asyncDialog;


    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case 100:
                    order_data.clear();
                    try {
                        JSONArray ja = new JSONArray(client.recvData);

                        if (ja.length() == 0) {

                            status_msg.setVisibility(View.VISIBLE);
                            status_msg.setText("조회된 목록이 없습니다.");
                        } else {
                            status_msg.setVisibility(View.GONE);
                            for (int i = 0; i < ja.length(); i++) {
                                final JSONObject data = ja.getJSONObject(i);
                                order_data.add(new DataOrder(data.getString("idx"), data.getString("user_name"), data.getString("user_tel"), data.getString("user_address"), data.getString("user_lng"), data.getString("user_lat")));
                            }
                        }

                        order_adpater.notifyDataSetChanged();
                        asyncDialog.hide();


                    } catch (Exception e) {

                    }


                    break;


            }
        }
    };


    DeliverListView(Context _context, ListView listView, User _user, PopupWindow window, TextView status_msg) {
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

        asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        asyncDialog.setMessage("로딩중입니다.");

        // show dialog
        asyncDialog.show();

        JSONObject obj1 = new JSONObject();
        try {
            obj1.put("SSID", user.getData("SSID"));
            obj1.put("UID", user.getData("UID"));
            obj1.put("PID", user.getData("PID"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String data = obj1.toString();
        client = new WebClient(user.URL_DELIVERY_LIST, data, mHandler, 100);


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
                view = mInflater.inflate(R.layout.custom_delivery_list, null);
            } else {
                view = v;
            }

            final DataOrder data = this.getItem(position);
            if (data != null) {

                FrameLayout delivery_row;
                TextView delivery_name, delivery_tel, delivery_address;


                delivery_row = (FrameLayout) view.findViewById(R.id.delivery_row);

                delivery_name = (TextView) view.findViewById(R.id.delivery_name);
                delivery_tel = (TextView) view.findViewById(R.id.delivery_tel);
                delivery_address = (TextView) view.findViewById(R.id.delivery_address);

                delivery_name.setText(data.user_name);
                delivery_tel.setText(data.user_tel);
                delivery_address.setText(data.user_address);

                delivery_row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        window.dismiss();

                        ((MainActivity) context).setDeliveryInfo(data.user_name,data.user_tel,data.user_address,data.user_lat,data.user_lng);

                    }
                });

            }
            return view;
        }
    }

    public class DataOrder {
        String idx, user_name, user_tel, user_address, user_lng, user_lat;

        DataOrder(String idx, String user_name, String user_tel, String user_address, String user_lng, String user_lat) {
            this.idx = idx;
            this.user_name = user_name;
            this.user_tel = user_tel;
            this.user_address = user_address;
            this.user_lng = user_lng;
            this.user_lat = user_lat;


        }
    }

}


