package com.mtime.base.widget.banner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mtime.base.adapter.ReusePagerAdapter;

import java.util.List;

/**
 */

public abstract class AbsBannerAdapter<T> extends ReusePagerAdapter {

    public static class BannerHolder extends Holder {

        public BannerHolder(View view) {
            super(view);
        }
    }


    protected List<T> mDatas;
    private boolean mCanLoop = true;
    private boolean mIsNotify = false;
    private IBannerIndicator mIndicator;

    void setCanLoop(boolean canLoop) {
        this.mCanLoop = canLoop;
    }

    public void setDatas(List<T> mDatas) {
        this.mDatas = mDatas;
    }

    public List<T> getDatas() {
        return mDatas;
    }

    public void setIndicator(IBannerIndicator indicator) {
        mIndicator = indicator;
    }

    @Override
    public void notifyDataSetChanged() {
        mIsNotify = mCanLoop;
        if (null != mIndicator) {
            mIndicator.onNotifyDataChanged(getRealCount());
        }
        super.notifyDataSetChanged();
        mIsNotify = false;
    }

    @Override
    public int getItemPosition(Object object) {
        if (!mIsNotify) {
            return super.getItemPosition(object);
        } else {
            return POSITION_NONE;
        }
    }

    /**
     * 返回真实数据的下标
     */
    public int getRealPosition2AdapterPostiton(int position) {
        if (getRealCount() > 0) {
            return position % getRealCount();
        } else {
            return 0;
        }
    }

    public T getItem(int position) {
        int realPosition = getRealPosition2AdapterPostiton(position);
        if (null != mDatas && realPosition >= 0 && realPosition < mDatas.size()) {
            return mDatas.get(realPosition);
        }
        return null;
    }

    //获取真实的count
    public int getRealCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public int getItemCount() {
        int count = getRealCount();
        return mCanLoop && count > 1 ? Integer.MAX_VALUE : count;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BannerHolder(onCreateView(LayoutInflater.from(parent.getContext()), parent, viewType));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        onBindView(holder.itemView, getRealPosition2AdapterPostiton(position), getItem(position));
    }

    public abstract View onCreateView(LayoutInflater inflater, ViewGroup container, int viewType);

    public abstract void onBindView(View itemView, int position, T itemBean);
}
