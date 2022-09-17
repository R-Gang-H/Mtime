package com.mtime.bussiness.ticket.movie.details.adapter;

import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mtime.R;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsBasic;

import java.util.List;

/**
 * @author vivian.wei
 * @date 2019/6/19
 * @desc 电影获奖页_获奖|提名详情列表_获奖影人列表Adapter
 */
public class MovieHonorAwardPersonAdapter extends BaseQuickAdapter<MovieDetailsBasic.Award.AwardList.WinAward.Person, BaseViewHolder> {

    public MovieHonorAwardPersonAdapter(@Nullable List<MovieDetailsBasic.Award.AwardList.WinAward.Person> data) {
        super(R.layout.item_movie_honor_award_person, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, MovieDetailsBasic.Award.AwardList.WinAward.Person bean) {
        // 影人中英文名
        StringBuffer buffer = new StringBuffer();
        if(!TextUtils.isEmpty(bean.nameCn)) {
            buffer.append(bean.nameCn);
            if(!TextUtils.isEmpty(bean.nameEn)) {
                buffer.append(" ");
            }
        }
        if(!TextUtils.isEmpty(bean.nameEn)) {
            buffer.append(bean.nameEn);
        }
        holder.setText(R.id.item_movie_honor_award_person_tv, buffer.toString());
    }

}
