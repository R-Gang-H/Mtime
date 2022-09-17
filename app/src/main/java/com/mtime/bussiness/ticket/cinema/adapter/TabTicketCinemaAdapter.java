package com.mtime.bussiness.ticket.cinema.adapter;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.IViewHolder;
import com.mtime.R;
import com.mtime.base.statistic.StatisticConstant;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.bussiness.ticket.cinema.bean.CinemaBaseInfo;
import com.mtime.bussiness.ticket.cinema.bean.CinemaResultFeatureBean;
import com.mtime.bussiness.ticket.cinema.bean.CinemaShowtimeBean;
import com.mtime.bussiness.ticket.cinema.bean.CouponActivityItem;
import com.mtime.frame.BaseFrameUIActivity;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.statistic.large.ticket.StatisticTicket;
import com.mtime.util.JumpUtil;
import com.mtime.util.MtimeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vivian.wei on 2017/10/19.
 * tab_购票_影院_影院列表、影片排片页_影院列表 Adapter
 */

public class TabTicketCinemaAdapter extends RecyclerView.Adapter<TabTicketCinemaAdapter.ViewHolder> {

    private final Context mContext;
    private List<CinemaBaseInfo> mList;
    private final MovieShowtimeCinemaShowtimeAdapter.OnDirectSaleShowtimeListener mDirectSaleShowtimeListener;
    // 是否为地铁筛选
    private boolean mIsSubwayFilter = false;
    private String mDateString;

    private static final int COUPON_SHOW_COUNT = 2;

    public TabTicketCinemaAdapter(final Context context, final List<CinemaBaseInfo> list,
                                  MovieShowtimeCinemaShowtimeAdapter.OnDirectSaleShowtimeListener directSaleShowtimeListener) {
        this.mContext = context;
        this.mList = list;
        this.mDirectSaleShowtimeListener = directSaleShowtimeListener;
    }

    // 是否为地铁筛选
    public void isSubwayFilter(boolean isSubwayFilter) {
        mIsSubwayFilter = isSubwayFilter;
    }

    // 影片排片页_选中的日期
    public void setSelectDateString(String dateString) {
        mDateString = dateString;
    }

    public void setList(final List<CinemaBaseInfo> list) {
        this.mList = list;
    }

    public Object getItem(final int arg0) {
        return mList.get(arg0);
    }

    public long getItemId(final int arg0) {
        return arg0;
    }

    @Override
    public int getItemCount() {
        return null != mList && mList.size() > 0 ? mList.size() : 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_cinema_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final CinemaBaseInfo bean = mList.get(position);

        holder.nameTv.setText(bean.getCinameName().trim());
        if (bean.isTicket() && bean.getMinPrice() > 0) {
            holder.priceTv.setVisibility(View.VISIBLE);
            holder.priceUnitTv.setVisibility(View.VISIBLE);
            String minPrice = MtimeUtils.formatPrice(String.valueOf((float) bean.getMinPrice() / 100.0));
            holder.priceTv.setText(minPrice);
        } else {
            holder.priceTv.setVisibility(View.GONE);
            holder.priceUnitTv.setVisibility(View.GONE);
        }
        holder.addressTv.setText(TextUtils.isEmpty(bean.getAddress()) ? "--" : bean.getAddress());
        // if order by subway, add "离地铁"
        String value = getDistance(bean.getDistance());
        if (mIsSubwayFilter && !TextUtils.isEmpty(value)) {
            value = String.format("离地铁%s", value);
        }
        holder.distanceTv.setText(value);
        holder.favIv.setVisibility(bean.isFavorit() ? View.VISIBLE : View.GONE);
        holder.directSaleTv.setVisibility(bean.getDirectSalesFlag() == 1 ? View.VISIBLE : View.GONE);
        // 上次去过和特效
        showHolderBeenAndFeature(holder, bean);

        // 优惠列表
        if (null != bean.getCouponActivityList() && bean.getCouponActivityList().size() > 0) {
            final List<CouponActivityItem> allCoupons = bean.getCouponActivityList();
            holder.couponIRecyclerView.setVisibility(View.VISIBLE);
            holder.couponIRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            // 超过2条
            if (allCoupons.size() > COUPON_SHOW_COUNT) {
                final List<CouponActivityItem> topCoupons = new ArrayList<>();
                for (int i = 0, count = Math.min(COUPON_SHOW_COUNT, allCoupons.size()); i < count; i++) {
                    topCoupons.add(allCoupons.get(i));
                }
                final TabTicketCinemaCouponAdapter adapter1 = new TabTicketCinemaCouponAdapter(mContext, topCoupons);
                holder.couponIRecyclerView.setIAdapter(adapter1);

                holder.couponCountLayout.setVisibility(View.VISIBLE);
                holder.couponCountTv.setText(String.format(mContext.getString(R.string.cinema_list_coupon_count), bean.getCouponActivityList().size()));
                holder.couponArrowIv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.arrow_cinema_list_coupon_down));

                // 点击N项优惠
                holder.couponCountLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapter1.setShowAll(!adapter1.isShowAll());
                        int drawableId = adapter1.isShowAll() ? R.drawable.arrow_cinema_list_coupon_up : R.drawable.arrow_cinema_list_coupon_down;
                        holder.couponArrowIv.setImageDrawable(ContextCompat.getDrawable(mContext, drawableId));
                        adapter1.setList(adapter1.isShowAll() ? allCoupons : topCoupons);
                        adapter1.notifyDataSetChanged();
                    }
                });

            } else {
                TabTicketCinemaCouponAdapter adapter2 = new TabTicketCinemaCouponAdapter(mContext, allCoupons);
                holder.couponIRecyclerView.setIAdapter(adapter2);
                holder.couponCountLayout.setVisibility(View.GONE);
            }
        } else {
            holder.couponIRecyclerView.setVisibility(View.GONE);
            holder.couponCountLayout.setVisibility(View.GONE);
        }

        // 收藏和去过影院_排片信息
        if ((bean.isFavorit() || bean.isBeen()) && null != bean.getShowtimeList() && bean.getShowtimeList().size() > 0) {
            List<CinemaShowtimeBean> showtimeBeans = bean.getShowtimeList();
            List<CinemaShowtimeBean> validBeans = getValidShowtimes(showtimeBeans);
            if (null != validBeans && validBeans.size() > 0) {
                holder.showtimeIRecyclerView.setVisibility(View.VISIBLE);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                holder.showtimeIRecyclerView.setLayoutManager(linearLayoutManager);
                final MovieShowtimeCinemaShowtimeAdapter showtimeAdapter = new MovieShowtimeCinemaShowtimeAdapter(mContext,
                        mDateString, bean, validBeans, mDirectSaleShowtimeListener);
                holder.showtimeIRecyclerView.setAdapter(showtimeAdapter);
            } else {
                holder.showtimeIRecyclerView.setVisibility(View.GONE);
            }
        } else {
            holder.showtimeIRecyclerView.setVisibility(View.GONE);
        }

        final String newDate = TextUtils.isEmpty(mDateString) ? "" : mDateString.replace("-", "");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //注意，这里的position不要用上面参数中的position，会出现位置错乱，用holder.getIAdapterPosition()

                // 埋点：影院列表页与影片排片页埋点定义相同
//                Map<String, String> businessParam = new HashMap<String, String>(3);
//                businessParam.put(StatisticConstant.CINEMA_ID, String.valueOf(bean.getCinemaId()));
//                businessParam.put(StatisticConstant.COLLECTION, bean.isFavorit() ? "1" : "0");
//                businessParam.put(StatisticConstant.LAST_TIME, bean.isBeen() ? "1" : "0");
//                StatisticPageBean statisticBean = mContext.assemble(StatisticTicket.TICKET_CINEMA_LIST, "",
//                        "cinema", String.valueOf(holder.getIAdapterPosition() + 1), "", "",
//                        businessParam);
//                StatisticManager.getInstance().submit(statisticBean);

                if (TextUtils.isEmpty(bean.getMovieId())) {
                    // 当前页：影院列表页
                    // 影院排片页
                    JumpUtil.startCinemaShowtimeActivity(mContext, null,
                            String.valueOf(bean.getCinemaId()), null, null, 0);
                } else {
                    // 当前页：影片排片页
                    // 影院排片页_定位到指定影片指定日期
                    JumpUtil.startCinemaShowtimeActivity(mContext, null,
                            String.valueOf(bean.getCinemaId()), bean.getMovieId(), newDate, 10);
                }
            }
        });

    }

    // 显示上次去过和特效
    private void showHolderBeenAndFeature(ViewHolder holder, CinemaBaseInfo bean) {
        CinemaResultFeatureBean feature = bean.getFeature();
        if (null == feature && !bean.isBeen()) {
            holder.featureLayout.setVisibility(View.GONE);
        } else {
            boolean showFeatureLayout = false;
            // 上次去过
            if (bean.isBeen()) {
                holder.beenTv.setVisibility(View.VISIBLE);
                showFeatureLayout = true;
            } else {
                holder.beenTv.setVisibility(View.GONE);
            }
            // 特效列表
            if (1 == bean.getFeature().getHas3D()) {
                holder.threeDTv.setVisibility(View.VISIBLE);
                showFeatureLayout = true;
            } else {
                holder.threeDTv.setVisibility(View.GONE);
            }
            if (1 == bean.getFeature().getHasIMAX()) {
                holder.imaxTv.setVisibility(View.VISIBLE);
                showFeatureLayout = true;
            } else {
                holder.imaxTv.setVisibility(View.GONE);
            }
            if (1 == bean.getFeature().getHasVIP()) {
                holder.vipTv.setVisibility(View.VISIBLE);
                showFeatureLayout = true;
            } else {
                holder.vipTv.setVisibility(View.GONE);
            }
            if (1 == bean.getFeature().getHasFeature4D()) {
                holder.fourDTv.setVisibility(View.VISIBLE);
                showFeatureLayout = true;
            } else {
                holder.fourDTv.setVisibility(View.GONE);
            }
            if (1 == bean.getFeature().getHasFeatureHuge()) {
                holder.hugeTv.setVisibility(View.VISIBLE);
                showFeatureLayout = true;
            } else {
                holder.hugeTv.setVisibility(View.GONE);
            }
            if (1 == bean.getFeature().getHasFeature4K()) {
                holder.fourKTv.setVisibility(View.VISIBLE);
                showFeatureLayout = true;
            } else {
                holder.fourKTv.setVisibility(View.GONE);
            }
            if (1 == bean.getFeature().getHasFeatureDolby()) {
                holder.dolbyTv.setVisibility(View.VISIBLE);
                showFeatureLayout = true;
            } else {
                holder.dolbyTv.setVisibility(View.GONE);
            }
            //TODO  新增SphereX，ScreenX
            if (1 == bean.getFeature().getHasSphereX()) {
                holder.sphereXTv.setVisibility(View.VISIBLE);
                showFeatureLayout = true;
            } else {
                holder.sphereXTv.setVisibility(View.GONE);
            }
            if (1 == bean.getFeature().getHasScreenX()) {
                holder.screenXTv.setVisibility(View.VISIBLE);
                showFeatureLayout = true;
            } else {
                holder.screenXTv.setVisibility(View.GONE);
            }
            if (1 == bean.getFeature().getHasLoveseat()) {
                holder.loveseatTv.setVisibility(View.VISIBLE);
                showFeatureLayout = true;
            } else {
                holder.loveseatTv.setVisibility(View.GONE);
            }
            if (1 == bean.getFeature().getHasPark()) {
                holder.parkTv.setVisibility(View.VISIBLE);
                showFeatureLayout = true;
            } else {
                holder.parkTv.setVisibility(View.GONE);
            }
            holder.featureLayout.setVisibility(showFeatureLayout ? View.VISIBLE : View.GONE);
        }
    }

    // 显示距离
    private String getDistance(double distance) {
        String value;

        if (distance < 1) {
            value = "";
        } else if (distance < 500) {
            value = "<500m";
        } else if (distance < 1000) {
            value = String.format("%dm", (int) distance);
        } else if (distance <= 20000) {
            value = String.format("%.1fkm", (float) (distance / 1000));
        } else {
            value = ">20km";
        }

        return value;
    }

    // 获取未过期的排片列表
    private List<CinemaShowtimeBean> getValidShowtimes(List<CinemaShowtimeBean> showtimeBeans) {
        List<CinemaShowtimeBean> validBeans = new ArrayList<>();
        CinemaShowtimeBean bean;
        for (int i = 0, size = showtimeBeans.size(); i < size; i++) {
            // TODO: 2017/12/4 是否需要加偏移量？
            bean = showtimeBeans.get(i);
            if (bean.getShowDay() * 1000 >= System.currentTimeMillis()) {
                validBeans.add(bean);
            }
        }
        return validBeans;
    }

    public class ViewHolder extends IViewHolder {

        ImageView favIv;
        TextView nameTv;
        TextView directSaleTv;
        TextView priceTv;
        TextView priceUnitTv;
        TextView addressTv;
        TextView distanceTv;
        View featureLayout;
        TextView threeDTv;
        TextView imaxTv;
        TextView parkTv;
        TextView vipTv;
        TextView fourDTv;
        TextView hugeTv;
        TextView fourKTv;
        TextView dolbyTv;
        TextView sphereXTv;
        TextView screenXTv;
        TextView loveseatTv;
        TextView beenTv;
        IRecyclerView couponIRecyclerView;
        View couponCountLayout;
        TextView couponCountTv;
        ImageView couponArrowIv;
        RecyclerView showtimeIRecyclerView;

        public ViewHolder(View itemView) {
            super(itemView);
            favIv = itemView.findViewById(R.id.adapter_cinema_list_item_fav_iv);
            nameTv = itemView.findViewById(R.id.adapter_cinema_list_item_name_tv);
            directSaleTv = itemView.findViewById(R.id.adapter_cinema_list_item_direct_sale_tv);
            priceTv = itemView.findViewById(R.id.adapter_cinema_list_item_price_tv);
            priceUnitTv = itemView.findViewById(R.id.adapter_cinema_list_item_price_unit_tv);
            addressTv = itemView.findViewById(R.id.adapter_cinema_list_item_address_tv);
            distanceTv = itemView.findViewById(R.id.adapter_cinema_list_item_distance_tv);
            featureLayout = itemView.findViewById(R.id.layout_cinema_list_item_feature_ll);
            threeDTv = itemView.findViewById(R.id.layout_cinema_list_item_feature_3d_tv);
            imaxTv = itemView.findViewById(R.id.layout_cinema_list_item_feature_imax_tv);
            parkTv = itemView.findViewById(R.id.layout_cinema_list_item_feature_parking_tv);
            vipTv = itemView.findViewById(R.id.layout_cinema_list_item_feature_vip_tv);
            fourDTv = itemView.findViewById(R.id.layout_cinema_list_item_feature_4d_tv);
            hugeTv = itemView.findViewById(R.id.layout_cinema_list_item_feature_huge_tv);
            fourKTv = itemView.findViewById(R.id.layout_cinema_list_item_feature_4k_tv);
            dolbyTv = itemView.findViewById(R.id.layout_cinema_list_item_feature_dolby_tv);
            sphereXTv = itemView.findViewById(R.id.layout_cinema_list_item_feature_spherex_tv);
            screenXTv = itemView.findViewById(R.id.layout_cinema_list_item_feature_screenx_tv);
            loveseatTv = itemView.findViewById(R.id.layout_cinema_list_item_feature_loveseat_tv);
            beenTv = itemView.findViewById(R.id.layout_cinema_list_item_feature_been_tv);
            couponIRecyclerView = itemView.findViewById(R.id.adapter_cinema_list_item_coupon_irecyclerview);
            couponCountLayout = itemView.findViewById(R.id.adapter_cinema_list_item_coupon_count_rl);
            couponCountTv = itemView.findViewById(R.id.adapter_cinema_list_item_coupon_count_tv);
            couponArrowIv = itemView.findViewById(R.id.adapter_cinema_list_item_coupon_arrow_iv);
            showtimeIRecyclerView = itemView.findViewById(R.id.adapter_cinema_list_item_showtime_irecyclerview);
        }
    }
}
