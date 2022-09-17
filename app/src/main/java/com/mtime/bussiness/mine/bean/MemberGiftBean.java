package com.mtime.bussiness.mine.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by vivian.wei on 2017/6/26.
 * 会员礼包Bean
 */

public class MemberGiftBean  extends MBaseBean {

    private int type;       // 1 购物券 2 购票券
    private String name;    // 券名称
    private int qty;        // 数量

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
