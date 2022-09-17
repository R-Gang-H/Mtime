package com.mtime.bussiness.ticket.movie.bean;


import com.mtime.base.bean.MBaseBean;

public class CardLogicBean extends MBaseBean
{
    private boolean success;
    private String msg;
    private int status;
    private double num;
    private int  needMoney;
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

    public int getNeedMoney()
    {
        return needMoney;
    }

    public void setNeedMoney(int needMoney)
    {
        this.needMoney = needMoney;
    }

    public double getNum()
    {
        return num;
    }

    public void setNum(double num)
    {
        this.num = num;
    }

}
