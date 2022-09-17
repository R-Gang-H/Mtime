package com.mtime.bussiness.location.adapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.bussiness.location.bean.ChangeCitySortBean;
import com.mtime.bussiness.location.bean.CityBean;
import com.mtime.frame.BaseActivity;

import java.util.ArrayList;

import androidx.core.content.ContextCompat;

public class CityAdadper extends BaseAdapter {
    private final ArrayList<ChangeCitySortBean> changeCitySortBeans;
    private final BaseActivity                  context;
    private final OnItemClickListener           itemClickListener;
    
    public CityAdadper(final ArrayList<ChangeCitySortBean> changeCitySortBeans, final BaseActivity context,
            final OnItemClickListener itemClickListener) {
        this.changeCitySortBeans = changeCitySortBeans;
        this.context = context;
        this.itemClickListener = itemClickListener;
    }
    
    public int getCount() {
        return changeCitySortBeans.size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.city_item, null);
            holder.cityGridView = convertView.findViewById(R.id.city_grid);
            holder.cityPinyinGrid = convertView.findViewById(R.id.city_pinyin_grid);
            holder.citypinYin = convertView.findViewById(R.id.city_pinYin);
            convertView.setTag(holder);
        }
        else {
            holder = (Holder) convertView.getTag();
        }

        ArrayList<CityBean> cityBeans = changeCitySortBeans.get(position).getCityBeans();
        if (CollectionUtils.isNotEmpty(cityBeans)) {
            convertView.setVisibility(View.VISIBLE);

            // 是否为热门城市
            boolean isHot = false;
            if (position == 0) {
                String shortName = cityBeans.get(0).getPinyinShort();
                isHot = TextUtils.isEmpty(shortName);
            }
            CityGridAdapter cityGridAdapter = new CityGridAdapter(context, isHot, cityBeans);
            if(isHot) {
                holder.cityPinyinGrid.setVisibility(View.GONE);
                holder.cityGridView.setVisibility(View.VISIBLE);
                // 热门城市title
                holder.citypinYin.setText(context.getString(R.string.s_hot_city));
                holder.citypinYin.setBackgroundColor(ContextCompat.getColor(context,android.R.color.transparent));
                // 列表
                holder.cityGridView.setAdapter(cityGridAdapter);
                holder.cityGridView.setTag(position);
                holder.cityGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
                holder.cityGridView.setOnItemClickListener(itemClickListener);
            } else {
                holder.cityGridView.setVisibility(View.GONE);
                holder.cityPinyinGrid.setVisibility(View.VISIBLE);
                // 拼音大写首字母
                holder.citypinYin.setText(changeCitySortBeans.get(position).getCityBeans().get(0).getPinyinFull()
                        .substring(0, 1));
                holder.citypinYin.setBackgroundColor(context.getColor(R.color.color_f2f3f6));
                // 列表
                holder.cityPinyinGrid.setAdapter(cityGridAdapter);
                holder.cityPinyinGrid.setTag(position);
                holder.cityPinyinGrid.setSelector(new ColorDrawable(Color.TRANSPARENT));
                holder.cityPinyinGrid.setOnItemClickListener(itemClickListener);
            }
        }
        else {
            convertView.setVisibility(View.GONE);
        }

        return convertView;
    }
    
    class Holder {
        com.mtime.widgets.MyGridView cityGridView;
        com.mtime.widgets.MyGridView cityPinyinGrid;
        TextView                     citypinYin;
    }
    
}
