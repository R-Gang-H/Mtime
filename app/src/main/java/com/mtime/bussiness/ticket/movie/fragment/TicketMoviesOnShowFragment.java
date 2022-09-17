package com.mtime.bussiness.ticket.movie.fragment;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.OnRefreshListener;
import com.kotlin.android.router.ext.ProviderExtKt;

import com.kotlin.android.app.router.provider.mine.IMineProvider;
import com.kotlin.android.user.UserManager;
import com.kotlin.android.user.login.UserLoginKt;
import com.mtime.R;
import com.mtime.applink.ApplinkManager;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.ticket.TicketFragment;
import com.mtime.bussiness.ticket.movie.adapter.Main_moviePlayingList_Adapter;
import com.mtime.bussiness.ticket.movie.bean.CinemaMovieJsonBean;
import com.mtime.bussiness.ticket.movie.bean.MovieAdBean;
import com.mtime.bussiness.ticket.movie.bean.MovieBean;
import com.mtime.event.entity.PosterFilterEvent;
import com.mtime.frame.BaseFragment;
import com.mtime.network.ConstantUrl;
import com.mtime.network.RequestCallback;
import com.mtime.statistic.large.ticket.StatisticTicket;
import com.mtime.util.HttpUtil;
import com.mtime.util.UIUtil;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * 购票tab 电影（正在热映）
 */
public class TicketMoviesOnShowFragment extends BaseFragment implements View.OnClickListener {
    private TextView couponNum;
    private LinearLayout promoLayout;
    private TextView iconTextTv;
    private TextView titleTv;
    private Main_moviePlayingList_Adapter moviePlaying_Adapter;
    private IRecyclerView lvPlaying;
    private View layout_failed_holder1;
    private String cityId;
    private RequestCallback movieCallback;
    private boolean loadingFailedHotMovie = false;
    private String voucherMsg;
    private boolean hasPromo;
    private MovieAdBean promoBean;
    private List<MovieBean> listMovieData = new ArrayList<>();
    private TextView tv_failed;
    private String strClickkview;
    private static final String HOTPLAY_TOP_AD = "正在热映顶部文字链广告";


    public void setCityId(final String cityId) {
        this.cityId = cityId;
    }

    @Override
    protected View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ticket_moviesonshow_view, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBaseStatisticHelper.setPageLabel(StatisticTicket.PN_ON_SHOW_LIST);
        mBaseStatisticHelper.setSubmit(true);
    }

    @Override
    protected void onInitView(Bundle savedInstanceState) {
        layout_failed_holder1 = getView().findViewById(R.id.loading_failed_layout1);
        tv_failed = getView().findViewById(R.id.retryErrorTv);

        couponNum = getView().findViewById(R.id.coupon_num);
        couponNum.setOnClickListener(this);
        couponNum.setVisibility(View.GONE);

        promoLayout = getView().findViewById(R.id.ad_linear);
        promoLayout.setOnClickListener(this);
        promoLayout.setVisibility(View.GONE);
        iconTextTv = getView().findViewById(R.id.ad_icon_tv);
        titleTv = getView().findViewById(R.id.ad_txt_tv);

        moviePlaying_Adapter = new Main_moviePlayingList_Adapter(context, new ArrayList<MovieBean>());

        lvPlaying = getView().findViewById(R.id.list_movie_hot);
        lvPlaying.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        lvPlaying.setLayoutManager(linearLayoutManager1);
        lvPlaying.setIAdapter(moviePlaying_Adapter);
        lvPlaying.setRefreshEnabled(true);
        lvPlaying.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 刷新购票tab顶部banner
                refreshBanner();
                onLoadData();
            }
        });
    }

    @Override
    protected void onInitEvent() {
        movieCallback = new RequestCallback() {
            @Override
            public void onSuccess(final Object o) {
                UIUtil.dismissLoadingDialog();
                loadingFailedHotMovie = false;
                lvPlaying.setRefreshing(false);
                lvPlaying.setVisibility(View.VISIBLE);
                CinemaMovieJsonBean object = (CinemaMovieJsonBean) o;
                voucherMsg = object.getVoucherMsg();
                hasPromo = object.isHasPromo();

                showVoucherMsg();
                if (hasPromo) {
                    promoBean = object.getPromo();
                    showPromo();
                }

                if (listMovieData != null) {
                    listMovieData.clear();
                }
                listMovieData = object.getMs();
                moviePlaying_Adapter.clear();
                moviePlaying_Adapter.addAll(listMovieData);
                if (null == listMovieData || listMovieData.isEmpty()) {
                    if(((TicketFragment)getParentFragment()).getCurrentItem() == 0) {
                        // 购票首页当前是热映tab才弹提示
                        MToastUtils.showShortToast(getString(R.string.ticket_tab_hot_show_empty));
                    }
                }
            }

            @Override
            public void onFail(Exception e) {
                loadingFailedHotMovie = true;
                UIUtil.dismissLoadingDialog();
                lvPlaying.setRefreshing(false);
                lvPlaying.setVisibility(View.GONE);
                UIUtil.showLoadingFailedLayout(layout_failed_holder1, tv_failed, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 刷新购票tab顶部banner
                        refreshBanner();
                        // 重新加载数据
                        reLoadData();
                    }
                });

            }
        };

    }

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (getUserVisibleHint()) {
//            onLoadData();
//        }
//
//    }

    @Override
    protected void onRequestData() {
//        onLoadData();
    }

    @Override
    protected void onErrorRetry() {
        // 刷新购票tab顶部banner
        refreshBanner();
        onLoadData();
    }

    /**
     * 热映列表
     */
    public void onLoadData() {
        if(layout_failed_holder1.getVisibility() == View.VISIBLE) {
            layout_failed_holder1.setVisibility(View.GONE);
        }
        if(((TicketFragment)getParentFragment()).getCurrentItem() == 0) {
            UIUtil.showLoadingDialog(context);
        }
        Map<String, String> param = new HashMap<>(1);
        param.put("locationId", cityId);
        HttpUtil.get(ConstantUrl.GET_CITY_CINEMA_MOVIES, param, CinemaMovieJsonBean.class, movieCallback);
    }

    /**
     * 刷新购票tab顶部banner
     */
    private void refreshBanner() {
        ((TicketFragment)getParentFragment()).requestBanner();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.coupon_num:
                if (UserManager.Companion.getInstance().isLogin()) {
                    //2020/11/3 需要跳转到新的我的钱包界面
                    //context.startActivity(MyVoucherListActivity.class);
                    ((IMineProvider) ProviderExtKt.getProvider(IMineProvider.class)).startMyWalletActivity(null, null);

                } else {
                    // 跳转到登录页面
//                    context.startActivity(LoginActivity.class);
                    UserLoginKt.gotoLoginPage(context, null, null);
                }
                break;
            case R.id.ad_linear:
                if (promoBean == null) {
                    return;
                }
//                context.setPageLabel(StatisticTicket.PN_ON_SHOW_LIST);
//                StatisticPageBean bean = context.assemble(StatisticTicket.TICKET_ADLINK, null, null, null, null, null, null);
//                StatisticManager.getInstance().submit(bean);
                ApplinkManager.jump(context, promoBean.applink, null);
                break;
            default:
                break;
        }

    }

    private void showVoucherMsg() {
        if (!TextUtils.isEmpty(voucherMsg)) {
            if (TextUtils.isEmpty(strClickkview)) {
                strClickkview = context.getResources().getString(R.string.st_click_view);
            }
            String newVoucherMsg = voucherMsg + strClickkview;
            if (!couponNum.getText().equals(newVoucherMsg)) {
                SpannableStringBuilder spannable = new SpannableStringBuilder(newVoucherMsg);
                spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_ff8600)), newVoucherMsg.indexOf(strClickkview), newVoucherMsg.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                couponNum.setText(spannable);
            }
            couponNum.setVisibility(View.VISIBLE);
        } else {
            couponNum.setVisibility(View.GONE);
        }
    }

    private void showPromo() {
        if (promoBean != null) {
            promoLayout.setVisibility(View.VISIBLE);
            if (!promoBean.getIconText().equals("")) {
                iconTextTv.setText(promoBean.getIconText());
                iconTextTv.setVisibility(View.VISIBLE);
            } else {
                iconTextTv.setVisibility(View.GONE);
            }
            titleTv.setText(promoBean.getTitle());
        } else {
            promoLayout.setVisibility(View.GONE);
        }
    }

    private void reLoadData() {
        lvPlaying.setVisibility(View.VISIBLE);
        if (loadingFailedHotMovie) {
            onLoadData();
        } else if (null == listMovieData) {
            onLoadData();
        }
        showVoucherMsg();
    }

    /**
     * 判断城市是否变更
     */
    public boolean needRequest(final String cityId) {
        if (null == this.cityId || !this.cityId.equalsIgnoreCase(cityId)) {
            this.cityId = cityId;
            return true;
        }

        return false;
    }

    /**
     * 返回接口数据
     */
    public List<MovieBean> getListMovieData() {
        return listMovieData;
    }
    @Override
    protected boolean openEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPosterFilter(PosterFilterEvent event) {
        if (null != moviePlaying_Adapter) {
            moviePlaying_Adapter.notifyDataSetChanged();
        }
    }
}
