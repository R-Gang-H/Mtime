// JSON Java Class Generator
// Written by Bruce Bao
// Used for API: 
package com.mtime.bussiness.ticket.movie.bean;

import android.text.TextUtils;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;

public class V2_MovieCommentBean extends MBaseBean implements Serializable {
    private static final long serialVersionUID = 8049887843466262892L;
    //为影评ID
    private int tweetId;
    //评论标题
    private String ct;
    //评论内容
    private String ce;
    //评论者的昵称
    private String ca;
    //评论时间
    private long cd;
    //本地时间/s
    private int lcd;
    //评论者的头像
    private String caimg;
    //评论者所在的城市
    private String cal;
    //评论数
    private int commentCount;
    //评论者评分
    private String cr;
    //总的点赞数
    private int totalPraise;
    //是否已经点赞
    private boolean isPraise;
    //评论图片url
    private String ceimg;
    private boolean isHot;
    private int position;
    public void setPosition(int position){
        this.position=position;
    }
    public int getPosition(){
        return position;
    }

    public void setIsHot(boolean isHot) {
        this.isHot = isHot;
    }

    public boolean getHot() {
        return isHot;
    }

    public int getTweetId() {
        return tweetId;
    }

    public void setTweetId(int tweetId) {
        this.tweetId = tweetId;
    }

    public String getCt() {
        return ct;
    }

    public void setCt(String ct) {
        this.ct = ct;
    }

    public int getTotalPraise() {
        return totalPraise;
    }

    public void setTotalPraise(int totalPraise) {
        this.totalPraise = totalPraise;
    }

    public boolean getIsPraise() {
        return isPraise;
    }

    public void setIsPraise(boolean isPraise) {
        this.isPraise = isPraise;
    }

    public String getCe() {
        return ce;
    }

    public void setCe(String ce) {
        this.ce = ce;
    }

    public String getCa() {
        return ca;
    }

    public void setCa(String ca) {
        this.ca = ca;
    }

    public long getCd() {
        return cd;
    }

    public void setCd(long cd) {
        this.cd = cd;
    }

    public int getLcd() {
        return lcd;
    }

    public void setLcd(int lcd) {
        this.lcd = lcd;
    }

    public String getCaimg() {
        return caimg;
    }

    public void setCaimg(String caimg) {
        this.caimg = caimg;
    }

    public String getCal() {
        return cal;
    }

    public void setCal(String cal) {
        this.cal = cal;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public double getCr() {
        if (TextUtils.isEmpty(cr)){
            return 0;
        }
        return Double.parseDouble(cr);
    }

    public void setCr(String cr) {
        this.cr = cr;
    }

    public String getCeimg() {
        return ceimg;
    }

    public void setCeimg(String ceimg) {
        this.ceimg = ceimg;
    }
}
