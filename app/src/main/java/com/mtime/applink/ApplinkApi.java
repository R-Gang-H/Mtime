package com.mtime.applink;

import com.mtime.base.network.BaseApi;
import com.mtime.base.network.NetworkManager;
import com.mtime.network.ConstantUrl;

import java.util.HashMap;

public class ApplinkApi extends BaseApi {
    @Override
    protected String host() {
        return null;
    }
    
    public void getPushMsgByMsgId(String source, String messageId, NetworkManager.NetworkListener<ApplinkPushInfoBean> listener) {
        String requestUrl = ConstantUrl.GET_PUSH_MESSAGE_BY_ID;
        if ("sms".equals(source)) {
            requestUrl = ConstantUrl.GET_SMS_MESSAGE_BY_ID;
        } else if ("push".equals(source)) {
            requestUrl = ConstantUrl.GET_PUSH_MESSAGE_BY_ID;
        }
    
        HashMap<String, String> params = new HashMap<>();
        params.put("messageId", messageId);
        get(this, requestUrl, params, listener);
    }
}
