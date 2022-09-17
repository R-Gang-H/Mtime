package com.mtime.bussiness.mine.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by vivian.wei on 2017/6/23.
 * 会员权益Bean
 */

public class MemberRightBean   extends MBaseBean {

    private int type;    // Icon显示类型： 0 等级礼包、1 生日礼包、2 购物折扣、3 免运费、4 消费返币、 5 线下活动优先
    private String name; // 名称，如：生日礼包

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
}
