package com.mtime.bussiness.ticket.movie.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.base.statistic.StatisticConstant;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.bussiness.ticket.bean.QuizGameBean;
import com.mtime.bussiness.ticket.bean.QuizGameUsersBean;
import com.mtime.mtmovie.widgets.CircleImageView;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.statistic.large.h5.StatisticH5;
import com.mtime.statistic.large.ticket.StatisticActor;
import com.mtime.util.ImageURLManager;
import com.mtime.util.JumpUtil;
import com.mtime.util.MallUrlHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by guangshun on 15-8-11.
 * 影片详情and影人详情猜电影
 */
public class QuizGameView extends RelativeLayout implements View.OnClickListener {

    private BaseActivity activity;

    private ImageView arrow;
    private RelativeLayout usersImg;
    private TextView gameName;
    private TextView gameNum;
    private TextView topUsers;
    private RelativeLayout topFirst;
    private RelativeLayout topSecond;
    private RelativeLayout topThird;
    private CircleImageView topFirstImg;
    private CircleImageView topSecondImg;
    private CircleImageView topThirdImg;

    private String mActorId;
    private String url;

    public QuizGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void init() {
        arrow = findViewById(R.id.arrow);
        usersImg = findViewById(R.id.users_img);
        gameName = findViewById(R.id.game_name);
        gameNum = findViewById(R.id.game_num);
        topUsers = findViewById(R.id.top_users);
        topFirst = findViewById(R.id.first);
        topSecond = findViewById(R.id.second);
        topThird = findViewById(R.id.third);
        topFirstImg = findViewById(R.id.first_img);
        topSecondImg = findViewById(R.id.second_img);
        topThirdImg = findViewById(R.id.third_img);
        arrow.setVisibility(GONE);
        setOnClickListener(this);
    }


    /**
     * 更新界面数据
     *
     * @param bean 接口返回的我猜游戏数据
     */
    public void onRefreshViev(BaseActivity activity, String actorId, final QuizGameBean bean) {
        this.activity = activity;
        this.mActorId = actorId;
        if (bean != null&&!TextUtils.isEmpty(bean.getUrl())) {
            url = bean.getUrl();
            setVisibility(VISIBLE);

            gameName.setText(bean.getSmallTitle());
            gameNum.setText(bean.getTitle());
            if (bean.getCount() < 10) {//当题目数小于10时，不显示用户排名
                arrow.setVisibility(VISIBLE);
                usersImg.setVisibility(GONE);
                topUsers.setVisibility(GONE);
                return;
            }
            usersImg.setVisibility(VISIBLE);
            topUsers.setVisibility(VISIBLE);
            arrow.setVisibility(GONE);
            topUsers.setText(bean.getTopMsg());
            List<QuizGameUsersBean> topUsers = bean.getTopUsers();
            if (topUsers == null || topUsers.size() == 0) {//没有用户数据
                return;
            }
            topFirst.setVisibility(GONE);
            topSecond.setVisibility(GONE);
            topThird.setVisibility(GONE);
            for (int i = 0; i < topUsers.size(); i++) {
                QuizGameUsersBean user = topUsers.get(i);
                if (i == 0 && user != null && !TextUtils.isEmpty(user.getHeadPic())) {
                    topFirst.setVisibility(VISIBLE);
                    activity.volleyImageLoader.displayImage(user.getHeadPic(), topFirstImg, R.drawable.head_star, R.drawable.head_star, ImageURLManager.ImageStyle.THUMB, null);
                } else if (i == 1 && user != null && !TextUtils.isEmpty(user.getHeadPic())) {
                    topSecond.setVisibility(VISIBLE);
                    activity.volleyImageLoader.displayImage(user.getHeadPic(), topSecondImg, R.drawable.head_star, R.drawable.head_star, ImageURLManager.ImageStyle.THUMB, null);
                } else if (i == 2 && user != null && !TextUtils.isEmpty(user.getHeadPic())) {
                    topThird.setVisibility(VISIBLE);
                    activity.volleyImageLoader.displayImage(user.getHeadPic(), topThirdImg, R.drawable.head_star, R.drawable.head_star, ImageURLManager.ImageStyle.THUMB, null);
                }
            }
        }

    }

    @Override
    public void onClick(View view) {
        if (!TextUtils.isEmpty(url)) {
            openview(activity, url);
        }

    }

    private void openview(BaseActivity activity, String url) {
        if (null == activity || TextUtils.isEmpty(url)) {
            return;
        }

        // 埋点
        Map<String, String> businessParam = new HashMap<>(2);
        businessParam.put(StatisticConstant.PERSON_ID, mActorId);
        businessParam.put(StatisticConstant.URL, url);
        StatisticPageBean statisticBean = activity.assemble(StatisticActor.STAR_DETAIL_GUESS_GAME, "", "", "", "", "", businessParam);
        StatisticManager.getInstance().submit(statisticBean);
        String refer = statisticBean.toString();

        MallUrlHelper.MallUrlType type = MallUrlHelper.getUrlType(url);
        switch (type) {
            case PRODUCT_VIEW: {
//                JumpUtil.startProductViewActivity(activity, refer, url);
                break;
            }
            case PRODUCTS_LIST:
            case PRODUCTS_LIST_SEARCH: {
//                JumpUtil.startProductListActivity(activity, refer, url, "");
                break;
            }
            default: {
//                JumpUtil.startAdvRecommendActivity(activity, refer, url,
//                        true, true, "", "", -1);
                JumpUtil.startCommonWebActivity(activity, url, StatisticH5.PN_H5, null,
                        true, true, true, false, refer);
                break;
            }
        }
    }
}
