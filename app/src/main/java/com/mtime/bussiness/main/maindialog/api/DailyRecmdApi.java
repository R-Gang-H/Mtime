package com.mtime.bussiness.main.maindialog.api;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;

import com.mtime.base.dialog.BaseDialogFragment;
import com.mtime.base.location.LocationInfo;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.utils.MTimeUtils;
import com.mtime.bussiness.daily.dialog.RecmdDialog;
import com.mtime.bussiness.daily.recommend.api.RecmdApi;
import com.mtime.bussiness.daily.recommend.bean.DailyRecommendBean;
import com.mtime.bussiness.main.maindialog.MainDialogApi;
import com.mtime.bussiness.main.maindialog.bean.DialogDataBean;
import com.mtime.frame.App;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2018-06-14
 */
public class DailyRecmdApi implements MainDialogApi.Api<DailyRecommendBean> {

    private static final String SP_NAME = "DAILY_RECMD_DIALOG";
    private DialogDataBean<DailyRecommendBean> wrapBean;

    private final RecmdApi mApi = new RecmdApi();
    private RecmdDialog mDialog;

    @Override
    public void onRequest(LocationInfo info, ApiRequestListener listener) {
        if (checkPopup()) {
            listener.onFinish(null);
            return;
        }
        mApi.getDailyPopup(new NetworkManager.NetworkListener<DailyRecommendBean>() {
            @Override
            public void onSuccess(DailyRecommendBean result, String showMsg) {
                if (result != null && !result.isEmpty()) {
                    wrapBean = DialogDataBean.get(DialogDataBean.TYPE_OF_DAILY_RECMD, false,
                            false, true, result);
                }
                if (null != listener) {
                    listener.onFinish(wrapBean);
                }
            }

            @Override
            public void onFailure(NetworkException<DailyRecommendBean> exception, String showMsg) {
                if (null != listener) {
                    listener.onFinish(wrapBean);
                }
            }
        });
    }

    private boolean checkPopup() {
        App app = App.getInstance();
        SharedPreferences sp = app.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd", Locale.getDefault());
        String dtString = sdf.format(MTimeUtils.getLastServerDate());
        return sp.getBoolean(dtString, false);
    }

    @Override
    public boolean onShow(AppCompatActivity act, final ApiShowListener listener) {
        if (null == act || act.isFinishing())
            return false;
        if (wrapBean == null) {
            return false;
        }
        if (checkPopup()) {
            return false;
        }
        App app = App.getInstance();
        SharedPreferences sp = app.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd", Locale.getDefault());
        String dtString = sdf.format(MTimeUtils.getLastServerDate());
        sp.edit().clear().putBoolean(dtString, true).apply();
        mDialog = new RecmdDialog();
        mDialog.fillData(wrapBean.data);
        mDialog.showAllowingStateLoss(act.getSupportFragmentManager());
        mDialog.setOnDismissListener(new BaseDialogFragment.DismissListener() {
            @Override
            public void onDismiss() {
                if (null != listener) {
                    listener.onDismiss(wrapBean);
                }
            }
        });
        return true;
    }

    @Override
    public void onDestroy() {
        mApi.cancel();
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        wrapBean = null;
    }

    @Override
    public DialogDataBean<DailyRecommendBean> getData() {
        return wrapBean;
    }
}
