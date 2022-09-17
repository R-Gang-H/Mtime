package com.mtime.bussiness.video.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

/**
 * Created by mtime on 2017/10/23.
 */

public class VideoInfoApiRequestBean extends MBaseBean {

    private List<VideoInfoApiRequestItem> videoInfoApiRequest;
    private String scheme;

    public List<VideoInfoApiRequestItem> getVideoInfoApiRequest() {
        return videoInfoApiRequest;
    }

    public void setVideoInfoApiRequest(List<VideoInfoApiRequestItem> videoInfoApiRequest) {
        this.videoInfoApiRequest = videoInfoApiRequest;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public static class VideoInfoApiRequestItem extends MBaseBean {
        private long videoId;
        private int source;

        public long getVideoId() {
            return videoId;
        }

        public void setVideoId(long videoId) {
            this.videoId = videoId;
        }

        public int getSource() {
            return source;
        }

        public void setSource(int source) {
            this.source = source;
        }
    }

}
