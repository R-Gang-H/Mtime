package com.mtime.util;

import com.mtime.bussiness.ticket.cinema.bean.CinemaOffenGoBean;
import com.mtime.common.cache.CacheManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

// 存储用户手动选择的cinema
public class SaveOffenGo {
	private static SaveOffenGo saveCinemaIds = null;

	private static final String KEY_SAVECINEMA_ID = "offen_go_cinema";

	private SaveOffenGo() {

	}

	/**
	 * 保存电影院id
	 * 
	 * 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void add(final CinemaOffenGoBean offengoBean) {
		if (!contains(String.valueOf(offengoBean.getId()))) {

			Set reminderSet = (HashSet<CinemaOffenGoBean>) CacheManager
					.getInstance().getFileCacheNoClean(
							SaveOffenGo.KEY_SAVECINEMA_ID);
			if (reminderSet == null) {
				reminderSet = new HashSet<CinemaOffenGoBean>();
			}
			reminderSet.add(offengoBean);
			CacheManager.getInstance().putFileCacheNoClean(
					SaveOffenGo.KEY_SAVECINEMA_ID, reminderSet, 315360000000L);

		}
	}

	/**
	 * 删除
	 * 
	 * @param id
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void remove(final String id) {
		if ((id == null) || "".equals(id)) {
			return;
		}
		final Set cinemaIdSet = (HashSet<CinemaOffenGoBean>) CacheManager
				.getInstance().getFileCacheNoClean(
						SaveOffenGo.KEY_SAVECINEMA_ID);
		if ((cinemaIdSet != null) && (cinemaIdSet.size() > 0)) {
			final Iterator it = cinemaIdSet.iterator();
			while (it.hasNext()) {
				final CinemaOffenGoBean bBean = (CinemaOffenGoBean) it.next();
				if (id.equals(String.valueOf(bBean.getId()))) {
					cinemaIdSet.remove(bBean);
					CacheManager.getInstance().putFileCacheNoClean(
							SaveOffenGo.KEY_SAVECINEMA_ID, cinemaIdSet,
                            315360000000L);
					return;
				}
			}
		}
	}

	/**
	 * 查询cinemaid是否已存
	 * 
	 * @param id
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean contains(final String id) {
		if ((id == null) || "".equals(id)) {
			return false;
		}

		final Set cinemaIdSet = (HashSet<CinemaOffenGoBean>) CacheManager
				.getInstance().getFileCacheNoClean(
						SaveOffenGo.KEY_SAVECINEMA_ID);
		if ((cinemaIdSet != null) && (cinemaIdSet.size() > 0)) {
			final Iterator it = cinemaIdSet.iterator();
			while (it.hasNext()) {
				final CinemaOffenGoBean bean = (CinemaOffenGoBean) it.next();
				if (id.equals(String.valueOf(bean.getId()))) {

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
				.getInstance().getFileCacheNoClean(
						SaveOffenGo.KEY_SAVECINEMA_ID);
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

	/*
	 * 获取本地常去影院数量
	 */
	public int getOffenCinemasCount() {
		int cinemasCount = 0;
		List<CinemaOffenGoBean> offenCinemas = this.getAll();

		if (offenCinemas != null) {
			cinemasCount = offenCinemas.size();
		}
		return cinemasCount;
	}

	public static synchronized SaveOffenGo getInstance() {
		if (SaveOffenGo.saveCinemaIds == null) {
			SaveOffenGo.saveCinemaIds = new SaveOffenGo();
		}
		return SaveOffenGo.saveCinemaIds;
	}

}
