package com.mtime.bussiness.information.bean;

import com.mtime.base.bean.MBaseBean;
import com.mtime.beans.ADDetailBean;

import java.util.List;

/**
 * 用于直播模块{@link com.mtime.bussiness.live.LiveNewView}
 * 后期需要分离开
 */
public class NewsListBean extends MBaseBean {
    private int                   id;
    private int                   type;           // 新闻类型. 0-普通新闻，1-图集，2-视频
    private String                image;
    private String                title;
    private String                title2;         // 副标题
    private String                summary;
    private String                summaryInfo;
    private boolean               hasSeen = false;
    private long                  publishTime;
    private long                  commentCount;
    private String                tag;
    private List<NewsPicBean> images;
    private ADDetailBean adBean;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(final int type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(final String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(final String summary) {
        this.summary = summary;
    }

    public String getSummaryInfo() {
        return summaryInfo;
    }

    public void setSummaryInfo(final String summaryInfo) {
        this.summaryInfo = summaryInfo;
    }

    /**
     * @return isSeen
     */
    public boolean isSeen() {
        return hasSeen;
    }

    /**
     * @param isSeen
     *            要设置的 isSeen
     */
    public void setSeen(final boolean isSeen) {
        hasSeen = isSeen;
    }

    public String getTitle2() {
        return title2;
    }

    public void setTitle2(String title2) {
        this.title2 = title2;
    }

    public long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(long publishTime) {
        this.publishTime = publishTime;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(long commentCount) {
        this.commentCount = commentCount;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<NewsPicBean> getImages() {
        return images;
    }

    public void setImages(List<NewsPicBean> images) {
        this.images = images;
    }

    public ADDetailBean getAdBean() {
        return adBean;
    }

    public void setAdBean(ADDetailBean adBean) {
        this.adBean = adBean;
    }
}
