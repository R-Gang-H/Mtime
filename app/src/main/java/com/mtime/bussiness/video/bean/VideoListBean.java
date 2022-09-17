package com.mtime.bussiness.video.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.ArrayList;
import java.util.List;

public class VideoListBean extends MBaseBean {

    private int                     totalPageCount;
    private int                     totalCount;
    private List<VideoListItemBean> videoList = new ArrayList<VideoListItemBean>();

    public int getTotalPageCount() {
        return totalPageCount;
    }

    public void setTotalPageCount(int totalPageCount) {
        this.totalPageCount = totalPageCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<VideoListItemBean> getVideoList() {
        return videoList;
    }

    public void setVideoList(List<VideoListItemBean> videoList) {
        this.videoList = videoList;
    }

}
