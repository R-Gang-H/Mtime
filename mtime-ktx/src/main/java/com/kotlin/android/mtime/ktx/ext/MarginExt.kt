package com.kotlin.android.mtime.ktx.ext

import android.view.View
import android.view.ViewGroup
import com.kotlin.android.core.statusbar.StatusBarUtils

/**
 * create by lushan on 2020/10/14
 * description:
 */

/**
 * 设置状态栏高度的上边距
 */
fun View?.topStatusMargin(){
    this?.topMargin(StatusBarUtils.getStatusBarHeight(this.context))
}

/**
 * 设置上边距
 */
fun View?.topMargin(top:Int){
    val marginLayoutParams = this?.layoutParams as? ViewGroup.MarginLayoutParams
    marginLayoutParams?.topMargin = top
    this?.layoutParams = marginLayoutParams
}