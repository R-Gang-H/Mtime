package com.mtime.bussiness.common.widget;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.helen.obfuscator.IObfuscateKeepAll;
import com.mtime.R;
import com.mtime.base.recyclerview.CommonViewHolder;

import me.drakeet.multitype.ItemViewBinder;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * @author ZhouSuQiang
 * @date 2018/10/13
 * <p>
 * 适配MultiTypeAdapter的加载更多帮助类
 */
public class MultiTypeLoadMoreHelper extends ItemViewBinder<MultiTypeLoadMoreHelper.LoadMoreBean, CommonViewHolder> {
    /**
     * 正在加载
     */
    private static final int STATE_LOADING = 0x0;
    /**
     * 加载完成
     */
    private static final int STATE_FINISH = 0x1;
    /**
     * 加载失败
     */
    private static final int STATE_FAILED = 0x2;
    /**
     * 没有更多数据
     */
    private static final int STATE_ALLLOADED = 0x3;
    
    public static String REFRESH_FOOTER_PULLUP = "上拉加载更多";
    public static String REFRESH_FOOTER_RELEASE = "释放立即加载";
    public static String REFRESH_FOOTER_LOADING = "正在加载...";
    public static String REFRESH_FOOTER_REFRESHING = "正在刷新...";
    public static String REFRESH_FOOTER_FINISH = "加载完成";
    public static String REFRESH_FOOTER_FAILED = "加载失败！滑动重新加载";
    public static String REFRESH_FOOTER_ALLLOADED = "没有更多数据了";
    
    private final RecyclerView mRecyclerView;
    private int mCurState = STATE_FINISH;
    private final LoadMoreBean mLoadMoreBean = new LoadMoreBean();
    private CommonViewHolder mViewHolder;
    private final LoadMoreListener mListener;
    private int mBeforeCount = 0;
    
    public MultiTypeLoadMoreHelper(@NonNull RecyclerView recyclerView, @NonNull MultiTypeAdapter adapter, @NonNull LoadMoreListener listener) {
        mRecyclerView = recyclerView;
        mListener = listener;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                final boolean loadable = mCurState == STATE_FINISH || mCurState == STATE_FAILED;
                if (loadable && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int lastPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                    if (lastPosition >= getAdapter().getItemCount() - 1 - mBeforeCount) {
                        onLoadMore();
                    }
                }
            }
        });
        adapter.register(LoadMoreBean.class, this);
        
        REFRESH_FOOTER_ALLLOADED = recyclerView.getContext().getString(R.string.footer_end);
    }
    
    @NonNull
    @Override
    protected CommonViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return CommonViewHolder.get(parent.getContext(), parent, R.layout.layout_load_more);
    }
    
    @Override
    protected void onBindViewHolder(@NonNull CommonViewHolder holder, @NonNull LoadMoreBean item) {
        holder.setVisibility(R.id.layout_load_more_progress_bar,
                (mCurState == STATE_ALLLOADED || mCurState == STATE_FAILED) ? View.GONE : View.VISIBLE)
                .setText(R.id.layout_load_more_text_tv, getHintStr());
        mViewHolder = holder;
    }
    
    private String getHintStr() {
        String hintStr;
        if (mCurState == STATE_ALLLOADED) {
            hintStr = REFRESH_FOOTER_ALLLOADED;
        } else if (mCurState == STATE_FAILED) {
            hintStr = REFRESH_FOOTER_FAILED;
        } else {
            hintStr = REFRESH_FOOTER_LOADING;
        }
        return hintStr;
    }
    
    @Override
    protected void onViewDetachedFromWindow(@NonNull CommonViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }
    
    public void setBeforeCount(int beforeCount) {
        mBeforeCount = beforeCount;
    }
    
    public void clearState() {
        mCurState = STATE_FINISH;
    }
    
    private void onLoadMore() {
        loading();
        if (null != mListener) {
            mListener.onLoadMore();
        }
    }
    
    private void loading() {
        int oldState = mCurState;
        mCurState = STATE_LOADING;
        
        Items items = (Items) getAdapter().getItems();
        if (oldState == STATE_FINISH && !items.contains(mLoadMoreBean)) {
            items.add(mLoadMoreBean);
            getAdapter().notifyItemInserted(items.size() - 1);
            mRecyclerView.scrollToPosition(items.size() - 1);
        }
        
        if (null != mViewHolder) {
            mViewHolder.setVisible(R.id.layout_load_more_progress_bar)
                    .setText(R.id.layout_load_more_text_tv, REFRESH_FOOTER_LOADING);
        }
    }
    
    public void finishLoadMore() {
        finishLoadMore(true);
    }
    
    public void finishLoadMore(boolean succeed) {
        if (isLoading() && null != mViewHolder) {
            mCurState = succeed ? STATE_FINISH : STATE_FAILED;
            
            String showText = succeed ? REFRESH_FOOTER_FINISH : REFRESH_FOOTER_FAILED;
            mViewHolder.setGone(R.id.layout_load_more_progress_bar)
                    .setText(R.id.layout_load_more_text_tv, showText);
            if (succeed) {
                Items items = (Items) getAdapter().getItems();
                int index = items.indexOf(mLoadMoreBean);
                items.remove(mLoadMoreBean);
                getAdapter().notifyItemRemoved(index);
            }
        }
    }
    
    public void finishLoadMore(boolean succeed, boolean noMoreData) {
        if (noMoreData) {
            finishLoadMoreWithNoMoreData();
        } else {
            finishLoadMore(succeed);
        }
    }
    
    public void finishLoadMoreWithNoMoreData() {
        mCurState = STATE_ALLLOADED;
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                Items items = (Items) getAdapter().getItems();
                if (null != mViewHolder) {
                    int index = items.indexOf(mLoadMoreBean);
                    if (index != items.size() - 1) {
                        items.remove(mLoadMoreBean);
                        items.add(mLoadMoreBean);
                        getAdapter().notifyItemMoved(index, items.size() - 1);
                    }
                } else {
                    items.add(mLoadMoreBean);
                    getAdapter().notifyItemInserted(items.size() - 1);
                }
            }
        }, 500);
        if (null != mViewHolder) {
            mViewHolder.setGone(R.id.layout_load_more_progress_bar)
                    .setText(R.id.layout_load_more_text_tv, REFRESH_FOOTER_ALLLOADED);
        }
    }
    
    public boolean isLoading() {
        return mCurState == STATE_LOADING;
    }
    
    public static class LoadMoreBean implements IObfuscateKeepAll {
    }
    
    public interface LoadMoreListener {
        void onLoadMore();
    }
    
}
