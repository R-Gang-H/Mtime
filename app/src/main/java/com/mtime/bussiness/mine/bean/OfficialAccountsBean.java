package com.mtime.bussiness.mine.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by yinguanping on 16/10/19.
 */
public class OfficialAccountsBean  extends MBaseBean {

    private int contentId;//内容id
    private int type;//文章类型  1:普通文章/视频  2:图集  3:榜单
    private boolean isVideo;//type为1时是否为视频，默认false
    private String videoLength;//片长
    private String imgsCount;//图集图片数量
    private String[] images;//图片数组(type为1/2时试用，为1时数组长度为1,为2时长度为3)
    private String title;//标题
    private String commentCount;//评论数量
    private String praiseCount;//赞数量
    private OfficialAccountsRankBean rank;

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public String getVideoLength() {
        return videoLength;
    }

    public void setVideoLength(String videoLength) {
        this.videoLength = videoLength;
    }

    public String getImgsCount() {
        return imgsCount;
    }

    public void setImgsCount(String imgsCount) {
        this.imgsCount = imgsCount;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public String getPraiseCount() {
        return praiseCount;
    }

    public void setPraiseCount(String praiseCount) {
        this.praiseCount = praiseCount;
    }

    public OfficialAccountsRankBean getRank() {
        return rank;
    }

    public void setRank(OfficialAccountsRankBean rank) {
        this.rank = rank;
    }
}
