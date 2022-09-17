package com.kotlin.android.message.ui.privateChat.binder

import android.app.Activity
import com.kotlin.android.app.router.provider.message_center.IMessageCenterProvider
import com.kotlin.android.message.R
import com.kotlin.android.message.databinding.MessageItemPrivateChatBinding
import com.kotlin.android.message.ui.privateChat.viewBean.PrivateChatViewBean
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * Created by zhaoninglongfei on 2022/3/17
 *
 */
class ItemPrivateChatBinder(val bean: PrivateChatViewBean) :
    MultiTypeBinder<MessageItemPrivateChatBinding>() {

    override fun layoutId(): Int = R.layout.message_item_private_chat

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is ItemPrivateChatBinder
    }

    fun jumpToChatFragment(conversationId: String?) {
        getProvider(IMessageCenterProvider::class.java)?.startChatActivity(
            binding?.root?.context as Activity,
            bean.authHeader?.userId,
            false,
            bean.name,
            conversationId,
            otherMtimeId = bean.authHeader?.userId,
            otherNickName = bean.name,
            otherHeadPic = bean.authHeader?.headImg,
            otherAuthType = bean.authHeader?.authType,
            otherAuthRole = bean.authHeader?.authRole
        )
    }
}