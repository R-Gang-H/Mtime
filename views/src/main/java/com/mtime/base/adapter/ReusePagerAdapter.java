package com.mtime.base.adapter;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import com.mtime.base.views.R;

import java.util.LinkedList;

/**
 * Created by <a href="mailto:kunlin.wang@mtime.com">Wang kunlin</a>
 * <p>
 * On 2017-06-14
 * <p>
 * 仿照 recyclerview.adapter 实现的具有 item view 可复用功能的 PagerAdapter
 */

public abstract class ReusePagerAdapter<VH extends ReusePagerAdapter.Holder> extends PagerAdapter {

    public static final int INVALID_TYPE = -1;

    public static final int INVALID_POSITION = -1;

    private final SparseArray<LinkedList<VH>> holders = new SparseArray<>(1);

    public abstract int getItemCount();

    public int getItemViewType(int position) {
        return 0;
    }

    public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);

    public abstract void onBindViewHolder(VH holder, int position);

    @Override
    public int getCount() {
        return getItemCount();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int itemViewType = getItemViewType(position);
        LinkedList<VH> holderList = holders.get(itemViewType);
        VH holder;
        if (holderList == null || holderList.size() == 0) {
            holder = onCreateViewHolder(container, itemViewType);
            holder.itemView.setTag(R.id.holder_id, holder);
        } else {
            holder = holderList.pollLast();
        }
        holder.position = position;
        holder.viewType = itemViewType;
        onBindViewHolder(holder, position);
        container.addView(holder.itemView);
        return holder.itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
        VH holder = (VH) view.getTag(R.id.holder_id);
        int itemViewType = holder.viewType;
        holder.viewType = INVALID_TYPE;
        holder.position = INVALID_POSITION;
        LinkedList<VH> holderList = holders.get(itemViewType);
        if (holderList == null) {
            holderList = new LinkedList<>();
            holders.append(itemViewType, holderList);
        }
        holderList.push(holder);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public static abstract class Holder {
        public View itemView;
        public int viewType = INVALID_TYPE;
        public int position = INVALID_POSITION;

        public Holder(View view) {
            if (view == null) {
                throw new IllegalArgumentException("itemView may not be null");
            }
            itemView = view;
        }
    }
}
