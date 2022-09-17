package com.mtime.bussiness.ticket.movie.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aspsine.irecyclerview.IViewHolder;
import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.bussiness.ticket.bean.TweetReply;
import com.mtime.common.utils.DateUtil;
import com.mtime.constant.FrameConstant;
import com.mtime.util.ImageURLManager;
import com.mtime.util.JumpUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class TwitterAdapter extends RecyclerView.Adapter<TwitterAdapter.ViewHolder> {
    private final int NODATAID = -1000;

    public interface ITwitterAdapterListener {
        void onEvent(final Object reply);
    }
    
    private final BaseActivity            context;
    private final ITwitterAdapterListener listener;
    private final List<TweetReply>        dataList = new ArrayList<TweetReply>();
    
    public TwitterAdapter(BaseActivity context, List<TweetReply> data, final ITwitterAdapterListener listener) {
        this.context = context;
        if (null != data) {
            this.dataList.addAll(data);
        }
        this.listener = listener;
    }
    
    public int getCount() {
        return dataList.size();
    }
    
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.v2_twitter_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final TweetReply reply = dataList.get(position);
        if (!TextUtils.isEmpty(reply.getCeimg())) {
            holder.commentImg.setVisibility(View.VISIBLE);
//            holder.commentImg.setTag(reply.getCeimg());
            context.volleyImageLoader.displayImage(reply.getCeimg(), holder.commentImg, R.drawable.default_image, R.drawable.default_image, 460, 270, ImageURLManager.SCALE_TO_FIT, null);
        }else{
            holder.commentImg.setVisibility(View.GONE);
        }

        if (reply.getReponseId() == NODATAID) {
            holder.itemView.findViewById(R.id.twitter_layout_data).setVisibility(View.GONE);
            holder.itemView.findViewById(R.id.no_info_view).setVisibility(View.VISIBLE);
            return;
        }
        else {
            holder.itemView.findViewById(R.id.twitter_layout_data).setVisibility(View.VISIBLE);
            holder.itemView.findViewById(R.id.no_info_view).setVisibility(View.GONE);
        }
        context.volleyImageLoader.displayImage(reply.getUserImage(), holder.userImageView, R.drawable.profile_default_head_h90, R.drawable.profile_default_head_h90, ImageURLManager.ImageStyle.THUMB, null);

        holder.name.setText(reply.getNickname());
        holder.content.setText(reply.getContent());

        String value;
        long replyTime = (System.currentTimeMillis() / 1000 - reply.getStampTime()) / 60;

        if (replyTime < 0) {
            Calendar now = Calendar.getInstance();
            TimeZone timeZone = now.getTimeZone();
            long totalMilliseconds = System.currentTimeMillis() + timeZone.getRawOffset();
            long totalSeconds = totalMilliseconds / 1000;
            replyTime = (totalSeconds - reply.getStampTime()) / 60;
        }
        if (replyTime < 0) {
            TimeZone oldZone = TimeZone.getTimeZone("Asia/Shanghai");
            TimeZone newZone = TimeZone.getTimeZone("GMT");
            int timeOffset = oldZone.getRawOffset() - newZone.getRawOffset();
            long totalMilliseconds = System.currentTimeMillis() + timeOffset;
            long totalSeconds = totalMilliseconds / 1000;
            replyTime = (totalSeconds - reply.getStampTime()) / 60;
        }
        if (replyTime < 0) {
            value = DateUtil.getLongToDate(DateUtil.sdf6, reply.getStampTime());
        }
        else if (replyTime < 60) {
            value = String.format("%d分钟前", replyTime);
        }
        else if (replyTime < (24 * 60)) {
            value = String.format("%d小时前", replyTime / 60);
        }
        else {
            value = DateUtil.getLongToDate(DateUtil.sdf6, reply.getStampTime());
        }
        holder.time.setText(value);
        holder.itemView.findViewById(R.id.reply_one).setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
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

    public void setDatas(List<TweetReply> datas) {
        if (this.dataList.size() == 1 && this.dataList.get(0).getReponseId() == NODATAID) {
            this.dataList.clear();
        }
        this.dataList.addAll(datas);
        if (this.dataList.size() == 0) {
            TweetReply reply = new TweetReply();
            reply.setReponseId(NODATAID);
            dataList.add(reply);
        }
        this.notifyDataSetChanged();
    }
    
    public void cleanDatas() {
        this.dataList.clear();
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends IViewHolder {
        ImageView userImageView;
        TextView  name;
        TextView  content;
        TextView  time;
        ImageView commentImg;

        public ViewHolder(View itemView) {
            super(itemView);
            userImageView = itemView.findViewById(R.id.comment_photo);
            name = itemView.findViewById(R.id.comment_name);
            content = itemView.findViewById(R.id.twitter_head_content);
            time = itemView.findViewById(R.id.twitter_head_time);
//            line = (View) itemView.findViewById(R.id.gray_line);
            commentImg = itemView.findViewById(R.id.comment_img);
            commentImg.setVisibility(View.GONE);
            commentImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String ceimg = (String) view.getTag();
                    if (!TextUtils.isEmpty(ceimg)) {
//                        Intent intent = new Intent();
//                        intent.putExtra(CommentImageDetailActivity.KEY_IMAGE_PATH, ceimg);
//                        context.startActivity(CommentImageDetailActivity.class, intent);
                        JumpUtil.startCommentImageDetailActivity(context,ceimg);
                    }
                }
            });
            itemView.findViewById(R.id.reply_one).setVisibility(View.VISIBLE);
            itemView.findViewById(R.id.twitter_layout_data).setVisibility(View.VISIBLE);
            itemView.findViewById(R.id.no_info_view).setVisibility(View.GONE);
            itemView.findViewById(R.id.no_info_view).setMinimumHeight(FrameConstant.SCREEN_HEIGHT >> 1);
        }
    }
}
