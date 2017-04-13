package com.iems5722.group1.pharos.module.contact;

/**
 * Created by Sora on 15/2/17.
 */

public class Entity_Contact {
    public String id;
    public String name;
    public Entity_Contact(String id, String name) {
        super();
        this.id = id;
        this.name = name;
    }
    public String getID() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Entity_Contact() {
        super();
        // TODO Auto-generated constructor stub
    }
}
