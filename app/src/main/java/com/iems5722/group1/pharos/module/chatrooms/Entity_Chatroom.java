package com.iems5722.group1.pharos.module.chatrooms;

/**
 * Created by Sora on 15/2/17.
 */

public class Entity_Chatroom {
    public String name;
    public String id;
    public String msg;
    public Entity_Chatroom(String name, String id,String msg) {
        super();
        this.name = name;
        this.id = id;
        this.msg = msg;
    }
    public String getName() {
        return name;
    }
    public void setName(String id) {
        this.name = name;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String name) {
        this.msg = msg;
    }
    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
    }
    public Entity_Chatroom() {
        super();
        // TODO Auto-generated constructor stub
    }
}
