package com.mtime.bussiness.ticket.movie.details.adapter;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mtime.R;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageLoadOptions;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.bussiness.ticket.movie.boxoffice.bean.HomeBoxOfficeTabListDetailMovieBean;
import com.mtime.mtmovie.widgets.PosterFilterView;
import com.mtime.mtmovie.widgets.ScoreView;

import java.util.List;



/**
 * @author vivian.wei
 * @date 2019/6/24
 * @desc 推荐票房榜Adapter
 */
public class RecommendBoxofficeAdapter extends BaseQuickAdapter<HomeBoxOfficeTabListDetailMovieBean, BaseViewHolder> {

    private static final int TOP_COUNT = 3;

    public RecommendBoxofficeAdapter(@Nullable List<HomeBoxOfficeTabListDetailMovieBean> data) {
        super(R.layout.item_recommend_boxoffice, data);
        addChildClickViewIds(R.id.item_recommend_boxoffice_ticket_tv);
    }

    @Override
    protected void convert(BaseViewHolder holder, HomeBoxOfficeTabListDetailMovieBean bean) {
        // 序号
        int position = holder.getAdapterPosition() + 1;
        holder.setText(R.id.item_recommend_boxoffice_num_tv, String.format("%d.", position));
        holder.setTextColor(R.id.item_recommend_boxoffice_num_tv,
                ContextCompat.getColor(getContext(), position > TOP_COUNT ? R.color.color_AAB7C7 : R.color.color_feb12a));
        // 中英文名
        holder.setText(R.id.item_recommend_boxoffice_name_tv, bean.getName());
        if(TextUtils.isEmpty(bean.getNameEn())) {
            holder.setGone(R.id.item_recommend_boxoffice_name_en_tv, true);
        } else {
            holder.setGone(R.id.item_recommend_boxoffice_name_en_tv, false);
            holder.setText(R.id.item_recommend_boxoffice_name_en_tv, bean.getNameEn());
        }
        // 评分
        ScoreView scoreView = holder.getView(R.id.item_recommend_boxoffice_score_view);
        scoreView.setScore(bean.getRating());
        holder.setGone(R.id.item_recommend_boxoffice_rating_label_tv, !(bean.getRating() > 0));
        // 票房
        if(TextUtils.isEmpty(bean.getWeekBoxOffice())) {
            holder.setGone(R.id.item_recommend_boxoffice_week_boxoffice_tv, true);
        } else {
            holder.setGone(R.id.item_recommend_boxoffice_week_boxoffice_tv, false);
            holder.setText(R.id.item_recommend_boxoffice_week_boxoffice_tv, bean.getWeekBoxOffice().replace("\n", " "));
        }
        if(TextUtils.isEmpty(bean.getTotalBoxOffice())) {
            holder.setGone(R.id.item_recommend_boxoffice_total_boxoffice_tv, true);
        } else {
            holder.setGone(R.id.item_recommend_boxoffice_total_boxoffice_tv, false);
            holder.setText(R.id.item_recommend_boxoffice_total_boxoffice_tv, bean.getTotalBoxOffice().replace("\n", " "));
        }
        // 封面图
        PosterFilterView imageView = holder.getView(R.id.item_recommend_boxoffice_poster_iv);
        ImageLoadOptions.Builder builder = ImageHelper.with(getContext(),
                ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                .override(MScreenUtils.dp2px(65), MScreenUtils.dp2px(97))
                .roundedCorners(4, 0)
                .view(imageView)
                .load(bean.getPosterUrl())
                .placeholder(R.drawable.default_image);
        imageView.setPosterFilter(false);
        builder.showload();
        // 购票|预售按钮
        if (bean.getIsTicket() || bean.getIsPresell()) {
            holder.setGone(R.id.item_recommend_boxoffice_ticket_tv, false);
            holder.setBackgroundResource(R.id.item_recommend_boxoffice_ticket_tv,
                    bean.getIsTicket() ? R.drawable.common_btn_buy_ticket_bg : R.drawable.common_btn_presell_ticket_bg);
        } else {
            holder.setGone(R.id.item_recommend_boxoffice_ticket_tv, true);
        }
    }

}
