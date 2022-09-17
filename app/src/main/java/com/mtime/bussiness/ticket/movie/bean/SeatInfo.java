package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * 座位实体，用于SeatManager
 */
public class SeatInfo extends MBaseBean
{
    private String    seatId; // 座位唯一标志,用于购票
    private int    status; // 枚举值见下
    private int    type;   // 枚举值见下
    private int    x;      // x坐标,用于绘座位图
    private int    y;      // y坐标,用于绘座位图
    private String seatNum;
    private String seatName;
    private ImagePiece selectImage;
    
    public String getSeatName() {
        return seatName;
    }

    public void setSeatName(String seatName) {
        this.seatName = seatName;
    }

    public String getSeatNum()
    {
        if (seatNum == null)
        {
            return "";
        }
        return seatNum;
    }

    public void setSeatNum(String seatNum)
    {
        this.seatNum = seatNum;
    }

    public String getSeatId() {
        return seatId;
    }

    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(final int status)
    {
        this.status = status;
    }

    public int getType()
    {
        return type;
    }

    public void setType(final int type)
    {
        this.type = type;
    }

    public int getX()
    {
        return x;
    }

    public void setX(final int x)
    {
        this.x = x;
    }

    public int getY()
    {
        return y;
    }

    public void setY(final int y)
    {
        this.y = y;
    }

    public ImagePiece getSelectImage() {
        return selectImage;
    }

    public void setSelectImage(ImagePiece selectImageIndex) {
        this.selectImage = selectImageIndex;
    }
}
