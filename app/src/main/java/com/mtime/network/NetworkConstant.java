package com.mtime.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.kotlin.android.retrofit.cookie.CookieManager;
import com.mtime.base.network.NetworkManager;
import com.mtime.constant.FrameConstant;

import android.os.Build;

import okhttp3.Cookie;

/**
 * Created by LiJiaZhi on 17/3/23.
 * <p>
 * 网络请求--公共参数
 */

public class NetworkConstant {
    private static final String UA = "User-Agent";
//    public static final String HEADER_CHECK_VALUE = "X-Mtime-Mobile-CheckValue";
    public static final String HEADER_DEVICE_INFO = "X-Mtime-Mobile-DeviceInfo";
    public static final String HEADER_PUSH_TOKEN = "X-Mtime-Mobile-PushToken";
    public static final String HEADER_CIP_LOCATION = "X-Mtime-Mobile-Location";
    public static final String HEADER_CIP_TELEPHONE_INFO = "X-Mtime-Mobile-TelephoneInfo";
    public static final String HEADER_JPUSH_ID = "X-Mtime-Mobile-JPushID";
    public static final String HEADER_ACCEPT = "Accept-Charset";
    public static final String HEADER_AE = "Accept-Encoding";
    public static final String REFERER = "Referer";


    public NetworkConstant() {
    }

    public static HashMap<String, String> getCommonHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(HEADER_DEVICE_INFO, String.format("%s_%s", Build.MODEL, Build.HARDWARE));
        headers.put(HEADER_PUSH_TOKEN, FrameConstant.push_token);
        headers.put(HEADER_JPUSH_ID, FrameConstant.jpush_id);
        // CIP数据经纬度
        /* http://zentao.wandatech-dev.com/zentao/story-view-271.html
        StringBuffer sblocation = new StringBuffer();
        sblocation.append(FrameConstant.UA_LOCATION_LONGITUDE);
        sblocation.append(FrameConstant.COMMA);
        sblocation.append(FrameConstant.UA_LOCATION_LATITUDE);
        */
        headers.put(HEADER_CIP_LOCATION, "");
        // CIP数据设备信息
        StringBuffer sbtelephone = new StringBuffer();
        sbtelephone.append("CELLID=");
        sbtelephone.append(FrameConstant.UA_MOBILE_CELLID);
        sbtelephone.append(";");
        sbtelephone.append("CID=");
        sbtelephone.append(FrameConstant.UA_MOBILE_CID);
        sbtelephone.append(";");
        sbtelephone.append("IMEI=");
        sbtelephone.append(FrameConstant.UA_MOBILE_IMEI);
        sbtelephone.append(";");
        sbtelephone.append("IMSI=");
        sbtelephone.append(FrameConstant.UA_MOBILE_IMSI);
        sbtelephone.append(";");
        sbtelephone.append("MAC=");
        sbtelephone.append(FrameConstant.UA_MOBILE_MAC_ADDRESS);
        sbtelephone.append(";");
        sbtelephone.append("LANGUAGE=");
        sbtelephone.append(FrameConstant.UA_MOBILE_LANGUAGE);
        headers.put(HEADER_CIP_TELEPHONE_INFO, "");//sbtelephone.toString()
    
        headers.put(HEADER_ACCEPT, FrameConstant.UTF8);
        headers.put(UA, UAUtils.apiUA());
        headers.put(HEADER_AE, "gzip");
        headers.put(REFERER, "B2C");
        // headers.put(COOKIE,getCookies());
        return headers;
    }

    /**
     * 获取cookie
     */
    public static List<Cookie> getCookies() {
        return CookieManager.Companion.getInstance().getCookies();
    }

    /**
     * 清除cookie
     */
    public static void clearCookie() {
//        NetworkManager.getInstance().mCookieJarManager.clear();
        CookieManager.Companion.getInstance().clear();
    }

    /**
     * 当定位成功、屏幕尺寸获取成功，要更新
     */
    public static void update() {
        NetworkManager.getInstance().setCommonHeaders(getCommonHeaders());
    }
    
//    /**
//     * 获取CheckValueHeader
//     */
//    public static Map<String, String> getCheckValueHeader(String url, Map<String, String> params) {
//        final long time = System.currentTimeMillis();
//
//        final StringBuffer paramBuffer = new StringBuffer();
//        // TODO  尝试添加header数据。验证的还是需要在这里负值。
//        if (null != params && params.size() > 0) {
//            LogWriter.i("MTNet", "businessParam:" + params.toString());
//            int index = 0;
//            for (Map.Entry<String, String> entry : params.entrySet()) {
//                //TODO X key需要encode吗?
//                paramBuffer.append(entry.getKey());
//                paramBuffer.append('=');
//                String value = entry.getValue() != null ? entry.getValue() : "";
//                // TODO 改成原来的url encode方法，需要验证一下
//                paramBuffer.append(Utils.UrlEncodeUnicode(value));
//                if (++index >= params.size()) {
//                    break;
//                }
//                paramBuffer.append('&');
//            }
//
//        }
//
//        StringBuffer sb1 = new StringBuffer();
//        sb1.append(FrameConstant.APP_ID).append(FrameConstant.CLIENT_KEY).append(time).append(url).append(paramBuffer.toString());
//
//        StringBuffer sb = new StringBuffer();
//        sb.append(FrameConstant.APP_ID).append(FrameConstant.COMMA).append(time).append(FrameConstant.COMMA);
//        sb.append(Utils.getMd5(sb1.toString()));
//        sb.append(FrameConstant.COMMA).append(FrameConstant.CHANNEL_ID);
//
//        Map<String, String> hearder = new HashMap<>();
//        hearder.put(HEADER_CHECK_VALUE, sb.toString());
//        return hearder;
//    }
}
