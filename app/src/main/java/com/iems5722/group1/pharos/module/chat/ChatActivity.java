package com.iems5722.group1.pharos.module.chat;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;
import com.iems5722.group1.pharos.R;
import com.iems5722.group1.pharos.module.chatrooms.ImageTools;
import com.iems5722.group1.pharos.utils.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Sora on 27/1/17.
 **/

public class ChatActivity extends AppCompatActivity {
    Button btnVideo;
    EditText edt_input;
    ListView listView;
    LvAdapter_Msg adapter;
    List<Entity_Get_Msg> dataArrays = new ArrayList();

    String friendName;
    String friendId;
    String roomId;
    TaskGetMsgL getMsgL;
    String postContent = "";
    String userName;
    static String comMsg;
    static String comName;
    static int newCome = 0;

    private static final int TAKE_PICTURE = 0;
    private static final int CHOOSE_PICTURE = 1;
    private static final int SCALE = 5;//照片缩小比例

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

       // btnVideo = (Button)findViewById(R.id.btnVideo);
        edt_input = (EditText)findViewById(R.id.edit_message);

        listView = (ListView)findViewById(R.id.lv);
        adapter = new LvAdapter_Msg(this,dataArrays);
        listView.setAdapter(adapter);

        TaskGetChatroomId taskGetChatroomId = new TaskGetChatroomId(friendId);
        taskGetChatroomId.execute();

//        btnVideo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(ChatActivity.this, VideoChatActivity.class);
//                startActivity(intent);
//            }
//        });

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

        final Handler mHandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                update(comMsg,comName);
            }
        };

        Runnable mRunnable = new Runnable() {
            public void run() {
                while(true) {
                    try {
                        if(newCome==1){
                            Log.e("chat test ","go");
                            mHandler.sendMessage(mHandler.obtainMessage());
                            Thread.sleep(1000);
                            newCome=0;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        new Thread(mRunnable).start();
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
            sendMsg();
        }
    }

    public void sendImage(View view){
        showPicturePicker(ChatActivity.this);
    }

    public void showPicturePicker(Context context){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle("图片来源");
        builder.setNegativeButton("取消", null);
        builder.setItems(new String[]{"拍照","相册"}, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case TAKE_PICTURE:
                        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"image.jpg"));
                        //指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                        break;

                    case CHOOSE_PICTURE:
                        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        openAlbumIntent.setType("image/*");
                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                        break;

                    default:
                        break;
                }
            }
        });
        builder.create().show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PICTURE:
                    //将保存在本地的图片取出并缩小后显示在界面上
                    Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/image.jpg");
                    Bitmap newBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth() / SCALE, bitmap.getHeight() / SCALE);
                    //由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
                    bitmap.recycle();

                    //将处理过的图片显示在界面上，并保存到本地
//                    iv_image.setImageBitmap(newBitmap);
                    sendImg(newBitmap);
                    ImageTools.savePhotoToSDCard(newBitmap, Environment.getExternalStorageDirectory().getAbsolutePath(), String.valueOf(System.currentTimeMillis()));
                    break;

                case CHOOSE_PICTURE:
                    ContentResolver resolver = getContentResolver();
                    //照片的原始资源地址
                    Uri originalUri = data.getData();
                    try {
                        //使用ContentProvider通过URI获取原始图片
                        Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                        if (photo != null) {
                            //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                            Bitmap smallBitmap = ImageTools.zoomBitmap(photo, photo.getWidth() / SCALE, photo.getHeight() / SCALE);
                            //释放原始图片占用的内存，防止out of memory异常发生
                            photo.recycle();
                            sendImg(smallBitmap);
                           // iv_image.setImageBitmap(smallBitmap);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                default:
                    break;
            }
        }
    }

    private void sendImg(Bitmap bitmap){
        Entity_Get_Msg entity = new Entity_Get_Msg();
        entity.setImage(bitmap);
        entity.setUserName(userName);
        entity.setTimestamp(getDate());
        entity.setIsComMsg(false);
        entity.setMsgType("img");
        dataArrays.add(entity);
        adapter.notifyDataSetChanged();
        listView.setSelection(listView.getCount()-1);
        String param="";
        param = "?chatroom_id="+roomId+"&sender_name="+userName+"&receiver_name="+friendName;
        TaskPostImg taskPostImg = new TaskPostImg(param,bitmap);
        taskPostImg.execute();
    }

    private void sendMsg() {
        String content = edt_input.getText().toString().trim();
        if(content.length() > 0) {
            Entity_Get_Msg entity = new Entity_Get_Msg();
            entity.setMessage(content);
            entity.setUserName(userName);
            entity.setTimestamp(getDate());
            entity.setIsComMsg(true);
            entity.setMsgType("text");
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
        private String jsonUrl = "http://54.202.138.123:8000/pharos/api/getChatRoomId";

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

    public static void setMsgNum(int msgNum,String msg,String senderName){
        Log.e("chat ","test");
        newCome=msgNum;
        comMsg = msg;
        comName = senderName;
    }

    public void update(String msg,String sender_name){
        Entity_Get_Msg entity = new Entity_Get_Msg();
        entity.setMessage(msg);
        entity.setUserName(sender_name);
        entity.setTimestamp(getDate());
        entity.setIsComMsg(true);
        dataArrays.add(entity);
        adapter.notifyDataSetChanged();
        edt_input.setText("");
        listView.setSelection(listView.getCount()-1);
    }

    public static boolean isAppRunningForeground(Context context){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Service.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = activityManager.getRunningAppProcesses();
        if (runningAppProcessInfoList==null){
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcessInfoList) {
            if (processInfo.processName.equals(context.getPackageName())
                    && processInfo.importance==ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                return true;
            }
        }
        return false;
    }


    class TaskGetMsgL extends AsyncTask<String, Integer, List<Entity_Get_Msg>> {
        private List<Entity_Get_Msg> newMsgList;
        private String jsonUrl = "http://54.202.138.123:8000/pharos/api/getMessages";
        String pageNum = "1";
        String curPage = "1";

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
                    en.setIsComMsg(false);
                } else {
                    en.setIsComMsg(true);
                }
                dataArrays.add(0, en);
                //   dataArrays.add(en);
                adapter.notifyDataSetChanged();
                listView.setSelection(newMsgList.size());
            }
        }

        public String getCurPage() {
            return curPage;
        }

        public String getPageNum() {
            return pageNum;
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
                if (jsonObject.getString("status").equals("OK")) {
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
                        entity.msgType = "text";
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
