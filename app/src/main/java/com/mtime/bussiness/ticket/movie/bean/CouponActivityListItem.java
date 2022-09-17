package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;

public class CouponActivityListItem extends MBaseBean implements Serializable {
    private static final long serialVersionUID = 516137708041254691L;
    private int id;
    private boolean isSelected;
    private int type;
    private String tag;
    private String desc;
    private boolean enable;
    private int deductionAmount;
    private String url;
    private boolean usedVoucher;
    private boolean used;
    private boolean checkMobileBind;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTag() {
        if (tag==null) {
            return "";
        }
        else{
            return tag;
        }
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDesc() {
        if (desc==null) {
            return "";
        }
        else{
            return desc;
        }
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean getEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public int getDeductionAmount() {
        return deductionAmount;
    }

    public void setDeductionAmount(int deductionAmount) {
        this.deductionAmount = deductionAmount;
    }

    public String getUrl() {
        if (url==null) {
            return "";
        }
        else{
            return url;
        }
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean getUsedVoucher() {
        return usedVoucher;
    }

    public void setUsedVoucher(boolean usedVoucher) {
        this.usedVoucher = usedVoucher;
    }

    public boolean getUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public boolean isCheckMobileBind() {
        return checkMobileBind;
    }

    public void setCheckMobileBind(boolean checkMobileBind) {
        this.checkMobileBind = checkMobileBind;
    }
}

