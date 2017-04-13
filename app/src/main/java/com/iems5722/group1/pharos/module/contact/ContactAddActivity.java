package com.iems5722.group1.pharos.module.contact;

import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iems5722.group1.pharos.R;
import com.iems5722.group1.pharos.utils.Util;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by Sora on 8/4/17.
 */

public class ContactAddActivity extends AppCompatActivity {
    EditText edSearchContent;
    Button btnSearch;
    TextView tvResult;
    Button btnAdd;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_contact_add_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        edSearchContent = (EditText) findViewById(R.id.search_content);
        btnSearch = (Button)findViewById(R.id.btn_add_search);
        tvResult = (TextView)findViewById(R.id.tv_search_result);
        btnAdd = (Button)findViewById(R.id.btn_add);
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

    public void searchUser(View view){
        Log.e("search","click");
        if(edSearchContent.getText().toString().equals("")){
            tvResult.setVisibility(View.INVISIBLE);
            btnAdd.setVisibility(View.INVISIBLE);
            Toast.makeText(this,"please input the username you want to search",Toast.LENGTH_SHORT).show();
        }
        else{
            String searchName = edSearchContent.getText().toString();
            Log.e("search",searchName);
            TaskSearch taskSearch = new TaskSearch(searchName,Util.getUsername(this));
            taskSearch.execute();
        }
    }

    class TaskSearch extends AsyncTask<String, Integer, String> {
        // private String jsonUrl = "http://iems5722.albertauyeung.com/api/asgn2/get_messages";
        private String jsonUrl = "http://54.202.138.123:5000/pharos/api/searchUser";
        String result = "";
        String name="";
        public TaskSearch(String searchName,String ownerName) {
            this.name = searchName;
            this.jsonUrl = this.jsonUrl + "?search_name="+searchName+"&owner_name="+ownerName;
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

        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.equals("SUCCESS")){
                tvResult.setVisibility(View.VISIBLE);
                tvResult.setText(name);
                btnAdd.setVisibility(View.VISIBLE);
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TaskAdd taskAdd = new TaskAdd(edSearchContent.getText().toString());
                        taskAdd.execute();
                    }
                });
            }
            else if(result.equals("EXIST")){
                tvResult.setVisibility(View.VISIBLE);
                tvResult.setText(name);
                btnAdd.setText("chat");
                btnAdd.setVisibility(View.VISIBLE);
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
            else if(result.equals("FAIL")){
                tvResult.setVisibility(View.INVISIBLE);
                btnAdd.setVisibility(View.INVISIBLE);
                Toast.makeText(ContactAddActivity.this,"no this user",Toast.LENGTH_SHORT).show();
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
                result = jsonObject.getString("status");
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    class TaskAdd extends AsyncTask<String, Integer, String> {
        // private String jsonUrl = "http://iems5722.albertauyeung.com/api/asgn2/get_messages";
        private String jsonUrl = "http://54.202.138.123:5000/pharos/api/searchAddUser";
        String result = "";
        String name="";
        public TaskAdd(String name) {
            this.name = name;
            this.jsonUrl = this.jsonUrl + "?action=add&from=" + Util.getUsername(ContactAddActivity.this) + "&to=" + name;
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
            if (result.equals("SUCCESS")){
                Toast.makeText(ContactAddActivity.this,"you have sent the request successfully",Toast.LENGTH_SHORT).show();

            }
            else if(result.equals("FAIL")){

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
                result = jsonObject.getString("status");
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }
}
