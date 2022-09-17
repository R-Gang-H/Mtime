//package com.mtime.bussiness.ticket.movie;
//
//import com.mtime.bussiness.ticket.movie.bean.ReminderMovieBean;
//import com.mtime.common.cache.CacheManager;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Set;
//
///**
// * 与产品沟通，此功能不再需要，有时间了可以整理下删除(2021.10.21提示功能去掉)
// */
//@Deprecated
//public class ReminderMovieShow {
//    private static ReminderMovieShow reminderMovieShow;
//    private static final String KEY_REMINDER_DATA = "reminder_movie_show";
//
//    public static ReminderMovieShow getInstance() {
//	if (reminderMovieShow == null) {
//	    reminderMovieShow = new ReminderMovieShow();
//	}
//	return reminderMovieShow;
//    }
//
//    @SuppressWarnings({ "unchecked" })
//    public void add(final ReminderMovieBean remindMovieBean) {
//	Set<ReminderMovieBean> reminderSet = (HashSet<ReminderMovieBean>) CacheManager
//		.getInstance()
//		.getFileCache(KEY_REMINDER_DATA);
//	if (reminderSet == null) {
//	    reminderSet = new HashSet<ReminderMovieBean>();
//	}
//	reminderSet.add(remindMovieBean);
//	CacheManager.getInstance().putFileCache(
//		KEY_REMINDER_DATA, reminderSet);
//    }
//
//    @SuppressWarnings({ "unchecked", "rawtypes" })
//    public void remove(final String id) {
//	if ((id == null) || "".equals(id)) {
//	    return;
//	}
//	final Set<ReminderMovieBean> reminderSet = (HashSet<ReminderMovieBean>) CacheManager
//		.getInstance()
//		.getFileCache(KEY_REMINDER_DATA);
//	if ((reminderSet != null) && (reminderSet.size() > 0)) {
//	    final Iterator it = reminderSet.iterator();
//	    while (it.hasNext()) {
//		final ReminderMovieBean remindMovieBean = (ReminderMovieBean) it
//			.next();
//		if (id.equals(remindMovieBean.getOrderId())) {
//		    reminderSet.remove(remindMovieBean);
//		    CacheManager.getInstance().putFileCache(
//			    KEY_REMINDER_DATA, reminderSet);
//		    return;
//		}
//	    }
//
//	}
//    }
//
//    @SuppressWarnings({ "unchecked", "rawtypes" })
//    public boolean contains(final String id) {
//	if ((id == null) || "".equals(id)) {
//	    return false;
//	}
//
//	final Set<ReminderMovieBean> reminderSet = (HashSet<ReminderMovieBean>) CacheManager
//		.getInstance()
//		.getFileCache(KEY_REMINDER_DATA);
//	if ((reminderSet != null) && (reminderSet.size() > 0)) {
//	    final Iterator it = reminderSet.iterator();
//	    while (it.hasNext()) {
//		final ReminderMovieBean remindMovieBean = (ReminderMovieBean) it
//			.next();
//		if (id.equals(remindMovieBean.getOrderId())) {
//		    return true;
//		}
//	    }
//	}
//
//	return false;
//    }
//
//    @SuppressWarnings({ "unchecked", "rawtypes" })
//    public List<ReminderMovieBean> getAll() {
//	final Set<ReminderMovieBean> reminderSet = (HashSet<ReminderMovieBean>) CacheManager
//		.getInstance()
//		.getFileCache(KEY_REMINDER_DATA);
//	if ((reminderSet != null) && (reminderSet.size() > 0)) {
//	    final List remindBeans = new ArrayList<ReminderMovieBean>();
//	    final Iterator it = reminderSet.iterator();
//	    while (it.hasNext()) {
//		remindBeans.add(it.next());
//	    }
//
//	    return remindBeans;
//	} else {
//	    return null;
//	}
//    }
//
//}
