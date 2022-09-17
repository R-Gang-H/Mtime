package com.mtime.bussiness.main.maindialog.bean;

import androidx.annotation.IntDef;

import com.helen.obfuscator.IObfuscateKeepAll;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class DialogDataBean<T> implements IObfuscateKeepAll {

    public static final int TYPE_OF_CITY_CHANGE = 0x0; //切换城市
    public static final int TYPE_OF_RICH_AD = 0x1; //富媒体广告
    public static final int TYPE_OF_NEW_USER_GIFT = 0x2; //注册礼包
    public static final int TYPE_OF_DIALOG_AD = 0x3; //弹窗广告
    public static final int KEY_OF_0X4 = 0x4; //每日佳片
    public static final int TYPE_OF_UNUSED_TICKET = 0x5; //在线选座
    public static final int TYPE_OF_UPDATE_APP = 0x6; //升级
    public static final int TYPE_OF_DAILY_RECMD = 0x7; // 每日推荐
    public static final int KEY_OF_0X8 = 0x8; //Mtime服务条款与隐私政策 弹框

    @IntDef({
            TYPE_OF_CITY_CHANGE,
            TYPE_OF_RICH_AD,
            TYPE_OF_NEW_USER_GIFT,
            TYPE_OF_DIALOG_AD,
            KEY_OF_0X4,
            TYPE_OF_UNUSED_TICKET,
            TYPE_OF_UPDATE_APP,
            TYPE_OF_DAILY_RECMD,
            KEY_OF_0X8
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    public static <R> DialogDataBean<R> get(@Type int type, boolean isNextShow, boolean isNewUserSave, boolean isAppRuningShow, R data) {
        DialogDataBean<R> bean = new DialogDataBean<>();
        bean.type = type;
        bean.isNextShow = isNextShow;
        bean.isNewUserSave = isNewUserSave;
        bean.isAppRuningShow = isAppRuningShow;
        bean.data = data;
        return bean;
    }

    @Type
    public int type; //数据类型
    public boolean isNextShow; //是否允许显示下个弹框
    public boolean isNewUserSave; //是否新用户保护
    public boolean isAppRuningShow; //是否允许App运行中弹出
    public T data; //具体的数据实体

}
