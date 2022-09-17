package com.mtime.bussiness.video.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

/**
 * Created by mtime on 2017/10/25.
 */

public class VideoAdBean extends MBaseBean {


    private List<AdItem> daList;

    public List<AdItem> getDaList() {
        return daList;
    }

    public void setDaList(List<AdItem> daList) {
        this.daList = daList;
    }

    /**
     * "daList": [
     {
     "aID":100, // 广告id
     "title": "广告标题", // 标题
     "duration": 10,     // 持续时间，单位:秒
     "isShowTag": false, // 是否展示广告标签
     "tagDesc":"广告",   // 广告标签描述
     "image": "https://m.mtime.cn/1.jpg", // 图片链接地址
     "applinkData": "{\"handleType\":\"xxx\", ...}"  // appLink通用规则
     }
     ]
     */
    public static class AdItem extends MBaseBean {
        private int aID;
        private String title;
        private int duration;
        private boolean isShowTag;
        private String tagDesc;
        private String image;
        private String applinkData;

        public int getaID() {
            return aID;
        }

        public void setaID(int aID) {
            this.aID = aID;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public boolean isShowTag() {
            return isShowTag;
        }

        public void setShowTag(boolean showTag) {
            this.isShowTag = showTag;
        }

        public String getTagDesc() {
            return tagDesc;
        }

        public void setTagDesc(String tagDesc) {
            this.tagDesc = tagDesc;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getApplinkData() {
            return applinkData;
        }

        public void setApplinkData(String applinkData) {
            this.applinkData = applinkData;
        }
    }

}
