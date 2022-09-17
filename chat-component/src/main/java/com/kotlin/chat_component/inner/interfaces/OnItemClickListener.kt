package com.kotlin.chat_component.inner.interfaces

import android.view.View

/**
 * 条目点击事件
 */
interface OnItemClickListener {
    /**
     * 条目点击
     * @param view
     * @param position
     */
    fun onItemClick(view: View?, position: Int)
}