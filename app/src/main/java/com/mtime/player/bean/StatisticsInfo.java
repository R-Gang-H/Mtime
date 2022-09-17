package com.mtime.player.bean;

import java.io.Serializable;

/**
 * Created by JiaJunHui on 2018/6/22.
 */
public class StatisticsInfo implements Serializable {

    private String vid;
    private int sourceType;
    private String pageRefer;
    private String pageLabel;

    public StatisticsInfo() {
    }

    public StatisticsInfo(String vid, int sourceType, String pageRefer, String pageLabel) {
        this.vid = vid;
        this.sourceType = sourceType;
        this.pageRefer = pageRefer;
        this.pageLabel = pageLabel;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    public String getPageRefer() {
        return pageRefer;
    }

    public void setPageRefer(String pageRefer) {
        this.pageRefer = pageRefer;
    }

    public String getPageLabel() {
        return pageLabel;
    }

    public void setPageLabel(String pageLabel) {
        this.pageLabel = pageLabel;
    }

}
