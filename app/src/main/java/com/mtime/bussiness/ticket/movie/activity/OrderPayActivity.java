package com.mtime.bussiness.ticket.movie.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.collection.ArrayMap;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alipay.sdk.app.PayTask;

import com.google.gson.Gson;

import com.kotlin.android.app.router.path.RouterActivityPath;
import com.kotlin.android.app.router.path.RouterProviderPath;
import com.kotlin.android.app.router.provider.mine.IMineProvider;
import com.kotlin.android.film.JavaOpenSeatActivity;
import com.kotlin.android.router.ext.ProviderExtKt;
import com.kotlin.android.user.UserManager;
import com.kotlin.android.app.data.entity.user.User;
import com.mtime.R;
import com.mtime.base.utils.DispatchAsync;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.base.utils.MTimeUtils;
import com.mtime.base.utils.MToastUtils;
import com.mtime.beans.SuccessBean;
import com.mtime.beans.WeixinPayBean;
import com.mtime.bussiness.main.maindialog.dialog.UpdateDlg;
import com.mtime.bussiness.mine.profile.activity.BindPhoneActivity;
import com.mtime.bussiness.ticket.movie.bean.ActivateVoucherCodeResult;
import com.mtime.bussiness.ticket.movie.bean.AddMtimeCardBean;
import com.mtime.bussiness.ticket.movie.bean.AnonymousPayBean;
import com.mtime.bussiness.ticket.movie.bean.BaseResultJsonBean;
import com.mtime.bussiness.ticket.movie.bean.BlendPayBean;
import com.mtime.bussiness.ticket.movie.bean.CardListBean;
import com.mtime.bussiness.ticket.movie.bean.CardLogicBean;
import com.mtime.bussiness.ticket.movie.bean.CouponActivityListItem;
import com.mtime.bussiness.ticket.movie.bean.ETicketDetailBean;
import com.mtime.bussiness.ticket.movie.bean.GiveupPayReasonBean;
import com.mtime.bussiness.ticket.movie.bean.OnlineOrderInfoJsonBean;
import com.mtime.bussiness.ticket.movie.bean.OrderStatusJsonBean;
import com.mtime.bussiness.ticket.movie.bean.PayItemBean;
import com.mtime.bussiness.ticket.movie.bean.ThirdPayListItem;
import com.mtime.bussiness.ticket.movie.bean.TicketDetailBean;
import com.mtime.bussiness.ticket.movie.bean.Voucher;
import com.mtime.bussiness.ticket.movie.bean.VoucherJsonBean;
import com.mtime.common.utils.PrefsManager;
import com.mtime.common.utils.TextUtil;
import com.mtime.common.utils.Utils;
import com.mtime.constant.Constants;
import com.mtime.frame.App;
import com.mtime.network.ConstantUrl;
import com.mtime.network.RequestCallback;
import com.mtime.payment.AbstractPayActivity;
import com.mtime.payment.WapPayActivity;
import com.mtime.payment.bean.AlipayPayResult;
import com.mtime.payment.dialog.OrderPayAgainDialog;
import com.mtime.payment.dialog.OrderPayRechargeWindowDialog;
import com.mtime.payment.dialog.OrderPayTicketOutingDialog;
import com.mtime.statistic.baidu.BaiduConstants;
import com.mtime.util.CapchaDlg;
import com.mtime.util.CustomAlertDlg;
import com.mtime.util.DesOld;
import com.mtime.util.HttpUtil;
import com.mtime.util.ImageLoader;
import com.mtime.util.InputDlg;
import com.mtime.util.InputTwoDlg;
import com.mtime.util.MtimeUtils;
import com.mtime.util.SecurityDlg;
import com.mtime.util.ToolsUtils;
import com.mtime.util.VolleyError;
import com.mtime.widgets.BaseTitleView.ActionType;
import com.mtime.widgets.BaseTitleView.ITitleViewLActListener;
import com.mtime.widgets.BaseTitleView.StructType;
import com.mtime.widgets.GiveupPayCollectionView;
import com.mtime.widgets.TimerCountDown;
import com.mtime.widgets.TitleOfNormalView;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.unionpay.UPQuerySEPayInfoCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

/**
 * ???????????????
 */
@Route(path = RouterActivityPath.Main.PAGER_ORDER_PAY_ACTIVITY)
public class OrderPayActivity extends AbstractPayActivity implements IWXAPIEventHandler {
    protected static final int POLLING_MAX_TIME = 180; // ??????????????????
    protected static final int POLLING_SLEEP_TIME = 1000; // ????????????????????????????????????????????????
    protected static final int NORMAL_ORDER_TYPE = 0; // ??????????????????????????????????????????type
    protected static final int AGAIN_ORDER_TYPE = 1; // ??????????????????????????????????????????type
    protected static final int OVER_ORDER_TYPE = 2; // ????????????????????????????????????type
    private static final int SDK_PAY_FLAG = 1;
    private final int VOUCHER_CARD = 0; // ?????????
    private final int SECOND_CARD = 1; // ??????
    private final int DROP_CARD = 2; // ??????

    // ?????? /order/PaymentItems.api ??????????????????thirdPayList???typeId??????8????????????
    private static final int PAY_ITEM_THIRD_TYPE_UPOMP = 8;
    private static final int PAY_ITEM_THIRD_TYPE_SAMSUNG = 2101;
    private static final int PAY_ITEM_THIRD_TYPE_HUAWEI = 2102;
    // ?????????????????????SEName???seType
    private static final String UPOMP_SE_NAME_SAMSUNG = "Samsung Pay";
    private static final String UPOMP_SE_NAME_HUAWEI = "Huawei Pay";
    private static final String UPOMP_SE_TYPE_SAMSUNG = "02";
    private static final String UPOMP_SE_TYPE_HUAWEI = "04";
    // ????????????_??????pay_??????icon_marginleft
    private static final int UPOMP_HUAWEI_UPOMPICON_MARGIN_LEFT = 28;
    private static final int UPOMP_SAMSUNG_UPOMPICON_MARGIN_LEFT = 40;
    // requestCode
    private static final int REQUEST_CODE_MY_WALLET = 10;

    protected ProgressDialog mDialog; // ???????????????, ????????????,?????????
    // 1.??????????????????, ???????????????????????????????????????????????????, 2. ???????????????, ????????????????????????????????????????????? 3. ?????????????????????
    protected CustomAlertDlg mCustomDlg;
    protected ProgressDialog mpayDialog;
    protected double mUserBalance = 0; // ??????????????? ???
    protected BigDecimal mNeedPayPrice; // ?????????????????????????????????
    protected double mPreferentialReducePrice = 0; // ??????????????????????????????, ??????????????????
    protected double mBalanceReducePrice = 0; // ?????????????????????????????????
    protected boolean isDoWithoutPayOrder = false; // ???????????????????????????????????????????????????????????????????????????????????????????????????,?????????????????????true,???????????????false
    protected int count;
    protected int mPoolingCounter = 1; // ?????????????????????
    protected Timer mPoolingMainOrderTimer; // ????????????????????????????????????
    protected OrderPayTicketOutingDialog ticketOutingDialog;
    protected boolean showTimer = true; // ????????????
    protected boolean isFirst = true; // ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    protected PrefsManager mPrefs;
    protected String payBindphone = "";
    protected boolean isRecharge; // ??????????????????true
    protected String mtimeCardUrl;
    protected OrderPayAgainDialog againCustomAlertDlg;
    protected LinearLayout preferential_view; // ??????????????? (-3.0)????????????,????????????????????????????????????
    protected View btn_activate; // ???????????????

    protected CheckBox cbox_balance; // ?????????????????????,checkbox??????????????????
    protected TextView tv_available_balance; // ?????????????????????,???????????????80
    protected View order_pay_balance_layout; // ???????????????????????????
    protected View order_pay_balance_layout_title; // ???????????????????????????
    protected TextView mBalanceReasonTipTv;
    protected View mBalanceReasonLl;

    //    protected TextView tv_reduce_price_balance; // ?????????????????? ??????:?????????????????? -30 (???????????????
//    private TextView rechargeBtn; // ?????????????????????,????????????

    protected TextView tv_need_pay_price; // ??????????????????36??????36
    protected SecurityDlg securitydlg; //????????????????????????????????? (????????????,????????????)
    private LayoutInflater mInflater;
    private boolean isAddVoucher = true; // ???????????????????????????????????????
    private boolean isUseBalance; // ??????????????????
    /**
     * ??????????????????????????????
     */
    private List<CardListBean> cardList;
    private List<Voucher> mVouchers;
    private List<CouponActivityListItem> couponActivityItemList; // CouponActivityItem.java??????????????????????????????
    private String mCurrentCheckedVoucherId = ""; // ??????????????????????????????id
    private final boolean hasUsedVoucherSuccessed = false; // ???????????????????????????
    private boolean hasCreateOrderFinished = false; // ???????????????????????????(????????????????????????????????????????????????????????????????????????)
    private int payType; // ?????????????????????:5, ???????????????:6 ????????????:9  ??????:14
    private String upompSeType; // ??????????????????_??????pay??????
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (SDK_PAY_FLAG == msg.what) {
                OrderPayActivity.this.completepay((String) msg.obj, payType);
            }
        }
    };
    private int subOrderStatus;
    private String mReselectSeatNewOrderId;
    private String mCurrentActivatedVoucherId; // ??????????????????????????????id
    private boolean isNeedValidate = false;
    private int countNum;
    private int cardType = -1; // ??????????????????
    private CapchaDlg capchaDlg;
    private String desPassWord;
    private InputTwoDlg addMovieDlg;
    private String unLocktoken; // ???????????????token;
    private ArrayList<CompoundButton> voucherViews; //???????????????????
    private OnClickListener checkClickListener;
    private IWXAPI api;
    private ArrayList<CompoundButton> allVoucherViews; //????????????????????????, ?????????????????????????????????,????????????????????????,?????????voucherViews????????????????????????,??????????????????
    private CompoundButton checkedCardCheckbox; //??????????????????????????????, ??????????????????????????????,????????????,?????????????????????,????????????????????????/???????????????
    /**
     * ******************************* UI?????? ****************************************
     */
    private TextView tv_total_price; // ??????:58
    private TextView tv_service_fee; // ????????????2???
    private View order_pay_coupon_activity_container_title; // ?????????????????? (-3.0)
    private LinearLayout coupon_activity_container; // ??????????????????-????????????
    private TextView order_pay_reduce_price_coupon_activity; // ??????????????????-20 ??? 20
    private TextView tv_reduce_price_preferential; // ??????????????????-?????????
    // ??????:???????????????(-3.0)??????-3.0
    private LinearLayout ll_checkbox_container; // ???????????????-????????????-?????????checkbox?????????
    private LinearLayout thirdPayLin; // ????????????????????????
    private LinearLayout orderTopPayLin; // ??????????????????,???????????????,??????????????????????????????,
    // ?????????????????????????????????, ??????????????????
    private InputDlg inputDlg; //?????????????????????????????????
    private CapchaDlg capchaCouponDlg; //???????????????????????????,??????????????????
    private OrderPayRechargeWindowDialog chargeDlg; // ???????????????????????????
    private LinearLayout paytype_layout;
    private LinearLayout order_pay_online_tip_layout;
    private View order_pay_bc_layout;
    private TextView order_pay_bc_des;
    private TextView order_pay_bc_btn;
    /**
     * ******************************* payitems???????????? ****************************************
     */
    // orderId?????????Id
    // topVoucherId????????????Id????????????????????????
    // activityIds???????????????Id????????????:activityId1,activityId2... ??????
    // voucherIdList????????????Id???????????????voucherId1,voucherId2... ??????
    // isBalance??????????????????????????????false
    // cardId????????????Id????????????
    // useNum?????????????????????????????????????????????
    // token:???????????????????????????,????????????????????????
    private String topVoucherId = "";
    private String activityIds = "";
    private String voucherIdList = "";
    private String cardId = "";
    private String useNum = "";
    private String token = "";
    private boolean notNeedRequestPayItem = false;
    /**
     * ????????????
     */
    private TimerCountDown timerCountDown;
    protected OrderPayAgainDialog againCBCustomAlertDlg;
    private boolean isCBGoto;//???????????? ????????????10????????????true

    private int checkedActivityId = 0;
    private boolean isFromSelectActivity;

    @Override
    protected void onInitVariable() {
        super.onInitVariable();

        voucherViews = new ArrayList<CompoundButton>();
        allVoucherViews = new ArrayList<CompoundButton>();
        mPrefs = App.getInstance().getPrefsManager();
        mDialog = Utils.createProgressDialog(this, this.getString(R.string.str_loading));

        final Intent intent = getIntent();
        mOrderId = intent.getStringExtra(App.getInstance().KEY_SEATING_ORDER_ID);
        isDoWithoutPayOrder = intent.getBooleanExtra(App.getInstance().KEY_IS_DO_WITH_OUT_PAY_ORDER, false);
        mIsEticket = intent.getBooleanExtra(App.getInstance().PAY_ETICKET, false); // ????????????????????????
        mPayEndTime = intent.getLongExtra(App.getInstance().KEY_SEATING_PAY_ENDTIME, 0);
        isnotVip = intent.getBooleanExtra(App.getInstance().KEY_TARGET_NOT_VIP, false);// ?????????vip??????
        notVipphoneNum = intent.getStringExtra(App.getInstance().KEY_TARGET_NOT_VIP_PHONE);
        isFromAccount = intent.getBooleanExtra(App.getInstance().KEY_ISFROM_ACCOUNT, false);
        isFromSelectActivity = intent.getBooleanExtra(App.getInstance().IS_FROM_SEATSELECT_ACTIVITY, false);
        if (!isDoWithoutPayOrder) {
            // ?????????????????????????????????????????????????????????????????????????????????, (????????????????????????)
            mTotalPrice = intent.getDoubleExtra(App.getInstance().KEY_SEATING_TOTAL_PRICE, 0);
            mSubOrderId = intent.getStringExtra(App.getInstance().KEY_SEATING_SUBORDER_ID);
            mCinemaName = intent.getStringExtra(App.getInstance().KEY_CINEMA_NAME);
            mCinemaPhone = intent.getStringExtra(App.getInstance().KEY_CINEMA_PHONE);
            mUserBuyTicketPhone = intent.getStringExtra(App.getInstance().KEY_USER_BUY_TICKET_PHONE);

            mMovieName = intent.getStringExtra(App.getInstance().KEY_MOVIE_NAME);
            mServiceFee = intent.getDoubleExtra(App.getInstance().KEY_SEATING_SERVICE_FEE, 0);
            mSeatId = intent.getStringExtra(App.getInstance().KEY_SEATING_SEAT_ID);
            mSelectedSeatCount = intent.getIntExtra(App.getInstance().KEY_SEATING_SELECTED_SEAT_COUNT, 0);
            mSeatSelectedInfo = intent.getStringExtra(App.getInstance().KEY_SEAT_SELECTED_INFO);
            mTicketDateInfo = intent.getStringExtra(App.getInstance().KEY_TICKET_DATE_INFO);
            // ??????4?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            mDId = intent.getStringExtra(App.getInstance().KEY_SEATING_DID);
            mMovieId = intent.getStringExtra(App.getInstance().KEY_MOVIE_ID);
            mCinemaId = intent.getStringExtra(App.getInstance().KEY_CINEMA_ID);
            mDate = intent.getStringExtra(App.getInstance().KEY_SHOWTIME_DATE);
        }

        setPageLabel("orderPay");
    }

    @Override
    protected void onInitView(final Bundle savedInstanceState) {
        this.setContentView(R.layout.act_order_pay);
        View navBar = this.findViewById(R.id.navigationbar);
        navigationbar = new TitleOfNormalView(this, navBar, StructType.TYPE_NORMAL_SHOW_BACK_TITLE_TIMER, getResources().getString(R.string.str_pay_money),
                new ITitleViewLActListener() {

                    @Override
                    public void onEvent(ActionType type, String content) {
                        switch (type) {
                            case TYPE_BACK:
                                if (null != giveupBean) {
                                    // ??????????????????????????????
                                    showCollectionView();
                                    return;
                                }

                                if (!isnotVip) {
                                    // ???????????????????????????
                                    showCancelOrderDialog();
                                } else {
                                    finish();
                                }
                                break;

                            default:
                                break;
                        }
                    }
                });
        navigationbar.setCloseParent(false);
        mInflater = getLayoutInflater();

        tv_total_price = findViewById(R.id.order_pay_tv_total_price);
        tv_service_fee = findViewById(R.id.order_pay_tv_service_fee);
        ll_checkbox_container = findViewById(R.id.order_pay_preferential_checkbox_container);
        coupon_activity_container = findViewById(R.id.order_pay_coupon_activity_container);
        order_pay_coupon_activity_container_title = findViewById(R.id.order_pay_coupon_activity_container_title);
        preferential_view = findViewById(R.id.order_preferential);
        tv_reduce_price_preferential = findViewById(R.id.order_pay_reduce_price_preferential);
        btn_activate = findViewById(R.id.movie_pay_ticket_btn_activate);
//        tv_reduce_price_balance = (TextView) findViewById(R.id.order_pay_reduce_price_balance);
        cbox_balance = findViewById(R.id.order_pay_cbox_balance);
        order_pay_balance_layout = findViewById(R.id.order_pay_balance_layout);
        order_pay_balance_layout_title = findViewById(R.id.order_pay_balance_layout_title);
        tv_available_balance = findViewById(R.id.order_pay_tv_available_balance);
        mBalanceReasonTipTv = findViewById(R.id.act_order_pay_balance_reason_tip_tv);
        mBalanceReasonLl = findViewById(R.id.act_order_pay_balance_reason_ll);
        tv_need_pay_price = findViewById(R.id.order_pay_tv_need_pay_price);
        thirdPayLin = findViewById(R.id.order_third_pay_lin);
        LinearLayout needpayLin = findViewById(R.id.order_need_pay_lin);
        orderTopPayLin = findViewById(R.id.order_toppay_lin);
//        rechargeBtn = (TextView) findViewById(R.id.btn_recharge);
        order_pay_reduce_price_coupon_activity = findViewById(R.id.order_pay_reduce_price_coupon_activity);
        paytype_layout = findViewById(R.id.paytype_layout);
        if (isnotVip) {
            btn_activate.setVisibility(View.GONE);
            preferential_view.setVisibility(View.GONE);
            order_pay_balance_layout.setVisibility(View.GONE);
            order_pay_balance_layout_title.setVisibility(View.GONE);
        } else {
            needpayLin.setVisibility(View.VISIBLE);
            orderTopPayLin.setVisibility(View.VISIBLE);
        }
        order_pay_online_tip_layout = findViewById(R.id.order_pay_online_tip_layout);
        order_pay_bc_layout = findViewById(R.id.order_pay_bc_layout);
        order_pay_bc_des = findViewById(R.id.order_pay_bc_des);
        order_pay_bc_btn = findViewById(R.id.order_pay_bc_btn);

        api = WXAPIFactory.createWXAPI(this, getResources().getString(R.string.key_wechat_app_id));
        api.registerApp(getResources().getString(R.string.key_wechat_app_id));

    }

    @Override
    protected void onInitEvent() {
        // ???????????????????????????
        mBalanceReasonTipTv.setOnClickListener(view -> {
            if (mBalanceReasonLl != null && mBalanceReasonLl.getVisibility() != View.VISIBLE) {
                mBalanceReasonLl.setVisibility(View.VISIBLE);
                mBalanceReasonTipTv.setVisibility(View.GONE);
            }
        });

        // ?????????????????????????????????
//        rechargeBtn.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                if (AccountManager.getAccountInfo() == null) {
//                    updateUserInfo();
//                    return;
//                }
//
//                if (AccountManager.getAccountInfo().getBindMobile() != null && !AccountManager.getAccountInfo().getBindMobile().equals("")) {
//
//                    if (String.valueOf(AccountManager.getAccountInfo().getRechargeMax()) != null) {
//                        chargeDlg = new OrderPayRechargeWindowDialog(OrderPayActivity.this, R.layout.act_order_pay_recharge_window);
//                        chargeDlg.show();
//                        chargeDlg.setTitle("??????");
//                        chargeDlg.setCanceledOnTouchOutside(false);
//                        chargeDlg.setBtnBackClickListener(new OnClickListener() {
//
//                            @Override
//                            public void onClick(View v) {
//                                if (chargeDlg != null) {
//                                    chargeDlg.dismiss();
//                                    chargeDlg = null;
//                                }
//                            }
//                        });
//                        chargeDlg.setBtnOKListener(new OnClickListener() {
//
//                            @Override
//                            public void onClick(View v) {
//
//                                final String chargeNum = chargeDlg.getText();
//                                if (TextUtil.stringIsNull(chargeNum)) {
//                                    chargeDlg.getEditView().setError("?????????????????????0");
//                                    return;
//                                }
//                                if (Integer.valueOf(chargeNum) <= 0) {
//                                    chargeDlg.getEditView().setError("?????????????????????0");
//                                    return;
//                                }
//                                final long money = Long.parseLong(chargeNum);
//                                if (null != AccountManager.getAccountInfo()) {
//                                    if (money > AccountManager.getAccountInfo().getRechargeMax()) {
//                                        chargeDlg.getEditView().setError("????????????????????????" + AccountManager.getAccountInfo().getRechargeMax());
//                                        return;
//                                    }
//                                }
//                                if (chargeDlg != null) {
//                                    chargeDlg.dismiss();
//                                    chargeDlg = null;
//                                }
//                                final Intent intent = new Intent();
//                                intent.putExtra("RechargeNum", money);
//                                intent.putExtra("isPay", true);
//                                OrderPayActivity.this.startActivityForResult(RechargeActivity.class, intent, 10);
//
//                            }
//                        });
//                        chargeDlg.getEditView().addTextChangedListener(new com.mtime.util.MaxLengthWatcher(6, chargeDlg.getEditView()));
//                        long rechareMax = null != AccountManager.getAccountInfo() ? AccountManager.getAccountInfo().getRechargeMax() : 0;
//                        chargeDlg.getEditView().setHint("??????????????????" + rechareMax + "?????????");
//                    }
//                } else {
//                    final CustomAlertDlg customAlertDlg = new CustomAlertDlg(OrderPayActivity.this, CustomAlertDlg.TYPE_OK_CANCEL);
//                    customAlertDlg.show();
//                    customAlertDlg.getBtnCancel().setText("??????");
//                    customAlertDlg.getBtnOK().setText("????????????");
//                    customAlertDlg.setText("????????????????????????\n???????????????????????????????????????");
//                    customAlertDlg.setBtnCancelListener(new OnClickListener() {
//
//                        @Override
//                        public void onClick(View v) {
//                            customAlertDlg.dismiss();
//                        }
//                    });
//                    customAlertDlg.setBtnOKListener(new OnClickListener() {
//
//                        @Override
//                        public void onClick(View v) {
//                            customAlertDlg.dismiss();
//                            startActivity(BindPhoneWithLoginActivity.class);
//                        }
//                    });
//                }
//
//            }
//        });

        // ???????????????/?????????

        // ???????????????/?????????

        // ?????? ???????????????/?????????
        btn_activate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                // ??????-??????????????????
//                Intent intent = new Intent();
//                startActivityForResult(OrderPayActivateActivty.class, intent, 10);

                // ???????????????
                ProviderExtKt.getProvider(IMineProvider.class)
                        .startMyWalletActivity(OrderPayActivity.this, REQUEST_CODE_MY_WALLET);
            }

        });

        // ???????????????
        checkClickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {

                unLocktoken = "";
                if (((CompoundButton) v).isChecked()) {

                    // ??????????????????
                    if (v.getTag(R.id.tag_first) instanceof Voucher) {
                        final Voucher mVoucher = (Voucher) v.getTag(R.id.tag_first);
                        // ??????????????????
                        if (!mVoucher.isUseMore()) {
                            if (voucherViews.size() > 1) {
                                MToastUtils.showShortToast("???????????????????????????????????????");
                                ((CompoundButton) v).setChecked(false);
                            } else if (voucherViews.size() == 1) {
                                if (voucherViews.get(0).getTag(R.id.tag_first) instanceof Voucher) {
                                    final Voucher vVoucher = (Voucher) voucherViews.get(0).getTag(R.id.tag_first);
                                    if (vVoucher.isUseMore()) {
                                        MToastUtils.showShortToast("???????????????????????????????????????");
                                        ((CompoundButton) v).setChecked(false);
                                    } else {
                                        // ?????????????????????????????????
                                        setVoucherViewCheck();
                                        voucherViews.add((CompoundButton) v);
                                        logicVoucher();
                                    }
                                } else {
                                    setVoucherViewCheck();
                                    voucherViews.add((CompoundButton) v);
                                    logicVoucher();
                                }

                            } else {
                                setVoucherViewCheck();
                                voucherViews.add((CompoundButton) v);
                                logicVoucher();
                            }
                        } else {// ??????????????????
                            // ??????????????????????????????????????????????????????
                            if (voucherViews.size() > 0) {
                                if (voucherViews.size() == 1) {
                                    if (voucherViews.get(0).getTag(R.id.tag_first) instanceof Voucher) {
                                        final Voucher dVoucher = (Voucher) voucherViews.get(0).getTag(R.id.tag_first);
                                        // ?????????????????????????????????????????????????????????
                                        if (!dVoucher.isUseMore()) {

                                            setVoucherViewCheck();
                                            voucherViews.add((CompoundButton) v);
                                            logicVoucher();
                                        } else {
                                            // ??????????????????????????????????????????????????????
                                            requestCheckVoucher(mCurrentCheckedVoucherId + "," + v.getId(), mOrderId, (CompoundButton) v);
                                        }
                                    } else {
                                        setVoucherViewCheck();
                                        voucherViews.add((CompoundButton) v);
                                        logicVoucher();
                                    }

                                } else {
                                    requestCheckVoucher(mCurrentCheckedVoucherId + "," + v.getId(), mOrderId, (CompoundButton) v);
                                }

                            } else {
                                voucherViews.add((CompoundButton) v);
                                logicVoucher();
                            }

                        }
                    }// ??????????????????
                    else if (v.getTag(R.id.tag_first) instanceof CardListBean) {
                        if (allVoucherViews != null) {
                            for (int i = 0; i < allVoucherViews.size(); i++) {
                                allVoucherViews.get(i).setEnabled(false);
                                allVoucherViews.get(i).setTextColor(getResources().getColor(R.color.color_bbbbbb));
                            }
                        }
                        setVoucherViewCheck();
                        if (checkedCardCheckbox != null) {
                            checkedCardCheckbox.setChecked(false);
                        }
                        checkedCardCheckbox = (CompoundButton) v;
                        voucherViews.add((CompoundButton) v);
                        mCurrentCheckedVoucherId = String.valueOf(v.getId());
//                        if (cbox_balance.isChecked()) {
//                            cbox_balance.setChecked(false);
//                        }

                        isNeedValidate = true;
                        CardListBean clBean = (CardListBean) v.getTag(R.id.tag_first);
                        if (clBean.getToken() != null && !clBean.getToken().equals("")) {
                            unLocktoken = clBean.getToken();
                        }
                        if (v.getTag(R.id.tag_second) instanceof View) {
                            View cbView = (View) v.getTag(R.id.tag_second);
                            // ??????????????????????????????
                            requestCardChangePrice(clBean, cbView, clBean.getcId(), 0, (CompoundButton) v);
                        }

                    }

                } else {
                    voucherIdList = "";
                    OrderPayActivity.this.cardId = "";
                    OrderPayActivity.this.useNum = "";
                    OrderPayActivity.this.token = "";
                    if (voucherViews.size() > 1) {
                        voucherViews.remove(v);
                        logicVoucher();
                    } else if (voucherViews.size() == 1) {
                        voucherViews.clear();
                        tv_reduce_price_preferential.setVisibility(View.GONE);
                        tv_reduce_price_preferential.setText("(-???0)");
                        mPreferentialReducePrice = 0;
                    }
                    if (v.getTag(R.id.tag_first) instanceof CardListBean) {
                        CardListBean clBean = (CardListBean) v.getTag(R.id.tag_first);
                        View cbView = (View) v.getTag(R.id.tag_second);
                        LinearLayout numLin = cbView.findViewById(R.id.num_lin);
                        if (clBean.getcType() == 0 || clBean.getcType() == 2) {
                            numLin.setVisibility(View.GONE);
                        }
                        requestPayItems();
                    }
                }
                refreshNeedPayPrice();
            }
        };

        // ????????????
//        cbox_balance.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                requestPayItems();
//            }
//        });
    }

    @Override
    protected void onLoadData() {
        super.onLoadData();

        if (App.getInstance().isFromWx) {
            App.getInstance().isFromWx = false;
            getOrderStatue(NORMAL_ORDER_TYPE);
        }
        String bindMobile = null;
        if (null != UserManager.Companion.getInstance().getUser()) {
            bindMobile = UserManager.Companion.getInstance().getBindMobile();
        }
        if (!TextUtils.isEmpty(bindMobile) && checkedActivityId != 0) {
            //??????????????????????????????????????????????????????
            activityIds = String.valueOf(checkedActivityId);
            requestPayItems();
            checkedActivityId = 0;
        }
        if (checkedActivityId != 0) {
            checkedActivityId = 0;
        }
    }

    @Override
    protected void onRequestData() {
        // ????????????????????????????????????????????????
        updateUserInfo();
        requestPayItems();

        this.giveupBean = null;
        if (ToolsUtils.getSpecialFilterGuider(this, "ticket_giveup_collection_show")) {
            HttpUtil.get(ConstantUrl.GET_GIVEUP_TICKET_PAY, null, GiveupPayReasonBean.class, new RequestCallback() {
                @Override
                public void onFail(Exception e) {

                }

                @Override
                public void onSuccess(Object o) {
                    if (null != o) {
                        giveupBean = (GiveupPayReasonBean) o;
                        if (null == giveupBean.getList() || giveupBean.getList().size() == 0 || TextUtils.isEmpty(giveupBean.getTitle())) {
                            giveupBean = null;
                        }
                    }
                }
            });
        }

    }

    private void requestPayItems() {
        // orderId?????????Id
        // topVoucherId????????????Id????????????????????????
        // activityIds???????????????Id????????????:activityId1,activityId2... ??????
        // voucherIdList????????????Id???????????????voucherId1,voucherId2... ??????
        // isBalance??????????????????????????????false
        // cardId????????????Id????????????
        // useNum?????????????????????????????????????????????
        // token:???????????????????????????,????????????????????????
        mDialog.show();

        // "Order/PaymentItems.api?orderId={0}&topVoucherId={1}&activityIds={2}&voucherIdList={3}&isBalance={4}&
        // // cardId={5}&useNum={6}&token={7}";
        Map<String, String> param = new HashMap<>(8);
        param.put("orderId", mOrderId);
        param.put("topVoucherId", topVoucherId);
        param.put("activityIds", activityIds);
        param.put("voucherIdList", voucherIdList);
        param.put("balance", String.valueOf(cbox_balance.isChecked()));
        param.put("cardId", cardId);
        param.put("useNum", useNum);
        param.put("token", token);
        HttpUtil.post(ConstantUrl.PAYMENT_ITEMS, param, PayItemBean.class, new RequestCallback() {

            @Override
            public void onSuccess(Object o) {
                if (null != mDialog && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                final PayItemBean payItemBean = (PayItemBean) o;
                if (payItemBean == null || payItemBean.getStatusCode() != 1) {
                    return;
                }

                mUserBalance = payItemBean.getBalance() / 100;
                mServiceFee = payItemBean.getServiceFee() / 100;
                mVouchers = payItemBean.getVoucherList();
                cardList = payItemBean.getCardList();
                couponActivityItemList = payItemBean.getCouponActivityList();

                mTotalPrice = Double.parseDouble(MtimeUtils.moneyF2Y(payItemBean.getTotalAmount()));
                mNeedPayPrice = new BigDecimal(Double.toString(payItemBean.getNeedPayAmount() / 100));
                isAddVoucher = payItemBean.getIsAddVoucher();
                // ?????????
                payLongTimeRemaining = payItemBean.getCountDown() * 1000;
                isUseBalance = payItemBean.getIsUseBalance();

                if (payItemBean.getCardNum() > 0) {
                    // ??????mtimeCardChangePrice.api?????????????????????
//                    tv_reduce_price_preferential.setVisibility(View.VISIBLE);
//                    tv_reduce_price_preferential.setText("(-" + payItemBean.getCardNum() + "???)");
                } else if (payItemBean.getCardAmount() > 0) {
                    tv_reduce_price_preferential.setVisibility(View.VISIBLE);
                    tv_reduce_price_preferential.setText(String.format("(-???%s)", MtimeUtils.formatPrice(payItemBean.getCardAmount() / 100.0)));
                } else if (payItemBean.getVoucherAmount() > 0) {
                    tv_reduce_price_preferential.setVisibility(View.VISIBLE);
                    tv_reduce_price_preferential.setText(String.format("(-???%s)", MtimeUtils.formatPrice(payItemBean.getVoucherAmount() / 100.0)));
                } else {
                    tv_reduce_price_preferential.setVisibility(View.GONE);
                    tv_reduce_price_preferential.setText("(-???0)");
                }
                if (payItemBean.getCouponActivityAmount() > 0) {
                    order_pay_reduce_price_coupon_activity.setVisibility(View.VISIBLE);
                    order_pay_reduce_price_coupon_activity.setText(String.format("(-???%s)", MtimeUtils.formatPrice(payItemBean.getCouponActivityAmount() / 100.0)));
                } else {
                    order_pay_reduce_price_coupon_activity.setVisibility(View.GONE);
                    order_pay_reduce_price_coupon_activity.setText("(-???0)");
                }
                List<ThirdPayListItem> thirdPayListBean = payItemBean.getThirdPayList();
                if (isnotVip && progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                // ?????????????????????
                if (thirdPayListBean != null) {
                    paytype_layout.removeAllViews();
                    ThirdPayListItem itemBean;
                    ThirdPayListItem huaweiItemBean = null;
                    ThirdPayListItem samsungItemBean = null;
                    // ??????????????????????????????
                    int upompPosition = -1;
                    for (int i = 0; i < thirdPayListBean.size(); i++) {
                        // ?????????????????????thirdPayListBean???Apple????????????????????????????????????????????????????????????
                        itemBean = thirdPayListBean.get(i);
                        // ?????????????????? 7????????????14???????????????9????????????8????????????
                        if (itemBean.getTypeId() == PAY_ITEM_THIRD_TYPE_UPOMP) {
                            // ???????????????
                            upompPosition = 0;
                        } else if (itemBean.getTypeId() == PAY_ITEM_THIRD_TYPE_SAMSUNG) {
                            // ???????????????
                            samsungItemBean = itemBean;
                        } else if (itemBean.getTypeId() == PAY_ITEM_THIRD_TYPE_HUAWEI) {
                            // ???????????????
                            huaweiItemBean = itemBean;
                        }
                    }
                    // ?????????????????????view: ???????????????/?????????????????????????????????????????????????????????????????????????????????
                    if (upompPosition >= 0) {
                        // ???????????????????????????????????????????????????/????????????
                        addUpompSeType(samsungItemBean, huaweiItemBean, thirdPayListBean);
                    } else {
                        // ??????????????????????????????
                        addUpompList(thirdPayListBean);
                    }
                }
                // ???????????????????????????????????????????????????
                mPreferentialReducePrice = payItemBean.getCardAmount() + payItemBean.getVoucherAmount() + payItemBean.getCouponActivityAmount();
//              TODO X ????????????, ????????? ???????????????, ??????????????????????????????????????????bug, ?????????????????????????????????????????????????????????
                if (payItemBean.getNeedPayAmount() == 0 && (payItemBean.getTotalAmount() == payItemBean.getVoucherAmount())) {
//                  //TODO X ????????????????????????, ??????????????????
//                  notNeedRequestPayItem = true;
//                  logicVoucher();

//                  if (isNeedValidate) { // ??????????????? ??????????????????
//                      requestSendCode(mCurrentCheckedVoucherId, "");
//                  } else { // ?????????????????? ???????????????--???????????????xx?????????????????????????????????????????????????????????
//                      showUseVoucherFullAmountPayDialog(mCurrentCheckedVoucherId, voucherViews, voucherPrice);
//                  }
                } else if (payItemBean.getNeedPayAmount() == 0 && payItemBean.getIsUseBalance()) { // 1.??????????????????,????????????,??????????????????
                    // ??????????????????????????????????????????
                    //??????????????????????????????needPayAmount???0, ????????????????????????????????????,???????????????????????????
                    double preferentialReducePriceYuan = Double.parseDouble(MtimeUtils.moneyF2Y(mPreferentialReducePrice));
                    mBalanceReducePrice = sub(mTotalPrice, preferentialReducePriceYuan).doubleValue();
                    if (mPreferentialReducePrice > 0) {
                        // ????????????????????????????????????????????????????????????????????????+?????????????????????
                        requestSendCode(mCurrentCheckedVoucherId, String.valueOf((int) (mBalanceReducePrice * 100)));
                    } else {
                        // ?????????????????????????????????
                        requestSendCode("", String.valueOf((int) (mBalanceReducePrice * 100)));
                    }
                } else if (payItemBean.getIsUseBalance()) {
                    mBalanceReducePrice = mUserBalance;
                }


                topVoucherId = "";
                List<String> subsidyMsgList = payItemBean.getSubsidyMsgList();
                if (subsidyMsgList != null && subsidyMsgList.size() > 0) {
                    order_pay_online_tip_layout.setVisibility(View.VISIBLE);
                    order_pay_online_tip_layout.removeAllViews();
                    for (int i = 0; i < subsidyMsgList.size(); i++) {
                        View v = View.inflate(OrderPayActivity.this, R.layout.subsidy_msg_listitem, null);
                        ((TextView) v.findViewById(R.id.order_pay_online_tip_tv)).setText(subsidyMsgList.get(i));
                        if (subsidyMsgList.size() > 1) {
                            v.findViewById(R.id.order_pay_online_tip_iv).setVisibility(View.VISIBLE);
                        } else {
                            v.findViewById(R.id.order_pay_online_tip_iv).setVisibility(View.GONE);
                        }

                        order_pay_online_tip_layout.addView(v);
                    }
                } else {
                    order_pay_online_tip_layout.setVisibility(View.GONE);
                }
                refreshUI();
                if (!isnotVip && !payItemBean.isDisplay()) {
                    order_pay_bc_layout.setVisibility(View.VISIBLE);
                    //?????????????????????
                    //TODO X1????????????????????????????????????????????????????????????????????????
                    preferential_view.setVisibility(View.GONE);
                    ll_checkbox_container.removeAllViews();
                    btn_activate.setVisibility(View.GONE);
                    order_pay_balance_layout_title.setVisibility(View.GONE);
                    order_pay_balance_layout.setVisibility(View.GONE);
                    thirdPayLin.setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(payItemBean.getActivityDescription())) {
                        order_pay_bc_des.setText(payItemBean.getActivityDescription());
                    }
                    order_pay_bc_btn.setText("????????? ???" + MtimeUtils.formatPrice(mNeedPayPrice.doubleValue()));

                    order_pay_bc_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!payItemBean.isGoto()) {
                                Intent intent = new Intent();
                                intent.putExtra(App.getInstance().CB_PAY_ITEM_BEAN, payItemBean);
                                intent.putExtra(App.getInstance().CB_PAY_ORDER_ID, mOrderId);
                                if (activityIds != null && !"".equals(activityIds.trim()) && !"-1".equals(activityIds.trim())) {
                                    intent.putExtra(App.getInstance().CB_PAY_ACTIVITYIDS, activityIds);
                                }
                                if (timerCountDown != null) {
                                    intent.putExtra(App.getInstance().PAY_LONG_TIME, timerCountDown.CLOCK_PAY_END_TIME);
                                }
                                intent.putExtra(App.getInstance().KEY_ISFROM_ACCOUNT, isFromAccount);
                                intent.putExtra(App.getInstance().KEY_SEATING_DID, mDId);
                                intent.putExtra(App.getInstance().IS_FROM_SEATSELECT_ACTIVITY, isFromSelectActivity);
                                intent.setClass(OrderPayActivity.this, OrderPayCheckBankCardActivity.class);
                                startActivity(intent);
                            } else {
                                BCpayTypePay(payItemBean.getPayType(), payItemBean.getPaymentNumber());
                            }
                        }
                    });

                } else {
                    order_pay_bc_layout.setVisibility(View.GONE);
                }
                if (!TextUtils.isEmpty(payItemBean.getAppVersionMsg())) {
                    //TODO X1 ??????????????? ?????????
                    //TODO X1 ?????????????????????????????????????????????
                    UpdateDlg.show(OrderPayActivity.this, UpdateDlg.TYPE_OK_CANCEL, payItemBean.getAppVersionMsg(), new UpdateDlg.SimpleListener() {
                        @Override
                        public void onCancelClick(View v) {
                            activityIds = "-1";
                            requestPayItems();
                        }
                    });

                } else if (!TextUtils.isEmpty(payItemBean.getActivityMsg())) {
                    if (payItemBean.isPromotionCount() || payItemBean.isUserLimitMAX()) {
                        MToastUtils.showShortToast(payItemBean.getActivityMsg());
                    } else {
                        final CustomAlertDlg msgDlg = new CustomAlertDlg(OrderPayActivity.this, CustomAlertDlg.TYPE_OK_CANCEL);
                        msgDlg.show();
                        msgDlg.setText(payItemBean.getActivityMsg());
                        msgDlg.setLabels("??????", "??????");
                        msgDlg.setBtnCancelListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                msgDlg.dismiss();
                                //????????????????????? ??????????????????????????????
                                activityIds = "-1";
                                requestPayItems();
                            }
                        });
                        msgDlg.setBtnOKListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                msgDlg.dismiss();
                            }
                        });
                    }
                }

            }

            @Override
            public void onFail(Exception e) {
                if (null != mDialog && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                if (canShowDlg) {
                    final AlertDialog alertDlg = Utils.createDlg(OrderPayActivity.this, getString(R.string.str_error) + ":" + e.getLocalizedMessage(),
                            e.getLocalizedMessage());
                    alertDlg.show();
                }
            }
        });
    }

    // ?????????????????????????????????_??????"?????????"??????
    private void BCpayTypePay(int typeId, String formXML) {
        if (endTime) {
            payOrderExpire(this);
            return;
        }
        if (mNeedPayPrice.doubleValue() <= 0) {
            return;
        }
        // todo weiwenli ?????????????????????????????????
        if (typeId == App.getInstance().PAYTYPE_YINLIAN) { // ??????????????????
            payType = typeId;
            upompSeType = "";
            isCBGoto = true;
            MtimeUtils.doPay(OrderPayActivity.this, formXML);
        } else if (typeId == App.getInstance().PAYTYPE_ALIPAY) {
            payType = typeId;
            OrderPayActivity.this.alipay(formXML);
        } else if (typeId == App.getInstance().PAYTYPE_ALIPAY_WAP) {
            payType = typeId;
            alipayWap(formXML);
        } else if (typeId == App.getInstance().PAYTYPE_WEIXIN) {
            payType = typeId;
            weixinPay(formXML);
        }
    }

    // ????????????????????????_??????/????????????
    private void addUpompSeType(final ThirdPayListItem samsungBean, final ThirdPayListItem huaweiBean, List<ThirdPayListItem> thirdPayListBean) {
        MtimeUtils.getSEPayinfo(OrderPayActivity.this, new UPQuerySEPayInfoCallback() {

            @Override
            public void onResult(String SEName, String seType, int cardNumbers, Bundle reserved) {
                boolean supportSamsung = SEName.equals(UPOMP_SE_NAME_SAMSUNG) && seType.equals(UPOMP_SE_TYPE_SAMSUNG);
                boolean supportHuawei = SEName.equals(UPOMP_SE_NAME_HUAWEI) && seType.equals(UPOMP_SE_TYPE_HUAWEI);
                if (supportSamsung || supportHuawei) {
                    // ???????????????????????????????????????/??????????????????
                    View paytypeView = View.inflate(OrderPayActivity.this, R.layout.layout_ticket_paytype_upomp_special, null);
                    int typeId = -1;
                    // ??????
                    ImageView iconIv = paytypeView.findViewById(R.id.paytype_image);
                    // ?????????
                    TextView nameTv = paytypeView.findViewById(R.id.paytype_text);
                    // ????????????
                    ImageView upompIv = paytypeView.findViewById(R.id.paytype_upomp_icon_iv);
                    LinearLayout.LayoutParams upompIvLp = (LinearLayout.LayoutParams) upompIv.getLayoutParams();
                    // ?????????
                    TextView recommendTv = paytypeView.findViewById(R.id.paytype_recommend);
                    String tag = "";
                    if (supportSamsung) { // ??????
                        iconIv.setImageResource(R.drawable.payment_samsung_icon);
                        nameTv.setText("");
                        nameTv.setVisibility(View.GONE);
                        upompIvLp.setMargins(MScreenUtils.dp2px(UPOMP_SAMSUNG_UPOMPICON_MARGIN_LEFT), 0, 0, 0);
                        tag = null == samsungBean ? "" : samsungBean.getTag();
                        typeId = PAY_ITEM_THIRD_TYPE_SAMSUNG;
                    } else if (supportHuawei) { // ??????
                        iconIv.setImageResource(R.drawable.payment_huawei_icon);
                        nameTv.setText(getResources().getString(R.string.upomp_pay_huawei));
                        nameTv.setVisibility(View.VISIBLE);
                        upompIvLp.setMargins(MScreenUtils.dp2px(UPOMP_HUAWEI_UPOMPICON_MARGIN_LEFT), 0, 0, 0);
                        tag = null == huaweiBean ? "" : huaweiBean.getTag();
                        typeId = PAY_ITEM_THIRD_TYPE_HUAWEI;
                    }
                    upompIv.setLayoutParams(upompIvLp);
                    // ???????????????????????????
                    recommendTv.setVisibility(TextUtils.isEmpty(tag) ? View.GONE : View.VISIBLE);
                    recommendTv.setText(tag.trim());

                    paytype_layout.addView(paytypeView, 0);
                    // ????????????
                    payTypeItemClick(typeId, paytypeView);
                }

                // ??????????????????????????????
                addUpompList(thirdPayListBean);
            }

            @Override
            public void onError(String SEName, String seType, String errorCode, String errorDesc) {
                // TODO: 2018/5/11 weiwenli  ?????????????????????
//                MToastUtils.showLongToast("errorCode: " + errorCode + ",   errorDesc: " + errorDesc);

                // ??????????????????????????????
                addUpompList(thirdPayListBean);
            }

        });
    }

    // ????????????????????????????????????????????????????????????????????????
    private void addUpompList(List<ThirdPayListItem> thirdPayListBean) {
        ThirdPayListItem itemBean;
        TextView nameTv;
        TextView recommendTv;
        for (int i = 0; i < thirdPayListBean.size(); i++) {
            itemBean = thirdPayListBean.get(i);
            // ?????????????????? 7 ????????????14???????????????9????????????8????????????
            if (itemBean.getTypeId() != PAY_ITEM_THIRD_TYPE_SAMSUNG && itemBean.getTypeId() != PAY_ITEM_THIRD_TYPE_HUAWEI) {
                View paytypeView = View.inflate(OrderPayActivity.this, R.layout.layout_ticket_paytype, null);
                // ?????????
                nameTv = paytypeView.findViewById(R.id.paytype_text);
                nameTv.setText(itemBean.getName());
                // ?????????
                recommendTv = paytypeView.findViewById(R.id.paytype_recommend);
                recommendTv.setVisibility(TextUtils.isEmpty(itemBean.getTag()) ? View.GONE : View.VISIBLE);
                recommendTv.setText(itemBean.getTag().trim());

                paytype_layout.addView(paytypeView);
                // ???????????????
                payTypeItemClick(itemBean.getTypeId(), paytypeView);
            }
        }
    }

    // ???????????????????????????
    private void payTypeItemClick(int typeId, View paytypeView) {
        if (endTime) {
            payOrderExpire(this);
            return;
        }
        if (mNeedPayPrice.doubleValue() <= 0) {
            return;
        }
        if (typeId == 7) {  // ?????????
            ((ImageView) paytypeView.findViewById(R.id.paytype_image)).setImageResource(R.drawable.icon_pay_alipay);
            paytypeView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    // ???????????????
                    payType = App.getInstance().PAYTYPE_ALIPAY;
                    logicThirdPay(0, "?????????");
                }
            });
        } else if (typeId == PAY_ITEM_THIRD_TYPE_UPOMP) { // ??????????????????
            ((ImageView) paytypeView.findViewById(R.id.paytype_image)).setImageResource(R.drawable.icon_pay_upomp);
            paytypeView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    // ????????????
                    payType = App.getInstance().PAYTYPE_YINLIAN;
                    upompSeType = "";
                    logicThirdPay(0, "??????????????????");
                }
            });
        } else if (typeId == 9) { // ??????????????????
            ((ImageView) paytypeView.findViewById(R.id.paytype_image)).setImageResource(R.drawable.icon_pay_bank);
            paytypeView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    // ?????????
                    payType = App.getInstance().PAYTYPE_ALIPAY_WAP;
                    startActivityForResult(BankCardListActivity.class, 0);
                }
            });
        } else if (typeId == 6) { // ?????????
            ((ImageView) paytypeView.findViewById(R.id.paytype_image)).setImageResource(R.drawable.icon_pay_moviecard);
            paytypeView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    // ????????????????????????
                    showAddMovieCard(false);
                }
            });
        } else if (typeId == 14) { // ??????
            ((ImageView) paytypeView.findViewById(R.id.paytype_image)).setImageResource(R.drawable.icon_pay_wechat);
            paytypeView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    // ????????????
                    if (api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT) {
                        payType = App.getInstance().PAYTYPE_WEIXIN;
                        logicThirdPay(0, "????????????");
                    } else {
                        MToastUtils.showShortToast(R.string.str_weixin_no_support);
                    }
                }
            });
        } else if (typeId == PAY_ITEM_THIRD_TYPE_SAMSUNG || typeId == PAY_ITEM_THIRD_TYPE_HUAWEI) { // ????????????_??????pay???????????????/?????????
            paytypeView.setOnClickListener((View view) -> {
                // ????????????
                payType = App.getInstance().PAYTYPE_YINLIAN;
                // ??????pay???????????????
                upompSeType = typeId == PAY_ITEM_THIRD_TYPE_SAMSUNG ? UPOMP_SE_TYPE_SAMSUNG : UPOMP_SE_TYPE_HUAWEI;
                logicThirdPay(0, "??????????????????");
            });
        }
    }

    private void refreshUI() {
        childRefreshViewValues();
        thirdPayLin.setVisibility(View.VISIBLE);
        if (isnotVip) {
            btn_activate.setVisibility(View.GONE);
            preferential_view.setVisibility(View.GONE);
            order_pay_balance_layout_title.setVisibility(View.GONE);
            order_pay_balance_layout.setVisibility(View.GONE);
        } else {
            if (isAddVoucher) {
                btn_activate.setVisibility(View.VISIBLE);
                preferential_view.setVisibility(View.VISIBLE);
            } else {
                btn_activate.setVisibility(View.GONE);
                preferential_view.setVisibility(View.GONE);
            }
            order_pay_balance_layout_title.setVisibility(View.VISIBLE);
            order_pay_balance_layout.setVisibility(View.VISIBLE);
        }
//        if (isUseBalance) {
//            cbox_balance.setChecked(true);
//        } else {
//            cbox_balance.setChecked(false);
//        }
        if (mNeedPayPrice.doubleValue() < 0) {
            // ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            mNeedPayPrice = BigDecimal.valueOf(0.0);
        }
        tv_need_pay_price.setText("???" + MtimeUtils.formatPrice(mNeedPayPrice.doubleValue()));
    }

    // ??????????????????
    private void updateUserInfo() {

        HttpUtil.get(ConstantUrl.ACCOUNT_DETAIL, User.class, new RequestCallback() {
            @Override
            public void onSuccess(final Object o) {
                UserManager.Companion.getInstance().update((User) o);
            }

            @Override
            public void onFail(final Exception e) {
            }
        });
    }

    /**
     * ****************************************** ???????????????????????? begin ********************************************************
     */
    //???????????????
    private void showAddMovieCard(boolean isShowCheck) {
        addMovieDlg = new InputTwoDlg(this);
        addMovieDlg.show();
        if (isShowCheck) {
            addMovieDlg.getLayoutCheckbox().setVisibility(View.VISIBLE);
            addMovieDlg.setFillLayoutVisible(View.GONE);
        } else {
            addMovieDlg.getLayoutCheckbox().setVisibility(View.GONE);
            addMovieDlg.setFillLayoutVisible(View.VISIBLE);
        }
        addMovieDlg.setBtnCancelListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addMovieDlg.dismiss();
            }
        });
        addMovieDlg.setBtnOKListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (addMovieDlg.getNumEdit().getText().toString().equals("")) {
                    MToastUtils.showShortToast("???????????????");
                    return;
                }
                if (addMovieDlg.getPwdEdit().getText().toString().equals("")) {
                    MToastUtils.showShortToast("???????????????");
                    return;
                }
                // ?????????????????????
                if (isnotVip) {
                    getMtimeCard(addMovieDlg.getNumEdit().getText().toString(), addMovieDlg.getPwdEdit().getText().toString(), "", "", mOrderId, false);
                } else {
                    getMtimeCard(addMovieDlg.getNumEdit().getText().toString(), addMovieDlg.getPwdEdit().getText().toString(), "", "", mOrderId, addMovieDlg
                            .getCheckBox().isChecked());
                }

            }
        });
    }

    // ?????????????????????
    private void getMtimeCard(final String cardNum, final String password, final String vcode, final String vcodeId, final String orderId, final boolean isBind) {
        try {
            desPassWord = DesOld.encode(password);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        showProgressDialog(this, "????????????...");
        RequestCallback getMtimeCardCallback = new RequestCallback() {
            @Override
            public void onSuccess(Object o) {
                if (null != progressDialog && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                AddMtimeCardBean adCardBean = (AddMtimeCardBean) o;
                if (adCardBean.isSuccess()) {
                    if (capchaDlg != null && capchaDlg.isShowing()) {
                        capchaDlg.dismiss();
                    }
                    if (addMovieDlg != null && addMovieDlg.isShowing()) {
                        addMovieDlg.dismiss();
                    }
                    mCurrentActivatedVoucherId = adCardBean.getCardInfo().getcId();
                    if (!isnotVip) {
                        if (activityIds != null && !"".equals(activityIds.trim()) && !"-1".equals(activityIds.trim())) {
                            ll_checkbox_container.setVisibility(View.GONE);
                            return;
                        }
                        if (isBind) {
                            requestVoucherListData();
                        } else {
                            requestUnBindList(adCardBean);
                        }
                    } else {
                        unLocktoken = adCardBean.getToken();
                        requestCardChangePrice(adCardBean.getCardInfo(), null, adCardBean.getCardInfo().getcId(), 0, null);
                    }
                } else {
                    if (adCardBean.getStatus() == -1) {
                        if (adCardBean.getCodeId() != null && adCardBean.getCodeUrl() != null) {
                            if (capchaDlg != null && capchaDlg.isShowing()) {
                                MToastUtils.showShortToast("???????????????");
                            }
                            requestCapcha(cardNum, desPassWord, adCardBean.getCodeUrl(), adCardBean.getCodeId(), orderId, isBind);
                        }
                    } else {
                        if (capchaDlg != null && capchaDlg.isShowing()) {
                            capchaDlg.dismiss();
                        }
                        showPayErrorDlg(adCardBean.getMsg());
                    }
                }
            }

            @Override
            public void onFail(Exception e) {
                if (null != progressDialog && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        };

        // ???????????????
        Map<String, String> parameterList = new ArrayMap<String, String>(6);
        parameterList.put("cardNum", cardNum);
        parameterList.put("password", desPassWord);
        parameterList.put("vcode", vcode);
        parameterList.put("vcodeId", vcodeId);
        parameterList.put("orderId", orderId);
        parameterList.put("isBind", String.valueOf(isBind));
        HttpUtil.post(ConstantUrl.GET_MTIME_CARD, parameterList, AddMtimeCardBean.class, getMtimeCardCallback);
    }

    /**
     * ****************************************** ???????????????????????? end ********************************************************
     */

    // ???????????????????????????????????????????????????
    private void requestUnBindList(AddMtimeCardBean adCardBean) {
        CardListBean unBindcardListBean = adCardBean.getCardInfo();
        unBindcardListBean.setToken(adCardBean.getToken());
        if (cardList != null) {
            cardList.add(unBindcardListBean);
        } else {
            cardList = new ArrayList<CardListBean>();
            cardList.add(unBindcardListBean);
        }
        OrderPayActivity.this.showVoucherView();
    }

    // ????????? ?????????????????????
    private void requestCapcha(final String cardNum, final String password, final String imgUrl, final String vcodeId, final String orderId,
                               final boolean isBind) {

        if (capchaDlg != null && capchaDlg.isShowing()) {
            capchaDlg.dismiss();
        }

        capchaDlg = null;
        capchaDlg = new CapchaDlg(OrderPayActivity.this, CapchaDlg.TYPE_OK_CANCEL);

        capchaDlg.setBtnOKListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                getMtimeCard(cardNum, addMovieDlg.getPwdEdit().getText().toString(), capchaDlg.getEditView().getText().toString(), vcodeId, orderId, isBind);

            }
        });
        capchaDlg.setBtnCancelListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                capchaDlg.dismiss();
            }
        });
        capchaDlg.setCapchaTextListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                getMtimeCard(cardNum, addMovieDlg.getPwdEdit().getText().toString(), capchaDlg.getEditView().getText().toString(), vcodeId, orderId, isBind);
            }
        });
        capchaDlg.show();

        volleyImageLoader.displayVeryImg(imgUrl, null, new ImageLoader.ImageListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {
                    capchaDlg.getImageView().setImageBitmap(response.getBitmap());
                }
            }
        });

    }

    // ????????? ???????????????/?????????
    private void requestCouponCapcha(final String cardNum, final String imgUrl, final String vcodeId) {

        if (capchaCouponDlg != null && capchaCouponDlg.isShowing()) {
            capchaCouponDlg.dismiss();
        }

        capchaCouponDlg = null;
        capchaCouponDlg = new CapchaDlg(OrderPayActivity.this, CapchaDlg.TYPE_OK_CANCEL);
        capchaCouponDlg.setBtnOKListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {

                OrderPayActivity.this.doActivateVoucherCode(String.valueOf(OrderPayActivity.this.mOrderId), cardNum, capchaCouponDlg.getEditView().getText()
                        .toString(), vcodeId);

            }
        });
        capchaCouponDlg.setBtnCancelListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                capchaCouponDlg.dismiss();
            }
        });
        capchaCouponDlg.setCapchaTextListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                OrderPayActivity.this.doActivateVoucherCode(String.valueOf(OrderPayActivity.this.mOrderId), cardNum, capchaCouponDlg.getEditView().getText()
                        .toString(), vcodeId);
            }
        });
        capchaCouponDlg.show();

        volleyImageLoader.displayVeryImg(imgUrl, null, new ImageLoader.ImageListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {
                    capchaCouponDlg.getImageView().setImageBitmap(response.getBitmap());
                }
            }
        });

    }

    /**
     * ???????????????????????????????????????????????????
     */
    private void requestVoucherListData() {
        showProgressDialog(this, "???????????????????????????...");

        final RequestCallback getAvaliableCallback = new RequestCallback() {

            @Override
            public void onSuccess(final Object o) {
                if (null != progressDialog && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                final VoucherJsonBean voucherBean = (VoucherJsonBean) o;
                mVouchers = voucherBean.getVoucherList();
                cardList = voucherBean.getCardList();
                final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT);
                lp.setMargins(Utils.px2dip(OrderPayActivity.this, 8), 0, 0, 0);
                // MallOrderPayActivity.this.showVoucherView();
                if (cardList != null && cardList.size() > 0) {
                    for (int i = 0; i < cardList.size(); i++) {
                        if ((mCurrentActivatedVoucherId != null)
                                && mCurrentActivatedVoucherId.equals(cardList.get(i).getcId())) {
                            final View cbView = mInflater.inflate(R.layout.order_pay_voucher_item, null);
                            CheckBox cb = cbView.findViewById(R.id.cbox_id);
                            cbView.setLayoutParams(lp);
                            CardListBean cBean = cardList.get(i);
                            String cardNum = cBean.getcNum().substring(cBean.getcNum().length() - 4
                            );
                            cb.setText(cardList.get(i).getcName() + "****" + cardNum);
                            // cb.setOnCheckedChangeListener(MallOrderPayActivity.this);
                            cb.setOnClickListener(checkClickListener);
                            cb.setTag(R.id.tag_first, cBean);
                            cb.setTag(R.id.tag_second, cbView);
                            cb.setId(Integer.valueOf(cBean.getcId()));
                            ll_checkbox_container.addView(cbView);
                        }
                    }

                }
                for (int j = 0; j < mVouchers.size(); j++) {
                    final Voucher voucher = mVouchers.get(j);
                    if ((mCurrentActivatedVoucherId != null)
                            && mCurrentActivatedVoucherId.equals(voucher.getVoucherID())) {

                        final View cbView = mInflater.inflate(R.layout.order_pay_voucher_item, null);
                        CheckBox cb = cbView.findViewById(R.id.cbox_id);
                        cbView.setLayoutParams(lp);
                        cb.setTag(R.id.tag_first, voucher);
                        cb.setId(Integer.valueOf(voucher.getVoucherID()));
                        cb.setText(voucher.getVoucherName());
                        cb.setOnClickListener(checkClickListener);
                        // ?????????????????????????????????
                        ll_checkbox_container.addView(cbView);
                        cb.setChecked(true);

                        // ??????????????????
                        if (cb.getTag(R.id.tag_first) instanceof Voucher) {
                            final Voucher mVoucher = (Voucher) cb.getTag(R.id.tag_first);
                            if (!mVoucher.isUseMore()) {

                                if (voucherViews.size() > 1) {
                                    MToastUtils.showShortToast("???????????????????????????????????????");
                                    cb.setChecked(false);
                                } else if (voucherViews.size() == 1) {
                                    if (voucherViews.get(0).getTag(R.id.tag_first) instanceof Voucher) {
                                        final Voucher vVoucher = (Voucher) voucherViews.get(0).getTag(R.id.tag_first);
                                        if (vVoucher.isUseMore()) {
                                            MToastUtils.showShortToast("???????????????????????????????????????");
                                            cb.setChecked(false);
                                        } else {
                                            setVoucherViewCheck();
                                            voucherViews.add(cb);
                                            logicVoucher();
                                        }
                                    } else {
                                        setVoucherViewCheck();
                                        voucherViews.add(cb);
                                        logicVoucher();
                                    }

                                } else {
                                    setVoucherViewCheck();
                                    voucherViews.add(cb);
                                    logicVoucher();
                                }

                            } else {// ??????????????????
                                // ??????????????????????????????????????????????????????
                                if (voucherViews.size() > 0) {
                                    if (voucherViews.size() == 1) {
                                        if (voucherViews.get(0).getTag(R.id.tag_first) instanceof Voucher) {
                                            final Voucher dVoucher = (Voucher) voucherViews.get(0).getTag(
                                                    R.id.tag_first);
                                            // ?????????????????????????????????????????????????????????
                                            if (!dVoucher.isUseMore()) {

                                                setVoucherViewCheck();
                                                voucherViews.add(cb);
                                                logicVoucher();
                                            } else {
                                                // ??????????????????????????????????????????????????????
                                                requestCheckVoucher(mCurrentCheckedVoucherId + "," + cb.getId(),
                                                        mOrderId, cb);
                                            }
                                        } else {
                                            setVoucherViewCheck();
                                            voucherViews.add(cb);
                                            logicVoucher();
                                        }

                                    } else {
                                        requestCheckVoucher(mCurrentCheckedVoucherId + "," + cb.getId(), mOrderId, cb);
                                    }

                                } else {
                                    voucherViews.add(cb);
                                    logicVoucher();
                                }

                            }
                        }
                        refreshNeedPayPrice();

                    }
                }

            }

            @Override
            public void onFail(final Exception e) {
                if (null != progressDialog && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

            }
        };
        // Ticket/AvaliableVoucherListByUserID.api?orderId={0}&topVoucherId={1}
        Map<String, String> param = new HashMap<>(2);
        param.put("orderId", String.valueOf(mOrderId));
        param.put("topVoucherId", String.valueOf(mCurrentActivatedVoucherId));
        HttpUtil.get(ConstantUrl.GET_AGAIN_USE_VOUCHER, param, VoucherJsonBean.class, getAvaliableCallback);
    }

    /**
     * ?????????????????????
     */
    protected void showVoucherView() {
        allVoucherViews.clear();
        // ??????????????????????????????????????????
        ll_checkbox_container.removeAllViews();
        voucherIdList = "";
        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(Utils.px2dip(this, 8), 0, 0, 0);
        // ???
        if (cardList != null && cardList.size() > 0) {
            ll_checkbox_container.setVisibility(View.VISIBLE);
            for (int i = 0; i < cardList.size(); i++) {
                final View cbView = mInflater.inflate(R.layout.order_pay_voucher_item, null);
                CheckBox cb = cbView.findViewById(R.id.cbox_id);
                cbView.setLayoutParams(lp);
                CardListBean cBean = cardList.get(i);
                String cardNum = cBean.getcNum().substring(cBean.getcNum().length() - 4);
                cb.setText(cardList.get(i).getcName() + "****" + cardNum);
                if (cBean.isSelected()) {
                    cb.setTextColor(getResources().getColor(R.color.color_333333));
                    cb.setEnabled(true);
                    cb.setOnClickListener(checkClickListener);
                } else {
                    cb.setTextColor(getResources().getColor(R.color.color_bbbbbb));
                    cb.setEnabled(false);
                }
                cb.setChecked(cBean.isUsed());
                if (cBean.isUsed()) {
                    cardId = cBean.getcId();
                }
//                if (cBean.getcType() == 0 || cBean.getcType() == 2) {
//                    tv_reduce_price_preferential.setVisibility(View.VISIBLE);
//                    tv_reduce_price_preferential.setText("(-" + countNum + "???)");
//                }
                cb.setTag(R.id.tag_first, cBean);
                cb.setTag(R.id.tag_second, cbView);
                cb.setId(Integer.valueOf(cBean.getcId()));
                ll_checkbox_container.addView(cbView);
            }
        }
        // ?????????
        if ((mVouchers != null) && (mVouchers.size() > 0)) {
            ll_checkbox_container.setVisibility(View.VISIBLE);
            for (int i = 0; i < mVouchers.size(); i++) {
                final View cbView = mInflater.inflate(R.layout.order_pay_voucher_item, null);
                final CheckBox cb = cbView.findViewById(R.id.cbox_id);
                cbView.setLayoutParams(lp);

                final Voucher voucher = mVouchers.get(i);
                cb.setTag(R.id.tag_first, voucher);
                cb.setId(Integer.valueOf(voucher.getVoucherID()));
                cb.setText(voucher.getVoucherName());
                if (voucher.isSelected()) {
                    cb.setTextColor(getResources().getColor(R.color.color_333333));
                    cb.setEnabled(true);
                    cb.setOnClickListener(checkClickListener);
                    allVoucherViews.add(cb);
                } else {
                    cb.setTextColor(getResources().getColor(R.color.color_bbbbbb));
                    cb.setEnabled(false);
                }
                cb.setChecked(voucher.isUsed());
                if (voucher.isUsed()) {
                    //TODO X
                    if ("".equals(voucherIdList.trim())) {
                        voucherIdList += voucher.getVoucherID();
                    } else {
                        voucherIdList = voucherIdList + "," + voucher.getVoucherID();
                    }
                }
                //TODO X remove?
                // ?????????????????????????????????
//                if ((mCurrentActivatedVoucherId != null) && mCurrentActivatedVoucherId.equals(voucher.getVoucherID())) {
//                    cb.setChecked(true);
//                }
                ll_checkbox_container.addView(cbView);
            }

        }
        if (null != mVouchers && null != cardList && mVouchers.size() == 0 && cardList.size() == 0) {
            tv_reduce_price_preferential.setVisibility(View.GONE);
        }
        if (null == mVouchers && null == cardList) {
            tv_reduce_price_preferential.setVisibility(View.GONE);
        }
        coupon_activity_container.removeAllViews();
        if (couponActivityItemList != null && couponActivityItemList.size() > 0) {
            order_pay_coupon_activity_container_title.setVisibility(View.VISIBLE);
            for (int i = 0; i < couponActivityItemList.size(); i++) {
                final CouponActivityListItem cBean = couponActivityItemList.get(i);
                View cbView = null;
                if (!cBean.getIsSelected()) {
                    // TODO  ?????????????????????????????????????????????????????????
                    cbView = mInflater.inflate(R.layout.order_pay_coupon_activity_item, null);
                    TextView activity_tag = cbView.findViewById(R.id.activity_tag);
                    TextView activity_desc = cbView.findViewById(R.id.activity_desc);
                    activity_tag.setText(cBean.getTag());
                    activity_desc.setText(cBean.getDesc());
                } else {
                    cbView = mInflater.inflate(R.layout.order_pay_voucher_item, null);
                    final CheckBox cb = cbView.findViewById(R.id.cbox_id);
                    cbView.setLayoutParams(lp);
                    cb.setChecked(cBean.getUsed());
                    if (cBean.getUsed()) {
                        activityIds = String.valueOf(cBean.getId());
                    }
                    cb.setEnabled(cBean.getEnable());
                    cb.setText(cBean.getTag());
                    if (cBean.getIsSelected() && cBean.getEnable()) {
                        cb.setTextColor(getResources().getColor(R.color.color_333333));
                        cb.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                boolean checkMobileBind = cBean.isCheckMobileBind();
                                //TODO X ??????????????????????????????true
//                                checkMobileBind = true;
                                String bindMobile = UserManager.Companion.getInstance().getBindMobile();
                                if (((CompoundButton) v).isChecked() && checkMobileBind && TextUtils.isEmpty(bindMobile)) {
                                    goBindPhoneFromActivity(cb, cBean.getId());
                                } else {
                                    if (((CompoundButton) v).isChecked()) {
                                        activityIds = String.valueOf(cBean.getId());
                                    } else {
                                        activityIds = "-1";
                                    }
                                    requestPayItems();
                                }
                            }
                        });
                    } else {
                        cb.setTextColor(getResources().getColor(R.color.color_bbbbbb));
                    }
                    cb.setTag(R.id.tag_first, cBean);
                    cb.setId(Integer.valueOf(cBean.getId()));
                }

                coupon_activity_container.addView(cbView);
            }
        } else {
            activityIds = "-1";
            order_pay_coupon_activity_container_title.setVisibility(View.GONE);
        }
        orderTopPayLin.setVisibility(View.VISIBLE);

    }

    //??????????????????????????????
    private void goBindPhoneFromActivity(final CheckBox cb, final int checkedId) {
        final CustomAlertDlg customAlertDlg = new CustomAlertDlg(OrderPayActivity.this, CustomAlertDlg.TYPE_OK_CANCEL);
        customAlertDlg.show();
        customAlertDlg.setCancelable(false);
        customAlertDlg.setCanceledOnTouchOutside(false);
        customAlertDlg.getBtnCancel().setText("??????");
        customAlertDlg.getBtnOK().setText("?????????");
        customAlertDlg.setText("????????????????????????????????????");
        customAlertDlg.setBtnCancelListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                customAlertDlg.dismiss();
                cb.setChecked(false);
            }
        });
        customAlertDlg.setBtnOKListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                customAlertDlg.dismiss();
                cb.setChecked(false);
                checkedActivityId = checkedId;
                Intent intent = new Intent();
                intent.putExtra(Constants.BIND_HAD_PASSWORD, UserManager.Companion.getInstance().getHasPassword());
                startActivity(BindPhoneActivity.class, intent);

            }
        });
    }

    @Override
    protected void onUnloadData() {

    }

    private void logicVoucher() {
        Double voucherPrice = 0d;
        Double deductVoucherPrice = 0d;
        mPreferentialReducePrice = 0;
        mCurrentCheckedVoucherId = "";
        isNeedValidate = false;
        for (int i = 0; i < voucherViews.size(); i++) {
            final Voucher cVoucher = (Voucher) voucherViews.get(i).getTag(R.id.tag_first);
            voucherPrice += cVoucher.getAmount() / 100;
            deductVoucherPrice = cVoucher.getDeductAmount() / 100;
            if (i == voucherViews.size() - 1) {
                mCurrentCheckedVoucherId += cVoucher.getVoucherID();
            } else {
                mCurrentCheckedVoucherId += cVoucher.getVoucherID() + ",";
            }

            mPreferentialReducePrice += deductVoucherPrice;
            if (cVoucher.isNeedValidate()) {
                isNeedValidate = true;
            }

        }

        cardType = VOUCHER_CARD;
        tv_reduce_price_preferential.setVisibility(View.VISIBLE);
        tv_reduce_price_preferential.setText(String.format("(-???%s)", MtimeUtils.formatPrice(mPreferentialReducePrice)));

        voucherIdList = mCurrentCheckedVoucherId;
        // ???????????????????????????????????????Id??????
        if (!TextUtils.isEmpty(voucherIdList)) {
            cardId = "";
        }
        if (!notNeedRequestPayItem) {
            requestPayItems();
        }
        notNeedRequestPayItem = false;

        // ????????????????????????????????????????????????????????????????????????????????????????????????

        if (mPreferentialReducePrice >= mTotalPrice) {

            if (isNeedValidate) { // ??????????????? ??????????????????
                requestSendCode(mCurrentCheckedVoucherId, "");
            } else { // ?????????????????? ???????????????--???????????????xx?????????????????????????????????????????????????????????
                showUseVoucherFullAmountPayDialog(mCurrentCheckedVoucherId, voucherViews, voucherPrice);
            }
        }
//        else {
//            // ?????????????????????????????????????????????????????????????????????????????????????????????????????????
//            if (cbox_balance.isChecked()) {
//                final double tmpPrice = sub(mTotalPrice, mPreferentialReducePrice).doubleValue();
//                if (mUserBalance >= tmpPrice) {
//                    // ???????????????????????????????????????????????????-???????????????????????????????????????tmpPrice
//                    mBalanceReducePrice = tmpPrice;
//                    // ????????????+????????????????????????
//                    requestSendCode(mCurrentCheckedVoucherId, String.valueOf((int) (mBalanceReducePrice * 100)));
//                } else {
//                    // ??????????????????????????????????????????mUserBalance
//                    mBalanceReducePrice = mUserBalance;
//                }
//            }
//        }
    }

    // ???????????????????????????
    private void requestCheckVoucher(final String voucherIdList, final String orderId, final CompoundButton buttonView) {
        showProgressDialog(this, "????????????...");
        Map<String, String> parameterList = new ArrayMap<String, String>(2);
        parameterList.put("voucherIdList", voucherIdList);
        parameterList.put("orderId", orderId);
        HttpUtil.get(ConstantUrl.GET_CHECK_VOUCHER, parameterList, BaseResultJsonBean.class, new RequestCallback() {

            @Override
            public void onSuccess(Object o) {
                if (null != progressDialog && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                BaseResultJsonBean bBean = (BaseResultJsonBean) o;
                if (bBean.isSuccess()) {
                    if (bBean.getStatus() == 1) {
                        voucherViews.add(buttonView);
                        logicVoucher();
                    } else {
                        buttonView.setChecked(false);
                        MToastUtils.showShortToast(bBean.getMsg());
                    }

                } else {
                    buttonView.setChecked(false);
                    MToastUtils.showShortToast(bBean.getError());
                }
                refreshNeedPayPrice();

            }

            @Override
            public void onFail(Exception e) {
                if (null != progressDialog && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                MToastUtils.showShortToast("????????????");
                buttonView.setChecked(false);
            }
        });
    }

    // ???????????????
    // * voucherIdList????????????id???????????????,??????????????????123654???123659??????
    // balancePayAmount???12 //??????????????????
    // rechargePayAmount???21 //?????????????????????
    // payType???????????????????????????????????????:5, ???????????????:6, ?????????wap:7

    // ??????????????????????????????
    private void requestCardChangePrice(final CardListBean clBean, final View cbView, final String cardId, int useNum, final CompoundButton buttonView) {
        showProgressDialog(this, "????????????...");
        RequestCallback getCardChangeCallback = new RequestCallback() {

            @Override
            public void onSuccess(Object o) {
                if (null != progressDialog && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                CardLogicBean cardLogicBean = (CardLogicBean) o;
                if (cardLogicBean.isSuccess()) {
                    if (isnotVip) {
                        // ??????????????????????????????
//                        countNum = cardLogicBean.getNum();
//                        // ??????
//                        if (clBean.getcType() == 0 || clBean.getcType() == 2) {
//                            cardType = SECOND_CARD;
//                            if (cardLogicBean.getNeedMoney() == 0) {
//                                showMovieCardDlg(clBean.getcId(), "??????????????????????????????" + cardLogicBean.getNum() + "???", buttonView);
//                            }
//
//                        } else if (clBean.getcType() == 1 || clBean.getcType() == 3) {
//                            cardType = DROP_CARD;
//                            if (cardLogicBean.getNeedMoney() == 0) {
//                                showMovieCardDlg(clBean.getcId(), "??????????????????????????????" + cardLogicBean.getNum() + "???", buttonView);
//                            }
//                        }

                    } else {
                        LinearLayout numLin = cbView.findViewById(R.id.num_lin);
                        TextView reduce = cbView.findViewById(R.id.btn_reduce);
                        TextView add = cbView.findViewById(R.id.btn_add);
                        TextView count = cbView.findViewById(R.id.count);
                        // ??????????????????countNum(??????????????????????????????
                        countNum = (new Double(cardLogicBean.getNum())).intValue();
                        // ??????
                        if (clBean.getcType() == 0 || clBean.getcType() == 2) {
                            cardType = SECOND_CARD;
                            tv_reduce_price_preferential.setVisibility(View.VISIBLE);
                            tv_reduce_price_preferential.setText("(-" + countNum + "???)");
                            numLin.setVisibility(View.VISIBLE);

                            reduce.setOnClickListener(new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    countNum--;
                                    requestCardChangePrice(clBean, cbView, cardId, countNum, buttonView);
                                }
                            });
                            add.setOnClickListener(new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    countNum++;
                                    requestCardChangePrice(clBean, cbView, cardId, countNum, buttonView);

                                }
                            });
                            count.setText(String.valueOf(countNum));
                            mPreferentialReducePrice = mTotalPrice - cardLogicBean.getNeedMoney() / 100.0;
                            refreshNeedPayPrice();
                            tv_need_pay_price.setText("???" + MtimeUtils.formatPrice(mNeedPayPrice.doubleValue()));
                            if (mNeedPayPrice.doubleValue() == 0) {
                                showMovieCardDlg(mCurrentCheckedVoucherId, "??????????????????????????????" + countNum + "???", buttonView);
                            }

                        }// ??????
                        else if (clBean.getcType() == 1 || clBean.getcType() == 3) {
                            cardType = DROP_CARD;
                            mPreferentialReducePrice = mTotalPrice - cardLogicBean.getNeedMoney() / 100.0;
                            refreshNeedPayPrice();
                            tv_reduce_price_preferential.setVisibility(View.VISIBLE);
                            tv_reduce_price_preferential.setText("(-" + cardLogicBean.getNum() + "???)");
                            tv_need_pay_price.setText("???" + MtimeUtils.formatPrice(mNeedPayPrice.doubleValue()));
                            if (mNeedPayPrice.doubleValue() == 0) {
                                showMovieCardDlg(mCurrentCheckedVoucherId, "??????????????????????????????" + cardLogicBean.getNum() + "???", buttonView);
                            }

                        }
                    }

                } else {
                    final CustomAlertDlg errorCustomDlg = new CustomAlertDlg(OrderPayActivity.this, CustomAlertDlg.TYPE_OK);
                    errorCustomDlg.setBtnOKListener(new View.OnClickListener() {

                        @Override
                        public void onClick(final View v) {
                            errorCustomDlg.dismiss();

                        }
                    });
                    errorCustomDlg.show();
                    errorCustomDlg.setCancelable(false);
                    errorCustomDlg.setText(cardLogicBean.getMsg());
                }

            }

            @Override
            public void onFail(Exception e) {
                if (null != progressDialog && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        };

        // ???????????????????????????
        Map<String, String> parameterList = new ArrayMap<String, String>(4);
        parameterList.put("orderId", mOrderId);
        parameterList.put("cardId", cardId);
        parameterList.put("useNum", String.valueOf(useNum));
        parameterList.put("token", unLocktoken);
        HttpUtil.post(ConstantUrl.GET_CARD_LOGIC, parameterList, CardLogicBean.class, getCardChangeCallback);
        //TODO X
        OrderPayActivity.this.cardId = cardId;
        OrderPayActivity.this.useNum = String.valueOf(useNum);
        OrderPayActivity.this.token = unLocktoken;
        requestPayItems();

    }

    // ??????????????????

    // ?????????????????????????????????????????????XXX???
    private void showMovieCardDlg(final String id, String msg, final CompoundButton buttonView) {
        final CustomAlertDlg movieCardDlg = new CustomAlertDlg(this, CustomAlertDlg.TYPE_OK_CANCEL);
        movieCardDlg.show();
        movieCardDlg.getTextView().setText(msg);
        movieCardDlg.setBtnCancelListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                movieCardDlg.dismiss();
                if (checkClickListener != null) {
                    buttonView.setChecked(false);
                    checkClickListener.onClick(buttonView);
                }
            }
        });
        movieCardDlg.setBtnOKListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isnotVip) {
                    if (cardType == SECOND_CARD) {
                        notVipGetUseMtimeCard(mOrderId, id, String.valueOf(countNum), unLocktoken);
                    } else if (cardType == DROP_CARD) {
                        notVipGetUseMtimeCard(mOrderId, id, "", unLocktoken);
                    }

                } else {
                    requestSendCode(id, "");
                }

                movieCardDlg.dismiss();
            }
        });
    }

    // ????????????????????????
    private void notVipGetUseMtimeCard(final String orderId, String cardId, String num, String token) {
        mpayDialog = Utils.createProgressDialog(OrderPayActivity.this, "????????????????????????...");
        mpayDialog.show();
        RequestCallback getUseMtimeCardCallback = new RequestCallback() {

            @Override
            public void onSuccess(Object o) {
                if (null != mpayDialog && mpayDialog.isShowing()) {
                    mpayDialog.dismiss();
                }
                BaseResultJsonBean baseResultJsonBean = (BaseResultJsonBean) o;
                if (baseResultJsonBean.isSuccess()) {
                    OrderPayActivity.this.pollMainOrderStatus(orderId);
                } else {
                    showPayErrorDlg(baseResultJsonBean.getMsg());
                }
            }

            @Override
            public void onFail(Exception e) {
                if (null != mpayDialog && mpayDialog.isShowing()) {
                    mpayDialog.dismiss();
                }
            }
        };
        // ?????????????????????(????????? ?????????)
        Map<String, String> parameterList = new ArrayMap<String, String>(5);
        parameterList.put("orderId", orderId);
        parameterList.put("cardId", cardId);
        parameterList.put("useNum", num);
        parameterList.put("token", token);
        parameterList.put("mtimeCardLog", "");
        HttpUtil.post(ConstantUrl.USE_MTIMECARD, parameterList, BaseResultJsonBean.class, getUseMtimeCardCallback);
    }

    private void requestSendCode(final String voucherIdList, final String balancePayAmount) {
        requestSendCode(voucherIdList, balancePayAmount, "", "");
    }

    private void requestSendCode(final String voucherIdList, final String balancePayAmount, final String rechargePayAmount, final String payType) {
        requestSendCode(voucherIdList, balancePayAmount, rechargePayAmount, payType, "");
    }

    // ?????????????????? ,??????????????????
    private void requestSendCode(final String voucherIdList, final String balancePayAmount, final String rechargePayAmount, final String payType,
                                 final String bankId) {

        // ???????????????????????? ?????????????????????
        final String bindMobile = UserManager.Companion.getInstance().getBindMobile();
        if (payBindphone == null || payBindphone.equals("")) {

            if (bindMobile == null || bindMobile.equals("")) {
                // intent.putExtra(Constant., value)
                goBindPhone(voucherIdList, balancePayAmount, rechargePayAmount, payType, bankId);
                return;
            }
        }
        securitydlg = new SecurityDlg(OrderPayActivity.this);
        securitydlg.show();
        securitydlg.setTitle("????????????");
        securitydlg.setCanceledOnTouchOutside(false);
        securitydlg.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    securitydlg.dismiss();
                    setVoucherViewCheck();
//                    cbox_balance.setChecked(false);
                    requestPayItems();
                }
                return false;
            }
        });
        if (bindMobile != null) {
            securitydlg.setTelText(MtimeUtils.getBindMobile(bindMobile));
        }
        securitydlg.getInfo().setText("????????????????????????????????????????????????????????????");
        securitydlg.setBtnSendClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != mDialog && mDialog.isShowing()) {
                    mDialog.show();
                }
                RequestCallback sendCodeCallback = new RequestCallback() {

                    @Override
                    public void onSuccess(Object o) {
                        if (null != mDialog && mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        BaseResultJsonBean bean = (BaseResultJsonBean) o;
                        if (bean.isSuccess()) {
                            if (bean.getStatus() == 6) {
                                MToastUtils.showShortToast(bean.getMsg());
                                if (cardType == VOUCHER_CARD || cardType == -1) {
                                    doBlendPay(mOrderId, "", voucherIdList, balancePayAmount, rechargePayAmount, payType, "", "", "", bankId);
                                } else if (cardType == SECOND_CARD) {
                                    doBlendPay(mOrderId, "", "", balancePayAmount, rechargePayAmount, payType, voucherIdList, String.valueOf(countNum), unLocktoken,
                                            bankId);
                                } else if (cardType == DROP_CARD) {
                                    doBlendPay(mOrderId, "", "", balancePayAmount, rechargePayAmount, payType, voucherIdList, "", unLocktoken, bankId);
                                }
                            } else {
                                securitydlg.setTime(60);
                                securitydlg.setThreadStart();
                                if (payBindphone.equals(" ")) {
                                    if (bindMobile != null) {
                                        securitydlg.setTelText(MtimeUtils.getBindMobile(bindMobile));
                                    }
                                    securitydlg.getInfo().setText("????????????????????????????????????????????????????????????");
                                } else {

                                }
                            }

                        } else {
                            String msg = bean.getMsg();
                            if (msg == null) {
                                msg = "?????????????????????????????????";
                            }
                            MToastUtils.showShortToast(msg);
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        if (null != mDialog && mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        MToastUtils.showShortToast("?????????????????????????????????");
                    }
                };
                String orderType = "";
                Map<String, String> parameterList = new ArrayMap<String, String>(3);
                parameterList.put("orderId", mOrderId);
                parameterList.put("mobile", payBindphone);
                if (!TextUtils.isEmpty(orderType)) {
                    parameterList.put("orderType", orderType);
                }
                HttpUtil.post(ConstantUrl.SEND_CODE, parameterList, BaseResultJsonBean.class, sendCodeCallback);
            }
        });
        securitydlg.setBtnOkClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (securitydlg.getSecurityEdit().getText().toString().equals("")) {
                    MToastUtils.showShortToast("??????????????????");
                    return;
                }
                if (cardType == VOUCHER_CARD || cardType == -1) {
                    doBlendPay(mOrderId, securitydlg.getSecurityEdit().getText().toString(), voucherIdList, balancePayAmount, rechargePayAmount, payType, "",
                            "", "", bankId);
                } else if (cardType == SECOND_CARD) {
                    doBlendPay(mOrderId, securitydlg.getSecurityEdit().getText().toString(), "", balancePayAmount, rechargePayAmount, payType, voucherIdList,
                            String.valueOf(countNum), unLocktoken, bankId);
                } else if (cardType == DROP_CARD) {
                    doBlendPay(mOrderId, securitydlg.getSecurityEdit().getText().toString(), "", balancePayAmount, rechargePayAmount, payType, voucherIdList,
                            "", unLocktoken, bankId);
                }

            }
        });
        securitydlg.setBtnBackClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                securitydlg.dismiss();
                setVoucherViewCheck();
//                cbox_balance.setChecked(false);
                requestPayItems();
            }
        });

    }

    // ???????????????????????????????????????
    protected void setVoucherViewCheck() {
        if (voucherViews.size() == 1) {
            CompoundButton v = voucherViews.get(0);
            if (v.getTag(R.id.tag_first) instanceof CardListBean) {
                CardListBean clBean = (CardListBean) v.getTag(R.id.tag_first);
                View cbView = (View) v.getTag(R.id.tag_second);
                LinearLayout numLin = cbView.findViewById(R.id.num_lin);
                if (clBean.getcType() == 0 || clBean.getcType() == 2) {
                    numLin.setVisibility(View.GONE);
                }

            }
        }
        if (voucherViews.size() > 0) {
            for (int i = 0; i < voucherViews.size(); i++) {
                voucherViews.get(i).setChecked(false);
            }
            for (int i = 0; i < allVoucherViews.size(); i++) {
                allVoucherViews.get(i).setChecked(false);
            }
        }
        allVoucherViews.clear();
        voucherViews.clear();
        voucherIdList = "";
        tv_reduce_price_preferential.setVisibility(View.GONE);
        tv_reduce_price_preferential.setText("(-???0)");
        mPreferentialReducePrice = 0;
    }

    // ????????????
    // payStatus: 1???????????????0 ???????????????-1???????????????-2???????????????-3????????????-4???????????????-5
    // ??????????????????-6?????????????????????-7??????????????????-8?????????????????????-9??????????????????????????????-10????????????????????????-11?????????????????????-12????????????????????????-13???????????????????????????-40??????????????????????????????
    private void doBlendPay(final String orderId, final String vcode, final String voucherIdList, final String balancePayAmount, String rechargePayAmount,
                            final String payType, final String cardId, final String useNum, final String token, final String bankId) {
        if (null != mDialog && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mpayDialog = Utils.createProgressDialog(OrderPayActivity.this, "????????????????????????...");
        mpayDialog.show();
        hasCreateOrderFinished = true;
        childDoBlendPay(orderId, vcode, voucherIdList, balancePayAmount, rechargePayAmount, payType, cardId, useNum, token, bankId);

    }

    // ?????????????????????
    private void goBindPhone(final String voucherIdList, final String balancePayAmount, final String rechargePayAmount, final String payType,
                             final String bankId) {
        final CustomAlertDlg bindDlg = new CustomAlertDlg(OrderPayActivity.this, CustomAlertDlg.TYPE_OK_CANCEL);
        bindDlg.show();
        bindDlg.setText("????????????????????????\n?????????????????????????????????");
        bindDlg.getBtnOK().setText("????????????");
        bindDlg.getBtnCancel().setText("??????");
        bindDlg.setBtnCancelListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                payBindphone = "";
                setVoucherViewCheck();
//                cbox_balance.setChecked(false);
                bindDlg.dismiss();
                requestPayItems();
            }
        });
        bindDlg.setBtnOKListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setVoucherViewCheck();
//                cbox_balance.setChecked(false);
                mBalanceReducePrice = 0;
//                tv_reduce_price_balance.setVisibility(View.VISIBLE);
//                tv_reduce_price_balance.setText("-???" + MtimeUtils.formatPrice(mBalanceReducePrice));
                refreshNeedPayPrice();
                Intent intent = new Intent();
                intent.putExtra("voucherIdList", voucherIdList);
                intent.putExtra("balancePayAmount", balancePayAmount);
                intent.putExtra("rechargePayAmount", rechargePayAmount);
                intent.putExtra("payType", payType);
                intent.putExtra("bankId", bankId);
                intent.putExtra(App.getInstance().KEY_BIND_TYPE, 4);// ?????????type??? 4???????????????
                intent.putExtra(Constants.BIND_HAD_PASSWORD, UserManager.Companion.getInstance().getHasPassword());
                startActivityForResult(BindPhoneActivity.class, intent, 2);
                bindDlg.dismiss();
            }
        });
        bindDlg.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    payBindphone = "";
                    setVoucherViewCheck();
//                    cbox_balance.setChecked(false);
                    bindDlg.dismiss();
                    requestPayItems();
                }
                return false;
            }
        });
    }

    /**
     * ?????????????????????????????????
     */
    protected void refreshNeedPayPrice() {
        mNeedPayPrice = sub(sub(mTotalPrice, mPreferentialReducePrice).doubleValue(), mBalanceReducePrice);
        if (mNeedPayPrice.doubleValue() < 0) {
            // ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            mNeedPayPrice = BigDecimal.valueOf(0.0);
        }
        tv_need_pay_price.setText("???" + MtimeUtils.formatPrice(mNeedPayPrice.doubleValue()));
    }

    // ???????????????????????????
    private void logicThirdPay(int bankId, String from) {
        String bankIdString = null;
        if (bankId == 0) {
            bankIdString = "";
        } else {
            bankIdString = String.valueOf(bankId);
        }
        if (isnotVip) {
            doNotVipPay(bankIdString);
        } else {
            String balancePrice = MtimeUtils.moneyY2F(mBalanceReducePrice);
            String needPay = String.valueOf(mNeedPayPrice.multiply(new BigDecimal(100)).intValue());

            if (mPreferentialReducePrice > 0) {
                // (?????????+???????????????)????????????+?????????+??????????????????
                if (mBalanceReducePrice > 0) { // ?????????+?????????+??????????????????

                    requestSendCode(mCurrentCheckedVoucherId, balancePrice, needPay, String.valueOf(payType), bankIdString);
                } else {
                    // (?????????+???????????????)

                    if (isNeedValidate) { // ???????????????
                        requestSendCode(mCurrentCheckedVoucherId, balancePrice, needPay, String.valueOf(payType), bankIdString);
                    } else {
                        // ??????????????????
                        doBlendPay(mOrderId, "", mCurrentCheckedVoucherId, "", needPay, String.valueOf(payType), "", "", "", bankIdString);
                    }
                }

            } else {
                if (mBalanceReducePrice > 0) {
                    // ??????+???????????????
                    requestSendCode("", balancePrice, needPay, String.valueOf(payType), bankIdString);
                } else {
                    // // (???????????????)
                    // doThirdPartyPay(mOrderId, mNeedPayPrice, payType);
                    // ??????????????????
                    // (int) (mNeedPayPrice * 100) 2.01 * 100??????200????????????bigDecimal
                    doBlendPay(mOrderId, "", "", "", needPay, String.valueOf(payType), "", "", "", bankIdString);
                }

            }
        }

    }

    // ???????????????
    private void doNotVipPay(String bankId) {
        showProgressDialog(this, "????????????????????????...");
        RequestCallback notVipCallback = new RequestCallback() {

            @Override
            public void onSuccess(Object o) {
                if (null != progressDialog && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                AnonymousPayBean result = (AnonymousPayBean) o;
                if (result.isSuccess()) { // ????????????
                    // TODO: 2018/5/3 weiwenli ??????+??????/???????????????
                    final String formXML = result.getFormXML(); // ????????????
                    if (payType == App.getInstance().PAYTYPE_YINLIAN) {  // ??????????????????
                        if (!TextUtils.isEmpty(upompSeType)
                                && (upompSeType.equals(UPOMP_SE_TYPE_SAMSUNG) || upompSeType.equals(UPOMP_SE_TYPE_HUAWEI))) {
                            // ??????????????????_??????pay??????
                            MtimeUtils.doSEPay(OrderPayActivity.this, formXML, upompSeType);
                        } else {
                            // ??????????????????
                            MtimeUtils.doPay(OrderPayActivity.this, formXML);
                        }
                    } else if (payType == App.getInstance().PAYTYPE_ALIPAY) {
                        OrderPayActivity.this.alipay(formXML);
                    } else if (payType == App.getInstance().PAYTYPE_ALIPAY_WAP) {
                        alipayWap(formXML);
                    } else if (payType == App.getInstance().PAYTYPE_WEIXIN) {
                        weixinPay(formXML);
                    }
                } else {
                    showPayErrorDlg(result.getMsg());
                }
            }

            @Override
            public void onFail(Exception e) {
                if (null != progressDialog && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        };
        Map<String, String> parameterList = new ArrayMap<String, String>(6);
        parameterList.put("orderId", mOrderId);
        parameterList.put("amount", String.valueOf(mNeedPayPrice.multiply(new BigDecimal(100)).intValue()));
        parameterList.put("payType", String.valueOf(payType));
        parameterList.put("bankId", bankId);
        parameterList.put("returnURL", "");
        if (activityIds != null && !"".equals(activityIds.trim()) && !"-1".equals(activityIds.trim())) {
            parameterList.put("activityIds", activityIds);
        }
        HttpUtil.post(ConstantUrl.USE_NOTVIPPAY, parameterList, AnonymousPayBean.class, notVipCallback);
    }

    /**
     * ?????????????????????????????????????????????????????????????????????????????????
     */
    private void showUseVoucherFullAmountPayDialog(final String voucherIdList, final ArrayList<CompoundButton> buttonView, final double voucherPrice) {
        final CustomAlertDlg dlg = new CustomAlertDlg(this, CustomAlertDlg.TYPE_OK_CANCEL);
        dlg.setBtnOKListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {

                // ??????????????????????????????
                doBlendPay(mOrderId, "", voucherIdList, "", "", "", "", "", "", "");

                dlg.dismiss();
            }
        });
        dlg.setBtnCancelListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                // ?????????????????????????????????????????????
                setVoucherViewCheck();
                mPreferentialReducePrice = 0;
                tv_reduce_price_preferential.setVisibility(View.GONE);
                refreshNeedPayPrice();
                dlg.dismiss();
            }
        });
        dlg.show();
        dlg.setCancelable(false);
        dlg.getTextView().setText("????????????" + voucherPrice + "??????????????????????????????????????????????????????");
        dlg.getBtnOK().setText("????????????");
        dlg.getBtnCancel().setText("??????");
    }

    // ??????????????????
    protected void showTicketOutingDialog() {
        if (ticketOutingDialog == null) {
            ticketOutingDialog = new OrderPayTicketOutingDialog(OrderPayActivity.this);
        }
        if (!ticketOutingDialog.isShowing()) {
            ticketOutingDialog.show();
            ticketOutingDialog.setCancelable(false);
        }
    }

    protected void alipayWap(final String fromXML) {
        Intent intent = new Intent();
        intent.putExtra(App.getInstance().WAP_PAY_URL, fromXML);
        startActivityForResult(WapPayActivity.class, intent, 1);

    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);
        if (arg2 != null) {
            /*
             * ???????????????????????????:success???fail???cancel ??????????????????????????????????????????????????????
             */
            // ??????????????????
            String str = arg2.getExtras().getString("pay_result");
            if (str != null && !str.equals("")) {
                if (str.equalsIgnoreCase("success")) {
                    pollMainOrderStatus(mOrderId);
                } else if (str.equalsIgnoreCase("fail")) {
                    if (!isCBGoto) {
                        showUpompPayError("????????????");
                    } else {
                        //????????????
                        showCBPayAgainDlg();
                    }

                } else if (str.equalsIgnoreCase("cancel")) {
                    if (!isCBGoto) {
                        showUpompPayError("?????????????????????");
                    } else {
                        showCBPayAgainDlg();
                    }
                }
            }

            String fromXML = arg2.getStringExtra(App.getInstance().WAP_PAY_URL);
            // ?????????????????????
            if (arg1 == 0 && fromXML != null) {
                OrderPayActivity.this.completepay(fromXML, payType);
            }
            int bankId = arg2.getIntExtra(App.getInstance().KEY_BANK_ID, 0);
            // ?????????????????????
            if (arg1 == 2) {
                logicThirdPay(bankId, "?????????");
            }
            // ??????????????????
            if (arg1 == 4) {
                payBindphone = arg2.getStringExtra(App.getInstance().KEY_BINDPHONE);
                requestFirstSendCode(arg2.getStringExtra("voucherIdList"), arg2.getStringExtra("balancePayAmount"), arg2.getStringExtra("rechargePayAmount"),
                        arg2.getStringExtra("payType"), arg2.getStringExtra("bankId"));
            }
        }

        // ????????????
        if (arg1 == 5) {
            isRecharge = true;
            requestPayItems();
        }
        // ??????????????????????????????
        if (arg1 == 6) {
            if (isDoWithoutPayOrder) {
                OrderPayActivity.this.requestLastOrderDetailInfo(OrderPayActivity.this.mOrderId);
            }
            getOrderStatue(NORMAL_ORDER_TYPE);

        }
        // ????????????????????????????????????
        if (arg1 == 7) {
            finish();
        }

        // ????????????????????????
        if (arg0 == 10) {
//            if (arg1 == 11) {
//                // ???????????????????????????
//                showActivateVoucherCodeDialog();
//            } else if (arg1 == 12) {
//                // ???????????????
//                showAddMovieCard(true);
//            }
        }

        // ?????????????????????????????????/???????????????
        if (arg0 == REQUEST_CODE_MY_WALLET) {
            // ???????????????????????????
            requestPayItems();
        }
    }

    // ???????????????????????????????????????
    private void requestFirstSendCode(final String voucherIdList, final String balancePayAmount, final String rechargePayAmount, final String payType,
                                      final String bankId) {
        if (canShowDlg) {
            mDialog.show();
        }
        RequestCallback sendCodeCallback = new RequestCallback() {

            @Override
            public void onSuccess(Object o) {
                if (null != mDialog && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                BaseResultJsonBean bean = (BaseResultJsonBean) o;
                if (bean.isSuccess()) {
                    securitydlg = new SecurityDlg(OrderPayActivity.this);
                    securitydlg.show();
                    securitydlg.setOnKeyListener(new OnKeyListener() {

                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                payBindphone = "";
                                setVoucherViewCheck();
//                                cbox_balance.setChecked(false);
                            }
                            return false;
                        }
                    });
                    securitydlg.setTime(60);
                    securitydlg.setThreadStart();
                    if (payBindphone != null) {
                        securitydlg.setTelText(MtimeUtils.getBindMobile(payBindphone));
                    }
                    securitydlg.getInfo().setText("????????????????????????????????????????????????????????????");
                    securitydlg.setBtnOkClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (securitydlg.getSecurityEdit().getText().toString().equals("")) {
                                MToastUtils.showShortToast("??????????????????");
                                return;
                            }
                            if (cardType == VOUCHER_CARD || cardType == -1) {
                                doBlendPay(mOrderId, securitydlg.getSecurityEdit().getText().toString(), voucherIdList, balancePayAmount, rechargePayAmount,
                                        payType, "", "", "", bankId);
                            } else if (cardType == SECOND_CARD) {
                                doBlendPay(mOrderId, securitydlg.getSecurityEdit().getText().toString(), "", balancePayAmount, rechargePayAmount, payType,
                                        voucherIdList, String.valueOf(countNum), unLocktoken, bankId);
                            } else if (cardType == DROP_CARD) {
                                doBlendPay(mOrderId, securitydlg.getSecurityEdit().getText().toString(), "", balancePayAmount, rechargePayAmount, payType,
                                        voucherIdList, "", unLocktoken, bankId);
                            }

                        }
                    });
                    securitydlg.setBtnBackClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            payBindphone = "";
                            securitydlg.dismiss();
                            // securitydlg.getThread().stop();
                            setVoucherViewCheck();
//                            cbox_balance.setChecked(false);
                        }
                    });

                } else {
                    payBindphone = "";
                    String msg = bean.getMsg();
                    if (msg == null) {
                        msg = "?????????????????????????????????";
                    }
                    MToastUtils.showShortToast(msg);
                }
            }

            @Override
            public void onFail(Exception e) {
                if (null != mDialog && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                MToastUtils.showShortToast("?????????????????????????????????");
            }
        };
        String orderType = "";
        if (isFromMall) {
            orderType = "94";
        }
        Map<String, String> parameterList = new ArrayMap<String, String>(3);
        parameterList.put("orderId", mOrderId);
        parameterList.put("mobile", payBindphone);
        if (!TextUtils.isEmpty(orderType)) {
            parameterList.put("orderType", orderType);
        }
        HttpUtil.post(ConstantUrl.SEND_CODE, parameterList, BaseResultJsonBean.class, sendCodeCallback);

    }

    /**
     * ???????????????
     *
     * @param orderInfo
     */
    protected void alipay(final String orderInfo) {
        // ????????????????????????????????????
        Acp.getInstance(this).request(new AcpOptions.Builder()
                        .setPermissions(
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ).build(),
                new AcpListener() {
                    @Override
                    public void onGranted() {
                        Acp.getInstance(getApplicationContext()).onDestroy();
                        // ????????????????????????
                        alipayGranted(orderInfo);
                    }

                    @Override
                    public void onDenied(List<String> permissions) {
                        Acp.getInstance(getApplicationContext()).onDestroy();
                        MToastUtils.showShortToast(permissions.toString() + "????????????");
                    }
                });
    }

    // ????????????????????????
    private void alipayGranted(final String orderInfo) {
        final Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // ??????PayTask ??????
                PayTask alipay = new PayTask(OrderPayActivity.this);
                // ???????????????????????????????????????
                Map<String, String> rawResult = alipay.payV2(orderInfo, true);
                // v15.5.9??????????????????????????????????????????????????????https://api-m.mtime.cn/Account/PayReturn.api??????????????????
                // ????????????????????????????????????ios???????????????
                AlipayPayResult payResult = new AlipayPayResult(rawResult);
                if (payResult != null && !TextUtils.isEmpty(payResult.getResultStatus()) && payResult.getResultStatus().equals("6001")) {
                    // ??????????????????????????????????????????????????????
                    getOrderStatue(NORMAL_ORDER_TYPE, false);
                } else {
                    String result = payResult.toString();

                    Message msg = new Message();
                    msg.what = SDK_PAY_FLAG;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }
            }
        };

        // ??????????????????
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * App????????????????????????
     */
    private void completepay(final String formXML, final int payType) {
        // ??????http://api.m.mtime.cn/Account/PayReturn.api????????????true
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if ((mpayDialog != null) && mpayDialog.isShowing()) {
            mpayDialog.dismiss();
        }
        if (null != mDialog && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        // ?????????????????????
        pollMainOrderStatus(mOrderId);

        // TODO: 2019-09-19 ?????? wwl
//        showProgressDialog(this, this.getString(R.string.ticketpayInfo));
//        final RequestCallback payReturnCallback = new RequestCallback() {
//            @Override
//            public void onSuccess(final Object o) {
//                if (null != progressDialog && progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
//                if ((mpayDialog != null) && mpayDialog.isShowing()) {
//                    mpayDialog.dismiss();
//                }
//                if (null != mDialog && mDialog.isShowing()) {
//                    mDialog.dismiss();
//                }
//                final BaseResultJsonBean payResult = (BaseResultJsonBean) o;
//                if (payResult == null) {
//                    return;
//                }
//
//                final int status = payResult.getStatus();
//                if (payResult.isSuccess()) { // ????????????
//                    pollMainOrderStatus(OrderPayActivity.this.mOrderId);
//                } else { // ????????????
//                    if (isFromMall) {
//                        getOrderStatue(NORMAL_ORDER_TYPE);
//                    }
//                    childPayReturnFailedComplete(status, payResult);
//                }
//            }
//
//            @Override
//            public void onFail(final Exception e) {
//                if (null != mDialog && mDialog.isShowing()) {
//                    mDialog.dismiss();
//                }
//                getOrderStatue(NORMAL_ORDER_TYPE);
//            }
//
//        };
//        if (isFromMall) {
//            // ??????
//            Map<String, String> parameterList = new ArrayMap<String, String>(2);
//            parameterList.put("formXML", formXML);
//            parameterList.put("payType", String.valueOf(payType));
//            if (presellPaymentMode == 1) {
//                HttpUtil.post(ConstantUrl.POST_MALL_GOODS_PAY_RETURN, parameterList, BaseResultJsonBean.class, payReturnCallback);
//            } else if (presellPaymentMode == 2) {
//                if (isFinalPay == 0) {// ????????????????????????
//                    HttpUtil.post(ConstantUrl.POST_MALL_GOODS_PAY_RETURN_DEPOSIT, parameterList, BaseResultJsonBean.class, payReturnCallback);
//                } else if (isFinalPay == 1) {// ????????????????????????
//                    HttpUtil.post(ConstantUrl.POST_MALL_GOODS_PAY_RETURN_FINAL, parameterList, BaseResultJsonBean.class, payReturnCallback);
//                }
//            }
//        } else {
//            // ???????????????????????????????????????
//            Map<String, String> parameterList = new ArrayMap<String, String>(2);
//            parameterList.put("formXML", formXML);
//            parameterList.put("payType", String.valueOf(payType));
//            HttpUtil.post(ConstantUrl.PAY_RETURN, parameterList, BaseResultJsonBean.class, payReturnCallback);
//        }
    }

    // ??????????????????
    protected void getOrderStatue(final int type) {
        getOrderStatue(type, true);
    }

    // ??????????????????(showProgress?????????true, ???????????????????????????false????????????Thread?????????Dialog????????????
    protected void getOrderStatue(final int type, final boolean showProgress) {
        if (showProgress) {
            showProgressDialog(OrderPayActivity.this, "????????????...");
        }
        RequestCallback getOrderStatueCallback = new RequestCallback() {

            @Override
            public void onSuccess(Object o) {
                if (!canShowDlg) {
                    return;
                }

                final OrderStatusJsonBean order = (OrderStatusJsonBean) o;
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (order.getPayStatus() == 0) {// ?????????0 ?????????????????????????????????????????????????????????????????????
                    if (type == NORMAL_ORDER_TYPE) {
                        showPayAgainDlg();
                    } else if (type == AGAIN_ORDER_TYPE) {
                        if (null != againCustomAlertDlg && againCustomAlertDlg.isShowing()) {
                            againCustomAlertDlg.dismiss();
                        }
                        //????????????, ??????????????????
                        topVoucherId = "";
                        activityIds = "";
                        voucherIdList = "";
                        cardId = "";
                        useNum = "";
                        token = "";
                        requestPayItems();
                    } else if (type == OVER_ORDER_TYPE) {
                        showPayWaitDlg();
                    }

                } else if (order.getPayStatus() == 1) { // 1 ????????? ???????????????
                    pollMainOrderStatus(mOrderId);
                } else {
                    MToastUtils.showShortToast("????????????");
                }
            }

            @Override
            public void onFail(Exception e) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (type == NORMAL_ORDER_TYPE) {
                    showPayAgainDlg();
                } else if (type == OVER_ORDER_TYPE) {
                    showPayWaitDlg();
                } else {
                    MToastUtils.showShortToast("??????????????????,???????????????");
                }
            }
        };
        Map<String, String> parameterList = new ArrayMap<String, String>(1);
        parameterList.put("orderId", mOrderId);
        HttpUtil.post(ConstantUrl.GET_ORDER_STATUS, parameterList, OrderStatusJsonBean.class, getOrderStatueCallback);
    }

    // ???????????????????????????????????????
    protected void showPayAgainDlg() {
        if (!canShowDlg) {
            return;
        }

        OrderPayActivity.this.clockFlag = false;
        if (againCustomAlertDlg != null && againCustomAlertDlg.isShowing()) {
            againCustomAlertDlg.dismiss();
        }
        againCustomAlertDlg = new OrderPayAgainDialog(OrderPayActivity.this);
        againCustomAlertDlg.show();
        againCustomAlertDlg.setCanceledOnTouchOutside(false);
        againCustomAlertDlg.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
        againCustomAlertDlg.setBtnChangeListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderPayActivity.this.clockFlag = true;
                // ????????????????????????
                getOrderStatue(AGAIN_ORDER_TYPE);
            }
        });
        againCustomAlertDlg.setBtnOKListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // ????????????????????????
                getOrderStatue(OVER_ORDER_TYPE);
            }
        });

    }

    // ????????????_??????????????????dialog
    protected void showCBPayAgainDlg() {
        clockFlag = false;
        if (againCBCustomAlertDlg != null && againCBCustomAlertDlg.isShowing()) {
            againCBCustomAlertDlg.dismiss();
        }
        againCBCustomAlertDlg = new OrderPayAgainDialog(OrderPayActivity.this);
        againCBCustomAlertDlg.show();
        againCBCustomAlertDlg.setCanceledOnTouchOutside(false);
        againCBCustomAlertDlg.setCancleText("????????????");
        againCBCustomAlertDlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
        // ??????????????????
        againCBCustomAlertDlg.setBtnChangeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clockFlag = true;
                cancelOrder();
            }
        });
        // ????????????
        againCBCustomAlertDlg.setBtnOKListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ????????????????????????
                getOrderStatue(OVER_ORDER_TYPE);
            }
        });

    }

    protected void showPayWaitDlg() {
        final CustomAlertDlg waitAlertDlg = new CustomAlertDlg(OrderPayActivity.this, CustomAlertDlg.TYPE_OK);
        waitAlertDlg.show();
        waitAlertDlg.setBtnOKListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                waitAlertDlg.dismiss();
            }
        });
        waitAlertDlg.getBtnOK().setText("????????????");
        waitAlertDlg.setText("???????????????????????????");
    }

    // ????????????????????????????????????????????????????????????
    private void showUpompPayError(String errorInfo) {
        if (errorInfo == null || errorInfo.equals("")) {
            errorInfo = "????????????";
        }

        mCustomDlg = new CustomAlertDlg(OrderPayActivity.this, CustomAlertDlg.TYPE_OK);
        mCustomDlg.setBtnOKListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                mCustomDlg.dismiss();
                // ??????????????????_????????????_?????????????????????????????????????????????????????????????????????????????????????????????????????????"??????????????????"???????????????
                OrderPayActivity.this.clockFlag = true;
                // ????????????????????????
                getOrderStatue(AGAIN_ORDER_TYPE);
            }
        });
        mCustomDlg.show();
        mCustomDlg.setCancelable(false);
        mCustomDlg.setText(errorInfo);
    }

    /**
     * ????????????
     */
    protected void showPayErrorDlg(String errorInfo) {
        if (errorInfo == null || errorInfo.equals("")) {
            errorInfo = "????????????";
        }

        mCustomDlg = new CustomAlertDlg(OrderPayActivity.this, CustomAlertDlg.TYPE_OK);
        mCustomDlg.setBtnOKListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                mCustomDlg.dismiss();
                getOrderInfo();
            }
        });
        mCustomDlg.show();
        mCustomDlg.setCancelable(false);
        mCustomDlg.setText(errorInfo);
    }

    // ??????????????????
    private void getOrderInfo() {
        if (mIsEticket) {
            RequestCallback eticketOrderInfoCallback = new RequestCallback() {

                @Override
                public void onSuccess(Object o) {
                    ETicketDetailBean eBean = (ETicketDetailBean) o;
                    mTotalPrice = Double.parseDouble(MtimeUtils.moneyF2Y(eBean.getSalesAmount()));
                    mPreferentialReducePrice = 0;
                    mBalanceReducePrice = 0;
                    refreshNeedPayPrice();
                }

                @Override
                public void onFail(Exception e) {

                }
            };
            // Eticket/getETicketOrderInfo.api?orderId={0}
            Map<String, String> param = new HashMap<>(1);
            param.put("orderId", mOrderId);
            HttpUtil.get(ConstantUrl.GET_ETICKET_ORDER_INFO, param, ETicketDetailBean.class, eticketOrderInfoCallback);
        } else {
            RequestCallback orderInfoCallback = new RequestCallback() {

                @Override
                public void onSuccess(Object o) {
                    OnlineOrderInfoJsonBean jBean = (OnlineOrderInfoJsonBean) o;
                    mTotalPrice = Double.parseDouble(MtimeUtils.moneyF2Y(jBean.getSalesAmount()));
                    mPreferentialReducePrice = 0;
                    mBalanceReducePrice = 0;
                    refreshNeedPayPrice();
                }

                @Override
                public void onFail(Exception e) {

                }
            };
            //  Showtime/getOnlineOrderInfo.api?orderId={0}
            Map<String, String> param = new HashMap<>(1);
            param.put("orderId", mOrderId);
            HttpUtil.get(ConstantUrl.GET_ONLINE_ORDER_INFO, param, OnlineOrderInfoJsonBean.class, orderInfoCallback);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        setUsedVoucherChecked();
    }

    /**
     * ????????????
     *
     * @param d1
     * @param d2
     * @return
     */
    protected BigDecimal sub(final double d1, final double d2) {
        final BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        final BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return bd1.subtract(bd2);
    }

    /**
     * ????????????????????????
     *
     * @param errorType      ????????????
     * @param errorTitle     ????????????
     * @param errorDetail    ????????????
     * @param errorButtonMsg ??????????????????
     */
    protected void gotoPayFailedActivity(final int errorType, final String errorTitle, final String errorDetail, final String errorButtonMsg) {
        final Intent intent = new Intent();
        intent.putExtra(App.getInstance().PAY_ETICKET, mIsEticket); // ??????????????????
        intent.putExtra(App.getInstance().KEY_SEATING_ORDER_ID, mOrderId); // ??????id
        intent.putExtra(App.getInstance().KEY_PAY_ERROR_TYPE, errorType);
        intent.putExtra(App.getInstance().KEY_PAY_ERROR_TITLE, errorTitle);
        intent.putExtra(App.getInstance().KEY_PAY_ERROR_DETAIL, errorDetail);
        intent.putExtra(App.getInstance().KEY_PAY_ERROR_BUTTON_MESSAGE, errorButtonMsg);
        intent.putExtra(App.getInstance().KEY_CINEMA_PHONE, mCinemaPhone);

        intent.putExtra(App.getInstance().KEY_SEATING_SELECT_AGAIN, true);// ??????????????????
        intent.putExtra(App.getInstance().KEY_SEATING_TOTAL_PRICE, mTotalPrice);
        intent.putExtra(App.getInstance().KEY_SEATING_SERVICE_FEE, mServiceFee); // ?????????
        intent.putExtra(App.getInstance().KEY_MOVIE_NAME, mMovieName);
        intent.putExtra(App.getInstance().KEY_CINEMA_NAME, mCinemaName);
        intent.putExtra(App.getInstance().KEY_CINEMA_PHONE, mCinemaPhone);
        intent.putExtra(App.getInstance().KEY_SEATING_SEAT_ID, mSeatId);
        intent.putExtra(App.getInstance().KEY_SEATING_SELECTED_SEAT_COUNT, mSelectedSeatCount); // ?????????
        intent.putExtra(App.getInstance().KEY_SEATING_DID, mDId);
        intent.putExtra(App.getInstance().KEY_TICKET_DATE_INFO, mTicketDateInfo);
        intent.putExtra(App.getInstance().KEY_SEAT_SELECTED_INFO, mSeatSelectedInfo);
        this.startActivity(OrderPayFailedActivity.class, intent);
        if (mIsEticket) {
            finish();
        }

    }

    /**
     * ??????????????????????????????????????????/???????????????
     */
    protected void requestLastOrderDetailInfo(final String orderId) {
        if (mIsEticket) {
            // ???????????????????????????
            requestETicketOrderDetailInfo(orderId);

        } else {
            // ??????????????????????????????
            requestOnlineTicketDetailInfo(orderId);
        }
    }

    // ????????????????????????
    private void requestOnlineTicketDetailInfo(final String orderId) {
        final RequestCallback orderDetailCallback = new RequestCallback() {
            @Override
            public void onSuccess(final Object o) {
                final TicketDetailBean ticketBean = (TicketDetailBean) o;
                mTotalPrice = Double.parseDouble(MtimeUtils.moneyF2Y(ticketBean.getSalesAmount() + ticketBean.getDeductedAmount()));
                OrderPayActivity.this.mMovieName = ticketBean.getMovieTitle();
                OrderPayActivity.this.mCinemaName = ticketBean.getCname();
                OrderPayActivity.this.mCinemaPhone = ticketBean.getCtel();
                OrderPayActivity.this.mPayEndTime = ticketBean.getPayEndTime();
                OrderPayActivity.this.mUserBuyTicketPhone = ticketBean.getMobile();
                OrderPayActivity.this.mTicketDateInfo = OrderPayActivity.this.createTicketDateInfo(ticketBean.getShowtimeLong(), ticketBean.getHallName(),
                        ticketBean.getVersionDesc(), ticketBean.getLanguage());

                // ?????????????????????
                if (OrderPayActivity.this.isnotVip) {
                    requestPayItems();
                } else {
                    requestPayItems();
                }
            }

            @Override
            public void onFail(final Exception e) {
                if (null != mDialog && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                OrderPayActivity.this.finish();
            }
        };

        // /order/onlineticketdetail.api?orderId={orderId}
        Map<String, String> param = new HashMap<>(1);
        param.put("orderId", orderId);
        HttpUtil.get(ConstantUrl.ONLINE_TICKET_DATEIL, param, TicketDetailBean.class, orderDetailCallback);
    }

    // ???????????????????????????
    private void requestETicketOrderDetailInfo(final String orderId) {
        final RequestCallback orderDetailCallback = new RequestCallback() {
            @Override
            public void onSuccess(final Object o) {
                final ETicketDetailBean bean = (ETicketDetailBean) o;
                mTotalPrice = Double.parseDouble(MtimeUtils.moneyF2Y(bean.getAmount()));
                OrderPayActivity.this.mServiceFee = bean.getServiceFee() / 100.0;
                OrderPayActivity.this.mPayEndTime = bean.getPayEndTime();
                OrderPayActivity.this.mCinemaName = bean.getCinemaName();
                OrderPayActivity.this.mCinemaPhone = bean.getCtel();
                OrderPayActivity.this.mUserBuyTicketPhone = bean.getMobile();

                // ?????????????????????
                if (OrderPayActivity.this.isnotVip) {
                    requestPayItems();
                } else {
                    requestPayItems();
                }

            }

            @Override
            public void onFail(final Exception e) {
                if (null != mDialog && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                OrderPayActivity.this.finish();
            }
        };

        // Order/eticketdetail.api?orderId={0}
        Map<String, String> param = new HashMap<>(1);
        param.put("orderId", orderId);
        HttpUtil.get(ConstantUrl.GET_ETICKET_DETAIL, param, ETicketDetailBean.class, orderDetailCallback);
    }

    /**
     * ??????????????????????????????????????????????????????????????? ?????? X???X?????????X???X???X??? ????????????????????????3D,??????,120??????
     */
    private String createTicketDateInfo(final long showDayLongTime, final String hall, final String versionDesc, final String language) {
        final StringBuilder dateInfo = new StringBuilder();
        dateInfo.append(getformatDate(showDayLongTime)).append(" ???").append(hall).append("??? ").append(" ").append(versionDesc).append(" ").append(language);

        return dateInfo.toString();
    }

    /**
     * ???????????????String???????????????XXXX?????? X???X?????????X??? X???X???
     */
    @SuppressLint("SimpleDateFormat")
    private String getformatDate(final long date) {
        // XXXX?????? X???X?????????X??? X???X???
        final SimpleDateFormat format = new SimpleDateFormat("M???d???(E)  HH:mm");
        final String dateInfo = format.format(date).replace("??????", "???");
        return dateInfo;
    }

    private void setUsedVoucherChecked() {
        if (hasUsedVoucherSuccessed) {
            if (ll_checkbox_container.getChildCount() > 0) {
                for (int i = 0; i < ll_checkbox_container.getChildCount(); i++) {
                    final CheckBox cb = (CheckBox) ll_checkbox_container.getChildAt(i);
                    if (Integer.valueOf(mCurrentCheckedVoucherId) == cb.getId()) {
                        cb.setChecked(true);
                    }
                    cb.setClickable(false);
                }
            }
            btn_activate.setClickable(false);
        } else if ((voucherViews.size() == 0) && hasCreateOrderFinished) {
            ll_checkbox_container.setVisibility(View.GONE);
            btn_activate.setClickable(false);
        }
    }

    /**
     * ?????????????????????????????????????????????
     */
    @SuppressLint("SimpleDateFormat")
    protected void doCreateOrder(final String orderId) {
        // http://api.m.mtime.cn/showtime/AutoCreateOrder.api ??????????????????, ???APP?????????????????????API

        if (isFromMall) {
            return;
        }

        if (!isFinishing()) {
            AlertDialog alertDlg = Utils.createDlg(OrderPayActivity.this, OrderPayActivity.this.getString(R.string.str_error),
                    OrderPayActivity.this.getString(R.string.str_load_error));
            alertDlg.show();
        }

        // TODO: 2019-09-19 ?????? wwl
//        final RequestCallback createOrderCallback = new RequestCallback() {
//            @Override
//            public void onSuccess(final Object o) {
//                if (null != mDialog && mDialog.isShowing()) {
//                    mDialog.dismiss();
//                }
//                CreateOrderJsonBean result = (CreateOrderJsonBean) o;
//
//                if ((null != result) && result.isSuccess()) {
//                    mReselectSeatNewOrderId = result.getOrderId();
//                    OrderPayActivity.this.mSubOrderId = result.getSubOrderId();
//                    OrderPayActivity.this.mPayEndTime = result.getPayEndTime();
//                    OrderPayActivity.this.pollSubOrderStatus(OrderPayActivity.this.mSubOrderId);
//                } else { // ??????????????????
//                    gotoPayFailedActivity(OrderPayFailedActivity.ERROR_TYPE_RESELECT_SEAT, "????????????", "????????????????????????????????????????????????\n????????????????????????????????????", "????????????");
//
//                }
//                result = null;
//            }
//
//            @Override
//            public void onFail(final Exception e) {
//                if (null != mDialog && mDialog.isShowing()) {
//                    mDialog.dismiss();
//                }
//
//                if (!OrderPayActivity.this.isFinishing()) {
//                    final AlertDialog alertDlg = Utils.createDlg(OrderPayActivity.this, OrderPayActivity.this.getString(R.string.str_error),
//                            OrderPayActivity.this.getString(R.string.str_load_error));
//                    alertDlg.show();
//                }
//            }
//        };
//        Map<String, String> parameterList = new ArrayMap<String, String>(1);
//        parameterList.put("orderId", orderId);
//        HttpUtil.post(ConstantUrl.AUTO_CREATE_ORDER, parameterList, CreateOrderJsonBean.class, createOrderCallback);
    }

    // TODO: 2019-09-19 ?????????????????????????????? wwl
    /**
     * ?????????????????????
     */
//    private void pollSubOrderStatus(final String subOrderId) {
//        // ????????????????????????????????????????????????
//        final RequestCallback subOrderStatusCallback = new RequestCallback() {
//
//            @Override
//            public void onSuccess(final Object o) {
//                if (o instanceof SubOrderStatusJsonBean) {
//                    final SubOrderStatusJsonBean subOrderStatusJsonBean = (SubOrderStatusJsonBean) o;
//                    subOrderStatus = subOrderStatusJsonBean.getSubOrderStatus();
//                    switch (subOrderStatus) {
//
//                        case 0: // 0-??????(??????????????????????????????)
//                            // ????????????????????????????????????????????????????????????????????????
//                            OrderPayActivity.this.pollSubOrderStatus(subOrderId);
//                            break;
//                        case 10: // 10-????????????(?????????????????????????????????)???
//                            OrderPayActivity.this.doReselectSeat(mReselectSeatNewOrderId, OrderPayActivity.this.mOrderId);
//                            break;
//                        case 20: // 20-????????????(?????????????????????????????????)
//                            gotoPayFailedActivity(OrderPayFailedActivity.ERROR_TYPE_RESELECT_SEAT, "????????????", "????????????????????????????????????????????????\n????????????????????????????????????", "????????????");
//                            break;
//                        default:
//                            gotoPayFailedActivity(OrderPayFailedActivity.ERROR_TYPE_RESELECT_SEAT, "????????????", "????????????????????????????????????????????????\n????????????????????????????????????", "????????????");
//                            break;
//
//                    }
//                }
//
//            }
//
//            @Override
//            public void onFail(final Exception e) {
//                final AlertDialog alertDlg = Utils.createDlg(OrderPayActivity.this, OrderPayActivity.this.getString(R.string.str_error),
//                        OrderPayActivity.this.getString(R.string.str_load_error));
//                alertDlg.show();
//            }
//        };
//
//        DispatchAsync.dispatchAsyncDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Map<String, String> parameterList = new ArrayMap<String, String>(1);
//                parameterList.put("subOrderId", subOrderId);
//                HttpUtil.post(ConstantUrl.GET_SUB_ORDER_STATUS, parameterList, SubOrderStatusJsonBean.class, subOrderStatusCallback);
//            }
//        }, OrderPayActivity.POLLING_SLEEP_TIME);
//
//    }

    // TODO: 2019-09-19 ?????????????????????????????? wwl
    /**
     * ???????????????????????????????????????
     */
//    private void doReselectSeat(final String newOrderId, final String reSelectSeatLastOrderId) {
//        final RequestCallback reselectSeatCallback = new RequestCallback() {
//            @Override
//            public void onSuccess(final Object o) {
//                if (null != mDialog && mDialog.isShowing()) {
//                    mDialog.dismiss();
//                }
//
//                final CommResultBean result = (CommResultBean) o;
//                if (result.isSuccess()) {
//                    // ???????????????
//                    OrderPayActivity.this.pollMainOrderStatus(newOrderId);
//                }
//            }
//
//            @Override
//            public void onFail(final Exception e) {
//                if (null != mDialog && mDialog.isShowing()) {
//                    mDialog.dismiss();
//                }
//
//                if (!OrderPayActivity.this.isFinishing()) {
//                    final AlertDialog alertDlg = Utils.createDlg(OrderPayActivity.this, OrderPayActivity.this.getString(R.string.str_error),
//                            OrderPayActivity.this.getString(R.string.str_load_error));
//                    alertDlg.show();
//                }
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
     * ??????????????????????????????
     */
    private void showActivateVoucherCodeDialog() {
        inputDlg = new InputDlg(OrderPayActivity.this, InputDlg.TYPE_OK_CANCEL, R.layout.dialog_activate_voucher_code, false);

        inputDlg.setBtnOKListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                final String code = inputDlg.getEditView().getText().toString();
                // ????????????-????????????
                if (TextUtil.stringIsNull(code)) {
                    MToastUtils.showShortToast("??????????????????");
                    return;
                }

                // ???????????????
                OrderPayActivity.this.doActivateVoucherCode(String.valueOf(OrderPayActivity.this.mOrderId), code, "", "");
                inputDlg.dismiss();
            }
        });
        inputDlg.setBtnCancelListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                inputDlg.dismiss();
            }
        });
        inputDlg.show();
    }

    // ???????????????
    private void doActivateVoucherCode(final String orderId, final String voucherCode, String vcode, String vcodeId) {
        showProgressDialog(OrderPayActivity.this, this.getString(R.string.ticketpayInfo));

        final RequestCallback getActivateCallback = new RequestCallback() {

            @Override
            public void onSuccess(final Object o) {
                if (null != progressDialog && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                final ActivateVoucherCodeResult result = (ActivateVoucherCodeResult) o;
                if (result.isSuccess()) {
                    if (capchaCouponDlg != null && capchaCouponDlg.isShowing()) {
                        capchaCouponDlg.dismiss();
                    }
                    // ?????????????????????
                    String msg = "????????????";
                    if (!result.isUsedOrder()) {
                        msg = "???????????????????????????????????????";
                    }
                    mCurrentActivatedVoucherId = result.getVocherId();
                    MToastUtils.showShortToast(msg);
                    topVoucherId = result.getVocherId();
                    // ???????????????????????????????????????????????????
                    OrderPayActivity.this.requestVoucherListData();

                } else {
                    // ?????????????????????
                    if (result.getStatus() == -4) {
                        requestCouponCapcha(voucherCode, result.getVcodeUrl(), result.getVcodeId());
                    } else {
                        MToastUtils.showShortToast("????????????," + result.getError());
                    }
                }
            }

            @Override
            public void onFail(final Exception e) {
                MToastUtils.showShortToast("?????????????????????");
                if (null != progressDialog && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        };
        Map<String, String> parameterList = new ArrayMap<String, String>(4);
        parameterList.put("orderId", orderId);
        parameterList.put("voucherCode", String.valueOf(voucherCode));
        parameterList.put("vcode", vcode);
        parameterList.put("vcodeId", vcodeId);
        HttpUtil.post(ConstantUrl.GET_ACTIVATEVUCHERCODE, parameterList, ActivateVoucherCodeResult.class, getActivateCallback);
    }

    public void requestWithLogin(String url) {
        Map<String, String> parameterList = new ArrayMap<String, String>(1);
        parameterList.put("url", url);
        HttpUtil.post(ConstantUrl.GET_COUPON_URL_WITH_LOGIN, parameterList, SuccessBean.class, new RequestCallback() {
            public void onSuccess(Object o) {
                SuccessBean bean = (SuccessBean) o;
                if (bean.getSuccess() != null) {
                    if (bean.getSuccess().equalsIgnoreCase("true")) {
                        if (UserManager.Companion.getInstance().isLogin()) {
                            setVoucherViewCheck();
//                            cbox_balance.setChecked(false);
                            String url = bean.getNewUrl();
                            Intent intent = new Intent();
                            intent.putExtra(App.getInstance().WAP_PAY_URL, url);
                            intent.putExtra(App.getInstance().MOVIE_CARD_PAY, true);
                            startActivityForResult(WapPayActivity.class, intent, 2);
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
    protected void weixinPay(final String formXML) {
        WeixinPayBean bean = new Gson().fromJson(formXML, WeixinPayBean.class);
        PayReq req = new PayReq();
        if (bean != null) {
            req.appId = bean.getAppid();
            req.partnerId = bean.getPartnerid();
            req.prepayId = bean.getPrepayid();
            req.nonceStr = bean.getNoncestr();
            req.timeStamp = bean.getTimestamp();
            try {
                JSONObject json = new JSONObject(formXML);
                if (json != null && !"".equals(json)) {
                    req.packageValue = json.getString("package");
                } else {
                    req.packageValue = "Sign=WXPay";
                }
            } catch (JSONException e) {
                e.printStackTrace();
                req.packageValue = "Sign=WXPay";
            }
            req.sign = bean.getSign();
        }
        // req.extData = "app data"; // optional
        api.sendReq(req);
    }

    @Override
    public void onReq(BaseReq arg0) {
    }

    @Override
    public void onResp(BaseResp resp) {
    }

    /**
     * ****************************************** ?????????????????? **********************************************************
     */

    // ????????????
    protected void childDoBlendPay(final String orderId, final String vcode, final String voucherIdList, final String balancePayAmount,
                                   String rechargePayAmount, final String payType, final String cardId, final String useNum, final String token, final String bankId) {

        RequestCallback blendPayCallback = new RequestCallback() {

            @Override
            public void onSuccess(Object o) {
                final BlendPayBean result = (BlendPayBean) o;
                final int payStatus = result.getStatus();

                if (result.isSuccess()) { // ????????????
                    if (securitydlg != null && securitydlg.isShowing()) {
                        securitydlg.dismiss();
                    }
                    if (null != mpayDialog && mpayDialog.isShowing()) {
                        mpayDialog.dismiss();
                    }
                    setVoucherViewCheck();
//                    cbox_balance.setChecked(false);
                    // TODO: 2018/5/2 weiwenli ??????/???????????????
                    final String formXML = result.getFormXML(); // ????????????
                    if (payType.equals(String.valueOf(App.getInstance().PAYTYPE_YINLIAN))) { // ????????????
                        if (!TextUtils.isEmpty(upompSeType)
                                && (upompSeType.equals(UPOMP_SE_TYPE_SAMSUNG) || upompSeType.equals(UPOMP_SE_TYPE_HUAWEI))) {
                            // ??????????????????_??????pay??????
                            MtimeUtils.doSEPay(OrderPayActivity.this, formXML, upompSeType);
                        } else {
                            // ??????????????????_
                            MtimeUtils.doPay(OrderPayActivity.this, formXML);
                        }
                    } else if (payType.equals(String.valueOf(App.getInstance().PAYTYPE_ALIPAY))) {
                        OrderPayActivity.this.alipay(formXML);
                    } else if (payType.equals(String.valueOf(App.getInstance().PAYTYPE_ALIPAY_WAP))) {
                        alipayWap(formXML);
                    } else if (payType.equals(String.valueOf(App.getInstance().PAYTYPE_WEIXIN))) {
                        weixinPay(formXML);
                    } else {
                        // ?????????????????????
                        pollMainOrderStatus(orderId);
                    }

                } else {
                    if (null != mpayDialog && mpayDialog.isShowing()) {
                        mpayDialog.dismiss();
                    }
                    switch (payStatus) {
                        case 1:
                            if (securitydlg != null && securitydlg.isShowing()) {
                                securitydlg.dismiss();
                                setVoucherViewCheck();
//                                cbox_balance.setChecked(false);
                            }
                            pollMainOrderStatus(orderId);
                            break;
                        case -4:
                            if (securitydlg != null && securitydlg.isShowing()) {
                                securitydlg.dismiss();
                                setVoucherViewCheck();
//                                cbox_balance.setChecked(false);
                            }
                            createOrderFail("????????????????????????????????????????????????????????????");
                            break;
                        case -3:
                            if (securitydlg != null && securitydlg.isShowing()) {
                                securitydlg.dismiss();
                                setVoucherViewCheck();
//                                cbox_balance.setChecked(false);
                            }
                            showPayErrorDlg(OrderPayActivity.this.getString(R.string.loginExprie));
                            break;
                        case -2:
                            if (securitydlg != null && securitydlg.isShowing()) {
                                securitydlg.dismiss();
                                setVoucherViewCheck();
//                                cbox_balance.setChecked(false);
                            }
//                            cbox_balance.setChecked(false);
                            MToastUtils.showShortToast(result.getMsg());
                            break;
                        case -1:
                            if (securitydlg != null && securitydlg.isShowing()) {
                                securitydlg.dismiss();
                                setVoucherViewCheck();
//                                cbox_balance.setChecked(false);
                            }
                            // payError("????????????????????????????????????????????????");
                            mCustomDlg = new CustomAlertDlg(OrderPayActivity.this, CustomAlertDlg.TYPE_OK);
                            mCustomDlg.show();
                            mCustomDlg.setCancelable(false);
                            mCustomDlg.setText("??????????????????????????????????????????");
                            mCustomDlg.getBtnOK().setText("????????????????????????");
                            mCustomDlg.setBtnOKListener(new OnClickListener() {

                                @Override
                                public void onClick(final View v) {
                                    requestPayItems();
                                    showTimer = false;
//                                    cbox_balance.setChecked(false);
                                    mCustomDlg.dismiss();
                                }
                            });
                            break;
                        case -5:
                            MToastUtils.showShortToast(result.getMsg());
                            break;
                        default:
                            if (securitydlg != null && securitydlg.isShowing()) {
                                securitydlg.dismiss();
                                setVoucherViewCheck();
                                cbox_balance.setChecked(false);
                                requestPayItems();
                            }
                            String msg = result.getMsg();
                            if (msg == null) {
                                msg = "????????????";
                            }
                            showPayErrorDlg(msg);
                    }

                }

            }

            @Override
            public void onFail(Exception e) {
                if (securitydlg != null && securitydlg.isShowing()) {
                    securitydlg.dismiss();
                }
                if (null != mpayDialog && mpayDialog.isShowing()) {
                    mpayDialog.dismiss();
                }
                MToastUtils.showShortToast("??????????????????????????????");
            }
        };
        Map<String, String> parameterList = new ArrayMap<String, String>(13);
        parameterList.put("orderId", orderId);
        parameterList.put("vcode", vcode);
        parameterList.put("voucherIdList", voucherIdList);
        parameterList.put("balancePayAmount", "0"); // balancePayAmount
        parameterList.put("rechargePayAmount", rechargePayAmount);
        parameterList.put("payType", payType);
        parameterList.put("cardId", cardId);
        parameterList.put("useNum", useNum);
        parameterList.put("token", token);
        parameterList.put("returnURL", "");
        parameterList.put("bankId", bankId);
        parameterList.put("mobile", payBindphone);
        if (activityIds != null && !"".equals(activityIds.trim()) && !"-1".equals(activityIds.trim())) {
            parameterList.put("activityIds", activityIds);
        }
        HttpUtil.post(ConstantUrl.BLEND_PAY, parameterList, BlendPayBean.class, blendPayCallback);

    }

    /**
     * ?????????????????????
     */
    protected void pollMainOrderStatus(final String orderId) {
        // ??????????????????
        showTicketOutingDialog();

        final RequestCallback getMainOrderStatusCallback = new RequestCallback() {
            @Override
            public void onSuccess(final Object o) {
                final OrderStatusJsonBean order = (OrderStatusJsonBean) o;
                final int orderStatus = order.getOrderStatus();
                if (orderStatus != 10) {
                    if (null != mDialog && mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                    // ??????????????????????????????
                    if (mPoolingMainOrderTimer != null) {
                        mPoolingMainOrderTimer.cancel();
                    }
                }
                switch (orderStatus) {
                    case 10: // 10-????????????(?????????????????????????????????)???
                        pollMainOrderStatus(orderId); // ???????????????????????????
                        break;

                    case 30: // 30-??????(???????????????????????????????????????????????????)

                        if (ticketOutingDialog != null && ticketOutingDialog.isShowing()) {
                            try {
                                ticketOutingDialog.dismiss();
                            } catch (Exception e) {

                            }
                        }
                        clockFlag = false;
                        OrderPayActivity.this.gotoPaySuccessActivity(OrderPayActivity.this, OrderPayActivity.this.mCinemaName,
                                OrderPayActivity.this.mMovieName, count, OrderPayActivity.this.mUnitPrice, OrderPayActivity.this.mTotalPrice,
                                OrderPayActivity.this.getString(R.string.payOrder), OrderPayActivity.this.mSeatSelectedInfo, OrderPayActivity.this.mIsEticket);
                        OrderPayActivity.this.finish();
                        break;

                    case 40: // 40-??????(?????????????????????????????????????????????)
                        if (ticketOutingDialog != null && ticketOutingDialog.isShowing()) {
                            ticketOutingDialog.dismiss();
                        }

                        if (OrderPayActivity.this.mIsEticket) {
                            gotoPayFailedActivity(OrderPayFailedActivity.ERROR_TYPE_PAY_TIME_OUT, "????????????", "????????????????????????\n????????????????????????", "");
                        } else if (order.isReSelectSeat()) {
                            // ????????????????????????
                            if (isFirst) {
                                isFirst = !isFirst;
                                doCreateOrder(orderId);
                            } else {
                                mPrefs.putLong(App.getInstance().KEY_SERVICE_DATE, MTimeUtils.getLastDiffServerTime());// ????????????
                                gotoPayFailedActivity(OrderPayFailedActivity.ERROR_TYPE_RESELECT_SEAT, "????????????", "????????????????????????????????????????????????\n????????????????????????????????????", "????????????");
                            }
                        } else {
                            // ????????????????????????
                            // ????????????: 0???????????????1????????????
                            if (order.getPayStatus() == 1) {

                                gotoPayFailedActivity(OrderPayFailedActivity.ERROR_TYPE_GO_HOMEPAGE, "????????????", "????????????????????????????????????????????????", "????????????");
                            } else {
                                gotoPayFailedActivity(OrderPayFailedActivity.ERROR_TYPE_RESELECT_PAY, "????????????", "", "???????????????????????????");
                            }

                        }
                        break;

                    case 100: // 100-?????????(??????????????????????????????)
                        showPayErrorDlg(OrderPayActivity.this.getString(R.string.payOrderCancel));
                        break;

                    default:
                        if (!OrderPayActivity.this.mIsEticket && order.isReSelectSeat()) {
                            if (isFirst) {
                                isFirst = !isFirst;
                                doCreateOrder(orderId);
                            } else {
                                // ????????????????????????

                                mPrefs.putLong(App.getInstance().KEY_SERVICE_DATE, MTimeUtils.getLastDiffServerTime());// ????????????
                                gotoPayFailedActivity(OrderPayFailedActivity.ERROR_TYPE_RESELECT_SEAT, "????????????", "????????????????????????????????????????????????\n????????????????????????????????????", "????????????");
                                finish();
                            }
                        } else {
                            if (order.getPayStatus() == 1) {
                                gotoPayFailedActivity(OrderPayFailedActivity.ERROR_TYPE_GO_HOMEPAGE, "????????????", "????????????????????????????????????????????????", "????????????");
                            } else {
                                gotoPayFailedActivity(OrderPayFailedActivity.ERROR_TYPE_RESELECT_PAY, "????????????", "", "???????????????????????????");
                                finish();
                            }

                        }
                }
            }

            @Override
            public void onFail(final Exception e) {
                if (null != mDialog && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                pollMainOrderStatus(orderId); // ???????????????????????????
            }
        };

        if (mPoolingCounter > OrderPayActivity.POLLING_MAX_TIME) {
            // ??????????????????????????????????????????
            if (null != mDialog && mDialog.isShowing()) {
                mDialog.dismiss();
            }

            mPrefs.putLong(App.getInstance().KEY_SERVICE_DATE, MTimeUtils.getLastDiffServerTime());// ????????????
            mCustomDlg = new CustomAlertDlg(OrderPayActivity.this, CustomAlertDlg.TYPE_OK);
            mCustomDlg.setBtnOKListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    mCustomDlg.dismiss();
                    // ????????????
                    final Intent intent = new Intent();
                    intent.putExtra(App.getInstance().KEY_SEATING_SELECT_AGAIN, true);
                    intent.putExtra(App.getInstance().KEY_SEATING_LAST_ORDER_ID, OrderPayActivity.this.mOrderId);
                    intent.putExtra(App.getInstance().KEY_SEATING_DID, OrderPayActivity.this.mDId);

//                    OrderPayActivity.this.startActivity(SeatSelectActivity.class, intent);

                    JavaOpenSeatActivity.INSTANCE.openSeatActivity(mDId, mOrderId, null, null, null);

                    finish();
                }
            });
            if (!isFinishing()) {
                mCustomDlg.show();
                mCustomDlg.setText("????????????????????????????????????");
                mCustomDlg.getBtnOK().setText("????????????");
                mCustomDlg.setCancelable(false);
            }
            return;
        }

        DispatchAsync.dispatchAsyncDelayed(new Runnable() {
            @Override
            public void run() {
                mPoolingCounter++;
                Map<String, String> parameterList = new ArrayMap<String, String>(1);
                parameterList.put("orderId", mOrderId);
                HttpUtil.get(ConstantUrl.GET_ORDER_STATUS, parameterList, OrderStatusJsonBean.class, getMainOrderStatusCallback);
            }
        }, OrderPayActivity.POLLING_SLEEP_TIME);

    }

//    protected void childPayReturnFailedComplete(int status, BaseResultJsonBean payResult) {
//
//        switch (status) {
//            case -1: // -1, ?????????????????????????????????
//                if (null != progressDialog && progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
//                showPayErrorDlg(payResult.getMsg());
//                break;
//            case -2: // -2, ???????????????????????????????????????;
//                if (null != progressDialog && progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
//                payMoneySuccess(OrderPayActivity.this, OrderPayActivity.this.mCinemaName, OrderPayActivity.this.mMovieName, count,
//                        OrderPayActivity.this.mUnitPrice, OrderPayActivity.this.mTotalPrice, OrderPayActivity.this.getString(R.string.pay_error_admin));
//                finish();
//                break;
//            case 0:
//                // 0?????????????????????????????????????????????
//                getOrderStatue(NORMAL_ORDER_TYPE);
//                break;
//            default: // 0???????????????????????????,???0???????????????
//                if (null != progressDialog && progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
//                showPayErrorDlg(OrderPayActivity.this.getString(R.string.pay_error));
//        }
//
//    }

    @Override
    protected void orderExpire(final Context context) {
        subContext = context;
        if (payLongTimeRemaining > 0L) {
            if (timerCountDown != null) {
                timerCountDown.cancel();
            }
            timerCountDown = new TimerCountDown(payLongTimeRemaining) {
                @Override
                public void onTimeFinish() {
                    isTimeFinish = true;
                    if (clockFlag) {
                        endTime = true;
                        timeEndGetOrderStatue();
                        navigationbar.setTimerText("00:00");
                        navigationbar.setTimerTextColor(Color.RED);
                    }
                }

                @Override
                public void onTickCallBack(final String value, final String min, final String sec) {
                    navigationbar.setTimerText(value);
                }

                @Override
                public void onTickCallBackTo(final String value, final String min, final String sec, final boolean colorFlag) {
                    navigationbar.setTimerText(value);
                    if (colorFlag) {
                        navigationbar.setTimerTextColor(Color.RED);
                    } else {
                        navigationbar.setTimerTextColor(Color.WHITE);
                    }

                }
            };
            timerCountDown.start();
        } else {
            payOrderExpire(subContext);
        }
    }

    /**
     * ??????-???????????????????????????????????????????????????
     */
    protected void childRefreshViewValues() {
        if (isRecharge && mUserBalance > 0) {
            tv_available_balance.setText("??????????????????" + MtimeUtils.formatPrice(mUserBalance));
//            if (cbox_balance.isChecked()) {
//                mBalanceReducePrice = mUserBalance < mTotalPrice ? mUserBalance : mTotalPrice;
//            } else {
//                mBalanceReducePrice = 0;
//            }
            mBalanceReducePrice = 0;
//            tv_reduce_price_balance.setVisibility(View.VISIBLE);
//            tv_reduce_price_balance.setText("-???" + MtimeUtils.formatPrice(mBalanceReducePrice));
            return;
        }
        // ??????
        tv_total_price.setText(MtimeUtils.formatPrice(mTotalPrice));
        // ?????????
        if (mServiceFee > 0) {
            tv_service_fee.setText("??????????????????" + MtimeUtils.formatPrice(mServiceFee) + "/??????");
        } else {
            tv_service_fee.setVisibility(View.GONE);
        }

        if (!mIsEticket) { // ???
            if (showTimer) {
                orderExpire(this);
            }
            clockFlag = true;
            navigationbar.setTimerViewVisibility(View.VISIBLE);
        } else {
            // ??????????????????????????????????????????
            navigationbar.setTimerViewVisibility(View.GONE);
        }

        // ????????????????????????
        if (mUserBalance > 0) {
            tv_available_balance.setText("??????????????????" + MtimeUtils.formatPrice(mUserBalance));
//            if (cbox_balance.isChecked()) {
//                mBalanceReducePrice = mUserBalance < mNeedPayPrice.doubleValue() ? mUserBalance : mNeedPayPrice.doubleValue();
//            } else {
//                mBalanceReducePrice = 0;
//            }
            mBalanceReducePrice = 0;
//            tv_reduce_price_balance.setVisibility(View.VISIBLE);
//            tv_reduce_price_balance.setText("-???" + MtimeUtils.formatPrice(mBalanceReducePrice));

        } else {
            // ?????????0???,????????????????????????????????????????????????????????????0?????????????????????????????????????????????
//            cbox_balance.setChecked(false);
//            cbox_balance.setClickable(false);
            tv_available_balance.setText("??????????????????" + 0);
//            tv_reduce_price_balance.setVisibility(View.GONE);
        }

        showVoucherView();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (null != giveupBean) {
                // ??????????????????????????????
                showCollectionView();
                return true;
            }
            if (null != this.collectionView && collectionView.isShowing()) {
                collectionView.showView(false);

                View navBar = this.findViewById(R.id.navigationbar);
                navBar.setVisibility(View.VISIBLE);

                View scroll_root_view = this.findViewById(R.id.scroll_root_view);
                scroll_root_view.setVisibility(View.VISIBLE);
                collectionView = null;
                return true;
            }

            if (!mIsEticket) {
                if (!isnotVip) {
                    //???????????????????????????????????????????????????
                    if (isFromMall) {
                        finish();
                    } else {
                        showCancelOrderDialog();
                    }
                    return true;
                } else {
                    finish();
                    return true;
                }
            }
        }

        return super.onKeyUp(keyCode, event);
    }

    // ??????????????????????????????
    private void showCollectionView() {
        View navBar = this.findViewById(R.id.navigationbar);
        navBar.setVisibility(View.GONE);

        View scroll_root_view = this.findViewById(R.id.scroll_root_view);
        scroll_root_view.setVisibility(View.GONE);

        View root = this.findViewById(R.id.guide_view);
        List<String> datas = this.giveupBean.getList();
//        datas.add("????????????");
        this.collectionView = new GiveupPayCollectionView(this, root, BaiduConstants.BAIDU_EVENTID_GIVEUP_BUY_TICKETS, this.giveupBean.getTitle(), datas, new Runnable() {
            @Override
            public void run() {
                View navBar = findViewById(R.id.navigationbar);
                navBar.setVisibility(View.VISIBLE);

                View scroll_root_view = findViewById(R.id.scroll_root_view);
                scroll_root_view.setVisibility(View.VISIBLE);
                collectionView = null;
            }
        });
        collectionView.showView(true);
        collectionView.setListener(new GiveupPayCollectionView.IGiveupPayCollectionListener() {
            @Override
            public void onItemClick() {
                if (!mIsEticket && !isnotVip && !isFromMall) {
                    cancelOrder();
                } else {
                    finish();
                }
            }
        });
        giveupBean = null;
    }

}