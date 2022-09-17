package com.mtime.util;

import com.mtime.common.cache.CacheManager;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author vivian.wei
 * @date 2019/4/17
 * @desc 保存邀请话题页_阅读过的邀请话题id
 */
public class SaveIids {

    private static SaveIids saveIIds = null;

    private static final String KEY_SAVEINVITE_ID = "save_i_ids";

    private SaveIids() {

    }

    /**
     * 保存id
     *
     *
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void add(final String nid) {
        if (!contains(nid)) {

            Set reminderSet = (HashSet<String>) CacheManager.getInstance().getFileCacheNoClean(
                    SaveIids.KEY_SAVEINVITE_ID);
            if (reminderSet == null) {
                reminderSet = new HashSet<String>();
            }
            reminderSet.add(nid);
            if (reminderSet.size() > 200) {
                Iterator it = reminderSet.iterator();
                String firstid = (String) it.next();
                reminderSet.remove(firstid);
            }
            CacheManager.getInstance().putFileCacheNoClean(SaveIids.KEY_SAVEINVITE_ID, reminderSet, 1209600000);

        }
    }

    /**
     * 查询nid是否已存
     *
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public boolean contains(final String id) {
        if ((id == null) || "".equals(id)) {
            return false;
        }

        final Set idSet = (HashSet<String>) CacheManager.getInstance().getFileCacheNoClean(
                SaveIids.KEY_SAVEINVITE_ID);
        if ((idSet != null) && (idSet.size() > 0)) {
            final Iterator it = idSet.iterator();
            while (it.hasNext()) {
                final String alreadyid = (String) it.next();
                if (id.equals(alreadyid)) {

                    return true;
                }
            }
        }

        return false;
    }

    public static synchronized SaveIids getInstance() {
        if (SaveIids.saveIIds == null) {
            SaveIids.saveIIds = new SaveIids();
        }
        return SaveIids.saveIIds;
    }

}
