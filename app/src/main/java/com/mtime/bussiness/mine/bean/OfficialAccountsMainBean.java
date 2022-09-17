package com.mtime.bussiness.mine.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.ArrayList;

/**
 * Created by yinguanping on 16/10/19.
 */
public class OfficialAccountsMainBean  extends MBaseBean {
    private String headUrl;//头像
    private String name;//公众号名称
    private int contentCount;//内容数量(列表总数)
    private int attentionCount;//关注数量
    private String introduce;//个人介绍
    private ArrayList<OfficialAccountsBean> offAccounts;//列表

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getContentCount() {
        return contentCount;
    }

    public void setContentCount(int contentCount) {
        this.contentCount = contentCount;
    }

    public int getAttentionCount() {
        return attentionCount;
    }

    public void setAttentionCount(int attentionCount) {
        this.attentionCount = attentionCount;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public ArrayList<OfficialAccountsBean> getOffAccounts() {
        return offAccounts;
    }

    public void setOffAccounts(ArrayList<OfficialAccountsBean> offAccounts) {
        this.offAccounts = offAccounts;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
