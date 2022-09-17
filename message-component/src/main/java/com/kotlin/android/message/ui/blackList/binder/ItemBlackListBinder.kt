package com.kotlin.android.message.ui.blackList.binder

import com.kotlin.android.message.R
import com.kotlin.android.message.databinding.MessageItemBlackListBinding
import com.kotlin.android.message.tools.MessageCenterJumper
import com.kotlin.android.message.ui.blackList.viewBean.BlackListViewBean
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * Created by zhaoninglongfei on 2022/3/23
 *
 */
class ItemBlackListBinder(
    val bean: BlackListViewBean,
    private val removeFromBlackList: (Long, Int) -> Unit
) :
    MultiTypeBinder<MessageItemBlackListBinding>() {

    override fun layoutId(): Int = R.layout.message_item_black_list

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is ItemBlackListBinder
    }

    fun jumpToFansUserHome() {
        MessageCenterJumper.jumpToUserHome(binding?.root?.context, bean.userId)
    }

    fun removeFromBlackList() {
        bean.userId?.let { userId ->
            removeFromBlackList(userId, 2)
        }
    }
}