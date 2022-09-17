package com.kotlin.android.publish.component.widget.article.view.event

/**
 * 键盘事件（删除/回车/完成关闭键盘）
 *
 * Created on 2022/4/1.
 *
 * @author o.s
 */
enum class PEvent {

    /**
     * 上一个
     */
    PRE,

    /**
     * 下一个
     */
    NEXT,

    /**
     * 完成：关闭键盘
     */
    DONE
}