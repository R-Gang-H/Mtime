package com.mtime.bussiness.daily.recommend.holder;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.View;
import android.widget.LinearLayout;

import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.OnLoadMoreListener;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.R;
import com.mtime.base.statistic.StatisticDataBuild;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.bussiness.daily.recommend.adapter.HistoryRcdAdapter;
import com.mtime.bussiness.daily.recommend.bean.DailyRecommendBean;
import com.mtime.bussiness.daily.widget.RcmdHistoryFloatingItemDecoration;
import com.mtime.mtmovie.widgets.pullrefresh.LoadMoreFooterView;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.statistic.large.dailyrecmd.StatisticDailyRecmd;
import com.mtime.util.JumpUtil;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * lys
 */
public class HistoryRecommendHolder extends ContentHolder<Void> implements HistoryRcdAdapter.OnItemClickListener {

    @BindView(R.id.history_recommend_recycler_view)
    public IRecyclerView mHistoryRecommendView;
    @BindView(R.id.history_recommend_empty_View)
    public LinearLayout mEmptyView;
    @BindView(R.id.history_recommend_root)
    public View mView;
    private Unbinder mUnbinder;
    private final String mRefer;
    private LoadMoreFooterView mLoadMoreFooterView;
    public HistoryRcdAdapter mAdapter;
    private RcmdHistoryFloatingItemDecoration mFloatingItemDecoration;

    public HistoryRecommendHolder(Context context, String refer) {
        super(context);
        mRefer = refer;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mUnbinder) {
            mUnbinder.unbind();
        }
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onHolderCreated() {
        super.onHolderCreated();
        setContentView(R.layout.act_history_recommend_layout);
        mUnbinder = ButterKnife.bind(this, mRootView);

        mFloatingItemDecoration = new RcmdHistoryFloatingItemDecoration(mContext);
        mFloatingItemDecoration.setTitleHeight(MScreenUtils.dp2px(30));
        mFloatingItemDecoration.setTitleTextColor(ContextCompat.getColor(mContext, R.color.color_8798af));
        mFloatingItemDecoration.setTitleTextSize(MScreenUtils.sp2px(14));
        mFloatingItemDecoration.setTtitleBackground(ContextCompat.getColor(mContext, R.color.white));
        mHistoryRecommendView.addItemDecoration(mFloatingItemDecoration);

        mLoadMoreFooterView = (LoadMoreFooterView) mHistoryRecommendView.getLoadMoreFooterView();
        mHistoryRecommendView.setLoadMorePreloadCount(8);
        mLoadMoreFooterView.setIsShowTheEnd(true);
        mHistoryRecommendView.setLayoutManager(new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false));
        mAdapter = new HistoryRcdAdapter(mContext, mFloatingItemDecoration);
        mHistoryRecommendView.setIAdapter(mAdapter);
        mHistoryRecommendView.setRefreshEnabled(false);
        mHistoryRecommendView.setLoadMoreEnabled(true);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setRefer(mRefer);
    }

    private void submitData4ClickItem(String movieId) {
        HashMap<String, String> params = new HashMap<>(1);
        params.put(StatisticDailyRecmd.MOVIE_ID, movieId);
        StatisticPageBean assemble = StatisticDataBuild.assemble(mRefer, StatisticDailyRecmd.HISTORY_RECOMMEND_PN
                , StatisticDailyRecmd.HISTORY_RCMD_LIST, null, StatisticDailyRecmd.POSTER,
                null, StatisticDailyRecmd.CLICK, null, params);
        StatisticManager.getInstance().submit(assemble);
    }

    public void setLoadMoreListener(OnLoadMoreListener laodMoreListener) {
        if (mHistoryRecommendView != null) {
            mHistoryRecommendView.setOnLoadMoreListener(laodMoreListener);
        }
    }

    public void setLoadMoreState(LoadMoreFooterView.Status status) {
        if (null != mLoadMoreFooterView) {
            mLoadMoreFooterView.setStatus(status);
        }
    }

    @Override
    public void onItemClick(DailyRecommendBean item) {
        submitData4ClickItem(item.movieId);
        JumpUtil.jumpDailyRecommendFromHistory(mContext, item, mRefer);
    }
}
