package com.iems5722.group1.pharos.module.favorite;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.iems5722.group1.pharos.R;
import com.iems5722.group1.pharos.fragment.subfragment.location.LocationDetailActivity;
import com.iems5722.group1.pharos.module.chat.ChatActivity;
import com.iems5722.group1.pharos.module.contact.ContactActivity;
import com.iems5722.group1.pharos.module.contact.Entity_Contact;
import com.iems5722.group1.pharos.module.contact.LvAdapter_Contact;
import com.iems5722.group1.pharos.utils.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sora on 6/4/17.
 */

public class FavActivity extends AppCompatActivity {

    private ListView listView;
    private LvAdapter_Fav adapter;
    private List<Entity_Fav> dataArrays = new ArrayList();
    private ImageButton ibCross;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_fav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       // ibCross = (ImageButton)findViewById(R.id.ic_action_cross);
        listView = (ListView)findViewById(R.id.lv_fav);
        adapter = new LvAdapter_Fav(FavActivity.this,dataArrays);

        TaskGetFavList taskGetFavList = new TaskGetFavList(Util.getUsername(this));
        taskGetFavList.execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                Log.e("place","click");
                Intent intent = new Intent();
                intent.putExtra("PlaceId", dataArrays.get(position).getID());
                intent.setClass(FavActivity.this, LocationDetailActivity.class);
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                dataArrays.remove(position);
                adapter.notifyDataSetChanged();

                return false;
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

    class TaskGetFavList extends AsyncTask<String, Integer, List<Entity_Fav>> {
        private List<Entity_Fav> newFavList;
        //  private String jsonUrl = "http://iems5722.albertauyeung.com/api/asgn2/get_chatrooms";
        private String jsonUrl = "http://54.202.138.123:5000/pharos/api/getFavList";
        private  String name;
        TaskGetFavList(String name){
            this.name = name;
            jsonUrl=jsonUrl+"?user_name="+name;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("GET", "onPreExecute() called");
        }
        @Override
        protected List<Entity_Fav> doInBackground(String... params) {
            Log.i("GET", "doInBackground(Params... params) called");
            return getJsonData(jsonUrl);
        }

        protected void onPostExecute(List<Entity_Fav> result) {
            Log.i("GET", "doPostExecute called");
            Log.e("GET", String.valueOf(result.size()));
            if(result.size()>0){
                listView.setAdapter(adapter);
                for (Entity_Fav en : result) {
                    dataArrays.add(en);
                    adapter.notifyDataSetChanged();
                    listView.setSelection(listView.getCount() - 1);
                }
            }
            super.onPostExecute(result);
        }

        public List<Entity_Fav> getJsonData(String jsonUrl) {
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
                newFavList = new ArrayList<>();
                // 整体是一个jsonObject
                JSONObject jsonObject = new JSONObject(sb.toString());
                if(jsonObject.getString("status").equals("OK")){
                    // 键是jsonArray数组
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        //获取jsonArray中的每个对象
                        JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                        //创建本地的newsBean对象
                        Entity_Fav entity = new Entity_Fav();
                        //为该对象进行属性值的设置操
                        entity.id = jsonObject2.getString("place_id");
                        entity.name = jsonObject2.getString("place_name");
                        Log.e("fetch",entity.getID());
                        Log.e("fetch",entity.getName());
                        //添加对象，组建集合
                        newFavList.add(entity);
                    }
                }
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return newFavList;
        }

    }

    class TaskDelFav extends AsyncTask<String, Integer, String> {
        // private String jsonUrl = "http://iems5722.albertauyeung.com/api/asgn2/get_messages";
        private String jsonUrl = "http://54.202.138.123:5000/pharos/api/delFav";
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
}
