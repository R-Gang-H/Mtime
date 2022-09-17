package com.mtime.bussiness.main.maindialog.bean;

import android.text.TextUtils;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;

public class UpdateVerBean extends MBaseBean implements Serializable {


    private static final long serialVersionUID = 5020625620235232216L;

    public String getVersion() {
        if (TextUtils.isEmpty(version)) {
            version = "";
        }
	    return version;
    }

    public void setVersion(final String version) {
	this.version = version;
    }

    public String getUrl() {
	return url;
    }

    public void setUrl(final String url) {
	this.url = url;
    }

    public boolean isForceUpdate() {
	return forceUpdate;
    }

    public void setForceUpdate(final boolean forceUpdate) {
	this.forceUpdate = forceUpdate;
    }

    public String getChangelog() {
	return changelog;
    }

    public void setChangelog(final String changelog) {
	this.changelog = changelog;
    }

    private String version; // 版本
    private String url; // 下载地址
    private boolean forceUpdate; // 是否必须更新
    private String changelog; // 更新内容

}
