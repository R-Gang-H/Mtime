package com.mtime.bussiness.mine.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtime.frame.App;
import com.mtime.R;
import com.mtime.bussiness.mine.bean.MemberCenterPopupBean;

/**
 * Created by vivian.wei on 2017/6/23.
 * 会员中心首页弹窗_生日/等级礼包
 */

public class MemberCenterGiftDlg extends Dialog {

    private TextView tvTitle;
    private TextView tvTip;
    private TextView tvDismantle;
    private ImageView ivClose;

    private final MemberCenterPopupBean popupBean;
    private View.OnClickListener dismantleListener = null;
    private View.OnClickListener closeListener = null;

    public MemberCenterGiftDlg(final Context context, final MemberCenterPopupBean popupBean) {
        super(context, R.style.upomp_bypay_MyDialog);
        this.popupBean = popupBean;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.setContentView(R.layout.dialog_member_center_gift);
        tvTitle = findViewById(R.id.tv_title);
        tvTip = findViewById(R.id.tv_tip);
        tvDismantle = findViewById(R.id.tv_dismantle);
        ivClose = findViewById(R.id.iv_close);

        if(App.GIFT_TYPE_LEVEL == popupBean.getType()) {
            tvTitle.setText(popupBean.getLevelGiftTitle());
            tvTip.setText(popupBean.getLevelGiftTips());
        } else if(App.GIFT_TYPE_BIRTH == popupBean.getType()) {
            tvTitle.setText(popupBean.getBirthdayGiftTitle());
            tvTip.setText(popupBean.getBirthdayGiftTips());
        }

        if(null != dismantleListener) {
            tvDismantle.setOnClickListener(dismantleListener);
        }

        if(null != closeListener) {
            ivClose.setOnClickListener(closeListener);
        }
    }

    /**
     * 设置拆礼物按钮的事件
     */
    public void setBtnDismantleListener(final View.OnClickListener click) {
        dismantleListener = click;
    }

    /**
     * 设置关闭按钮的事件
     */
    public void setBtnCloseListener(final View.OnClickListener click) {
        closeListener = click;
    }
}
