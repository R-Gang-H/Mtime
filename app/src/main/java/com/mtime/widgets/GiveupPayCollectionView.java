package com.mtime.widgets;

import androidx.core.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.bussiness.common.FeedBackActivity;
import com.mtime.bussiness.ticket.movie.activity.OrderPayActivity;
import com.mtime.common.utils.LogWriter;
import com.mtime.statistic.baidu.BaiduConstants;
import com.mtime.util.ToolsUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LEE on 15-8-11.
 * 放弃填写订单理由弹窗
 */
public class GiveupPayCollectionView {
    private class CollectionAdapter extends BaseAdapter {

        class ViewHolder {
            TextView content;
        }

        public CollectionAdapter() {
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (null == convertView) {
                holder = new ViewHolder();

                convertView = context.getLayoutInflater().inflate(R.layout.giveup_pay_list_item, null);
                holder.content = convertView.findViewById(R.id.collection_content);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (position < datas.size() - 1) {
                holder.content.setTextColor(ContextCompat.getColor(context,R.color.color_555555));
            } else {
                holder.content.setTextColor(ContextCompat.getColor(context,R.color.color_0075C4));
            }

            holder.content.setText(datas.get(position));

            return convertView;
        }
    }
    private final BaseActivity context;
    private final View root;
    // 统计事件的id
    private final String eventId;
    private final List<String> datas = new ArrayList<>();
    private IGiveupPayCollectionListener listener;

    public GiveupPayCollectionView(final BaseActivity context, final View root, final String eventid, final String label, final List<String> data, final Runnable runnable) {
        this.context = context;
        this.root = root;
        this.eventId = eventid;
        this.datas.addAll(data);
        String key;
        if (BaiduConstants.BAIDU_EVENTID_GIVEUP_WRITE_TICKET_ORDER.equalsIgnoreCase(eventId) || BaiduConstants.BAIDU_EVENTID_GIVEUP_BUY_TICKETS.equalsIgnoreCase(eventId)) {
            key = "ticket_giveup_collection_show";
        } else {
            key = "goods_giveup_collection_show";
        }
        // 仅第一次安装后显示，之后不再显示放弃理由弹窗
        ToolsUtils.saveSpecialFilterGuider(context.getApplicationContext(), key, false);

        this.init(label, runnable);
    }

    private void init(final String label, final Runnable runnable) {
        View holder = this.root.findViewById(R.id.root_collection);
        holder.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showView(false);
                runnable.run();
            }
        });

        TextView labelView = this.root.findViewById(R.id.collection_label);
        labelView.setText(label);

        ListView list = this.root.findViewById(R.id.collection_list);
        // 设置adapter
        CollectionAdapter adapter = new CollectionAdapter();
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // 统计点击，如果是反馈意见，则进入反馈页面
                showView(false);
                LogWriter.e("checkColl", "position: " + position + ", count size:" + datas.size());
                if (!(context instanceof OrderPayActivity) && position == datas.size() - 1) {
                    // 跳转到反馈页面
                    LogWriter.e("checkColl", "start feedbackactivity ");
                    context.startActivity(FeedBackActivity.class);
                }

                if (listener != null) {
                    listener.onItemClick();
                } else {
                    context.finish();
                }

            }
        });
    }

    public void showView(final boolean show) {
        this.root.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public boolean isShowing() {
        return root.getVisibility() == View.VISIBLE;
    }

    public void setListener(IGiveupPayCollectionListener listener){
        this.listener = listener;
    }

    public interface IGiveupPayCollectionListener {
        void onItemClick();
    }
}
