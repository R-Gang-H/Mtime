package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

public class SeatRowNameInfo extends MBaseBean
{
    private int    rowId;
    private String rowName;

    public int getRowId()
    {
        return rowId;
    }

    public void setRowId(int rowId)
    {
        this.rowId = rowId;
    }

    public String getRowName()
    {
        if (rowName != null)
        {
            return rowName;
        }
        return "";
    }

    public void setRowName(String rowName)
    {
        this.rowName = rowName;
    }

}
