package com.kotlin.android.message.ui.center.binder

import android.app.Activity
import com.hyphenate.chat.EMConversation
import com.kotlin.android.app.router.provider.message_center.IMessageCenterProvider
import com.kotlin.android.message.R
import com.kotlin.android.message.databinding.MessageItemChatListBinding
import com.kotlin.android.message.ui.center.viewBean.ChatViewBean
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.dialog.showDialog
import com.kotlin.chat_component.HuanxinConversationManager
import com.kotlin.chat_component.manager.ChatUserCacheManager

/**
 * Created by zhaoninglongfei on 2022/3/16
 *
 */
class ItemChatListBinder(val bean: ChatViewBean,val chatDelete : ()->Unit) : MultiTypeBinder<MessageItemChatListBinding>() {

    override fun layoutId(): Int = R.layout.message_item_chat_list

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is ItemChatListBinder
    }

    fun jumpToChatFragment(conversationId: String?) {
        getProvider(IMessageCenterProvider::class.java)?.startChatActivity(
            binding?.root?.context as Activity,
            ChatUserCacheManager.getMtimeUserSimple(bean.imId)?.mtimeId,
            ChatUserCacheManager.getMtimeUserSimple(bean.imId)?.isOfficial,
            bean.name,
            conversationId
        )
    }

    fun deleteConversation(conversationId: String?): Boolean {
        showDialog(
            context = binding?.root?.context,
            content = getString(R.string.message_confirm_delete_conversation),
        ) {
            HuanxinConversationManager.deleteConversation(
                conversationId,
                EMConversation.EMConversationType.Chat,object :
                    HuanxinConversationManager.DeleteConversationListener {
                    override fun onSuccess() {
                        chatDelete.invoke()
                    }

                    override fun onError() {
                    }
                }
            )
        }
        return true
    }
}