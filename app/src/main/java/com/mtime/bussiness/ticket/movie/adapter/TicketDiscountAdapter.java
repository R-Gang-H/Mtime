package com.mtime.bussiness.ticket.movie.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.bussiness.ticket.movie.bean.CommodityList;
import com.mtime.common.utils.Utils;
import com.mtime.util.MtimeUtils;

import java.util.List;

/**
 * Created by zhulinping on 2017/6/21.
 * 小卖页中有用到
 */

public class TicketDiscountAdapter extends BaseAdapter{
    private final BaseActivity mContext;
    private final List<CommodityList> mDisCountList;
    public TicketDiscountAdapter(BaseActivity context, List<CommodityList> list){
        mContext = context;
        mDisCountList = list;
    }
    @Override
    public int getCount() {
        return mDisCountList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDisCountList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommodityList bean = mDisCountList.get(position);
        ViewHolder vh = null;
        if(convertView == null){
            vh = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.ticket_discount_item_layout,null);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }
        vh.discountImv = convertView.findViewById(R.id.smallpay_imv);
        vh.discountNameTv = convertView.findViewById(R.id.smallpay_name_tv);
        vh.disCountPriceTv = convertView.findViewById(R.id.smallpay_price_tv);
        mContext.volleyImageLoader.displayImage(bean.getImagePath(), vh.discountImv,
                R.drawable.default_mtime, R.drawable.default_mtime,
                Utils.dip2px(mContext, 128),
                Utils.dip2px(mContext, 128), null);
        vh.discountNameTv.setText(bean.getShortName()+" (" + bean.getDesc() + ")");
        vh.disCountPriceTv.setText("¥ "+ MtimeUtils.formatPrice(bean.getPrice())+" x "+bean.getQuantity());
        return convertView;
    }
    class ViewHolder{
        ImageView discountImv;
        TextView discountNameTv;
        TextView disCountPriceTv;
    }
}
