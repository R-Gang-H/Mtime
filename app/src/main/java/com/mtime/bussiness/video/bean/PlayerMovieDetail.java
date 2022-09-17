package com.mtime.bussiness.video.bean;

import com.kk.taurus.uiframe.i.HolderData;
import com.mtime.base.bean.MBaseBean;
import com.mtime.bussiness.information.bean.ArticleRelatedMoviesBean;

import java.io.Serializable;

/**
 * Created by mtime on 2017/10/11.
 */

/**
 * "data": {
 * "vId": 42239, //视频id
 * "image": "http://img31.test.cn/mg/2012/09/27/094150.46515035.jpg",  //视频图片
 * "videoSource",  1,  //视频来源1 预告片、2 自媒体、3 媒资视频
 * "title": "郭_flv",       //影片名
 * "type": 0,                //0-预告片,1-精彩片段,2-拍摄花絮,3-影人访谈,4-电影首映,5-MV
 * "length": 70  //视频时长,秒
 * "playCount" : "11,111" ，             //播放数量字段串
 * "commentTotal"："123,122"，           //评论总数
 * "pulishTime":111133333               //发布时间戳，标准unix时间戳，单位秒
 * }
 */

public class PlayerMovieDetail extends MBaseBean implements Serializable, HolderData {

    private int vId;
    private String image;
    private int videoSource;
    private String title;
    private int type;
    private int length;
    private String playCount;
    private String commentTotal;
    private long pulishTime;
    public boolean isAllowComment = true; // 是否允许评论 -- 201910新增
    private ArticleRelatedMoviesBean relatedMovie;

    public ArticleRelatedMoviesBean getRelatedMovie() {
        return relatedMovie;
    }

    public void setRelatedMovie(ArticleRelatedMoviesBean relatedMovie) {
        this.relatedMovie = relatedMovie;
    }

    public int getvId() {
        return vId;
    }

    public void setvId(int vId) {
        this.vId = vId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getVideoSource() {
        return videoSource;
    }

    public void setVideoSource(int videoSource) {
        this.videoSource = videoSource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getPlayCount() {
        return playCount;
    }

    public void setPlayCount(String playCount) {
        this.playCount = playCount;
    }

    public String getCommentTotal() {
        return commentTotal;
    }

    public void setCommentTotal(String commentTotal) {
        this.commentTotal = commentTotal;
    }

    public long getPulishTime() {
        return pulishTime;
    }

    public void setPulishTime(long pulishTime) {
        this.pulishTime = pulishTime;
    }
}
