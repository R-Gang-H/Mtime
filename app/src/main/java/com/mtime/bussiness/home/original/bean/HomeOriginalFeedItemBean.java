package com.mtime.bussiness.home.original.bean;

import com.mtime.bussiness.home.bean.HomeFeedItemBean;

import java.io.Serializable;

/**
 * Created by ZhouSuQiang on 2017/11/27.
 * 首页-原创-feed流中的item实体
 */

public class HomeOriginalFeedItemBean extends HomeFeedItemBean implements Serializable {
    public long relatedId;
    public String tag;
    public String tagFontColor;
    public HomeOriginalFeedAdvBean adv;

    public long getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(long relatedId) {
        this.relatedId = relatedId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTagFontColor() {
        return tagFontColor;
    }

    public void setTagFontColor(String tagFontColor) {
        this.tagFontColor = tagFontColor;
    }

    public HomeOriginalFeedAdvBean getAdv() {
        return adv;
    }

    public void setAdv(HomeOriginalFeedAdvBean adv) {
        this.adv = adv;
    }
}
