package com.mtime.bussiness.ticket.movie.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.collection.ArrayMap;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.alibaba.android.arouter.facade.annotation.Route;
import com.kotlin.android.app.router.ext.AppLinkExtKt;
import com.kotlin.android.app.router.path.RouterActivityPath;
import com.kotlin.android.film.JavaOpenSeatActivity;
import com.kotlin.android.user.UserManager;
import com.mtime.R;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.utils.DispatchAsync;
import com.mtime.base.utils.MToastUtils;
import com.mtime.beans.SuccessBean;
import com.mtime.bussiness.ticket.api.TicketApi;
import com.mtime.bussiness.ticket.cinema.bean.RefoundTicketMsgBean;
import com.mtime.bussiness.ticket.movie.bean.CancelOrderJsonBean;
import com.mtime.bussiness.ticket.movie.bean.CapchaMainBean;
import com.mtime.bussiness.ticket.movie.bean.CommodityList;
import com.mtime.bussiness.ticket.movie.bean.CreateOrderJsonBean;
import com.mtime.bussiness.ticket.movie.bean.FailCommodityIdBean;
import com.mtime.bussiness.ticket.movie.bean.GetPayListBean;
import com.mtime.bussiness.ticket.movie.bean.GiveupPayReasonBean;
import com.mtime.bussiness.ticket.movie.bean.PayCardListBean;
import com.mtime.bussiness.ticket.movie.bean.SubOrderStatusJsonBean;
import com.mtime.common.utils.PrefsManager;
import com.mtime.common.utils.TextUtil;
import com.mtime.common.utils.Utils;
import com.mtime.frame.App;
import com.mtime.frame.BaseActivity;
import com.mtime.network.ConstantUrl;
import com.mtime.network.RequestCallback;
import com.mtime.statistic.baidu.BaiduConstants;
import com.mtime.util.CustomAlertDlg;
import com.mtime.util.HttpUtil;
import com.mtime.util.JumpUtil;
import com.mtime.util.MtimeUtils;
import com.mtime.util.ToolsUtils;
import com.mtime.util.UIUtil;
import com.mtime.widgets.BaseTitleView;
import com.mtime.widgets.BaseTitleView.StructType;
import com.mtime.widgets.GiveupPayCollectionView;
import com.mtime.widgets.TitleOfNormalView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * ????????????
 */
@Route(path = RouterActivityPath.Main.PAGER_ORDER_CONFIRM_ACTIVITY)
public class OrderConfirmActivity extends BaseActivity implements OnClickListener {

    public static final String EXPLAINS = "explains";

    private CustomAlertDlg mLockingSeatDlg;
    private String mCinemaName;
    private String mCinemaPhone;
    private String mUserBuyTicketPhone;
    private String mSeatSelectedInfo;
    private String mTicketDateInfo;
    private double mTotalPrice;
    private double mServiceFee;
    private String mMovieName;
    private String mSeatId;
    private int mSelectedSeatCount;
    private String mOrderId;
    private String mSubOrderId;
    private long mPayEndTime;                                                // ??????????????????
    // ??????4????????????????????????????????????????????????????????????????????????
    private String mDId;
    private String mCinemaId;
    private String mDate;
    private String mMovieId;
    // ???????????????????????????????????????????????????+???????????????+???????????????????????????????????????
    private boolean mHasUserCanceledCreateOrder = false;

    private TextView tv_total_price;
    private TextView tv_service_fee;
    private TextView tv_movie_name;
    private TextView tv_cinema_name;
    private TextView tv_date;
    private TextView tv_seat_info;
    private EditText edt_phone;
    private TextView btn_next_step, btn_mtime_step;

    private Timer mPoolingSuborderTimer;                                      // ????????????????????????????????????
    private static final int MAX_POLLING_SUBORDER_LONG_TIME = 3 * 60 * 1000;             // ???????????????????????????(3??????)
    // private static final int MAX_POLLING_SUBORDER_LONG_TIME = 1 * 1000;
    // ??????(lidesheng)????????????500ms 2020-12-22 by wwl
    private static final int POLLING_SLEEP_TIME = 500;                        // ????????????????????????????????????????????????
    // //???????????????????????????(3??????)
    private int subOrderStatus;
    // ???????????????????????????????????????????????????+???????????????+???????????????????????????????????????
    private boolean mHasPollingTimeout = false;                     // ??????????????????
    private boolean hasOrderCreateSuccess = false;                     // ???????????????????????????3????????????????????????????????????

    private PrefsManager mPrefs;
    // ??????????????????????????????
    private static final String KEY_MOBILE_LAST_TIME_INPUTED = "mobile_last_time_inputed";
    private String mAutoFillMobileNumber;
    private boolean isnotVip;                                                   // ?????????????????????
    private LinearLayout notVipLayout;
    private TextView notVipPhone;
    private LinearLayout vipLayout;
    private String notVipphoneNum;
    private LinearLayout capchaLin;                                                  // ???????????????
    private EditText capchaText;
    private ImageView capchaImg;
    private RequestCallback capchaCallback;
    //    private String                   mWithoutPayOrderId;                                         // ????????????????????????id
    private String vcodeId, vcode;
    private boolean isVaild = false;
    private ArrayList<CommodityList> smallPayHasCountList;
    private String smallPayDesc = "";
    private TextView smallPayInfoText;
    private String smallPayInfo = "";
    private LinearLayout smallPayInfoLin;
    private boolean isCreatOrder = false;
    private boolean isMembershipCard = false;                     // ???????????????????????????
    private boolean isMtimeCard = false;                     // ???????????????????????????
    private String mtimeLoginUrl = null;

    private String mTimeInfo;
    private String mHallnameInfo;
    private String mVersiondescInfo;
    private String mLanguageInfo;
    private ArrayList<String> orderExplains;
    private LinearLayout explainsView;
    private TextView refoundMsgTv;

    protected GiveupPayReasonBean giveupBean;
    protected GiveupPayCollectionView collectionView;
    private boolean isFromSelectActivity;
    private TicketApi mApi;

    @SuppressWarnings("unchecked")
    @Override
    protected void onInitVariable() {


        final Intent intent = getIntent();
        mTotalPrice = intent.getDoubleExtra(App.getInstance().KEY_SEATING_TOTAL_PRICE, 0);
        mServiceFee = intent.getDoubleExtra(App.getInstance().KEY_SEATING_SERVICE_FEE, 0);
        mMovieName = intent.getStringExtra(App.getInstance().KEY_MOVIE_NAME);
        mCinemaName = intent.getStringExtra(App.getInstance().KEY_CINEMA_NAME);
        mCinemaPhone = intent.getStringExtra(App.getInstance().KEY_CINEMA_PHONE);
        mUserBuyTicketPhone = intent.getStringExtra(App.getInstance().KEY_USER_BUY_TICKET_PHONE);
        notVipphoneNum = intent.getStringExtra(App.getInstance().KEY_TARGET_NOT_VIP_PHONE);
        mSeatId = intent.getStringExtra(App.getInstance().KEY_SEATING_SEAT_ID);
        mSelectedSeatCount = intent.getIntExtra(App.getInstance().KEY_SEATING_SELECTED_SEAT_COUNT, 0);
        mOrderId = intent.getStringExtra(App.getInstance().KEY_SEATING_ORDER_ID);
        mSubOrderId = intent.getStringExtra(App.getInstance().KEY_SEATING_SUBORDER_ID);
        mSeatSelectedInfo = intent.getStringExtra(App.getInstance().KEY_SEAT_SELECTED_INFO);
        mTicketDateInfo = intent.getStringExtra(App.getInstance().KEY_TICKET_DATE_INFO);
        isFromSelectActivity = intent.getBooleanExtra(App.getInstance().IS_FROM_SEATSELECT_ACTIVITY, false);
        // ??????4?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        mDId = intent.getStringExtra(App.getInstance().KEY_SEATING_DID);
        mMovieId = intent.getStringExtra(App.getInstance().KEY_MOVIE_ID);
        mCinemaId = intent.getStringExtra(App.getInstance().KEY_CINEMA_ID);
        mDate = intent.getStringExtra(App.getInstance().KEY_SHOWTIME_DATE);
        //
        isnotVip = intent.getBooleanExtra(App.getInstance().KEY_TARGET_NOT_VIP, false);
        isMembershipCard = intent.getBooleanExtra(App.getInstance().KEY_ISMEMBERSHIPCARD, false);

        mTimeInfo = intent.getStringExtra(App.getInstance().KEY_TICKET_TIME_INFO);
        mHallnameInfo = intent.getStringExtra(App.getInstance().KEY_TICKET_HALLNAME_INFO);
        mVersiondescInfo = intent.getStringExtra(App.getInstance().KEY_TICKET_VERSIONDESC_INFO);
        mLanguageInfo = intent.getStringExtra(App.getInstance().KEY_TICKET_LANGUAGE_INFO);

        orderExplains = intent.getStringArrayListExtra(OrderConfirmActivity.EXPLAINS);
        /**
         * 1,??????????????????????????????????????????????????? 2,??????????????????????????????????????? 3,????????????????????????????????????????????????
         * 4,?????????????????????????????????????????????
         */
        mPrefs = App.getInstance().getPrefsManager();
        if (isnotVip) {

        } else {
            if ((mAutoFillMobileNumber == null) || "".equals(mAutoFillMobileNumber)) {
                if (UserManager.Companion.getInstance().getUser() != null) {
                    mAutoFillMobileNumber = UserManager.Companion.getInstance().getBindMobile();
                }
            }
            if ((mAutoFillMobileNumber == null) || "".equals(mAutoFillMobileNumber)) {
                mAutoFillMobileNumber = mPrefs.getString(OrderConfirmActivity.KEY_MOBILE_LAST_TIME_INPUTED);
            }
        }
        smallPayHasCountList = (ArrayList<CommodityList>) intent.getSerializableExtra(App.getInstance().HAS_COUNT_LIST);
        if (smallPayHasCountList != null && smallPayHasCountList.size() > 0) {
            for (int i = 0; i < smallPayHasCountList.size(); i++) {
                if (i == smallPayHasCountList.size() - 1) {
                    smallPayDesc += smallPayHasCountList.get(i).getCommodityId() + ","
                            + smallPayHasCountList.get(i).getCount();
                    smallPayInfo += smallPayHasCountList.get(i).getShortName() + smallPayHasCountList.get(i).getCount()
                            + "???" + "???" + smallPayHasCountList.get(i).getDesc() + "???";
                } else {
                    smallPayDesc += smallPayHasCountList.get(i).getCommodityId() + ","
                            + smallPayHasCountList.get(i).getCount() + "|";
                    smallPayInfo += smallPayHasCountList.get(i).getShortName() + smallPayHasCountList.get(i).getCount()
                            + "???" + "???" + smallPayHasCountList.get(i).getDesc() + "???" + "/";
                }
                mTotalPrice += smallPayHasCountList.get(i).getPrice() * smallPayHasCountList.get(i).getCount();
            }
            if (smallPayInfo.contains("\n")) {
                smallPayInfo = smallPayInfo.replace("\n", "");
            }

        }

        setPageLabel("orderConfirm");
    }

    @Override
    protected void onInitView(final Bundle savedInstanceState) {
        this.setContentView(R.layout.act_order_confirm);
        View navBar = findViewById(R.id.navigationbar);
        TitleOfNormalView title = new TitleOfNormalView(this, navBar, StructType.TYPE_NORMAL_SHOW_BACK_TITLE, getResources().getString(
                R.string.confrim_order), new BaseTitleView.ITitleViewLActListener() {
            @Override
            public void onEvent(BaseTitleView.ActionType type, String content) {
                if (type == BaseTitleView.ActionType.TYPE_BACK) {
                    if (null != giveupBean) {
                        showCollectionView();
                        return;
                    }

                    finish();
                }
            }
        });
        title.setCloseParent(false);

        tv_total_price = findViewById(R.id.confirm_oder_tv_total_price);
        tv_service_fee = findViewById(R.id.confirm_oder_tv_service_fee);
        edt_phone = findViewById(R.id.confirm_oder_edt_phone);
        btn_next_step = findViewById(R.id.confirm_oder_btn_next_step);
        btn_mtime_step = findViewById(R.id.confirm_oder_btn_mtime_card);
        notVipLayout = findViewById(R.id.notvip_show_lin);
        vipLayout = findViewById(R.id.vip_show_phone_lin);
        notVipPhone = findViewById(R.id.notvip_telephone);
        capchaLin = findViewById(R.id.capcha_lin);
        capchaText = findViewById(R.id.capcha_edit);
        capchaImg = findViewById(R.id.capcha_img);
        smallPayInfoText = findViewById(R.id.small_pay_info);
        smallPayInfoLin = findViewById(R.id.small_pay_info_lin);
        tv_movie_name = findViewById(R.id.confirm_oder_tv_movie_name);
        tv_cinema_name = findViewById(R.id.confirm_oder_tv_cinema_name);
        tv_date = findViewById(R.id.confirm_oder_tv_date);
        tv_seat_info = findViewById(R.id.confirm_oder_tv_seat_info);
        explainsView = findViewById(R.id.explains_view);
        refoundMsgTv = findViewById(R.id.confirm_oder_edt_refound_tv);
        initViewValues();
        initExplainsView();
    }

    private void initViewValues() {

        tv_total_price.setText(getResources().getString(R.string.str_money) + MtimeUtils.formatPrice(MtimeUtils.moneyY2Str(mTotalPrice)));

        // ???????????????
        // mServiceFee = ?????????
        if (smallPayHasCountList != null && smallPayHasCountList.size() > 0) {
            btn_next_step.setText("?????????");
            btn_mtime_step.setVisibility(View.GONE);
        } else {
            if (isMembershipCard) {
                btn_next_step.setText("??????????????????/?????????/???????????????");
                btn_mtime_step.setVisibility(View.VISIBLE);
            } else {
                btn_next_step.setText("?????????");
                btn_mtime_step.setVisibility(View.GONE);
            }
        }

        if (mServiceFee > 0) {
            tv_service_fee.setText("?????????????????? " + MtimeUtils.formatPrice(mServiceFee) + "/??????");
        } else {
            tv_service_fee.setVisibility(View.GONE);
        }

        if (mMovieName != null) {
            StringBuffer movieBuffer = new StringBuffer();
            movieBuffer.append(mMovieName);
            if (mVersiondescInfo != null) {
                movieBuffer.append(", ").append(mVersiondescInfo);
                if (mLanguageInfo != null) {
                    movieBuffer.append("/").append(mLanguageInfo);
                }
            } else if (mLanguageInfo != null) {
                movieBuffer.append(", ").append(mLanguageInfo);
            }
            tv_movie_name.setText(movieBuffer);
        }

        if (mCinemaName != null) {
            StringBuffer cinemaBuffer = new StringBuffer();
            cinemaBuffer.append(mCinemaName);
            if (mHallnameInfo != null) {
                cinemaBuffer.append(", ").append(mHallnameInfo);
            }
            tv_cinema_name.setText(cinemaBuffer);
        }
        if (mTimeInfo != null) {
            tv_date.setText(mTimeInfo);
        }

        if (mSeatSelectedInfo != null) {
            tv_seat_info.setText(mSeatSelectedInfo);
        }
        if (smallPayInfo != null && smallPayInfo.length() > 0) {
            smallPayInfoLin.setVisibility(View.VISIBLE);
            smallPayInfoText.setText(smallPayInfo);

        } else {
            smallPayInfoLin.setVisibility(View.GONE);
        }
        if (isnotVip) {
            notVipLayout.setVisibility(View.VISIBLE);
            vipLayout.setVisibility(View.GONE);
            final String phone = TextUtil.splitTelString(notVipphoneNum);
            notVipPhone.setText(phone);
        } else {
            notVipLayout.setVisibility(View.GONE);
            vipLayout.setVisibility(View.VISIBLE);
            // ???????????????
            if ((mAutoFillMobileNumber != null) && !"".equals(mAutoFillMobileNumber)) {
                edt_phone.setText(mAutoFillMobileNumber);
            }
        }

    }

    @Override
    protected void onInitEvent() {
        btn_next_step.setOnClickListener(this);
        btn_mtime_step.setOnClickListener(this);
        capchaImg.setOnClickListener(this);
        edt_phone.addTextChangedListener(new PhoneEditTextWatcher());
        capchaCallback = new RequestCallback() {

            @Override
            public void onSuccess(final Object o) {
                UIUtil.dismissLoadingDialog();
                final CapchaMainBean cBean = (CapchaMainBean) o;
                isVaild = cBean.isVaild();
                if (cBean.isVaild()) {
                    vcodeId = cBean.getCodeId();
                    capchaLin.setVisibility(View.VISIBLE);
                    volleyImageLoader.displayVeryImg(cBean.getUrl(), capchaImg, null);
                } else {
                    capchaLin.setVisibility(View.GONE);
                    vcodeId = "";
                }
            }

            @Override
            public void onFail(final Exception e) {
                UIUtil.dismissLoadingDialog();
            }
        };
    }

    @Override
    protected void onLoadData() {

    }

    @Override
    protected void onRequestData() {
        // ???????????????????????????????????????
        if (isnotVip) {
            UIUtil.showLoadingDialog(this);
            HttpUtil.get(ConstantUrl.GET_VERIFY_CODE, CapchaMainBean.class, capchaCallback);
        }

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
        if (null == mApi)
            mApi = new TicketApi();
        mApi.getCinemaRefoundMsg(mCinemaId, new NetworkManager.NetworkListener<RefoundTicketMsgBean>() {
            @Override
            public void onSuccess(RefoundTicketMsgBean result, String showMsg) {
                if (null != result) {
                    refoundMsgTv.setText(TextUtils.isEmpty(result.refundExplain) ? "?????????????????????????????????????????????????????????????????????????????????????????????????????????" : result.refundExplain);
                }
                refoundMsgTv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(NetworkException<RefoundTicketMsgBean> exception, String showMsg) {
                refoundMsgTv.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onUnloadData() {

    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.confirm_oder_btn_next_step:
                if (isVaild) {
                    if (capchaText.getText().toString().equals("")) {
                        MToastUtils.showShortToast("??????????????????");

                    } else {
                        vcode = capchaText.getText().toString();
                        doNext();
                    }
                } else {
                    doNext();
                }

                break;
            case R.id.confirm_oder_btn_mtime_card:
                isMtimeCard = true;
                if (isVaild) {
                    if (capchaText.getText().toString().equals("")) {
                        MToastUtils.showShortToast("??????????????????");

                    } else {
                        vcode = capchaText.getText().toString();
                        doNext();
                    }
                } else {
                    doNext();
                }

                break;
            case R.id.capcha_img:
                UIUtil.showLoadingDialog(OrderConfirmActivity.this);
                HttpUtil.get(ConstantUrl.GET_VERIFY_CODE, CapchaMainBean.class, capchaCallback);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (null != giveupBean) {
                showCollectionView();
                return true;
            }
            if (null != this.collectionView && collectionView.isShowing()) {
                collectionView.showView(false);
                collectionView = null;
            }

            if (!isnotVip && isCreatOrder) {
                showCancelOrderDialog();
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    private void showCancelOrderDialog() {

        final CustomAlertDlg mCustomDlg = new CustomAlertDlg(this, CustomAlertDlg.TYPE_OK_CANCEL);
        mCustomDlg.setBtnOKListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                doCancelOrder(mOrderId);
            }

        });
        mCustomDlg.setBtnCancelListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                mCustomDlg.dismiss();
            }
        });
        mCustomDlg.show();
        mCustomDlg.getTextView().setText("??????????????????????????????");

    }

    // ???????????????
    private void doNext() {
        // ?????????????????????????????????????????????
        if (isCreatOrder) {
            OrderConfirmActivity.this.showLockingDialog();
            OrderConfirmActivity.this.setPollingTimerTask(); // ???????????????????????????3???????????????????????????????????????
            OrderConfirmActivity.this.pollSubOrderStatus();
        } else {
            if (isnotVip) {
                mHasUserCanceledCreateOrder = false; // ????????????
                if (isVaild) {
                    if ((vcodeId != null) && (vcode != null)) {
                        OrderConfirmActivity.this.doCreateOrder(mSeatId, notVipphoneNum, vcodeId, vcode);
                    }

                } else {
                    OrderConfirmActivity.this.doCreateOrder(mSeatId, notVipphoneNum, "", "");
                }
            } else {
                mHasUserCanceledCreateOrder = false; // ????????????
                // ?????????
                // ?????????????????????
                final String phone = edt_phone.getText().toString();
                if ((phone == null) || !TextUtil.isMobileNO(phone)) {
                    MToastUtils.showShortToast("?????????????????????????????????");
                    return;
                }
                // if (!TextUtil.isMobileNO(phone)) {
                // final Toast toast = Toast.makeText(OrderConfirmActivity.this,
                // "????????????????????????????????????", Toast.LENGTH_SHORT);
                // toast.setGravity(Gravity.CENTER, 0, 0);
                // toast.show();
                // return;
                // }
                // ??????????????????????????????????????????????????????
                if ((mAutoFillMobileNumber != null) && !phone.equals(mAutoFillMobileNumber)) {
                    mPrefs.putString(OrderConfirmActivity.KEY_MOBILE_LAST_TIME_INPUTED, phone);
                }

                mUserBuyTicketPhone = phone;
                // ??????
                doCreateOrder(mSeatId, phone, "", "");
            }
        }

    }

    private void showErrorDialog(String info) {
        if (!canShowDlg) {
            return;
        }

        if (info == null || info.equals("")) {
            info = "????????????";
        }
        final CustomAlertDlg errDlg = new CustomAlertDlg(this, CustomAlertDlg.TYPE_OK);
        errDlg.setBtnOKListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                errDlg.dismiss();
                OrderConfirmActivity.this.finish();
            }
        });
        errDlg.show();
        errDlg.setCancelable(false);
        errDlg.getTextView().setText(info);
        errDlg.getBtnOK().setText("??????");
    }

    private void doCreateOrder(final String seatId, final String phone, final String vcodeId, final String vcode) {
        final RequestCallback createOrderCallback = new RequestCallback() {
            @Override
            public void onSuccess(final Object o) {
                UIUtil.dismissLoadingDialog();
                CreateOrderJsonBean result = (CreateOrderJsonBean) o;
                if ((null != result) && result.isSuccess()) {
                    mOrderId = String.valueOf(result.getOrderId());
                    mSubOrderId = String.valueOf(result.getSubOrderId());
                    mPayEndTime = result.getPayEndTime();
                    // mHasCreateOrderSuccessed = true;
                    if (mHasUserCanceledCreateOrder) {
                        // ???????????????????????????????????????????????????(?????????????????????)

                        OrderConfirmActivity.this.doCancelOrder(mOrderId);
                        if (mLockingSeatDlg != null && mLockingSeatDlg.isShowing()) {
                            mLockingSeatDlg.dismiss();
                        }

                        return;
                    }
                    if (smallPayDesc != null && smallPayDesc.length() > 0) {
                        if (result.isAddBuffetSubOrder()) {
                            OrderConfirmActivity.this.showLockingDialog();
                            OrderConfirmActivity.this.setPollingTimerTask(); // ???????????????????????????3???????????????????????????????????????
                            OrderConfirmActivity.this.pollSubOrderStatus();

                        } else {
                            MToastUtils.showShortToast("??????????????????");

                            // ??????????????????
//                            if (result.getBufferError() != null && result.getBufferError().length() > 0
//                                    && result.getFailedCommoditys() != null && result.getFailedCommoditys().size() > 0) {
//                                showSmallPayErrorDlg(result.getBufferError(), result.getFailedCommoditys());
//                            } else {
//                                if (result.getFailedCommoditys() != null && result.getFailedCommoditys().size() > 0) {
//                                    showSmallPayErrorDlg("??????????????????", result.getFailedCommoditys());
//                                } else {
//                                    MToastUtils.showShortToast("??????????????????");
//
//                                }
//
//                            }
                        }
                    } else {
                        OrderConfirmActivity.this.showLockingDialog();
                        OrderConfirmActivity.this.setPollingTimerTask(); // ???????????????????????????3???????????????????????????????????????
                        OrderConfirmActivity.this.pollSubOrderStatus();
                    }

                } else { // ??????????????????

                    if (mHasUserCanceledCreateOrder) {
                        // ???????????????????????????????????????????????????
                        return;
                    }

                    String msg = null != result ? result.getMsg() : "";
                    if (null != result && result.getStatus() == -100) {
                        //????????????-100
                        OrderConfirmActivity.this.createOrderRefresh(msg);
                    } else {
                        OrderConfirmActivity.this.showErrorDialog(msg);
                    }

                }
                result = null;
            }

            @Override
            public void onFail(final Exception e) {
                UIUtil.dismissLoadingDialog();
                // mDialog.dismiss();
                if (mLockingSeatDlg != null && mLockingSeatDlg.isShowing()) {
                    mLockingSeatDlg.dismiss();
                }
                if (!OrderConfirmActivity.this.isFinishing()) {
                    final AlertDialog alertDlg = Utils.createDlg(OrderConfirmActivity.this,
                            OrderConfirmActivity.this.getString(R.string.str_error),
                            OrderConfirmActivity.this.getString(R.string.str_load_error));
                    alertDlg.show();
                }
            }
        };
        UIUtil.showLoadingDialog(this);
        // ?????????????????????????????????????????????????????????
        if (isnotVip) {
            // ????????????????????????????????????????????? 2020-11-19 by wwl???
            Map<String, String> parameterList = new ArrayMap<String, String>(6);
            parameterList.put("dId", String.valueOf(mDId));
            parameterList.put("seatId", seatId);
            parameterList.put("mobile", phone);
            parameterList.put("vcodeId", vcodeId); // ??????????????????????????????????????????
            parameterList.put("vcode", vcode);
            parameterList.put("buffetCommoditys", smallPayDesc);
            HttpUtil.post(ConstantUrl.CREATE_ORDER_NOT_VIP, parameterList, CreateOrderJsonBean.class, createOrderCallback);
        } else {
            // ????????????
            Map<String, String> parameterList = new ArrayMap<String, String>(4);
            parameterList.put("dId", String.valueOf(mDId));
            parameterList.put("seatId", seatId);
            parameterList.put("mobile", phone);
            parameterList.put("orderPayModel", isMtimeCard ? "1" : "0");
            HttpUtil.post(ConstantUrl.CREATE_ORDER, parameterList, CreateOrderJsonBean.class, createOrderCallback);
        }

    }

    private void showSmallPayErrorDlg(String msg, final List<FailCommodityIdBean> failedCommoditys) {
        final CustomAlertDlg dlg = new CustomAlertDlg(OrderConfirmActivity.this, CustomAlertDlg.TYPE_OK);
        dlg.show();
        dlg.setText(msg);
        dlg.getBtnOK().setText("????????????");
        // dlg.setBtnCancelListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View arg0) {
        // doCancelOrder(mOrderId);
        // dlg.dismiss();
        // }
        // });
        dlg.setBtnOKListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // ????????????????????????
                refreshSmallPay(failedCommoditys);
                dlg.dismiss();
            }
        });

    }

    private void refreshSmallPay(List<FailCommodityIdBean> failedCommoditys) {
        isCreatOrder = true;
        for (int i = smallPayHasCountList.size() - 1; i >= 0; i--) {
            for (int j = 0; j < failedCommoditys.size(); j++) {
                if (smallPayHasCountList.get(i).getCommodityId() == failedCommoditys.get(j).getCommodityId()) {
                    mTotalPrice -= smallPayHasCountList.get(i).getPrice() * smallPayHasCountList.get(i).getCount();
                    // TODO ???????????????????????????break? ?????????????????????????????????id??????,?????????????????????????????????????????????
                    smallPayHasCountList.remove(i);
                    break;
                }
            }
        }
        smallPayDesc = "";
        smallPayInfo = "";
        if (smallPayHasCountList != null && smallPayHasCountList.size() > 0) {
            for (int i = 0; i < smallPayHasCountList.size(); i++) {
                if (i == smallPayHasCountList.size() - 1) {
                    smallPayDesc += smallPayHasCountList.get(i).getCommodityId() + ","
                            + smallPayHasCountList.get(i).getCount();
                    smallPayInfo += smallPayHasCountList.get(i).getShortName() + smallPayHasCountList.get(i).getCount()
                            + "???" + "???" + smallPayHasCountList.get(i).getDesc() + "???";
                } else {
                    smallPayDesc += smallPayHasCountList.get(i).getCommodityId() + ","
                            + smallPayHasCountList.get(i).getCount() + "|";
                    smallPayInfo += smallPayHasCountList.get(i).getShortName() + smallPayHasCountList.get(i).getCount()
                            + "???" + "???" + smallPayHasCountList.get(i).getDesc() + "???" + "/";
                }
            }
            if (smallPayInfo.contains("\n")) {
                smallPayInfo = smallPayInfo.replace("\n", "");
            }
        } else {
            // smallPayDesc="";
            // smallPayInfo="";
        }
        initViewValues();
    }

    private void showLockingDialog() {
        if (!canShowDlg) {
            return;
        }

        if (mLockingSeatDlg == null) {
            mLockingSeatDlg = new CustomAlertDlg(this, CustomAlertDlg.TYPE_CANCEL);
        }
        if (!mLockingSeatDlg.isShowing()) {
            mLockingSeatDlg.setBtnCancelListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    // ????????????
                    UIUtil.showLoadingDialog(OrderConfirmActivity.this);
                    mHasUserCanceledCreateOrder = true;
                    mLockingSeatDlg.dismiss();
                }
            });

            mLockingSeatDlg.show();
            mLockingSeatDlg.setCanceledOnTouchOutside(false);
            mLockingSeatDlg.getTextView().setText("??????????????????????????????...");
        }
    }

    /**
     * 3??????????????????????????????????????????
     */
    private void setPollingTimerTask() {
        if (mPoolingSuborderTimer != null) {
            mPoolingSuborderTimer.cancel();
        }

        mPoolingSuborderTimer = new Timer();
        mPoolingSuborderTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (!hasOrderCreateSuccess) {
                    mHasPollingTimeout = true;
                }
            }
        }, OrderConfirmActivity.MAX_POLLING_SUBORDER_LONG_TIME);
    }

    /**
     * ????????????
     */
    private void doCancelOrder(final String orderId) {

        final RequestCallback cancelOrderCallback = new RequestCallback() {
            @Override
            public void onSuccess(final Object o) {
                UIUtil.dismissLoadingDialog();
                if (mLockingSeatDlg != null && mLockingSeatDlg.isShowing()) {
                    mLockingSeatDlg.dismiss();
                }

                if (o instanceof CancelOrderJsonBean) {
                    final CancelOrderJsonBean result = (CancelOrderJsonBean) o;

                    String message;
                    if (result.isSuccess()) { // ??????????????????????????????????????????
                        // hasOrderCanceled = true; //????????????????????????????????????????????????????????????
                        message = OrderConfirmActivity.this.getString(R.string.orderCancelSuccess);
                    } else {
                        message = OrderConfirmActivity.this.getString(R.string.orderCancelError);
                        if (result.getStatus() == 1) {
                            message = result.getMsg();
                        } else if (result.getStatus() == 2) { // ??????????????????
                            message = OrderConfirmActivity.this.getString(R.string.orderCancelOk);
                        }
                    }
                    MToastUtils.showShortToast(message);

                }

//                Intent intent = getIntent();
//                startActivity(SeatSelectActivity.class, intent);

                JavaOpenSeatActivity.INSTANCE.openSeatActivity(mDId, null, mMovieId, mCinemaId, null);
            }

            @Override
            public void onFail(final Exception e) {
                UIUtil.dismissLoadingDialog();
                if (mLockingSeatDlg != null && mLockingSeatDlg.isShowing()) {
                    mLockingSeatDlg.dismiss();
                }

                MToastUtils.showShortToast(e.getLocalizedMessage());
            }
        };

        Map<String, String> parameterList = new ArrayMap<String, String>(1);
        parameterList.put("orderId", orderId);
        HttpUtil.post(ConstantUrl.CANCEL_ORDER, parameterList, CancelOrderJsonBean.class, cancelOrderCallback);
    }

    /**
     * ?????????????????????
     */
    private void pollSubOrderStatus() {
        // ????????????????????????????????????????????????
        final RequestCallback subOrderStatusCallback = new RequestCallback() {

            @Override
            public void onSuccess(final Object o) {
                if (mHasUserCanceledCreateOrder) {
                    // ?????????????????????????????????????????????????????????????????????????????????????????????????????????
                    OrderConfirmActivity.this.doCancelOrder(mOrderId);
                    return;
                }

                if (o instanceof SubOrderStatusJsonBean) {
                    final SubOrderStatusJsonBean subOrderStatusJsonBean = (SubOrderStatusJsonBean) o;
                    subOrderStatus = subOrderStatusJsonBean.getSubOrderStatus();
                    switch (subOrderStatus) {
                        case -1: // -1?????????ID?????????
                            if (mLockingSeatDlg != null && mLockingSeatDlg.isShowing()) {
                                mLockingSeatDlg.dismiss();
                            }
                            createOrderFail("");
                            break;
                        case 0: // 0-??????(??????????????????????????????)
                            // ????????????????????????????????????????????????????????????????????????
                            pollSubOrderStatus();
                            break;
                        case 10: // 10-????????????(?????????????????????????????????)
                            hasOrderCreateSuccess = true;
                            if (mLockingSeatDlg != null && mLockingSeatDlg.isShowing()) {
                                mLockingSeatDlg.dismiss();
                            }
                            // ???????????????????????????
                            if (mPoolingSuborderTimer != null) {
                                mPoolingSuborderTimer.cancel();
                            }

                            if (isMtimeCard) {
                                // ?????????????????????????????????webview ????????????
                                Map<String, String> parameterList = new ArrayMap<String, String>(1);
                                parameterList.put("orderId", mOrderId);
                                HttpUtil.get(ConstantUrl.GET_PAY_LIST, parameterList, GetPayListBean.class, new RequestCallback() {

                                    @Override
                                    public void onSuccess(Object o) {
                                        GetPayListBean payListBean = (GetPayListBean) o;
                                        ArrayList<PayCardListBean> cardList = payListBean.getCardList();
                                        for (int i = 0; i < cardList.size(); i++) {
                                            if (cardList.get(i).getTypeId() == 5) {
                                                // ??????????????????????????????
                                                String url = cardList.get(i).getUrl();
                                                if (url != null && url.length() > 0) {
                                                    // ?????????webview
                                                    // Intent intent=
                                                    // new Intent();
                                                    // intent.putExtra(Constant.WAP_PAY_URL,
                                                    // url);
                                                    // intent.putExtra(Constant.KEY_SEATING_ORDER_ID,
                                                    // mOrderId);
                                                    // startActivity(Constant.ACTIVITY_WAP_PAY,
                                                    // intent);
                                                    // finish();
                                                    requestWithLogin(url);
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFail(Exception e) {

                                    }
                                });
                            } else {
                                // ????????????????????????
                                gotoOrderPayActivity();
                                finish();

                            }
                            break;
                        case 20: // 20-????????????(?????????????????????????????????)
                            if (mLockingSeatDlg != null && mLockingSeatDlg.isShowing()) {
                                mLockingSeatDlg.dismiss();
                            }
                            createOrderFail("");
                            break;
                        case 100:
                            if (mLockingSeatDlg != null && mLockingSeatDlg.isShowing()) {
                                mLockingSeatDlg.dismiss();
                            }
                            createOrderFail("");
                            break;
                        default:
                            if (mLockingSeatDlg != null && mLockingSeatDlg.isShowing()) {
                                mLockingSeatDlg.dismiss();
                            }
                            createOrderFail("");
                            break;
                    }
                }

            }

            @Override
            public void onFail(final Exception e) {
                if (!OrderConfirmActivity.this.isFinishing()) {
                    OrderConfirmActivity.this.pollSubOrderStatus();
                }
            }
        };

        if (mHasPollingTimeout) {
            // ????????????
            createOrderFail("??????????????????");
            if (mLockingSeatDlg != null && mLockingSeatDlg.isShowing()) {
                mLockingSeatDlg.dismiss();
            }

            return;
        }

        DispatchAsync.dispatchAsyncDelayed(new Runnable() {
            @Override
            public void run() {
                Map<String, String> parameterList = new ArrayMap<String, String>(1);
                parameterList.put("subOrderId", mSubOrderId);
                HttpUtil.get(ConstantUrl.GET_SUB_ORDER_STATUS, parameterList, SubOrderStatusJsonBean.class, subOrderStatusCallback);
            }
        }, POLLING_SLEEP_TIME);

    }

    /**
     * ????????????????????????
     */
    private void gotoOrderPayActivity() {
        final Intent intent = new Intent();
        intent.putExtra(App.getInstance().PAY_ETICKET, false); // ?????????????????????
        intent.putExtra(App.getInstance().KEY_SEATING_ORDER_ID, mOrderId); // ??????id
        intent.putExtra(App.getInstance().KEY_SEATING_TOTAL_PRICE, mTotalPrice); // ??????
        intent.putExtra(App.getInstance().KEY_SEATING_SERVICE_FEE, mServiceFee); // ?????????
        intent.putExtra(App.getInstance().KEY_SEATING_PAY_ENDTIME, mPayEndTime); // ??????????????????

        intent.putExtra(App.getInstance().KEY_MOVIE_NAME, mMovieName);
        intent.putExtra(App.getInstance().KEY_CINEMA_NAME, mCinemaName);
        intent.putExtra(App.getInstance().KEY_CINEMA_PHONE, mCinemaPhone);
        intent.putExtra(App.getInstance().KEY_USER_BUY_TICKET_PHONE, mUserBuyTicketPhone);
        intent.putExtra(App.getInstance().KEY_SEATING_SEAT_ID, mSeatId);
        intent.putExtra(App.getInstance().KEY_SEATING_SELECTED_SEAT_COUNT, mSelectedSeatCount); // ?????????
        // ????????????????????????getOrderId()???getSubOrderID()???????????????0
        intent.putExtra(App.getInstance().KEY_SEATING_SUBORDER_ID, mSubOrderId);
        intent.putExtra(App.getInstance().KEY_TICKET_DATE_INFO, mTicketDateInfo);
        intent.putExtra(App.getInstance().KEY_SEAT_SELECTED_INFO, mSeatSelectedInfo);
        // ??????4?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        intent.putExtra(App.getInstance().KEY_SEATING_DID, mDId);
        intent.putExtra(App.getInstance().KEY_MOVIE_ID, mMovieId);
        intent.putExtra(App.getInstance().KEY_CINEMA_ID, mCinemaId);
        intent.putExtra(App.getInstance().KEY_SHOWTIME_DATE, mDate);
        intent.putExtra(App.getInstance().KEY_TARGET_NOT_VIP_PHONE, notVipphoneNum);
        intent.putExtra(App.getInstance().KEY_TARGET_NOT_VIP, isnotVip);
        intent.putExtra(App.getInstance().IS_FROM_SEATSELECT_ACTIVITY, isFromSelectActivity);
        if (isMtimeCard && mtimeLoginUrl != null) {
            intent.putExtra(App.getInstance().KEY_MTIME_URL, mtimeLoginUrl);
        }
        this.startActivity(OrderPayActivity.class, intent);
        finish();
    }

    /**
     * ???????????????()
     */
    private void createOrderRefresh(final String failInfo) {
        if ((vcodeId != null) && (vcode != null)) {
            capchaText.setText("");
            MToastUtils.showShortToast(failInfo);
            HttpUtil.get(ConstantUrl.GET_VERIFY_CODE, CapchaMainBean.class, capchaCallback);
        } else {
            MToastUtils.showShortToast(failInfo);
        }

    }

    /**
     * ???????????????
     */
    private void createOrderFail(final String failInfo) {
        if (!canShowDlg) {
            return;
        }
        // ???????????????????????????
        final CustomAlertDlg lockFailedDlg = new CustomAlertDlg(this, CustomAlertDlg.TYPE_OK_CANCEL);
        lockFailedDlg.setBtnOKListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                // ????????????
                AppLinkExtKt.openHome();
                OrderConfirmActivity.this.finish();
            }
        });
        lockFailedDlg.setBtnCancelListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                // ????????????
                OrderConfirmActivity.this.finish();
            }
        });
        lockFailedDlg.show();
        lockFailedDlg.setCancelable(false);
        if (failInfo.equals("")) {
            lockFailedDlg.getTextView().setText(this.getString(R.string.ticketCommitFail));
        } else {
            lockFailedDlg.getTextView().setText(failInfo);
        }
        lockFailedDlg.getBtnOK().setText("????????????");
        lockFailedDlg.getBtnCancel().setText("????????????");
    }

    // ???????????????????????????????????????
    private class PhoneEditTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
        }

        @Override
        public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
        }

        @Override
        public void afterTextChanged(final Editable s) {
            // // ????????????????????????????????????????????????????????????????????????????????????
            // if (edt_phone.getText().length() == VALID_PHONE_NUMBER_LENGTH) {
            // btn_next_step.setBackgroundResource(R.drawable.btn_orange_selector);
            // btn_next_step.setClickable(true);
            // } else {
            // btn_next_step.setBackgroundColor(Color.GRAY);
            // btn_next_step.setClickable(false);
            // }
        }
    }

    private void requestWithLogin(String url) {
        Map<String, String> parameterList = new ArrayMap<String, String>(1);
        parameterList.put("url", url);
        HttpUtil.post(ConstantUrl.GET_COUPON_URL_WITH_LOGIN, parameterList, SuccessBean.class, new RequestCallback() {
            public void onSuccess(Object o) {
                SuccessBean bean = (SuccessBean) o;
                if (bean.getSuccess() != null) {
                    if (bean.getSuccess().equalsIgnoreCase("true")) {
                        if (UserManager.Companion.getInstance().isLogin()) {
                            mtimeLoginUrl = bean.getNewUrl();
                            gotoOrderPayActivity();
                        }

                    } else {
                        MToastUtils.showShortToast("??????????????????????????????????????????");
                    }
                } else {
                    MToastUtils.showShortToast("??????????????????????????????????????????");
                }

            }

            public void onFail(Exception e) {
                MToastUtils.showShortToast("???????????????????????????????????????");
            }
        });
    }

    private void showCollectionView() {

        View root = this.findViewById(R.id.guide_view);
        List<String> datas = this.giveupBean.getList();
        datas.add("????????????");
        this.collectionView = new GiveupPayCollectionView(this, root, BaiduConstants.BAIDU_EVENTID_GIVEUP_WRITE_TICKET_ORDER, this.giveupBean.getTitle(), datas, new Runnable() {
            @Override
            public void run() {
                collectionView = null;
            }
        });
        collectionView.showView(true);
        giveupBean = null;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!ToolsUtils.getSpecialFilterGuider(this, "ticket_giveup_collection_show")) {
            this.giveupBean = null;
        }
    }


    private void initExplainsView() {
        if (orderExplains != null && orderExplains.size() > 0) {
            for (int i = 0; i < orderExplains.size(); i++) {
                View view = View.inflate(OrderConfirmActivity.this, R.layout.order_confirm_explains_item, null);
                ((TextView) view.findViewById(R.id.explains_text)).setText(orderExplains.get(i));
                explainsView.addView(view);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mApi) {
            mApi.cancel();
        }
    }
}
