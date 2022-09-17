//package com.mtime.bussiness.ticket.movie;
//
//import com.mtime.bussiness.ticket.movie.bean.RemindBean;
//import com.mtime.common.cache.CacheManager;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Set;
//
///**
// * 上映提醒工具
// */
///**
// * 与产品沟通，此功能不再需要，有时间了可以整理下删除(2021.10.21提示功能去掉)
// */
//@Deprecated
//public class ReleaseReminder {
//    private static ReleaseReminder mReminder;
//    private static final String KEY_REMINDER_DATA = "reminder_data";
//
//    public static ReleaseReminder getInstance() {
//        if (mReminder == null) {
//            mReminder = new ReleaseReminder();
//        }
//        return mReminder;
//    }
//
//    /**
//     * 添加电影id到上映提醒
//     *
//     * @param id
//     */
//    @SuppressWarnings({ "unchecked", "rawtypes" })
//    public void add(final RemindBean remindBean) {
//        Set reminderSet = (HashSet<RemindBean>) CacheManager.getInstance().getFileCache(
//                KEY_REMINDER_DATA);
//        if (reminderSet == null) {
//            reminderSet = new HashSet<RemindBean>();
//        }
//
//        reminderSet.add(remindBean);
//        CacheManager.getInstance().putFileCache(KEY_REMINDER_DATA, reminderSet);
//    }
//
//    /**
//     * 删除上映提醒列表里的movieId
//     *
//     * @param id
//     */
//    @SuppressWarnings({ "unchecked", "rawtypes" })
//    public void remove(final String id) {
//        if ((id == null) || "".equals(id)) {
//            return;
//        }
//
//        final Set reminderSet = (HashSet<RemindBean>) CacheManager.getInstance().getFileCache(
//                KEY_REMINDER_DATA);
//        if ((reminderSet != null) && (reminderSet.size() > 0)) {
//            final Iterator it = reminderSet.iterator();
//            while (it.hasNext()) {
//                final RemindBean remindBean = (RemindBean) it.next();
//                if (id.equals(remindBean.getId())) {
//                    reminderSet.remove(remindBean);
//                    CacheManager.getInstance().putFileCache(KEY_REMINDER_DATA, reminderSet);
//                    return;
//                }
//            }
//
//        }
//    }
//
//    /**
//     * 查询该电影id是否已添加到上映提醒
//     *
//     * @param id
//     * @return
//     */
//    @SuppressWarnings({ "unchecked", "rawtypes" })
//    public boolean contains(final String id) {
//        if ((id == null) || "".equals(id)) {
//            return false;
//        }
//
//        final Set reminderSet = (HashSet<RemindBean>) CacheManager.getInstance().getFileCache(
//                KEY_REMINDER_DATA);
//        if ((reminderSet != null) && (reminderSet.size() > 0)) {
//            final Iterator it = reminderSet.iterator();
//            while (it.hasNext()) {
//                final RemindBean remindBean = (RemindBean) it.next();
//                if (id.equals(remindBean.getId())) {
//                    return true;
//                }
//            }
//        }
//
//        return false;
//    }
//
//    /**
//     * 获取上映提醒列表中的所有movieId
//     *
//     * @return
//     */
//    @SuppressWarnings({ "unchecked", "rawtypes" })
//    public List<RemindBean> getAll() {
//        final Set reminderSet = (HashSet<RemindBean>) CacheManager.getInstance().getFileCache(
//                KEY_REMINDER_DATA);
//        if ((reminderSet != null) && (reminderSet.size() > 0)) {
//            final List remindBeans = new ArrayList<RemindBean>();
//            final Iterator it = reminderSet.iterator();
//            while (it.hasNext()) {
//                remindBeans.add(it.next());
//            }
//
//            return remindBeans;
//        } else {
//            return null;
//        }
//    }
//}
