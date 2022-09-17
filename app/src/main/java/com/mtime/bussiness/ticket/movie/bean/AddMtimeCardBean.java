package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

public class AddMtimeCardBean extends MBaseBean
{
    private boolean success;
    private String msg;
    private int status;
    private String codeId;
    private String codeUrl;
    private String token;
    private CardListBean cardInfo;
    
    public String getCodeId()
    {
        return codeId;
    }

    public void setCodeId(String codeId)
    {
        this.codeId = codeId;
    }

    public String getCodeUrl()
    {
        return codeUrl;
    }

    public void setCodeUrl(String codeUrl)
    {
        this.codeUrl = codeUrl;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public CardListBean getCardInfo()
    {
        return cardInfo;
    }

    public void setCardInfo(CardListBean cardInfo)
    {
        this.cardInfo = cardInfo;
    }

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
}