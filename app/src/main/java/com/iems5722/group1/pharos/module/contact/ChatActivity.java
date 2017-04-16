package com.iems5722.group1.pharos.module.contact;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;
import com.iems5722.group1.pharos.R;
import com.iems5722.group1.pharos.utils.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Sora on 27/1/17.
 */

public class ChatActivity extends AppCompatActivity {
    EditText edt_input;
    private ListView listView;
    LvAdapter_Msg adapter;
    List<Entity_Get_Msg> dataArrays = new ArrayList();
    String friendName;
    String friendId;
    String roomId;
    TaskGetMsgL getMsgL;
    String postContent = "";
    String userName;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_chat_room);

        SwipeBack.attach(this, Position.LEFT)
                .setSwipeBackView(R.layout.swipeback_default);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userName = Util.getUsername(this);

        friendId = getIntent().getStringExtra("friend_id");
        friendName = getIntent().getStringExtra("friend_name");
        getSupportActionBar().setTitle(friendName);

        edt_input = (EditText)findViewById(R.id.edit_message);

        listView = (ListView)findViewById(R.id.lv);
        adapter = new LvAdapter_Msg(this,dataArrays);
        listView.setAdapter(adapter);

        TaskGetChatroomId taskGetChatroomId = new TaskGetChatroomId(friendId);
        taskGetChatroomId.execute();


        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(!dataArrays.isEmpty() && firstVisibleItem == 0 && listIsAtTop()){
                    Log.e("test_scroll","touch top");
                    String pageCur = getMsgL.getCurPage();
                    String pageNum = getMsgL.getPageNum();
                    if(pageCur.equals(pageNum)){
                        Toast.makeText(getBaseContext(),"loaded all",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        // add page num
                        int nextPage = Integer.parseInt(pageCur);
                        nextPage++;
                        pageCur = Integer.toString(nextPage);
                        // get next page
                        getMsgL = new TaskGetMsgL(friendId,pageCur);
                        getMsgL.execute();

                        Toast.makeText(ChatActivity.this,"load page " + pageCur,Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private boolean listIsAtTop()   {
        if(listView.getChildCount() == 0) {
            return true;
        }
        return listView.getChildAt(0).getTop() == 0;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                   onBackPressed();
                break;
            case R.id.action_update:
                Log.e("refresh","removeall");
                listView.removeViewInLayout(listView);
                Log.e("refresh","notify");
                dataArrays.clear();
              //  adapter = new LvAdapter_Msg(this,dataArrays);
                adapter.notifyDataSetChanged();
              //  listView.setAdapter(adapter);
                Log.e("refresh","get");
                getMsgL = new TaskGetMsgL(friendId,"1");
                getMsgL.execute();
                Log.e("update","refresh");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_chatroom, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void sendMessage(View view) {
        Log.e("click","success");
        if("".equals(edt_input.getText().toString().trim())){
            AlertDialog.Builder error= new AlertDialog.Builder(ChatActivity.this);
            error.setMessage("内容不能为空");
            error.show();
        }
        else{
            send();
        }
    }

    private void send() {
        String content = edt_input.getText().toString().trim();
        if(content.length() > 0) {
            Entity_Get_Msg entity = new Entity_Get_Msg();
            entity.setMessage(content);
            entity.setUserName(userName);
            entity.setTimestamp(getDate());
            entity.setMsgType(false);
            dataArrays.add(entity);
            adapter.notifyDataSetChanged();
            edt_input.setText("");
            listView.setSelection(listView.getCount()-1);
            postContent = "chatroom_id=" + roomId + "&sender_name=" + userName + "&receiver_name=" + friendName + "&message=" + content;
            TaskPostMsg postMsg = new TaskPostMsg(postContent);
            postMsg.execute();
        }

    }

    private String getDate() {
        int mYear;
        int mMonth;
        int mDay;
        int mHour;
        int mMinute;
        int mSecond;
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);//获取当前的年份
        mMonth = c.get(Calendar.MONTH);//获取当前的月份
        mDay = c.get(Calendar.DAY_OF_MONTH);//获取当前的日期
        mHour = c.get(Calendar.HOUR_OF_DAY);//获取当前的小时数
        mMinute = c.get(Calendar.MINUTE);//获取当前的分钟数
        mSecond = c.get(Calendar.SECOND);

        String y=Integer.toString(mYear);
        String mon=Integer.toString(mMonth);
        String d=Integer.toString(mDay);
        String h=Integer.toString(mHour);
        String m=Integer.toString(mMinute);
        String s=Integer.toString(mSecond);
        Log.e("month",mon);
        if(mMonth<10){
            mon="0"+mon;
        }
        if(mDay<10){
            d="0"+d;
        }
        if(mHour<10){
            h="0"+h;
        }
        if(mMinute<10){
            m="0"+m;
        }
        if(mSecond<10){
            s="0"+s;
        }
        return y+"-"+mon+"-"+d+" "+h+":"+m+":"+s;
    }

    class TaskGetChatroomId extends AsyncTask<String, Integer, String>{

        // private String jsonUrl = "http://iems5722.albertauyeung.com/api/asgn2/get_messages";
        private String jsonUrl = "http://54.202.138.123:5000/pharos/api/getChatRoomId";

        public TaskGetChatroomId(String friendId) {
            this.jsonUrl = this.jsonUrl + "?friend_id=" + friendId + "&owner_name=" + Util.getUsername(ChatActivity.this);
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
            Log.i("GET","onPostExecute "+result);
            if(!result.equals("")){
                roomId = result;
                getMsgL = new TaskGetMsgL(roomId,"1");
                getMsgL.execute();
            }
        }

        public String getJsonData(String jsonUrl) {
            String chatroomId="";
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
                if(jsonObject.getString("status").equals("OK")) {
                    // 键是jsonArray数组
                   chatroomId=jsonObject.getString("data");
                }
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return chatroomId;
        }
    }

    class TaskGetMsgL extends AsyncTask<String, Integer, List<Entity_Get_Msg>> {
        private List<Entity_Get_Msg> newMsgList;
        private String jsonUrl = "http://54.202.138.123:5000/pharos/api/getMessages";
        String pageNum="1";
        String curPage="1";

        public TaskGetMsgL(String id, String page) {
            this.jsonUrl = this.jsonUrl + "?chatroom_id=" + id + "&page=" + page;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("GET", "onPreExecute() called");
        }

        @Override
        protected List<Entity_Get_Msg> doInBackground(String... params) {
            Log.i("GET", "doInBackground(Params... params) called");
            return getJsonData(jsonUrl);
        }

        protected void onPostExecute(List<Entity_Get_Msg> result) {
            super.onPostExecute(result);
            for (Entity_Get_Msg en : result) {
                if (en.getUserName().equals(userName)) {
                    en.setMsgType(false);
                } else {
                    en.setMsgType(true);
                }
                dataArrays.add(0, en);
                //   dataArrays.add(en);
                adapter.notifyDataSetChanged();
                listView.setSelection(newMsgList.size());
            }
        }

        public String getCurPage(){
            return curPage;
        }

        public String getPageNum(){
            return  pageNum;
        }

        public List<Entity_Get_Msg> getJsonData(String jsonUrl) {
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
                newMsgList = new ArrayList<Entity_Get_Msg>();
                // 整体是一个jsonObject
                JSONObject jsonObject = new JSONObject(sb.toString());
                if(jsonObject.getString("status").equals("OK")) {
                    // 键是jsonArray数组
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        //获取jsonArray中的每个对象
                        JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                        //创建本地的newsBean对象
                        Entity_Get_Msg entity = new Entity_Get_Msg();
                        //为该对象进行属性值的设置操
                        entity.message = jsonObject2.getString("message");
                        entity.user_name = jsonObject2.getString("user_name");
                        entity.timestamp = jsonObject2.getString("timestamp");
                        //添加对象，组建集合
                        newMsgList.add(entity);

                    }
                    pageNum = jsonObject.getString("total_pages");
                    curPage = jsonObject.getString("page");
                }
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return newMsgList;
        }
    }


}
