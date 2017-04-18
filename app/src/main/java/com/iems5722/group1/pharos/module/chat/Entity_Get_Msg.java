package com.iems5722.group1.pharos.module.chat;

/**
 * Created by Sora on 16/2/17.
 */

public class Entity_Get_Msg {
    public String message;
    public String user_name;
    public String timestamp;
    private boolean isComMsg;
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

    public boolean getMsgType() {
        return this.isComMsg;
    }
    public void setMsgType(boolean isComMsg) {
        this.isComMsg = isComMsg;
    }

    public Entity_Get_Msg() {
        super();
        // TODO Auto-generated constructor stub
    }
}
