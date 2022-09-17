package com.mtime.statistic.large;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mtime.BuildConfig;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.statistic.StatisticConstant;
import com.mtime.base.statistic.StatisticEnum;
import com.mtime.base.statistic.bean.H5ApplinkBean;
import com.mtime.base.statistic.bean.StatisticH5Bean;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.base.utils.MJsonUtils;
import com.mtime.base.utils.MLogWriter;
import com.mtime.common.utils.Utils;
import com.mtime.constant.FrameConstant;
import com.mtime.frame.App;
import com.mtime.network.NetworkConstant;
import com.mtime.statistic.large.h5.StatisticH5;
import com.mtime.statistic.large.video.StatisticVideo;
import com.mtime.util.ToolsUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import okhttp3.Cookie;

/**
 * Created by LiJiaZhi on 2017/9/2.
 * <p>
 * 4.0数据上报 管理类
 */
/**
 * logx统计功能废弃
 */
@Deprecated
public class StatisticManager {
    // 注意事项：
    // 1. submit按业务分开方法
    // 2. 全部定义成常量，不要直接写字符串
    // 3. 放公共参数

    private static final String TAG = "StatisticManager";
    /**
     * 缓存在SharedPreference中的key
     */
    private static final String SHARED_PREFERENCE_CACHE_KEY_PREFIX = "sp_statistic_cache_key";
    /**
     * 每组上报大小  暂时写1，方便QA测试
     */
    private static final int SUBMIT_GROUP_SIZE = 5;
    /**
     * 缓存上限
     */
    private static final int CACHE_MAX_COUNT = 100;
    /**
     * // 分辨率
     */
    private static final String RESOLUTION = "%d*%d";
    private static final String NETWORK_OTHER = "其它";
//    private static final String IMSI_CHINA_MOBILE_1 = "46000";
//    private static final String IMSI_CHINA_MOBILE_2 = "46002";
//    private static final String IMSI_CHINA_UNICOM = "46001";
//    private static final String IMSI_CHINA_TELECOM = "46003";


    private static StatisticManager instance;
    private final Gson mGson = new Gson();
    private final StatisticApi mStatisticApi = new StatisticApi();
//    private final Handler mHandler;

    /**
     * 内存中等待上报的队列
     */
    private final CopyOnWriteArrayList<HashMap<String, Object>> mMemoryCacheParams = new CopyOnWriteArrayList<>();

    /**
     * 一组上报数据的集合
     */
    private final CopyOnWriteArrayList<HashMap<String, Object>> mGroupDataParams = new CopyOnWriteArrayList<>();

    /**
     * 公共参数
     */
    private final ConcurrentHashMap<String, String> mCommonParams = new ConcurrentHashMap<>();
    /**
     * 传递给H5的统计参数 http://wiki.inc-mtime.com/pages/viewpage.action?pageId=81199163
     */
    private final StatisticH5Bean mH5Bean = new StatisticH5Bean();

    /**
     * 单例
     */
    public static StatisticManager getInstance() {
        if (instance == null) {
            synchronized (StatisticManager.class) {
                if (instance == null) {
                    instance = new StatisticManager();
                }
            }
        }
        return instance;
    }

    private StatisticManager() {
//        HandlerThread ht = new HandlerThread("StatisticManager");
//        ht.start();
//        mHandler = new StatisticHandler(ht.getLooper());
//
//        initStatisticCommonParams();
//        initCacheDatas();
    }

    public void clear() {
        instance = null;
        mCommonParams.clear();
    }

    /****** 公共字段 ******/

    // 1. 登录用户id
    public void setUid(String userId) {
        //  user_id为业务系统的 户ID，uid=md5(user_id+盐值)，32位 写的MD5值，盐值=mtime
        if (TextUtils.isEmpty(userId)) {
            mCommonParams.remove(StatisticConstant.UID);
            mH5Bean.mu = "";
        } else {
            final String mu = Utils.getMd5(userId + StatisticConstant.MD5_SALT).toUpperCase();
            mCommonParams.put(StatisticConstant.UID, mu);
            mH5Bean.mu = mu;
        }
    }

    // 2. 会话标识session
    private void setSid() {
        // session由客户端记录， 成规则:MD5(毫秒时间戳+UUID)，32位 写的MD5值，有效期:30分钟
        // APP端， 户将APP切到后台超过30分钟或者退出APP重新进 属于新session

//        String sid = Utils.getMd5(System.currentTimeMillis() + Guid.get());
//        String sid = Utils.getMd5(String.valueOf(FrameConstant.sessionStartTime) + FrameConstant.deviceToken);
        String sid = Utils.getMd5(System.currentTimeMillis() + FrameConstant.deviceToken);
        if (TextUtils.isEmpty(sid)) {
            mCommonParams.remove(StatisticConstant.SID);
        } else {
            mCommonParams.put(StatisticConstant.SID, sid.toUpperCase());
        }
        mH5Bean.session_id = sid;
    }

    // 3. 产品类型
    private void setPf() {
        // pc、h5、appcom(时光 2C APP)、apppro(时光 2B APP)、apppwanda，参考枚
        mCommonParams.put(StatisticConstant.PF, StatisticEnum.EnumPf.APPCOM.getValue());
        mH5Bean.platform = StatisticEnum.EnumPf.APPCOM.getValue();
    }

    // 11. UDID
    private void setUDID() {
        if (TextUtils.isEmpty(FrameConstant.deviceToken)) {
            mCommonParams.remove(StatisticConstant.UDID);
        } else {
            mCommonParams.put(StatisticConstant.UDID, FrameConstant.deviceToken);
        }
    }

    // 12. IMEI
    private void setIMEI() {
        if (TextUtils.isEmpty(FrameConstant.UA_MOBILE_IMEI)) {
            mCommonParams.remove(StatisticConstant.IMEI);
        } else {
            mCommonParams.put(StatisticConstant.IMEI, FrameConstant.UA_MOBILE_IMEI);
        }
    }

    // 15. 手机品牌
    private void setBrand() {
        mCommonParams.put(StatisticConstant.BRAND, android.os.Build.BRAND);
    }

    // 16. 手机型号
    private void setMobileVersion() {
        mCommonParams.put(StatisticConstant.MOBILE_VERSION, android.os.Build.MODEL);
    }

    // 17. mac地址
    private void setMac() {
        if (TextUtils.isEmpty(FrameConstant.UA_MOBILE_MAC_ADDRESS)) {
            mCommonParams.remove(StatisticConstant.MAC);
        } else {
            mCommonParams.put(StatisticConstant.MAC, FrameConstant.UA_MOBILE_MAC_ADDRESS);
        }
    }

    // 18. 分辨率
    public void setResolution() {
        String resolution = String.format(Locale.getDefault(),
                RESOLUTION, FrameConstant.SCREEN_HEIGHT, FrameConstant.SCREEN_WIDTH);
        if (TextUtils.isEmpty(resolution)) {
            mCommonParams.remove(StatisticConstant.RESOLUTION);
        } else {
            mCommonParams.put(StatisticConstant.RESOLUTION, resolution);
        }
    }

    // 19. 运营商: 枚举值
    private void setOperator() {
//        String imsi = ""; // 其他
//        if(TextUtils.isEmpty(FrameConstant.UA_MOBILE_IMSI)) {
//            imsi = StatisticEnum.EnumOperator.NO.getValue();
//        } else if(FrameConstant.UA_MOBILE_IMSI.startsWith(IMSI_CHINA_MOBILE_1) || FrameConstant.UA_MOBILE_IMSI.startsWith(IMSI_CHINA_MOBILE_2)) {
//            imsi = StatisticEnum.EnumOperator.CHINA_MOBILE.getValue();
//        } else if(FrameConstant.UA_MOBILE_IMSI.startsWith(IMSI_CHINA_UNICOM)) {
//            imsi = StatisticEnum.EnumOperator.CHINA_UNICOM.getValue();
//        } else if(FrameConstant.UA_MOBILE_IMSI.startsWith(IMSI_CHINA_TELECOM)) {
//            imsi = StatisticEnum.EnumOperator.CHINA_TELECOM.getValue();
//        }
        //40060上报
        if (TextUtils.isEmpty(FrameConstant.UA_MOBILE_IMSI)) {
            mCommonParams.remove(StatisticConstant.OPERATOR);
        } else {
            mCommonParams.put(StatisticConstant.OPERATOR, FrameConstant.UA_MOBILE_IMSI);
        }
//        mCommonParams.put(StatisticConstant.OPERATOR, AppConstants.getInstance().MOBILE_OPE);
    }

    // 20. network: 枚举值 EnumNetwork
    private void setNetwork() {
        String network = ToolsUtils.getNetworkType(App.getInstance());
        if (TextUtils.isEmpty(network)) {
            mCommonParams.remove(StatisticConstant.NETWORK);
        } else {
            network = network.equalsIgnoreCase(NETWORK_OTHER) ? StatisticEnum.EnumNetwork.OTHER.getValue() : network;
            mCommonParams.put(StatisticConstant.NETWORK, network);
        }
    }

    // 21. 操作系统类型: 枚举值
    private void setOS() {
        mCommonParams.put(StatisticConstant.OS, StatisticEnum.EnumOs.ANDROID.getValue());
    }

    // 22. 操作系统版本
    private void setOSVersion() {
        String oSVersion = (Build.VERSION.RELEASE == null) ? "" : Build.VERSION.RELEASE;
        if (TextUtils.isEmpty(oSVersion)) {
            mCommonParams.remove(StatisticConstant.OS_VERSION);
        } else {
            mCommonParams.put(StatisticConstant.OS_VERSION, oSVersion);
        }
    }

    // 23. 日志版本: 枚举值
    private void setV() {
        mCommonParams.put(StatisticConstant.V, StatisticEnum.EnumV.FOUR.getValue());
    }

    // 24. APP版本
    private void setAppVersion() {
        mCommonParams.put(StatisticConstant.APP_VERSION, com.mtime.BuildConfig.VERSION_NAME);
    }

    // 25. 下载渠道
    public void setDC() {
        if (TextUtils.isEmpty(FrameConstant.CHANNEL_ID)) {
            mCommonParams.remove(StatisticConstant.DC);
        } else {
            mCommonParams.put(StatisticConstant.DC, FrameConstant.CHANNEL_ID);
        }
    }

    // 27. 城市
    public void setCityName(String cityName) {
        // 与首页左上角城市显示保持一致
        if (TextUtils.isEmpty(cityName)) {
            mCommonParams.remove(StatisticConstant.CITY);
        } else {
            mCommonParams.put(StatisticConstant.CITY, cityName);
        }
        mH5Bean.city_name = cityName;
    }

    //城市id  传递给H5的参数，不是大数据的参数
    public void setCityId(String cityId) {
        mH5Bean.city_id = cityId;
    }

    // reach_id:传递给H5的参数，不是大数据的参数
    public void setReachId(String reachId) {
        mH5Bean.reach_id = reachId;
    }

    /**
     * 初始化处理;
     */
    private void initCacheDatas() {
        mMemoryCacheParams.clear();
        // 从SharedPreference中取出未上报的参数并添加到内存缓存集中
        String value = App.getInstance().getPrefsManager().getString(SHARED_PREFERENCE_CACHE_KEY_PREFIX);
        if (!TextUtils.isEmpty(value)) {
            ArrayList<HashMap<String, Object>> list = mGson.fromJson(value, new TypeToken<ArrayList<HashMap<String, Object>>>() {
            }.getType());
            if (null != list) {
                mMemoryCacheParams.addAll(list);
            }
        }

        MLogWriter.i(TAG, "initCacheDatas...value=" + value);
        MLogWriter.d(TAG, "initCacheDatas... Cache Params Size : " + mMemoryCacheParams.size());
    }

    /**
     * 初始化统计的公共参数----需要动态设置字段 1，20
     */
    private void initStatisticCommonParams() {
        MLogWriter.i(TAG, "initStatisticCommonParams...");
//        setSid();
//        setPf();
//        setUDID();
//        setIMEI();
//        setBrand();
//        setMobileVersion();
//        setMac();
//        setResolution();
//        setOperator();
//        setNetwork();
//        setOS();
//        setOSVersion();
//        setV();
//        setAppVersion();
//        setDC();
//        setCityName();
    }

    public void submit(StatisticWrapBean bean) {
        submit(bean.bean);
    }

    /**
     * 上报统一入口
     */
    public StatisticPageBean submit(StatisticPageBean bean) {
//        if (null == bean || TextUtils.isEmpty(bean.pageName)) {
//            return bean;
//        }
//        if (BuildConfig.DEBUG) {
//            return bean;
//        }
//        Message message = mHandler.obtainMessage(StatisticHandler.WHAT_SUBMIT);
//        message.obj = bean;
//        mHandler.sendMessage(message);

        return bean;
    }

    /**
     * logx统计功能弃用
     * @param bean
     */
    private synchronized void upload(StatisticPageBean bean) {
        /*try {
            //拼接参数
            HashMap<String, Object> params = appendCommonParams(bean);
            //添加到缓存
            addParams2Cache(params);

            //判断是否足够一组上报
            final int cacheSize = mMemoryCacheParams.size();
            MLogWriter.i(TAG, "cacheSize=" + cacheSize + " ,SUBMIT_GROUP_SIZE=" + SUBMIT_GROUP_SIZE);
            if (cacheSize >= SUBMIT_GROUP_SIZE) {
                //获取队列中最先进来的数据（先进先出原侧）
                mGroupDataParams.clear();
                List<HashMap<String, Object>> subList = mMemoryCacheParams.subList(0, SUBMIT_GROUP_SIZE);
                mGroupDataParams.addAll(subList);
                subList.clear();
                //更新本地缓存
                updateLocalCache();

                MLogWriter.d(TAG, "准备上报统计...GroupList Size : " + mGroupDataParams.size());
                //开始上报
                submitMulti(mGroupDataParams);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    /**
     * 更新本地缓存
     */
    private void updateLocalCache() {
        if (null == mMemoryCacheParams || mMemoryCacheParams.isEmpty()) {
            App.getInstance().getPrefsManager().removeKey(SHARED_PREFERENCE_CACHE_KEY_PREFIX);
            return;
        }
        App.getInstance().getPrefsManager().putString(SHARED_PREFERENCE_CACHE_KEY_PREFIX, mGson.toJson(mMemoryCacheParams));
    }

    /**
     * 拼接参数
     */
    private HashMap<String, Object> appendCommonParams(StatisticPageBean bean) {
        HashMap<String, Object> params = new HashMap<>(mCommonParams);
        //deal refer---如果是map则传入map，如果是string则传入string
        try {
            Map<String, String> mapRefer = MJsonUtils.parseString(bean.refer, new TypeToken<Map<String, String>>() {
            }.getType());
            if (null != mapRefer && !mapRefer.isEmpty()) {
                params.put(StatisticConstant.REFERER, mapRefer);
            }
        } catch (Exception e) {
            if (!TextUtils.isEmpty(bean.refer)) {
                params.put(StatisticConstant.REFERER, bean.refer);
            }
        }
        params.put(StatisticConstant.PN, bean.pageName);
        params.put(StatisticConstant.CTS, String.valueOf(System.currentTimeMillis()));
        setNetwork();//实时获取网络状态
        if (null != bean.path && bean.path.size() > 0) {
            params.putAll(bean.path);
        }
        if (null != bean.businessParam && bean.businessParam.size() > 0) {
            for (Map.Entry<String, String> entry : bean.businessParam.entrySet()) {
                if (!TextUtils.isEmpty(entry.getValue())) {
                    params.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return params;
    }

    /**
     * 添加一条上报参数到缓存
     */
    private void addParams2Cache(HashMap<String, Object> params) {
        if (mMemoryCacheParams.size() < CACHE_MAX_COUNT) {
            mMemoryCacheParams.add(params);
            updateLocalCache();
        } else {
            MLogWriter.e(TAG, "缓存达到上限,以后的上报数据将会丢失");
        }
    }

    /**
     * 真正上报操作
     * logx统计功能弃用
     */
    private void submitMulti(final List<HashMap<String, Object>> list) {
        /*if (CollectionUtils.isEmpty(list)) {
            return;
        }
        mStatisticApi.submitMulti(list, new NetworkManager.NetworkListener<String>() {
            @Override
            public void onSuccess(String result, String showMsg) {
                MLogWriter.d(TAG, "上报统计成功！");
            }

            @Override
            public void onFailure(NetworkException<String> exception, String showMsg) {
                MLogWriter.d(TAG, "上报统计失败！");
                //上报失败，恢复到缓存中等待重新上报
                Message message = mHandler.obtainMessage(StatisticHandler.SUBMIT_FAIL);
                message.obj = list;
                mHandler.sendMessage(message);
            }
        });*/
    }

    /**
     * 页面时长
     */
    public void submitTiming(StatisticPageBean bean) {
        submit(bean);
    }

    /**
     * open
     */
    public void submitOpen(StatisticPageBean bean) {
        submit(bean);
    }

    /**
     * close
     */
    public void submitClose(StatisticPageBean bean) {
        submit(bean);
    }

    /**
     * 统计4.0-----传递给H5的统计参数
     */
    public List<Cookie> getH5CookieStr() {
        List<Cookie> cookieList = new ArrayList<>();
        if (NetworkConstant.getCookies() != null && NetworkConstant.getCookies().size() > 0) {
            cookieList.addAll(NetworkConstant.getCookies());
        }
        // 统计4.0
        try {
            String value = URLEncoder.encode(mGson.toJson(mH5Bean), "utf-8");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, 1);
            Cookie newCookie = new Cookie.Builder().name(StatisticConstant.H5_KEY).value(value)
                    .domain(StatisticConstant.LOGX_PUSH_REACH_ID_H5_COOKIE_DOMAIN).path(StatisticConstant.LOGX_PUSH_REACH_ID_H5_COOKIE_PATH)
                    .expiresAt(calendar.getTimeInMillis()).build();
            cookieList.add(newCookie);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return cookieList;
    }

    /**
     * todo open 参数
     */
    public static Map<String, String> getVideoOpenParam(String sId, String vId, String cId) {
        Map<String, String> map = new HashMap<>();
        map.put(StatisticVideo.VIDEO_TOPIC_ID, sId);
        map.put(StatisticVideo.VIDEO_ID, vId);
        map.put(StatisticVideo.COMMENT_ID, cId);
        return map;
    }

    /**
     * 拼接 gotyType:url
     */
    @SuppressWarnings("unused")
    public static String getGotoTypeUrl(String gotoType, String url) {
        if (gotoType.contains("https://") || gotoType.contains("http://") || TextUtils.isEmpty(url)) {
            return gotoType;
        }
        return gotoType + ":" + url;
    }

    public static String getH5Refer(String url) {
        Map<String, String> params = new HashMap<>();
        params.put(StatisticConstant.PN, StatisticH5.PN_H5);
        params.put(StatisticConstant.URL, url);
        return MJsonUtils.toString(params);
    }

    /**
     *
     */
    @SuppressWarnings("unused")
    public String dealApplinkRefer(String refer) {
        return new H5ApplinkBean(refer).toString();
    }

    private class StatisticHandler extends Handler {

        static final int WHAT_SUBMIT = 101;
        static final int SUBMIT_FAIL = 102;

        StatisticHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_SUBMIT:
                    StatisticPageBean bean = (StatisticPageBean) msg.obj;
                    upload(bean);
                    break;
                case SUBMIT_FAIL:
                    @SuppressWarnings("unchecked")
                    List<HashMap<String, Object>> list = (List<HashMap<String, Object>>) msg.obj;
                    mMemoryCacheParams.addAll(0, list);
                    updateLocalCache();
                    break;
                default:
                    break;
            }
        }
    }
}
