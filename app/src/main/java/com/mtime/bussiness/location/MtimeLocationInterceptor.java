package com.mtime.bussiness.location;

import com.kotlin.android.mtime.ktx.GlobalDimensionExt;
import com.mtime.base.location.ILocationInterceptor;
import com.mtime.base.location.LocationInfo;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.bussiness.location.api.LocationApi;
import com.mtime.bussiness.location.bean.LocationRawBean;
import com.mtime.constant.FrameConstant;
import com.mtime.network.NetworkConstant;
import com.mtime.statistic.large.StatisticManager;

import android.text.TextUtils;

/**
 * Created by ZhouSuQiang on 2017/11/6.
 */

public class MtimeLocationInterceptor implements ILocationInterceptor {
    private final LocationApi mLocationApi;
    
    public MtimeLocationInterceptor() {
        mLocationApi = new LocationApi();
    }
    
    @Override
    public void intercept(final LocationInfo locationInfo, final OnLocationInterceptorListener listener) {
        if(null == locationInfo || null == listener)
            return;
        
        //获取本地缓存的用户城市信息
        locationInfo.setCityId(GlobalDimensionExt.INSTANCE.getCurrentCityId());
        locationInfo.setCityName(GlobalDimensionExt.INSTANCE.getCurrentCityName());
    
        //设置全局常量 用于Network
        FrameConstant.UA_LOCATION_LONGITUDE = String.valueOf(locationInfo.getLongitude());
        FrameConstant.UA_LOCATION_LATITUDE = String.valueOf(locationInfo.getLatitude());
        NetworkConstant.update();

        GlobalDimensionExt.INSTANCE.saveLongitudeAndLatitude(locationInfo.getLongitude(), locationInfo.getLatitude());
        
        if(TextUtils.isEmpty(locationInfo.getLocationCityId())) {
            mLocationApi.loadCityInfo(
                    locationInfo.getLongitude(),
                    locationInfo.getLatitude(),
                    locationInfo.getCity(),
                    new NetworkManager.NetworkListener<LocationRawBean>() {
                        @Override
                        public void onSuccess(LocationRawBean result, String showMsg) {
                            if (null != result) {
                                locationInfo.setLocationCityId(result.getCityId());
                                locationInfo.setLocationCityName(result.getName());
                                if(TextUtils.isEmpty(locationInfo.getCityId())) {
                                    //第一次本地缓存用户城市信息
                                    LocationHelper.cacheUserCityInfo(result.getCityId(), result.getName());
                                    locationInfo.setCityId(result.getCityId());
                                    locationInfo.setCityName(result.getName());
                                }
                            } else {
                                String cityId = GlobalDimensionExt.INSTANCE.getCurrentCityId();
                                if (TextUtils.isEmpty(cityId))
                                    cityId = String.valueOf(GlobalDimensionExt.CITY_ID);
                                String cityName = GlobalDimensionExt.INSTANCE.getCurrentCityName();
                                if (TextUtils.isEmpty(cityName))
                                    cityName = GlobalDimensionExt.CITY_NAME;
                                locationInfo.setCityId(cityId);
                                locationInfo.setCityName(cityName);
                            }
                            listener.onInterceptResult(locationInfo);
                            // 埋点
                            StatisticManager.getInstance().setCityName(locationInfo.getCityName());
                            StatisticManager.getInstance().setCityId(locationInfo.getCityId());
                        }
                
                        @Override
                        public void onFailure(NetworkException<LocationRawBean> exception, String showMsg) {
                            String cityId = GlobalDimensionExt.INSTANCE.getCurrentCityId();
                            if (TextUtils.isEmpty(cityId))
                                cityId = String.valueOf(GlobalDimensionExt.CITY_ID);
                            String cityName = GlobalDimensionExt.INSTANCE.getCurrentCityName();
                            if (TextUtils.isEmpty(cityName))
                                cityName = GlobalDimensionExt.CITY_NAME;
                            locationInfo.setCityId(cityId);
                            locationInfo.setCityName(cityName);
                            listener.onInterceptResult(locationInfo);
                        }
                    });
            return;
        }
        listener.onInterceptResult(locationInfo);
        // 埋点
        StatisticManager.getInstance().setCityName(locationInfo.getCityName());
        StatisticManager.getInstance().setCityId(locationInfo.getCityId());
    }
}
