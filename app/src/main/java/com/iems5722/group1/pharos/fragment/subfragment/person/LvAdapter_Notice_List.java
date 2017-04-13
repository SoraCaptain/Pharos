package com.iems5722.group1.pharos.fragment.subfragment.person;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iems5722.group1.pharos.R;
import com.iems5722.group1.pharos.utils.Util;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Sora on 10/4/17.
 */

public class LvAdapter_Notice_List extends BaseAdapter {
    private List<Entity_Notice_List> content;
    private LayoutInflater mInflater;

    public LvAdapter_Notice_List(Context context, List<Entity_Notice_List> content) {
        this.content = content;
        this.mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return this.content.size();
    }

    public Object getItem(int position) {
        return this.content.get(position);
    }

    public long getItemId(int position) {
        return (long)position;
    }

    public int getItemViewType(int position) {

        return 1;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final Entity_Notice_List entity = this.content.get(position);
        final ViewHolder viewHolder = new ViewHolder();
//        if(convertView == null) {
            if (entity.getAction() == 1) {
                convertView = this.mInflater.inflate(R.layout.fragment_me_notice_content, null);
                TextView tvNotice = (TextView)convertView.findViewById(R.id.tv_content);
                Button btnRefuse = (Button)convertView.findViewById(R.id.btn_refuse);
                Button btnAccept = (Button)convertView.findViewById(R.id.btn_accept);
                TextView tvStatus = (TextView)convertView.findViewById(R.id.tv_status);
                viewHolder.tvNotice = tvNotice;
                viewHolder.btnRefuse = btnRefuse;
                viewHolder.btnAccept = btnAccept;
                viewHolder.tvStatus = tvStatus;
                convertView.setTag(viewHolder);
            }
//        }
//        else {
//            viewHolder = (ViewHolder)convertView.getTag();
//        }

        viewHolder.tvNotice.setText(entity.getContent().toString()+" wants to be your friend");

        if (entity.getHandleStatus()==0){
            viewHolder.btnAccept.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    TaskProcessNotice taskProcessNotice = new TaskProcessNotice(entity.getContent().toString(), entity.getOwner(),"accept");
                    taskProcessNotice.execute();
                    viewHolder.btnAccept.setVisibility(View.INVISIBLE);
                    viewHolder.btnRefuse.setVisibility(View.INVISIBLE);
                    viewHolder.tvStatus.setVisibility(View.VISIBLE);
                }
            });

            viewHolder.btnRefuse.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Log.e("click","refuse");
                    TaskProcessNotice taskProcessNotice = new TaskProcessNotice(entity.getContent().toString(), entity.getOwner(),"refuse");
                    taskProcessNotice.execute();
                    viewHolder.btnAccept.setVisibility(View.INVISIBLE);
                    viewHolder.btnRefuse.setVisibility(View.INVISIBLE);
                    viewHolder.tvStatus.setVisibility(View.VISIBLE);
                }
            });
        }
        else{
            viewHolder.btnAccept.setVisibility(View.INVISIBLE);
            viewHolder.btnRefuse.setVisibility(View.INVISIBLE);
            viewHolder.tvStatus.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    private class ViewHolder {
        public TextView tvNotice;
        public Button btnRefuse;
        public Button btnAccept;
        public TextView tvStatus;
        ViewHolder() {}
    }

    class TaskProcessNotice extends AsyncTask<String, Integer, String> {
        // private String jsonUrl = "http://iems5722.albertauyeung.com/api/asgn2/get_messages";
        private String jsonUrl = "http://54.202.138.123:5000/pharos/api/processNotice";
        String result = "";
        public TaskProcessNotice(String from_name, String to_name, String action) {
            this.jsonUrl = this.jsonUrl + "?from_name=" + from_name + "&to_name=" + to_name + "&action=" + action;
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
            if (result.equals("OK")){

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
