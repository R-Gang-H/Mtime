package com.mtime.bussiness.video.adapter;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.collection.SparseArrayCompat;

import com.mtime.bussiness.video.bean.CategoryVideosBean;
import com.mtime.bussiness.video.fragment.RecommendVideoListFragment;

import java.util.List;

/**
 * Created by JiaJunHui on 2018/2/28.
 */

public class RecommendCategoryVideoTabAdapter extends FragmentPagerAdapter {

    private final List<CategoryVideosBean.Category> mCategoryList;
    private final SparseArrayCompat<RecommendVideoListFragment> mFragments;

    private final int movieId;

    public RecommendCategoryVideoTabAdapter(FragmentManager fm, List<CategoryVideosBean.Category> categories, int movieId) {
        super(fm);
        mCategoryList = categories;
        mFragments = new SparseArrayCompat<>(categories.size());
        this.movieId = movieId;
    }

    @Override
    public RecommendVideoListFragment getItem(int position) {
        RecommendVideoListFragment fragment = mFragments.get(position);
        if(fragment==null){
            CategoryVideosBean.Category category = mCategoryList.get(position);
            fragment = RecommendVideoListFragment.createInstance(movieId, category);
            mFragments.append(position, fragment);
        }
        return fragment;
    }

    public void notifySelectIndex(){
        int size = mFragments.size();
        for(int i=0;i<size;i++){
            mFragments.valueAt(i).notifyIndex();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return getTabName(position);
    }

    public String getTabName(int position){
        if(mCategoryList==null || mCategoryList.size()<=0)
            return "";
        return mCategoryList.get(position).getName();
    }

    @Override
    public int getCount() {
        if(mCategoryList==null)
            return 0;
        return mCategoryList.size();
    }
}
