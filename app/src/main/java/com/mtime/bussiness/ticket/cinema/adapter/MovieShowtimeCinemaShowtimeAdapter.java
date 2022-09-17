package com.mtime.bussiness.ticket.cinema.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aspsine.irecyclerview.IViewHolder;
import com.kotlin.android.film.JavaOpenSeatActivity;
import com.kotlin.android.ktx.ext.time.TimeExt;
import com.kotlin.android.user.UserManager;
import com.kotlin.android.user.login.UserLoginKt;
import com.mtime.R;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.bussiness.mine.login.activity.BindPhoneWithLoginActivity;
import com.mtime.bussiness.ticket.bean.Provider;
import com.mtime.bussiness.ticket.cinema.bean.CinemaBaseInfo;
import com.mtime.bussiness.ticket.cinema.bean.CinemaShowtimeBean;
import com.mtime.bussiness.ticket.cinema.util.CinemaListHelper;
import com.mtime.common.utils.DateUtil;
import com.mtime.constant.Constants;
import com.mtime.util.JumpUtil;
import com.mtime.util.MtimeUtils;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by vivian.wei on 2017/12/4.
 * 影片排片页_“我的收藏”、“上次去的”的影院有余票的场次排期
 */

public class MovieShowtimeCinemaShowtimeAdapter extends RecyclerView.Adapter<MovieShowtimeCinemaShowtimeAdapter.ViewHolder> {

    private static final String BAIDU_FROM = "影片排片页";

    private final Context mContext;
    private List<CinemaShowtimeBean> mList;
    private final String mDateString;
    private final CinemaBaseInfo mCinemaBean;
    private final OnDirectSaleShowtimeListener mDSListener;

    public MovieShowtimeCinemaShowtimeAdapter(final Context context, final String dateString,
                                              final CinemaBaseInfo cinemaBean, final List<CinemaShowtimeBean> list,
                                              final OnDirectSaleShowtimeListener dSListener) {
        mContext = context;
        mList = list;
        mDateString = dateString;
        mCinemaBean = cinemaBean;
        mDSListener = dSListener;
    }

    public void setList(final List<CinemaShowtimeBean> list) {
        mList = list;
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_movie_showtime_cinema_showtime_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final CinemaShowtimeBean bean = mList.get(position);

        if (position == getItemCount() - 1) {
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            params.rightMargin = MScreenUtils.dp2px(10);
            holder.itemView.setLayoutParams(params);
        }
        String date = TimeExt.INSTANCE.millis2String(bean.getShowDay(), DateUtil.sdf2);
        final boolean isTomorrow = CinemaListHelper.isTomorrow((bean.getShowDay()), mDateString);
        if (isTomorrow) {
            holder.timeTv.setText(String.format("%s次日", date));
            holder.timeTv.setTextSize(12);
        } else {
            holder.timeTv.setText(date);
            holder.timeTv.setTextSize(15);
        }

        StringBuilder versionSb = new StringBuilder();
        versionSb.append(bean.getLanguage()).append(" ").append(bean.getVersionDesc());
        String version = versionSb.toString();
        holder.versionTv.setText(version.trim());

        if (bean.isTicket()) {
            holder.priceTv.setVisibility(View.VISIBLE);
            StringBuilder sb = new StringBuilder();
            sb.append(mContext.getResources().getString(R.string.str_money)).append(" ").append(MtimeUtils.formatPrice(String.valueOf((float) bean.getSalePrice() / 100.0)));
            holder.priceTv.setText(sb.toString());
        } else if (TextUtils.isEmpty(bean.getPrice()) || "0".equals(bean.getPrice())) {
            holder.priceTv.setVisibility(View.GONE);
        } else {
            holder.priceTv.setVisibility(View.VISIBLE);
            StringBuilder sb = new StringBuilder();
            sb.append(mContext.getResources().getString(R.string.str_money)).append(" ").append(MtimeUtils.formatPrice(bean.getPrice()));
            holder.priceTv.setText(sb.toString());
        }

        holder.itemView.setOnClickListener(v -> {
            List<Provider> providers = bean.getProviders();
            if (!bean.isVaildTicket() || CollectionUtils.isEmpty(providers)) {
                return;
            }

            Provider provider = providers.get(0);
            if (provider.getDirectSalesFlag() == Constants.DIRECT_SALES_FLAG) {
                // 第三方直销平台
                if (!UserManager.Companion.getInstance().isLogin()) {
                    // 登录页
                    /*Intent intent = new Intent(mContext, LoginActivity.class);
                    intent.putExtra(App.getInstance().KEY_SHOW_NOT_VIP, false);
                    mContext.startActivity(LoginActivity.class, intent);*/
                    JumpUtil.startLoginActivity(mContext, null);
                } else if (!UserManager.Companion.getInstance().getHasBindMobile()) {
                    // 手机绑定页
                    Intent intent = new Intent(mContext, BindPhoneWithLoginActivity.class);
                    intent.putExtra(Constants.BIND_SKIP_STATUS, false);
                    mContext.startActivity(intent);
                } else if (null != mDSListener) {
                    // webview打开合作影院的H5选座页
                    mDSListener.gotoSeatSelect(mCinemaBean.getDsPlatformId(), mCinemaBean.getGovCinemaId(), mCinemaBean.getCinemaId(), provider.getDsShowtimeId());
                }
            } else {
                // 时光平台
                String dId = String.valueOf(provider.getdId()); // 取第一个provider的dId即可（服务端的说明）
                // yyyyMMdd
                String passDateString = DateUtil.getLongToDate(DateUtil.sdf9, bean.getShowDay());
                // 原生选座页
//                JumpUtil.startSeatSelectActivity(mContext, "", dId, String.valueOf(mCinemaBean.getCinemaId()),
//                        mCinemaBean.getMovieId(), passDateString, BAIDU_FROM);

                JavaOpenSeatActivity.INSTANCE.openSeatActivity(dId, null, mCinemaBean.getMovieId(), String.valueOf(mCinemaBean.getCinemaId()), passDateString);
            }
        });
    }

    public class ViewHolder extends IViewHolder {

        TextView timeTv;
        TextView versionTv;
        TextView priceTv;

        public ViewHolder(View itemView) {
            super(itemView);
            timeTv = itemView.findViewById(R.id.adapter_movie_showtime_cinema_showtime_item_time_tv);
            versionTv = itemView.findViewById(R.id.adapter_movie_showtime_cinema_showtime_item_version_tv);
            priceTv = itemView.findViewById(R.id.adapter_movie_showtime_cinema_showtime_item_price_tv);
        }
    }

    public interface OnDirectSaleShowtimeListener {
        void gotoSeatSelect(long dsPlatformId, String govCinemaId, int cinemaId, String dsShowtimeId);
    }

}
