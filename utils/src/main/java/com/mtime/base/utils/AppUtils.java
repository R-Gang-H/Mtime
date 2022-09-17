package com.mtime.base.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by LiJiaZhi on 17/3/31.
 * <p>
 * 应用相关工具类
 */
public class AppUtils {

    private static final String TAG = AppUtils.class.getSimpleName();

    @SuppressLint("NewApi")
    @SuppressWarnings("ResourceType")
    private static UsageEvents getUsageEvents(Context context) {
        final UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");
        UsageEvents events =
                usageStatsManager.queryEvents(System.currentTimeMillis() - 24 * 60 * 60 * 1000, System.currentTimeMillis());
        if (!events.hasNextEvent()) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        return events;
    }

    /**
     * 5.0以上系统使用的
     *
     * @param context 上下文
     * @param num     数量
     * @return 列表
     */
    @SuppressLint("NewApi")
    public static List<RecentApp> getRecentRunningTaskLollipop(Context context, int num) {
        UsageEvents events = getUsageEvents(context);
        List<RecentApp> recentApps = new ArrayList<>();
        // Get apps shown in launcher
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> installedApps = context.getPackageManager().queryIntentActivities(mainIntent, 0);
        final Set<String> installedPackages = new HashSet<>();
        final Set<String> addedPackages = new HashSet<>();
        String launcher = getLauncher(context);
        String packageName = context.getPackageName();
        for (ResolveInfo info : installedApps) {
            if (info.activityInfo.packageName.equalsIgnoreCase(launcher)
                    || info.activityInfo.packageName.equalsIgnoreCase(packageName)) {
                continue;
            }
            installedPackages.add(info.activityInfo.packageName);
        }
        while (events.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            events.getNextEvent(event);
            String pkg = event.getPackageName();
            RecentApp app = new RecentApp();
            app.packageName = event.getPackageName();
            app.intent = new Intent("android.intent.action.MAIN");
            try {
                app.intent.setClassName(event.getPackageName(), event.getClassName());
            } catch (Exception e) {
                Log.e(TAG, "catch exception when set class name for " + app.packageName);
                continue;
            }
            app.icon = null;
            if (installedPackages.contains(pkg) && !addedPackages.contains(pkg)) {
                recentApps.add(app);
                addedPackages.add(pkg);
            }
        }
        // Sort by most recent
        Collections.reverse(recentApps);
        if (recentApps.size() > num) {
            recentApps = recentApps.subList(0, num);
        }
        return recentApps;
    }

    /**
     * 5.0以下系统使用的
     *
     * @param context 上下文
     * @param num     数量
     * @return 列表
     */
    @SuppressWarnings("deprecated")
    public static List<RecentApp> getRecentRunningTask(Context context, int num) {
        // <uses-permission android:name="android.permission.GET_TASKS" />
        List<RecentApp> result = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        ActivityManager activityManager = (ActivityManager) (context.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RecentTaskInfo> runningTaskInfos = activityManager.getRecentTasks(num + 10, 0);
        if (runningTaskInfos != null && runningTaskInfos.size() > 0) {
            String launcher = getLauncher(context);
            String packageName = context.getPackageName();
            for (ActivityManager.RecentTaskInfo info : runningTaskInfos) {
                Intent intent = new Intent("android.intent.action.MAIN");// info.baseIntent;
                intent.setComponent(info.baseIntent.getComponent());
                ResolveInfo resolveInfo = pm.resolveActivity(info.baseIntent, 0);
                if (resolveInfo != null && resolveInfo.activityInfo != null) {
                    if (resolveInfo.activityInfo.packageName.equalsIgnoreCase(launcher)
                            || resolveInfo.activityInfo.packageName.equalsIgnoreCase(packageName)) {
                        continue;
                    }
                    Log.v(TAG, "get recent running task:" + resolveInfo.activityInfo.packageName);
                    RecentApp app = new RecentApp();
                    app.packageName = resolveInfo.activityInfo.packageName;
                    app.intent = intent;
                    app.icon = resolveInfo.loadIcon(pm);
                    result.add(app);
                    if (result.size() >= num) {
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * 获取当前launcher进程名
     *
     * @param context 上下文
     * @return 进程名
     */
    public static String getLauncher(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo =
                context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }

    /**
     * 查看当前程序是否在前台运行
     * 存在问题，需要新的权限 <uses-permission android:name="android.permission.GET_TASKS" />
     */
    public static boolean isRunningForeground(Context context, String packageName) {
        // <uses-permission android:name="android.permission.GET_TASKS" />
        String topActivityClassName = null;
        ActivityManager activityManager = (ActivityManager) (context.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
        if (runningTaskInfos != null) {
            ComponentName f = runningTaskInfos.get(0).topActivity;
            topActivityClassName = f.getClassName();
        }
        return topActivityClassName != null && topActivityClassName.startsWith(packageName);
    }

    /**
     * 判断程序是否在前台运行
     * 存在问题，如果程序有一个前台运行的service则会始终返回true
     */
    public static boolean isRunningForeground2(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    Log.d("background", appProcess.processName);
                    return false;
                } else {
                    Log.i("foreground", appProcess.processName);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取app的启动activity
     */
    public static Class getSelfLauncherActivity(Context context) {
        String packageName = context.getPackageName();
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        String className = launchIntent.getComponent().getClassName();
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "class not found:" + className);
            return null;
        }
    }

    /**
     * 判断屏幕是否点亮
     */
    public static boolean isScreenOn(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return pm.isScreenOn();
    }

    /**
     * 获取程序版本号
     */
    public static String getAppVersion(Context context) {
        PackageInfo packInfo = getPackageInfo(context);
        if (packInfo != null) {
            return packInfo.versionName;
        } else {
            return "";
        }
    }

    /**
     * 获取程序版本code
     */
    public static int getAppVersionCode(Context context) {
        PackageInfo packInfo = getPackageInfo(context);
        if (packInfo != null) {
            return packInfo.versionCode;
        } else {
            return -1;
        }
    }

    /**
     * 从AndroidManifest.xml中读取自定义的meta数据
     */
    public static String getMetaData(Context context, String key) {
        try {
            ApplicationInfo ai =
                    context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            return bundle.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
        }
        return null;
    }

    private static PackageInfo getPackageInfo(Context context) {
        // 获取PackageManager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo;
        try {
            if (packageManager == null) {
                return null;
            }
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packInfo;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "catch exception when get version, e:" + e.getMessage());
            return null;
        }
    }

    public static boolean isWeChatInstalled(Context context) {
        return isApplicationInstalled(context, "com.tencent.mm");
    }

    public static boolean isWeiboInstalled(Context context) {
        return isApplicationInstalled(context, "com.sina.weibo");
    }

    public static boolean isQQINstalled(Context context) {
        return isApplicationInstalled(context, "com.tencent.mobileqq") || isApplicationInstalled(context, "com.tencent.tim");
    }

    public static boolean isApplicationInstalled(Context context, String appPackageName) {
        return isApplicationInstalledOther(context, appPackageName);
    }

    /**
     * 坑
     * https://www.jianshu.com/p/ddf194530bb6
     * http://blog.csdn.net/xxooyc/article/details/50162523
     *
     * @param context
     * @param appPackageName
     * @return
     */
    private static boolean isApplicationInstalledOther(Context context, String appPackageName) {
        PackageManager pm = context.getPackageManager();
        try {
            synchronized (AppUtils.class) {
                pm.getPackageInfo(appPackageName, PackageManager.GET_SIGNATURES);
            }
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 获取 MAC 地址
     * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
     */
    @SuppressWarnings("all")
    public static String getMacAddress(Context context) {
        // wifi mac地址
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        String mac = info.getMacAddress();
        Log.i(TAG, "MAC:" + mac);
        return mac;
    }

    /**
     * 获取 开机时间
     */
    public static String getBootTimeString() {
        long ut = SystemClock.elapsedRealtime() / 1000;
        int h = (int) ((ut / 3600));
        int m = (int) ((ut / 60) % 60);
        Log.i(TAG, "boot time:" + h + ":" + m);
        return h + ":" + m;
    }

    /**
     * 获取android userId
     */
    private static String getAndroidID(Context context) {
        String m_szAndroidID = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if ("9774d56d682e549c".equals(m_szAndroidID) || TextUtils.isEmpty(m_szAndroidID)) {
            m_szAndroidID = Guid.get();
        }
        return m_szAndroidID;
    }

    public static String getDeviceId(Context context) {
        String udid = "";
        if (TextUtils.isEmpty(udid)) {
            udid = "android" + getAndroidID(context) + android.os.Build.SERIAL;
        }
        return udid;
    }

    public static class RecentApp {
        public String packageName;
        public Drawable icon;
        public Intent intent;
    }
}
