package com.mtime.bussiness.mine.bean;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;

public class AccountDetailBean extends MBaseBean implements Serializable  {
    /**
     * 用户登录
     */
    private static final long serialVersionUID = 516840708041254691L;
    private String nickname;
    private String headPic;
    private double balance;
    private long rechargeMax;
    private String bindMobile;
    private String email;
    private int sex;
    private int userLevel;
    private String vipUrl;
    private int userId;
    //是否是第三方授权登录
    private boolean isAuthLogin;
    private String mobile;  //用户注册手机号
    private boolean hasPassword;
    private String birthday;
    private AccountLocationBean location;   // 居住地
    private String userLevelDesc;           // 等级描述 如：黄金会员
    private int mtimeCoin;                  // 时光币
    private boolean hasGiftPack;            // 是否有礼包
    private String memberIcon;              //会员显示文案，是否签到显示签到抽好礼，是否有礼包显示会员礼包待领取，前者显示优先级大于后者

    public String getMemberIcon() {
        return memberIcon;
    }

    public void setMemberIcon(String memberIcon) {
        this.memberIcon = memberIcon;
    }

    public boolean isModifiedSex() {
        return isModifiedSex;
    }

    public void setIsModifiedSex(boolean isModifiedSex) {
        this.isModifiedSex = isModifiedSex;
    }

    private boolean isModifiedSex;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(final String nickname) {
        this.nickname = nickname;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(final String headPic) {
        this.headPic = headPic;
    }

    public double getBalance() {
        return balance / 100;
    }

    public void setBalance(final double balance) {
        this.balance = balance;
    }

    public long getRechargeMax() {
        return rechargeMax / 100;
    }

    public void setRechargeMax(final long rechargeMax) {
        this.rechargeMax = rechargeMax;
    }

    public String getBindMobile() {
        return bindMobile;
    }

    public void setBindMobile(final String bindMobile) {
        this.bindMobile = bindMobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(final int sex) {
        this.sex = sex;
        // TODO 这里是为了防止老接口返回0代表女士的问题。
        // 1男2女3保密
        if (0 == this.sex) {
            this.sex = 2;
        }
    }

    public int getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(final int userLevel) {
        this.userLevel = userLevel;
    }

    public String getVipUrl() {
        return vipUrl;
    }

    public void setVipUrl(final String vipUrl) {
        this.vipUrl = vipUrl;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isAuthLogin (){
        return isAuthLogin;
    }

    public void setAuthLogin(boolean isAuthLogin) {
        this.isAuthLogin = isAuthLogin;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public boolean isHasPassword() {
        return hasPassword;
    }

    public void setHasPassword(boolean hasPassword) {
        this.hasPassword = hasPassword;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }


    public AccountLocationBean getLocation() {
        return location;
    }

    public void setLocation(AccountLocationBean location) {
        this.location = location;
    }

    public String getUserLevelDesc() {
        return userLevelDesc;
    }

    public void setUserLevelDesc(String userLevelDesc) {
        this.userLevelDesc = userLevelDesc;
    }

    public int getMtimeCoin() {
        return mtimeCoin;
    }

    public void setMtimeCoin(int mtimeCoin) {
        this.mtimeCoin = mtimeCoin;
    }

    public boolean isHasGiftPack() {
        return hasGiftPack;
    }

    public void setHasGiftPack(boolean hasGiftPack) {
        this.hasGiftPack = hasGiftPack;
    }
}
