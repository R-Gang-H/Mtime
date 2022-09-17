package com.mtime.bussiness.mine.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kotlin.android.user.UserManager;
import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.bussiness.mine.bean.FeedbackListBean;
import com.mtime.common.utils.DateUtil;
import com.mtime.common.utils.Utils;
import com.mtime.util.ImageURLManager;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class FeedBackListAdapter extends BaseAdapter {
    private final BaseActivity           context;
    private final List<FeedbackListBean> feedbackListBeans;

    public FeedBackListAdapter(BaseActivity context, List<FeedbackListBean> feedbackListBeans) {
        this.context = context;
        this.feedbackListBeans = feedbackListBeans;
    }
    
    public int getCount() {
        return feedbackListBeans.size();
    }
    
    public Object getItem(int position) {
        return position;
    }
    
    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        final Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = context.getLayoutInflater().inflate(R.layout.feedbacklist_item, null);
            holder.userPhoto = convertView.findViewById(R.id.user_photo);
            holder.userCotent = convertView.findViewById(R.id.text_content);
            holder.userNickName = convertView.findViewById(R.id.user_nick_name);
            holder.creatTime = convertView.findViewById(R.id.content_time);
            holder.replayContent = convertView.findViewById(R.id.replay_content);
            holder.replayTime = convertView.findViewById(R.id.replay_time);
            holder.replayLin = convertView.findViewById(R.id.replay_lin);
            convertView.setTag(holder);
            
        }
        else {
            holder = (Holder) convertView.getTag();
        }
        if (null != UserManager.Companion.getInstance().getUser()) {
            holder.userNickName.setText(UserManager.Companion.getInstance().getNickname());
        } else {
            holder.userNickName.setText("--");
        }
        
        if (feedbackListBeans.get(position).getReplyContent() != null
                && !feedbackListBeans.get(position).getReplyContent().equals("")) {
            
            holder.replayLin.setVisibility(View.VISIBLE);
            holder.replayContent.setText(feedbackListBeans.get(position).getReplyContent());
            String value;
            long replyTime = (System.currentTimeMillis() / 1000 - feedbackListBeans.get(position).getReplyTime()) / 60;
            
            if (replyTime < 0) {
                Calendar now = Calendar.getInstance();
                TimeZone timeZone = now.getTimeZone();
                long totalMilliseconds = System.currentTimeMillis() + timeZone.getRawOffset();
                long totalSeconds = totalMilliseconds / 1000;
                replyTime = (totalSeconds - feedbackListBeans.get(position).getReplyTime()) / 60;
            }
            if (replyTime < 60) {
                value = String.format("%d分钟前", replyTime);
            }
            else if (replyTime < (24 * 60)) {
                value = String.format("%d小时前", replyTime / 60);
            }
            else {
                value = DateUtil.getLongToDate(DateUtil.sdf6, feedbackListBeans.get(position).getReplyTime());
            }
            holder.replayTime.setText(value);
        }
        else {
            holder.replayLin.setVisibility(View.GONE);
        }
        holder.userCotent.setText(feedbackListBeans.get(position).getContent());
        String value;
        long replyTime = (System.currentTimeMillis() / 1000 - feedbackListBeans.get(position).getCreateTime()) / 60;
        
        if (replyTime < 0) {
            Calendar now = Calendar.getInstance();
            TimeZone timeZone = now.getTimeZone();
            long totalMilliseconds = System.currentTimeMillis() + timeZone.getRawOffset();
            long totalSeconds = totalMilliseconds / 1000;
            replyTime = (totalSeconds - feedbackListBeans.get(position).getCreateTime()) / 60;
        }
        if (replyTime < 60) {
            value = String.format("%d分钟前", replyTime);
        }
        else if (replyTime < (24 * 60)) {
            value = String.format("%d小时前", replyTime / 60);
        }
        else {
            value = DateUtil.getLongToDate(DateUtil.sdf6, feedbackListBeans.get(position).getCreateTime());
        }
        holder.creatTime.setText(value);
        context.volleyImageLoader.displayImage(UserManager.Companion.getInstance().getUserAvatar(), holder.userPhoto, R.drawable.profile_default_head_h90, R.drawable.profile_default_head_h90,
                Utils.dip2px(this.context, 45), Utils.dip2px(this.context, 45), ImageURLManager.FIX_WIDTH_TRIM_HEIGHT, null);
        return convertView;
    }
    
    
    class Holder {
        ImageView    userPhoto;
        TextView     userNickName;
        TextView     userCotent;
        TextView     creatTime;
        TextView     replayContent;
        TextView     replayTime;
        LinearLayout replayLin;
    }
    
}
