package com.kotlin.android.app.router.provider.tablet

import android.os.Bundle
import com.kotlin.android.app.data.entity.filmlist.FilmListCreateResult
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.router.annotation.RouteProvider
import com.kotlin.android.router.provider.IBaseProvider

/**
 * 片单页面provider
 */
@RouteProvider(path = RouterProviderPath.Provider.PROVIDER_TABLET)
interface ITabletProvider : IBaseProvider {
    /**
     * 跳转片单列表页面
     */
    fun startTabletMainActivity()

    /**
     * 跳转片单详情页面
     * source 【选填】默认值为0； 0:缓存5分钟，1:缓存1秒-我创建的片单列表用，
     * filmListId //片单id
     */
    fun startFilmListDetailsActivity(source: Long? = 0, filmListId: Long?)

    /**
     * 跳转片单创建、编辑页
     * @param isEdit true编辑片单  false创建片单
     * @param filmListId 片单Id  编辑片单使用，创建片单不用传
     */
    fun startFilmListCreateActivity(isEdit: Boolean, filmListId: Long? = 0L)

    /**
     * 跳转片单成功页面
     * @param simple 片单信息
     */
    fun startFilmListCreateSuccessActivity(simple: FilmListCreateResult.SimpleFilmListInfo?)

    /**
     * 跳转添加电影页面
     * @param filmListId 片单Id
     * @param isEdit 是否是编辑状态
     */
    fun startFilmSearchActivity(filmListId: Long?)

    /**
     * 跳转投稿页面
     */
    fun startContributeActivity()

    /**
     * 跳转投稿历史主题页面
     */
    fun startContributeHistoryActivity()

    /**
     * 跳转我创建的片单页面
     * @param 活动ID
     */
    fun startMyCreateActivity(activityId: Long?)

    /**
     * 跳转添加电影-已选
     * @param filmListId 片单Id
     * @param from 1片单编辑页 0电影搜索页
     * @param isEdit 是否是编辑状态
     */
    fun startFilmSelectedActivity(filmListId: Long?, from: Int)

    /**
     * 跳转个人中心-我的片单
     * @param tab 0我创建的片单  1 我收藏的片单
     */
    fun startFilmListMineActivity(tab: Int? = 0)

    /**
     * 跳转片单列表搜索页面
     */
    fun startFilmListSearchActivity(label: String?, isSelected: Long?)
}