package com.mtime.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationManagerCompat;

import java.util.List;

/**
 * Created by yinguanping on 2017/7/5.
 */

public class SystemUtil {

    /**
     * 返回app运行状态
     * 1:程序在前台运行
     * 2:程序在后台运行
     * 3:程序未启动
     * 注意：需要配置权限<uses-permission android:name="android.permission.GET_TASKS" />
     */
    public static int getAppSatus(Context context, String pageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (null == am) {
            return 3;
        }
        // 5.0之后的版本
        if (Build.VERSION.SDK_INT > 20) {
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            if (null != tasks && tasks.size() > 0) {
                if (tasks.get(0).processName.equals(pageName)) {
                    return 1;
                } else {
                    for (ActivityManager.RunningAppProcessInfo info : tasks) {
                        if (info.processName.equals(pageName)) {
                            return 2;
                        }
                    }
                    return 3;
                }
            }
            return 3;
        } else {
            List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(20);
            //判断程序是否在栈顶
            if (null != list && list.size() > 0) {
                if (list.get(0).topActivity.getPackageName().equals(pageName)) {
                    return 1;
                } else {
                    //判断程序是否在栈里
                    for (ActivityManager.RunningTaskInfo info : list) {
                        if (info.topActivity.getPackageName().equals(pageName)) {
                            return 2;
                        }
                    }
                    return 3;
                }
            }
            return 3;//栈里找不到，返回3
        }
    }

    public static boolean isAppAlive(Context context, String proessName) {

        boolean isAlive = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> lists = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : lists) {
            if (info.processName.equals(proessName)) {
                isAlive = true;
                return isAlive;
            }
        }
        return isAlive;
    }

    /**
     * 通知权限是否可用
     *
     * @param context
     * @return
     */
    public static boolean checkNotificationPermission(Context context) {
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }

}
