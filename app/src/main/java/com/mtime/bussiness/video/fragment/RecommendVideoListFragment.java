package com.mtime.bussiness.video.fragment;

import android.os.Bundle;

import com.mtime.bussiness.video.CategoryDataManager;
import com.mtime.frame.BaseFrameUIFragment;
import com.kk.taurus.uiframe.d.BaseState;
import com.kk.taurus.uiframe.v.BaseStateContainer;
import com.mtime.bussiness.video.holder.TransBgNoTitleBarContainer;
import com.mtime.bussiness.video.bean.CategoryVideosBean;
import com.mtime.bussiness.video.holder.RecommendCategoryVideoListHolder;

import java.util.List;

/**
 * Created by JiaJunHui on 2018/2/28.
 */

public class RecommendVideoListFragment extends BaseFrameUIFragment<CategoryVideosBean, RecommendCategoryVideoListHolder> {

    private static final String KEY_MOVIE_ID = "movie_id";
    private static final String KEY_CATEGORY = "category";

    private int movieId;
    private CategoryVideosBean.Category mCategory;

    private int mPageIndex = 1;

    @Override
    public RecommendCategoryVideoListHolder onBindContentHolder() {
        return new RecommendCategoryVideoListHolder(getContext());
    }

    @Override
    protected BaseStateContainer getStateContainer() {
        return new TransBgNoTitleBarContainer(getActivity(), this, this);
    }

    public static RecommendVideoListFragment createInstance(int movieId, CategoryVideosBean.Category category){
        RecommendVideoListFragment fragment = new RecommendVideoListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_MOVIE_ID, movieId);
        bundle.putSerializable(KEY_CATEGORY, category);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void onInit(Bundle savedInstanceState) {
        super.onInit(savedInstanceState);
        movieId = getArguments().getInt(KEY_MOVIE_ID);
        mCategory = (CategoryVideosBean.Category) getArguments().getSerializable(KEY_CATEGORY);
        getUserContentHolder().setCategory(mCategory);

    }

    public void notifyIndex(){
        if(getUserContentHolder()!=null)
            getUserContentHolder().notifyCurrIndex();
    }

    @Override
    public void onHolderEvent(int eventCode, Bundle bundle) {
        super.onHolderEvent(eventCode, bundle);
        switch (eventCode){
            case RecommendCategoryVideoListHolder.EVENT_CODE_LOAD_MORE:
                mPageIndex++;
                loadData();
                break;
        }
    }

    @Override
    protected void onLoadState() {
        super.onLoadState();
    }

    @Override
    protected void onLazyLoad() {
        super.onLazyLoad();
        if(!getUserContentHolder().hasListData()){
            loadData();
        }else{
            setPageState(BaseState.SUCCESS);
        }
    }

    private void loadData() {
        if(mPageIndex==1){
            _setPageState(BaseState.LOADING);
        }

        CategoryDataManager.get().loadCategoryListData(mCategory.getType(), mPageIndex, new CategoryDataManager.OnCategoryListLoadListener() {
            @Override
            public void onDataReady(List<CategoryVideosBean.RecommendVideoItem> videoItems, boolean loadFinish) {
                if(videoItems!=null){
                    CategoryVideosBean result = new CategoryVideosBean();
                    result.setVideoList(videoItems);
                    setData(result);
                }
                _setPageState(BaseState.SUCCESS);
                if(loadFinish){
                    getUserContentHolder().noMoreData();
                }
                getUserContentHolder().finishLoadMore();
            }
            @Override
            public void onFailure() {
                mPageIndex = CategoryDataManager.get().getCategoryTypePageIndex(mCategory.getType());
                getUserContentHolder().finishLoadMore();
            }
        });
    }

    private void _setPageState(BaseState state){
        setPageState(state);
    }

}