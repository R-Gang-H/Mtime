package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.ArrayList;
import java.util.List;

public class MovieHotLongCommentAllBean extends MBaseBean {
	private List<MovieHotLongCommentBean> comments = new ArrayList<MovieHotLongCommentBean>();
	private int totalCount;

	public List<MovieHotLongCommentBean> getComments() {
		return comments;
	}

	public void setComments(List<MovieHotLongCommentBean> comments) {
		this.comments = comments;
	}


	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

}
