package com.jssdk;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.jssdk.beans.NewShowShareBean;
import com.jssdk.listener.JSNewShowShareListener;
import com.tencent.smtt.sdk.WebView;

import com.google.gson.Gson;
import com.jssdk.beans.CommonBean;
import com.jssdk.beans.HandleGoBackBean;
import com.jssdk.beans.HandleTelBean;
import com.jssdk.beans.NavBarStatusBean;
import com.jssdk.beans.OpenAppLinkBean;
import com.jssdk.beans.OpenImageBrowser;
import com.jssdk.beans.ShowBirthdayLevelPopPageBean;
import com.jssdk.beans.ShowShareBean;
import com.jssdk.beans.ShowVideoPlayerBean;
import com.jssdk.listener.JSGetNetStatusListener;
import com.jssdk.listener.JSHandleGoBackListener;
import com.jssdk.listener.JSHandleDialTelListener;
import com.jssdk.listener.JSNavBarStatusListener;
import com.jssdk.listener.JSOpenAppLinkListener;
import com.jssdk.listener.JSOpenImageBrowserListener;
import com.jssdk.listener.JSShowBirthdayLevelPopPageListener;
import com.jssdk.listener.JSShowShareListener;
import com.jssdk.listener.JSShowVideoPlayerListener;
import com.jssdk.listener.JSTotalListener;


/**
 * Created by guangshun on 16/12/15.
 */

public class JSCenter {
    private final WebView mX5WebView;
    private final android.webkit.WebView mWebView;

    private JSTotalListener mJSTotalListener;
    private JSGetNetStatusListener jsGetNetStatusListener;
    private JSOpenImageBrowserListener jsOpenImageBrowserListener;
    private JSOpenAppLinkListener jsOpenAppLinkListener;
    private JSNavBarStatusListener jsNavBarStatusListener;
    private JSHandleGoBackListener jsHandleGoBackListener;
    private JSShowShareListener jsShowShareListener;
    private JSShowBirthdayLevelPopPageListener jsShowBirthdayLevelPopPageListener;
    private JSShowVideoPlayerListener jsShowVideoPlayerListener;
    private JSHandleDialTelListener mJSHandleDialTelListener;
    private JSNewShowShareListener mJSNewShowShareListener;

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public JSCenter(WebView x5WebView, android.webkit.WebView webView) {
        mX5WebView = x5WebView;
        mWebView = webView;
    }
    
    //----------------------------------------------------------------------------------------------
    
    /**
     * 如果H5需要回执，执行完操作后，返回给H5结果
     *
     * @param result
     */
    public void callJS(String result) {
        if (TextUtils.isEmpty(result)) {
            return;
        }
        final String callJS;
        if (!result.startsWith("javascript:")) {
            callJS = "javascript:" + result;
        } else {
            callJS = result;
        }
        mHandler.post(new Runnable() {
            public void run() {
                if(null != mX5WebView) {
                    mX5WebView.loadUrl(callJS);
                } else if(null != mWebView) {
                    mWebView.loadUrl(callJS);
                }
            }
        });
    }
    
    /**
     * 如果H5需要回执，执行完操作后，返回给H5结果
     *
     * @param callbackName js返回的callbackName
     * @param result       传递给H5的内容
     */
    public void callJS(String callbackName, String result) {
        callJS(String.format("javascript:%1$s(%2$s)", callbackName, result));
    }
    
    public <T> T handleJson(final String data, final Class<T> className) {
        
        final Gson gson = new Gson();
        final Object bean = gson.fromJson(data, className);
        return (T) bean;
    }
    
    public WebView getX5WebView() {
        return mX5WebView;
    }
    
    public android.webkit.WebView getWebView() {
        return mWebView;
    }
    
    public void setJSTotalListener(JSTotalListener JSTotalListener) {
        mJSTotalListener = JSTotalListener;
    }
    
    ///////////////////////////////////////iOS 10.0.0 & Android 6.0.0 开始支持的类型/////////////////////////////////////////////////
    
    public void setJsGetNetStatusListener(JSGetNetStatusListener jsGetNetStatusListener) {
        this.jsGetNetStatusListener = jsGetNetStatusListener;
    }
    
    protected void getNativeNetStatus(String jsonStr) {
        CommonBean commonBean = handleJson(jsonStr, CommonBean.class);
        if (null != jsGetNetStatusListener) {
            jsGetNetStatusListener.getNativeNetStatus(commonBean);
        }
        if(null != mJSTotalListener) {
            mJSTotalListener.getNativeNetStatus(commonBean);
        }
    }
    
    public void setJsOpenImageBrowserListener(JSOpenImageBrowserListener jsOpenImageBrowserListener) {
        this.jsOpenImageBrowserListener = jsOpenImageBrowserListener;
    }
    
    protected void openImageBrowser(String jsonStr) {//需要添加refer
        OpenImageBrowser commonBean = handleJson(jsonStr, OpenImageBrowser.class);
        if (null != jsOpenImageBrowserListener) {
            jsOpenImageBrowserListener.openImageBrowser(commonBean);
        }
        if(null != mJSTotalListener) {
            mJSTotalListener.openImageBrowser(commonBean);
        }
    }
    
    ///////////////////////////////////////iOS 10.3.0 & Android 6.3.0 开始支持的类型/////////////////////////////////////////////////
    
    public void setJsOpenAppLinkListener(JSOpenAppLinkListener jsOpenAppLinkListener) {
        this.jsOpenAppLinkListener = jsOpenAppLinkListener;
    }
    
    protected void openAppLinkClient(String jsonStr) {
        OpenAppLinkBean appLinkBean = handleJson(jsonStr, OpenAppLinkBean.class);
        if (null != jsOpenAppLinkListener) {
            jsOpenAppLinkListener.openAppLinkClient(appLinkBean);
        }
        if(null != mJSTotalListener) {
            mJSTotalListener.openAppLinkClient(appLinkBean);
        }
    }
    
    public void setJsNavBarStatusListener(JSNavBarStatusListener jsNavBarStatusListener) {
        this.jsNavBarStatusListener = jsNavBarStatusListener;
    }
    
    protected void setNavBarStatus(String jsonStr) {
        NavBarStatusBean navBarStatusBean = handleJson(jsonStr, NavBarStatusBean.class);
        if (null != jsNavBarStatusListener) {
            jsNavBarStatusListener.setNavBarStatus(navBarStatusBean);
        }
        if(null != mJSTotalListener) {
            mJSTotalListener.setNavBarStatus(navBarStatusBean);
        }
    }
    
    public void setJsHandleGoBackListener(JSHandleGoBackListener jsHandleGoBackListener) {
        this.jsHandleGoBackListener = jsHandleGoBackListener;
    }
    
    protected void handleGoBack(String jsonStr) {
        HandleGoBackBean handleGoBackBean = handleJson(jsonStr, HandleGoBackBean.class);
        if (null != jsHandleGoBackListener) {
            jsHandleGoBackListener.handleGoBack(handleGoBackBean);
        }
        if(null != mJSTotalListener) {
            mJSTotalListener.handleGoBack(handleGoBackBean);
        }
    }
    
    public void setJsShowShareListener(JSShowShareListener jsShowShareListener) {
        this.jsShowShareListener = jsShowShareListener;
    }
    
    protected void showShare(String jsonStr) {
        ShowShareBean showShareBean = handleJson(jsonStr, ShowShareBean.class);
        if (null != jsShowShareListener) {
            jsShowShareListener.showShare(showShareBean);
        }
        if(null != mJSTotalListener) {
            mJSTotalListener.showShare(showShareBean);
        }
    }
    
    public void setJsShowBirthdayLevelPopPageListener(JSShowBirthdayLevelPopPageListener listener) {
        this.jsShowBirthdayLevelPopPageListener = listener;
    }
    
    protected void showBirthdayLevelPopPage(String jsonStr) {
        ShowBirthdayLevelPopPageBean bean = handleJson(jsonStr, ShowBirthdayLevelPopPageBean.class);
        if (null != jsShowBirthdayLevelPopPageListener) {
            jsShowBirthdayLevelPopPageListener.showBirthdayLevelPopPage(bean);
        }
        if(null != mJSTotalListener) {
            mJSTotalListener.showBirthdayLevelPopPage(bean);
        }
    }
    
    ///////////////////////////////////////iOS 10.6.0 & Android 6.6.0 开始支持的类型/////////////////////////////////////////////////
    
    public void setJsShowVideoPlayerListener(JSShowVideoPlayerListener listener) {
        this.jsShowVideoPlayerListener = listener;
    }
    
    protected void showVideoPlayer(String jsonStr) {
        ShowVideoPlayerBean bean = handleJson(jsonStr, ShowVideoPlayerBean.class);
        if (null != jsShowVideoPlayerListener) {
            jsShowVideoPlayerListener.showVideoPlayer(bean);
        }
        if(null != mJSTotalListener) {
            mJSTotalListener.showVideoPlayer(bean);
        }
    }
    
    //pragma mark - iOS 10.11.0 & Android 6.11.0 开始支持的类型
    
    public void setJSHandleDialTelListener(JSHandleDialTelListener JSHandleDialTelListener) {
        mJSHandleDialTelListener = JSHandleDialTelListener;
    }
    
    protected void handleDialTel(String jsonStr) {
        HandleTelBean bean = handleJson(jsonStr, HandleTelBean.class);
        if(null != mJSHandleDialTelListener) {
            mJSHandleDialTelListener.handleDialTel(bean);
        }
        if(null != mJSTotalListener) {
            mJSTotalListener.handleDialTel(bean);
        }
    }

    //pragma mark - iOS 12.2.0 & Android 8.2.0 开始支持的类型

    public void setJSNewShowShareListener(JSNewShowShareListener listener) {
        mJSNewShowShareListener = listener;
    }

    protected void newShowShare(String jsonStr) {
        NewShowShareBean bean = handleJson(jsonStr, NewShowShareBean.class);
        if(null != mJSNewShowShareListener) {
            mJSNewShowShareListener.newShowShare(bean);
        }
        if(null != mJSTotalListener) {
            mJSTotalListener.newShowShare(bean);
        }
    }
}
