package com.mtime.bussiness.ticket.movie.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.mtime.R;
import com.mtime.base.statistic.StatisticConstant;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.bussiness.ticket.movie.activity.ActorViewActivity;
import com.mtime.bussiness.ticket.movie.bean.ActorInfoBean;
import com.mtime.bussiness.ticket.movie.bean.ActorRelationShips;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.statistic.large.ticket.StatisticActor;
import com.mtime.util.ImageURLManager;
import com.mtime.util.JumpUtil;
import com.mtime.util.MtimeUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by guangshun on 16/8/2.
 */
public class ActorRelationsView extends RelativeLayout {
    private ActorInfoBean infoBean;
    private ActorViewActivity activity;
    private LinearLayout relationship_people_layout;

    public ActorRelationsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void init() {
        relationship_people_layout = findViewById(R.id.relationship_people_layout);
    }

    public void onDrawView(final ActorViewActivity act, final ActorInfoBean bean) {
        activity = act;
        infoBean = bean;
        if (null == infoBean) {
            setVisibility(GONE);
            return;
        }
        List<ActorRelationShips> relations = infoBean.getRelationPersons();
        if (null == relations || relations.isEmpty()) {
            setVisibility(View.GONE);
            return;
        }

        relationship_people_layout.setVisibility(View.VISIBLE);
        relationship_people_layout.removeAllViews();
        for (int i = 0; i < relations.size(); i++) {
            final ActorRelationShips relationShips = relations.get(i);

            View convertView = LayoutInflater.from(activity).inflate(R.layout.actor_detail_movies_item, null);
            convertView.setTag(relationShips);
            ImageView header = convertView.findViewById(R.id.header);
            TextView name = convertView.findViewById(R.id.name);
            TextView nameEn = convertView.findViewById(R.id.nameEn);
            TextView year = convertView.findViewById(R.id.year);

            activity.volleyImageLoader.displayImage(relationShips.getrCover(), header, R.drawable.default_image, R.drawable.default_image, ImageURLManager.ImageStyle.STANDARD, null);
            name.setText(relationShips.getrNameCn());
            nameEn.setVisibility(TextUtils.isEmpty(relationShips.getrNameEn()) ? GONE : VISIBLE);
            nameEn.setText(relationShips.getrNameEn());
            year.setText(relationShips.getRelation());

            if (i != 0) {
                convertView.setPadding( MScreenUtils.dp2px(activity, 10f), 0, 0, 0);
            }

            relationship_people_layout.addView(convertView);
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActorRelationShips relation = (ActorRelationShips) view.getTag();
                    if (null == relation || relation.getrPersonId() == 0) {
                        return;
                    }

                    String targetPersonId = String.valueOf(relation.getrPersonId());
                    // 埋点
                    Map<String, String> businessParam = new HashMap<>(2);
                    businessParam.put(StatisticConstant.PERSON_ID, activity.actorId);
                    businessParam.put(StatisticConstant.TARGET_PERSON_ID, targetPersonId);
                    StatisticPageBean statisticBean = activity.assemble(StatisticActor.STAR_DETAIL_RELATION_SHIP, "", "", "", "", "", businessParam);
                    StatisticManager.getInstance().submit(statisticBean);

                    JumpUtil.startActorDetail(activity, statisticBean.toString(), targetPersonId, relation.getrNameCn());
                }
            });
        }
    }
}
