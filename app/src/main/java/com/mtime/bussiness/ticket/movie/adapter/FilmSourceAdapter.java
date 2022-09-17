package com.mtime.bussiness.ticket.movie.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mtime.R;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.bussiness.ticket.movie.bean.ExternalPlayPlayListBean;

import java.util.List;

/**
 * Created by zhuqiguang on 2018/6/20.
 * website www.zhuqiguang.cn
 */
public class FilmSourceAdapter extends BaseQuickAdapter<ExternalPlayPlayListBean, BaseViewHolder> {
    public FilmSourceAdapter(@Nullable List<ExternalPlayPlayListBean> data) {
        super(R.layout.layout_film_resource, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ExternalPlayPlayListBean item) {
        int dp40 = MScreenUtils.dp2px(40);
        ImageHelper.with(getContext(), ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                .override(dp40, dp40)
                .cropCircle()
                .placeholder(R.drawable.common_icon_round_default_avatar)
                .view(helper.getView(R.id.layout_film_resource_icon_iv))
                .load(item.getPicUrl())
                .showload();
        helper.setText(R.id.layout_film_resource_name_tv, item.getPlaySourceName());
        helper.setText(R.id.layout_film_resource_pay_rule_tv, item.getPayRule());
        helper.setGone(R.id.layout_film_resource_line_view, helper.getAdapterPosition() == getItemCount() - 1);
    }
}
