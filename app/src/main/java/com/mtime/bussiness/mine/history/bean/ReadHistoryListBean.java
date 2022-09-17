package com.mtime.bussiness.mine.history.bean;

import com.helen.obfuscator.IObfuscateKeepAll;
import com.kk.taurus.uiframe.i.HolderData;
import com.mtime.bussiness.mine.history.dao.HistoryDao;

import java.util.List;

/**
 * Created by vivian.wei on 2018/8/3.
 * 阅读历史列表Bean
 * 记录在本地数据库，不混淆
 */

public class ReadHistoryListBean implements IObfuscateKeepAll, HolderData {

    private List<HistoryDao> list;

    public List<HistoryDao> getList() {
        return list;
    }

    public void setList(List<HistoryDao> list) {
        this.list = list;
    }
}
