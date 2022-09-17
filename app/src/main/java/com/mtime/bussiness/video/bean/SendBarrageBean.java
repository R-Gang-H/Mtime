package com.mtime.bussiness.video.bean;


import com.mtime.base.bean.MBaseBean;

/**
 * 发送弹幕的bean
 * Created by wangdaban on 17/8/15.
 * "bizCode" : 1,  1 发布成功、2 敏感词 、3 超发送字符、4 其他错误
   "bizMsg"  : "", 描述
 */

public class SendBarrageBean extends MBaseBean {

    private int bizCode;

    private  String bizMsg;

    public int getBizCode() {
        return bizCode;
    }

    public void setBizCode(int bizCode) {
        this.bizCode = bizCode;
    }

    public String getBizMsg() {
        return bizMsg;
    }

    public void setBizMsg(String bizMsg) {
        this.bizMsg = bizMsg;
    }
}
