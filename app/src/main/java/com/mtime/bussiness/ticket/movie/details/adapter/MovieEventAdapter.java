package com.mtime.bussiness.ticket.movie.details.adapter;

import androidx.annotation.Nullable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mtime.R;

import java.util.List;

/**
 * @author vivian.wei
 * @date 2019/5/23
 * @desc 影片幕后揭秘Adapter
 */
public class MovieEventAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    private static final String NUM = "%d.";

    public MovieEventAdapter(@Nullable List<String> data) {
        super(R.layout.item_movie_event, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, String bean) {
        holder.setText(R.id.item_movie_event_num_tv, String.format(NUM, holder.getAdapterPosition() + 1));
        TextView contentTv = holder.getView(R.id.item_movie_event_content_tv);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            contentTv.setText(Html.fromHtml(bean, Html.FROM_HTML_MODE_COMPACT));
        } else {
            contentTv.setText(Html.fromHtml(bean));
        }
        contentTv.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
