package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;
import com.mtime.bussiness.ticket.bean.TweetReply;

import java.util.List;

public class TwitterAllBean extends MBaseBean {
    private String error;
    private TweetParent parentTweet;
    private int replyTotalCount;
    private String name;
    private int id;
    private String totalCommentCountName;
    private List<TweetReply> replyTweets;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTotalCommentCountName() {
        return totalCommentCountName;
    }

    public void setTotalCommentCountName(String totalCommentCountName) {
        this.totalCommentCountName = totalCommentCountName;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public TweetParent getParentTweet() {
        return parentTweet;
    }

    public void setParentTweet(TweetParent parentTweet) {
        this.parentTweet = parentTweet;
    }

    public int getReplyTotalCount() {
        return replyTotalCount;
    }

    public void setReplyTotalCount(int replyTotalCount) {
        this.replyTotalCount = replyTotalCount;
    }

    public List<TweetReply> getReplyTweets() {
        return replyTweets;
    }

    public void setReplyTweets(List<TweetReply> replyTweets) {
        this.replyTweets = replyTweets;
    }
}
