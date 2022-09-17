package com.mtime.bussiness.location.adapter;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.bussiness.location.bean.CityBean;
import com.mtime.frame.BaseActivity;

import java.util.ArrayList;

public class CitySearchAdapter extends BaseAdapter {
    private final BaseActivity  context;
    private ArrayList<CityBean> cityBeans;
    private String mKey;
    
    public CitySearchAdapter(final BaseActivity context, final ArrayList<CityBean> cityBeans, String key) {
        this.context = context;
        this.cityBeans = cityBeans;
        mKey = key;
    }
    
    public void setCityBean(final ArrayList<CityBean> cityBeans, String key) {
        this.cityBeans = cityBeans;
        mKey = key;
    }
    
    public int getCount() {
        return cityBeans.size();
    }
    
    public Object getItem(final int arg0) {
        return arg0;
    }
    
    public long getItemId(final int arg0) {
        return arg0;
    }
    
    public View getView(final int arg0, View arg1, final ViewGroup arg2) {
        
        Holder holder;
        if (arg1 == null) {
            holder = new Holder();
            arg1 = context.getLayoutInflater().inflate(R.layout.city_search_item, null);
            holder.cityName = arg1.findViewById(R.id.city_name);
            holder.lineView = arg1.findViewById(R.id.city_search_item_line_view);
            arg1.setTag(holder);
        }
        else {
            holder = (Holder) arg1.getTag();
        }

        String name = cityBeans.get(arg0).getName();
        if(TextUtils.isEmpty(mKey)) {
            holder.cityName.setText(name);
        } else {
            // 匹配的显示桔色
            int start = name.indexOf(mKey);
            if(start >= 0) {
                int end = start + mKey.length();
                SpannableStringBuilder styleName = new SpannableStringBuilder(name);
                styleName.setSpan(new ForegroundColorSpan(context.getColor(R.color.color_ff5a36)), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                holder.cityName.setText(styleName);
            } else {
                holder.cityName.setText(name);
            }
        }
        holder.lineView.setVisibility(arg0 < getCount() - 1 ? View.VISIBLE : View.GONE);
        return arg1;
    }
    
    class Holder {
        TextView cityName;
        View lineView;
    }
    
}
