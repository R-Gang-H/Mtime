package com.mtime.bussiness.mine.profile.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.kk.taurus.uiframe.d.BaseState;
import com.kk.taurus.uiframe.v.BaseErrorHolder;
import com.kk.taurus.uiframe.v.BaseStateContainer;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.kk.taurus.uiframe.v.NoTitleBarContainer;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.mine.api.MineApi;
import com.mtime.bussiness.mine.login.holder.SetPasswordHolder;
import com.mtime.bussiness.mine.profile.bean.RegetPasswordWithLoginBean;
import com.mtime.frame.BaseFrameUIActivity;
import com.mtime.util.MtimeUtils;

/**
 * @author vivian.wei
 * @date 2019/8/22
 * @desc （账号密码登录）密码修改页
 */
public class ChangePasswordLoginActivity  extends BaseFrameUIActivity<Void, SetPasswordHolder> {

    public static final int HOLDER_EVENT_OK_BTN_CLICK = 1001;

    private MineApi mMineApi;

    @Override
    protected BaseStateContainer getStateContainer() {
        return new NoTitleBarContainer(this, this, this);
    }

    @Override
    public ContentHolder onBindContentHolder() {
        return new SetPasswordHolder(this, true);
    }

    @Override
    public BaseErrorHolder onBindErrorHolder() {
        return super.onBindErrorHolder();
    }

    @Override
    protected void onParseIntent() {
        super.onParseIntent();
    }

    @Override
    protected void onInit(Bundle savedInstanceState) {
        super.onInit(savedInstanceState);

        // 埋点
        mBaseStatisticHelper.setPageLabel("changePassword");

        if (null == mMineApi) {
            mMineApi = new MineApi();
        }

        getUserContentHolder().setTitle("修改密码");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (null != mMineApi) {
            mMineApi.cancel();
        }
    }

    @Override
    public void onHolderEvent(int eventCode, Bundle bundle) {
        super.onHolderEvent(eventCode, bundle);

        switch (eventCode) {
            case HOLDER_EVENT_OK_BTN_CLICK:
                // 点击确定按钮
                clickOkBtn();
            default:
                break;
        }
    }

    // 兼容物理返回键
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // 点击确定按钮
    private void clickOkBtn() {
        String oldPwd = getUserContentHolder().getOldPwd();
        String newPwd = getUserContentHolder().getNewPwd();
        String newPwdConfirm = getUserContentHolder().getNewPwdConfirm();

        // 旧密码
        if (TextUtils.isEmpty(oldPwd) || oldPwd.length() == 0) {
            MToastUtils.showShortToast("请输入您的旧的密码");
            return;
        }

        if (oldPwd.length() < 6 || oldPwd.length() > 20) {
            MToastUtils.showShortToast("旧密码长度要在6-20个字符以内");
            return;
        }

        // 新密码
        if (TextUtils.isEmpty(newPwd) || 0 == newPwd.length()) {
            MToastUtils.showShortToast("请输入您的新密码");
            return;
        }

        // 这里需要有逻辑对密码进行validation
        if (newPwd.length() < 6 || newPwd.length() > 20) {
            MToastUtils.showShortToast("新密码长度要在6-20个字符以内");
            return;
        }

        if (MtimeUtils.isChinese(newPwd)) {
            MToastUtils.showShortToast("新密码包含中文字符");
            return;
        }

        if (newPwd.equals(oldPwd)) {
            MToastUtils.showShortToast("新密码与旧密码一致，请重新输入");
            return;
        }

        // 确认密码
        if (TextUtils.isEmpty(newPwdConfirm) || newPwdConfirm.length() == 0) {
            MToastUtils.showShortToast("请输入确认密码");
            return;
        }

        if (!newPwd.equals(newPwdConfirm)) {
            MToastUtils.showShortToast("新密码俩次输入不一致，请检查");
            return;
        }

        // 修改密码
        setPageState(BaseState.LOADING);
        mMineApi.modifyPassword(oldPwd, newPwd, newPwdConfirm, new NetworkManager.NetworkListener<RegetPasswordWithLoginBean>() {
            @Override
            public void onSuccess(RegetPasswordWithLoginBean bean, String showMsg) {
                setPageState(BaseState.SUCCESS);

                MToastUtils.showShortToast(bean.getMessage());
                if (0 == bean.getBizCode()) {
                    // 个人资料页
                    finish();
                }
            }

            @Override
            public void onFailure(NetworkException<RegetPasswordWithLoginBean> exception, String showMsg) {
                setPageState(BaseState.SUCCESS);
                MToastUtils.showShortToast("修改密码失败：" + showMsg);
            }
        });
    }

    // 启动
    public static void launch(Context context, String refer) {
        Intent intent = new Intent(context, ChangePasswordLoginActivity.class);
        dealRefer(context, refer, intent);
        context.startActivity(intent);
    }

}
