package com.mtime.bussiness.ticket.movie.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.mtime.R;
import com.mtime.base.statistic.StatisticConstant;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.ticket.movie.activity.ActorHonorsActivity;
import com.mtime.bussiness.ticket.movie.activity.ActorViewActivity;
import com.mtime.bussiness.ticket.movie.bean.ActorInfoBean;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.statistic.large.ticket.StatisticActor;
import com.mtime.util.JumpUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by guangshun on 16/8/2.
 */
public class ActorHonorsView extends RelativeLayout implements View.OnClickListener {
    private ActorInfoBean infoBean;
    private ActorViewActivity activity;
    private TextView honors_tip;

    @Override
    public void onClick(View view) {
        if (null == activity) {
            return;
        }
        if (null == infoBean) {
            MToastUtils.showShortToast("没有显示内容");
            return;
        }

        // 埋点
        Map<String, String> businessParam = new HashMap<>(1);
        businessParam.put(StatisticConstant.PERSON_ID, activity.actorId);
        StatisticPageBean statisticBean = activity.assemble(StatisticActor.STAR_DETAIL_HONOR, "", "", "", "", "", businessParam);
        StatisticManager.getInstance().submit(statisticBean);

        ActorHonorsActivity.actorBean = infoBean;
        JumpUtil.startActorHonorsActivity(activity, statisticBean.toString(), activity.actorId);
    }

    public ActorHonorsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }
    private void init() {
        honors_tip = findViewById(R.id.honors_title);
    }

    public void onDrawView(final ActorViewActivity act, final ActorInfoBean bean) {
        activity = act;
        infoBean = bean;
        int totalWinAward = bean.getTotalWinAward();
        int totalNominateAward = bean.getTotalNominateAward();

        if (0 == totalNominateAward && 0 == totalWinAward) {
            setVisibility(View.GONE);
            return;
        }

        setOnClickListener(this);

        honors_tip.setText(String.format(getResources().getString(R.string.st_movie_info_total_festival),
                totalWinAward, totalNominateAward));
    }
}
