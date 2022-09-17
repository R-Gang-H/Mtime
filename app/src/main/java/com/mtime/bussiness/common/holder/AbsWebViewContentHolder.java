package com.mtime.bussiness.common.holder;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.R;
import com.mtime.base.widget.X5WebView;
import com.mtime.bussiness.common.widget.Browser;
import com.mtime.bussiness.common.widget.BrowserImpl;
import com.mtime.network.CookiesHelper;
import com.mtime.statistic.large.StatisticManager;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2018-05-25
 */
public class AbsWebViewContentHolder extends ContentHolder<String> {
    private final String PROCESSNAME = "com.mtime";

    private BrowserImpl mWebView;
    private final WebViewAction mAction;
    private ProgressBar mProgressBar;
    private TextView mAdTagView;
    private View mErrorView;

    public AbsWebViewContentHolder(Context context, WebViewAction action) {
        super(context);
        mAction = action;
    }

    @Override
    public void onHolderCreated() {
        super.onHolderCreated();
        setContentView(R.layout.act_base_web_view_content);
        mWebView = getViewById(R.id.common_base_web_view);
        mAdTagView = getViewById(R.id.common_base_web_view_ad_tag_tv);
        mProgressBar = getViewById(R.id.common_base_web_view_progress);
        mErrorView = getViewById(R.id.err_load);
        mErrorView.setOnClickListener(this);
        mWebView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        initWebSettings();
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
        ViewParent parent = mWebView.getParent();
        if(null != parent) {
            ((ViewGroup)parent).removeView(mWebView);
        }
        mWebView.getSettings().setJavaScriptEnabled(false);
        mWebView.getSettings().setSupportZoom(false);
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.clearHistory();
        mWebView.removeAllViews();
        mWebView.destroy();
        mWebView = null;
    }

    @Override
    public void onDataChanged(String url) {
        super.onDataChanged(url);
        fillCookie(url);
        mWebView.loadUrl(url);
    }
    
    /**
     * 设置广告tag
     * @param tag
     */
    public void setAdTag(String tag) {
        if(null != mAdTagView) {
            if(!TextUtils.isEmpty(tag)) {
                mAdTagView.setText(tag);
                mAdTagView.setVisibility(View.VISIBLE);
            } else {
                mAdTagView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 返回  browser 实例
     *
     * @return browser
     */
    public Browser getBrowser() {
        return mWebView;
    }

    /**
     * 返回 webview 实例
     *
     * @return webview
     */
    public X5WebView getWebView() {
        return mWebView;
    }

    @Override
    public boolean onBackPressed() {
        if (mAction.shouldGoBack()) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            }
        }
        return super.onBackPressed();
    }

    private void initWebSettings() {
        mWebView.setInitialScale(100);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        String originUA = webSettings.getUserAgentString();

        mAction.onConfigBrowser(mWebView);

        webSettings.setUserAgentString(mAction.onSetWebUA(originUA));
        mWebView.setWebViewClient(new BaseViewClient());
        mWebView.setWebChromeClient(new BaseChromeClient());
        //监听下载
        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(url));
                mContext.startActivity(intent);
                mWebView.stopLoading();
            }
        });
        //https://www.jianshu.com/p/7279e36d932b
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = getProcessName(mWebView.getContext());
            if (!PROCESSNAME.equals(processName)){//判断不等于默认进程名称
                WebView.setDataDirectorySuffix(processName);}
        }
    }

    public  String getProcessName(Context context) {
        if (context == null) return null;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == android.os.Process.myPid()) {
                return processInfo.processName;
            }
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.err_load) {
            mErrorView.setVisibility(View.GONE);
            mWebView.setVisibility(View.VISIBLE);
            mWebView.reload();
        }
    }

    /**
     * 为 指定 url 设置 cookie
     *
     * @param url url
     */
    public void fillCookie(String url) {
        CookiesHelper.setHttpCookiesToX5WebView(mWebView, url, StatisticManager.getInstance().getH5CookieStr());
    }

    private class BaseViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!mAction.onInterceptUrlLoading(mWebView, url)) {
                view.loadUrl(url);
            }
            return true;
        }

        @Override
        public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, com.tencent.smtt.export.external.interfaces.SslError sslError) {
            sslErrorHandler.proceed();
        }
    
        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            if (mAction.onInterceptRequest(mWebView, url)) {
                return null;
            }
            return super.shouldInterceptRequest(view, url);
        }
    
        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                    && mAction.onInterceptRequest(mWebView, request.getUrl().toString())) {
                return null;
            }
            return super.shouldInterceptRequest(view, request);
        }
    
        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            mWebView.stopLoading();
            mWebView.setVisibility(View.GONE);
            mErrorView.setVisibility(View.VISIBLE);
        }
    
    }

    private class BaseChromeClient extends WebChromeClient {
        // file upload callback (Android 3.0 (API level 11) -- Android 4.0 (API level 15)) (hidden method)
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            openFileChooser(uploadMsg, acceptType, null);
        }
    
        // file upload callback (Android 4.1 (API level 16) -- Android 4.3 (API level 18)) (hidden method)
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            mAction.openFileInput(uploadMsg, null);
        }
    
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            return mAction.openFileInput(null, filePathCallback);
        }
    
        @Override
        public void onReceivedTitle(WebView view, String title) {
            mAction.onReceivedTitle(title);
        }

        @Override
        public void onProgressChanged(WebView view, int progress) {
            if (progress == 100) {
                mProgressBar.setVisibility(View.GONE);
            } else {
                mProgressBar.setVisibility(View.VISIBLE);
            }
            mProgressBar.setProgress(progress);
        }
    }

    public interface WebViewAction {
        /**
         * 配置 webview 回调
         *
         * @param browser Browser
         */
        void onConfigBrowser(Browser browser);

        /**
         * 是否自定义 UA
         *
         * @param originUA webview 原始 UA
         * @return 自定义 UA
         */
        String onSetWebUA(String originUA);

        void onReceivedTitle(String title);

        /**
         * 是否拦截 url 加载  see shouldOverrideUrlLoading
         *
         * @param browser browser
         * @param url     url
         * @return true 拦截
         */
        boolean onInterceptUrlLoading(Browser browser, String url);

        /**
         * 是否拦截 url 请求 see  shouldInterceptRequest
         *
         * @param browser browser
         * @param url     url
         * @return true 拦截
         */
        boolean onInterceptRequest(Browser browser, String url);
        
        /**
         * 设置 浏览器能发 后退
         *
         * @return true  可以后退
         */
        boolean shouldGoBack();
    
        //用于WebChromeClient
        boolean openFileInput(final ValueCallback<Uri> fileUploadCallbackFirst, final ValueCallback<Uri[]> fileUploadCallbackSecond);
    }
}
