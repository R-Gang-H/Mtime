//package com.mtime.bussiness.ticket.movie;
//
//import android.app.AlarmManager;
//import android.app.PendingIntent;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.text.TextUtils;
//
//import com.mtime.R;
//import com.mtime.bussiness.main.MainActivity;
//import com.mtime.bussiness.ticket.movie.bean.RemindBean;
//import com.mtime.bussiness.ticket.movie.bean.ReminderMovieBean;
//import com.mtime.bussiness.ticket.movie.details.MovieDetailsActivity;
//import com.mtime.frame.App;
//import com.mtime.util.notification.NotificationUtils;
//
//import java.util.List;
//
///**
// * 与产品沟通，此功能不再需要，有时间了可以整理下删除(2021.10.21提示功能去掉)
// */
//@Deprecated
//public class AlarmReceiver extends BroadcastReceiver {
//    public static final String ALARM_REMINDER = "com.mtime.REMINDER_MOVIE";
//    public static final String BOOT = "android.intent.action.BOOT_COMPLETED";
//    public static final String REMINDER_MOVIE_SHOW = "com.mtime.REMINDER_MOVIE_SHOW";
//    public static final String REMINDER_MOVIE_COMMENT = "com.mtime.REMINDER_MOVIE_COMMENT";
//
//    private NotificationUtils mNotificationUtils;
//
//    @SuppressWarnings("deprecation")
//    @Override
//    public void onReceive(final Context context, final Intent intent) {
//        if (null == intent || TextUtils.isEmpty(intent.getAction())) {
//            return;
//        }
//
//        if(null == mNotificationUtils) {
//			mNotificationUtils = new NotificationUtils(context);
//		}
//
//		if (intent.getAction().equals(ALARM_REMINDER)) {
//			final String id = intent.getStringExtra("MovieID");
//			int notifyId = 0;
//			try {
//				notifyId = Integer.parseInt(id);
//			}
//			catch (Exception e) {
//			}
//
//			final Intent i = getStartIntent(context, MovieDetailsActivity.class);
//			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			i.putExtra("MovieTitle", intent.getStringExtra("MovieTitle"));
//			i.putExtra(App.getInstance().KEY_MOVIE_ID, intent.getStringExtra("MovieID"));
//			i.putExtra(App.getInstance().KEY_MOVIE_TYPE, "hot_movie");
//			final PendingIntent contentIntent = PendingIntent.getActivity(
//					context, notifyId, i, PendingIntent.FLAG_CANCEL_CURRENT);
//
//			mNotificationUtils.sendNotification(notifyId,"时光网新片上映提醒", "今日将上映电影" + intent.getStringExtra("MovieTitle"), contentIntent);
//
//			if (id != null) {
//				ReleaseReminder.getInstance().remove(id);
//			}
//		} else if (intent.getAction().equals(REMINDER_MOVIE_SHOW)) {
//			final String id = intent.getStringExtra("MovieID");
//			Intent i = getStartIntent(context, MainActivity.class);
//			final PendingIntent contentIntent = PendingIntent.getActivity(
//				context, R.string.app_name, i, PendingIntent.FLAG_UPDATE_CURRENT);
//
//			mNotificationUtils.sendNotification("时光网影片播放提醒", "今日上映电影" + intent.getStringExtra("MovieTitle"), contentIntent);
//
//			if (id != null) {
//				ReminderMovieShow.getInstance().remove(id);
//			}
//		} else if (intent.getAction().equals(REMINDER_MOVIE_COMMENT)) {
//			final String id = intent.getStringExtra("MovieID");
//			final Intent i = getStartIntent(context, MovieDetailsActivity.class);
//			i.putExtra("MovieTitle", intent.getStringExtra("MovieTitle"));
//			i.putExtra(App.getInstance().KEY_MOVIE_ID, intent.getStringExtra("MovieID"));
//			final PendingIntent contentIntent = PendingIntent.getActivity(
//				context, R.string.app_name, i, PendingIntent.FLAG_UPDATE_CURRENT);
//
//			mNotificationUtils.sendNotification("时光网影评提醒","电影" + intent.getStringExtra("MovieTitle") + "已经上映完毕，去给这部影片一个评价吧！", contentIntent);
//
//			if (id != null) {
//				ReminderMovieShow.getInstance().remove(id);
//			}
//		} else if (intent.getAction().equals(BOOT)) {
//			// 开机的时候要重新设置一下提醒
//			final List<RemindBean> list = ReleaseReminder.getInstance().getAll();
//			if ((list != null) && (list.size() > 0)) {
//				for (final RemindBean b : list) {
//					final AlarmManager am = (AlarmManager) context
//						.getSystemService(Context.ALARM_SERVICE);
//					if(null != am) {
//						final Intent i1 = new Intent(ALARM_REMINDER);
//						intent.putExtra("MovieTitle", b.getName());
//						intent.putExtra("MovieID", b.getId());
//						final PendingIntent pi = PendingIntent.getBroadcast(
//								context, 0, i1, PendingIntent.FLAG_UPDATE_CURRENT);
//						am.set(AlarmManager.RTC_WAKEUP, b.getDate(), pi);
//					}
//				}
//			}
//
//			final List<ReminderMovieBean> reminderList = ReminderMovieShow
//				.getInstance().getAll();
//			if ((reminderList != null) && (reminderList.size() > 0)) {
//				for (final ReminderMovieBean ticketBean : reminderList) {
//					final long twoHours = 1000 * 60 * 60 * 2;
//					final long halfHour = 1000 * 60 * 30;
//
//					final AlarmManager am = (AlarmManager) context
//						.getSystemService(Context.ALARM_SERVICE);
//
//					if(null != am) {
//						final Intent i2 = new Intent(REMINDER_MOVIE_SHOW);
//						i2.putExtra("MovieTitle", ticketBean.getMovieTitle());
//						i2.putExtra("MovieID", ticketBean.getMovieId());
//						final PendingIntent pi = PendingIntent.getBroadcast(
//								context, 0, i2, PendingIntent.FLAG_UPDATE_CURRENT);
//						am.set(AlarmManager.RTC_WAKEUP, (ticketBean.getShowtime() * 1000) - twoHours, pi);// 提前两个小时通知用户
//
//						final Intent intent2 = new Intent(REMINDER_MOVIE_COMMENT);
//						intent2.putExtra("MovieTitle", ticketBean.getMovieTitle());
//						intent2.putExtra("MovieID", ticketBean.getMovieId());
//						final PendingIntent pIntent = PendingIntent.getBroadcast(
//								context, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
//						am.set(AlarmManager.RTC_WAKEUP,
//								(ticketBean.getShowtime() * 1000) + halfHour
//										+ (ticketBean.getMovieLength() * 1000 * 60),
//								pIntent);// 电影结束半小时之后通知用户进行评论
//					}
//				}
//			}
//		}
//    }
//
//    public Intent getStartIntent(final Context context, final Class<?> clazz) {
//        final Intent intent = new Intent();
//        if (clazz != null) {
//            intent.setClass(context, clazz);
//            return intent;
//        }
//        return null;
//    }
//}
