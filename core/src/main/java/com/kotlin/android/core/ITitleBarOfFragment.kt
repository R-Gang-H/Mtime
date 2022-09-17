package com.kotlin.android.core

import android.view.View

/**
 *
 * Created on 2021/12/29.
 *
 * @author o.s
 */
interface ITitleBarOfFragment {

    /**
     * 创建 Fragment 的视图
     */
    fun createViewOfFragment(root: View?): View

}