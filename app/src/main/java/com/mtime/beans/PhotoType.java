package com.mtime.beans;

import com.mtime.base.bean.MBaseBean;

public class PhotoType extends MBaseBean {

    public String typeName;
    public int type;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(final String typeName) {
        this.typeName = typeName;
    }

    public int getType() {
        if (type <= 0) {
            return -1;
        }
        return type;
    }

    public void setType(final int type) {
        this.type = type;
    }
}
