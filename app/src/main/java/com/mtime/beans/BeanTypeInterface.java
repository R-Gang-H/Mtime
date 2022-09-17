package com.mtime.beans;

public interface BeanTypeInterface {
    int TYPE_UPDATE_VERSION = 1; // 更新版本
    int TYPE_ADV_RECOMMEND = 2; // 全屏幕广告
    int TYPE_NEWS = 3; // 新闻提醒
    int TYPE_OUT_DATE = 4; // 票券过期提醒
    int TYPE_UNUSED = 5;
    int TYPE_MODE_SWITCH = 6;
    int TYPE_SWITCH_CITY = 7;
    int TYPE_LOW_MODE = 8;
    int TYPE_ADV_INFO = 9;
    int TYPE_ETICKET = 10;
    int TYPE_TICKET = 11;
    int TYPE_REMIND = 12;
    int TYPE_REMIND_COUPON_TIME = 13;

    int getBeanType();
}
