package com.mtime.bussiness.ticket.cinema.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aspsine.irecyclerview.IViewHolder;
import com.mtime.R;
import com.mtime.bussiness.ticket.cinema.bean.CouponActivityItem;
import com.mtime.constant.Constants;

import java.util.List;

/**
 * Created by vivian.wei on 2017/10/23.
 * tab_购票_影院_具体影院优惠活动列表Adapter
 */

public class TabTicketCinemaCouponAdapter extends RecyclerView.Adapter<TabTicketCinemaCouponAdapter.ViewHolder> {

    private static final String ICON_TEXT_MSG = "讯";
    private static final String ICON_TEXT_COUPON = "惠";

    private final Context mContext;
    private List<CouponActivityItem> mList;
    private boolean mShowAll = false;

    public TabTicketCinemaCouponAdapter(final Context context, final List<CouponActivityItem> list) {
        this.mContext = context;
        this.mList = list;
    }

    public void setList(final List<CouponActivityItem> list) {
        this.mList = list;
    }

    // 显示全部
    public void setShowAll(boolean showAll) {
        mShowAll = showAll;
    }

    public boolean isShowAll() {
        return mShowAll;
    }

    public Object getItem(final int arg0) {
        return mList.get(arg0);
    }

    public long getItemId(final int arg0) {
        return arg0;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_cinema_list_coupon_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        CouponActivityItem bean = mList.get(position);
        if(bean.getDsActivityFlag() == Constants.DIRECT_SALES_ACTIVITY_FLAG) {
            // 第三方直销
            setIconColorAndText(holder.iconTv, R.color.color_f15353, ICON_TEXT_COUPON);
            holder.tagTv.setText(bean.getDesc());
        } else {
            // 时光
            if (bean.getIsSelected()) {
                setIconColorAndText(holder.iconTv, R.color.color_ff783d, ICON_TEXT_MSG);
            } else {
                setIconColorAndText(holder.iconTv, R.color.color_f15353, ICON_TEXT_COUPON);
            }
            holder.tagTv.setText(bean.getTag());
        }
    }

    // 设置Icon背景色和文字
    private void setIconColorAndText(TextView view, int color, String text) {
        view.setBackgroundColor(ContextCompat.getColor(mContext, color));
        view.setText(text);
    }

    public class ViewHolder extends IViewHolder {

        TextView iconTv;
        TextView tagTv;

        public ViewHolder(View itemView) {
            super(itemView);

            iconTv = itemView.findViewById(R.id.adapter_cinema_list_coupon_item_icon_tv);
            tagTv = itemView.findViewById(R.id.adapter_cinema_list_coupon_item_tag_tv);
        }
    }
}
