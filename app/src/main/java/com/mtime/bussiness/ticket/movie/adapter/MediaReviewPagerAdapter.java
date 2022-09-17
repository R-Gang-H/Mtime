package com.mtime.bussiness.ticket.movie.adapter;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by mtime on 15/10/28.
 */
public class MediaReviewPagerAdapter extends PagerAdapter {
    private ArrayList<String> titleStrings = new ArrayList<String>();
    private final ArrayList<View>   pageViews;

    public MediaReviewPagerAdapter(ArrayList<View> pageViews, ArrayList<String> titleStrings) {
        super();
        this.pageViews = pageViews;
        this.titleStrings = titleStrings;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleStrings.get(position);
    }

    @Override
    public int getCount() {
        return pageViews.size();
    }

    @Override
    public boolean isViewFromObject(final View arg0, final Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public int getItemPosition(final Object object) {

        return super.getItemPosition(object);
    }

    @Override
    public void destroyItem(final View arg0, final int arg1, final Object arg2) {

        ((ViewPager) arg0).removeView((View) arg2);
    }

    @Override
    public Object instantiateItem(final View arg0, final int arg1) {
        ((ViewPager) arg0).addView(pageViews.get(arg1));
        return pageViews.get(arg1);
    }
}
