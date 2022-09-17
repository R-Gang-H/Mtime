package com.mtime.bussiness.mine.bean;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;

/**
 * Created by vivian.wei on 15/8/7.
 * 意见反馈－有奖反馈文字Bean
 */
@SuppressWarnings("serial")
public class FeedbackAwardTipsBean  extends MBaseBean implements Serializable{
    // 标题
    private String tag;
    // 详情
    private String desc;

    public String getTag() {
        if (tag == null) {
            return  "";
        }
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDesc() {
        if (desc == null) {
            return  "";
        }
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}