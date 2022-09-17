package com.mtime.bussiness.information.adapter.render;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.adapter.render.base.BaseAdapterTypeRender;
import com.mtime.adapter.render.base.MRecyclerViewTypeExtraHolder;
import com.mtime.base.utils.MTimeUtils;
import com.mtime.bussiness.information.adapter.NewsCommentListAdapter;
import com.mtime.bussiness.information.bean.NewsCommentItemBean;
import com.mtime.bussiness.information.bean.NewsCommentItemReplyBean;
import com.mtime.common.utils.DateUtil;
import com.mtime.util.ImageURLManager;

import java.util.List;

/**
 * 新闻评论列表页item
 * Created by yinguanping on 16/7/5.
 */
public class NewsCommentItemRender implements BaseAdapterTypeRender<MRecyclerViewTypeExtraHolder> {
    private final BaseActivity context;
    private final NewsCommentListAdapter adapter;
    private final MRecyclerViewTypeExtraHolder holder;

    public NewsCommentItemRender(BaseActivity context, NewsCommentListAdapter adapter) {
        this.context = context;
        this.adapter = adapter;
        View view = LayoutInflater.from(context).inflate(R.layout.find_news_comment_list_item, null);
        holder = new MRecyclerViewTypeExtraHolder(view);
    }

    @Override
    public MRecyclerViewTypeExtraHolder getReusableComponent() {
        return holder;
    }

    @Override
    public void fitEvents() {
        holder.obtainView(R.id.comment_reply_num).setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {
                final NewsCommentItemBean newsCommentItemBean = (NewsCommentItemBean) v.getTag();
                String commentId = String.valueOf(newsCommentItemBean.getId());
                adapter.getOnRecyclerViewListener().onClickReply(commentId);
            }
        });
    }

    @Override
    public void fitDatas(int position) {
        long currentTime = MTimeUtils.getLastDiffServerTime() / 1000 + 3600 * 8;
        if (currentTime == 0) {
            currentTime = System.currentTimeMillis() / 1000 + 3600 * 8;
        }
        NewsCommentItemBean listBean = adapter.getList().get(position);
        ImageView imgPhoto = holder.obtainView(R.id.comment_photo, ImageView.class);
        ImageView arrow_up = holder.obtainView(R.id.arrow_up, ImageView.class);
        TextView name = holder.obtainView(R.id.comment_name, TextView.class);
        TextView info = holder.obtainView(R.id.comment_content, TextView.class);
        TextView time = holder.obtainView(R.id.comment_time, TextView.class);
        TextView replyNum = holder.obtainView(R.id.comment_reply_num, TextView.class);
        LinearLayout layoutReply = holder.obtainView(R.id.layout_reply, LinearLayout.class);
        View arrow_line = holder.obtainView(R.id.arrow_line);
        View divider = holder.obtainView(R.id.divider);

        name.setText(listBean.getNickname());
        info.setText(listBean.getContent());
        replyNum.setTag(listBean);

        time.setText(DateUtil.getShowSdf12(currentTime, listBean.getTimestamp()));
        if (listBean.getReplies().size() > 0) {
            replyNum.setText(String.valueOf(listBean.getReplies().size()));
            List<NewsCommentItemReplyBean> replies = listBean.getReplies();
            layoutReply.removeAllViews();
            for (int i = 0; i < replies.size(); i++) {
                View childView = View.inflate(context, R.layout.find_news_comment_list_reply_item, null);
                final ImageView childImgPhoto = childView.findViewById(R.id.comment_photo);
                TextView childInfo = childView.findViewById(R.id.comment_content);
                TextView childName = childView.findViewById(R.id.comment_name);
                TextView childTime = childView.findViewById(R.id.comment_time);
                View dividerLine = childView.findViewById(R.id.divider_line);
                View list_divider = childView.findViewById(R.id.list_divider);
                childName.setText(replies.get(i).getNickname());
                childInfo.setText(replies.get(i).getContent());
                childTime.setText(DateUtil.getShowSdf12(currentTime, replies.get(i).getTimestamp()));
                if (i == (replies.size() - 1)) {
                    dividerLine.setVisibility(View.GONE);
                    list_divider.setVisibility(View.VISIBLE);
                } else {
                    dividerLine.setVisibility(View.VISIBLE);
                    list_divider.setVisibility(View.GONE);
                }

                context.volleyImageLoader.displayImage(listBean.getReplies().get(i).getUserImage(), childImgPhoto, R.drawable.profile_default_head_h90, R.drawable.profile_default_head_h90, ImageURLManager.ImageStyle.THUMB, null);

                layoutReply.addView(childView);
            }

            layoutReply.setVisibility(View.VISIBLE);
            arrow_up.setVisibility(View.VISIBLE);
            arrow_line.setVisibility(View.VISIBLE);
            divider.setVisibility(View.GONE);
        } else {
            layoutReply.setVisibility(View.GONE);
            arrow_up.setVisibility(View.GONE);
            arrow_line.setVisibility(View.GONE);
            divider.setVisibility(View.VISIBLE);
            replyNum.setText("回复");
        }

        context.volleyImageLoader.displayImage(listBean.getUserImage(), imgPhoto, R.drawable.profile_default_head_h90, R.drawable.profile_default_head_h90, ImageURLManager.ImageStyle.THUMB, null);
    }
}
