package com.mtime.bussiness.ticket.cinema.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aspsine.irecyclerview.IViewHolder;
import com.mtime.R;
import com.mtime.bussiness.ticket.cinema.bean.DistrictBean;
import com.mtime.bussiness.ticket.cinema.widget.CinemaFilterAdapterListener;
import com.mtime.bussiness.ticket.cinema.widget.CinemaFilterAdapterType;

import java.util.List;

/**
 * Created by vivian.wei on 2017/10/25.
 * 影院列表页_筛选_城区Adapter
 */

public class CinemaFilterDistrictAdapter extends RecyclerView.Adapter<CinemaFilterDistrictAdapter.ViewHolder> {

    private final Context mContext;
    private List<DistrictBean> mList;
    private int mSelectIndex = 0;
    private CinemaFilterAdapterListener mListener;

    public CinemaFilterDistrictAdapter(final Context context, final List<DistrictBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    public void setOnItemClickListener(CinemaFilterAdapterListener listener) {
        mListener = listener;
    }

    public void setSelectIndex(int index) {
        mSelectIndex = index;
    }

    public int getSelectIndex() {
        return mSelectIndex;
    }

    public void setList(final List<DistrictBean> list) {
        this.mList = list;
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

    @Override
    public int getItemCount() {
        return null != mList && mList.size() > 0 ? mList.size() : 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_cinema_list_filter_district_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        DistrictBean bean = mList.get(position);
        holder.itemLayout.setBackgroundColor(ContextCompat.getColor(mContext, mSelectIndex == position ? R.color.white : R.color.color_f5f5f5));
        holder.nameTv.setText(bean.getName());
        holder.nameTv.setTextColor(ContextCompat.getColor(mContext, mSelectIndex == position ? R.color.color_f97d3f : R.color.color_555555));
        if(position > 0) {
            holder.countTv.setText(String.format(mContext.getResources().getString(R.string.cinema_list_district_count), bean.getCinemaCount()));
            holder.countTv.setTextColor(ContextCompat.getColor(mContext, mSelectIndex == position ? R.color.color_f97d3f : R.color.color_999999));
        } else {
            holder.countTv.setText("");
        }

        if (mListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //注意，这里的position不要用上面参数中的position，会出现位置错乱
                    mListener.onEvent(CinemaFilterAdapterType.TYPE_DISTRICT, holder.getIAdapterPosition());
                }
            });
        }
    }

    public class ViewHolder extends IViewHolder {

        View itemLayout;
        TextView nameTv;
        TextView countTv;

        public ViewHolder(View itemView) {
            super(itemView);

            itemLayout = itemView.findViewById(R.id.adapter_cinema_list_filter_district_item_ll);
            nameTv = itemView.findViewById(R.id.adapter_cinema_list_filter_district_item_name_tv);
            countTv = itemView.findViewById(R.id.adapter_cinema_list_filter_district_item_count_tv);
        }
    }
}
