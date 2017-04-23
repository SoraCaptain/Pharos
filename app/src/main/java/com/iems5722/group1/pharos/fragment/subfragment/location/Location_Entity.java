package com.iems5722.group1.pharos.fragment.subfragment.location;

import android.text.style.BulletSpan;

/**
 * Created by nicai on 17/04/2017.
 */

public class Location_Entity {
    private String city="";
    private String country="";
    private String address="";
    private String name="";
    private String phoneNum="";
    private String busHour="";
    private String placeId="";
    private boolean open_now;

    public Location_Entity(String city, String country, String address, String name, String phoneNum, String busHour, String placeId, boolean open_now) {
        super();
        this.city = city;
        this.country = country;
        this.address = address;
        this.name = name;
        this.phoneNum = phoneNum;
        this.busHour = busHour;
        this.placeId = placeId;
        this.open_now = open_now;
    }
    public String getplaceId() {
        return placeId;
    }
    public void setPlaceId(String city) { this.placeId = placeId;
    }


    public String getCity() {
        return city;
    }
    public void setCity(String city) { this.city = city;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddress() {return address;}
    public void setAddress(String address) {this.address = address;}

    public String getName() {
        return name;
    }
    public void setName(String name) {this.name = name;}

    public String getPhoneNum() { return phoneNum; }
    public void setPhoneNum(String phoneNum) { this.phoneNum = phoneNum;}

    public String getBusHour(){ return busHour; }
    public void setBusHour(String busHour) {this.busHour = busHour; }

    public boolean getOpennow(){ return open_now; }
    public void setOpennow(boolean open_now) {this.open_now = open_now; }

    public Location_Entity() {
        super();
    }

}
