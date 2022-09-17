package com.mtime.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-12-23
 */
public class ConnectivityUtil {

    public static int getNetworkStatusH5(Context context) {
        String netStatus = getNetworkType(context);

        int status;
        if ("nolink".equals(netStatus)) {
            status = 0;
        } else if ("wifi".equals(netStatus)) {
            status = 1;
        } else if ("2G".equals(netStatus)) {
            status = 2;
        } else if ("3G".equals(netStatus)) {
            status = 3;
        } else if ("4G".equals(netStatus)) {
            status = 4;
        } else {
            status = 99;
        }
        return status;
    }

    public static String getNetworkType(Context activity) {
        String wifi = "wifi";
        String m3G = "3G";
        String m2G = "2G";
        String other = "other";
        ConnectivityManager connectMgr = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectMgr.getActiveNetworkInfo();
        if (info == null) {
            return "nolink";
        } else if (info.getType() == 0) {
            return is3GNetwork(activity) ? m3G : m2G;
        } else {
            return info.getType() == 1 ? wifi : other;
        }
    }

    public static boolean is3GNetwork(Context activity) {
        TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
            default:
                return false;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_LTE:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return true;
        }
    }
}
