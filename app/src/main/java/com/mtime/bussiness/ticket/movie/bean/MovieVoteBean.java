package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LEE on 16/4/1.
 */
public class MovieVoteBean extends MBaseBean {
    private int type; //1显示在详情，2显示在评论
    private String tag; //标识
    private String url;  //投票链接
    private int qId;
    private List<VoteDataItemBean> options = new ArrayList<VoteDataItemBean>();

    public void setqId(int qId) {
        this.qId = qId;
    }



    public void setOptions(List<VoteDataItemBean> options) {
        this.options = options;
    }

    public int getqId() {
        return qId;
    }


    public List<VoteDataItemBean> getOptions() {
        return options;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
