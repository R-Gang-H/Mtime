package com.mtime.bussiness.mine.history;

import android.text.TextUtils;

import com.mtime.bussiness.mine.history.dao.HistoryDao;

import org.litepal.LitePal;

/**
 * Created by vivian.wei on 2018/8/20.
 * 对外调用的保存阅读历史的
 */

public class ReadHistoryUtil {
    public static final int CONTENT_TYPE_ARTICLES = 1;  // 图文
    public static final int CONTENT_TYPE_VIDEO = 2;     // 视频
    public static final int CONTENT_TYPE_RANK = 4;      // 榜单

    public static final String DATE_GROUP_TODAY = "today";      // 今日
    public static final String DATE_GROUP_WEEK = "week";        // 一周内
    public static final String DATE_GROUP_OTHER = "earlier";    // 更早

    // 保存一条阅读历史到本地数据库
    public static void saveReadHistory2Local(final int contentType, final long relatedId,
                                             final String title, final String publicName,
                                             final String img, final boolean showVideoIcon,
                                             final int videoId, final int videoSourceType) {
        HistoryDao.save(contentType, relatedId, title, publicName, img, showVideoIcon, videoId, videoSourceType);
    }
    
    public static boolean hasReadHistory(String relatedId) {
        if(!TextUtils.isEmpty(relatedId)) {
            try {
                return hasReadHistory(Long.parseLong(relatedId));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    
    public static boolean hasReadHistory(long relatedId) {
        return LitePal.isExist(HistoryDao.class, "relatedId=?", String.valueOf(relatedId));
    }
}
