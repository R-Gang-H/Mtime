package com.kotlin.android.message.ui.center.binder

import android.app.Activity
import com.kotlin.android.app.router.provider.message_center.IMessageCenterProvider
import com.kotlin.android.message.R
import com.kotlin.android.message.databinding.MessageItemMessageCenterChatListBinding
import com.kotlin.android.message.ui.center.viewBean.MessageCenterChatListViewBean
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter

/**
 * Created by zhaoninglongfei on 2022/3/7
 *
 */
class MessageCenterChatListItemBinder(var bean: MessageCenterChatListViewBean? = null) :
    MultiTypeBinder<MessageItemMessageCenterChatListBinding>() {

    private lateinit var mAdapter: MultiTypeAdapter

    override fun layoutId(): Int = R.layout.message_item_message_center_chat_list

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean =
        other is MessageCenterChatListItemBinder

    override fun onBindViewHolder(binding: MessageItemMessageCenterChatListBinding, position: Int) {
        super.onBindViewHolder(binding, position)

        binding.apply {
            mAdapter = createMultiTypeAdapter(rvChatList)
            bean?.build()?.let { mAdapter.notifyAdapterAdded(it) }

            binding.ivChatList.setOnClickListener {
                getProvider(IMessageCenterProvider::class.java)?.startPrivateChatActivity(binding.root.context as Activity)
            }
        }
    }

    fun updateChatList(it: MessageCenterChatListViewBean?) {
        this.bean = it
        notifyAdapterSelfChanged()
    }
}