package com.kotlin.chat_component

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import com.kotlin.chat_component.inner.constants.EaseConstant
import com.kotlin.chat_component.inner.modules.chat.EaseChatFragment

/**
 * Created by zhaoninglongfei on 2021/11/24.
 * 封装聊天室需要的一些参数
 */
class ChatroomFragment : EaseChatFragment() {

    companion object {
        fun builder(context: AppCompatActivity) = Builder(context)

        fun bundle(conversationId: String?): Bundle {
            val bundle = Bundle()
            bundle.putString(EaseConstant.EXTRA_CONVERSATION_ID, conversationId)
            bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_CHATROOM)
            return bundle
        }
    }

    class Builder(private val context: AppCompatActivity) {
        private var conversationId: String? = null //聊天室ID
        private var resourceId: Int? = null //需要替换的容器id
        private var chatType: Int = EaseConstant.CHATTYPE_CHATROOM //聊天室type

        fun conversationId(id: String?): Builder {
            this.conversationId = id
            return this
        }

        fun replace(@IdRes id: Int?): Builder {
            this.resourceId = id
            return this
        }

        fun commit() {
            checkParams()
            val chatroomFragment = ChatroomFragment()
            val bundle = Bundle()
            bundle.putString(EaseConstant.EXTRA_CONVERSATION_ID, this.conversationId)
            bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, chatType)
            chatroomFragment.arguments = bundle
            context.supportFragmentManager
                .beginTransaction()
                .replace(resourceId!!, chatroomFragment, "chatRoom")
                .commit()
        }

        fun build(): ChatroomFragment {
            return ChatroomFragment()
        }

        private fun checkParams() {
            if (this.conversationId.isNullOrEmpty()) return
            if (this.resourceId == null) return
        }
    }
}