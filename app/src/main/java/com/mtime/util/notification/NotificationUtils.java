package com.mtime.util.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.mtime.R;

public class NotificationUtils extends ContextWrapper {
    
    private NotificationManager manager;
    public static final String id = "channel_1";
    public static final String name = "channel_name_1";
    
    public NotificationUtils(Context context){
        super(context);
    }
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotificationChannel(){
        NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
        getManager().createNotificationChannel(channel);
    }
    
    private NotificationManager getManager(){
        if (manager == null){
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return manager;
    }
    
    public NotificationCompat.Builder getChannelNotificationBuilder(String title, String content, boolean autoCancel){
        return new NotificationCompat.Builder(getApplicationContext(), id)
                .setTicker(title)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(autoCancel);
    }
    
    public NotificationCompat.Builder getNotificationBuilder_25(String title, String content, boolean autoCancel){
        return new NotificationCompat.Builder(getApplicationContext())
                .setTicker(title)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(autoCancel);
    }
    
    public void sendNotification(int notifyId, String title, String content, PendingIntent contentIntent, boolean autoCancel){
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= 26){
            createNotificationChannel();
            builder = getChannelNotificationBuilder(title, content, autoCancel);
        }else{
            builder = getNotificationBuilder_25(title, content, autoCancel);
        }
        getManager().notify(notifyId, builder.setContentIntent(contentIntent).build());
    }
    
    public void sendNotification(int notifyId, String title, String content, PendingIntent contentIntent) {
        sendNotification(notifyId, title, content, contentIntent, true);
    }
    
    public void sendNotification(String title, String content, PendingIntent contentIntent) {
        sendNotification(1, title, content, contentIntent, true);
    }
}
