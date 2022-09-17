package com.mtime.bussiness.ticket.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by guangshun on 15-8-11.
 */
public class QuizGameUsersBean extends MBaseBean {
    private int userId;
    private String nickname;
    private String headPic;

    public void setUserId(int id) {
        this.userId = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getHeadPic() {
        return headPic;
    }
}
