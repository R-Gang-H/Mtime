package com.mtime.bussiness.mine.login.bean;

public class LoginBean extends LoginBaseItem {
    private String codeId; //验证码编号
    private String codeUrl; // 验证图片地址

    public String getCodeId() {
        return codeId;
    }

    public void setCodeId(String codeId) {
        this.codeId = codeId;
    }

    public String getCodeUrl() {
        return codeUrl;
    }

    public void setCodeUrl(String codeUrl) {
        this.codeUrl = codeUrl;
    }

}
