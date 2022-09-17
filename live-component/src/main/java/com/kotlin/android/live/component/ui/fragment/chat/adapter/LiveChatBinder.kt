package com.kotlin.android.live.component.ui.fragment.chat.adapter

import android.text.TextUtils
import com.kotlin.android.live.component.R
import com.kotlin.android.live.component.databinding.ItemLiveChatBinding
import com.kotlin.android.live.component.viewbean.LiveChatMessageBean
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2021/3/15
 */
class LiveChatBinder(val itemData: LiveChatMessageBean) : MultiTypeBinder<ItemLiveChatBinding>() {

    override fun layoutId(): Int = R.layout.item_live_chat

//    override fun areItemsTheSame(other: MultiTypeBinder<*>): Boolean {
//        return other is LiveChatBinder && itemData.tt == other.itemData.tt
//    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is LiveChatBinder && TextUtils.equals(other.itemData.messageId, itemData.messageId)
    }

}