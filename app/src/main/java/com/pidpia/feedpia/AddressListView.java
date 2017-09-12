package com.pidpia.feedpia;

/**
 * Created by jenorain on 2017-02-14.
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by jenorain on 2016-12-16.
 */

public class AddressListView {

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
    GoogleMap googleMap;
    TextView map_address_text;
    LinearLayout map_address_layout;

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case 100:
                    order_data.clear();
                    try {
                        if (client.recvData != null) {
                            JSONObject obj = new JSONObject(client.recvData);
                            JSONObject obj_sub = obj.getJSONObject("channel");
                            JSONArray obj_data = obj_sub.getJSONArray("item");


                            if (obj_data.length() == 0) {
                            } else {
                                for (int i = 0; i < obj_data.length(); i++) {
                                    final JSONObject data = obj_data.getJSONObject(i);
                                    Log.d("DATA", data.getString("title"));
                                    Log.d("DATA", data.getString("address"));
                                    order_data.add(new DataOrder(data.getString("title"), data.getString("address"), data.getString("latitude"), data.getString("longitude")));

                                }
                            }


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


    AddressListView(Context _context, ListView listView, User _user, GoogleMap googleMap, TextView map_address_text) {
        context = _context;
        user = _user;
        mListView = listView;
        order_data = new ArrayList<DataOrder>();
        this.googleMap = googleMap;
        order_adpater = new OrderDataAdapter(context, order_data);
        mListView.setAdapter(order_adpater);
        this.map_address_text = map_address_text;


        asyncDialog = new ProgressDialog(context);

        asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        asyncDialog.setMessage("로딩중입니다.");

    }


    void Update(String keyword) {
        mListView.setVisibility(View.VISIBLE);
        asyncDialog.show();
        if (keyword.length() > 0) try {
            client = new WebClient("https://apis.daum.net/local/v1/search/keyword.json?apikey=c87b55e3d2a9d5bc0df826000ba665c0&query=" + URLEncoder.encode(keyword, "UTF-8"), "", mHandler, 100);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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
                view = mInflater.inflate(R.layout.custom_address_list, null);
            } else {
                view = v;
            }

            final DataOrder data = this.getItem(position);
            if (data != null) {


                LinearLayout address_row;
                ImageView address_icon;
                TextView address_title, address_msg;

                address_row = (LinearLayout) view.findViewById(R.id.address_row);

               // address_icon = (ImageView) view.findViewById(R.id.address_icon);

                address_title = (TextView) view.findViewById(R.id.address_title);
                address_msg = (TextView) view.findViewById(R.id.address_msg);


                address_title.setText(data.title);
                address_msg.setText(data.address);
                map_address_layout = (LinearLayout)view.findViewById(R.id.map_address_layout);

                address_row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MarkerOptions marker = new MarkerOptions().position(new LatLng(Float.parseFloat(data.gps_lat), Float.parseFloat(data.gps_lng))).title(data.title);
                        googleMap.addMarker(marker);

                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Float.parseFloat(data.gps_lat), Float.parseFloat(data.gps_lng)), 16));
                        Toast.makeText(context, data.gps_lat+" "+data.gps_lng+" "+data.title, Toast.LENGTH_SHORT).show();

                        Log.d("[LOC]", data.gps_lat + "/" + data.gps_lng);
                        map_address_text.setText(data.address);
                        //map_address_layout.setVisibility(View.VISIBLE);
                        user.setData("map_lng", data.gps_lng);
                        user.setData("map_lat", data.gps_lat);
                        user.setData("map_addr", data.address);

                        mListView.setVisibility(View.INVISIBLE);

                    }
                });


            }
            return view;
        }
    }

    public class DataOrder {
        String title, address, gps_lat, gps_lng;

        DataOrder(String title, String address, String gps_lat, String gps_lng) {

            this.title = title;
            this.address = address;
            this.gps_lat = gps_lat;
            this.gps_lng = gps_lng;

        }
    }

}


