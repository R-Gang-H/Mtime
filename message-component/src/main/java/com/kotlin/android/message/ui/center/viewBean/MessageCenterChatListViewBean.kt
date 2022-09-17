package com.kotlin.android.message.ui.center.viewBean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.message.ui.center.binder.ItemChatListBinder
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder


/**
 * Created by zhaoninglongfei on 2022/3/7
 *
 */
data class MessageCenterChatListViewBean(
    val chatList: List<ChatViewBean>?,
    val chatDelete : ()->Unit
) : ProguardRule {
    fun hasChatList(): Boolean {
        return !chatList.isNullOrEmpty()
    }

    fun build(): List<MultiTypeBinder<*>> {
        return this.chatList?.map {
            ItemChatListBinder(it,chatDelete)
        }?.toList() ?: listOf()
    }
}