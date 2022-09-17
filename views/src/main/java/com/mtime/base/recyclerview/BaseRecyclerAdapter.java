package com.mtime.base.recyclerview;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;

import com.mtime.base.utils.CollectionUtils;

import java.util.List;

/**
 * Created by liuyu on 2017/5/2.
 */

public abstract class BaseRecyclerAdapter<VH extends RecyclerView.ViewHolder, T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public LayoutInflater mInflate;

    public List<T> mDataList;

    public Context mContext;

    public BaseRecyclerAdapter(Context context, List<T> dataList) {
        this.mInflate = LayoutInflater.from(context);
        this.mDataList = dataList;
        this.mContext = context;
    }

    public void setDataList(List<T> dataList) {
        this.mDataList = dataList;
    }

    public void addDataList(List<T> dataList) {
        if (dataList != null && dataList.size() > 0) {
            this.mDataList.addAll(dataList);
        }
    }

    public List<T> getDataList() {
        return mDataList;
    }


    public T getItem(int position) {
        if (CollectionUtils.size(mDataList) > 0) {
            return mDataList.get(position);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return CollectionUtils.size(mDataList);
    }

    public Context getContext() {
        return mContext;
    }
}
