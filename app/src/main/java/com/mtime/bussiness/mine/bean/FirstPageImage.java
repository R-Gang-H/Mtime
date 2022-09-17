package com.mtime.bussiness.mine.bean;


import com.mtime.base.bean.MBaseBean;

public class FirstPageImage   extends MBaseBean {
	private int gId;
	private String title;
	private String desc;
	private String url1;
	private String url2;

	public int getGId() {
		return gId;
	}

	public void setGId(int gId) {
		this.gId = gId;
	}

	public String getTitle() {
              if (title==null) {
                  return "";
              }
              else{
                  return title;
              }
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDesc() {
              if (desc==null) {
                  return "";
              }
              else{
                  return desc;
              }
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getUrl1() {
              if (url1==null) {
                  return "";
              }
              else{
                  return url1;
              }
	}

	public void setUrl1(String url1) {
		this.url1 = url1;
	}

	public String getUrl2() {
              if (url2==null) {
                  return "";
              }
              else{
                  return url2;
              }
	}

	public void setUrl2(String url2) {
		this.url2 = url2;
	}
}
