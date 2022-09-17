package com.mtime.event;

import com.mtime.bussiness.mine.bean.AccountDetailBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mtime on 2017/12/12.
 */
@Deprecated
public class LoginEventManager {

    public static final int EVENT_TYPE_LOGIN_SUCCESS = 1;
    public static final int EVENT_TYPE_UPDATE_USER_INFO = 2;

    private static LoginEventManager instance  = null;
    private LoginEventManager(){}

    public static LoginEventManager get(){
        if(null==instance){
            synchronized (LoginEventManager.class){
                if(null==instance){
                    instance = new LoginEventManager();
                }
            }
        }
        return instance;
    }

    private List<OnLoginCallBack> mLoginCallbacks;

    public void registerLoginCallback(OnLoginCallBack onLoginCallBack){
        if(mLoginCallbacks==null){
            mLoginCallbacks = new ArrayList<>();
        }
        mLoginCallbacks.add(onLoginCallBack);
    }

    public void unregisterCallback(OnLoginCallBack onLoginCallBack){
        if(mLoginCallbacks!=null){
            mLoginCallbacks.remove(onLoginCallBack);
        }
    }

    public void callBack(int eventType, Object data){
        switch (eventType){
            case EVENT_TYPE_LOGIN_SUCCESS:
                callBackLoginSuccess(data);
                break;

            case EVENT_TYPE_UPDATE_USER_INFO:
                callBackUpdateUserInfo(data);
                break;
        }
    }

    private void callBackLoginSuccess(Object data) {
        if(mLoginCallbacks!=null){
            for(OnLoginCallBack loginCallBack : mLoginCallbacks){
                loginCallBack.onLoginSuccess(null);
            }
        }
    }

    private void callBackUpdateUserInfo(Object data) {
        if(mLoginCallbacks!=null){
            AccountDetailBean detailBean = null;
            if(data!=null && data instanceof AccountDetailBean){
                detailBean = (AccountDetailBean) data;
            }
            DataBean dataBean = new DataBean();
            dataBean.setAccountDetailBean(detailBean);
            for(OnLoginCallBack loginCallBack : mLoginCallbacks){
                loginCallBack.onUpdateUserInfo(dataBean);
            }
        }
    }


    public interface OnLoginCallBack{
        void onLoginSuccess(DataBean dataBean);
        void onUpdateUserInfo(DataBean dataBean);
    }

    public static class DataBean{
        private AccountDetailBean accountDetailBean;

        public AccountDetailBean getAccountDetailBean() {
            return accountDetailBean;
        }

        public void setAccountDetailBean(AccountDetailBean accountDetailBean) {
            this.accountDetailBean = accountDetailBean;
        }
    }

}
