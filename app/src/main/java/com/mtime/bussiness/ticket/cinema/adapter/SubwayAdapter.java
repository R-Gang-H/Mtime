//package com.mtime.bussiness.ticket.cinema.adapter;
//
//import android.graphics.Typeface;
//import android.support.v4.content.ContextCompat;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.TextView;
//
//import com.mtime.frame.BaseActivity;
//import com.mtime.R;
//import com.mtime.bussiness.ticket.cinema.bean.SubwayBaseBean;
//import com.mtime.bussiness.ticket.cinema.widget.CinemaFilterAdapterListener;
//import com.mtime.bussiness.ticket.cinema.widget.CinemaFilterAdapterType;
//
//import java.util.List;
//
//public class SubwayAdapter extends BaseAdapter {
//    private final BaseActivity          context;
//    private final List<SubwayBaseBean>  subwayBaseBeans;
//    private CinemaFilterAdapterListener listener;
//    private int                         selected;
//
//    public SubwayAdapter(final BaseActivity context, final List<SubwayBaseBean> subwayBaseBeans) {
//        this.context = context;
//        this.subwayBaseBeans = subwayBaseBeans;
//        selected = -1;
//    }
//
//    public void unselect() {
//        this.selected = -1;
//        notifyDataSetChanged();
//    }
//
//    public void setListener(CinemaFilterAdapterListener listener) {
//        this.listener = listener;
//    }
//
//    public int getCount() {
//        return subwayBaseBeans.size() + 1;
//    }
//
//    public Object getItem(final int arg0) {
//
//        return arg0;
//    }
//
//    public long getItemId(final int arg0) {
//
//        return arg0;
//    }
//
//    public View getView(final int arg0, View arg1, final ViewGroup arg2) {
//        Holder holder;
//        if (arg1 == null) {
//            holder = new Holder();
//            arg1 = context.getLayoutInflater().inflate(R.layout.train_item, null);
//            holder.trainName = (TextView) arg1.findViewById(R.id.train_name);
//            arg1.setTag(holder);
//            holder.trainName.setOnClickListener(new OnClickListener() {
//
//                @Override
//                public void onClick(View view) {
//                    selected = (int) view.getTag();
//                    notifyDataSetChanged();
//                    if (null != listener) {
//                        listener.onEvent(CinemaFilterAdapterType.TYPE_SUBWAY, selected);
//                    }
//                }
//            });
//        }
//        else {
//            holder = (Holder) arg1.getTag();
//        }
//
//        if (0 == arg0) {
//            holder.trainName.setText(context.getResources().getString(R.string.s_all));
//            holder.trainName.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//??????
//        }
//        else {
//            holder.trainName.setText(subwayBaseBeans.get(arg0 - 1).getName());
//            holder.trainName.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
//        }
//
//        if (selected == arg0) {
//            holder.trainName.setTextColor(ContextCompat.getColor(context,R.color.color_0075c4));
//            holder.trainName.setBackgroundResource(R.drawable.cinema_filter_item_background_new);
//        }
//        else {
//            holder.trainName.setTextColor(ContextCompat.getColor(context,R.color.color_333333));
//            holder.trainName.setBackgroundResource(R.color.transparent);
//        }
//        holder.trainName.setTag(arg0);
//
//
//        return arg1;
//    }
//
//    class Holder {
//        TextView trainName;
//
//    }
//
//}
