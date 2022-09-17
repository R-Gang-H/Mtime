package com.mtime.bussiness.ticket.cinema.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.bussiness.ticket.cinema.bean.Coupon;

import java.util.ArrayList;
import java.util.List;

/**
 * 优惠公告列表Adapter
 */
public class PreferentialListAdapter extends BaseAdapter {
    
    private final Context mContext;
    private final List<Coupon>         mCoupons = new ArrayList<Coupon>();
    private final OnShareClickListener mShareClickListener;
    
    public PreferentialListAdapter(final Context context, final List<Coupon> coupons,
            final OnShareClickListener listener) {
        mContext = context;
        if (null != coupons) {
            mCoupons.addAll(coupons);
        }
        mShareClickListener = listener;
    }
    
    public void setDatas(List<Coupon> coupons) {
        mCoupons.addAll(coupons);
        this.notifyDataSetChanged();
    }
    
    public int getCount() {
        return mCoupons.size();
    }
    
    public Object getItem(final int position) {
        return mCoupons.get(position);
    }
    
    public long getItemId(final int position) {
        return position;
    }
    
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.preferential_info_item, null);
            holder = new ViewHolder();
            holder.validity = convertView.findViewById(R.id.item_preferential_validity);
            holder.content = convertView.findViewById(R.id.item_preferential_content);
            holder.send_friend = convertView.findViewById(R.id.item_preferential_send_friend);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        final Coupon coupon = mCoupons.get(position);
        holder.validity.setText("有效期：" + coupon.getValidity());
        holder.content.setText(coupon.getContent());
        holder.send_friend.setVisibility(View.INVISIBLE);
//        holder.send_friend.setOnClickListener(new OnClickListener() {
//
//            public void onClick(final View v) {
//                // 回调分享组件
//                if (mShareClickListener != null) {
//                    mShareClickListener.onClick(coupon.getfId());
//                }
//            }
//        });
        
        return convertView;
    }
    
    static class ViewHolder {
        TextView  validity;
        TextView  content;
        ImageView send_friend;
    }
    
    public interface OnShareClickListener {
        void onClick(String position);
    }
    
}
