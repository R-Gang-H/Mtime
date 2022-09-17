package com.mtime.bussiness.mine.bean;

import com.mtime.base.bean.MBaseBean;

public class UnreadMessageBean  extends MBaseBean {

    private int errorCode;
    private String errorMsg;
    private int unreadNotificationCount;    // 528版本更名为时光小秘书未读数
    private int unreadBroadcastCount;
    private int unreadReviewCount;          // 评论回复未读数
    private int unreadPraiseCount;          // 点赞未读数
    private String notificationContent;     // 时光小秘书未读消息第一条
    private int unreadInviteTopicCount;     // 话题邀请的未读数

    // 非接口返回字段
    private String unreadInviteTopicTitle;  // 邀请话题未读第一条

    public int getUnreadReviewCount() {
        return unreadReviewCount;
    }

    public void setUnreadReviewCount(int unreadReviewCount) {
        this.unreadReviewCount = unreadReviewCount;
    }

    public void setErrorCode(final int code) {
        this.errorCode = code;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(final String err) {
        this.errorMsg = err;
    }


    public void setUnreadNotificationCount(final int count) {
        this.unreadNotificationCount = count;
    }

    public int getUnreadNotificationCount() {
        return unreadNotificationCount;
    }

    public void setUnreadBroadcastCount(final int count) {
        this.unreadBroadcastCount = count;
    }

    public int getUnreadBroadcastCount() {
        return unreadBroadcastCount;
    }

    public int getUnreadPraiseCount() {
        return unreadPraiseCount;
    }

    public void setUnreadPraiseCount(int unreadPraiseCount) {
        this.unreadPraiseCount = unreadPraiseCount;
    }

    public String getNotificationContent() {
        return notificationContent;
    }

    public void setNotificationContent(String notificationContent) {
        this.notificationContent = notificationContent;
    }

    public int getUnreadInviteTopicCount() {
        return unreadInviteTopicCount;
    }

    public void setUnreadInviteTopicCount(int unreadInviteTopicCount) {
        this.unreadInviteTopicCount = unreadInviteTopicCount;
    }

    public String getUnreadInviteTopicTitle() {
        return unreadInviteTopicTitle;
    }

    public void setUnreadInviteTopicTitle(String unreadInviteTopicTitle) {
        this.unreadInviteTopicTitle = unreadInviteTopicTitle;
    }
}
