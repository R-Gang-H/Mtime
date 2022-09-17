package com.mtime.bussiness.ticket.movie.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.bussiness.ticket.movie.bean.TicketDetailBean;

import java.util.List;

public class ShowExchangeCodeAdapter extends BaseAdapter {
    private final BaseActivity    context;
    private final List<TicketDetailBean.NeoElectronicCodeBean> exchangeCodeList;

    public ShowExchangeCodeAdapter(BaseActivity context, List<TicketDetailBean.NeoElectronicCodeBean> exchangeCodeList) {
        this.context = context;
        this.exchangeCodeList = exchangeCodeList;
    }
    
    public int getCount() {
        return exchangeCodeList.size();
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
            arg1 = context.getLayoutInflater().inflate(R.layout.exchange_code_item, null);
            holder.exchange_name = arg1.findViewById(R.id.exchange_name);
            holder.exchange_number = arg1.findViewById(R.id.exchange_number);
            arg1.setTag(holder);
        }
        else {
            holder = (Holder) arg1.getTag();
        }

        holder.exchange_name.setText(exchangeCodeList.get(arg0).getName()+":");
       // holder.exchange_number.setText(TextUtil.splitString(exchangeCodeList.get(arg0).getValue(), 4, " "));
        holder.exchange_number.setText(exchangeCodeList.get(arg0).getValue());


        return arg1;
    }
    
    class Holder {
        TextView  exchange_name;
        TextView  exchange_number;
    }
    
}
