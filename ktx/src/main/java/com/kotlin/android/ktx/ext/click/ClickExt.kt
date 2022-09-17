package com.kotlin.android.ktx.ext.click

import android.view.View
import com.kotlin.android.ktx.R

/**
 * 创建者: zl
 * 创建时间: 2020/8/5 9:05 AM
 * 描述:带延迟过滤点击事件的 View 扩展
 */

/***
 * 带延迟过滤点击事件的 View 扩展
 * @param delay Long 延迟时间，默认500毫秒
 * @param method: (T) -> Unit 函数
 * @return Unit
 */
fun <T : View> T.onClick(delay: Long = 500, method: (T) -> Unit) {
    setOnClickListener {
        if (clickEnable(delay)) {
            method(this)
        }
    }
}

/**
 * get set
 * 给view添加一个上次触发时间的属性（用来屏蔽连击操作）
 */
private var <T : View>T.triggerLastTime: Long
    get() = getTag(R.id.triggerLastTimeKey) as? Long ?: 0L
    set(value) {
        setTag(R.id.triggerLastTimeKey, value)
    }

/**
 * 判断时间是否满足再次点击的要求（控制点击）
 */
private fun <T : View> T.clickEnable(delay: Long = 500): Boolean {
    var clickable = false
    val currentClickTime = System.currentTimeMillis()
    if (currentClickTime - triggerLastTime >= delay) {
        clickable = true
        triggerLastTime = currentClickTime
    }
    return clickable
}