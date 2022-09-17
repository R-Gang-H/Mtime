package com.mtime.statistic.large;

import com.mtime.base.network.BaseApi;
import com.mtime.base.network.NetworkManager;
import com.mtime.network.ConstantUrl;

/**
 * Created by LiJiaZhi on 2017/9/2.
 * <p>
 * api
 */
/**
 * logx统计功能废弃
 */
@Deprecated
public class StatisticApi extends BaseApi {
    @Override
    protected String host() {
        return null;
    }

    @Override
    public void cancel(Object tag) {
        super.cancel(tag);
    }

    public void cancel() {
        NetworkManager.getInstance().cancel(this);
    }

    // N条
    public void submitMulti(Object payload, NetworkManager.NetworkListener<String> listener) {
        //postJson(this, ConstantUrl.HOST_STATISTIC, payload, null, null, listener);
    }
}
