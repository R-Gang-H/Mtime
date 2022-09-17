package com.mtime.bussiness.splash.api;

import java.util.HashMap;
import java.util.Map;

import com.kotlin.android.mtime.ktx.GlobalDimensionExt;
import com.mtime.base.network.NetworkManager;
import com.mtime.bussiness.common.api.CommonApi;
import com.mtime.bussiness.common.bean.CommonAdListBean;
import com.mtime.bussiness.splash.bean.SplashStartLoad;
import com.mtime.network.ConstantUrl;

/**
 * Created by CSY on 2018/4/27.
 * <>
 */
public class SplashApi extends CommonApi {
    @Override
    protected String host() {
        return null;
    }


    @Override
    public void cancel(Object tag) {
        super.cancel(tag);
    }

    public void cancel() {
        cancel(this);
    }
    /**
     * 启动加载数据
     *
     * @param isUpgrade  是否升级
     * @param networkListener
     */
    public void startUpload(boolean isUpgrade, NetworkManager.NetworkListener<SplashStartLoad> networkListener) {
        Map<String, String> params = new HashMap<>();
        params.put("isUpgrade", String.valueOf(isUpgrade));
        params.put("locationId", GlobalDimensionExt.INSTANCE.getCurrentCityId());

        get(this, ConstantUrl.GET_APP_START_REQUEST, params, networkListener);

    }

    /**
     * 预先获取最近一个月的前2条广告，用于app端提前加载广告资源
     * @param cityId 位置id
     * @param networkListener
     */
    public void getSplashAdInfo(String cityId, NetworkManager.NetworkListener<CommonAdListBean> networkListener) {
        getAdInfo(cityId, CommonAdListBean.AD_POSITION_CODE_STARTUP_FULLSCREEN, networkListener);
    }
}
