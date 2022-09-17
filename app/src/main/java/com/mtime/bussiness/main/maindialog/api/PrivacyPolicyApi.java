package com.mtime.bussiness.main.maindialog.api;

import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;

import com.mtime.base.location.LocationInfo;
import com.mtime.base.network.BaseApi;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.utils.MJsonUtils;
import com.mtime.bussiness.common.utils.MSharePreferenceHelper;
import com.mtime.bussiness.main.maindialog.MainDialogApi;
import com.mtime.bussiness.main.maindialog.bean.DialogDataBean;
import com.mtime.bussiness.main.maindialog.bean.PricacyPolicyBean;
import com.mtime.bussiness.main.maindialog.dialog.PrivacyPolicyDialog;
import com.mtime.network.ConstantUrl;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-07-31
 *
 * Mtime服务条款与隐私政策 弹框
 */
public class PrivacyPolicyApi extends BaseApi implements MainDialogApi.Api<PricacyPolicyBean> {
    public static final String SP_KEY_PRICACY_POLICY_API_RESULT_JSON_STR = "Privacy_Policy_Api_Result_Json_Str";

    private DialogDataBean<PricacyPolicyBean> item;

    @Override
    public void onRequest(LocationInfo info, ApiRequestListener listener) {
        get(this, ConstantUrl.GET_PRIVACY_POLICY, null, new NetworkManager.NetworkListener<PricacyPolicyBean>() {
            @Override
            public void onSuccess(PricacyPolicyBean result, String showMsg) {
                if (null != result) {
                    boolean isHint = true;
                    String stringValue = MSharePreferenceHelper.get().getStringValue(SP_KEY_PRICACY_POLICY_API_RESULT_JSON_STR, "");
                    if (!TextUtils.isEmpty(stringValue)) {
                        try {
                            PricacyPolicyBean pricacyPolicyBean = MJsonUtils.parseString(stringValue, PricacyPolicyBean.class);
                            if (TextUtils.equals(pricacyPolicyBean.policyVersion, result.policyVersion)) {
                                isHint = false;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (isHint) {
                        item = DialogDataBean.get(DialogDataBean.KEY_OF_0X8, true, false, false, result);
                    }
                }
                if (null != listener) {
                    listener.onFinish(item);
                }
            }

            @Override
            public void onFailure(NetworkException<PricacyPolicyBean> exception, String showMsg) {
                if (null != listener) {
                    listener.onFinish(item);
                }
            }
        });
    }

    @Override
    public boolean onShow(AppCompatActivity activity, ApiShowListener listener) {
        if(null == activity || activity.isFinishing()) {
            return false;
        }

        if (null != item.data) {
            PrivacyPolicyDialog.show(item.data, activity.getSupportFragmentManager(), new PrivacyPolicyDialog.OnDismissListner() {
                @Override
                public void onDismiss(boolean isOk) {
                    if (null != listener) {
                        listener.onDismiss(item);
                    }
                }
            });
        }

        return true;
    }

    @Override
    public void onDestroy() {
        cancel();
    }

    @Override
    public DialogDataBean<PricacyPolicyBean> getData() {
        return item;
    }

    @Override
    protected String host() {
        return null;
    }
}
