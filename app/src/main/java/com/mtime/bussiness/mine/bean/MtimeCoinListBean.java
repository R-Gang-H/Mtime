package com.mtime.bussiness.mine.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.ArrayList;

/**
 * Created by zhulinping on 2017/6/13.
 */

public class MtimeCoinListBean  extends MBaseBean {

    private int totalCount;     // 总条数
    private boolean hasMore;    // 是否有更多数据
    ArrayList<MtimeCoinBean> list;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public ArrayList<MtimeCoinBean> getList() {
        return list;
    }

    public void setList(ArrayList<MtimeCoinBean> list) {
        this.list = list;
    }
}