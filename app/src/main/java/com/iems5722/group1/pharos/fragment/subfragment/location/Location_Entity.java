package com.iems5722.group1.pharos.fragment.subfragment.location;

/**
 * Created by nicai on 17/04/2017.
 */

public class Location_Entity {
    private String city;
    private String country;
    private String address;
    public Location_Entity(String city, String country, String address ) {
        super();
        this.city = city;
        this.country = country;
        this.address = address;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getAddress() {return address;}
    public void setAddress(String address) {this.address = address;}
    public Location_Entity() {
        super();
    }

}
