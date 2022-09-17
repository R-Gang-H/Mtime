package com.mtime.frame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.litepal.LitePal;

import com.baidu.mapapi.SDKInitializer;
import com.kingja.loadsir.callback.SuccessCallback;
import com.kingja.loadsir.core.LoadSir;
import com.kotlin.android.api.config.AppConfig;
import com.kotlin.android.app.api.ApiManager;
import com.kotlin.android.player.PlayerConfig;
import com.kotlin.android.push.PushManager;
import com.kotlin.android.retrofit.cookie.CookieManager;
import com.kotlin.android.router.RouterManager;
import com.kotlin.android.statistics.sensors.SensorsManager;
import com.kotlin.android.user.login.jguang.JLoginManager;
import com.kotlin.android.widget.refresh.Refresh;
import com.kotlin.chat_component.HuanxinInitializer;
import com.mtime.BuildConfig;
import com.mtime.R;
import com.mtime.account.AccountForeBackListener;
import com.mtime.base.application.MSimpleBaseApplication;
import com.mtime.base.cache.MCacheManager;
import com.mtime.base.callback.EmptyCallback;
import com.mtime.base.callback.ErrorCallback;
import com.mtime.base.callback.LoadingCallback;
import com.mtime.base.callback.UnLoginCallback;
import com.mtime.base.imageload.IImageLoadStrategy;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.network.TokenExpireFilter;
import com.mtime.base.utils.MLogWriter;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.base.utils.UtilsManager;
import com.mtime.base.views.titlebar.TitleBar;
import com.mtime.beans.Photo;
import com.mtime.bussiness.common.utils.MSharePreferenceHelper;
import com.mtime.bussiness.common.widget.DefaultTtileBarStyle;
import com.mtime.bussiness.common.widget.MtimeRefreshHeader;
import com.mtime.bussiness.main.maindialog.bean.UpdateVerBean;
import com.mtime.bussiness.mine.bean.AccountDetailBean;
import com.mtime.bussiness.mine.bean.UnreadMessageBean;
import com.mtime.bussiness.splash.bean.PullRefreshFilmWord;
import com.mtime.common.cache.CacheManager;
import com.mtime.common.utils.PrefsManager;
import com.mtime.constant.Constants;
import com.mtime.constant.FrameConstant;
import com.mtime.exception.CustomUnCaughtExceptionHandler;
import com.mtime.frame.callback.MtimeLoadingCallback;
import com.mtime.network.ConstantUrl;
import com.mtime.network.MDynamicHeaderInterceptor;
import com.mtime.network.NetworkConstant;
import com.mtime.network.UAUtils;
import com.mtime.player.AdHelper;
import com.mtime.player.PlayerLibraryConfig;
import com.mtime.statistic.large.StatisticForeBackListener;
import com.mtime.util.ToolsUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.sdk.QbSdk;
import com.youzan.androidsdk.YouzanSDK;
import com.youzan.androidsdkx5.YouZanSDKX5Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import androidx.multidex.MultiDex;
import cn.jpush.android.api.JPushInterface;
import okhttp3.Cookie;
import okhttp3.HttpUrl;

import static com.kotlin.android.ktx.ext.KeyExtKt.KEY_JPUSH_REGID;

@SuppressLint("NewApi")
public class App extends MSimpleBaseApplication {

    private static App mInstance;

    /**
     * 全局的SharedPreferences
     */
    private PrefsManager mPrefsManager;

    /**
     * 全局的SharedPreference
     */
    public PrefsManager getPrefsManager() {
        if (null == mPrefsManager) {
            mPrefsManager = PrefsManager.get(this);
        }
        return mPrefsManager;
    }

    public static App getInstance() {
        return mInstance;
    }

    /**
     * MultiDex 设置
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
//            layout.setPrimaryColorsId(R.color.color_f2f2f2);//全局设置主题颜色
            MtimeRefreshHeader header = new MtimeRefreshHeader(context);
            return header;//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {
            ClassicsFooter.REFRESH_FOOTER_NOTHING = context.getString(R.string.footer_end);
            ClassicsFooter classicsFooter = new ClassicsFooter(context);
            //指定为经典Footer，默认是 BallPulseFooter
            return classicsFooter.setDrawableSize(20).setTextSizeTitle(12).setAccentColor(Color.parseColor("#8798af"));
        });
    }

    @Override
    public void onCreateWithMainProcess() {

        /*// LeakCanary
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);*/

        // 是否开启严格模式
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll() // 开启所有的detectXX系列方法
                    .penaltyLog() // 在Logcat中打印违规日志
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
        }
        super.onCreateWithMainProcess();
    }

    @Override
    protected void initCommonParams() {
        mInstance = this;

        RouterManager.Companion.getInstance()
                .init(this, BuildConfig.DEBUG, "com.kotlin.android.app.router.path");

        //初始化工具类
        UtilsManager.initUtils(this);
        ApiManager.INSTANCE.init();
        //用户隐私相关及三方SDK
        initUserPrivacyInfos();
        //初始化有赞商城
//        initYouZan();
    }

    private void initYouZan() {
        // 初始化SDK
        //appkey:可以前往<a href="http://open.youzan.com/sdk/access">有赞开放平台</a>申请
        YouzanSDK.isDebug(true);
        //TODO clientId 写入
        YouzanSDK.init(this, "0073bccbaf5369028a","", new YouZanSDKX5Adapter());

        // 可选
        // 预取html，一般是预取店铺主页的url。
        // 注意：当发生重定向时，预取不生效。
        // YouzanPreloader.preloadHtml(this, "准备预加载的页面的URL");

    }

    @Override
    public void initNetworkManager() {
        //初始化图片加载框架配置
        IImageLoadStrategy.Config config = new IImageLoadStrategy.Config();
        config.headers = new HashMap<>(1);
        config.headers.put("Referer", "http://www.mtime.com");
        ImageHelper.init(this, config);
        //network
        MCacheManager.DEFAULT.initialize(this);
        NetworkManager.getInstance().initialize(this, ConstantUrl.HOST,
                20, 0, new TokenExpireFilter());
    }

    @Override
    public void initOthers() {

        /*====================kotlin新框架中的些一组件初始化====================*/
        //初始化全局下拉刷新和上拉加载样式
        Refresh.initHeaderAndFooter();
        //播放器初始化
        PlayerConfig.init(this, BuildConfig.DEBUG);
        //环信初始化
        HuanxinInitializer.INSTANCE.init(this);
        /*====================kotlin新框架中的些一组件初始化====================*/

    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        switch (level) {
            case TRIM_MEMORY_RUNNING_CRITICAL:
                AdHelper.destroy();
                break;
        }
    }

    /**
     * todo 注意：涉及到用户隐私和三方SDK初始用到用户隐私相关数据的必须放到此方法中，
     * todo 因为工信部已查到公司，要求必须在用户同意隐私条款后才可使用隐私信息。
     */
    public void initUserPrivacyInfos() {
        //如没有同意隐私协议不进行以下初始化
        if (!getPrefsManager().getBoolean(App.getInstance().MORE_THAN_ONCE, false)) {
            return;
        }

        //init custom uncaught exception handler
        CustomUnCaughtExceptionHandler.openState(this, !BuildConfig.DEBUG);

        //初始化base-framework库页面的title样式
        TitleBar.initStyle(new DefaultTtileBarStyle());
        //初始化base-framework库页面的各种状态样式
        LoadSir.beginBuilder()
                .addCallback(new ErrorCallback(R.drawable.icon_exception, "", getString(R.string.error_normal_tv)))
                .addCallback(new EmptyCallback(R.drawable.icon_exception, getString(R.string.empty_normal_tv)))
                .addCallback(new LoadingCallback(new MtimeLoadingCallback()))
                .addCallback(new UnLoginCallback())
                .setDefaultCallback(SuccessCallback.class)
                .commit();

        final AccountDetailBean obj = (AccountDetailBean) CacheManager.getInstance()
                .getFileCacheNoClean(App.getInstance().KEY_USER_INFO);
        if (null != obj) {// 版本更新时把本地保存的用户信息转存到SharePreference中
            // TODO 重构缓存处理未执行
//            AccountManager.updateAccountInfo(obj);
//            UserManager.Companion.getInstance().update();
//            CacheManager.getInstance().removeFileCache(App.getInstance().KEY_USER_INFO);
        }


        //log开关
        MLogWriter.setToggle(BuildConfig.DEBUG);

        //播放器
        PlayerLibraryConfig.init(this);

        /**
         * 此处这么做是为了防止版本更新时旧的cookie文件没有更新到新的cookie中导致登录失败。
         */
        String cookie = (String) CacheManager.getInstance().getFileCacheNoClean("volleycookie");
        if (!TextUtils.isEmpty(cookie)) {
            String[] cookieStrings = cookie.split(";");
            for (int i = 0, size = cookieStrings.length; i < size; i++) {
                String cookieStr = cookieStrings[i];
//                if (TextUtils.isEmpty(cookieStr))
//                    continue;
//                // 这里用一个固定的地址url。随便哪一个。都行。因为mall-wv.mtime.cn识别出来的domain跟其他的不一样。会导致无法添加cookie
//                NetworkManager.getInstance().mCookieJarManager.setCookies(HttpUrl.parse("https://api-m.mtime.cn/AccountDetail.api"), cookieStr);
                HttpUrl httpUrl = HttpUrl.parse("https://api-m.mtime.cn/ccount/detail.api");
                if (!TextUtils.isEmpty(cookieStr) && null != Cookie.parse(httpUrl, cookieStr)) {
                    //这里用一个固定的地址url。随便哪一个。都行。因为mall-wv.mtime.cn识别出来的domain跟其他的不一样。会导致无法添加cookie
//                    NetworkManager.getInstance().mCookieJarManager.setCookies(httpUrl, cookieStr);
                    CookieManager.Companion.getInstance().setCookies(httpUrl, cookieStr);
                }
            }
            CacheManager.getInstance().removeFileCache("volleycookie");
        }

        //监听程序进入前台、后台
        registerAppForeBackListener(new SessionForeBackListener());
        registerAppForeBackListener(new StatisticForeBackListener());
        registerAppForeBackListener(new AccountForeBackListener());


        //获取手机信息
        ToolsUtils.initTelephoneInfo(this);

        initVariables();

        //初始网络
        NetworkManager.getInstance().setDynamicHeaderInterceptor(new MDynamicHeaderInterceptor());
        NetworkManager.getInstance().setCommonHeaders(NetworkConstant.getCommonHeaders());

        //百度地图
        SDKInitializer.initialize(this);

        //推送
        PushManager.Companion.getInstance().initPush(this,BuildConfig.DEBUG);

        JLoginManager.Companion.getInstance().initialize(getApplicationContext());
        /**
         * 首次安装APP，rId可能会获取不到
         */
        String rId = JPushInterface.getRegistrationID(this);
        Log.e("zl","rId "+rId);
        MSharePreferenceHelper.get().putString(KEY_JPUSH_REGID, rId);

        //初始化定位服务
        SDKInitializer.initialize(this);

        //初始化数据库
        LitePal.initialize(this);

        SensorsManager.Companion.getInstance().initSensors(this, AppConfig.Companion.getInstance().getApi(), true);

        // 开启线程初始化其他数据
        ExecutorService exec = Executors.newSingleThreadExecutor();
        exec.execute(new Runnable() {
            @Override
            public void run() {

                //提前初始化x5解决在配置较低手机第一次加载过慢的问题
                QbSdk.initX5Environment(mInstance, null);

                if (!BuildConfig.DEBUG) {
                    CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
                    strategy.setAppChannel(FrameConstant.CHANNEL_ID); // 设置渠道
                    strategy.setAppVersion(BuildConfig.VERSION_NAME); // App的版本
                    CrashReport.initCrashReport(getApplicationContext(), Constants.BUGLG_APPID_RELEASE, false, strategy);
                }
            }
        });
    }

    private void initVariables() {
        // 这里需要检验下这些值是否合适，
        FrameConstant.APP_ID = 6;
        FrameConstant.CLIENT_KEY = getResources().getString(R.string.key_mtime_api_client);
        FrameConstant.HEADER = "X-Mtime-Mobile-CheckValue";
        FrameConstant.UA_STR = " Mtime Android App ";
        FrameConstant.REG_URL = "https://m.mtime.cn/#!/member/email/signup";
        FrameConstant.image_quality = 70;
        FrameConstant.PACKAGE_VERSION = BuildConfig.VERSION_NAME;
        // 使用设备的Android id和序列号来标示设备唯一码
        FrameConstant.deviceToken = ToolsUtils.getToken(this);

        // 初始化本地缓存的用户账号信息
//        AccountManager.initAccountInfo();

        if (0 == FrameConstant.SCREEN_WIDTH) {
            FrameConstant.SCREEN_WIDTH = MScreenUtils.getScreenWidth();
            FrameConstant.SCREEN_HEIGHT = MScreenUtils.getScreenHeight();
            NetworkConstant.update();
            //更新分辨率
//            StatisticManager.getInstance().setResolution();
        }

        if (TextUtils.isEmpty(FrameConstant.CHANNEL_ID) || TextUtils.isEmpty(FrameConstant.UA_STR)) {
            /**
             * @author zl
             * @date 2020/9/15 9:06 上午
             * @desc 统一在AppConfig里获取，原来的source文件弃用
             */
//            FrameConstant.CHANNEL_ID = ToolsUtils.getChannelID(this, "unknown");
            FrameConstant.CHANNEL_ID = AppConfig.Companion.getInstance().getChannel();

            FrameConstant.UA_STR = UAUtils.apiUA();
            //更新下载渠道
//            StatisticManager.getInstance().setDC();
        }
    }

    /*****************************
     * 可变的
     *********************************/
    // 以下中注释掉的代码暂时不要删除

    public boolean hasSubmitCommnets = false;
    // 影人 使用的
    public boolean isPraised = false;
    public int totalPraise = 0;
    // 影片使用的
    public boolean isMoviePraised = false;
    public int totalMoviePraise = 0;
    // 设置页－是否开启海报屏蔽
    public boolean FILTER_SET = false;
    // 选座、购票
    public String lastPayFailedOrderId = "0";
    public boolean isFromWx = false;
    // collection used
    public long app_start_time = 0;
    public boolean hasCollectCell = false;
    public boolean hasCollectTopic = false;
    public boolean hasCollectCategory = false;
    public boolean hasCollectWant = false;

    // 首页是否已统计各区域滚动事件
    public boolean hasCollectHomeMall = false;
    public boolean hasCollectHomeEntry = false;
    public boolean hasCollectHomeHotpointFirstShow = false;
    // 商城是否使用原生常量 true为使用原生，false为使用h5
    public final boolean MALL_USERNATIVE = true;
    // 主标签页弹新手礼包图片src
    public String REGISTER_DLG_NEWGIFT_IMG = null;
    public ArrayList<Boolean> hasCollectCategoryList = new ArrayList<Boolean>();
    public UpdateVerBean updateVersion;
    public UnreadMessageBean UNREADMESSAGEBEAN;

    public ArrayList<Photo> photoList = new ArrayList<>();
    // 影片广告词
    public List<PullRefreshFilmWord> pullAdvwordList = new ArrayList<PullRefreshFilmWord>();

    public enum GoodStatus {
        Finished, Edit
    }

    /*****************************
     * 不变的
     *********************************/
    public final int PAYTYPE_YINLIAN = 9; // 支付类型，银联:9,
    // 支付宝手机:6
    public final int PAYTYPE_ALIPAY = 6; // 支付类型，支付宝:6,
    public final int PAYTYPE_ALIPAY_WAP = 7; // 支付类型，支付宝wap:7,
    // 微信支付:14
    public final int PAYTYPE_WEIXIN = 14; // 支付类型，微信:14,
    // 充值状态
    public final int RECHARGE_STATUS_LOGIN = -2;
    public final int RECHARGE_STATUS_FAIL = 0;
    public final int RECHARGE_STATUS_SUCCESS = 1;
    // getTargetObjStatus接口用到的对象类型常量：1电影 2影人 33影院 51新闻 6影评 排行榜
    public final int TARGET_OBJ_TYPE_MOVIE = 1;
    public final int TARGET_OBJ_TYPE_ACTOR = 2;
    public final int TARGET_OBJ_TYPE_CINEMA = 33;
    public final int TARGET_OBJ_TYPE_NEWS = 51;
    public final int TARGET_OBJ_TYPE_REVIEW = 6;
    public final int TARGET_OBJ_TYPE_TOP = 90;
    // 性别
    public final int male = 1; // 男
    public final int female = 2; // 女
    public final long USER_INFO_SAVED_TIME = 1000 * 60 * 60 * 24 * 60L;// 填写订单页发票台头本地保存字段 只有在发票类型为2(公司)时保存
    // 账号切换平台
    public static final int APPID_IOS = 5;
    public static final int APPID_ANDROID = 6;
    public static final int APPID_PC = 11;
    public final int LOGIN_SWITCH_MIN_SIZE = 2;
    // 通用轮播图设计尺寸
    public final int COMMON_IMGCAROUSE_WIDTH = 750;
    public final int COMMON_IMGCAROUSE_HEIGHT = 350;
    // 选座购票页广告尺寸
    public final int ONLINE_SEATS_ADS_IMG1_WIDTH = 750;
    public final int ONLINE_SEATS_ADS_IMG1_HEIGHT = 210;
    public final int ONLINE_SEATS_ADS_IMG2_WIDTH = 240;
    public final int ONLINE_SEATS_ADS_IMG2_HEIGHT = 110;
    // 恐怖海报高斯模糊参数
    public final int FILTER_COVER_BLUR_RADIUS = 25;
    public final int FILTER_COVER_BLUR_SAMPLING = 1;
    // 会员等级
    public static final int USER_LEVER_NORMAL = 0;
    public static final int USER_LEVER_SILVEL = 1;
    public static final int USER_LEVER_GOLDEN = 2;
    public static final int USER_LEVER_PLATINUM = 3;
    public static final int USER_LEVER_DIAMOND = 4;
    // 会员首页弹窗类型
    public static final int GIFT_TYPE_NONE = 0;
    public static final int GIFT_TYPE_LEVEL = 1;
    public static final int GIFT_TYPE_BIRTH = 2;
    public static final int GIFT_TYPE_UPDATE = 3;

    public final String KEY_LOCATION_LEVEL_RELATION = "loc_level_relation";
    // 记录用户信息与登录相关内容
    public final String KEY_USER_INFO = "userinfo";
    // public final String KEY_VOLLEY_COOKIE = "volleycookie";

    // 影片资料页模块
    public final String KEY_MOVIE_TRAILER = "movie_trailer";
    public final String KEY_CINEMA_ID = "cinema_id";
    public final String KEY_CINEMA_NAME = "cinema_name";
    public final String KEY_CINEMA_ADRESS = "cinema_adress";
    public final String KEY_CINEMA_HALL = "cinema_hall";
    public final String KEY_CINEMA_PHONE = "cinema_phone";
    public final String KEY_USER_BUY_TICKET_PHONE = "user_buy_ticket_phone";
    public final String KEY_MOVIE_BEAN = "movie_bean";
    public final String KEY_MOVIE_ID = "movie_id";
    public final String KEY_COMPANY_ID = "company_id";
    public final String KEY_COMPANY_NAME = "company_name";
    public final String KEY_MOVIE_NAME = "movie_name";
    public final String KEY_MOVIE_VERSION_DESC = "movie_version_desc";
    public final String KEY_MOVIE_LANGUAGE = "movie_language";
    public final String KEY_MOVIE_SHOW_DAY_LONG_TIME = "movie_show_day_long_time";
    public final String KEY_MOVIE_ENAME = "movie_e_name";
    public final String KEY_MOVIE_TYPE = "movie_type";
    public final String KEY_MOVIE_TICKET = "movie_isticket";
    public final String KEY_LICENCE_URL = "key_licence_url";
    public final String KEY_MAP_LONGITUDE = "map_longitude"; // 经度
    public final String KEY_MAP_LATITUDE = "map_latitude"; // 纬度
    public final String KEY_VIDEO_ID = "video_id";
    public final String KEY_VIDEO_CURRENT_TIME = "video_current_time";
    public final String KEY_SCORE_TARGET_ID = "score_target_id"; // 将被评论的对象id
    public final String KEY_SCORE_TARGET_NAME = "score_target_name"; // 将被评论的对象名
    public final String KEY_SCORE_TARGET_TYPE = "score_target_type"; // 将被评论的类型（0为影院，1为影片，2为影人）
    public final String KEY_SCORE_TARGET_RATING = "score_target_rating"; // 将被评论对象的分数
    public final String KEY_SCORE_TARGET_PHOTO_URL = "score_target_photo_url"; // 将被评论对象的图片地址
    public final String KEY_SCORE_ACTIVITY_COMMENT_SUCCESS = "score_activity_comment_success"; // 评分activity返回的评分结果key
    public final String KEY_SCORE_ACTIVITY_RESULT = "score_activity_result"; // 评分activity返回的评分结果key
    public final String KEY_SCORE_TARGET_IS_MOVIECOMMENTBEAN = "key_score_target_is_moviecommentbean";
    // 选座、购票
    public final String KEY_GO_ACCOUNT_CENTER = "go_account_center"; // 去账户中心
    public final String KEY_TICKET_DATE_INFO = "ticket_date_info";
    public final String KEY_SEATING_SELECT_AGAIN = "seating_select_again";
    public final String KEY_SEATING_LAST_ORDER_ID = "seating_last_order_id";
    public final String KEY_SEATING_SEAT_ID = "seating_seat_id";
    public final String KEY_SEATING_SELECTED_SEAT_COUNT = "seating_selected_seat_count"; // 被选择的座位数
    public final String KEY_SEATING_DID = "seating_did";
    public final String KEY_MTIME_URL = "mtime_url";
    public final String KEY_SEATING_TOTAL_PRICE = "seating_total_price";
    public final String KEY_SEATING_PRICE_INTRODUCTION = "seating__price_introduction";
    public final String KEY_SEATING_SERVICE_FEE = "seating_service_fee";
    public final String KEY_SEATING_PAY_ENDTIME = "seating_pay_endtime";
    public final String KEY_IS_DO_WITH_OUT_PAY_ORDER = "is_do_with_out_pay_order";
    public final String KEY_SEATING_ORDER_ID = "seating_order_id";
    public final String KEY_PASS_TYPE_ID = "key_pass_type_id";//是否支持华为钱包
    public final String KEY_TOAST_APPEAR = "key_toast_appear";//弹窗出现

    //TODO 华为钱包返回的错误码
    public final String CODE_ADDED = "91013";  //该影票已经添加过了

    public final String KEY_HUAWEI_AUTHROIZE = "key_huawei_authroize";//是否授权过
    public final String KEY_DS_PLATFORM_ID = "ds_platform_id"; // 第三方直销平台的ID
    public final String KEY_DS_ORDER_NO = "ds_order_no"; // 第三方直销平台的ID

    public final String KEY_ISFROM_ACCOUNT = "is_from_account";
    public final String KEY_SEATING_SUBORDER_ID = "seating_suborder_id";
    public final String KEY_SEAT_SELECTED_INFO = "seat_selected_info";
    public final String KEY_SHOWTIME_DATE = "showtime_date";
    public final String KEY_ISMEMBERSHIPCARD = "key_ismembershipcard";
    public final String KEY_PAY_ERROR_TYPE = "pay_error_type"; // 跳转入支付报错页的类型
    public final String KEY_PAY_ERROR_TITLE = "pay_error_title";
    public final String KEY_PAY_ERROR_DETAIL = "pay_error_detail";
    public final String KEY_PAY_ERROR_BUTTON_MESSAGE = "pay_error_button_message";
    public final String KEY_ETICKET_BEAN = "pay_eticket_bean";
    public final String KEY_ETICKET_ID = "pay_eticket_id";
    public final String KEY_BANK_ID = "bank_id";
    public final String CHAR_SLASH = " / ";
    public final String NO_DETAIL = "不详";
    public final String COMMA = ",";
    public final String SMALL_PAY_LIST = "smallpaylist"; // 小卖
    public final String HAS_COUNT_LIST = "hascountlist"; // 小卖(选中的列表)
    public final String CB_PAY_ITEM_BEAN = "cb_pay_item_bean";
    public final String CB_PAY_ORDER_ID = "cb_pay_orderid";
    public final String CB_PAY_ACTIVITYIDS = "cb_pay_activityids";
    public final String PAY_LONG_TIME = "pay_long_time";
    public final String IS_FROM_SEATSELECT_ACTIVITY = "is_from_seatselect_activity";

    public String notVipLoginNum = "";
    public final String KEY_MOVIE_PERSOM_ID = "movie_person_id"; // 影人（导演/演员）id
    public final String KEY_MOVIE_PERSOM_NAME = "movie_person_name"; // 影人（导演/演员）name
    public final String KEY_MOVIE_PERSOM_WORK_COUNT = "movie_person_work_count"; // 影人（导演/演员）作品数
    public final String KEY_FEATURE_EXTRA = "feature_extra"; // 特设设施数据
    public final String KEY_REVIEWID = "reviewid"; // 影评id
    public final String KEY_REVIEW_INDEX = "index"; // 影评点击的标示
    public final String KEY_TOPMOVIEID = "topmovie_id";
    public final String KEY_FROMFIX = "fromfix";
    //    public final String KEY_TOPFIXED_GLOBAL = "key_topfixed_global";
    public final String KEY_NEWSID = "news_id";
    public final String KEY_NEWSTYPE = "news_type";// 新闻类型 1:图集 2:普通
    //    public final String KEY_ARTICLEID = "article_id";
    public final String KEY_ARTICLEYPE = "article_from_type";// 文章来源类型 1:文章 2:榜单
    public final String KEY_COUPON_REMIND_TYPE = "CouponRemindType"; // 我的优惠券页类型

    public final String KEY_COMMENT_SIZE = "comment_size";
    public final String KEY_FROM_FAVOURITE = "fromFavourite";
    public final String KEY_BIND_TYPE = "bindtype";
    public final String KEY_FROM_REVIEW = "fromreview";
    public final String KEY_SHOWTITLE = "showtitle";
    public final String KEY_CLOSE_PARENT = "close_parent";

    public final String MAIN_TAB0_GOTOTYPE = "main_tab0_gototype";// 因为int默认是0，所以从1开始
    public final String MAIN_TAB_BUYTICKET_TYPE = "main_tab_buyticket_type";
    public final String MAIN_TAB_BUYTICKET_SHOWCINEMA = "main_tab_buyticket_showcinema";
    public final String MAIN_TAB_BUYTICKET_CINEMA_LIST_REFRESH = "main_tab_buyticket_cinema_list_refresh";
    // 在确认订单页面显示 4个
    public final String KEY_TICKET_TIME_INFO = "key_ticket_time_info";
    public final String KEY_TICKET_HALLNAME_INFO = "key_ticket_hallname_info";
    public final String KEY_TICKET_VERSIONDESC_INFO = "key_ticket_versiondesc_info";
    public final String KEY_TICKET_LANGUAGE_INFO = "key_ticket_language_info";
    // baidu collection used.
    public final String KEY_ACTIVITY_FROM = "activity_from";
    // 支付模块
    public final String TICKET_PRICE = "ticket_price";
    public final String PAY_ETICKET = "pay_etickey"; // 电子券
    public final String COUNT_TIME = "count_time";

    public final String PAY_MESSAGE = "pay_message";
    public final String PAY_MONEY = "pay_money";
    public final String KEY_LOCATION_ID = "location_id";
    public final String WAP_PAY_URL = "wap_pay_url";
    public final String MOVIE_CARD_PAY = "movie_card_pay";
    public final String KEY_PRESELLPAYMENTMODE = "presellPaymentMode"; // 商城支付-跳转到礼品卡列表
    public final String KEY_ISFINALPAY = "isFinalPay"; // 商城支付-跳转到礼品卡列表
    public final String KEY_BALANCE_REDUCE_PRICE = "BalanceReducePrice"; // 商城支付-跳转到礼品卡列表
    public final String KEY_TOTALPRICE = "TotalPrice"; // 商城支付-跳转到礼品卡列表
    public final String KEY_ISORDERVALIDITY = "isOrderValidity"; // 商城支付-跳转到礼品卡列表
    public final String KEY_CARDTYPE = "card_type"; // 商城支付-跳转到礼品卡列表
    public final String KEY_PAYBINDPHONE = "payBindphone"; // 商城支付-跳转到礼品卡列表
    public final String KEY_FEEDBACK_MAIN = "feed_back_main";
    // 图片列表
    public final String KEY_PHOTO_LIST_TARGET_ID = "photo_list_target_id";
    public final String KEY_PHOTO_LIST_TITLE = "photo_list_title"; // 名称标题
    public final String KEY_PHOTO_LIST_TARGET_TYPE = "photo_list_target_type"; // 图片类型（电影/影人）
    public final String KEY_PHOTO_LIST_TYPE = "photo_list_type"; // 影片类型
    public final String KEY_PHOTO_LIST_DATA = "photo_list_data";
    public final String KEY_PHOTO_LIST_POSITION_CLICKED = "photo_list_position_clicked";
    public final String KEY_PHOTO_LIST_POSITION_RESULT = "photo_list_position_result";
    public final String KEY_PHOTO_LIST_TOTALCOUNT = "photo_list_totalcount";

    public final String KEY_PHOTO_DETAIL_SINGLE_URL = "key_photo_detail_single_url"; // 具体单张图片的url
    public final String KEY_PHOTO_DETAIL_SINGLE_IMAGEID = "key_photo_detail_single_imageid"; // 具体单张图片的id
    // 广告模块
    public final String KEY_ADVERT_NUM = "KEY_ADVERT_NUM";
    public final String KEY_ADVERT_PHOTO_URL_START_PAGE = "KEY_ADVERT_PHOTO_URL_START_PAGE";
    public final String KEY_ADVERT_PHOTO_URL_START_DATE = "KEY_ADVERT_PHOTO_URL_START_DATE";
    public final String KEY_ADVERT_PHOTO_URL_END_DATE = "KEY_ADVERT_PHOTO_URL_END_DATE";
    public final String KEY_ADVERT_HOME_JUMP_BEAN = "KEY_ADVERT_HOME_JUMP_BEAN";// 首页开屏广告跳转bean
    public final String CACHE_SPLASH_AD_KEY1 = "cache_splash_ad_key1";
    public final String CACHE_SPLASH_AD_KEY2 = "cache_splash_ad_key2";
    public final String KEY_WEBVIEW_TITLE_NAME = "webview_title_name"; // webview的标题文字key

    public final String KEY_SERVICE_DATE = "service_date";
    public final String SEEN_TYPE_MOVIE = "seen_type_movie";
    public final String SEEN_TYPE_REVIEW = "seen_type_review";
    public final String SEEN_TYPE_GUIDE = "seen_type_guide";
    public final String SEEN_TYPE_TOP = "seen_type_top";
    public final String SEEN_TYPE_TRAILER = "seen_type_trailer";
    // 我的电影_想看/看过页
    public final String KEY_MY_MOVIE_CURRENT_WINDOW = "my_movie_current_window";
    /**
     * 是否显示引导页
     */
    public final String MORE_THAN_ONCE = "more_than_once_v5";
    /**
     * 城市选择按返回键是正常走，还是跳到首页 false默认跳首页
     */
    public final String MORE_THAN_ONCE_V5_2_7 = "more_than_once_v5";

    public final String LAST_TIME_COMMENTARY = "lastTimeCommentary";

    public final String KEY_BINDPHONE = "key_bindphone";
    public final String KEY_MOVIE_SHOWTIME_DATE = "key_movie_showtime_date";
    public final String KEY_CINEMA_SHOWTIME_DATE = "key_cinema_showtime_date";
    // 推送
    public final String KEY_MESSAGEBROADCAST = "key_messagebroadcast";
    public final String KEY_MESSAGENOTIFICATION = "key_messagenotification";
    public final String KEY_THIRD_LOGIN_TOKEN = "key_third_login_token";
    public final String KEY_THIRD_LOGIN_CODE = "key_third_login_code";
    public final String KEY_THIRD_LOGIN_EXPIRES = "key_third_login_expires";
    public final String KEY_THIRD_LOGIN_PLATFORM_ID = "key_third_login_platformid";
    public final String KEY_RETRIEVE_PASSWORD_BY_PHONE_TOKEN = "key_retrieve_password_by_phone_token";
    public final String KEY_THIRD_LOGIN_ONLY_SHOW_REGISTER = "key_third_login_only_show_register";
    public final String KEY_THIRD_LOGIN_OAUTHCODE = "key_third_login_oauthcode";
    // public final String BAIDU_RELEASE_VERSION = "5.1.6";
    // AD
    public final String AD_START_APP = "100";
    public final String AD_HOME_RICH = "101";
    // public final String AD_HOME_BOTTOM = "103";

    public final String AD_MOVIE_INCOMING = "201";
    public final String AD_MOVIE_BANNER1 = "202";
    // public final String AD_MOVIE_BANNER2 = "203";
    public final String AD_ACTOR_BANNER1 = "204";
    public final String AD_ACTOR_BANNER2 = "205";

    public final String AD_CINEMA_LIST = "301";
    public final String AD_CINEMA_BANNER1 = "302";
    // public final String AD_CINEMA_BANNER2 = "303";

    public final String AD_SEARCH_BEFORE = "401";
    public final String AD_SEARCH_AFTER = "402";

    public final String AD_TOP = "601";

    public final String AD_FIND_1 = "602"; // 发现频道第二帧广告
    // public final String AD_FIND_2 = "603"; // 发现频道第四帧广告
    // public final String AD_FIND_3 = "604"; // 发现频道第五帧广告

    public final String AD_NEWS_LIST_1 = "605"; // 新闻列表第3条下banner
    public final String AD_NEWS_LIST_2 = "606"; // 具体文字新闻页底部banner
    // public final String AD_NEWS_LIST_3 = "607"; // 图片新闻最后一帧广告
    public final String AD_MOVIE_COMMENTS = "608"; // 具体影评页底部banner
    /*
     * // Scheme_H5 NOW. 501-商城首页头图banner1 502-具体商品页banner1 503-购物成功页banner1 504-购物成功页banner2 505-商城首页banner2
     */

    public final String HAS_MY_TAB_GIFT_PACK_REMIND = "hasMyTabGiftPackRemind"; // 是否有等级礼包或者生日礼包未领取提醒，用于"我的"tab上的礼包Icon显示
    public final String HAS_MY_TAB_CART_REMIND = "hasMyTabCartRemind"; // 是否有购物车提醒，用于"我的"tab上的红点显示
    public final String HAS_BOUGHT_MOVIES_REMIND = "hasBoughtMoviesremind";// 是否有已购影片提醒，用于"我的"tab上的红点显示
    public final String HAS_GOODS_ORDER_REMIND = "hasGoodsOrderRemind"; // 是否有商品订单提醒，用于"我的"tab上的红点显示
    public final String KEY_NOTDISTURB_TIME_START = "key_notdisturb_time_start";// 免打扰时间
    public final String KEY_NOTDISTURB_TIME_END = "key_notdisturb_time_end";

    // address
    public final String API_ADDRESS_ALIWAP_PAY = "https://api-m.mtime.cn/account/appWapPayReturn/AliWapPay/";
    public final String API_ADDRESS_WAPPAY_RETURN = "https://api-m.mtime.cn/account/appWapPayReturn/";
    // 版本Key
    public final String VERSION_KEY = "version_key";

    /*------首页推荐位专用字段 Start-------*/
    public final String HOME_SUBFIRST_ID = "subFirstID";// 第一个推荐位内容ID
    public final String HOME_SUBFIRST_SHOW_COUNT = "subFirstShowCount";// 第一个推荐位内容展示次数
    public final String HOME_SUBFIRST_OPERATE = "subFirstOperate";// 第一个推荐位内容操作类型

    public final String HOME_SUBSECOND_ID = "subSecondID";// 第二个推荐位内容ID
    public final String HOME_SUBSECOND_SHOW_COUNT = "subSecondShowCount";// 第二个推荐位内容展示次数
    public final String HOME_SUBSECOND_OPERATE = "subSecondOperate";// 第二个推荐位内容操作类型

    public final String HOME_SUBTHIRD_ID = "subThirdID";// 第三个推荐位内容ID
    public final String HOME_SUBTHIRD_SHOW_COUNT = "subThirdShowCount";// 第三个推荐位内容展示次数
    public final String HOME_SUBTHIRD_OPERATE = "subThirdOperate";// 第三个推荐位内容操作类型

    public final String HOME_SUBFOURTH_ID = "subFourthID";// 第四个推荐位内容ID
    public final String HOME_SUBFOURTH_SHOW_COUNT = "subFourthShowCount";// 第四个推荐位内容展示次数
    public final String HOME_SUBFOURTH_OPERATE = "subFourthOperate";// 第四个推荐位内容操作类型

    public final String HOME_SUBFIFTH_ID = "subFifthID";// 第五个推荐位内容ID
    public final String HOME_SUBFIFTH_SHOW_COUNT = "subFifthShowCount";// 第五个推荐位内容展示次数
    public final String HOME_SUBFIFTH_OPERATE = "subFifthOperate";// 第五个推荐位内容操作类型

    public final String HOME_SUBSIXTH_ID = "subSixthID";// 第六个推荐位内容ID
    public final String HOME_SUBSIXTH_SHOW_COUNT = "subSixthShowCount";// 第六个推荐位内容展示次数
    public final String HOME_SUBSIXTH_OPERATE = "subSixthOperate";// 第六个推荐位内容操作类型

    /*------首页推荐位专用字段 End---------*/
    public final String KEY_VIDEO_HIGH_QUALITY_URL = "video_high_quality_url";

    public final String MALL_EXPRESSLIST = "expressList";// 填写订单页配送方式本地保存字段
    public final String MALL_DELIVERYTIMELIST = "deliveryTimeList";// 填写订单页发货时间本地保存字段
    public final String MALL_INVOICE_TITLE = "invoiceTitle";// 填写订单页发票台头本地保存字段 只有在发票类型为2(公司)时保存
    public final String MALL_INVOICE_TELPHONE = "invoiceTelphone";// 填写订单页电子发票收票人电话本地保存字段
    public final String MALL_INVOICE_TYPE = "invoiceType";// 填写订单页发票类型
    public final String MALL_INVOICE_TAX = "invoiceTax";// 填写订单页税号

    //Mytab
    public final String TAB_MY_GUIDE_SHOW = "tab_my_guide_show";// My Tab首次进来的引导

    public final String MALL_GOODSINFO_GOODSID = "goodsIDList";// 用户进入商品详情页的商品ID本地保存字段
    public final String ISMANAGER = "isManager";

//    public final String MALL_ICON_SELECTED = "mallIconSelected";
//    public final String MALL_ICON_UNSELECTED = "mallIconUnSelected";
//    public final String MALL_NAME = "mallName";

    //社区tab
    public final String COMMUNITY_ICON_SELECTED = "communityIconSelected";
    public final String COMMUNITY_ICON_UNSELECTED = "communityIconUnSelected";
    public final String COMMUNITY_NAME = "communityName";

    public final String GAME_ICON_SELECTED = "gameIconSelected";
    public final String GAME_ICON_UNSELECTED = "gameIconUnSelected";
    public final String GAME_NAME = "gameName";

    public final String HOME_ICON_SELECTED = "homeIconSelected";
    public final String HOME_ICON_UNSELECTED = "homeIconUnSelected";
    public final String HOME_NAME = "homeName";

    public final String TICKET_ICON_SELECTED = "ticketIconSelected";
    public final String TICKET_ICON_UNSELECTED = "ticketIconUnSelected";
    public final String TICKET_NAME = "ticketName";

//    public final String VIDEO_ICON_SELECTED = "findIconSelected";
//    public final String VIDEO_ICON_UNSELECTED = "findIconUnSelected";
//    public final String VIDEO_NAME = "findName";

    public final String VIDEO_ICON_SELECTED = "videoIconSelected";
    public final String VIDEO_ICON_UNSELECTED = "videoIconUnSelected";
    public final String VIDEO_NAME = "videoName";

    public final String MINE_ICON_SELECTED = "mineIconSelected";
    public final String MINE_ICON_UNSELECTED = "mineIconUnSelected";
    public final String MINE_NAME = "mineName";

    // 公众号ID
    public final String OFFICIAL_ACCOUNT_ID = "official_account_id";

    // 文章评论ID
    public final String ARTICLE_COMMENT_ID = "article_comment_id";

    public final String REGISTER_SEX_CACHE = "registerSex_";
    // 是否弹新手礼包图片
    public final String KEY_SHOW_NEWGIFT_DLG = "showNewGiftDlg";
    public final String MALL_LASTTIME = "mallLastTime";// 商城首页商品优惠券上次请求的时间
    public final String MALL_COUPON_CLOSE_TIME = "mallCouponCloseTime";// 用户点击优惠券提示关闭按钮的时间
    public final String MALL_COUPON_OUTDATE_CLOSE = "mallCoupnOutdateColse";// 过了7天之后当优惠券提示又出现时用户又点击了关闭按钮
    // 首页今日热点第一条和最后一条底部分隔线tag
    // public final String HOME_HOTPOINT_FIRST_SEPERATE = "hotpoint_first_seperate";
    // public final String HOME_HOTPOINT_LAST_SEPERATE = "hotpoint_last_seperate";

    public final String KEY_FILTER_SET = "filter_set";
    // 0, 时光网登陆；1， 微博登陆； 2， QQ登陆
    public String KEY_TARGET_ACTIVITY_ID = "target_activity_Id";
    public String KEY_TARGET_NOT_VIP = "target_not_vip";
    public String KEY_SHOW_NOT_VIP = "show_not_vip";
    public String KEY_TARGET_NOT_VIP_PHONE = "target_not_vip_phone";
    public String KEY_REQUEST_CODE = "RequestCode";

    public String SEATS_ICON_TAG = "seats_icon_tag";

    // 登录之后的状态对象
    public static final int STATUS_CLOSE = 1;
    public static final int STATUS_LOGIN = 2;
    public static final int STATUS_NO_VIP = 3;
    public static final int STATUS_REG_PHONE = 4;

    // 我的收藏使用到的
    public static final int STATUS_MOVIE_REFRESH = 1000;
    public static final int STATUS_PERSON_REFRESH = 1001;
    public static final int STATUS_CINEMA_REFRESH = 1002;
    public static final int STATUS_REVIEW_REFRESH = 1003;
    public static final int STATUS_NEWS_REFRESH = 1004;
    public static final int STATUS_TOP_REFRESH = 1005;
    public static final int STATUS_ARTICLE_REFRESH = 1006;

    public static String mallSearchHint = "";
    public static String appSearchHint = "";

    // 首页、商城首页_专题入口模版类型
    public final static int TYPE_HOME_UNDER_HOTPLAY = 1; // 首页-购票下
    // public final static int TYPE_HOME_UNDER_MALL = 2; // 首页-商品推荐下
    // public final static int TYPE_HOME_UNDER_ENTRY = 3; // 首页-四个入口下
    // public final static int TYPE_HOME_UNDER_MIDDEL_AD = 4; // 首页-广告下
    // public final static int TYPE_MALL_UNDER_ICON = 5; // 商城首页-8个入口下
    // public final static int TYPE_MALL_UNDER_ROLE = 6; // 商城首页-角色下
    // public final static int TYPE_MALL_UNDER_CATEGORY = 7; // 商城首页-分类推荐下
    public final static int TYPE_MALL_UNDER_FLASH = 8; // 商城—闪购特惠入口下

    // 影片资料二级页面eventCode
    public static final int MOVIE_SUB_PAGE_EVENT_CODE_BACK = 1001;  // 返回
    public static final int MOVIE_SUB_PAGE_EVENT_CODE_SHARE = 1002; // 分享

}
