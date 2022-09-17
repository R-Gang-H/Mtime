package com.kotlin.android.message.ui.center.binder

import android.app.Activity
import com.kotlin.android.app.data.entity.message.UnreadCountResult
import com.kotlin.android.app.router.provider.message_center.IMessageCenterProvider
import com.kotlin.android.message.R
import com.kotlin.android.message.databinding.MessageItemMessageCenterHeaderBinding
import com.kotlin.android.message.ui.center.viewBean.MessageCenterHeaderViewBean
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * Created by zhaoninglongfei on 2022/3/7
 *
 */
class MessageCenterHeaderItemBinder(var bean: MessageCenterHeaderViewBean? = null) :
    MultiTypeBinder<MessageItemMessageCenterHeaderBinding>() {

    override fun layoutId(): Int = R.layout.message_item_message_center_header

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean =
        other is MessageCenterHeaderItemBinder

    fun jumpToCommentActivity() {
        getProvider(IMessageCenterProvider::class.java)?.startCommentActivity(binding?.root?.context as Activity)
    }

    fun jumpToPraiseActivity() {
        getProvider(IMessageCenterProvider::class.java)?.startPraiseActivity(binding?.root?.context as Activity)
    }

    fun updateNotifyCount(it: UnreadCountResult) {
        val build = MessageCenterHeaderViewBean.build(it)
        this.bean = build
        notifyAdapterSelfChanged()
    }
}