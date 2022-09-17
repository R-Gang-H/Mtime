package com.mtime.bussiness.ticket.movie.bean;


import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;

public class ThirdPayListItem  extends MBaseBean implements Serializable {
    private static final long serialVersionUID = 516237728041254691L;

    private String name;
    private int typeId;
    // TODO X url没有返回, 相关的可以直接删掉,影院会员卡的
    private String  url;
    private String  tag;

    public String getName() {
        if (name==null) {
            return "";
        }
        else{
            return name;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }
    
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getTag() {
        if (tag == null) {
            return "";
        }
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
