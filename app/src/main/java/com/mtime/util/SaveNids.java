package com.mtime.util;

import com.mtime.bussiness.ticket.cinema.bean.CinemaOffenGoBean;
import com.mtime.common.cache.CacheManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

//存储用户手动选择的nids
public class SaveNids {
    private static SaveNids saveNIds = null;

    private static final String KEY_SAVECINEMA_ID = "save_nids";

    private SaveNids() {

    }

    /**
     * 保存电影院id
     * 
     * 
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void add(final String nid) {
        if (!contains(nid)) {
            
            Set reminderSet = (HashSet<String>) CacheManager.getInstance().getFileCacheNoClean(
                    SaveNids.KEY_SAVECINEMA_ID);
            if (reminderSet == null) {
                reminderSet = new HashSet<String>();
            }
            reminderSet.add(nid);
            if (reminderSet.size() > 200) {
                Iterator it = reminderSet.iterator();
                String firstid = (String) it.next();
                reminderSet.remove(firstid);
            }
            CacheManager.getInstance().putFileCacheNoClean(SaveNids.KEY_SAVECINEMA_ID, reminderSet, 1209600000);
            
        }
    }

    /**
     * 删除
     * 
     * @param Id
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void remove(final String id) {
        if ((id == null) || "".equals(id)) {
            return;
        }
        final Set cinemaIdSet = (HashSet<String>) CacheManager.getInstance().getFileCacheNoClean(
                SaveNids.KEY_SAVECINEMA_ID);
        if ((cinemaIdSet != null) && (cinemaIdSet.size() > 0)) {
            final Iterator it = cinemaIdSet.iterator();
            while (it.hasNext()) {
                final String alreadyid = (String) it.next();
                if (id.equals(alreadyid)) {
                    cinemaIdSet.remove(alreadyid);
                    CacheManager.getInstance().putFileCacheNoClean(SaveNids.KEY_SAVECINEMA_ID, cinemaIdSet, 1209600000);
                    return;
                }
            }
        }
    }

    /**
     * 查询nid是否已存
     * 
     * @param Id
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public boolean contains(final String id) {
        if ((id == null) || "".equals(id)) {
            return false;
        }
        
        final Set cinemaIdSet = (HashSet<String>) CacheManager.getInstance().getFileCacheNoClean(
                SaveNids.KEY_SAVECINEMA_ID);
        if ((cinemaIdSet != null) && (cinemaIdSet.size() > 0)) {
            final Iterator it = cinemaIdSet.iterator();
            while (it.hasNext()) {
                final String alreadyid = (String) it.next();
                if (id.equals(alreadyid)) {
                    
                    return true;
                }
            }
        }
        
        return false;
    }

    /**
     * 获取所有cinema
     * 
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<CinemaOffenGoBean> getAll() {
	final Set reminderSet = (HashSet<CinemaOffenGoBean>) CacheManager
		.getInstance().getFileCacheNoClean(SaveNids.KEY_SAVECINEMA_ID);
	if ((reminderSet != null) && (reminderSet.size() > 0)) {
	    final List<CinemaOffenGoBean> offenGoBean = new ArrayList<CinemaOffenGoBean>();
	    final Iterator it = reminderSet.iterator();
	    while (it.hasNext()) {
		offenGoBean.add((CinemaOffenGoBean) it.next());
	    }
	    return offenGoBean;
	} else {
	    return null;
	}
    }

    public static synchronized SaveNids getInstance() {
	if (SaveNids.saveNIds == null) {
	    SaveNids.saveNIds = new SaveNids();
	}
	return SaveNids.saveNIds;
    }

}
