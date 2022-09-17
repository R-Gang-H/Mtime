package com.mtime.bussiness.ticket.movie.widget;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.mtime.R;
import com.mtime.base.statistic.StatisticConstant;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.bussiness.ticket.movie.activity.ActorViewActivity;
import com.mtime.bussiness.ticket.movie.bean.FilmographyBean;
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
 * 影人代表作
 */
public class ActorMoviesView extends RelativeLayout implements View.OnClickListener {
    private ActorViewActivity activity;
    private View movies_title;
    private HorizontalScrollView movies_scrollview;
    private LinearLayout movies_list_layout;

    public ActorMoviesView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void init() {
        movies_title = findViewById(R.id.movies_title);
        movies_list_layout = findViewById(R.id.movies_list_layout);
        movies_title.setOnClickListener(this);
        movies_scrollview = findViewById(R.id.movies_scrollview);

        movies_scrollview.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        View view = ((HorizontalScrollView) v).getChildAt(0);
                        // 判断是否滑动栏到底了，如果是，就加载下一页数据
                        if (view.getMeasuredWidth() <= v.getScrollX() + v.getWidth() + 2 && null != activity) {
                            activity.loadMoreFilmgraphies();
                        }
                        break;

                    default:
                        break;

                }
                return false;
            }
        });
        setOnClickListener(this);
    }

    public void cleanMoviesList() {
        if (null != movies_list_layout) {
            movies_list_layout.removeAllViews();
        }
    }

    public void addFilmography(final ActorViewActivity act, List<FilmographyBean> films) {
        if (null == activity) {
            activity = act;
        }
        movies_list_layout.setVisibility(View.VISIBLE);
        View converView;
        for (int i = 0; i < films.size(); i++) {
            final FilmographyBean bean = films.get(i);
            converView = LayoutInflater.from(activity).inflate(R.layout.actor_detail_movies_item, null);
            converView.setTag(String.valueOf(bean.getId()));
            if (activity.indexMovies == 2) {
                if (i != 0) {
                    converView.setPadding( MScreenUtils.dp2px(activity, 12.5f), 0, 0, 0);
                }
            } else {
                converView.setPadding( MScreenUtils.dp2px(activity, 12.5f), 0, 0, 0);
            }
            ImageView header = converView.findViewById(R.id.header);
            TextView name = converView.findViewById(R.id.name);
            TextView nameEn = converView.findViewById(R.id.nameEn);
            TextView year = converView.findViewById(R.id.year);
            activity.volleyImageLoader.displayImage(bean.getImage(), header, R.drawable.default_image, R.drawable.default_image, ImageURLManager.ImageStyle.STANDARD, null);

            name.setText(TextUtils.isEmpty(bean.getName()) ? " " : bean.getName());
            nameEn.setVisibility(GONE);
            year.setText(String.format(activity.getString(R.string.st_actor_info_format), bean.getYear()));

            movies_list_layout.addView(converView);

            converView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (TextUtils.isEmpty((String) arg0.getTag())) {
                        return;
                    }

                    Intent intent = new Intent();
                    final String movieId = (String) arg0.getTag();

                    // 埋点
                    Map<String, String> businessParam = new HashMap<>(2);
                    businessParam.put(StatisticConstant.PERSON_ID, activity.actorId);
                    businessParam.put(StatisticConstant.MOVIE_ID, movieId);
                    StatisticPageBean statisticBean1 = activity.assemble(StatisticActor.STAR_DETAIL_REPRESENTATIVE_WORK, "", "poster", "", "", "", businessParam);
                    StatisticManager.getInstance().submit(statisticBean1);

                    JumpUtil.startMovieInfoActivity(activity, statisticBean1.toString(), movieId, 0);
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        if(null==activity||null==activity.actorInfo){
            return;
        }

        // 埋点
        Map<String, String> businessParam = new HashMap<>(1);
        businessParam.put(StatisticConstant.PERSON_ID, activity.actorId);
        StatisticPageBean statisticBean2 = activity.assemble(StatisticActor.STAR_DETAIL_REPRESENTATIVE_WORK, "", "representativeWorkList", "", "", "", businessParam);
        StatisticManager.getInstance().submit(statisticBean2);

        String name = activity.actorInfo.getNameCn();
        name = TextUtils.isEmpty(name) ? activity.actorInfo.getNameEn() : name;
        JumpUtil.startActorFilmographyActivity(activity, statisticBean2.toString(), activity.actorId, name, activity.actorInfo.getMovieCount());
    }
}
