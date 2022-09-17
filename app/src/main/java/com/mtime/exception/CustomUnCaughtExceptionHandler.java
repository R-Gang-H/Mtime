package com.mtime.exception;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.mtime.bussiness.main.MainTabActivity;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by JiaJunHui on 2018/5/30.
 */
public class CustomUnCaughtExceptionHandler {

    public static void openState(Context context, boolean open){
        if(open){
            Cockroach.install((thread, throwable)-> {
                try{
                    if(Looper.getMainLooper().equals(Looper.myLooper())){
                        Toast.makeText(context, "出错了！", Toast.LENGTH_SHORT).show();

                        restart(context);
                    }else{
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "出错了！", Toast.LENGTH_SHORT).show();
                                restart(context);
                            }
                        });
                    }
                    CrashReport.postCatchedException(throwable);
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
        }else{
            Cockroach.uninstall();
        }
    }

    private static void restart(Context context) {
        Intent intent = new Intent(context, MainTabActivity.class);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //重启应用，得使用PendingIntent
        PendingIntent restartIntent = PendingIntent.getActivity(
                context.getApplicationContext(), 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis(),restartIntent); // 重启应用
    }

}
