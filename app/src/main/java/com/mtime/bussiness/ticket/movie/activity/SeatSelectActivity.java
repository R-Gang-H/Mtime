package com.mtime.bussiness.ticket.movie.activity;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TreeMap;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.kotlin.android.ktx.ext.time.TimeExt;
import com.kotlin.android.app.router.path.RouterActivityPath;
import com.kotlin.android.user.UserManager;
import com.kotlin.android.user.login.UserLoginKt;
import com.mtime.R;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.base.utils.DispatchAsync;
import com.mtime.base.utils.MTimeUtils;
import com.mtime.base.utils.MToastUtils;
import com.mtime.beans.SuccessBean;
import com.mtime.bussiness.ticket.api.TicketApi;
import com.mtime.bussiness.ticket.bean.Provider;
import com.mtime.bussiness.ticket.movie.SeatManager;
import com.mtime.bussiness.ticket.movie.bean.AutoSeatsBean;
import com.mtime.bussiness.ticket.movie.bean.CancelOrderJsonBean;
import com.mtime.bussiness.ticket.movie.bean.CinemaJsonBean;
import com.mtime.bussiness.ticket.movie.bean.CreateOrderJsonBean;
import com.mtime.bussiness.ticket.movie.bean.GetPayListBean;
import com.mtime.bussiness.ticket.movie.bean.OnlineSeatsStatusBean;
import com.mtime.bussiness.ticket.movie.bean.OnlineSeatsStatusRowSeatBean;
import com.mtime.bussiness.ticket.movie.bean.OrderStatusJsonBean;
import com.mtime.bussiness.ticket.movie.bean.PayCardListBean;
import com.mtime.bussiness.ticket.movie.bean.SeatColl;
import com.mtime.bussiness.ticket.movie.bean.SeatInfo;
import com.mtime.bussiness.ticket.movie.bean.SeatInfoJsonBean;
import com.mtime.bussiness.ticket.movie.bean.SeatInfoUIBean;
import com.mtime.bussiness.ticket.movie.bean.SeatsIconUseData;
import com.mtime.bussiness.ticket.movie.bean.ShowTimeUIBean;
import com.mtime.bussiness.ticket.movie.bean.SubOrderStatusJsonBean;
import com.mtime.bussiness.ticket.movie.bean.TicketDetailBean;
import com.mtime.bussiness.ticket.movie.bean.WithoutPayOnlineSeat;
import com.mtime.bussiness.ticket.movie.widget.SeatSelectChangeDialog;
import com.mtime.bussiness.ticket.movie.widget.SeatSelectDataView;
import com.mtime.bussiness.ticket.movie.widget.SeatSelectView;
import com.mtime.bussiness.ticket.movie.widget.SeatThumView;
import com.mtime.common.utils.LogWriter;
import com.mtime.common.utils.TextUtil;
import com.mtime.common.utils.Utils;
import com.mtime.frame.App;
import com.mtime.frame.BaseActivity;
import com.mtime.mtmovie.widgets.ISeatSelectInterface;
import com.mtime.network.ConstantUrl;
import com.mtime.network.RequestCallback;
import com.mtime.payment.dialog.OrderPayTicketOutingDialog;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.statistic.large.ticket.StatisticTicket;
import com.mtime.util.CustomAlertDlg;
import com.mtime.util.HttpUtil;
import com.mtime.util.JumpUtil;
import com.mtime.util.MtimeUtils;
import com.mtime.util.TipsDlg;
import com.mtime.util.UIUtil;
import com.mtime.widgets.BaseTitleView.StructType;
import com.mtime.widgets.TitleOfNormalView;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.collection.ArrayMap;

/**
 * ?????????
 */
@SuppressLint({"UseSparseArrays", "SimpleDateFormat"})
@Route(path = RouterActivityPath.Ticket.PAGE_SEAT_SELECT)
public class SeatSelectActivity extends BaseActivity implements OnClickListener {
    private static final int PAY_TICKET_COUNT = 4;                                  // ????????????
    private SeatSelectDataView mDataView;
    // ????????????????????????
    // 20191015?????????????????????false?????????????????????????????????????????????????????????????????????????????????????????????api?????????????????????api????????????
    // ??????http://wiki.inc-mtime.com/pages/viewpage.action?pageId=232816649
    private boolean mIsSeatSelectAgain = false;                              // ?????????????????????
    private String mReSelectSeatLastOrderId;                                 // ??????????????????id??????????????????????????????????????????id???
    private int mCanSelectSeatCount = SeatSelectActivity.PAY_TICKET_COUNT; // ?????????????????????????????????4??????????????????????????????????????????????????????
    private String mReselectSeatNewOrderId;                                  // ?????????????????????????????????id
    private String mWithoutPayOrderId;                                       // ????????????????????????id
    private String mSubOrderId;
    private String mUserBuyTicketPhone;
    private String mCinemaId;
    private String mDate;
    private String mMovieId;
    public String mDId;
    private String mSeatSelectedInfo;
    private boolean hasOrderCanceled = false;                              // ?????????????????????
    // ????????????????????????????????????
    private long mPayEndTime;                                              // ??????????????????
    private String mSeatId;

    private TextView tv_cinema_time;
    private TextView tv_cinema_name;
    private TextView tv_screen;
    private RelativeLayout ll_price_info;                                            // ?????????????????????????????????
    private TextView tv_seat_limit;
    private TextView btn_next_step;
    private TextView salePrice;                                                // ??????????????????
    // ??????
    private TextView tv_service_fee;                                           // ??????????????????

    protected double mServiceFee;                                              // ?????????
    private LinearLayout seat_result_container;                                    // ??????????????????textview?????????
    private final Map<String, Integer> ticketMap = new TreeMap<String, Integer>();     // ???????????????????????????
    private final Map<Integer, Button> btnMap = new HashMap<Integer, Button>();     // ??????????????????

    private LayoutInflater flater;
    private boolean isOrderRun = true;                               // ???????????????????????????
    private boolean isZoomin = true;                               // ?????????????????????true????????????false????????????
    private double mTotalPrice;                                              // ?????????????????????????????????
    private String mTicketDateInfo;                                          // ????????????????????????

    // X???X?????????X???X???X???
    // ????????????????????????3D,??????,120??????
    private int numberOfTimes = 1;
    private static final int MAX_POLLING_TIME = 30;                                 // ??????????????????
    private int mPoolingCounter = 1;                                  // ?????????????????????
    private static final int POLLING_MAX_TIME = 30;                                 // ??????????????????
    private static final int POLLING_SLEEP_TIME = 1000;                               // ????????????????????????????????????????????????
    private Timer mPoolingMainOrderTimer;                                   // ????????????????????????????????????
    private ImageButton btn_zoom;                                                 // ??????????????????
    private TextView btn_change;
    private int subOrderStatus;
    private SeatInfoUIBean mSeatInfo;

    private boolean isFirstChangeScene = true;                               // ????????????????????????????????????

    private ArrayList<LinearLayout> seatResultLayouts = null;
    private OrderPayTicketOutingDialog ticketOutingDialog;
    private SeatSelectView seatSelectView;
    private SeatThumView seatSelectThumView;
    private String hallSpecialDes;
    private boolean isFromLogin = false;
    private boolean isFromIsnotVip = false;
    private String notVipLoginNum = "";         // ????????????????????????
    private String mtimeLoginUrl = null;

    private TitleOfNormalView navigationBar;
    private SeatSelectChangeDialog changeDialog;
    private TextView seating_tv_max4;
    private TextView autoseat1;
    private TextView autoseat2;
    private TextView autoseat3;
    private TextView autoseat4;
    private View autoseat_layout;
    private View autoseat_num_layout;
    private TextView seating_number_text;
    private ProgressDialog progressDialog;
    private boolean isAutoSelected = true;
    private ArrayList<String> orderExplains;    // ?????????????????????
    private ImageView ivBuyticketAd1;
    private TextView mAdTag;
    private ImageView ivBuyticketAd2;
    private static final String BAIDU_LABEL = "?????????banner????????????";
    private TextView tv_introduction;
    private String mIntroduction;
    private TicketApi mTicketApi;

    @Override
    protected void onInitVariable() {
        final Intent intent = getIntent();
        String label = intent.getStringExtra(App.getInstance().KEY_ACTIVITY_FROM);

        setPageLabel(StatisticTicket.PN_ONLINE_TICKET);

        mReSelectSeatLastOrderId = intent.getStringExtra(App.getInstance().KEY_SEATING_LAST_ORDER_ID);
        // TODO: 2019-09-19 ?????? wwl
//        mIsSeatSelectAgain = intent.getBooleanExtra(App.getInstance().KEY_SEATING_SELECT_AGAIN, false);
        mDId = intent.getStringExtra(App.getInstance().KEY_SEATING_DID);
        if (!mIsSeatSelectAgain) {
            // ??????????????????????????????/????????????????????????????????????????????????????????????????????????????????????
            mMovieId = intent.getStringExtra(App.getInstance().KEY_MOVIE_ID);
            mCinemaId = intent.getStringExtra(App.getInstance().KEY_CINEMA_ID);
            mDate = intent.getStringExtra(App.getInstance().KEY_SHOWTIME_DATE);
            // ???????????????????????????????????????????????????????????????????????????????????????
            if ((mDate != null) && !"".equals(mDate)) {
                try {
                    if (!MtimeUtils.isValidDate(mDate)) {
                        // ??????????????????????????????????????????yyyyMMdd????????????????????????yyyy-MM-dd??????
                        final SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
                        final SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
                        mDate = format2.format(format1.parse(mDate));
                    }
                } catch (final Exception e) {
                    LogWriter.e("?????????" + mDate + "??????????????????" + e);
                }
            } else {
                // ?????????????????????????????????????????????????????????????????????????????????????????????date
                final Date serverDate = MTimeUtils.getLastDiffServerDate();
                final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                mDate = format.format(serverDate);// ?????????2012-12-18
            }
            mTicketDateInfo = createTicketDateInfo(0, "", "", "");
        }
        mTicketApi = new TicketApi();
    }

    @Override
    protected void onInitView(final Bundle savedInstanceState) {
        this.setContentView(R.layout.act_seat_select);
        View navBar = findViewById(R.id.navigationbar);
        navigationBar = new TitleOfNormalView(this, navBar, StructType.TYPE_NORMAL_SHOW_BACK_TITLE, "", null);
        mDataView = new SeatSelectDataView(this);
        seating_tv_max4 = findViewById(R.id.seating_tv_max4);
        tv_cinema_time = findViewById(R.id.seating_tv_cinema_time);
        tv_cinema_name = findViewById(R.id.seating_tv_cinema_name);
        tv_screen = findViewById(R.id.seating_tv_screen);
        ll_price_info = findViewById(R.id.seating_ll_price_info);
        tv_seat_limit = findViewById(R.id.seating_tv_seat_limit);
        btn_next_step = findViewById(R.id.seating_btn_next_step);
        seatSelectView = findViewById(R.id.seat_select_view);
        seatSelectThumView = findViewById(R.id.seat_select_thumview);
        seatSelectThumView.setVisibility(View.VISIBLE);
        seatSelectView.setContext(this);
        seatSelectView.setThumView(seatSelectThumView);
        salePrice = findViewById(R.id.seating_tv_price);
        tv_service_fee = findViewById(R.id.seating_tv_service_fee);
        tv_introduction = findViewById(R.id.seating_tv_introduction);

        seat_result_container = findViewById(R.id.seating_ll_seat_result_container);
        //btn_zoom =  findViewById(R.id.seating_btn_zoom);
        btn_change = findViewById(R.id.seating_btn_change);
        autoseat1 = findViewById(R.id.autoseat1);
        autoseat2 = findViewById(R.id.autoseat2);
        autoseat3 = findViewById(R.id.autoseat3);
        autoseat4 = findViewById(R.id.autoseat4);
        autoseat_layout = findViewById(R.id.autoseat_layout);
        // autoseat_num_layout = findViewById(R.id.seating_number_layout);
        seating_number_text = findViewById(R.id.seating_number_text);
        flater = getLayoutInflater();

        //?????????????????????
        ivBuyticketAd1 = findViewById(R.id.iv_buyticket_ad1);
        mAdTag = findViewById(R.id.act_seat_select_adv_tag_tv);
        ivBuyticketAd2 = findViewById(R.id.iv_buyticket_ad2);
    }

    @Override
    protected void onInitEvent() {
        seatSelectView.setSeatInterface(new ISeatSelectInterface() {

            @Override
            public void onSelect(SeatInfo seat, int selectStatus) {
                LogWriter.e("mylog", "?????????????????? onSelect - " + seat.getSeatName() + ", seat.status: " + seat.getStatus() + ", selectStatus:" + selectStatus);
                if (selectStatus == SeatManager.RESULT_NOTSEAT) {
                    StatisticPageBean bean = SeatSelectActivity.this.assemble(StatisticTicket.TICKET_SEAT_MAP, null, "select", null, "inexistence", null, null);
                    StatisticManager.getInstance().submit(bean);
                    TipsDlg.showTipsDlg(SeatSelectActivity.this, "??????????????????", false);
                } else if (selectStatus == SeatManager.RESULT_SELECTED_BY_OTHERS) {
                    StatisticPageBean bean = SeatSelectActivity.this.assemble(StatisticTicket.TICKET_SEAT_MAP, null, "select", null, "unavailable", null, null);
                    StatisticManager.getInstance().submit(bean);
                    TipsDlg.showTipsDlg(SeatSelectActivity.this, "????????????????????????????????????20??????????????????????????????????????????", false);
                } else if (selectStatus == SeatManager.RESULT_MORE_THAN_LIMIT) {
                    StatisticPageBean bean = SeatSelectActivity.this.assemble(StatisticTicket.TICKET_SEAT_MAP, null, "select", null, "overstep", null, null);
                    StatisticManager.getInstance().submit(bean);
                    TipsDlg.showTipsDlg(SeatSelectActivity.this, "???????????????" + mCanSelectSeatCount + "?????????", false);
                } else if (selectStatus == SeatManager.RESULT_ONE_LEFT_BUT_CHOOSE_LOVERSEAT) {
                    StatisticPageBean bean = SeatSelectActivity.this.assemble(StatisticTicket.TICKET_SEAT_MAP, null, "select", null, "loveSeat", null, null);
                    StatisticManager.getInstance().submit(bean);
                    TipsDlg.showTipsDlg(SeatSelectActivity.this, "?????????????????????????????????????????????????????????????????????????????????", false);
                } else if (selectStatus == SeatManager.RESULT_LEFT_SINGLE_SEAT) {
                    StatisticPageBean bean = SeatSelectActivity.this.assemble(StatisticTicket.TICKET_SEAT_MAP, null, "select", null, "noEmpty", null, null);
                    StatisticManager.getInstance().submit(bean);
                    TipsDlg.showTipsDlg(SeatSelectActivity.this, "?????????????????????????????????????????????", false);
                } else if (selectStatus == SeatManager.RESULT_ONE_LEFT) {
                    StatisticPageBean bean = SeatSelectActivity.this.assemble(StatisticTicket.TICKET_SEAT_MAP, null, "select", null, "noEmpty", null, null);
                    StatisticManager.getInstance().submit(bean);
                    TipsDlg.showTipsDlg(SeatSelectActivity.this, "???????????????????????????", false);
                } else if (seat.getStatus() == SeatManager.STATUS_SELECTED_BY_SELF) {
                    checkSeatsStatus();
                }
                showSeatingInfo(seatSelectView.getSeatManager().getSelectedSeatList());

            }

            @Override
            public void onMaxOrMin(boolean isMax) {
                isZoomin = !isMax;
            }

            @Override
            public void onBind() {
                TipsDlg.showTipsDlg(SeatSelectActivity.this, "?????????????????????????????????????????????", false);
            }

            @Override
            public void onScaleChenged(float scale) {
                StatisticPageBean bean = SeatSelectActivity.this.assemble(StatisticTicket.TICKET_SEAT_MAP, null, "gesturesToZoom", null, null, null, null);
                StatisticManager.getInstance().submit(bean);
            }
        });
        //btn_zoom.setOnClickListener(this);
        btn_change.setOnClickListener(this);
        btn_next_step.setOnClickListener(this);
        autoseat1.setOnClickListener(this);
        autoseat2.setOnClickListener(this);
        autoseat3.setOnClickListener(this);
        autoseat4.setOnClickListener(this);
        //autoseat_num_layout.setOnClickListener(this);

        // ?????????????????????
        if (mIsSeatSelectAgain) {
            // ???????????????????????????????????????????????????
            btn_change.setClickable(false);
            btn_change.setTextColor(getResources().getColor(R.color.gray_normal));
            btn_change.setBackgroundResource(R.drawable.bg_stroke_bbbbbb_frame);
        } else {
            // ???????????????????????????????????????
            btn_change.setClickable(true);
            btn_change.setTextColor(getResources().getColor(R.color.color_f97d3f));
            btn_change.setBackgroundResource(R.drawable.bg_stroke_f97d3f_frame);
        }
    }

    @Override
    protected void onLoadData() {

    }

    @Override
    protected void onRequestData() {
        //???????????????????????????
//        RequestCallback onlineSeatsAdsCallback = new RequestCallback() {
//            @Override
//            public void onSuccess(Object o) {
//                final OnlineSeatsAdvsBean bean = (OnlineSeatsAdvsBean) o;
//                if (bean == null) {
//                    ivBuyticketAd1.setVisibility(View.GONE);
//                    ivBuyticketAd2.setVisibility(View.GONE);
//                    mAdTag.setVisibility(View.GONE);
//                } else {
//                    //?????????????????????720??????????????????1
//                    if (bean.getImg1() != null && bean.getImg1() != "" && FrameConstant.SCREEN_WIDTH >= 720) {
//                        // 750*210
//                        int width = FrameConstant.SCREEN_WIDTH;
//                        int height = FrameConstant.SCREEN_WIDTH * App.getInstance().ONLINE_SEATS_ADS_IMG1_HEIGHT / App.getInstance().ONLINE_SEATS_ADS_IMG1_WIDTH;
//                        volleyImageLoader.displayImage(bean.getImg1(), ivBuyticketAd1, 0, 0, width, height, ImageURLManager.FIX_WIDTH_AND_HEIGHT, null);
//                        if (!TextUtils.isEmpty(bean.advTag)) {
//                            mAdTag.setText(bean.advTag);
//                            mAdTag.setVisibility(View.VISIBLE);
//                        } else {
//                            mAdTag.setVisibility(View.GONE);
//                        }
//                    } else {
//                        ivBuyticketAd1.setVisibility(View.GONE);
//                        mAdTag.setVisibility(View.GONE);
//                    }
//                    if (bean.getImg2() != null && bean.getImg2() != "") {
//                        // 240*110
//                        int width = App.getInstance().ONLINE_SEATS_ADS_IMG2_WIDTH;
//                        int height = App.getInstance().ONLINE_SEATS_ADS_IMG2_HEIGHT;
//                        volleyImageLoader.displayImage(bean.getImg2(), ivBuyticketAd2, 0, 0, width, height, ImageURLManager.FIX_WIDTH_AND_HEIGHT, null);
//                    } else {
//                        ivBuyticketAd2.setVisibility(View.GONE);
//                    }
//                    if (bean.getUrl1() != null && bean.getUrl1() != "") {
//                        ivBuyticketAd1.setOnClickListener(new OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                if (bean.isIsOpenH5()) {
//                                    MtimeUtils.callBrowser(SeatSelectActivity.this, bean.getUrl1());
//                                } else {
////                                    JumpUtil.startAdvRecommendActivity(SeatSelectActivity.this, assemble().toString(),
////                                            bean.getUrl1(), true, true, null, null, -1);
//                                    JumpUtil.startCommonWebActivity(SeatSelectActivity.this, bean.getUrl1(), StatisticH5.PN_H5, null,
//                                            true, true, true, false, assemble().toString());
//                                }
//                            }
//                        });
//                    } else {
//                        ivBuyticketAd1.setClickable(false);
//                    }
//                }
//
//            }
//
//            @Override
//            public void onFail(Exception e) {
//                ivBuyticketAd1.setVisibility(View.GONE);
//                ivBuyticketAd2.setVisibility(View.GONE);
//            }
//        };
        // ??????
//        Map<String, String> param = new HashMap<>(1);
//        param.put("movieId", mMovieId);
//        HttpUtil.get(ConstantUrl.ONLINE_SEATS_ADS, param, OnlineSeatsAdvsBean.class, onlineSeatsAdsCallback);

    }

    @Override
    protected void onUnloadData() {

    }

    /**
     * ????????????????????????????????????
     */
    private void loadTicket(final String dId) {
        UIUtil.showLoadingDialog(this);
        if (mIsSeatSelectAgain) {
            // ????????????????????????????????????????????????????????????????????????????????????????????????
            DispatchAsync.dispatchAsyncDelayed(new Runnable() {
                @Override
                public void run() {
                    loadTicket2(dId);
                }
            }, SeatSelectActivity.POLLING_SLEEP_TIME);
        } else {
            loadTicket2(dId);
        }
    }

    private void loadTicket2(final String dId) {
        mTicketApi.getSeatInfo(dId, new NetworkManager.NetworkListener<SeatInfoJsonBean>() {
            @Override
            public void onSuccess(SeatInfoJsonBean sj, String showMsg) {
                UIUtil.dismissLoadingDialog();
                if (null == mDataView || null == sj) {
                    UIUtil.dismissLoadingDialog();
                    MToastUtils.showShortToast("?????????????????????,??????????????????");
                    return;
                }
                isAutoSelected = sj.isAutoSelected();

                orderExplains = (ArrayList<String>) sj.getOrderExplains();

                if (!isAutoSelected) {
                    autoseat_layout.setVisibility(View.GONE);
                    seating_number_text.setVisibility(View.VISIBLE);
                    seating_tv_max4.setVisibility(View.VISIBLE);
                } else {
                    autoseat_layout.setVisibility(View.VISIBLE);
                    seating_number_text.setVisibility(View.GONE);
                    seating_tv_max4.setVisibility(View.GONE);
                }

                mDataView.setSeatInfo(sj);
                mSeatInfo = mDataView.getSeatInfo();
                hallSpecialDes = sj.getHallSpecialDes();
                mMovieId = mSeatInfo.getMovieId();
                mCinemaId = mSeatInfo.getCinemaId();

                if (null == sj.getSeat()) {
                    UIUtil.dismissLoadingDialog();
                    MToastUtils.showShortToast("?????????????????????????????????????????????");
                    return;
                }

                seatSelectView.setData(sj.getSeat(), sj.getRowNameList(), mCanSelectSeatCount,
                        sj.getSeatRowCount(), sj.getSeatColumnCount());
                seatSelectView.setVisibility(View.VISIBLE);
                // ????????????????????????
                refreshView(mSeatInfo);
                // ??????????????????????????????
                Acp.getInstance(SeatSelectActivity.this).request(new AcpOptions.Builder()
                                .setPermissions(
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ).build(),
                        new AcpListener() {
                            @Override
                            public void onGranted() {
                                Acp.getInstance(getApplicationContext()).onDestroy();
                                requestSeatsIcon();
                            }

                            @Override
                            public void onDenied(List<String> permissions) {
                                Acp.getInstance(getApplicationContext()).onDestroy();
                                MToastUtils.showShortToast("sd???????????????");
                            }
                        });

                if (!TextUtils.isEmpty(sj.getOrderId()) && !TextUtils.isEmpty(sj.getSubOrderID())
                        && !"0".equals(sj.getOrderId()) && !"0".equals(sj.getSubOrderID()) && UserManager.Companion.getInstance().isLogin()) {
                    showWithoutPayDlg(sj.getOrderId(), sj.getDateMessage());
                } else {
                    showTipDlg(sj.getDateMessage());
                }

                if (null != mSeatInfo && mSeatInfo.isSale()) {
                    // ?????????????????????????????????????????????????????????
                    clearAllData();
                    return;
                }

                UIUtil.dismissLoadingDialog();
                bottomBarNoSale();
                if (sj.getMovieName() != null) {
                    if (TextUtils.isEmpty(sj.getVersionDesc()) || TextUtils.isEmpty(sj.getLanguage())) {
                        navigationBar.setTitleText(sj.getMovieName());
                    } else {
                        navigationBar.setTitles(sj.getMovieName(), sj.getVersionDesc() + " " + sj.getLanguage());
                    }
                }
            }

            @Override
            public void onFailure(NetworkException<SeatInfoJsonBean> exception, String showMsg) {
                UIUtil.dismissLoadingDialog();
                MToastUtils.showShortToast(showMsg);
            }
        });

//        final RequestCallback seatInfoCallback = new RequestCallback() {
//            @Override
//            public void onSuccess(final Object o) {
//                UIUtil.dismissLoadingDialog();
//                SeatInfoJsonBean sj = new SeatInfoJsonBean();
//                sj = (SeatInfoJsonBean) o;
//                if (null == mDataView || null == sj) {
//                    UIUtil.dismissLoadingDialog();
//                    MToastUtils.showShortToast("?????????????????????,??????????????????");
//                    return;
//                }
//                isAutoSelected = sj.isAutoSelected();
//
//                orderExplains = (ArrayList<String>) sj.getOrderExplains();
//
//                if (!isAutoSelected) {
//                    autoseat_layout.setVisibility(View.GONE);
//                    seating_number_text.setVisibility(View.VISIBLE);
//                    seating_tv_max4.setVisibility(View.VISIBLE);
//                } else {
//                    autoseat_layout.setVisibility(View.VISIBLE);
//                    seating_number_text.setVisibility(View.GONE);
//                    seating_tv_max4.setVisibility(View.GONE);
//                }
//
//                mDataView.setSeatInfo(sj);
//                mSeatInfo = mDataView.getSeatInfo();
//                hallSpecialDes = sj.getHallSpecialDes();
//                mMovieId = mSeatInfo.getMovieId();
//                mCinemaId = mSeatInfo.getCinemaId();
//
//                if (null == sj.getSeat()) {
//                    UIUtil.dismissLoadingDialog();
//                    MToastUtils.showShortToast("?????????????????????????????????????????????");
//                    return;
//                }
//
//                seatSelectView.setData(sj.getSeat(), sj.getRowNameList(), mCanSelectSeatCount,
//                        sj.getSeatRowCount(), sj.getSeatColumnCount());
//                seatSelectView.setVisibility(View.VISIBLE);
//                // ????????????????????????
//                refreshView(mSeatInfo);
//                // ??????????????????????????????
//                Acp.getInstance(SeatSelectActivity.this).request(new AcpOptions.Builder()
//                                .setPermissions(
//                                        Manifest.permission.READ_EXTERNAL_STORAGE,
//                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
//                                ).build(),
//                        new AcpListener() {
//                            @Override
//                            public void onGranted() {
//                                requestSeatsIcon();
//                            }
//
//                            @Override
//                            public void onDenied(List<String> permissions) {
//                                MToastUtils.showShortToast("sd???????????????");
//                            }
//                        });
//
//                if (!TextUtils.isEmpty(sj.getOrderId()) && !TextUtils.isEmpty(sj.getSubOrderID())
//                        && !"0".equals(sj.getOrderId()) && !"0".equals(sj.getSubOrderID()) && UserManager.Companion.getInstance().isLogin()) {
//                    showWithoutPayDlg(sj.getOrderId(), sj.getDateMessage());
//                } else {
//                    showTipDlg(sj.getDateMessage());
//                }
//
//                if (null != mSeatInfo && mSeatInfo.isSale()) {
//                    // ?????????????????????????????????????????????????????????
//                    clearAllData();
//                    return;
//                }
//
//                UIUtil.dismissLoadingDialog();
//                bottomBarNoSale();
//                if (sj.getMovieName() != null) {
//                    if (TextUtils.isEmpty(sj.getVersionDesc()) || TextUtils.isEmpty(sj.getLanguage())) {
//                        navigationBar.setTitleText(sj.getMovieName());
//                    } else {
//                        navigationBar.setTitles(sj.getMovieName(), sj.getVersionDesc() + " " + sj.getLanguage());
//                    }
//                }
//
//            }
//
//            @Override
//            public void onFail(final Exception e) {
//                UIUtil.dismissLoadingDialog();
//                MToastUtils.showShortToast(e.getLocalizedMessage());
//            }
//        };

        // Showtime/OnlineSeatsByShowTimeID.api?dId={0}
//        Map<String, String> param = new HashMap<>(1);
//        param.put("dId", dId);
//        HttpUtil.post(ConstantUrl.GET_SEAT_INFO, param, SeatInfoJsonBean.class, seatInfoCallback);
    }

    private void showTipDlg(String dateMessage) {
        if (canShowDlg && !TextUtils.isEmpty(dateMessage)) {
            //??????????????????????????????
            final CustomAlertDlg alertDlg = new CustomAlertDlg(SeatSelectActivity.this, CustomAlertDlg.TYPE_OK);
            alertDlg.setBtnOKListener(new OnClickListener() {
                public void onClick(final View v) {
                    alertDlg.dismiss();
                }
            });
            alertDlg.show();
            String content = "<font color=\"#ff8600\">" + dateMessage + "</font>";
            alertDlg.setText(Html.fromHtml("????????????" + content + "???????????????????????????~"));
            alertDlg.setLabel(getResources().getString(R.string.st_iknow));
        }
    }

    /**
     * ?????????????????????????????????
     */
    private void showSeatingInfo(ArrayList<SeatInfo> seatinfos) {
        final int tecketSize = seatinfos.size();

        LogWriter.e("mylog", "tecketSize:" + tecketSize);
        if (seatResultLayouts == null) {
            seatResultLayouts = new ArrayList<LinearLayout>();
        }

        if (tecketSize == 0) {
            // ?????????????????????
            ll_price_info.setVisibility(View.GONE);
            tv_seat_limit.setVisibility(View.VISIBLE);
            // btn_next_step.setBackgroundResource(R.drawable.btn_solid_gray_h76);
            btn_next_step.setText("????????????");
            seating_number_text.setVisibility(View.GONE);
        } else {
            // ??????????????????
            ll_price_info.setVisibility(View.VISIBLE);
            tv_seat_limit.setVisibility(View.GONE);
            btn_next_step.setText("????????????");
            seating_number_text.setVisibility(View.VISIBLE);
            // btn_next_step.setBackgroundResource(R.drawable.btn_solid_orange_h76);
        }

        StringBuffer sbf = new StringBuffer();
        seat_result_container.removeAllViews();
        if (isAutoSelected) {
            autoseat_layout.setVisibility(View.VISIBLE);
            seating_number_text.setVisibility(View.GONE);
            seating_tv_max4.setVisibility(View.GONE);
        } else {
            autoseat_layout.setVisibility(View.GONE);
            seating_number_text.setVisibility(View.VISIBLE);
            seating_tv_max4.setVisibility(View.VISIBLE);
        }

        for (SeatInfo seatInfo : seatinfos) {
            final LinearLayout seatResultLayout = (LinearLayout) flater.inflate(R.layout.seat_select_result_textview, null);
            final TextView seatResult = seatResultLayout.findViewById(R.id.item_seat_select_result_tv);
            seatResult.setText(seatInfo.getSeatName());
            seatResult.setTextColor(getResources().getColor(R.color.black_normal));
            seatResult.setTextSize(13);
            /*
             * TextView seatResult = (TextView)
             * flater.inflate(R.layout.seat_select_result_textview, null);
             * seatResult.setText(ticketName);
             */
            seatResultLayout.setTag(seatInfo.getSeatName());
            seatResultLayouts.add(seatResultLayout);

            seat_result_container.addView(seatResultLayout);
            if (isAutoSelected) {
                autoseat_layout.setVisibility(View.GONE);
                seating_number_text.setVisibility(View.VISIBLE);
            } else {
                seating_tv_max4.setVisibility(View.GONE);
            }

            sbf.append(seatInfo.getSeatName()).append(" / ");
        }

        final String info = sbf.toString();
        sbf = null;
        double price = 0.0;
        String introduction = "";

        if (TextUtil.stringIsNotNull(info)) {
            // ??????????????????"/"
            mSeatSelectedInfo = info.substring(0, info.lastIndexOf("/"));
            if (mSeatInfo.getSalePriceList() != null && mSeatInfo.getSalePriceList().size() == 4) {
                if (tecketSize == 1) {
                    price = mSeatInfo.getSalePriceList().get(0).getSaleAmount() / 100;
                    introduction = mSeatInfo.getSalePriceList().get(0).getAmountFormat();
                } else if (tecketSize == 2) {
                    price = mSeatInfo.getSalePriceList().get(1).getSaleAmount() / 100;
                    introduction = mSeatInfo.getSalePriceList().get(1).getAmountFormat();
                } else if (tecketSize == 3) {
                    price = mSeatInfo.getSalePriceList().get(2).getSaleAmount() / 100;
                    introduction = mSeatInfo.getSalePriceList().get(2).getAmountFormat();
                } else if (tecketSize == 4) {
                    price = mSeatInfo.getSalePriceList().get(3).getSaleAmount() / 100;
                    introduction = mSeatInfo.getSalePriceList().get(3).getAmountFormat();
                }
            } else {
                BigDecimal sellPrice = new BigDecimal(Double.toString(mSeatInfo.getMtimeSellPrice()));
                BigDecimal tSize = new BigDecimal(Double.toString(tecketSize));

                BigDecimal multiplyPrice = sellPrice.multiply(tSize);
                price = multiplyPrice.doubleValue();
//                price = ((double)tecketSize) * mSeatInfo.getMtimeSellPrice();
                introduction = getResources().getString(R.string.str_money) + mSeatInfo.getMtimeSellPrice() + " X " + tecketSize;
            }
        } else {
            price = 0.0;
            introduction = "";
        }

        mTotalPrice = price;
        mIntroduction = introduction;
        salePrice.setText(getResources().getString(R.string.str_money) + MtimeUtils.formatPrice(MtimeUtils.moneyY2Str(price)));

        tv_introduction.setText(introduction);

        if (mSeatInfo.getServiceFee() > 0) {
            tv_service_fee.setText("??????????????? " + getResources().getString(R.string.str_money) + MtimeUtils.formatPrice(mSeatInfo.getServiceFee()) + "/??????");
        }
        seatLayoutListener(seatinfos);
    }

    // ???????????????????????????4????????????????????????
    private void seatLayoutListener(final ArrayList<SeatInfo> seatinfos) {
        if (seatResultLayouts == null) {
            seatResultLayouts = new ArrayList<LinearLayout>();
        }
        for (int i = 0; i < seatResultLayouts.size(); i++) {
            seatResultLayouts.get(i).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(final View v) {

                    LogWriter.e("mylog", "seatLayoutListener-onClick");
                    final String ticketName = (String) v.getTag();
                    int x = 0;
                    int y = 0;
                    for (int j = 0; j < seatinfos.size(); j++) {
                        if (seatinfos.get(j).getSeatName().equals(ticketName)) {
                            x = seatinfos.get(j).getX();
                            y = seatinfos.get(j).getY();
                        }
                    }
                    int result = seatSelectView.getSeatManager().chooseSeat(x, y);
                    // ????????????????????????????????????
                    StatisticPageBean bean4 = SeatSelectActivity.this.assemble(StatisticTicket.TICKET_CANCEL_SEAT, null, null, null, null, null, null);
                    StatisticManager.getInstance().submit(bean4);
                    showSeatingInfo(seatinfos);
                }
            });

        }

    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
//            case R.id.seating_btn_zoom: // ???????????????
//                String action = "";
//                if (isZoomin) {
//                    action = "seatAmplification";
//                    seatSelectView.setScale(1.0f, 1.0f);
//                    btn_zoom.setBackgroundResource(R.drawable.pic_btn_zoomout);
//                    isZoomin = false;
//                } else {
//                    action = "seatDiminish";
//                    seatSelectView.setScale(seatSelectView.getMinScale(), seatSelectView.getMinScale());
//                    btn_zoom.setBackgroundResource(R.drawable.pic_btn_zoomin);
//                    isZoomin = true;
//                }
//                /*
//                 * Set<Integer> set = ymap.keySet(); createSeating(set);
//                 */
//                break;

            case R.id.seating_btn_next_step: // ?????????(?????????????????????)
                if (seatSelectView.getSeatManager() == null) {
                    return;
                }
                StatisticPageBean bean6 = SeatSelectActivity.this.assemble(StatisticTicket.TICKET_NEXT, null, null, null, null, null, null);
                StatisticManager.getInstance().submit(bean6);
                ArrayList<SeatInfo> seatInfos = seatSelectView.getSeatManager().getSelectedSeatList();
                final int selectedSeatCount = seatInfos.size();
                if (selectedSeatCount == 0) {
                    TipsDlg.showTipsDlg(SeatSelectActivity.this, "???????????????????????????", false);
                    return;
                }

                // ????????????XX?????????????????????????????????XX????????????
                if (mIsSeatSelectAgain && (selectedSeatCount < mCanSelectSeatCount)) {
                    final String msg = "????????????" + mCanSelectSeatCount + "?????????????????????????????????"
                            + (mCanSelectSeatCount - selectedSeatCount) + "?????????";
                    TipsDlg.showTipsDlg(SeatSelectActivity.this, msg, false);
                    return;
                }
                // doCreateOrder(mDId, seatId, TEL_PHONE);
                if (hallSpecialDes != null && !hallSpecialDes.equals("")) {
                    showhallSpecialDlg(hallSpecialDes);
                    return;
                }
                StringBuffer sbf = new StringBuffer();
                for (final SeatInfo seatInfo : seatInfos) {
                    sbf.append(seatInfo.getSeatId()).append(App.getInstance().COMMA);
                }
                sbf.deleteCharAt(sbf.length() - 1);
                mSeatId = sbf.toString();
                sbf = null;

                if (mIsSeatSelectAgain) {
                    // ????????????
                    doCreateOrder(mDId, mSeatId, mUserBuyTicketPhone);
                } else {
                    // ???????????????????????????????????????
                    if (UserManager.Companion.getInstance().isLogin()) {
                        // ??????????????????????????????
                        UIUtil.showLoadingDialog(this);
                        requestSmallPayList(selectedSeatCount);
                    } else {
                        JumpUtil.startLoginActivity(this, bean6.toString(), mTotalPrice, mIntroduction, mSeatInfo.getMovieName(), mSeatInfo.getCinemaName(), mSeatId, selectedSeatCount, mSeatInfo.getServiceFee(),
                                mSeatInfo.getSubOrderID(), mTicketDateInfo, mSeatSelectedInfo, mDId, mMovieId, mCinemaId, mDate, true, false, 2);
                    }
                }
                break;
            case R.id.seating_btn_change:
                if (mIsSeatSelectAgain) {
                    return;
                }
                StatisticPageBean bean5 = SeatSelectActivity.this.assemble(StatisticTicket.TICKET_CHANGE_TIME, null, "timePage", null, null, null, null);
                StatisticManager.getInstance().submit(bean5);
                // ?????????????????????sidebar???
                if (isFirstChangeScene) {
                    // ???????????????
                    doGetMovieTimesByCinema(mCinemaId, mMovieId, mDate);
                } else {
                    changeDialog.showActionSheet();
                }

                break;
            case R.id.autoseat1:
                Map<String, String> businessParam1 = new HashMap<String, String>();
                businessParam1.put("attendance", "1");
                StatisticPageBean bean1 = SeatSelectActivity.this.assemble(StatisticTicket.TICKET_SELECT_ATTENDANCE, "1", null, null, null, null, businessParam1);
                StatisticManager.getInstance().submit(bean1);
                requestAutoSeats(1);
                break;
            case R.id.autoseat2:
                requestAutoSeats(2);
                Map<String, String> businessParam2 = new HashMap<String, String>();
                businessParam2.put("attendance", "2");
                StatisticPageBean bean2 = SeatSelectActivity.this.assemble(StatisticTicket.TICKET_SELECT_ATTENDANCE, "2", null, null, null, null, businessParam2);
                StatisticManager.getInstance().submit(bean2);
                break;
            case R.id.autoseat3:
                Map<String, String> businessParam3 = new HashMap<String, String>();
                businessParam3.put("attendance", "3");
                StatisticPageBean bean3 = SeatSelectActivity.this.assemble(StatisticTicket.TICKET_SELECT_ATTENDANCE, "3", null, null, null, null, businessParam3);
                StatisticManager.getInstance().submit(bean3);
                requestAutoSeats(3);
                break;
            case R.id.autoseat4:
                Map<String, String> businessParam4 = new HashMap<String, String>();
                businessParam4.put("attendance", "4");
                StatisticPageBean bean4 = SeatSelectActivity.this.assemble(StatisticTicket.TICKET_SELECT_ATTENDANCE, "4", null, null, null, null, businessParam4);
                StatisticManager.getInstance().submit(bean4);
                requestAutoSeats(4);
                break;
//            case R.id.seating_number_layout:
//                //??????????????????????????????
//                if (seatSelectView != null && seatSelectView.getSeatManager() != null) {
//                    List<SeatInfo> allSeats = seatSelectView.getSeatManager().getSelectedSeatList();
//                    int size = allSeats.size();
//                    for (int j = 0; j < size; j++) {
//                        seatSelectView.getSeatManager().deselectSeat(allSeats.get(0));
//                    }
//                    showSeatingInfo(seatSelectView.getSeatManager().getSelectedSeatList());
//                }
//                break;
        }
    }


    /**
     * ????????????????????????
     */
    protected void doGetMovieTimesByCinema(final String cinemaId, final String movieId, final String date) {

        final RequestCallback getShowTimesByCinemaMovieDateCallback = new RequestCallback() {

            @Override
            public void onSuccess(final Object o) {
                UIUtil.dismissLoadingDialog();
                mDataView.cinemaJsonBean2UIBean((CinemaJsonBean) o, true);
                // ?????????????????????????????????????????????????????????????????????
                final List<ShowTimeUIBean> filteredShowtimes = mDataView.getCinemaShowTimeInfo()
                        .getFilteredShowTimeList();
                changeDialog = new SeatSelectChangeDialog(SeatSelectActivity.this, R.style.SelectSeatChangeDialogStyle,
                        filteredShowtimes, date);
                changeDialog.showActionSheet();
                if (null != changeDialog.getListView()) {
                    changeDialog.getListView().setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(final AdapterView<?> parent, final View view, final int position,
                                                final long id) {
                            final List<Provider> providers = filteredShowtimes.get(position).getProviderList();
                            if ((providers != null) && (providers.size() > 0)) {
                                final Provider provider1 = providers.get(0);
                                mDId = String.valueOf(provider1.getdId()); // ????????????provider???dId??????????????????????????????
                                changeDialog.dismiss();
                                // ????????????????????????
                                SeatSelectActivity.this.loadTicket(mDId);
                            }
                        }
                    });
                }

                isFirstChangeScene = false;
            }

            @Override
            public void onFail(final Exception e) {
                UIUtil.dismissLoadingDialog();
                if (!canShowDlg) {
                    return;
                }
                MToastUtils.showShortToast(e.getLocalizedMessage());
            }
        };

        // Showtime/ShowTimesByCinemaMovieDate.api?cinemaId={0}&movieId={1}&date={2}
        Map<String, String> param = new HashMap<>(3);
        param.put("cinemaId", cinemaId);
        param.put("movieId", movieId);
        param.put("date", date);
        HttpUtil.get(ConstantUrl.GET_SHOWTIMES_BY_CINEMA_MOVIE_DATE, param, CinemaJsonBean.class, getShowTimesByCinemaMovieDateCallback, 180000);

    }

    /**
     * ??????????????????????????????????????????????????????????????? ?????? X???X?????????X???X???X??? ????????????????????????3D,??????,120??????
     */
    private String createTicketDateInfo(final long showDayLongTime, final String hall, final String versionDesc,
                                        final String language) {
        final StringBuilder dateInfo = new StringBuilder();
        dateInfo.append(getformatDate(showDayLongTime)).append(" ???").append(hall).append("??? ").append(" ")
                .append(versionDesc).append(" ").append(language);
        return dateInfo.toString();
    }

    /**
     * ???????????????
     */
    private void bottomBarNoSale() {
        // ????????????????????????4?????????????????????????????????????????????
        ll_price_info.setVisibility(View.GONE);
        tv_seat_limit.setVisibility(View.VISIBLE);
    }

    /**
     * ????????????map????????????????????????????????????????????????
     */
    private void clearAllData() {
        if ((ticketMap != null) && (ticketMap.size() > 0)) {
            ticketMap.clear();
        }

        if ((btnMap != null) && (btnMap.size() > 0)) {
            btnMap.clear();
        }

        // ???????????????????????????
        // btn_zoom.setBackgroundResource(R.drawable.pic_btn_zoomin);
        // ??????????????????
        // seatLayout.removeAllViews();
        // ??????????????????????????????textview??????
        isZoomin = true;
        hasOrderCanceled = false;
        seat_result_container.removeAllViews();
        if (isAutoSelected) {
            autoseat_layout.setVisibility(View.VISIBLE);
            seating_number_text.setVisibility(View.GONE);
            seating_tv_max4.setVisibility(View.GONE);
        } else {
            seating_tv_max4.setVisibility(View.VISIBLE);
        }
    }

    private void refreshView(final SeatInfoUIBean bean) {
        if (bean.getMovieName() != null) {
            if ((bean.getVersionDesc() != null) && (bean.getLanguage() != null)) {
                navigationBar.setTitles(bean.getMovieName(), bean.getVersionDesc() + " " + bean.getLanguage());
            } else {
                navigationBar.setTitleText(bean.getMovieName());
            }
        }
        if (bean.getHallName() != null) {
            tv_screen.setText(bean.getHallName() + " " + "??????");
        }

        final String dateInfo = getformatDate(bean.getRealTime());
        // XXXX?????? X???X?????????X??? X???X???
        if ((bean.getCinemaName() != null) && (dateInfo != null)) {
            tv_cinema_time.setText(dateInfo);
            tv_cinema_name.setText(bean.getCinemaName());
            tv_cinema_name.setVisibility(View.VISIBLE);
            tv_cinema_time.setVisibility(View.VISIBLE);
        }

        mTicketDateInfo = createTicketDateInfo(bean.getRealTime(), bean.getHallName(), bean.getVersionDesc(),
                bean.getLanguage());

        // ??????????????????????????????????????????????????????????????????????????????
        ll_price_info.setVisibility(View.GONE);
        tv_seat_limit.setVisibility(View.VISIBLE);
        //btn_next_step.setBackgroundResource(R.drawable.btn_solid_gray_h76);
    }

    @Override
    public boolean onKeyUp(final int keyCode, final KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (changeDialog != null && changeDialog.isShowing()) {
                StatisticPageBean bean7 = SeatSelectActivity.this.assemble(StatisticTicket.TICKET_CHANGE_TIME, null, "cancel", null, null, null, null);
                StatisticManager.getInstance().submit(bean7);
                changeDialog.dismiss();
                return true;
            } else {
                // ????????????????????????
                finish();
            }
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // ??????????????????
        if (mIsSeatSelectAgain) {
            requestLastOrderDetailInfo(mReSelectSeatLastOrderId);
        } else {
            if (mDId != null) {
                loadTicket(mDId);
            }
        }
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        mReSelectSeatLastOrderId = intent.getStringExtra(App.getInstance().KEY_SEATING_LAST_ORDER_ID);
        String mDid = intent.getStringExtra(App.getInstance().KEY_SEATING_DID);
        if (mDid != null && TextUtils.isEmpty(mDid)) {
            mDId = mDid;
        }


        // ?????????????????????
        if (mIsSeatSelectAgain) {
            // ???????????????????????????????????????????????????
            btn_change.setClickable(false);
            btn_change.setTextColor(getResources().getColor(R.color.gray_normal));
            btn_change.setBackgroundResource(R.drawable.bg_stroke_bbbbbb_frame);
        } else {
            // ???????????????????????????????????????
            btn_change.setClickable(true);
            btn_change.setTextColor(getResources().getColor(R.color.color_f97d3f));
            btn_change.setBackgroundResource(R.drawable.bg_stroke_f97d3f_frame);
        }

        isZoomin = true;
    }

    /**
     * ???????????????String???????????????XXXX?????? X???X?????????X??? X???X???
     */
    private String getformatDate(final long date) {
        if (date == 0) {
            return "";
        }
        // XXXX?????? X???X?????????X??? X???X???
        String dateInfo = TimeExt.INSTANCE.millis2String(date, "M???d??? (E)  HH:mm")
                .replace("??????", "???");
        return dateInfo;
    }

    /**
     * ????????????,???????????????????????????
     */
    private void checkSeatsStatus() {
        final ArrayList<SeatInfo> selectedSeatList = seatSelectView.getSeatManager().getSelectedSeatList();
        if (selectedSeatList.size() == 0) {
            return;
        }
        StringBuffer sbf = new StringBuffer();
        for (final SeatInfo seatInfo : selectedSeatList) {
            sbf.append(seatInfo.getSeatId()).append(App.getInstance().COMMA);
        }
        sbf.deleteCharAt(sbf.length() - 1);

        // Showtime/OnlineSeatsStatus.api?dId={0}&seatId={1}
        Map<String, String> param = new HashMap<>(2);
        param.put("dId", mDId);
        param.put("seatId", sbf.toString());
        HttpUtil.post(ConstantUrl.ONLINE_SEATS_STATUS, param, OnlineSeatsStatusBean.class, new RequestCallback() {

            @Override
            public void onSuccess(Object o) {
                OnlineSeatsStatusBean result = (OnlineSeatsStatusBean) o;
                if (result.getRowSeats() == null || result.getRowSeats().size() == 0) {
                    return;
                }
                List<OnlineSeatsStatusRowSeatBean> responsSeats = result.getRowSeats();

                for (int i = 0; i < seatSelectView.allSeats.length; i++) {
                    for (int j = 0; j < responsSeats.size(); j++) {
                        if (null == seatSelectView.allSeats[i]) {
                            continue;
                        }
                        if (seatSelectView.allSeats[i].getSeatId().equals(responsSeats.get(j).getId())) {
                            int status = -1;
                            if (TextUtils.isEmpty(responsSeats.get(j).getId())) {
                                status = SeatManager.STATUS_NOT_SEAT;
                            } else if (responsSeats.get(j).getEnable() == true) {
                                status = SeatManager.STATUS_CAN_SELECT;
                                for (int k = 0; k < selectedSeatList.size(); k++) {
                                    //?????? ??????????????????
                                    if (responsSeats.get(j).getId().equals(selectedSeatList.get(k).getSeatId())) {
                                        status = SeatManager.STATUS_SELECTED_BY_SELF;
                                        break;
                                    }
                                }
                            } else {
                                status = SeatManager.STATUS_SELECTED_BY_OTHERS;
                            }
                            seatSelectView.allSeats[i].setStatus(status);
                            //????????????????????????????????????????????????
                            break;
                        }
                    }
                }
                List<SeatInfo> needRemoveInfos = new ArrayList<SeatInfo>();
                for (int i = 0; i < selectedSeatList.size(); i++) {
                    for (int j = 0; j < responsSeats.size(); j++) {
                        if (selectedSeatList.get(i).getSeatId().equals(responsSeats.get(j).getId()) && !responsSeats.get(j).getEnable()) {
                            needRemoveInfos.add(selectedSeatList.get(i));
                            break;
                        }
                    }
                }
                final StringBuffer sbf = new StringBuffer();
                for (SeatInfo seatInfo : needRemoveInfos) {
                    sbf.append(seatInfo.getSeatName()).append(App.getInstance().COMMA);
                    selectedSeatList.remove(seatInfo);
                }
                if (sbf.length() > 0) {
                    sbf.deleteCharAt(sbf.length() - 1);
                }
                LogWriter.e("mylog", "sbf" + sbf.toString());

                int size = selectedSeatList.size();
                if (size > 0) {
                    for (int i = 0; i < size; i++) {
                        if (selectedSeatList.get(i).getType() == 2 || selectedSeatList.get(i).getType() == 3) {
                            StatisticPageBean bean = SeatSelectActivity.this.assemble(StatisticTicket.TICKET_SEAT_MAP, null, "select", null, "loveSeat", null, null);
                            StatisticManager.getInstance().submit(bean);
                        } else {
                            StatisticPageBean bean = SeatSelectActivity.this.assemble(StatisticTicket.TICKET_SEAT_MAP, null, "select", null, "selected", null, null);
                            StatisticManager.getInstance().submit(bean);
                        }
                    }
                }
                showSeatingInfo(selectedSeatList);

                if (!TextUtils.isEmpty(sbf.toString())) {
                    new Handler().post(new Runnable() {

                        @Override
                        public void run() {
                            StatisticPageBean bean = SeatSelectActivity.this.assemble(StatisticTicket.TICKET_SEAT_MAP, null, "select", null, "occupation", null, null);
                            StatisticManager.getInstance().submit(bean);
                            MToastUtils.showShortToast(sbf.toString() + " ??????????????????");
                        }
                    });
                }
            }

            @Override
            public void onFail(Exception e) {
            }
        });
    }

    /**
     * ??????????????????????????????
     */
    private void requestSeatsIcon() {
        if (TextUtils.isEmpty(mMovieId)) {
            return;
        }
        // PageSubArea/UseSeatsIcon.api?movieId={0}
        Map<String, String> param = new HashMap<>(1);
        param.put("movieId", mMovieId);
        HttpUtil.get(ConstantUrl.USE_SEATS_ICON, param, SeatsIconUseData.class, new RequestCallback() {

            @Override
            public void onSuccess(Object o) {
                SeatsIconUseData seatsIconUseData = (SeatsIconUseData) o;
                if (seatsIconUseData != null) {
                    int movieSeatId = seatsIconUseData.getSelectedId();
                    InputStream inStream = MtimeUtils.getSeatsIcon(String.valueOf(movieSeatId));
                    ImageView seattip_selected_byself = findViewById(R.id.seattip_selected_byself);
                    ImageView seattip_selected_byother = findViewById(R.id.seattip_selected_byother);
                    seatSelectView.setSeatIcon(inStream, seattip_selected_byself, seattip_selected_byother);
                }
            }

            @Override
            public void onFail(Exception e) {
            }
        });
    }

    /**
     * ??????????????????
     */
    private void requestAutoSeats(int i) {
        if (mCanSelectSeatCount < i) {
            TipsDlg.showTipsDlg(SeatSelectActivity.this, "???????????????" + mCanSelectSeatCount + "?????????", false);
            return;
        }

        showProgressDialog(this, "????????????...");
        Map<String, String> params = new ArrayMap<String, String>(2);
        params.put("showtimeId", mDId);
        params.put("count", String.valueOf(i));
        HttpUtil.post(ConstantUrl.AUTO_OPTIMAL_SEATS, params, AutoSeatsBean.class, new RequestCallback() {
            @Override
            public void onFail(Exception e) {
                dismissProgressDialog();
                e.printStackTrace();
            }

            @Override
            public void onSuccess(Object o) {
                dismissProgressDialog();
                AutoSeatsBean responseBean = (AutoSeatsBean) o;
                final ArrayList<SeatInfo> selectedSeatList = seatSelectView.getSeatManager().getSelectedSeatList();
                if (responseBean.getSeatColl() != null && responseBean.getSeatColl().size() > 0 && null != seatSelectView && null != seatSelectView.allSeats) {
                    List<SeatColl> responsSeats = responseBean.getSeatColl();
                    for (int i = 0; i < seatSelectView.allSeats.length; i++) {
                        for (int j = 0; j < responsSeats.size(); j++) {
                            if (seatSelectView.allSeats[i] != null && responsSeats.get(j) != null) {
                                if (seatSelectView.allSeats[i].getSeatId().equals(responsSeats.get(j).getSeatId())) {
                                    int status = -1;
                                    if (TextUtils.isEmpty(responsSeats.get(j).getSeatId())) {
                                        status = SeatManager.STATUS_NOT_SEAT;
                                    } else if (responsSeats.get(j).getEnable() == true) {
                                        status = SeatManager.STATUS_CAN_SELECT;
                                        for (int k = 0; k < selectedSeatList.size(); k++) {
                                            //?????? ??????????????????
                                            if (responsSeats.get(j).getSeatId().equals(selectedSeatList.get(k).getSeatId())) {
                                                status = SeatManager.STATUS_SELECTED_BY_SELF;
                                                break;
                                            }
                                        }
                                    } else {
                                        status = SeatManager.STATUS_SELECTED_BY_OTHERS;
                                    }
                                    seatSelectView.allSeats[i].setStatus(status);
                                }
                            }
                        }
                    }
                }


                SeatInfo[] allSeats = seatSelectView.getSeatManager().getAllSeats();
                if (null != allSeats) {
                    if (responseBean.getAutoSeatIds() != null && responseBean.getAutoSeatIds().size() > 0) {
                        for (int i = 0; i < responseBean.getAutoSeatIds().size(); i++) {
                            if (seatSelectView != null && seatSelectView.getSeatManager() != null) {
                                for (int j = 0; j < allSeats.length; j++) {
                                    if (allSeats[j] != null && allSeats[j].getSeatId().equals(responseBean.getAutoSeatIds().get(i))) {
                                        seatSelectView.getSeatManager().selectSeat(allSeats[j]);
                                    }
                                }
                                showSeatingInfo(seatSelectView.getSeatManager().getSelectedSeatList());
                                autoseat_layout.setVisibility(View.GONE);
                                seating_number_text.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
                if (responseBean.getBizCode() == 4 && !TextUtils.isEmpty(responseBean.getMsg())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SeatSelectActivity.this);
                    builder.setTitle("??????");
                    builder.setMessage(responseBean.getMsg());
                    builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }
            }
        });
    }

    /**
     * ?????????????????????
     */
    private void showProgressDialog(final Context context, final String message) {
        if (!canShowDlg) {
            return;
        }

        if (null == progressDialog) {
            progressDialog = Utils.createProgressDialog(this, message);
        }

        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (null != progressDialog) {
            progressDialog.dismiss();
        }
    }


    /***************************************??????????????????begin***************************************/

    /**
     * ??????????????????????????????
     */
    private void doGetWithoutPaymentOnlineSeat() {
        final RequestCallback withoutPayOrderCallback = new RequestCallback() {
            @Override
            public void onSuccess(final Object o) {
                UIUtil.dismissLoadingDialog();
                final WithoutPayOnlineSeat orderBean = (WithoutPayOnlineSeat) o;
                mWithoutPayOrderId = orderBean.getOrderId();
                if ((mWithoutPayOrderId != null) && !"".equals(mWithoutPayOrderId)
                        && (Long.valueOf(mWithoutPayOrderId) > 0) && !hasOrderCanceled) {
                    // ?????????????????????????????????
                    SeatSelectActivity.this.getPayList(mWithoutPayOrderId);
                } else {
                    if (isFromLogin) {
                        isFromLogin = false;
                        if (null != seatSelectView && null != seatSelectView.getSeatManager()) {
                            ArrayList<SeatInfo> seatInfos = seatSelectView.getSeatManager().getSelectedSeatList();
                            final int selectedSeatCount = seatInfos.size();
                            requestSmallPayList(selectedSeatCount);
                        }
                    }
                }
            }

            @Override
            public void onFail(final Exception e) {
                UIUtil.dismissLoadingDialog();
            }
        };
        UIUtil.showLoadingDialog(this, false);
        HttpUtil.get(ConstantUrl.GET_WITHOUT_PAYMENT_ONLINE_SEAT, WithoutPayOnlineSeat.class, withoutPayOrderCallback);
    }

    private void getPayList(final String withoutPayOrderId) {
        Map<String, String> parameterList = new ArrayMap<String, String>(1);
        parameterList.put("orderId", withoutPayOrderId);
        HttpUtil.get(ConstantUrl.GET_PAY_LIST, parameterList, GetPayListBean.class, new RequestCallback() {

            @Override
            public void onSuccess(Object o) {
                GetPayListBean payListBean = (GetPayListBean) o;
                ArrayList<PayCardListBean> cardList = payListBean.getCardList();
                if (null == cardList) {
                    return;
                }

                String url = null;
                for (int i = 0; i < cardList.size(); i++) {
                    if (cardList.get(i).getTypeId() == 5) {
                        // ??????????????????????????????
                        url = cardList.get(i).getUrl();
                        if (url != null && url.length() > 0) {
                            requestWithLogin(withoutPayOrderId, url);
                        }
                    }
                }
                if (url == null || url.length() == 0) {
                    showWithoutPayDlg(withoutPayOrderId, "");
                }
            }

            @Override
            public void onFail(Exception e) {

            }
        });
    }

    /**
     * ?????????????????????????????????
     */
    private void showWithoutPayDlg(final String withoutPayOrderId, final String dateMessage) {
        if (!canShowDlg) {
            return;
        }

        final CustomAlertDlg withoutPayDlg = new CustomAlertDlg(this, CustomAlertDlg.TYPE_OK_CANCEL);
        withoutPayDlg.setBtnOKListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                // ?????????????????????????????????
                // pollSubOrderStatus(subOrderId); // ??????????????????????????????
                final Intent intent = new Intent();
                // intent.putExtra(Constant.PAY_ETICKET, mIsETicket);
                intent.putExtra(App.getInstance().KEY_IS_DO_WITH_OUT_PAY_ORDER, true); // ????????????????????????
                // ??????4?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                intent.putExtra(App.getInstance().KEY_SEATING_DID, mDId);
                intent.putExtra(App.getInstance().KEY_MOVIE_ID, mMovieId);
                intent.putExtra(App.getInstance().KEY_CINEMA_ID, mCinemaId);
                intent.putExtra(App.getInstance().KEY_SHOWTIME_DATE, mDate);
                if (mtimeLoginUrl != null) {
                    intent.putExtra(App.getInstance().KEY_MTIME_URL, mtimeLoginUrl);
                }
                // ??????????????????id
                intent.putExtra(App.getInstance().KEY_SEATING_ORDER_ID, withoutPayOrderId);
                intent.putExtra(App.getInstance().IS_FROM_SEATSELECT_ACTIVITY, true);
                SeatSelectActivity.this.startActivity(OrderPayActivity.class, intent);
                withoutPayDlg.dismiss();
            }
        });
        withoutPayDlg.setBtnCancelListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                // ????????????
                SeatSelectActivity.this.doCancelOrder(withoutPayOrderId, dateMessage);
                withoutPayDlg.dismiss();
            }
        });
        withoutPayDlg.show();
        withoutPayDlg.setCancelable(false);
        withoutPayDlg.getTextView().setText("??????????????????????????????");
        withoutPayDlg.getBtnOK().setText("????????????");
        withoutPayDlg.getBtnCancel().setText("????????????");
    }

    private void requestWithLogin(final String withoutPayOrderId, String url) {
        Map<String, String> parameterList = new ArrayMap<String, String>(1);
        parameterList.put("url", url);
        HttpUtil.post(ConstantUrl.GET_COUPON_URL_WITH_LOGIN, parameterList, SuccessBean.class, new RequestCallback() {
            public void onSuccess(Object o) {
                SuccessBean bean = (SuccessBean) o;
                if (bean.getSuccess() != null) {
                    if (bean.getSuccess().equalsIgnoreCase("true")) {
                        if (UserManager.Companion.getInstance().isLogin()) {
                            mtimeLoginUrl = bean.getNewUrl();
                            showWithoutPayDlg(withoutPayOrderId, "");
                        }
                    } else {
                        MToastUtils.showShortToast("??????????????????????????????????????????");
                    }
                } else {
                    MToastUtils.showShortToast("??????????????????????????????????????????");
                }
            }

            @Override
            public void onFail(Exception e) {
                MToastUtils.showShortToast("???????????????????????????????????????");
            }
        });
    }

    /**
     * ????????????
     */
    private void doCancelOrder(final String orderId, final String dateMessage) {
        UIUtil.showLoadingDialog(this);

        final RequestCallback cancelOrderCallback = new RequestCallback() {
            @Override
            public void onSuccess(final Object o) {
                UIUtil.dismissLoadingDialog();
                if (o instanceof CancelOrderJsonBean) {
                    final CancelOrderJsonBean result = (CancelOrderJsonBean) o;

                    String message;
                    if (result.isSuccess()) { // ??????????????????????????????????????????
                        hasOrderCanceled = true; // ????????????????????????????????????????????????????????????
                        message = SeatSelectActivity.this.getString(R.string.orderCancelSuccess);
                    } else {
                        message = SeatSelectActivity.this.getString(R.string.orderCancelError);
                        if (result.getStatus() == 1) {
                            message = result.getMsg();
                        } else if (result.getStatus() == 2) { // ??????????????????
                            message = SeatSelectActivity.this.getString(R.string.orderCancelOk);
                        }
                    }

                    if (canShowDlg) {
                        TipsDlg.showTipsDlg(SeatSelectActivity.this, message, false);
                        showTipDlg(dateMessage);
                    }
                }
            }

            @Override
            public void onFail(final Exception e) {
                UIUtil.dismissLoadingDialog();
                MToastUtils.showShortToast(e.getLocalizedMessage());
            }
        };

        Map<String, String> parameterList = new ArrayMap<String, String>(1);
        parameterList.put("orderId", orderId);
        HttpUtil.post(ConstantUrl.CANCEL_ORDER, parameterList, CancelOrderJsonBean.class, cancelOrderCallback);
    }

    private void requestSmallPayList(final int selectedSeatCount) {
        // ??????????????????????????????????????????????????? 2020-11-18 by wwl
        final Intent intent = new Intent();
        intent.putExtra(App.getInstance().KEY_SEATING_TOTAL_PRICE, mTotalPrice);
        intent.putExtra(App.getInstance().KEY_SEATING_PRICE_INTRODUCTION, mIntroduction);
        intent.putExtra(App.getInstance().KEY_MOVIE_NAME, mSeatInfo.getMovieName());
        intent.putExtra(App.getInstance().KEY_CINEMA_NAME, mSeatInfo.getCinemaName());
        intent.putExtra(App.getInstance().KEY_SEATING_SEAT_ID, mSeatId);
        intent.putExtra(App.getInstance().KEY_SEATING_SELECTED_SEAT_COUNT, selectedSeatCount); // ?????????
        intent.putExtra(App.getInstance().KEY_SEATING_SERVICE_FEE, mSeatInfo.getServiceFee());
        // ????????????????????????getOrderId()???getSubOrderID()???????????????0
        // ????????????????????????OrderId??????????????????????????????????????????
        // intent.putExtra(Constant.KEY_SEATING_ORDER_ID,
        // mSeatInfo.getOrderId());
        intent.putExtra(App.getInstance().KEY_SEATING_SUBORDER_ID, mSeatInfo.getSubOrderID());
        intent.putExtra(App.getInstance().KEY_TICKET_DATE_INFO, mTicketDateInfo);
        intent.putExtra(App.getInstance().KEY_SEAT_SELECTED_INFO, mSeatSelectedInfo);
        // ????????????????????????????????????????????????(???????????????????????????sBean.isMembershipCard()????????????????????????false  2020-11-18 by wwl)
        intent.putExtra(App.getInstance().KEY_ISMEMBERSHIPCARD, false);
        // ??????4?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        intent.putExtra(App.getInstance().KEY_SEATING_DID, mDId);
        intent.putExtra(App.getInstance().KEY_MOVIE_ID, mMovieId);
        intent.putExtra(App.getInstance().KEY_CINEMA_ID, mCinemaId);
        intent.putExtra(App.getInstance().KEY_SHOWTIME_DATE, mDate);

        intent.putExtra(App.getInstance().KEY_TICKET_TIME_INFO, getformatDate(mSeatInfo.getRealTime()));
        intent.putExtra(App.getInstance().KEY_TICKET_HALLNAME_INFO, mSeatInfo.getHallName());
        intent.putExtra(App.getInstance().KEY_TICKET_VERSIONDESC_INFO, mSeatInfo.getVersionDesc());
        intent.putExtra(App.getInstance().KEY_TICKET_LANGUAGE_INFO, mSeatInfo.getLanguage());
        if (isFromIsnotVip) {
            isFromIsnotVip = false;
            intent.putExtra(App.getInstance().KEY_TARGET_NOT_VIP, true);
            intent.putExtra(App.getInstance().KEY_TARGET_NOT_VIP_PHONE, notVipLoginNum);
            App.getInstance().notVipLoginNum = "";
        }

        intent.putStringArrayListExtra(OrderConfirmActivity.EXPLAINS, orderExplains);
        intent.putExtra(App.getInstance().IS_FROM_SEATSELECT_ACTIVITY, true);
        startActivity(OrderConfirmActivity.class, intent);

        // ?????????app????????????????????????
//        UIUtil.showLoadingDialog(this, false);
//        // Showtime/GetBuffetCommoditys.api?showtimeId={0}
//        Map<String, String> param = new HashMap<>(1);
//        param.put("showtimeId", mDId);
//        HttpUtil.get(ConstantUrl.GET_SMALL_PAY, param, SmallPayBean.class, new RequestCallback() {
//
//            @Override
//            public void onSuccess(Object o) {
//                UIUtil.dismissLoadingDialog();
//                SmallPayBean sBean = (SmallPayBean) o;
//                final Intent intent = new Intent();
//                intent.putExtra(App.getInstance().KEY_SEATING_TOTAL_PRICE, mTotalPrice);
//                intent.putExtra(App.getInstance().KEY_SEATING_PRICE_INTRODUCTION, mIntroduction);
//                intent.putExtra(App.getInstance().KEY_MOVIE_NAME, mSeatInfo.getMovieName());
//                intent.putExtra(App.getInstance().KEY_CINEMA_NAME, mSeatInfo.getCinemaName());
//                intent.putExtra(App.getInstance().KEY_SEATING_SEAT_ID, mSeatId);
//                intent.putExtra(App.getInstance().KEY_SEATING_SELECTED_SEAT_COUNT, selectedSeatCount); // ?????????
//                intent.putExtra(App.getInstance().KEY_SEATING_SERVICE_FEE, mSeatInfo.getServiceFee());
//                // ????????????????????????getOrderId()???getSubOrderID()???????????????0
//                // ????????????????????????OrderId??????????????????????????????????????????
//                // intent.putExtra(Constant.KEY_SEATING_ORDER_ID,
//                // mSeatInfo.getOrderId());
//                intent.putExtra(App.getInstance().KEY_SEATING_SUBORDER_ID, mSeatInfo.getSubOrderID());
//                intent.putExtra(App.getInstance().KEY_TICKET_DATE_INFO, mTicketDateInfo);
//                intent.putExtra(App.getInstance().KEY_SEAT_SELECTED_INFO, mSeatSelectedInfo);
//                intent.putExtra(App.getInstance().KEY_ISMEMBERSHIPCARD, sBean.isMembershipCard());
//                // ??????4?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
//                intent.putExtra(App.getInstance().KEY_SEATING_DID, mDId);
//                intent.putExtra(App.getInstance().KEY_MOVIE_ID, mMovieId);
//                intent.putExtra(App.getInstance().KEY_CINEMA_ID, mCinemaId);
//                intent.putExtra(App.getInstance().KEY_SHOWTIME_DATE, mDate);
//
//                intent.putExtra(App.getInstance().KEY_TICKET_TIME_INFO, getformatDate(mSeatInfo.getRealTime()));
//                intent.putExtra(App.getInstance().KEY_TICKET_HALLNAME_INFO, mSeatInfo.getHallName());
//                intent.putExtra(App.getInstance().KEY_TICKET_VERSIONDESC_INFO, mSeatInfo.getVersionDesc());
//                intent.putExtra(App.getInstance().KEY_TICKET_LANGUAGE_INFO, mSeatInfo.getLanguage());
//                if (isFromIsnotVip) {
//                    isFromIsnotVip = false;
//                    intent.putExtra(App.getInstance().KEY_TARGET_NOT_VIP, true);
//                    intent.putExtra(App.getInstance().KEY_TARGET_NOT_VIP_PHONE, notVipLoginNum);
//                    App.getInstance().notVipLoginNum = "";
//                }
//
//                if (sBean.isSuccess()) { // ?????????
//                    if (sBean.getCommodityList().size() > 0) {
//                        JumpUtil.startSmallPayActivity(SeatSelectActivity.this, intent, sBean, true);
//                    } else {
//                        intent.putStringArrayListExtra(OrderConfirmActivity.EXPLAINS, orderExplains);
//                        intent.putExtra(App.getInstance().IS_FROM_SEATSELECT_ACTIVITY, true);
//                        SeatSelectActivity.this.startActivity(OrderConfirmActivity.class, intent);
//                    }
//
//                } else {
//                    intent.putStringArrayListExtra(OrderConfirmActivity.EXPLAINS, orderExplains);
//                    intent.putExtra(App.getInstance().IS_FROM_SEATSELECT_ACTIVITY, true);
//                    SeatSelectActivity.this.startActivity(OrderConfirmActivity.class, intent);
//                }
//
//            }
//
//            @Override
//            public void onFail(Exception e) {
//                UIUtil.dismissLoadingDialog();
//                final Intent intent = new Intent();
//                intent.putExtra(App.getInstance().KEY_SEATING_TOTAL_PRICE, mTotalPrice);
//                intent.putExtra(App.getInstance().KEY_SEATING_PRICE_INTRODUCTION, mIntroduction);
//                intent.putExtra(App.getInstance().KEY_MOVIE_NAME, mSeatInfo.getMovieName());
//                intent.putExtra(App.getInstance().KEY_CINEMA_NAME, mSeatInfo.getCinemaName());
//                intent.putExtra(App.getInstance().KEY_SEATING_SEAT_ID, mSeatId);
//                intent.putExtra(App.getInstance().KEY_SEATING_SELECTED_SEAT_COUNT, selectedSeatCount); // ?????????
//                intent.putExtra(App.getInstance().KEY_SEATING_SERVICE_FEE, mSeatInfo.getServiceFee());
//                // ????????????????????????getOrderId()???getSubOrderID()???????????????0
//                // ????????????????????????OrderId??????????????????????????????????????????
//                // intent.putExtra(Constant.KEY_SEATING_ORDER_ID,
//                // mSeatInfo.getOrderId());
//                intent.putExtra(App.getInstance().KEY_SEATING_SUBORDER_ID, mSeatInfo.getSubOrderID());
//                intent.putExtra(App.getInstance().KEY_TICKET_DATE_INFO, mTicketDateInfo);
//                intent.putExtra(App.getInstance().KEY_SEAT_SELECTED_INFO, mSeatSelectedInfo);
//                intent.putExtra(App.getInstance().KEY_ISMEMBERSHIPCARD, false);
//                // ??????4?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
//                intent.putExtra(App.getInstance().KEY_SEATING_DID, mDId);
//                intent.putExtra(App.getInstance().KEY_MOVIE_ID, mMovieId);
//                intent.putExtra(App.getInstance().KEY_CINEMA_ID, mCinemaId);
//                intent.putExtra(App.getInstance().KEY_SHOWTIME_DATE, mDate);
//                if (isFromIsnotVip) {
//                    isFromIsnotVip = false;
//                    intent.putExtra(App.getInstance().KEY_TARGET_NOT_VIP, true);
//                    intent.putExtra(App.getInstance().KEY_TARGET_NOT_VIP_PHONE, notVipLoginNum);
//                    App.getInstance().notVipLoginNum = "";
//                }
//                intent.putStringArrayListExtra(OrderConfirmActivity.EXPLAINS, orderExplains);
//                intent.putExtra(App.getInstance().IS_FROM_SEATSELECT_ACTIVITY, true);
//                SeatSelectActivity.this.startActivity(OrderConfirmActivity.class, intent);
//
//            }
//        });
    }

    private void showhallSpecialDlg(final String hallSpecialDes) {
        final CustomAlertDlg customAlertDlg = new CustomAlertDlg(SeatSelectActivity.this, CustomAlertDlg.TYPE_OK_CANCEL);
        customAlertDlg.show();
        customAlertDlg.getTitleText().setText("????????????");
        customAlertDlg.getTitleText().setVisibility(View.VISIBLE);
        customAlertDlg.getBtnCancel().setText("????????????");
        customAlertDlg.getBtnOK().setText("????????????");
        customAlertDlg.setBtnCancelListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                customAlertDlg.dismiss();
            }
        });
        customAlertDlg.setBtnOKListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                customAlertDlg.dismiss();
                ArrayList<SeatInfo> seatInfos = seatSelectView.getSeatManager().getSelectedSeatList();
                final int selectedSeatCount = seatInfos.size();
                StringBuffer sbf = new StringBuffer();
                for (final SeatInfo seatInfo : seatInfos) {
                    sbf.append(seatInfo.getSeatId()).append(App.getInstance().COMMA);
                }
                if (sbf.length() > 1) {
                    sbf.deleteCharAt(sbf.length() - 1);
                }
                mSeatId = sbf.toString();
                sbf = null;

                if (mIsSeatSelectAgain) {
                    // ????????????
                    doCreateOrder(mDId, mSeatId, mUserBuyTicketPhone);
                } else {
                    // ???????????????????????????????????????
                    if (UserManager.Companion.getInstance().isLogin()) {
                        // ??????????????????????????????
                        requestSmallPayList(selectedSeatCount);
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putDouble(App.getInstance().KEY_SEATING_TOTAL_PRICE, mTotalPrice);
                        bundle.putString(App.getInstance().KEY_SEATING_PRICE_INTRODUCTION, mIntroduction);
                        bundle.putString(App.getInstance().KEY_MOVIE_NAME, mSeatInfo.getMovieName());
                        bundle.putString(App.getInstance().KEY_CINEMA_NAME, mSeatInfo.getCinemaName());
                        bundle.putString(App.getInstance().KEY_SEATING_SEAT_ID, mSeatId);
                        bundle.putInt(App.getInstance().KEY_SEATING_SELECTED_SEAT_COUNT, selectedSeatCount); // ?????????
                        bundle.putDouble(App.getInstance().KEY_SEATING_SERVICE_FEE, mSeatInfo.getServiceFee());
                        // ????????????????????????getOrderId()???getSubOrderID()???????????????0
                        // ????????????????????????OrderId??????????????????????????????????????????
                        // intent.putExtra(Constant.KEY_SEATING_ORDER_ID,
                        // mSeatInfo.getOrderId());
                        bundle.putString(App.getInstance().KEY_SEATING_SUBORDER_ID, mSeatInfo.getSubOrderID());
                        bundle.putString(App.getInstance().KEY_TICKET_DATE_INFO, mTicketDateInfo);
                        bundle.putString(App.getInstance().KEY_SEAT_SELECTED_INFO, mSeatSelectedInfo);

                        // ??????4?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                        bundle.putString(App.getInstance().KEY_SEATING_DID, mDId);
                        bundle.putString(App.getInstance().KEY_MOVIE_ID, mMovieId);
                        bundle.putString(App.getInstance().KEY_CINEMA_ID, mCinemaId);
                        bundle.putString(App.getInstance().KEY_SHOWTIME_DATE, mDate);
                        // intent.putExtra(Constant.KEY_TARGET_ACTIVITY_ID,
                        // Constant.ACTIVITY_ORDER_CONFIRM);
                        bundle.putBoolean(App.getInstance().KEY_SHOW_NOT_VIP, true);
                        bundle.putInt("RequestCode", 2);
                        bundle.putBoolean(App.getInstance().KEY_SHOW_NEWGIFT_DLG, false);

                        UserLoginKt.gotoLoginPage(SeatSelectActivity.this, bundle, 2);

//                        final Intent intent = new Intent();
//                        intent.putExtra(App.getInstance().KEY_SEATING_TOTAL_PRICE, mTotalPrice);
//                        intent.putExtra(App.getInstance().KEY_SEATING_PRICE_INTRODUCTION, mIntroduction);
//                        intent.putExtra(App.getInstance().KEY_MOVIE_NAME, mSeatInfo.getMovieName());
//                        intent.putExtra(App.getInstance().KEY_CINEMA_NAME, mSeatInfo.getCinemaName());
//                        intent.putExtra(App.getInstance().KEY_SEATING_SEAT_ID, mSeatId);
//                        intent.putExtra(App.getInstance().KEY_SEATING_SELECTED_SEAT_COUNT, selectedSeatCount); // ?????????
//                        intent.putExtra(App.getInstance().KEY_SEATING_SERVICE_FEE, mSeatInfo.getServiceFee());
//                        // ????????????????????????getOrderId()???getSubOrderID()???????????????0
//                        // ????????????????????????OrderId??????????????????????????????????????????
//                        // intent.putExtra(Constant.KEY_SEATING_ORDER_ID,
//                        // mSeatInfo.getOrderId());
//                        intent.putExtra(App.getInstance().KEY_SEATING_SUBORDER_ID, mSeatInfo.getSubOrderID());
//                        intent.putExtra(App.getInstance().KEY_TICKET_DATE_INFO, mTicketDateInfo);
//                        intent.putExtra(App.getInstance().KEY_SEAT_SELECTED_INFO, mSeatSelectedInfo);
//
//                        // ??????4?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
//                        intent.putExtra(App.getInstance().KEY_SEATING_DID, mDId);
//                        intent.putExtra(App.getInstance().KEY_MOVIE_ID, mMovieId);
//                        intent.putExtra(App.getInstance().KEY_CINEMA_ID, mCinemaId);
//                        intent.putExtra(App.getInstance().KEY_SHOWTIME_DATE, mDate);
//                        // intent.putExtra(Constant.KEY_TARGET_ACTIVITY_ID,
//                        // Constant.ACTIVITY_ORDER_CONFIRM);
//                        intent.putExtra(App.getInstance().KEY_SHOW_NOT_VIP, true);
//                        intent.putExtra("RequestCode", 2);
//                        intent.putExtra(App.getInstance().KEY_SHOW_NEWGIFT_DLG, false);
//                        SeatSelectActivity.this.startActivityForResult(LoginActivity.class, intent, 2);

                        // ???????????????????????????????????????????????????
                    }
                }
            }
        });
        customAlertDlg.getTextView().setText(hallSpecialDes);
    }

    // TODO: 2019-09-19 ?????? wwl
    /**
     * ???????????????????????????????????????
     */
//    private void doReselectSeat(final String newOrderId, final String reSelectSeatLastOrderId) {
//        final RequestCallback reselectSeatCallback = new RequestCallback() {
//            @Override
//            public void onSuccess(final Object o) {
//                UIUtil.dismissLoadingDialog();
//
//                final CommResultBean result = (CommResultBean) o;
//                if (result.isSuccess()) {
//                    // SeatSelectActivity.this.loadCustomDialog();
//                    showTicketOutingDialog();
//                    // ???????????????
//                    SeatSelectActivity.this.pollMainOrderStatus(mReselectSeatNewOrderId);
//                } else {
//                    LogWriter.w("??????????????????--result.getError(): " + result.getError());
//                }
//            }
//
//            @Override
//            public void onFail(final Exception e) {
//
//                UIUtil.dismissLoadingDialog();
//                MToastUtils.showShortToast(e.getLocalizedMessage());
//            }
//        };
//
//        // Ticket/reselectseat.api?orderId={0}&resOrderId={1}
//        Map<String, String> param = new HashMap<>(2);
//        param.put("orderId", newOrderId);
//        param.put("resOrderId", reSelectSeatLastOrderId);
//        HttpUtil.get(ConstantUrl.RESELECT_SEAT, param, CommResultBean.class, reselectSeatCallback);
//    }

    /**
     * ?????????????????????
     */
    private void pollMainOrderStatus(final String orderId) {
        if (isOrderRun) {

            final RequestCallback getMainOrderStatusCallback = new RequestCallback() {

                @Override
                public void onSuccess(final Object o) {
                    final OrderStatusJsonBean order = (OrderStatusJsonBean) o;
                    final int orderStatus = order.getOrderStatus();

                    if (orderStatus != 10) {
                        // ??????????????????????????????
                        if (mPoolingMainOrderTimer != null) {
                            mPoolingMainOrderTimer.cancel();
                        }
                    }
                    switch (orderStatus) {
                        case 10: // 10-????????????(?????????????????????????????????)???
                            SeatSelectActivity.this.pollMainOrderStatus(orderId); // ???????????????????????????
                            break;
                        case 30:
                            isOrderRun = false;
                            if (ticketOutingDialog != null) {
                                ticketOutingDialog.dismiss();
                            }
                            SeatSelectActivity.this.paySuccess();
                            finish();
                            break;
                        case 40: // 40-??????(??????????????????????????????????????????) --????????????,????????????????????????????????????
                            isOrderRun = true;
                            if (ticketOutingDialog != null) {
                                ticketOutingDialog.dismiss();
                            }
                            if (order.isReSelectSeat()) {
                                // ????????????????????????
                                SeatSelectActivity.this.gotoPayFailedActivity(
                                        OrderPayFailedActivity.ERROR_TYPE_RESELECT_SEAT, "????????????",
                                        "????????????????????????????????????????????????\n????????????????????????????????????", "????????????", true);
                            }
                            SeatSelectActivity.this.finish();
                            break;
                        case 100: // 100-?????????(??????????????????????????????)
                            // progressDialog.dismiss();
                            // payError(getString(R.string.payOrderCancel));
                            if (ticketOutingDialog != null) {
                                ticketOutingDialog.dismiss();
                            }
                            break;
                        default:
                            // progressDialog.dismiss();
                            // payError(getString(R.string.pay_error));
                            if (ticketOutingDialog != null) {
                                ticketOutingDialog.dismiss();
                            }
                    }
                }

                @Override
                public void onFail(final Exception e) {
                    // ???????????????
                    if (mPoolingMainOrderTimer != null) {
                        mPoolingMainOrderTimer.cancel();
                    }
                    if (ticketOutingDialog != null) {
                        ticketOutingDialog.dismiss();
                    }
                    if (!SeatSelectActivity.this.isFinishing()) {
                        final AlertDialog alertDlg = Utils.createDlg(SeatSelectActivity.this,
                                SeatSelectActivity.this.getString(R.string.str_error),
                                SeatSelectActivity.this.getString(R.string.str_load_error));
                        alertDlg.show();
                    }
                }

            };

            if (mPoolingCounter > SeatSelectActivity.POLLING_MAX_TIME) {
                // ??????????????????????????????????????????
                // payError("???????????????");
                return;
            }

            DispatchAsync.dispatchAsyncDelayed(new Runnable() {
                @Override
                public void run() {
                    mPoolingCounter++;
                    Map<String, String> parameterList = new ArrayMap<String, String>(1);
                    parameterList.put("orderId", orderId);
                    HttpUtil.post(ConstantUrl.GET_ORDER_STATUS, parameterList, OrderStatusJsonBean.class, getMainOrderStatusCallback);
                }
            }, SeatSelectActivity.POLLING_SLEEP_TIME);
        }
    }

    /**
     * ?????????????????????
     */
    private void pollSubOrderStatus(final String subOrderId) {
        // ????????????????????????????????????????????????
        final RequestCallback subOrderStatusCallback = new RequestCallback() {

            @Override
            public void onSuccess(final Object o) {
                if (o instanceof SubOrderStatusJsonBean) {
                    final SubOrderStatusJsonBean subOrderStatusJsonBean = (SubOrderStatusJsonBean) o;
                    subOrderStatus = subOrderStatusJsonBean.getSubOrderStatus();
                    switch (subOrderStatus) {
                        case -1: // -1?????????ID?????????
                            // dialog.dismiss();
                            // createOrderFail();
                            break;
                        case 0: // 0-??????(??????????????????????????????)
                            // ????????????????????????????????????????????????????????????????????????
                            SeatSelectActivity.this.pollSubOrderStatus(subOrderId);
                            break;
                        case 10: // 10-????????????(?????????????????????????????????)???
                            // dialog.dismiss();
                            // isSubOrderRun = false;
                            // doSetOrderCreateFinshed(mOrderId); //???????????????????????????
                            if (!mIsSeatSelectAgain) {
                                SeatSelectActivity.this.gotoOrderPayActivity();
                            }
                            // TODO: 2019-09-19 ?????? wwl
//                            if (mIsSeatSelectAgain) {
//                                // ????????????????????????
//                                SeatSelectActivity.this.doReselectSeat(mReselectSeatNewOrderId,
//                                        mReSelectSeatLastOrderId);
//                            } else {
//                                SeatSelectActivity.this.gotoOrderPayActivity();
//                            }
                            break;
                        case 20: // 20-????????????(?????????????????????????????????)
                            // dialog.dismiss();
                            // createOrderFail();
                            break;
                    }
                }

            }

            @Override
            public void onFail(final Exception e) {
                if (canShowDlg) {
                    final AlertDialog alertDlg = Utils.createDlg(SeatSelectActivity.this,
                            SeatSelectActivity.this.getString(R.string.str_error),
                            SeatSelectActivity.this.getString(R.string.str_load_error));
                    alertDlg.show();
                }
            }
        };

        if (mPoolingCounter > SeatSelectActivity.POLLING_MAX_TIME) {
            // ??????????????????????????????????????????
            // payError("???????????????");
            return;
        }

        if (numberOfTimes > SeatSelectActivity.MAX_POLLING_TIME) {
            // ??????????????????????????????????????????
            // createOrderFail();
            // dialog.dismiss();
            return;
        }

        DispatchAsync.dispatchAsyncDelayed(new Runnable() {
            @Override
            public void run() {
                numberOfTimes++;
                Map<String, String> parameterList = new ArrayMap<String, String>(1);
                parameterList.put("subOrderId", subOrderId);
                HttpUtil.get(ConstantUrl.GET_SUB_ORDER_STATUS, parameterList, SubOrderStatusJsonBean.class, subOrderStatusCallback);
            }
        }, SeatSelectActivity.POLLING_SLEEP_TIME);

    }

    /**
     * ?????????????????????????????????????????????????????????????????????
     */
    private void doCreateOrder(final String dId, final String seatId, final String userBuyTicketPhone) {
        UIUtil.showLoadingDialog(this);

        final RequestCallback createOrderCallback = new RequestCallback() {
            @Override
            public void onSuccess(final Object o) {
                CreateOrderJsonBean result = (CreateOrderJsonBean) o;

                if (null != result && result.isSuccess()) {
                    mReselectSeatNewOrderId = String.valueOf(result.getOrderId());
                    mSubOrderId = String.valueOf(result.getSubOrderId());
                    mPayEndTime = result.getPayEndTime();
                    SeatSelectActivity.this.pollSubOrderStatus(mSubOrderId);
                } else { // ??????????????????
                    MToastUtils.showShortToast(result.getMsg());
                }
                result = null;
            }

            @Override
            public void onFail(final Exception e) {
                UIUtil.dismissLoadingDialog();
                MToastUtils.showShortToast(e.getLocalizedMessage());
            }
        };

        Map<String, String> parameterList = new ArrayMap<String, String>(4);
        parameterList.put("dId", dId);
        parameterList.put("seatId", seatId);
        parameterList.put("mobile", userBuyTicketPhone);
        parameterList.put("orderPayModel", "");
        HttpUtil.post(ConstantUrl.CREATE_ORDER, parameterList, CreateOrderJsonBean.class, createOrderCallback);

    }

    /**
     * ????????????????????????
     *
     * @param errorType            ????????????
     * @param errorTitle           ????????????
     * @param errorDetail          ????????????
     * @param errorButtonMsg       ??????????????????
     * @param idNeedFinishActivity ????????????????????????????????????????????????activity
     */
    protected void gotoPayFailedActivity(final int errorType, final String errorTitle, final String errorDetail,
                                         final String errorButtonMsg, final boolean idNeedFinishActivity) {
        final Intent intent = new Intent();
        intent.putExtra(App.getInstance().PAY_ETICKET, false); // ??????????????????
        intent.putExtra(App.getInstance().KEY_SEATING_ORDER_ID, mReselectSeatNewOrderId); // ??????id
        intent.putExtra(App.getInstance().KEY_PAY_ERROR_TYPE, errorType);
        intent.putExtra(App.getInstance().KEY_PAY_ERROR_TITLE, errorTitle);
        intent.putExtra(App.getInstance().KEY_PAY_ERROR_DETAIL, errorDetail);
        intent.putExtra(App.getInstance().KEY_PAY_ERROR_BUTTON_MESSAGE, errorButtonMsg);
        this.startActivity(OrderPayFailedActivity.class, intent);

        if (idNeedFinishActivity) {
            finish();
        }
    }

    protected void paySuccess() {
        Intent intent = new Intent();
        intent.putExtra(App.getInstance().KEY_SEATING_ORDER_ID, mReselectSeatNewOrderId);
        intent.putExtra(App.getInstance().PAY_ETICKET, false);
        intent.putExtra(App.getInstance().KEY_TARGET_ACTIVITY_ID, OrderPayActivity.class.getName());
        startActivity(OrderPaySuccessActivity.class, intent);
    }

    private void requestLastOrderDetailInfo(final String orderId) {
        UIUtil.showLoadingDialog(this);

        // ???????????????????????????????????????
        final RequestCallback orderDetailCallback = new RequestCallback() {
            @Override
            public void onSuccess(final Object o) {
                final TicketDetailBean ticketBean = (TicketDetailBean) o;
                mDId = ticketBean.getShowtimeId();
                mUserBuyTicketPhone = ticketBean.getMobile();
                // ?????????????????????,???????????????????????????????????????
                mCanSelectSeatCount = ticketBean.getQuantity();
                // ??????????????????
                if (mDId != null) {
                    SeatSelectActivity.this.loadTicket(mDId);
                }
            }

            @Override
            public void onFail(final Exception e) {
                UIUtil.dismissLoadingDialog();
                SeatSelectActivity.this.finish();
            }
        };

        // Order/onlineticketdetail.api?orderId={0}
        Map<String, String> param = new HashMap<>(1);
        param.put("orderId", orderId);
        HttpUtil.get(ConstantUrl.ONLINE_TICKET_DATEIL, param, TicketDetailBean.class, orderDetailCallback);
    }

    /**
     * ????????????????????????
     */
    private void gotoOrderPayActivity() {
        final Intent intent = new Intent();
        intent.putExtra(App.getInstance().PAY_ETICKET, false); // ?????????????????????
        intent.putExtra(App.getInstance().KEY_SEATING_ORDER_ID, mWithoutPayOrderId); // ??????id
        intent.putExtra(App.getInstance().KEY_SEATING_TOTAL_PRICE, mTotalPrice); // ??????
        intent.putExtra(App.getInstance().KEY_SEATING_PRICE_INTRODUCTION, mIntroduction);
        intent.putExtra(App.getInstance().KEY_SEATING_SERVICE_FEE, mServiceFee); // ?????????
        intent.putExtra(App.getInstance().KEY_SEATING_PAY_ENDTIME, mPayEndTime); // ??????????????????

        intent.putExtra(App.getInstance().KEY_USER_BUY_TICKET_PHONE, mUserBuyTicketPhone);
        intent.putExtra(App.getInstance().KEY_SEATING_SEAT_ID, mSeatId);
        intent.putExtra(App.getInstance().KEY_SEATING_SELECTED_SEAT_COUNT, ticketMap.keySet().size()); // ?????????
        intent.putExtra(App.getInstance().KEY_SEATING_DID, mDId);
        // ????????????????????????getOrderId()???getSubOrderID()???????????????0
        intent.putExtra(App.getInstance().KEY_SEATING_SUBORDER_ID, mSubOrderId);
        intent.putExtra(App.getInstance().KEY_TICKET_DATE_INFO, mTicketDateInfo);
        intent.putExtra(App.getInstance().KEY_SEAT_SELECTED_INFO, mSeatSelectedInfo);
        intent.putExtra(App.getInstance().IS_FROM_SEATSELECT_ACTIVITY, true);
        this.startActivity(OrderPayActivity.class, intent);
    }

    // ??????????????????
    private void showTicketOutingDialog() {
        if (ticketOutingDialog == null) {
            ticketOutingDialog = new OrderPayTicketOutingDialog(SeatSelectActivity.this);
        }
        if (!ticketOutingDialog.isShowing()) {
            ticketOutingDialog.show();
            ticketOutingDialog.setCancelable(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mTicketApi != null) {
            mTicketApi = null;
        }
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);
        if (arg1 == App.STATUS_LOGIN || arg1 == App.STATUS_NO_VIP) {
            if (arg1 == App.STATUS_NO_VIP) {
                if (App.getInstance().notVipLoginNum != null && App.getInstance().notVipLoginNum.length() > 0) {
                    isFromIsnotVip = true;
                    notVipLoginNum = App.getInstance().notVipLoginNum;
                } else {
                    return;
                }
            }
            isFromLogin = true;
            UIUtil.showLoadingDialog(this);
            doGetWithoutPaymentOnlineSeat();
        }
    }

    public static void launch(Context context, String refer, String seatId, String mCinemaId, String movieId, String passDateString, String excel) {
        Intent intent = new Intent(context, SeatSelectActivity.class);
        intent.putExtra(App.getInstance().KEY_SEATING_DID, seatId);
        intent.putExtra(App.getInstance().KEY_CINEMA_ID, mCinemaId);
        intent.putExtra(App.getInstance().KEY_MOVIE_ID, movieId);
        intent.putExtra(App.getInstance().KEY_SHOWTIME_DATE, passDateString);
        intent.putExtra(App.getInstance().KEY_ACTIVITY_FROM, excel);
        dealRefer(context, refer, intent);
        context.startActivity(intent);
    }
    /***************************************??????????????????end***************************************/
}
