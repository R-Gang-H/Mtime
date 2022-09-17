package com.mtime.bussiness.splash.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

/**
 *
 */
public class SplashStartLoad extends MBaseBean {
    private SplashEntryPointBean entry;

    private String imageProxy;
    private String imageUploadUrl;
    private String mallDomain;
    private String newPeopleGiftImage;
    private List<SplashBottomIcons> bottomIconList;
    //是否强制填写性别
    private boolean isEditGender;
    // 应该是添加到这个组下才对
    private String allowHost;
    private boolean isCheckHost;
    private SplashLoadingIconBean loadingIcon;
    private List<PullRefreshFilmWord> movieAdvList;
    private SplashUpgradeBean record; //记录设备信息
    private SplashSeatsIconList seatIcon;
    private String loginText;
    private String registerServiceUrl; //注册服务条款url
    private String findPasswordText;
    private String hwpassAppId;//华为钱包注册的应用Id
    private String hwpassTypeIdentifier;//华为钱包PassTypeId
    private long feedbackPostIdAndroid = 0L;//【意见反馈】模块用的群组帖子ID
    private String authTemplateImg;//身份认证公函模板（图片)地址

    public String getAuthTemplateImg() {
        return authTemplateImg;
    }

    public void setAuthTemplateImg(String authTemplateImg) {
        this.authTemplateImg = authTemplateImg;
    }

    public String getHwpassTypeIdentifier() {
        return hwpassTypeIdentifier;
    }

    public void setHwpassTypeIdentifier(String hwpassTypeIdentifier) {
        this.hwpassTypeIdentifier = hwpassTypeIdentifier;
    }

    public String getHwpassAppId() {
        return hwpassAppId;
    }

    public void setHwpassAppId(String hwpassAppId) {
        this.hwpassAppId = hwpassAppId;
    }

    public List<SplashBottomIcons> getBottomIconList() {
        return bottomIconList;
    }

    public void setBottomIconList(List<SplashBottomIcons> bottomIconList) {
        this.bottomIconList = bottomIconList;
    }

    public List<PullRefreshFilmWord> getMovieAdvlist() {
        return movieAdvList;
    }

    public void setMovieAdvlist(List<PullRefreshFilmWord> movieAdvlist) {
        this.movieAdvList = movieAdvlist;
    }

    public SplashUpgradeBean getRecord() {
        return record;
    }

    public void setRecord(SplashUpgradeBean record) {
        this.record = record;
    }

    public SplashSeatsIconList getSeatIcon() {
        return seatIcon;
    }

    public void setSeatIcon(SplashSeatsIconList seatIcon) {
        this.seatIcon = seatIcon;
    }

    public String getAllowHost() {
        return allowHost;
    }

    public void setAllowHost(String allowHost) {
        this.allowHost = allowHost;
    }

    public boolean isCheckHost() {
        return isCheckHost;
    }

    public void setIsCheckHost(boolean isCheckHost) {
        this.isCheckHost = isCheckHost;
    }

    public String getImageProxy() {
        if (imageProxy == null) {
            return "";
        } else {
            return imageProxy;
        }
    }

    public void setImageProxy(String imageProxy) {
        this.imageProxy = imageProxy;
    }

    public String getMallDomain() {
        return mallDomain;
    }

    public void setMallDomain(String mallDomain) {
        this.mallDomain = mallDomain;
    }

    public String getNewPeopleGiftImage() {
        return newPeopleGiftImage;
    }

    public void setNewPeopleGiftImage(String newPeopleGiftImage) {
        this.newPeopleGiftImage = newPeopleGiftImage;
    }

    public boolean getIsEditGender() {
        return isEditGender;
    }

    public void setIsEditGender(boolean isEditGender) {
        this.isEditGender = isEditGender;
    }

    public SplashLoadingIconBean getLoadingIcon() {
        return loadingIcon;
    }

    public void setLoadingIcon(SplashLoadingIconBean loadingIcon) {
        this.loadingIcon = loadingIcon;
    }

    public SplashEntryPointBean getEntry() {
        return entry;
    }

    public void setEntry(SplashEntryPointBean entry) {
        this.entry = entry;
    }

    public String getLoginText() {
        return loginText;
    }

    public void setLoginText(String loginText) {
        this.loginText = loginText;
    }

    public boolean isEditGender() {
        return isEditGender;
    }

    public void setEditGender(boolean editGender) {
        isEditGender = editGender;
    }

    public void setCheckHost(boolean checkHost) {
        isCheckHost = checkHost;
    }

    public String getRegisterServiceUrl() {
        return registerServiceUrl;
    }

    public void setRegisterServiceUrl(String registerServiceUrl) {
        this.registerServiceUrl = registerServiceUrl;
    }

    public String getFindPasswordText() {
        if (null == findPasswordText) {
            findPasswordText = "";
        }
        return findPasswordText;
    }

    public void setFindPasswordText(String findPasswordText) {
        this.findPasswordText = findPasswordText;
    }

    public String getImageUploadUrl() {
        return imageUploadUrl;
    }

    public void setImageUploadUrl(String imageUploadUrl) {
        this.imageUploadUrl = imageUploadUrl;
    }

    public long getFeedbackPostIdAndroid() {
        return feedbackPostIdAndroid;
    }

    public void setFeedbackPostIdAndroid(long feedbackPostIdAndroid) {
        this.feedbackPostIdAndroid = feedbackPostIdAndroid;
    }
}
