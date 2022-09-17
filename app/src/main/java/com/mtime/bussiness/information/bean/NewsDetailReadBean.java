package com.mtime.bussiness.information.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * 相关阅读bean
 * Created by yinguanping on 16/3/29.
 */
public class NewsDetailReadBean extends MBaseBean {
    private int newsId;//对应跳转的新闻id
    private String title;//主标题
    private String subTitle;//副标题
    private String imgUrl;//图片地址
    private int type;//类型 1:图集新闻 2:普通新闻

    public int getNewsId() {
        return newsId;
    }

    public void setNewsId(int newsId) {
        this.newsId = newsId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
