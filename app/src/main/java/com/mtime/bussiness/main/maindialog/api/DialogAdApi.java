package com.mtime.bussiness.main.maindialog.api;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;


import com.mtime.R;
import com.mtime.applink.ApplinkManager;
import com.mtime.base.dialog.CancelableDialog;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.base.imageload.ImageShowLoadCallback;
import com.mtime.base.location.LocationInfo;
import com.mtime.base.network.BaseApi;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.statistic.StatisticDataBuild;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.base.utils.MTimeUtils;
import com.mtime.bussiness.common.bean.CommonRegionPusblish;
import com.mtime.bussiness.main.maindialog.MainDialogApi;
import com.mtime.bussiness.main.maindialog.bean.DialogDataBean;
import com.mtime.common.utils.PrefsManager;
import com.mtime.frame.App;
import com.mtime.network.ConstantUrl;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.statistic.large.home.StatisticHome;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 【弹窗广告】
 * 1、奇数时间段内，弹硬广，如果没有硬广，则弹软广，每2小时内弹一次（软广和硬广的奇偶规则以线上为准）
 * 2、偶数时间段内，弹软广，如果没有软广，则弹硬广，每2小时内弹一次
 * 3、有注册礼包时，不弹出
 */
public class DialogAdApi extends BaseApi implements MainDialogApi.Api<CommonRegionPusblish> {
    private final String HOME_DIALOG_AD_LAST_TIME = "dialog_ad_last_time";

    private DialogDataBean<CommonRegionPusblish> item;
    private Dialog advertDialog;
    
    @Override
    protected String host() {
        return null;
    }
    
    @Override
    public void onRequest(LocationInfo info, ApiRequestListener listener) {
        Map<String, String> param = new HashMap<>(1);
        param.put("codes", "APP_M20_Pop_Screen");
        get(this, ConstantUrl.GET_RCMD_REGION_PUBLISH, param, new NetworkManager.NetworkListener<CommonRegionPusblish>() {
            @Override
            public void onSuccess(CommonRegionPusblish dialogAdBean, String s) {
                if(null != dialogAdBean) {
                    item = DialogDataBean.get(DialogDataBean.TYPE_OF_DIALOG_AD,
                            false, true, false, dialogAdBean);
                }
                if(null != listener) {
                    listener.onFinish(item);
                }
            }
        
            @Override
            public void onFailure(NetworkException<CommonRegionPusblish> networkException, String s) {
                if(null != listener) {
                    listener.onFinish(item);
                }
            }
        });
    }
    
    @Override
    public boolean onShow(AppCompatActivity context, ApiShowListener showListener) {
        if(null == context || context.isFinishing()) {
            return false;
        }
        final PrefsManager sp = App.getInstance().getPrefsManager();
        long lastTime = sp.getLong(HOME_DIALOG_AD_LAST_TIME);
        final CommonRegionPusblish info = item.data;
        final boolean isNoShow = null == info || !info.hasItems()
                || (lastTime > 0 && MTimeUtils.isTheSameDay(new Date(lastTime), MTimeUtils.getLastDiffServerDate()))
                || TextUtils.isEmpty(info.regionList.get(0).items.get(0).get("img"));
        if(isNoShow) {
            item.isNextShow = true;
            return false;
        }
    

        advertDialog = new CancelableDialog(context, R.style.Main_Dialog_Ad);
        advertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        final View.OnClickListener clickToAdv = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                StatisticPageBean bean = StatisticDataBuild.assemble(StatisticHome.HOME_AD, "", "pop", "", "click", "", null);
                StatisticManager.getInstance().submit(bean);

                ApplinkManager.jump(context, info.regionList.get(0).items.get(0).get("appLink"), bean.toString());
                advertDialog.dismiss();
            }
        };
        
        advertDialog.setContentView(R.layout.dialog_homepage_popup_bigad);
        final ImageView dialog_popup_bigad_img = advertDialog.findViewById(R.id.dialog_popup_bigad_img);
        ImageView dialog_popup_bigad_close = advertDialog.findViewById(R.id.dialog_popup_bigad_close);
        
        int height = (int) context.getResources().getDimension(R.dimen.home_pop_ad_height);
        int width = height * 1126 / 1522;
        ImageHelper.with(context, ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                .override(width, height)
                .view(dialog_popup_bigad_img)
                .load(info.regionList.get(0).items.get(0).get("img"))
                .callback(new ImageShowLoadCallback() {
                    @Override
                    public void onLoadCompleted(Bitmap bitmap) {
                        if(null != context && !context.isFinishing()) {
                            advertDialog.show();
                        }
                    }
                
                    @Override
                    public void onLoadFailed() {
                    }
                })
                .showload();
    
        dialog_popup_bigad_img.setOnClickListener(clickToAdv);
        dialog_popup_bigad_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatisticPageBean bean = StatisticDataBuild.assemble(StatisticHome.HOME_AD, "", "pop", "", "skip", "", null);
                StatisticManager.getInstance().submit(bean);
            
                advertDialog.dismiss();
            }
        });
    
        sp.putLong(HOME_DIALOG_AD_LAST_TIME, MTimeUtils.getLastDiffServerTime());

        advertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                StatisticPageBean bean = StatisticDataBuild.assemble(StatisticHome.HOME_AD, "", "pop", "", "skip", "", null);
                StatisticManager.getInstance().submit(bean);
            }
        });
    
        // 广告标示
        final String tag = info.regionList.get(0).items.get(0).get("advTag");
        final String enableAdvTag = info.regionList.get(0).items.get(0).get("enableAdvTag");
        if (!TextUtils.isEmpty(tag) && TextUtils.equals("1", enableAdvTag)) {
            TextView adTag = advertDialog.findViewById(R.id.adv_tag);
            adTag.setVisibility(View.VISIBLE);
            adTag.setText(tag);
        }
        
        advertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(null != showListener) {
                    showListener.onDismiss(item);
                }
            }
        });
        return true;
    }
    
    @Override
    public DialogDataBean<CommonRegionPusblish> getData() {
        return item;
    }
    
    @Override
    public void onDestroy() {
        cancel();
        if(null != advertDialog && advertDialog.isShowing()) {
            advertDialog.setOnDismissListener(null);
            advertDialog.cancel();
        }
        advertDialog = null;
        item = null;
    }
}
