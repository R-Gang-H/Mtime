package com.mtime.account;

import android.text.TextUtils;

import com.kotlin.android.retrofit.cookie.CookieManager;
import com.mtime.frame.App;
import com.google.gson.Gson;
import com.mtime.bussiness.mine.bean.AccountDetailBean;
import com.mtime.bussiness.mine.login.bean.SwitchLoginBean;
import com.mtime.bussiness.mine.login.bean.UserItem;
import com.mtime.statistic.large.StatisticManager;

/**
 * 管理用户信息
 */
public class AccountManager {

    private static AccountDetailBean sAccountDetailBean = null;

    /**
     * 获取用户ID
     */
    public static long getUserId() {
        return sAccountDetailBean != null ? sAccountDetailBean.getUserId() : 0;
    }

    /**
     * 获取用户昵称
     */
    public static String getUserNickName() {
        return sAccountDetailBean != null ? sAccountDetailBean.getNickname() : "";
    }

    /**
     * 获取用户的头像
     */
    public static String getUserAvatar() {
        return sAccountDetailBean != null ? sAccountDetailBean.getHeadPic() : null;
    }

    /**
     * 获取用户的性别
     */
    public static int getUserSex() {
        return sAccountDetailBean != null ? sAccountDetailBean.getSex() : -1;
    }

    /**
     * 是否未绑定手机号,true 已经绑定
     */
    public static boolean getHasBindedMobile() {
        return sAccountDetailBean != null && !TextUtils.isEmpty(sAccountDetailBean.getBindMobile());
    }

    /**
     * 用户是否登录
     */
    public static boolean isLogin() {
//        return sAccountDetailBean != null && null != NetworkManager.getInstance().getCookieByName("_mi_");
        return sAccountDetailBean != null && CookieManager.Companion.getInstance().isCookieExistByName("_mi_");
    }

    /**
     * 初始化用户账号信息
     */
    public static void initAccountInfo() {
        updateAccountInfo(getAccountInfo());
    }

    /**
     * 更新用户账号信息
     */
    public static void updateAccountInfo(AccountDetailBean bean) {
        sAccountDetailBean = bean;
        //保存用户账号信息到本地
        saveAccountInfo(sAccountDetailBean);
        updateOther();
    }

    private static void updateOther() {
        //更新统计的uid
        StatisticManager.getInstance().setUid(null != sAccountDetailBean ? String.valueOf(sAccountDetailBean.getUserId()) : "");
    }

    /**
     * 更新用户账号信息
     * 之前不知道为什么又弄出来个UserItem实体，后期看看能不能统一成AccountDetailBean
     */
    public static void updateAccountInfo(final UserItem user, final boolean hasPassword) {
        if (sAccountDetailBean == null) {
            sAccountDetailBean = new AccountDetailBean();
        }
        sAccountDetailBean.setHasPassword(hasPassword);
        sAccountDetailBean.setUserId(user.getUserId());
        sAccountDetailBean.setHeadPic(user.getHeadImg());
        sAccountDetailBean.setBindMobile(user.getMobile());
        sAccountDetailBean.setSex(user.getGender());
        sAccountDetailBean.setNickname(user.getNickname());

        updateAccountInfo(sAccountDetailBean);
    }

    /**
     * 更新用户账号信息
     */
//    public static void updateAccountInfo(MallPayUserBean userBean) {
//        if (sAccountDetailBean == null) {
//            sAccountDetailBean = new AccountDetailBean();
//        }
//        sAccountDetailBean.setUserId(userBean.getUserId());
//        sAccountDetailBean.setNickname(userBean.getNickname());
//        sAccountDetailBean.setSex(userBean.getSex());
//        sAccountDetailBean.setHeadPic(userBean.getHeadPic());
//        sAccountDetailBean.setBalance(userBean.getBalance());
//        sAccountDetailBean.setRechargeMax(userBean.getRechargeMax());
//        sAccountDetailBean.setBindMobile(userBean.getBindMobile());
//
//        updateAccountInfo(sAccountDetailBean);
//    }

    /**
     * 更新用户账号信息
     */
    public static void updateAccountInfo(SwitchLoginBean bean) {
        if (sAccountDetailBean == null) {
            sAccountDetailBean = new AccountDetailBean();
        }
        sAccountDetailBean.setUserId(bean.getUserId());
        sAccountDetailBean.setUserLevel(bean.getUserLevel());
        sAccountDetailBean.setNickname(bean.getNickname());
        sAccountDetailBean.setHeadPic(bean.getHeadPic());
        sAccountDetailBean.setBindMobile(bean.getMobile());
        sAccountDetailBean.setSex(bean.getSex());
        sAccountDetailBean.setBalance(bean.getBalance());
        sAccountDetailBean.setBirthday(bean.getBirthday());
        sAccountDetailBean.setLocation(bean.getLocation());

        updateAccountInfo(sAccountDetailBean);
    }

    /**
     * 保存用户账号信息
     */
    public static void saveAccountInfo(AccountDetailBean bean) {
        if (null != bean) {
            String userinfo = new Gson().toJson(bean);
            App.getInstance().getPrefsManager().putString(App.getInstance().KEY_USER_INFO, userinfo);
        }
    }

    /**
     * 获得用户账号信息实体
     */
    public static AccountDetailBean getAccountInfo() {
        if (null == sAccountDetailBean) {
            String userinfo = App.getInstance().getPrefsManager().getString(App.getInstance().KEY_USER_INFO);
            if (!TextUtils.isEmpty(userinfo)) {
                sAccountDetailBean = new Gson().fromJson(userinfo, AccountDetailBean.class);
            }
        }
        return sAccountDetailBean;
    }

    /**
     * 清除本地用户账号信息
     */
    public static void removeAccountInfo() {
        sAccountDetailBean = null;
        App.getInstance().getPrefsManager().removeKey(App.getInstance().KEY_USER_INFO);
        StatisticManager.getInstance().setUid("");
    }
}
