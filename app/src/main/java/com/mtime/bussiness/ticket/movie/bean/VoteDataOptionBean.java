package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by guangshun on 16-6-24.
 */
public class VoteDataOptionBean extends MBaseBean {
    private int topicOptionId;//子项Id
    private String content;//内容
    private int voteCount;//投票数
    private boolean isSelf; //是否自己选中

    public void setTopicOptionId(int topicOptionId) {
        this.topicOptionId = topicOptionId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public void setIsSelf(boolean isSelf) {
        this.isSelf = isSelf;
    }

    public int getTopicOptionId() {
        return topicOptionId;
    }

    public String getContent() {
        return content;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public boolean getIsSelf() {
        return isSelf;
    }
}
