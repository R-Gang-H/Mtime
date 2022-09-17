package com.mtime.beans;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

public class UploadResultBean extends MBaseBean {
	private List<ResultList> resultList;

	public List<ResultList> getResult() {
		return resultList;
	}

	public void setResult(List<ResultList> list) {
		this.resultList = list;
	}
}
