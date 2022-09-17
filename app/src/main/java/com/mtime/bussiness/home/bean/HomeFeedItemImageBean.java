package com.mtime.bussiness.home.bean;

import com.helen.obfuscator.IObfuscateKeepAll;

/**
 * Created by ZhouSuQiang on 2017/11/22.
 * 首页-feed流中的图片数据实体
 */

public class HomeFeedItemImageBean implements IObfuscateKeepAll {
    public String imgUrl;
    public boolean isVideo;
    
    public String getImgUrl() {
        return imgUrl;
    }
    
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
    
    public boolean isVideo() {
        return isVideo;
    }
    
    public void setVideo(boolean video) {
        isVideo = video;
    }
    
}
