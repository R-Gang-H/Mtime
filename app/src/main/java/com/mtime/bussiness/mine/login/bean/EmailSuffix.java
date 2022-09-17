package com.mtime.bussiness.mine.login.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

/**
 * 邮箱后缀实体
 * 
 * @author ye
 */
public class EmailSuffix  extends MBaseBean {
    private List<String> mailExts; // 邮箱后缀的列表，如：@163.com, @126.com, @qq.com

    public List<String> getMailExts() {
	return mailExts;
    }

    public void setMailExts(final List<String> mailExts) {
	this.mailExts = mailExts;
    }
}
