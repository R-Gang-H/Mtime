package com.mtime.bussiness.daily.recommend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.aspsine.irecyclerview.OnLoadMoreListener;
import com.kk.taurus.uiframe.d.BaseState;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.R;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.utils.MNetworkUtils;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.daily.recommend.api.RecmdApi;
import com.mtime.bussiness.daily.recommend.bean.HistoryMovieListBean;
import com.mtime.bussiness.daily.recommend.bean.HistoryRecommendBean;
import com.mtime.bussiness.daily.recommend.holder.HistoryRecommendHolder;
import com.mtime.bussiness.daily.recommend.util.TimeHandler;
import com.mtime.frame.BaseFrameUIActivity;
import com.mtime.mtmovie.widgets.pullrefresh.LoadMoreFooterView;
import com.mtime.statistic.large.dailyrecmd.StatisticDailyRecmd;

import java.util.ArrayList;
import java.util.List;

/**
 * 历史推荐
 */
public class HistoryRecommendActivity extends BaseFrameUIActivity<Void, HistoryRecommendHolder> implements OnLoadMoreListener, StatisticDailyRecmd {

    private String endTime;
    private RecmdApi mApi;
    private HistoryRecommendHolder mHolder;
    private boolean hasMore = false;
    private boolean isFirst = true;
    private final List<HistoryMovieListBean> mItems = new ArrayList<>();

    public static void launch(Context context, String refre) {
        Intent launcher = new Intent(context, HistoryRecommendActivity.class);
        if (!(context instanceof Activity)) {
            launcher.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        dealRefer(context, refre, launcher);
        context.startActivity(launcher);
    }

    @Override
    public ContentHolder onBindContentHolder() {
        return (mHolder = new HistoryRecommendHolder(this, getRefer()));
    }

    @Override
    protected void onInit(Bundle savedInstanceState) {
        super.onInit(savedInstanceState);
        setPageLabel(HISTORY_RECOMMEND_PN);

        mHolder.setLoadMoreListener(this);
    }

    private void init() {
        setPageState(BaseState.LOADING);
        loadHistoryRecommendData();
    }

    private void loadHistoryRecommendData() {
        if (null == mApi) {
            mApi = new RecmdApi();
        }
        if (MNetworkUtils.isNetConnected(this)) {
            mApi.getHistoryRecmd(endTime, new NetworkManager.NetworkListener<HistoryRecommendBean>() {
                @Override
                public void onSuccess(HistoryRecommendBean result, String showMsg) {
                    setPageState(BaseState.SUCCESS);
                    if (null != result && null != result.historyMovie && result.historyMovie.size() > 0) {
                        hasMore = result.hasMore();
                        endTime = TimeHandler.getPrevTime(result.historyMovie.get(result.historyMovie.size() - 1).month);
                        handleData(result.historyMovie);
                        mHolder.mEmptyView.setVisibility(View.GONE);
                    } else {
                        if (isFirst) {
                            mHolder.mEmptyView.setVisibility(View.VISIBLE);
                        } else {
                            hasMore = false;
                        }
                    }
                    if (!hasMore) {
                        mHolder.mView.invalidate();
                        mHolder.setLoadMoreState(LoadMoreFooterView.Status.THE_END);
                    }
                    isFirst = false;
                }

                @Override
                public void onFailure(NetworkException<HistoryRecommendBean> exception, String showMsg) {
                    MToastUtils.showShortToast(showMsg);
                    cancelState();
                    if (isFirst) {
                        setPageState(BaseState.ERROR);
                    }
                }
            });
        } else {
            if (isFirst || mHolder.mAdapter.getmRcdList().size() == 0) {
                setPageState(BaseState.ERROR);
            }
            cancelState();
            MToastUtils.showShortToast(getResources().getString(R.string.str_no_connection));
        }

    }

    @Override
    protected void onLoadState() {
        super.onLoadState();
        init();
    }

    @Override
    protected void onErrorRetry() {
        setPageState(BaseState.LOADING);
        loadHistoryRecommendData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mApi) {
            mApi.cancel();
        }
    }

    /**
     * 更新界面数据
     */
    private void handleData(List<HistoryMovieListBean> totalList) {
        mHolder.mAdapter.addList(totalList);
    }

    @Override
    public void onLoadMore(View loadMoreView) {
        if (hasMore) {
            if (isFirst) {
                setPageState(BaseState.LOADING);
            }
            mHolder.setLoadMoreState(LoadMoreFooterView.Status.LOADING);
            loadHistoryRecommendData();
        }
    }

    private void cancelState() {
        mHolder.setLoadMoreState(LoadMoreFooterView.Status.GONE);
    }
}
