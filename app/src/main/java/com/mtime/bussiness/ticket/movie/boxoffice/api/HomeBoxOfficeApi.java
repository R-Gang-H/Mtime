package com.mtime.bussiness.ticket.movie.boxoffice.api;

import com.mtime.base.network.BaseApi;
import com.mtime.base.network.NetworkManager;
import com.mtime.bussiness.home.api.HomeApiConstant;
import com.mtime.bussiness.ticket.movie.boxoffice.bean.HomeBoxOfficeTabListBean;
import com.mtime.bussiness.ticket.movie.boxoffice.bean.HomeBoxOfficeTabListDetailBean;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangdaban on 2017/11/20.
 */

public class HomeBoxOfficeApi extends BaseApi implements HomeApiConstant{

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
     * 加载首页-票房 列表详细信息
     *
     * @param locationId   城市id
     * @param listener     回调
     * @param pageIndex
     * @param fixedSubBean
     */
    public void loadTabListDetail(String locationId, int pageIndex, HomeBoxOfficeTabListBean fixedSubBean, NetworkManager.NetworkListener<HomeBoxOfficeTabListDetailBean> listener) {
        Map<String, String> param = new HashMap<>(3);
        param.put("pageIndex", String.valueOf(pageIndex));
        param.put("pageSubAreaID", fixedSubBean.getPageSubAreaId());
        param.put("locationId", locationId);
        get(this, API_HOME_BOXOFFICE_TABLIST_DETAIL, param, listener);
    }

    /**
     * 指定推荐位票房列表
     * @param locationId    城市id
     * @param pageIndex     第几页
     * @param pageSubAreaID tab对应的推荐位Id
     * @param listener      回调
     */
    public void loadTabListDetail(String locationId, int pageIndex, String pageSubAreaID, NetworkManager.NetworkListener<HomeBoxOfficeTabListDetailBean> listener) {
        Map<String, String> param = new HashMap<>(3);
        param.put("pageIndex", String.valueOf(pageIndex));
        param.put("pageSubAreaID", pageSubAreaID);
        param.put("locationId", locationId);
        get(this, API_HOME_BOXOFFICE_TABLIST_DETAIL, param, listener);
    }
}
