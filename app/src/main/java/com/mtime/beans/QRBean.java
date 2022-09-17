package com.mtime.beans;

import com.mtime.base.bean.MBaseBean;

public class QRBean extends MBaseBean {

    // http://wiki.inc-mtime.com/pages/viewpage.action?pageId=3277093

    private int        status;      // 状态值：1 表示成功、4出票必需登陆、5 参数不能为空、6 Url格式不正确、7 取票机正被使用，请稍后再试、8 查找到XX笔订单，请在取票机屏幕确认并取票、9 未找到可用订单。
    private String     msg;         // 状态信息 1 时 不返回
    private int        scanType;    // 内容类型，1 表示出票、2跳转客户端页面

    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public String getMsg() {
        return msg;
    }
    
    public void setMsg(String msg) {
        this.msg = msg;
    }
    
    public int getScanType() {
        return scanType;
    }
    
    public void setScanType(int scanType) {
        this.scanType = scanType;
    }
}
