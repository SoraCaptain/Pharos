package com.iems5722.group1.pharos.fragment.subfragment.home;

import android.Manifest;
import android.content.Context;
import android.content.EntityIterator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.iems5722.group1.pharos.Constants;
import com.iems5722.group1.pharos.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Kevin on 2016/11/28.
 * Blog:http://blog.csdn.net/student9128
 * Description: HomeFragment
 */

public class HomeFragment extends Fragment{
    private LocationManager locationManager;
    private LocationListener listener;
    private double longitude;
    private double latitude;
    private List<Entity_Home> location_list;
    private String photo_reference;

    ListView listView;
    LvAdapter_Home adapter;

    public static HomeFragment newInstance(String s){
        HomeFragment homeFragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ARGS,s);
        homeFragment.setArguments(bundle);
        return homeFragment;
}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        location_list = new ArrayList<>();
        listView = (ListView)view.findViewById(R.id.lvHome);
        adapter = new LvAdapter_Home(getActivity(),location_list);
        listView.setAdapter(adapter);

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

                }
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
    private void showLocation(Location location) {
        // 获取经度
        longitude = location.getLongitude();
        // 获取纬度
        latitude = location.getLatitude();
        Log.e("latitude", Double.toString(latitude));
        Log.e("longitude", Double.toString(longitude));
        Log.v("TAG", "IN ON LOCATION CHANGE");

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

    private class MyAsyncExtue extends AsyncTask<Location, Void, List<Entity_Home>> {

        private double latitude;
        private double longitude;

        public MyAsyncExtue(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected List<Entity_Home> doInBackground(Location... params) {
            List<Entity_Home> locationList =new ArrayList<>();
            StringBuffer sb = new StringBuffer();
            try {
                String str_la = String.valueOf(latitude);
                String str_lo = String.valueOf(longitude);
                URL httpUrl = new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + str_la + "," + str_lo + "&radius=50000&type=restaurant&key=AIzaSyARMqBMqfTYhi6NrUuF7RmvoJ69yBTynYA");
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

                Log.e("location_nearby:", sb.toString());

                JSONObject jsonObject = null;

                jsonObject = new JSONObject(sb.toString());

                String status = null;
                status = jsonObject.getString("status");
                location_list.clear();
                if (status.equals("OK")) {
                    JSONArray jsonArray = null;
                    jsonArray = jsonObject.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject_1 = jsonArray.getJSONObject(i);
                        Entity_Home location = new Entity_Home();
                        String typearray= "";
                        if(!jsonObject_1.isNull("types")){
                            JSONArray jsonArray_2 = jsonObject_1.getJSONArray("types");
                            for(int j = 0; j < jsonArray_2.length(); j++){
                                typearray = typearray + jsonArray_2.get(j) + ";";
                            }
                            Log.e("typearray", typearray);
                            location.setPlaceType(typearray);
                        }

                        Log.e("place_id", jsonObject_1.getString("place_id"));
                        if(!jsonObject_1.isNull("photos")){
                            location.setPlaceId(jsonObject_1.getString("place_id"));

                            JSONArray jsonArray_2 = jsonObject_1.getJSONArray("photos");

                            Log.e("photo_reference", jsonArray_2.getJSONObject(0).getString("photo_reference"));
                            photo_reference = jsonArray_2.getJSONObject(0).getString("photo_reference");
                            location.setPlacePhotoPre(jsonArray_2.getJSONObject(0).getString("photo_reference"));

                            Log.e("height&width", jsonArray_2.getJSONObject(0).getString("height") + ' ' + jsonArray_2.getJSONObject(0).getString("width"));
                            location.setPlaceImgWidth(jsonArray_2.getJSONObject(0).getString("width"));

                            Log.e("place_name", jsonObject_1.getString("name"));
                            location.setPlaceName(jsonObject_1.getString("name"));

                            locationList.add(location);
                        }

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return locationList;
        }

        @Override
        protected void onPostExecute(List<Entity_Home> m_list) {
            super.onPostExecute(m_list);


            for (Entity_Home entity : m_list) {
                location_list.add(entity);
                //new MyAsyncGetImg().execute();
                //Log.e("str", m_list.get(0).getAddress());
                adapter.notifyDataSetChanged();
            }
        }
    }

    private class MyAsyncGetImg extends AsyncTask<Object, Object, Void> {
        int index=0;
        MyAsyncGetImg(int i){
            this.index = i;
        }
        @Override
        protected void onPreExecute(){
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Object... params) {
            Log.e("getPic",String.valueOf(location_list.size()));
           // ArrayList<Bitmap> bmp = new ArrayList<Bitmap>();

                try {
                    Bitmap bitmap = null;
                    //Log.e("location",location_list.get(i).getPlaceName());
                    URL httpUrl = new URL("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + location_list.get(index).getPlacePhotoPre() + "&key=AIzaSyARMqBMqfTYhi6NrUuF7RmvoJ69yBTynYA");
                    HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
                    conn.setConnectTimeout(6000);//设置超时
                    conn.setDoInput(true);
                    conn.setUseCaches(false);//不缓存
                    conn.connect();
                    InputStream is = conn.getInputStream();//获得图片的数据流

                    bitmap = BitmapFactory.decodeStream(is);
                    is.close();
                    //bmp.add(bitmap);
                    Log.e("big",String.valueOf(bitmap.getDensity()));
                    location_list.get(index).setPlaceImg(bitmap);

                    //   dataArrays.add(en);

                } catch (Exception e) {
                    e.printStackTrace();
                }

           return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            adapter.notifyDataSetChanged();

         //   listView.setSelection(location_list.size());

            //Log.e("str", m_list.get(0).getAddress());
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

    }

}
