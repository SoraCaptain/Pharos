package com.iems5722.group1.pharos.fragment.subfragment.location;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;
import com.iems5722.group1.pharos.R;
import com.iems5722.group1.pharos.utils.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Sora on 22/4/17.
 */

public class LocationDetailActivity extends AppCompatActivity {
    ImageView ivPic;
    ImageButton btnFav;
    TextView tvName;
    TextView tvPhoneNum;
    TextView tvBusHour;
    TextView tvContact;
    TextView tvAddress;
    TextView tvOpenNow;

    String Name;
    String PhoneNum;
    String BusHour;
    String PlaceId;
    String Address;
    String OpenNow;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_maps_detail);
        PlaceId = getIntent().getStringExtra("PlaceId");
        if (PlaceId != null) {
            new poiAsyncExtue(PlaceId).execute();
        }
        SwipeBack.attach(this, Position.LEFT)
                .setSwipeBackView(R.layout.swipeback_default);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ivPic = (ImageView) findViewById(R.id.ivPic);
        btnFav = (ImageButton) findViewById(R.id.ibFav);
        tvName = (TextView) findViewById(R.id.tvName);
        tvPhoneNum = (TextView) findViewById(R.id.tvPhoneNum);
        tvBusHour = (TextView) findViewById(R.id.tvBusHour);
        tvContact = (TextView) findViewById(R.id.tvContact);
        tvAddress = (TextView) findViewById(R.id.tvAdd);
        tvOpenNow = (TextView) findViewById(R.id.tvOpenNow);



        String userName = Util.getUsername(this);
        if (!userName.equals("null")) {

        }

        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class poiAsyncExtue extends AsyncTask<String, Void, Location_Entity> {

        private String placeId;

        public poiAsyncExtue(String placeid) {
            this.placeId = placeid;
        }

        @Override
        protected Location_Entity doInBackground(String... params) {

            StringBuffer sb = new StringBuffer();
            Location_Entity location = new Location_Entity();
            try {

                URL httpUrl = new URL("https://maps.googleapis.com/maps/api/place/details/json?placeid=" + this.placeId + "&key=AIzaSyCEJXxPebN1xP15X8ShzQMsWT0etG3fqow");
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
                    JSONObject jsonObject1 = null;
                    jsonObject1 = jsonObject.getJSONObject("result");

                    if (!jsonObject1.isNull("formatted_address")) {
                        Log.e("formatted_address", jsonObject1.getString("formatted_address"));
                        location.setAddress(jsonObject1.getString("formatted_address"));
                    }

                    if (!jsonObject1.isNull("name")) {
                        Log.e("name", jsonObject1.getString("name"));
                        location.setName(jsonObject1.getString("name"));
                    }

                    Log.e("placeId", this.placeId);
                    location.setPlaceId(this.placeId);

                    if (!jsonObject1.isNull("international_phone_number")) {
                        Log.e("phoneNum", jsonObject1.getString("international_phone_number"));
                        location.setPhoneNum(jsonObject1.getString("international_phone_number"));
                    } else if (!jsonObject1.isNull("formatted_phone_number")) {
                        Log.e("phoneNum", jsonObject1.getString("formatted_phone_number"));
                        location.setPhoneNum(jsonObject1.getString("formatted_phone_number"));
                    }


                    JSONObject jsonObject2 = null;
                    if (!jsonObject1.isNull("opening_hours")) {
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
            return location;
        }

        @Override
        protected void onPostExecute(Location_Entity location) {
            super.onPostExecute(location);
            Name = location.getName();
            PhoneNum = location.getPhoneNum();
            BusHour = location.getBusHour();
            Address = location.getAddress();
            if(location.getOpennow()){
                OpenNow = "Open Now";
            }else{
                OpenNow = "Closed Now";
            }
            tvName.setText(Name);
            tvPhoneNum.setText(PhoneNum);
            tvBusHour.setText(BusHour);
            tvAddress.setText(Address);
            tvOpenNow.setText(OpenNow);
            //ivPic.setImageBitmap(getURLimage());

        }
    }
    public Bitmap getURLimage(String url) {
        Bitmap bmp = null;
        try {
            URL myurl = new URL(url);
            // 获得连接
            HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
            conn.setConnectTimeout(6000);//设置超时
            conn.setDoInput(true);
            conn.setUseCaches(false);//不缓存
            conn.connect();
            InputStream is = conn.getInputStream();//获得图片的数据流
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }
}
