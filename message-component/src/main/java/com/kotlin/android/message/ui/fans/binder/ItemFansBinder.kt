package com.kotlin.android.message.ui.fans.binder

import com.kotlin.android.message.R
import com.kotlin.android.message.databinding.MessageItemFansBinding
import com.kotlin.android.message.tools.MessageCenterJumper
import com.kotlin.android.message.ui.fans.FansViewModel
import com.kotlin.android.message.ui.fans.viewBean.FansViewBean
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.dialog.showDialog

/**
 * Created by zhaoninglongfei on 2022/3/17
 *
 */
class ItemFansBinder(
    val bean: FansViewBean,
    private val followUser: (Long, Long, FansViewModel.FollowSuccessCallback) -> Unit
) :
    MultiTypeBinder<MessageItemFansBinding>() {

    override fun layoutId(): Int = R.layout.message_item_fans

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
                    context = binding?.root?.context,
                    content = getString(R.string.message_confirm_unfollow_user),
                ) {
                    followUser(2L, userId, object : FansViewModel.FollowSuccessCallback {
                        override fun followSuccess() {
                            bean.changeFollowedStatus()
                            notifyAdapterSelfChanged()
                        }
                    })
                }
            } else {
                followUser(1L, userId, object : FansViewModel.FollowSuccessCallback {
                    override fun followSuccess() {
                        bean.changeFollowedStatus()
                        notifyAdapterSelfChanged()
                    }
                })
            }
        }
    }
}