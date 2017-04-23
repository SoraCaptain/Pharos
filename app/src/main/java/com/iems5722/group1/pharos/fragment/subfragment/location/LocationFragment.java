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

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
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
import com.iems5722.group1.pharos.utils.Util;

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


public class LocationFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnPoiClickListener, GoogleMap.OnMapLongClickListener {
    private Button button;
    private TextView textView;
    private LocationManager locationManager;
    private LocationListener listener;
    private double longitude;
    private double latitude;
    private boolean transfer;
    private List<Location_Entity> location_list;

    private GoogleMap mMap;
    private String formatted_address;

    public static LocationFragment newInstance(String s) {
        LocationFragment homeFragment = new LocationFragment();
        Bundle bundle = new Bundle();
        bundle.putString("data", s);
        return homeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_maps, null, false);
//        Bundle bundle = getArguments();
//        String s = bundle.getString("data");
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
//        String[] data = s.split(",");
//        if(data.length==3 && data[2].equals("true")) {
//
//            latitude = getArguments().getDouble("Latitude", 0);
//            Log.e("latitude", String.valueOf(latitude));
//
//            longitude = getArguments().getDouble("Longitude", 0);
//            Log.e("longitude", String.valueOf(longitude));
//
//            transfer = getArguments().getBoolean("transfer", false);
//            Log.e("transfer", String.valueOf(transfer));
//
//            new MyAsyncExtue(latitude, longitude).execute();
//    }
//        else{
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
                        new MyAsyncExtue(latitude, longitude).execute(location);
                    } else {
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
//        }

        return view;
    }

    @Override
    public void onMapLongClick(LatLng point){
        mMap.addMarker(new MarkerOptions().position(point).draggable(true).title("Current location: " + formatted_address));
        Toast.makeText(getActivity(), "longpress", Toast.LENGTH_SHORT);
    }


    @Override
    public void onPoiClick(PointOfInterest poi) {
        Toast.makeText(getActivity(), "Clicked: " +
                        poi.name + "\nPlace ID:" + poi.placeId +
                        "\nLatitude:" + poi.latLng.latitude +
                        " Longitude:" + poi.latLng.longitude,
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.putExtra("PlaceId", poi.placeId);
        intent.setClass(getActivity(), LocationDetailActivity.class);
        startActivity(intent);
    }




    private class MyAsyncExtue extends AsyncTask<Location, Void, List<Location_Entity>> {

        private double latitude;
        private double longitude;

        public MyAsyncExtue(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected List<Location_Entity> doInBackground(Location... params) {

            StringBuffer sb = new StringBuffer();
            try {
                String str_la = String.valueOf(latitude);
                String str_lo = String.valueOf(longitude);
                URL httpUrl = new URL("https://maps.google.com/maps/api/geocode/json?latlng=" + str_la + "," + str_lo + "&key=AIzaSyC81MNlU9zbqBVYAWGxapY6XstRSfFh92I");
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
        googleMap.setOnMapLongClickListener(this);
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        // Add a marker in Sydney and move the camera
        LatLng current_location = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(current_location).draggable(true).title("Current location: " + formatted_address));
        CameraPosition.Builder cameraPosition = new CameraPosition.Builder();
        cameraPosition.target(current_location);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current_location));
        mMap.setMinZoomPreference(16.0f);
        mMap.setMaxZoomPreference(20.0f);
        googleMap.setPadding(0, 300, 0, 125);//padding
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setCompassEnabled(false);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(false);
    }

}
