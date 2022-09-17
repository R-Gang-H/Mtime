package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * 普通返回结果
 */
public class BaseResultJsonBean extends MBaseBean {
    private boolean success;
    private String msg;
    private int status;
    private String error;
    private String orderSuccessUrl;//后产品跳转到支付成功页使用
    //新手礼包提示图片
    private String newPeopleGiftImage;
    private String vcodeId;//图片验证码编号 ，只有状态值5时，才返回图片验证码编号和图片验证码地址
    private String vcodeImg;//图片验证码地址

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(final boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(final String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(final int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getOrderSuccessUrl() {
        if (orderSuccessUrl == null) {
            return "";
        }
        return orderSuccessUrl;
    }

    public void setOrderSuccessUrl(String orderSuccessUrl) {
        this.orderSuccessUrl = orderSuccessUrl;
    }

    public String getNewPeopleGiftImage() {
        return newPeopleGiftImage;
    }

    public void setNewPeopleGiftImage(String newPeopleGiftImage) {
        this.newPeopleGiftImage = newPeopleGiftImage;
    }

    public String getVcodeId() {
        return vcodeId;
    }

    public void setVcodeId(String vcodeId) {
        this.vcodeId = vcodeId;
    }

    public String getVcodeImg() {
        return vcodeImg;
    }

    public void setVcodeImg(String vcodeImg) {
        this.vcodeImg = vcodeImg;
    }
}