package com.mtime.base.recyclerview;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用的RecyclerAdapter
 */
public abstract class CommonRecyclerAdapter<T> extends RecyclerView.Adapter<CommonViewHolder>{
    public interface OnItemListener{
        void onItemClick(View itemView, int position);
    }
    
    protected Context mContext;
    private OnItemListener mOnItemListener;
    private List<T> mDatas = new ArrayList<>();
    private boolean mIsBindListener = false; //是否绑定item点击监听

    public CommonRecyclerAdapter(Context context){
        mContext = context;
    }

    public CommonRecyclerAdapter(Context context, List<T> datas){
        this(context);
        setDatas(datas);
    }
    
    public void setDatas(List<T> datas){
        this.mDatas = datas;
    }
    
    public void appendDatas(List<T> datas) {
        int positionStart = getItemCount();
        int itemCount = datas.size() - 1;
        this.mDatas.addAll(datas);
        notifyItemRangeInserted(positionStart, itemCount);
    }
    
    public void removeItem(int postion) {
        if(null != mDatas && postion < mDatas.size()) {
            mDatas.remove(postion);
            notifyItemRemoved(postion);
        }
    }

    public void setBindListener(boolean bindListener) {
        mIsBindListener = bindListener;
    }

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return CommonViewHolder.get(this.mContext, viewGroup, getItemLayoutId(viewType));
    }

    @Override
    public void onBindViewHolder(final CommonViewHolder holder, int position) {
        if(mIsBindListener) {
            onBindItemListener(holder);
        }
        onBindItemHolder(holder, getItem(position), position);
    }
    
    private void onBindItemListener(final CommonViewHolder holder){
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnItemListener) {
                    int pos = holder.getAdapterPosition();
                    try {
                        mOnItemListener.onItemClick(holder.itemView, pos);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return null != this.mDatas ? this.mDatas.size() : 0;
    }

    public T getItem(int position) {
        return (null != mDatas && position >= 0 && position < mDatas.size()) ? mDatas.get(position) : null;
    }

    public void setOnItemListener(OnItemListener listener) {
            this.mOnItemListener = listener;
    }

    public abstract int getItemLayoutId(int viewType);

    public abstract void onBindItemHolder(final CommonViewHolder holder, T item, int position);
}
