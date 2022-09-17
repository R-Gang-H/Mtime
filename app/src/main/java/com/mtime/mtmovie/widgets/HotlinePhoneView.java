package com.mtime.mtmovie.widgets;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.mtime.frame.App;
import com.mtime.R;
import com.mtime.base.utils.MToastUtils;
import com.mtime.statistic.baidu.BaiduConstants;
import com.mtime.util.CustomAlertDlg;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import java.util.List;

/**
 * 热线电话自定义控件
 */
public class HotlinePhoneView extends LinearLayout {

    private String mPhoneNumber;
    private boolean mIsShowDialog = true;
    private CustomAlertDlg mCustomDlg;
    private int mAction;
    // public static final String DEFAULT_PHONE_NUMBER = "4006059500";
    // //时光网票务客服热线
    public static final int ACTION_CALL = 0;
    public static final int ACTION_DIAL = 1;
    private final Context context;

    public HotlinePhoneView(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        final TypedArray Attributes = context.obtainStyledAttributes(attrs, R.styleable.HotlineView);
        mPhoneNumber = Attributes.getString(R.styleable.HotlineView_phoneNumber);
        mIsShowDialog = Attributes.getBoolean(R.styleable.HotlineView_showDialog, true);
        mAction = Attributes.getInteger(R.styleable.HotlineView_action, HotlinePhoneView.ACTION_CALL);
        this.context = context;

        // TODO 如果这里调用这个，会有什么影响吗？ 需要确定一下。理论上应该调用一下的
        // TODO 这个名字取的？
        Attributes.recycle();

        init(context);
    }

    public HotlinePhoneView(final Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    private void init(final Context context) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.telephone_hotline_view, null);
        if ((mPhoneNumber == null) || "".equals(mPhoneNumber)) {
            final TextView tv_number = view.findViewById(R.id.telephone_view_tv_number);
            mPhoneNumber = tv_number.getText().toString();
        }

        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                if (mIsShowDialog) {
                    HotlinePhoneView.this.showCallPhoneDlg(context);
                } else {
//                    PermissionsDispatcher.getInstance().checkPermission((BaseActivity) context, PermissionsDispatcher.REQUEST_SHOWCALLPHONE);
                    HotlinePhoneView.this.doAction(context);
                }
            }
        });
        this.addView(view);
    }

    private void showCallPhoneDlg(final Context context) {
        mCustomDlg = new CustomAlertDlg(context, CustomAlertDlg.TYPE_OK_CANCEL);
        mCustomDlg.setBtnOKListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
//                PermissionsDispatcher.getInstance().checkPermission((BaseActivity) context, PermissionsDispatcher.REQUEST_SHOWCALLPHONE);
                HotlinePhoneView.this.doAction(context);
                mCustomDlg.dismiss();
            }

        });
        mCustomDlg.setBtnCancelListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                mCustomDlg.dismiss();
            }
        });
        mCustomDlg.show();
        mCustomDlg.setTitle("呼叫" + mPhoneNumber);
        mCustomDlg.getTextView().setText("呼叫" + mPhoneNumber);
    }

    private void doAction(final Context context) {
        try {
            Acp.getInstance(context).request(new AcpOptions.Builder().setPermissions(
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_PHONE_STATE).build(), new AcpListener() {
                @Override
                public void onGranted() {
                    Acp.getInstance(context).onDestroy();
                    final Uri uri = Uri.parse("tel:" + mPhoneNumber);
                    final Intent intent = new Intent();
                    intent.setData(uri);
                    if (mAction == HotlinePhoneView.ACTION_CALL) {
                        // 直接拨打电话
                        intent.setAction(Intent.ACTION_CALL);
                    } else {
                        // 调拨打电话界面
                        intent.setAction(Intent.ACTION_DIAL);
                    }
                    context.startActivity(intent);
                }


                @Override
                public void onDenied(List<String> permissions) {
                    Acp.getInstance(context).onDestroy();
                    MToastUtils.showShortToast(permissions.toString() + "权限拒绝");
                }
            });
        } catch (Exception e) {

        }
    }

    public void setPhoneNumber(final String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    /**
     * 设置是打开拨号盘还是直接拨号
     *
     * @param action 使用HotlineView.ACTION_CALL或HotlineView.ACTION_DIAL
     */
    public void setAction(final int action) {
        mAction = action;
    }

    /**
     * 是否显示提示对话框
     *
     * @param isShowDialog
     */
    public void setShowDialog(final boolean isShowDialog) {
        mIsShowDialog = isShowDialog;
    }

//    @Override
//    public void agreePermission(int requestCode) {
//        HotlinePhoneView.this.doAction(context);
//    }
//
//    @Override
//    public void disagreePermission(int requestCode) {
//        MToastUtils.showShortToast("不同意用不了啊");
//    }
}
