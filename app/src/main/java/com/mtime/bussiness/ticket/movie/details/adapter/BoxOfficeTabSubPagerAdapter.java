package com.mtime.bussiness.ticket.movie.details.adapter;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.collection.SparseArrayCompat;

import com.mtime.bussiness.ticket.movie.boxoffice.bean.HomeBoxOfficeTabListBean;
import com.mtime.bussiness.ticket.movie.boxoffice.fragment.RecommendBoxOfficeFragment;

import java.util.List;

/**
 * @author vivian.wei
 * @date 2019/6/21
 * @desc 票房Tab子页面PagerAdapter
 */
public class BoxOfficeTabSubPagerAdapter extends FragmentPagerAdapter {

    private final SparseArrayCompat<Fragment> mFragments;
    private final List<HomeBoxOfficeTabListBean> mNavList;

    public BoxOfficeTabSubPagerAdapter(FragmentManager fm, List<HomeBoxOfficeTabListBean> navList) {
        super(fm);
        mFragments = new SparseArrayCompat<>(navList.size());
        mNavList = navList;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = mFragments.get(position);
        if (null == fragment) {
            fragment = new RecommendBoxOfficeFragment();
            Bundle bundle = new Bundle();
            bundle.putString(RecommendBoxOfficeFragment.KEY_PAGE_SUB_AREA_ID, mNavList.get(position).getPageSubAreaId());
            fragment.setArguments(bundle);
            mFragments.append(position, fragment);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return null != mNavList ? mNavList.size() : 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mNavList != null ? mNavList.get(position).getTitle() : null;
    }
}
