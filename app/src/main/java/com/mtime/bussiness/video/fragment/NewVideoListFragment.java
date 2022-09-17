package com.mtime.bussiness.video.fragment;

import android.content.res.Configuration;
import android.os.Bundle;

import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.uiframe.d.BaseState;
import com.kotlin.android.app.data.constant.CommConstant;
import com.kotlin.android.app.data.entity.common.CommBizCodeResult;
import com.kotlin.android.app.data.ext.VariateExt;
import com.kotlin.android.router.ext.ProviderExtKt;

import com.kotlin.android.app.router.path.RouterProviderPath;
import com.kotlin.android.app.router.provider.ugc.IUgcProvider;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.statistic.StatisticConstant;
import com.mtime.base.statistic.StatisticEnum;
import com.mtime.base.widget.layout.OnVisibilityCallback;
import com.mtime.bussiness.video.CategoryDataManager;
import com.mtime.bussiness.video.PreviewVideoPlayer;
import com.mtime.bussiness.video.api.MovieApi;
import com.mtime.bussiness.video.api.PraiseCommentApi;
import com.mtime.bussiness.video.bean.CategoryVideosBean;
import com.mtime.bussiness.video.bean.SendBarrageBean;
import com.mtime.bussiness.video.dialog.PlayerShareDialog;
import com.mtime.bussiness.video.holder.NewCategoryVideoListHolder;
import com.mtime.frame.BaseFrameUIFragment;
import com.mtime.player.bean.StatisticsInfo;
import com.mtime.statistic.large.MapBuild;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.util.JumpUtil;

import java.util.List;

import static com.mtime.bussiness.video.holder.NewCategoryVideoListHolder.KEY_NEED_CONTINUE_PLAY;

/**
 * Created by JiaJunHui on 2018/2/28.
 */

public class NewVideoListFragment extends BaseFrameUIFragment<CategoryVideosBean, NewCategoryVideoListHolder> implements NewCategoryVideoListHolder.OnListHolderListener {

    private static final String KEY_MOVIE_ID = "movie_id";
    private static final String KEY_CATEGORY = "category";

    final String PAGE_REGION = "movieVideoList";

    final int page_size = 10;

    private int movieId;
    private CategoryVideosBean.Category mCategory;

    private MovieApi mMovieApi;
    private PraiseCommentApi mPraiseCommentApi;

    private int mPageIndex = 1;
    private PlayerShareDialog shareDialog;
    private boolean isFullScreen;

    private CategoryVideosBean.RecommendVideoItem mShareItem;

    @Override
    public NewCategoryVideoListHolder onBindContentHolder() {
        return new NewCategoryVideoListHolder(getContext(), this);
    }

    public static NewVideoListFragment createInstance(int movieId, CategoryVideosBean.Category category){
        NewVideoListFragment fragment = new NewVideoListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_MOVIE_ID, movieId);
        bundle.putSerializable(KEY_CATEGORY, category);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void onInit(Bundle savedInstanceState) {
        super.onInit(savedInstanceState);
        mMovieApi = new MovieApi();
        mPraiseCommentApi = new PraiseCommentApi();
        movieId = getArguments().getInt(KEY_MOVIE_ID);
        mCategory = (CategoryVideosBean.Category) getArguments().getSerializable(KEY_CATEGORY);
        getUserContentHolder().setCategory(mCategory);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mMovieApi!=null){
            mMovieApi.cancelAllTags();
        }
        if(mPraiseCommentApi!=null){
            mPraiseCommentApi.cancelAllTags();
        }
    }

    public void onNextPlayAttach(int index){
        if(getUserContentHolder()!=null)
            getUserContentHolder().onNextPlayAttach(index);
    }

    public void onPortraitCurrentAttach(){
        if(getUserContentHolder()!=null)
            getUserContentHolder().portraitAttachCurrent();
    }

    public void onSelectStateChange(boolean selected){
        if(getUserContentHolder()!=null)
            getUserContentHolder().onPageSelectChange(selected);
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        onVisibleChange(true);
    }

    @Override
    protected void onInvisible() {
        super.onInvisible();
        onVisibleChange(false);
    }

    private void onVisibleChange(boolean visible){
        if(getUserContentHolder()!=null)
            getUserContentHolder().onVisibleChange(visible);
    }

    @Override
    public void onResume() {
        super.onResume();
        dismissDialog();
    }

    private void dismissDialog() {
        if(shareDialog!=null){
            shareDialog.dismiss();
        }
    }

    @Override
    public void onHolderEvent(int eventCode, Bundle bundle) {
        super.onHolderEvent(eventCode, bundle);

        CategoryVideosBean.RecommendVideoItem item;
        switch (eventCode){
            case NewCategoryVideoListHolder.EVENT_CODE_REFRESH:
                mPageIndex = 1;
                loadData(false);
                break;
            case NewCategoryVideoListHolder.EVENT_CODE_LOAD_MORE:
                mPageIndex++;
                loadData(true);
                break;
            case NewCategoryVideoListHolder.EVENT_CODE_PRAISE:
                item = (CategoryVideosBean.RecommendVideoItem) bundle.getSerializable(NewCategoryVideoListHolder.KEY_VIDEO_ITEM);
                handlePraise(item.getvId(), false);
                logPraise(true, item, bundle.getInt(KEY_INT_DATA));
                break;
            case NewCategoryVideoListHolder.EVENT_CODE_CANCEL_PRAISE:
                item = (CategoryVideosBean.RecommendVideoItem) bundle.getSerializable(NewCategoryVideoListHolder.KEY_VIDEO_ITEM);
                handlePraise(item.getvId(), true);
                logPraise(false, item, bundle.getInt(KEY_INT_DATA));
                break;
            case NewCategoryVideoListHolder.EVENT_CODE_NEED_LOGIN:
                JumpUtil.startLoginActivity(getActivity(),assemble().toString());
                break;
            case NewCategoryVideoListHolder.EVENT_CODE_SHARE:
                mShareItem = (CategoryVideosBean.RecommendVideoItem) bundle.getSerializable(NewCategoryVideoListHolder.KEY_VIDEO_ITEM);
                shareDialog = new PlayerShareDialog(getActivity(), isFullScreen);
                shareDialog.setValues(String.valueOf(mShareItem.getvId()), "41");
                shareDialog.showActionSheet(onShareListener);

                StatisticManager.getInstance().submit(assemble("videoList", null,
                        "videoItem", String.valueOf(bundle.getInt(KEY_INT_DATA)),
                        "shareBtn", null,
                        getBaseParams(mShareItem).build()));
                break;
            case NewCategoryVideoListHolder.EVENT_CODE_TO_DETAIL_PAGE:
                PreviewVideoPlayer.get().pause();
                item = (CategoryVideosBean.RecommendVideoItem) bundle.getSerializable(NewCategoryVideoListHolder.KEY_VIDEO_ITEM);
                boolean continuePlay = bundle.getBoolean(KEY_NEED_CONTINUE_PLAY);
                JumpUtil.startPrevueVideoPlayerActivity(getActivity(), String.valueOf(item.getvId()), item.getVideoSource(), continuePlay, assemble().toString());
                boolean commentClick = bundle.getBoolean(KEY_BOOLEAN_DATA);
                if(commentClick){
                    int position = bundle.getInt(KEY_INT_DATA);
                    //点击评论按钮时
                    StatisticManager.getInstance().submit(assemble("videoList", null,
                            "videoItem", String.valueOf(position),
                            "commentBtn", null,
                            getBaseParams(item).put(StatisticConstant.REPLY_COUNT, item.getCommentTotal()).build()));
                }else{
                    StatisticManager.getInstance().submit(assemble("videoList", null,
                            "videoItem", String.valueOf(bundle.getInt(KEY_INT_DATA)),
                            "block", null,
                            getBaseParams(item).build()));
                }
                break;
            case NewCategoryVideoListHolder.EVENT_CODE_SEND_DANMU:
                String danmuText = bundle.getString(EventKey.STRING_DATA);
                sendDanmu(danmuText);
                break;
            case NewCategoryVideoListHolder.EVENT_CODE_FEED_BACK:
                IUgcProvider instance = ProviderExtKt.getProvider(IUgcProvider.class);
                if (null != instance) {
                    instance.launchDetail(
                            VariateExt.INSTANCE.getFeedbackPostId(),
                            CommConstant.PRAISE_OBJ_TYPE_POST,
                            0L,
                            false
                    );
                }
                break;
        }
    }

    private void logPraise(boolean praise, CategoryVideosBean.RecommendVideoItem item, int position){
        StatisticEnum.EnumThumbsUpState state = praise? StatisticEnum.EnumThumbsUpState.THUMBS_UP: StatisticEnum.EnumThumbsUpState.CANCEL;
        StatisticManager.getInstance().submit(assemble("videoList", null,
                "videoItem", String.valueOf(position),
                "thumbsUp", null,
                getBaseParams(item)
                        .put(StatisticConstant.THUMBS_UP_STATE, state.getValue())
                        .put(StatisticConstant.THUMBS_UP_COUNT, item.getPraiseInfo()).build()));
    }

    private void sendDanmu(String danmuText) {
        CategoryVideosBean.RecommendVideoItem currItem = getUserContentHolder().getCurrItem();
        if(currItem==null)
            return;
        int videoId = currItem.getvId();
        int sourceType = currItem.getVideoSource();
        mMovieApi.sendDanmu("send_danmu",
                String.valueOf(videoId),
                String.valueOf(sourceType),
                getUserContentHolder().getCurrentPlayPosition(),
                danmuText,
                new NetworkManager.NetworkListener<SendBarrageBean>() {
                    @Override
                    public void onSuccess(SendBarrageBean result, String showMsg) {

                    }
                    @Override
                    public void onFailure(NetworkException<SendBarrageBean> exception, String showMsg) {

                    }
                });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        dismissDialog();
        isFullScreen = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    private final PlayerShareDialog.OnShareListener onShareListener = new PlayerShareDialog.OnShareListener() {
        @Override
        public void onShareSuccess(int shareChannel) {

        }

        @Override
        public void onShareFailure(int shareChannel, String message) {

        }

        @Override
        public void onShareChannelClick(String shareChannel) {
            if(mShareItem==null)
                return;
            StatisticManager.getInstance().submit(assemble("shareDlg", null,
                    "shareTo", null,
                    null, null,
                    getBaseParams(mShareItem).put(StatisticConstant.SHARE_TO, shareChannel).build()));
        }

        @Override
        public void onShareCancel(boolean userClose) {

        }

        @Override
        public void onShareDismiss(int shareChannel) {
            if(mShareItem==null)
                return;
            StatisticManager.getInstance().submit(assemble("shareDlg", null,
                    "close", null,
                    null, null,
                    getBaseParams(mShareItem).build()));
        }
    };

    @Override
    protected void onLoadState() {
        super.onLoadState();
    }

    @Override
    protected void onLazyLoad() {
        super.onLazyLoad();

        CategoryDataManager.CategoryData categoryData = CategoryDataManager.get().getCacheCategoryData(mCategory.getType());
        if(!getUserContentHolder().hasListData()){
            //cache data available
            if(categoryData!=null && categoryData.isListAvailable()){
                mPageIndex = categoryData.getPageIndex();
                CategoryVideosBean result = new CategoryVideosBean();
                result.setVideoList(categoryData.getAllItems());
                setData(result);
                setPageState(BaseState.SUCCESS);
                getUserContentHolder().onLazyReadyPlay();
            }else{
                getUserContentHolder().onLazyFirstLoad();
                //load remote data
                loadData(false);
            }
        }else{
            //缓存数据是否多余目前列表展示的数据，如果多于则完全填充刷新。
            if(categoryData!=null && categoryData.isListAvailable() && categoryData.getPageIndex() > mPageIndex){
                mPageIndex = categoryData.getPageIndex();
                CategoryVideosBean result = new CategoryVideosBean();
                result.setVideoList(categoryData.getAllItems());
                setData(result);
                setPageState(BaseState.SUCCESS);
            }
            getUserContentHolder().onLazyReadyPlay();
        }
    }

    private void loadData(boolean fromLoadMore) {
        _setPageState(BaseState.LOADING);

        CategoryDataManager.get().loadCategoryListData(mCategory.getType(), mPageIndex, new CategoryDataManager.OnCategoryListLoadListener() {
            @Override
            public void onDataReady(List<CategoryVideosBean.RecommendVideoItem> videoItems, boolean loadFinish) {
                if(videoItems!=null){
                    CategoryVideosBean result = new CategoryVideosBean();
                    result.setVideoList(videoItems);
                    setData(result);
                    setPageState(BaseState.SUCCESS);
                }
                if(loadFinish){
                    getUserContentHolder().noMoreData();
                }
                getUserContentHolder().finishLoadMore();
                if(!fromLoadMore){
                    getUserContentHolder().onLazyReadyPlay();
                }
            }
            @Override
            public void onFailure() {
                getUserContentHolder().noMoreData();
                getUserContentHolder().finishLoadMore();
            }
        });
    }

    private void _setPageState(BaseState state){
        if(mPageIndex==1){
            setPageState(state);
        }
    }

    private void handlePraise(long id,boolean isCancel){
        mPraiseCommentApi.postEditPraise("edit_praise", String.valueOf(id), String.valueOf(CommConstant.PRAISE_OBJ_TYPE_MOVIE_TRAILER),isCancel, new NetworkManager.NetworkListener<CommBizCodeResult>() {
            @Override
            public void onSuccess(CommBizCodeResult result, String showMsg) {

            }
            @Override
            public void onFailure(NetworkException<CommBizCodeResult> exception, String showMsg) {

            }
        });
    }

    @Override
    public void onVideoPlay(CategoryVideosBean.RecommendVideoItem item, int position) {
        if(item==null)
            return;
        //4.0埋点 上报相关
        mBaseStatisticHelper.setPageLabel(PAGE_REGION);

        StatisticsInfo statisticsInfo = new StatisticsInfo(String.valueOf(item.getvId()), item.getVideoSource(), mBaseStatisticHelper.getLastPageRefer(), mBaseStatisticHelper.getPageLabel());
        PreviewVideoPlayer.get().setStatisticsInfo(statisticsInfo);
    }

    @Override
    public void onItemVisibleChange(boolean show, OnVisibilityCallback.Tag tag) {
        if(!show)
            return;
        CategoryVideosBean.RecommendVideoItem item = (CategoryVideosBean.RecommendVideoItem) tag.data;
        StatisticManager.getInstance().submit(assemble("videoList", null,
                "videoItem", String.valueOf(tag.position),
                "show", null,
                getBaseParams(item).build()));
    }

    private MapBuild<String, String> getBaseParams(CategoryVideosBean.RecommendVideoItem item){
        return getBaseParams(
                String.valueOf(item.getvId()),
                String.valueOf(item.getVideoSource()),
                item.getRecommendID(),
                item.getRecommendType());
    }

    private MapBuild<String,String> getBaseParams(String vid, String vType, String rcmdId, String rcmdType){
        MapBuild<String, String> mapBuild = new MapBuild<>();
        mapBuild.put(StatisticConstant.MOVIE_ID, String.valueOf(movieId));
        mapBuild.put(StatisticConstant.V_ID, vid);
        mapBuild.put(StatisticConstant.V_TYPE, vType);
        mapBuild.put(StatisticConstant.RCMD_ID, rcmdId);
        mapBuild.put(StatisticConstant.RCMD_TYPE, rcmdType);
        return mapBuild;
    }
}