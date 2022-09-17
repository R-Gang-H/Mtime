package com.mtime.bussiness.mine.profile.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aspsine.irecyclerview.IViewHolder;
import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.bussiness.mine.profile.bean.LocationListItemBean;
import com.mtime.bussiness.mine.profile.activity.LocationSelectActivity;

import java.util.List;

/**
 * Created by vivian.wei on 2017/4/11.
 * 个人资料_居住里选择页地区列表Adapter
 */

public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.ViewHolder> {
    private final BaseActivity context;
    private final List<LocationListItemBean> list;
    private LocationSelectActivity.OnLocationItemClickListener onItemClickListener;

    public LocationListAdapter(BaseActivity context, List<LocationListItemBean> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnItemClickListener(LocationSelectActivity.OnLocationItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public LocationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.location_list_item, parent, false);
        return new LocationListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final LocationListAdapter.ViewHolder holder, final int position) {
        final LocationListItemBean bean = (LocationListItemBean)getItem(position);
        holder.tvLocationName.setText(bean.getLocationName());
        // 箭头
        if(bean.isSubset()) {
            holder.ivRightArrow.setVisibility(View.VISIBLE);
        } else {
            holder.ivRightArrow.setVisibility(View.GONE);
        }
        // 文字"已选地区"
        if(0 == position) {
            if(bean.isSelect()) {
                holder.tvSelectTip.setVisibility(View.VISIBLE);
                if(View.GONE == holder.ivRightArrow.getVisibility()) {
                    // 已选地区需要右移
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.tvSelectTip.getLayoutParams();
                    params.setMargins(0, 0, 0, 0);
                    holder.tvSelectTip.setLayoutParams(params);
                }
            } else {
                holder.tvSelectTip.setVisibility(View.GONE);
            }
        } else {
            holder.tvSelectTip.setVisibility(View.GONE);
        }
        // 分隔线
        holder.bottomLine.setVisibility(position < getItemCount() - 1 ? View.VISIBLE : View.GONE);

        if (null != onItemClickListener) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(holder.itemView, bean);
                }

            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends IViewHolder {
        TextView tvLocationName;
        TextView tvSelectTip;
        ImageView ivRightArrow;
        View bottomLine;

        public ViewHolder(View itemView) {
            super(itemView);
            tvLocationName = itemView.findViewById(R.id.tv_location_name);
            tvSelectTip = itemView.findViewById(R.id.tv_select_tip);
            ivRightArrow = itemView.findViewById(R.id.iv_right_arrow);
            bottomLine = itemView.findViewById(R.id.bottom_line);
        }
    }
}
