package com.mtime.base.location;

/**
 * Created by mtime on 2017/11/2.
 */

public abstract class BaseLocationProvider implements ILocationProvider {

    protected OnLocationProviderListener mOnLocationProviderListener;

    @Override
    public void startLocation(OnLocationProviderListener onLocationProviderListener) {
        this.mOnLocationProviderListener = onLocationProviderListener;
    }

}
