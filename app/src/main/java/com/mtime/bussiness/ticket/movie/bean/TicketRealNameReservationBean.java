package com.mtime.bussiness.ticket.movie.bean;

import com.helen.obfuscator.IObfuscateKeepAll;

import java.util.List;

/**
 * @author vivian.wei
 * @date 2020/7/28
 * @desc 实名预约信息Bean
 */
public class TicketRealNameReservationBean implements IObfuscateKeepAll {

    private boolean need;  // true-启用实名预约  false-不启用实名预约
    private List<TicketRealNameBean> realNameInfoList;

    public boolean isNeed() {
        return need;
    }

    public void setNeed(boolean need) {
        this.need = need;
    }

    public List<TicketRealNameBean> getRealNameInfoList() {
        return realNameInfoList;
    }

    public void setRealNameInfoList(List<TicketRealNameBean> realNameInfoList) {
        this.realNameInfoList = realNameInfoList;
    }
}
