package com.mtime.player.db;

/**
 * Created by mtime on 2017/11/8.
 */

public class VideoRecordInfo {

    public static final String TABLE_NAME = "record_info";

    public static final String COLUMN_VIDEO_ID = "vid";
    public static final String COLUMN_VIDEO_CURRENT_POSITION = "current_pos";
    public static final String COLUMN_VIDEO_DURATION = "duration";

    private String vid;
    private int current;
    private int duration;

    public String getVid() {
        return vid;
    }

    public VideoRecordInfo setVid(String vid) {
        this.vid = vid;
        return this;
    }

    public int getCurrent() {
        return current;
    }

    public VideoRecordInfo setCurrent(int current) {
        this.current = current;
        return this;
    }

    public int getDuration() {
        return duration;
    }

    public VideoRecordInfo setDuration(int duration) {
        this.duration = duration;
        return this;
    }

}
