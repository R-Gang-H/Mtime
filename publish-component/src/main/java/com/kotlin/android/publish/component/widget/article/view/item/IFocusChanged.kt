package com.kotlin.android.publish.component.widget.article.view.item

import android.view.View

/**
 *
 * Created on 2022/5/2.
 *
 * @author o.s
 */
interface IFocusChanged {

    /**
     * 焦点改变的监听
     */
    var focusChanged: ((View, Boolean) -> Unit)?

    /**
     * 获取焦点的视图
     */
    var hasFocused: ((View, Boolean) -> Unit)?
}