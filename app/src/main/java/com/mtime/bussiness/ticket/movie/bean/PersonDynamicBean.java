package com.mtime.bussiness.ticket.movie.bean;

import com.helen.obfuscator.IObfuscateKeepAll;

/**
 * @author vivian.wei
 * @date 2020/9/17
 * @desc 用户对影人的动态信息Bean
 */
public class PersonDynamicBean implements IObfuscateKeepAll {

    private long userId;        // 当前用户Id
    private String nickName;    // 用户昵称
    private String avatar;      // 用户头像
    private String rating;	    // 当前用户对该影人评分
    private boolean favorite;   // 当前用户是否收藏
    private boolean follow;     // 当前用户是否关注该影人

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isFollow() {
        return follow;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
