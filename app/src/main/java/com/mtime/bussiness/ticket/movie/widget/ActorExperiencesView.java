package com.mtime.bussiness.ticket.movie.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.mtime.R;
import com.mtime.base.statistic.StatisticConstant;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.bussiness.ticket.movie.activity.ActorExperienceActivity;
import com.mtime.bussiness.ticket.movie.activity.ActorViewActivity;
import com.mtime.bussiness.ticket.movie.bean.ActorExperience;
import com.mtime.bussiness.ticket.movie.bean.ActorInfoBean;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.statistic.large.ticket.StatisticActor;
import com.mtime.util.ImageURLManager;
import com.mtime.util.JumpUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by guangshun on 16/8/2.
 */
public class ActorExperiencesView extends RelativeLayout implements View.OnClickListener {
    private ActorInfoBean infoBean;
    private ActorViewActivity activity;
    private TextView experiences_summary;
    private TextView experiences_label;
    private ImageView experiences_header;

    public ActorExperiencesView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }
    private void init() {
        experiences_summary = findViewById(R.id.experiences_summary);
        experiences_label = findViewById(R.id.experiences_label);
        experiences_header = findViewById(R.id.experiences_header);
    }

    public void onDrawView(final ActorViewActivity act, final ActorInfoBean bean) {
        activity = act;
        infoBean = bean;
        if (null == infoBean) {
            setVisibility(GONE);
            return;
        }
        List<ActorExperience> experiences = infoBean.getExpriences();
        if (null == experiences || experiences.isEmpty()) {
            setVisibility(View.GONE);
            return;
        }
        ActorExperience item = experiences.get(0);
        if(null==item||item.isEmpty()){
            setVisibility(View.GONE);
            return;
        }
        setOnClickListener(this);
        experiences_summary.setText(item.getContent());
        if(0==item.getYear()){
            experiences_label.setText(item.getTitle());
        }else{
            experiences_label.setText(String.format("%d %s", item.getYear(), item.getTitle()));
        }

        activity.volleyImageLoader.displayImage(item.getImg(), experiences_header, R.drawable.default_image, R.drawable.default_image, ImageURLManager.ImageStyle.STANDARD_HOR, null);
    }

    @Override
    public void onClick(View view) {
        if (null == activity || null == infoBean) {
            return;
        }

        // 埋点
        Map<String, String> businessParam = new HashMap<>(1);
        businessParam.put(StatisticConstant.PERSON_ID, activity.actorId);
        StatisticPageBean statisticBean = activity.assemble(StatisticActor.STAR_DETAIL_PERFORMING_EXPERIENCE, "", "", "", "", "", businessParam);
        StatisticManager.getInstance().submit(statisticBean);

        ActorExperienceActivity.actorInfo = infoBean;
        JumpUtil.startActorExperienceActivity(activity, statisticBean.toString(), activity.actorId);
    }
}
