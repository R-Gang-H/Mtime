package com.mtime.bussiness.video.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

import com.kk.taurus.playerbase.event.BundlePool;
import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.playerbase.player.IPlayer;
import com.kk.taurus.playerbase.receiver.GroupValue;
import com.kk.taurus.uiframe.d.BaseState;
import com.kk.taurus.uiframe.v.BaseStateContainer;
import com.kk.taurus.uiframe.v.NoTitleBarContainer;
import com.mtime.base.application.AppForeBackListener;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.statistic.StatisticConstant;
import com.mtime.bussiness.video.CategoryDataManager;
import com.mtime.bussiness.video.PlayTemp;
import com.mtime.bussiness.video.PreviewVideoPlayer;
import com.mtime.bussiness.video.api.MovieApi;
import com.mtime.bussiness.video.bean.CategoryVideosBean;
import com.mtime.bussiness.video.bean.SendBarrageBean;
import com.mtime.bussiness.video.holder.CategoryVideoHolder;
import com.mtime.frame.App;
import com.mtime.frame.BaseFrameUIActivity;
import com.mtime.player.DataInter;
import com.mtime.player.PlayerLibraryConfig;
import com.mtime.player.bean.VideoInfo;
import com.mtime.player.listener.OnBackRequestListener;
import com.mtime.player.listener.OnRequestInputDialogListener;
import com.mtime.statistic.large.MapBuild;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.util.JumpUtil;

/**
 * Created by JiaJunHui on 2018/2/28.
 */

public class CategoryVideoListActivity extends BaseFrameUIActivity<CategoryVideosBean,CategoryVideoHolder> implements
        CategoryVideoHolder.OnCategoryVideoHolderListener,
        OnRequestInputDialogListener,
        OnBackRequestListener {

    public static final String KEY_MOVIE_ID = "movie_id";

    private int mMovieId;

    private boolean isFullScreen;

    private MovieApi mMovieApi;

    public static void launch(Context context, String refer, String movieID) {
        Intent intent = new Intent(context, CategoryVideoListActivity.class);
        intent.putExtra(KEY_MOVIE_ID, movieID);
        dealRefer(context, refer, intent);
        context.startActivity(intent);
    }

    @Override
    public CategoryVideoHolder onBindContentHolder() {
        return new CategoryVideoHolder(this, this, getSupportFragmentManager());
    }

    @Override
    protected BaseStateContainer getStateContainer() {
        return new NoTitleBarContainer(this, this, this);
    }

    @Override
    protected void onParseIntent() {
        super.onParseIntent();
        String movieId = getIntent().getExtras().getString(KEY_MOVIE_ID);
        mMovieId = Integer.parseInt(movieId);
    }

    @Override
    protected void onInit(Bundle savedInstanceState) {
        super.onInit(savedInstanceState);

        PlayerLibraryConfig.isListMode = true;
        PlayTemp.push(this);

        keepScreenOn();
        setPageLabel("movieVideoList");
        putBaseStatisticParam(StatisticConstant.MOVIE_ID,String.valueOf(mMovieId));
        mMovieApi = new MovieApi();
        PlayTemp.cleanDanmuTextCache();
        getUserContentHolder().setMovieId(mMovieId);

        PreviewVideoPlayer.get().setNeedRecommendList(true);

        App.getInstance().registerAppForeBackListener(mAppForeBackListener);
    }

    @Override
    protected void onErrorRetry() {
        onLoadState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreviewVideoPlayer.get().attachActivity(this, getRefer());
        PreviewVideoPlayer.get().setOnRequestInputDialogListener(this);
        PreviewVideoPlayer.get().setOnBackRequestListener(this);
    }

    @Override
    public void onBack() {
        onBackPressed();
    }

    private final AppForeBackListener mAppForeBackListener = new AppForeBackListener() {
        @Override
        public void onBecameForeground() {

        }

        @Override
        public void onBecameBackground() {
            int status = PreviewVideoPlayer.get().getState();
            if(PreviewVideoPlayer.get().isInPlaybackState() && status!= IPlayer.STATE_PLAYBACK_COMPLETE){
                PreviewVideoPlayer.get().pause();
            }else{
                if(status!=IPlayer.STATE_ERROR){
                    PreviewVideoPlayer.get().reset();
                }
            }
        }
    };

    @Override
    protected void onLoadState() {
        setPageState(BaseState.LOADING);
        mMovieApi.getCategroyVideos("category_video", mMovieId, 1,
                CategoryVideosBean.Category.TYPE_RECOMMEND, new NetworkManager.NetworkListener<CategoryVideosBean>() {
                    @Override
                    public void onSuccess(CategoryVideosBean result, String showMsg) {
                        if(result!=null){
                            CategoryDataManager.get().init(mMovieId, result.getCategory());
                            setData(result);
                            setPageState(BaseState.SUCCESS);
                        }else{
                            setPageState(BaseState.ERROR);
                        }
                    }
                    @Override
                    public void onFailure(NetworkException<CategoryVideosBean> exception, String showMsg) {
                        setPageState(BaseState.ERROR);
                    }
                });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            isFullScreen = true;
            fullScreen();
        }else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            updatePagerSelection();
            isFullScreen = false;
            quitFullScreen();
        }
        PreviewVideoPlayer.get().updateGroupValue(DataInter.Key.KEY_IS_FULL_SCREEN, isFullScreen);
    }

    private void updatePagerSelection() {
        int currCategoryIndex = CategoryDataManager.get().getCurrentCategoryIndex();
        if(getUserContentHolder().getCurrItemIndex() != currCategoryIndex){
            getUserContentHolder().portraitUpdateChangeItem(currCategoryIndex);
        }else{
            getUserContentHolder().portraitAttachCurrentItem();
        }
    }

    @Override
    public void onBackPressed() {
        boolean recommendListShow = PreviewVideoPlayer.get().getGroupValue()!=null
                && PreviewVideoPlayer.get().getGroupValue().getBoolean(DataInter.Key.KEY_RECOMMEND_LIST_STATE);
        if(recommendListShow){
            getUserContentHolder().sendHiddenRecommendListCover();
            return;
        }
        if(isFullScreen){
            updatePagerSelection();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void finish() {
        PlayerLibraryConfig.isListMode = false;
        super.finish();
        CategoryDataManager.get().destroy();
        if(PlayTemp.pop()){
            PreviewVideoPlayer.get().destroy();
        }else{
            PreviewVideoPlayer.get().stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.getInstance().unregisterAppForeBackListener(mAppForeBackListener);
    }

    @Override
    public void onTabScrolled() {
        StatisticManager.getInstance().submit(assemble("segment", null,
                "scroll", null,
                null, null,
                getMovieIdBaseParams().build()));
    }

    @Override
    public void onTabClicked(int position, String tabName) {
        StatisticManager.getInstance().submit(assemble("segment", null,
                "click", String.valueOf(position),
                null, null,
                getMovieIdBaseParams().put(StatisticConstant.SEGMENT_NAME, tabName).build()));
    }

    @Override
    public void onPageSelected(int position, String tabName) {
        StatisticManager.getInstance().submit(assemble("segment", null,
                "swipe", String.valueOf(position),
                null, null,
                getMovieIdBaseParams().put(StatisticConstant.SEGMENT_NAME, tabName).build()));
    }

    private MapBuild<String,String> getMovieIdBaseParams(){
        MapBuild<String, String> mapBuild = new MapBuild<>();
        mapBuild.put(StatisticConstant.MOVIE_ID, String.valueOf(mMovieId));
        return mapBuild;
    }

    @Override
    public void onRequest() {

    }
}
