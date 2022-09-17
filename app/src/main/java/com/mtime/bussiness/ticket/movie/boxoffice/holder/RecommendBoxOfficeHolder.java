package com.mtime.bussiness.ticket.movie.boxoffice.holder;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.R;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.bussiness.ticket.movie.boxoffice.bean.HomeBoxOfficeTabListDetailBean;
import com.mtime.bussiness.ticket.movie.details.adapter.RecommendBoxofficeAdapter;
import com.mtime.bussiness.ticket.movie.details.widget.MovieActorFloatingItemDecoration;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author vivian.wei
 * @date 2019/6/21
 * @desc 推荐票房榜Holder
 */
public class RecommendBoxOfficeHolder extends ContentHolder<HomeBoxOfficeTabListDetailBean> {

    @BindView(R.id.fragment_recommend_boxoffice_smartrefreshlayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.fragment_recommend_boxoffice_recyclerview)
    RecyclerView mRecyclerView;

    private Unbinder mUnbinder;
    private RecommendBoxofficeAdapter mAdapter;
    private MovieActorFloatingItemDecoration mFloatingItemDecoration;
    private final OnItemClickListener mOnItemClickListener;
    private final OnItemChildClickListener mOnItemChildClickListener;

    public RecommendBoxOfficeHolder(Context context, OnItemClickListener onItemClickListener,
                                    OnItemChildClickListener onItemChildClickListener) {
        super(context);
        mOnItemClickListener = onItemClickListener;
        mOnItemChildClickListener = onItemChildClickListener;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onHolderCreated() {
        super.onHolderCreated();
        setContentView(R.layout.fragment_recommend_boxoffice);
        initView();
        initListener();
    }

    private void initView() {
        mUnbinder = ButterKnife.bind(this, mRootView);

        mAdapter = new RecommendBoxofficeAdapter(null);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);
        // 列表分组
        mFloatingItemDecoration = new MovieActorFloatingItemDecoration(mContext);
        mFloatingItemDecoration.setTitleHeight(MScreenUtils.dp2px(30));
        mFloatingItemDecoration.setTtitleBackground(ContextCompat.getColor(mContext, R.color.color_F2F3F6_alpha_92));
        mFloatingItemDecoration.setTitleTextColor(ContextCompat.getColor(mContext, R.color.color_8798AF));
        mFloatingItemDecoration.setTitleTextSize(MScreenUtils.sp2px( 12));
        mFloatingItemDecoration.setTitleTextGravity(true);
        mRecyclerView.addItemDecoration(mFloatingItemDecoration);

        mRefreshLayout.setEnableLoadMore(false);
    }

    // 初始化事件
    private void initListener() {
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mAdapter.setOnItemChildClickListener(mOnItemChildClickListener);
    }

    @Override
    public void refreshView() {
        super.refreshView();

        if (mData == null) {
            return;
        }
        if(mData.getTopList() == null && CollectionUtils.isEmpty(mData.getMovies())) {
            return;
        }

        if(CollectionUtils.isNotEmpty(mData.getMovies())) {
            mAdapter.getData().clear();
            // 数据分组
            if(null != mFloatingItemDecoration) {
                mFloatingItemDecoration.appendTitles(0, mData.getTopList() != null ? mData.getTopList().getSummary() : "");
            }
            mAdapter.addData(mData.getMovies());
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (null != mUnbinder) {
            mUnbinder.unbind();
        }
    }

    // 添加刷新、加载更多事件Listener
    public void setRefreshLoadMoreListener(OnRefreshListener onRefreshListener) {
        mRefreshLayout.setOnRefreshListener(onRefreshListener);
    }

    // 完成刷新
    public void finishRefresh(boolean success) {
        if(null != mRefreshLayout) {
            if(mRefreshLayout.getState() == RefreshState.Refreshing) {
                mRefreshLayout.finishRefresh(success);
            }
            if(mRefreshLayout.getState() == RefreshState.Loading) {
                if(success) {
                    mRefreshLayout.finishLoadMore(success);
                } else {
                    mRefreshLayout.finishLoadMore(1000, success, false);
                }
            }
        }
    }

}
