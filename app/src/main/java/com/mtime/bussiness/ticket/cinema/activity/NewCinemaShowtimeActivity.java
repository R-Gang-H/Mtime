package com.mtime.bussiness.ticket.cinema.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.kk.taurus.uiframe.v.BaseStateContainer;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.kk.taurus.uiframe.v.NoTitleBarContainer;
import com.kotlin.android.app.data.entity.cinema.RcmdTicketShowtime;
import com.kotlin.android.app.router.path.RouterActivityPath;
import com.kotlin.android.user.login.UserLoginKt;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.statistic.StatisticConstant;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.mine.login.activity.BindPhoneWithLoginActivity;
import com.mtime.bussiness.ticket.api.TicketApi;
import com.mtime.bussiness.ticket.cinema.bean.CinemaShowtimeUPHalfBean;
import com.mtime.bussiness.ticket.cinema.bean.CinemaShowtimeUPHalfCinemaBean;
import com.mtime.bussiness.ticket.cinema.bean.GeniueSurroundingBean;
import com.mtime.bussiness.ticket.cinema.holder.CinemaShowtimeContentHolder;
import com.mtime.bussiness.ticket.movie.bean.DirectSellingOrderPrepareBean;
import com.mtime.constant.Constants;
import com.mtime.frame.App;
import com.mtime.frame.BaseFrameUIActivity;
import com.mtime.network.ConstantUrl;
import com.mtime.network.RequestCallback;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.statistic.large.h5.StatisticH5;
import com.mtime.statistic.large.ticket.StatisticTicket;
import com.mtime.util.HttpUtil;
import com.mtime.util.JumpUtil;
import com.mtime.util.SaveCinemaIds;
import com.mtime.util.UIUtil;

import java.util.HashMap;
import java.util.Map;

import static com.mtime.bussiness.ticket.movie.bean.DirectSellingOrderPrepareBean.BIZ_CODE_SUCCESS;

/**
 * Created by zhuqiguang on 2018/5/22.
 * website www.zhuqiguang.cn
 * 影院排片页
 */
@Route(path = RouterActivityPath.Ticket.PAGE_CINEMA_SHOWTIME)
public class NewCinemaShowtimeActivity extends BaseFrameUIActivity<GeniueSurroundingBean, CinemaShowtimeContentHolder> implements CinemaShowtimeContentHolder.OnShowtimeListener {
    public static final String KEY_MOVIE_SHOWTIME_DATE = "key_movie_showtime_date";
    public static final String KEY_CINEMA_ID = "cinema_id";
    public static final String KEY_MOVIE_ID = "movie_id";
    private String mCinemaId;
    private String showDateString;
    private CinemaShowtimeUPHalfBean upHalfBean;
    private String mMovieId;
    private TicketApi mTicketApi;

    @Override
    protected BaseStateContainer getStateContainer() {
        return new NoTitleBarContainer(this, this);
    }

    public static void launch(Context context, String refer, String cinemaId, String movieId, String movieShowTimeDate, int requestCode) {
        Intent intent = new Intent(context, NewCinemaShowtimeActivity.class);
        if (!TextUtils.isEmpty(cinemaId))
            intent.putExtra(KEY_CINEMA_ID, cinemaId);
        if (!TextUtils.isEmpty(movieId))
            intent.putExtra(KEY_MOVIE_ID, movieId);
        if (!TextUtils.isEmpty(movieShowTimeDate))
            intent.putExtra(KEY_MOVIE_SHOWTIME_DATE, movieShowTimeDate);
        dealRefer(context, refer, intent);
        startActivity(context, intent, requestCode);
    }

    @Override
    protected void onInit(Bundle savedInstanceState) {
        super.onInit(savedInstanceState);
        this.setResult(0); // 让AdvRecommendActivity的提醒链能往后走
        // 添加本地浏览记录
        if (mCinemaId != null) {
            SaveCinemaIds.getInstance().add(mCinemaId);
        }
        setPageLabel(StatisticTicket.PN_CINEMA_TIME);
        putBaseStatisticParam(StatisticConstant.CINEMA_ID, String.valueOf(mCinemaId));
        mTicketApi = new TicketApi();
        getData();
    }

    private void getData() {
        UIUtil.showLoadingDialog(this);
        // Showtime/ShowtimeMovieAndDateListByCinema.api?cinemaId={0}";
        Map<String, String> param = new HashMap<>(1);
        param.put("cinemaId", mCinemaId);
        HttpUtil.get(ConstantUrl.CINEMA_MOVIES, param, CinemaShowtimeUPHalfBean.class, getCallback());
        // 公告推荐位
        getRcmdNotice();
        // 活动推荐位
        getRcmdActivity();
    }

    /**
     * 公告推荐位
     */
    private void getRcmdNotice() {
        mTicketApi.getRcmdTicketShowtimeNotice(new NetworkManager.NetworkListener<RcmdTicketShowtime>() {
            @Override
            public void onSuccess(RcmdTicketShowtime result, String showMsg) {
                String appCopywriting = "";
                String h5Link = "";
                if(result != null) {
                    appCopywriting = result.getAppCopywriting();
                    h5Link = result.getH5Link();
                }
                getUserContentHolder().updateRcmdNotice(appCopywriting, h5Link);
            }

            @Override
            public void onFailure(NetworkException<RcmdTicketShowtime> exception, String showMsg) {
                getUserContentHolder().updateRcmdNotice("", "");
            }
        });
    }

    /**
     * 活动推荐位
     */
    private void getRcmdActivity() {
        mTicketApi.getRcmdTicketShowtimeActivity(new NetworkManager.NetworkListener<RcmdTicketShowtime>() {
            @Override
            public void onSuccess(RcmdTicketShowtime result, String showMsg) {
                String appCopywriting = "";
                String h5Link = "";
                if(result != null) {
                    appCopywriting = result.getAppCopywriting();
                    h5Link = result.getH5Link();
                }
                getUserContentHolder().updateRcmdActivity(appCopywriting, h5Link);
            }

            @Override
            public void onFailure(NetworkException<RcmdTicketShowtime> exception, String showMsg) {
                getUserContentHolder().updateRcmdNotice("", "");
            }
        });
    }

    @Override
    public ContentHolder onBindContentHolder() {
        mCinemaId = getIntent().getStringExtra(KEY_CINEMA_ID);
        mMovieId = getIntent().getStringExtra(KEY_MOVIE_ID);
        showDateString = getIntent().getStringExtra(KEY_MOVIE_SHOWTIME_DATE);
        if (showDateString != null && !"".equals(showDateString)
                && showDateString.length() == 8) {
            StringBuffer sb = new StringBuffer(showDateString);
            showDateString = sb.insert(4, "-").insert(7, "-").toString();
        }
        return new CinemaShowtimeContentHolder(this, getSupportFragmentManager(), mCinemaId, mMovieId,
                showDateString, this);
    }

    private RequestCallback getCallback() {
        RequestCallback uphalfCallback = new RequestCallback() {

            @Override
            public void onSuccess(Object o) {
                upHalfBean = (CinemaShowtimeUPHalfBean) o;
                if (upHalfBean != null) {
                    final CinemaShowtimeUPHalfCinemaBean upHalfCinemaBean = upHalfBean.getCinema();
                    if (upHalfCinemaBean != null) {
                        getUserContentHolder().updateCinemaShowtime(upHalfBean);
                    }
                }
                UIUtil.dismissLoadingDialog();
            }

            @Override
            public void onFail(Exception e) {
                UIUtil.dismissLoadingDialog();
                UIUtil.showLoadingFailedLayout(NewCinemaShowtimeActivity.this, v -> getData());
            }
        };
        return uphalfCallback;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("cinemaId", TextUtils.isEmpty(mCinemaId) ? "" : mCinemaId);
        intent.putExtra("isFavoriate", getUserContentHolder().getFavoriate());
        setResult(CinemaShowtimeContentHolder.RESULT_CODE, intent);
        finish();
    }

    @Override
    public void onHolderEvent(int eventCode, Bundle bundle) {
        super.onHolderEvent(eventCode, bundle);
        switch (eventCode) {
            case CinemaShowtimeContentHolder.EVENT_CODE_START:
                break;
            case CinemaShowtimeContentHolder.EVENT_CODE_ITEM_CHANGED:
                break;
        }
    }

    @Override
    public void setResultForActivity(int resultCode, Intent intent) {
        setResult(resultCode, intent);
    }

    @Override
    public void gotoLogin(int directSalesFlag) {
//        Intent intent = new Intent(this, LoginActivity.class);
//        intent.putExtra(App.getInstance().KEY_SHOW_NOT_VIP, directSalesFlag == CinemaShowtimeUPHalfCinemaBean.MTIME_SALES_FALG);
//        startActivity(intent);
        Bundle bundle = new Bundle();
        bundle.putBoolean(App.getInstance().KEY_SHOW_NOT_VIP, directSalesFlag == CinemaShowtimeUPHalfCinemaBean.MTIME_SALES_FALG);
        UserLoginKt.gotoLoginPage(this, bundle, null);
    }

    @Override
    public void gotoBindPhone(int directSalesFlag) {
        Intent intent = new Intent(this, BindPhoneWithLoginActivity.class);
        intent.putExtra(Constants.BIND_SKIP_STATUS, directSalesFlag == CinemaShowtimeUPHalfCinemaBean.MTIME_SALES_FALG);
        startActivity(intent);
    }

    @Override
    public void gotoCinemaDetail() {
        Map<String, String> businessParam = new HashMap<>();
        businessParam.put(StatisticConstant.CINEMA_ID, mCinemaId);
        StatisticPageBean bean = assemble(StatisticTicket.TICKET_CINEMA, null, null, null, null, null, businessParam);
        StatisticManager.getInstance().submit(bean);
        JumpUtil.startCinemaViewActivity(this, bean.toString(), mCinemaId);
    }

    @Override
    public void gotoSeatSelect(long dsPlatformId, String govCinemaId, int cinemaId, String dsShowtimeId) {
        UIUtil.showLoadingDialog(this);
        mTicketApi.getDirectSaleTicketDetail(dsPlatformId, govCinemaId, String.valueOf(cinemaId), dsShowtimeId, new NetworkManager.NetworkListener<DirectSellingOrderPrepareBean>() {
            @Override
            public void onSuccess(DirectSellingOrderPrepareBean bean, String s) {
                UIUtil.dismissLoadingDialog();
                if (bean == null) return;
                if (TextUtils.equals(bean.getBizCode(), BIZ_CODE_SUCCESS)) {
//                    JumpUtil.startCooperationSeatSelectActivity(NewCinemaShowtimeActivity.this, bean.getJumpUrl());
                    JumpUtil.startCommonWebActivity(NewCinemaShowtimeActivity.this, bean.getJumpUrl(), StatisticH5.PN_H5, null,
                            true, false, true, false, false, assemble().toString());
                }
                MToastUtils.showShortToast(bean.getBizMsg());
            }

            @Override
            public void onFailure(NetworkException<DirectSellingOrderPrepareBean> networkException, String msg) {
                UIUtil.dismissLoadingDialog();
                MToastUtils.showShortToast(msg);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTicketApi.cancel();
    }
}
