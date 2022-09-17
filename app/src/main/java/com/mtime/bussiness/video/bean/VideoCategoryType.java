package com.mtime.bussiness.video.bean;

/**
 * Created by mtime on 2017/10/19.
 */

public enum VideoCategoryType {

    RECOMMEND(-1,"推荐"),
    TRAILER(0,"预告片"),
    WONDERFUL_CLIPS(1,"精彩片段"),
    SHOOTING_HIGHLIGHTS(2,"拍摄花絮"),
    INTERVIEW(3,"影人访谈"),
    MOVIE_PREMIERE(4,"电影首映"),
    MV(5,"MV")
    ;

    int typeValue;
    String typeName;

    VideoCategoryType(int typeValue, String typeName){
        this.typeValue = typeValue;
        this.typeName = typeName;
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
}
