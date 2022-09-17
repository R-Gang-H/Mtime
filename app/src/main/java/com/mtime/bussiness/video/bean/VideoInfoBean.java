package com.mtime.bussiness.video.bean;

import android.text.TextUtils;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mtime on 2017/10/20.
 */

public class VideoInfoBean extends MBaseBean implements Serializable {

    private List<VideoInfoItem> videoInfoList;

    public List<VideoInfoItem> getVideoInfoList() {
        return videoInfoList;
    }

    public void setVideoInfoList(List<VideoInfoItem> videoInfoList) {
        this.videoInfoList = videoInfoList;
    }

    public String getTitle() {
        String title = "";
        if (videoInfoList != null && videoInfoList.size() > 0) {
            return videoInfoList.get(0).title;
        }
        return title;
    }

    public boolean isPlayable(String vid) {
        if (videoInfoList != null && !TextUtils.isEmpty(vid)) {
            for (VideoInfoItem item : videoInfoList) {
                if (vid.equals(String.valueOf(item.videoId))) {
                    return item.playable;
                }
            }
        }
        return false;
    }

    public static class VideoInfoItem extends MBaseBean {
        private long videoId;
        private String title;
        private int playLength;
        private String logo;
        private boolean playable;

        public long getVideoId() {
            return videoId;
        }

        public void setVideoId(long videoId) {
            this.videoId = videoId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getPlayLength() {
            return playLength;
        }

        public void setPlayLength(int playLength) {
            this.playLength = playLength;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public boolean isPlayable() {
            return playable;
        }

        public void setPlayable(boolean playable) {
            this.playable = playable;
        }
    }

}
