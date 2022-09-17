package com.mtime.bussiness.ticket.movie.dialog;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;


import com.mtime.frame.App;
import com.mtime.base.utils.MToastUtils;
import com.mtime.statistic.baidu.BaiduConstants;
import com.mtime.util.CustomAlertDlg;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import java.util.List;

/**
 * Created by zhulinping on 2017/6/20.
 */

public class PhoneDialog {
    private static final String DEFAULT_PHONE_NUMBER = "4006059500";

    private final Activity mCotext;
    private final String mPhoneNum;
    private CustomAlertDlg mCustomDlg;

    public PhoneDialog(Activity context) {
        this(context, DEFAULT_PHONE_NUMBER);
    }

    public PhoneDialog(Activity context, String phoneNumber) {
        mCotext = context;
        this.mPhoneNum = phoneNumber;
    }

    public void showCallPhoneDlg() {
        mCustomDlg = new CustomAlertDlg(mCotext, CustomAlertDlg.TYPE_OK_CANCEL);
        mCustomDlg.setBtnOKListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                doAction(mCotext);
                mCustomDlg.dismiss();
            }

        });
        mCustomDlg.setBtnCancelListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                mCustomDlg.dismiss();
            }
        });
        mCustomDlg.show();
        mCustomDlg.setTitle("呼叫" + mPhoneNum);
        mCustomDlg.getTextView().setText("呼叫" + mPhoneNum);
    }

    private void doAction(final Context context) {
        try {
            Acp.getInstance(context).request(new AcpOptions.Builder().setPermissions(
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_PHONE_STATE).build(), new AcpListener() {
                @Override
                public void onGranted() {
                    Acp.getInstance(context).onDestroy();
                    final Uri uri = Uri.parse("tel:" + mPhoneNum);
                    final Intent intent = new Intent();
                    intent.setData(uri);
                    // 直接拨打电话
                    intent.setAction(Intent.ACTION_CALL);
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
}
