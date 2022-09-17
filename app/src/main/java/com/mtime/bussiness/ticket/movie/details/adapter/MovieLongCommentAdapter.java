package com.mtime.bussiness.ticket.movie.details.adapter;

import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mtime.R;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.bussiness.ticket.movie.bean.MovieHotLongCommentBean;
import com.mtime.mtmovie.widgets.ScoreView;
import com.mtime.util.MtimeUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author vivian.wei
 * @date 2019/5/27
 * @desc 影片长影评列表Adapter
 */
public class MovieLongCommentAdapter extends BaseQuickAdapter<MovieHotLongCommentBean, BaseViewHolder> {

    private static final Pattern LINE_PATTERN = Pattern.compile("\\s*|\t|\r|\n", Pattern.CASE_INSENSITIVE);
    private static final String ELLIPSIS = "...";
    private static final String TIME = "· %s";

    private final OnMovieLongCommentAdapterListener mListener;

    public MovieLongCommentAdapter(@Nullable List<MovieHotLongCommentBean> data, OnMovieLongCommentAdapterListener listener) {
        super(R.layout.item_movie_long_comment, data);
        mListener = listener;
        addChildClickViewIds(R.id.item_movie_long_comment_comment_cl);
    }

    @Override
    protected void convert(BaseViewHolder holder, MovieHotLongCommentBean bean) {
        // 第一条数据顶部多5dp间距
        holder.setGone(R.id.item_movie_long_comment_top_view, holder.getAdapterPosition() != 0);
        // 头像
        ImageHelper.with(getContext(), ImageProxyUrl.SizeType.RATIO_1_1, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                .view(holder.getView(R.id.item_movie_long_comment_head_iv))
                .load(bean.getHeadurl())
                .placeholder(R.drawable.default_image)
                .showload();
        // 昵称
        holder.setText(R.id.item_movie_long_comment_nickname_tv, bean.getNickname());
        // 时间
        holder.setText(R.id.item_movie_long_comment_time_tv, bean.getModifyTime() > 0 ? String.format(TIME, MtimeUtils.formatPublishTime(bean.getModifyTime())) : "");
        // 评分
        ScoreView scoreView = holder.getView(R.id.item_movie_long_comment_rating_tv);
        scoreView.setScore(bean.getRatin());
        // 想看
        if (bean.getRatin() > 0) {
            holder.setGone(R.id.item_movie_long_comment_want_see_tv, true);
        }
        else {
            holder.setGone(R.id.item_movie_long_comment_want_see_tv, !bean.isIsWantSee());
        }
        // 标题
        if(TextUtils.isEmpty(bean.getTitle())) {
            holder.setGone(R.id.item_movie_long_comment_title_tv, true);
        } else {
            holder.setText(R.id.item_movie_long_comment_title_tv, bean.getTitle());
            holder.setGone(R.id.item_movie_long_comment_title_tv, false);
        }
        // 内容
        String content = "";
        Matcher m = LINE_PATTERN.matcher(bean.getContent());
        content = m.replaceAll("");
        if(content.endsWith(ELLIPSIS)) {
            content = content.substring(0, content.length() - ELLIPSIS.length());
        }
        content = content.replace("&nbsp;", "").replace(" ", "");
        if(TextUtils.isEmpty(content)) {
            holder.setGone(R.id.item_movie_long_comment_content_tv, true);
        } else {
            holder.setText(R.id.item_movie_long_comment_content_tv, content);
            holder.setGone(R.id.item_movie_long_comment_content_tv, false);
        }
        // 回复数
        holder.setText(R.id.item_movie_long_comment_comment_comment_count_tv, MtimeUtils.formatCount(bean.getCommentCount()));
    }

    @Override
    public void onViewAttachedToWindow(BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        if(mListener != null && CollectionUtils.isNotEmpty(getData())) {
            int position = holder.getAdapterPosition();
            MovieHotLongCommentBean bean = getData().get(position);
            mListener.onShow(holder.getAdapterPosition(), bean);
        }
    }

    public interface OnMovieLongCommentAdapterListener {
        void onShow(int position, MovieHotLongCommentBean bean);
    }
}
