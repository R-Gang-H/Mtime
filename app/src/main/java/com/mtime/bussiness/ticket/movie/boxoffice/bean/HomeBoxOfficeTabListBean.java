package com.mtime.bussiness.ticket.movie.boxoffice.bean;

import com.helen.obfuscator.IObfuscateKeepAll;
import com.kk.taurus.uiframe.i.HolderData;
import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HomeBoxOfficeTabListBean extends MBaseBean implements IObfuscateKeepAll, HolderData ,Serializable {
    private String title;
    private String titleSmall;
    private String pageSubAreaId;
    public List<HomeBoxOfficeTabListBean> subTopList;

    public String getTitle() {
        if (title == null) {
            return "";
        } else {
            return title;
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleSmall() {
        if (titleSmall == null) {
            return "";
        } else {
            return titleSmall;
        }
    }

    public void setTitleSmall(String titleSmall) {
        this.titleSmall = titleSmall;
    }

    public String getPageSubAreaId() {
        if (pageSubAreaId == null) {
            return "";
        } else {
            return pageSubAreaId;
        }
    }

    public void setPageSubAreaId(String pageSubAreaId) {
        this.pageSubAreaId = pageSubAreaId;
    }

    public List<HomeBoxOfficeTabListBean> getSubTopList() {
        if (subTopList == null) {
            return new ArrayList<HomeBoxOfficeTabListBean>();
        }
        return subTopList;
    }

    public void setSubTopList(List<HomeBoxOfficeTabListBean> subTopList) {
        this.subTopList = subTopList;
    }

}
