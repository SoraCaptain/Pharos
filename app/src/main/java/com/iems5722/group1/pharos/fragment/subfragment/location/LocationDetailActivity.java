package com.iems5722.group1.pharos.fragment.subfragment.location;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;
import com.iems5722.group1.pharos.R;
import com.iems5722.group1.pharos.fragment.subfragment.person.Entity_Person_List;
import com.iems5722.group1.pharos.fragment.subfragment.person.LvAdapter_List;
import com.iems5722.group1.pharos.module.contact.Entity_Contact;
import com.iems5722.group1.pharos.utils.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

    double lat;
    double lng;
    boolean isFav;
    String userName;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_maps_detail);

        ivPic = (ImageView) findViewById(R.id.ivPic);
        btnFav = (ImageButton) findViewById(R.id.ibFav);
        tvName = (TextView) findViewById(R.id.tvName);
        tvPhoneNum = (TextView) findViewById(R.id.tvPhoneNum);
        tvBusHour = (TextView) findViewById(R.id.tvBusHour);
        tvContact = (TextView) findViewById(R.id.tvContact);
        tvAddress = (TextView) findViewById(R.id.tvAdd);
        tvOpenNow = (TextView) findViewById(R.id.tvOpenNow);

        btnFav.setClickable(true);
        PlaceId = getIntent().getStringExtra("PlaceId");
        if (PlaceId != null) {
            new poiAsyncExtue(PlaceId).execute();
        }
        SwipeBack.attach(this, Position.LEFT)
                .setSwipeBackView(R.layout.swipeback_default);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        isFav = false;

        userName = Util.getUsername(this);
        Log.e("username",userName);
        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("click","ok");
                if(isFav){
                    isFav=false;
                    Log.e("click","isFav");
                    btnFav.setImageResource(R.drawable.ic_action_fav);
                    Log.e("placeid",PlaceId);
                    new TaskDelFav(userName,PlaceId).execute();
                }
                else{
                    isFav=true;
                    Log.e("click","isnotFav");
                    Log.e("placeid",PlaceId);
                    btnFav.setImageResource(R.drawable.ic_action_faved);
                    new TaskSetFav(userName,PlaceId).execute();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_share:
                String userName = Util.getUsername(LocationDetailActivity.this);
                if(userName.equals("null")){
                    Toast.makeText(LocationDetailActivity.this,"please login first",Toast.LENGTH_SHORT);
                }
                else{
                    TaskGetFriendList taskGetFriendList = new TaskGetFriendList(userName);
                    taskGetFriendList.execute();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_place, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void toMap(View v){
        Intent intent = new Intent();
        intent.putExtra("Latitude", lat);
        intent.putExtra("Longitude", lng);
        intent.setClass(this, LocationMapsActivity.class);
        startActivity(intent);
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

                    JSONObject jsonObject3 = null;
                    if(!jsonObject1.isNull("geometry")){
                        jsonObject3 = jsonObject1.getJSONObject("geometry").getJSONObject("location");
                        lat = jsonObject3.getDouble("lat");
                        lng = jsonObject3.getDouble("lng");
                        Log.e("lat", String.valueOf(lat));
                        Log.e("lng", String.valueOf(lng));
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
            tvPhoneNum.setText("Phone: "+PhoneNum);
            tvBusHour.setText("business time" + BusHour);
            tvAddress.setText("Address: "+Address);
            tvOpenNow.setText(OpenNow);
            if (userName.equals("null")) {
                btnFav.setClickable(false);
            }
            else{
                btnFav.setClickable(true);
                Log.e("checkFav",String.valueOf(PlaceId)+" "+Name);
                TaskGetCheckFav taskGetCheckFav = new TaskGetCheckFav(userName,PlaceId,Name);
                taskGetCheckFav.execute();
            }
            //ivPic.setImageBitmap(getURLimage());

        }
    }

    class TaskGetCheckFav extends AsyncTask<String, Integer, String> {
        // private String jsonUrl = "http://iems5722.albertauyeung.com/api/asgn2/get_messages";
        private String jsonUrl = "http://54.202.138.123:8000/pharos/api/checkFav";
        String result = "";
        public TaskGetCheckFav(String name,String place_id,String place_name) {
            place_name = place_name.replace(" ","%20");
            Log.e("checkFav",place_name);
            this.jsonUrl = this.jsonUrl + "?user_name="+name+"&place_id="+place_id+"&place_name="+place_name;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("GET", "onPreExecute() called");
        }

        @Override
        protected String doInBackground(String... params) {
            Log.i("GET", "doInBackground(Params... params) called");
            return getJsonData(jsonUrl);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("checkFav",result);
            if(result.equals("1")){
                isFav=true;
                btnFav.setImageResource(R.drawable.ic_action_faved);
            }
        }

        public String getJsonData(String jsonUrl) {
            try {
                //创建url http地址
                URL httpUrl = new URL(jsonUrl);
                //打开http 链接
                HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
                //设置参数  请求为get请求
                connection.setReadTimeout(5000);
                connection.setRequestMethod("GET");
                //connection.getInputStream()得到字节输入流，InputStreamReader从字节到字符的桥梁，外加包装字符流
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                //创建字符串容器
                StringBuffer sb = new StringBuffer();
                String str = "";
                //行读取
                while ((str = bufferedReader.readLine()) != null) {
                    // 当读取完毕，就添加到容器中
                    sb.append(str);
                }
                //测试是否得到json字符串
                Log.e("TAG", "" + sb.toString());
                //创建本地对象的集合
                JSONObject jsonObject = new JSONObject(sb.toString());
                if(jsonObject.getString("status").equals("OK")){
                    result = jsonObject.getJSONArray("data").getString(0);
                }

                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    class TaskSetFav extends AsyncTask<String, Integer, String> {
        // private String jsonUrl = "http://iems5722.albertauyeung.com/api/asgn2/get_messages";
        private String jsonUrl = "http://54.202.138.123:8000/pharos/api/setFav";
        String result = "";
        public TaskSetFav(String name,String place_id) {
            this.jsonUrl = this.jsonUrl + "?user_name="+name+"&place_id="+place_id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("GET", "onPreExecute() called");
        }

        @Override
        protected String doInBackground(String... params) {
            Log.i("GET", "doInBackground(Params... params) called");
            return getJsonData(jsonUrl);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

        }

        public String getJsonData(String jsonUrl) {
            try {
                //创建url http地址
                URL httpUrl = new URL(jsonUrl);
                //打开http 链接
                HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
                //设置参数  请求为get请求
                connection.setReadTimeout(5000);
                connection.setRequestMethod("GET");
                //connection.getInputStream()得到字节输入流，InputStreamReader从字节到字符的桥梁，外加包装字符流
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                //创建字符串容器
                StringBuffer sb = new StringBuffer();
                String str = "";
                //行读取
                while ((str = bufferedReader.readLine()) != null) {
                    // 当读取完毕，就添加到容器中
                    sb.append(str);
                }
                //测试是否得到json字符串
                Log.e("TAG", "" + sb.toString());
                //创建本地对象的集合
                JSONObject jsonObject = new JSONObject(sb.toString());
                result = jsonObject.getString("status");
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    class TaskDelFav extends AsyncTask<String, Integer, String> {
        // private String jsonUrl = "http://iems5722.albertauyeung.com/api/asgn2/get_messages";
        private String jsonUrl = "http://54.202.138.123:8000/pharos/api/delFav";
        String result = "";
        public TaskDelFav(String name,String place_id) {
            this.jsonUrl = this.jsonUrl + "?user_name="+name+"&place_id="+place_id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("GET", "onPreExecute() called");
        }

        @Override
        protected String doInBackground(String... params) {
            Log.i("GET", "doInBackground(Params... params) called");
            return getJsonData(jsonUrl);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

        }

        public String getJsonData(String jsonUrl) {
            try {
                //创建url http地址
                URL httpUrl = new URL(jsonUrl);
                //打开http 链接
                HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
                //设置参数  请求为get请求
                connection.setReadTimeout(5000);
                connection.setRequestMethod("GET");
                //connection.getInputStream()得到字节输入流，InputStreamReader从字节到字符的桥梁，外加包装字符流
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                //创建字符串容器
                StringBuffer sb = new StringBuffer();
                String str = "";
                //行读取
                while ((str = bufferedReader.readLine()) != null) {
                    // 当读取完毕，就添加到容器中
                    sb.append(str);
                }
                //测试是否得到json字符串
                Log.e("TAG", "" + sb.toString());
                //创建本地对象的集合
                JSONObject jsonObject = new JSONObject(sb.toString());
                result = jsonObject.getString("status");
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
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

    class TaskGetFriendList extends AsyncTask<String, Integer,ArrayList<String>> {
        //  private String jsonUrl = "http://iems5722.albertauyeung.com/api/asgn2/get_chatrooms";
        private String jsonUrl = "http://54.202.138.123:8000/pharos/api/getFriendsList";
        private  String name;
        TaskGetFriendList(String name){
            this.name = name;
            jsonUrl=jsonUrl+"?user_name="+name;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("GET", "onPreExecute() called");
        }
        @Override
        protected ArrayList<String> doInBackground(String... params) {
            Log.i("GET", "doInBackground(Params... params) called");
            return getJsonData(jsonUrl);
        }

        protected void onPostExecute(ArrayList<String> result) {
            Log.i("GET", "doPostExecute called");
            Log.e("GET", String.valueOf(result.size()));
            if(result.size()>0){
                LocationShareFragment dialog = new LocationShareFragment();
                Bundle bundle = new Bundle();
                    bundle.putCharSequenceArray("friendList", result.toArray(new CharSequence[result.size()]));
                    dialog.setArguments(bundle);
                    bundle.putString("placeName", Name);
                    bundle.putString("userName",userName);
                    dialog.show(getSupportFragmentManager(), "LocationShareFragment");
            }
            else{
                Toast.makeText(LocationDetailActivity.this,"you don't have any friend yet",Toast.LENGTH_SHORT);
            }
            super.onPostExecute(result);
        }

        public ArrayList<String> getJsonData(String jsonUrl) {
            ArrayList<String> friendsList = new ArrayList<>();
            try {
                //创建url http地址
                URL httpUrl = new URL(jsonUrl);
                //打开http 链接
                HttpURLConnection connection = (HttpURLConnection) httpUrl
                        .openConnection();
                //设置参数  请求为get请求
                connection.setReadTimeout(5000);
                connection.setRequestMethod("GET");
                //connection.getInputStream()得到字节输入流，InputStreamReader从字节到字符的桥梁，外加包装字符流
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                //创建字符串容器
                StringBuffer sb = new StringBuffer();
                String str = "";
                //行读取
                while ((str = bufferedReader.readLine()) != null) {
                    // 当读取完毕，就添加到容器中
                    sb.append(str);
                }
                //测试是否得到json字符串
                Log.e("TAG", ""+sb.toString());
                //创建本地对象的集合

                // 整体是一个jsonObject
                JSONObject jsonObject = new JSONObject(sb.toString());
                if(jsonObject.getString("status").equals("OK")){
                    // 键是jsonArray数组
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        //获取jsonArray中的每个对象
                        JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                        //添加对象，组建集合
                        friendsList.add(jsonObject2.getString("friend_name"));
                    }
                }
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return friendsList;
        }

    }
}
