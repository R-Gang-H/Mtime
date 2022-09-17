package com.mtime.payment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.collection.ArrayMap;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.kotlin.android.app.router.ext.AppLinkExtKt;
import com.kotlin.android.film.JavaOpenSeatActivity;
import com.kotlin.android.router.ext.ProviderExtKt;

import com.kotlin.android.app.router.path.RouterProviderPath;
import com.kotlin.android.app.router.provider.ticket_order.ITicketOrderProvider;
import com.mtime.R;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.ticket.movie.activity.OrderPayActivity;
import com.mtime.bussiness.ticket.movie.activity.OrderPaySuccessActivity;
import com.mtime.bussiness.ticket.movie.activity.SeatSelectActivity;
import com.mtime.bussiness.ticket.movie.bean.CancelOrderJsonBean;
import com.mtime.bussiness.ticket.movie.bean.GiveupPayReasonBean;
import com.mtime.bussiness.ticket.movie.bean.OrderStatusJsonBean;
import com.mtime.common.utils.Utils;
import com.mtime.constant.Constants;
import com.mtime.frame.App;
import com.mtime.frame.BaseActivity;
import com.mtime.network.ConstantUrl;
import com.mtime.network.RequestCallback;
import com.mtime.util.CustomAlertDlg;
import com.mtime.util.HttpUtil;
import com.mtime.util.JumpUtil;
import com.mtime.widgets.GiveupPayCollectionView;
import com.mtime.widgets.TimerCountDown;
import com.mtime.widgets.TitleOfNormalView;

import java.util.Map;

/**
 * 封装支付相关逻辑
 */
public abstract class AbstractPayActivity extends BaseActivity {
    protected String mOrderId;
    protected double mUnitPrice;           // 上一个界面传过来的单价
    protected double mTotalPrice;          // 上一个界面传过来的总价  元
    protected double mServiceFee;          // 上一个界面传过来的服务费
    protected String mCinemaName;
    protected String mCinemaPhone;
    protected String mUserBuyTicketPhone;
    protected String mSeatSelectedInfo;
    protected String mTicketDateInfo;
    protected String mMovieName;
    protected long mPayEndTime;
    protected String mSeatId;
    protected int mSelectedSeatCount;
    protected String mSubOrderId;
    // 以下4个数据需为从支付页重新返回选座页需要带回来的数据
    protected String mDId;
    protected String mCinemaId;
    protected String mDate;
    protected String mMovieId;

    protected int mChildPosition;
    protected TitleOfNormalView navigationbar;        // 支付结束时间
    protected ImageView clockImg;             // 时钟
    protected Context subContext;
    protected boolean clockFlag;            // 实现的子类停止调用，防止出错
    protected boolean endTime;              // 时间 到了
    protected ProgressDialog progressDialog;
    protected boolean mIsEticket;           // 电子券
    private CustomAlertDlg mCustomDlg;
    private CustomAlertDlg mBuyAgainDlg;
    public boolean isnotVip;             // 非会员购票标识
    public String notVipphoneNum;
    protected boolean isTimeFinish = false;
    public long payLongTimeRemaining;
    protected boolean isFromAccount = false;

    protected boolean isFromMall;
    protected int presellPaymentMode = 1;    // 预售订单付款模式，1 全款、2
    // 定金 + 尾款
    protected int isFinalPay = 0;    // 0 定金 1 尾款

    protected GiveupPayReasonBean giveupBean;
    protected GiveupPayCollectionView collectionView;

    protected boolean isBackToHome = true;

    protected void onInitVariable() {
        Constants.isShowRegisterGiftDlg = false;
    }

    protected void onInitView(final Bundle savedInstanceState) {

    }

    protected void onInitEvent() {

    }

    protected void onLoadData() {
        Constants.isShowRegisterGiftDlg = false;
    }

    protected void onUnloadData() {

    }

    protected void onRequestData() {

    }

    /**
     * 支付时间
     */
    protected void orderExpire(final Context context) {
        subContext = context;
        if (payLongTimeRemaining > 0L) {
            new TimerCountDown(payLongTimeRemaining) {

                public void onTimeFinish() {
                    isTimeFinish = true;
                    if (clockFlag) {
                        endTime = true;
                        timeEndGetOrderStatue();
                        // clockImg.setImageResource(R.drawable.img_clock_to);
                        navigationbar.setTimerText("00:00");
                        navigationbar.setTimerTextColor(Color.RED);
                    }
                }

                public void onTickCallBack(final String value, final String min, final String sec) {
                    navigationbar.setTimerText(value);
                }

                public void onTickCallBackTo(final String value, final String min, final String sec,
                                             final boolean colorFlag) {
                    navigationbar.setTimerText(value);
                    if (colorFlag) {
                        // clockImg.setImageResource(R.drawable.img_clock_to);
                        navigationbar.setTimerTextColor(Color.RED);
                    } else {
                        // clockImg.setImageResource(R.drawable.img_clock);
                        navigationbar.setTimerTextColor(Color.WHITE);
                    }

                }
            }.start();
        } else {
            payOrderExpire(subContext);
        }
    }

    private TimerCountDown goodsTimerCountDown;

    /**
     * 支付时间-后产品
     */
    protected void orderExpireGoods(final Context context) {
        subContext = context;
        if (payLongTimeRemaining > 0L) {
            if (goodsTimerCountDown != null) {
                goodsTimerCountDown.cancel();
                goodsTimerCountDown = null;
            }
            goodsTimerCountDown = new TimerCountDown(payLongTimeRemaining) {

                public void onTimeFinish() {
                    isTimeFinish = true;
                    if (clockFlag) {
                        endTime = true;
                        AbstractPayActivity.this.payOrderExpire(subContext);
                        navigationbar.setTimerText("00:00:00");
                        navigationbar.setTimerTextColor(Color.RED);
                    }
                }

                public void onTickCallBack(final String value, final String min, final String sec) {
                    String hourStr = "";
                    String minStr = "";
                    int m = Integer.parseInt(min);
                    int hour = 0;
                    if (m >= 60) {
                        hour = m / 60;
                        m = m % 60;
                    }
                    if (hour < 10) {
                        hourStr = "0" + hour + ":";
                    } else {
                        hourStr = hour + ":";
                    }
                    if (m < 10) {
                        minStr = "0" + m + ":";
                    } else {
                        minStr = m + ":";
                    }
                    navigationbar.setTimerText(hourStr + minStr + sec);
                }

                public void onTickCallBackTo(final String value, final String min, final String sec,
                                             final boolean colorFlag) {
                    String hourStr = "";
                    String minStr = "";
                    int m = Integer.parseInt(min);
                    int hour = 0;
                    if (m >= 60) {
                        hour = m / 60;
                        m = m % 60;
                    }
                    if (hour < 10) {
                        hourStr = "0" + hour + ":";
                    } else {
                        hourStr = hour + ":";
                    }
                    if (m < 10) {
                        minStr = "0" + m + ":";
                    } else {
                        minStr = m + ":";
                    }
                    navigationbar.setTimerText(hourStr + minStr + sec);
                    if (colorFlag) {
                        navigationbar.setTimerTextColor(Color.RED);
                    } else {
                        navigationbar.setTimerTextColor(Color.WHITE);
                    }

                }
            };
            goodsTimerCountDown.start();
        } else {
            payOrderExpire(subContext);
        }
    }

    /**
     * 显示加载对话框
     */
    protected void showProgressDialog(final Context context, final String message) {
        if (!canShowDlg) {
            return;
        }

        if (null == progressDialog) {
            progressDialog = Utils.createProgressDialog(this, message);
        }

        progressDialog.setMessage(message);
        progressDialog.show();

        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return KeyEvent.KEYCODE_BACK == keyCode;
            }
        });
    }

    /**
     * 显示取消订单对话框
     */
    protected void showCancelOrderDialog() {
        if ((null != mCustomDlg) && !mCustomDlg.isShowing()) {
            mCustomDlg.show();
        } else {
            mCustomDlg = new CustomAlertDlg(this, CustomAlertDlg.TYPE_OK_CANCEL);
            mCustomDlg.setBtnOKListener(new View.OnClickListener() {

                public void onClick(final View v) {
                    AbstractPayActivity.this.cancelOrder();
                }

            });
            mCustomDlg.setBtnCancelListener(new View.OnClickListener() {

                public void onClick(final View v) {
                    mCustomDlg.dismiss();
                }
            });
            mCustomDlg.show();
            mCustomDlg.getTextView().setText(R.string.s_back_to_cancel_order);
        }
    }

    /**
     * 取消订单
     */
    protected void cancelOrder() {
        showProgressDialog(this, this.getString(R.string.cancelOrder));
        // 加载数据
        final RequestCallback cancelOrderCallback = new RequestCallback() {

            public void onSuccess(final Object o) {
                if (o instanceof CancelOrderJsonBean) {
                    final CancelOrderJsonBean result = (CancelOrderJsonBean) o;

                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {

                    }
                    String message;
                    if (result.isSuccess()) { // 取消订单成功，返回选座页面中
                        clockFlag = false;
                        message = AbstractPayActivity.this.getString(R.string.orderCancelSuccess);
                        if (isFromMall) {
                            AbstractPayActivity.this.finish();
                        } else {
                            if (isFromAccount) {
                                AbstractPayActivity.this.finish();
                            } else {
                                // 重新选座
                                AbstractPayActivity.this.intentForwardAnewTicket();
                            }
                        }
                    } else {
                        message = AbstractPayActivity.this.getString(R.string.orderCancelError);
                        if (result.getStatus() == 1) {
                            message = result.getMsg();
                        } else if (result.getStatus() == 2) { // 订单已经取消
                            clockFlag = false;
                            message = AbstractPayActivity.this.getString(R.string.orderCancelOk);
                            AbstractPayActivity.this.intentForwardAnewTicket();
                        }
                    }
                    MToastUtils.showShortToast(message);
                }
            }

            public void onFail(final Exception e) {
                try {
                    progressDialog.dismiss();
                } catch (Exception e1) {

                }

                // 加载数据出错，返回首页
                if (canShowDlg) {
                    showFailDlg(e.getLocalizedMessage());
                }
            }

        };
        Map<String, String> parameterList = new ArrayMap<String, String>(1);
        parameterList.put("orderId", mOrderId);
        HttpUtil.post(ConstantUrl.CANCEL_ORDER, parameterList, CancelOrderJsonBean.class, cancelOrderCallback);
        if (null != mCustomDlg && mCustomDlg.isShowing()) {
            mCustomDlg.dismiss();
        }
    }

    private void showFailDlg(final String msg) {
        final CustomAlertDlg failDlg = new CustomAlertDlg(AbstractPayActivity.this, CustomAlertDlg.TYPE_OK);
        failDlg.show();
        failDlg.getTextView().setText(msg);
        failDlg.getBtnOK().setText(R.string.s_back_to_main);
        failDlg.getBtnOK().setOnClickListener(new OnClickListener() {

            public void onClick(final View v) {
                AppLinkExtKt.openHome();
            }
        });
    }

    /**
     * 重新选座（普通情况下的重新选座，未付款，不是订单状态为40的订单）
     */
    private void intentForwardAnewTicket() {
        final Intent intent = new Intent();
        intent.putExtra(App.getInstance().KEY_MOVIE_NAME, mMovieName); // 影片名
        intent.putExtra(App.getInstance().KEY_MOVIE_NAME, mMovieName);
        intent.putExtra(App.getInstance().KEY_CINEMA_NAME, mCinemaName);
        intent.putExtra(App.getInstance().KEY_CINEMA_PHONE, mCinemaPhone);
        intent.putExtra(App.getInstance().KEY_SEATING_SEAT_ID, mSeatId);

        // 当有未支付订单时getOrderId()和getSubOrderID()的值才大于0
        intent.putExtra(App.getInstance().KEY_SEATING_LAST_ORDER_ID, mOrderId); // 传入mOrderId作为重新选座时用到的“上一个id”
        intent.putExtra(App.getInstance().KEY_SEATING_SUBORDER_ID, mSubOrderId);
        intent.putExtra(App.getInstance().KEY_SEAT_SELECTED_INFO, mSeatSelectedInfo);
        intent.putExtra(App.getInstance().KEY_SEATING_SELECT_AGAIN, false);// 不需要重新选座
        // 以下4个数据需要带回来选座页（更换场次要用到）
        intent.putExtra(App.getInstance().KEY_SEATING_DID, mDId);
        intent.putExtra(App.getInstance().KEY_MOVIE_ID, mMovieId);
        intent.putExtra(App.getInstance().KEY_CINEMA_ID, mCinemaId);
        intent.putExtra(App.getInstance().KEY_SHOWTIME_DATE, mDate);

//        this.startActivity(SeatSelectActivity.class, intent);

        JavaOpenSeatActivity.INSTANCE.openSeatActivity(mDId, mOrderId, mMovieId, mCinemaId, mDate);

        // MtimeUtils.startActivityWithNetworkCheck(this,
        // Constant.ACTIVITY_MOVIE_TICKET_SEATING, i);
        finish();
    }

    /**
     * 订单过期，订单支付时间到了
     */
    protected void payOrderExpire(final Context context) {

        mBuyAgainDlg = new CustomAlertDlg(AbstractPayActivity.this, CustomAlertDlg.TYPE_OK);
        mBuyAgainDlg.setBtnOKListener(new View.OnClickListener() {

            public void onClick(final View v) {
                mBuyAgainDlg.dismiss();
                if (isFromMall) {
                    finish();
                } else {
                    if (isFromAccount) {
                        ITicketOrderProvider ticketOrderProvider = ProviderExtKt.getProvider(ITicketOrderProvider.class);
                        if (ticketOrderProvider != null) {
                            ticketOrderProvider.startTicketOrderListActivity(AbstractPayActivity.this);
                        }
                        finish();
                    } else {
                        // 重新选座
                        AbstractPayActivity.this.intentForwardAnewTicket();
                    }
                }
            }
        });
        mBuyAgainDlg.show();
        mBuyAgainDlg.setCancelable(false);
        if (isFromMall) {
            mBuyAgainDlg.setText(context.getString(R.string.str_mall_pay_overtime_retry));
            mBuyAgainDlg.getBtnOK().setText(R.string.str_mall_buy_retry);
        } else {
            mBuyAgainDlg.setText(context.getString(R.string.s_pay_overtime_retry));
            if (isFromAccount) {
                mBuyAgainDlg.getBtnOK().setText(R.string.str_back);
            } else {
                mBuyAgainDlg.getBtnOK().setText(R.string.str_reselect_seats);
            }
        }

    }

    /**
     * 下订单失败
     */
    protected void createOrderFail(final String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(AbstractPayActivity.this);
        builder.setTitle(this.getString(R.string.prompt));
        builder.setMessage(message);
        builder.setPositiveButton(this.getString(R.string.afreshTicket), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, final int which) {
                dialog.dismiss();

                clockFlag = false;
                final Intent intent = new Intent();
                intent.putExtra(App.getInstance().KEY_MOVIE_NAME, mMovieName); // 影片名
                intent.putExtra(App.getInstance().KEY_SEATING_DID, mDId);
                intent.putExtra(App.getInstance().KEY_MOVIE_NAME, mMovieName);
                intent.putExtra(App.getInstance().KEY_CINEMA_NAME, mCinemaName);
                intent.putExtra(App.getInstance().KEY_CINEMA_PHONE, mCinemaPhone);
                intent.putExtra(App.getInstance().KEY_SEATING_SELECT_AGAIN, false);// 不需要重新选座

//                AbstractPayActivity.this.startActivity(SeatSelectActivity.class, intent);

                JavaOpenSeatActivity.INSTANCE.openSeatActivity(mDId, null, null, null, null);

                // MtimeUtils.startActivityWithNetworkCheck(AbstractPayActivity.this,Constant.ACTIVITY_MOVIE_TICKET_SEATING,
                // i);
                AbstractPayActivity.this.finish();
            }
        });
        final Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * 跳转到支付成功页
     *
     * @param context
     * @param cinemaName 影院名称
     * @param movieName  电影名称
     * @param moviePrice 电影票单价
     * @param cPrice     总价
     * @param message    成功提示信息
     */
    protected void gotoPaySuccessActivity(final Context context, final String cinemaName, final String movieName,
                                          final int ticketCount, final double moviePrice, final double cPrice, final String message,
                                          final String seatSelectedInfo, final boolean isEticket) {
        clockFlag = false;
        final Intent intent = new Intent();
        intent.putExtra(App.getInstance().KEY_SEATING_ORDER_ID, mOrderId);// 影院名称
        /*
         * intent.putExtra(Constant.KEY_SEATING_DID, mDId);
         * intent.putExtra(Constant.KEY_SEATING_SUBORDER_ID, mSubOrderId);
         * intent.putExtra(Constant.KEY_SEAT_SELECTED_INFO, mSeatSelectedInfo);
         * intent.putExtra(Constant.KEY_TICKET_DATE_INFO, mTicketDateInfo);
         * intent.putExtra(Constant.KEY_CINEMA_NAME, cinemaName);
         * intent.putExtra(Constant.KEY_MOVIE_NAME, movieName);
         * intent.putExtra(Constant.KEY_BUY_TICKET_COUNT, ticketCount);
         * intent.putExtra(Constant.TICKET_PRICE, moviePrice);
         * intent.putExtra(Constant.KEY_SEATING_TOTAL_PRICE, cPrice);
         * intent.putExtra(Constant.PAY_MESSAGE, message);
         */
        // intent.putExtra(Constant.PAY_ETICKET, isEticket); // 是否是电子券
        intent.putExtra(App.getInstance().PAY_ETICKET, isEticket);
        intent.putExtra(App.getInstance().KEY_TARGET_NOT_VIP, isnotVip);
        intent.putExtra(App.getInstance().KEY_TARGET_NOT_VIP_PHONE, notVipphoneNum);
        intent.putExtra(App.getInstance().KEY_TARGET_ACTIVITY_ID, OrderPayActivity.class.getName());
        this.startActivity(OrderPaySuccessActivity.class, intent);
        App.getInstance().getPrefsManager().putBoolean(App.getInstance().HAS_BOUGHT_MOVIES_REMIND, true);

        // MtimeUtils.startActivityWithNetworkCheck(this,Constant.ACTIVITY_MOVIE_PAY_SUCCESS,
        // intent);
    }

    // TODO: 2019-09-19 没有调用的地方，去掉 wwl

//    /**
//     * 重新选座--订单状态为40的订单 付款成功,但票没有出来（可能过期）/重复支付，请联系管理员退款
//     *
//     * @param context
//     * @param cinemaName 影院名称
//     * @param movieName  电影名称
//     * @param movieCount 电影票数量
//     * @param moviePrice 电影票单价
//     */
//    protected void payMoneySuccess(final Context context, final String cinemaName, final String movieName,
//                                   final int movieCount, final double moviePrice, final double totalPrice, final String message) {
//        clockFlag = false;
//        final Intent intent = new Intent();
//        intent.putExtra(App.getInstance().KEY_SEATING_ORDER_ID, mOrderId);
//        intent.putExtra(App.getInstance().KEY_SEATING_LAST_ORDER_ID, mOrderId); // 传入mOrderId作为重新选座时用到的“上一个id”
//        intent.putExtra(App.getInstance().KEY_SEATING_DID, mDId);
//        intent.putExtra(App.getInstance().KEY_SEATING_SUBORDER_ID, mSubOrderId);
//        intent.putExtra(App.getInstance().KEY_SEAT_SELECTED_INFO, mSeatSelectedInfo);
//        intent.putExtra(App.getInstance().KEY_CINEMA_NAME, cinemaName); // 影院名称
//        intent.putExtra(App.getInstance().KEY_MOVIE_NAME, movieName); // 电影名称
//        intent.putExtra(App.getInstance().KEY_CINEMA_PHONE, mCinemaPhone);
//        intent.putExtra(App.getInstance().KEY_USER_BUY_TICKET_PHONE, mUserBuyTicketPhone);
//        intent.putExtra(App.getInstance().KEY_LOCATION_ID, movieCount); // 电影票数量
//        intent.putExtra(App.getInstance().TICKET_PRICE, moviePrice); // 电影票单价
//        intent.putExtra(App.getInstance().KEY_SEATING_TOTAL_PRICE, totalPrice); // 总价
//        intent.putExtra(App.getInstance().PAY_MONEY, true); // 标识
//        intent.putExtra(App.getInstance().PAY_MESSAGE, message); // 提示信息
//        intent.putExtra(App.getInstance().PAY_ETICKET, mIsEticket); // 是否是电子券
//
//        // 重新选座
//        intent.putExtra(App.getInstance().KEY_SEATING_SELECT_AGAIN, true);// 需要重新选座
//        intent.putExtra(App.getInstance().KEY_SEATING_SEAT_ID, mSeatId);
//        intent.putExtra(App.getInstance().KEY_SEATING_SELECTED_SEAT_COUNT, mSelectedSeatCount); // 座位数
//        this.startActivity(SeatSelectActivity.class, intent);
//        // MtimeUtils.startActivityWithNetworkCheck(this,Constant.ACTIVITY_MOVIE_PAY_SUCCESS,
//        // intent);
//    }

    protected void onDestroy() {
        if (null != mCustomDlg && mCustomDlg.isShowing()) {
            mCustomDlg.dismiss();
        }
        mCustomDlg = null;
        clockFlag = false;

        Constants.isShowRegisterGiftDlg = true;
        super.onDestroy();
    }

    // 查看订单状态
    protected void timeEndGetOrderStatue() {
        showProgressDialog(AbstractPayActivity.this, "正在加载...");
        RequestCallback getOrderStatueCallback = new RequestCallback() {

            @Override
            public void onSuccess(Object o) {

                final OrderStatusJsonBean order = (OrderStatusJsonBean) o;
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if (order.getPayStatus() == 1 && order.getOrderStatus() == 30) { // 1 是成功 轮巡主订单
                    clockFlag = false;
                    gotoPaySuccessActivity(AbstractPayActivity.this, AbstractPayActivity.this.mCinemaName,
                            AbstractPayActivity.this.mMovieName, 0, AbstractPayActivity.this.mUnitPrice, AbstractPayActivity.this.mTotalPrice,
                            AbstractPayActivity.this.getString(R.string.payOrder), AbstractPayActivity.this.mSeatSelectedInfo, AbstractPayActivity.this.mIsEticket);
                    AbstractPayActivity.this.finish();
                } else {
                    payOrderExpire(subContext);
                }
            }

            @Override
            public void onFail(Exception e) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                payOrderExpire(subContext);
            }
        };
        Map<String, String> parameterList = new ArrayMap<String, String>(1);
        parameterList.put("orderId", mOrderId);
        HttpUtil.post(ConstantUrl.GET_ORDER_STATUS, parameterList, OrderStatusJsonBean.class, getOrderStatueCallback);
    }


}
