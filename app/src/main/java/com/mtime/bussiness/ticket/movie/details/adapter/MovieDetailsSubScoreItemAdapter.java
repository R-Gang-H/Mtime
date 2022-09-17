package com.mtime.bussiness.ticket.movie.details.adapter;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mtime.R;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsBasic;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2021/5/24
 */
public class MovieDetailsSubScoreItemAdapter extends BaseQuickAdapter<MovieDetailsBasic.SubItemRating, BaseViewHolder> {

    public MovieDetailsSubScoreItemAdapter(List<MovieDetailsBasic.SubItemRating> data) {
        super(R.layout.item_movie_details_head_sub_score_item ,data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, MovieDetailsBasic.SubItemRating subItemRating) {
        holder.setText(R.id.item_movie_details_head_sub_score_title_tv, subItemRating.title)
                .setText(R.id.item_movie_details_head_sub_score_num_tv, String.valueOf(subItemRating.rating));
    }

}
