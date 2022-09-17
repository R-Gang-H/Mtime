package com.mtime.constant;

/**
 * Created by LEE on 7/20/16.
 */
public class Constants {
    /*------------------------------ 静态变量 Begin ------------------------------*/

    public static boolean isShowRegisterGiftDlg = true;

    /*------------------------------ 静态变量 End --------------------------------*/

    /*------------------------------ 静态常量 Begin ------------------------------*/

    public static final String NEW_REGISTER_GIT_URL = "new_register_gift_url";
    public static final String KEY_MAIN_TAB_INDEX = "key_main_tab_index";

    public static final long REQUEST_CHACHE_TIME_10MINS = 10 * 60 * 1000L;
    public static final long REQUEST_CHACHE_TIME_30MINS = 30 * 60 * 1000L;
    public static final long REQUEST_CACHE_TIME_1DAY = 24 * 60 * 60 * 1000L;

    public static final String BIND_FROM = "bind_type";
    public static final String BIND_PLATFORM = "bind_platform";
    public static final String BIND_THIRD_ACCESS_TOKEN = "bind_third_access_token";
    public static final String BIND_WEIXIN_CODE = "bind_xp_code";
    public static final String BIND_QQEXPIRES = "bind_qqexpires";
    public static final String BIND_ACCESS_TOKEN = "bind_access_token";
    public static final String BIND_LOGIN_ACCOUNT = "bind_login_account";
    public static final String BIND_LOGIN_PASSWORD = "bind_login_password";
    public static final String BIND_SMS_CODE = "bind_sms_code";
    public static final String BIND_SMS_CODEID = "bind_sms_codeid";
    public static final String BIND_SKIP_LABEL = "bind_skip_label";
    public static final String BIND_SKIP_STATUS = "bind_skip_status";
    public static final String BIND_HAD_PASSWORD = "bind_need_password";
    public static final String BIND_MOBILE_NUMBER = "bind_mobile_number";
    public static final String BIND_REGISTER_STATUS = "bind_register_status";
    public static final String BIND_MOBILE_TOKEN = "bind_mobile_token";
    public static final String BIND_MOBILE_WITH_LOGIN = "bind_mobile_with_login";
    public static final String BIND_MOBILE_WITH_THIRD_OAUTHTOKEN = "bind_mobile_with_third_oauthtoken";

    public static final int BIND_PHONE_LOGIN = 1; // 正常登陆绑定
    public static final int BIND_PHONE_THIRD_LOGIN = 2; // 第三方登录绑定
    public static final int BIND_PHONE = 3; // 可能用到的重新绑定,
    public static final int BIND_AFTER_LOGIN = 4; // 登录之后的第一次绑定。

    // bugly appid
    public static final String BUGLG_APPID_RELEASE = "75a75ae4cc";

    //视频专题ID
    public static final String VIDEO_TOPIC_ID = "video_topic_id";
    //视频专题的评论ID
    public static final String VIDEO_COMMENT_ID = "video_comment_id";

    public static final int DIRECT_SALES_FLAG = 1; // 第三方直销影院
    public static final int DIRECT_SALES_ACTIVITY_FLAG = 1; // 为第三方直销影院准备的新类型票补活动

    // 搜索&筛选_影片筛选
    public static final String CHOOSE_MOVIE_SEARCH_TYPE_MOVIE = "1";
    public static final String CHOOSE_MOVIE_SEARCH_TYPE_TV = "2";
    public static final String CHOOSE_MOVIE_SOURCE_ALL = "3";
    public static final String CHOOSE_MOVIE_ONLINE_FREE = "1";
    public static final String CHOOSE_MOVIE_ONLINE_PAY = "2";

    // 反馈/举报的话题ID
    public static final int FEEDBACK_TOPIC_ID = 1505;

    public static final String AES_KEY = "2C09D728A90A45A4";

    public static final String BUNDLE_KEY_AGREE_CHECK = "bundle_key_agree_check";

    /*------------------------------ 静态常量 End --------------------------------*/

}
