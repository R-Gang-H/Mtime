package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.ArrayList;
import java.util.List;

public class V2_MovieCommentAllBean extends MBaseBean {
	private List<V2_MovieCommentBean> cts = new ArrayList<V2_MovieCommentBean>();
	private int tpc;
	private int totalCount;
	private int totalCommentCount;

	public List<V2_MovieCommentBean> getCts() {
		return cts;
	}

	public void setCts(List<V2_MovieCommentBean> cts) {
		if (null != cts ) {
			this.cts = cts;
		}
	}

	public int getTpc() {
		return tpc;
	}

	public void setTpc(int tpc) {
		this.tpc = tpc;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int gettotalCommentCount() {
		return totalCommentCount;
	}

	public void settotalCommentCount(int totalCommentCount) {
		this.totalCommentCount = totalCommentCount;
	}

}
