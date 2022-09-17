package com.mtime.bussiness.daily.recommend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.kk.taurus.uiframe.d.BaseState;
import com.kk.taurus.uiframe.v.BaseStateContainer;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.kk.taurus.uiframe.v.NoTitleBarContainer;
import com.kotlin.android.app.router.path.RouterActivityPath;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.base.utils.MTimeUtils;
import com.mtime.bussiness.daily.recommend.api.RecmdApi;
import com.mtime.bussiness.daily.recommend.bean.DailyRecommendBean;
import com.mtime.bussiness.daily.recommend.bean.HistoryMovieListBean;
import com.mtime.bussiness.daily.recommend.holder.RecommendHolder;
import com.mtime.frame.BaseFrameUIActivity;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.statistic.large.dailyrecmd.StatisticDailyRecmd;
import com.mtime.util.JumpUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2018-06-20
 */
@Route(path = RouterActivityPath.Daily.PAGER_RCMD)
public class RecommendActivity extends BaseFrameUIActivity<List<DailyRecommendBean>, RecommendHolder> implements StatisticDailyRecmd {

    private static final String KEY_OF_RECMD_BEAN = "KEY_OF_RECMD_BEAN";
    private static final String SP_NAME = "DAILY_RECMD_DIALOG";
    private RecommendHolder mHolder;
    private RecmdApi mApi;

    @Override
    protected BaseStateContainer getStateContainer() {
        return new NoTitleBarContainer(this, this, this);
    }

    @Override
    public ContentHolder onBindContentHolder() {
        return (mHolder = new RecommendHolder(this));
    }

    @Override
    protected void onInit(Bundle savedInstanceState) {
        super.onInit(savedInstanceState);
        DailyRecommendBean upbean = getIntent().getParcelableExtra(KEY_OF_RECMD_BEAN);

        if (upbean != null) {
            setPageLabel(ONE_DAY_RCMD);
            List<DailyRecommendBean> list = new ArrayList<>();
            setPageState(BaseState.SUCCESS);
            list.add(upbean);
            showData(list);
            return;
        }
        recordStartup();

        setPageLabel(DAILY_RECOMMEND_PN);
        mHolder.showCalendar();
        mApi = new RecmdApi();
        getDailyRecmd();
    }

    private void recordStartup() {
        SharedPreferences sp = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd", Locale.getDefault());
        String dtString = sdf.format(MTimeUtils.getLastServerDate());
        sp.edit().clear().putBoolean(dtString, true).apply();
    }

    private void getDailyRecmd() {
        setPageState(BaseState.LOADING);
        mApi.getDailyRecmd(new NetworkManager.NetworkListener<HistoryMovieListBean>() {
            @Override
            public void onSuccess(HistoryMovieListBean bean, String s) {
                if (CollectionUtils.isEmpty(bean.rcmdList)) {
                    setPageState(BaseState.ERROR);
                    return;
                }
                showData(bean.rcmdList);
                setPageState(BaseState.SUCCESS);
            }

            @Override
            public void onFailure(NetworkException<HistoryMovieListBean> e, String s) {
                setPageState(BaseState.ERROR);
            }
        });
    }

    @Override
    public void onHolderEvent(int eventCode, Bundle bundle) {
        super.onHolderEvent(eventCode, bundle);
        switch (eventCode) {
            case RecommendHolder.JUMP_HISTORY_RECOMMEND: {
                StatisticPageBean assemble = assemble(DAILY_RCMD_MOVIE, null, CALENDAR,
                        null, null, null, null);
                StatisticManager.getInstance().submit(assemble);
                JumpUtil.jumpHistoryRecommend(this, assemble.toString());
                break;
            }
            case RecommendHolder.SHARE_RECMD: {
                StatisticPageBean assemble = assemble(DAILY_RCMD_MOVIE, null, SHARE,
                        null, null, null, null);
                StatisticManager.getInstance().submit(assemble);
                break;
            }
            case RecommendHolder.DOWNLOAD_RECMD: {
                StatisticPageBean assemble = assemble(DAILY_RCMD_MOVIE, null, DOWNLOAD,
                        null, null, null, null);
                StatisticManager.getInstance().submit(assemble);
                break;
            }
            case RecommendHolder.POSTER_SHOW: {
                String movieId = bundle.getString("movieId");
                Map<String, String> param = new HashMap<>(1);
                param.put(MOVIE_ID, movieId);
                StatisticPageBean assemble = assemble(DAILY_RCMD_MOVIE, null, POSTER,
                        null, SHOW, null, param);
                StatisticManager.getInstance().submit(assemble);
                break;
            }
            case RecommendHolder.POSTER_CLICK: {
                String movieId = bundle.getString("movieId");
                Map<String, String> param = new HashMap<>(1);
                param.put(MOVIE_ID, movieId);
                StatisticPageBean assemble = assemble(DAILY_RCMD_MOVIE, null, POSTER,
                        null, CLICK, null, param);
                StatisticManager.getInstance().submit(assemble);
                JumpUtil.startMovieInfoActivity(this, assemble.toString(), movieId, 0);
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mApi != null) {
            mApi.cancel();
        }
    }

    @Override
    protected void onErrorRetry() {
        super.onErrorRetry();
        getDailyRecmd();
    }

    private void showData(List<DailyRecommendBean> data) {
        setData(data);
        mHolder.locatePosition(0);
    }

    public static void launchFromHome(Context context, String refre) {
        launchFromRecmdHistory(context, null, refre);
    }

    public static void launchFromRecmdHistory(Context context, DailyRecommendBean bean, String refre) {
        Intent intent = new Intent(context, RecommendActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (bean != null) {
            intent.putExtra(KEY_OF_RECMD_BEAN, bean);
        }
        dealRefer(context, refre, intent);
        context.startActivity(intent);
    }
}
