package com.mtime.player.bean;

/**
 * Created by mtime on 2017/10/27.
 */

public enum SchemeType {

    HTTP("http","http"),
    HTTPS("https","https")
    ;

    String type;
    String desc;

    SchemeType(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
