package com.mtime.base.utils;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by LiJiaZhi on 17/3/31.
 *
 * app桌面图标的角标
 */

public class MBadgeUtils {

    private static final int MAX_MESSAGE_COUNT = 99;

    /**
     * 获取当前APP的启动页面activity的classname
     * @param context
     * @return
     */
    private static String getLauncherClassName(Context context){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setPackage(context.getPackageName());
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager packageManager = context.getPackageManager();
        ResolveInfo resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if(resolveInfo == null){
            resolveInfo = packageManager.resolveActivity(intent,0);
        }

        return resolveInfo.activityInfo.name;
    }

    /**
     * 根据不同的Android机型调用不同的处理方法
     * @param context
     * @param notification
     * @param count
     */
    public static void setBadgeCount(Context context, Notification notification, int count){
        if(count <= 0){
            count = 0;
        }else{
            count = Math.min(count,MAX_MESSAGE_COUNT);
        }

        if(Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")){//小米
            //sendToXiaoMi(context,notification,count);
        }else if(Build.MANUFACTURER.equalsIgnoreCase("meizu")){//魅族

        }else if(Build.MANUFACTURER.equalsIgnoreCase("Huawei")){//华为

        }else if(Build.MANUFACTURER.equalsIgnoreCase("ZUK")){//联想
            //sendToZUK(context,count);
        }else if(Build.MANUFACTURER.equalsIgnoreCase("samsung")){//三星
            sendToSamsung(context,count);
        }else if(Build.MANUFACTURER.equalsIgnoreCase("LGE")){//LG
            //sendToLG(context,count);
        }else if(Build.MANUFACTURER.equalsIgnoreCase("HTC")){//HTC
            //sendToHTC(context,count);
        }else if(Build.MANUFACTURER.equalsIgnoreCase("Sony Ericsson")){//Sony
            //sendToSony(context,count);
        }
    }

    /**
     * 小米
     * @param context
     * @param notification
     * @param count
     */
    private static void sendToXiaoMi(Context context, Notification notification, int count){
        try{
            Field filed = notification.getClass().getDeclaredField("extraNotification");
            Object extraNotification = filed.get(notification);
            Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount",int.class);
            method.invoke(extraNotification,count);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 联想ZUK
     * @param context
     * @param count
     */
    private static void sendToZUK(Context context, int count){
        final Uri contentUri = Uri.parse("content://"+"com.android.badge"+"/"+"badge");
        Bundle extra = new Bundle();
        extra.putInt("app_badge_count",count);
        try{
            context.getApplicationContext().getContentResolver().call(contentUri,"setAppBadgeCount",null,extra);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 三星
     * @param context
     * @param count
     */
    private static void sendToSamsung(Context context, int count){
        Intent localIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        localIntent.putExtra("badge_count", count);
        localIntent.putExtra("badge_count_package_name", context.getPackageName());
        localIntent.putExtra("badge_count_class_name", getLauncherClassName(context));
        context.getApplicationContext().sendBroadcast(localIntent);
    }

    /**
     * LG
     * @param context
     * @param count
     */
    private static void sendToLG(Context context, int count){
        Intent localIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        localIntent.putExtra("badge_count", count);
        localIntent.putExtra("badge_count_package_name", context.getPackageName());
        localIntent.putExtra("badge_count_class_name", getLauncherClassName(context));
        context.getApplicationContext().sendBroadcast(localIntent);
    }

    /**
     * HTC
     * @param context
     * @param count
     */
    private static void sendToHTC(Context context, int count){
        ComponentName localComponentName = new ComponentName(context.getPackageName(),getLauncherClassName(context));

        Intent notificationIntent = new Intent("com.htc.launcher.action.SET_NOTIFICATION");
        notificationIntent.putExtra("com.htc.launcher.extra.COMPONENT",localComponentName.flattenToShortString());
        notificationIntent.putExtra("com.htc.launcher.extra.COUNT",count);
        context.getApplicationContext().sendBroadcast(notificationIntent);

        Intent shortcutIntent = new Intent("com.htc.launcher.action.UPDATE_SHORTCUT");
        notificationIntent.putExtra("packagename",context.getPackageName());
        notificationIntent.putExtra("count",count);
        context.getApplicationContext().sendBroadcast(shortcutIntent);
    }

    /**
     * Sony
     * @param context
     * @param count
     */
    private static void sendToSony(Context context, int count){
        Intent intent = new Intent("com.sonyericsson.home.action.UPDATE_BADGE");
        intent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME",context.getPackageName());
        intent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME",getLauncherClassName(context));
        intent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE",count);
        intent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE",count>0);
        context.getApplicationContext().sendBroadcast(intent);
    }
}
