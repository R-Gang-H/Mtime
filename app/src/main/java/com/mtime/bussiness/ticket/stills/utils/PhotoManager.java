package com.mtime.bussiness.ticket.stills.utils;

import com.mtime.beans.Photo;

import java.util.ArrayList;

/**
 * create by lushan on 2021/6/1
 * description: 管理影片详情中跳转到剧照传递的图片列表，避免因内容过多导致crash bug
 */
public class PhotoManager {
    private static ArrayList<Photo> photoList = new ArrayList<>();

    public static void setPhotoData(ArrayList<Photo>list){
        photoList.clear();
        photoList.addAll(list);
    }

    public static void  clearPhotoData(){
        photoList.clear();
    }

    public static ArrayList<Photo> getPhotoList(){
        return photoList;
    }
}
