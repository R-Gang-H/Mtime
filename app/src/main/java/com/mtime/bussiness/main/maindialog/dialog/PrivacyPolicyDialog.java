package com.mtime.bussiness.main.maindialog.dialog;

import com.mtime.R;
import com.mtime.base.dialog.BaseMDialog;
import com.mtime.base.recyclerview.CommonViewHolder;
import com.mtime.base.utils.MJsonUtils;
import com.mtime.bussiness.common.utils.MSharePreferenceHelper;
import com.mtime.bussiness.main.maindialog.api.PrivacyPolicyApi;
import com.mtime.bussiness.main.maindialog.bean.PricacyPolicyBean;
import com.mtime.bussiness.splash.LoadManager;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2018/12/17
 * <p>
 * Mtime服务条款与隐私政策 弹框
 */
public class PrivacyPolicyDialog extends BaseMDialog implements View.OnClickListener {

    public static void show(PricacyPolicyBean bean, FragmentManager fm, OnDismissListner onDismissListner) {
        if (null != bean) {
            PrivacyPolicyDialog dialog = new PrivacyPolicyDialog();
            dialog.setPricacyPolicyBean(bean);
            dialog.setOnDismissListner(onDismissListner);
            dialog.show(fm);
        }
    }

    private PricacyPolicyBean mPricacyPolicyBean;
    private OnDismissListner mOnDismissListner;

    public PrivacyPolicyDialog() {
        setCancelable(false);
        setOutCancel(false);
        setDimAmount(0.3f);
        setMargin(40);
    }

    public void setPricacyPolicyBean(PricacyPolicyBean pricacyPolicyBean) {
        mPricacyPolicyBean = pricacyPolicyBean;
    }

    public void setOnDismissListner(OnDismissListner onDismissListner) {
        mOnDismissListner = onDismissListner;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int intLayoutId() {
        return R.layout.dialog_privacy_policy;
    }

    @Override
    public void convertView(CommonViewHolder holder, BaseMDialog dialog) {
        if (null != mPricacyPolicyBean) {
            holder.setText(R.id.dlg_title, mPricacyPolicyBean.title)
                    .setText(R.id.dlg_small_text, mPricacyPolicyBean.content);
        }
        holder.setOnClickListener(R.id.dialog_register_pricacy_text1, this)
                .setOnClickListener(R.id.dialog_register_pricacy_text2, this)
                .setOnClickListener(R.id.dialog_privacy_btn1, this)
                .setOnClickListener(R.id.dialog_privacy_btn2, this);

        TextView view1 = holder.getView(R.id.dialog_register_pricacy_text1);
        view1.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        view1.getPaint().setAntiAlias(true);//抗锯齿
        TextView view2 = holder.getView(R.id.dialog_register_pricacy_text2);
        view2.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        view2.getPaint().setAntiAlias(true);//抗锯齿
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_register_pricacy_text1:
//                JumpUtil.startCommonWebActivity(getContext(), LoadManager.getRegisterServiceUrl(), StatisticH5.PN_H5, null,
//                        true, true, true, false, null);
                gotoWeb(LoadManager.getRegisterServiceUrl());
                break;

            case R.id.dialog_register_pricacy_text2:
//                JumpUtil.startCommonWebActivity(getContext(), LoadManager.getRegisterPrivacyUrl(), StatisticH5.PN_H5, null,
//                        true, true, true, false, null);
                gotoWeb(LoadManager.getRegisterPrivacyUrl());
                break;

            case R.id.dialog_privacy_btn2:
                if (null != mPricacyPolicyBean) {
                    MSharePreferenceHelper.get().putString(PrivacyPolicyApi.SP_KEY_PRICACY_POLICY_API_RESULT_JSON_STR, MJsonUtils.toString(mPricacyPolicyBean));
                }
                if (null != mOnDismissListner) {
                    mOnDismissListner.onDismiss(true);
                }
                dismissAllowingStateLoss();
                break;

            case R.id.dialog_privacy_btn1:
                if (null != mOnDismissListner) {
                    mOnDismissListner.onDismiss(false);
                }
                dismissAllowingStateLoss();
                if (null != getActivity()) {
                    getActivity().finish();
                }
                break;
        }
    }

    private void gotoWeb(String url) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri uri = Uri.parse(url);
        intent.setData(uri);
        startActivity(intent);
    }

    public interface OnDismissListner {
        void onDismiss(boolean isOk);
    }
}
