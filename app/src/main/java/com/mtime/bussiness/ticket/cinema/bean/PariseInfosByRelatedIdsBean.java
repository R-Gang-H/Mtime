package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;
import com.mtime.bussiness.ticket.bean.ReviewParis;

import java.util.List;

public class PariseInfosByRelatedIdsBean extends MBaseBean {
	private boolean success;
	private String error;
	private List<ReviewParis> reviewParises;

	public boolean getSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getError() {
              if (error==null) {
                  return "";
              }
              else{
                  return error;
              }
	}

	public void setError(String error) {
		this.error = error;
	}

	public List<ReviewParis> getReviewParises() {
		return reviewParises;
	}

	public void setReviewParises(List<ReviewParis> reviewParises) {
		this.reviewParises = reviewParises;
	}
}
