package com.kotlin.chat_component.inner.interfaces

import android.view.View

/**
 * 条目长按点击事件
 */
interface OnItemLongClickListener {
    /**
     * 条目点击
     * @param view
     * @param position
     */
    fun onItemLongClick(view: View?, position: Int): Boolean
}