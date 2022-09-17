package com.mtime.bussiness.video.bean;

/**
 * Created by mtime on 2017/10/19.
 */

public enum VideoSource {

    MOVIEVIDEO(1, "预告片/老资料库", "lab"),
    SMAVIDEO(2, "自媒体视频", "mp"),
    MEDIAVIDEO(3, "媒资视频/新媒资", "vrs");

    int typeValue;
    String typeName;
    String statisticsVType;

    VideoSource(int typeValue, String typeName, String statisticsVType) {
        this.typeValue = typeValue;
        this.typeName = typeName;
        this.statisticsVType = statisticsVType;
    }

    public int getTypeValue() {
        return typeValue;
    }

    public void setTypeValue(int typeValue) {
        this.typeValue = typeValue;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getStatisticsVType() {
        return statisticsVType;
    }

    public void setStatisticsVType(String statisticsVType) {
        this.statisticsVType = statisticsVType;
    }
}
