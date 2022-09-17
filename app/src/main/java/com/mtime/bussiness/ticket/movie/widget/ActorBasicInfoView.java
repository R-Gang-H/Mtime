package com.mtime.bussiness.ticket.movie.widget;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.mtime.frame.App;
import com.mtime.R;
import com.mtime.bussiness.ticket.movie.bean.ActorInfoBean;
import com.mtime.bussiness.ticket.movie.activity.ActorFilmographyActivity;
import com.mtime.bussiness.ticket.movie.activity.ActorViewActivity;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.statistic.large.ticket.StatisticActor;
import com.mtime.util.ImageURLManager;
import com.mtime.util.ToolsUtils;
import com.mtime.widgets.EllipsizingTextView;

/**
 * Created by guangshun on 16/7/25.
 * 影人资料页－基本资料
 */
public class ActorBasicInfoView extends RelativeLayout implements View.OnClickListener {
    private ActorViewActivity activity;
    private ActorInfoBean infoBean;
    private ImageView poster_background;
    private ImageView poster_header;
    private TextView liked_rate;
    private TextView name_china;
    private TextView name_en;
    private TextView birthday;
    private TextView addressHome;
    private TextView career;
    private ImageView iv_arrow_down;
    private EllipsizingTextView tv_introduction;

    public ActorBasicInfoView(Context context) {
        super(context);
    }

    public ActorBasicInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void init() {
        poster_background = findViewById(R.id.poster_background);
        poster_header = findViewById(R.id.poster_header);
        liked_rate = findViewById(R.id.like_rate);
        name_china = findViewById(R.id.name_china);
        name_en = findViewById(R.id.name_en);
        birthday = findViewById(R.id.birthday);
        addressHome = findViewById(R.id.address_home);
        career = findViewById(R.id.career);
        iv_arrow_down = findViewById(R.id.item_actor_introduction_iv_arrow_down);
        tv_introduction = findViewById(R.id.item_actor_introduction_tv_content);


        name_china.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (ToolsUtils.isTextOverFlowed(name_china) && null != activity && null != activity.showNameView) {
                    activity.showNameView.setVisibility(View.VISIBLE);
                }
            }
        });
        name_en.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (ToolsUtils.isTextOverFlowed(name_en) && null != activity && null != activity.showNameView) {
                    activity.showNameView.setVisibility(View.VISIBLE);
                }
            }
        });
        tv_introduction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != activity) {
                    // 埋点
                    StatisticPageBean statisticBean = activity.assemble(StatisticActor.STAR_DETAIL_BASIC_DATA, "", "viewDetails", "", "", "", activity.getBaseStatisticParam());
                    StatisticManager.getInstance().submit(statisticBean);

                    tv_introduction.toggleShowLines();
                }
            }
        });
        iv_arrow_down.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != activity) {
                    // 埋点
                    StatisticPageBean statisticBean = activity.assemble(StatisticActor.STAR_DETAIL_BASIC_DATA, "", "viewDetails", "", "", "", activity.getBaseStatisticParam());
                    StatisticManager.getInstance().submit(statisticBean);

                    tv_introduction.toggleShowLines();
                }
            }
        });
        tv_introduction.addEllipsizeListener(new EllipsizingTextView.EllipsizeListener() {

            @Override
            public void ellipsizeStateChanged(final boolean ellipsized) {
                if (ellipsized) {
                    iv_arrow_down.setImageResource(R.drawable.actor_honors_arrow_down);
                } else {
                    if (tv_introduction.LineCount() > tv_introduction.getMaxLines()) {
                        iv_arrow_down.setImageResource(R.drawable.new_arrow_up);
                    } else {
                        iv_arrow_down.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    public void onDrawView(ActorViewActivity act, ActorInfoBean bean) {
        activity = act;
        if (null==act){
            return;
        }
        infoBean = bean;
        if (null != activity && !activity.isDestroyed()) {
            activity.volleyImageLoader.displayImage(bean.getImage(), poster_header, R.drawable.default_image, R.drawable.default_image, ImageURLManager.ImageStyle.STANDARD, null);
            activity.volleyImageLoader.displayBlurImg(activity, bean.getImage(), poster_background, 25, 10, R.drawable.default_image, R.drawable.default_image, null);
        }

        String value;
        double rate = 0;
        try {
            rate = Double.valueOf(bean.getRatingFinal());
        } catch (Exception e) {
        }

        if (rate > 0) {
            StringBuffer sb = new StringBuffer();
            sb.append("喜爱度 ").append((int) (rate * 10)).append("%");
            liked_rate.setText(sb.toString());
            liked_rate.setVisibility(View.VISIBLE);
        } else {
            liked_rate.setVisibility(View.INVISIBLE);
        }

        boolean emptyCN = TextUtils.isEmpty(bean.getNameCn());
        name_china.setText(emptyCN ? bean.getNameEn() : bean.getNameCn());
        if (name_china.getText() != null) {
//            SearchHistorySingleBean historySingleBean = new SearchHistorySingleBean();
//            historySingleBean.setType(SearchUtil.SEARCH_TYPE_ACTOR);
//            historySingleBean.setId(activity.actorId);
//            historySingleBean.setName(name_china.getText().toString());
//            SearchUtil.saveSearchBrowseHistories(historySingleBean);
        }

        if (emptyCN) {
            name_en.setVisibility(View.INVISIBLE);
            activity.showNameView.setLabels(bean.getNameEn(), null);
        } else {
            name_en.setText(bean.getNameEn());
            activity.showNameView.setLabels(bean.getNameCn(), bean.getNameEn());
        }


        StringBuffer sb = new StringBuffer();
        if (bean.getBirthYear() > 0) {
            sb.append(bean.getBirthYear());
        }

        if (bean.getBirthMonth() > 0) {
            sb.append("-");
            value = String.format(activity.getString(R.string.st_actor_info_format_birthday), bean.getBirthMonth());
            sb.append(value);
        }

        if (sb.length() > 1 && bean.getBirthDay() > 0) {
            sb.append("-");
            value = String.format(activity.getString(R.string.st_actor_info_format_birthday), bean.getBirthDay());
            sb.append(value);
        }

        if (sb.length() > 1 && bean.getDeathYear() > 0) {
            sb.append("~");
            sb.append(bean.getDeathYear());
            if (bean.getDeathMonth() > 0) {
                sb.append("-");
                value = String.format(activity.getString(R.string.st_actor_info_format_birthday), bean.getDeathMonth());
                sb.append(value);
                if (sb.length() > 1 && bean.getDeathDay() > 0) {
                    sb.append("-");
                    value = String.format(activity.getString(R.string.st_actor_info_format_birthday), bean.getDeathDay());
                    sb.append(value);
                }
            }
        }
        birthday.setText(sb.toString());
        addressHome.setText(!TextUtils.isEmpty(bean.getAddress()) ? bean.getAddress() : "");
        career.setText(bean.getProfession());

        if (TextUtils.isEmpty(bean.getContent())) {
            tv_introduction.setVisibility(View.GONE);
            iv_arrow_down.setVisibility(View.GONE);
            return;
        }

        tv_introduction.setVisibility(View.VISIBLE);
        iv_arrow_down.setVisibility(View.VISIBLE);
        String content = bean.getContent().trim();
        content = content.replaceFirst("^[　*| *]*", "").trim();
        tv_introduction.setText(String.format(activity.getString(R.string.st_actor_info_format_introduction), content));
    }

    public ImageView getPosterBackground() {
        return poster_background;
    }

    @Override
    public void onClick(View view) {
        if (null == activity) {
            return;
        }
        final Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.movies_title:
                if (null == infoBean) {
                    return;
                }
                final Intent intent1 = new Intent();
                String name = infoBean.getNameCn();
                name = TextUtils.isEmpty(name) ? infoBean.getNameEn() : name;
                intent1.putExtra(App.getInstance().KEY_MOVIE_PERSOM_ID, activity.actorId);
                intent1.putExtra(App.getInstance().KEY_MOVIE_PERSOM_NAME, name);
                intent1.putExtra(App.getInstance().KEY_MOVIE_PERSOM_WORK_COUNT, infoBean.getMovieCount());
                activity.startActivity(ActorFilmographyActivity.class, intent1);
                break;
        }
    }
}
