package com.mtime.bussiness.home.original.bean;

import com.helen.obfuscator.IObfuscateKeepAll;
import com.kk.taurus.uiframe.i.HolderData;

import java.util.List;

/**
 * Created by ZhouSuQiang on 2017/11/27.
 * 首页-原创-feed流中的item列表实体
 */

public class HomeOriginalFeedListBean implements IObfuscateKeepAll, HolderData {
    public List<HomeOriginalFeedItemBean> list;

    // 影片时光原创列表页使用
    private boolean hasMore;
    
    public List<HomeOriginalFeedItemBean> getList() {
        return list;
    }
    
    public void setList(List<HomeOriginalFeedItemBean> list) {
        this.list = list;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }
}
