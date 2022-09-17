package com.mtime.bussiness.ticket.movie.details.adapter;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mtime.R;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsBasic;

import java.util.List;

/**
 * @author vivian.wei
 * @date 2019/6/19
 * @desc 电影获奖页_获奖|提名详情列表Adapter
 */
public class MovieHonorAwardAdapter extends BaseQuickAdapter<MovieDetailsBasic.Award.AwardList.WinAward, BaseViewHolder> {

    private static final float PADDING_BOTTOM_DP = 15;

    public MovieHonorAwardAdapter(@Nullable List<MovieDetailsBasic.Award.AwardList.WinAward> data) {
        super(R.layout.item_movie_honor_award, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, MovieDetailsBasic.Award.AwardList.WinAward bean) {
        // 获奖信息
        StringBuffer buffer = new StringBuffer();
        if(bean.sequenceNumber > 0) {
            buffer.append(String.format("第%d届", bean.sequenceNumber));
        }
        if(!TextUtils.isEmpty(bean.festivalEventYear)) {
            buffer.append(String.format("(%s)", bean.festivalEventYear));
        }
        if(!TextUtils.isEmpty(bean.awardName)) {
            buffer.append(String.format("-%s", bean.awardName));
        }
        if(buffer.length() > 0) {
            holder.setGone(R.id.item_movie_honor_award_info_tv, false);
            holder.setText(R.id.item_movie_honor_award_info_tv, buffer.toString());
        } else {
            holder.setGone(R.id.item_movie_honor_award_info_tv, true);
        }
        // 获奖人列表
        if (CollectionUtils.isEmpty(bean.persons)) {
            holder.setGone(R.id.item_movie_honor_award_person_recyclerview, true);
        } else {
            holder.setGone(R.id.item_movie_honor_award_person_recyclerview, false);
            MovieHonorAwardPersonAdapter adapter = new MovieHonorAwardPersonAdapter(bean.persons);
            RecyclerView recyclerView = holder.getView(R.id.item_movie_honor_award_person_recyclerview);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
        }
        holder.itemView.setPadding(0, 0, 0 , holder.getLayoutPosition() == getData().size() - 1 ? 0 : MScreenUtils.dp2px(PADDING_BOTTOM_DP));
    }

}
