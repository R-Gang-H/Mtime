package com.mtime.network;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import androidx.collection.ArrayMap;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

import com.mtime.frame.App;
import com.kotlin.android.user.UserManager;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.common.utils.LogWriter;
import com.mtime.common.utils.Utils;
import com.mtime.util.MallUrlHelper;
import com.mtime.util.MallUrlHelper.MallUrlType;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

public class CookiesHelper {
    public static boolean initialize = true;

    public static synchronized void clearCookies() {
        try {
            Context context = App.getInstance().getApplicationContext();

            CookieSyncManager.createInstance(context);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();

            context.deleteDatabase("webview.db");
            context.deleteDatabase("webviewCache.db");
        } catch (Exception e) {

        }
    }

    public static String getCookies(Context context, String url) {
        // clear cookies.
        @SuppressWarnings("unused")
        CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

        return cookieManager.getCookie(url);
    }

    // TODO 这里是保存webview 的cookie到cookie文件的函数。只有在未登录的情况下才会去取webview的cookie进行使用。
    public static void syncWebViewCookies(Context context, String url) {
        if (UserManager.Companion.getInstance().isLogin()) {
            return;
        }
        String strCookies = CookiesHelper.getCookies(context, url);
        if (strCookies != null && !strCookies.equals("")) {
            String[] cookieStrings = strCookies.split(";");
            for (int i = 0, size = cookieStrings.length; i < size; i++) {
                String cookieStr = cookieStrings[i];
//                if (TextUtils.isEmpty(cookieStr)) continue;
//                //这里用一个固定的地址url。随便哪一个。都行。因为mall-wv.mtime.cn识别出来的domain跟其他的不一样。会导致无法添加cookie
//                NetworkManager.getInstance().mCookieJarManager.setCookies(HttpUrl.parse("https://api-m.mtime.cn/AccountDetail.api"), cookieStr);
                HttpUrl httpUrl = HttpUrl.parse("https://api-m.mtime.cn/account/detail.api");
                if (!TextUtils.isEmpty(cookieStr)&&null!= Cookie.parse(httpUrl, cookieStr)) {
                    //这里用一个固定的地址url。随便哪一个。都行。因为mall-wv.mtime.cn识别出来的domain跟其他的不一样。会导致无法添加cookie
//                    NetworkManager.getInstance().mCookieJarManager.setCookies(httpUrl, cookieStr);
                    com.kotlin.android.retrofit.cookie.CookieManager.Companion.getInstance().setCookies(httpUrl, cookieStr);
                }
            }
        }

    }

    /*
     * 同步h5所用到Cookies到http请求中
     */
    public static void syncWebViewMallCartsCookies() {
        Context context = App.getInstance().getApplicationContext();
        syncWebViewCookies(context, MallUrlHelper.getUrl(MallUrlType.CART_NUM));
    }

    public static Date getExpiryDate(int offsetHour) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, offsetHour);
        Date dxpiryDate = calendar.getTime();

        return dxpiryDate;
    }

    //TODO take it out later.
    public static void clearWebViewCache() {
        if (!Utils.sdcardMounted()) {
            return;
        }

        String path = Environment.getExternalStorageDirectory().getPath() + "/mTime/webviewcache/";
        File file = new File(path);
        ArrayList<File> dirList = new ArrayList<File>();
        dirList.add(file);
        while (dirList.size() > 0) {
            File f = dirList.get(0);
            dirList.remove(0);
            File[] files = f.listFiles();
            if (null == files) {
                continue;
            }

            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    dirList.add(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }

        if (null != dirList) {
            dirList.clear();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setHttpCookiesToWebView(final WebView view, final String url, final List<Cookie> cookies) {
        if (null == view || TextUtils.isEmpty(url) || CollectionUtils.isEmpty(cookies)) {
            return;
        }

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(view, true);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(view.getContext());
        }
        cookieManager.removeSessionCookie();
        cookieManager.removeAllCookie();
        // 注入cookies
        for (int i = 0; i < cookies.size(); i++) {
            Cookie cookie = cookies.get(i);
            // 注入cookie要按照domain来设置
            cookieManager.setCookie(TextUtils.isEmpty(cookie.domain()) ? url : cookie.domain(), cookie.toString());
        }
    
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().sync();
        } else {
            cookieManager.flush();
        }
        
        
        

        /*String usedcookies = removeSpeCookies(cookies, "expires");
        if (TextUtils.isEmpty(usedcookies)) {
            return;
        }

        LogWriter.d("checkCookies", "set cookie url:" + url + ", set cookies: " + usedcookies);
        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptThirdPartyCookies(view, true);
            cookieManager.setAcceptCookie(true);
            setNativeCookies(cookieManager, url, usedcookies);
            cookieManager.flush();
        } else {
            Context context = App.getInstance().getApplicationContext();
            CookieSyncManager.createInstance(context);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            setNativeCookies(cookieManager, url, usedcookies);
            CookieSyncManager.getInstance().sync();
        }*/
    }
    
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setHttpCookiesToX5WebView(final com.tencent.smtt.sdk.WebView view, final String url, final List<Cookie> cookies) {
        if (null == view || TextUtils.isEmpty(url) || CollectionUtils.isEmpty(cookies)) {
            return;
        }
        
        com.tencent.smtt.sdk.CookieManager cookieManager = com.tencent.smtt.sdk.CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(view, true);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            com.tencent.smtt.sdk.CookieSyncManager.createInstance(view.getContext());
        }
        cookieManager.removeSessionCookie();
        cookieManager.removeAllCookie();
        // 注入cookies
        for (int i = 0; i < cookies.size(); i++) {
            Cookie cookie = cookies.get(i);
            // 注入cookie要按照domain来设置
            cookieManager.setCookie(TextUtils.isEmpty(cookie.domain()) ? url : cookie.domain(), cookie.toString());
        }
        
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            com.tencent.smtt.sdk.CookieSyncManager.getInstance().sync();
        } else {
            cookieManager.flush();
        }
        
        
        

        /*String usedcookies = removeSpeCookies(cookies, "expires");
        if (TextUtils.isEmpty(usedcookies)) {
            return;
        }

        LogWriter.d("checkCookies", "set cookie url:" + url + ", set cookies: " + usedcookies);
        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptThirdPartyCookies(view, true);
            cookieManager.setAcceptCookie(true);
            setNativeCookies(cookieManager, url, usedcookies);
            cookieManager.flush();
        } else {
            Context context = App.getInstance().getApplicationContext();
            CookieSyncManager.createInstance(context);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            setNativeCookies(cookieManager, url, usedcookies);
            CookieSyncManager.getInstance().sync();
        }*/
    }

    public static String removeSpeCookies(final String cookies, final String removeKey) {
        if (!cookies.contains(";") || TextUtils.isEmpty(removeKey)) {
            return cookies;
        }

        String[] oldCookie = cookies.split(";");
        Map<String, String> values = new ArrayMap(oldCookie.length);
        for (int i = 0; i < oldCookie.length; i++) {
            String[] data = oldCookie[i].split("=");
            if (1 == data.length || TextUtils.isEmpty(data[0]) || removeKey.equalsIgnoreCase(data[0].trim())) {
                continue;
            }

            values.put(data[0], data[1]);
        }

        StringBuffer cookieStringBuffer = new StringBuffer();
        Iterator cookieNames = values.keySet().iterator();
        while (cookieNames.hasNext()) {
            String cookieName = (String) cookieNames.next();
            if (TextUtils.isEmpty(cookieName) || TextUtils.isEmpty(values.get(cookieName))) {
                continue;
            }
            cookieStringBuffer.append(cookieName);
            cookieStringBuffer.append("=");
            cookieStringBuffer.append(values.get(cookieName));
            if (cookieNames.hasNext()) cookieStringBuffer.append(";");
        }

        return cookieStringBuffer.toString();
    }

    public static String removeSpeCookies(final String cookies) {
        if (!cookies.contains(";")) {
            return cookies;
        }

        String[] oldCookie = cookies.split(";");
        Map values = new ArrayMap(oldCookie.length);
        for (int i = 0; i < oldCookie.length; i++) {
            String[] data = oldCookie[i].split("=");
            LogWriter.d("checkCookies", "cookie key:" + data[0]);
            if (1 == data.length || TextUtils.isEmpty(data[0]) ||
                    "domain".equalsIgnoreCase(data[0].trim()) ||
                    "path".equalsIgnoreCase(data[0].trim()) ||
                    "expires".equalsIgnoreCase(data[0].trim())) {
                LogWriter.d("checkCookies", "continue cookie key:" + data[0]);
                continue;
            }

            LogWriter.d("checkCookies", "cookie key2:" + data[0]);
            values.put(data[0], data[1]);
        }

        StringBuffer cookieStringBuffer = new StringBuffer();
        Iterator cookieNames = values.keySet().iterator();
        while (cookieNames.hasNext()) {
            String cookieName = (String) cookieNames.next();

            cookieStringBuffer.append(cookieName);
            cookieStringBuffer.append("=");
            cookieStringBuffer.append((String) values.get(cookieName));
            if (cookieNames.hasNext()) cookieStringBuffer.append(";");
        }

        return cookieStringBuffer.toString();
    }

    public static String getSpeCookies(final String cookies) {
        if (!cookies.contains(";")) {
            return cookies;
        }

        String[] oldCookie = cookies.split(";");
        StringBuffer cookieStringBuffer = new StringBuffer();
        for (int i = 0; i < oldCookie.length; i++) {
            String[] data = oldCookie[i].split("=");
            if (2 == data.length && !TextUtils.isEmpty(data[0]) &&
                    "_mi_".equalsIgnoreCase(data[0].trim())) {
                cookieStringBuffer.append(data[0]);
                cookieStringBuffer.append("=");
                cookieStringBuffer.append(data[1]);
                break;
            }
        }

        return cookieStringBuffer.toString();
    }

    private static void setNativeCookies(CookieManager manager, String url, String cookies) {
        String[] oldCookie = cookies.split(";");
        for (int i = 0; i < oldCookie.length; i++) {
            String[] data = oldCookie[i].split("=");
            if (null != data && data.length == 2) {
                manager.setCookie(url, String.format("%s=%s", data[0], data[1]));
            }
        }
    }
}
