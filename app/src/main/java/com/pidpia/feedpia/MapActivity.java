package com.pidpia.feedpia;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    Geocoder coder;
    LocationManager locManager;
    LocationListener locationListener;
    double latPoint, lngPoint;
    GoogleMap googleMap;

    LinearLayout map_location_arrive, map_location_current;
    LatLng dest_point;

    EditText map_address_search_input;
    ImageButton map_address_search_button;
    ImageButton map_address_search_exit;
    TextView map_address_text;
    LinearLayout map_address_layout;

    User user;
    float lat, lng;
    WebClient client;

    ListView map_addres_listview;
    AddressListView listView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        user = new User(this);

        locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Log.d("LOC:", user.getData("end_lat") + "/" + user.getData("end_lng"));

        if (!locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
            builder.setMessage("GPS가 꺼져있습니다. GPS를 켜주세요")
                    .setCancelable(false)
                    .setPositiveButton("GPS 켜기", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent gps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(gps);
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            onBackPressed();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        }

        try {


            lat = Float.parseFloat(user.getData("end_lat"));
            lng = Float.parseFloat(user.getData("end_lng"));

            Log.d("LOC2:", lat + "/" + lng);
        } catch (Exception ee) {
            Log.d("error point", "");
            lat = 0.00f;
            lng = 0.00f;
        }

        dest_point = new LatLng(lat, lng);


        map_address_search_input = (EditText) findViewById(R.id.map_address_search_input);
        map_address_search_button = (ImageButton) findViewById(R.id.map_address_search_button);
        map_address_search_exit = (ImageButton)findViewById(R.id.map_address_search_exit);
        map_address_text = (TextView) findViewById(R.id.map_address_text);
        map_address_layout = (LinearLayout) findViewById(R.id.map_address_layout);
        map_addres_listview = (ListView) findViewById(R.id.map_addres_listview);
        Button bt_map_finish = (Button)findViewById(R.id.bt_map_finish);


        bt_map_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(100);
                finish();
            }
        });

        map_address_text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.ACTION_DOWN) {

                    String keyword = map_address_search_input.getText().toString().trim();

                    listView.Update(keyword);

                }
                return false;
            }
        });


        map_address_search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = map_address_search_input.getText().toString().trim();

                listView.Update(keyword);

            }
        });

        map_address_search_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(map_addres_listview.getVisibility()==View.INVISIBLE){
                    onBackPressed();
                }else {
                    map_addres_listview.setVisibility(View.INVISIBLE);
                }

            }
        });
        //map_address_search_input.

        Log.d("[LOC]", "Start");

        //map.moveCamera(CameraUpdateFactory.newLatLng(sydney));


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

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location locations) {

                    String location_address = "";
                    latPoint = locations.getLatitude();
                    lngPoint = locations.getLongitude();

                    Log.d("[LOC]:", latPoint + " " + lngPoint + " - " + location_address);



                    try {
                        if (coder != null) {
                            List<Address> address = coder.getFromLocation(latPoint, lngPoint, 1);
                            if (address != null && address.size() > 0) {
                                location_address = address.get(0).getAddressLine(0).toString();
                                //  order_input_address.setText(location_address);
                                Toast.makeText(MapActivity.this, location_address, Toast.LENGTH_SHORT).show();
                                if (ActivityCompat.checkSelfPermission(MapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.

                                    MarkerOptions marker = new MarkerOptions().position(new LatLng(latPoint, lngPoint)).title(location_address);

                                    googleMap.addMarker(marker);
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latPoint, lngPoint), 16));
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

        Log.d("[LOC]", "Start ok");


        if (ActivityCompat.checkSelfPermission(MapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 1, locationListener);
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, locationListener);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //  gps functionality

            Log.d("[LOC]:", "get permission ok");


        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        //현재위치 버튼 활성화
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);

        LatLng start_point = new LatLng(37.56,126.97);
        MarkerOptions marker = new MarkerOptions().position(dest_point).title("배송지");

//        MarkerOptions marker = new MarkerOptions().position(new LatLng(latPoint, lngPoint)).title("현재위치").icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_location));

        googleMap.addMarker(marker);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start_point, 15));

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                user.setData("map_lng", latLng.longitude + "");
                user.setData("map_lat", latLng.latitude + "");

                // Clears the previously touched position
                googleMap.clear();

                // Animating to the touched position
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                // Placing a marker on the touched position
                googleMap.addMarker(markerOptions);

                String location_address = "";


                if (coder != null) {
                    List<Address> address = null;
                    try {
                        address = coder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (address != null && address.size() > 0) {
                        location_address = address.get(0).getAddressLine(0).toString();
                        map_address_text.setText(location_address);
                        //  order_input_address.setText(location_address);
                        user.setData("map_addr", location_address);

                        Toast.makeText(MapActivity.this, location_address, Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), "Address null.", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });


        listView = new AddressListView(MapActivity.this,map_addres_listview,user,googleMap,map_address_text);


    }
}