package com.mtime.bussiness.ticket.adapter;

import android.os.Parcelable;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * 重写PagerAdapter使ViewPager能够循环左右滑动
 * 
 * @author ye
 * 
 */
public class PhotoDetailCyclicPagerAdapter extends PagerAdapter {

    private final PagerAdapter mAdapter;

    public PhotoDetailCyclicPagerAdapter(final PagerAdapter adapter) {
	mAdapter = adapter;
    }

    
    public int getCount() {
	if (getRealCount() == 1) {
	    return 1;
	} else {
	    return Integer.MAX_VALUE;
	}

    }

    public int getRealCount() {
	return mAdapter.getCount();
    }

    
    public Object instantiateItem(final ViewGroup container, final int position) {
	final int virtualPosition = position % getRealCount();
	return mAdapter.instantiateItem(container, virtualPosition);
    }

    
    public void destroyItem(final ViewGroup container, final int position,
	    final Object object) {
	final int virtualPosition = position % getRealCount();
	mAdapter.destroyItem(container, virtualPosition, object);
    }

    
    public void finishUpdate(final ViewGroup container) {
	mAdapter.finishUpdate(container);
    }

    
    public boolean isViewFromObject(final View view, final Object object) {
	return mAdapter.isViewFromObject(view, object);
    }

    
    public void restoreState(final Parcelable bundle,
	    final ClassLoader classLoader) {
	mAdapter.restoreState(bundle, classLoader);
    }

    
    public Parcelable saveState() {
	return mAdapter.saveState();
    }

    
    public void startUpdate(final ViewGroup container) {
	mAdapter.startUpdate(container);
    }
}
