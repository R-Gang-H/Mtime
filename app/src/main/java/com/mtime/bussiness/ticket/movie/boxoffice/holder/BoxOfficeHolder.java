package com.mtime.bussiness.ticket.movie.boxoffice.holder;

import android.content.Context;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.R;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.bussiness.common.widget.TabLayoutHelper;
import com.mtime.bussiness.ticket.movie.boxoffice.bean.HomeBoxOfficeTabListBean;
import com.mtime.bussiness.ticket.movie.details.adapter.BoxOfficeTabSubPagerAdapter;
import com.mtime.bussiness.ticket.movie.boxoffice.fragment.BoxOfficeFragment;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author vivian.wei
 * @date 2019/6/21
 * @desc 票房Holder
 */
public class BoxOfficeHolder extends ContentHolder<HomeBoxOfficeTabListBean> {

    @BindView(R.id.fragment_boxoffice_smarttablayout)
    SmartTabLayout mTabLayout;
    @BindView(R.id.fragment_boxoffice_viewpager)
    ViewPager mViewPager;

    private Unbinder mUnbinder;
    private final FragmentManager mFragmentManager;
    private BoxOfficeTabSubPagerAdapter mPageAdapter;
    private TabLayoutHelper mTabLayoutHelper;
    private final ViewPager.OnPageChangeListener mOnPageChangeListener;

    private int mFromType;   //0:默认，设置为全球页签  1：点击内地票房榜。第一个tab

    public BoxOfficeHolder(Context context, FragmentManager fragmentManager, ViewPager.OnPageChangeListener onPageChangeListener) {
        super(context);
        mFragmentManager = fragmentManager;
        mOnPageChangeListener = onPageChangeListener;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onHolderCreated() {
        super.onHolderCreated();
        setContentView(R.layout.fragment_boxoffice);
        initView();
    }

    private void initView() {
        mUnbinder = ButterKnife.bind(this, mRootView);
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);
        mTabLayoutHelper = new TabLayoutHelper(mTabLayout, TabLayoutHelper.TYPE_OF_WEIGHT);
    }

    @Override
    public void refreshView() {
        super.refreshView();

        if(mData == null || CollectionUtils.isEmpty(mData.getSubTopList())) {
            return;
        }

        HomeBoxOfficeTabListBean bean;
        int selectIndex = 0;
        for(int i = 0, size = mData.getSubTopList().size(); i < size; i++) {
            bean = mData.getSubTopList().get(i);
            if(bean.getTitle().equals(BoxOfficeFragment.TAB_TITLE_GLOBAL)) {
                selectIndex = i;
            }
        }
        mPageAdapter = new BoxOfficeTabSubPagerAdapter(mFragmentManager, mData.getSubTopList());
        mViewPager.setAdapter(mPageAdapter);
        mTabLayout.setViewPager(mViewPager);
        if(mFromType == BoxOfficeFragment.FROM_TYPE_GLOBAL) {
            setCurrentItem(selectIndex);
        } else {
            mTabLayoutHelper.initDefaultFocus();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (null != mUnbinder) {
            mUnbinder.unbind();
        }
    }

    private void setCurrentItem(int position) {
        if(null != mViewPager) {
            mViewPager.post(new Runnable() {
                @Override
                public void run() {
                    mViewPager.setCurrentItem(position, false);
                }
            });
        }
    }

    // 设置来源页面
    public void setFromType(int fromType) {
        mFromType = fromType;
    }

    // 获取Tab的title
    public String getTabTitle(int position) {
        if(mData != null && CollectionUtils.isNotEmpty(mData.getSubTopList()) && position > -1 && position < mData.getSubTopList().size()) {
            return mData.getSubTopList().get(position).getTitle();
        }
        return "";
    }

}
