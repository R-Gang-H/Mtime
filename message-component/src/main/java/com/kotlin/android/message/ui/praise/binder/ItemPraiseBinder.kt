package com.kotlin.android.message.ui.praise.binder

import com.kotlin.android.core.ext.showDialogFragment
import com.kotlin.android.message.R
import com.kotlin.android.message.databinding.MessageItemPraiseBinding
import com.kotlin.android.message.tools.MessageCenterJumper
import com.kotlin.android.message.ui.praise.dialog.MultiplePraiseDialog
import com.kotlin.android.message.ui.praise.viewBean.PraiseViewBean
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * Created by zhaoninglongfei on 2022/3/22
 * 点赞列表 binder
 */
class ItemPraiseBinder(val bean: PraiseViewBean) : MultiTypeBinder<MessageItemPraiseBinding>() {
    override fun layoutId(): Int = R.layout.message_item_praise

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is ItemPraiseBinder
    }

    fun clickUser() {
        if (bean.isMultiplePraise) {
            showMultiplePraiseDialog()
        } else {
            jumpToUserHome()
        }
    }

    private fun jumpToUserHome() {
        MessageCenterJumper.jumpToUserHome(binding?.root?.context, bean.authHeader?.userId)
    }

    private fun showMultiplePraiseDialog() {
        bean.messageId?.let { messageId ->
            binding?.root?.showDialogFragment(
                MultiplePraiseDialog::class.java, {
                    MultiplePraiseDialog.newInstance(
                        messageId,
                        "${bean.priseCount}人${bean.praiseContentType}"
                    )
                }
            )
        }
    }
}