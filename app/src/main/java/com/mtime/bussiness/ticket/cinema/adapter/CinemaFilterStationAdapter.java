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
import com.mtime.bussiness.ticket.cinema.bean.StationBean;
import com.mtime.bussiness.ticket.cinema.widget.CinemaFilterAdapterListener;
import com.mtime.bussiness.ticket.cinema.widget.CinemaFilterAdapterType;

import java.util.List;

/**
 * Created by vivian.wei on 2017/10/27.
 * 影院列表页_筛选_地铁站Adapter
 */

public class CinemaFilterStationAdapter extends RecyclerView.Adapter<CinemaFilterStationAdapter.ViewHolder> {

    private final Context mContext;
    private List<StationBean> mList;
    private CinemaFilterAdapterListener mListener;
    private int mSelectIndex = -1;

    public CinemaFilterStationAdapter(final Context context, final List<StationBean> list) {
        this.mContext = context.getApplicationContext();
        this.mList = list;
    }

    public void setOnItemClickListener(CinemaFilterAdapterListener listener) {
        mListener = listener;
    }

    public void setSelectIndex(int index) {
        mSelectIndex = index;
    }

    public void setList(final List<StationBean> list) {
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_cinema_list_filter_business_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        StationBean bean = mList.get(position);
        holder.nameTv.setText(bean.getStName());
        holder.countTv.setText(String.valueOf(bean.getCinemaCount()));
        holder.nameTv.setTextColor(ContextCompat.getColor(mContext, mSelectIndex == position ? R.color.color_f97d3f : R.color.color_555555));
        holder.countTv.setTextColor(ContextCompat.getColor(mContext, mSelectIndex == position ? R.color.color_f97d3f : R.color.color_555555));

        if (mListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //注意，这里的position不要用上面参数中的position，会出现位置错乱
                    mListener.onEvent(CinemaFilterAdapterType.TYPE_STATION, holder.getIAdapterPosition());
                }
            });
        }
    }

    public class ViewHolder extends IViewHolder {

        TextView nameTv;
        TextView countTv;

        public ViewHolder(View itemView) {
            super(itemView);

            nameTv = itemView.findViewById(R.id.adapter_cinema_list_filter_business_item_name_tv);
            countTv = itemView.findViewById(R.id.adapter_cinema_list_filter_business_item_count_tv);
        }
    }
}
