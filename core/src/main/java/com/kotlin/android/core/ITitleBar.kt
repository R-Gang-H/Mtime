package com.kotlin.android.core

/**
 *
 * Created on 2021/12/29.
 *
 * @author o.s
 */
interface ITitleBar {

    /**
     * 创建 Fragment 的视图
     */
//    fun createViewOfFragment(root: View?): View

    /**
     * 同步状态栏样式
     */
    fun syncStatusBar()

    /**
     * 应用皮肤
     */
    fun applySkin(skin: String?)
}