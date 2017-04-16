package com.iems5722.group1.pharos.fragment.subfragment.person;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.iems5722.group1.pharos.Constants;
import com.iems5722.group1.pharos.R;
import com.iems5722.group1.pharos.module.contact.ChatActivity;
import com.iems5722.group1.pharos.module.contact.ContactAddActivity;
import com.iems5722.group1.pharos.utils.Util;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;




public class PersonFragment extends Fragment{
    private View view;
    private TextView textView;
    private Button btnExit;
    private ListView listView;
    private LvAdapter_List adapter;
    List<Entity_Person_List> dataArrays;
    public static PersonFragment newInstance(String s){
        PersonFragment homeFragment = new PersonFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ARGS,s);
        homeFragment.setArguments(bundle);
        return homeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_item_me, container, false);
        Util.checkToken(getActivity());
        Bundle bundle = getArguments();
        final String s = Util.getUsername(getActivity());
        textView = (TextView) view.findViewById(R.id.lr);
        btnExit = (Button)view.findViewById(R.id.btnExit);
        listView = (ListView)view.findViewById(R.id.lv);
        dataArrays = new ArrayList();
        adapter = new LvAdapter_List(getActivity(),dataArrays);
        if(s.equals("null")){
            btnExit.setVisibility(View.INVISIBLE);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), personLogin.class);
                    startActivity(intent);
                }
            });

            listView.removeViewInLayout(listView);
            dataArrays.clear();
            adapter.notifyDataSetChanged();
        }
        else{
            textView.setText(s);
            btnExit.setVisibility(View.VISIBLE);
            listView.setVisibility(View.VISIBLE);
            TaskGetNotice taskGetNotice = new TaskGetNotice(s);
            taskGetNotice.execute();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), PersonNoticeList.class);
                    startActivity(intent);
                }
            });
        }
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("click",Util.getUsername(getActivity()));
                TaskDeleteToken taskDeleteToken = new TaskDeleteToken(Util.getUsername(getActivity()));
                taskDeleteToken.execute();
                btnExit.setVisibility(View.INVISIBLE);
                listView.setVisibility(View.INVISIBLE);
                Util.setUserName("null",getActivity());
                Log.e("string",Util.getUsername(getActivity()));
                textView.setText("login/register");
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), personLogin.class);
                        startActivity(intent);
                    }
                });
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), PersonNoticeList.class);
                startActivity(intent);
            }
        });
        return view;
    }

    class TaskGetNotice extends AsyncTask<String, Integer, String> {
        // private String jsonUrl = "http://iems5722.albertauyeung.com/api/asgn2/get_messages";
        private String jsonUrl = "http://54.202.138.123:5000/pharos/api/getNoticeNum";
        String result = "";
        public TaskGetNotice(String name) {
            this.jsonUrl = this.jsonUrl + "?receiver_name="+name;
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
            if(result.equals("ERROR")){

            }
            Entity_Person_List entity = new Entity_Person_List(1,result);
            dataArrays.add(0,entity);
            adapter = new LvAdapter_List(getActivity(),dataArrays);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
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

    class TaskDeleteToken extends AsyncTask<String, Integer, String> {

        // private String jsonUrl = "http://iems5722.albertauyeung.com/api/asgn2/get_messages";
        private String jsonUrl = "http://54.202.138.123:5000/pharos/api/deleteToken";

        public TaskDeleteToken(String username) {
            Log.e("username ",username);
            this.jsonUrl = this.jsonUrl + "?user_name=" + username;
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
            Log.i("GET", "onPostExecute " + result);
        }

        public String getJsonData(String jsonUrl) {
            String result = "";
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

                // 整体是一个jsonObject
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
