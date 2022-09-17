package com.mtime.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mtime.R;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.beans.ADDetailBean;
import com.mtime.beans.ADTotalBean;
import com.mtime.bussiness.common.utils.MSharePreferenceHelper;
import com.mtime.bussiness.mine.api.MineApi;
import com.mtime.bussiness.mine.bean.MessageConfigsSetBean;
import com.mtime.bussiness.mine.bean.UnreadMessageBean;
import com.mtime.bussiness.splash.LoadManager;
import com.mtime.bussiness.ticket.cinema.bean.CinemaOffenGoBean;
import com.mtime.bussiness.ticket.cinema.bean.SyncFavoriteCinemaBean;
import com.mtime.constant.AppConstants;
import com.mtime.constant.FrameConstant;
import com.mtime.frame.App;
import com.mtime.frame.BaseActivity;
import com.mtime.network.ConstantUrl;
import com.mtime.network.RequestCallback;
import com.mtime.statistic.baidu.BaiduConstants;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.mylhyl.acp.AcpOptions.Builder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;

import androidx.collection.ArrayMap;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static com.kotlin.android.ktx.ext.KeyExtKt.KEY_JPUSH_REGID;

@SuppressLint("NewApi")
public class ToolsUtils {

    private static ScriptIntrinsicBlur blurScript;
    private static RenderScript rs;

    private static MineApi mMineApi;

    private static MineApi getMineApi() {
        if (mMineApi == null) {
            return new MineApi();
        }
        return mMineApi;
    }

    public enum BaiDuEventType {
        TYPE_APP_START_TIME, TYPE_MOVIE_QUERY_FINISHED,
    }

//    public static String getEventIdWithType(Context context, BaiDuEventType type) {
//        String id = null;
//        switch (type) {
//            case TYPE_APP_START_TIME:
//                if (ToolsUtils.getNetworkType(context).equalsIgnoreCase("WIFI")) {
//                    id = BaiduConstants.BAIDU_EVENTID_APP_OPENTIMEWIFI;
//                } else {
//                    id = BaiduConstants.BAIDU_EVENTID_APP_OPENTIME;
//                }
//                break;
//            case TYPE_MOVIE_QUERY_FINISHED:
//                if (ToolsUtils.getNetworkType(context).equalsIgnoreCase("WIFI")) {
//                    id = BaiduConstants.BAIDU_EVENTID_MOVIEVIEW_LOADTIMEWIFI;
//                } else {
//                    id = BaiduConstants.BAIDU_EVENTID_MOVIEVIEW_LOADTIME;
//                }
//                break;
//            default:
//                break;
//        }
//
//        return id;
//    }

    public static String getEventTime(long time) {
        time = time / 1000;
        if (time < 1) {
            return "<1s";
        } else if (time < 2) {
            return "1-2";
        } else if (time < 3) {
            return "2-3";
        } else if (time < 4) {
            return "3-4";
        } else if (time < 5) {
            return "4-5";
        } else if (time < 6) {
            return "5-6";
        } else if (time < 7) {
            return "6-7";
        } else {
            return ">7s";
        }
    }

    // *************** calculate distance *******************
    public static String getDistance(double distance) {
        String value;

        if (distance < 1) {
            value = "";
        }
        if (distance < 500) {
            value = "<500m";
        } else if (distance < 1000) {
            value = String.format("%dm", (int) distance);
        } else if (distance <= 20000) {
            value = String.format("%.1fkm", (float) (distance / 1000));
        } else {
            value = ">20km";
        }

        return value;
    }

    // ***************** TEXT VIEW *************
    public static boolean isTextOverFlowed(final TextView view) {
        int availableWidth = view.getWidth() - view.getPaddingLeft() - view.getPaddingRight();
        Paint paint = view.getPaint();
        float width = paint.measureText(view.getText().toString());

        return width > availableWidth;
    }

    public Bitmap setAlpha(Bitmap sourceImg, int number) {
        int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];
        sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0, sourceImg.getWidth(), sourceImg.getHeight());// 获得图片的ARGB值
        number = number * 255 / 100;
        for (int i = 0; i < argb.length; i++) {
            argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF);
        }
        return Bitmap.createBitmap(argb, sourceImg.getWidth(), sourceImg.getHeight(), Config.ARGB_8888);
    }

    // ****************** cinema favorites ****************
    public static void syncFavorites(BaseActivity context, List<CinemaOffenGoBean> localFavorites) {

        List<String> addedIds = getFavoritedCinemas(localFavorites);
        for (int i = 0; i < addedIds.size(); i++) {
            String item = addedIds.get(i);
            Map<String, String> parameterList = new ArrayMap<String, String>(2);
            parameterList.put("addIds", item);
            parameterList.put("deleteFIds", "");
            HttpUtil.post(ConstantUrl.SYNC_FAVORITE_CINEMA, parameterList, SyncFavoriteCinemaBean.class, new RequestCallback() {

                @Override
                public void onFail(Exception e) {
                }

                @Override
                public void onSuccess(Object o) {
                }
            });
        }
    }

    // 同步收藏
    public static void syncFavorites() {
        List<CinemaOffenGoBean> localFavorites = SaveOffenGo.getInstance().getAll();
        List<String> addedIds = getFavoritedCinemas(localFavorites);
        for (int i = 0; i < addedIds.size(); i++) {
            String item = addedIds.get(i);
            getMineApi().syncFavoriteCinema(item, new NetworkManager.NetworkListener<SyncFavoriteCinemaBean>() {
                @Override
                public void onSuccess(SyncFavoriteCinemaBean result, String showMsg) {
                }

                @Override
                public void onFailure(NetworkException<SyncFavoriteCinemaBean> exception, String showMsg) {
                }
            });
        }
    }

    public static List<String> getFavoritedCinemas(List<CinemaOffenGoBean> ids) {
        List<String> syncList = new ArrayList<String>();

        if (null == ids || ids.size() < 1) {
            return syncList;
        }

        StringBuffer sb = new StringBuffer();
        int index = 0;

        for (int i = 0; i < ids.size(); i++) {
            index++;
            sb.append(ids.get(i).getId());
            sb.append(",");

            if (index == 40) {
                syncList.add(sb.toString());
                index = 0;
                sb.delete(0, sb.toString().length());
            }
        }

        // the left values ( for less than 40)
        if (index > 0 && index < 40) {
            syncList.add(sb.toString());
        }

        return syncList;
    }

    // **************** network ********************
    public static String getNetworkType(Context context) {
        ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectMgr.getActiveNetworkInfo();
        if (null == info) {
            return "其它";
        }
        if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            return ToolsUtils.getMobileNetType(context);
        }

        return "WIFI";
    }

    public static String getMobileNetType(final Context context) {
        final String[] type = {"其它"};
//        极光一键登录  android.permission.READ_PHONE_STATE 设置maxSdkVersion 为29，编译版本为30就无法获取到
        Acp.getInstance(context).request(new Builder().setPermissions(Manifest.permission.READ_PHONE_STATE).build(), new AcpListener() {
            @Override
            public void onGranted() {
                try{
                    Acp.getInstance(context).onDestroy();
                    TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    switch (telephonyManager.getNetworkType()) {
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            type[0] = "2G";
                            break;
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            type[0] = "3G";
                            break;
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            type[0] = "4G";
                            break;
                        default:
                            break;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onDenied(List<String> permissions) {
                Acp.getInstance(context).onDestroy();
            }
        });



        return type[0];
    }

    public static void saveHomeGuider(Context context, boolean show) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("HomeGuider", show);
        editor.apply();
    }

    //special filter guider
    public static void saveSpecialFilterGuider(Context context, final String key, final boolean show) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, show);
        editor.apply();
    }

    public static boolean getSpecialFilterGuider(Context context, final String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(key, true);
    }

    // ****************TOKEN **********
    public static String getToken(Context context) {
        String androidID = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (TextUtils.isEmpty(androidID)) {
            androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        return Build.VERSION.SDK_INT >= 29 ? androidID : androidID + android.os.Build.SERIAL;
    }

    public static String getJPushId(Context context) {
        return MSharePreferenceHelper.get().getStringValue(KEY_JPUSH_REGID, "");
    }
    // ************ AD *************

    /**
     * get the AD url
     *
     * @param bean AD data
     * @param type AD type
     * @return url
     */
    public static String getADUrl(ADTotalBean bean, String type) {
        if (!bean.getSuccess() || 0 == bean.getCount() || TextUtils.isEmpty(type)) {
            return null;
        }

        for (int i = 0; i < bean.getCount(); i++) {
            ADDetailBean item = bean.getAdvList().get(i);
            if (type.equalsIgnoreCase(item.getType())) {
                int time = (int) (System.currentTimeMillis() / 1000);
                if (time >= item.getStartDate() && time <= item.getEndDate()) {
                    return item.getUrl();
                }
            }
        }

        return null;
    }

    public static ADDetailBean getADBean(ADTotalBean bean, String type) {
        if (!bean.getSuccess() || 0 == bean.getCount() || TextUtils.isEmpty(type)) {
            return null;
        }

        for (int i = 0; i < bean.getCount(); i++) {
            ADDetailBean item = bean.getAdvList().get(i);
            if (type.equalsIgnoreCase(item.getType())) {
                return item;
            }
        }

        return null;
    }

    //获取广告集合
    public static List<ADDetailBean> getADBeans(ADTotalBean bean, String type) {
        List<ADDetailBean> adDetailBeans = null;
        if (!bean.getSuccess() || 0 == bean.getCount() || TextUtils.isEmpty(type)) {
            return null;
        }

        adDetailBeans = new ArrayList<ADDetailBean>();
        for (ADDetailBean item : bean.getAdvList()) {
            if (type.equalsIgnoreCase(item.getType())) {
                adDetailBeans.add(item);
            }
        }
        return adDetailBeans;
    }

    //清除上次保存的广告数据
    public static void clearLastADPres(int adNum) {
        if (adNum == 0) {
            return;
        }
        for (int i = 0; i < adNum; i++) {
            App.getInstance().getPrefsManager().putString(App.getInstance().KEY_ADVERT_PHOTO_URL_START_PAGE + i, "");
            App.getInstance().getPrefsManager().putLong(App.getInstance().KEY_ADVERT_PHOTO_URL_START_DATE + i, 0l);
            App.getInstance().getPrefsManager().putLong(App.getInstance().KEY_ADVERT_PHOTO_URL_END_DATE + i, 0l);
        }
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    // 设置评分TextView的样式.
    public static void drawScoreView(Context context, TextView textView, int prefixSize, int postSize, int viewWidth,
                                     int viewHeight, boolean showBg) {
        String score = textView.getText().toString();
        String tag = ".";
        if (score.indexOf(tag) >= 0) {
            Spannable text = new SpannableString(score);
            text.setSpan(new AbsoluteSizeSpan(prefixSize, true), 0, score.indexOf(tag), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            text.setSpan(new AbsoluteSizeSpan(postSize, true), score.indexOf(tag) + 1, score.length(),
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//            text.setSpan(new AbsoluteSizeSpan(px2dip(context, prefixSize), true), 0, score.indexOf(tag), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//            text.setSpan(new AbsoluteSizeSpan(px2dip(context, postSize), true), score.indexOf(tag) + 1, score.length(),
//                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//            text.setSpan(new SubscriptSpan(), 0, score.indexOf(tag),
//                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//保留，看设计这边是否要加上
            text.setSpan(new SuperscriptSpan(), score.indexOf(tag) + 1,
                    score.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//保留，设计说去掉上角显示
            textView.setText(text);
        } else {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, px2dip(context, prefixSize));
            textView.setText(score);
        }

//        if (viewWidth > 0 && viewHeight > 0 && textView.getHeight() <= viewHeight && textView.getWidth() <= viewWidth) {// 防止传递的值小于实际控件的大小
//            LayoutParams businessParam = textView.getLayoutParams();
//            businessParam.width = viewWidth;
//            businessParam.height = viewHeight;
//        }
//        else 
//        if (showBg) {
//            textView.setPadding(px2dip(context, 32), 0, px2dip(context, 32), 0);
//        }
        if (showBg) {
            textView.setBackgroundColor(ContextCompat.getColor(context, R.color.color_659d0e));
            textView.setPadding(20, 10, 20, 10);
        } else {
            textView.setTextColor(ContextCompat.getColor(context, R.color.color_659d0e));
            textView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        }
    }

    // 设置消息通知设置接口
    public static void sendConfigMsg(Context context) {
        String pushtoken = getToken(context);
        String jpushid = getJPushId(context);
        if (!TextUtils.isEmpty(pushtoken) || !TextUtils.isEmpty(jpushid)) {
            getMineApi().setMessageConfigs(pushtoken, jpushid, new NetworkManager.NetworkListener<MessageConfigsSetBean>() {
                @Override
                public void onSuccess(MessageConfigsSetBean result, String showMsg) {
                    // 获取未读通知或消息数量

                }

                @Override
                public void onFailure(NetworkException<MessageConfigsSetBean> exception, String showMsg) {

                }
            });
        }
    }

    public static boolean isVisit(final String url) {
        // 如果为null或者长度小于2，则认为不需要过滤。直接返回true.
        if (!LoadManager.isCheckHost() || TextUtils.isEmpty(LoadManager.getAllowHost()) || LoadManager.getAllowHost().length() < 2) {
            return true;
        }

        if (TextUtils.isEmpty(url)) {
            return false;
        }

        try {
            URL urls = new URL(url);
            String host = urls.getHost();
//            LogWriter.e("checklogin", "jump url host:" + host);
            if (TextUtils.isEmpty(host)) {
                return false;
            }

            String[] postfix = LoadManager.getAllowHost().split(";");
            for (String item : postfix) {
//                LogWriter.e("checklogin", "allow postfix:" + item);
                if (TextUtils.isEmpty(item)) {
                    continue;
                }
                if (host.endsWith(item)) {
//                    LogWriter.e("checklogin", "allow jump");
                    return true;
                }
            }
        } catch (Exception e) {
            if (url.startsWith("goto") || url.startsWith("open") || url.startsWith("close")) {
                return true;
            }
        }

        return false;
    }

    public static void initTelephoneInfo(Context context) {

        // 基站号
        FrameConstant.UA_MOBILE_CELLID = "";
        //小区号
        FrameConstant.UA_MOBILE_CID = "";
        FrameConstant.UA_MOBILE_IMEI = getToken(context);
        FrameConstant.UA_MOBILE_IMSI = "";
        AppConstants.getInstance().MOBILE_OPE = "";
        // 获取系统语言
        FrameConstant.UA_MOBILE_LANGUAGE = Locale.getDefault().getLanguage();
        // 获取MAC地址
        FrameConstant.UA_MOBILE_MAC_ADDRESS = getMacAddress();

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (null == tm) {
            return;
        }

        // 小区号
        /*try {
            List<NeighboringCellInfo> neighbors = tm.getNeighboringCellInfo();
            if (neighbors != null && neighbors.size() > 0) {
                FrameConstant.UA_MOBILE_CID = String.valueOf(neighbors.get(0).getCid());
            }
        } catch (Exception e) {

        }*/

        // IMEI
        /*try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                FrameConstant.UA_MOBILE_IMEI = tm.getImei();
            }
            else {
                FrameConstant.UA_MOBILE_IMEI = tm.getDeviceId();
            }
        } catch (Exception e) {

        }*/

        //IMSI
        try {
            FrameConstant.UA_MOBILE_IMSI = tm.getNetworkOperator();
            AppConstants.getInstance().MOBILE_OPE = tm.getNetworkOperatorName();
        } catch (Exception e) {
            Log.e("ToolsUtils", "initTelephoneInfo...", e);
        }


        try {
            GsmCellLocation location = (GsmCellLocation) tm.getCellLocation();
            if (null != location) {
                // 基站号在这里获取
                FrameConstant.UA_MOBILE_CELLID = String.valueOf(location.getCid());
            }
        } catch (Exception e) {
//            Log.e("ToolsUtils", "initTelephoneInfo...", e);
            try {
                CdmaCellLocation cdmaLocation = (CdmaCellLocation) tm.getCellLocation();
                if (null != cdmaLocation) {
                    // 基站号在这里获取
                    FrameConstant.UA_MOBILE_CELLID = String.valueOf(cdmaLocation.getBaseStationId());
                }
            } catch (Exception e1) {
                FrameConstant.UA_MOBILE_CELLID = "";
                Log.e("ToolsUtils", "initTelephoneInfo...", e);
            }
        }
    }

    /**
     * 获取mac地址
     *
     * @return
     */
    private static String mMacAddress = "";

    public static String getMacAddress() {
        if (TextUtils.isEmpty(mMacAddress)) {
            try {
                List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
                for (NetworkInterface nif : all) {
                    if (!nif.getName().equalsIgnoreCase("wlan0"))
                        continue;

                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes == null) {
                        mMacAddress = "";
                        break;
                    }

                    StringBuilder res1 = new StringBuilder();
                    for (byte b : macBytes) {
                        res1.append(String.format("%02X:", b));
                    }

                    if (res1.length() > 0) {
                        res1.deleteCharAt(res1.length() - 1);
                    }
                    mMacAddress = res1.toString();
                    break;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return mMacAddress;
    }

    public static void clear() {
        if (null != blurScript) {
            blurScript.destroy();
            blurScript = null;
        }

        if (null != rs) {
            rs = null;
        }
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }

        return dest;
    }

    public static String getChannelID(Context context, String defaultID) {
        String text = null;
        InputStream is = null;

        try {
            is = context.getAssets().open("source");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            text = new String(buffer);
        } catch (IOException var14) {
            text = defaultID;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException var13) {
                    var13.printStackTrace();
                }

                is = null;
            }

        }

        return replaceBlank(text);
    }
}