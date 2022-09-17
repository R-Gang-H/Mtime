package com.mtime.bussiness.ticket.movie.boxoffice.api;

import com.mtime.base.network.BaseApi;
import com.mtime.base.network.NetworkManager;
import com.mtime.bussiness.home.api.HomeApiConstant;
import com.mtime.bussiness.ticket.movie.boxoffice.bean.HomeBoxOfficeMainBean;

/**
 * Created by wangdaban on 2017/11/17.
 */

public class HomeBoxOfficeMainApi extends BaseApi implements HomeApiConstant{

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
     * 加载首页-票房 标签数据
     *
     * @param listener 回调
     */
    public void loadTabList(NetworkManager.NetworkListener<HomeBoxOfficeMainBean> listener) {
        get(this, API_HOME_BOXOFFICE_TABLIST, null, listener);
    }
}
