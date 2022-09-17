package com.mtime.bussiness.ticket.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页获取的评论信息（影片/影人/影院）
 * 
 * @author ye
 * 
 */
public class CommentPageBean extends MBaseBean {
    private int count;
    private List<CommentBean> list = new ArrayList<CommentBean>();

    public int getCount() {
	return count;
    }

    public void setCount(final int count) {
	this.count = count;
    }

    public List<CommentBean> getList() {
	return list;
    }

    public void setList(final List<CommentBean> list) {
	this.list = list;
    }
}