// JSON Java Class Generator
// Written by Bruce Bao
// Used for API: 
package com.mtime.bussiness.mine.bean;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class FeedBackMainBean  extends MBaseBean implements Serializable{
	private int unreadCount;
	private List<FeedbackListBean> messages;

	public int getUnreadCount() {
		return unreadCount;
	}

	public void setUnreadCount(int unreadCount) {
		this.unreadCount = unreadCount;
	}

	public List<FeedbackListBean> getMessages() {
		return messages;
	}

	public void setMessages(List<FeedbackListBean> messages) {
		this.messages = messages;
	}
}
