package com.mtime.beans;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by cong.zhang on 17/3/14.
 */

public class PromotionPromptBean extends MBaseBean {
    private String title;
    private long endTime;// 最近促销结束时间，unix时间戳，单位秒，需要获取服务时间进行比对。

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
