package com.mtime.bussiness.ticket.movie.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.bussiness.ticket.movie.bean.ShopAfterBuyTicketBean;
import com.mtime.util.ImageURLManager;

import java.util.List;

/**
 * 用于{@link com.mtime.bussiness.ticket.movie.activity.OrderPaySuccessActivity}
 */
public class ShowGoodsAfterBuyTicketAdapter extends BaseAdapter {
    private final BaseActivity    context;
    private final List<ShopAfterBuyTicketBean.ListBean> goodsList;

    public ShowGoodsAfterBuyTicketAdapter(BaseActivity context, List<ShopAfterBuyTicketBean.ListBean> goodsList) {
        this.context = context;
        this.goodsList = goodsList;
    }
    
    public int getCount() {
        return goodsList.size();
    }
    
    public Object getItem(int arg0) {
        return arg0;
    }
    
    public long getItemId(int arg0) {
        return arg0;
    }
    
    public View getView(final int arg0, View arg1, ViewGroup arg2) {
        Holder holder;
        if (arg1 == null) {
            holder = new Holder();
            arg1 = context.getLayoutInflater().inflate(R.layout.shop_after_buy_ticket_item, null);

            holder.sku_img = arg1.findViewById(R.id.sku_img_after_buy_ticket);
            holder.sku_prompt = arg1.findViewById(R.id.prompt_after_buy_ticket);
            holder.sku_all = arg1.findViewById(R.id.sku_all_after_buy_ticket);
            holder.sku_name = arg1.findViewById(R.id.sku_name_after_buy_ticket);
            holder.sku_price = arg1.findViewById(R.id.sku_price_after_buy_ticket);

            arg1.setTag(holder);
        }
        else {
            holder = (Holder) arg1.getTag();
        }
            if(goodsList.size()>3 && arg0+1 ==goodsList.size()){
                holder.sku_img.setVisibility(View.GONE);
                holder.sku_prompt.setVisibility(View.GONE);
                holder.sku_all.setVisibility(View.VISIBLE);

            }else {
                holder.sku_img.setVisibility(View.VISIBLE);
                holder.sku_all.setVisibility(View.GONE);
                context.volleyImageLoader.displayImage(goodsList.get(arg0).getImage(), holder.sku_img,
                        0, 0, 200, 200, ImageURLManager.FIX_WIDTH_AND_HEIGHT, null);
                if(!TextUtils.isEmpty(goodsList.get(arg0).getPrompt())){
                    holder.sku_prompt.setText(goodsList.get(arg0).getPrompt());
                }else {
                    holder.sku_prompt.setVisibility(View.GONE);
                }
                holder.sku_name.setText(goodsList.get(arg0).getName());

                int salePrice = goodsList.get(arg0).getSalePrice();

                if(salePrice%100==0){
                    holder.sku_price.setText("¥ " + salePrice/100);
                }else if(salePrice%100!=0 && salePrice%10==0){
                    holder.sku_price.setText("¥ " + salePrice/100+"."+(salePrice%100)/10);
                }else  if(salePrice%100<10 && salePrice%10!=0){
                    holder.sku_price.setText("¥ " + salePrice/100+".0"+salePrice%100);
                }else if(salePrice%100>10 && salePrice%10!=0){
                    holder.sku_price.setText("¥ " + salePrice/100+"."+salePrice%100);
                }
            }
        return arg1;
    }
    
    class Holder {
        ImageView sku_img;
        TextView  sku_prompt;
        LinearLayout  sku_all;
        TextView  sku_name;
        TextView  sku_price;

    }
    
}
