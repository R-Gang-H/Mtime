package com.mtime.constant;

import com.mtime.common.utils.PrefsManager;
import com.mtime.frame.App;

/**
 * @author ZhouSuQiang
 * @date 2018/8/6
 *
 * 全局的SharedPreference管理类
 */
public class SpManager {
    
    /**
     * 全局的SharedPreference
     */
    public static PrefsManager get() {
        return PrefsManager.get(App.get());
    }
    
    /**
     * 这里的清除是为了在用户退出登录时，
     * 清除与用户帐号相关联的缓存记录；
     * 与用户帐号无关的缓存不用清楚；
     */
    public static void clearUsageRecord() {
        get().removeKey(SP_KEY_RED_POINT_LAST_CLICK_TIME);
        get().removeKey(SP_KEY_COUPON_REMIND_REMIND_TIME);
        get().removeKey(SP_KEY_COUPON_REMIND_COUPON_STATUS);
        get().removeKey(SP_KEY_COUPON_REMIND_EXPIRE_REMIND_TIME);
    }
    
    /** 主页面"我的tab"上红点的点击时间 */
    public static final String SP_KEY_RED_POINT_LAST_CLICK_TIME = "sp_key_red_point_last_click_time";
    /** 主页面"我的tab"上方的优惠券提示时间 */
    public static final String SP_KEY_COUPON_REMIND_REMIND_TIME = "sp_key_coupon_remind_remind_time";
    /** 主页面"我的tab"上方的优惠券提示(即将到期优惠券提醒)时间 */
    public static final String SP_KEY_COUPON_REMIND_EXPIRE_REMIND_TIME = "sp_key_coupon_remind_expire_remind_time";
    /** 主页面"我的tab"上方的优惠券提示时间 */
    public static final String SP_KEY_COUPON_REMIND_COUPON_STATUS = "sp_key_coupon_remind_coupon_status";
    /** 定位权限用户拒绝（拒绝后 以后便不再申请权限） */
    public static final String SP_KEY_LOCATION_PERMISSION_DENIED = "sp_key_location_permission_denied";
    // 个性化推荐
    public static final String SP_KEY_PERSONALITY_RECOMMEND = "sp_key_personality_recommend";
}
