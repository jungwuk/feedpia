package com.pidpia.feedpia;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class DeliverStatus extends FragmentActivity implements OnMapReadyCallback {
    WebClient client;
    TextView tv_delivey_address;
    TextView tv_delivey_request_time;
    TextView tv_delivey_arrive_time;
    TextView tv_delivey_deliver;
    TextView tv_delivery_start_time;
    TextView tv_delivey_deliver_car;
    ImageView img_destination;
    ImageView img_deliver;
    ImageView iv_deliver_call;

    private GoogleMap mMap;


    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case 100:
                    try {
                        if (client.recvData != null) {
                            final JSONObject obj = new JSONObject(client.recvData);
                            if (tv_delivey_address != null) tv_delivey_address.setText("배송지 : "+obj.getString("address"));
                            if (tv_delivey_request_time != null) tv_delivey_request_time.setText(obj.getString("request_time"));
                            if (tv_delivey_arrive_time != null) tv_delivey_arrive_time.setText(obj.getString("arrive_time"));
                            if (tv_delivey_deliver != null) tv_delivey_deliver.setText(obj.getString("deliver"));
                            if (tv_delivey_deliver_car != null) tv_delivey_deliver_car.setText(obj.getString("deliver_car"));
                            if (tv_delivery_start_time != null) tv_delivery_start_time.setText(obj.getString("start_time"));
                            Log.d("GPS_status", String.valueOf(obj));
                            LatLng deliver = new LatLng(obj.getDouble("deliver_lat"),obj.getDouble("deliver_lng"));
                            iv_deliver_call.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+obj.getString("deliver_call")));
                                        startActivity(intent);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(deliver,10));
                            Double del_lat,del_lng,farm_lat,farm_lng ;
                            del_lat = obj.getDouble("deliver_lat");
                            del_lng = obj.getDouble("deliver_lng");
                            farm_lat = obj.getDouble("farm_lat");
                            farm_lng = obj.getDouble("farm_lng");

                            reload(del_lat,del_lng,farm_lat,farm_lng);
                        }

                        break;
                    }catch (Exception e) {

                    }

                case 200:
                    try{
                        if(client.recvData != null){
                            JSONObject objGPS = new JSONObject(client.recvData);
                            Double del_lat,del_lng,farm_lat,farm_lng ;
                            del_lat = objGPS.getDouble("deliver_lat");
                            del_lng = objGPS.getDouble("deliver_lng");
                            farm_lat = objGPS.getDouble("farm_lat");
                            farm_lng = objGPS.getDouble("farm_lng");

                            reload(del_lat,del_lng,farm_lat,farm_lng);
                        }
                    }catch (Exception e) {

                    }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliver_status);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.delivey_map);
        mapFragment.getMapAsync(this);


        final User user = new User(this);
        user.setBoolean("GPS",true);

        Intent intent = getIntent();
        String order_num = intent.getStringExtra("order_num");

        ImageView img_delivey_back = (ImageView)findViewById(R.id.img_delivey_back);
        tv_delivey_address = (TextView)findViewById(R.id.tv_delivey_address);
        tv_delivey_request_time = (TextView)findViewById(R.id.tv_delivey_request_time);
        tv_delivey_arrive_time = (TextView)findViewById(R.id.tv_delivey_arrive_time);
        tv_delivey_deliver = (TextView)findViewById(R.id.tv_delivey_deliver);
        tv_delivery_start_time = (TextView)findViewById(R.id.tv_delivery_start_time);
        img_destination = (ImageView)findViewById(R.id.img_destination);
        tv_delivey_deliver_car = (TextView)findViewById(R.id.tv_delivercar_num);
        img_deliver = (ImageView)findViewById(R.id.img_deliver);
        iv_deliver_call = (ImageView)findViewById(R.id.iv_deliver_call) ;

        JSONObject obj1 = new JSONObject();
        try {
            obj1.put("order_num", order_num);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String data = obj1.toString();
        client = new WebClient(user.URL_DELIVEY_STATE, data, mHandler, 100);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(user.getBoolean("GPS")==true) {
                    SystemClock.sleep(1500);
                    client = new WebClient(user.URL_DELIVEY_GPS, data, mHandler, 200);
                    Log.d("GPS_THREAD","RUUUUUUUUUUUUUUUUUUUUUUUUUN");

                }
            }
        }).start();


        img_delivey_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

    }

    //백버튼 누를시 쓰레드 종료시키기 위해 GPS->false
    @Override
    public void onBackPressed() {
        User user = new User(this);
        user.setBoolean("GPS",false);
        super.onBackPressed();
    }

    public void reload(Double del_lat,Double del_lng,Double farm_lat,Double farm_lng){
        this.mMap.clear();
        final LatLng deliver_location = new LatLng(del_lat,del_lng);
        final LatLng farm_location = new LatLng(farm_lat,farm_lng);

        //배송지 이미지뷰 클릭시 이벤트
        img_destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(farm_location,10));
            }
        });

        //배송차량 이미지뷰 클릭시 이벤트
        img_deliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(deliver_location,10));
            }
        });

        //배송트럭 마커
        MarkerOptions delivery_marker = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_location2))
                .position(deliver_location);

        //배송지 마커
        MarkerOptions farm_marker = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.delivery_marker))
                .position(farm_location);

        //마커추가
        mMap.addMarker(delivery_marker);
        mMap.addMarker(farm_marker);

          }



}
