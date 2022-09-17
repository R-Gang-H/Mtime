package com.kotlin.chat_component

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import com.kotlin.android.ktx.ext.core.put
import com.kotlin.chat_component.inner.constants.EaseConstant
import com.kotlin.chat_component.inner.modules.chat.EaseChatFragment
import com.kotlin.chat_component.inner.modules.chat.interfaces.IChatExtendMenu

/**
 * Created by zhaoninglongfei on 2022/4/15.
 * 封装私聊页面需要的一些参数
 */
class ChatFragment : EaseChatFragment() {

    companion object {
        fun builder(context: AppCompatActivity) = Builder(context)

        fun bundle(conversationId: String?): Bundle {
            val bundle = Bundle()
            bundle.putString(EaseConstant.EXTRA_CONVERSATION_ID, conversationId)
            bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE)
            return bundle
        }
    }

    override fun initData(){
        super.initData()
        resetChatExtendMenu()
        chatLayout?.chatMessageListLayout?.refreshToLatest()

    }

    private fun resetChatExtendMenu() {
        var chatExtendMenu: IChatExtendMenu = chatLayout?.chatInputMenu?.chatExtendMenu!!
        chatExtendMenu.clear()
        chatExtendMenu.registerMenuItem(R.string.attach_picture, R.drawable.ease_chat_image_selector, R.id.extend_item_picture)
        chatExtendMenu.registerMenuItem(R.string.attach_take_pic, R.drawable.ease_chat_takepic_selector, R.id.extend_item_take_picture)
        chatExtendMenu.registerMenuItem(R.string.attach_video, R.drawable.em_chat_video_selector, R.id.extend_item_video)
    }



    class Builder(private val context: AppCompatActivity) {
        private var conversationId: String? = null //会话id
        private var resourceId: Int? = null //需要替换的容器id
        private var chatType: Int = EaseConstant.CHATTYPE_SINGLE

        private var otherMtimeId: Long? = null
        private var otherNickName: String? = null
        private var otherHead: String? = null
        private var otherAuthType: Long? = null
        private var otherAuthRole: String? = null

        fun conversationId(id: String?): Builder {
            this.conversationId = id
            return this
        }

        fun otherMtimeId(id : Long?) : Builder{
            this.otherMtimeId = id
            return this
        }

        fun otherNickName(name : String?) : Builder{
            this.otherNickName = name
            return this
        }

        fun otherHead(head : String?) : Builder{
            this.otherHead = head
            return this
        }

        fun otherAuthType(type : Long?) : Builder{
            this.otherAuthType = type
            return this
        }

        fun otherAuthRole(role : String?) : Builder{
            this.otherAuthRole = role
            return this
        }


        fun replace(@IdRes id: Int?): Builder {
            this.resourceId = id
            return this
        }

        fun commit() {
            checkParams()
            val chatFragment = ChatFragment()
            val bundle = Bundle()
            bundle.putString(EaseConstant.EXTRA_CONVERSATION_ID, this.conversationId)
            bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, chatType)

            bundle.put(EaseConstant.KEY_CHAT_OTHER_MTIME_ID, otherMtimeId)
            bundle.put(EaseConstant.KEY_CHAT_OTHER_NICK_NAME, otherNickName)
            bundle.put(EaseConstant.KEY_CHAT_OTHER_HEAD, otherHead)
            bundle.put(EaseConstant.KEY_CHAT_OTHER_AUTH_TYPE, otherAuthType)
            bundle.put(EaseConstant.KEY_CHAT_OTHER_AUTH_ROLE, otherAuthRole)
            chatFragment.arguments = bundle
            context.supportFragmentManager
                .beginTransaction()
                .replace(resourceId!!, chatFragment, "chat")
                .commit()
        }

        fun build(): ChatFragment {
            return ChatFragment()
        }

        private fun checkParams() {
            if (this.conversationId.isNullOrEmpty()) return
            if (this.resourceId == null) return
        }
    }
}