package com.mtime.bussiness.ticket.movie.details.adapter;

import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mtime.R;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageLoadOptions;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.bussiness.ticket.movie.bean.RelatedMovieBean;
import com.mtime.common.utils.ConvertHelper;
import com.mtime.mtmovie.widgets.PosterFilterView;
import com.mtime.mtmovie.widgets.ScoreView;

import java.util.List;

/**
 * @author vivian.wei
 * @date 2019/5/22
 * @desc 影片关联电影Adapter
 */
public class MovieRelatedMovieAdapter extends BaseQuickAdapter<RelatedMovieBean, BaseViewHolder> {

    public MovieRelatedMovieAdapter(@Nullable List<RelatedMovieBean> data) {
        super(R.layout.item_movie_related_movie, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, RelatedMovieBean bean) {
        // 图片
        PosterFilterView imageView = holder.getView(R.id.item_movie_related_movie_poster_iv);
        ImageLoadOptions.Builder builder = ImageHelper.with(getContext(),
                ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                .override(MScreenUtils.dp2px(80), MScreenUtils.dp2px(119))
                .roundedCorners(4, 0)
                .view(imageView)
                .load(bean.getImg())
                .placeholder(R.drawable.default_image);
        imageView.setPosterFilter(false);
        builder.showload();
        // 评分
        if(TextUtils.isEmpty(bean.getRating())) {
            holder.setGone(R.id.item_movie_related_movie_score_layout, true);
        } else {
            holder.setGone(R.id.item_movie_related_movie_score_layout, false);
            ScoreView scoreView = holder.getView(R.id.item_movie_related_movie_score_tv);
            scoreView.setScore(bean.getRating());
        }

        holder.setText(R.id.item_movie_related_movie_title_tv, bean.getTitle());
        holder.setText(R.id.item_movie_related_movie_name_en_tv, bean.getNameEn());
        String director = bean.getDirector1();
        String actor = bean.getActor1() + " " + bean.getActor2();
        String release = bean.getReleaseDate() + " " + bean.getReleaseArea();
        holder.setText(R.id.item_movie_related_movie_director_tv, "导演：" + ConvertHelper.toString(director, "--"));
        holder.setText(R.id.item_movie_related_movie_actor_tv, "主演：" + ConvertHelper.toString(actor, "--"));
        holder.setText(R.id.item_movie_related_movie_release_tv, "上映日期：" + ConvertHelper.toString(release, "--"));
        // 分隔线
        holder.setVisible(R.id.item_movie_related_movie_line_view, !bean.isGroupEnd());
    }
}
