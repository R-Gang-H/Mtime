package com.kotlin.tablet.provider

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.data.entity.filmlist.FilmListCreateResult
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.tablet.ITabletProvider
import com.kotlin.android.ktx.ext.core.put
import com.kotlin.android.router.RouterManager
import com.kotlin.tablet.*

@Route(path = RouterProviderPath.Provider.PROVIDER_TABLET)
class TabletProvider : ITabletProvider {
    /**
     * 跳转片单/榜单主页面
     */
    override fun startTabletMainActivity() {
        RouterManager.instance.navigation(RouterActivityPath.TABLET.TABLET_MAIN)
    }

    /**
     * 跳转片单/榜单详情页面
     * source 【选填】默认值为0； 0:缓存5分钟，1:缓存1秒-我创建的片单列表用，
     * filmListId //片单id
     */
    override fun startFilmListDetailsActivity(source: Long?, filmListId: Long?) {
        Bundle().apply {
            putLong(KEY_TO_DETAILS_SOURCE, source ?: 0L)
            putLong(KEY_FILM_LIST_ID, filmListId ?: 0L)
        }.also {
            RouterManager.instance.navigation(RouterActivityPath.TABLET.FILM_LIST_DETAILS, it)
        }
    }

    /**
     * 跳转片单创建、编辑页
     * @param isEdit true编辑片单  false创建片单
     * @param filmListId 片单Id  编辑片单使用，创建片单不用传
     */
    override fun startFilmListCreateActivity(isEdit: Boolean, filmListId: Long?) {
        Bundle().apply {
            putBoolean(KEY_IS_EDIT, isEdit)
            putLong(KEY_FILM_LIST_ID, filmListId ?: 0L)
        }.also {
            RouterManager.instance.navigation(RouterActivityPath.TABLET.FILM_LIST_CREATE, it)
        }
    }

    /**
     * 跳转片单创建成功页
     * @param simple 片单信息
     */
    override fun startFilmListCreateSuccessActivity(simple: FilmListCreateResult.SimpleFilmListInfo?) {
        Bundle().apply {
            putParcelable(KEY_FILM_LIST_INFO, simple)
        }.also {
            RouterManager.instance.navigation(
                RouterActivityPath.TABLET.FILM_LIST_CREATE_SUCCESS,
                it
            )
        }
    }

    /**
     * 跳转添加电影页面
     * @param filmListId 片单Id
     */
    override fun startFilmSearchActivity(filmListId: Long?) {
        Bundle().apply {
            putLong(KEY_FILM_LIST_ID, filmListId ?: 0L)
        }.also {
            RouterManager.instance.navigation(
                RouterActivityPath.TABLET.FILM_LIST_ADD_FILM,
                it
            )
        }
    }

    /**
     * 跳转投稿页面
     */
    override fun startContributeActivity() {
        RouterManager.instance.navigation(
            RouterActivityPath.TABLET.FILM_LIST_CONTRIBUTE
        )
    }

    /**
     * 跳转投稿历史主题页面
     */
    override fun startContributeHistoryActivity() {
        RouterManager.instance.navigation(
            RouterActivityPath.TABLET.FILM_LIST_CONTRIBUTE_HISTORY
        )
    }

    /**
     * 跳转我创建的片单页面
     */
    override fun startMyCreateActivity(activityId: Long?) {
        Bundle().apply {
            put(KEY_CONTRIBUTE_ACTIVITY_ID, activityId)
        }.also {
            RouterManager.instance.navigation(
                RouterActivityPath.TABLET.FILM_LIST_CONTRIBUTE_MY_CREATE, it
            )
        }
    }

    override fun startFilmSelectedActivity(filmListId: Long?, from: Int) {
        Bundle().apply {
            put(KEY_FILM_LIST_ID, filmListId)
            put(KEY_TO_SELECTED_FROM, from)
        }.also {
            RouterManager.instance.navigation(
                RouterActivityPath.TABLET.FILM_LIST_SELECTED, it
            )
        }
    }

    /**
     * 跳转个人中心-我的片单
     * @param tab 0我创建的片单  1 我收藏的片单
     */
    override fun startFilmListMineActivity(tab: Int?) {
        Bundle().apply {
            put(KEY_FILM_LIST_MINE_TAB, tab)
        }.also {
            RouterManager.instance.navigation(
                RouterActivityPath.TABLET.FILM_LIST_OF_MINE, it
            )
        }
    }

    /**
     * 跳转片单列表搜索页面
     */
    override fun startFilmListSearchActivity(label: String?, isSelected: Long?) {
        Bundle().apply {
            put("key_label", label)
            put("key_isSelected", isSelected)
        }.also {
            RouterManager.instance.navigation(
                RouterActivityPath.TABLET.FILM_LIST_SEARCH, it
            )
        }
    }


}