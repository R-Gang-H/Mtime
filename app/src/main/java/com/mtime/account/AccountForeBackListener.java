package com.mtime.account;

import com.kotlin.android.retrofit.cookie.CookieManager;
import com.kotlin.android.user.UserManager;
import com.mtime.base.application.AppForeBackListener;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.beans.IsLoginResultBean;
import com.mtime.common.utils.LogWriter;
import com.mtime.frame.App;

/**
 * 程序进入前后台的业务监听
 */
public class AccountForeBackListener implements AppForeBackListener {
    @Override
    public void onBecameForeground() {
        App.getInstance().hasCollectCell = false;
        App.getInstance().hasCollectTopic = false;
        
        if(UserManager.Companion.getInstance().isLogin()) {
            // (此接口是判断cookie是否过期，从后台切回前台时调用进行验证)
            new AccountAPi().getUserIsLogin(new NetworkManager.NetworkListener<IsLoginResultBean>() {
                @Override
                public void onSuccess(IsLoginResultBean isLoginResultBean, String msg) {
                    if (!isLoginResultBean.isSuccess()) {
                        // 取消登录
                        cancelLogin();
                    }
                }
    
                private void cancelLogin() {
                    CookieManager.Companion.getInstance().clear();
                    UserManager.Companion.getInstance().clear();
                }
        
                @Override
                public void onFailure(NetworkException<IsLoginResultBean> networkException, String showMsg) {
                    LogWriter.d("checklogin", "check login failed:" + showMsg);
                }
            });
        }
    }

    @Override
    public void onBecameBackground() {
    }
}
