package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * 激活优惠券后返回的结果
 * 
 * @author Administrator 2013-01-11
 * 
 */
public class ActivateVoucherCodeResult extends MBaseBean {
    private boolean success; // 激活是否成功，true成功，false反之。
    private String error; // 错误信息，如：请输入验证码
    private String voucherId; // 优惠券Id
    private boolean usedOrder; // 该订单是否能使用，true 可以、反之不行。
    private int status; //状态值 1表示激活成功，0表示失败，-4表示需要验证码
    //验证码ID， 当状态值为-4时，返回。
    private String vcodeId;
    //验证码地址，当状态值为-4时，返回。
    private String vcodeUrl; 

    public boolean isSuccess() {
	return success;
    }

    public void setSuccess(final boolean success) {
	this.success = success;
    }

    public String getError() {
	return error;
    }

    public void setError(final String error) {
	this.error = error;
    }

    public String getVocherId() {
	return voucherId;
    }

    public void setVocherId(final String vocherId) {
	voucherId = vocherId;
    }

    public boolean isUsedOrder() {
	return usedOrder;
    }

    public void setUsedOrder(final boolean usedOrder) {
	this.usedOrder = usedOrder;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getVcodeId() {
        return vcodeId;
    }

    public void setVcodeId(String vcodeId) {
        this.vcodeId = vcodeId;
    }

    public String getVcodeUrl() {
        return vcodeUrl;
    }

    public void setVcodeUrl(String vcodeUrl) {
        this.vcodeUrl = vcodeUrl;
    }
    
    
}
