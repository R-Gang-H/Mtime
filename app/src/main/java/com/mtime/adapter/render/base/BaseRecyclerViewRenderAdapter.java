package com.mtime.adapter.render.base;

import android.annotation.TargetApi;
import android.os.Build;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.mtime.R;

/**
 * 继承自RecyclerView.Adapter的基类adapter，同时将ViewHolder和AdapterTypeRender封装加入。在具体的根据type实现不同item布局
 * 的adapter只需要实现BaseAdapterTypeRender<MRecyclerViewTypeExtraHolder>方法实例出一个render类即可实现item布局以及事件等
 * Created by yinguanping on 16/3/25.
 */
@Deprecated
public abstract class BaseRecyclerViewRenderAdapter extends RecyclerView.Adapter<MRecyclerViewTypeExtraHolder> {
    private static final int TYPE_HEADER_VIEW = 0x7683;
    private final View headerView;
    private static final int TYPE_FOOTER_VIEW = 0x7684;
    private final View footerView;
    private int extraCount;


    protected BaseRecyclerViewRenderAdapter(View headerView, View footerView) {
        this.headerView = headerView;
        this.footerView = footerView;
        extraCount += hasHeaderView() ? 1 : 0;
        extraCount += hasFooterView() ? 1 : 0;
    }

    public boolean hasHeaderView() {
        return null != headerView;
    }

    public boolean hasFooterView() {
        return null != footerView;
    }

    public int innerPositionToRealItemPosition(int innerPosition) {
        return hasHeaderView() ? innerPosition - 1 : innerPosition;
    }

    public int realItemPositionToInnerPosition(int realItemPosition) {
        return hasHeaderView() ? realItemPosition + 1 : realItemPosition;
    }

    @TargetApi(Build.VERSION_CODES.DONUT)
    @Override
    public MRecyclerViewTypeExtraHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        BaseAdapterTypeRender<MRecyclerViewTypeExtraHolder> render = getAdapterTypeRender(viewType);
        MRecyclerViewTypeExtraHolder holder = render.getReusableComponent();
        holder.itemView.setTag(R.id.adapter_item_type_render, render);
        render.fitEvents();
        return holder;
    }

    @TargetApi(Build.VERSION_CODES.DONUT)
    @Override
    public void onBindViewHolder(MRecyclerViewTypeExtraHolder holder, int innerPosition) {
        BaseAdapterTypeRender<MRecyclerViewTypeExtraHolder> render = (BaseAdapterTypeRender<MRecyclerViewTypeExtraHolder>)
                holder.itemView.getTag(R.id.adapter_item_type_render);
        /**
         * 计算该item在list中的index（不包括headerView和footerView）
         */
        int realItemPosition = innerPositionToRealItemPosition(innerPosition);
        render.fitDatas(realItemPosition);
        /**
         * 重新设置item在list中的index（不包括headerView和footerView）
         */
        holder.setRealItemPosition(realItemPosition);
    }

    /**
     * 通过类型获得对应的render（不包括headerView和footerView）
     *
     * @param type
     * @return
     */
    public abstract BaseAdapterTypeRender<MRecyclerViewTypeExtraHolder> getAdapterTypeRenderExcludeExtraView(int type);

    /**
     * 获取item的数量（不包括headerView和footerView）
     *
     * @return
     */
    public abstract int getItemCountExcludeExtraView();

    /**
     * 通过realItemPosition得到该item的类型（不包括headerView和footerView）
     *
     * @param realItemPosition
     * @return
     */
    public abstract int getItemViewTypeExcludeExtraView(int realItemPosition);

    public BaseAdapterTypeRender<MRecyclerViewTypeExtraHolder> getAdapterTypeRender(int type) {
        switch (type) {
            case TYPE_HEADER_VIEW:
                return new MRecyclerViewTypeExtraRender(headerView);
            case TYPE_FOOTER_VIEW:
                return new MRecyclerViewTypeExtraRender(footerView);
            default:
                return getAdapterTypeRenderExcludeExtraView(type);
        }
    }

    @Override
    public int getItemCount() {
        return getItemCountExcludeExtraView() + extraCount;
    }

    @Override
    public int getItemViewType(int innerPosition) {
        if (null != headerView && 0 == innerPosition) { // header
            return TYPE_HEADER_VIEW;
        } else if (null != footerView && getItemCount() - 1 == innerPosition) { // footer
            return TYPE_FOOTER_VIEW;
        }
        return getItemViewTypeExcludeExtraView(innerPositionToRealItemPosition(innerPosition));
    }
}