package com.mtime.bussiness.ticket.movie.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.mtime.R;
import com.mtime.base.statistic.StatisticConstant;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.bussiness.ticket.movie.activity.ActorViewActivity;
import com.mtime.bussiness.ticket.movie.bean.ActorImage;
import com.mtime.bussiness.ticket.movie.bean.ActorInfoBean;
import com.mtime.bussiness.ticket.stills.MovieStillsActivity;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.statistic.large.ticket.StatisticActor;
import com.mtime.util.JumpUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by guangshun on 16/8/2.
 */
public class ActorImagesView extends RelativeLayout implements View.OnClickListener {
    private ActorViewActivity activity;
    private ActorInfoBean infoBean;

    public ActorImagesView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void onDrawView(final ActorViewActivity act, final ActorInfoBean bean) {
        activity = act;
        infoBean = bean;
        if (null == bean) {
            setVisibility(GONE);
            return;
        }
        List<ActorImage> actorPictures = bean.getImages();
        if (null == actorPictures || actorPictures.isEmpty()) {
            setVisibility(GONE);
            return;
        }

        setOnClickListener(this);

        int[] ids = {R.id.actor_detail_picture_1, R.id.actor_detail_picture_2, R.id.actor_detail_picture_3,
                R.id.actor_detail_picture_4};
        int count = actorPictures.size() > 4 ? 4 : actorPictures.size();
        for (int i = 0; i < count; i++) {
            ImageView view = findViewById(ids[i]);
            view.setVisibility(View.VISIBLE);
            int w = getResources().getDimensionPixelSize(R.dimen.actor_detail_picture_item_height);
            activity.volleyImageLoader.displayImage(actorPictures.get(i).getImage(), view, R.drawable.default_image, R.drawable.default_image, w, w, null);
        }
    }

    @Override
    public void onClick(View view) {
        if (null == activity || null == infoBean) {
            return;
        }

        // 埋点
        Map<String, String> businessParam = new HashMap<>(1);
        businessParam.put(StatisticConstant.PERSON_ID, activity.actorId);
        StatisticPageBean statisticBean = activity.assemble(StatisticActor.STAR_DETAIL_IMAGE, "", "", "", "", "", businessParam);
        StatisticManager.getInstance().submit(statisticBean);

        JumpUtil.startPhotoListActivity(activity, statisticBean.toString(),
                MovieStillsActivity.TARGET_TYPE_ACTOR, activity.actorId, infoBean.getNameCn(), null);
    }
}
