package com.jssdk;

import android.webkit.JavascriptInterface;

import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

/**
 * Created by guangshun on 16/12/15.
 * js调用原生的接口，所有的js调用原生的方法放在此类
 */

public class JSInterfaceNative {
    private JSCenter jsCenter;

    public void setupWebView(WebView x5WebView, android.webkit.WebView webView, String name) {
        if (null != x5WebView) {
            WebSettings webSettings = x5WebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            x5WebView.addJavascriptInterface(this, name);
        } else if (null != webView) {
            android.webkit.WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webView.addJavascriptInterface(this, name);
        }
        jsCenter = new JSCenter(x5WebView, webView);
    }

    public JSInterfaceNative(WebView x5WebView, String name) {
        setupWebView(x5WebView, null, name);
    }

    public JSInterfaceNative(android.webkit.WebView webView, String name) {
        setupWebView(null, webView, name);
    }

    public JSCenter getJsCenter() {
        return jsCenter;
    }


    ///////////////////////////////////////iOS 10.0.0 & Android 6.0.0 开始支持的类型/////////////////////////////////////////////////

    /**
     * 1.获取客户端网络状态
     * 注 :   "nativeNetStatus" : 1 // 0 : 网络不可用 ; 1 : wifi; 2 : 2G;3 : 3G;4 : 4G;99 : 未知网络
     *
     * @param jsonStr
     */
    @JavascriptInterface
    public void getNativeNetStatus(String jsonStr) {
        if (null != jsCenter) {
            jsCenter.getNativeNetStatus(jsonStr);
        }
    }

    /**
     * 2.打开图片浏览
     *
     * @param jsonStr
     */
    @JavascriptInterface
    public void openImageBrowser(String jsonStr) {
        if (null != jsCenter) {
            jsCenter.openImageBrowser(jsonStr);
        }
    }


    ///////////////////////////////////////iOS 10.3.0 & Android 6.3.0 开始支持的类型/////////////////////////////////////////////////

    /**
     * 3.统一的applink跳转
     *
     * @param jsonStr
     */
    @JavascriptInterface
    public void openAppLinkClient(String jsonStr) {
        if (null != jsCenter) {
            jsCenter.openAppLinkClient(jsonStr);
        }
    }

    /**
     * 4.设置导航栏
     *
     * @param jsonStr
     */
    @JavascriptInterface
    public void setNavBarStatus(String jsonStr) {
        if (null != jsCenter) {
            jsCenter.setNavBarStatus(jsonStr);
        }
    }

    /**
     * 5.回到上一级原生页面（有可能刷新）
     *
     * @param jsonStr
     */
    @JavascriptInterface
    public void handleGoBack(String jsonStr) {
        if (null != jsCenter) {
            jsCenter.handleGoBack(jsonStr);
        }
    }

    /**
     * 6.显示分享弹框
     *
     * @param jsonStr
     */
    @JavascriptInterface
    public void showShare(String jsonStr) {
        if (null != jsCenter) {
            jsCenter.showShare(jsonStr);
        }
    }

    /**
     * 7.显示生日/等级礼包弹窗
     *
     * @param jsonStr
     */
    @JavascriptInterface
    public void showBirthdayLevelPopPage(String jsonStr) {
        if (null != jsCenter) {
            jsCenter.showBirthdayLevelPopPage(jsonStr);
        }
    }

    ///////////////////////////////////////iOS 10.6.0 & Android 6.6.0 开始支持的类型/////////////////////////////////////////////////

    /**
     * 8.显示播放器控件
     *
     * @param jsonStr
     */
    @JavascriptInterface
    public void showVideoPlayer(String jsonStr) {
        if (null != jsCenter) {
            jsCenter.showVideoPlayer(jsonStr);
        }
    }

    //pragma mark - iOS 10.11.0 & Android 6.11.0 开始支持的类型

    /**
     * 9.拨打电话
     *
     * @param jsonStr
     */
    @JavascriptInterface
    public void handleDialTel(String jsonStr) {
        if (null != jsCenter) {
            jsCenter.handleDialTel(jsonStr);
        }
    }

    /**
     * 10. 新的显示分享弹框
     * #pragma mark - iOS 12.2.0 & Android 8.2.0 开始支持的类型
     * 调起分享界面时，H5将下面的参数传给App，App通过下面的参数请求Api 1.5 获取分享信息（utility/share.api）获取分享数据
     * @param jsonStr
     */
    @JavascriptInterface
    public void newShowShare(String jsonStr) {
        if (null != jsCenter) {
            jsCenter.newShowShare(jsonStr);
        }
    }
}
