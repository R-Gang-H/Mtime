package com.mtime.base.location;

import android.content.Context;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mtime on 2017/11/2.
 */

public class LocationManager {
    
    private static LocationManager instance;
    private final ILocationProvider mLocationProvider;
    protected ILocationInterceptor mInterceptor = null;
    /**
     * 获取到的定位信息，放在这个类里，内存缓存
     */
    public LocationInfo mLocationInfo;
    
    private final List<OnLocationCallback> mCallbacks = new ArrayList<>();
    private boolean mIsLocationing = false; //标记当前是否处于正在定位中的状态
    
    private LocationManager() {
        mLocationProvider = new BaiduLocationProvider();
    }
    
    public static LocationManager getInstance() {
        if (null == instance) {
            synchronized (LocationManager.class) {
                if (null == instance) {
                    instance = new LocationManager();
                }
            }
        }
        return instance;
    }
    
    public void registerInterceptor(ILocationInterceptor interceptor) {
        mInterceptor = interceptor;
    }
    
    public void unregisterInterceptor(ILocationInterceptor interceptor) {
        mInterceptor = interceptor;
    }
    
    public void postLocation(Context context, final OnLocationCallback onLocationCallback) {
        postLocation(context, null, onLocationCallback);
    }
    
    public synchronized void postLocation(Context context, LocationOption option, final OnLocationCallback onLocationCallback) {
        if(null != onLocationCallback) {
            onLocationCallback.onStartLocation();
            mCallbacks.add(onLocationCallback);
        }
    
        if(mIsLocationing)
            return; //当前正在定位，所以只需记录callback无需继续执行
        
        mLocationProvider.initLocation(context.getApplicationContext(), null);
        if (option == null)
            option = new LocationOption();
        
        final boolean needStartLocation = option.isRefreshLocation() || mLocationInfo == null;
        final boolean isInterceptor = null != mInterceptor && option.isInterceptor();
        
        if (needStartLocation) {
            mIsLocationing = true;
            
            mLocationProvider.startLocation(new ILocationProvider.OnLocationProviderListener() {
                @Override
                public void onProviderLocationSuccess(LocationInfo locationInfo) {
                    if(isInterceptor) {
                        mInterceptor.intercept(locationInfo, new ILocationInterceptor.OnLocationInterceptorListener() {
                            @Override
                            public void onInterceptResult(LocationInfo locationInfo) {
                                mIsLocationing = false;
                                mLocationInfo = locationInfo.clone();
                                dipatchOnLocationSuccess(mLocationInfo);
                            }
                        });
                    } else {
                        mIsLocationing = false;
                        if (null != mLocationInfo) {
                            // 刷新经纬度和基本的城市信息和位置描述信息
                            mLocationInfo.locationDescribe = locationInfo.locationDescribe;
                            mLocationInfo.addr = locationInfo.addr;
                            mLocationInfo.country = locationInfo.country;
                            mLocationInfo.district = locationInfo.district;
                            mLocationInfo.province = locationInfo.province;
                            mLocationInfo.street = locationInfo.street;
                            mLocationInfo.setLatitude(locationInfo.getLatitude());
                            mLocationInfo.setLongitude(locationInfo.getLongitude());
                        }
                        dipatchOnLocationSuccess(null != mLocationInfo ? mLocationInfo : locationInfo);
                    }
                }
                
                @Override
                public void onProviderException(LocationException locationException) {
                    mIsLocationing = false;
                    dipatchOnLocationFailure(locationException);
                }
            });
            
            return;
        }
        
        if (mLocationInfo != null) {
            dipatchOnLocationSuccess(mLocationInfo);
        }
    }

    public synchronized void updateCachedUserCityInfo(String cityId, String cityName) {
        if (null != mLocationInfo) {
            mLocationInfo.setCityId(cityId);
            mLocationInfo.setCityName(cityName);
        }
    }
    
    private synchronized void dipatchOnLocationSuccess(LocationInfo locationInfo) {
        for(OnLocationCallback callback : mCallbacks) {
            if(null != callback) {
                callback.onLocationSuccess(locationInfo);
            }
        }
        mCallbacks.clear();
    }
    
    private synchronized void dipatchOnLocationFailure(LocationException e) {
        for(OnLocationCallback callback : mCallbacks) {
            if(null != callback) {
                callback.onLocationFailure(e);
            }
        }
        mCallbacks.clear();
    }
    
    public void stopLocation() {
        mLocationProvider.stop();
    }

    public LocationInfo getLocationInfo() {
        return mLocationInfo;
    }
}
