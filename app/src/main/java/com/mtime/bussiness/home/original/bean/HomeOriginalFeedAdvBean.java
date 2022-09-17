package com.mtime.bussiness.home.original.bean;

import com.helen.obfuscator.IObfuscateKeepAll;

import java.io.Serializable;

/**
 * Created by ZhouSuQiang on 2017/11/22.
 * 首页-原创-feed流中的广告数据实体
 */

public class HomeOriginalFeedAdvBean implements IObfuscateKeepAll, Serializable {
    public String applinkData;
    public String brandName;
    public String positionCode; //广告位Code
    public long positionType; //广告位置类型

    public String getApplinkData() {
        return applinkData;
    }

    public void setApplinkData(String applinkData) {
        this.applinkData = applinkData;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
    }

    public long getPositionType() {
        return positionType;
    }

    public void setPositionType(long positionType) {
        this.positionType = positionType;
    }
}
