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
 * 购票成功页
 */
public class OrderPaySuccessActivity extends BaseActivity implements OnClickListener {

    // 电子券模块的组件----------------------------------------------------
    private RequestCallback saveImgCallback = null;
    private ETicketDetailBean eTicket = null;
    private RequestCallback eTicketDetailCallback = null;
    private View exchangView = null;
    // 订单详情----------------------------------------------------------
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
    //影片详情
    private TextView mHotTelTv;
    //取票码展示
    private LinearLayout oldExchangeStyle = null;
    private LinearLayout mOnlyOneLayout;
    private TextView mTicketNameTv;
    private TextView mTicketCodeTv;
    private TextView mTelPhoneTv;
    private TextView exchangeTips = null;
    private TextView exchangeBtn = null;
    private MyListView exchange_more = null;
    //付款状态展示
    private TextView cancelOrderBtn = null;
    private TextView btnOrder = null;
    private TextView titleInfo = null;
    private TextView minTextView = null;
    private TextView secTextView = null;
    private View timerView = null;
    private LinearLayout timeLayout = null;
    private TextView payTextView = null;
    private boolean mCancel = true;
    //订单影片详细信息展示
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
    //小卖
    private LinearLayout smallPayLin;
    private MyListView mSmallPayLv;
    private TextView vip_notice;
    private View vip_reward_content;
    public static final String SCREEN_ORIGIN = "ticketResult";

    private TicketApi mApi;//下载服务器给华为SDK的文件流
    private WalletPassApi mWalletPassApi;//华为SDK

    // 实名身份
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
                    vipPhoneNum = AccountManager.getAccountInfo().getBindMobile();// 获取用户绑定的手机
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
        navigationbar = new TitleOfNormalView(this, navBar, StructType.TYPE_NORMAL_SHOW_BACK_TITLE, "购票成功", new ITitleViewLActListener() {

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

        //Banner的处理（轮播广告）
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

        //购票送自提商品活动
        afterBuyTicketPromotion = headerView.findViewById(R.id.after_buy_ticket_promotion);
        afterBuyTicketPromotion.setVisibility(View.GONE);
        niv_promotion = headerView.findViewById(R.id.niv_promotion);

        //会员奖励模块
        vip_reward = headerView.findViewById(R.id.vip_reward);
        vip_reward_content = headerView.findViewById(R.id.vip_reward_content);
        vip_content = headerView.findViewById(R.id.vip_content);
        vip_notice = headerView.findViewById(R.id.vip_notice);


        //购票后购物
        shopAfterBuyticket = headerView.findViewById(R.id.shopping_after_buyticket);
        shopAfterBuyticket.setVisibility(View.GONE);
        tvShopAfterBuyticket = headerView.findViewById(R.id.tv_shop_after_buyticket);
        bt_hour = headerView.findViewById(R.id.bt_hour);
        bt_minute = headerView.findViewById(R.id.bt_minute);
        bt_sec = headerView.findViewById(R.id.bt_sec);

        orderPhoneView = headerView.findViewById(R.id.phone_view);
        orderPhoneText = headerView.findViewById(R.id.order_detail_phone);
        // 电影票---------------------------------------------------------
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
        titleInfo = headerView.findViewById(R.id.titleText);// 在选座失败的时候，用来提示信息
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
        // 实名身份
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
        // 提醒用户类似授权(华为钱包）
//        showMsg();
    }

    /**
     * 初始化BannerView
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
     * 请求华为钱包
     */
    private void requestHuaWeiWalletData() {
        downLoadHuaWeiZipData();
    }

    /**
     * 下载华为ZIP数据
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
                                    MToastUtils.showShortToast("已成功添加到华为钱包");
                                } else if (response.getReturnCode().equals(App.getInstance().CODE_ADDED)) {
                                    MToastUtils.showShortToast("该凭证已存在");
                                } else {
                                    MToastUtils.showShortToast("添加失败");
                                }
                            } else {
                                MToastUtils.showShortToast("添加失败");

                            }
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UIUtil.dismissLoadingDialog();
                            MToastUtils.showShortToast("添加失败");
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
                        MToastUtils.showShortToast("添加失败");
                    }
                });
            }
        }, true);
    }


    /**
     * 广告-数据上报
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
     * Banner设置数据
     */
//    private void setData4Banner(BuyTicketSuccessADBean adBean) {
//        mAdapter.setDatas(adBean.list);
//        mBannerView.setAdapter(mAdapter);
//        mBannerView.setVisibility(View.VISIBLE);
//        mBannerView.startTurn();
//    }

    /**
     * 提醒用户类似授权
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
     * 请求购票成功后的AD
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
     * 下面是原来代码，可重构
     **************************************/

    private void loadView() {
        if (mIsETicket) {
            navigationbar.setTitleText("购券成功");
        } else {
            navigationbar.setTitleText("购票成功");
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
                    notvipDialog.getBtnOK().setText("保存");
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
                    // 以订单id为key，本地保存是否已经发送过
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
                MToastUtils.showShortToast("请求兑换码出错！" + e.getLocalizedMessage());
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
                // 已经发送过
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
                    MToastUtils.showShortToast("取消订单成功");
                    OrderPaySuccessActivity.this.finish();
                } else {
                    MToastUtils.showShortToast(bean.getMsg());
                }
            }

            @Override
            public void onFail(final Exception e) {
                UIUtil.dismissLoadingDialog();
                MToastUtils.showShortToast("取消订单出现错误:" + e.getLocalizedMessage());
                OrderPaySuccessActivity.this.finish();
            }
        };

        orderDetailCallback = new RequestCallback() {
            @Override
            public void onSuccess(final Object o) {
                ticketBean = (TicketDetailBean) o;
                // 轮播广告
//                if (null != ticketBean) {
//                    requestBuyTicketSuccessAD();
//                }
                mSeatCount = ticketBean == null ? 0 : ticketBean.getQuantity();
                OrderPaySuccessActivity.this.updateUI();
//                //弹注册新手礼包图片
//                showRegisterNewGiftDlg();

                //轮询购票送自提商品活动（包含加价购活动）
//                time = System.currentTimeMillis() + 15 * 1000;
//                pollOrderStatus();
                shopAfterBuyticket.setVisibility(View.GONE);
                afterBuyTicketPromotion.setVisibility(View.GONE);

                UIUtil.dismissLoadingDialog();

                //获取购票订单实名预约信息
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

//        随机彩蛋弹框关闭后，需要出发是否显示评价弹框
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
            //获取购票订单实名预约信息
            getRealNameReservationDetail();
        }
        isFirst = false;
    }

    /**
     * 轮询订单状态
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
//        //每隔一秒轮询一次
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
     * 购票送自提商品活动
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
//                    //跳转衍生品自提订单页面
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
     * 展示购票后购物商品
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
//            //显示优惠活动名称
//            tvShopAfterBuyticket.setText(shopAfterBuyTicketBean.getTitle());
//
//            //倒计时
//            final long time = (shopAfterBuyTicketBean.getEndTime() * 1000);
//            final Date date = MTimeUtils.getLastDiffServerDate();
//            final long dateInSecond = date.getTime();
//
//            //剩余倒计时时间
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
//                        // 打开商品详情页
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
                        // 如果是影院会员卡付款
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
                        MToastUtils.showShortToast("登录失败，请重新登录后重试！");
                    }
                } else {
                    MToastUtils.showShortToast("登录失败，请重新登录后重试！");
                }

            }

            @Override
            public void onFail(Exception e) {
                MToastUtils.showShortToast("请求数据失败，请稍后重试！");
            }
        });
    }

    private void goToPay() {
        final Intent intent = new Intent();

        intent.putExtra(App.getInstance().PAY_ETICKET, mIsETicket); // 是购买电子券
        if (mtimeLoginUrl != null) {
            intent.putExtra(App.getInstance().KEY_MTIME_URL, mtimeLoginUrl);
        }
        intent.putExtra(App.getInstance().KEY_IS_DO_WITH_OUT_PAY_ORDER, true); // 是处理未支付订单
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
         * 主订单状态： 0 -新建(此时订单对用户不可见) 10
         * -创建成功(调用FinishOrder时所有子订单创建成功，此后主订单再不允许添加子订单) 20
         * -创建失败(调用了FinishOrder时部分或全部子订单创建失败，此后主订单再不允许添加子订单) 30
         * -成功(已支付，且所有子订单都成功) 40 -失败(已支付，但部分或全部子订单失败) 100-已取消(用户在支付前主动取消)
         */
        if (orderStatus == 10) {
            long payEndTime = 0;
            if (ticketBean != null) {
                // 减去时区调整
                payEndTime = (ticketBean.getPayEndTime() * 1000);
            } else if (eTicket != null) {
                // 电子券过期时间为创建时间+24小时,减去时区调整
                payEndTime = ((eTicket.getCreateTimelong() * 1000) + 86400000);
            }

            final Date date = MTimeUtils.getLastDiffServerDate();
            final long dateInSecond = date.getTime();
            if (dateInSecond < payEndTime) // 倒计时
            {
                // 创建成功
                timerView.setVisibility(View.VISIBLE);
                btnOrder.setText("立即付款");
                exchangView.setVisibility(View.GONE); // 显示倒计时则不显示兑换码
                cancelOrderBtn.setVisibility(View.VISIBLE);
                initCancelBtn(true);
                savePhoto.setVisibility(View.GONE);
                showTopInfo();
                statusStr = getString(R.string.order_status_wait_pay);
            } else {
                // 创建成功
                timerView.setVisibility(View.VISIBLE);
                timeLayout.setVisibility(View.GONE);
                titleInfo.setVisibility(View.VISIBLE);
                titleInfo.setText("支付超时，取消订单释放优惠券");
                btnOrder.setVisibility(View.GONE);
                exchangView.setVisibility(View.GONE); // 显示倒计时则不显示兑换码
                cancelOrderBtn.setVisibility(View.VISIBLE);
                initCancelBtn(true);
                savePhoto.setVisibility(View.GONE);
            }

            // 如果支付时间没过期则显示倒计时
            // showTimerIfNeed(ticketBean.getPayEndTime());
            // showCentralInfo();
        } else if (orderStatus == 30) {
            // 成功
            timerView.setVisibility(View.GONE);
            exchangView.setVisibility(View.VISIBLE);
            cancelOrderBtn.setVisibility(View.GONE); // 显示兑换码则不显示“取消订单”按钮
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
                // 减去时区调整
                payEndTime = (ticketBean.getPayEndTime() * 1000);
            } else if (eTicket != null) {
                // 电子券过期时间为创建时间+24小时,减去时区调整
                payEndTime = ((eTicket.getCreateTimelong() * 1000) + 86400000);
            }

            final Date date = MTimeUtils.getLastDiffServerDate();
            final long dateInSecond = date.getTime();
            if (dateInSecond < payEndTime) // 倒计时
            {
                timerView.setVisibility(View.VISIBLE);
                btnOrder.setText("立即付款");
                exchangView.setVisibility(View.GONE); // 显示倒计时则不显示兑换码
                cancelOrderBtn.setVisibility(View.VISIBLE);
                initCancelBtn(true);
                savePhoto.setVisibility(View.GONE);
                payTextView.setVisibility(View.GONE);// 以前显示"尚未完成付款"并且VISIBLE
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
                // payTextView.setText("已支付，因操作超时座位已经被释放，请联系客服退款");
                payTextView.setText("出票失败，请重新选座");
                exchangView.setVisibility(View.GONE);
                btnOrder.setVisibility(View.VISIBLE);
                btnOrder.setText("重新选座");
                cancelOrderBtn.setVisibility(View.GONE); // 显示兑换码则不显示“取消订单”按钮
                savePhoto.setVisibility(View.GONE);
                showTopInfo();
            } else {
                // 重新选座
                timerView.setVisibility(View.VISIBLE);
                btnOrder.setVisibility(View.VISIBLE);
                btnOrder.setText("重新选座");
                exchangView.setVisibility(View.GONE); // 显示倒计时则不显示兑换码
                cancelOrderBtn.setVisibility(View.GONE);
                savePhoto.setVisibility(View.GONE);
                showTopInfo();
            }
            statusStr = getString(R.string.order_status_fail);
            // 如果支付时间没过期则显示倒计时
            // showTimerIfNeed(ticketBean.getPayEndTime());
        } else if (orderStatus == 40) {
            timeLayout.setVisibility(View.GONE);
            payTextView.setVisibility(View.VISIBLE);
            cancelOrderBtn.setVisibility(View.VISIBLE);
            initCancelBtn(false);
            if (mIsETicket) {
                navigationbar.setShareVisibility(View.GONE);
                payTextView.setText("已支付，出券失败\r\n请联系客服退款");
            } else {
                navigationbar.setShareVisibility(View.GONE);
                payTextView.setText("已支付，出票失败，请联系客服退款");
                statusStr = getString(R.string.order_status_fail);
            }
            timerView.setVisibility(View.VISIBLE);
            btnOrder.setVisibility(View.GONE);
            exchangView.setVisibility(View.GONE);
            savePhoto.setVisibility(View.GONE);
        } else if (orderStatus != 30) // 显示顶部的订单状态信息（定时器、新建、交易成功、超时、已取消等）
        {
            showTopInfo();
        }

        // 显示中部信息（兑换码、电影院、影片信息）
        showCentralInfo();
        // 显示底部信息（保存相册、取消订单）
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
     * 显示顶部的订单状态信息（新建、交易成功、超时、已取消等）
     */
    private void showTopInfo() {
        long payEndTime = 0;
        if (ticketBean != null) {
            if (ticketBean.isReSelectSeat()) {
                // 减去时区调整
                payEndTime = ((ticketBean.getCreateTimelong())) + (1000 * 60 * 60);
            } else {
                // 减去时区调整
                payEndTime = (ticketBean.getPayEndTime());
            }
        } else if (eTicket != null) {
            // 电子券过期时间为创建时间+24小时,减去时区调整
            payEndTime = ((eTicket.getCreateTimelong()) + 86400000);
        }

        final Date date = MTimeUtils.getLastDiffServerDate();
        final long dateInSecond = date.getTime();

        // 如果是倒计时
        if ((dateInSecond < payEndTime) && (countDownTimer == null)) {
            countDownTimer = new TimerCountDown(payEndTime - dateInSecond) {
                final long TICKET_COUNT_TIME = 1000 * 60 * 15;
                final long ETICKET_COUNT_TIME = 1000 * 60 * 30;

                @Override
                public void onTimeFinish() {
                    // 定时器到时的处理
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
                            payTextView.setText("尚未完成付款");
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
     * 显示中部信息（电影院、影片信息）
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


            // 添加小卖信息
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

            // 如果是购票
//            if ((fromActID != null) && fromActID.equals(OrderPayActivity.class.getName())) {
            movieId = ticketBean.getMovieId();
            //RecyclerViewStateUtils.setFooterViewState(OrderPaySuccessActivity.this, recyclerView, 0, LoadingFooter.State.Loading, null);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    requestRelatedGoodsById();
//                }
//            }, 1000);
            //(2021.10.21提示功能去掉)
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
//                am.set(AlarmManager.RTC_WAKEUP, (ticketBean.getShowtimeLong()) - twoHours, pi);// 提前两个小时通知用户
//
//                final Intent intent2 = new Intent(AlarmReceiver.REMINDER_MOVIE_COMMENT);
//                intent2.putExtra("MovieTitle", ticketBean.getMovieTitle());
//                intent2.putExtra("MovieID", ticketBean.getMovieId());
//                final PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
//                am.set(AlarmManager.RTC_WAKEUP,
//                        (ticketBean.getShowtimeLong()) + halfHour + (ticketBean.getMovieLength() * 1000 * 60),
//                        pIntent);// 电影结束半小时之后通知用户进行评论
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
            movieTitle = eTicket.getCommodityName() + " X " + eTicket.getQuantity() + " 张";
            strCinemaAddress = eTicket.getcAddress();

            final StringBuffer infoBuf = new StringBuffer();
            infoBuf.append("有效期:");
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
                exchangeTips.setText("（" + versionDesc + "）");
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
        // 客服时间
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
        // 请求电子券或电影票详情
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
     * 保存图片到相册
     */
    private void doSavePhoto() {
//        final ProgressDialog creatingPhotoDlg = StrToBytesUtils.createProgressDialog(this, "正在生成图片");
        final ProgressDialog savingPhotoDlg = Utils.createProgressDialog(this, "正在保存");
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
//                    // 下载图片
//                    MtimeUtils.savaBitmap(OrderPaySuccessActivity.this, bitmap, StrToBytesUtils.getMd5(mOrderId) + ".png");
//                } catch (final Exception e) {
//                    MToastUtils.showShortToast("保存图片失败,请稍后重试:" + e.getLocalizedMessage());
//                    return;
//                }

                MToastUtils.showShortToast("已成功下载至SD卡时光网文件夹中");
                /** 扫描图片,非常重要，会将图片加入media database，如果不加入，getImages()将获取不到此图 */
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
                MToastUtils.showShortToast("保存图片失败,请稍后重试:" + e.getLocalizedMessage());
            }
        };
        savingPhotoDlg.show();


        // Ticket/GetTicketOrETicketImage.api?orderId={0}
        Map<String, String> param = new HashMap<>(1);
        param.put("orderId", mOrderId);
        HttpUtil.download(ConstantUrl.GET_TICKET_OR_ETICKET_IMAGE, param, savePath, saveImgCallback);
    }

    /**
     * 保存图片到相册-偷偷的保存-适用于非会员购票
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
                new NotificationUtils(OrderPaySuccessActivity.this).sendNotification("时光网提醒", "您购买的电影票兑换码已保存至手机相册，请注意查看", pendingIntent);


                if (canShowDlg) {
                    // 只有在当前activity存在的情况下才弹出，否则会异常
                    MToastUtils.showShortToast("您购买的电影票兑换码已保存至手机相册，请注意查看");
                }

                /** 扫描图片,非常重要，会将图片加入media database，如果不加入，getImages()将获取不到此图 */
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
        // 跳转到地图
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

    //弹注册新手礼包图片,如果打开新手礼包，则关闭后请求红包弹窗，否则直接打开红包弹窗
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
     * 引导用户去应用市场评价弹框
     */
    private void showLeadToCommentDialog() {
        String version_code = App.getInstance().getPrefsManager().getString("VERSION_CODE");
        String version = MtimeUtils.getVersion(this);
        if (!TextUtils.equals(version, version_code)) {
            //引导用户去应用商店评价
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
                    dlg.setText("确定取消订单？");
                } else {
                    showHotPhoneDialog();
                }
                break;
            case R.id.btn_order:
                if ((ticketBean != null) && ticketBean.isReSelectSeat()) {
                    // 跳转到重新选座页面
                    Intent intent = new Intent();
                    intent.putExtra(App.getInstance().PAY_ETICKET, false); // 不是购买电子券
                    // 以下数据为所有页面都必须传过来的数据
                    intent.putExtra(App.getInstance().KEY_SEATING_DID, ticketBean.getShowtimeId());
                    intent.putExtra(App.getInstance().KEY_SEATING_SELECT_AGAIN, true);// 需要重新选座
                    intent.putExtra(App.getInstance().KEY_MOVIE_NAME, ticketBean.getMovieTitle());
                    intent.putExtra(App.getInstance().KEY_CINEMA_NAME, ticketBean.getCname());
                    intent.putExtra(App.getInstance().KEY_MOVIE_SHOW_DAY_LONG_TIME, ticketBean.getShowtimeLong());
                    intent.putExtra(App.getInstance().KEY_CINEMA_HALL, ticketBean.getHallName());
                    intent.putExtra(App.getInstance().KEY_MOVIE_VERSION_DESC, ticketBean.getVersionDesc());
                    intent.putExtra(App.getInstance().KEY_MOVIE_LANGUAGE, ticketBean.getLanguage());
                    intent.putExtra(App.getInstance().KEY_CINEMA_PHONE, ticketBean.getCtel());
                    intent.putExtra(App.getInstance().KEY_USER_BUY_TICKET_PHONE, ticketBean.getMobile());

                    // 以下数据为重新选座时必须传过来的数据（不支持更换场次）
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
                    // 保存图片到相册
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
     * 出发彩蛋弹框
     * 彩蛋弹框消失后还需要出发到应用市场评价逻辑，需要在彩蛋中发送一个消息出来，在该页面接收
     */
    private void showBonusSceneDialog() {
        BonusSceneExtKt.postPaySuccess();
    }

    /**
     * 获取购票订单实名预约信息
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
                // 启用实名预约系统
                if (CollectionUtils.isEmpty(result.getRealNameInfoList())) {
                    if (mRealNameLayout != null) {
                        mRealNameLayout.setVisibility(View.GONE);
                    }
                    // 弹窗
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
                    // 显示实名身份列表
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
//                    弹彩蛋
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
