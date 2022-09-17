package com.mtime.bussiness.mine.login.holder;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;


import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.BuildConfig;
import com.mtime.R;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.mine.login.fragment.LoginAccountFragment;
import com.mtime.util.MtimeUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author vivian.wei
 * @date 2019/8/8
 * @desc 登录页_账号密码登录Holder
 */
public class LoginAccountHolder extends ContentHolder<List<String>> {

    @BindView(R.id.fragment_login_account_account_tv)
    AutoCompleteTextView mAccountTv;
    @BindView(R.id.fragment_login_account_account_cancel_iv)
    ImageView mAccountCancelIv;
    @BindView(R.id.fragment_login_account_password_et)
    EditText mPasswordEt;
    @BindView(R.id.fragment_login_account_password_cancel_iv)
    ImageView mPasswordCancelIv;
    @BindView(R.id.fragment_login_account_password_show_line_view)
    View mPasswordShowLineView;
    @BindView(R.id.fragment_login_account_password_show_iv)
    ImageView mPasswordShowIv;
    @BindView(R.id.fragment_login_account_login_tv)
    TextView mLoginTv;

    private Unbinder mUnbinder;

    private AutoCompleteEmailAdatper mAdapter;
    private List<String> mEmailSuffixList;

    private boolean mVisiblePassword;

    public LoginAccountHolder(Context context ) {
        super(context);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onHolderCreated() {
        super.onHolderCreated();
        setContentView(R.layout.fragment_login_account);
        initView();
        initEvent();
        initVariables();
    }

    private void initView() {
        mUnbinder = ButterKnife.bind(this, mRootView);

        mAccountCancelIv.setVisibility(View.INVISIBLE);
        mPasswordCancelIv.setVisibility(View.INVISIBLE);
        mPasswordShowLineView.setVisibility(View.INVISIBLE);
    }

    // 初始化事件
    private void initEvent() {
        mAccountTv.addTextChangedListener(new AccountTextWatcher());
        mPasswordEt.addTextChangedListener(new PwdTextWatcher());

        mPasswordShowIv.setOnClickListener(this);
        mAccountCancelIv.setOnClickListener(this);
        mPasswordCancelIv.setOnClickListener(this);
        mLoginTv.setOnClickListener(this);
    }

    private void initVariables() {
        // 设置输入1个字符就显示提示
        mAccountTv.setThreshold(1);
        // 自动提示输入补全
        mAdapter = new AutoCompleteEmailAdatper(mContext);
        mAccountTv.setAdapter(mAdapter);
    }

    @Override
    public void refreshView() {
        super.refreshView();

        mEmailSuffixList = mData;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_login_account_account_cancel_iv:
                // 清空账号输入框
                mAccountTv.getText().clear();
                mAccountCancelIv.setVisibility(View.INVISIBLE);
                break;
            case R.id.fragment_login_account_password_cancel_iv:
                // 清空密码输入框
                mPasswordEt.getText().clear();
                mPasswordCancelIv.setVisibility(View.INVISIBLE);
                mPasswordShowLineView.setVisibility(View.INVISIBLE);
                break;
            case R.id.fragment_login_account_password_show_iv:
                // 密码明文切换
                mVisiblePassword = !mVisiblePassword;
                if (mVisiblePassword) {
                    // 明文
                    mPasswordEt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    mPasswordShowIv.setImageResource(R.drawable.login_switch_on);
                } else {
                    // 密码
                    mPasswordEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mPasswordShowIv.setImageResource(R.drawable.login_switch_off);
                }
                break;
            case R.id.fragment_login_account_login_tv:
                // 点击登录按钮
                // 检查输入框
                String userAccount = mAccountTv.getText().toString();
                String userPassword = mPasswordEt.getText().toString();
                /**
                 * 为了方便开发阶段登录，暂时在这里写个测试账户
                 */
                if (BuildConfig.DEBUG) {
                    if(TextUtils.isEmpty(userAccount) && TextUtils.isEmpty(userPassword)){
                        userAccount = "sha@mtime.com";
                        userPassword = "111111";
                    }
                }
                if (TextUtils.isEmpty(userAccount)) {
                    MToastUtils.showShortToast("请输入账号");
                } else if (TextUtils.isEmpty(userPassword)) {
                    MToastUtils.showShortToast("请输入密码");
                } else if (MtimeUtils.isChinese(userPassword)) {
                    MToastUtils.showShortToast("新密码包含中文字符");
                } else {
                    // 登录
                    hideSoftKeyboard();
                    Bundle bundle = new Bundle();
                    bundle.putString("name", userAccount);
                    bundle.putString("password", userPassword);
                    onHolderEvent(LoginAccountFragment.HOLDER_EVENT_CODE_LOGIN, bundle);
                }
                break;
            default:
                break;
        }
    }

    // 隐藏软键盘
    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(mPasswordEt.getWindowToken(), 0);
        }
    }

    // Email自动选择下拉框
    private class AutoCompleteEmailAdatper extends BaseAdapter implements Filterable {

        List<String> mList;
        private AutoCompleteEmailAdatper.AutoCompleteEmailFilter mFilter;

        public AutoCompleteEmailAdatper(final Context context) {
            mList = new ArrayList<String>();
        }

        @Override
        public int getCount() {
            return mList == null ? 0 : mList.size();
        }

        @Override
        public Object getItem(final int position) {
            return mList == null ? null : mList.get(position);
        }

        @Override
        public long getItemId(final int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            final ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (null == convertView) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.act_login_auto_complete_item, null);
                viewHolder = new ViewHolder();
                viewHolder.textView = convertView.findViewById(R.id.login_auto_complete_Email);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (mList != null) {

                viewHolder.textView.setText(mList.get(position));
            }

            return convertView;
        }

        @Override
        public Filter getFilter() {
            if (mFilter == null) {
                mFilter = new AutoCompleteEmailAdatper.AutoCompleteEmailFilter();
            }
            return mFilter;
        }

        private class AutoCompleteEmailFilter extends Filter {

            @Override
            protected FilterResults performFiltering(final CharSequence constraint) {
                //执行过滤
                final FilterResults results = new FilterResults();
                if (mList == null) {
                    mList = new ArrayList<String>();
                }
                results.values = mList;
                results.count = mList.size();
                return results;
            }

            @Override
            protected void publishResults(final CharSequence constraint,
                                          final FilterResults results) {//筛选结果
                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }

        }
    }

    private class ViewHolder {
        TextView textView;
    }

    // 账号文本检测
    private class AccountTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(final CharSequence s, final int start,
                                      final int count, final int after) {
        }

        @Override
        public void onTextChanged(final CharSequence s, final int start,
                                  final int before, final int count) {
        }

        @Override
        public void afterTextChanged(final Editable s) {
            String label = s.toString();
            mAccountCancelIv.setVisibility(TextUtils.isEmpty(label) ? View.INVISIBLE : View.VISIBLE);

            // 埋点
            onHolderEvent(LoginAccountFragment.HOLDER_EVENT_CODE_ACCOUNT_TEXT_CHANGE, null);

            // 用户输入账号为邮箱账号时（输入0~9以外的字符时），显示提醒选项，第一次登录显示全部提示，
            // 非第一次登录，优先提醒用户上次登录时的账号信息
            // 如果如果输入框内容包括“@”，则去掉@及后面的内容，只取@前面的内容
            if ((label != null) && label.contains("@")) {
                String subAccount = label.substring(0, label.indexOf('@'));
                String subString = label.substring(label.indexOf('@'));
                mAdapter.mList.clear();

                if ((subAccount.length() > 0) && (mEmailSuffixList != null)) {
                    for (final String suffix : mEmailSuffixList) {
                        if (subString == null || subString.length() == 0) {
                            mAdapter.mList.add(subAccount + suffix);
                        } else {
                            if (suffix.startsWith(subString)) {
                                mAdapter.mList.add(subAccount + suffix);
                            }
                        }

                    }

                    mAdapter.notifyDataSetChanged();
                    mAccountTv.showDropDown();
                }

            } else {
                mAdapter.mList.clear();
                mAdapter.notifyDataSetChanged();
            }

        }
    }

    // 检测密码输入框的变化
    private class PwdTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(final CharSequence s, final int start,
                                      final int count, final int after) {
        }

        @Override
        public void onTextChanged(final CharSequence s, final int start,
                                  final int before, final int count) {
        }

        @Override
        public void afterTextChanged(final Editable s) {
            String pwd = mPasswordEt.getText().toString();
            mPasswordCancelIv.setVisibility(TextUtils.isEmpty(pwd) ? View.INVISIBLE : View.VISIBLE);
            mPasswordShowLineView.setVisibility(TextUtils.isEmpty(pwd) ? View.INVISIBLE : View.VISIBLE);

            // 埋点
            onHolderEvent(LoginAccountFragment.HOLDER_EVENT_CODE_PASSWORD_TEXT_CHANGE, null);
        }
    }

}
