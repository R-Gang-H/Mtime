package com.mtime.player.bean;

import com.kk.taurus.playerbase.entity.DataSource;

/**
 * Created by mtime on 2017/10/19.
 */

public class MTimeVideoData extends DataSource {

    /**
     * 视频ID
     */
    private String videoId;
    /**
     * 视频来源
     */
    private int source;

    /**
     * 文件大小
     */
    private long fileSize;

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public MTimeVideoData() {
    }

    public MTimeVideoData(String videoId, int source) {
        this.videoId = videoId;
        this.source = source;
    }

    public MTimeVideoData(String data) {
        super(data);
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }
}
