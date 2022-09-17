package com.mtime.bussiness.ticket.movie.details.holder;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.R;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.bussiness.ticket.movie.bean.MovieHotLongCommentAllBean;
import com.mtime.bussiness.ticket.movie.details.adapter.MovieLongCommentAdapter;
import com.mtime.frame.App;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author vivian.wei
 * @date 2019/5/24
 * @desc 影片长影评列表页Holder
 */
public class MovieLongCommentListHolder extends ContentHolder<MovieHotLongCommentAllBean> {

    @BindView(R.id.layout_movie_sub_page_back_iv)
    ImageView mBackIv;
    @BindView(R.id.layout_movie_sub_page_title_tv)
    TextView mTitleTv;
    @BindView(R.id.activity_movie_long_comment_list_smartrefreshlayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.activity_movie_long_comment_list_recyclerview)
    RecyclerView mRecyclerView;

    private Unbinder mUnbinder;
    private MovieLongCommentAdapter mAdapter;
    private int mPageIndex = 1;
    private final MovieLongCommentAdapter.OnMovieLongCommentAdapterListener mOnMovieLongCommentAdapterListener;

    public MovieLongCommentListHolder(Context context, MovieLongCommentAdapter.OnMovieLongCommentAdapterListener onMovieLongCommentAdapterListener) {
        super(context);
        mOnMovieLongCommentAdapterListener = onMovieLongCommentAdapterListener;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onHolderCreated() {
        super.onHolderCreated();
        setContentView(R.layout.activity_movie_long_comment_list);
        initView();
        initListener();
    }

    private void initView() {
        mUnbinder = ButterKnife.bind(this, mRootView);

        mAdapter = new MovieLongCommentAdapter(null, mOnMovieLongCommentAdapterListener);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initListener() {
        mBackIv.setOnClickListener(this);
    }

    @Override
    public void refreshView() {
        super.refreshView();

        // 刷新数据
        if (null == mData || CollectionUtils.isEmpty(mData.getComments())) {
            return;
        }

        if(mPageIndex == 1) {
            mAdapter.getData().clear();
        }
        mAdapter.addData(mData.getComments());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_movie_sub_page_back_iv:
                onHolderEvent(App.MOVIE_SUB_PAGE_EVENT_CODE_BACK, null);
                break;
            default:
                break;
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

    // 设置标题
    public void setTitle(String title) {
        if(mTitleTv != null) {
            mTitleTv.setText(TextUtils.isEmpty(title) ? getResource().getString(R.string.st_long_comment_title) : title);
        }
    }

    // 设置页码
    public void setPageIndex(int pageIndex) {
        mPageIndex = pageIndex;
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

    // 设置是否启用上拉加载更多
    public void setEnableLoadMore(boolean enable) {
        if(null != mRefreshLayout) {
            if(enable) {
                mRefreshLayout.setEnableLoadMore(enable);
            } else {
                mRefreshLayout.finishLoadMoreWithNoMoreData();
            }
        }
    }

    // 添加刷新、加载更多事件Listener
    public void setRefreshLoadMoreListener(OnRefreshListener onRefreshListener, OnLoadMoreListener onLoadMoreListener) {
        mRefreshLayout.setOnRefreshListener(onRefreshListener);
        mRefreshLayout.setOnLoadMoreListener(onLoadMoreListener);
    }

    // 添加点击列表Listener
    public void setAdapterListener(OnItemClickListener onItemClickListener,
                                   OnItemChildClickListener onItemChildClickListener) {
        if(mAdapter != null) {
            mAdapter.setOnItemClickListener(onItemClickListener);
            mAdapter.setOnItemChildClickListener(onItemChildClickListener);
        }
    }

}
