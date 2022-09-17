package com.mtime.bussiness.video.holder;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.playerbase.log.PLog;
import com.kk.taurus.playerbase.receiver.OnReceiverEventListener;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.R;
import com.kotlin.android.user.UserManager;
import com.mtime.base.widget.layout.OnVisibilityCallback;
import com.mtime.bussiness.video.CategoryDataManager;
import com.mtime.bussiness.video.NewPreviewListPlayLogic;
import com.mtime.bussiness.video.PreviewVideoPlayer;
import com.mtime.bussiness.video.ScrollSpeedLinearLayoutManager;
import com.mtime.bussiness.video.adapter.NewCategoryVideoListAdapter;
import com.mtime.bussiness.video.bean.CategoryVideosBean;
import com.mtime.player.DataInter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.kk.taurus.uiframe.listener.OnHolderListener.KEY_BOOLEAN_DATA;
import static com.kk.taurus.uiframe.listener.OnHolderListener.KEY_INT_DATA;

/**
 * Created by JiaJunHui on 2018/2/28.
 */

public class NewCategoryVideoListHolder extends ContentHolder<CategoryVideosBean> implements OnLoadMoreListener, OnReceiverEventListener
        , NewCategoryVideoListAdapter.OnCategoryVideoListListener, NewPreviewListPlayLogic.OnLoadJudgeListener {

    public static final int EVENT_CODE_REFRESH = 100;
    public static final int EVENT_CODE_LOAD_MORE = 101;

    public static final int EVENT_CODE_PRAISE = 102;
    public static final int EVENT_CODE_CANCEL_PRAISE = 103;

    public static final int EVENT_CODE_NEED_LOGIN = 104;
    public static final int EVENT_CODE_SHARE = 105;
    public static final int EVENT_CODE_TO_DETAIL_PAGE = 106;
    public static final int EVENT_CODE_SEND_DANMU = 107;
    public static final int EVENT_CODE_FEED_BACK = 108;

    public static final String KEY_VIDEO_ITEM = "video_item";

    public static final String KEY_NEED_CONTINUE_PLAY = "need_continue_play";

    @BindView(R.id.fragment_category_video_list_recycler)
    RecyclerView mRecycler;
    @BindView(R.id.fragment_category_video_list_refresh_layout)
    SmartRefreshLayout mRefreshLayout;

    private Unbinder unbinder;

    private final List<CategoryVideosBean.RecommendVideoItem> mVideoItems = new ArrayList<>();
    private NewCategoryVideoListAdapter mListAdapter;

    private OnListHolderListener mOnListHolderListener;

    private CategoryVideosBean.Category mCategory;
    private boolean toDetailPage;

    private boolean mActive;
    private boolean isLandScape;
    private boolean firstResume;
//    private boolean mRecommendFirstLoad = true;

    public NewCategoryVideoListHolder(Context context) {
        super(context);
    }

    public NewCategoryVideoListHolder(Context context, OnListHolderListener onListHolderListener) {
        super(context);
        this.mOnListHolderListener = onListHolderListener;
    }

    @Override
    public void onCreate() {
        setContentView(R.layout.fragment_category_video_list);
        unbinder = ButterKnife.bind(this, mRootView);
    }

    @Override
    public void onHolderCreated() {
        super.onHolderCreated();
        PreviewVideoPlayer.get().addOnReceiverEventListener(this);
        mRefreshLayout.setEnableRefresh(false);
        mRefreshLayout.setOnLoadMoreListener(this);
        mRecycler.setLayoutManager(new ScrollSpeedLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mListAdapter = new NewCategoryVideoListAdapter(mContext, mVideoItems, mRecycler);
        mListAdapter.getListPlayLogic().setOnLoadJudgeListener(this);
        mListAdapter.setOnCategoryVideoListListener(this);
        mRecycler.setAdapter(mListAdapter);
    }

    public void setCategory(CategoryVideosBean.Category category){
        this.mCategory = category;
        if(mListAdapter!=null){
            mListAdapter.getListPlayLogic().setCategory(category);
        }
    }

    public void onPageSelectChange(boolean selected){

    }

    public void onVisibleChange(boolean visible){
        mActive = visible;
    }

    public void onLazyFirstLoad(){
        //TODO first load, maybe need to stop player
        PreviewVideoPlayer.get().stop();
    }

    public void onLazyReadyPlay(){
        if(!mActive || mRecycler==null) {
            return;
        }
        CategoryDataManager.get().updateCurrentCategoryType(mCategory.getType());

        //TODO handle play
        PLog.d("NewCategoryVideoListHolder", "-->onLazyReadyPlay = " + mCategory.getType());
        mRecycler.post(new Runnable() {
            @Override
            public void run() {

                // 影片详情页点击单个视频，进入推荐列表页，自动定位到该视频位置播放
//                if(mRecommendFirstLoad && mCategory.getType() == CategoryVideosBean.Category.TYPE_RECOMMEND) {
//                    mListAdapter.getListPlayLogic().updatePlayPosition(mRecPosition);
//                    mRecommendFirstLoad = false;
//                }

                mListAdapter.getListPlayLogic().openListPlay();
            }
        });
    }

    public boolean hasListData() {
        return mVideoItems != null && mVideoItems.size() > 0;
    }

    @Override
    public void refreshView() {
        super.refreshView();
        if(mData==null) {
            return;
        }
        List<CategoryVideosBean.RecommendVideoItem> videoList = mData.getVideoList();
        if(hasListData()){
            int size = mVideoItems.size();
            mVideoItems.addAll(videoList);
            mListAdapter.notifyListInsert(size, videoList.size());
        }else{
            mVideoItems.addAll(videoList);
            mListAdapter.notifyAdapter();
        }
        PLog.d("NewCategoryVideoListHolder", "refreshView = " + mCategory.getType());
    }

    public void finishLoadMore() {
        if(mRefreshLayout!=null) {
            mRefreshLayout.finishLoadMore();
        }
    }

    public void noMoreData() {
        if (null ==mRefreshLayout){
            return;
        }
        mRefreshLayout.finishLoadMoreWithNoMoreData();
        mListAdapter.getListPlayLogic().setLoadMoreEnable(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!firstResume){
            firstResume = true;
            return;
        }
        if(mActive){
            if(toDetailPage && mListAdapter!=null){
                mListAdapter.getListPlayLogic().openListPlay();
            }
            PreviewVideoPlayer.get().logicResume();
        }
        toDetailPage = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mListAdapter!=null && !toDetailPage && mActive){
            PreviewVideoPlayer.get().logicPause();
        }
    }

    public void onNextPlayAttach(int index){
        if(mListAdapter!=null){
            mListAdapter.getListPlayLogic().onNextAttach(index);
        }
    }

    public void portraitAttachCurrent(){
        mRecycler.post(new Runnable() {
            @Override
            public void run() {
                if(mListAdapter!=null){
                    mListAdapter.getListPlayLogic().portraitAttachCurrent();
                }
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        isLandScape = newConfig.orientation== Configuration.ORIENTATION_LANDSCAPE;
        super.onConfigurationChanged(newConfig);
        mListAdapter.getListPlayLogic().onConfigChanged(newConfig);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreviewVideoPlayer.get().removeReceiverEventListener(this);
        if (mListAdapter != null) {
            mListAdapter.getListPlayLogic().destroy();
        }
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public CategoryVideosBean.RecommendVideoItem getCurrItem(){
        if(mListAdapter!=null){
            int pos = mListAdapter.getListPlayLogic().getCurrPlayPosition();
            return mListAdapter.getListPlayLogic().getItem(pos);
        }
        return null;
    }

    public int getCurrentPlayPosition(){
        return PreviewVideoPlayer.get().getCurrentPosition()/1000;
    }

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {
        if(!mActive) {
            return;
        }
        switch (eventCode){
            case DataInter.ReceiverEvent.EVENT_CODE_SHARE:
                int pos = mListAdapter.getListPlayLogic().getCurrPlayPosition();
                onShare(mListAdapter.getListPlayLogic().getItem(pos), pos);
                break;
            case DataInter.ReceiverEvent.EVENT_DANMU_COVER_SEND_DANMU:
                String text = bundle.getString(EventKey.STRING_DATA);
                handleSendDanmu(text);
                break;
            case DataInter.ReceiverEvent.EVENT_CODE_ERROR_FEED_BACK:
                onHolderEvent(EVENT_CODE_FEED_BACK, null);
                break;
        }
    }

    private void handleSendDanmu(String danmuText) {
        if (UserManager.Companion.getInstance().isLogin()) {
            Bundle bundle = new Bundle();
            bundle.putString(EventKey.STRING_DATA,danmuText);
            onHolderEvent(EVENT_CODE_SEND_DANMU, bundle);
        }else{
            onHolderEvent(EVENT_CODE_NEED_LOGIN, null);
        }
    }

    @Override
    public void onLoadMore(RefreshLayout refreshlayout) {
        onHolderEvent(EVENT_CODE_LOAD_MORE, null);
    }

    @Override
    public void onNeedLoadNextPage() {
        onHolderEvent(EVENT_CODE_LOAD_MORE, null);
    }

    @Override
    public boolean isPageVisible() {
        return mActive;
    }

    @Override
    public void onPlayItem(CategoryVideosBean.RecommendVideoItem item, int position) {
        if(mOnListHolderListener!=null){
            mOnListHolderListener.onVideoPlay(item, position);
        }
    }

    @Override
    public void onNeedLogin() {
        onHolderEvent(EVENT_CODE_NEED_LOGIN, null);
    }

    @Override
    public void onToDetailPage(boolean commentClick, CategoryVideosBean.RecommendVideoItem item, int position) {
        if(mListAdapter==null) {
            return;
        }
        mListAdapter.getListPlayLogic().updatePlayPosition(position);
        toDetailPage = true;
        int playPosition = mListAdapter.getListPlayLogic().getCurrPlayPosition();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_VIDEO_ITEM, item);
        bundle.putInt(KEY_INT_DATA, position);
        bundle.putBoolean(KEY_BOOLEAN_DATA, commentClick);
        bundle.putBoolean(KEY_NEED_CONTINUE_PLAY, position==playPosition);
        onHolderEvent(EVENT_CODE_TO_DETAIL_PAGE, bundle);
    }

    @Override
    public void onPraiseHandle(boolean praise, CategoryVideosBean.RecommendVideoItem item, int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_VIDEO_ITEM, item);
        bundle.putInt(KEY_INT_DATA, position);
        onHolderEvent(praise?EVENT_CODE_PRAISE:EVENT_CODE_CANCEL_PRAISE, bundle);
    }

    @Override
    public void onShare(CategoryVideosBean.RecommendVideoItem item, int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_VIDEO_ITEM, item);
        bundle.putInt(KEY_INT_DATA, position);
        onHolderEvent(EVENT_CODE_SHARE, bundle);
    }

    @Override
    public void onItemVisibleChange(boolean show, OnVisibilityCallback.Tag tag) {
        mOnListHolderListener.onItemVisibleChange(show, tag);
    }

    public interface OnListHolderListener{
        void onVideoPlay(CategoryVideosBean.RecommendVideoItem item, int position);
        void onItemVisibleChange(boolean show, OnVisibilityCallback.Tag tag);

    }
}
