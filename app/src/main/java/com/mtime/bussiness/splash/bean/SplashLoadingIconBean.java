package com.mtime.bussiness.splash.bean;

import com.mtime.base.bean.MBaseBean;

public class SplashLoadingIconBean extends MBaseBean {
    private String loadIcon;
    private String loadFailImg;
    private String locationFailImg;

    public String getLoadIcon() {
        if (loadIcon == null){
            return "";
        }
        return loadIcon;
    }

    public void setLoadIcon(String loadIcon) {
        this.loadIcon = loadIcon;
    }

    public String getLoadFailImg() {
        if (loadFailImg == null){
            return "";
        }
        return loadFailImg;
    }

    public void setLoadFailImg(String loadFailImg) {
        this.loadFailImg = loadFailImg;
    }

    public String getLocationFailImg() {
        if (locationFailImg == null){
            return "";
        }
        return locationFailImg;
    }

    public void setLocationFailImg(String locationFailImg) {
        this.locationFailImg = locationFailImg;
    }
}
