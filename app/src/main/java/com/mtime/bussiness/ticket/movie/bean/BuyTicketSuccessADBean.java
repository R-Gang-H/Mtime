package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

public class BuyTicketSuccessADBean extends MBaseBean {
    public List<ADItemBean> list;
    public static class ADItemBean extends MBaseBean {
        public String advTag;
        public String applinkData;
        public String img;
    }
}
