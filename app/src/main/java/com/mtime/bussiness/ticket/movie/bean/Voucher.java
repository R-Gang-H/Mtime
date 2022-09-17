package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;

/**
 * 优惠券
 * 
 * @author ye
 */
public class Voucher extends MBaseBean implements Serializable {
    private static final long serialVersionUID = 516237708011254691L;

    private double amount; // 金额，单位 "分"
    private double deductAmount;//实际抵扣金额
    private String voucherName;
    private String useEndTime; // 截止使用日期(格式：2013-05-31)
    private String voucherID;
    private boolean needValidate;
    private boolean isUseMore;
    private boolean isSelected = true;
    private boolean used = false;
    public double getAmount() {
	return amount;
    }

    public void setAmount(final double amount) {
	this.amount = amount;
    }

    public String getVoucherName() {
	return voucherName;
    }

    public void setVoucherName(final String voucherName) {
	this.voucherName = voucherName;
    }

    public String getUseEndTime() {
	return useEndTime;
    }

    public void setUseEndTime(final String useEndTime) {
	this.useEndTime = useEndTime;
    }

    public String getVoucherID() {
	return voucherID;
    }

    public void setVoucherID(final String voucherID) {
	this.voucherID = voucherID;
    }

  
    public boolean isNeedValidate() {
        return needValidate;
    }


    public void setNeedValidate(Boolean needValidate) {
        this.needValidate = needValidate;
    }

    public double getDeductAmount() {
        return deductAmount;
    }

    public void setDeductAmount(double deductAmount) {
        this.deductAmount = deductAmount;
    }

	public boolean isUseMore() {
		return isUseMore;
	}

	public void setUseMore(boolean isUseMore) {
		this.isUseMore = isUseMore;
	}
	
	public boolean isSelected() {
        return isSelected;
    }
    
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
    
    public boolean isUsed() {
        return used;
    }
    
    public void setUsed(boolean used) {
        this.used = used;
    }
}