package com.iems5722.group1.pharos.module.chat;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import static android.util.Base64.CRLF;
import static com.iems5722.group1.pharos.R.id.map;


/**
 * Created by Sora on 17/2/17.
 */

public class TaskPostImg extends AsyncTask<String, Integer, String> {
    private Bitmap bitmap;
    private String parameter;
    public TaskPostImg(String parameter,Bitmap bitmap){
        this.parameter = parameter;
        this.bitmap = bitmap;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.i("POST", "onPreExecute() called");
    }

    @Override
    protected String doInBackground(String... params) {
        //     String jsonUrl = "http://iems5722.albertauyeung.com/api/asgn2/send_message";
        String jsonUrl = "http://54.202.138.123:8000/pharos/api/sendImg";
        //jsonUrl = jsonUrl + parameter;
        Log.i("POST", "doInBackground(Params... params) called");
        postJsonData(jsonUrl,bitmap);
        return null;
    }

    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.i("POST", "onPostExecute(String result) called");
       // Log.e("output",result);
    }

    public String postJsonData(String jsonUrl,Bitmap bitmap){
        HttpURLConnection con = null;
        String result="";
        try {
            // open connection
            URL url = new URL(jsonUrl);
            con = (HttpURLConnection) url.openConnection();

            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "image/jpeg");

            DataOutputStream dous = new DataOutputStream(con.getOutputStream());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            InputStream is = new ByteArrayInputStream(baos.toByteArray());
           //bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

//            byte[] data = baos.toByteArray();
//
//            int bufferSize = 1024*10;
//            byte[] buffer = new byte[bufferSize];
//            int bytesRead = -1;
//            while ((bytesRead = is.read(data)) != -1) {
//                dous.write(buffer, 0, bytesRead);
//            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            byte[] byteRead=out.toByteArray();
            String img = Base64.encodeToString(byteRead, 0, byteRead.length, Base64.DEFAULT);
            dous.writeBytes("img="+img);
            is.close();
            dous.flush();

            //

            String output="";
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null){
                output = output + line;
            }
            reader.close();
            //该干的都干完了,记得把连接断了
            con.disconnect();
            JSONObject jsonObject = new JSONObject(output.toString());
            result = jsonObject.getString("status");
            Log.e("post image",result);
        }catch(Exception e){
            Log.e("error",String.valueOf(e));
        }
        return result;
    }


}
