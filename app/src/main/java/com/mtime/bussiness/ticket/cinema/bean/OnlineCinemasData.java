package com.mtime.bussiness.ticket.cinema.bean;

import com.kk.taurus.uiframe.i.HolderData;
import com.mtime.base.bean.MBaseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vivian.wei on 2017/10/19.
 * Tab购票_影院_所有可购票及有影讯的影院列表数据（2017.10新版）
 */

public class OnlineCinemasData extends MBaseBean implements HolderData {
    private List<CinemaBaseInfo> list = new ArrayList<>();  // 所有可购票及有影讯的影院列表

    public List<CinemaBaseInfo> getList() {
        return list;
    }

    public void setList(List<CinemaBaseInfo> list) {
        this.list = list;
    }
}
