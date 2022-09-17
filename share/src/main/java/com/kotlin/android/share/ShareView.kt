package com.kotlin.android.share

import com.kotlin.android.share.ui.ShareFragment

/**
 * 该方案弃用，请使用 [ShareFragment]
 *
 * Created on 2020/6/22.
 *
 * @author o.s
 */
class ShareView {

    private var action: ((event: EventType) -> Unit)? = null

    fun setData() {

    }

    fun show(action: ((event: EventType) -> Unit)? = null) {
        this.action = action
    }

    /**
     * 事件类型枚举
     */
    enum class EventType {
        EVENT_TYPE_CLOSE,
        EVENT_TYPE_WE_CHAT_FRIEND,
        EVENT_TYPE_WE_CHAT_TIMELINE,
        EVENT_TYPE_QQ,
        EVENT_TYPE_WEI_BO
    }

}