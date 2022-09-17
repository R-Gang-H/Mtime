package com.mtime.bussiness.mine.widget;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.aspsine.irecyclerview.IRecyclerView;
import com.mtime.frame.App;
import com.mtime.R;
import com.mtime.bussiness.mine.adapter.MemberCenterGiftContentAdapter;
import com.mtime.bussiness.mine.bean.MemberGiftBean;
import com.mtime.bussiness.mine.bean.MemberGiftDismantleBean;

import java.util.List;

/**
 * Created by vivian.wei on 2017/6/28.
 * 会员中心首页弹窗_生日/等级礼包内容
 */

public class MemberCenterGiftContentDlg extends Dialog {

    private final Activity context;
    private ImageView ivIcon;
    private TextView tvTitle;
    private TextView tvHint;
    private TextView tvGotoCouponList;
    private ImageView ivClose;
    private IRecyclerView rvList;
    private MemberCenterGiftContentAdapter adapter;

    // 礼包类型
    private final int giftType;
    private final MemberGiftDismantleBean dismantleBean;
    private View.OnClickListener btnListener = null;
    private View.OnClickListener closeListener = null;

    private static final int LIST_MAX_SHOW_COUNT = 3;
    private static final int LIST_MAX_HEIGHT = 260; // 3个半行高

    public MemberCenterGiftContentDlg(final Activity context, final int giftType,
                                      final MemberGiftDismantleBean dismantleBean) {
        super(context, R.style.upomp_bypay_MyDialog);
        this.context = context;
        this.giftType = giftType;
        this.dismantleBean = dismantleBean;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.setContentView(R.layout.dialog_member_center_gift_content);
        ivIcon = findViewById(R.id.iv_icon);
        tvTitle = findViewById(R.id.tv_title);
        tvHint = findViewById(R.id.tv_hint);
        tvGotoCouponList = findViewById(R.id.tv_goto_coupon_list);
        ivClose = findViewById(R.id.iv_close);
        rvList = findViewById(R.id.dialog_member_center_gift_content_rv_list);
        rvList.setLayoutManager(new LinearLayoutManager(context));

        ivIcon.setBackgroundResource(App.GIFT_TYPE_LEVEL == giftType ? R.drawable.dialog_member_center_gift_content_level : R.drawable.dialog_member_center_gift_content_birth);
        tvTitle.setText(dismantleBean.getTitle());
        tvHint.setText(dismantleBean.getHint());

        List<MemberGiftBean> list = dismantleBean.getList();
        if(null != list && list.size() > 0) {
            adapter = new MemberCenterGiftContentAdapter(context, list);
            rvList.setIAdapter(adapter);

            if(list.size() > LIST_MAX_SHOW_COUNT) {
                ViewGroup.LayoutParams params = rvList.getLayoutParams();
                params.height = LIST_MAX_HEIGHT;
                rvList.setLayoutParams(params);
            }
        }

        if(null != btnListener) {
            tvGotoCouponList.setOnClickListener(btnListener);
        }

        if(null != closeListener) {
            ivClose.setOnClickListener(closeListener);
        }
    }

    /**
     * 设置查看优惠券按钮的事件
     */
    public void setBtnGotoCouponListListener(final View.OnClickListener click) {
        btnListener = click;
    }

    /**
     * 设置关闭按钮的事件
     */
    public void setBtnCloseListener(final View.OnClickListener click) {
        closeListener = click;
    }

}
