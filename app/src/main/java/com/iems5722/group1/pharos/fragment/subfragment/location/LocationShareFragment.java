package com.iems5722.group1.pharos.fragment.subfragment.location;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.iems5722.group1.pharos.module.chat.TaskPostMsg;
import com.iems5722.group1.pharos.utils.Util;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Sora on 26/4/17.
 */

public class LocationShareFragment extends DialogFragment {

    CharSequence[] friendList;
    String placeName;
    String userName;
    int selectedIndex;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            friendList = getArguments().getCharSequenceArray("friendList");
            placeName = getArguments().getString("placeName");
            userName = getArguments().getString("userName");
        }
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("select a friend")
                .setSingleChoiceItems(friendList,-1,new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedIndex = which;
                    }
                }).
                setNegativeButton("cancel", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                } ).
                setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String friendName = friendList[selectedIndex].toString();
                        new TaskGetChatroomId(friendName).execute();

                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    class TaskGetChatroomId extends AsyncTask<String, Integer, String> {

        // private String jsonUrl = "http://iems5722.albertauyeung.com/api/asgn2/get_messages";
        private String jsonUrl = "http://54.202.138.123:8000/pharos/api/getChatRoomId";
        String friendName;
        public TaskGetChatroomId(String friendName) {
            this.friendName = friendName;
            this.jsonUrl = this.jsonUrl + "?friend_name=" + friendName + "&owner_name=" + userName;
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
                String postContent = "chatroom_id=" + result + "&sender_name=" +
                        userName + "&receiver_name=" +
                        friendName + "&message=~share place~\n" + placeName;
                TaskPostMsg postMsg = new TaskPostMsg(postContent);
                postMsg.execute();
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
}
