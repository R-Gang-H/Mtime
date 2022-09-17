// JSON Java Class Generator
// Written by Bruce Bao
// Used for API: 
package com.mtime.bussiness.mine.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

public class MemberCardBean  extends MBaseBean
{
    private boolean          success;
    private String           error;
    private List<MemberList> memberList;

    public boolean getSuccess()
    {
        return success;
    }

    public void setSuccess(boolean success)
    {
        this.success = success;
    }

    public String getError()
    {
        return error;
    }

    public void setError(String error)
    {
        this.error = error;
    }

    public List<MemberList> getMemberList()
    {
        return memberList;
    }

    public void setMemberList(List<MemberList> memberList)
    {
        this.memberList = memberList;
    }
}
