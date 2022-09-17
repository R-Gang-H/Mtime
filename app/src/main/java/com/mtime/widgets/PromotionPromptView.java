package com.mtime.widgets;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.kotlin.android.user.UserManager;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.base.utils.MTimeUtils;
import com.mtime.beans.PromotionPromptBean;
import com.mtime.common.utils.PrefsManager;
import com.mtime.network.ConstantUrl;
import com.mtime.network.RequestCallback;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.statistic.large.home.StatisticHome;
import com.mtime.util.HttpUtil;
import com.mtime.util.JumpUtil;
import com.mtime.util.MtimeUtils;

/**
 * Created by cong.zhang on 17/3/14.
 */

public class PromotionPromptView extends FrameLayout {
    private TextView tvHour;
    private TextView tvMinute;
    private TextView tvSecond;
    private TextView tvTips;
    private ImageView ivClose;
    private MyHandler handler;
    private BaseActivity mContext;
    private final static String NEXT_REQUEST_TIME = "nextRequestTime";

    private class MyHandler extends Handler {
        String[] times;

        @Override
        public void handleMessage(Message msg) {
            int leftTime = 0;

            switch (msg.what) {
                case 1:
                    leftTime = Integer.parseInt(msg.obj.toString());
                    if (leftTime > 0) {
                        Message message = new Message();
                        message.what = 1;
                        message.obj = leftTime - 1;
                        sendMessageDelayed(message, 1000);
                    } else {
                        setVisibility(GONE);
                        return;
                    }
                    break;
                case 2:
                    // 获取到到期时间再计算剩余时间，否则由于第一次刷新整个大页面，Message会延期到达，此时时间会不准
                    long endTime = Long.parseLong(msg.obj.toString());
                    leftTime = (int) (endTime - MTimeUtils.getLastDiffServerTime() / 1000);
                    if (leftTime > 0) {
                        Message message = new Message();
                        message.what = 1;
                        message.obj = leftTime - 1;
                        sendMessageDelayed(message, 1000);
                    } else {
                        setVisibility(GONE);
                        return;
                    }
                    break;
            }

            times = MtimeUtils.parseMills(leftTime).split(":");
            tvHour.setText(times[0]);
            tvMinute.setText(times[1]);
            tvSecond.setText(times[2]);
        }
    }

    public PromotionPromptView(BaseActivity context) {
        this(context, null);
        mContext = context;
    }

    public PromotionPromptView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = ((BaseActivity) context);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_promotion_prompt, this);
        tvHour = findViewById(R.id.tv_hour);
        tvMinute = findViewById(R.id.tv_minute);
        tvSecond = findViewById(R.id.tv_second);
        tvTips = findViewById(R.id.tv_tips);
        ivClose = findViewById(R.id.iv_close);
        handler = new MyHandler();

        ivClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNextRequestTime();
                PromotionPromptView.this.setVisibility(GONE);
                combinationClose();
            }
        });

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                JumpUtil.startPromotionListActivity(getContext());
            }
        });
    }

    private void setPromotionPromptBean(PromotionPromptBean bean) {
        StatisticPageBean bea = mContext.assemble(StatisticHome.HOME_TICKET, "", "combination", "", "open", "", null);
        StatisticManager.getInstance().submit(bea);
        setVisibility(VISIBLE);
        tvTips.setText(bean.getTitle());
        Message message = new Message();
        message.what = 2;
        message.obj = bean.getEndTime();
        handler.sendMessage(message);
    }

    public void showPromotionPrompt() {
        if (!UserManager.Companion.getInstance().isLogin() || !isShowPromotionPrompt()) {
            setVisibility(View.GONE);
            return;
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }

        HttpUtil.get(ConstantUrl.GET_PROMOTION_PROMPT, PromotionPromptBean.class, new RequestCallback() {
            @Override
            public void onSuccess(Object o) {
                if (o == null) {
                    setVisibility(GONE);
                    return;
                }

                PromotionPromptBean bean = (PromotionPromptBean) o;
                if (bean.getEndTime() < MTimeUtils.getLastDiffServerTime() / 1000) {
                    setVisibility(GONE);
                    return;
                }
                setPromotionPromptBean(bean);
            }

            @Override
            public void onFail(Exception e) {
                setVisibility(GONE);
            }
        });
    }

    // 每次切换回
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        showPromotionPrompt();
    }

    // View准备销毁后取消handler消息，否则会引起内存泄漏，同时View不显示在窗口时取消handler，提升性能，避免卡顿
    @Override
    protected void onDetachedFromWindow() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        super.onDetachedFromWindow();
    }

    // 关闭购票购物促销提示，24小时内不再请求，此时保存下次请求时间为24h后，用服务器时间，避免本地时间不准
    private void saveNextRequestTime() {
        long nextTime = MTimeUtils.getLastDiffServerTime() + 24 * 60 * 60 * 1000;
        PrefsManager.get(getContext()).putLong(NEXT_REQUEST_TIME, nextTime);
    }

    // 退出登录后清除下次请求时间
    public static void removeNextRequestTime(Context context) {
        PrefsManager.get(context).removeKey(NEXT_REQUEST_TIME);
    }

    private boolean isShowPromotionPrompt() {
        return MTimeUtils.getLastDiffServerTime() >
                PrefsManager.get(getContext()).getLong(NEXT_REQUEST_TIME);
    }

    private void combinationClose() {
        StatisticPageBean bea = mContext.assemble(StatisticHome.HOME_TICKET, "", "combination", "", "close", "", null);
        StatisticManager.getInstance().submit(bea);
    }

}