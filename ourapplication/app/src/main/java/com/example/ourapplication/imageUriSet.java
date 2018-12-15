package com.example.ourapplication;

import android.net.Uri;

/**
 * Created by 10065 on 2018/12/4.
 */

public class imageUriSet {
    private Uri imageUri;        //图片uri
    private String imagePath;          //图片存储路径
    private String imageParam;    //图片参数

    public imageUriSet(Uri uri){
        this.imageUri = uri;
    }

    public void setImagePath(String path){
        this.imagePath = path;
    }
    public void setImageParam(String param){
        this.imageParam = param;
    }
    public Uri getImageUri(){
        return imageUri;
    }

    public String getImagePath(){ return imagePath; }

    public String getImageParam(){
        return imageParam;
    }
}
