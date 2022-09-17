package com.mtime.bussiness.video.holder;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.R;
import com.mtime.bussiness.video.CategoryDataManager;
import com.mtime.bussiness.video.ScrollSpeedLinearLayoutManager;
import com.mtime.bussiness.video.adapter.RecommendListAdapter;
import com.mtime.bussiness.video.bean.CategoryVideosBean;
import com.mtime.bussiness.video.event.PlayEvent;
import com.mtime.bussiness.video.event.RecommendListOperatingEvent;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by JiaJunHui on 2018/2/28.
 */

public class RecommendCategoryVideoListHolder extends ContentHolder<CategoryVideosBean> implements OnLoadMoreListener, RecommendListAdapter.OnRecommendListListener {

    public static final int EVENT_CODE_LOAD_MORE = 101;

    @BindView(R.id.fragment_category_video_list_recycler)
    RecyclerView mRecycler;
    @BindView(R.id.fragment_category_video_list_refresh_layout)
    SmartRefreshLayout mRefreshLayout;

    private RecommendListAdapter mAdapter;

    private final List<CategoryVideosBean.RecommendVideoItem> mVideoItems = new ArrayList<>();
    private Unbinder unbinder;
    private boolean move;

    private int mListIndex = -1;

    private boolean hasScrollToCurr;

    private CategoryVideosBean.Category mCategory;

    public RecommendCategoryVideoListHolder(Context context) {
        super(context);
    }

    @Override
    public void onCreate() {
        setContentView(R.layout.fragment_recommend_category_video_list);
        unbinder = ButterKnife.bind(this, mRootView);
    }

    @Override
    public void onHolderCreated() {
        super.onHolderCreated();
        mRefreshLayout.setEnableRefresh(false);
        mRefreshLayout.setOnLoadMoreListener(this);
        mRecycler.setLayoutManager(new ScrollSpeedLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                EventBus.getDefault().post(new RecommendListOperatingEvent());
            }
        });
        mAdapter = new RecommendListAdapter(mContext, mVideoItems);
        mAdapter.setOnRecommendListListener(this);
        mRecycler.setAdapter(mAdapter);
    }

    public void setCategory(CategoryVideosBean.Category mCategory) {
        this.mCategory = mCategory;
        notifyCurrIndex();
    }

    public boolean hasListData() {
        return mVideoItems != null && mVideoItems.size() > 0;
    }

    public void finishLoadMore() {
        mRefreshLayout.finishLoadMore();
    }

    public void noMoreData() {
        mRefreshLayout.finishLoadMoreWithNoMoreData();
    }

    public void notifyCurrIndex(){
        int index = 0;
        int categoryType = CategoryDataManager.get().getCurrentCategoryType();
        if(categoryType==mCategory.getType()){
            index = CategoryDataManager.get().getListIndex(categoryType);
        }else{
            index = -1;
        }
        this.mListIndex = index;
        mAdapter.setCategory(mCategory);
        mAdapter.setCheckIndex(mListIndex);
        mAdapter.notifyDataSetChanged();
        hasScrollToCurr = false;
    }

    @Override
    public void refreshView() {
        super.refreshView();
        List<CategoryVideosBean.RecommendVideoItem> videoList = mData.getVideoList();
        if(videoList!=null && videoList.size()>0){
            mVideoItems.addAll(videoList);
            mAdapter.setCheckIndex(mListIndex);
            mAdapter.notifyDataSetChanged();

            if(!hasScrollToCurr){
                hasScrollToCurr = true;
                mRecycler.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollToPosition(mListIndex);
                    }
                });
            }
        }
    }

    private void scrollToPosition(final int p){
        final LinearLayoutManager manager = (LinearLayoutManager) mRecycler.getLayoutManager();
        int fir = manager.findFirstVisibleItemPosition();
        int end = manager.findLastVisibleItemPosition();
        if (p <= fir) {
            mRecycler.scrollToPosition(p);
        } else if (p <= end) {
            int top = mRecycler.getChildAt(p - fir).getTop();
            mRecycler.scrollBy(0, top);
        } else {
            mRecycler.scrollToPosition(p);    //先让当前view滚动到列表内
            move = true;
        }
        mRecycler.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (move) {
                    move = false;
                    int n = p - manager.findFirstVisibleItemPosition();
                    if (n >= 0 && n < recyclerView.getChildCount()) {
                        recyclerView.scrollBy(0, recyclerView.getChildAt(n).getTop()); //滚动到顶部
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(unbinder!=null){
            unbinder.unbind();
        }
    }

    @Override
    public void onLoadMore(RefreshLayout refreshlayout) {
        onHolderEvent(EVENT_CODE_LOAD_MORE, null);
    }

    @Override
    public void onItemClick(RecommendListAdapter.RecommendItemHolder holder, int position) {
        CategoryDataManager.get().updateCurrentCategoryType(mCategory.getType());
        CategoryDataManager.get().updateListIndex(mCategory.getType(), position);
        EventBus.getDefault().post(new PlayEvent(mCategory.getType(), position));
    }

}
