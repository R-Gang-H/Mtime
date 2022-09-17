package com.mtime.bussiness.main.maindialog.api;

import android.content.DialogInterface;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.jeremyliao.liveeventbus.LiveEventBus;
import com.kotlin.android.mtime.ktx.GlobalDimensionExt;
import com.kotlin.android.router.liveevent.EventKeyExtKt;
import com.kotlin.android.app.router.liveevent.event.CityChangedState;
import com.mtime.base.location.LocationInfo;
import com.mtime.bussiness.location.LocationHelper;
import com.mtime.bussiness.main.maindialog.MainDialogApi;
import com.mtime.bussiness.main.maindialog.bean.DialogDataBean;
import com.mtime.bussiness.mine.activity.SettingActivity;
import com.mtime.event.EventManager;
import com.mtime.frame.App;
import com.mtime.util.CustomAlertDlg;

/**
 * 【切换城市】
 * 检查当前定位信息是否与缓存不一致；
 * 弹出之前需要去设置页判断是否允许城市弹窗
 */
public class CityChangedApi implements MainDialogApi.Api<LocationInfo> {
    
    private DialogDataBean<LocationInfo> item;
    private CustomAlertDlg dlg;
    
    @Override
    public void onRequest(LocationInfo info, ApiRequestListener listener) {
        //检查设置页是否允许城市弹窗
        if(null != info && info.isChangeCity() &&
                App.getInstance().getPrefsManager().getBoolean(SettingActivity.KEY_CITYCHANGE_SET, true)) {
            item = DialogDataBean.get(DialogDataBean.TYPE_OF_CITY_CHANGE,
                    true, false, false, info);
        }
        if(null != listener) {
            listener.onFinish(item);
        }
    }
    
    /**
     * 切换城市 弹框
     */
    @Override
    public boolean onShow(AppCompatActivity activity, ApiShowListener listener) {
        if(null == activity || activity.isFinishing())
            return false;
        
        LocationInfo info = item.data;
        if(null != info) {
            dlg = new CustomAlertDlg(activity, CustomAlertDlg.TYPE_OK_CANCEL);
            dlg.setBtnCancelListener(new View.OnClickListener() {
                @Override
                public void onClick(final View arg0) {
                    // 用户不想切换城市
                    dlg.dismiss();
                }
            });
            dlg.setBtnOKListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    // 切换城市，保存到磁盘
                    String cityId = info.getLocationCityId();
                    String cityName = info.getLocationCityName();
                    LocationHelper.cacheUserCityInfo(
                            cityId,
                            cityName);
                    EventManager.getInstance().sendCityChangedEvent(
                            info.getCityId(),
                            info.getCityName(),
                            cityId,
                            cityName);
                    GlobalDimensionExt.INSTANCE.saveCurrentCityInfo(cityId, cityName);
                    LiveEventBus.get(EventKeyExtKt.CITY_CHANGED).post(
                            new CityChangedState(
                                    cityId,
                                    cityName));
                    dlg.dismiss();
                }
            });
            dlg.show();
            dlg.setText("系统检测到您现在的城市是【" + info.getLocationCityName() +"】"+ "是否切换城市？");
            dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if(null != listener) {
                        listener.onDismiss(item);
                    }
                }
            });
        }
        return true;
    }
    
    @Override
    public DialogDataBean<LocationInfo> getData() {
        return item;
    }
    
    @Override
    public void onDestroy() {
        if(null != dlg && dlg.isShowing()) {
            dlg.setOnDismissListener(null);
            dlg.cancel();
        }
        dlg = null;
        item = null;
    }
}
