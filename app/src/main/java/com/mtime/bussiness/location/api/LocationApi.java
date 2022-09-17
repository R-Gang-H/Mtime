package com.mtime.bussiness.location.api;

import com.mtime.base.network.BaseApi;
import com.mtime.base.network.NetworkManager;
import com.mtime.bussiness.location.bean.LocationRawBean;
import com.mtime.network.ConstantUrl;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ZhouSuQiang on 2017/11/6.
 */

public class LocationApi extends BaseApi {
    @Override
    protected String host() {
        return null;
    }
    
    public void cancel() {
        NetworkManager.getInstance().cancel(this);
    }
    
    public void loadCityInfo(double longitude, double latitude, String city, NetworkManager.NetworkListener<LocationRawBean> listener) {
        Map<String, String> params = new HashMap<>();
        params.put("longitude", String.valueOf(longitude));
        params.put("latitude", String.valueOf(latitude));
        params.put("cityName", String.valueOf(city));
        post(this, ConstantUrl.GET_CELLCHINA_LOCATIONS, params, listener);
    }
}
