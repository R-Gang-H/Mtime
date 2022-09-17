package com.mtime.bussiness.location;

import android.Manifest;
import android.content.Context;
import android.text.TextUtils;

import com.kotlin.android.ktx.ext.KeyExtKt;
import com.kotlin.android.mtime.ktx.GlobalDimensionExt;
import com.mtime.base.location.LocationException;
import com.mtime.base.location.LocationInfo;
import com.mtime.base.location.LocationManager;
import com.mtime.base.location.LocationOption;
import com.mtime.base.location.OnLocationCallback;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.common.utils.MSharePreferenceHelper;
import com.mtime.constant.SpManager;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import java.util.List;

/**
 * Created by ZhouSuQiang on 2017/11/7.
 * LocationManager的辅助类，主要是把定位所需运行时权限包装在内
 */

public class LocationHelper {
    //    private static String cachedCityId;
//    private static String cachedCityName;
    private static MtimeLocationInterceptor sMtimeLocationInterceptor = new MtimeLocationInterceptor();

    public static String getLocationDescribe(LocationInfo locationInfo) {
        String describe = locationInfo.locationDescribe;
        if (TextUtils.isEmpty(describe)) {
            describe = locationInfo.street;
        }
        if (TextUtils.isEmpty(describe)) {
            describe = locationInfo.addr;
        }
        if (TextUtils.isEmpty(describe)) {
            describe = locationInfo.getCity();
        }
        return describe;
    }

    public static void location(Context context, OnLocationCallback onLocationCallback) {
        location(context, null, onLocationCallback);
    }

    public static void location(Context context, boolean isRefreshLocation, OnLocationCallback onLocationCallback) {
        LocationOption option = new LocationOption();
        option.setRefreshLocation(isRefreshLocation);
        location(context, option, onLocationCallback);
    }

    /**
     * 刷新当前的定位信息而不会走拦截回调
     * 从而也不会影响到用户当前正在使用的城市ID和城市名称
     * 只会刷新经纬度和基本的城市信息和位置描述信息等。
     *
     * @param context
     * @param onLocationCallback
     */
    public static void refreshLatitudeAndLongitudeInfo(Context context, OnLocationCallback onLocationCallback) {
        LocationOption option = new LocationOption();
        option.setRefreshLocation(true);
        option.setInterceptor(false);
        location(context, option, new OnLocationCallback() {

            @Override
            public void onStartLocation() {
                if (null != onLocationCallback) {
                    onLocationCallback.onStartLocation();
                }
            }

            @Override
            public void onLocationSuccess(LocationInfo locationInfo) {
                if (null != locationInfo && TextUtils.isEmpty(locationInfo.getCityId())) {
                    locationInfo.setCityId(GlobalDimensionExt.INSTANCE.getCurrentCityId());
                    locationInfo.setCityName(GlobalDimensionExt.INSTANCE.getCurrentCityName());
                    if (TextUtils.isEmpty(locationInfo.getCityId())) {
                        locationInfo.setCityId(String.valueOf(GlobalDimensionExt.CITY_ID));
                        locationInfo.setCityName(GlobalDimensionExt.CITY_NAME);
                    }
                }
                if (null != onLocationCallback) {
                    onLocationCallback.onLocationSuccess(locationInfo);
                }
            }

            @Override
            public void onLocationFailure(LocationException e) {
                if (null != onLocationCallback) {
                    onLocationCallback.onLocationFailure(e);
                }
            }
        });
    }

    private static void location(final Context context, final LocationOption option, final OnLocationCallback onLocationCallback) {
        // TODO: 2020/8/24 当本地缓存的 locationInfo不为null，或者不需要刷新定位，取缓存里的定位数据
        final LocationInfo locationInfo = LocationManager.getInstance().getLocationInfo();
        final boolean needStartLocation = null != option && option.isRefreshLocation();
        if (!needStartLocation && null != locationInfo) {
            onLocationCallback.onLocationSuccess(locationInfo);
            return;
        }

        // 定位权限如用户拒绝过则不再申请权限）
        if (MSharePreferenceHelper.get().getBooleanValue(SpManager.SP_KEY_LOCATION_PERMISSION_DENIED, false)) {
            requestLocationManager(context, option, onLocationCallback);
            return;
        }

        // 危险权限：运行时请求
        Acp.getInstance(context).request(new AcpOptions.Builder()
                        .setPermissions(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        ).build(),
                new AcpListener() {
                    @Override
                    public void onGranted() {
                        Acp.getInstance(context).onDestroy();
                        requestLocationManager(context, option, onLocationCallback);
                    }

                    @Override
                    public void onDenied(List<String> permissions) {
                        Acp.getInstance(context).onDestroy();
                        String msg = permissions.toString() + "权限拒绝";
                        MToastUtils.showShortToast(msg);
                        MSharePreferenceHelper.get().putBoolean(SpManager.SP_KEY_LOCATION_PERMISSION_DENIED, true);
                        requestLocationManager(context, option, onLocationCallback);
                    }
                });
    }

    private static void requestLocationManager(Context context, LocationOption option, OnLocationCallback onLocationCallback) {
        //注册拦截器
        LocationManager.getInstance().registerInterceptor(sMtimeLocationInterceptor);
        //开始定位
        LocationManager.getInstance().postLocation(context, option, onLocationCallback);
    }

    /**
     * 缓存用户使用的城市信息
     *
     * @param cityId   城市id
     * @param cityName 城市name
     */
    public static void cacheUserCityInfo(String cityId, String cityName) {
        GlobalDimensionExt.INSTANCE.saveCurrentCityInfo(TextUtils.isEmpty(cityId) ? "" : cityId, TextUtils.isEmpty(cityName) ? "" : cityName);
        LocationManager.getInstance().updateCachedUserCityInfo(cityId, cityName);
    }

    public static LocationInfo getDefaultLocationInfo() {
        LocationInfo locationInfo = new LocationInfo();
        locationInfo.setCityId(GlobalDimensionExt.INSTANCE.getCurrentCityId());
        locationInfo.setCityName(GlobalDimensionExt.INSTANCE.getCurrentCityName());
        locationInfo.setLongitude(0d);
        locationInfo.setLatitude(0d);
        return locationInfo;
    }

}
