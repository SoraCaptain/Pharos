package com.iems5722.group1.pharos.module.contact;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.iems5722.group1.pharos.MainActivity;
import com.iems5722.group1.pharos.R;
import com.iems5722.group1.pharos.fragment.subfragment.person.Entity_Notice_List;
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

public class ContactActivity extends AppCompatActivity {
    private ListView listView;
    private LvAdapter_Contact adapter;
    private List<Entity_Contact> dataArrays = new ArrayList();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = (ListView)findViewById(R.id.lv_contact);
        adapter = new LvAdapter_Contact(ContactActivity.this,dataArrays);
        TaskGetFriendList taskGetFriendList = new TaskGetFriendList(Util.getUsername(this));
        taskGetFriendList.execute();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("friend_id", dataArrays.get(position).getID());
                intent.putExtra("friend_name", dataArrays.get(position).getName());
                intent.setClass(ContactActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contact, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_plus:
                Intent intent = new Intent();
                intent.setClass(ContactActivity.this, ContactAddActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    class TaskGetFriendList extends AsyncTask<String, Integer, List<Entity_Contact>> {
        private List<Entity_Contact> newFriendsList;
        //  private String jsonUrl = "http://iems5722.albertauyeung.com/api/asgn2/get_chatrooms";
        private String jsonUrl = "http://54.202.138.123:5000/pharos/api/getFriendsList";
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
        protected List<Entity_Contact> doInBackground(String... params) {
            Log.i("GET", "doInBackground(Params... params) called");
            return getJsonData(jsonUrl);
        }

        protected void onPostExecute(List<Entity_Contact> result) {
            Log.i("GET", "doPostExecute called");
            Log.e("GET", String.valueOf(result.size()));
            if(result.size()>0){
                listView.setAdapter(adapter);
                for (Entity_Contact en : result) {
                    dataArrays.add(en);
                    adapter.notifyDataSetChanged();
                    listView.setSelection(listView.getCount() - 1);
                }
            }
            super.onPostExecute(result);
        }

        public List<Entity_Contact> getJsonData(String jsonUrl) {
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
                newFriendsList = new ArrayList<>();
                // 整体是一个jsonObject
                JSONObject jsonObject = new JSONObject(sb.toString());
                if(jsonObject.getString("status").equals("OK")){
                    // 键是jsonArray数组
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        //获取jsonArray中的每个对象
                        JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                        //创建本地的newsBean对象
                        Entity_Contact entity = new Entity_Contact();
                        //为该对象进行属性值的设置操
                        entity.id = jsonObject2.getString("friend_id");
                        entity.name = jsonObject2.getString("friend_name");
                        //添加对象，组建集合
                        newFriendsList.add(entity);
                    }
                }
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return newFriendsList;
        }

    }

}
