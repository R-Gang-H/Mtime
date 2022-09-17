package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by guangshun on 16-6-27.
 * 点击投票后接口返回的Bean
 */
public class SubmitVoteBean extends MBaseBean {
    private int code;//系统状态，1表示成功，其他表示错误
    private String showMsg; //"预留字段，有需要给用户返回的信息，一般不设置值",
    private String msg;
    private BzCode data;

    public void setCode(int code) {
        this.code = code;
    }

    public void setShowMsg(String showMsg) {
        this.showMsg = showMsg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getShowMsg() {
        return showMsg;
    }

    public String getMsg() {
        return msg;
    }

    public void setData(BzCode data) {
        this.data = data;
    }

    public BzCode getData() {
        return data;
    }

    public class BzCode {
        private int bzCode;//1表示成功，2 必须登陆、3 重复投票、0 其他错误
        private String msg;

        public void setBbzCode(int bzCode) {
            this.bzCode = bzCode;
        }

        public int getBzCode() {
            return bzCode;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getMsg() {
            return msg;
        }
    }
}
