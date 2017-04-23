package com.iems5722.group1.pharos.fragment.subfragment.person;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.iems5722.group1.pharos.R;
import com.iems5722.group1.pharos.module.contact.ContactActivity;
import com.iems5722.group1.pharos.module.contact.ContactAddActivity;
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
 * Created by Sora on 10/4/17.
 */

public class PersonNoticeList extends AppCompatActivity {
    private ListView listView;
    private LvAdapter_Notice_List adapter;
    private List<Entity_Notice_List> dataArrays = new ArrayList();
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_me_notice_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = (ListView)findViewById(R.id.lv);
        adapter = new LvAdapter_Notice_List(PersonNoticeList.this,dataArrays);

        TaskGetNoticeList taskGetNoticeList = new TaskGetNoticeList(Util.getUsername(this));
        taskGetNoticeList.execute();
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

    class TaskGetNoticeList extends AsyncTask<String, Integer, List<Entity_Notice_List>> {
        private List<Entity_Notice_List> newNoticeList;
        //  private String jsonUrl = "http://iems5722.albertauyeung.com/api/asgn2/get_chatrooms";
        private String jsonUrl = "http://54.202.138.123:8000/pharos/api/getNoticeList";
        private  String name;
        TaskGetNoticeList(String name){
            this.name = name;
            jsonUrl=jsonUrl+"?user_name="+name;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("GET", "onPreExecute() called");
        }
        @Override
        protected List<Entity_Notice_List> doInBackground(String... params) {
            Log.i("GET", "doInBackground(Params... params) called");
            return getJsonData(jsonUrl);
        }

        protected void onPostExecute(List<Entity_Notice_List> result) {
            listView.setAdapter(adapter);
            for (Entity_Notice_List en : result) {
                dataArrays.add(en);
                Log.e("en",en.getContent());
                adapter.notifyDataSetChanged();
                listView.setSelection(listView.getCount() - 1);
            }
            super.onPostExecute(result);
        }

        public List<Entity_Notice_List> getJsonData(String jsonUrl) {
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
                newNoticeList = new ArrayList<>();
                // 整体是一个jsonObject
                JSONObject jsonObject = new JSONObject(sb.toString());
                if(jsonObject.getString("status").equals("OK")){
                    // 键是jsonArray数组
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        //获取jsonArray中的每个对象
                        JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                        //创建本地的newsBean对象
                        Entity_Notice_List entity = new Entity_Notice_List();
                        //为该对象进行属性值的设置操

                        entity.action = Integer.valueOf(jsonObject2.getString("action"));
                        entity.content = jsonObject2.getString("content");
                        entity.owner = name;
                        entity.handleStatus = Integer.valueOf(jsonObject2.getString("handleStatus"));
                        //添加对象，组建集合
                        Log.e("get",entity.getOwner());
                        Log.e("get",entity.getContent());
                        Log.e("get",String.valueOf(entity.getAction()));
                        Log.e("get",String.valueOf(entity.getHandleStatus()));
                        newNoticeList.add(entity);
                    }
                }
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return newNoticeList;
        }

    }

}
