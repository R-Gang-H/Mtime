package com.mtime.beans;


import android.text.TextUtils;

import com.mtime.base.bean.MBaseBean;

public class ADDetailBean extends MBaseBean implements BeanTypeInterface {

    /**
     * "advId":111,//广告id 目前此值为0  long类型
     "positionType": 1,  //广告位置类型  目前type等于100的时候，有值
     "positionCode":"ad_startup_splash_1",  //广告位Code
     * */
    private long advId;
    private int positionType;
    private String positionCode;

    private String type;
    private String typeName;
    private boolean isHorizontalScreen; //之前goto使用，改为applink后将不再使用
    private int startDate;
    private int endDate;
    private String url;
    private String tag;
    private boolean isOpenH5; //之前goto使用，改为applink后将不再使用
    private boolean isLogo;//是否需要logo
    private String entryText;//入口文案
    private String advTag;

    public long getAdvId() {
        return advId;
    }

    public void setAdvId(long advId) {
        this.advId = advId;
    }

    public int getPositionType() {
        return positionType;
    }

    public void setPositionType(int positionType) {
        this.positionType = positionType;
    }

    public String getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public boolean getIsHorizontalScreen() {
        return isHorizontalScreen;
    }

    public void setIsHorizontalScreen(boolean isHorizontalScreen) {
        this.isHorizontalScreen = isHorizontalScreen;
    }

    public int getStartDate() {
        return startDate;
    }

    public void setStartDate(int startDate) {
        this.startDate = startDate;
    }

    public int getEndDate() {
        return endDate;
    }

    public void setEndDate(int endDate) {
        this.endDate = endDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean getIsOpenH5() {
        return isOpenH5;
    }

    public void setIsOpenH5(boolean isOpenH5) {
        this.isOpenH5 = isOpenH5;
    }

    @Override
    public int getBeanType() {
        return BeanTypeInterface.TYPE_ADV_RECOMMEND;
    }

    public boolean isHorizontalScreen() {
        return isHorizontalScreen;
    }

    public boolean isOpenH5() {
        return isOpenH5;
    }

    public boolean isLogo() {
        return isLogo;
    }

    public void setIsLogo(boolean isLogo) {
        this.isLogo = isLogo;
    }

    public String getEntryText() {
        return entryText;
    }

    public void setEntryText(String entryText) {
        this.entryText = entryText;
    }

    public boolean isEmpty() {
        return TextUtils.isEmpty(type) || TextUtils.isEmpty(url);
    }

    public String getAdvTag() {
        return advTag;
    }

    public void setAdvTag(String advTag) {
        this.advTag = advTag;
    }
}
