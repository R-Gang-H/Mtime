package com.mtime.bussiness.main.widget.tab

import android.view.View
import androidx.fragment.app.Fragment
import com.kotlin.android.core.entity.PageFlag

/**
 *
 * Created on 2022/1/6.
 *
 * @author o.s
 */
interface ITabBarItem {

    val view: View

    val fragment: Fragment?

    val tag: String

    /**
     * 选择/取消选择
     */
    fun select(isSelected: Boolean = true)

    /**
     * 应用页面标示（跳转）
     */
    fun applyPageFlag(flag: PageFlag)

    /**
     * 应用皮肤
     */
//    fun applySkin(skin: SkinTabBarItem?)

    /**
     * 显示红点
     */
    fun showHotView(isShow: Boolean = true, title: CharSequence = "")
}