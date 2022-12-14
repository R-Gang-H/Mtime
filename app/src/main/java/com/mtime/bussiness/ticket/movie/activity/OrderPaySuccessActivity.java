package com.mtime.bussiness.ticket.movie.activity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.wallet.hmspass.service.WalletPassApiResponse;
import com.hw.passsdk.WalletPassApi;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.kotlin.android.app.router.ext.AppLinkExtKt;
import com.kotlin.android.bonus.scene.component.BonusSceneExtKt;
import com.kotlin.android.bonus.scene.component.bean.BonusSceneDialogDismissBean;
import com.kotlin.android.film.JavaOpenSeatActivity;
import com.mtime.R;
import com.mtime.account.AccountManager;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.base.utils.MTimeUtils;
import com.mtime.base.utils.MToastUtils;
import com.mtime.beans.ResultBean;
import com.mtime.beans.SuccessBean;
import com.mtime.bussiness.mine.bean.MemberRewards;
import com.mtime.bussiness.splash.LoadManager;
import com.mtime.bussiness.ticket.api.TicketApi;
import com.mtime.bussiness.ticket.cinema.activity.CinemaViewActivity;
import com.mtime.bussiness.ticket.movie.StrToBytesUtils;
import com.mtime.bussiness.ticket.movie.adapter.OrderPaySuccessGoodsListAdapter;
import com.mtime.bussiness.ticket.movie.adapter.ShowExchangeCodeAdapter;
import com.mtime.bussiness.ticket.movie.adapter.TicketDiscountAdapter;
import com.mtime.bussiness.ticket.movie.adapter.TicketRealNameAdapter;
import com.mtime.bussiness.ticket.movie.bean.CancelOrderJsonBean;
import com.mtime.bussiness.ticket.movie.bean.CommodityList;
import com.mtime.bussiness.ticket.movie.bean.ETicketDetailBean;
import com.mtime.bussiness.ticket.movie.bean.GetPayListBean;
import com.mtime.bussiness.ticket.movie.bean.PayCardListBean;
import com.mtime.bussiness.ticket.movie.bean.TicketDetailBean;
import com.mtime.bussiness.ticket.movie.bean.TicketRealNameReservationBean;
import com.mtime.bussiness.ticket.movie.dialog.PhoneDialog;
import com.mtime.bussiness.ticket.movie.widget.HorizontalListView;
import com.mtime.bussiness.ticket.movie.widget.MyListView;
import com.mtime.bussiness.ticket.movie.widget.TicketRealNameDialog;
import com.mtime.common.utils.LogWriter;
import com.mtime.common.utils.TextUtil;
import com.mtime.common.utils.Utils;
import com.mtime.frame.App;
import com.mtime.frame.BaseActivity;
import com.mtime.mtmovie.widgets.recycler.EndlessRecyclerOnScrollListener;
import com.mtime.mtmovie.widgets.recycler.HeaderAndFooterRecyclerViewAdapter;
import com.mtime.mtmovie.widgets.recycler.HeaderSpanSizeLookup;
import com.mtime.mtmovie.widgets.recycler.LoadingFooter;
import com.mtime.mtmovie.widgets.recycler.RecyclerViewStateUtils;
import com.mtime.mtmovie.widgets.recycler.RecyclerViewUtils;
import com.mtime.network.ConstantUrl;
import com.mtime.network.RequestCallback;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.statistic.large.home.StatisticHome;
import com.mtime.util.Base64Util;
import com.mtime.util.CustomAlertDlg;
import com.mtime.util.HttpUtil;
import com.mtime.util.JumpUtil;
import com.mtime.util.MtimeUtils;
import com.mtime.util.RegGiftDlg;
import com.mtime.util.UIUtil;
import com.mtime.util.notification.NotificationUtils;
import com.mtime.widgets.BaseTitleView.ActionType;
import com.mtime.widgets.BaseTitleView.ITitleViewLActListener;
import com.mtime.widgets.BaseTitleView.StructType;
import com.mtime.widgets.NetworkImageView;
import com.mtime.widgets.TimerCountDown;
import com.mtime.widgets.TitleOfNormalView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kotlin.android.router.liveevent.EventKeyExtKt.CLOSE_BONUS_SCENE;

/**
 * ???????????????
 */
public class OrderPaySuccessActivity extends BaseActivity implements OnClickListener {

    // ????????????????????????----------------------------------------------------
    private RequestCallback saveImgCallback = null;
    private ETicketDetailBean eTicket = null;
    private RequestCallback eTicketDetailCallback = null;
    private View exchangView = null;
    // ????????????----------------------------------------------------------
    private boolean mIsETicket = false;
    private String mOrderId = null;
    private TextView money = null;
    private TextView mWorkTime = null;
    private View savePhoto = null;
    private View addToHuawei = null;
    private View saveLine = null;
    private RequestCallback cancelCallback = null;
    private TimerCountDown countDownTimer = null;
    private TicketDetailBean ticketBean = null;
    private RequestCallback orderDetailCallback = null;
    private RequestCallback exchangeCodeCallback = null;


    private View orderTimeView = null;

    private boolean isnotVip = false;
    private String fromActID = "";
    private View orderPhoneView;
    private TextView orderPhoneText;
    private String notVipphoneNum = "";
    private String vipPhoneNum = "";
    private static final String KEY_MOBILE_LAST_TIME_INPUTED = "mobile_last_time_inputed";
    private View orderDetailMain;
    private String mtimeLoginUrl = null;
    private TitleOfNormalView navigationbar;

    private RecyclerView recyclerView;
    private OrderPaySuccessGoodsListAdapter mDataAdapter = null;
    private HeaderAndFooterRecyclerViewAdapter mHeaderAndFooterRecyclerViewAdapter = null;
    private int pageIndex = 1;
    private String movieId;
    private LinearLayout paysuccess_voucherimg;
    private View paysuccess_goods_title;
    private HorizontalListView skusLayoutList;
//    private BannerView mBannerView = null;
//    private AbsBannerAdapter<BuyTicketSuccessADBean.ADItemBean> mAdapter;

    private View shopAfterBuyticket;
    private TextView tvShopAfterBuyticket;
    private Button bt_hour;
    private Button bt_minute;
    private Button bt_sec;
    private Handler handler;
    private View afterBuyTicketPromotion;
    private NetworkImageView niv_promotion;
    private long time;
    private View vip_reward;
    private TextView vip_content;
    //????????????
    private TextView mHotTelTv;
    //???????????????
    private LinearLayout oldExchangeStyle = null;
    private LinearLayout mOnlyOneLayout;
    private TextView mTicketNameTv;
    private TextView mTicketCodeTv;
    private TextView mTelPhoneTv;
    private TextView exchangeTips = null;
    private TextView exchangeBtn = null;
    private MyListView exchange_more = null;
    //??????????????????
    private TextView cancelOrderBtn = null;
    private TextView btnOrder = null;
    private TextView titleInfo = null;
    private TextView minTextView = null;
    private TextView secTextView = null;
    private View timerView = null;
    private LinearLayout timeLayout = null;
    private TextView payTextView = null;
    private boolean mCancel = true;
    //??????????????????????????????
    private TextView orderTextView = null;
    private TextView orderStatusTextView = null;
    private TextView cinemaName = null;
    private TextView cinema_address;
    private TextView movieName = null;
    private TextView movieInfo = null;
    private LinearLayout mSeatLineOneLayout;
    private LinearLayout mSeatLineTwoLayout;
    private TextView mSeatOneTv;
    private TextView mSeatTwoTv;
    private TextView mSeatThreeTv;
    private TextView mSeatFourTv;
    private TextView mSeatFiveTv;
    private TextView mSeatSixTv;
    private TextView mHallOneTv;
    private TextView mHallTwoTv;
    //??????
    private LinearLayout smallPayLin;
    private MyListView mSmallPayLv;
    private TextView vip_notice;
    private View vip_reward_content;
    public static final String SCREEN_ORIGIN = "ticketResult";

    private TicketApi mApi;//????????????????????????SDK????????????
    private WalletPassApi mWalletPassApi;//??????SDK

    // ????????????
    private View mRealNameLayout;
    private RecyclerView mRealNameRv;
    private TicketRealNameAdapter mRealNameAdapter;
    private int mSeatCount = 0;
    private boolean isFirst = true;

    @Override
    protected void onInitVariable() {
        final Intent intent = getIntent();
        mOrderId = intent.getStringExtra(App.getInstance().KEY_SEATING_ORDER_ID);
        mIsETicket = intent.getBooleanExtra(App.getInstance().PAY_ETICKET, false);
        fromActID = intent.getStringExtra(App.getInstance().KEY_TARGET_ACTIVITY_ID);
        isnotVip = intent.getBooleanExtra(App.getInstance().KEY_TARGET_NOT_VIP, false);
        notVipphoneNum = intent.getStringExtra(App.getInstance().KEY_TARGET_NOT_VIP_PHONE);
        if (!isnotVip) {
            if (TextUtils.isEmpty(vipPhoneNum)) {
                if (null != AccountManager.getAccountInfo()) {
                    vipPhoneNum = AccountManager.getAccountInfo().getBindMobile();// ???????????????????????????
                } else {
                    vipPhoneNum = App.getInstance().getPrefsManager()
                            .getString(OrderPaySuccessActivity.KEY_MOBILE_LAST_TIME_INPUTED);
                }
            }
        }

        setPageLabel("orderPaySuccess");
    }

    @Override
    protected void onInitView(final Bundle savedInstanceState) {
        this.setContentView(R.layout.act_order_pay_success);
        View navBar = findViewById(R.id.navigationbar);
        navigationbar = new TitleOfNormalView(this, navBar, StructType.TYPE_NORMAL_SHOW_BACK_TITLE, "????????????", new ITitleViewLActListener() {

            @Override
            public void onEvent(ActionType type, String content) {
                if (ActionType.TYPE_BACK == type) {
                    AppLinkExtKt.openFilm(1);
                    finish();
                }
            }
        });

        recyclerView = findViewById(R.id.paysuccess_recyclerview);
        mDataAdapter = new OrderPaySuccessGoodsListAdapter(this);
        mHeaderAndFooterRecyclerViewAdapter = new HeaderAndFooterRecyclerViewAdapter(mDataAdapter);
        recyclerView.setAdapter(mHeaderAndFooterRecyclerViewAdapter);

        //setLayoutManager
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        manager.setSpanSizeLookup(new HeaderSpanSizeLookup((HeaderAndFooterRecyclerViewAdapter) recyclerView.getAdapter(), manager.getSpanCount()));
        recyclerView.setLayoutManager(manager);

        View headerView = View.inflate(this, R.layout.orderpay_success_header, null);
        RecyclerViewUtils.setHeaderView(recyclerView, headerView);

        recyclerView.setOnScrollListener(mOnScrollListener);
        savePhoto = findViewById(R.id.save);
        addToHuawei = findViewById(R.id.add_huawei);
        saveLine = findViewById(R.id.save_line_huawei);

        //Banner???????????????????????????
//        initBannerView(headerView);

        paysuccess_voucherimg = headerView.findViewById(R.id.paysuccess_voucherimg);
        paysuccess_goods_title = headerView.findViewById(R.id.paysuccess_goods_title);
        paysuccess_goods_title.setVisibility(View.GONE);
        orderDetailMain = headerView.findViewById(R.id.order_detail_main);
        timeLayout = headerView.findViewById(R.id.timer_layout);
        exchange_more = headerView.findViewById(R.id.exchange_more);
        exchange_more.setVisibility(View.GONE);
        oldExchangeStyle = headerView.findViewById(R.id.old_exchange_style);
        exchangeTips = headerView.findViewById(R.id.exchange_tips);
        exchangeBtn = headerView.findViewById(R.id.sent_exchangeCode);
        exchangView = headerView.findViewById(R.id.exchange_view);
        mOnlyOneLayout = headerView.findViewById(R.id.only_one_layout);
        mOnlyOneLayout.setVisibility(View.GONE);
        mTicketNameTv = headerView.findViewById(R.id.ticket_name_tv);
        mTicketCodeTv = headerView.findViewById(R.id.ticket_code_tv);
        mTelPhoneTv = headerView.findViewById(R.id.tel_phone_tv);
        mHotTelTv = headerView.findViewById(R.id.hot_tel_tv);
        mHotTelTv.setOnClickListener(this);

        //???????????????????????????
        afterBuyTicketPromotion = headerView.findViewById(R.id.after_buy_ticket_promotion);
        afterBuyTicketPromotion.setVisibility(View.GONE);
        niv_promotion = headerView.findViewById(R.id.niv_promotion);

        //??????????????????
        vip_reward = headerView.findViewById(R.id.vip_reward);
        vip_reward_content = headerView.findViewById(R.id.vip_reward_content);
        vip_content = headerView.findViewById(R.id.vip_content);
        vip_notice = headerView.findViewById(R.id.vip_notice);


        //???????????????
        shopAfterBuyticket = headerView.findViewById(R.id.shopping_after_buyticket);
        shopAfterBuyticket.setVisibility(View.GONE);
        tvShopAfterBuyticket = headerView.findViewById(R.id.tv_shop_after_buyticket);
        bt_hour = headerView.findViewById(R.id.bt_hour);
        bt_minute = headerView.findViewById(R.id.bt_minute);
        bt_sec = headerView.findViewById(R.id.bt_sec);

        orderPhoneView = headerView.findViewById(R.id.phone_view);
        orderPhoneText = headerView.findViewById(R.id.order_detail_phone);
        // ?????????---------------------------------------------------------
        orderTextView = headerView.findViewById(R.id.textOrder);
        orderStatusTextView = headerView.findViewById(R.id.textOrderStatus);
        cinemaName = headerView.findViewById(R.id.info_title);
        movieName = headerView.findViewById(R.id.movieName);
        movieInfo = headerView.findViewById(R.id.movieInfo);
        money = headerView.findViewById(R.id.textMoney);
        mWorkTime = headerView.findViewById(R.id.order_detail_view_worktime_tv);

        cancelOrderBtn = headerView.findViewById(R.id.cancel_btn);
        orderTimeView = headerView.findViewById(R.id.order_info);
        payTextView = headerView.findViewById(R.id.pay_not_complate);
        timerView = headerView.findViewById(R.id.timer_view);
        btnOrder = headerView.findViewById(R.id.btn_order);
        titleInfo = headerView.findViewById(R.id.titleText);// ?????????????????????????????????????????????
        minTextView = headerView.findViewById(R.id.textMin);
        secTextView = headerView.findViewById(R.id.textSecond);

        smallPayLin = headerView.findViewById(R.id.small_pay_info_lin);
        smallPayLin.setVisibility(View.GONE);
        mSmallPayLv = headerView.findViewById(R.id.small_pay_lv);

        skusLayoutList = headerView.findViewById(R.id.skus_layout_list);

        mSeatLineOneLayout = headerView.findViewById(R.id.seat_line_one_layout);
        mSeatLineTwoLayout = headerView.findViewById(R.id.seat_line_two_layout);
        mSeatOneTv = headerView.findViewById(R.id.seat_one_tv);
        mSeatTwoTv = headerView.findViewById(R.id.seat_two_tv);
        mSeatThreeTv = headerView.findViewById(R.id.seat_three_tv);
        mSeatFourTv = headerView.findViewById(R.id.seat_four_tv);
        mSeatFiveTv = headerView.findViewById(R.id.seat_five_tv);
        mSeatSixTv = headerView.findViewById(R.id.seat_six_tv);
        mHallOneTv = headerView.findViewById(R.id.hall_name_one);
        mHallTwoTv = headerView.findViewById(R.id.hall_name_two);
        cinema_address = headerView.findViewById(R.id.info_title_address);
        TextView mapBtn = headerView.findViewById(R.id.btnMap);
        View layoutCinema = headerView.findViewById(R.id.layout_order_detail_cinema);
        // ????????????
        mRealNameLayout = findViewById(R.id.order_detail_view_real_name_layout);
        mRealNameRv = findViewById(R.id.order_detail_view_real_name_rv);
        if (mRealNameLayout != null) {
            mRealNameLayout.setVisibility(View.GONE);
        }
        if (mRealNameRv != null) {
            mRealNameAdapter = new TicketRealNameAdapter(null);
            mRealNameRv.setLayoutManager(new LinearLayoutManager(this));
            mRealNameRv.setAdapter(mRealNameAdapter);
        }

        mapBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mapClick(view);
            }
        });
        layoutCinema.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                cinemaClick(view);
            }
        });
        loadView();

        mApi = new TicketApi();
        mWalletPassApi = new WalletPassApi(this);
        // ????????????????????????(???????????????
//        showMsg();
    }

    /**
     * ?????????BannerView
     */
//    private void initBannerView(View v) {
//        mBannerView = v.findViewById(R.id.item_buy_ticket_success_ad_banner_view);
//        DefaultBannerIndicator indicator = new DefaultBannerIndicator(this);
//        indicator.setIndicatorRes(R.drawable.icon_dian_normal, R.drawable.icon_dian_focus);
//        int margin = MScreenUtils.dp2px(10);
//        indicator.setSpacing(MScreenUtils.dp2px(4));
//        indicator.setMargin(0, 0, 0, margin);
//        mBannerView.setIndicator(indicator);
//        mBannerView.setIndicatorGravity(Gravity.BOTTOM | Gravity.CENTER);
//        mAdapter = new AbsBannerAdapter<BuyTicketSuccessADBean.ADItemBean>() {
//            @Override
//            public View onCreateView(LayoutInflater inflater, ViewGroup container, int viewType) {
//                return inflater.inflate(R.layout.act_ticket_page_featured, container, false);
//            }
//
//            @Override
//            public void onBindView(View itemView, final int position, final BuyTicketSuccessADBean.ADItemBean itemBean) {
//                ImageView image = itemView.findViewById(R.id.act_ticket_page_featured_bg_iv);
//                TextView advTag = itemView.findViewById(R.id.act_ticket_page_featured_adv_tv);
//                int width = FrameConstant.SCREEN_WIDTH;
//                double b = 175 / 750f;
//                int height = (int) (width * b);
//                ViewPager.LayoutParams params = (ViewPager.LayoutParams) itemView.getLayoutParams();
//                params.height = height;
//                params.width = width;
//                itemView.setLayoutParams(params);
//                ImageHelper.with(ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
//                        .override(width, height)
//                        .view(image)
//                        .placeholder(R.drawable.ad_default_img)
//                        .error(R.drawable.ad_default_img)
//                        .load(itemBean.img)
//                        .showload();
//                advTag.setText(itemBean.advTag);
//                advTag.setVisibility(TextUtils.isEmpty(itemBean.advTag) ? View.GONE : View.VISIBLE);
//                itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        StatisticPageBean bean = submitData(movieId, GlobalDimensionExt.INSTANCE.getCurrentCityId(), (position + 1));
//                        ApplinkManager.jump(OrderPaySuccessActivity.this, itemBean.applinkData, bean.toString());
//                    }
//                });
//            }
//        };
//    }

    /**
     * ??????????????????
     */
    private void requestHuaWeiWalletData() {
        downLoadHuaWeiZipData();
    }

    /**
     * ????????????ZIP??????
     */
    private void downLoadHuaWeiZipData() {
        String path = App.get().getFileStreamPath("download").getPath() + "/huawei.pass";
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        UIUtil.showLoadingDialog(OrderPaySuccessActivity.this);
        mApi.downloadFile("down_load_huawei", ConstantUrl.GET_HUA_WEI_WALLET_INFO + "?orderId=" + mOrderId, file.getPath(), new NetworkManager.NetworkProgressListener<String>() {
            @Override
            public void onProgress(float progress, long done, long total) {

            }

            @Override
            public void onSuccess(String result, String showMsg) {
                byte[] bytes = StrToBytesUtils.getBytes(result);
                Log.e("onSuccess", result);
                if (null != bytes) {
                    String base64ZipData = Base64Util.encode(bytes);
                    WalletPassApiResponse response = mWalletPassApi.addPass(base64ZipData);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UIUtil.dismissLoadingDialog();
                            if (null != response) {
                                if (response.getReturnCode().equals("0")) {
                                    MToastUtils.showShortToast("??????????????????????????????");
                                } else if (response.getReturnCode().equals(App.getInstance().CODE_ADDED)) {
                                    MToastUtils.showShortToast("??????????????????");
                                } else {
                                    MToastUtils.showShortToast("????????????");
                                }
                            } else {
                                MToastUtils.showShortToast("????????????");

                            }
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UIUtil.dismissLoadingDialog();
                            MToastUtils.showShortToast("????????????");
                        }
                    });
                }
            }

            @Override
            public void onFailure(NetworkException<String> exception, String showMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UIUtil.dismissLoadingDialog();
                        MToastUtils.showShortToast("????????????");
                    }
                });
            }
        }, true);
    }


    /**
     * ??????-????????????
     */
    private StatisticPageBean submitData(String movieId, String locationId, int position) {
        Map<String, String> params = new HashMap<>();
        params.put("movieID", movieId);
        params.put("locationID", locationId);
        StatisticPageBean bean = assemble(StatisticHome.TAB_ADS_BANNDER, String.valueOf(position), null, null, "click", null, params);
        StatisticManager.getInstance().submit(bean);
        return bean;
    }

    /**
     * Banner????????????
     */
//    private void setData4Banner(BuyTicketSuccessADBean adBean) {
//        mAdapter.setDatas(adBean.list);
//        mBannerView.setAdapter(mAdapter);
//        mBannerView.setVisibility(View.VISIBLE);
//        mBannerView.startTurn();
//    }

    /**
     * ????????????????????????
     */
    private void showMsg() {
        String hwAppId = LoadManager.getLoadInfo().getHwpassAppId();
        String passTypeId = LoadManager.getLoadInfo().getHwpassTypeIdentifier();
        if (!TextUtils.isEmpty(passTypeId) && !TextUtils.isEmpty(hwAppId)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    WalletPassApiResponse response = mWalletPassApi.canAddPass(hwAppId, passTypeId);
                    String returnCode = response.getReturnCode();
                    boolean support = returnCode.equals("0");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (support) {
                                showNoticeDialog();
                                saveLine.setVisibility(View.VISIBLE);
                                addToHuawei.setVisibility(View.VISIBLE);
                                if (App.getInstance().getPrefsManager().getBoolean(App.getInstance().KEY_HUAWEI_AUTHROIZE, false)) {
                                    requestHuaWeiWalletData();
                                }
                            } else {
                                saveLine.setVisibility(View.GONE);
                                addToHuawei.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }).start();
        }
    }

    private void showNoticeDialog() {
        if (!App.getInstance().getPrefsManager().getBoolean(App.getInstance().KEY_TOAST_APPEAR, false)) {
            App.getInstance().getPrefsManager().putBoolean(App.getInstance().KEY_TOAST_APPEAR, true);
            CustomAlertDlg mDialog = new CustomAlertDlg(this, CustomAlertDlg.TYPE_OK_CANCEL);
            mDialog.show();
            mDialog.setBtnCancelText(getString(R.string.cancel));
            mDialog.setBtnOkText(getString(R.string.well));
            mDialog.setText(getResources().getString(R.string.agree_add_ticket_to_huawei_wallet));
            mDialog.setBtnCancelListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();
                    App.getInstance().getPrefsManager().putBoolean(App.getInstance().KEY_HUAWEI_AUTHROIZE, false);
                }
            });
            mDialog.setBtnOKListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    App.getInstance().getPrefsManager().putBoolean(App.getInstance().KEY_HUAWEI_AUTHROIZE, true);
                    mDialog.dismiss();
                }
            });
        }
    }

    /**
     * ????????????????????????AD
     */
//    private void requestBuyTicketSuccessAD() {
//        if (null != ticketBean) {
//            {
//                String movieId = ticketBean.getMovieId();
//                String locationId = GlobalDimensionExt.INSTANCE.getCurrentCityId();
//                new TicketApi().getADBuyTicketSuccess(locationId, movieId, new NetworkManager.NetworkListener<BuyTicketSuccessADBean>() {
//                    @Override
//                    public void onSuccess(BuyTicketSuccessADBean buy, String s) {
//                        if (null != buy && null != buy && buy.list.size() > 0) {
//                            setData4Banner(buy);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(NetworkException<BuyTicketSuccessADBean> networkException, String s) {
//                        MToastUtils.showShortToast(s);
//                    }
//                });
//            }
//        }
//
//    }

    private final EndlessRecyclerOnScrollListener mOnScrollListener = new EndlessRecyclerOnScrollListener() {

        @Override
        public void onLoadNextPage(View view) {
            super.onLoadNextPage(view);

            LoadingFooter.State state = RecyclerViewStateUtils.getFooterViewState(recyclerView);
            if (state == LoadingFooter.State.Loading) {
                return;
            }
            if (state == LoadingFooter.State.TheEnd) {
                return;
            }

            //RecyclerViewStateUtils.setFooterViewState(OrderPaySuccessActivity.this, recyclerView, 1, LoadingFooter.State.Loading, null);
//            requestRelatedGoodsById();

        }
    };


    /**************************************
     * ?????????????????????????????????
     **************************************/

    private void loadView() {
        if (mIsETicket) {
            navigationbar.setTitleText("????????????");
        } else {
            navigationbar.setTitleText("????????????");
            if (isnotVip) {
                final CustomAlertDlg notvipDialog = new CustomAlertDlg(this, CustomAlertDlg.TYPE_OK_CANCEL);
                notvipDialog.setBtnOKListener(new OnClickListener() {
                    public void onClick(final View v) {
                        notvipDialog.dismiss();
                        doSavePhotoBack();
                    }
                });

                notvipDialog.setBtnCancelListener(new OnClickListener() {
                    public void onClick(final View v) {
                        notvipDialog.dismiss();
                    }
                });
                notvipDialog.show();
                notvipDialog.setText(getResources().getString(R.string.st_notvip_tip));
                if (notvipDialog.getBtnOK() != null) {
                    notvipDialog.getBtnOK().setText("??????");
                }
            }
        }
        if (isnotVip) {
            final String phone = TextUtil.splitTelString(notVipphoneNum);
            orderPhoneText.setText(phone);
        } else {
            final String phone = TextUtil.splitTelString(vipPhoneNum);
            orderPhoneText.setText(phone);
        }
    }

    @Override
    @SuppressLint("SimpleDateFormat")
    protected void onInitEvent() {
        exchangeCodeCallback = new RequestCallback() {
            @Override
            public void onSuccess(final Object o) {
                UIUtil.dismissLoadingDialog();
                final ResultBean bean = (ResultBean) o;
                if (bean.isSuccess()) {
                    // ?????????id???key????????????????????????????????????
                    App.getInstance().getPrefsManager().putBoolean(mOrderId, true);
                    MToastUtils.showShortToast(R.string.toast_send_order_send);
                } else {
                    switch (bean.getStatus()) {
                        case 0: {
                            MToastUtils.showShortToast(R.string.toast_get_order_failed);
                        }
                        break;
                        case 2: {
                            MToastUtils.showShortToast(R.string.toast_send_order_reach_limiation);
                        }
                        break;
                        case 3: {
                            MToastUtils.showShortToast(R.string.toast_order_not_existed);
                        }
                        break;
                        case 4: {
                            MToastUtils.showShortToast(R.string.toast_sms_module_not_existed);
                        }
                        break;
                        default: {
                        }
                    }
                }
            }

            @Override
            public void onFail(final Exception e) {
                UIUtil.dismissLoadingDialog();
                MToastUtils.showShortToast("????????????????????????" + e.getLocalizedMessage());
            }
        };
        eTicketDetailCallback = new RequestCallback() {
            @Override
            public void onSuccess(final Object o) {
                UIUtil.dismissLoadingDialog();
                eTicket = (ETicketDetailBean) o;
                OrderPaySuccessActivity.this.updateUI();
            }

            @Override
            public void onFail(final Exception e) {
                UIUtil.dismissLoadingDialog();
            }
        };

        OnClickListener exchangeClick = new OnClickListener() {
            @Override
            public void onClick(final View v) {

                final boolean alreadyGetExchangeCode = App.getInstance().getPrefsManager()
                        .getBoolean(mOrderId);
                // ???????????????
                if (alreadyGetExchangeCode) {
                    final CustomAlertDlg alertDlg = new CustomAlertDlg(OrderPaySuccessActivity.this, CustomAlertDlg.TYPE_OK);
                    alertDlg.setBtnOKListener(new OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            alertDlg.dismiss();
                        }
                    });
                    alertDlg.show();
                    alertDlg.setText(getResources().getString(R.string.toast_order_send_only_once));
                } else {
                    final CustomAlertDlg alertDlg = new CustomAlertDlg(OrderPaySuccessActivity.this,
                            CustomAlertDlg.TYPE_OK_CANCEL);

                    alertDlg.setBtnCancelListener(new OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            alertDlg.dismiss();
                        }
                    });

                    alertDlg.setBtnOKListener(new OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            UIUtil.showLoadingDialog(OrderPaySuccessActivity.this);
                            Map<String, String> parameterList = new ArrayMap<String, String>(1);
                            if (mIsETicket) {
                                parameterList.put("subOrderId", eTicket.getSubOrderId());
                            } else {
                                parameterList.put("subOrderId", ticketBean.getSubOrderId());
                            }
                            HttpUtil.post(ConstantUrl.SEND_EXCHANGE_SMS, parameterList, ResultBean.class, exchangeCodeCallback);

                            alertDlg.dismiss();
                        }
                    });
                    alertDlg.show();
                    alertDlg.setText(getResources().getString(R.string.toast_order_send_confirm));
                }
            }
        };

        cancelCallback = new RequestCallback() {
            @Override
            public void onSuccess(final Object o) {
                UIUtil.dismissLoadingDialog();
                final CancelOrderJsonBean bean = (CancelOrderJsonBean) o;
                if (bean.isSuccess()) {
                    MToastUtils.showShortToast("??????????????????");
                    OrderPaySuccessActivity.this.finish();
                } else {
                    MToastUtils.showShortToast(bean.getMsg());
                }
            }

            @Override
            public void onFail(final Exception e) {
                UIUtil.dismissLoadingDialog();
                MToastUtils.showShortToast("????????????????????????:" + e.getLocalizedMessage());
                OrderPaySuccessActivity.this.finish();
            }
        };

        orderDetailCallback = new RequestCallback() {
            @Override
            public void onSuccess(final Object o) {
                ticketBean = (TicketDetailBean) o;
                // ????????????
//                if (null != ticketBean) {
//                    requestBuyTicketSuccessAD();
//                }
                mSeatCount = ticketBean == null ? 0 : ticketBean.getQuantity();
                OrderPaySuccessActivity.this.updateUI();
//                //???????????????????????????
//                showRegisterNewGiftDlg();

                //????????????????????????????????????????????????????????????
//                time = System.currentTimeMillis() + 15 * 1000;
//                pollOrderStatus();
                shopAfterBuyticket.setVisibility(View.GONE);
                afterBuyTicketPromotion.setVisibility(View.GONE);

                UIUtil.dismissLoadingDialog();

                //????????????????????????????????????
                getRealNameReservationDetail();

            }

            @Override
            public void onFail(final Exception e) {
                UIUtil.dismissLoadingDialog();
            }
        };

        btnOrder.setOnClickListener(this);
        cancelOrderBtn.setOnClickListener(this);
        exchangeBtn.setOnClickListener(exchangeClick);
        savePhoto.setOnClickListener(this);
        addToHuawei.setOnClickListener(this);
        if (mIsETicket) {
            navigationbar.setShareVisibility(View.GONE);
        } else {
            navigationbar.setShareVisibility(View.GONE);
        }

//        ??????????????????????????????????????????????????????????????????
        LiveEventBus.get(CLOSE_BONUS_SCENE).observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                if (o != null && o instanceof BonusSceneDialogDismissBean) {
                    showLeadToCommentDialog();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isFirst) {
            //????????????????????????????????????
            getRealNameReservationDetail();
        }
        isFirst = false;
    }

    /**
     * ??????????????????
     */
//    private void pollOrderStatus() {
//
//        RequestCallback shopAfterBuyTicketCallback = new RequestCallback() {
//            @Override
//            public void onSuccess(final Object o) {
//                ShopAfterBuyTicketBean shopAfterBuyTicketBean = (ShopAfterBuyTicketBean) o;
//                if (shopAfterBuyTicketBean != null && shopAfterBuyTicketBean.getList() != null) {
//                    showGoodsAfterBuyTicket(shopAfterBuyTicketBean);
//                    if (shopAfterBuyTicketBean.getStatus() == 1 && shopAfterBuyTicketBean.getEntrance() != null) {
//                        ShopAfterBuyTicketBean.EntranceBean entrance = shopAfterBuyTicketBean.getEntrance();
//                        showPromotion(entrance);
//                    } else {
//                        if (System.currentTimeMillis() >= time) {
//                            return;
//                        }
//                        pollOrderStatus();
//
//                    }
//                } else {
//                    shopAfterBuyticket.setVisibility(View.GONE);
//                    afterBuyTicketPromotion.setVisibility(View.GONE);
//                    if (System.currentTimeMillis() >= time) {
//                        return;
//                    }
//                    pollOrderStatus();
//                }
//            }
//
//            @Override
//            public void onFail(final Exception e) {
//                shopAfterBuyticket.setVisibility(View.GONE);
//                afterBuyTicketPromotion.setVisibility(View.GONE);
//                if (System.currentTimeMillis() >= time) {
//                    return;
//                }
//                pollOrderStatus();
//            }
//        };
//        //????????????????????????
//        DispatchAsync.dispatchAsyncDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Map<String, String> param = new HashMap<>(1);
//                param.put("orderId", mOrderId);
//                HttpUtil.get(ConstantUrl.GET_SHOP_AFTER_BUY_TICKET, param, ShopAfterBuyTicketBean.class, shopAfterBuyTicketCallback);
//            }
//        }, 1000);
//
//    }

    /**
     * ???????????????????????????
     */
//    private void showPromotion(final ShopAfterBuyTicketBean.EntranceBean entrance) {
//        if (entrance.getImage() != null && entrance.getUrl() != null && !TextUtils.isEmpty(entrance.getImage()) && !TextUtils.isEmpty(entrance.getUrl())) {
////            niv_promotion.setImageResource(R.drawable.default_image_item_small);
////        } else {
//            afterBuyTicketPromotion.setVisibility(View.VISIBLE);
//            int width = FrameConstant.SCREEN_WIDTH;
//            this.volleyImageLoader.displayNetworkImage(this.volleyImageLoader, entrance.getImage(), niv_promotion, R.drawable.default_image, R.drawable.default_image,
//                    width, Utils.dip2px(this, 260), ImageURLManager.SCALE_TO_FIT, null);
//            niv_promotion.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    //?????????????????????????????????
////                    Intent intent = new Intent();
////                    intent.putExtra(AdvRecommendActivity.KEY_ADVERT_ID, entrance.getUrl());
////                    intent.putExtra(App.getInstance().KEY_SHOWTITLE, true);
////                    intent.setClass(OrderPaySuccessActivity.this, AdvRecommendActivity.class);
////                    startActivity(intent);
//
////                    JumpUtil.startAdvRecommendActivity(OrderPaySuccessActivity.this, assemble().toString(),
////                            entrance.getUrl(), true, true, null, null, -1);
//                    JumpUtil.startCommonWebActivity(OrderPaySuccessActivity.this, entrance.getUrl(), StatisticH5.PN_H5, null,
//                            true, true, true, false, assemble().toString());
//                }
//            });
//        } else {
//            afterBuyTicketPromotion.setVisibility(View.GONE);
//        }
//    }

    /**
     * ???????????????????????????
     */
//    private void showGoodsAfterBuyTicket(final ShopAfterBuyTicketBean shopAfterBuyTicketBean) {
//
//        if (shopAfterBuyTicketBean.getList() != null) {
//
//            List<ShopAfterBuyTicketBean.ListBean> listBeen = shopAfterBuyTicketBean.getList();
//
//            if (listBeen.size() == 0) {
//                shopAfterBuyticket.setVisibility(View.GONE);
//            } else {
//                shopAfterBuyticket.setVisibility(View.VISIBLE);
//            }
//
//            //????????????????????????
//            tvShopAfterBuyticket.setText(shopAfterBuyTicketBean.getTitle());
//
//            //?????????
//            final long time = (shopAfterBuyTicketBean.getEndTime() * 1000);
//            final Date date = MTimeUtils.getLastDiffServerDate();
//            final long dateInSecond = date.getTime();
//
//            //?????????????????????
//            long restTime = time - dateInSecond;
//
//            if (restTime < 0) {
//                restTime = 0;
//            }
//            final long[] timeToCountDown = {restTime / 1000};
//            handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if (timeToCountDown[0] > 0) {
//                        timeToCountDown[0]--;
//                    }
//                    String formatLongToTimeStr = MtimeUtils.formatLongToTimeStr(timeToCountDown[0]);
//                    String[] split = formatLongToTimeStr.split(":");
//                    for (int i = 0; i < split.length; i++) {
//                        if (i == 0) {
//                            bt_hour.setText(split[0]);
//                        }
//                        if (i == 1) {
//                            bt_minute.setText(split[1]);
//                        }
//                        if (i == 2) {
//                            bt_sec.setText(split[2]);
//                        }
//
//                    }
//                    if (timeToCountDown[0] > 0) {
//                        handler.postDelayed(this, 1000);
//                    }
//                }
//            }, 1000);
//
//            if (listBeen.size() > 0 && listBeen.size() <= 3) {
//
//                ShowGoodsAfterBuyTicketAdapter adapter = new ShowGoodsAfterBuyTicketAdapter(OrderPaySuccessActivity.this, listBeen);
//                skusLayoutList.setAdapter(adapter);
//            }
//
//            if (listBeen.size() > 3) {
//                List<ShopAfterBuyTicketBean.ListBean> listBeens = listBeen.subList(0, 3);
//                ShopAfterBuyTicketBean.ListBean bean = new ShopAfterBuyTicketBean.ListBean();
//                listBeens.add(bean);
//                ShowGoodsAfterBuyTicketAdapter adapter = new ShowGoodsAfterBuyTicketAdapter(OrderPaySuccessActivity.this, listBeens);
//                skusLayoutList.setAdapter(adapter);
//            }
//
//            skusLayoutList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    if (position == 3) {
////                        JumpUtil.startPromotionListActivity(OrderPaySuccessActivity.this);
//                    } else {
//                        // ?????????????????????
//                        String url = shopAfterBuyTicketBean.getList().get(position).getUrl();
//                        if (TextUtils.isEmpty(url)) {
//                            long goodsId = shopAfterBuyTicketBean.getList().get(position).getGoodsId();
//                            url = MallUrlHelper.getUrl(MallUrlHelper.MallUrlType.PRODUCT_VIEW, goodsId);
//                        }
////                        Intent intent = new Intent();
////                        intent.putExtra("SHOW_TITLE", true);
////                        intent.putExtra("LOAD_URL", url);
////                        intent.putExtra("DEFAULT_URL", url);
////                        intent.setClass(OrderPaySuccessActivity.this, ProductViewActivity.class);
////                        startActivity(intent);
//                    }
//                }
//            });
//        }
//
//    }
    private void getPayList(final String withoutPayOrderId) {
        Map<String, String> parameterList = new ArrayMap<String, String>(1);
        parameterList.put("orderId", withoutPayOrderId);
        HttpUtil.get(ConstantUrl.GET_PAY_LIST, parameterList, GetPayListBean.class, new RequestCallback() {

            @Override
            public void onSuccess(Object o) {
                GetPayListBean payListBean = (GetPayListBean) o;
                ArrayList<PayCardListBean> cardList = payListBean.getCardList();
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
                    goToPay();
                }
            }

            @Override
            public void onFail(Exception e) {

            }
        });
    }

    private void requestWithLogin(final String withoutPayOrderId, String url) {
        Map<String, String> parameterList = new ArrayMap<String, String>(1);
        parameterList.put("url", url);
        HttpUtil.post(ConstantUrl.GET_COUPON_URL_WITH_LOGIN, parameterList, SuccessBean.class, new RequestCallback() {
            @Override
            public void onSuccess(Object o) {
                SuccessBean bean = (SuccessBean) o;
                if (bean.getSuccess() != null) {
                    if (bean.getSuccess().equalsIgnoreCase("true")) {
                        if (AccountManager.isLogin()) {
                            mtimeLoginUrl = bean.getNewUrl();
                            LogWriter.d("mtimeLoginUrl" + mtimeLoginUrl);
                            goToPay();
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

    private void goToPay() {
        final Intent intent = new Intent();

        intent.putExtra(App.getInstance().PAY_ETICKET, mIsETicket); // ??????????????????
        if (mtimeLoginUrl != null) {
            intent.putExtra(App.getInstance().KEY_MTIME_URL, mtimeLoginUrl);
        }
        intent.putExtra(App.getInstance().KEY_IS_DO_WITH_OUT_PAY_ORDER, true); // ????????????????????????
        intent.putExtra(App.getInstance().KEY_SEATING_ORDER_ID, mOrderId);
        intent.putExtra(App.getInstance().KEY_ISFROM_ACCOUNT, true);
        OrderPaySuccessActivity.this.startActivity(OrderPayActivity.class, intent);
        finish();
    }

    private void updateUI() {
        orderDetailMain.setVisibility(View.VISIBLE);
        payTextView.setText("");
        int orderStatus = 0;
        boolean isReSelectSeat = false;
        if (ticketBean != null) {
            if (ticketBean.getMobile() != null && ticketBean.getMobile().length() > 0) {
                final String phone = TextUtil.splitTelString(ticketBean.getMobile());
                orderPhoneText.setText(phone);

            }
            orderStatus = ticketBean.getOrderStatus();
            isReSelectSeat = ticketBean.isReSelectSeat();
        } else if (eTicket != null) {
            orderStatus = eTicket.getOrderStatus();
        }
        String statusStr = "";

        /**
         * ?????????????????? 0 -??????(??????????????????????????????) 10
         * -????????????(??????FinishOrder???????????????????????????????????????????????????????????????????????????) 20
         * -????????????(?????????FinishOrder????????????????????????????????????????????????????????????????????????????????????) 30
         * -??????(???????????????????????????????????????) 40 -??????(?????????????????????????????????????????????) 100-?????????(??????????????????????????????)
         */
        if (orderStatus == 10) {
            long payEndTime = 0;
            if (ticketBean != null) {
                // ??????????????????
                payEndTime = (ticketBean.getPayEndTime() * 1000);
            } else if (eTicket != null) {
                // ????????????????????????????????????+24??????,??????????????????
                payEndTime = ((eTicket.getCreateTimelong() * 1000) + 86400000);
            }

            final Date date = MTimeUtils.getLastDiffServerDate();
            final long dateInSecond = date.getTime();
            if (dateInSecond < payEndTime) // ?????????
            {
                // ????????????
                timerView.setVisibility(View.VISIBLE);
                btnOrder.setText("????????????");
                exchangView.setVisibility(View.GONE); // ????????????????????????????????????
                cancelOrderBtn.setVisibility(View.VISIBLE);
                initCancelBtn(true);
                savePhoto.setVisibility(View.GONE);
                showTopInfo();
                statusStr = getString(R.string.order_status_wait_pay);
            } else {
                // ????????????
                timerView.setVisibility(View.VISIBLE);
                timeLayout.setVisibility(View.GONE);
                titleInfo.setVisibility(View.VISIBLE);
                titleInfo.setText("??????????????????????????????????????????");
                btnOrder.setVisibility(View.GONE);
                exchangView.setVisibility(View.GONE); // ????????????????????????????????????
                cancelOrderBtn.setVisibility(View.VISIBLE);
                initCancelBtn(true);
                savePhoto.setVisibility(View.GONE);
            }

            // ?????????????????????????????????????????????
            // showTimerIfNeed(ticketBean.getPayEndTime());
            // showCentralInfo();
        } else if (orderStatus == 30) {
            // ??????
            timerView.setVisibility(View.GONE);
            exchangView.setVisibility(View.VISIBLE);
            cancelOrderBtn.setVisibility(View.GONE); // ???????????????????????????????????????????????????
//            savePhoto.setVisibility(View.VISIBLE);
            savePhoto.setVisibility(View.GONE);
            if (!mIsETicket) {
                navigationbar.setShareVisibility(View.GONE);
                statusStr = getString(R.string.order_status_success);
            }
            if ((fromActID != null) && fromActID.equals(OrderPayActivity.class.getName())) {
                orderPhoneView.setVisibility(View.GONE);
            }
        } else if (orderStatus == 0) {
            long payEndTime = 0;
            if (ticketBean != null) {
                // ??????????????????
                payEndTime = (ticketBean.getPayEndTime() * 1000);
            } else if (eTicket != null) {
                // ????????????????????????????????????+24??????,??????????????????
                payEndTime = ((eTicket.getCreateTimelong() * 1000) + 86400000);
            }

            final Date date = MTimeUtils.getLastDiffServerDate();
            final long dateInSecond = date.getTime();
            if (dateInSecond < payEndTime) // ?????????
            {
                timerView.setVisibility(View.VISIBLE);
                btnOrder.setText("????????????");
                exchangView.setVisibility(View.GONE); // ????????????????????????????????????
                cancelOrderBtn.setVisibility(View.VISIBLE);
                initCancelBtn(true);
                savePhoto.setVisibility(View.GONE);
                payTextView.setVisibility(View.GONE);// ????????????"??????????????????"??????VISIBLE
                showTopInfo();
                statusStr = getString(R.string.order_status_wait_pay);
            } else {
                timerView.setVisibility(View.GONE);
                cancelOrderBtn.setVisibility(View.GONE);
                btnOrder.setVisibility(View.GONE);
                savePhoto.setVisibility(View.GONE);
                exchangView.setVisibility(View.GONE);
            }
            showCentralInfo();

        } else if (isReSelectSeat) {
            if (orderStatus == 40) {
                timeLayout.setVisibility(View.VISIBLE);
                // payTextView.setText("????????????????????????????????????????????????????????????????????????");
                payTextView.setText("??????????????????????????????");
                exchangView.setVisibility(View.GONE);
                btnOrder.setVisibility(View.VISIBLE);
                btnOrder.setText("????????????");
                cancelOrderBtn.setVisibility(View.GONE); // ???????????????????????????????????????????????????
                savePhoto.setVisibility(View.GONE);
                showTopInfo();
            } else {
                // ????????????
                timerView.setVisibility(View.VISIBLE);
                btnOrder.setVisibility(View.VISIBLE);
                btnOrder.setText("????????????");
                exchangView.setVisibility(View.GONE); // ????????????????????????????????????
                cancelOrderBtn.setVisibility(View.GONE);
                savePhoto.setVisibility(View.GONE);
                showTopInfo();
            }
            statusStr = getString(R.string.order_status_fail);
            // ?????????????????????????????????????????????
            // showTimerIfNeed(ticketBean.getPayEndTime());
        } else if (orderStatus == 40) {
            timeLayout.setVisibility(View.GONE);
            payTextView.setVisibility(View.VISIBLE);
            cancelOrderBtn.setVisibility(View.VISIBLE);
            initCancelBtn(false);
            if (mIsETicket) {
                navigationbar.setShareVisibility(View.GONE);
                payTextView.setText("????????????????????????\r\n?????????????????????");
            } else {
                navigationbar.setShareVisibility(View.GONE);
                payTextView.setText("????????????????????????????????????????????????");
                statusStr = getString(R.string.order_status_fail);
            }
            timerView.setVisibility(View.VISIBLE);
            btnOrder.setVisibility(View.GONE);
            exchangView.setVisibility(View.GONE);
            savePhoto.setVisibility(View.GONE);
        } else if (orderStatus != 30) // ????????????????????????????????????????????????????????????????????????????????????????????????
        {
            showTopInfo();
        }

        // ????????????????????????????????????????????????????????????
        showCentralInfo();
        // ???????????????????????????????????????????????????
        // showBottomInfo();
        if (ticketBean.getRefundStatus() == 1) {
            statusStr = getString(R.string.order_status_refunding);
        } else if (ticketBean.getRefundStatus() == 2) {
            statusStr = getString(R.string.order_status_refunded);
        }
        if ((ticketBean != null) && !mIsETicket) {
            orderStatusTextView.setText(statusStr);
        }

    }

    private void initCancelBtn(boolean isCancel) {
        if (isCancel) {
            cancelOrderBtn.setText(getResources().getString(R.string.cancelOrder));
            mCancel = true;
        } else {
            cancelOrderBtn.setText(getResources().getString(R.string.contact_us));
            mCancel = false;
        }
    }

    /**
     * ????????????????????????????????????????????????????????????????????????????????????
     */
    private void showTopInfo() {
        long payEndTime = 0;
        if (ticketBean != null) {
            if (ticketBean.isReSelectSeat()) {
                // ??????????????????
                payEndTime = ((ticketBean.getCreateTimelong())) + (1000 * 60 * 60);
            } else {
                // ??????????????????
                payEndTime = (ticketBean.getPayEndTime());
            }
        } else if (eTicket != null) {
            // ????????????????????????????????????+24??????,??????????????????
            payEndTime = ((eTicket.getCreateTimelong()) + 86400000);
        }

        final Date date = MTimeUtils.getLastDiffServerDate();
        final long dateInSecond = date.getTime();

        // ??????????????????
        if ((dateInSecond < payEndTime) && (countDownTimer == null)) {
            countDownTimer = new TimerCountDown(payEndTime - dateInSecond) {
                final long TICKET_COUNT_TIME = 1000 * 60 * 15;
                final long ETICKET_COUNT_TIME = 1000 * 60 * 30;

                @Override
                public void onTimeFinish() {
                    // ????????????????????????
                    if (ticketBean.isReSelectSeat()) {
                        timeLayout.setVisibility(View.GONE);
                        titleInfo.setVisibility(View.GONE);
                        btnOrder.setVisibility(View.GONE);
                        payTextView.setVisibility(View.VISIBLE);
                    } else {
                        OrderPaySuccessActivity.this.handleTimeFinish();
                    }
                }

                @Override
                public void onTickCallBackTo(final String value, final String min, final String sec, final boolean flag) {
                    int color;
                    if (flag) {
                        color = Color.RED;
                    } else {
                        color = OrderPaySuccessActivity.this.getResources().getColor(R.color.color_ff8600);
                    }
                    minTextView.setTextColor(color);
                    minTextView.setText(min);
                    secTextView.setTextColor(color);
                    secTextView.setText(sec);
                }

                @Override
                public void onTickCallBack(final String value, final String min, final String sec) {
                    if (mIsETicket) {
                        if (CLOCK_PAY_END_TIME < ETICKET_COUNT_TIME) {
                            timeLayout.setVisibility(View.VISIBLE);
                            minTextView.setText(min);
                            secTextView.setText(sec);
                        } else {
                            timeLayout.setVisibility(View.GONE);
                            payTextView.setText("??????????????????");
                            payTextView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (CLOCK_PAY_END_TIME < TICKET_COUNT_TIME) {
                            timeLayout.setVisibility(View.VISIBLE);
                            minTextView.setText(min);
                            secTextView.setText(sec);
                        } else {
                            timeLayout.setVisibility(View.GONE);
                            payTextView.setVisibility(View.VISIBLE);
                        }
                    }
                }
            };
            countDownTimer.start();
            timerView.setVisibility(View.VISIBLE);
            timeLayout.setVisibility(View.VISIBLE);
            exchangView.setVisibility(View.GONE);
            savePhoto.setVisibility(View.GONE);
        } else {
            if ((ticketBean != null) && ticketBean.isReSelectSeat()) {
                timeLayout.setVisibility(View.GONE);
                titleInfo.setVisibility(View.GONE);
                btnOrder.setVisibility(View.GONE);
                payTextView.setVisibility(View.VISIBLE);
            } else {
                exchangView.setVisibility(View.VISIBLE);
//                savePhoto.setVisibility(View.VISIBLE);
                savePhoto.setVisibility(View.GONE);
            }
        }
    }

    private void handleTimeFinish() {
        minTextView.setText("0");
        secTextView.setText("0");
        timerView.setVisibility(View.GONE);
    }

    /**
     * ????????????????????????????????????????????????
     */
    private void showCentralInfo() {
        String strCinemaName;
        String strCinemaAddress;
        String movieTitle;
        String versionDesc = null;
        String seatName = null;
        String balanceStr;
        String strMovieInfo;
        Double moviePrice;
        int ticketCount = 0;

        if ((ticketBean != null) && !mIsETicket) {
            strCinemaName = ticketBean.getName();
            movieTitle = ticketBean.getMovieTitle();
            strCinemaAddress = ticketBean.getcAddress();
            versionDesc = ticketBean.getChangeInfo();
            seatName = ticketBean.getSeatName();
            moviePrice = ticketBean.getSalePrice();
            ticketCount = ticketBean.getQuantity();
            MemberRewards memberRewards = ticketBean.getMemberRewards();

            final StringBuffer infoBuf = new StringBuffer();
            infoBuf.append(ticketBean.getShowtime());
            strMovieInfo = infoBuf.toString();

            if (memberRewards != null && !TextUtils.isEmpty(memberRewards.getInfo())) {
                vip_reward.setVisibility(View.VISIBLE);
                vip_reward_content.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        JumpUtil.startMemberCenterActivity(OrderPaySuccessActivity.this);
                    }
                });
                vip_content.setText(memberRewards.getInfo());
                vip_notice.setText(memberRewards.getRemark());
            } else {
                vip_reward.setVisibility(View.GONE);
            }


            // ??????????????????
            if (ticketBean.getBuffetList() != null && ticketBean.getBuffetList().size() > 0) {
                //String smallPayInfoStr = "";
                double totalSmallPayMomnny = 0d;
                List<CommodityList> buffetList = ticketBean.getBuffetList();
                TicketDiscountAdapter discountAdapter = new TicketDiscountAdapter(OrderPaySuccessActivity.this, buffetList);
                mSmallPayLv.setAdapter(discountAdapter);
                for (int i = 0; i < buffetList.size(); i++) {
                    totalSmallPayMomnny += buffetList.get(i).getPrice() * buffetList.get(i).getQuantity();
                }
                smallPayLin.setVisibility(View.VISIBLE);
                if (ticketCount > 0) {
                    balanceStr = MtimeUtils.formatPrice(ticketBean.getSalePrice() * ticketCount
                            + totalSmallPayMomnny);
                } else {
                    balanceStr = MtimeUtils.formatPrice(ticketBean.getSalePrice() + totalSmallPayMomnny);
                }
            } else {
                smallPayLin.setVisibility(View.GONE);
                if (ticketCount > 0) {
                    balanceStr = MtimeUtils.formatPrice(ticketBean.getSalePrice() * ticketCount);
                } else {
                    balanceStr = MtimeUtils.formatPrice(ticketBean.getSalePrice());
                }
            }

            // ???????????????
//            if ((fromActID != null) && fromActID.equals(OrderPayActivity.class.getName())) {
            movieId = ticketBean.getMovieId();
            //RecyclerViewStateUtils.setFooterViewState(OrderPaySuccessActivity.this, recyclerView, 0, LoadingFooter.State.Loading, null);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    requestRelatedGoodsById();
//                }
//            }, 1000);
            //(2021.10.21??????????????????)
//            if (!ReminderMovieShow.getInstance().contains(ticketBean.getOrderId())) {
//                final long twoHours = 1000 * 60 * 60 * 2;
//                final long halfHour = 1000 * 60 * 30;
//                ReminderMovieShow.getInstance().add(
//                        new ReminderMovieBean(ticketBean.getMovieId(), ticketBean.getOrderId(), ticketBean
//                                .getSubOrderId(), ticketBean.getMovieTitle(), ticketBean.getShowtimeLong(),
//                                ticketBean.getMovieLength()));
//
//                final AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                final Intent intent = new Intent(AlarmReceiver.REMINDER_MOVIE_SHOW);
//                intent.putExtra("MovieTitle", ticketBean.getMovieTitle());
//                intent.putExtra("MovieID", ticketBean.getMovieId());
//                // intent.putExtra("MovieSubID",
//                // ticketBean.getSubOrderId());
//                final PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                am.set(AlarmManager.RTC_WAKEUP, (ticketBean.getShowtimeLong()) - twoHours, pi);// ??????????????????????????????
//
//                final Intent intent2 = new Intent(AlarmReceiver.REMINDER_MOVIE_COMMENT);
//                intent2.putExtra("MovieTitle", ticketBean.getMovieTitle());
//                intent2.putExtra("MovieID", ticketBean.getMovieId());
//                final PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
//                am.set(AlarmManager.RTC_WAKEUP,
//                        (ticketBean.getShowtimeLong()) + halfHour + (ticketBean.getMovieLength() * 1000 * 60),
//                        pIntent);// ???????????????????????????????????????????????????
//            }
        } else if ((eTicket != null) && mIsETicket) {
            if ((eTicket.getDesc() == null) || eTicket.getDesc().equals("")) {
                exchangeTips.setVisibility(View.GONE);
                versionDesc = "";
            } else {
                exchangeTips.setVisibility(View.VISIBLE);
                versionDesc = eTicket.getDesc();
            }

            strCinemaName = eTicket.getCinemaName();
            movieTitle = eTicket.getCommodityName() + " X " + eTicket.getQuantity() + " ???";
            strCinemaAddress = eTicket.getcAddress();

            final StringBuffer infoBuf = new StringBuffer();
            infoBuf.append("?????????:");
            infoBuf.append(eTicket.getStartTime());
            infoBuf.append("-");
            infoBuf.append(eTicket.getEndTime());
            strMovieInfo = infoBuf.toString();

            balanceStr = MtimeUtils.formatPrice((eTicket.getDeductedAmount() / 100) + (eTicket.getSalesAmount() / 100));
            if (eTicket.getOrderStatus() == 0) {
                orderTimeView.setVisibility(View.VISIBLE);
            } else {
                orderTimeView.setVisibility(View.GONE);
            }

        } else {
            return;
        }
        orderTextView.setText(mOrderId);
        movieInfo.setText(strMovieInfo);
        updateSeatView(seatName, ticketBean.getHallName());
        if (ticketBean != null && ticketBean.isNewElectronicCode() && ticketBean.getNeoElectronicCode() != null && ticketBean.getPrompt() != null) {
            List<TicketDetailBean.NeoElectronicCodeBean> list = ticketBean.getNeoElectronicCode();
            if (list.size() == 1) {
                exchange_more.setVisibility(View.GONE);
                mOnlyOneLayout.setVisibility(View.VISIBLE);
                mTicketNameTv.setText(list.get(0).getName());
                mTicketCodeTv.setText(list.get(0).getValue());
            } else {
                exchange_more.setVisibility(View.VISIBLE);
                mOnlyOneLayout.setVisibility(View.GONE);
                ShowExchangeCodeAdapter spAdapter = new ShowExchangeCodeAdapter(OrderPaySuccessActivity.this, ticketBean.getNeoElectronicCode());
                exchange_more.setAdapter(spAdapter);
            }
            mTelPhoneTv.setText(getResources().getString(R.string.tel_phone_num) + ticketBean.getMobile());
            exchangeTips.setText(ticketBean.getPrompt());
        }

        if (ticketBean != null && !ticketBean.isNewElectronicCode()) {
            oldExchangeStyle.setVisibility(View.VISIBLE);
            if (versionDesc != null && !"".equals(versionDesc.trim())) {
                exchangeTips.setText("???" + versionDesc + "???");
            } else {
                exchangeTips.setVisibility(View.GONE);
            }
        }

        cinemaName.setText(strCinemaName);
        if (strCinemaAddress != null && !"".equals(strCinemaAddress)) {
            cinema_address.setVisibility(View.VISIBLE);
            cinema_address.setText(strCinemaAddress);
        }
        StringBuffer movieBuf = new StringBuffer();
        movieBuf.append(movieTitle);
        if (!TextUtils.isEmpty(ticketBean.getVersionDesc())) {
            movieBuf.append(" ");
            movieBuf.append(ticketBean.getVersionDesc());
        }
        if (!TextUtils.isEmpty(ticketBean.getLanguage())) {
            movieBuf.append(" ");
            movieBuf.append(ticketBean.getLanguage());
        }
        movieName.setText(movieBuf.toString());
        money.setText(balanceStr);
        // ????????????
        mWorkTime.setText(ticketBean.getOnlineTime());
    }

    private void updateSeatView(String seatName, String hallName) {
        String[] seatList = seatName.split(",");
        int seatCount = seatList.length;
        switch (seatCount) {
            case 1:
                mSeatLineOneLayout.setVisibility(View.VISIBLE);
                mSeatLineTwoLayout.setVisibility(View.GONE);
                mSeatOneTv.setText(seatList[0]);
                mSeatOneTv.setVisibility(View.VISIBLE);
                mSeatTwoTv.setVisibility(View.GONE);
                mSeatThreeTv.setVisibility(View.GONE);
                mSeatFourTv.setVisibility(View.GONE);
                mSeatFiveTv.setVisibility(View.GONE);
                mSeatSixTv.setVisibility(View.GONE);
                mHallOneTv.setText(hallName);
                break;
            case 2:
                mSeatLineOneLayout.setVisibility(View.VISIBLE);
                mSeatLineTwoLayout.setVisibility(View.GONE);
                mSeatLineOneLayout.setVisibility(View.VISIBLE);
                mSeatLineTwoLayout.setVisibility(View.GONE);
                mSeatOneTv.setText(seatList[0]);
                mSeatTwoTv.setText(seatList[1]);
                mSeatOneTv.setVisibility(View.VISIBLE);
                mSeatTwoTv.setVisibility(View.VISIBLE);
                mSeatThreeTv.setVisibility(View.GONE);
                mSeatFourTv.setVisibility(View.GONE);
                mSeatFiveTv.setVisibility(View.GONE);
                mSeatSixTv.setVisibility(View.GONE);
                mHallOneTv.setText(hallName);
                break;
            case 3:
                mSeatLineOneLayout.setVisibility(View.VISIBLE);
                mSeatLineTwoLayout.setVisibility(View.GONE);
                mSeatLineOneLayout.setVisibility(View.VISIBLE);
                mSeatLineTwoLayout.setVisibility(View.GONE);
                mSeatOneTv.setText(seatList[0]);
                mSeatTwoTv.setText(seatList[1]);
                mSeatThreeTv.setText(seatList[2]);
                mSeatOneTv.setVisibility(View.VISIBLE);
                mSeatTwoTv.setVisibility(View.VISIBLE);
                mSeatThreeTv.setVisibility(View.VISIBLE);
                mSeatFourTv.setVisibility(View.GONE);
                mSeatFiveTv.setVisibility(View.GONE);
                mSeatSixTv.setVisibility(View.GONE);
                mHallOneTv.setText(hallName);
                break;
            case 4:
                mSeatLineOneLayout.setVisibility(View.VISIBLE);
                mSeatLineTwoLayout.setVisibility(View.VISIBLE);
                mHallOneTv.setVisibility(View.GONE);
                mSeatOneTv.setText(seatList[0]);
                mSeatTwoTv.setText(seatList[1]);
                mSeatThreeTv.setText(seatList[2]);
                mSeatFourTv.setText(seatList[3]);
                mSeatOneTv.setVisibility(View.VISIBLE);
                mSeatTwoTv.setVisibility(View.VISIBLE);
                mSeatThreeTv.setVisibility(View.VISIBLE);
                mSeatFourTv.setVisibility(View.VISIBLE);
                mSeatFiveTv.setVisibility(View.GONE);
                mSeatSixTv.setVisibility(View.GONE);
                mHallTwoTv.setText(hallName);
                break;
            case 5:
                mSeatLineOneLayout.setVisibility(View.VISIBLE);
                mSeatLineTwoLayout.setVisibility(View.VISIBLE);
                mHallOneTv.setVisibility(View.GONE);
                mSeatOneTv.setText(seatList[0]);
                mSeatTwoTv.setText(seatList[1]);
                mSeatThreeTv.setText(seatList[2]);
                mSeatFourTv.setText(seatList[3]);
                mSeatFiveTv.setText(seatList[4]);
                mSeatOneTv.setVisibility(View.VISIBLE);
                mSeatTwoTv.setVisibility(View.VISIBLE);
                mSeatThreeTv.setVisibility(View.VISIBLE);
                mSeatFourTv.setVisibility(View.VISIBLE);
                mSeatFiveTv.setVisibility(View.VISIBLE);
                mSeatSixTv.setVisibility(View.GONE);
                mHallTwoTv.setText(hallName);
                break;
            case 6:
                mSeatLineOneLayout.setVisibility(View.VISIBLE);
                mSeatLineTwoLayout.setVisibility(View.VISIBLE);
                mHallOneTv.setVisibility(View.GONE);
                mSeatOneTv.setText(seatList[0]);
                mSeatTwoTv.setText(seatList[1]);
                mSeatThreeTv.setText(seatList[2]);
                mSeatFourTv.setText(seatList[3]);
                mSeatFiveTv.setText(seatList[4]);
                mSeatSixTv.setText(seatList[5]);
                mSeatOneTv.setVisibility(View.VISIBLE);
                mSeatTwoTv.setVisibility(View.VISIBLE);
                mSeatThreeTv.setVisibility(View.VISIBLE);
                mSeatFourTv.setVisibility(View.VISIBLE);
                mSeatFiveTv.setVisibility(View.VISIBLE);
                mSeatSixTv.setVisibility(View.VISIBLE);
                mHallTwoTv.setText(hallName);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onLoadData() {

    }

    @Override
    protected void onRequestData() {
        // ?????????????????????????????????
        if (mIsETicket) {
            UIUtil.showLoadingDialog(this);
            // Order/eticketdetail.api?orderId={0}
            Map<String, String> param = new HashMap<>(1);
            param.put("orderId", mOrderId);
            HttpUtil.get(ConstantUrl.GET_ETICKET_DETAIL, param, ETicketDetailBean.class, eTicketDetailCallback);
        } else {
            UIUtil.showLoadingDialog(this);
            // /order/onlineticketdetail.api?orderId={orderId}
            Map<String, String> param = new HashMap<>(1);
            param.put("orderId", mOrderId);
            HttpUtil.get(ConstantUrl.ONLINE_TICKET_DATEIL, param, TicketDetailBean.class, orderDetailCallback);
        }
    }

    @Override
    protected void onUnloadData() {
    }

    /**
     * ?????????????????????
     */
    private void doSavePhoto() {
//        final ProgressDialog creatingPhotoDlg = StrToBytesUtils.createProgressDialog(this, "??????????????????");
        final ProgressDialog savingPhotoDlg = Utils.createProgressDialog(this, "????????????");
        final String savePath = MtimeUtils.DOWNLOAD_FILENAME + Utils.getMd5(mOrderId) + ".png";

        saveImgCallback = new RequestCallback() {

            @Override
            public void onSuccess(final Object o) {
//                if (null != creatingPhotoDlg && creatingPhotoDlg.isShowing()) {
//                    creatingPhotoDlg.dismiss();
//                }

                if (null != savingPhotoDlg && savingPhotoDlg.isShowing()) {
                    savingPhotoDlg.dismiss();
                }

//                try {
//                    final ByteArrayInputStream iStream = new ByteArrayInputStream((byte[]) o);
//                    final Bitmap bitmap = BitmapFactory.decodeStream(iStream);
//                    // ????????????
//                    MtimeUtils.savaBitmap(OrderPaySuccessActivity.this, bitmap, StrToBytesUtils.getMd5(mOrderId) + ".png");
//                } catch (final Exception e) {
//                    MToastUtils.showShortToast("??????????????????,???????????????:" + e.getLocalizedMessage());
//                    return;
//                }

                MToastUtils.showShortToast("??????????????????SD????????????????????????");
                /** ????????????,?????????????????????????????????media database?????????????????????getImages()????????????????????? */
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(new File(savePath));
                mediaScanIntent.setData(contentUri);
                sendBroadcast(mediaScanIntent);
            }

            @Override
            public void onFail(final Exception e) {
                if (null != savingPhotoDlg && savingPhotoDlg.isShowing()) {
                    savingPhotoDlg.dismiss();
                }
                MToastUtils.showShortToast("??????????????????,???????????????:" + e.getLocalizedMessage());
            }
        };
        savingPhotoDlg.show();


        // Ticket/GetTicketOrETicketImage.api?orderId={0}
        Map<String, String> param = new HashMap<>(1);
        param.put("orderId", mOrderId);
        HttpUtil.download(ConstantUrl.GET_TICKET_OR_ETICKET_IMAGE, param, savePath, saveImgCallback);
    }

    /**
     * ?????????????????????-???????????????-????????????????????????
     */
    private void doSavePhotoBack() {
        final String savePath = MtimeUtils.DOWNLOAD_FILENAME + Utils.getMd5(mOrderId) + ".png";

        saveImgCallback = new RequestCallback() {

            @Override
            public void onSuccess(final Object o) {

//                try {
//                    final ByteArrayInputStream iStream = new ByteArrayInputStream((byte[]) o);
//                    final Bitmap bitmap = BitmapFactory.decodeStream(iStream);
//                    MtimeUtils.savaBitmap(OrderPaySuccessActivity.this, bitmap, StrToBytesUtils.getMd5(mOrderId) + ".png");
//                } catch (final Exception e) {
//                    return;
//                }

                PendingIntent pendingIntent = PendingIntent.getActivity(OrderPaySuccessActivity.this, 0,
                        new Intent(), 0);
                new NotificationUtils(OrderPaySuccessActivity.this).sendNotification("???????????????", "????????????????????????????????????????????????????????????????????????", pendingIntent);


                if (canShowDlg) {
                    // ???????????????activity?????????????????????????????????????????????
                    MToastUtils.showShortToast("????????????????????????????????????????????????????????????????????????");
                }

                /** ????????????,?????????????????????????????????media database?????????????????????getImages()????????????????????? */
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(new File(savePath));
                mediaScanIntent.setData(contentUri);
                sendBroadcast(mediaScanIntent);
            }

            @Override
            public void onFail(final Exception e) {
            }
        };

        // Ticket/GetTicketOrETicketImage.api?orderId={0}
        Map<String, String> param = new HashMap<>(1);
        param.put("orderId", mOrderId);
        HttpUtil.download(ConstantUrl.GET_TICKET_OR_ETICKET_IMAGE, param, savePath, saveImgCallback);
    }

    public void mapClick(View v) {
        // ???????????????
//        final Intent intent = new Intent();
        String cinemaId = "";
        String cinemaName = "";
        String cinemaAddress = "";
        double longitude = 0;
        double latitude = 0;
        if (mIsETicket) {
            if (eTicket != null) {
//                intent.putExtra(App.getInstance().KEY_CINEMA_NAME, eTicket.getCinemaName());
//                intent.putExtra(App.getInstance().KEY_CINEMA_ADRESS, eTicket.getcAddress());
//                intent.putExtra(App.getInstance().KEY_MAP_LATITUDE, eTicket.getBaiduLatitude());
//                intent.putExtra(App.getInstance().KEY_MAP_LONGITUDE, eTicket.getBaiduLongitude());
//                intent.putExtra(App.getInstance().KEY_CINEMA_ID, eTicket.getCinemaId());
                cinemaId = eTicket.getCinemaId();
                cinemaName = eTicket.getCinemaName();
                cinemaAddress = eTicket.getcAddress();
                longitude = eTicket.getBaiduLongitude();
                latitude = eTicket.getBaiduLatitude();
            }
        } else {
//            intent.putExtra(App.getInstance().KEY_MAP_LATITUDE, ticketBean.getBaiduLatitude());
//            intent.putExtra(App.getInstance().KEY_MAP_LONGITUDE, ticketBean.getBaiduLongitude());
//            intent.putExtra(App.getInstance().KEY_CINEMA_ID, ticketBean.getCinemaId());
//            intent.putExtra(App.getInstance().KEY_CINEMA_NAME, ticketBean.getCname());
//            intent.putExtra(App.getInstance().KEY_CINEMA_ADRESS, ticketBean.getcAddress());
            cinemaId = ticketBean.getCinemaId();
            cinemaName = ticketBean.getCname();
            cinemaAddress = ticketBean.getcAddress();
            longitude = ticketBean.getBaiduLongitude();
            latitude = ticketBean.getBaiduLatitude();
        }

//        OrderPaySuccessActivity.this.startActivity(MapViewActivity.class, intent);
        JumpUtil.startMapViewActivity(OrderPaySuccessActivity.this, longitude, latitude, cinemaId, cinemaName, cinemaAddress, "");
    }

    public void cinemaClick(View v) {
        Intent intent = new Intent();
        if (mIsETicket) {
            if (eTicket != null) {
                intent.putExtra(App.getInstance().KEY_CINEMA_ID, eTicket.getCinemaId());
            }
        } else {
            if (ticketBean != null) {
                intent.putExtra(App.getInstance().KEY_CINEMA_ID, ticketBean.getCinemaId());
            }
        }
        startActivity(CinemaViewActivity.class, intent);
    }

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AppLinkExtKt.openHome();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


//    private void requestRelatedGoodsById() {
//        if (TextUtils.isEmpty(movieId)) {
//            return;
//        }
//        // Search/RelatedGoodsById.api?relatedId={0}&relatedObjType={1}&orderId={2}&pageIndex={3}
//        Map<String, String> param = new HashMap<>(4);
//        param.put("relatedId", movieId);
//        param.put("relatedObjType", "1");
//        param.put("orderId", mOrderId);
//        param.put("pageIndex", String.valueOf(pageIndex));
//        HttpUtil.get(ConstantUrl.GET_RELATEDGOODSBYID, param, RelatedGoods.class, new RequestCallback() {
//
//            @Override
//            public void onFail(Exception e) {
//            }
//
//            @Override
//            public void onSuccess(Object o) {
//                RecyclerViewStateUtils.setFooterViewState(recyclerView, LoadingFooter.State.Normal);
//                RelatedGoods relatedGoods = (RelatedGoods) o;
//                if (relatedGoods == null) {
//                    return;
//                }
//                if (relatedGoods.getGoodsList() != null && relatedGoods.getGoodsList().size() > 0) {
//                    List<GoodsListBean> dataList = relatedGoods.getGoodsList();
//                    if (dataList != null && dataList.size() > 0) {
//                        mDataAdapter.addAll(dataList);
//                    } else {
//                        RecyclerViewStateUtils.setFooterViewState(OrderPaySuccessActivity.this, recyclerView, 0, LoadingFooter.State.TheEnd, null);
//                    }
//                    if (mDataAdapter.getItemCount() > 0) {
//                        paysuccess_goods_title.setVisibility(View.VISIBLE);
//                    }
//                    pageIndex++;
//                } else {
//                    RecyclerViewStateUtils.setFooterViewState(OrderPaySuccessActivity.this, recyclerView, 0, LoadingFooter.State.TheEnd, null);
//                    recyclerView.scrollToPosition(0);
//                }
//                if (relatedGoods.getVoucherimg().size() > 0) {
//                    paysuccess_voucherimg.setVisibility(View.VISIBLE);
//                } else {
//                    paysuccess_voucherimg.setVisibility(View.GONE);
//                }
//
//                if (paysuccess_voucherimg.getChildCount() == 0 && relatedGoods.getVoucherimg() != null && relatedGoods.getVoucherimg().size() > 0) {
//                    for (int i = 0; i < relatedGoods.getVoucherimg().size(); i++) {
//                        View voucherImg = View.inflate(OrderPaySuccessActivity.this, R.layout.orderpay_success_voucher_item, null);
//                        final ImageView voucherImgView = voucherImg.findViewById(R.id.orderpay_success_voucher_item_img);
//                        TextView voucherTip = voucherImg.findViewById(R.id.orderpay_success_voucher_item_tip);
//                        volleyImageLoader.displayOriginalImg(relatedGoods.getVoucherimg().get(i), voucherImgView, new ImageLoader.ImageListener() {
//                            @Override
//                            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
//                                if (imageContainer != null && imageContainer.getBitmap() != null) {
//                                    int width = FrameConstant.SCREEN_WIDTH - MScreenUtils.dp2px(OrderPaySuccessActivity.this, 30);
//                                    int height = (int) (imageContainer.getBitmap().getHeight() * (width + 0.0f) / ((imageContainer.getBitmap().getWidth() + 0.0f)));
//                                    ViewGroup.LayoutParams para = voucherImgView.getLayoutParams();
//                                    para.width = width;
//                                    para.height = height;
//                                    voucherImgView.setLayoutParams(para);
//                                    voucherImgView.setImageBitmap(imageContainer.getBitmap());
//                                }
//                            }
//
//                            @Override
//                            public void onErrorResponse(VolleyError volleyError) {
//
//                            }
//                        });
//
//
//                        if (i == relatedGoods.getVoucherimg().size() - 1) {
//                            if (!TextUtils.isEmpty(relatedGoods.getVoucherMessage())) {
//                                voucherTip.setText(relatedGoods.getVoucherMessage());
//                                voucherTip.setVisibility(View.VISIBLE);
//                            } else {
//                                voucherTip.setVisibility(View.VISIBLE);
//                            }
//                        } else {
//                            voucherTip.setVisibility(View.GONE);
//                        }
//                        paysuccess_voucherimg.addView(voucherImg);
//                    }
//                }
//
//            }
//        });
//    }

    //???????????????????????????,??????????????????????????????????????????????????????????????????????????????????????????
    private void showRegisterNewGiftDlg() {
        if (App.getInstance().REGISTER_DLG_NEWGIFT_IMG != null && !TextUtils.isEmpty(App.getInstance().REGISTER_DLG_NEWGIFT_IMG)) {
            final RegGiftDlg giftDlg = new RegGiftDlg(this, App.getInstance().REGISTER_DLG_NEWGIFT_IMG);
            giftDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (ticketBean.getRedPacket() != null && !MtimeUtils.isNull(ticketBean.getRedPacket().getUrl())) {
                        MtimeUtils.showRedPacketDlg(ticketBean.getRedPacket(), OrderPaySuccessActivity.this, 1);
                    } else {
                        showLeadToCommentDialog();
                    }
                }
            });
            giftDlg.show();

            App.getInstance().REGISTER_DLG_NEWGIFT_IMG = null;
        } else if (ticketBean.getRedPacket() != null && !MtimeUtils.isNull(ticketBean.getRedPacket().getUrl())) {
            MtimeUtils.showRedPacketDlg(ticketBean.getRedPacket(), OrderPaySuccessActivity.this, 1);
        } else {
            showLeadToCommentDialog();
        }
    }

    /**
     * ???????????????????????????????????????
     */
    private void showLeadToCommentDialog() {
        String version_code = App.getInstance().getPrefsManager().getString("VERSION_CODE");
        String version = MtimeUtils.getVersion(this);
        if (!TextUtils.equals(version, version_code)) {
            //?????????????????????????????????
            MtimeUtils.showLeadToCommentDialog(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (null != mApi) {
            mApi.cancel();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hot_tel_tv:
                showHotPhoneDialog();
                break;
            case R.id.cancel_btn:
                if (mCancel) {
                    final CustomAlertDlg dlg = new CustomAlertDlg(OrderPaySuccessActivity.this, CustomAlertDlg.TYPE_OK_CANCEL);
                    dlg.setBtnCancelListener(new OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            dlg.dismiss();
                        }
                    });
                    dlg.setBtnOKListener(new OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            if (mOrderId != null) {
                                UIUtil.showLoadingDialog(OrderPaySuccessActivity.this);
                                Map<String, String> parameterList = new ArrayMap<String, String>(1);
                                parameterList.put("orderId", mOrderId);
                                HttpUtil.post(ConstantUrl.CANCEL_ORDER, parameterList, CancelOrderJsonBean.class, cancelCallback);
                            }
                            dlg.dismiss();
                        }
                    });
                    dlg.show();
                    dlg.setText("?????????????????????");
                } else {
                    showHotPhoneDialog();
                }
                break;
            case R.id.btn_order:
                if ((ticketBean != null) && ticketBean.isReSelectSeat()) {
                    // ???????????????????????????
                    Intent intent = new Intent();
                    intent.putExtra(App.getInstance().PAY_ETICKET, false); // ?????????????????????
                    // ??????????????????????????????????????????????????????
                    intent.putExtra(App.getInstance().KEY_SEATING_DID, ticketBean.getShowtimeId());
                    intent.putExtra(App.getInstance().KEY_SEATING_SELECT_AGAIN, true);// ??????????????????
                    intent.putExtra(App.getInstance().KEY_MOVIE_NAME, ticketBean.getMovieTitle());
                    intent.putExtra(App.getInstance().KEY_CINEMA_NAME, ticketBean.getCname());
                    intent.putExtra(App.getInstance().KEY_MOVIE_SHOW_DAY_LONG_TIME, ticketBean.getShowtimeLong());
                    intent.putExtra(App.getInstance().KEY_CINEMA_HALL, ticketBean.getHallName());
                    intent.putExtra(App.getInstance().KEY_MOVIE_VERSION_DESC, ticketBean.getVersionDesc());
                    intent.putExtra(App.getInstance().KEY_MOVIE_LANGUAGE, ticketBean.getLanguage());
                    intent.putExtra(App.getInstance().KEY_CINEMA_PHONE, ticketBean.getCtel());
                    intent.putExtra(App.getInstance().KEY_USER_BUY_TICKET_PHONE, ticketBean.getMobile());

                    // ?????????????????????????????????????????????????????????????????????????????????
                    intent.putExtra(App.getInstance().KEY_SEATING_LAST_ORDER_ID, ticketBean.getOrderId());
                    intent.putExtra(App.getInstance().KEY_SEATING_SELECTED_SEAT_COUNT, ticketBean.getQuantity());
//                    OrderPaySuccessActivity.this.startActivity(SeatSelectActivity.class, intent);

                    JavaOpenSeatActivity.INSTANCE.openSeatActivity(ticketBean.getShowtimeId(), ticketBean.getOrderId(), null, null, null);
                } else {
                    getPayList(mOrderId);
                }
                break;
            case R.id.save:
                if (mOrderId != null) {
                    // ?????????????????????
                    OrderPaySuccessActivity.this.doSavePhoto();
                }
                break;
            case R.id.add_huawei:
                if (mOrderId != null)
                    requestHuaWeiWalletData();
                break;
            default:
                break;
        }
    }

    private void showHotPhoneDialog() {
        PhoneDialog dialog = new PhoneDialog(this);
        dialog.showCallPhoneDlg();
    }

    /**
     * ??????????????????
     * ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     */
    private void showBonusSceneDialog() {
        BonusSceneExtKt.postPaySuccess();
    }

    /**
     * ????????????????????????????????????
     */
    private void getRealNameReservationDetail() {
        UIUtil.showLoadingDialog(this);
        mApi.getRealNameReservationDetail(mOrderId, new NetworkManager.NetworkListener<TicketRealNameReservationBean>() {
            @Override
            public void onSuccess(TicketRealNameReservationBean result, String showMsg) {
                UIUtil.dismissLoadingDialog();
                if (mRealNameLayout == null) {
                    mRealNameLayout = findViewById(R.id.order_detail_view_real_name_layout);
                }
                if (result == null || !result.isNeed()) {
                    showBonusSceneDialog();
                    if (mRealNameLayout != null) {
                        mRealNameLayout.setVisibility(View.GONE);
                    }
                    return;
                }
                // ????????????????????????
                if (CollectionUtils.isEmpty(result.getRealNameInfoList())) {
                    if (mRealNameLayout != null) {
                        mRealNameLayout.setVisibility(View.GONE);
                    }
                    // ??????
                    TicketRealNameDialog dlg = new TicketRealNameDialog();
                    dlg.setData(mOrderId, mSeatCount);
                    dlg.show(getSupportFragmentManager());
                    dlg.setCloseListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showBonusSceneDialog();
                        }
                    });
                } else {
                    // ????????????????????????
                    if (mRealNameLayout != null) {
                        mRealNameLayout.setVisibility(View.VISIBLE);
                    }
                    if (mRealNameRv == null) {
                        mRealNameRv = findViewById(R.id.order_detail_view_real_name_rv);
                    }
                    if (mRealNameAdapter == null) {
                        mRealNameAdapter = new TicketRealNameAdapter(null);
                        mRealNameRv.setLayoutManager(new LinearLayoutManager(OrderPaySuccessActivity.this));
                        mRealNameRv.setAdapter(mRealNameAdapter);
                    }
                    mRealNameAdapter.setNewData(result.getRealNameInfoList());
//                    ?????????
                    showBonusSceneDialog();
                }
            }

            @Override
            public void onFailure(NetworkException<TicketRealNameReservationBean> exception, String showMsg) {
                UIUtil.dismissLoadingDialog();
                showBonusSceneDialog();
                if (mRealNameLayout != null) {
                    mRealNameLayout.setVisibility(View.GONE);
                }
            }
        });
    }

}
