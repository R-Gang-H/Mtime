package com.mtime.bussiness.mine.login.widget;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.mine.api.MineApi;
import com.mtime.bussiness.mine.login.bean.CapchaBean;
import com.mtime.common.utils.TextUtil;
import com.mtime.util.CapchaDlg;
import com.mtime.util.CustomAlertDlg;
import com.mtime.util.UIUtil;
import com.mtime.widgets.TimerCountDown;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author vivian.wei
 * @date 2019/8/14
 * @desc 短信验证码登录组件
 */
public class LoginSmsCodeView implements View.OnClickListener {

    public static final int ACCOUNT_TYPE_PHONE = 1;
    public static final int ACCOUNT_TYPE_EMAIL = 2;

    @BindView(R.id.login_sms_code_view_phone_number_et)
    EditText mPhoneNumberEt;
    @BindView(R.id.login_sms_code_view_phone_number_cancel_iv)
    ImageView mPhoneNumberCancelIv;
    @BindView(R.id.login_sms_code_view_code_et)
    EditText mSmsCodeEt;
    @BindView(R.id.login_sms_code_view_send_code_tv)
    TextView mSendSmsCodeTv;
    @BindView(R.id.login_sms_code_view_sms_code_cancel_iv)
    ImageView mSmsCodeCancelIv;
    @BindView(R.id.login_sms_code_view_send_code_line_view)
    View mSendSmsCodeLineView;
    @BindView(R.id.login_sms_code_view_line_view)
    View mLineView;
    @BindView(R.id.login_sms_code_view_tip_tv)
    TextView mTipTv;
    @BindView(R.id.login_sms_code_view_login_btn)
    TextView mLoginBtn;

    private final Context mContext;
    private final View mRoot;
    private int mAccountType = ACCOUNT_TYPE_PHONE;
    private final long mTimeRemaining = 60000L;
    private boolean mShowAlert = false;
    private MineApi mMineApi;
    private String mVCodeId;
    private String mSmsCodeId;
    private CapchaDlg mCapchaDlg;
    private CustomAlertDlg mCustomAlertDlg;
    private TimerCountDown mTimer;
    private final ILoginSmsCodeViewClickListener mListener;
    private OnEditTextChangeListener mOnEditTextChangeListener;

    public LoginSmsCodeView(Context context, View root, ILoginSmsCodeViewClickListener listener) {
        mContext = context;
        mRoot = root;
        mListener = listener;
        if(mMineApi == null) {
            mMineApi = new MineApi();
        }
        initView();
        initEvent();
    }

    private void initView() {
        ButterKnife.bind(this, mRoot);

        mPhoneNumberCancelIv.setVisibility(View.INVISIBLE);
        mSmsCodeCancelIv.setVisibility(View.INVISIBLE);
        mSendSmsCodeLineView.setVisibility(View.INVISIBLE);
    }

    // 初始化事件
    private void initEvent() {
        mPhoneNumberEt.addTextChangedListener(new AccountTextWatcher());
        mPhoneNumberCancelIv.setOnClickListener(this);
        mSendSmsCodeTv.setOnClickListener(this);
        mSmsCodeCancelIv.setOnClickListener(this);
        mLoginBtn.setOnClickListener(this);

        mSmsCodeEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String smsCode = s.toString().trim();
                mSmsCodeCancelIv.setVisibility(TextUtils.isEmpty(smsCode) ? View.INVISIBLE : View.VISIBLE);
                mSendSmsCodeLineView.setVisibility(TextUtils.isEmpty(smsCode) ? View.INVISIBLE : View.VISIBLE);
                if(mOnEditTextChangeListener != null) {
                    mOnEditTextChangeListener.onEditTextChange(mSmsCodeEt);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.login_sms_code_view_phone_number_cancel_iv:
                // 点击手机号输入框取消按钮
                mPhoneNumberEt.getEditableText().clear();
                mPhoneNumberCancelIv.setVisibility(View.INVISIBLE);
                break;
            case R.id.login_sms_code_view_sms_code_cancel_iv:
                // 点击手机验证码输入框取消按钮
                mSmsCodeEt.getEditableText().clear();
                mSmsCodeCancelIv.setVisibility(View.INVISIBLE);
                mSendSmsCodeLineView.setVisibility(View.INVISIBLE);
                break;
            case R.id.login_sms_code_view_send_code_tv:
                // 点击获取验证码按钮
                String phone = getPhoneNumber();
                String error = "";
                if(mAccountType == ACCOUNT_TYPE_PHONE) {
                    if (TextUtils.isEmpty(phone)) {
                        error = mContext.getResources().getString(R.string.register_phone_validatemobileempty);
                    } else if (!TextUtil.isMobileNO(phone)) {
                        error = mContext.getResources().getString(R.string.register_phone_validatemobile);
                    }
                } else if(mAccountType == ACCOUNT_TYPE_EMAIL && TextUtils.isEmpty(phone)) {
                    error = "请输入邮箱地址";
                }
                if(!TextUtils.isEmpty(error)) {
                    showAlertDlg(error, null);
                    return;
                }

                mShowAlert = false;
                if(mListener != null) {
                    mListener.onSendSmsBtnClick(phone, "", "");
                }
                break;
            case R.id.login_sms_code_view_login_btn:
                // 点击登录按钮
                // 校验
                String message = validate();
                if (!TextUtils.isEmpty(message)) {
                    showAlertDlg(message, null);
                    return;
                }
                if (mListener != null) {
                    if (mCapchaDlg != null) {
                        mCapchaDlg.hide();
                    }
                    // vivo手机登录成功跳转到来源页，软键盘不能自动收起
                    hideSoftkeyBoard();
                    mListener.onLoginBtnClick(getPhoneNumber(), getSmsCode(), mSmsCodeId);
                }
                break;

            default:
                break;
        }
    }

    // 显示图片验证码弹窗
    private void showCapchaDlg(String vCodeId, String vCodeUrl) {
        if (mCapchaDlg != null) {
            mCapchaDlg.dismiss();
            mCapchaDlg = null;
        }

        mVCodeId = vCodeId;
        mCapchaDlg = new CapchaDlg(mContext, CapchaDlg.TYPE_OK_CANCEL, R.layout.dialog_capcha_login);
        mCapchaDlg.setBackgroundDimAmount(0.12f);
        mCapchaDlg.setCanceledOnTouchOutside(false);

        // 点击确认按钮
        mCapchaDlg.setBtnOKListener((v) -> {
            String imgCode = mCapchaDlg.getEditView().getText().toString();
            if (TextUtils.isEmpty(imgCode)) {
                showAlertDlg("请输入图片验证码后再获取短信验证码", null);
                return;
            }
            // 发送短信验证码
            if(mListener != null) {
                mListener.onSendSmsBtnClick(getPhoneNumber(), mVCodeId, imgCode);
            }
            mCapchaDlg.hide();
        });

        // 点击取消按钮
        mCapchaDlg.setBtnCancelListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                mCapchaDlg.hide();
            }
        });

        // 点击图片
        mCapchaDlg.setCapchaTextListener((v) -> {
            // 请求新的图片验证码
            requestImgCode();
        });

        mCapchaDlg.show();
        ImageHelper.with()
                .view(mCapchaDlg.getImageView())
                .load(vCodeUrl)
                .asGif()
                .showload();
    }

    // 请求新的图片验证码
    private void requestImgCode() {
        UIUtil.showLoadingDialog(mContext);
        mMineApi.getImageVerifyCode(new NetworkManager.NetworkListener<CapchaBean>() {
            @Override
            public void onSuccess(CapchaBean result, String showMsg) {
                UIUtil.dismissLoadingDialog();
                mVCodeId = result.getCodeId();
                ImageHelper.with()
                        .view(mCapchaDlg.getImageView())
                        .load(result.getUrl())
                        .asGif()
                        .showload();
            }

            @Override
            public void onFailure(NetworkException<CapchaBean> exception, String showMsg) {
                UIUtil.dismissLoadingDialog();
                MToastUtils.showShortToast("获取图片验证码失败：" + showMsg);
            }
        });
    }

    // 倒计时
    private void timer() {

        mTimer = new TimerCountDown(mTimeRemaining) {

            @Override
            public void onTimeFinish() {
                cancelTimer();
            }

            @Override
            public void onTickCallBack(final String value, final String min, final String sec) {
            }

            @Override
            public void onTickCallBackTo(final String value, final String min, final String sec, final boolean flag) {
                if (sec.equals("0")) {
                    cancelTimer();
                } else {
                    mSendSmsCodeTv.setTextColor(ContextCompat.getColor(mContext, R.color.color_8798AF));
                    mSendSmsCodeTv.setBackgroundResource(R.drawable.shape_login_send_sms_code_false_bg);
                    mSendSmsCodeTv.setClickable(false);
                    mSendSmsCodeTv.setEnabled(false);
                    mSendSmsCodeTv.setText(sec + "秒后重发");
                }
            }
        };
        mTimer.start();
    }

    // 取消倒计时
    private void cancelTimer() {
        if (null != mTimer) {
            mTimer.cancel();
        }
        mTimer = null;

        mSendSmsCodeTv.setClickable(true);
        mSendSmsCodeTv.setEnabled(true);
        mSendSmsCodeTv.setText("重发验证码");
        mSendSmsCodeTv.setTextColor(ContextCompat.getColor(mContext, R.color.color_20A0DA));
        mSendSmsCodeTv.setBackgroundResource(R.drawable.shape_login_send_sms_code_bg);
    }

    // 手机号
    private String getPhoneNumber() {
        return mPhoneNumberEt.getText().toString().trim();
    }

    // 短信验证码
    private String getSmsCode() {
        return mSmsCodeEt.getText().toString().trim();
    }

    // 校验
    private String validate() {
        String phone = getPhoneNumber();
        String smsCode = getSmsCode();
        if(mAccountType == ACCOUNT_TYPE_PHONE) {
            if (TextUtils.isEmpty(phone)) {
                return mContext.getResources().getString(R.string.register_phone_validatemobileempty);
            } else if (!TextUtil.isMobileNO(phone)) {
                return mContext.getResources().getString(R.string.register_phone_validatemobile);
            }
        }
        if(mAccountType == ACCOUNT_TYPE_EMAIL && TextUtils.isEmpty(phone)) {
            // 找回密码页用
            return "请输入邮箱地址";
        }
        if (TextUtils.isEmpty(smsCode)) {
            return mContext.getResources().getString(R.string.register_phone_validatecodeempty);
        }
        return "";
    }

    // 显示确认弹窗
    private void showAlertDlg(String message, ISendSmsCodeListener sendSmsCodeListener) {
        mCustomAlertDlg = new CustomAlertDlg(mContext, CustomAlertDlg.TYPE_OK);
        mCustomAlertDlg.setBtnOKListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mCustomAlertDlg.dismiss();

                if (null != sendSmsCodeListener) {
                    sendSmsCodeListener.onImageCodeError();
                }
            }
        });

        mCustomAlertDlg.show();
        mCustomAlertDlg.getTextView().setText(message);
    }

    // 发送短信验证码Listener
    private interface ISendSmsCodeListener {
        // 图片验证码错误
        void onImageCodeError();
    }

    // 隐藏软键盘
    private void hideSoftkeyBoard() {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(mSmsCodeEt.getWindowToken(), 0);
        }
    }

    // 手机号输入框监控
    private class AccountTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
        }

        @Override
        public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
        }

        @Override
        public void afterTextChanged(final Editable s) {
            String label = s.toString();
            mPhoneNumberCancelIv.setVisibility(TextUtils.isEmpty(label) ? View.INVISIBLE : View.VISIBLE);
            if(mOnEditTextChangeListener != null) {
                mOnEditTextChangeListener.onEditTextChange(mPhoneNumberEt);
            }
        }
    }

    // 是否显示底部分隔线和提示文字(默认显示）
    public void showTip(boolean show) {
        if(mLineView != null) {
            mLineView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if(mTipTv != null) {
            mTipTv.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    // 设置账户类型：手机|邮箱
    public void setAccountType(int type) {
        mAccountType = type;
    }

    // 设置默认手机号
    public void setDefaultMobile(String mobile) {
        if(mPhoneNumberEt != null) {
            mPhoneNumberEt.setText(mobile);
        }
    }

    // 设置手机号输入框提示语
    public void setPhoneNumberEtHint(String hint) {
        if(mPhoneNumberEt != null) {
            mPhoneNumberEt.setHint(hint);
        }
    }

    // 设置手机号输入框输入类型
    public void setPhoneNumberEtInputType(int inputType) {
        if(mPhoneNumberEt != null) {
            mPhoneNumberEt.setInputType(inputType);
        }
    }

    // 设置手机文本框不能编辑
    public void enablePhoneEt(boolean enable) {
        if(mPhoneNumberEt != null) {
            mPhoneNumberEt.setEnabled(enable);
            // 隐藏取消按钮
            mPhoneNumberCancelIv.setVisibility(View.INVISIBLE);
        }
    }

    // 发送短信验证码成功
    public void sendSmsCodeSuccess(String smsCodeId) {
        if (mCapchaDlg != null) {
            mCapchaDlg.dismiss();
            mCapchaDlg = null;
        }
        timer();
        mSmsCodeId = smsCodeId;
    }

    // 直接显示返回的图片验证码
    public void showImgVCodeDlg(String imgCodeId, String imgCode) {
        if (mShowAlert) {
            showAlertDlg("图片验证码输入有误，请重新输入", new LoginSmsCodeView.ISendSmsCodeListener() {
                // 图片验证码错误
                @Override
                public void onImageCodeError() {
                    showCapchaDlg(imgCodeId, imgCode);
                }
            });
        } else {
            showCapchaDlg(imgCodeId, imgCode);
        }
        mShowAlert = true;
    }

    // 取消倒计时
    public void cancelViewTimer() {
        cancelTimer();
    }

    // 设置按钮文字
    public void setBtnText(String text) {
        if (null != mLoginBtn) {
            mLoginBtn.setText(text);
        }
    }

    // 文本框内容发生改变
    public void setOnTextChangeListener(OnEditTextChangeListener listener) {
        mOnEditTextChangeListener = listener;
    }

    // 组件点击事件Listener
    public interface ILoginSmsCodeViewClickListener {
        // 点击获取验证码按钮
        void onSendSmsBtnClick(String phone, String imgCodeId, String imgCode);

        // 点击登录/确定按钮
        void onLoginBtnClick(String mobile, String smsCode, String smsCodeId);
    }

    public interface OnEditTextChangeListener {
        // 文本框内容发生改变
        void onEditTextChange(View view);

    }

}
