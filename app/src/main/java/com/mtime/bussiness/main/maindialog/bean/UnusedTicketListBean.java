package com.mtime.bussiness.main.maindialog.bean;

import com.helen.obfuscator.IObfuscateKeepAll;

import java.util.List;

public class UnusedTicketListBean implements IObfuscateKeepAll {
    public List<UnusedTicketItemBean> ticketList;
    public boolean hasDatas() {
        return null != ticketList && !ticketList.isEmpty();
    }
}
