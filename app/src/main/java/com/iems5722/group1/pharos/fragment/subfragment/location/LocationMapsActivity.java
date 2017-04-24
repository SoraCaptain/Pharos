package com.iems5722.group1.pharos.fragment.subfragment.location;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;
import com.iems5722.group1.pharos.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LocationMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnPoiClickListener, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;

    private List<Location_Entity> location_list;
    private String formatted_address;
    private double longitude;
    private double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_maps);
        SwipeBack.attach(this, Position.LEFT)
                .setSwipeBackView(R.layout.swipeback_default);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        latitude = getIntent().getDoubleExtra("Latitude",0);
        longitude = getIntent().getDoubleExtra("Longitude",0);
        Log.e("lat",String.valueOf(latitude));
        Log.e("lon",String.valueOf(longitude));
        new MyAsyncExtue(latitude, longitude).execute();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setOnPoiClickListener(this);
        googleMap.setOnMapLongClickListener(this);
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setCompassEnabled(false);
        uiSettings.setZoomControlsEnabled(true);

    }

    @Override
    public void onMapLongClick(LatLng point){
        mMap.addMarker(new MarkerOptions().position(point).draggable(true).title("Current location: " + formatted_address));
        Toast.makeText(this, "longpress", Toast.LENGTH_SHORT);
    }


    @Override
    public void onPoiClick(PointOfInterest poi) {
        Toast.makeText(this, "Clicked: " +
                        poi.name + "\nPlace ID:" + poi.placeId +
                        "\nLatitude:" + poi.latLng.latitude +
                        " Longitude:" + poi.latLng.longitude,
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.putExtra("PlaceId", poi.placeId);
        intent.setClass(this, LocationDetailActivity.class);
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
//            Log.e("str", m_list.get(0).getAddress());
            formatted_address = location_list.get(0).getAddress();
        }
    }
}
