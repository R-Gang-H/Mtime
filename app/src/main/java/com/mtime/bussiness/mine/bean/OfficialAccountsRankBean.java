package com.mtime.bussiness.mine.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.ArrayList;

/**
 * Created by yinguanping on 16/10/19.
 */
public class OfficialAccountsRankBean  extends MBaseBean {
    private int rankType;//榜单类型  1:影片  2：影人  3：电视剧
    private String bkUrl;//榜单背景url
    private int count;//列表数量
    private ArrayList<OfficialAccountsRankListBean> rankList;//榜单item列表(最多9条)

    public int getRankType() {
        return rankType;
    }

    public void setRankType(int rankType) {
        this.rankType = rankType;
    }

    public String getBkUrl() {
        return bkUrl;
    }

    public void setBkUrl(String bkUrl) {
        this.bkUrl = bkUrl;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<OfficialAccountsRankListBean> getRankList() {
        return rankList;
    }

    public void setRankList(ArrayList<OfficialAccountsRankListBean> rankList) {
        this.rankList = rankList;
    }
}
