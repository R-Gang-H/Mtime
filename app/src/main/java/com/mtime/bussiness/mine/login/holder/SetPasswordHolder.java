package com.mtime.bussiness.mine.login.holder;

import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kk.taurus.uiframe.i.ITitleBar;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.R;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.mine.profile.activity.SetPasswordActivity;
import com.mtime.bussiness.splash.LoadManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author vivian.wei
 * @date 2019/8/21
 * @desc 设置密码页Holder
 */
public class SetPasswordHolder extends ContentHolder<Void> {

    private static final int NEW_PASSWORD_CL_MARGIN_TOP_DP = 50;

    // 标题
    @BindView(R.id.activity_set_password_back_iv)
    ImageView mBackIv;
    @BindView(R.id.activity_set_password_title_tv)
    TextView mTitleTv;
    @BindView(R.id.activity_set_password_tip_tv)
    TextView mTipTv;

    @BindView(R.id.activity_set_password_old_cl)
    View mOldPassworlCl;
    @BindView(R.id.activity_set_password_old_input_et)
    EditText mOldInputEt;
    @BindView(R.id.activity_set_password_old_cancel_iv)
    ImageView mOldCancelIv;

    @BindView(R.id.activity_set_password_new_cl)
    View mNewPassworlCl;
    @BindView(R.id.activity_set_password_new_input_et)
    EditText mNewInputEt;
    @BindView(R.id.activity_set_password_new_cancel_iv)
    ImageView mNewCancelIv;

    @BindView(R.id.activity_set_password_verify_input_et)
    EditText mVerifyInputEt;
    @BindView(R.id.activity_set_password_verify_cancel_iv)
    ImageView mVerifyCancelIv;

    @BindView(R.id.activity_set_password_ok_tv)
    TextView mOkTv;

    private Unbinder mUnbinder;
    private final boolean mIsAccountPasswordLogin;  // 是否为账号密码登录

    public SetPasswordHolder(Context context, boolean isAccountPasswordLogin) {
        super(context);
        mIsAccountPasswordLogin = isAccountPasswordLogin;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onHolderCreated() {
        super.onHolderCreated();
        setContentView(R.layout.activity_set_password);
        initView();
        initListener();
    }

    private void initView() {
        mUnbinder = ButterKnife.bind(this, mRootView);

        if(mIsAccountPasswordLogin) {
            mOldPassworlCl.setVisibility(View.VISIBLE);
            mOldCancelIv.setVisibility(View.INVISIBLE);
        } else {
            mOldPassworlCl.setVisibility(View.GONE);
        }
        mNewCancelIv.setVisibility(View.INVISIBLE);
        mVerifyCancelIv.setVisibility(View.INVISIBLE);
        // 提示语
        mTipTv.setVisibility(TextUtils.isEmpty(LoadManager.getFindPasswordText()) ? View.INVISIBLE : View.VISIBLE);
        mTipTv.setText(LoadManager.getFindPasswordText());
        // 新密码布局topMargin
        if(!mIsAccountPasswordLogin) {
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mNewPassworlCl.getLayoutParams();
            params.topMargin = MScreenUtils.dp2px(NEW_PASSWORD_CL_MARGIN_TOP_DP);
            mNewPassworlCl.setLayoutParams(params);
        }
    }

    private void initListener() {
        mBackIv.setOnClickListener(this);
        mNewCancelIv.setOnClickListener(this);
        mVerifyCancelIv.setOnClickListener(this);
        mOkTv.setOnClickListener(this);

        if(mIsAccountPasswordLogin) {
            mOldCancelIv.setOnClickListener(this);

            mOldInputEt.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    String pwd = getOldPwd();
                    mOldCancelIv.setVisibility(TextUtils.isEmpty(pwd) ? View.INVISIBLE : View.VISIBLE);
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });
        }

        mNewInputEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                String pwd = getNewPwd();
                mNewCancelIv.setVisibility(TextUtils.isEmpty(pwd) ? View.INVISIBLE : View.VISIBLE);
            }
        });

        mVerifyInputEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String newPwdConfirm = getNewPwdConfirm();
                mVerifyCancelIv.setVisibility(TextUtils.isEmpty(newPwdConfirm) ? View.INVISIBLE : View.VISIBLE);

                // 如果密码和确认密码不同，则直接判断,不能再这里判断
                // 这里不能直接这么写，忘记了这个函数的作用了
                String newPwd = getNewPwd();
                if (TextUtils.isEmpty(newPwd) || newPwd.length() < 6 || newPwd.length() > 20) {
                    MToastUtils.showShortToast("请输入您的新密码, 长度要在6-20个字符以内");
                    return;
                }

                if (!TextUtils.isEmpty(newPwdConfirm)) {
                    if (newPwdConfirm.length() > newPwd.length() || (newPwdConfirm.length() == newPwd.length() && !newPwd.equals(newPwdConfirm))) {
                        MToastUtils.showShortToast("新密码俩次输入不一致，请检查");
                    }
                }
            }
        });

    }

    @Override
    public void refreshView() {
        super.refreshView();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_set_password_back_iv:
                // 返回箭头
                onHolderEvent(ITitleBar.TITLE_BAR_EVENT_NAVIGATION_CLICK, null);
                break;
            case R.id.activity_set_password_old_cancel_iv:
                // 清空旧密码
                mOldInputEt.getText().clear();
                mOldCancelIv.setVisibility(View.INVISIBLE);
                break;
            case R.id.activity_set_password_new_cancel_iv:
                // 清空新密码
                mNewInputEt.getText().clear();
                mNewCancelIv.setVisibility(View.INVISIBLE);
                break;
            case R.id.activity_set_password_verify_cancel_iv:
                // 清空确认密码
                mVerifyInputEt.getText().clear();
                mVerifyCancelIv.setVisibility(View.INVISIBLE);
                break;
            case R.id.activity_set_password_ok_tv:
                // 确定按钮
                onHolderEvent(SetPasswordActivity.HOLDER_EVENT_OK_BTN_CLICK, null);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (null != mUnbinder) {
            mUnbinder.unbind();
        }
    }

    // 获取旧密码
    public String getOldPwd() {
        return mOldInputEt.getText().toString().trim();
    }

    // 获取新密码
    public String getNewPwd() {
        return mNewInputEt.getText().toString().trim();
    }

    // 获取确认密码
    public String getNewPwdConfirm() {
        return mVerifyInputEt.getText().toString().trim();
    }

    // 设置标题文字
    public void setTitle(String title) {
        if(mTitleTv != null) {
            mTitleTv.setText(title);
        }
    }

}
