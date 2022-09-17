package com.mtime.bussiness.ticket.movie.adapter;

import android.content.Intent;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtime.bussiness.common.CommonWebActivity;
import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.bussiness.ticket.cinema.bean.GoodsListBean;
import com.mtime.constant.FrameConstant;
import com.mtime.statistic.large.h5.StatisticH5;
import com.mtime.util.ImageURLManager;
import com.mtime.util.JumpUtil;
import com.mtime.util.MallUrlHelper;
import com.mtime.util.MtimeUtils;

import java.util.ArrayList;
import java.util.List;

public class OrderPaySuccessGoodsListAdapter extends RecyclerView.Adapter {

    private final BaseActivity context;
    private final LayoutInflater mLayoutInflater;
    private final ArrayList<GoodsListBean> mDataList = new ArrayList<GoodsListBean>();

    public OrderPaySuccessGoodsListAdapter(BaseActivity context) {
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void addAll(List<GoodsListBean> list) {
        int lastIndex = this.mDataList.size();
        if (this.mDataList.addAll(list)) {
            notifyItemRangeInserted(lastIndex, list.size());
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.orderpay_success_goods_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final GoodsListBean itemBean = mDataList.get(position);

        ViewHolder viewHolder = (ViewHolder) holder;
        if (!MtimeUtils.isNull(itemBean.getImage())) {
            int width = (FrameConstant.SCREEN_WIDTH - MScreenUtils.dp2px(context, 30)) / 2;
            context.volleyImageLoader.displayImage(itemBean.getImage(), viewHolder.goods_item_img, 0, 0,
                    width, width, ImageURLManager.SCALE_TO_FIT, null);
        }

        try {
            if (!MtimeUtils.isNull(itemBean.getIconText())) {
                viewHolder.goods_item_tag.setText(itemBean.getIconText());
                if (!MtimeUtils.isNull(itemBean.getBackground())) {
                    String titleColor = itemBean.getBackground();
                    if (!titleColor.contains("#")) {
                        titleColor = "#" + titleColor;
                    }
                    viewHolder.goods_item_tag.setBackgroundColor(Color.parseColor(titleColor));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        viewHolder.goods_item_name.setText(itemBean.getName());
        viewHolder.goods_item_marketprice.setText(String.format(context.getResources().getString(R.string.st_order_pay_success_marketprice), MtimeUtils.moneyF2Y(itemBean.getMarketPrice())));
        viewHolder.goods_item_price.setText(String.format(context.getResources().getString(R.string.st_order_pay_success_price), MtimeUtils.moneyF2Y(itemBean.getMinSalePrice())));
        viewHolder.goods_item_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO X 不走logx?
                openview(itemBean.getGoodsUrl());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        View goods_item_layout;
        ImageView goods_item_img;
        TextView goods_item_tag;
        TextView goods_item_name;
        TextView goods_item_marketprice;
        TextView goods_item_price;

        public ViewHolder(View itemView) {
            super(itemView);
            goods_item_layout = itemView.findViewById(R.id.goods_item_layout);
            goods_item_img = itemView.findViewById(R.id.goods_item_img);
            goods_item_tag = itemView.findViewById(R.id.goods_item_tag);
            goods_item_name = itemView.findViewById(R.id.goods_item_name);
            goods_item_marketprice = itemView.findViewById(R.id.goods_item_marketprice);
            goods_item_price = itemView.findViewById(R.id.goods_item_price);

        }
    }

    private void openview(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        MallUrlHelper.MallUrlType type = MallUrlHelper.getUrlType(url);

        Class<?> activityId = getActivityId(type);
        
        if (activityId == CommonWebActivity.class) {
            JumpUtil.startCommonWebActivity(context, url, StatisticH5.PN_H5, null,
                    true, true, true, false, context.assemble().toString());
        } else {
            Intent intent = new Intent();
            intent.putExtra("SHOW_TITLE", true);
            intent.putExtra("LOAD_URL", url);
            intent.putExtra("DEFAULT_URL", url);
            context.startActivity(activityId, intent);
        }
    }

    private Class<?> getActivityId(MallUrlHelper.MallUrlType urlType) {
        Class<?> activityId = null;

        switch (urlType) {
            case PRODUCT_VIEW: {
//                activityId = ProductViewActivity.class;
                break;
            }
            case PRODUCTS_LIST:
            case PRODUCTS_LIST_SEARCH: {
//                activityId = ProductListActivity.class;
                break;
            }
            default: {
                activityId = CommonWebActivity.class;
                break;
            }
        }
        return activityId;
    }
}
