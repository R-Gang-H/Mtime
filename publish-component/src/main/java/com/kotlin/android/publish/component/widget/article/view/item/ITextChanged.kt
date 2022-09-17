package com.kotlin.android.publish.component.widget.article.view.item

/**
 *
 * Created on 2022/5/2.
 *
 * @author o.s
 */
interface ITextChanged {

    /**
     * 文本改变的监听
     */
    var notifyTextChanged: (() -> Unit)?

}