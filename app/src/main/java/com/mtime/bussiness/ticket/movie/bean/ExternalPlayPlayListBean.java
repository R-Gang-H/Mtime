package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by zhuqiguang on 2018/6/26.
 * website www.zhuqiguang.cn
 */
public class ExternalPlayPlayListBean extends MBaseBean{
    private String sourceId;
    private String playSourceName;
    private String picUrl;
    private String payRule;

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getPlaySourceName() {
        return playSourceName;
    }

    public void setPlaySourceName(String playSourceName) {
        this.playSourceName = playSourceName;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getPayRule() {
        return payRule;
    }

    public void setPayRule(String payRule) {
        this.payRule = payRule;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    private String playUrl;
}
