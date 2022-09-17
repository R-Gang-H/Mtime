package com.mtime.bussiness.mine.adapter;

import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aspsine.irecyclerview.IViewHolder;
import com.mtime.R;
import com.mtime.bussiness.mine.bean.CommentAndReplyListBean;
import com.mtime.bussiness.ticket.movie.activity.TwitterActivity;
import com.mtime.common.utils.DateUtil;
import com.mtime.frame.BaseActivity;
import com.mtime.util.ImageURLManager;
import com.mtime.util.JumpUtil;

import java.util.List;

/**
 * Created by ZhangCong on 16/4/6.
 */
public class MyCommentAdapter extends RecyclerView.Adapter<MyCommentAdapter.ViewHolder> {

    private final BaseActivity context;
    private final List<CommentAndReplyListBean> comments;
    // 时间戳得除以1000
    private final int currentYear = DateUtil.getYearByTimeStamp(System.currentTimeMillis() / 1000);

    public MyCommentAdapter(final BaseActivity context, final List<CommentAndReplyListBean> comments) {
        this.context = context;
        this.comments = comments;
    }

    public int getCount() {
        return comments.size();
    }

    public Object getItem(int position) {
        if (position >= getCount())
            return null;
        return comments == null ? null : comments.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_my_comment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final CommentAndReplyListBean bean = comments.get(position);

        context.volleyImageLoader.displayImage(bean.getRelateImg(), holder.imageComment,
                R.drawable.default_image, R.drawable.default_image, ImageURLManager.ImageStyle.LARGE, null);

        //holder.txComment.setText(String.format(context.getResources().getString(R.string.my_comment_body), bean.getBody()));
        holder.txComment.setText(String.format(bean.getBody()));
        if (!TextUtils.isEmpty(bean.getRelatedName())) {
            holder.txMovieName.setText(bean.getRelatedName().replace("<<", "").replace(">>", ""));
        } else {
            holder.txMovieName.setText("");
        }
        holder.txMovieName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JumpUtil.startMovieInfoActivity(context, context.assemble().toString(), String.valueOf(bean.getRelatedId()), 0);
            }
        });

        holder.imageComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JumpUtil.startMovieInfoActivity(context, context.assemble().toString(), String.valueOf(bean.getRelatedId()), 0);
            }
        });

        holder.txComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent();
                intent.putExtra("tweetId", bean.getTweetId());
                context.startActivity(TwitterActivity.class, intent);
            }
        });

        if (bean.getRating() > 0) {
            float num = (float) (Math.round(bean.getRating() * 10)) / 10;
            if (num == 10) {
                holder.txScore.setText("10");
            } else if (num > 10 || num < 0) {
                holder.txScore.setVisibility(View.GONE);
            } else {
                holder.txScore.setText(String.valueOf(num));
            }
            holder.txScore.setVisibility(View.VISIBLE);
        } else {
            holder.txScore.setText("");
            holder.txScore.setVisibility(View.GONE);
        }
        int year = DateUtil.getYearByTimeStamp(bean.getTime());
        int month = DateUtil.getMonthByTimeStamp(bean.getTime());
        int day = DateUtil.getDayByTimeStamp(bean.getTime());

        holder.lyMonth.setVisibility(View.VISIBLE);
        holder.divider.setVisibility(View.GONE);
        //holder.txDay.setVisibility(View.VISIBLE);
        //holder.txDay.setText(String.format(context.getResources().getString(R.string.my_comment_and_reply_day), day));

        if (position == 0) {
            // 如果是今年的评论，仅显示XX月，否则显示XXXX年XX月
            if (currentYear == year) {
                holder.txMonth.setText(String.format(context.getResources().getString(R.string.my_comment_and_reply_month), month));
            } else {
                holder.txMonth.setText(String.format(context.getResources().getString(R.string.my_comment_and_reply_year), year, month));
            }
            // 年月日与上一个相同
        } else /*if (year == DateUtil.getYearByTimeStamp(comments.get(position - 1).getTime()) && month == DateUtil.getMonthByTimeStamp(comments.get(position - 1).getTime())
                && day == DateUtil.getDayByTimeStamp(comments.get(position - 1).getTime())) {
            holder.lyMonth.setVisibility(View.GONE);
            holder.txDay.setVisibility(View.INVISIBLE);
            // 年月相同，但不是同一天
        } else*/ if (year == DateUtil.getYearByTimeStamp(comments.get(position - 1).getTime()) && month == DateUtil.getMonthByTimeStamp(comments.get(position - 1).getTime())) {
            holder.lyMonth.setVisibility(View.GONE);
            holder.divider.setVisibility(View.VISIBLE);
        }
        // 年份相同，但不是同一月
        else if (year == currentYear) {
            holder.txMonth.setText(String.format(context.getResources().getString(R.string.my_comment_and_reply_month), month));
        } else {
            holder.txMonth.setText(String.format(context.getResources().getString(R.string.my_comment_and_reply_year), year, month));
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    // 判断该item上的年月title是否要显示
    public boolean isHeader(int position) {
        int year = DateUtil.getYearByTimeStamp(comments.get(position).getTime());
        int month = DateUtil.getMonthByTimeStamp(comments.get(position).getTime());
        int lastMonth = DateUtil.getMonthByTimeStamp(comments.get(position - 1).getTime());
        int lastYear = DateUtil.getYearByTimeStamp(comments.get(position - 1).getTime());
        if (position >= getCount())
            return false;
        else return year != lastYear || month != lastMonth;
    }

    public class ViewHolder extends IViewHolder {
        LinearLayout lyMonth;
        TextView txMonth;
        TextView txDay;
        ImageView imageComment;
        TextView txComment;
        TextView txMovieName;
        TextView txScore;
        View divider;

        public ViewHolder(View itemView) {
            super(itemView);
            lyMonth = itemView.findViewById(R.id.month);
            txMonth = itemView.findViewById(R.id.month_txt);
            txDay = itemView.findViewById(R.id.day);
            imageComment = itemView.findViewById(R.id.comment_img);
            txComment = itemView.findViewById(R.id.comment);
            txMovieName = itemView.findViewById(R.id.movie_name);
            txScore = itemView.findViewById(R.id.comment_score);
            divider = itemView.findViewById(R.id.divider);
        }
    }
}
