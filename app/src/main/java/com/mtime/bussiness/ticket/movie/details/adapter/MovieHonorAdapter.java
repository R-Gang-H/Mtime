package com.mtime.bussiness.ticket.movie.details.adapter;

import java.util.ArrayList;
import java.util.List;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mtime.R;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsBasic;
import com.mtime.mtmovie.widgets.RoundAngleImageView;

import android.text.TextUtils;
import android.widget.RelativeLayout;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author vivian.wei
 * @date 2019/6/18
 * @desc 电影获奖页Adapter
 */
public class MovieHonorAdapter extends BaseQuickAdapter<MovieDetailsBasic.Festivals, BaseViewHolder> {

    private static final String WIN_COUNT = "获奖%d次";
    private static final String NOMINATE_COUNT = "提名%d次";
    private static final String COMMA = "，";

    // 所有获奖列表
    private List<MovieDetailsBasic.Award.AwardList> mAwardList = new ArrayList<>();

    public MovieHonorAdapter(@Nullable List<MovieDetailsBasic.Festivals> data) {
        super(R.layout.item_movie_honor, data);
        addChildClickViewIds(R.id.item_movie_honor_festival_rl);
    }

    @Override
    protected void convert(BaseViewHolder holder, MovieDetailsBasic.Festivals bean) {
        // 电影节图片
        RoundAngleImageView imageView = holder.getView(R.id.item_movie_honor_festival_img_iv);
        imageView.setForegroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.topic_negative_bg));
        ImageHelper.with(getContext(), ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_TRIM_HEIGHT)
                .override(MScreenUtils.dp2px(65), MScreenUtils.dp2px(97))
                .view(imageView)
                .load(bean.img)
                .placeholder(R.drawable.default_image)
                .showload();

        // 电影节中英文名
        if(TextUtils.isEmpty(bean.nameCn)) {
            holder.setText(R.id.item_movie_honor_festival_name_cn_tv, bean.nameEn);
            holder.setGone(R.id.item_movie_honor_festival_name_en_tv, true);
        } else {
            holder.setText(R.id.item_movie_honor_festival_name_cn_tv, bean.nameCn);
            holder.setText(R.id.item_movie_honor_festival_name_en_tv, bean.nameEn);
            holder.setGone(R.id.item_movie_honor_festival_name_en_tv, false);
        }

        // 获取指定电影节的获奖列表
        MovieDetailsBasic.Award.AwardList fesAwardList = getAwardListByFesId(bean.festivalId);
        // 指定电影节获奖总数
        if(fesAwardList == null || (fesAwardList.winCount == 0 && fesAwardList.nominateCount == 0)) {
            holder.setGone(R.id.item_movie_honor_festival_award_count_tv, true);
        } else {
            holder.setGone(R.id.item_movie_honor_festival_award_count_tv, false);
            StringBuffer buffer = new StringBuffer();
            if(fesAwardList.winCount > 0) {
                buffer.append(String.format(WIN_COUNT, fesAwardList.winCount));
            }
            if(fesAwardList.nominateCount > 0) {
                if(fesAwardList.winCount > 0) {
                    buffer.append(COMMA);
                }
                buffer.append(String.format(NOMINATE_COUNT, fesAwardList.nominateCount));
            }
            holder.setText(R.id.item_movie_honor_festival_award_count_tv, buffer.toString());
        }

        // 箭头
        holder.setImageResource(R.id.item_movie_honor_festival_arrow_iv, bean.isExpand ? R.drawable.movie_honor_up : R.drawable.movie_honor_down);

        // 展开模块
        if(fesAwardList == null || CollectionUtils.isEmpty(fesAwardList.winAwards) && CollectionUtils.isEmpty(fesAwardList.nominateAwards)) {
            holder.setGone(R.id.item_movie_honor_expand_rl, true);
        } else {
            holder.setGone(R.id.item_movie_honor_expand_rl, !bean.isExpand);
            // 获奖列表
            if (CollectionUtils.isEmpty(fesAwardList.winAwards)) {
                holder.setGone(R.id.item_movie_honor_win_label_tv, true);
                holder.setGone(R.id.item_movie_honor_win_recyclerview, true);
            } else {
                holder.setGone(R.id.item_movie_honor_win_label_tv, false);
                holder.setGone(R.id.item_movie_honor_win_recyclerview, false);
                MovieHonorAwardAdapter winAdapter = new MovieHonorAwardAdapter(fesAwardList.winAwards);
                RecyclerView winRecyclerView = holder.getView(R.id.item_movie_honor_win_recyclerview);
                winRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                winRecyclerView.setAdapter(winAdapter);
                // 底部间距
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) winRecyclerView.getLayoutParams();
                params.bottomMargin = CollectionUtils.isEmpty(fesAwardList.nominateAwards) ? 0 : MScreenUtils.dp2px(30);
                winRecyclerView.setLayoutParams(params);
            }
            // 提名列表
            if (CollectionUtils.isEmpty(fesAwardList.nominateAwards)) {
                holder.setGone(R.id.item_movie_honor_nominate_label_tv, true);
                holder.setGone(R.id.item_movie_honor_nominate_recyclerview, true);
            } else {
                holder.setGone(R.id.item_movie_honor_nominate_label_tv, false);
                holder.setGone(R.id.item_movie_honor_nominate_recyclerview, false);
                MovieHonorAwardAdapter nominateAdapter = new MovieHonorAwardAdapter(fesAwardList.nominateAwards);
                RecyclerView nominateRecyclerView = holder.getView(R.id.item_movie_honor_nominate_recyclerview);
                nominateRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                nominateRecyclerView.setAdapter(nominateAdapter);
            }
        }
    }

    // 获奖提名列表赋值
    public void setAwardList(List<MovieDetailsBasic.Award.AwardList> awardList) {
        mAwardList = awardList;
    }

    // 获取指定电影节的获奖列表
    private MovieDetailsBasic.Award.AwardList getAwardListByFesId(int fesId) {
        MovieDetailsBasic.Award.AwardList fesAwardList;
        for(int i = 0, size = mAwardList.size(); i < size; i++) {
            fesAwardList = mAwardList.get(i);
            if(fesAwardList.festivalId == fesId) {
                return fesAwardList;
            }
        }
        return null;
    }
}
