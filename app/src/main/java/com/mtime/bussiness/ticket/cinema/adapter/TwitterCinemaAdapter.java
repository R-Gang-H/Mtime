package com.mtime.bussiness.ticket.cinema.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aspsine.irecyclerview.IViewHolder;
import com.mtime.R;
import com.mtime.bussiness.ticket.movie.adapter.TwitterAdapter.ITwitterAdapterListener;
import com.mtime.bussiness.ticket.cinema.bean.TopicReply;
import com.mtime.common.utils.DateUtil;
import com.mtime.bussiness.ticket.cinema.activity.TwitterCinemaActivity;
import com.mtime.util.ImageURLManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class TwitterCinemaAdapter extends RecyclerView.Adapter<TwitterCinemaAdapter.ViewHolder> {
    
    private final TwitterCinemaActivity   context;
    private List<TopicReply>        dataList;
    private final ITwitterAdapterListener listener;
    
    public TwitterCinemaAdapter(TwitterCinemaActivity context, List<TopicReply> data, final ITwitterAdapterListener listener) {
        this.context = context;
        this.dataList = data;
        if (null == this.dataList) {
            this.dataList = new ArrayList<TopicReply>();
        }

        this.listener = listener;
    }
    
    public int getCount() {
        return dataList.size();
    }
    
    public Object getItem(int position) {
        return dataList.get(position);
    }

    public int getRealCount() {
        if (1 == dataList.size() && -1 == dataList.get(0).getReplyId()) {
            return 0;
        }

        return dataList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.v2_twitter_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (0 == position && this.dataList.get(0).getReplyId() == -1) {
            holder.itemView.findViewById(R.id.twitter_layout).setVisibility(View.GONE);
            holder.itemView.findViewById(R.id.no_info_view).setVisibility(View.VISIBLE);
            return;
        }
        else {
            holder.itemView.findViewById(R.id.twitter_layout).setVisibility(View.VISIBLE);
            holder.itemView.findViewById(R.id.no_info_view).setVisibility(View.GONE);
        }

        if (0 == position) {
            holder.line.setVisibility(View.INVISIBLE);
        }
        else {
            holder.line.setVisibility(View.VISIBLE);
        }
        final TopicReply reply = dataList.get(position);

        context.volleyImageLoader.displayImage(reply.getUserImage(), holder.userImageView, ImageURLManager.ImageStyle.THUMB, null);

        holder.name.setText(reply.getNickname());
        holder.content.setText(reply.getContent());

        String value;
        long sendTime = reply.getEnterTime() - 8*60*60L;
        if (0 == position && 0 == reply.getReplyId()) {
            sendTime += 8*60*60L;
        }
        long replyTime = (System.currentTimeMillis() / 1000 - sendTime) / 60;

        if (replyTime < 0) {
            Calendar now = Calendar.getInstance();
            TimeZone timeZone = now.getTimeZone();
            long totalMilliseconds = System.currentTimeMillis() + timeZone.getRawOffset();
            long totalSeconds = totalMilliseconds / 1000;
            replyTime = (totalSeconds - reply.getEnterTime()) / 60;
            if (replyTime < 1) {
                replyTime = 1;
            }
        }
        if (replyTime < 60) {
            value = String.format("%d分钟前", replyTime);
        }
        else if (replyTime < (24 * 60)) {
            value = String.format("%d小时前", replyTime / 60);
        }
        else {
            value = DateUtil.getLongToDate(DateUtil.sdf5, reply.getEnterTime());
        }
        holder.time.setText(value);

        holder.replyBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (null != listener) {
                    listener.onEvent(reply);
                }
            }
        });
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends IViewHolder {
        ImageView userImageView;
        TextView  name;
        TextView  content;
        TextView  time;
        View  replyBtn;
        View line;


        public ViewHolder(View itemView) {
            super(itemView);
            userImageView = itemView.findViewById(R.id.comment_photo);
            name = itemView.findViewById(R.id.comment_name);
            content = itemView.findViewById(R.id.twitter_head_content);
            time = itemView.findViewById(R.id.twitter_head_time);

            replyBtn = itemView.findViewById(R.id.reply_one);
            replyBtn.setVisibility(View.VISIBLE);
            line = itemView.findViewById(R.id.gray_line);
            // hide all
            itemView.findViewById(R.id.reply_two).setVisibility(View.GONE);
            itemView.findViewById(R.id.twitter_head_comment).setVisibility(View.INVISIBLE);
            itemView.findViewById(R.id.twitter_head_score).setVisibility(View.INVISIBLE);
        }
    }
    
}
