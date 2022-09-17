package com.mtime.util;

import com.mtime.base.utils.MTimeUtils;
import com.mtime.beans.BoughtTicketListBean;
import com.mtime.common.cache.CacheManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

//存储本地浏览过的cinema列表
public class SaveCinemaIds {
    //    private static SaveCinemaIds saveCinemaIds = null;
    private static volatile SaveCinemaIds saveCinemaIds = null;

    private static final String KEY_SAVECINEMA_ID = "savecinema_data";

    private SaveCinemaIds() {

    }

    /**
     * 保存电影id
     *
     * @param movieId
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void add(final String cinemaId) {
        if (!contains(cinemaId)) {

            Set reminderSet = (HashSet<BoughtTicketListBean>) CacheManager
                    .getInstance()
                    .getFileCache(SaveCinemaIds.KEY_SAVECINEMA_ID);
            if (reminderSet == null) {
                reminderSet = new HashSet<BoughtTicketListBean>();
            }
            final BoughtTicketListBean bean = new BoughtTicketListBean();
            bean.setId(Integer.parseInt(cinemaId));
            bean.setBuyCount(0);
            bean.setLastBuyTime(MTimeUtils.getLastDiffServerTime());
            reminderSet.add(bean);
            CacheManager.getInstance().putFileCache(
                    SaveCinemaIds.KEY_SAVECINEMA_ID, reminderSet);

        }
    }

    /**
     * 删除
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void remove(final String id) {
        if ((id == null) || "".equals(id)) {
            return;
        }

        final Set cinemaIdSet = (HashSet<BoughtTicketListBean>) CacheManager
                .getInstance().getFileCache(SaveCinemaIds.KEY_SAVECINEMA_ID);
        if ((cinemaIdSet != null) && (cinemaIdSet.size() > 0)) {
            final Iterator it = cinemaIdSet.iterator();
            while (it.hasNext()) {
                final BoughtTicketListBean bBean = (BoughtTicketListBean) it
                        .next();
                if (id.equals(bBean.getId())) {
                    cinemaIdSet.remove(bBean);
                    CacheManager.getInstance().putFileCache(
                            SaveCinemaIds.KEY_SAVECINEMA_ID, cinemaIdSet);
                    return;
                }
            }

        }
    }

    /**
     * 查询cinemaid是否已存
     *
     * @param movieId
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean contains(final String id) {
        if ((id == null) || "".equals(id)) {
            return false;
        }

        final Set cinemaIdSet = (HashSet<BoughtTicketListBean>) CacheManager
                .getInstance().getFileCache(SaveCinemaIds.KEY_SAVECINEMA_ID);
        if ((cinemaIdSet != null) && (cinemaIdSet.size() > 0)) {
            final Iterator it = cinemaIdSet.iterator();
            while (it.hasNext()) {
                final BoughtTicketListBean bean = (BoughtTicketListBean) it
                        .next();
                if (id.equals(String.valueOf(bean.getId()))) {
                    cinemaIdSet.remove(bean);
                    bean.buyCount++;
                    if (MTimeUtils.getLastDiffServerDate() == null) {
                        bean.setLastBuyTime(0l);
                    } else {
                        bean.setLastBuyTime(MTimeUtils.getLastDiffServerTime());
                    }
                    cinemaIdSet.add(bean);
                    CacheManager.getInstance().putFileCache(
                            SaveCinemaIds.KEY_SAVECINEMA_ID, cinemaIdSet);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 获取所有cinemaId
     *
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<BoughtTicketListBean> getAll() {
        final Set reminderSet = (HashSet<BoughtTicketListBean>) CacheManager
                .getInstance().getFileCache(SaveCinemaIds.KEY_SAVECINEMA_ID);
        if ((reminderSet != null) && (reminderSet.size() > 0)) {
            final List<BoughtTicketListBean> remindBeans = new ArrayList<BoughtTicketListBean>();
            final Iterator it = reminderSet.iterator();
            while (it.hasNext()) {
                remindBeans.add((BoughtTicketListBean) it.next());
            }
            MtimeUtils.compareOffenToSee(remindBeans);// 重新进行排序
            final List<BoughtTicketListBean> shifList = new ArrayList<BoughtTicketListBean>();
            for (int i = 0; i < remindBeans.size(); i++) {

                if (remindBeans.get(i).getBuyCount() >= 2) {
                    shifList.add(remindBeans.get(i));

                }

            }

            return shifList;
        } else {
            return null;
        }
    }

    /*public static synchronized SaveCinemaIds getInstance() {
    if (SaveCinemaIds.saveCinemaIds == null) {
	    SaveCinemaIds.saveCinemaIds = new SaveCinemaIds();
	}
	return SaveCinemaIds.saveCinemaIds;
    }*/
    //采用双重检查加锁，提升性能
    public static SaveCinemaIds getInstance() {
        if (null == SaveCinemaIds.saveCinemaIds) {
            synchronized (SaveCinemaIds.class) {
                if (null == SaveCinemaIds.saveCinemaIds) {
                    SaveCinemaIds.saveCinemaIds = new SaveCinemaIds();
                }
            }
        }
        return SaveCinemaIds.saveCinemaIds;
    }

}
