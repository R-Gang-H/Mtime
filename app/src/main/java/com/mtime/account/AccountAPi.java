package com.mtime.account;

import com.mtime.base.network.BaseApi;
import com.mtime.base.network.NetworkManager;
import com.mtime.beans.IsLoginResultBean;
import com.mtime.network.ConstantUrl;

public class AccountAPi extends BaseApi {
    @Override
    protected String host() {
        return null;
    }

    /**
     * 取消网络请求
     */
    @Override
    public void cancel() {
        NetworkManager.getInstance().cancel(this);
    }

    /**
     * http://apidocs.mt-dev.com/user-front-api/#api-UserController-isLogin
     * 获取用户是否还在线
     * @param listener
     */
    public void getUserIsLogin(NetworkManager.NetworkListener<IsLoginResultBean> listener) {
        get(this, ConstantUrl.IS_LOGIN, null, listener);
    }
}
