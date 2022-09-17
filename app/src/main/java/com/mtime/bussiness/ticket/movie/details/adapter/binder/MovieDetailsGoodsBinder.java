package com.mtime.bussiness.ticket.movie.details.adapter.binder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mtime.R;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsRelatedGoods;
import com.mtime.bussiness.ticket.movie.details.holder.MovieDetailsHolder;
import com.mtime.bussiness.common.widget.CommonItemTitleView;
import com.mtime.frame.BaseStatisticHelper;
import com.mtime.statistic.large.MapBuild;
import com.mtime.util.JumpUtil;

import java.util.List;


/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-06-03
 *
 * 影片详情-相关商品
 */
public class MovieDetailsGoodsBinder extends MovieDetailsBaseBinder<MovieDetailsRelatedGoods> {

    public MovieDetailsGoodsBinder(MovieDetailsHolder.OnJumpPageCallback callback, BaseStatisticHelper helper) {
        super(callback, helper);
    }

    @NonNull
    @Override
    protected BaseViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new BaseViewHolder(inflater.inflate(R.layout.layout_movie_details_goods_list, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull BaseViewHolder holder, @NonNull MovieDetailsRelatedGoods item) {
        CommonItemTitleView titleView = holder.getView(R.id.movie_details_goods_list_title_view);
        titleView.setOnAllBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 埋点上报
                MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam())
                        .put("moreCount", String.valueOf(item.goodsCount));
                String refer = mBaseStatisticHelper.assemble1(
                        "periphery", null,
                        "more", null,
                        null, null, mapBuild.build()).submit();

                //商品列表
//                JumpUtil.startProductListActivity(v.getContext(), null, item.relatedUrl, null);
            }
        });

        RecyclerView recyclerView = holder.getView(R.id.movie_details_goods_list_rv);
        if (null == recyclerView.getAdapter()) {
            recyclerView.setNestedScrollingEnabled(false);
            ListAdapter listAdapter = new ListAdapter(item.goodsList);
            listAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    MovieDetailsRelatedGoods.Goods goods = listAdapter.getItem(position);
                    if (null != goods) {
                        // 埋点上报
                        MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam())
                                .put("goodsID", String.valueOf(goods.goodsId));
                        String refer = mBaseStatisticHelper.assemble1(
                                "periphery", null,
                                "showPeripheries", null,
                                null, null, mapBuild.build()).submit();

                        //商品详情
//                        JumpUtil.startProductViewActivity(view.getContext(), refer, goods.goodsUrl);
                    }
                }
            });
            recyclerView.setAdapter(listAdapter);
        }
    }

    private class ListAdapter extends BaseQuickAdapter<MovieDetailsRelatedGoods.Goods, BaseViewHolder> {
        private int size;

        public ListAdapter(@Nullable List<MovieDetailsRelatedGoods.Goods> data) {
            super(R.layout.item_movie_details_goods, data);
        }

        @Override
        protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
            BaseViewHolder holder = super.onCreateDefViewHolder(parent, viewType);
            int tw = MScreenUtils.getScreenWidth() - MScreenUtils.dp2px(15 * 4);
            size = tw / 3;
            View img = holder.getView(R.id.movie_details_goods_poster_iv);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) img.getLayoutParams();
            params.width = size;
            params.height = size;
            img.setLayoutParams(params);
            return holder;
        }

        @Override
        protected void convert(BaseViewHolder helper, MovieDetailsRelatedGoods.Goods item) {
            ImageView img = helper.getView(R.id.movie_details_goods_poster_iv);
            ImageHelper.with(ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                    .override(size, size)
                    .placeholder(R.drawable.default_image)
                    .load(item.image)
                    .view(img)
                    .showload();

            String price = "";
            if (!TextUtils.isEmpty(item.minSalePriceFormat)) {
                price = "￥" + item.minSalePriceFormat;
            } else if (!TextUtils.isEmpty(item.marketPriceFormat)) {
                price = "￥" + item.marketPriceFormat;
            }
            helper.setText(R.id.movie_details_goods_name_tv, item.longName)
                    .setText(R.id.movie_details_goods_price_tv, price)
                    .setText(R.id.movie_details_goods_ziying_tag_tv, item.goodsTip)
                    .setGone(R.id.movie_details_goods_ziying_tag_tv, TextUtils.isEmpty(item.goodsTip))
                    .setText(R.id.movie_details_goods_new_tag_tv, item.iconText)
                    .setGone(R.id.movie_details_goods_new_tag_tv, TextUtils.isEmpty(item.iconText));
        }
    }
}
