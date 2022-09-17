package com.mtime.bussiness.common;

import android.content.Intent;
import android.os.Bundle;
import androidx.collection.ArrayMap;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.kotlin.android.user.UserManager;
import com.kotlin.android.user.login.UserLoginKt;
import com.mtime.frame.App;
import com.mtime.frame.BaseActivity;
import com.mtime.BuildConfig;
import com.mtime.R;
import com.mtime.base.utils.MTimeUtils;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.mine.activity.FeedBackListActivity;
import com.mtime.bussiness.mine.bean.FeedBackMainBean;
import com.mtime.bussiness.mine.bean.FeedbackAwardTipsBean;
import com.mtime.bussiness.mine.login.activity.LoginActivity;
import com.mtime.bussiness.ticket.movie.bean.BaseResultJsonBean;
import com.mtime.common.utils.TextUtil;
import com.mtime.network.ConstantUrl;
import com.mtime.network.RequestCallback;
import com.mtime.util.CustomAlertDlg;
import com.mtime.util.HttpUtil;
import com.mtime.util.UIUtil;
import com.mtime.widgets.BaseTitleView.ActionType;
import com.mtime.widgets.BaseTitleView.ITitleViewLActListener;
import com.mtime.widgets.BaseTitleView.StructType;
import com.mtime.widgets.TitleOfNormalView;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FeedBackActivity extends BaseActivity {
    private EditText           txtContact;
    private TextView           txtFeedContent = null;
    private TextView             btnSaveFeed    = null;
    
    private OnClickListener    saveFeedClick  = null;
    
    private BaseResultJsonBean result;
    private CustomAlertDlg     mCustomAlertDig;
    private CustomAlertDlg     cancelAlertDig;
    
    private RequestCallback    feedCallback;
    
    // private ArrayAdapter<FeedBackTypeItem> spAdapter = null;
    private RequestCallback    feedBackListCallback;
    private RequestCallback feedBacAwardTipsCallback;
    private String             timespan;
    private FeedBackMainBean   feedBackMainBean;
    
    private EditText           emailView;
    private View               emailSeperate;
    
    @Override
    protected void onInitVariable() {
        setPageLabel("nativeFeedback");
    }
    
    @Override
    protected void onInitView(final Bundle savedInstanceState) {
        this.setContentView(R.layout.activity_feedback);
        View navBar = this.findViewById(R.id.navigationbar);
        new TitleOfNormalView(this, navBar, StructType.TYPE_NORMAL_SHOW_BACK_TITLE_FEEDBACKLIST, getString(R.string.str_feedback),
                new ITitleViewLActListener() {
                    
                    @Override
                    public void onEvent(ActionType type, String content) {
                        switch (type) {
                            case TYPE_FEEDBACKLIST:
                                if (!UserManager.Companion.getInstance().isLogin()) {
//                                    Intent intent = new Intent();
//                                    startActivityForResult(LoginActivity.class, intent);
                                    UserLoginKt.gotoLoginPage(FeedBackActivity.this, null, 0);
                                    return;
                                }
                                long feedBackTime = App.getInstance().getPrefsManager()
                                        .getLong("feedback_time");
                                if (feedBackTime == 0) {
                                    final Date serverDate = MTimeUtils.getLastDiffServerDate();
                                    long nowTime = (serverDate.getTime()) / 1000;
                                    timespan = String.valueOf(nowTime);
                                }
                                else {
                                    timespan = String.valueOf((feedBackTime) / 1000);
                                }
                                
                                UIUtil.showLoadingDialog(FeedBackActivity.this);
                                
                                // Mobile/FeedBackList.api?timespan={0}
                                Map<String, String> param = new HashMap<>(1);
                                param.put("timespan", timespan);
                                HttpUtil.get(ConstantUrl.FEED_BACK_LIST, param, FeedBackMainBean.class, feedBackListCallback);
                            default:
                                break;
                        }
                        
                    }
                });
        
        btnSaveFeed = findViewById(R.id.btn_savefeedback);
        txtFeedContent = findViewById(R.id.txt_feedcontent);
        txtContact = findViewById(R.id.txt_contanct);
        
        this.emailSeperate = this.findViewById(R.id.email_seperate);
        this.emailView = this.findViewById(R.id.email);
        if (!UserManager.Companion.getInstance().isLogin()) {
            this.emailSeperate.setVisibility(View.VISIBLE);
            this.emailView.setVisibility(View.VISIBLE);
        }

        //
        Intent intent = this.getIntent();
        if (null != intent && !TextUtils.isEmpty(intent.getStringExtra("cinema_info"))) {
            txtFeedContent.setText(intent.getStringExtra("cinema_info"));
            // 调整焦点位置
            txtFeedContent.requestFocus();
        }

    }
    
    @Override
    protected void onInitEvent() {

        saveFeedClick = new OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String errorTips = validate();
                if (!TextUtils.isEmpty(errorTips)) {
                    showAlertDlg(errorTips, null);
                    return;
                }
                
                final String typeId = Integer.toString(getTypeID());
                final String content = getContent();
                String mobile, email = "";
                if (UserManager.Companion.getInstance().isLogin()) {
                    mobile = getBindPhone();
                    
                    if (TextUtils.isEmpty(mobile) && null != UserManager.Companion.getInstance().getUser()) {
                        String userEmail = UserManager.Companion.getInstance().getEmail();
                        if(!TextUtils.isEmpty(userEmail)) {
                            email = userEmail;
                        }
                        String bindMobile = UserManager.Companion.getInstance().getBindMobile();
                        if (TextUtil.isMobileNO(bindMobile)) {
                            mobile = bindMobile;
                        }
                    }
                }
                else {
                    email = getEmail();
                    mobile = getContact();
                }
                
                Map<String, String> parameterList = new ArrayMap<String, String>(6);
                parameterList.put("type", typeId);
                parameterList.put("email", email);
                parameterList.put("mobile", mobile);
                parameterList.put("content", content);
                parameterList.put("version", BuildConfig.VERSION_NAME);
                parameterList.put("osversion", String.valueOf(android.os.Build.VERSION.SDK_INT));
                UIUtil.showLoadingDialog(FeedBackActivity.this);
                HttpUtil.post(ConstantUrl.FEED_BACK, parameterList, BaseResultJsonBean.class, feedCallback);
            }
        };
        
        feedCallback = new RequestCallback() {
            @Override
            public void onSuccess(final Object o) {
                
                result = (BaseResultJsonBean) o;
                UIUtil.dismissLoadingDialog();

                if (null != result && result.isSuccess()) {
                    String successMsg;
                    if (UserManager.Companion.getInstance().isLogin()) {
                        successMsg = FeedBackActivity.this.getString(R.string.st_feedback_success_login);
                    }
                    else {
                        successMsg = FeedBackActivity.this.getString(R.string.st_feedback_success_nologin);
                    }

                    showAlertDlg(successMsg, hideDlgAndReturnClickListener);
                }
                else {
                    final String failMsg = getResources().getString(R.string.feedback_feedFail);
                    showAlertDlg(failMsg, null);
                }
            }
            
            @Override
            public void onFail(final Exception e) {
                UIUtil.dismissLoadingDialog();
                // btnSaveFeed.setEnabled(true);
                // btnSaveFeed.setBackgroundResource(R.drawable.btn_orange_selector);
                // btnSaveFeed.setTextColor(FeedBackActivity.this.getResources().getColor(R.color.white));
                
            }
        };
        feedBackListCallback = new RequestCallback() {
            
            @Override
            public void onSuccess(Object o) {
                UIUtil.dismissLoadingDialog();
                
                Intent intent = new Intent();
                feedBackMainBean = (FeedBackMainBean) o;
                
                if (feedBackMainBean!=null&&feedBackMainBean.getMessages().size() > 0) {
                    intent.putExtra(App.getInstance().KEY_FEEDBACK_MAIN, feedBackMainBean);
                    startActivity(FeedBackListActivity.class, intent);
                }
                else {
                    MToastUtils.showShortToast(R.string.st_feedback_norecoder);
                }
            }
            
            @Override
            public void onFail(Exception e) {
                UIUtil.dismissLoadingDialog();
                
                MToastUtils.showShortToast(getString(R.string.st_feedback_recoder_failed)+":"+e.getLocalizedMessage());
            }
        };

        //意见反馈－有奖反馈文字回调
        feedBacAwardTipsCallback = new RequestCallback() {

            @Override
            public void onSuccess(Object o) {

                FeedbackAwardTipsBean feedbackAwardTipsBean = (FeedbackAwardTipsBean)o;
                if(null != feedbackAwardTipsBean) {
                    String awardDesc = feedbackAwardTipsBean.getDesc();
                    if (!TextUtils.isEmpty(awardDesc)) {
                        TextView feedbackAwardDesc = findViewById(R.id.feedback_awarddesc);
                        feedbackAwardDesc.setVisibility(View.VISIBLE);
                        feedbackAwardDesc.setText(awardDesc);
                    }
                }
            }

            @Override
            public void onFail(Exception e) {

            }
        };
        
        btnSaveFeed.setOnClickListener(saveFeedClick);
    }
    
    @Override
    protected void onLoadData() {
        
    }
    
    @Override
    protected void onUnloadData() {
        
    }
    
    @Override
    protected void onRequestData() {
        //有奖反馈描述
        HttpUtil.get(ConstantUrl.FEED_BACK_AWARD_TIPS, FeedbackAwardTipsBean.class, feedBacAwardTipsCallback);
    }
    
    // /获取意见类型id
    private int getTypeID() {
        return 0;
    }
    
    // 错误提示对话框
    private void showAlertDlg(final String message, final View.OnClickListener onclick) {
        if (!canShowDlg) {
            return;
        }
        mCustomAlertDig = new CustomAlertDlg(this, CustomAlertDlg.TYPE_OK);
        mCustomAlertDig.setBtnOKListener(new View.OnClickListener() {
            
            @Override
            public void onClick(final View v) {
                mCustomAlertDig.dismiss();
            }
        });
        mCustomAlertDig.show();
        if (onclick != null) {
            mCustomAlertDig.setBtnOKListener(onclick);
        }
        mCustomAlertDig.getTextView().setText(message);
    }
    
    // 提交反馈成功，返回
    private final View.OnClickListener hideDlgAndReturnClickListener = new View.OnClickListener() {
                                                                         
                                                                         @Override
                                                                         public void onClick(final View v) {
                                                                             if (null != mCustomAlertDig && mCustomAlertDig.isShowing()) {
                                                                                 mCustomAlertDig.dismiss();
                                                                             }
                                                                             finish();
                                                                         }
                                                                     };
    
    // 获取意见内容
    private String getContent() {
        return txtFeedContent.getText().toString().trim();
    }
    
    private String getEmail() {
        return   this.emailView.getText().toString();
    }
    
    // 获取联系方式
    private String getContact() {
        return    txtContact.getText().toString().trim();
    }
    
    private String getBindPhone() {
        String mobile = getContact();
        if (TextUtils.isEmpty(mobile) && null != UserManager.Companion.getInstance().getUser()) {
            mobile = UserManager.Companion.getInstance().getBindMobile();
        }
        
        return mobile;
    }
    
    @Override
    public boolean onKeyUp(final int keyCode, final KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            cancel();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    private void cancel() {
        if (TextUtil.stringIsNotNull(getContent())) {
            final String message = getResources().getString(R.string.feedback_cancelmessage);
            
            cancelAlertDig = new CustomAlertDlg(this, CustomAlertDlg.TYPE_OK_CANCEL);
            cancelAlertDig.setBtnOKListener(new OnClickListener() {
                
                @Override
                public void onClick(final View v) {
                    finish();
                }
            });
            cancelAlertDig.setBtnCancelListener((new OnClickListener() {
                
                @Override
                public void onClick(final View v) {
                    cancelAlertDig.dismiss();
                }
            }));
            cancelAlertDig.show();
            cancelAlertDig.getTextView().setText(message);
            cancelAlertDig.show();
        }
        else {
            finish();
        }
    }
    
    /**
     * 
     * @return
     */
    private String validate() {
        String result = "";
        String content = getContent();
        String contact = getContact();
        String email = getEmail();
        
        if (TextUtils.isEmpty(content)) {
            result = getString(R.string.st_feedback_input_content);
        }
        else {
            if (!UserManager.Companion.getInstance().isLogin() && (TextUtils.isEmpty(email))) {
                result = getString(R.string.st_feedback_input_email_address);
            }
            if (!TextUtils.isEmpty(contact) && !TextUtil.isMobileNO(contact)) {
                result = getString(R.string.st_feedback_input_right_telphone);
            }
        }
        return result;
        
    }
}
