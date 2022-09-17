package com.mtime.bussiness.mine.profile.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.kotlin.android.user.UserManager;
import com.mtime.R;
import com.mtime.bussiness.mine.widget.TitleOfLoginView;
import com.mtime.frame.BaseActivity;
import com.mtime.util.JumpUtil;
import com.mtime.util.MtimeUtils;
import com.mtime.widgets.BaseTitleView.ActionType;
import com.mtime.widgets.BaseTitleView.ITitleViewLActListener;
import com.mtime.widgets.BaseTitleView.StructType;

/**
 * Created by LEE on 12/30/16.
 * 个人资料_绑定手机页(已绑定时）
 */

public class ChangeMobileBindingActivity extends BaseActivity {


    @Override
    protected boolean  enableSliding(){
        return true;
    }

    @Override
    protected void onInitEvent() {
    }
    
    @Override
    protected void onInitVariable() {
        this.setSwipeBack(true);
        setPageLabel("changeBindPhone");
    }
    
    @Override
    protected void onInitView(final Bundle savedInstanceState) {
        this.setContentView(R.layout.act_profile_change_mobilebinding);
        
        View root = this.findViewById(R.id.unbind_title);
        new TitleOfLoginView(this, root, StructType.TYPE_LOGIN_SHOW_TITLE_ONLY, "手机绑定", new ITitleViewLActListener() {
            
            @Override
            public void onEvent(ActionType type, String content) {
               finish();
            }
        });
        
        
        TextView phone = findViewById(R.id.bind_phone);
        if(null != UserManager.Companion.getInstance().getUser()) {
            phone.setText(MtimeUtils.getBindMobile(UserManager.Companion.getInstance().getBindMobile()));
        }
        TextView unbind_btn = this.findViewById(R.id.unbind_btn);
        unbind_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String bindMobile = UserManager.Companion.getInstance().getBindMobile();
                // 身份验证页
                JumpUtil.startSecuritycodeActivity(ChangeMobileBindingActivity.this, assemble().toString(), bindMobile, 1);
                finish();
            }});
        
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
    
//    private void requestImageVerifyCode() {
//        UIUtil.showLoadingDialog(this);
//        RequestCallback imageVerifyCodeCallback = new RequestCallback() {
//
//            @Override
//            public void onSuccess(Object o) {
//
//                ImageVerifyCodeBean imageVerifyCodeBean = (ImageVerifyCodeBean) o;
//                if (imageVerifyCodeBean.isNeedCode()) {
//                    UIUtil.dismissLoadingDialog();
//                    codeId = imageVerifyCodeBean.getCodeId();
//                    if (capchaDlg != null && capchaDlg.isShowing()) {
//                        volleyImageLoader.displayVeryImg(imageVerifyCodeBean.getUrl(), capchaDlg.getImageView(), null);
//                    }else {
//                        showCapchadlg(imageVerifyCodeBean.getUrl());
//                    }
//                }
//                else {
//                    requestBindPhone("", "");
//                }
//
//            }
//
//            @Override
//            public void onFail(Exception e) {
//                UIUtil.dismissLoadingDialog();
//            }
//        };
//        HttpUtil.post(ConstantUrl.IMAGE_VERIFYCODE, ImageVerifyCodeBean.class, imageVerifyCodeCallback);
//    }
//
//    private void showCapchadlg( String codeUrl) {
//
//        if (capchaDlg != null && capchaDlg.isShowing()) {
//            capchaDlg.dismiss();
//        }
//
//        capchaDlg = null;
//        capchaDlg = new CapchaDlg(ChangeMobileBindingActivity.this, CapchaDlg.TYPE_OK_CANCEL);
//
//        capchaDlg.setBtnOKListener(new OnClickListener() {
//
//            @Override
//            public void onClick(final View v) {
//                UIUtil.showLoadingDialog(ChangeMobileBindingActivity.this);
//                requestBindPhone(codeId, capchaDlg.getEditView().getText().toString());
//            }
//        });
//        capchaDlg.setBtnCancelListener(new OnClickListener() {
//
//            @Override
//            public void onClick(final View v) {
//                capchaDlg.dismiss();
//            }
//        });
//        capchaDlg.setCapchaTextListener(new OnClickListener() {
//
//            @Override
//            public void onClick(final View v) {
//                requestImageVerifyCode();
//            }
//        });
//        capchaDlg.show();
//
//        volleyImageLoader.displayVeryImg(codeUrl, capchaDlg.getImageView(), null);
//    }
//
//    private void requestBindPhone(String codeId, String code) {
//        if (TextUtils.isEmpty(codeId)) {
//            return;
//        }
//        RequestCallback changeBindPhoneCallback = new RequestCallback() {
//
//            @Override
//            public void onSuccess(Object o) {
//                UIUtil.dismissLoadingDialog();
//                BaseResultJsonBean baseResultJsonBean = (BaseResultJsonBean) o;
//                if (baseResultJsonBean.isSuccess()) {
//                    Intent intent = new Intent();
//                    intent.putExtra(App.getInstance().KEY_BIND_TYPE, 2);
//                    intent.putExtra(App.getInstance().KEY_BINDPHONE, App.getInstance().userInfo.getBindMobile());
//                    startActivity(SecuritycodeActivity.class, intent);
//                    finish();
//                }
//                else {
//                    Toast.makeText(ChangeMobileBindingActivity.this, baseResultJsonBean.getMsg(), Toast.LENGTH_SHORT)
//                            .show();
//                }
//
//            }
//
//            @Override
//            public void onFail(Exception e) {
//                UIUtil.dismissLoadingDialog();
//            }
//        };
//        Map<String, String> parameterList = new ArrayMap<String, String>(2);
//        parameterList.put("vcode", code);
//        parameterList.put("vcodeId", codeId);
//        HttpUtil.post(ConstantUrl.OLD_MOBILESEND_VERITFICODE, parameterList, BaseResultJsonBean.class, changeBindPhoneCallback);
//
//    }
//
//
//    private void getSmsCode(final String phone, final int bindtype, final String imgcode, final String imgcodeid) {
//
//        UIUtil.showLoadingDialog(ChangeMobileBindingActivity.this);
//
//        Map<String, String> param = new ArrayMap<String, String>(5);
//        // 还需要其他参数。待加
//        param.put("mobile", phone);
//        param.put("bindType", String.valueOf(bindtype));
//        param.put("imgCode", imgcode);
//        param.put("imgCodeId", imgcodeid);
//        param.put("oauthToken", "");
//
//        HttpUtil.post(ConstantUrl.POST_BIND_GET_SMSCODE, param, SmsVeryCodeBean.class, new RequestCallback() {
//            @Override
//            public void onSuccess(Object o) {
//                UIUtil.dismissLoadingDialog();
//
//                final SmsVeryCodeBean bean = (SmsVeryCodeBean) o;
//                if (-4 == bean.getBizCode()) {
//                    requestCapcha(phone, bindtype, bean.getImgCodeId(), bean.getImgCodeUrl());
//                    // 图片验证码
//                }else if ( 0 == bean.getBizCode()) {
//                    Toast.makeText(ChangeMobileBindingActivity.this, bean.getMessage(), Toast.LENGTH_SHORT).show();
//                    // 发送短信成功, 启动换绑页面
//                    Intent intent = new Intent();
//                    intent.putExtra(App.getInstance().KEY_BIND_TYPE, 2);
//                    intent.putExtra(App.getInstance().KEY_BINDPHONE, App.getInstance().userInfo.getBindMobile());
//                    startActivity(SecuritycodeActivity.class, intent);
//                    finish();
//                } else {
//                    Toast.makeText(ChangeMobileBindingActivity.this, bean.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFail(Exception e) {
//
//                UIUtil.dismissLoadingDialog();
//                Toast.makeText(ChangeMobileBindingActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }
//    private void requestCapcha(final String mobile, final int type, final String imgcodeId, final String imgcodeUrl) {
//        imgecodeid = imgcodeId;
//        final CapchaDlg capchaDlg = new CapchaDlg(this, CapchaDlg.TYPE_OK_CANCEL);
//        capchaDlg.setBtnOKListener(new OnClickListener() {
//
//            @Override
//            public void onClick(final View v) {
//                String imgcode = capchaDlg.getEditView().getText().toString();
//                getSmsCode(mobile, type, imgcode, imgcodeId);
//                capchaDlg.dismiss();
//            }
//        });
//
//        capchaDlg.setBtnCancelListener(new OnClickListener() {
//
//            @Override
//            public void onClick(final View v) {
//                capchaDlg.dismiss();
//            }
//        });
//
//        capchaDlg.setCapchaTextListener(new OnClickListener() {
//
//            @Override
//            public void onClick(final View v) {
//                UIUtil.showLoadingDialog(ChangeMobileBindingActivity.this);
//
//                HttpUtil.get(ConstantUrl.LOGIN_CAPCHA, CapchaBean.class, new RequestCallback() {
//
//                    @Override
//                    public void onSuccess(Object o) {
//                        UIUtil.dismissLoadingDialog();
//
//                        CapchaBean bean = (CapchaBean) o;
//                        imgecodeid = bean.getCodeId();
//                        volleyImageLoader.displayVeryImg(bean.getUrl(), capchaDlg.getImageView(), null);
//                    }
//
//                    @Override
//                    public void onFail(Exception e) {
//                        UIUtil.dismissLoadingDialog();
//                    }
//                });
//            }
//        });
//
//        capchaDlg.show();
//        volleyImageLoader.displayVeryImg(imgcodeUrl, capchaDlg.getImageView(), null);
//    }


}
