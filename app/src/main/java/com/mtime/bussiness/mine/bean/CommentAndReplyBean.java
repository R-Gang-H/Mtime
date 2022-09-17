package com.mtime.bussiness.mine.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

public class CommentAndReplyBean  extends MBaseBean {
    private int count;// 事实上是分页以后的当前页码
    private List<CommentAndReplyListBean> userCommtentList;

    public int getCount() {
        return count;
    }

    public List<CommentAndReplyListBean> getUserCommtentList() {
        return userCommtentList;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setUserCommtentList(List<CommentAndReplyListBean> userCommtentList) {
        this.userCommtentList = userCommtentList;
    }
}
