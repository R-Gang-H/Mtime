package com.mtime.base.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import java.lang.reflect.Method;

/**
 * Created by LiJiaZhi on 17/3/31.
 *
 * app通用类
 */

public class MScreenUtils {
    private static Context sContext;
    
    public static void init(Context context) {
        if(null != context) {
            sContext = context.getApplicationContext();
        }
    }
    
    private static void checkNullContext() {
        if (null == sContext) {
            MLogWriter.e("MScreenUtils", "MScreenUtils...context is null, 请在init方法中传入context");
            throw new RuntimeException("MScreenUtils...context is null, 请在init方法中传入context");
        }
    }
    
    public static int getScreenHeight() {
        checkNullContext();
        return sContext.getResources().getDisplayMetrics().heightPixels;
    }
    
    public static int getScreenWidth() {
        checkNullContext();
        return sContext.getResources().getDisplayMetrics().widthPixels;
    }
    
    public static int getScreenWidthDp() {
        checkNullContext();
        return px2dp(getScreenWidth());
    }
    
    public static int getScreenHeightDp() {
        checkNullContext();
        return px2dp(getScreenHeight());
    }
    
    public static int dp2px(float dpValue) {
        final float scale = getDensity();
        return (int) (dpValue * scale + 0.5f);
    }
    
    public static int px2dp(float pxValue) {
        final float scale = getDensity();
        return (int) (pxValue / scale + 0.5f);
    }
    
    public static int px2sp(float pxValue) {
        final float scale = getDensity();
        return (int) (pxValue / scale + 0.5f);
    }
    
    public static int sp2px(float spValue) {
        final float scale = getDensity();
        return (int) (spValue * scale + 0.5f);
    }
    
    public static int getDpi() {
        checkNullContext();
        return sContext.getResources().getDisplayMetrics().densityDpi;
    }
    
    public static float getDensity() {
        checkNullContext();
        return sContext.getResources().getDisplayMetrics().density;
    }
    
    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight() {
        checkNullContext();
        // 默认为38
        int height = 38;
        //获取status_bar_height资源的ID
        int resourceId = sContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            height = sContext.getResources().getDimensionPixelSize(resourceId);
        Log.e("Hubert", "状态栏的高度:" + height);
        return height;
    }
    
    /**
     * 虚拟操作拦（home等）是否显示
     */
    public static boolean isNavigationBarShow(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y != size.y;
        } else {
            boolean menu = ViewConfiguration.get(activity).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            return !menu && !back;
        }
    }
    
    /**
     * 获取虚拟操作拦（home等）高度
     */
    public static int getNavigationBarHeight(Activity activity) {
        if (!isNavigationBarShow(activity))
            return 0;
        int height = 0;
        Resources resources = activity.getResources();
        //获取NavigationBar的高度
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0)
            height = resources.getDimensionPixelSize(resourceId);
        Log.e("Hubert", "NavigationBar的高度:" + height);
        return height;
    }
    
    
    
    //以下为废弃的方法///////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 将一个px转换成dp值
     *
     * @param context 上下文对象
     * @param px      需要转换的px值
     * @return px对应的dp值
     */
    @Deprecated
    public static int px2dip(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    @Deprecated
    public static int getScreenHight(Context context) {
        return getScreenHeight();
    }

    @Deprecated
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    @Deprecated
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    @Deprecated
    public static int getScreenWidthDp(Context context) {
        int heightInDp = px2dp(context, getScreenWidth(context));
        return heightInDp;
    }

    @Deprecated
    public static int getScreenHeightDp(Context context) {
        int widthInDp = px2dp(context, getScreenHeight(context));
        return widthInDp;
    }

    @Deprecated
    public static int dp2px(Context context, float dpValue) {
        final float scale = getDensity(context);
        return (int) (dpValue * scale + 0.5f);
    }

    @Deprecated
    public static int px2dp(Context context, float pxValue) {
        final float scale = getDensity(context);
        return (int) (pxValue / scale + 0.5f);
    }

    @Deprecated
    public static int px2sp(Context context, float pxValue) {
        final float scale = getDensity(context);
        return (int) (pxValue / scale + 0.5f);
    }

    @Deprecated
    public static int sp2px(Context context, float spValue) {
        final float scale = getDensity(context);
        return (int) (spValue * scale + 0.5f);
    }

    @Deprecated
    public static int getDpi(Context context) {
        return context.getResources().getDisplayMetrics().densityDpi;
    }

    @Deprecated
    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * 获取状态栏高度
     */
    @Deprecated
    public static int getStatusBarHeight(Context context) {
        // 默认为38
        int height = 38;
        //获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            height = context.getResources().getDimensionPixelSize(resourceId);
        Log.e("Hubert", "状态栏的高度:" + height);
        return height;
    }
    
}
