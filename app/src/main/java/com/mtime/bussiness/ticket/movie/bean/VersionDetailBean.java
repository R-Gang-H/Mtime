package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

// 关于我们中的版版本信息
public class VersionDetailBean extends MBaseBean {

    private String versionId;
    private String summary;
    private String version;

    public String getVersion() {
	return version;
    }

    public void setVersion(final String version) {
	this.version = version;
    }

    public String getVersionID() {
	return versionId;
    }

    public void setVersionID(final String versionId) {
	this.versionId = versionId;
    }

    public String getSummary() {
	return summary;
    }

    public void setSummary(final String summary) {
	this.summary = summary;
    }
}
