package com.iems5722.group1.pharos.module.chat;

import android.graphics.Bitmap;

/**
 * Created by Sora on 16/2/17.
 */

public class Entity_Get_Msg {
    public String message;
    public Bitmap image;
    public String user_name;
    public String timestamp;
    private boolean isComMsg;
    public String msgType;
    public Entity_Get_Msg(String message, String name, String timestamp) {
        super();
        this.message = message;
        this.user_name = name;
        this.timestamp = timestamp;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Bitmap getImage(){return image;}
    public void setImage(Bitmap image){this.image = image;}
    public String getUserName() {
        return user_name;
    }
    public void setUserName(String name) {
        this.user_name = name;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public boolean getIsComMsg(){return this.isComMsg;}
    public void setIsComMsg(boolean isComMsg){this.isComMsg = isComMsg;}
    public String getMsgType() {
        return this.msgType;
    }
    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public Entity_Get_Msg() {
        super();
        // TODO Auto-generated constructor stub
    }
}
