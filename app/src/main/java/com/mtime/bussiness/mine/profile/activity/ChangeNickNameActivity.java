package com.mtime.bussiness.mine.profile.activity;

import android.os.Bundle;
import androidx.collection.ArrayMap;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.kotlin.android.user.UserManager;
import com.mtime.common.utils.TextUtil;
import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.mine.profile.bean.ChangeNicknameBean;
import com.mtime.util.HttpUtil;
import com.mtime.network.RequestCallback;
import com.mtime.widgets.BaseTitleView.ActionType;
import com.mtime.widgets.BaseTitleView.ITitleViewLActListener;
import com.mtime.widgets.BaseTitleView.StructType;
import com.mtime.bussiness.mine.widget.TitleOfLoginView;
import com.mtime.network.ConstantUrl;
import com.mtime.util.CustomAlertDlg;
import com.mtime.util.UIUtil;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 我的--个人资料--修改昵称
 */
public class ChangeNickNameActivity extends BaseActivity implements
        ITitleViewLActListener {

    // 汉字长度 1个汉字为2个字符
    private static final int MIN_LENGTH = 4;
    private static final int MAX_LENGTH = 20;

    private EditText et_nickname;
    private ImageView iv_delete;

    private RequestCallback save_callback;
    private CustomAlertDlg changeErrorDlg;//修改失败的弹窗

    private TitleOfLoginView navigationbar;

    @Override
    protected void onInitVariable() {
        setPageLabel("changeNickname");
    }

    @Override
    protected void onInitView(final Bundle savedInstanceState) {
        this.setContentView(R.layout.act_profile_change_nickname);
        View navbar = this.findViewById(R.id.navigationbar);
        navigationbar = new TitleOfLoginView(this, navbar,
                StructType.TYPE_LOGIN_SHOW_TITLE_SKIP, getResources()
                .getString(R.string.str_edit_name), this);
        navigationbar.setRightText("保存", false);

        et_nickname = findViewById(R.id.et_nickname);
        iv_delete = findViewById(R.id.iv_delete);

        et_nickname.setText(UserManager.Companion.getInstance().getNickname());
        et_nickname.setSelection(et_nickname.getText().length()); // 将光标移至最后
    }

    @Override
    protected void onInitEvent() {
        OnClickListener onDeleteListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                et_nickname.getText().clear();
                iv_delete.setVisibility(View.INVISIBLE);
            }
        };

        iv_delete.setOnClickListener(onDeleteListener);

        save_callback = new RequestCallback() {
            @Override
            public void onSuccess(final Object result) {
                UIUtil.dismissLoadingDialog();

                final ChangeNicknameBean r = (ChangeNicknameBean) result;
                if (r.getSuccess()) {
                    UserManager.Companion.getInstance().setNickname(getNickNameText());
                    MToastUtils.showShortToast("用户昵称修改成功");
                    finish();
                } else {
                    showChangeErrorDlg();
                    changeErrorDlg.setText(r.getErrorMessage());
                }
            }

            private String getNickNameText() {
                return et_nickname.getText().toString().trim();
            }

            @Override
            public void onFail(final Exception e) {
                UIUtil.dismissLoadingDialog();
                MToastUtils.showShortToast("修改昵称失败:" + e.getLocalizedMessage());
            }
        };

        et_nickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(final CharSequence s, final int start,
                                      final int before, final int count) {
//                clearNickName();
            }

            private void clearNickName() {
                String nickName = et_nickname.getText().toString().trim();
                String clearNickName = nickNameFilter(nickName);
                if (!nickName.equals(clearNickName)) {
                    et_nickname.setText(clearNickName);
                    et_nickname.setSelection(clearNickName.length());
                }
            }

            @Override
            public void beforeTextChanged(final CharSequence s,
                                          final int start, final int count, final int after) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (s.length() > 0) {
                    iv_delete.setVisibility(View.VISIBLE);
                    String nickName = et_nickname.getText().toString().trim();
                    // 4-20个字符，1个英文或1个汉字都算1个字符
                    int length = nickName.length();
                    boolean clickable = length >= MIN_LENGTH
                            && length <= MAX_LENGTH
                            && !nickName.equalsIgnoreCase(UserManager.Companion.getInstance().getNickname());
                    navigationbar.setRightText("保存", clickable);
                } else {
                    iv_delete.setVisibility(View.INVISIBLE);
                    navigationbar.setRightText("保存", false);
                }
            }
        });
    }

    private void showChangeErrorDlg() {
        changeErrorDlg = new CustomAlertDlg(ChangeNickNameActivity.this, CustomAlertDlg.TYPE_OK);
        changeErrorDlg.setBtnOKListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                changeErrorDlg.dismiss();
            }
        });
        changeErrorDlg.show();
    }

    @Override
    protected void onLoadData() {

    }

    @Override
    protected void onRequestData() {

    }

    @Override
    protected void onUnloadData() {

    }

    @Override
    public void onEvent(ActionType type, String content) {
        switch (type) {
            case TYPE_SKIP:
                String nickName = et_nickname.getText().toString().trim();

                if (!TextUtils.isEmpty(nickName)) {
                    boolean rs = nickName.matches("^[\\u4e00-\\u9fa5[A-Za-z0-9]\\w\\--_]*$");
                    // 4-20个字符，1个英文或1个汉字都算1个字符
                    int length = nickName.length();
                    if(length < MIN_LENGTH || length > MAX_LENGTH || nickName.matches("[0-9]*") || !rs) {
                        MToastUtils.showShortToast( getString(R.string.edit_nick_name_tip));
                        return;
                    }

                    UIUtil.showLoadingDialog(ChangeNickNameActivity.this);
                    Map<String, String> parameterList = new ArrayMap<String, String>(1);
                    parameterList.put("nickname", nickName);
                    HttpUtil.post(ConstantUrl.CHANGE_NICKNAME, parameterList, ChangeNicknameBean.class, save_callback);
                }
                break;

            default:
                this.finish();
                break;
        }
    }

    public static String nickNameFilter(String str)
            throws PatternSyntaxException {
        // 只允许字母、数字和汉字

        String validateStr = "[\\w\\-－＿[０-９]\u4e00-\u9fa5\uFF21-\uFF3A\uFF41-\uFF5A]";
//        String regEx = "[\\w\\--_0-9\u4E00-\u9FA5]";
        Pattern p = Pattern.compile(validateStr);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }
}
