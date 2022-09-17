package com.mtime.share.api;

import com.kotlin.android.app.data.entity.common.CommonShare;
import com.mtime.base.network.BaseApi;
import com.mtime.base.network.NetworkManager;
import com.mtime.network.ConstantUrl;

import java.util.HashMap;
import java.util.Map;

public class ShareApi extends BaseApi {
    @Override
    protected String host() {
        return null;
    }

    /**
     * 获取分享数据
     * shareWindow、shareDialog
     * @param type
     * @param shareId
     * @param networkListener
     */
    public void getShareInfo(String type, String shareId, String secondRelateId, NetworkManager.NetworkListener<CommonShare> networkListener) {
        Map<String, String> params = new HashMap<>();
        params.put("type", type);
        params.put("relateId", shareId);
        params.put("secondRelateId", secondRelateId);
        get(this, ConstantUrl.GET_SHARE_DATA, params, networkListener);
    }
}
