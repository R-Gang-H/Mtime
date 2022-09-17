package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;

/**
 * 特色
 *
 * @author ye
 */
public class CinemaViewFeature extends MBaseBean implements Serializable {
    private static final long serialVersionUID = -3519987546478163869L;

    private int has3D = 0;
    private int hasIMAX = 0;
    private int hasVIP = 0;
    private int hasGame = 0;
    private int hasPark = 0;
    private int hasFood = 0;
    private int hasLeisure = 0;
    private int has4D = 0;
    private int hasCardPay = 0;
    private int hasServiceTicket = 0;
    private int hasWifi = 0;
    private int hasLoveseat = 0;
    private int hasSphereX = 0;
    private int hasScreenX = 0;

    private String feature3DContent;
    private String featureIMAXContent;
    private String featureVIPContent;
    private String featureGameContent;
    private String featureParkContent;
    private String featureFoodContent;
    private String featureLeisureContent;
    private String feature4DContent;
    private String cardPayContent;
    private String serviceTicketContent;
    private String wifiContent;
    private String loveseatContent;
    private String featureSphereXContent;
    private String featureScreenXContent;

    public int getHas3D() {
        return has3D;
    }

    public void setHas3D(final int has3d) {
        has3D = has3d;
    }

    public int getHasIMAX() {
        return hasIMAX;
    }

    public void setHasIMAX(final int hasIMAX) {
        this.hasIMAX = hasIMAX;
    }

    public int getHasVIP() {
        return hasVIP;
    }

    public void setHasVIP(final int hasVIP) {
        this.hasVIP = hasVIP;
    }

    public int getHasGame() {
        return hasGame;
    }

    public void setHasGame(final int hasGame) {
        this.hasGame = hasGame;
    }

    public int getHasPark() {
        return hasPark;
    }

    public void setHasPark(final int hasPark) {
        this.hasPark = hasPark;
    }

    public int getHasFood() {
        return hasFood;
    }

    public void setHasFood(final int hasFood) {
        this.hasFood = hasFood;
    }

    public int getHasLeisure() {
        return hasLeisure;
    }

    public void setHasLeisure(final int hasLeisure) {
        this.hasLeisure = hasLeisure;
    }

    public int getHas4D() {
        return has4D;
    }

    public void setHas4D(final int has4d) {
        has4D = has4d;
    }

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

    public String getFeature3DContent() {
        return feature3DContent;
    }

    public void setFeature3DContent(final String feature3dContent) {
        feature3DContent = feature3dContent;
    }

    public String getFeatureIMAXContent() {
        return featureIMAXContent;
    }

    public void setFeatureIMAXContent(final String featureIMAXContent) {
        this.featureIMAXContent = featureIMAXContent;
    }

    public String getFeatureVIPContent() {
        return featureVIPContent;
    }

    public void setFeatureVIPContent(final String featureVIPContent) {
        this.featureVIPContent = featureVIPContent;
    }

    public String getFeatureGameContent() {
        return featureGameContent;
    }

    public void setFeatureGameContent(final String featureGameContent) {
        this.featureGameContent = featureGameContent;
    }

    public String getFeatureParkContent() {
        return featureParkContent;
    }

    public void setFeatureParkContent(final String featureParkContent) {
        this.featureParkContent = featureParkContent;
    }

    public String getFeatureFoodContent() {
        return featureFoodContent;
    }

    public void setFeatureFoodContent(final String featureFoodContent) {
        this.featureFoodContent = featureFoodContent;
    }

    public String getFeatureLeisureContent() {
        return featureLeisureContent;
    }

    public void setFeatureLeisureContent(final String featureLeisureContent) {
        this.featureLeisureContent = featureLeisureContent;
    }

    public String getFeature4DContent() {
        return feature4DContent;
    }

    public void setFeature4DContent(final String feature4dContent) {
        feature4DContent = feature4dContent;
    }

    public int getHasCardPay() {
        return hasCardPay;
    }

    public void setHasCardPay(int hasCardPay) {
        this.hasCardPay = hasCardPay;
    }

    public int getHasServiceTicket() {
        return hasServiceTicket;
    }

    public void setHasServiceTicket(int hasServiceTicket) {
        this.hasServiceTicket = hasServiceTicket;
    }

    public int getHasWifi() {
        return hasWifi;
    }

    public void setHasWifi(int hasWifi) {
        this.hasWifi = hasWifi;
    }

    public int getHasLoveseat() {
        return hasLoveseat;
    }

    public void setHasLoveseat(int hasLoveseat) {
        this.hasLoveseat = hasLoveseat;
    }

    public String getCardPayContent() {
        return cardPayContent;
    }

    public void setCardPayContent(String cardPayContent) {
        this.cardPayContent = cardPayContent;
    }

    public String getServiceTicketContent() {
        return serviceTicketContent;
    }

    public void setServiceTicketContent(String serviceTicketContent) {
        this.serviceTicketContent = serviceTicketContent;
    }

    public String getWifiContent() {
        return wifiContent;
    }

    public void setWifiContent(String wifiContent) {
        this.wifiContent = wifiContent;
    }

    public String getLoveseatContent() {
        return loveseatContent;
    }

    public void setLoveseatContent(String loveseatContent) {
        this.loveseatContent = loveseatContent;
    }

    public String getFeatureSphereXContent() {
        return featureSphereXContent;
    }

    public void setFeatureSphereXContent(String featureSphereXContent) {
        this.featureSphereXContent = featureSphereXContent;
    }

    public String getFeatureScreenXContent() {
        return featureScreenXContent;
    }

    public void setFeatureScreenXContent(String featureScreenXContent) {
        this.featureScreenXContent = featureScreenXContent;
    }
}
