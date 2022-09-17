package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;
import com.mtime.bussiness.ticket.cinema.activity.TwitterCinemaActivity;

import java.util.List;

/**
 * @see TwitterCinemaActivity
 */
public class TopicAllBean extends MBaseBean {
    private String           error;
    private TopicParent parentTopic;
    private int              replyTotalCount;
    private List<TopicReply> replyTopic;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public TopicParent getParentTopic() {
        return parentTopic;
    }

    public void setParentTopic(TopicParent parentTopic) {
        this.parentTopic = parentTopic;
    }

    public int getReplyTotalCount() {
        return replyTotalCount;
    }

    public void setReplyTotalCount(int replyTotalCount) {
        this.replyTotalCount = replyTotalCount;
    }

    public List<TopicReply> getReplyTopic() {
        return replyTopic;
    }

    public void setReplyTopic(List<TopicReply> replyTopic) {
        this.replyTopic = replyTopic;
    }
}
