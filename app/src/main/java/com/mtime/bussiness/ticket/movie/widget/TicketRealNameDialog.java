package com.mtime.bussiness.ticket.movie.widget;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.mtime.R;
import com.mtime.base.dialog.BaseMDialog;
import com.mtime.base.recyclerview.CommonViewHolder;
import com.mtime.bussiness.splash.LoadManager;
import com.mtime.network.ConstantUrl;
import com.mtime.statistic.large.h5.StatisticH5;
import com.mtime.util.JumpUtil;

/**
 * @author vivian.wei
 * @date 2020/7/27
 * @desc 实名预约购票弹窗（本弹窗不能取消）
 */
public class TicketRealNameDialog extends BaseMDialog implements View.OnClickListener {

    private String mOrderId;
    private int mSeatCount;
    private View.OnClickListener closeListener;

    public void setCloseListener(View.OnClickListener closeListener) {
        this.closeListener = closeListener;
    }

    public TicketRealNameDialog() {
        // 是否可以滑动取消
        setCancelable(false);
        // 是否可以点击弹窗外部取消
        setOutCancel(false);
        // 灰度深浅
        setDimAmount(0.6f);
        // 左右边距
        setMargin(40);
    }

    public void setData(String orderId, int seatCount) {
        mOrderId = orderId;
        mSeatCount = seatCount;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int intLayoutId() {
        return R.layout.dialog_ticket_real_name;
    }

    @Override
    public void convertView(CommonViewHolder holder, BaseMDialog dialog) {
        holder.setOnClickListener(R.id.dialog_ticket_close_iv, (view) -> {
            if (closeListener != null) {
                closeListener.onClick(view);
            }
            dialog.dismiss();
        });
        holder.setOnClickListener(R.id.dialog_ticket_real_name_service_tv, this)
                .setOnClickListener(R.id.dialog_ticket_real_name_privacy_tv, this)
                .setOnClickListener(R.id.dialog_ticket_real_name_ok_btn, (view) -> {
                    dialog.dismiss();
                    // 跳转到h5页面填写身份信息
                    String url = String.format(ConstantUrl.FEATURE_COMMIT_REAL_NAME_INFO, mOrderId, mSeatCount);
                    JumpUtil.startCommonWebActivity(this.getContext(), url, StatisticH5.PN_H5, null,
                            true, true, true, false, null);
                });

        TextView view1 = holder.getView(R.id.dialog_ticket_real_name_service_tv);
        view1.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        view1.getPaint().setAntiAlias(true);//抗锯齿
        TextView view2 = holder.getView(R.id.dialog_ticket_real_name_privacy_tv);
        view2.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        view2.getPaint().setAntiAlias(true);//抗锯齿
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_ticket_real_name_service_tv:
                gotoWeb(LoadManager.getRegisterServiceUrl());
                break;
            case R.id.dialog_ticket_real_name_privacy_tv:
                gotoWeb(LoadManager.getRegisterPrivacyUrl());
                break;
            default:
                break;
        }
    }

    /**
     * 打开浏览器
     *
     * @param url
     */
    private void gotoWeb(String url) {
        dismiss();
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri uri = Uri.parse(url);
        intent.setData(uri);
        startActivity(intent);
    }

}
