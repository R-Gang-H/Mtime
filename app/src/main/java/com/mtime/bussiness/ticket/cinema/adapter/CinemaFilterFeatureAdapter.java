package com.mtime.bussiness.ticket.cinema.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.bussiness.ticket.cinema.bean.CinemaFeatureBean;
import com.mtime.bussiness.ticket.cinema.widget.CinemaFilterAdapterListener;
import com.mtime.bussiness.ticket.cinema.widget.CinemaFilterAdapterType;

import java.util.List;

/**
 * Created by vivian.wei on 2017/10/24.
 * Tab购票_影院_排序&筛选组件_影厅特效层（2017年10月新版）
 */

public class CinemaFilterFeatureAdapter extends BaseAdapter {

    private final Context mContext;
    private List<CinemaFeatureBean> mList;
    private int mSelectIndex = 0;
    private CinemaFilterAdapterListener mListener;

    public CinemaFilterFeatureAdapter(final Context context, final List<CinemaFeatureBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    public void setListener(CinemaFilterAdapterListener listener) {
        mListener = listener;
    }

    public void setList(final List<CinemaFeatureBean> list) {
        this.mList = list;
    }

    public void setSelectIndex(int selectIndex) {
        mSelectIndex = selectIndex;
    }

    public int getCount() {
        return null != mList && mList.size() > 0 ? mList.size() : 0;
    }

    public Object getItem(final int arg0) {
        if(null == mList || mList.size() == 0) {
            return null;
        }
        return mList.get(arg0);
    }

    public long getItemId(final int arg0) {
        return arg0;
    }

    public View getView(final int arg0, View arg1, final ViewGroup arg2) {
        final CinemaFeatureBean bean = mList.get(arg0);
        Holder holder;
        if (arg1 == null) {
            holder = new Holder();
            arg1 = LayoutInflater.from(mContext).inflate(R.layout.adapter_cinema_list_filter_feature_item, null);
            holder.nameTv = arg1.findViewById(R.id.adapter_cinema_list_filter_feature_item_name_tv);
            arg1.setTag(holder);

            holder.nameTv.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    mSelectIndex = arg0;
                    notifyDataSetChanged();
                    if (null != mListener) {
                        mListener.onEvent(CinemaFilterAdapterType.TYPE_FEATURE, arg0);
                    }
                }
            });

        } else {
            holder = (Holder) arg1.getTag();
        }

        holder.nameTv.setText(bean.getName());
        if(mSelectIndex == arg0) {
            holder.nameTv.setTextColor(ContextCompat.getColor(mContext, R.color.color_f97d3f));
            holder.nameTv.setBackground(ContextCompat.getDrawable(mContext, R.drawable.shape_cinema_list_filter_feature_select_bg));
        } else {
            holder.nameTv.setTextColor(ContextCompat.getColor(mContext, R.color.color_777777));
            holder.nameTv.setBackground(ContextCompat.getDrawable(mContext, R.drawable.shape_cinema_list_filter_feature_bg));
        }

        if (!bean.isSupport()) {
            holder.nameTv.setTextColor(ContextCompat.getColor(mContext, R.color.color_bbbbbb));
            holder.nameTv.setBackground(ContextCompat.getDrawable(mContext, R.drawable.shape_cinema_list_filter_feature_unclick_bg));
            holder.nameTv.setClickable(false);
        }
        return arg1;
    }

    class Holder {
        TextView nameTv;
    }
}
