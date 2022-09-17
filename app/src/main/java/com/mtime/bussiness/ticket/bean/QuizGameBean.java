package com.mtime.bussiness.ticket.bean;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by guangshun on 15-8-11.
 */
public class QuizGameBean extends MBaseBean implements Serializable {
    private int count;
    private String url;
    private String title;
    private String smallTitle;
    private String topMsg;
    private List<QuizGameUsersBean> topUsers;

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setSmallTitle(String smallTitle) {
        this.smallTitle = smallTitle;
    }

    public String getSmallTitle() {
        return smallTitle;
    }

    public void setTopMsg(String topMsg) {
        this.topMsg = topMsg;

    }

    public String getTopMsg() {
        return topMsg;
    }

    public void setTopUsers(List<QuizGameUsersBean> topUsers) {
        this.topUsers = topUsers;
    }

    public List<QuizGameUsersBean> getTopUsers() {
        return topUsers;
    }
}
