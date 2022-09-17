package com.mtime.bussiness.main.widget.tab

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import com.kotlin.android.core.entity.PageFlag

/**
 *
 * Created on 2022/1/6.
 *
 * @author o.s
 */
interface ITabBar {

    /**
     * 初始化
     */
    fun init(@IdRes container: Int, fragmentManager: FragmentManager)

    /**
     * 当前选择位置从 0 开始
     */
    var currentPosition: Int

//    /**
//     * 选择位置从 0 开始
//     */
//    fun selectPosition(position: Int)

    /**
     * Item [ITabBarItem] 列表
     */
    val items: List<ITabBarItem>

    /**
     * 添加 [ITabBarItem]
     */
    fun addItem(item: ITabBarItem): ITabBar

    /**
     * 获取对应 [position] 的 Item
     */
    fun getItem(position: Int): ITabBarItem?

    /**
     * 显示红点
     */
    fun showHotView(position: Int, isShow: Boolean = true, title: CharSequence = "")

    /**
     * 应用皮肤
     */
//    fun applySkin(skin: String?)

    /**
     * 应用页面标示（跳转）
     */
    fun applyPageFlag(flag: PageFlag?)
}