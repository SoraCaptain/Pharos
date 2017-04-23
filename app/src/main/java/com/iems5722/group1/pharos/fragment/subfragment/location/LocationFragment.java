package com.iems5722.group1.pharos.fragment.subfragment.location;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.iems5722.group1.pharos.Constants;
import com.iems5722.group1.pharos.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.iems5722.group1.pharos.R.id.map;


public class LocationFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnPoiClickListener {
    private Button button;
    private TextView textView;
    private LocationManager locationManager;
    private LocationListener listener;
    private double longitude;
    private double latitude;
    private List<Location_Entity> location_list;

    private GoogleMap mMap;
    private String formatted_address;

    public static LocationFragment newInstance(String s){
        LocationFragment homeFragment = new LocationFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ARGS,s);

        return homeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {




        View view = inflater.inflate(R.layout.fragment_maps, null, false);
        //Bundle bundle = getArguments();
        //String s = bundle.getString(Constants.ARGS);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);


        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getActivity(), "GPS available", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "GPS not available", Toast.LENGTH_SHORT).show();
        }


        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e("TAG", "IN ON LOCATION CHANGE");
                if (location != null) {
                    showLocation(location);
                    new MyAsyncExtue(latitude,longitude).execute(location);
                }
                else {
                   // textView.setText("Can not obtain data");
                }
//                if(location==null) {
//                    Log.e("longitude", Double.toString(location.getLongitude()));
//                    Log.e("latitude", Double.toString(location.getLatitude()));
//                }
//                textView.append("\n " + location.getLongitude() + " " + location.getLatitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
        configure_button();
        return view;
    }

    @Override
    public void onPoiClick(PointOfInterest poi) {
        Toast.makeText(getActivity(), "Clicked: " +
                        poi.name + "\nPlace ID:" + poi.placeId +
                        "\nLatitude:" + poi.latLng.latitude +
                        " Longitude:" + poi.latLng.longitude,
                Toast.LENGTH_SHORT).show();
        if(poi.placeId!=null) {
            new poiAsyncExtue(poi.placeId, poi.name).execute();
        }
    }

    private class poiAsyncExtue extends AsyncTask<String, Void, String> {

        private String placeId;
        private String name;
        public poiAsyncExtue(String placeid, String name){
            this.placeId=placeid;
            this.name=name;
        }
        @Override
        protected String doInBackground(String... params) {

            StringBuffer sb = new StringBuffer();
            try {
                URL httpUrl = new URL("https://maps.googleapis.com/maps/api/place/details/json?placeid="+ this.placeId+ "&key=AIzaSyCEJXxPebN1xP15X8ShzQMsWT0etG3fqow");
                HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
                connection.setReadTimeout(5000);
                connection.setRequestMethod("GET");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String str = "";

                while ((str = bufferedReader.readLine()) != null) {
                    //Log.e("str", str);
                    sb.append(str);
                }
                Log.e("location_info:", sb.toString());
                JSONObject jsonObject = null;
                jsonObject = new JSONObject(sb.toString());

                String status = null;
                status = jsonObject.getString("status");
                if (status.equals("OK")) {
                    Location_Entity location = new Location_Entity();
                    JSONObject jsonObject1 = null;
                    jsonObject1 = jsonObject.getJSONObject("result");

                    if(!jsonObject1.isNull("formatted_address")) {
                        Log.e("formatted_address", jsonObject1.getString("formatted_address"));
                        location.setAddress(jsonObject1.getString("formatted_address"));
                    }

                    Log.e("name", name);
                    location.setName(name);

                    Log.e("placeId", this.placeId);
                    location.setPlaceId(this.placeId);

                    if(!jsonObject1.isNull("international_phone_number")) {
                        Log.e("phoneNum", jsonObject1.getString("international_phone_number"));
                        location.setPhoneNum(jsonObject1.getString("international_phone_number"));
                    }else if (!jsonObject1.isNull("formatted_phone_number")){
                        Log.e("phoneNum", jsonObject1.getString("formatted_phone_number"));
                        location.setPhoneNum(jsonObject1.getString("formatted_phone_number"));
                    }


                    JSONObject jsonObject2= null;
                    if(!jsonObject1.isNull("opening_hours")) {
                        jsonObject2 = jsonObject1.getJSONObject("opening_hours");

                        if (!jsonObject2.isNull("open_now")) {
                            Log.e("openNow:", String.valueOf(jsonObject2.getBoolean("open_now")));
                            location.setOpennow(jsonObject2.getBoolean("open_now"));
                        }

                        //Log.e("busHour", jsonObject1.getJSONArray("opening_hours"));
                        JSONArray jsonArray2 = jsonObject2.getJSONArray("weekday_text");
                        String opening_hours = "";
                        for (int i = 0; i < 7; i++) {
                            Log.e("weekday_text", jsonArray2.getString(i));
                            if (!jsonArray2.getString(i).isEmpty()) {
                                opening_hours = opening_hours + '\n' + jsonArray2.getString(i);
                            }
                        }
                        Log.e("busHour:", opening_hours);
                        location.setBusHour(opening_hours);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "hello";
        }

        @Override
        protected void onPostExecute(String m_list) {
            super.onPostExecute(m_list);

//            Intent intent = new Intent();
//            intent.putExtra("longitude", longitude);
//            intent.putExtra("latitude", latitude);
//            intent.putExtra("formatted_address", location_list.get(0).getAddress());
//            intent.setClass(MainActivity.this, MapsActivity.class);
//            startActivity(intent);



//            String city = "";
////                if (m_list != null && m_list.size() > 0) {
////                    city = m_list.get(0).getLocality();//获取城市
////                }
//            city = m_list;
//            show_GPS.setText("城市:" + city);
        }
    }


    private class MyAsyncExtue extends AsyncTask<Location, Void, List<Location_Entity>> {

        private double latitude;
        private double longitude;
        public MyAsyncExtue(double latitude, double longitude){
            this.latitude=latitude;
            this.longitude=longitude;
        }
        @Override
        protected List<Location_Entity> doInBackground(Location... params) {

            StringBuffer sb = new StringBuffer();
            try {
                String str_la = String.valueOf(latitude);
                String str_lo = String.valueOf(longitude);
                URL httpUrl = new URL("https://maps.google.com/maps/api/geocode/json?latlng="+ str_la + "," + str_lo +"&key=AIzaSyC81MNlU9zbqBVYAWGxapY6XstRSfFh92I");
                HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
                connection.setReadTimeout(5000);

                connection.setRequestMethod("GET");

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                //StringBuffer sb = new StringBuffer();
                String str = "";

                while ((str = bufferedReader.readLine()) != null) {
                    //Log.e("str", str);
                    sb.append(str);
                }

                Log.e("location_JSON:", sb.toString());
                location_list = new ArrayList<Location_Entity>();
                JSONObject jsonObject = null;

                jsonObject = new JSONObject(sb.toString());

                String status = null;
                status = jsonObject.getString("status");

                if (status.equals("OK")) {
                    JSONArray jsonArray = null;

                    jsonArray = jsonObject.getJSONArray("results");

                    for (int i = 0; i < 1; i++) {
                        JSONObject jsonObject_1 = jsonArray.getJSONObject(i);
                        Location_Entity location = new Location_Entity();
                        //location.setCity(jsonObject_1.getString("city"));
                        //location.setCountry(jsonObject_1.getString("country"));
                        Log.e("formatted_address", jsonObject_1.getString("formatted_address"));
                        location.setAddress(jsonObject_1.getString("formatted_address"));
                        location_list.add(location);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return location_list;
        }

        @Override
        protected void onPostExecute(List<Location_Entity> m_list) {
            super.onPostExecute(m_list);
            Log.e("str", m_list.get(0).getAddress());
            formatted_address = location_list.get(0).getAddress();
//            Intent intent = new Intent();
//            intent.putExtra("longitude", longitude);
//            intent.putExtra("latitude", latitude);
//            intent.putExtra("formatted_address", location_list.get(0).getAddress());
//            intent.setClass(MainActivity.this, MapsActivity.class);
//            startActivity(intent);



//            String city = "";
////                if (m_list != null && m_list.size() > 0) {
////                    city = m_list.get(0).getLocality();//获取城市
////                }
//            city = m_list;
//            show_GPS.setText("城市:" + city);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }

    void configure_button() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }



        // this code won'textView execute IF permissions are not allowed, because in the line above there is return statement.

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                            , 10);
                }
                return;
            }
            if (netWorkIsOpen()) {
                //noinspection MissingPermission
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 500, listener);
            }
            if (gpsIsOpen()) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 500, listener);
            }

//                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);



    }

    private void showLocation(Location location) {
        // 获取经度
        longitude = location.getLongitude();
        // 获取纬度
        latitude = location.getLatitude();
        Log.e("latitude", Double.toString(latitude));
        Log.e("longitude", Double.toString(longitude));
        //Log.e("location(0):", location_list.get(0).toString());
        Log.v("TAG", "IN ON LOCATION CHANGE");
        //String message="经度为:"+longitude+"\n"+"纬度为:"+latitude;
        //textView.setText(message);
    }
    private boolean gpsIsOpen() {
        boolean isOpen = true;
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {//没有开启GPS
            isOpen = false;
        }
        return isOpen;
    }

    private boolean netWorkIsOpen() {
        boolean netIsOpen = true;
        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {//没有开启网络定位
            netIsOpen = false;
        }
        return netIsOpen;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setOnPoiClickListener(this);
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng current_location = new LatLng(latitude,longitude);
        mMap.addMarker(new MarkerOptions().position(current_location).draggable(true).title("Current location: "+ formatted_address));
        CameraPosition.Builder cameraPosition = new CameraPosition.Builder();
        cameraPosition.target(current_location);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current_location));
        mMap.setMinZoomPreference(15.0f);
        mMap.setMaxZoomPreference(20.0f);
        googleMap.setPadding(0, 0, 0, 125);//padding
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setCompassEnabled(false);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(false);
    }

}
