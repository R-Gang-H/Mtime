package com.mtime.bussiness.video.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.collection.SparseArrayCompat;

import com.mtime.bussiness.video.bean.CategoryVideosBean;
import com.mtime.bussiness.video.fragment.NewVideoListFragment;

import java.util.List;

/**
 * Created by JiaJunHui on 2018/2/28.
 */

public class CategoryVideoTabAdapter extends FragmentPagerAdapter {

    private final List<CategoryVideosBean.Category> mCategoryList;
    private final SparseArrayCompat<NewVideoListFragment> mFragments;

    private final int movieId;

    public CategoryVideoTabAdapter(FragmentManager fm, List<CategoryVideosBean.Category> categories, int movieId) {
        super(fm);
        mCategoryList = categories;
        mFragments = new SparseArrayCompat<>(categories.size());
        this.movieId = movieId;
    }

    @Override
    public NewVideoListFragment getItem(int position) {
        NewVideoListFragment fragment = mFragments.get(position);
        if(fragment==null){
            CategoryVideosBean.Category category = mCategoryList.get(position);
            category.setIndex(position);
            fragment = NewVideoListFragment.createInstance(movieId, category);
            mFragments.append(position, fragment);
        }
        return fragment;
    }

    public void onSelectChange(int selectPosition){
        int size = mFragments.size();
        for(int i=0;i<size;i++){
            mFragments.valueAt(i).onSelectStateChange(i==selectPosition);
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return getTabName(position);
    }

    public String getTabName(int position){
        if(mCategoryList==null || mCategoryList.size()<=0) {
            return "";
        }
        return mCategoryList.get(position).getName();
    }

    @Override
    public int getCount() {
        if(mCategoryList==null) {
            return 0;
        }
        return mCategoryList.size();
    }
}
