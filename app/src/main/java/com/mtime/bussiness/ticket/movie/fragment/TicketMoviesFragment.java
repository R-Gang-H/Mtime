package com.mtime.bussiness.ticket.movie.fragment;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mtime.R;
import com.mtime.frame.BaseFragment;
import com.mtime.bussiness.common.widget.TabLayoutHelper;
import com.mtime.bussiness.ticket.TabTicketFragment;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;

/**
 * 购票tab 电影（含 正在热映 即将上映）
 */
public class TicketMoviesFragment extends BaseFragment implements ViewPager.OnPageChangeListener {
    private View root;
    private SmartTabLayout mTabLayout;
    public ViewPager viewPager;
    private ArrayList<Fragment> fragments;
    private TicketMoviesOnShowFragment ticketMoviesOnShowFragment;
    private TicketMoviesInComingFragment ticketMoviesInComingFragment;
    private String cityId;
    private static CheckSwitchCityListener mCheckSwitchCityListener;
    private MyPagerAdapter mMyPagerAdapter;
    private TabLayoutHelper mTabLayoutHelper;

    public void setCityId(final String cityId) {
        this.cityId = cityId;
    }

    public void setCheckSwitchCityListener(CheckSwitchCityListener listener) {
        mCheckSwitchCityListener = listener;
    }

    @Override
    protected View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_ticket_movie, container, false);
        return root;
    }

    @Override
    protected void onInitView(Bundle savedInstanceState) {
        mTabLayout = root.findViewById(R.id.ticket_tabLayout);
        viewPager = root.findViewById(R.id.ticket_movie_viewpager);
        mTabLayoutHelper = new TabLayoutHelper(mTabLayout, TabLayoutHelper.TYPE_OF_WEIGHT);

        ticketMoviesOnShowFragment = new TicketMoviesOnShowFragment();
        ticketMoviesInComingFragment = new TicketMoviesInComingFragment();

        ticketMoviesOnShowFragment.setCityId(cityId);
        ticketMoviesInComingFragment.setCityId(cityId);

        fragments = new ArrayList<>();
        fragments.add(ticketMoviesOnShowFragment);
        fragments.add(ticketMoviesInComingFragment);

        //绑定适配器
        mMyPagerAdapter = new MyPagerAdapter(getChildFragmentManager(), fragments);
        viewPager.setAdapter(mMyPagerAdapter);
        //添加切换界面的监听器
        viewPager.addOnPageChangeListener(this);

        mTabLayout.setViewPager(viewPager);
        mTabLayoutHelper.initDefaultFocus();
    }

    // 切换正在热映/即将上映Tab
    @Override
    public void onPageSelected(int arg0) {
        switch (arg0) {
            case 0:
                TabTicketFragment.type = TabTicketFragment.TYPE_MOVIE_HOT;
                break;
            case 1:
                TabTicketFragment.type = TabTicketFragment.TYPE_MOVIE_INCOMING;
                break;
        }
        if((0 == arg0 || 1 == arg0) && null != mCheckSwitchCityListener) {
            mCheckSwitchCityListener.onCheckSwitchCity();
        }
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        String[] titles = {"正在热映", "即将上映"};

        private final ArrayList<Fragment> fragments;

        public Fragment getCurrentFragment() {
            return currentFragment;
        }

        Fragment currentFragment;

        public MyPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public Fragment getItem(int arg0) {
            return fragments.get(arg0);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }


        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            currentFragment = (BaseFragment) object;
            super.setPrimaryItem(container, position, object);
        }
    }

    public interface CheckSwitchCityListener {
        void onCheckSwitchCity();
    }

}
