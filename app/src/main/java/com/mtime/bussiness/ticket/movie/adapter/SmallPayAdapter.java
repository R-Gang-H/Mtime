package com.mtime.bussiness.ticket.movie.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.bussiness.ticket.movie.bean.CommodityList;
import com.mtime.bussiness.ticket.movie.activity.SmallPayActivity;
import com.mtime.util.ImageURLManager;

import java.util.List;

/**
 * 小卖页中用到
 */
public class SmallPayAdapter extends BaseAdapter {
    private final SmallPayActivity    context;
    private final List<CommodityList> commodityList;

    public SmallPayAdapter(SmallPayActivity context, List<CommodityList> commodityList) {
        this.context = context;
        this.commodityList = commodityList;
    }
    
    public int getCount() {
        return commodityList.size();
    }
    
    public Object getItem(int arg0) {
        return arg0;
    }
    
    public long getItemId(int arg0) {
        return arg0;
    }
    
    public View getView(final int arg0, View arg1, ViewGroup arg2) {
        final Holder holder;
        if (arg1 == null) {
            holder = new Holder();
            arg1 = context.getLayoutInflater().inflate(R.layout.small_pay_item, null);
            holder.smallpay_tip = arg1.findViewById(R.id.smallpay_tip);
            holder.img = arg1.findViewById(R.id.small_pay_img);
            holder.textInfo = arg1.findViewById(R.id.small_pay_info);
            holder.textName = arg1.findViewById(R.id.small_pay_name);
            holder.textPrice = arg1.findViewById(R.id.small_pay_price);
            holder.retailPriceLayout = arg1.findViewById(R.id.small_pay_retailPrice_layout);
            holder.retailPrice = arg1.findViewById(R.id.small_pay_retailPrice);
            holder.btnAdd = arg1.findViewById(R.id.small_pay_btn_add);
            holder.btnReduce = arg1.findViewById(R.id.small_pay_btn_reduce);
            holder.textCount = arg1.findViewById(R.id.small_pay_tv_count);
            arg1.setTag(holder);
        }
        else {
            holder = (Holder) arg1.getTag();
        }
        if (arg0 == 0) {
            holder.smallpay_tip.setVisibility(View.VISIBLE);
        }
        else {
            holder.smallpay_tip.setVisibility(View.GONE);
        }
        
        context.volleyImageLoader.displayImage(commodityList.get(arg0).getImagePath(), holder.img, R.drawable.default_image, R.drawable.default_image, ImageURLManager.ImageStyle.THUMB, null);

        holder.textName.setText(commodityList.get(arg0).getShortName());
        holder.textInfo.setText(commodityList.get(arg0).getDesc());
        holder.textPrice.setText(String.valueOf(commodityList.get(arg0).getPrice()));
        if (commodityList.get(arg0).getRetailPrice() != null && !"".equals(commodityList.get(arg0).getRetailPrice())) {
            holder.retailPriceLayout.setVisibility(View.VISIBLE);
            holder.retailPrice.setText(String.valueOf(Double.parseDouble(commodityList.get(arg0).getRetailPrice()) / 100));
        }
        else {
            holder.retailPriceLayout.setVisibility(View.INVISIBLE);
        }
        if (commodityList.get(arg0).getCount() == commodityList.get(arg0).getMaxLimited()
                && commodityList.get(arg0).getMaxLimited() != 0) {
            holder.btnAdd.setClickable(false);
            holder.btnAdd.setBackgroundResource(R.drawable.icon_add_pressed);
        }
        else {
            holder.btnAdd.setClickable(true);
            holder.btnAdd.setBackgroundResource(R.drawable.btn_add_temp);
        }
        if (commodityList.get(arg0).getCount() == 0) {
            holder.btnReduce.setClickable(false);
            holder.btnReduce.setBackgroundResource(R.drawable.icon_minus_pressed);
        }
        else {
            holder.btnReduce.setClickable(true);
            holder.btnReduce.setBackgroundResource(R.drawable.btn_minus);
        }
        
        holder.btnAdd.setTag(arg0);
        holder.btnReduce.setTag(arg0);
        holder.btnAdd.setOnClickListener(new OnClickListener() {
            
            public void onClick(View v) {
                int i = (Integer) v.getTag();
                int count = commodityList.get(i).getCount();
                count++;
                commodityList.get(i).setCount(count);
                holder.textCount.setText(String.valueOf(count));
                if (commodityList.get(i).getCount() == commodityList.get(i).getMaxLimited()
                        && commodityList.get(i).getMaxLimited() != 0) {
                    v.setClickable(false);
                    v.setBackgroundResource(R.drawable.icon_add_pressed);
                }
                else {
                    v.setClickable(true);
                    v.setBackgroundResource(R.drawable.btn_add_temp);
                }
                if (commodityList.get(i).getCount() == 0) {
                    holder.btnReduce.setClickable(false);
                    holder.btnReduce.setBackgroundResource(R.drawable.icon_minus_pressed);
                }
                else {
                    holder.btnReduce.setClickable(true);
                    holder.btnReduce.setBackgroundResource(R.drawable.btn_minus);
                }
                context.refreshPrice();
            }
            
        });
        holder.btnReduce.setOnClickListener(new OnClickListener() {
            
            public void onClick(View v) {
                int i = (Integer) v.getTag();
                int count = commodityList.get(i).getCount();
                if (count == 0) {
                    return;
                }
                count--;
                commodityList.get(i).setCount(count);
                holder.textCount.setText(String.valueOf(count));
                if (commodityList.get(i).getCount() == commodityList.get(i).getMaxLimited()
                        && commodityList.get(i).getMaxLimited() != 0) {
                    holder.btnAdd.setClickable(false);
                    holder.btnAdd.setBackgroundResource(R.drawable.icon_add_pressed);
                }
                else {
                    holder.btnAdd.setClickable(true);
                    holder.btnAdd.setBackgroundResource(R.drawable.btn_add_temp);
                }
                if (commodityList.get(i).getCount() == 0) {
                    v.setClickable(false);
                    v.setBackgroundResource(R.drawable.icon_minus_pressed);
                }
                else {
                    v.setClickable(true);
                    v.setBackgroundResource(R.drawable.btn_minus);
                }
                context.refreshPrice();
            }
        });
        
        return arg1;
    }
    
    class Holder {
        TextView  smallpay_tip;
        ImageView img;
        TextView  textName;
        TextView  textInfo;
        TextView  textPrice;
        View      retailPriceLayout;
        TextView  retailPrice;
        TextView    btnReduce;
        TextView    btnAdd;
        TextView  textCount;
    }
    
}
