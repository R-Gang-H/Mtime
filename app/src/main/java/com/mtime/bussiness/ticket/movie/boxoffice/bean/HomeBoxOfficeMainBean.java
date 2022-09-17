package com.mtime.bussiness.ticket.movie.boxoffice.bean;

import com.helen.obfuscator.IObfuscateKeepAll;
import com.kk.taurus.uiframe.i.HolderData;
import com.mtime.base.bean.MBaseBean;

import java.util.List;

public class HomeBoxOfficeMainBean extends MBaseBean implements IObfuscateKeepAll, HolderData {
    private List<HomeBoxOfficeTabListBean> topList;
    
    public List<HomeBoxOfficeTabListBean> getTopList() {
        return topList;
    }
    
    public void setTopList(List<HomeBoxOfficeTabListBean> topList) {
        this.topList = topList;
    }
}
