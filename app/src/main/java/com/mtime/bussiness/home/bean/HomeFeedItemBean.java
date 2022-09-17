package com.mtime.bussiness.home.bean;

import com.helen.obfuscator.IObfuscateKeepAll;

import java.util.List;

/**
 * Created by ZhouSuQiang on 2017/11/20.
 * 首页-feed流item实体类
 */

public class HomeFeedItemBean implements IObfuscateKeepAll {
    public static final int CONTENT_TYPE_ARTICLES = 1; //图文
    public static final int CONTENT_TYPE_VIDEO = 2; //视频
    public static final int CONTENT_TYPE_AD = 3; //硬广告
    public static final int CONTENT_TYPE_RANK = 4; //榜单
    
    public String recommendID; //推荐id
    public String recommendType; //推荐类型
    public String title; //文章标题
    public int styleType; //1 小图样式、2 大图样式、3 三图样式
    public int contentType; //内容类型，1 图文、2 视频、3 硬广
    public int videoId; //视频id
    public int videoSourceType; //视频类型
    public long publishTime; //发布时间，时间戳，单位秒
    public String pageviews; //浏览量(100万浏览)
    public List<HomeFeedItemImageBean> images; //图片信息
    public HomePublicBaseInfoBean publicInfo; //公众号信息

    public int showType;//标记字段。 showType字段取值为1或2。 1-广告初次露出；2-屏幕滑走后滑回广告位置，页面未重新加载的情况下广告再次露出
    
    //===================非get set方法===========================
    
    public String getImgUrl(int index) {
        if(null != images && !images.isEmpty()
                && index >= 0 && index < images.size()) {
            return images.get(index).imgUrl;
        }
        return null;
    }
    
    public boolean isShowPlayIcon(int index) {
        if(null != images && !images.isEmpty()
                && index >= 0 && index < images.size()) {
            return images.get(index).isVideo;
        }
        return false;
    }
    
    public String getPublicName() {
        return null != publicInfo ? publicInfo.name : "";
    }
    
    //============================================================
    
    
    public String getRecommendID() {
        return recommendID;
    }
    
    public void setRecommendID(String recommendID) {
        this.recommendID = recommendID;
    }
    
    public String getRecommendType() {
        return recommendType;
    }
    
    public void setRecommendType(String recommendType) {
        this.recommendType = recommendType;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public int getStyleType() {
        return styleType;
    }
    
    public void setStyleType(int styleType) {
        this.styleType = styleType;
    }
    
    public int getContentType() {
        return contentType;
    }
    
    public void setContentType(int contentType) {
        this.contentType = contentType;
    }
    
    public int getVideoId() {
        return videoId;
    }
    
    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }
    
    public int getVideoSourceType() {
        return videoSourceType;
    }
    
    public void setVideoSourceType(int videoSourceType) {
        this.videoSourceType = videoSourceType;
    }
    
    public long getPublishTime() {
        return publishTime;
    }
    
    public void setPublishTime(long publishTime) {
        this.publishTime = publishTime;
    }
    
    public String getPageviews() {
        return pageviews;
    }
    
    public void setPageviews(String pageviews) {
        this.pageviews = pageviews;
    }
    
    public List<HomeFeedItemImageBean> getImages() {
        return images;
    }
    
    public void setImages(List<HomeFeedItemImageBean> images) {
        this.images = images;
    }
    
    public HomePublicBaseInfoBean getPublicInfo() {
        return publicInfo;
    }
    
    public void setPublicInfo(HomePublicBaseInfoBean publicInfo) {
        this.publicInfo = publicInfo;
    }

    public int getShowType() {
        return showType;
    }

    public void setShowType(int showType) {
        this.showType = showType;
    }
}