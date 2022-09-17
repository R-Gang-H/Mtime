/**
 * 
 */
package com.mtime.util;

import com.mtime.beans.SaveSeenRecommendBean;
import com.mtime.common.cache.CacheManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author wangjin
 * 
 */
public class SaveSeenRecommendId {

    private static SaveSeenRecommendId mRecommendId;
    private static final String KEY_REMINDER_DATA = "save_recommendId_data";

    public static SaveSeenRecommendId getInstance() {
	if (SaveSeenRecommendId.mRecommendId == null) {
	    SaveSeenRecommendId.mRecommendId = new SaveSeenRecommendId();
	}
	return SaveSeenRecommendId.mRecommendId;
    }

    /**
     * 添加id
     * 
     * 
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void add(final SaveSeenRecommendBean seenBean) {
	Set reminderSet = (HashSet<SaveSeenRecommendBean>) CacheManager
		.getInstance().getFileCache(
			SaveSeenRecommendId.KEY_REMINDER_DATA);
	if (reminderSet == null) {
	    reminderSet = new HashSet<SaveSeenRecommendBean>();
	}

	reminderSet.add(seenBean);
	CacheManager.getInstance().putFileCache(
		SaveSeenRecommendId.KEY_REMINDER_DATA, reminderSet);
    }

    /**
     * 删除
     * 
     * @param movieId
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void remove(final String id) {
	if ((id == null) || "".equals(id)) {
	    return;
	}

	final Set reminderSet = (HashSet<SaveSeenRecommendBean>) CacheManager
		.getInstance().getFileCache(
			SaveSeenRecommendId.KEY_REMINDER_DATA);
	if ((reminderSet != null) && (reminderSet.size() > 0)) {
	    final Iterator it = reminderSet.iterator();
	    while (it.hasNext()) {
		final SaveSeenRecommendBean seenBean = (SaveSeenRecommendBean) it
			.next();
		if (id.equals(seenBean.getId())) {
		    reminderSet.remove(seenBean);
		    CacheManager.getInstance().putFileCache(
			    SaveSeenRecommendId.KEY_REMINDER_DATA, reminderSet);
		    return;
		}
	    }

	}
    }

    /**
     * 查询
     * 
     * @param movieId
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public boolean contains(final String id) {
	if ((id == null) || "".equals(id)) {
	    return false;
	}

	final Set reminderSet = (HashSet<SaveSeenRecommendBean>) CacheManager
		.getInstance().getFileCache(
			SaveSeenRecommendId.KEY_REMINDER_DATA);
	if ((reminderSet != null) && (reminderSet.size() > 0)) {
	    final Iterator it = reminderSet.iterator();
	    while (it.hasNext()) {
		final SaveSeenRecommendBean seenBean = (SaveSeenRecommendBean) it
			.next();
		if (id.equals(seenBean.getId())) {
		    return true;
		}
	    }
	}

	return false;
    }

    /**
     * 获取
     * 
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<SaveSeenRecommendBean> getAll() {
	final Set reminderSet = (HashSet<SaveSeenRecommendBean>) CacheManager
		.getInstance().getFileCache(
			SaveSeenRecommendId.KEY_REMINDER_DATA);
	if ((reminderSet != null) && (reminderSet.size() > 0)) {
	    final List seenBean = new ArrayList<SaveSeenRecommendBean>();
	    final Iterator it = reminderSet.iterator();
	    while (it.hasNext()) {
		seenBean.add(it.next());
	    }

	    return seenBean;
	} else {
	    return null;
	}
    }

}
