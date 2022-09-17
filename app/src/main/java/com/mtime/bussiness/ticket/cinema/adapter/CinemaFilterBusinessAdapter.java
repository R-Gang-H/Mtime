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
import com.mtime.bussiness.ticket.cinema.bean.BusinessAreaNewBean;
import com.mtime.bussiness.ticket.cinema.widget.CinemaFilterAdapterListener;
import com.mtime.bussiness.ticket.cinema.widget.CinemaFilterAdapterType;

import java.util.List;

/**
 * Created by vivian.wei on 2017/10/25.
 * 影院列表页_筛选_商圈Adapter
 */

public class CinemaFilterBusinessAdapter extends RecyclerView.Adapter<CinemaFilterBusinessAdapter.ViewHolder> {

    private final Context mContext;
    private List<BusinessAreaNewBean> mList;
    private CinemaFilterAdapterListener mListener;
    private int mSelectIndex = -1;

    public CinemaFilterBusinessAdapter(final Context context, final List<BusinessAreaNewBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    public void setOnItemClickListener(CinemaFilterAdapterListener listener) {
        mListener = listener;
    }

    public void setSelectIndex(int index) {
        mSelectIndex = index;
    }

    public void setList(final List<BusinessAreaNewBean> list) {
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
        BusinessAreaNewBean bean = mList.get(position);
        holder.nameTv.setText(bean.getName());
        holder.countTv.setText(String.valueOf(bean.getCinemaCount()));
        holder.nameTv.setTextColor(ContextCompat.getColor(mContext, mSelectIndex == position ? R.color.color_f97d3f : R.color.color_555555));
        holder.countTv.setTextColor(ContextCompat.getColor(mContext, mSelectIndex == position ? R.color.color_f97d3f : R.color.color_555555));

        if (mListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //注意，这里的position不要用上面参数中的position，会出现位置错乱
                    mListener.onEvent(CinemaFilterAdapterType.TYPE_BUSINESS, holder.getIAdapterPosition());
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
