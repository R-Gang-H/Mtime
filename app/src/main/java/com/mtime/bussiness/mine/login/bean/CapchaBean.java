/**
 * 
 */
package com.mtime.bussiness.mine.login.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * @author wangjin
 * 
 */
public class CapchaBean  extends MBaseBean {
    private String codeId;
    private String url;

    public String getCodeId() {
	return codeId;
    }

    public void setCodeId(final String codeId) {
	this.codeId = codeId;
    }

    public String getUrl() {
	return url;
    }

    public void setUrl(final String url) {
	this.url = url;
    }

}
