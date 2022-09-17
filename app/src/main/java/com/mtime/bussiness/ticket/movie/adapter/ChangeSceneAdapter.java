package com.mtime.bussiness.ticket.movie.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.bussiness.ticket.movie.activity.SeatSelectActivity;
import com.mtime.bussiness.ticket.movie.bean.ShowTimeUIBean;
import com.mtime.common.utils.DateUtil;
import com.mtime.common.utils.Utils;
import com.mtime.util.MtimeUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 更换场次Adapter
 */
public class ChangeSceneAdapter extends BaseAdapter {
    
    SeatSelectActivity                 mContext;
    private final List<ShowTimeUIBean> mShowtimes;
    private final String strDate;
    
    public ChangeSceneAdapter(final SeatSelectActivity context, final List<ShowTimeUIBean> showtimes, final String date) {
        mContext = context;
        mShowtimes = showtimes;
        this.strDate = date;
    }
    
    public int getCount() {
        return mShowtimes.size();
    }
    
    public Object getItem(final int position) {
        return mShowtimes.get(position);
    }
    
    public long getItemId(final int position) {
        return position;
    }
    
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.seat_select_sidebar_item, null);
            holder = new ViewHolder();
            holder.tv_time = convertView.findViewById(R.id.item_change_scene_tv_time);
            holder.tv_nextday = convertView.findViewById(R.id.movie_child_tv_nextday);
            holder.tv_tomorrow = convertView.findViewById(R.id.movie_child_tv_tomorrow);
            holder.tv_version_and_language = convertView
                    .findViewById(R.id.item_change_scene_tv_version_and_language);
            holder.tv_hall = convertView.findViewById(R.id.item_change_scene_tv_hall);
            holder.tv_price = convertView.findViewById(R.id.item_change_scene_tv_price);
            holder.movie_child_tv_activity = convertView.findViewById(R.id.movie_child_tv_activity);
            holder.childtextCinemaPrice = convertView.findViewById(R.id.movie_child_tv_cinema_price);
            holder.childtextCinemaPriceLine = convertView.findViewById(R.id.movie_child_tv_cinema_price_line);
            holder.seatLess = convertView.findViewById(R.id.seatless_tag);
            holder.changeBtn = convertView.findViewById(R.id.seat_select_change_item_btn);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ShowTimeUIBean showtime = mShowtimes.get(position);
        if (showtime.getProviderList() != null) {
            if (String.valueOf(showtime.getProviderList().get(0).getdId()).equals(mContext.mDId)) {
                holder.changeBtn.setVisibility(View.INVISIBLE);
            }
            else {
                holder.changeBtn.setVisibility(View.VISIBLE);
            }
        }
        
        // holder.tv_time.setText(showtime.getDateTime());
        /* SimpleDateFormat sdf = new SimpleDateFormat("HH:mm"); */
        final String date = DateUtil.getLongToDateForLocal(DateUtil.sdf2, showtime.getTime());
        holder.tv_time.setText(date);

        // 减去8小时啊8小时
        Date checkDate = new Date(showtime.getTime());
        Date compareDate = new Date();
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            compareDate = df.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 是否显示“次日”
        if (DateUtil.isTomorrow(checkDate, compareDate)) {
            holder.tv_nextday.setVisibility(View.VISIBLE);
            holder.tv_time.setTextSize(Utils.px2sp(mContext, 30));
        }
        else {
            holder.tv_nextday.setVisibility(View.GONE);
            holder.tv_time.setTextSize(Utils.px2sp(mContext, 48));
        }
        
        if (showtime.getDuration() > 0) {
            String date1 = DateUtil.getLongToDateForLocal(DateUtil.sdf2, showtime.getTime() + showtime.getDuration()
                    * 60 * 1000);
            holder.tv_tomorrow.setText(date1 + "散场");
            holder.tv_tomorrow.setVisibility(View.VISIBLE);
        }
        else {
            holder.tv_tomorrow.setVisibility(View.GONE);
        }
        
        String versionLanguage = null;
        // 场次版本：有6种“2D、3D、IMAX、IMAX3D、4D、DMAX”。版本不详，则不显示
        final String version = showtime.getVersionDesc();
        if ((version != null) && !"".equals(version.trim()) && !mContext.getString(R.string.s_unkonw).equals(version)) {
            // 如：2D
            versionLanguage = version;
        }
        // 场次语言：有4种“原版、中文版、粤语、川话”。若语言为不详，则不显示
        final String language = showtime.getLanguage();
        if ((language != null) && !"".equals(language.trim())
                && !mContext.getString(R.string.s_unkonw).equals(language)) {
            if (versionLanguage != null) {
                // 如：2D/英文版
                versionLanguage = versionLanguage + "/" + language;
            }
            else {
                // 如：英文版
                versionLanguage = language;
            }
        }
        if (versionLanguage != null) {
            holder.tv_version_and_language.setText(versionLanguage);
            holder.tv_version_and_language.setVisibility(View.VISIBLE);
        }
        else {
            holder.tv_version_and_language.setVisibility(View.GONE);
        }
        
        final String hall = showtime.getHall();
        if ((hall != null) && !"".equals(hall.trim())) {
            holder.tv_hall.setText(hall);
            holder.tv_hall.setVisibility(View.VISIBLE);
        }
        else {
            holder.tv_hall.setVisibility(View.GONE);
        }
        
        if (showtime.isCoupon()) {
            holder.movie_child_tv_activity.setVisibility(View.VISIBLE);
        } else {
            holder.movie_child_tv_activity.setVisibility(View.INVISIBLE);
        }
        holder.tv_price.setText(mContext.getString(R.string.s_rmb) + MtimeUtils.formatPrice(showtime.getPrice()));
        
        if (!TextUtils.isEmpty(showtime.getSeatSalesTip())) {
            holder.seatLess.setVisibility(View.VISIBLE);
            holder.seatLess.setText(showtime.getSeatSalesTip());
            holder.childtextCinemaPrice.setVisibility(View.GONE);
            holder.childtextCinemaPriceLine.setVisibility(View.GONE);
        } else {
            if ((showtime.getCinemaPrice() > 0) && (((showtime.getCinemaPrice() / 100) - Double.parseDouble(showtime.getPrice())) > 0)) {
                holder.childtextCinemaPrice.setVisibility(View.VISIBLE);
                holder.childtextCinemaPriceLine.setVisibility(View.VISIBLE);
                holder.childtextCinemaPrice.setText("￥" + MtimeUtils.formatPrice(showtime.getCinemaPrice() / 100));
                holder.childtextCinemaPrice.setTextSize(14);
            } else {
                holder.childtextCinemaPrice.setVisibility(View.GONE);
                holder.childtextCinemaPriceLine.setVisibility(View.GONE);
            }
            holder.seatLess.setVisibility(View.GONE);
        }

        return convertView;
    }
    
    static class ViewHolder {
        TextView  tv_time;
        TextView  tv_nextday;
        TextView  tv_tomorrow;
        TextView  tv_version_and_language;
        TextView  tv_hall;
        TextView  tv_price;
        TextView  childtextCinemaPrice;
        View      childtextCinemaPriceLine;
        TextView  seatLess;
        TextView    changeBtn;
        TextView  movie_child_tv_activity;
    }
}
