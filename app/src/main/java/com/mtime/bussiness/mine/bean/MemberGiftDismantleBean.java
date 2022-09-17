package com.mtime.bussiness.mine.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vivian.wei on 2017/6/26.
 * 拆礼包Bean
 */

public class MemberGiftDismantleBean  extends MBaseBean {

    private int bizCode;    // 0:兑换失败 1:兑换成功 2 需绑定手机号
    private String bizMsg;  // 兑换成功
    private String title;   // 标题
    private String hint;    // 提示信息
    private List<MemberGiftBean> list = new ArrayList<>();  // 礼包内容

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public List<MemberGiftBean> getList() {
        return list;
    }

    public void setList(List<MemberGiftBean> list) {
        this.list = list;
    }
}
