package com.mtime.bussiness.common;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;

import com.kk.taurus.uiframe.v.BaseStateContainer;
import com.kk.taurus.uiframe.v.BaseTitleBarHolder;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.R;
import com.mtime.bussiness.common.holder.AbsWebViewContentHolder;
import com.mtime.bussiness.common.holder.AbsWebViewTitleBarHolder;
import com.mtime.bussiness.common.widget.Browser;
import com.mtime.frame.BaseFrameUIActivity;
import com.mtime.network.UAUtils;
import com.tencent.smtt.sdk.ValueCallback;

import static com.kotlin.android.ktx.ext.KeyExtKt.KEY_WEB_OF_AD_TAG;
import static com.kotlin.android.ktx.ext.KeyExtKt.KEY_WEB_OF_IS_HORIZONTAL_SCREEN;
import static com.kotlin.android.ktx.ext.KeyExtKt.KEY_WEB_OF_PAGE_LABEL;
import static com.kotlin.android.ktx.ext.KeyExtKt.KEY_WEB_OF_SHOW_BACK;
import static com.kotlin.android.ktx.ext.KeyExtKt.KEY_WEB_OF_SHOW_CLOSE;
import static com.kotlin.android.ktx.ext.KeyExtKt.KEY_WEB_OF_SHOW_TITLE;
import static com.kotlin.android.ktx.ext.KeyExtKt.KEY_WEB_OF_URL;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2018-05-25
 */
public abstract class AbsWebViewActivity extends BaseFrameUIActivity<String, AbsWebViewContentHolder>
        implements AbsWebViewTitleBarHolder.ActionCallback, AbsWebViewContentHolder.WebViewAction {

    protected final static String JSNAME = "mtime";
    protected final static String KEY_OF_SHOW_TITLE = KEY_WEB_OF_SHOW_TITLE;
    protected final static String KEY_OF_URL  = KEY_WEB_OF_URL;
    protected final static String KEY_OF_SHOW_BACK = KEY_WEB_OF_SHOW_BACK;
    protected final static String KEY_OF_SHOW_CLOSE = KEY_WEB_OF_SHOW_CLOSE;
    protected final static String KEY_OF_AD_TAG = KEY_WEB_OF_AD_TAG;
    protected final static String KEY_OF_PAGE_LABEL = KEY_WEB_OF_PAGE_LABEL;
    protected final static String KEY_OF_IS_HORIZONTAL_SCREEN = KEY_WEB_OF_IS_HORIZONTAL_SCREEN;

    protected AbsWebViewTitleBarHolder mTitleBar;
    protected AbsWebViewContentHolder mWebViewContainer;

    @Override
    public ContentHolder onBindContentHolder() {
        return (mWebViewContainer = new AbsWebViewContentHolder(this, this));
    }
    
    @Override
    protected BaseStateContainer getStateContainer() {
        return super.getStateContainer();
    }
    
    @Override
    public BaseTitleBarHolder onBindTitleBarHolder() {
        return (mTitleBar = new AbsWebViewTitleBarHolder(this, this));
    }
    
    @Override
    protected void onInit(Bundle savedInstanceState) {
        super.onInit(savedInstanceState);
        boolean showTitle = getIntent().getBooleanExtra(KEY_OF_SHOW_TITLE, true);
        boolean showBack = getIntent().getBooleanExtra(KEY_OF_SHOW_BACK, true);
        boolean showClose = getIntent().getBooleanExtra(KEY_OF_SHOW_CLOSE, true);
        boolean isHorizontalScreen = getIntent().getBooleanExtra(KEY_OF_IS_HORIZONTAL_SCREEN, false);
        String url = getIntent().getStringExtra(KEY_OF_URL);
        String adTag = getIntent().getStringExtra(KEY_OF_AD_TAG);
        String pageLabel = getIntent().getStringExtra(KEY_OF_PAGE_LABEL);
    
        mTitleBar.setTitleBarShow(showTitle);
        mTitleBar.setBackShow(showBack);
        mTitleBar.setCloseShow(showClose);
        getUserContentHolder().setAdTag(adTag);
        setPageLabel(pageLabel);
        setData(url);
        if(isHorizontalScreen) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public int onGetTitleHeight() {
        return (int) getResources().getDimension(R.dimen.title_bar_height);
    }

    @Override
    public void onTitleAction(int id) {
    }

    @Override
    public void onBackClicked() {
        onBackPressed();
    }

    @Override
    public void onCloseClicked() {
        finish();
    }

    @Override
    public void onShareClicked() {
        // share
    }

    @Override
    public final void onReceivedTitle(String title) {
        mTitleBar.setTitle(title);
    }

    @Override
    public boolean onInterceptUrlLoading(Browser browser, String url) {
        // 是否拦截 url 加载
        return false;
    }

    @Override
    public boolean onInterceptRequest(Browser browser, String url) {
        return false;
    }

    @Override
    public boolean shouldGoBack() {
        return null != mTitleBar && mTitleBar.isTitleBarShow() && mTitleBar.isBackShow();
    }

    @Override
    public void onConfigBrowser(Browser browser) {
//        JSCenter jsCenter = webView.enableJsSdk(this, JSNAME);
    }

    @Override
    public String onSetWebUA(String originUA) {
        return UAUtils.changeWebUA(originUA, "Mtime_Android_Showtime");
    }
    
    @Override
    public boolean openFileInput(ValueCallback<Uri> fileUploadCallbackFirst, ValueCallback<Uri[]> fileUploadCallbackSecond) {
        return false;
    }
}
