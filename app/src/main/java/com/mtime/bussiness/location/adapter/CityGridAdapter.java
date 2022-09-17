package com.mtime.bussiness.location.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.bussiness.location.bean.CityBean;
import com.mtime.frame.BaseActivity;

import java.util.ArrayList;

public class CityGridAdapter extends BaseAdapter {
    private final BaseActivity        context;
    private final ArrayList<CityBean> cityBeans;
    private boolean mIsHot = false; // 是否热门城市
    
    public CityGridAdapter(final BaseActivity context, boolean isHot, final ArrayList<CityBean> cityBeans) {
        this.context = context;
        this.cityBeans = cityBeans;
        mIsHot = isHot;
    }
    
    public int getCount() {
        return cityBeans.size();
    }
    
    public Object getItem(final int position) {
        return position;
    }
    
    public long getItemId(final int position) {
        return position;
    }
    
    public View getView(final int position, View convertView, final ViewGroup parent) {
        
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            if(mIsHot) {
                convertView = LayoutInflater.from(context).inflate(R.layout.city_grid_item, null);
            } else {
                convertView = LayoutInflater.from(context).inflate(R.layout.city_search_item, null);
                holder.lineView = convertView.findViewById(R.id.city_search_item_line_view);
            }
            holder.cityTextView = convertView.findViewById(R.id.city_name);
            convertView.setTag(holder);
        }
        else {
            holder = (Holder) convertView.getTag();
        }
        holder.cityTextView.setText(cityBeans.get(position).getName());
        if(holder.lineView != null) {
            holder.lineView.setVisibility(position < getCount() - 1 ? View.VISIBLE : View.GONE);
        }
        return convertView;
    }
    
    class Holder {
        TextView cityTextView;
        View lineView;
    }
    
}
