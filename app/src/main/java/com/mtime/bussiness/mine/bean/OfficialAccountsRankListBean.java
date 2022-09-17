package com.mtime.bussiness.mine.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by yinguanping on 16/10/19.
 */
public class OfficialAccountsRankListBean  extends MBaseBean {
    private int rankId;//榜单详情item id(影人/影片/电视)
    private String rating;//评分
    private String imgUrl;//item图片的url

    public int getRankId() {
        return rankId;
    }

    public void setRankId(int rankId) {
        this.rankId = rankId;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
