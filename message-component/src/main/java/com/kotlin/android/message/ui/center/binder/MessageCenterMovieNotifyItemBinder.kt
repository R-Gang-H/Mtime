package com.kotlin.android.message.ui.center.binder

import android.app.Activity
import android.view.View
import com.kotlin.android.app.data.entity.message.UnreadCountResult
import com.kotlin.android.app.router.provider.message_center.IMessageCenterProvider
import com.kotlin.android.message.R
import com.kotlin.android.message.databinding.MessageItemMessageCenterMovieNotifyBinding
import com.kotlin.android.message.ui.center.viewBean.MessageCenterMovieNotifyViewBean
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * Created by zhaoninglongfei on 2022/3/7
 *
 */
class MessageCenterMovieNotifyItemBinder(var bean: MessageCenterMovieNotifyViewBean? = null) :
    MultiTypeBinder<MessageItemMessageCenterMovieNotifyBinding>() {

    override fun layoutId(): Int = R.layout.message_item_message_center_movie_notify

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean =
        other is MessageCenterMovieNotifyItemBinder

    fun jumpToMovieNotify(){
        if (bean?.hasMore == true){
            getProvider(IMessageCenterProvider::class.java)?.startMovieNotifyActivity(binding?.root?.context as Activity)
        }
    }

    fun updateMovieNotify(it: UnreadCountResult) {
        val build = MessageCenterMovieNotifyViewBean.build(it)
        this.bean = build
        notifyAdapterSelfChanged()
    }
}