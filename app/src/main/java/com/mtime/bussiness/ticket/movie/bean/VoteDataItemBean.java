package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guangshun on 16-6-27.
 */
public class VoteDataItemBean extends MBaseBean {
    private String title; //投票标题
    private boolean isMult;  //是否多项
    private int maxChooseCount;//最多可选数
    private int topicId;//主题Id
    private int voteCount;
    private List<VoteDataOptionBean> options = new ArrayList<VoteDataOptionBean>();

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIsMult(boolean isMult) {
        this.isMult = isMult;
    }

    public void setMaxChooseCount(int maxChooseCount) {
        this.maxChooseCount = maxChooseCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public void setOptions(List<VoteDataOptionBean> options) {
        this.options = options;
    }

    public String getTitle() {
        return title;
    }

    public boolean getIsMult() {
        return isMult;
    }

    public int getMaxChooseCount() {
        return maxChooseCount;
    }


    public int getTopicId() {
        return topicId;
    }

    public List<VoteDataOptionBean> getOptions() {
        return options;
    }
}
