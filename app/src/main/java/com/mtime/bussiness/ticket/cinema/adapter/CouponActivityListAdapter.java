package com.mtime.bussiness.ticket.cinema.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.base.statistic.StatisticConstant;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.bussiness.ticket.cinema.bean.CinemaMoviesCouponActivityItem;
import com.mtime.constant.Constants;
import com.mtime.frame.BaseFrameUIActivity;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.statistic.large.h5.StatisticH5;
import com.mtime.statistic.large.ticket.StatisticTicket;
import com.mtime.util.JumpUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 影院排片-优惠活动列表adapter
 */
public class CouponActivityListAdapter extends BaseAdapter {

    private final BaseFrameUIActivity context;
    private final List<CinemaMoviesCouponActivityItem> data;

    public CouponActivityListAdapter(BaseFrameUIActivity context, List<CinemaMoviesCouponActivityItem> beans) {
        super();
        this.context = context;
        this.data = beans;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.couponactivity_listitem, null);
            holder.desc = convertView.findViewById(R.id.couponactivity_listitem_desc);
            holder.tag = convertView.findViewById(R.id.couponactivity_listitem_tag);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (data.get(position).getDsActivityFlag() == Constants.DIRECT_SALES_ACTIVITY_FLAG) {
            holder.tag.setText(data.get(position).getTag());
            holder.desc.setText(data.get(position).getDesc());
        } else {
            holder.tag.setText(context.getString(R.string.movie_buybtn_coupons));
            holder.desc.setText(data.get(position).getDesc());
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String descDetail = data.get(position).getDescDetail();
                String url = data.get(position).getUrl();
                if (TextUtils.isEmpty(url) || "".equals(url.trim())) {
                    Map<String, String> businessParam = new HashMap<>();
                    businessParam.put(StatisticConstant.URL, "");
                    StatisticPageBean bean = context.assemble(StatisticTicket.TICKET_EVENT, null, null, null, null, null, businessParam);
                    StatisticManager.getInstance().submit(bean);
                    //活动说明页
                    JumpUtil.startActivityInstructionActivity(context, descDetail);
                } else {
                    Map<String, String> businessParam = new HashMap<>();
                    businessParam.put(StatisticConstant.URL, url);
                    StatisticPageBean bean = context.assemble(StatisticTicket.TICKET_EVENT, null, null, null, null, null, businessParam);
                    StatisticManager.getInstance().submit(bean);
//                    JumpUtil.startAdvRecommendActivity(context, bean.toString(), data.get(position).getUrl(),
//                            true, true, "", "", -1);
                    JumpUtil.startCommonWebActivity(context, data.get(position).getUrl(), StatisticH5.PN_H5, null,
                            true, true, true, false, bean.toString());
                }
            }
        });
        return convertView;
    }

    public static final class ViewHolder {
        public TextView desc;
        public TextView tag;
    }

}
