//package com.mtime.bussiness.ticket.cinema.adapter;
//
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.TextView;
//
//import com.mtime.frame.BaseActivity;
//import com.mtime.R;
//import com.mtime.bussiness.ticket.cinema.bean.StationBaseBean;
//import com.mtime.bussiness.ticket.cinema.widget.CinemaFilterAdapterListener;
//import com.mtime.bussiness.ticket.cinema.widget.CinemaFilterAdapterType;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class StationAdapter extends BaseAdapter {
//    private final BaseActivity          context;
//    private List<StationBaseBean> stationBaseBeans = new ArrayList<StationBaseBean>();
//    private CinemaFilterAdapterListener listener;
//    private int selected;
//
//    public StationAdapter(final BaseActivity context, final List<StationBaseBean> stationBaseBeans) {
//        this.context = context;
//        if (null != stationBaseBeans) {
//            this.stationBaseBeans = stationBaseBeans;
//        }
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
//        return stationBaseBeans.size();
//    }
//
//    public Object getItem(final int arg0) {
//        return arg0;
//    }
//
//    public long getItemId(final int arg0) {
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
//                        listener.onEvent(CinemaFilterAdapterType.TYPE_STATION, selected);
//                    }
//                }
//            });
//        }
//        else {
//            holder = (Holder) arg1.getTag();
//        }
//
//        holder.trainName.setText(stationBaseBeans.get(arg0).getStName());
//
////        if (selected == arg0) {
////            holder.trainName.setTextColor(ContextCompat.getColor(context,R.color.color_0075c4));
////        } else {
////            holder.trainName.setTextColor(ContextCompat.getColor(context,R.color.color_333333));
////        }
//        holder.trainName.setTag(arg0);
//
//
//        return arg1;
//    }
//
//    class Holder {
//        TextView trainName;
//    }
//
//    public void setDatas(final List<StationBaseBean> list) {
//        this.stationBaseBeans.clear();
//        this.stationBaseBeans.addAll(list);
//        this.notifyDataSetChanged();
//    }
//
//    public List<StationBaseBean> getBeans() {
//        return this.stationBaseBeans;
//    }
//}
