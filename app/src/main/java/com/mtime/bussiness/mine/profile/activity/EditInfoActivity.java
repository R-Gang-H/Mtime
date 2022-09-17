package com.mtime.bussiness.mine.profile.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;

import com.kk.taurus.uiframe.d.BaseState;
import com.kk.taurus.uiframe.v.BaseErrorHolder;
import com.kk.taurus.uiframe.v.BaseStateContainer;
import com.kk.taurus.uiframe.v.NoTitleBarContainer;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.mine.api.MineApi;
import com.mtime.bussiness.mine.profile.bean.UpdateMemberInfoBean;
import com.mtime.bussiness.mine.profile.holder.EditInfoHolder;
import com.mtime.frame.BaseFrameUIActivity;
import com.mtime.util.UIUtil;

/**
 * @author vivian.wei
 * @date 2020/9/21
 * @desc 编辑通用页面
 */
public class EditInfoActivity extends BaseFrameUIActivity<String, EditInfoHolder> {

    public static final String KEY_INFO_EDIT_TYPE = "key_info_edit_type";
    public static final String KEY_INFO_EDIT_CONTENT = "key_info_edit_content";
    public static final int EDIT_INFO_TYPE_SIGN = 100001;   // 签名

    // 1.生日，2居住地 3,用户签名
    private static final String TYPE_SIGN = "3";

    private int mType = 0;
    private String mContent;
    private MineApi mMineApi;

    public static void launch(Context context, String refer, int type, String content, int requestCode) {
        Intent intent = new Intent(context, EditInfoActivity.class);
        intent.putExtra(KEY_INFO_EDIT_TYPE, type);
        intent.putExtra(KEY_INFO_EDIT_CONTENT, content);
        dealRefer(context, refer, intent);
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, requestCode);
        } else {
            context.startActivity(intent);
        }
    }

    @Override
    public EditInfoHolder onBindContentHolder() {
        return new EditInfoHolder(this);
    }

    @Override
    protected BaseStateContainer getStateContainer() {
        return new NoTitleBarContainer(this, this, this);
    }

    @Override
    public BaseErrorHolder onBindErrorHolder() {
        return super.onBindErrorHolder();
    }

    @Override
    protected void onParseIntent() {
        super.onParseIntent();

        mType = getIntent().getIntExtra(KEY_INFO_EDIT_TYPE, 0);
        mContent = getIntent().getStringExtra(KEY_INFO_EDIT_CONTENT);
    }

    @Override
    protected void onInit(Bundle savedInstanceState) {
        super.onInit(savedInstanceState);

        mMineApi = new MineApi();
        getUserContentHolder().setType(mType);
        // 弹出软键盘
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    protected void onErrorRetry() {
        super.onErrorRetry();

        onLoadState();
    }

    @Override
    protected void onLoadState() {
        setPageState(BaseState.SUCCESS);
        setData(mContent);
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

        if(mMineApi != null) {
            mMineApi = null;
        }
    }

    @Override
    public void onHolderEvent(int eventCode, Bundle bundle) {
        super.onHolderEvent(eventCode, bundle);

        switch (eventCode) {
            case EditInfoHolder.EVENT_CODE_BACK:
                // 点击"返回"箭头
                onBackPressed();
                break;
            case EditInfoHolder.EVENT_CODE_SAVE:
                // 点击"保存"
                switch (mType) {
                    case EDIT_INFO_TYPE_SIGN:
                        // 签名
                        // 接口成功：返回修改资料页，失败：留在本页，提示错误信息
                        String sign = bundle.getString(KEY_INFO_EDIT_CONTENT);
                        saveSign(sign);
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    // 兼容物理返回键
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * 保存签名
     */
    private void saveSign(String sign) {
        if(TextUtils.isEmpty(sign)) {
            MToastUtils.showShortToast("签名不能为空");
            return;
        }
        if(mContent.equals(sign)) {
            MToastUtils.showShortToast("请修改签名内容再保存");
            return;
        }

        UIUtil.showLoadingDialog(this);
        mMineApi.updateMemberInfo("", "", sign, TYPE_SIGN, new NetworkManager.NetworkListener<UpdateMemberInfoBean>() {
            @Override
            public void onSuccess(UpdateMemberInfoBean result, String showMsg) {
                UIUtil.dismissLoadingDialog();
                // 显示接口返回的提示信息
                MToastUtils.showShortToast(result.getBizMsg());
                if (result.getBizCode() == 0) {
                    // 更新成功（后台审核通过后才能显示，所以不用设置到UserManager中）
                    onBackPressed();
                }
            }

            @Override
            public void onFailure(NetworkException<UpdateMemberInfoBean> exception, String showMsg) {
                UIUtil.dismissLoadingDialog();
                MToastUtils.showShortToast("签名修改失败:" + showMsg);
            }
        });
    }

}
