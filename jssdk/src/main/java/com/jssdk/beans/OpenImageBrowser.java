package com.jssdk.beans;

import java.util.ArrayList;

/**
 * Created by yinguanping on 2017/5/25.
 */

public class OpenImageBrowser extends CommonBean {

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    private Data data;

    public static class Data {

        private int currentImageIndex;// 点击图片位置
        private ArrayList<String> photoImageUrls;// 图片数组
        public boolean isGif; // false or true ，图片类型是否是gif图，埋点使用，在iOS10.5.0和Android6.5.0版本添加
        public String originUrl; // 埋点用的字段，目前传原始URL，iOS10.5.0 & 6.5.0 开始支持

        public int getCurrentImageIndex() {
            return currentImageIndex;
        }

        public void setCurrentImageIndex(int currentImageIndex) {
            this.currentImageIndex = currentImageIndex;
        }

        public ArrayList<String> getPhotoImageUrls() {
            return photoImageUrls;
        }

        public void setPhotoImageUrls(ArrayList<String> photoImageUrls) {
            this.photoImageUrls = photoImageUrls;
        }
    }
}
