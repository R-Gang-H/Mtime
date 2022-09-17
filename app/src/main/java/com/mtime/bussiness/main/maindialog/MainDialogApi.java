package com.mtime.bussiness.main.maindialog;

import androidx.appcompat.app.AppCompatActivity;

import com.mtime.base.location.LocationException;
import com.mtime.base.location.LocationInfo;
import com.mtime.base.location.OnLocationCallback;
import com.mtime.bussiness.main.maindialog.api.DailyRecmdApi;
import com.mtime.bussiness.main.maindialog.api.PrivacyPolicyApi;
import com.mtime.bussiness.main.maindialog.bean.UpdateVerBean;
import com.mtime.bussiness.location.LocationHelper;
import com.mtime.bussiness.main.maindialog.api.CityChangedApi;
import com.mtime.bussiness.main.maindialog.api.DialogAdApi;
import com.mtime.bussiness.main.maindialog.api.UnusedTicketApi;
import com.mtime.bussiness.main.maindialog.api.VersionUpdateApi;
import com.mtime.bussiness.main.maindialog.bean.DialogDataBean;
import com.mtime.frame.App;

import java.util.ArrayList;
import java.util.List;

/**
 * 具体优先级和需求，详见 http://wiki.inc-mtime.com/pages/viewpage.action?pageId=145653840
 * */
public class MainDialogApi {
    public interface MainDialogApiListener {
        void onRequestApiFinish(List<Api> queue);
    }
    
    public interface Api<T> {
        interface ApiRequestListener {
            void onFinish(DialogDataBean item);
        }
        interface ApiShowListener {
            void onDismiss(DialogDataBean item);
        }
        
        void onRequest(LocationInfo info, ApiRequestListener listener);

        /**
         * @return true 弹出了弹框，false 未弹框。
         */
        boolean onShow(AppCompatActivity activity, ApiShowListener listener);
        void onDestroy();
        DialogDataBean<T> getData();
    }
    
    private List<Api> mApiQueue;
    private MainDialogApiListener mListener;
    private LocationInfo mLocationInfo;
    private int mCount = -1;
    
    public void requestDatas(MainDialogApiListener listener) {
        mListener = listener;
        mApiQueue = new ArrayList<>();
        LocationHelper.location(App.getInstance(), true, new OnLocationCallback() {
            @Override
            public void onLocationSuccess(LocationInfo locationInfo) {
                mLocationInfo = locationInfo.clone();
                nextStep();
            }
    
            @Override
            public void onLocationFailure(LocationException e) {
                nextStep();
            }
        });
    }
    
    //优先级管理
    private void nextStep() {
        final Api api;
        ++mCount;
        switch (mCount) {
//            case 0:
//                api = new PrivacyPolicyApi(); //Mtime服务条款与隐私政策 弹框
//                break;

            case 1:
                api = new CityChangedApi(); //【切换城市】
                break;
                
            case 2:
                api = new DialogAdApi(); // 弹窗广告
                break;

            case 3:
                api = new DailyRecmdApi(); // 每日佳片
                break;
                
            case 4:
                api = new UnusedTicketApi(); //在线选座(未使用的影票)
                break;
                
            case 5:
                api = new VersionUpdateApi(); //版本升级
                break;
                
            default:
                api = null;
                if(null != mListener) {
                    mListener.onRequestApiFinish(mApiQueue);
                    mListener = null;
                }
                break;
        }
        if(null != api) {
            api.onRequest(mLocationInfo, new Api.ApiRequestListener() {
                @Override
                public void onFinish(DialogDataBean item) {
                    if(null != item) {
                        if(item.type == DialogDataBean.TYPE_OF_UPDATE_APP) {
                            UpdateVerBean ver = (UpdateVerBean) item.data;
                            if(null != ver && ver.isForceUpdate()) {
                                mApiQueue.add(0, api);
                                nextStep();
                                return;
                            }
                        }
                        mApiQueue.add(api);
                    }
                    nextStep();
                }
            });
        }
    }
    
}
