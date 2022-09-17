package com.mtime.network;

import android.os.Build;
import android.provider.Settings;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.mtime.frame.App;
import com.mtime.BuildConfig;
import com.mtime.constant.FrameConstant;
import com.mtime.util.ToolsUtils;

/**
 * Created by LiJiaZhi on 2017/9/8.
 *
 * 所有UA处理操作
 * http://wiki.inc-mtime.com/pages/viewpage.action?pageId=82313229
 */

public class UAUtils {
    /**
     * http api UA
     * @return
     */
    public static String apiUA(){
        StringBuilder sb = new StringBuilder();
        sb.append(" Mtime_Android_App").append(getCommonUAParams(0));
        return sb.toString();
    }

    /**
     * 打开内嵌H5时，其他模块传
      * @param webView
     */
    public static void changeWebViewUA(WebView webView) {
        if (null == webView) {
            return;
        }

        WebSettings webSettings = webView.getSettings();
        webSettings.setDatabaseEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        StringBuilder sb = new StringBuilder();
        sb.append(webSettings.getUserAgentString()).append(" Mtime_Android_Showtime")
                .append(getCommonUAParams(0));
        webSettings.setUserAgentString(sb.toString());
    }

    public static String changeWebUA(String origin, String special) {
        StringBuilder sb = new StringBuilder();
        sb.append(origin).append(' ').append(special).append(getCommonUAParams(0));
        return sb.toString();
    }

    // 获取webview Ua
    public static String getWebViewUA() {
        StringBuilder sb = new StringBuilder();
        sb.append(" Mtime_Android_Showtime").append(getCommonUAParams(0));
        return sb.toString();
    }

        /**
         * 打开内嵌H5时，商城模块
         * @param webView
         * @param height
         */
    public static void changeMallWebViewUA(WebView webView, int height) {
        if (null == webView) {
            return;
        }
        WebSettings webSettings = webView.getSettings();
        StringBuilder sb = new StringBuilder();
        sb.append(webSettings.getUserAgentString()).append(" Mtime_Android_Showtime_Hybird")
                .append(getCommonUAParams(height));
        webSettings.setUserAgentString(sb.toString());
    }

    /**
     * 获取通用UA参数
     * http://wiki.inc-mtime.com/pages/viewpage.action?pageId=82313229
     *
     * @return
     */
    public static String getCommonUAParams(int height){
        StringBuilder sb = new StringBuilder();
        sb.append("/channel_").append(FrameConstant.CHANNEL_ID).append("/").append(BuildConfig.VERSION_NAME)
                .append("(WebView Width ").append(FrameConstant.SCREEN_WIDTH)
                .append(" Height ").append(FrameConstant.SCREEN_HEIGHT - height).append(")")
                .append(" (Device ").append(android.os.Build.MODEL).append(")")
                .append(" (Token ").append(ToolsUtils.getToken(App.getInstance())).append(")")
                .append(" (UDID ").append(ToolsUtils.getToken(App.getInstance())).append(")")
                .append(" (Brand ").append(Build.BRAND).append(")");
        return sb.toString();
    }
}
