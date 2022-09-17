package com.mtime.bussiness.main.maindialog.api;

import android.content.DialogInterface;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.mtime.BuildConfig;
import com.mtime.base.location.LocationInfo;
import com.mtime.base.network.BaseApi;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.bussiness.main.maindialog.bean.UpdateVerBean;
import com.mtime.bussiness.main.maindialog.MainDialogApi;
import com.mtime.bussiness.main.maindialog.bean.DialogDataBean;
import com.mtime.bussiness.mine.activity.SettingActivity;
import com.mtime.common.utils.Utils;
import com.mtime.frame.App;
import com.mtime.network.ConstantUrl;
import com.mtime.bussiness.main.maindialog.dialog.UpdateDlg;

/**
 * 【升级检查】
 * 1、有新版本时，请求设置页面的开关，允许弹出时，提示更新，如果需要强制更新时，只有一个OK选择，点击OK，继续使用；
 * 2、强制升级时，OK等同于确认；
 * 若需要强制升级，优先级动态调整为最高级别
 */
public class VersionUpdateApi extends BaseApi implements MainDialogApi.Api<UpdateVerBean> {
    private DialogDataBean<UpdateVerBean> item;
    private UpdateDlg dlg;
    
    @Override
    protected String host() {
        return null;
    }
    
    @Override
    public void onRequest(LocationInfo info, ApiRequestListener listener) {
        boolean ver = App.getInstance().getPrefsManager().getBoolean(SettingActivity.KEY_UPDATE_VER, true);
        if(ver) {
            get(this, ConstantUrl.GET_UPDATE_VER, null, new NetworkManager.NetworkListener<UpdateVerBean>() {
                @Override
                public void onSuccess(UpdateVerBean updateVerBean, String s) {
                    if(null != updateVerBean) {
                        final String appVersion = BuildConfig.VERSION_NAME;
                        if (Utils.compareVersion(updateVerBean.getVersion().trim(), appVersion)) {
                            App.getInstance().updateVersion = updateVerBean;
                            item = DialogDataBean.get(DialogDataBean.TYPE_OF_UPDATE_APP,
                                    false, false, false, updateVerBean);
                        }
                    }
                    if(null != listener) {
                        listener.onFinish(item);
                    }
                }
            
                @Override
                public void onFailure(NetworkException<UpdateVerBean> networkException, String s) {
                    if(null != listener) {
                        listener.onFinish(item);
                    }
                }
            });
        } else {
            if(null != listener) {
                listener.onFinish(item);
            }
        }
    }
    
    @Override
    public boolean onShow(AppCompatActivity activity, ApiShowListener listener) {
        if(null == activity || activity.isFinishing())
            return false;
    
        final UpdateVerBean ver = item.data;
        int type = UpdateDlg.TYPE_OK_CANCEL;
        if (ver.isForceUpdate()) {
            type = UpdateDlg.TYPE_OK;
        }
        dlg = UpdateDlg.show(activity, type, ver.getChangelog(), new UpdateDlg.Listener() {
            @Override
            public void onCancelClick(View v) {
                item.isNextShow = true;
            }
    
            @Override
            public void onOKClick(View v) {
                item.isNextShow = false;
            }
    
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(null != listener) {
                    listener.onDismiss(item);
                }
            }
        });
        return true;
    }
    
    @Override
    public DialogDataBean<UpdateVerBean> getData() {
        return item;
    }
    
    @Override
    public void onDestroy() {
        cancel();
        if(null != dlg && dlg.isShowing()) {
            dlg.setOnDismissListener(null);
            dlg.cancel();
        }
        dlg = null;
        item = null;
    }
}
