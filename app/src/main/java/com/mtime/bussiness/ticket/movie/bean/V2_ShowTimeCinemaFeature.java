package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;

public class V2_ShowTimeCinemaFeature extends MBaseBean implements Serializable {
    private static final long serialVersionUID = -3519987546478163869L;
    
    private int hasIMAX = 0;
    private int hasPark = 0;// 是否有停车场
    private int hasWifi = 0;// 是否有wifi
    private int hasFeature4K = 0;// 是否4K
    private int has2D = 0;// 在影院是否上的2D
    private int has3D = 0;// 在影院是否上的3D，包括3D、DMAX3D、IMAX3D
    private int isDMAX = 0;// 在影院是否上的DMAX 中国巨幕
    private int hasFeature4D = 0;
    private int hasFeatureDolby = 0;
    private int hasFeatureHuge = 0;
    private int hasLoveseat = 0;
    private int hasServiceTicket = 0;
    private int hasVIP = 0;
    private int hasSphereX = 0;

    public int getHasSphereX() {
        return hasSphereX;
    }

    public void setHasSphereX(int hasSphereX) {
        this.hasSphereX = hasSphereX;
    }

    public int getHasScreenX() {
        return hasScreenX;
    }

    public void setHasScreenX(int hasScreenX) {
        this.hasScreenX = hasScreenX;
    }

    private int hasScreenX = 0;

    public int getHasIMAX() {
        return hasIMAX;
    }
    
    public void setHasIMAX(final int hasIMAX) {
        this.hasIMAX = hasIMAX;
    }
    
    public int getHasPark() {
        return hasPark;
    }
    
    public void setHasPark(final int hasPark) {
        this.hasPark = hasPark;
    }
    
    public int getHasWifi() {
        return hasWifi;
    }
    
    public void setHasWifi(final int hasWifi) {
        this.hasWifi = hasWifi;
    }
    
    public int getHasFeature4K() {
        return hasFeature4K;
    }
    
    public void setHasFeature4K(final int hasFeature4K) {
        this.hasFeature4K = hasFeature4K;
    }
    
    public int getHas2D() {
        return has2D;
    }
    
    public void setHas2D(final int has2D) {
        this.has2D = has2D;
    }
    
    public int getHas3D() {
        return has3D;
    }
    
    public void setHas3D(final int has3D) {
        this.has3D = has3D;
    }
    
    public int getIsDMAX() {
        return isDMAX;
    }
    
    public void setIsDMAX(final int isDMAX) {
        this.isDMAX = isDMAX;
    }

    public int getHasFeature4D() {
        return hasFeature4D;
    }

    public void setHasFeature4D(int hasFeature4D) {
        this.hasFeature4D = hasFeature4D;
    }

    public int getHasFeatureDolby() {
        return hasFeatureDolby;
    }

    public void setHasFeatureDolby(int hasFeatureDolby) {
        this.hasFeatureDolby = hasFeatureDolby;
    }

    public int getHasFeatureHuge() {
        return hasFeatureHuge;
    }

    public void setHasFeatureHuge(int hasFeatureHuge) {
        this.hasFeatureHuge = hasFeatureHuge;
    }

    public int getHasLoveseat() {
        return hasLoveseat;
    }

    public void setHasLoveseat(int hasLoveseat) {
        this.hasLoveseat = hasLoveseat;
    }

    public int getHasServiceTicket() {
        return hasServiceTicket;
    }

    public void setHasServiceTicket(int hasServiceTicket) {
        this.hasServiceTicket = hasServiceTicket;
    }

    public int getHasVIP() {
        return hasVIP;
    }

    public void setHasVIP(int hasVIP) {
        this.hasVIP = hasVIP;
    }
}
