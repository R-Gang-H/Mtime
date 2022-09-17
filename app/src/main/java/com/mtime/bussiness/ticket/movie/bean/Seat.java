package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * 座位
 */
public class Seat extends MBaseBean {
    private String     id;        // 座位id，""表示不是座位
    private int     x;         // 排位的X值。
    private int     y;         // 排位的Y值
    private String  name;      // 座位名字
    private int     type;      // 2座位类型 (座位和非座位) // 0普通座;1残疾人座;2情侣左座;3情侣右座
    private boolean status;    // 座位状态（可选和不可选）
    private String  seatNumber; // 座位号（5排7座中的7，如长度大于2，不予显示）
                                
    public Seat(String id, int x, int y, boolean status, int type) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.status = status;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }
    
    public void setX(final int x) {
        this.x = x;
    }
    
    public int getY() {
        return y;
    }
    
    public void setY(final int y) {
        this.y = y;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public int getType() {
        return type;
    }
    
    public void setType(final int type) {
        this.type = type;
    }
    
    public boolean isStatus() {
        return status;
    }
    
    public void setStatus(final boolean status) {
        this.status = status;
    }
    
    public String getSeatNumber() {
        return seatNumber;
    }
    
    public void setSeatNumber(final String seatNumber) {
        this.seatNumber = seatNumber;
    }
}
