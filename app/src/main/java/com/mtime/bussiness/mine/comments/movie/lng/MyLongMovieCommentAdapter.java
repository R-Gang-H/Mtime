package com.mtime.bussiness.mine.comments.movie.lng;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.bussiness.ticket.movie.comment.bean.MineLongMovieCommentBean;
import com.mtime.common.utils.DateUtil;
import com.mtime.mtmovie.widgets.ScoreView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-10-10
 */
class MyLongMovieCommentAdapter extends RecyclerView.Adapter<MyLongMovieCommentAdapter.CommentHolder> {

    private final Context mContext;
    private final LayoutInflater mInflater;
    private final List<MineLongMovieCommentBean> comments;
    private final int currentYear = DateUtil.getYearByTimeStamp(System.currentTimeMillis() / 1000);
    private final int mDp65;
    private final int mDp97;

    private final ClickCallback mCallback;

    interface ClickCallback {
        void gotoCommentDetail(long commentId);

        void gotoMovieDetail(long relatedId);

        void onMoreClick(int position, long commentId);
    }

    MyLongMovieCommentAdapter(Context context, List<MineLongMovieCommentBean> comments, ClickCallback callback) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        this.comments = comments;

        mCallback = callback;
        mDp65 = MScreenUtils.dp2px(65);
        mDp97 = MScreenUtils.dp2px(97);
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup p, int i) {
        View v = mInflater.inflate(R.layout.item_my_long_movie_comment_layout, p, false);
        return new CommentHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
        holder.mCurrentPosition = position;
        // movie_details_no_score
        MineLongMovieCommentBean bean = comments.get(position);

        ImageHelper.with(mContext, ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                .override(mDp65, mDp97)
                .placeholder(R.drawable.default_image)
                .error(R.drawable.default_image)
                .load(bean.relateImg)
                .view(holder.mPosterIv)
                .showload();

        holder.mCommentTitleTv.setText(bean.title);
        String body = bean.body;
        if (!TextUtils.isEmpty(body)) {
            body = body.replace("\n", "");
        }
        holder.mCommentTv.setText(body);
        holder.mMovieNameTv.setText(bean.relatedName);

        if (bean.rating > 0) {
            float num = (float) (Math.round(bean.rating * 10)) / 10;
            holder.mScorePrefixTv.setText("??????");
            holder.mScoreView.setVisibility(View.VISIBLE);
            if (num == 10) {
                holder.mScoreView.setText("10");
            } else if (num > 10 || num <= 0) {
                holder.mScorePrefixTv.setText(R.string.movie_details_no_score);
                holder.mScoreView.setVisibility(View.GONE);
            } else {
                holder.mScoreView.setScore(String.valueOf(num));
            }
        } else {
            holder.mScorePrefixTv.setText(R.string.movie_details_no_score);
            holder.mScoreView.setVisibility(View.GONE);
        }

        int year = DateUtil.getYearByTimeStamp(bean.time);
        int month = DateUtil.getMonthByTimeStamp(bean.time);

        holder.mMonthTv.setVisibility(View.VISIBLE);
        holder.mDivider.setVisibility(View.GONE);

        if (position == 0) {
            // ????????????????????????????????????XX??????????????????XXXX???XX???
            if (currentYear == year) {
                holder.mMonthTv.setText(String.format(mContext.getString(R.string.my_comment_and_reply_month), month));
            } else {
                holder.mMonthTv.setText(String.format(mContext.getString(R.string.my_comment_and_reply_year), year, month));
            }
            // ???????????????????????????
        } else if (year == DateUtil.getYearByTimeStamp(comments.get(position - 1).time)
                && month == DateUtil.getMonthByTimeStamp(comments.get(position - 1).time)) {
            holder.mMonthTv.setVisibility(View.GONE);
            holder.mDivider.setVisibility(View.VISIBLE);
        }
        // ?????????????????????????????????
        else if (year == currentYear) {
            holder.mMonthTv.setText(String.format(mContext.getString(R.string.my_comment_and_reply_month), month));
        } else {
            holder.mMonthTv.setText(String.format(mContext.getString(R.string.my_comment_and_reply_year), year, month));
        }
    }

    @Override
    public int getItemCount() {
        return comments == null ? 0 : comments.size();
    }

    // ?????????item????????????title???????????????
    boolean isHeader(int position) {
        int year = DateUtil.getYearByTimeStamp(comments.get(position).time);
        int month = DateUtil.getMonthByTimeStamp(comments.get(position).time);
        int lastMonth = DateUtil.getMonthByTimeStamp(comments.get(position - 1).time);
        int lastYear = DateUtil.getYearByTimeStamp(comments.get(position - 1).time);
        if (position >= getItemCount()) {
            return false;
        } else return year != lastYear || month != lastMonth;
    }

    class CommentHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private int mCurrentPosition = -1;

        @BindView(R.id.month_tv)
        TextView mMonthTv;
        @BindView(R.id.divider)
        View mDivider;
        @BindView(R.id.comment_img)
        ImageView mPosterIv;
        @BindView(R.id.movie_name)
        TextView mMovieNameTv;
        @BindView(R.id.score_view_sv)
        ScoreView mScoreView;
        @BindView(R.id.my_comment_score_tv)
        TextView mScorePrefixTv;
        @BindView(R.id.comment_title_tv)
        TextView mCommentTitleTv;
        @BindView(R.id.comment)
        TextView mCommentTv;

        CommentHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @OnClick({
                R.id.comment_img,
                R.id.movie_name,
        })
        void jumpMovieDetail() {
            int positon = mCurrentPosition;
            if (positon < 0 || positon >= getItemCount()) {
                return;
            }
            MineLongMovieCommentBean bean = comments.get(positon);
            if (mCallback != null) {
                mCallback.gotoMovieDetail(bean.relatedId);
            }
        }

        @OnClick(R.id.item_more_btn)
        void onMoreClick() {
            int positon = mCurrentPosition;
            if (positon < 0 || positon >= getItemCount()) {
                return;
            }
            MineLongMovieCommentBean bean = comments.get(positon);
            if (mCallback != null) {
                mCallback.onMoreClick(positon, bean.commentId);
            }
        }

        @Override
        public void onClick(View v) {
            int positon = mCurrentPosition;
            if (positon < 0 || positon >= getItemCount()) {
                return;
            }
            MineLongMovieCommentBean bean = comments.get(positon);
            if (mCallback != null) {
                mCallback.gotoCommentDetail(bean.commentId);
            }
        }
    }
}
