package com.iems5722.group1.pharos.module.chat;

/**
 * Created by Sora on 19/4/17.
 */

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.iems5722.group1.pharos.R;
import com.iems5722.group1.pharos.module.chat.config.ConfigEntity;
import com.iems5722.group1.pharos.module.chat.config.ConfigService;
import com.iems5722.group1.pharos.module.chat.video_sdk.AnyChatBaseEvent;
import com.iems5722.group1.pharos.module.chat.video_sdk.AnyChatCoreSDK;
import com.iems5722.group1.pharos.module.chat.video_sdk.AnyChatDefine;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Android视频聊天
 * 1、初始化SDK 2、连接服务器、 3、用户登录；4、进入房间；5、打开本地视频；6、请求对方视频
 */
public class VideoChatActivity extends Activity implements AnyChatBaseEvent
{
    private AnyChatCoreSDK anychat;         // 核心SDK
    private SurfaceView remoteSurfaceView;  // 对方视频
    private SurfaceView localSurfaceView;   // 本地视频
    private ConfigEntity configEntity;
    private boolean bSelfVideoOpened = false;   // 本地视频是否已打开
    private boolean bOtherVideoOpened = false;  // 对方视频是否已打开
    private TimerTask mTimerTask;               // 定时器
    private Timer mTimer = new Timer(true);
    private Handler handler;                    // 用Handler来不间断刷新即时视频
    private List<String> userlist = new ArrayList<String>();//保存在线用户列表
    private int userid;                         // 用户ID
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_chat_video);
        remoteSurfaceView = (SurfaceView) findViewById(R.id.surface_remote);
        localSurfaceView = (SurfaceView) findViewById(R.id.surface_local);
        configEntity = ConfigService.LoadConfig(this);//加载视频通话设置
        loginSystem();// 初始化SDK 连接服务器
        mTimerTask = new TimerTask(){
            public void run(){
                Message mesasge = new Message();
                handler.sendMessage(mesasge);
            }
        };
        mTimer.schedule(mTimerTask, 1000, 100);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                VideoChat();// 不间断显示即时视频通话画面
                super.handleMessage(msg);
            }
        };
    }
    // 初始化SDK 连接服务器
    private void loginSystem(){
        if (anychat == null){
            anychat = AnyChatCoreSDK.getInstance(VideoChatActivity.this);
            anychat.SetBaseEvent(this);         // 设置基本事件回调函数
            if (configEntity.useARMv6Lib != 0)  // 使用ARMv6指令集
                anychat.SetSDKOptionInt(AnyChatDefine.
                        BRAC_SO_CORESDK_USEARMV6LIB, 1);
            anychat.InitSDK(android.os.Build.VERSION.SDK_INT, 0); // 初始化SDK
        }
        anychat.Connect("demo.anychat.cn", 8906);// 连接服务器
    }
    // 显示即时视频通话画面
    public void VideoChat(){
        if (!bOtherVideoOpened){
            if (anychat.GetCameraState(userid) == 2
                    && anychat.GetUserVideoWidth(userid) != 0){
                SurfaceHolder holder = remoteSurfaceView.getHolder();
                holder.setFormat(PixelFormat.RGB_565);
                holder.setFixedSize(anychat.GetUserVideoWidth(userid),
                        anychat.GetUserVideoHeight(userid));
                Surface s = holder.getSurface();            // 获得视频画面
                anychat.SetVideoPos(userid, s, 0, 0, 0, 0); // 调用API显示视频画面
                bOtherVideoOpened = true;
            }
        }
        if (!bSelfVideoOpened){
            if (anychat.GetCameraState(-1) == 2
                    && anychat.GetUserVideoWidth(-1) != 0){
                SurfaceHolder holder = localSurfaceView.getHolder();
                holder.setFormat(PixelFormat.RGB_565);
                holder.setFixedSize(anychat.GetUserVideoWidth(-1),
                        anychat.GetUserVideoHeight(-1));
                Surface s = holder.getSurface();
                anychat.SetVideoPos(-1, s, 0, 0, 0, 0);
                bSelfVideoOpened = true;
            }
        }
    }
    public void OnAnyChatConnectMessage(boolean bSuccess){
        if (!bSuccess){
            Toast.makeText(VideoChatActivity.this, "连接服务器失败，自动重连，请稍后...", Toast.LENGTH_SHORT).show();
        }
        anychat.Login("android", "");               // 服务器连接成功 用户登录
    }
    public void OnAnyChatLoginMessage(int dwUserId, int dwErrorCode){
        if (dwErrorCode == 0) {
            Toast.makeText(this, "登录成功！", Toast.LENGTH_SHORT).show();
            anychat.EnterRoom(1, "");               // 用户登录成功 进入房间
            ApplyVideoConfig();
        } else {
            Toast.makeText(this, "登录失败，错误代码：" + dwErrorCode, Toast.LENGTH_SHORT).show();
        }
    }
    public void OnAnyChatEnterRoomMessage(int dwRoomId, int dwErrorCode){
        if (dwErrorCode == 0) {                     // 进入房间成功  打开本地音视频
            Toast.makeText(this, "进入房间成功", Toast.LENGTH_SHORT).show();
            anychat.UserCameraControl(-1, 1);       // 打开本地视频
            anychat.UserSpeakControl(-1, 1);        // 打开本地音频
        } else {
            Toast.makeText(this, "进入房间失败，错误代码：" + dwErrorCode, Toast.LENGTH_SHORT).show();
        }
    }
    public void OnAnyChatOnlineUserMessage(int dwUserNum, int dwRoomId){
        if (dwRoomId == 1){
            int user[] = anychat.GetOnlineUser();
            if (user.length != 0){
                for (int i = 0; i < user.length; i++){
                    userlist.add(user[i]+"");
                }
                String temp =userlist.get(0);
                userid = Integer.parseInt(temp);
                anychat.UserCameraControl(userid, 1);// 请求用户视频
                anychat.UserSpeakControl(userid, 1); // 请求用户音频
            }
            else {
                Toast.makeText(VideoChatActivity.this, "当前没有在线用户", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void OnAnyChatUserAtRoomMessage(int dwUserId, boolean bEnter){
        if (bEnter) {//新用户进入房间
            userlist.add(dwUserId+"");
        }
        else {       //用户离开房间
            if (dwUserId == userid)
            {
                Toast.makeText(VideoChatActivity.this, "视频用户已下线", Toast.LENGTH_SHORT).show();
                anychat.UserCameraControl(userid, 0);// 关闭用户视频
                anychat.UserSpeakControl(userid, 0); // 关闭用户音频
                userlist.remove(userid+"");          //移除该用户
                if (userlist.size() != 0)
                {
                    String temp =userlist.get(0);
                    userid = Integer.parseInt(temp);
                    anychat.UserCameraControl(userid, 1);// 请求其他用户视频
                    anychat.UserSpeakControl(userid, 1); // 请求其他用户音频
                }
            }
            else {
                userlist.remove(dwUserId+"");//移除该用户
            }
        }
    }
    public void OnAnyChatLinkCloseMessage(int dwErrorCode){
        Toast.makeText(VideoChatActivity.this, "连接关闭，error：" + dwErrorCode, Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onDestroy(){ //程序退出
        anychat.LeaveRoom(-1);  //离开房间
        anychat.Logout();       //注销登录
        anychat.Release();      //释放资源
        mTimer.cancel();
        super.onDestroy();
    }
    // 根据配置文件配置视频参数
    private void ApplyVideoConfig(){
        if (configEntity.configMode == 1) // 自定义视频参数配置
        {
            // 设置本地视频编码的码率（如果码率为0，则表示使用质量优先模式）
            anychat.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_BITRATECTRL,configEntity.videoBitrate);
            if (configEntity.videoBitrate == 0)
            {
                // 设置本地视频编码的质量
                anychat.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_QUALITYCTRL,configEntity.videoQuality);
            }
            // 设置本地视频编码的帧率
            anychat.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_FPSCTRL,configEntity.videoFps);
            // 设置本地视频编码的关键帧间隔
            anychat.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_GOPCTRL,configEntity.videoFps * 4);
            // 设置本地视频采集分辨率
            anychat.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_WIDTHCTRL,configEntity.resolution_width);
            anychat.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_HEIGHTCTRL,configEntity.resolution_height);
            // 设置视频编码预设参数（值越大，编码质量越高，占用CPU资源也会越高）
            anychat.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_PRESETCTRL,configEntity.videoPreset);
        }
        // 让视频参数生效
        anychat.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_APPLYPARAM,configEntity.configMode);
        // P2P设置
        anychat.SetSDKOptionInt(AnyChatDefine.BRAC_SO_NETWORK_P2PPOLITIC,configEntity.enableP2P);
        // 本地视频Overlay模式设置
        anychat.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_OVERLAY,configEntity.videoOverlay);
        // 回音消除设置
        anychat.SetSDKOptionInt(AnyChatDefine.BRAC_SO_AUDIO_ECHOCTRL,configEntity.enableAEC);
        // 平台硬件编码设置
        anychat.SetSDKOptionInt(AnyChatDefine.BRAC_SO_CORESDK_USEHWCODEC,configEntity.useHWCodec);
        // 视频旋转模式设置
        anychat.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_ROTATECTRL,configEntity.videorotatemode);
        // 视频平滑播放模式设置
      //  anychat.SetSDKOptionInt(AnyChatDefine.BRAC_SO_STREAM_SMOOTHPLAYMODE,configEntity.smoothPlayMode);
        // 视频采集驱动设置
      //  anychat.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_CAPDRIVER,configEntity.videoCapDriver);
        // 本地视频采集偏色修正设置
        anychat.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_FIXCOLORDEVIA,configEntity.fixcolordeviation);
        // 视频显示驱动设置
      //  anychat.SetSDKOptionInt(AnyChatDefine.BRAC_SO_VIDEOSHOW_DRIVERCTRL,configEntity.videoShowDriver);
    }
}