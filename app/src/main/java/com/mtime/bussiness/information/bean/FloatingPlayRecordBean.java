package com.mtime.bussiness.information.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by mtime on 2017/10/24.
 */

public class FloatingPlayRecordBean extends MBaseBean {

    private int recordPos;

    public int getRecordPos() {
        return recordPos;
    }

    public FloatingPlayRecordBean setRecordPos(int recordPos) {
        this.recordPos = recordPos;
        return this;
    }
}
