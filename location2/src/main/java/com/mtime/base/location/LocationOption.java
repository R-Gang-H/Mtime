package com.mtime.base.location;

/**
 * Created by mtime on 2017/11/3.
 */

public class LocationOption {

    private boolean refreshLocation;
    private boolean isInterceptor = true;

    public boolean isRefreshLocation() {
        return refreshLocation;
    }

    public LocationOption setRefreshLocation(boolean refreshLocation) {
        this.refreshLocation = refreshLocation;
        return this;
    }

    public boolean isInterceptor() {
        return isInterceptor;
    }

    public void setInterceptor(boolean interceptor) {
        isInterceptor = interceptor;
    }
}
