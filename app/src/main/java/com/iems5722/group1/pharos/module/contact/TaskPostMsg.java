package com.iems5722.group1.pharos.module.contact;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Sora on 17/2/17.
 */

public class TaskPostMsg extends AsyncTask<String, Integer, String> {
    private String content;

    public TaskPostMsg(String content){
        this.content = content;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.i("POST", "onPreExecute() called");
    }

    @Override
    protected String doInBackground(String... params) {
   //     String jsonUrl = "http://iems5722.albertauyeung.com/api/asgn2/send_message";
        String jsonUrl = "http://54.202.138.123:5000/pharos/api/sendMessage";
        Log.i("POST", "doInBackground(Params... params) called");
        postJsonData(jsonUrl);
        return null;
    }

    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.i("POST", "onPostExecute(String result) called");
      //  Log.e("output",result);
    }

    public String postJsonData(String jsonUrl) {
        String output = "";
        String result = "";
        try {
            //创建url http地址
            URL httpUrl = new URL(jsonUrl);
            //打开http 链接
            HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
            //设置参数  请求为get请求
            connection.setReadTimeout(5000);
            // 设置是否向connection输出，因为这个是post请求，参数要放在
            // http正文内，因此需要设为true
            connection.setDoOutput(true);
            // Read from the connection. Default is true.
            connection.setDoInput(true);
            // 默认是 GET方式
            connection.setRequestMethod("POST");
            // Post 请求不能使用缓存
            connection.setUseCaches(false);
            //设置本次连接是否自动重定向
            connection.setInstanceFollowRedirects(true);
            // 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
            // 意思是正文是urlencoded编码过的form参数
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            // 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，
            // 要注意的是connection.getOutputStream会隐含的进行connect。
            connection.connect();

            Log.e("post","connect success");
            DataOutputStream out = new DataOutputStream(connection
                    .getOutputStream());
//            // 正文，正文内容其实跟get的URL中 '? '后的参数字符串一致
//            String content = "字段名=" + URLEncoder.encode("字符串值", "编码");
            // DataOutputStream.writeBytes将字符串中的16位的unicode字符以8位的字符形式写到流里面
            out.writeBytes(content);
            //流用完记得关
            out.flush();
            out.close();
            Log.e("post","out success");
            //获取响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null){
                output = output + line;
            }
            reader.close();
            //该干的都干完了,记得把连接断了
            connection.disconnect();
            JSONObject jsonObject = new JSONObject(output.toString());
            result = jsonObject.getString("status");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("POST out",result);
        return result;
    }

}