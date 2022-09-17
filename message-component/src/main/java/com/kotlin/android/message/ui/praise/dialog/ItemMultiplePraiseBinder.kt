package com.kotlin.android.message.ui.praise.dialog

import android.content.ContextWrapper
import com.kotlin.android.message.R
import com.kotlin.android.message.databinding.MessageItemMultiplePraiseBinding
import com.kotlin.android.message.tools.MessageCenterJumper
import com.kotlin.android.message.ui.fans.binder.ItemFansBinder
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.dialog.showDialog

/**
 * Created by zhaoninglongfei on 2022/4/26
 *
 */
class ItemMultiplePraiseBinder(
    val bean: MultiplePraiseViewBean,
    private val followUser: (Long, Long, MultiplePraiseViewModel.FollowSuccessCallback) -> Unit
) :
    MultiTypeBinder<MessageItemMultiplePraiseBinding>() {

    override fun layoutId(): Int = R.layout.message_item_multiple_praise

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is ItemFansBinder
    }

    fun jumpToFansUserHome() {
        MessageCenterJumper.jumpToUserHome(binding?.root?.context, bean.userId)
    }

    fun focusOnThisUser() {
        bean.userId?.let { userId ->
            if (bean.hasFollowed) {
                showDialog(
                    context = (binding?.root?.context as ContextWrapper).baseContext,
                    content = getString(R.string.message_confirm_unfollow_user),
                ) {
                    followUser(2L, userId, object : MultiplePraiseViewModel.FollowSuccessCallback {
                        override fun followSuccess() {
                            bean.changeFollowedStatus()
                            notifyAdapterSelfChanged()
                        }
                    })
                }
            } else {
                followUser(1L, userId, object : MultiplePraiseViewModel.FollowSuccessCallback {
                    override fun followSuccess() {
                        bean.changeFollowedStatus()
                        notifyAdapterSelfChanged()
                    }
                })
            }
        }
    }
}