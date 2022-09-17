package com.mtime.bussiness.ticket.cinema.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mtime.R;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * @author vivian.wei
 * @date 2020/10/5
 * @desc 影院详情页_特色设施
 */
public class CinemaViewFeatureAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public CinemaViewFeatureAdapter(@Nullable List<String> data) {
        super(R.layout.item_special_part, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, String bean) {
        holder.setText(R.id.item_special_part_tv, bean);
    }
}
