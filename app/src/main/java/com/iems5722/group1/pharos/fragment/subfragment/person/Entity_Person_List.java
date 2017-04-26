package com.iems5722.group1.pharos.fragment.subfragment.person;

/**
 * Created by Sora on 15/2/17.
 */

public class Entity_Person_List {
    private int type;
    private String content;
    public Entity_Person_List(int type, String content) {
        super();
        this.type = type;
        this.content = content;
    }
    public void setType(int type) {
        this.type = type;
    }
    public int getType(){
        return type;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public Entity_Person_List() {
        super();
        // TODO Auto-generated constructor stub
    }
}
