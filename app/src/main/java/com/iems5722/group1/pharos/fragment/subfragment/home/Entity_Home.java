package com.iems5722.group1.pharos.fragment.subfragment.home;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * Created by Sora on 10/4/17.
 */

public class Entity_Home {
    private String placeId;
    private String placeName;
    private String placeRate;
    private Bitmap placeImg;
    private String placeType;
    private String placePhotoPre;
    private String placeImgWidth;
    private String placeImgHeight;
    public Entity_Home(String placeId, String placeName, String placeRate, Bitmap placeImg,String placeType, String placeImgHeight,String placeImgWidth) {
        super();
        this.placeId = placeId;
        this.placeName = placeName;
        this.placeImg = placeImg;
        this.placeRate = placeRate;
        this.placeType = placeType;
        this.placeImgHeight = placeImgHeight;
        this.placeImgWidth = placeImgWidth;
    }
    public void setPlaceId(String placeId){
        this.placeId = placeId;
    }
    public String getPlaceId(){
        return this.placeId;
    }
    public void setPlaceName(String placeName){
        this.placeName = placeName;
    }
    public String getPlaceName(){
        return this.placeName;
    }
    public void setPlaceRate(String placeRate){
        this.placeRate = placeRate;
    }
    public String getPlaceRate(){
        return this.placeRate;
    }
    public void setPlaceImg(Bitmap placeImg){
        this.placeImg = placeImg;
    }
    public Bitmap getPlaceImg(){
        return this.placeImg;
    }
    public void setPlaceType(String placeType){
        this.placeType = placeType;
    }
    public String getPlaceType(){
        return this.placeType;
    }
    public void setPlacePhotoPre(String placePhotoPre){
        this.placePhotoPre = placePhotoPre;
    }
    public String getPlacePhotoPre(){
        return this.placePhotoPre;
    }
    public void setPlaceImgWidth(String placeImgWidth){
        this.placeImgWidth = placeImgWidth;
    }
    public String getPlaceImgWidth(){
        return this.placeImgWidth;
    }
    public Entity_Home() {
        super();
        // TODO Auto-generated constructor stub
    }
}