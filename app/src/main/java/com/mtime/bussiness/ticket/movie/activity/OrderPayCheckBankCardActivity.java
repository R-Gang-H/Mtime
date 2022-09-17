package com.mtime.bussiness.ticket.movie.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.collection.ArrayMap;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import com.kotlin.android.film.JavaOpenSeatActivity;
import com.mtime.R;
import com.mtime.base.utils.DispatchAsync;
import com.mtime.base.utils.MTimeUtils;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.ticket.movie.bean.BlendPayBean;
import com.mtime.bussiness.ticket.movie.bean.OrderStatusJsonBean;
import com.mtime.bussiness.ticket.movie.bean.PayItemBean;
import com.mtime.common.utils.PrefsManager;
import com.mtime.common.utils.Utils;
import com.mtime.frame.App;
import com.mtime.network.ConstantUrl;
import com.mtime.network.RequestCallback;
import com.mtime.payment.AbstractPayActivity;
import com.mtime.payment.dialog.OrderPayAgainDialog;
import com.mtime.payment.dialog.OrderPayTicketOutingDialog;
import com.mtime.statistic.baidu.BaiduConstants;
import com.mtime.util.CustomAlertDlg;
import com.mtime.util.HttpUtil;
import com.mtime.util.MtimeUtils;
import com.mtime.widgets.BaseTitleView;
import com.mtime.widgets.TitleOfNormalView;

import java.util.Map;
import java.util.Timer;

/**
 * 验证银行卡页
 */
public class OrderPayCheckBankCardActivity extends AbstractPayActivity {
    private EditText etCardnumber;
    private TextView btnOk;

    private String activityIds;
    private PayItemBean payItemBean;

    protected CustomAlertDlg mCustomDlg;
    protected OrderPayTicketOutingDialog ticketOutingDialog;
    protected ProgressDialog mDialog; // 加载对话框, 正在加载,请稍候
    protected int mPoolingCounter = 1; // 轮询次数计数器
    protected PrefsManager mPrefs;
    protected Timer mPoolingMainOrderTimer; // 轮询子订单超时控制定时器
    protected int count;
    protected boolean isFirst = true; // 是否第一次失败，如果是第一次失败，自动重新选座，如果不是，则跳转到失败页面，手动选座
    private String mReselectSeatNewOrderId;
    protected OrderPayAgainDialog againCBCustomAlertDlg;
    protected static final int OVER_ORDER_TYPE = 2; // 已完成付款查看的订单状态type
    private boolean isFromSelectActivity;


    @Override
    protected void onInitVariable() {
        mDialog = Utils.createProgressDialog(this, this.getString(R.string.str_loading));
        mPrefs = App.getInstance().getPrefsManager();
        mDId = getIntent().getStringExtra(App.getInstance().KEY_SEATING_DID);
        Intent intent = getIntent();
        isFromSelectActivity = intent.getBooleanExtra(App.getInstance().IS_FROM_SEATSELECT_ACTIVITY, false);
        setPageLabel("orderCheckBank");
    }

    @Override
    protected void onInitView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_order_pay_check_bank_card);
        initTitlebar();
        TextView tvNote = findViewById(R.id.check_bank_card_note_tv);
        etCardnumber = findViewById(R.id.check_bank_card_cardnumber_et);
        bankCardNumAddSpace(etCardnumber);
        TextView tvTip = findViewById(R.id.check_bank_card_tip_tv);
        btnOk = findViewById(R.id.check_bank_card_ok_btn);
        if (getIntent() != null && getIntent().getExtras() != null) {
            payItemBean = (PayItemBean) getIntent().getSerializableExtra(App.getInstance().CB_PAY_ITEM_BEAN);
            mOrderId = getIntent().getStringExtra(App.getInstance().CB_PAY_ORDER_ID);
            activityIds = getIntent().getStringExtra(App.getInstance().CB_PAY_ACTIVITYIDS);
            isFromAccount = getIntent().getBooleanExtra(App.getInstance().KEY_ISFROM_ACCOUNT, false);
            if (payItemBean != null) {
                String note = payItemBean.getActivitiesNote();
                String tip = payItemBean.getPaymentVerification();
                String bankName = payItemBean.getBankName();
                tvNote.setText(Html.fromHtml(note + "<font color=\"#0075c4\">" + bankName + "</font>"));
                tvTip.setText(tip);
                if (getIntent().getLongExtra(App.getInstance().PAY_LONG_TIME, 0) > 0) {
                    payLongTimeRemaining = getIntent().getLongExtra(App.getInstance().PAY_LONG_TIME, 0);
                }
                clockFlag = true;
                orderExpire(this);
            }
        }

    }

    private void initTitlebar() {
        View navBar = this.findViewById(R.id.navigationbar);
        navigationbar = new TitleOfNormalView(this, navBar, BaseTitleView.StructType.TYPE_NORMAL_SHOW_BACK_TITLE_TIMER, getResources().getString(R.string.str_pay_money),
                new BaseTitleView.ITitleViewLActListener() {

                    @Override
                    public void onEvent(BaseTitleView.ActionType type, String content) {
                        switch (type) {
                            case TYPE_BACK:
                                finish();
                                break;
                            default:
                                break;
                        }
                    }
                });
        navigationbar.setTimerViewVisibility(View.VISIBLE);
    }

    @Override
    protected void onInitEvent() {
        etCardnumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null && !"".equals(editable.toString())) {
                    btnOk.setBackgroundResource(R.drawable.bt_solid_orange_w660_h80);
                    btnOk.setClickable(true);
                } else {
                    btnOk.setBackgroundResource(R.drawable.bt_solid_gray_w660_h80);
                    btnOk.setClickable(false);
                }
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String etCardnumberText = etCardnumber.getText().toString().replace(" ", "");
                if (TextUtils.isEmpty(etCardnumberText) || etCardnumberText.length() < 8 || etCardnumberText.length() > 30) {
                    MToastUtils.showLongToast("请输入正确的银行卡号");
                } else {
                    if (payItemBean != null) {
                        doBlendPay(mOrderId, "", "", "", String.valueOf((int) payItemBean.getNeedPayAmount()), String.valueOf(payItemBean.getPayType()), "", "", "", "");
                    }
                }
            }
        });

    }

    @Override
    protected void onRequestData() {

    }

    @Override
    protected void onLoadData() {

    }

    @Override
    protected void onUnloadData() {

    }

    /**
     * 银行卡四位加空格
     *
     * @param mEditText
     */
    protected void bankCardNumAddSpace(final EditText mEditText) {
        mEditText.addTextChangedListener(new TextWatcher() {
            int beforeTextLength = 0;
            int onTextLength = 0;
            boolean isChanged = false;

            int location = 0;// 记录光标的位置
            private char[] tempChar;
            private final StringBuffer buffer = new StringBuffer();
            int konggeNumberB = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                beforeTextLength = s.length();
                if (buffer.length() > 0) {
                    buffer.delete(0, buffer.length());
                }
                konggeNumberB = 0;
                for (int i = 0; i < s.length(); i++) {
                    if (s.charAt(i) == ' ') {
                        konggeNumberB++;
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                onTextLength = s.length();
                buffer.append(s.toString());
                if (onTextLength == beforeTextLength || onTextLength <= 3
                        || isChanged) {
                    isChanged = false;
                    return;
                }
                isChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isChanged) {
                    location = mEditText.getSelectionEnd();
                    int index = 0;
                    while (index < buffer.length()) {
                        if (buffer.charAt(index) == ' ') {
                            buffer.deleteCharAt(index);
                        } else {
                            index++;
                        }
                    }

                    index = 0;
                    int konggeNumberC = 0;
                    while (index < buffer.length()) {
                        if ((index == 4 || index == 9 || index == 14 || index == 19)) {
                            buffer.insert(index, ' ');
                            konggeNumberC++;
                        }
                        index++;
                    }

                    if (konggeNumberC > konggeNumberB) {
                        location += (konggeNumberC - konggeNumberB);
                    }

                    tempChar = new char[buffer.length()];
                    buffer.getChars(0, buffer.length(), tempChar, 0);
                    String str = buffer.toString();
                    if (location > str.length()) {
                        location = str.length();
                    } else if (location < 0) {
                        location = 0;
                    }

                    mEditText.setText(str);
                    Editable etable = mEditText.getText();
                    Selection.setSelection(etable, location);
                    isChanged = false;
                }
            }
        });
    }


    private ProgressDialog mpayDialog;

//    /**


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        /*************************************************
         * 步骤3：处理银联手机支付控件返回的支付结果
         ************************************************/
        /*
         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
         */
        String str = data.getExtras().getString("pay_result");
        if (!TextUtils.isEmpty(str)) {
            if (str.equalsIgnoreCase("success")) {
                pollMainOrderStatus(mOrderId);
                return;
            } else if (str.equalsIgnoreCase("fail")) {
                showCBPayAgainDlg();
            } else if (str.equalsIgnoreCase("cancel")) {
                showCBPayAgainDlg();
            }
        }
    }

    private void doBlendPay(final String orderId, final String vcode, final String voucherIdList,
                            final String balancePayAmount, String rechargePayAmount, final String payType, final String cardId,
                            final String useNum, final String token, final String bankId)

    {
        mpayDialog = Utils.createProgressDialog(OrderPayCheckBankCardActivity.this, "正在支付，请稍候...");
        mpayDialog.show();
        childDoBlendPay(orderId, vcode, voucherIdList, balancePayAmount, rechargePayAmount, payType, cardId, useNum,
                token, bankId);

    }

    protected void childDoBlendPay(final String orderId, final String vcode, final String voucherIdList, final String balancePayAmount,
                                   String rechargePayAmount, final String payType, final String cardId, final String useNum, final String token, final String bankId) {

        RequestCallback blendPayCallback = new RequestCallback() {

            @Override
            public void onSuccess(Object o) {
                final BlendPayBean result = (BlendPayBean) o;
                final int payStatus = result.getStatus();

                if (result.isSuccess()) { // 支付成功
                    if (null != mpayDialog && mpayDialog.isShowing()) {
                        mpayDialog.dismiss();
                    }
                    if (!TextUtils.isEmpty(result.getActivityMsg())) {
                        if (result.isPromotionCount() || result.isUserLimitMAX()) {
                            MToastUtils.showLongToast(result.getActivityMsg());
                        } else {
                            //TODO X1 用不用弹框
                            final CustomAlertDlg msgDlg = new CustomAlertDlg(OrderPayCheckBankCardActivity.this, CustomAlertDlg.TYPE_OK_CANCEL);
                            msgDlg.show();
                            msgDlg.setText(result.getActivityMsg());
                            msgDlg.setLabels("取消订单", "继续支付");
                            msgDlg.setBtnCancelListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    msgDlg.dismiss();
                                    cancelOrder();
                                    //TODO X1 支付页里是取消按钮点击后 取消当前中行的选中项，请求PaymentItems.api
                                }
                            });
                            msgDlg.setBtnOKListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    msgDlg.dismiss();
                                    final String formXML = result.getFormXML(); // 支付报文
                                    if (payType.equals(String.valueOf(App.getInstance().PAYTYPE_YINLIAN))) { // 银联支付
                                        MtimeUtils.doPay(OrderPayCheckBankCardActivity.this, formXML);
                                    }
                                }
                            });
                        }
                    } else {
                        final String formXML = result.getFormXML(); // 支付报文
                        if (payType.equals(String.valueOf(App.getInstance().PAYTYPE_YINLIAN))) { // 银联支付
                            MtimeUtils.doPay(OrderPayCheckBankCardActivity.this, formXML);
                        }
                        //TODO X1 打开
//                    else if (payType.equals(App.getInstance().PAYTYPE_ALIPAY + "")) {
//                        OrderPayCheckBankCardActivity.this.alipay(formXML);
//                    } else if (payType.equals(App.getInstance().PAYTYPE_ALIPAY_WAP + "")) {
//                        alipayWap(formXML);
//                    } else if (payType.equals(App.getInstance().PAYTYPE_WEIXIN + "")) {
//                        weixinPay(formXML);
//                    } else {
//                        mpayDialog.dismiss();
//                        pollMainOrderStatus(orderId);
//                    }
                    }

                } else {
                    if (null != mpayDialog && mpayDialog.isShowing()) {
                        mpayDialog.dismiss();
                    }
                    if (!TextUtils.isEmpty(result.getUnionPayMsg())) {
                        MToastUtils.showLongToast(result.getUnionPayMsg());
                    } else {
                        switch (payStatus) {
                            case 1:
                                pollMainOrderStatus(orderId);
                                break;
                            case -4:
                                createOrderFail("支付超时：交易已关闭，您可以重新选择座位");
                                break;
                            case -3:
                                showPayErrorDlg(OrderPayCheckBankCardActivity.this.getString(R.string.loginExprie));
                                break;
                            case -2:
                                MToastUtils.showLongToast(result.getMsg());
                                break;
                            case -1:
                                break;
                            case -5:
                                MToastUtils.showLongToast(result.getMsg());
                                break;
                            case -83:
                                if (isFromSelectActivity) {
                                    gotoPayFailedActivity(OrderPayFailedActivity.ERROR_TYPE_RESELECT_SEAT_AND_CANCEL_ORDER, "支付失败！", "支付失败", "取消订单，重新选座");
                                } else {
                                    gotoPayFailedActivity(OrderPayFailedActivity.ERROR_TYPE_RESELECT_SEAT_AND_BACK_TO_HOME, "支付失败！", "支付失败", "取消订单，重新选座");
                                }
                                break;
                            default:
                                String msg = result.getMsg();
                                if (msg == null) {
                                    msg = "支付异常";
                                }
                                showPayErrorDlg(msg);
                        }
                    }
                }
            }

            @Override
            public void onFail(Exception e) {
                if (null != mpayDialog && mpayDialog.isShowing()) {
                    mpayDialog.dismiss();
                }
                MToastUtils.showLongToast("支付失败，请稍后重试:" + e.getLocalizedMessage());
            }
        };
        Map<String, String> parameterList = new ArrayMap<String, String>(14);
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
        parameterList.put("mobile", "");
        parameterList.put("bankCard", etCardnumber.getText().toString().replace(" ", ""));
        if (activityIds != null && !"".equals(activityIds.trim()) && !"-1".equals(activityIds.trim())) {
            parameterList.put("activityIds", activityIds);
        }
        HttpUtil.post(ConstantUrl.BLEND_PAY, parameterList, BlendPayBean.class, blendPayCallback);

    }

    /**
     * 付款失败
     */
    protected void showPayErrorDlg(String errorInfo) {
        if (!canShowDlg) {
            return;
        }

        if (errorInfo == null || errorInfo.equals("")) {
            errorInfo = "支付失败";
        }

        mCustomDlg = new CustomAlertDlg(OrderPayCheckBankCardActivity.this, CustomAlertDlg.TYPE_OK);
        mCustomDlg.setBtnOKListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                mCustomDlg.dismiss();
                if (!isFromMall) {
                    //TODO X1 打开
//                    getOrderInfo();
                }

            }
        });
        mCustomDlg.show();
        mCustomDlg.setCancelable(false);
        mCustomDlg.setText(errorInfo);
    }

    // 加载正在出票
    protected void showTicketOutingDialog() {
        if (ticketOutingDialog == null) {
            ticketOutingDialog = new OrderPayTicketOutingDialog(OrderPayCheckBankCardActivity.this);
        }
        if (!ticketOutingDialog.isShowing()) {
            ticketOutingDialog.show();
            ticketOutingDialog.setCancelable(false);
        }
    }

    /**
     * 轮询主订单状态
     */
    protected void pollMainOrderStatus(final String orderId) {
        showTicketOutingDialog();

        final RequestCallback getMainOrderStatusCallback = new RequestCallback() {
            @Override
            public void onSuccess(final Object o) {
                final OrderStatusJsonBean order = (OrderStatusJsonBean) o;
                final int orderStatus = order.getOrderStatus();
                if (orderStatus != 10) {
                    if (mDialog != null && mDialog.isShowing()) {
                        mDialog.dismiss();
                    }

                    // 不再轮询则取消定时器
                    if (mPoolingMainOrderTimer != null) {
                        mPoolingMainOrderTimer.cancel();
                    }
                }
                switch (orderStatus) {
                    case 10: // 10-创建成功(对应第三方创建订单成功)，
                        pollMainOrderStatus(orderId); // 接着轮询主订单状态
                        break;

                    case 30: // 30-成功(已支付，对应第三方订单成功完成订单)

                        if (ticketOutingDialog != null && ticketOutingDialog.isShowing()) {
                            ticketOutingDialog.dismiss();
                        }
                        clockFlag = false;
                        gotoPaySuccessActivity(OrderPayCheckBankCardActivity.this, OrderPayCheckBankCardActivity.this.mCinemaName,
                                OrderPayCheckBankCardActivity.this.mMovieName, count, OrderPayCheckBankCardActivity.this.mUnitPrice, OrderPayCheckBankCardActivity.this.mTotalPrice,
                                OrderPayCheckBankCardActivity.this.getString(R.string.payOrder), OrderPayCheckBankCardActivity.this.mSeatSelectedInfo, OrderPayCheckBankCardActivity.this.mIsEticket);
                        OrderPayCheckBankCardActivity.this.finish();
                        break;

                    case 40: // 40-失败(已支付，但部分或全部子订单失败)
                        if (ticketOutingDialog != null && ticketOutingDialog.isShowing()) {
                            ticketOutingDialog.dismiss();
                        }

                        if (OrderPayCheckBankCardActivity.this.mIsEticket) {
                            gotoPayFailedActivity(OrderPayFailedActivity.ERROR_TYPE_PAY_TIME_OUT, "付款失败", "已支付，出券失败\n请联系号客服退款", "");
                        } else if (order.isReSelectSeat()) {
                            // 跳转到支付报错页
                            if (isFirst) {
                                isFirst = !isFirst;
                                doCreateOrder(orderId);
                            } else {
                                mPrefs.putLong(App.getInstance().KEY_SERVICE_DATE, MTimeUtils.getLastDiffServerTime());// 存储时间
                                gotoPayFailedActivity(OrderPayFailedActivity.ERROR_TYPE_RESELECT_SEAT, "很抱歉！", "已付款，但因操作超时座位已被释放\n请重新选座或联系客服退款", "重新选座");
                            }
                        } else {
                            // 跳转到支付报错页
                            // 支付状态: 0—未支付，1—已支付
                            if (order.getPayStatus() == 1) {

                                gotoPayFailedActivity(OrderPayFailedActivity.ERROR_TYPE_GO_HOMEPAGE, "很抱歉！", "已付款，但因操作超时座位已被释放", "返回首页");
                            } else {
                                gotoPayFailedActivity(OrderPayFailedActivity.ERROR_TYPE_RESELECT_PAY, "付款失败", "", "请重新选择付款方式");
                            }

                        }
                        break;

                    case 100: // 100-已取消(用户在支付前主动取消)
                        showPayErrorDlg(OrderPayCheckBankCardActivity.this.getString(R.string.payOrderCancel));
                        break;

                    default:
                        if (!OrderPayCheckBankCardActivity.this.mIsEticket && order.isReSelectSeat()) {
                            if (isFirst) {
                                isFirst = !isFirst;
                                doCreateOrder(orderId);
                            } else {
                                // 跳转到支付报错页

                                mPrefs.putLong(App.getInstance().KEY_SERVICE_DATE, MTimeUtils.getLastDiffServerTime());// 存储时间
                                gotoPayFailedActivity(OrderPayFailedActivity.ERROR_TYPE_RESELECT_SEAT, "很抱歉！", "已付款，但因操作超时座位已被释放\n请重新选座或联系客服退款", "重新选座");
                                finish();
                            }
                        } else {
                            if (order.getPayStatus() == 1) {

                                gotoPayFailedActivity(OrderPayFailedActivity.ERROR_TYPE_GO_HOMEPAGE, "很抱歉！", "已付款，但因操作超时座位已被释放", "返回首页");
                            } else {
                                gotoPayFailedActivity(OrderPayFailedActivity.ERROR_TYPE_RESELECT_PAY, "付款失败", "", "请重新选择付款方式");
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
                pollMainOrderStatus(orderId); // 接着轮询主订单状态
            }
        };

        if (mPoolingCounter > OrderPayActivity.POLLING_MAX_TIME) {
            // 如果轮询超过最大次数，则失败
            if (null != mDialog && mDialog.isShowing()) {
                mDialog.dismiss();
            }
            mPrefs.putLong(App.getInstance().KEY_SERVICE_DATE, MTimeUtils.getLastDiffServerTime());// 存储时间
            mCustomDlg = new CustomAlertDlg(OrderPayCheckBankCardActivity.this, CustomAlertDlg.TYPE_OK);
            mCustomDlg.setBtnOKListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    mCustomDlg.dismiss();
                    // 重新选座
                    final Intent intent = new Intent();
                    intent.putExtra(App.getInstance().KEY_SEATING_SELECT_AGAIN, true);
                    intent.putExtra(App.getInstance().KEY_SEATING_LAST_ORDER_ID, OrderPayCheckBankCardActivity.this.mOrderId);
                    intent.putExtra(App.getInstance().KEY_SEATING_DID, OrderPayCheckBankCardActivity.this.mDId);

//                    OrderPayCheckBankCardActivity.this.startActivity(SeatSelectActivity.class, intent);

                    JavaOpenSeatActivity.INSTANCE.openSeatActivity(mDId, mOrderId, null, null, null);

                    finish();
                }
            });
            mCustomDlg.show();
            mCustomDlg.setText("付款已超时，请您重新选座");
            mCustomDlg.getBtnOK().setText("重新选座");
            mCustomDlg.setCancelable(false);
            return;
        }

        DispatchAsync.dispatchAsyncDelayed(new Runnable() {
            @Override
            public void run() {
                mPoolingCounter++;
                Map<String, String> parameterList = new ArrayMap<String, String>(1);
                parameterList.put("orderId", mOrderId);
                HttpUtil.post(ConstantUrl.GET_ORDER_STATUS, parameterList, OrderStatusJsonBean.class, getMainOrderStatusCallback);
            }
        }, OrderPayActivity.POLLING_SLEEP_TIME);

    }

    /**
     * 跳转到支付报错页
     *
     * @param errorType      错误类型
     * @param errorTitle     错误标题
     * @param errorDetail    错误详情
     * @param errorButtonMsg 错误按钮文字
     */
    protected void gotoPayFailedActivity(final int errorType, final String errorTitle, final String errorDetail, final String errorButtonMsg) {
        final Intent intent = new Intent();
        intent.putExtra(App.getInstance().PAY_ETICKET, mIsEticket); // 是否是电子券
        intent.putExtra(App.getInstance().KEY_SEATING_ORDER_ID, mOrderId); // 订单id
        intent.putExtra(App.getInstance().KEY_PAY_ERROR_TYPE, errorType);
        intent.putExtra(App.getInstance().KEY_PAY_ERROR_TITLE, errorTitle);
        intent.putExtra(App.getInstance().KEY_PAY_ERROR_DETAIL, errorDetail);
        intent.putExtra(App.getInstance().KEY_PAY_ERROR_BUTTON_MESSAGE, errorButtonMsg);
        intent.putExtra(App.getInstance().KEY_CINEMA_PHONE, mCinemaPhone);

        intent.putExtra(App.getInstance().KEY_SEATING_SELECT_AGAIN, true);// 需要重新选座
        intent.putExtra(App.getInstance().KEY_SEATING_TOTAL_PRICE, mTotalPrice);
        intent.putExtra(App.getInstance().KEY_SEATING_SERVICE_FEE, mServiceFee); // 服务费
        intent.putExtra(App.getInstance().KEY_MOVIE_NAME, mMovieName);
        intent.putExtra(App.getInstance().KEY_CINEMA_NAME, mCinemaName);
        intent.putExtra(App.getInstance().KEY_CINEMA_PHONE, mCinemaPhone);
        intent.putExtra(App.getInstance().KEY_SEATING_SEAT_ID, mSeatId);
        intent.putExtra(App.getInstance().KEY_SEATING_SELECTED_SEAT_COUNT, mSelectedSeatCount); // 座位数
        intent.putExtra(App.getInstance().KEY_SEATING_DID, mDId);
        intent.putExtra(App.getInstance().KEY_TICKET_DATE_INFO, mTicketDateInfo);
        this.startActivity(OrderPayFailedActivity.class, intent);
    }

    /**
     * 创建订单（自动重新选座时用到）
     */
    @SuppressLint("SimpleDateFormat")
    protected void doCreateOrder(final String orderId) {
        // http://api.m.mtime.cn/showtime/AutoCreateOrder.api 永远返回失败, 新APP不再需要调用此API

        if (isFromMall) {
            return;
        }

        if (!isFinishing()) {
            AlertDialog alertDlg = Utils.createDlg(OrderPayCheckBankCardActivity.this, 
                    OrderPayCheckBankCardActivity.this.getString(R.string.str_error),
                    OrderPayCheckBankCardActivity.this.getString(R.string.str_load_error));
            alertDlg.show();
        }

        // TODO: 2019-09-19 去掉 wwl
//        final RequestCallback createOrderCallback = new RequestCallback() {
//            @Override
//            public void onSuccess(final Object o) {
//                if (null != mDialog && mDialog.isShowing()) {
//                    mDialog.dismiss();
//                }
//
//                CreateOrderJsonBean result = (CreateOrderJsonBean) o;
//
//                if ((null != result) && result.isSuccess()) {
//                    mReselectSeatNewOrderId = result.getOrderId();
//                    OrderPayCheckBankCardActivity.this.mSubOrderId = result.getSubOrderId();
//                    OrderPayCheckBankCardActivity.this.mPayEndTime = result.getPayEndTime();
//                    OrderPayCheckBankCardActivity.this.pollSubOrderStatus(OrderPayCheckBankCardActivity.this.mSubOrderId);
//                } else { // 下订单未成功
//                    gotoPayFailedActivity(OrderPayFailedActivity.ERROR_TYPE_RESELECT_SEAT, "很抱歉！", "已付款，但因操作超时座位已被释放\n请重新选座或联系客服退款", "重新选座");
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
//                if (!OrderPayCheckBankCardActivity.this.isFinishing()) {
//                    final AlertDialog alertDlg = Utils.createDlg(OrderPayCheckBankCardActivity.this, OrderPayCheckBankCardActivity.this.getString(R.string.str_error),
//                            OrderPayCheckBankCardActivity.this.getString(R.string.str_load_error));
//                    alertDlg.show();
//                }
//            }
//        };
//        Map<String, String> parameterList = new ArrayMap<String, String>(1);
//        parameterList.put("orderId", orderId);
//        HttpUtil.post(ConstantUrl.AUTO_CREATE_ORDER, parameterList, CreateOrderJsonBean.class, createOrderCallback);
    }

    // TODO: 2019-09-19 没有调用的地方，去掉 wwl
    /**
     * 轮询子订单状态
     */
//    private void pollSubOrderStatus(final String subOrderId) {
//        // 请求数据（此处不应该显示进度条）
//        final RequestCallback subOrderStatusCallback = new RequestCallback() {
//
//            @Override
//            public void onSuccess(final Object o) {
//                if (o instanceof SubOrderStatusJsonBean) {
//                    final SubOrderStatusJsonBean subOrderStatusJsonBean = (SubOrderStatusJsonBean) o;
//                    int subOrderStatus = subOrderStatusJsonBean.getSubOrderStatus();
//                    switch (subOrderStatus) {
//
//                        case 0: // 0-新建(此时订单对用户不可见)
//                            // 回调，一直轮询子订单状态，直到成功或用户取消订单
//                            OrderPayCheckBankCardActivity.this.pollSubOrderStatus(subOrderId);
//                            break;
//                        case 10: // 10-创建成功(对应第三方创建订单成功)，
//                            OrderPayCheckBankCardActivity.this.doReselectSeat(mReselectSeatNewOrderId, OrderPayCheckBankCardActivity.this.mOrderId);
//                            break;
//                        case 20: // 20-创建失败(对应第三方创建订单失败)
//                            gotoPayFailedActivity(OrderPayFailedActivity.ERROR_TYPE_RESELECT_SEAT, "很抱歉！", "已付款，但因操作超时座位已被释放\n请重新选座或联系客服退款", "重新选座");
//                            break;
//                        default:
//                            gotoPayFailedActivity(OrderPayFailedActivity.ERROR_TYPE_RESELECT_SEAT, "很抱歉！", "已付款，但因操作超时座位已被释放\n请重新选座或联系客服退款", "重新选座");
//                            break;
//
//                    }
//                }
//
//            }
//
//            @Override
//            public void onFail(final Exception e) {
//                final AlertDialog alertDlg = Utils.createDlg(OrderPayCheckBankCardActivity.this, OrderPayCheckBankCardActivity.this.getString(R.string.str_error),
//                        OrderPayCheckBankCardActivity.this.getString(R.string.str_load_error));
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

    // TODO: 2019-09-19 没有调用的地方，去掉 wwl
    /**
     * 重新选座（正常选座不调用）
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
//                    // 轮询主订单
//                    OrderPayCheckBankCardActivity.this.pollMainOrderStatus(newOrderId);
//                } else {
//                }
//            }
//
//            @Override
//            public void onFail(final Exception e) {
//                if (null != mDialog && mDialog.isShowing()) {
//                    mDialog.dismiss();
//                }
//
//                if (!OrderPayCheckBankCardActivity.this.isFinishing()) {
//                    final AlertDialog alertDlg = Utils.createDlg(OrderPayCheckBankCardActivity.this, OrderPayCheckBankCardActivity.this.getString(R.string.str_error),
//                            OrderPayCheckBankCardActivity.this.getString(R.string.str_load_error));
//                    alertDlg.show();
//                }
//            }
//        };
//        // Ticket/reselectseat.api?orderId={0}&resOrderId={1}
//        Map<String, String> param = new HashMap<>(2);
//        param.put("orderId", newOrderId);
//        param.put("resOrderId", reSelectSeatLastOrderId);
//        HttpUtil.get(ConstantUrl.RESELECT_SEAT, param, CommResultBean.class, reselectSeatCallback);
//    }

    protected void showCBPayAgainDlg() {

        clockFlag = false;
        if (againCBCustomAlertDlg != null && againCBCustomAlertDlg.isShowing()) {
            againCBCustomAlertDlg.dismiss();
        }
        againCBCustomAlertDlg = new OrderPayAgainDialog(OrderPayCheckBankCardActivity.this);
        againCBCustomAlertDlg.show();
        againCBCustomAlertDlg.setCanceledOnTouchOutside(false);
        againCBCustomAlertDlg.setCancleText("取消订单");
        againCBCustomAlertDlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
        againCBCustomAlertDlg.setBtnChangeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clockFlag = true;
                cancelOrder();
            }
        });
        againCBCustomAlertDlg.setBtnOKListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 再次获取订单状态
                getOrderStatue(OVER_ORDER_TYPE);
            }
        });

    }


    // 查看订单状态
    protected void getOrderStatue(final int type) {
        showProgressDialog(OrderPayCheckBankCardActivity.this, "正在加载...");
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
                if (order.getPayStatus() == 0) {// 如果是0 则为失败，弹框提示用户，让用户重新选择支付方式
                    if (type == OVER_ORDER_TYPE) {
                        showPayWaitDlg();
                    }

                } else if (order.getPayStatus() == 1) { // 1 是成功 轮巡主订单
                    pollMainOrderStatus(mOrderId);
                } else {
                    MToastUtils.showLongToast("支付异常");
                }
            }

            @Override
            public void onFail(Exception e) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (type == OVER_ORDER_TYPE) {
                    showPayWaitDlg();
                } else {
                    MToastUtils.showLongToast("加载数据失败,请稍后重试");
                }
            }
        };
        Map<String, String> parameterList = new ArrayMap<String, String>(1);
        parameterList.put("orderId", mOrderId);
        HttpUtil.post(ConstantUrl.GET_ORDER_STATUS, parameterList, OrderStatusJsonBean.class, getOrderStatueCallback);
    }

    protected void showPayWaitDlg() {
        final CustomAlertDlg waitAlertDlg = new CustomAlertDlg(OrderPayCheckBankCardActivity.this, CustomAlertDlg.TYPE_OK);
        waitAlertDlg.show();
        waitAlertDlg.setBtnOKListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                waitAlertDlg.dismiss();
            }
        });
        waitAlertDlg.getBtnOK().setText("继续等待");
        waitAlertDlg.setText("未获取到已付款信息");
    }

//     */

}
