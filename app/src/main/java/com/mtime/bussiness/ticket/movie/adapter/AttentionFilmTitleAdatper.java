package com.mtime.bussiness.ticket.movie.adapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.statistic.baidu.BaiduConstants;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.statistic.large.ticket.StatisticTicket;

import java.util.ArrayList;

/**
 * 即将上映列表最受关注标签
 * Created by lsmtime on 17/4/5.
 */

public class AttentionFilmTitleAdatper extends RecyclerView.Adapter<AttentionFilmTitleAdatper.ViewHolder> {
    private final LayoutInflater inflater;
    private final AppCompatActivity context;
    private final ArrayList<String> titleList = new ArrayList<>();
    private int selectPosition;//选中的标题的位置
    private OnItemSelectListener onItemSelectListener;

    public AttentionFilmTitleAdatper(AppCompatActivity context, ArrayList<String> titleList) {
        this.context = context;
        if (titleList != null) {
            this.titleList.addAll(titleList);
        }
        inflater = LayoutInflater.from(context);
    }

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.adapter_attention_title, parent, false));
    }

    public void clear() {
        this.titleList.clear();
        this.selectPosition = 0;
        this.notifyDataSetChanged();
    }

    public void addAll(ArrayList<String> list) {
        if (list != null && list.size() > 0) {
            this.titleList.addAll(list);
            this.notifyDataSetChanged();
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String title = titleList.get(position);
        if (!TextUtils.isEmpty(title)) {
            holder.titleTv.setText(title);
        } else {
            holder.titleTv.setText("");
        }
        if (position == selectPosition) {
            holder.layout.setBackgroundResource(R.drawable.bg_f97d3f_conner);
            holder.titleTv.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else {
            holder.layout.setBackgroundResource(R.drawable.bg_f6f6f6_conner);
            holder.titleTv.setTextColor(ContextCompat.getColor(context, R.color.color_999999));
        }

        holder.layout.setTag(R.string.app_name, title);
        holder.layout.setTag(R.string.appbar_scrolling_view_behavior, position);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = (String) v.getTag(R.string.app_name);
                int position = (int) v.getTag(R.string.appbar_scrolling_view_behavior);
                if (selectPosition == position) {
                    return;
                }
                selectPosition = position;
                logX(selectPosition);//埋点统计
                if (onItemSelectListener != null) {
                    onItemSelectListener.onSelect(selectPosition, tag);
                }
                notifyDataSetChanged();
            }
        });
    }

    /**
     * 加入埋点
     *
     * @param position
     */
    private void logX(int position) {
        String type = "";
        if (position == 0) {
            type = StatisticTicket.TICKET_HOT;
        } else {
            type = StatisticTicket.TICKET_RECOMMEND;
        }
//        context.setPageLabel(StatisticTicket.PN_FUTURE_SCHEDULE);
//        StatisticPageBean bean = context.assemble(type, null, "tag", null, null, null, null);
//        StatisticManager.getInstance().submit(bean);
    }

    @Override
    public int getItemCount() {
        return this.titleList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final View layout;
        private final TextView titleTv;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            titleTv = itemView.findViewById(R.id.item_title_tv);
        }
    }

    public interface OnItemSelectListener {
        void onSelect(int position, String title);
    }
}
