package com.mtime.bussiness.ticket.movie.activity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.huawei.wallet.hmspass.service.WalletPassApiResponse;
import com.huawei.wallet.hmspass.service.WalletPassStaus;
import com.hw.passsdk.WalletPassApi;
import com.kotlin.android.app.router.ext.AppLinkExtKt;
import com.kotlin.android.app.router.path.RouterActivityPath;
import com.kotlin.android.film.JavaOpenSeatActivity;
import com.mtime.R;
import com.mtime.account.AccountManager;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.base.utils.DispatchAsync;
import com.mtime.base.utils.MTimeUtils;
import com.mtime.base.utils.MToastUtils;
import com.mtime.beans.ResultBean;
import com.mtime.beans.SuccessBean;
import com.mtime.bussiness.common.CommonWebActivity;
import com.mtime.bussiness.splash.LoadManager;
import com.mtime.bussiness.splash.bean.SplashStartLoad;
import com.mtime.bussiness.ticket.api.TicketApi;
import com.mtime.bussiness.ticket.cinema.activity.CinemaViewActivity;
import com.mtime.bussiness.ticket.movie.StrToBytesUtils;
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
import com.mtime.bussiness.ticket.movie.widget.MyListView;
import com.mtime.bussiness.ticket.movie.widget.TicketRealNameDialog;
import com.mtime.common.utils.TextUtil;
import com.mtime.common.utils.Utils;
import com.mtime.frame.App;
import com.mtime.frame.BaseActivity;
import com.mtime.network.ConstantUrl;
import com.mtime.network.RequestCallback;
import com.mtime.payment.dialog.OrderPayTicketOutingDialog;
import com.mtime.statistic.large.h5.StatisticH5;
import com.mtime.util.Base64ToBitmap;
import com.mtime.util.Base64Util;
import com.mtime.util.CustomAlertDlg;
import com.mtime.util.HttpUtil;
import com.mtime.util.JumpUtil;
import com.mtime.util.MallUrlHelper;
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


/**
 * 订单详情页
 */
@Route(path = RouterActivityPath.Main.PAGE_ORDER_DETAIL)
public class OrderDetailActivity extends BaseActivity implements OnClickListener {
    // 电子券模块的组件----------------------------------------------------
    private RequestCallback saveImgCallback = null;
    private ETicketDetailBean eTicket = null;
    private RequestCallback eTicketDetailCallback = null;
    private View exchangView = null;
    // 订单详情----------------------------------------------------------
    private boolean mIsETicket = false;
    private String mOrderId = null;
    private TextView money = null;
    private TextView savePhoto = null;
    private RequestCallback cancelCallback = null;
    private TimerCountDown countDownTimer = null;
    private TicketDetailBean ticketBean = null;
    private RequestCallback orderDetailCallback = null;
    private RequestCallback exchangeCodeCallback = null;

    private View orderTimeView = null;

    private boolean goAccountCenter = false;
    private boolean isnotVip = false;
    private String fromActID = "";
    private View orderPhoneView;
    private TextView orderPhoneText;
    private String notVipphoneNum = "";
    private String vipPhoneNum = "";
    private static final String KEY_MOBILE_LAST_TIME_INPUTED = "mobile_last_time_inputed";
    private ScrollView orderDetailMain;
    private String mtimeLoginUrl = null;
    private TitleOfNormalView navigationbar;

    // 商城sku部分
//    private LinearLayout layoutSku;
//    private View layoutOneSku;
//    private View layoutMoreSku;
//    private TextView skuNum;
    // 商城sku部分 单个sku布局
//    private TextView skuIconTxt;
//    private ImageView skuIcon;
//    private TextView skuName;
//    private TextView skuMtimePrice;
//    private TextView skuMarketPrice;
    // 商城sku部分 多个sku布局(最多显示2个)
//    private ImageView skuIcon1;
//    private TextView skuName1;
//    private TextView skuIconTxt1;
//    private TextView skuMtimePrice1;
//    private ImageView skuIcon2;
//    private TextView skuName2;
//    private TextView skuMtimePrice2;
//    private TextView skuIconTxt2;
//    private ImageView skuIcon3;
//    private TextView skuName3;
//    private TextView skuMtimePrice3;
//    private TextView skuIconTxt3;

    //影片详情
    private TextView mHotTelTv;
    //取票码展示
    private LinearLayout oldExchangeStyle = null;
    private LinearLayout mOnlyOneLayout;
    private TextView mTicketNameTv;
    private TextView mTicketCodeTv;
    private TextView mTelPhoneTv;

    private ImageView mQrCode;

    private TextView exchangeTips = null;
    private TextView exchangeBtn = null;
    private MyListView exchange_more = null;
    //付款状态展示
    private TextView cancelOrderBtn = null;
    private TextView btnOrder = null;
    private TextView titleInfo = null;                      // 在选座失败的时候，用来提示信息
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
    private TextView movieInfoSeat;
    //小卖
    private MyListView mSmallPayLv;
    private LinearLayout smallPayLin;

//    private String relatedUrl;
//    private String oneSkuUrl;                                                                      // 单个sku的详情url
//    private String moreSkuUrl1;                                                                    // 多个sku(只取前3个)中的第一个sku的详情url
//    private String moreSkuUrl2;                                                                    // 多个sku(只取前3个)中的第二个sku的详情url
//    private String moreSkuUrl3;                                                                    // 多个sku(只取前3个)中的第三个sku的详情url

    private View afterBuyTicketPromotion;
    private NetworkImageView niv_promotion;
    private OrderPayTicketOutingDialog ticketOutingDialog;
    private LinearLayout ll_order_detail;

    private View mSaveHuaWeiLine;
    private TextView mSaveToHuaWeiWallet;
    private WalletPassApi mWalletPassApi;
    private TicketApi mApi;
    private boolean isAdd;
    private static final int UPDATE_ADD_WALLET_TXT = 106;//更新添加华为钱包按钮文案
    private boolean mSupport = false;
    private boolean isFirst = true;
    private TextView mWorkTimeTv;

    // 实名身份
    private View mRealNameLayout;
    private RecyclerView mRealNameRv;
    private TicketRealNameAdapter mRealNameAdapter;
    // 实名身份: 获取订单座位数轮询次数
    private static final int POLLING_MAX_TIME = 20; // 最大轮询次数
    private static final int POLLING_SLEEP_TIME = 1000; // 每次轮询的睡眠时间（单位：毫秒）
    private int mPoolingCounter = 1; // 订单座位数轮询次数计数器

    @Override
    protected void onInitVariable() {
        final Intent intent = getIntent();
        mOrderId = intent.getStringExtra(App.getInstance().KEY_SEATING_ORDER_ID);
        goAccountCenter = intent.getBooleanExtra(App.getInstance().KEY_GO_ACCOUNT_CENTER, false);
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
                            .getString(OrderDetailActivity.KEY_MOBILE_LAST_TIME_INPUTED);
                }
            }
        }

        setPageLabel("orderDetail");
    }

    @Override
    protected void onInitView(final Bundle savedInstanceState) {
        this.setContentView(R.layout.act_order_detail);
        View navBar = findViewById(R.id.navigationbar);
        navigationbar = new TitleOfNormalView(this, navBar, StructType.TYPE_NORMAL_SHOW_BACK_TITLE, getResources()
                .getString(R.string.str_order_detail), new ITitleViewLActListener() {

            @Override
            public void onEvent(ActionType type, String content) {
                if (ActionType.TYPE_BACK == type) {
                    if ((fromActID != null) && fromActID.equals(OrderPayActivity.class.getName())) {
                        AppLinkExtKt.openHome();
                        finish();
                    }
                    if (goAccountCenter) {
                        AppLinkExtKt.openHome();
                        finish();
                    }
                }
            }
        });

        ll_order_detail = findViewById(R.id.ll_order_detail);
        ll_order_detail.setVisibility(View.VISIBLE);
        orderDetailMain = findViewById(R.id.order_detail_main);
        timeLayout = findViewById(R.id.timer_layout);
        exchange_more = findViewById(R.id.exchange_more);
        oldExchangeStyle = findViewById(R.id.old_exchange_style);
        exchangeTips = findViewById(R.id.exchange_tips);
        exchangeBtn = findViewById(R.id.sent_exchangeCode);
        // exchangeCodeView = findViewById(R.id.exchangeCodeView);
        exchangView = findViewById(R.id.exchange_view);
        orderPhoneView = findViewById(R.id.phone_view);
        orderPhoneText = findViewById(R.id.order_detail_phone);
        // 电影票---------------------------------------------------------
        timerView = findViewById(R.id.timer_view);
        btnOrder = findViewById(R.id.btn_order);
        titleInfo = findViewById(R.id.titleText);// 在选座失败的时候，用来提示信息
        minTextView = findViewById(R.id.textMin);
        secTextView = findViewById(R.id.textSecond);
        orderTextView = findViewById(R.id.textOrder);
        orderStatusTextView = findViewById(R.id.textOrderStatus);
        cinemaName = findViewById(R.id.info_title);
        movieName = findViewById(R.id.movieName);
        movieInfo = findViewById(R.id.movieInfo);
        cancelOrderBtn = findViewById(R.id.cancel_btn);
        money = findViewById(R.id.textMoney);
        savePhoto = findViewById(R.id.save);
        orderTimeView = findViewById(R.id.order_info);
        payTextView = findViewById(R.id.pay_not_complate);
        smallPayLin = findViewById(R.id.small_pay_info_lin);
        mSmallPayLv = findViewById(R.id.small_pay_lv);

        mOnlyOneLayout = findViewById(R.id.only_one_layout);
        mOnlyOneLayout.setVisibility(View.GONE);
        mTicketNameTv = findViewById(R.id.ticket_name_tv);
        mTicketCodeTv = findViewById(R.id.ticket_code_tv);
        mTelPhoneTv = findViewById(R.id.tel_phone_tv);
        mQrCode = findViewById(R.id.layout_ticket_direct_sale_order_top_state_success_qr_code_iv);
        mHotTelTv = findViewById(R.id.hot_tel_tv);
        mHotTelTv.setOnClickListener(this);


        //添加到华为钱包
        mWalletPassApi = new WalletPassApi(this);
        mSaveHuaWeiLine = findViewById(R.id.save_line_huawei_detail);
        mSaveToHuaWeiWallet = findViewById(R.id.add_huawei_detail);
        mSaveToHuaWeiWallet.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdd) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            jumpHuaWeiWalletMovieOrderDetail();
                        }
                    });
                } else {
                    downLoadHuaWeiZipData();
                }
            }
        });
        mSeatLineOneLayout = findViewById(R.id.seat_line_one_layout);
        mSeatLineTwoLayout = findViewById(R.id.seat_line_two_layout);
        mSeatOneTv = findViewById(R.id.seat_one_tv);
        mSeatTwoTv = findViewById(R.id.seat_two_tv);
        mSeatThreeTv = findViewById(R.id.seat_three_tv);
        mSeatFourTv = findViewById(R.id.seat_four_tv);
        mSeatFiveTv = findViewById(R.id.seat_five_tv);
        mSeatSixTv = findViewById(R.id.seat_six_tv);
        mHallOneTv = findViewById(R.id.hall_name_one);
        mHallTwoTv = findViewById(R.id.hall_name_two);
        cinema_address = findViewById(R.id.info_title_address);
        mWorkTimeTv = findViewById(R.id.order_detail_view_worktime_tv);
        TextView mapBtn = findViewById(R.id.btnMap);
        View layoutCinema = findViewById(R.id.layout_order_detail_cinema);
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

        //购票送自提商品活动
        afterBuyTicketPromotion = findViewById(R.id.after_buy_ticket_promotion);
        afterBuyTicketPromotion.setVisibility(View.GONE);
        niv_promotion = findViewById(R.id.niv_promotion);

        loadView();

        // sku
//        layoutSku = findViewById(R.id.movie_info_sku);
//        layoutOneSku = findViewById(R.id.mall_one_sku);
//        layoutMoreSku = findViewById(R.id.mall_more_sku);
//        skuNum = findViewById(R.id.sku_num);
        // sku 只有一条数据的布局
//        skuIconTxt = findViewById(R.id.sku_icon_txt);
//        skuIcon = findViewById(R.id.sku_icon);
//        skuName = findViewById(R.id.sku_name);
//        skuMtimePrice = findViewById(R.id.sku_mtime_price);
//        skuMarketPrice = findViewById(R.id.sku_market_price);
        // sku多条数据的布局
//        skuIcon1 = findViewById(R.id.sku_icon1);
//        skuName1 = findViewById(R.id.sku_name1);
//        skuIconTxt1 = findViewById(R.id.sku_icon_txt1);
//        skuMtimePrice1 = findViewById(R.id.sku_mtime_price1);
//        skuIcon2 = findViewById(R.id.sku_icon2);
//        skuName2 = findViewById(R.id.sku_name2);
//        skuMtimePrice2 = findViewById(R.id.sku_mtime_price2);
//        skuIconTxt2 = findViewById(R.id.sku_icon_txt2);
//        skuIcon3 = findViewById(R.id.sku_icon3);
//        skuName3 = findViewById(R.id.sku_name3);
//        skuMtimePrice3 = findViewById(R.id.sku_mtime_price3);
//        skuIconTxt3 = findViewById(R.id.sku_icon_txt3);
//        layoutSku.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openview(relatedUrl);
//            }
//        });
//        layoutOneSku.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openview(oneSkuUrl);
//            }
//        });
//        (findViewById(R.id.sku_more_layout3)).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openview(moreSkuUrl3);
//            }
//        });
//        (findViewById(R.id.sku_more_layout2)).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openview(moreSkuUrl2);
//            }
//        });
//        (findViewById(R.id.sku_more_layout1)).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openview(moreSkuUrl1);
//            }
//        });

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

        mApi = new TicketApi();
        //判断是否支持华为钱包
        // TODO: 2021/1/5 不要删掉，有可能会加回来 by vivian.wei
//        showAddWallet();
    }

    /**
     * 是否添加过
     */
    private void checkIsAdded(List<WalletPassStaus> walletPass) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                isAdd = isAdd(walletPass);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isAdd) {//已经添加过了
                            mSaveToHuaWeiWallet.setText(getResources().getString(R.string.str_go_huawei_check));
                        } else {
                            mSaveToHuaWeiWallet.setText(getResources().getString(R.string.str_save_to_huawei));
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 跳转到华为钱包电影订单详情页
     */
    private void jumpHuaWeiWalletMovieOrderDetail() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage("com.huawei.wallet");
        intent.putExtra("passId", mOrderId);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("wallet");
        builder.authority("com.huawei.wallet");
        builder.path("/openwallet");
        builder.appendQueryParameter("action", "com.huawei.wallet.movie.action.MOVIEDETAIL");
        builder.appendQueryParameter("channel", App.getInstance().getPackageName());
        builder.appendQueryParameter("type", "movie");
        Uri uri = builder.build();
        intent.setData(uri);
        startActivityForResult(intent, 0);
    }

    /**
     * 判断是否支持华为钱包
     */
    private void showAddWallet() {
        SplashStartLoad loadInfo = LoadManager.getLoadInfo();
        if (loadInfo == null) {
            return;
        }
        String hwAppId = loadInfo.getHwpassAppId();
        String passTypeId = loadInfo.getHwpassTypeIdentifier();
        if (!TextUtils.isEmpty(passTypeId) && !TextUtils.isEmpty(hwAppId)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    WalletPassApiResponse response = mWalletPassApi.canAddPass(hwAppId, passTypeId);
                    updateShowHuaWeiWalletTxt(response);
                }
            }).start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isFirst && mSupport) {
            // 华为钱包
//            SplashStartLoad loadInfo = LoadManager.getLoadInfo();
//            if (loadInfo != null) {
//                String hwAppId = loadInfo.getHwpassAppId();
//                String passTypeId = loadInfo.getHwpassTypeIdentifier();
//                if (!TextUtils.isEmpty(passTypeId) && !TextUtils.isEmpty(hwAppId)) {
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            WalletPassApiResponse response = mWalletPassApi.canAddPass(hwAppId, passTypeId);
//                            checkIsAdded(response.getPassIdList());
//                        }
//                    }).start();
//                }
//            }
        }

        isFirst = false;

        /**
         * 不放在订单获取成功后再获取实名接口的原因是：
         * 获取订单接口是放在oncreate里面调用的，而从h5返回需要重新获取实名信息，
         * 所以放在onResume()中，不能与获取订单接口串行调用
         */

        // 获取购票订单实名预约信息
        getRealNameReservationDetail();
    }

    /**
     * 设置华为钱包展示与否
     */
    private void updateShowHuaWeiWalletTxt(WalletPassApiResponse response) {
        String returnCode = response.getReturnCode();
        boolean support = returnCode.equals("0");
        mSupport = support;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (support) {
                    mSaveHuaWeiLine.setVisibility(View.VISIBLE);
                    mSaveToHuaWeiWallet.setVisibility(View.VISIBLE);
                    checkIsAdded(response.getPassIdList());
                } else {
                    mSaveHuaWeiLine.setVisibility(View.GONE);
                    mSaveToHuaWeiWallet.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 通过passId判断是否添加过华为钱包
     */
    private boolean isAdd(List<WalletPassStaus> walletPass) {
        if (null != walletPass && walletPass.size() > 0) {
            for (WalletPassStaus bean : walletPass) {
                if (null != mOrderId && mOrderId.equals(bean.getPassId())) {
                    return true;
                }
            }
        }
        return false;
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == UPDATE_ADD_WALLET_TXT) {
                if (null != mSaveToHuaWeiWallet) {
                    isAdd = true;
                    mSaveToHuaWeiWallet.setText(getResources().getString(R.string.str_go_huawei_check));
                }
            }
        }
    };

    /**
     * 下载华为ZIP数据
     */
    private void downLoadHuaWeiZipData() {
        String path = App.get().getFileStreamPath("download").getPath() + "/huawei.pass";
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        UIUtil.showLoadingDialog(OrderDetailActivity.this);
        if (!TextUtils.isEmpty(mOrderId)) {
            mApi.downloadFile("down_load_huawei", ConstantUrl.GET_HUA_WEI_WALLET_INFO + "?orderId=" + mOrderId, file.getPath(), new NetworkManager.NetworkProgressListener<String>() {
                @Override
                public void onProgress(float progress, long done, long total) {
                }

                @Override
                public void onSuccess(String result, String showMsg) {
                    byte[] bytes = StrToBytesUtils.getBytes(result);
                    UIUtil.dismissLoadingDialog();
                    if (null != bytes) {
                        String base64ZipData = Base64Util.encode(bytes);
                        WalletPassApiResponse response = mWalletPassApi.addPass(base64ZipData);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (null != response) {
                                    if (response.getReturnCode().equals("0")) {
                                        MToastUtils.showShortToast("已成功添加到华为钱包");
                                        Message msg = handler.obtainMessage();
                                        msg.what = UPDATE_ADD_WALLET_TXT;
                                        handler.sendMessage(msg);
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
        } else {
            //TODO  订单号不能为空
        }
    }

    private void loadView() {
        if ((fromActID != null) && fromActID.equals(OrderPayActivity.class.getName())) {
            if (mIsETicket) {
                navigationbar.setTitleText("购券成功");
            } else {
                navigationbar.setTitleText("购票成功");
                if (isnotVip) {
                    final CustomAlertDlg notvipDialog = new CustomAlertDlg(this, CustomAlertDlg.TYPE_OK_CANCEL);
                    notvipDialog.setBtnOKListener(new View.OnClickListener() {
                        public void onClick(final View v) {
                            notvipDialog.dismiss();
                            doSavePhotoBack();
                        }
                    });

                    notvipDialog.setBtnCancelListener(new View.OnClickListener() {
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
                //弹注册新手礼包图片
                showRegisterNewGiftDlg();
            }
        } else {
            navigationbar.setTitleText("订单详情");
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
                OrderDetailActivity.this.updateUI();
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
                    final CustomAlertDlg alertDlg = new CustomAlertDlg(OrderDetailActivity.this, CustomAlertDlg.TYPE_OK);
                    alertDlg.setBtnOKListener(new OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            alertDlg.dismiss();
                        }
                    });
                    alertDlg.show();
                    alertDlg.setText(getResources().getString(R.string.toast_order_send_only_once));
                } else {
                    final CustomAlertDlg alertDlg = new CustomAlertDlg(OrderDetailActivity.this,
                            CustomAlertDlg.TYPE_OK_CANCEL);

                    alertDlg.setBtnCancelListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            alertDlg.dismiss();
                        }
                    });

                    alertDlg.setBtnOKListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            UIUtil.showLoadingDialog(OrderDetailActivity.this);
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

        OnClickListener savePhotoClick = new OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (mOrderId != null) {
                    // 保存图片到相册
                    OrderDetailActivity.this.doSavePhoto();
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
                    OrderDetailActivity.this.finish();
                } else {
                    MToastUtils.showShortToast(bean.getMsg());
                }
            }

            @Override
            public void onFail(final Exception e) {
                UIUtil.dismissLoadingDialog();
                MToastUtils.showShortToast("取消订单出现错误:" + e.getLocalizedMessage());
                OrderDetailActivity.this.finish();
            }
        };


        orderDetailCallback = new RequestCallback() {
            @Override
            public void onSuccess(final Object o) {
                UIUtil.dismissLoadingDialog();
                ticketBean = (TicketDetailBean) o;
                OrderDetailActivity.this.updateUI();
                //请求加价购活动信息
//                requestPromotion();
                afterBuyTicketPromotion.setVisibility(View.GONE);
            }

            @Override
            public void onFail(final Exception e) {
                UIUtil.dismissLoadingDialog();
            }
        };

        btnOrder.setOnClickListener(this);
        cancelOrderBtn.setOnClickListener(this);
        exchangeBtn.setOnClickListener(exchangeClick);
        savePhoto.setOnClickListener(savePhotoClick);
        if (mIsETicket) {
            navigationbar.setShareVisibility(View.GONE);
        } else {
            navigationbar.setShareVisibility(View.GONE);
        }
    }

    /**
     * 请求加价购活动信息
     */
//    private void requestPromotion() {
//
//        RequestCallback promotionRequest = new RequestCallback() {
//            @Override
//            public void onSuccess(final Object o) {
//                PromotionBean promotionBean = (PromotionBean) o;
//                if (promotionBean != null && promotionBean.getImage() != null && promotionBean.getUrl() != null && !TextUtils.isEmpty(promotionBean.getImage()) && !TextUtils.isEmpty(promotionBean.getUrl())) {
//                    showPromotion(promotionBean);
//                } else {
//                    afterBuyTicketPromotion.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onFail(final Exception e) {
//                afterBuyTicketPromotion.setVisibility(View.GONE);
//            }
//        };
//
//        Map<String, String> param = new HashMap<>(1);
//        param.put("orderId", mOrderId);
//        HttpUtil.get(ConstantUrl.AFTER_BUY_TICKET_PROMOTION, param, PromotionBean.class, promotionRequest);
//    }

    /**
     * 购票送自提商品活动
     */
//    private void showPromotion(final PromotionBean promotionBean) {
//        afterBuyTicketPromotion.setVisibility(View.VISIBLE);
////        if (TextUtils.isEmpty(promotionBean.getUrl()) || promotionBean.getImage().length() < 5) {
////            niv_promotion.setImageResource(R.drawable.default_image_item_small);
////        } else {
//        int width = FrameConstant.SCREEN_WIDTH;
//        this.volleyImageLoader.displayNetworkImage(this.volleyImageLoader, promotionBean.getImage(), niv_promotion, R.drawable.default_image, R.drawable.default_image,
//                width, Utils.dip2px(this, 260), ImageURLManager.SCALE_TO_FIT, null);
//        niv_promotion.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //跳转衍生品自提订单页面
////                Intent intent = new Intent();
////                intent.putExtra(AdvRecommendActivity.KEY_ADVERT_ID, promotionBean.getUrl());
////                intent.putExtra(App.getInstance().KEY_SHOWTITLE, true);
////                intent.setClass(OrderDetailActivity.this, AdvRecommendActivity.class);
////                startActivity(intent);
//
////                JumpUtil.startAdvRecommendActivity(OrderDetailActivity.this, assemble().toString(),
////                        promotionBean.getUrl(), true, true, null, null, -1);
//                JumpUtil.startCommonWebActivity(OrderDetailActivity.this, promotionBean.getUrl(), StatisticH5.PN_H5, null,
//                        true, true, true, false, assemble().toString());
//            }
//        });
//        //}
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
                            goToPay();
                        }

                    } else {
                        MToastUtils.showShortToast("登录失败，请重新登录后重试！");
                    }
                } else {
                    MToastUtils.showShortToast("登录失败，请重新登录后重试！");
                }

            }

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
        OrderDetailActivity.this.startActivity(OrderPayActivity.class, intent);
        finish();
    }

    private void updateUI() {
        orderDetailMain.setVisibility(View.VISIBLE);
        payTextView.setText("");
        int orderStatus = 0;
        boolean isReSelectSeat = false;
        if (ticketBean != null) {
            mWorkTimeTv.setText(ticketBean.getOnlineTime());
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
         * 主订单状态：
         * 0  新建(此时订单对用户不可见)
         * 10 创建成功(调用FinishOrder时所有子订单创建成功，此后主订单再不允许添加子订单)
         * 20 创建失败(调用了FinishOrder时部分或全部子订单创建失败，此后主订单再不允许添加子订单)
         * 30 成功(已支付，且所有子订单都成功)
         * 40 失败(已支付，但部分或全部子订单失败)
         * 100已取消(用户在支付前主动取消)
         */
        if (orderStatus == 10) {
            if (ticketBean.getPayStatus() == 1) {
                // 已支付：显示出票中
                showTicketOutingDialog();
            } else {
                // 未支付
                long payEndTime = 0;
                if (ticketBean != null) {
                    // 减去时区调整
                    payEndTime = (ticketBean.getPayEndTime() ) ;
                } else if (eTicket != null) {
                    // 电子券过期时间为创建时间+24小时,减去时区调整
                    payEndTime = ((eTicket.getCreateTimelong()) + 86400000);
                }

                final Date date = MTimeUtils.getLastDiffServerDate();
                final long dateInSecond = date.getTime();
                // 倒计时
                if (dateInSecond < payEndTime) {
                    // 支付没有超时
                    titleInfo.setVisibility(View.GONE);
                    timerView.setVisibility(View.VISIBLE);
                    btnOrder.setText("立即付款");
                    exchangView.setVisibility(View.GONE); // 显示倒计时则不显示兑换码
                    cancelOrderBtn.setVisibility(View.VISIBLE);
                    cancelOrderBtn.setText(getResources().getString(R.string.str_cancel_order));
                    cancelOrderBtn.setTextColor(getResources().getColor(R.color.color_666666));
                    mCancel = true;
                    savePhoto.setVisibility(View.GONE);
                    payTextView.setVisibility(View.GONE);// 以前显示"尚未完成付款"并且VISIBLE
                    showTopInfo();
                    statusStr = getString(R.string.order_status_wait_pay);
                } else {
                    // 支付超时
                    timerView.setVisibility(View.VISIBLE);
                    timeLayout.setVisibility(View.GONE);
                    titleInfo.setVisibility(View.VISIBLE);
                    titleInfo.setText("因支付超时座位已被释放");
                    btnOrder.setVisibility(View.GONE);
                    exchangView.setVisibility(View.GONE);
                    cancelOrderBtn.setVisibility(View.VISIBLE);
                    initCancelBtn(true);
                    savePhoto.setVisibility(View.GONE);
                }
            }
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
                orderPhoneView.setVisibility(View.VISIBLE);
            }
        } else if (orderStatus == 0) {
            long payEndTime = 0;
            if (ticketBean != null) {
                // 减去时区调整
                payEndTime = (ticketBean.getPayEndTime());
            } else if (eTicket != null) {
                // 电子券过期时间为创建时间+24小时,减去时区调整
                payEndTime = ((eTicket.getCreateTimelong()) + 86400000) ;
            }

            final Date date = MTimeUtils.getLastDiffServerDate();
            final long dateInSecond = date.getTime();
            if (dateInSecond < payEndTime) // 倒计时
            {
                titleInfo.setVisibility(View.GONE);
                timerView.setVisibility(View.VISIBLE);
                btnOrder.setText("立即付款");
                exchangView.setVisibility(View.GONE); // 显示倒计时则不显示兑换码
                cancelOrderBtn.setVisibility(View.VISIBLE);
                cancelOrderBtn.setText(getResources().getString(R.string.str_cancel_order));
                cancelOrderBtn.setTextColor(getResources().getColor(R.color.color_666666));
                mCancel = true;
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
                titleInfo.setVisibility(View.GONE);
                timeLayout.setVisibility(View.VISIBLE);
                payTextView.setText("出票失败，请重新选座");
                exchangView.setVisibility(View.GONE);
                btnOrder.setVisibility(View.VISIBLE);
                btnOrder.setText("重新选座");
                cancelOrderBtn.setVisibility(View.GONE);
                savePhoto.setVisibility(View.GONE);
                showTopInfo();
            } else {
                // 重新选座
                titleInfo.setVisibility(View.GONE);
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
            titleInfo.setVisibility(View.GONE);
            timerView.setVisibility(View.VISIBLE);
            btnOrder.setVisibility(View.GONE);
            exchangView.setVisibility(View.GONE);
            savePhoto.setVisibility(View.GONE);
            // 显示顶部的订单状态信息（定时器、新建、交易成功、超时、已取消等）
        } else if (orderStatus != 30) {
            showTopInfo();
        }

        // 显示中部信息（兑换码、电影院、影片信息）
        showCentralInfo();
        // 显示底部信息（保存相册、取消订单）
        // showBottomInfo();
        if (null != ticketBean) {
            if (ticketBean.getRefundStatus() == 1) {
                statusStr = getString(R.string.order_status_refunding);
            } else if (ticketBean.getRefundStatus() == 2) {
                statusStr = getString(R.string.order_status_refunded);
            }
            if (!mIsETicket) {
                orderStatusTextView.setText(statusStr);
            }
        }

    }

    private void initCancelBtn(boolean isCancel) {
        if (isCancel) {
            cancelOrderBtn.setText(getResources().getString(R.string.str_cancel_order));
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
                payEndTime = (ticketBean.getPayEndTime()) ;
            }
        } else if (eTicket != null) {
            // 电子券过期时间为创建时间+24小时,减去时区调整
            payEndTime = ((eTicket.getCreateTimelong()) + 86400000) ;
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
                        OrderDetailActivity.this.handleTimeFinish();
                    }
                }

                @Override
                public void onTickCallBackTo(final String value, final String min, final String sec, final boolean flag) {
                    int color;
                    if (flag) {
                        color = Color.RED;
                    } else {
                        color = OrderDetailActivity.this.getResources().getColor(R.color.color_ff8600);
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
//                        if (CLOCK_PAY_END_TIME < TICKET_COUNT_TIME) {
//                            timeLayout.setVisibility(View.VISIBLE);
//                            minTextView.setText(min);
//                            secTextView.setText(sec);
//                        } else {
//                            timeLayout.setVisibility(View.GONE);
//                            payTextView.setVisibility(View.VISIBLE);
//                        }
                        timeLayout.setVisibility(View.VISIBLE);
                        minTextView.setText(min);
                        secTextView.setText(sec);
                    }
                }
            };
            countDownTimer.start();
            titleInfo.setVisibility(View.GONE);
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

            final StringBuffer infoBuf = new StringBuffer();
            infoBuf.append(ticketBean.getShowtime());
            strMovieInfo = infoBuf.toString();

            // 添加小卖信息
            if (ticketBean.getBuffetList() != null && ticketBean.getBuffetList().size() > 0) {
                double totalSmallPayMomnny = 0d;
                List<CommodityList> buffetList = ticketBean.getBuffetList();
                TicketDiscountAdapter discountAdapter = new TicketDiscountAdapter(OrderDetailActivity.this, buffetList);
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
                // 如果价格后面有.0或.00则返回整数，否则返回原来字符串,最多2位小数
                balanceStr = MtimeUtils.formatPrice(Double.parseDouble(ticketBean.getDoubleAmount()));
            }

            // orderTimeView.setVisibility(View.GONE);
            // 如果是购票(2021.10.21提示功能去掉)
//            if ((fromActID != null) && fromActID.equals(OrderPayActivity.class.getName())) {
////                requestRelatedGoodsById(ticketBean.getMovieId());
//                if (!ReminderMovieShow.getInstance().contains(ticketBean.getOrderId())) {
//                    final long twoHours = 1000 * 60 * 60 * 2;
//                    final long halfHour = 1000 * 60 * 30;
//                    ReminderMovieShow.getInstance().add(
//                            new ReminderMovieBean(ticketBean.getMovieId(), ticketBean.getOrderId(), ticketBean
//                                    .getSubOrderId(), ticketBean.getMovieTitle(), ticketBean.getShowtimeLong(),
//                                    ticketBean.getMovieLength()));
//
//                    final AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                    final Intent intent = new Intent(AlarmReceiver.REMINDER_MOVIE_SHOW);
//                    intent.putExtra("MovieTitle", ticketBean.getMovieTitle());
//                    intent.putExtra("MovieID", ticketBean.getMovieId());
//                    // intent.putExtra("MovieSubID",
//                    // ticketBean.getSubOrderId());
//                    final PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                    am.set(AlarmManager.RTC_WAKEUP, (ticketBean.getShowtimeLong()) - twoHours, pi);// 提前两个小时通知用户
//
//                    final Intent intent2 = new Intent(AlarmReceiver.REMINDER_MOVIE_COMMENT);
//                    intent2.putExtra("MovieTitle", ticketBean.getMovieTitle());
//                    intent2.putExtra("MovieID", ticketBean.getMovieId());
//                    final PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
//                    am.set(AlarmManager.RTC_WAKEUP,
//                            (ticketBean.getShowtimeLong()) + halfHour + (ticketBean.getMovieLength() * 1000 * 60),
//                            pIntent);// 电影结束半小时之后通知用户进行评论
//                }
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
        if (ticketBean != null)
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
                ShowExchangeCodeAdapter spAdapter = new ShowExchangeCodeAdapter(OrderDetailActivity.this, ticketBean.getNeoElectronicCode());
                exchange_more.setAdapter(spAdapter);
            }
            mTelPhoneTv.setText(getResources().getString(R.string.tel_phone_num) + ticketBean.getMobile());
            exchangeTips.setText(ticketBean.getPrompt());

        }

        if (ticketBean != null) {
            //二维码
            String qrCode = ticketBean.getQrcode();
            if (TextUtils.isEmpty(qrCode) || (ticketBean.getNeoElectronicCode() != null && ticketBean.getNeoElectronicCode().size() >= 2)) {
                mQrCode.setVisibility(View.GONE);
            } else {
                mQrCode.setVisibility(View.VISIBLE);
                Bitmap bitmap = Base64ToBitmap.getQrCodeByBase64String(qrCode);
                if (bitmap != null)
                    mQrCode.setImageBitmap(bitmap);
            }
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
        if (ticketBean != null) {
            StringBuffer movieBuf = new StringBuffer();
            movieBuf.append(movieTitle);
            // 版本
            if(!TextUtils.isEmpty(ticketBean.getVersionDesc())) {
                movieBuf.append(" ");
                movieBuf.append(ticketBean.getVersionDesc());
            }
            // 语言
            if(!TextUtils.isEmpty(ticketBean.getLanguage())) {
                movieBuf.append(" ");
                movieBuf.append(ticketBean.getLanguage());
            }
            movieName.setText(movieBuf.toString());
        }
        money.setText(balanceStr);
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
                mHallOneTv.setVisibility(View.VISIBLE);
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
                mHallOneTv.setVisibility(View.VISIBLE);
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
                mHallOneTv.setVisibility(View.VISIBLE);
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
                mHallTwoTv.setVisibility(View.VISIBLE);
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
                mHallTwoTv.setVisibility(View.VISIBLE);
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
                mHallTwoTv.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    /**
     * 显示底部信息（保存相册、取消订单）
     */
    /*
     * private void showBottomInfo() { }
     */

    /*
     * private void showTimerIfNeed(long payEndTime) { }
     */
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
            param.put("serialNo", "");
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
                if (null != savingPhotoDlg && savingPhotoDlg.isShowing()) {
                    savingPhotoDlg.dismiss();
                }
//                if (canShowDlg) {
//                    savingPhotoDlg.show();
//                }
//
//                try {
//                    final ByteArrayInputStream iStream = new ByteArrayInputStream((byte[]) o);
//                    final Bitmap bitmap = BitmapFactory.decodeStream(iStream);
//                    // 下载图片
//                    MtimeUtils.savaBitmap(OrderDetailActivity.this, bitmap, StrToBytesUtils.getMd5(mOrderId) + ".png");
////                    iStream.close();
//                } catch (final Exception e) {
//                    if (null != savingPhotoDlg && savingPhotoDlg.isShowing()) {
//                        savingPhotoDlg.dismiss();
//                    }
//                    MToastUtils.showShortToast("保存图片失败,请稍后重试:" + e.getLocalizedMessage());
//                    return;
//                }
//
//                if (null != savingPhotoDlg && savingPhotoDlg.isShowing()) {
//                    savingPhotoDlg.dismiss();
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
//                    MtimeUtils.savaBitmap(OrderDetailActivity.this, bitmap, StrToBytesUtils.getMd5(mOrderId) + ".png");
//                } catch (final Exception e) {
//                    return;
//                }

                PendingIntent pendingIntent = PendingIntent.getActivity(OrderDetailActivity.this, 0,
                        new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
                new NotificationUtils(OrderDetailActivity.this).sendNotification("时光网提醒", "您购买的电影票兑换码已保存至手机相册，请注意查看", pendingIntent);

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

//        OrderDetailActivity.this.startActivity(MapViewActivity.class, intent);
        JumpUtil.startMapViewActivity(OrderDetailActivity.this, longitude, latitude, cinemaId, cinemaName, cinemaAddress, "");
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
            if ((fromActID != null) && fromActID.equals(OrderPayActivity.class.getName())) {
                AppLinkExtKt.openHome();
                finish();
                return true;
            }
            if (goAccountCenter) {
                AppLinkExtKt.openHome();
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

//    private void requestRelatedGoodsById(String movieid) {
//        if (TextUtils.isEmpty(movieid)) {
//            return;
//        }
//        layoutSku.setVisibility(View.GONE);
//        // Search/RelatedGoodsById.api?relatedId={0}&relatedObjType={1}
//        Map<String, String> param = new HashMap<>(2);
//        param.put("relatedId", movieid);
//        param.put("relatedObjType", "1");
//        HttpUtil.get(ConstantUrl.GET_RELATEDGOODSBYID, param, RelatedGoods.class, new RequestCallback() {
//
//            @Override
//            public void onFail(Exception e) {
//            }
//
//            @Override
//            public void onSuccess(Object o) {
//                RelatedGoods relatedGoods = (RelatedGoods) o;
//                if (relatedGoods != null && relatedGoods.getGoodsList() != null
//                        && relatedGoods.getGoodsList().size() > 0 && relatedGoods.getGoodsCount() > 0) {
//                    layoutSku.setVisibility(View.VISIBLE);
//                    relatedUrl = relatedGoods.getRelatedUrl();
//                    skuNum.setText(String.format(getResources().getString(R.string.st_movie_info_sku), relatedGoods.getGoodsCount()));
//                    if (relatedGoods.getGoodsList().size() == 1) {
//                        layoutOneSku.setVisibility(View.VISIBLE);
//                        layoutMoreSku.setVisibility(View.GONE);
//                        showOneSku(relatedGoods.getGoodsList().get(0));
//                    } else {
//                        layoutOneSku.setVisibility(View.GONE);
//                        layoutMoreSku.setVisibility(View.VISIBLE);
//                        showMoreSku(relatedGoods.getGoodsList());
//                    }
//                }
//
//            }
//        }, 600000);
//    }

//    private void showOneSku(GoodsListBean sku) {
//        if (!TextUtils.isEmpty(sku.getIconText())) {
//            skuIconTxt.setText(sku.getIconText());
//            skuIconTxt.setVisibility(View.VISIBLE);
//            if (!TextUtils.isEmpty(sku.getBackground())) {
//                skuIconTxt.setBackgroundColor(android.graphics.Color.parseColor(sku.getBackground()));
//            }
//        }
//        oneSkuUrl = sku.getGoodsUrl();
//        skuName.setText(sku.getName());
//        skuMtimePrice.setText(String.format(getResources().getString(R.string.st_sku_write_order_sku_price), sku.getMinSalePrice() / 100));
//        skuMarketPrice.setText(String.format(getResources().getString(R.string.st_sku_write_order_sku_price), sku.getMarketPrice() / 100));
//        skuMarketPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
//        int iconSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getResources()
//                .getDimensionPixelSize(R.dimen.offset_pxtodx_203), getResources().getDisplayMetrics());
//        //
//        volleyImageLoader.displayImage(sku.getImage(), skuIcon, R.drawable.profile_default_head_h90, R.drawable.profile_default_head_h90, iconSize, iconSize, ImageURLManager.SCALE_TO_FIT, null);
//    }

//    private void showMoreSku(List<GoodsListBean> skus) {
//        if (skus.size() >= 2) {
//            int iconSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getResources()
//                    .getDimensionPixelSize(R.dimen.offset_pxtodx_180), getResources().getDisplayMetrics());
//            GoodsListBean sku1 = skus.get(0);
//            GoodsListBean sku2 = skus.get(1);
//            GoodsListBean sku3 = null;
//            moreSkuUrl3 = "";
//
//            if (skus.size() >= 3) {
//                findViewById(R.id.sku_more_layout3).setVisibility(View.VISIBLE);
//                sku3 = skus.get(2);
//                moreSkuUrl3 = sku3.getGoodsUrl();
//                skuName3.setText(sku3.getName());
//                if (!TextUtils.isEmpty(sku3.getIconText())) {
//                    skuIconTxt3.setText(sku3.getIconText());
//                    skuIconTxt3.setVisibility(View.VISIBLE);
//                    if (!TextUtils.isEmpty(sku3.getBackground())) {
//                        skuIconTxt3.setBackgroundColor(android.graphics.Color.parseColor(sku3.getBackground()));
//                    }
//                }
//                skuMtimePrice3.setText(String.format(getResources().getString(R.string.st_sku_write_order_sku_price), sku3.getMinSalePrice() / 100));
//                volleyImageLoader.displayImage(sku3.getImage(), skuIcon3, R.drawable.profile_default_head_h90, R.drawable.profile_default_head_h90, iconSize, iconSize, ImageURLManager.SCALE_TO_FIT, null);
//
//            } else {
//                findViewById(R.id.sku_more_layout3).setVisibility(View.INVISIBLE);
//            }
//            moreSkuUrl1 = sku1.getGoodsUrl();
//            moreSkuUrl2 = sku2.getGoodsUrl();
//
//
//            skuName1.setText(sku1.getName());
//            if (!TextUtils.isEmpty(sku1.getIconText())) {
//                skuIconTxt1.setText(sku1.getIconText());
//                skuIconTxt1.setVisibility(View.VISIBLE);
//                if (!TextUtils.isEmpty(sku1.getBackground())) {
//                    skuIconTxt1.setBackgroundColor(android.graphics.Color.parseColor(sku1.getBackground()));
//                }
//            }
//            if (!TextUtils.isEmpty(sku2.getIconText())) {
//                skuIconTxt2.setText(sku2.getIconText());
//                skuIconTxt2.setVisibility(View.VISIBLE);
//                if (!TextUtils.isEmpty(sku2.getBackground())) {
//                    skuIconTxt2.setBackgroundColor(android.graphics.Color.parseColor(sku2.getBackground()));
//                }
//            }
//
//            skuMtimePrice1.setText(String.format(getResources().getString(R.string.st_sku_write_order_sku_price), sku1.getMinSalePrice() / 100));
//
//            volleyImageLoader.displayImage(sku1.getImage(), skuIcon1, R.drawable.profile_default_head_h90, R.drawable.profile_default_head_h90, iconSize, iconSize, ImageURLManager.SCALE_TO_FIT, null);
//
//            skuName2.setText(sku2.getName());
//            skuMtimePrice2.setText(String.format(getResources().getString(R.string.st_sku_write_order_sku_price), sku2.getMinSalePrice() / 100));
//            volleyImageLoader.displayImage(sku2.getImage(), skuIcon2, R.drawable.profile_default_head_h90, R.drawable.profile_default_head_h90, iconSize, iconSize, ImageURLManager.SCALE_TO_FIT, null);
//        }
//
//    }

    private void openview(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        MallUrlHelper.MallUrlType type = MallUrlHelper.getUrlType(url);

        Class<?> activityId = getActivityId(type);

        if (activityId == CommonWebActivity.class) {
            JumpUtil.startCommonWebActivity(this, url, StatisticH5.PN_H5, null,
                    true, true, true, false, assemble().toString());
        } else {
            Intent intent = new Intent();
            intent.putExtra("SHOW_TITLE", true);
            intent.putExtra("LOAD_URL", url);
            intent.putExtra("DEFAULT_URL", url);
            this.startActivity(activityId, intent);
        }
    }

    private Class<?> getActivityId(MallUrlHelper.MallUrlType urlType) {
        Class<?> activityId = null;

        switch (urlType) {
            case PRODUCT_VIEW: {
//                activityId = ProductViewActivity.class;
                break;
            }
            case PRODUCTS_LIST:
            case PRODUCTS_LIST_SEARCH: {
//                activityId = ProductListActivity.class;
                break;
            }
            default: {
                activityId = CommonWebActivity.class;
                break;
            }
        }
        return activityId;
    }

    //弹注册新手礼包图片
    private void showRegisterNewGiftDlg() {
        if (App.getInstance().REGISTER_DLG_NEWGIFT_IMG != null && !TextUtils.isEmpty(App.getInstance().REGISTER_DLG_NEWGIFT_IMG)) {
            RegGiftDlg giftDlg = new RegGiftDlg(this, App.getInstance().REGISTER_DLG_NEWGIFT_IMG);
            giftDlg.show();

            App.getInstance().REGISTER_DLG_NEWGIFT_IMG = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hot_tel_tv:
                showHotPhoneDialog();
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
//                    OrderDetailActivity.this.startActivity(SeatSelectActivity.class, intent);

                    JavaOpenSeatActivity.INSTANCE.openSeatActivity(ticketBean.getShowtimeId(), ticketBean.getOrderId(), null, null, null);
                } else {
                    getPayList(mOrderId);
                }
                break;
            case R.id.cancel_btn:
                if (mCancel) {
                    final CustomAlertDlg dlg = new CustomAlertDlg(OrderDetailActivity.this, CustomAlertDlg.TYPE_OK_CANCEL);
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
                                UIUtil.showLoadingDialog(OrderDetailActivity.this);
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
        }
    }

    private void showHotPhoneDialog() {
        PhoneDialog dialog = new PhoneDialog(this);
        dialog.showCallPhoneDlg();
    }

    // 加载正在出票
    protected void showTicketOutingDialog() {
        if (ticketOutingDialog == null) {
            ticketOutingDialog = new OrderPayTicketOutingDialog(OrderDetailActivity.this);
        }
        if (!ticketOutingDialog.isShowing()) {
            ticketOutingDialog.show();
            ticketOutingDialog.setCancelable(true);
            ticketOutingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    if (OrderDetailActivity.this != null) {
                        ll_order_detail.setVisibility(View.INVISIBLE);
                        finish();
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mApi) {
            mApi.cancel();
        }
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
                if (result == null || !result.isNeed()) {
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
                    mPoolingCounter = 1;
                    getSeatCount();
                } else {
                    // 显示实名身份列表
                    if (mRealNameLayout != null) {
                        mRealNameLayout.setVisibility(View.VISIBLE);
                    }
                    if (mRealNameAdapter != null) {
                        mRealNameAdapter.setNewData(result.getRealNameInfoList());
                    }
                }
            }

            @Override
            public void onFailure(NetworkException<TicketRealNameReservationBean> exception, String showMsg) {
                UIUtil.dismissLoadingDialog();
                if (mRealNameLayout != null) {
                    mRealNameLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 获取订单座位数
     */
    private void getSeatCount() {
        if (mPoolingCounter > POLLING_MAX_TIME) {
            return;
        }

        DispatchAsync.dispatchAsyncDelayed(new Runnable() {
            @Override
            public void run() {
                mPoolingCounter++;
                if(ticketBean == null) {
                    getSeatCount();
                } else {
                    showRealNameDialog(ticketBean.getQuantity());
                }
            }
        }, POLLING_SLEEP_TIME);
    }

    /**
     * 显示实名预约弹窗
     */
    private void showRealNameDialog(int seatCount) {
        TicketRealNameDialog dlg = new TicketRealNameDialog();
        dlg.setData(mOrderId, seatCount);
        dlg.show(getSupportFragmentManager());
    }

}
