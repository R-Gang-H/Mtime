package com.mtime.bussiness.ticket.movie.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.base.statistic.StatisticConstant;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.bussiness.ticket.movie.activity.ActorViewActivity;
import com.mtime.bussiness.ticket.movie.bean.ActorHotMovie;
import com.mtime.bussiness.ticket.movie.bean.ActorInfoBean;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.statistic.large.ticket.StatisticActor;
import com.mtime.util.ImageURLManager;
import com.mtime.util.JumpUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by guangshun on 16/7/27.
 * 影人资料页－该人员正在上映的影片
 */
public class ActorHotPlayingView extends RelativeLayout implements View.OnClickListener {
    private ActorHotMovie movieData;
    private ActorInfoBean infoBean;
    private ActorViewActivity activity;
    private ImageView hot_playing_header;
    private TextView hot_playing_movie_title;
    private TextView hot_playing_grade;
    private TextView hot_playing_movie_type;
    private TextView hot_playing_movie_play;
    private TextView hot_playing_ticket_value;
    private TextView hot_playing_ticket_mark;
    private TextView hot_playing_ticket_lowest_tv;
    private TextView hot_playing_buy_ticket;

    public ActorHotPlayingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }
    private void init() {
        hot_playing_header = findViewById(R.id.hot_playing_header);
        hot_playing_movie_title = findViewById(R.id.hot_playing_movie_title);
        hot_playing_grade = findViewById(R.id.hot_playing_grade);
        hot_playing_movie_type = findViewById(R.id.hot_playing_movie_type);
        hot_playing_movie_play = findViewById(R.id.hot_playing_movie_play);
        hot_playing_ticket_value = findViewById(R.id.hot_playing_ticket_value);
        hot_playing_ticket_mark = findViewById(R.id.hot_playing_ticket_mark);
        hot_playing_ticket_lowest_tv = findViewById(R.id.hot_playing_ticket_lowest_tv);
        hot_playing_buy_ticket = findViewById(R.id.hot_playing_buy_ticket);
    }

    public void onDrawView(final ActorViewActivity act, final ActorInfoBean bean) {
        activity = act;
        infoBean=bean;
        movieData = bean.getHotMovie();
        if (null == movieData || movieData.getMovieId() < 1) {
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);
        setOnClickListener(this);
        activity.volleyImageLoader.displayImage(movieData.getMovieCover(), hot_playing_header, R.drawable.default_image, R.drawable.default_image, ImageURLManager.ImageStyle.STANDARD, null);
        hot_playing_movie_title.setText(movieData.getMovieTitleCn());


        if (movieData.getRatingFinal() > 0) {
            hot_playing_grade.setText(String.valueOf(movieData.getRatingFinal()));
        } else {
            hot_playing_grade.setVisibility(View.GONE);
        }
        hot_playing_movie_type.setText(movieData.getType());


        if (TextUtils.isEmpty(movieData.getRoleName())) {
            hot_playing_movie_play.setVisibility(View.GONE);
        } else {
            String value = String.format(this.getResources().getString(R.string.actor_detail_hot_playing_act),
                    movieData.getRoleName());
            hot_playing_movie_play.setText(value);
        }
        // 2020新版票务后台1期没有最低票价，所以暂时没有该值
        boolean showPrice = movieData.getTicketPrice() > 0;
        hot_playing_ticket_value.setVisibility(showPrice ? VISIBLE : GONE);
        hot_playing_ticket_value.setText(showPrice ? String.valueOf(movieData.getTicketPrice() / 100) : "");
        hot_playing_ticket_mark.setVisibility(showPrice ? VISIBLE : GONE);
        hot_playing_ticket_lowest_tv.setVisibility(showPrice ? VISIBLE : GONE);

        if (!movieData.isTiket()) {
            hot_playing_buy_ticket.setText(R.string.str_movie_info);
        }
        hot_playing_buy_ticket.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (null == movieData) {
            return;
        }

        String movieId = String.valueOf(movieData.getMovieId());
        // 埋点
        Map<String, String> businessParam = new HashMap<>(2);
        businessParam.put(StatisticConstant.PERSON_ID, activity.actorId);
        businessParam.put(StatisticConstant.MOVIE_ID, movieId);

        switch (view.getId()) {
            case R.id.hot_playing_buy_ticket:
                // 埋点
                StatisticPageBean statisticBean1 = activity.assemble(StatisticActor.STAR_DETAIL_TICKET, "", "ticketIcon", "", "", "", businessParam);
                StatisticManager.getInstance().submit(statisticBean1);

                JumpUtil.startMovieShowtimeActivity(activity, statisticBean1.toString(), movieId, movieData.getMovieTitleCn(), true, null, 0);
                break;
            default:
                // 埋点
                StatisticPageBean statisticBean2 = activity.assemble(StatisticActor.STAR_DETAIL_TICKET, "", "poster", "", "", "", businessParam);
                StatisticManager.getInstance().submit(statisticBean2);

                JumpUtil.startMovieInfoActivity(activity, statisticBean2.toString(), movieId, 0);
                break;
        }
    }
}
