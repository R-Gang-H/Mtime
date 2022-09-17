package com.mtime.bussiness.ticket.movie.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.collection.ArrayMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.kotlin.android.app.router.ext.AppLinkExtKt;
import com.kotlin.android.film.JavaOpenSeatActivity;
import com.mtime.bussiness.main.MainTabActivity;
import com.mtime.frame.App;
import com.mtime.R;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.ticket.movie.bean.CancelOrderJsonBean;
import com.mtime.payment.AbstractPayActivity;
import com.mtime.constant.Constants;
import com.mtime.util.HttpUtil;
import com.mtime.network.RequestCallback;
import com.mtime.common.utils.Utils;
import com.mtime.util.JumpUtil;
import com.mtime.widgets.BaseTitleView.StructType;
import com.mtime.widgets.TitleOfNormalView;
import com.mtime.network.ConstantUrl;
import com.mtime.util.CustomAlertDlg;

import java.util.Map;

/**
 * 支付失败页(报错页)
 */
public class OrderPayFailedActivity extends AbstractPayActivity {
    private TextView         tv_error_title;
    private TextView         tv_error_detail;
    private TextView           btn_reselect;
    private TextView           btn_cancel_order;
    
    private ProgressDialog   mDialog;
    public static final int  ERROR_TYPE_RESELECT_SEAT  = 0;
    public static final int  ERROR_TYPE_RESELECT_PAY   = 1;
    public static final int  ERROR_TYPE_CHECK_ACCOUNT  = 2;
    public static final int  ERROR_TYPE_PASSWORD_ERROR = 3;
    public static final int  ERROR_TYPE_PAY_TIME_OUT   = 4;
    public static final int  ERROR_TYPE_GO_HOMEPAGE    = 5;
    public static final int  ERROR_TYPE_RESELECT_SEAT_AND_CANCEL_ORDER  = 6;
    public static final int  ERROR_TYPE_RESELECT_SEAT_AND_BACK_TO_HOME  = 7;



    private int              mErrorType;
    private String           mErrorTitle;
    private String           mErrorDetail;
    private String           mErrorButtonMessage;
    private static final int DIALOG_CANCEL_ORDER       = 0;
    
    @Override
    protected void onInitVariable() {
        
        mDialog = Utils.createProgressDialog(this, "正在取消订单...");
        
        final Intent intent = getIntent();
        mIsEticket = intent.getBooleanExtra(App.getInstance().PAY_ETICKET, false); // 是否是购买电子券
        mOrderId = intent.getStringExtra(App.getInstance().KEY_SEATING_ORDER_ID);
        mErrorType = intent.getIntExtra(App.getInstance().KEY_PAY_ERROR_TYPE, -1);
        mErrorTitle = intent.getStringExtra(App.getInstance().KEY_PAY_ERROR_TITLE);
        mErrorDetail = intent.getStringExtra(App.getInstance().KEY_PAY_ERROR_DETAIL);
        mErrorButtonMessage = intent.getStringExtra(App.getInstance().KEY_PAY_ERROR_BUTTON_MESSAGE);

        setPageLabel("orderPayFailed");
    }
    
    @Override
    protected void onInitView(final Bundle savedInstanceState) {
        this.setContentView(R.layout.act_order_pay_failed);
        View navBar = this.findViewById(R.id.navigationbar);
        navigationbar = new TitleOfNormalView(this, navBar, StructType.TYPE_NORMAL_SHOW_BACK_TITLE,
                getResources().getString(R.string.str_order_detail), null);
        tv_error_title = findViewById(R.id.order_pay_failed_tv_error_title);
        tv_error_detail = findViewById(R.id.order_pay_failed_tv_error_detail);
        btn_reselect = findViewById(R.id.order_pay_failed_btn_reselect);
        btn_cancel_order = findViewById(R.id.order_pay_failed_btn_cancel_order);
        
        initViewValues();
    }
    
    private void initViewValues() {
        tv_error_title.setText(mErrorTitle);
        tv_error_detail.setText(mErrorDetail);
        if (mErrorButtonMessage == null || mErrorButtonMessage.equals("")) {
            btn_reselect.setVisibility(View.GONE);
        }
        else {
            btn_reselect.setText(mErrorButtonMessage);
            btn_reselect.setVisibility(View.VISIBLE);
        }
        
        // 如果同一订单两次进入这个页面，则显示“取消订单”按钮
        if (App.getInstance().lastPayFailedOrderId.equals(mOrderId)) {
            btn_cancel_order.setVisibility(View.VISIBLE);
        }
        App.getInstance().lastPayFailedOrderId = mOrderId; // 记录本次支付失败的订单id
    }
    
    @Override
    protected void onInitEvent() {
        // 界面跳转
        btn_reselect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doJump();
            }
        });
        // 取消订单
        btn_cancel_order.setOnClickListener(new OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                showDialog(OrderPayFailedActivity.DIALOG_CANCEL_ORDER);
            }
        });
    }
    
    @Override
    protected void onLoadData() {
        
    }
    
    @Override
    protected void onUnloadData() {
        
    }
    
    @Override
    protected void onRequestData() {
        
    }
    
    @SuppressWarnings("deprecation")
    @Override
    protected Dialog onCreateDialog(final int id) {
        switch (id) {
            case DIALOG_CANCEL_ORDER:
                final CustomAlertDlg dlg = new CustomAlertDlg(this, CustomAlertDlg.TYPE_OK_CANCEL);
                dlg.setBtnOKListener(new View.OnClickListener() {
                    
                    @Override
                    public void onClick(final View v) {
                        dlg.dismiss();
                        doCancelOrder();
                    }
                    
                });
                dlg.setBtnCancelListener(new View.OnClickListener() {
                    
                    @Override
                    public void onClick(final View v) {
                        dlg.dismiss();
                    }
                });
                dlg.show();
                dlg.getBtnOK().setText("取消订单");
                dlg.getBtnCancel().setText("返回");
                dlg.getTextView().setText("确定取消当前订单？");
                return dlg;
                
            default:
                break;
        }
        
        return super.onCreateDialog(id);
    }
    
    private void doJump() {
        final Intent intent = new Intent();
        switch (mErrorType) {
            case ERROR_TYPE_RESELECT_SEAT_AND_CANCEL_ORDER:
                // 取消订单并且重新选座
                doCancelOrderAndReselect();
                break;
            case ERROR_TYPE_RESELECT_SEAT_AND_BACK_TO_HOME:
                // 取消订单并且回到首页
                doCancelOrderAndBackToHomeActivity();
                break;
            case ERROR_TYPE_RESELECT_SEAT:
                // 重新选座
                intent.putExtra(App.getInstance().KEY_SEATING_SELECT_AGAIN, true);
                intent.putExtra(App.getInstance().KEY_SEATING_LAST_ORDER_ID, mOrderId);
                intent.putExtra(App.getInstance().KEY_SEATING_DID, mDId);
//                this.startActivity(SeatSelectActivity.class, intent);

                JavaOpenSeatActivity.INSTANCE.openSeatActivity(mDId, mOrderId, null, null, null);

                finish();
                break;
            
            case ERROR_TYPE_RESELECT_PAY:
                // 重新选择支付方式
                break;
            
            case ERROR_TYPE_CHECK_ACCOUNT:
                // 查看我的账户
                break;
            case ERROR_TYPE_PASSWORD_ERROR:
            case ERROR_TYPE_PAY_TIME_OUT:
                // 两种情况做同样处理
                // 返回上一页（支付页），重新输入密码
                finish();
                break;
            case ERROR_TYPE_GO_HOMEPAGE:
                // 回首页
                AppLinkExtKt.openHome();
                finish();
                break;
            
            default:
                break;
        }
    }

    private void doCancelOrderAndReselect() {
        final RequestCallback cancelOrderCallback = new RequestCallback() {
            @Override
            public void onSuccess(final Object o) {
                if (null != mDialog && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                if (o instanceof CancelOrderJsonBean) {
                    final CancelOrderJsonBean result = (CancelOrderJsonBean) o;
                    String message;
                    if (result.isSuccess()) {
                        // 取消订单成功
                        MToastUtils.showLongToast( OrderPayFailedActivity.this.getString(R.string.orderCancelSuccess));
                        final Intent intent = new Intent();
                        intent.putExtra(App.getInstance().KEY_SEATING_SELECT_AGAIN, false);
                        intent.putExtra(App.getInstance().KEY_SEATING_LAST_ORDER_ID, mOrderId);
                        intent.putExtra(App.getInstance().KEY_SEATING_DID, mDId);
//                        OrderPayFailedActivity.this.startActivity(SeatSelectActivity.class, intent);

                        JavaOpenSeatActivity.INSTANCE.openSeatActivity(mDId, mOrderId, null, null, null);

                        OrderPayFailedActivity.this.finish();
                    }
                    else {
                        message = OrderPayFailedActivity.this.getString(R.string.orderCancelError);
                        if (result.getStatus() == 1) {
                            message = result.getMsg();
                        }
                        else if (result.getStatus() == 2) { // 订单已经取消
                            message = OrderPayFailedActivity.this.getString(R.string.orderCancelOk);
                        }
                        MToastUtils.showLongToast(message);
                    }

                }
            }

            @Override
            public void onFail(final Exception e) {
                if (null != mDialog && mDialog.isShowing()) {
                    mDialog.dismiss();
                }

                if (!OrderPayFailedActivity.this.isFinishing()) {
                    final AlertDialog alertDlg = Utils.createDlg(OrderPayFailedActivity.this,
                            OrderPayFailedActivity.this.getString(R.string.str_error),
                            OrderPayFailedActivity.this.getString(R.string.str_load_error));
                    alertDlg.show();
                }
            }
        };

        mDialog.show();
        Map<String, String> parameterList = new ArrayMap<String, String>(1);
        parameterList.put("orderId", mOrderId);
        HttpUtil.post(ConstantUrl.CANCEL_ORDER, parameterList, CancelOrderJsonBean.class, cancelOrderCallback);
    }


    private void doCancelOrderAndBackToHomeActivity() {
        final RequestCallback cancelOrderCallback = new RequestCallback() {
            @Override
            public void onSuccess(final Object o) {
                if (null != mDialog && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                if (o instanceof CancelOrderJsonBean) {
                    final CancelOrderJsonBean result = (CancelOrderJsonBean) o;
                    String message;
                    if (result.isSuccess()) {
                        // 取消订单成功
                        MToastUtils.showLongToast( OrderPayFailedActivity.this.getString(R.string.orderCancelSuccess));
                        final Intent intent = new Intent();
                        intent.putExtra(App.getInstance().KEY_SEATING_SELECT_AGAIN, false);
                        intent.putExtra(App.getInstance().KEY_SEATING_LAST_ORDER_ID, mOrderId);
                        intent.putExtra(App.getInstance().KEY_SEATING_DID, mDId);
                        intent.putExtra(Constants.KEY_MAIN_TAB_INDEX, 4);
                        OrderPayFailedActivity.this.startActivity(MainTabActivity.class, intent);
                        OrderPayFailedActivity.this.finish();
                    }
                    else {
                        message = OrderPayFailedActivity.this.getString(R.string.orderCancelError);
                        if (result.getStatus() == 1) {
                            message = result.getMsg();
                        }
                        else if (result.getStatus() == 2) { // 订单已经取消
                            message = OrderPayFailedActivity.this.getString(R.string.orderCancelOk);
                        }
                        MToastUtils.showLongToast( message);
                    }

                }
            }

            @Override
            public void onFail(final Exception e) {
                if (null != mDialog && mDialog.isShowing()) {
                    mDialog.dismiss();
                }

                if (!OrderPayFailedActivity.this.isFinishing()) {
                    final AlertDialog alertDlg = Utils.createDlg(OrderPayFailedActivity.this,
                            OrderPayFailedActivity.this.getString(R.string.str_error),
                            OrderPayFailedActivity.this.getString(R.string.str_load_error));
                    alertDlg.show();
                }
            }
        };

        mDialog.show();
        Map<String, String> parameterList = new ArrayMap<String, String>(1);
        parameterList.put("orderId", mOrderId);
        HttpUtil.post(ConstantUrl.CANCEL_ORDER, parameterList, CancelOrderJsonBean.class, cancelOrderCallback);
    }


    private void doCancelOrder() {
        final RequestCallback cancelOrderCallback = new RequestCallback() {
            @Override
            public void onSuccess(final Object o) {
                if (null != mDialog && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                if (o instanceof CancelOrderJsonBean) {
                    final CancelOrderJsonBean result = (CancelOrderJsonBean) o;
                    String message;
                    if (result.isSuccess()) {
                        // 取消订单成功，返回首页
                        MToastUtils.showLongToast( OrderPayFailedActivity.this.getString(R.string.orderCancelSuccess));
                        AppLinkExtKt.openHome();
                        OrderPayFailedActivity.this.finish();
                    }
                    else {
                        message = OrderPayFailedActivity.this.getString(R.string.orderCancelError);
                        if (result.getStatus() == 1) {
                            message = result.getMsg();
                        }
                        else if (result.getStatus() == 2) { // 订单已经取消
                            message = OrderPayFailedActivity.this.getString(R.string.orderCancelOk);
                        }
                        MToastUtils.showLongToast(message);
                    }
                    
                }
            }
            
            @Override
            public void onFail(final Exception e) {
                if (null != mDialog && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                
                if (!OrderPayFailedActivity.this.isFinishing()) {
                    final AlertDialog alertDlg = Utils.createDlg(OrderPayFailedActivity.this,
                            OrderPayFailedActivity.this.getString(R.string.str_error),
                            OrderPayFailedActivity.this.getString(R.string.str_load_error));
                    alertDlg.show();
                }
            }
        };
        
        mDialog.show();
        Map<String, String> parameterList = new ArrayMap<String, String>(1);
        parameterList.put("orderId", mOrderId);
        HttpUtil.post(ConstantUrl.CANCEL_ORDER, parameterList, CancelOrderJsonBean.class, cancelOrderCallback);
    }
    
    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        finish();
        return true;
    }
}
