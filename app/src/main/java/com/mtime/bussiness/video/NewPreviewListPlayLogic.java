package com.mtime.bussiness.video;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewTreeObserver;

import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.log.PLog;
import com.kk.taurus.playerbase.receiver.GroupValue;
import com.kk.taurus.playerbase.receiver.OnReceiverEventListener;
import com.mtime.bussiness.video.adapter.NewCategoryVideoListAdapter;
import com.mtime.bussiness.video.bean.CategoryVideosBean;
import com.mtime.player.DataInter;
import com.mtime.player.PlayerHelper;
import com.mtime.player.bean.MTimeVideoData;

import java.util.List;

/**
 * Created by JiaJunHui on 2018/6/19.
 */
public class NewPreviewListPlayLogic implements OnReceiverEventListener, OnPlayerEventListener {

    private final String TAG = "PreviewListPlayLogic";

    final int MIN_PAGE_SIZE = 5;

    private final Context mContext;
    private final List<CategoryVideosBean.RecommendVideoItem> mVideoItems;
    private final RecyclerView mRecyclerView;
    private final NewCategoryVideoListAdapter mListAdapter;

    private int mPlayPos = -1;

    private OnLoadJudgeListener mOnLoadJudgeListener;

    private int mVerticalRecyclerStart;

    private CategoryVideosBean.Category mCategory;

    private final int mScreenH;
    private boolean mLoadMoreEnable = true;

    private boolean isLandScape;

    public NewPreviewListPlayLogic(RecyclerView recyclerView, NewCategoryVideoListAdapter adapter, List<CategoryVideosBean.RecommendVideoItem> items) {
        mContext = recyclerView.getContext();
        this.mRecyclerView = recyclerView;
        this.mListAdapter = adapter;
        this.mVideoItems = items;
        mScreenH = PlayerHelper.getScreenMaxH(mContext);
        init();
        setRecyclerListener();
    }

    public void setOnLoadJudgeListener(OnLoadJudgeListener onLoadJudgeListener) {
        this.mOnLoadJudgeListener = onLoadJudgeListener;
    }

    private void init() {
        PreviewVideoPlayer.get().addOnReceiverEventListener(this);
        PreviewVideoPlayer.get().addOnPlayerEventListener(this);
        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int[] location = new int[2];
                mRecyclerView.getLocationOnScreen(location);
                mVerticalRecyclerStart = location[1];
                mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void setRecyclerListener() {
        /**
         * 设置滚动模式为快速
         */
        getLayoutManager().setSpeedFast();
        mRecyclerView.addOnScrollListener(onScrollListener);
    }

    private final RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            /**
             * 当滚动停止时，计算播放
             */
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                PLog.d(TAG, "onScrollStateChanged ... IDLE-->>");
                onScrollStopJudgePlay();
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            judgeCurrentItemHolder(dx, dy);
        }
    };

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {

    }

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {

    }

    public void setCategory(CategoryVideosBean.Category category) {
        this.mCategory = category;
    }

    private boolean isCurrentCategory(int type) {
        return mCategory != null && mCategory.getType() == type;
    }

    private boolean isCurrentCategoryVisible() {
        return mOnLoadJudgeListener != null && mOnLoadJudgeListener.isPageVisible();
    }

    public void play(int position) {
        if (!isCurrentCategoryVisible() || position < 0 || position > getItemCount() - 1)
            return;
        boolean completeVisible = isCompleteVisibleRect(position);
        if (!completeVisible) {
            scrollToPlay(position);
        } else {
            playItemHolder(position);
        }
    }

    public void onConfigChanged(Configuration configuration) {
        isLandScape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    private boolean isLegalState() {
        GroupValue groupValue = PreviewVideoPlayer.get().getGroupValue();
        if (groupValue != null) {
            boolean isErrorState = groupValue.getBoolean(DataInter.Key.KEY_ERROR_SHOW_STATE);
            boolean isListComplete = groupValue.getBoolean(DataInter.Key.KEY_LIST_COMPLETE);
            return !isErrorState && !isListComplete;
        }
        return false;
    }

    public void onNextAttach(int index) {
        PLog.d(TAG, "onNextAttach : index = " + index);
        boolean completeVisible = isCompleteVisibleRect(index);
        if (completeVisible) {
            mPlayPos = index;
            attachItemHolder(getItemHolder(index));
        } else {
            scrollToAttach(index);
        }
    }

    public void portraitAttachCurrent() {
        int listIndex = CategoryDataManager.get().getListIndex(mCategory.getType());
        PLog.d(TAG, "portraitAttachCurrent : position = " + listIndex);
        boolean completeVisible = isCompleteVisibleRect(listIndex);
        if (completeVisible) {
            attachItemHolder(listIndex);
        } else {
            scrollToAttach(listIndex);
        }
    }

    private void judgeCurrentItemHolder(int dx, int dy) {

    }

    private void onScrollStopJudgePlay() {
        if (!isCurrentCategoryVisible())
            return;
        PreviewVideoPlayer.get().updateAutoPlayFlag(true);
        if (mPlayPos < 0) {
            PLog.d(TAG, "onScrollStopJudgePlay : play first");
            playItemHolder(0);
        } else {
            boolean isCompleteVisible = isCompleteVisibleRect(mPlayPos);
            boolean availablePlay = isVisibleRectAvailablePlay(mPlayPos);
            int firstCompleteVisiblePosition = getFirstCompleteVisiblePosition();
            if (!isCompleteVisible && !availablePlay) {
                PLog.d(TAG, "onScrollStopJudgePlay : play position = " + firstCompleteVisiblePosition);
                playItemHolder(firstCompleteVisiblePosition);
            }
        }
    }

    public void openListPlay() {
        int currentCategoryType = CategoryDataManager.get().getCurrentCategoryType();
        if (mCategory == null && !isCurrentCategoryVisible() && !isCurrentCategory(currentCategoryType))
            return;
        int listIndex = CategoryDataManager.get().getListIndex(currentCategoryType);

        CategoryVideosBean.RecommendVideoItem item = getItem(listIndex);
        if (item == null)
            return;
        boolean isEqualDataSource = PreviewVideoPlayer.get().isEqualDataSource(String.valueOf(item.getvId()), String.valueOf(item.getVideoSource()));

        if (isEqualDataSource) {
            boolean completeVisible = isCompleteVisibleRect(listIndex);
            if (completeVisible) {
                if (PreviewVideoPlayer.get().isInPlaybackState()) {
                    mPlayPos = listIndex;
                    attachItemHolder(getItemHolder(listIndex));
                } else {
                    playItemHolder(listIndex);
                }
            } else {
                if (PreviewVideoPlayer.get().isInPlaybackState()) {
                    scrollToAttach(listIndex);
                } else {
                    scrollToPlay(listIndex);
                }
            }
        } else {
            scrollToPlay(listIndex);
        }

    }

    //use this method, the holder must visible
    private void attachItemHolder(int position) {
        if (!isCurrentCategoryVisible())
            return;
        mPlayPos = position;
        attachItemHolder(getItemHolder(position));
    }

    private void scrollToAttach(int position) {
        scrollToPosition(position);
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                attachItemHolder(position);
            }
        });
    }

    private void scrollToPlay(int position) {
        scrollToPosition(position);
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                playItemHolder(position);
            }
        });
    }

    private void attachItemHolder(NewCategoryVideoListAdapter.VideoItemHolder itemHolder) {
        if (itemHolder != null && !isLandScape && isCurrentCategoryVisible()) {
            PLog.d(TAG, "attachItemHolder : position = " + mPlayPos);
            PreviewVideoPlayer.get().setReceiverGroupConfigState(mContext, PreviewVideoPlayer.RECEIVER_GROUP_CONFIG_LIST_STATE);
            PreviewVideoPlayer.get().attachContainer(itemHolder.playerContainer);
        }
    }

    private void playItemHolder(int position) {
        NewCategoryVideoListAdapter.VideoItemHolder itemHolder = getItemHolder(position);
        CategoryVideosBean.RecommendVideoItem item = getItem(position);
        if (itemHolder != null) {
            MTimeVideoData data = new MTimeVideoData(String.valueOf(item.getvId()), item.getVideoSource());
            PreviewVideoPlayer.get().play(data, true);
            mPlayPos = position;
            attachItemHolder(itemHolder);
            CategoryDataManager.get().updateListIndex(mCategory.getType(), position);
            onPlayItemCallBack(position, item);
            PLog.d(TAG, "playItemHolder : position = " + position + ", name = " + item.getTitle());
        }
        onJudgeLoadNextPage();
    }

    private void onPlayItemCallBack(int position, CategoryVideosBean.RecommendVideoItem item) {
        if (mOnLoadJudgeListener != null) {
            mOnLoadJudgeListener.onPlayItem(item, position);
        }
    }

    private void scrollToPosition(int position) {
        if (isLegalPosition(position)) {
            PLog.d(TAG, "scrollToPosition : position = " + position);
            ScrollSpeedLinearLayoutManager layoutManager = getLayoutManager();
            layoutManager.scrollToPositionWithOffset(position, 0);
        }
    }

    /**
     * 获取Item中渲染视图的可见高度
     *
     * @param position
     * @return
     */
    private int getItemVisibleRectHeight(int position) {
        NewCategoryVideoListAdapter.VideoItemHolder itemHolder = getItemHolder(position);
        if (itemHolder == null)
            return 0;
        int[] location = new int[2];
        itemHolder.playerContainer.getLocationOnScreen(location);
        int height = itemHolder.playerContainer.getHeight();

        int visibleRect;
        if (location[1] <= mVerticalRecyclerStart) {
            visibleRect = location[1] - mVerticalRecyclerStart + height;
        } else {
            if (location[1] + height >= mScreenH) {
                visibleRect = mScreenH - location[1];
            } else {
                visibleRect = height;
            }
        }
        return visibleRect;
    }

    /**
     * 获取两个索引条目中渲染视图可见高度最大的条目
     *
     * @param position1
     * @param position2
     * @return
     */
    private int getVisibleRectMaxPosition(int position1, int position2) {
        NewCategoryVideoListAdapter.VideoItemHolder itemHolder1 = getItemHolder(position1);
        NewCategoryVideoListAdapter.VideoItemHolder itemHolder2 = getItemHolder(position2);
        if (itemHolder1 == null && itemHolder2 == null) {
            return RecyclerView.NO_POSITION;
        }
        if (itemHolder1 == null) {
            return position2;
        }
        if (itemHolder2 == null) {
            return position1;
        }
        int visibleRect1 = getItemVisibleRectHeight(position1);
        int visibleRect2 = getItemVisibleRectHeight(position2);
        return visibleRect1 >= visibleRect2 ? position1 : position2;
    }

    /**
     * 判断给定的索引条目，渲染视图的可见高度是否满足播放条件.
     *
     * @param position
     * @return
     */
    private boolean isVisibleRectAvailablePlay(int position) {
        NewCategoryVideoListAdapter.VideoItemHolder itemHolder = getItemHolder(position);
        if (itemHolder == null)
            return false;
        int height = itemHolder.playerContainer.getHeight();
        return getItemVisibleRectHeight(position) > (height / 2);
    }

    private boolean isCompleteVisibleRect(int position) {
        NewCategoryVideoListAdapter.VideoItemHolder itemHolder = getItemHolder(position);
        if (itemHolder == null)
            return false;
        int height = itemHolder.playerContainer.getHeight();
        return getItemVisibleRectHeight(position) == height;
    }

    /**
     * 判断当前播放条目是否满足播放条件
     *
     * @param firstVisiblePos
     * @param lastVisiblePos
     * @return
     */
    private boolean isCurrentInVisiblePlayArea(int firstVisiblePos, int lastVisiblePos) {
        return mPlayPos >= firstVisiblePos && mPlayPos <= lastVisiblePos && isVisibleRectAvailablePlay(mPlayPos);
    }

    private void onJudgeLoadNextPage() {
        if (isNeedLoadNextPage() && mOnLoadJudgeListener != null) {
            mOnLoadJudgeListener.onNeedLoadNextPage();
            PLog.d(TAG, "onNeedLoadNextPage......");
        }
    }

    public void setLoadMoreEnable(boolean loadMoreEnable) {
        this.mLoadMoreEnable = loadMoreEnable;
    }

    public boolean isLoadMoreEnable() {
        return mLoadMoreEnable;
    }

    private boolean isNeedLoadNextPage() {
        if (!mLoadMoreEnable)
            return false;
        int size = getItemCount();
        return size >= MIN_PAGE_SIZE && mPlayPos >= size - 2;
    }

    private boolean isLegalPosition(int position) {
        return position >= 0 && position <= getItemCount() - 1;
    }

    private int getItemCount() {
        if (mVideoItems == null)
            return 0;
        return mVideoItems.size();
    }

    private ScrollSpeedLinearLayoutManager getLayoutManager() {
        return (ScrollSpeedLinearLayoutManager) mRecyclerView.getLayoutManager();
    }

    private int getFirstCompleteVisiblePosition() {
        ScrollSpeedLinearLayoutManager layoutManager = getLayoutManager();
        return layoutManager.findFirstCompletelyVisibleItemPosition();
    }

    private int getFirstVisiblePosition() {
        ScrollSpeedLinearLayoutManager layoutManager = getLayoutManager();
        return layoutManager.findFirstVisibleItemPosition();
    }

    private int getLastVisiblePosition() {
        ScrollSpeedLinearLayoutManager layoutManager = getLayoutManager();
        return layoutManager.findLastVisibleItemPosition();
    }

    private NewCategoryVideoListAdapter.VideoItemHolder getItemHolder(int position) {
        RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForLayoutPosition(position);
        if (viewHolder != null) {
            return (NewCategoryVideoListAdapter.VideoItemHolder) viewHolder;
        }
        return null;
    }

    private int getPositionByViewHolder(NewCategoryVideoListAdapter.VideoItemHolder holder) {
        if (holder == null)
            return RecyclerView.NO_POSITION;
        return mRecyclerView.getChildLayoutPosition(holder.itemView);
    }

    public CategoryVideosBean.RecommendVideoItem getItem(int position) {
        if (mVideoItems == null)
            return null;
        int size = mVideoItems.size();
        if (position < 0 || position > size - 1)
            return null;
        return mVideoItems.get(position);
    }

    public void updatePlayPosition(int position) {
        mPlayPos = position;
        if (mCategory != null)
            CategoryDataManager.get().updateListIndex(mCategory.getType(), position);
    }

    public int getCurrPlayPosition() {
        return mPlayPos;
    }

    public void destroy() {
        PreviewVideoPlayer.get().removeReceiverEventListener(this);
        PreviewVideoPlayer.get().removePlayerEventListener(this);
    }

    public interface OnLoadJudgeListener {
        void onNeedLoadNextPage();

        boolean isPageVisible();

        void onPlayItem(CategoryVideosBean.RecommendVideoItem item, int position);
    }

}
