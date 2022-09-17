package com.mtime.bussiness.ticket.bean;

import android.text.TextUtils;

import com.mtime.base.bean.MBaseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JiaJunHui on 2018/5/23.
 */
public class SeatItem extends MBaseBean {

    private String seatInfo;

    public String getSeatInfo() {
        return seatInfo;
    }

    public void setSeatInfo(String seatInfo) {
        this.seatInfo = seatInfo;
    }

    public static List<SeatItem> parseSeat(String seatText){
        List<SeatItem> items = new ArrayList<>();
        if(!TextUtils.isEmpty(seatText)){
            String[] seatArray = seatText.split(",");
            SeatItem item;
            for(String seat:seatArray){
                item = new SeatItem();
                item.setSeatInfo(seat);
                items.add(item);
            }
        }
        return items;
    }
}
