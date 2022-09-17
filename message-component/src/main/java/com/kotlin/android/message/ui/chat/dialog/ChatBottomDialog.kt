package com.kotlin.android.message.ui.chat.dialog

import android.view.Gravity
import android.view.WindowManager
import com.kotlin.android.core.BaseVMDialogFragment
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.core.annotation.DialogFragmentTag
import com.kotlin.android.ktx.ext.core.Direction
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.message.R
import com.kotlin.android.message.databinding.MessageDialogChatBinding

/**
 * Created by zhaoninglongfei on 2022/4/19
 * 私聊页的底部弹框
 */
@DialogFragmentTag(tag = "tag_dialog_chat_bottom_dialog")
class ChatBottomDialog(private val isOfficial: Boolean, private val isInBlackList: Boolean) :
    BaseVMDialogFragment<BaseViewModel, MessageDialogChatBinding>() {

    companion object {
        fun newInstance(isOfficial: Boolean, isInBlackList: Boolean) =
            ChatBottomDialog(isOfficial, isInBlackList)
    }

    var onVisitHomePage: (() -> Unit)? = null
    var onAddToBlacklist: (() -> Unit)? = null

    override fun initEnv() {
        window = {
            decorView.setPadding(0, 0, 0, 0)
            attributes.run {
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
                gravity = Gravity.BOTTOM
                windowAnimations = R.style.BottomDialogAnimation
            }
            setBackgroundDrawable(null)
        }
    }

    override fun initView() {
        mBinding?.isOfficial = isOfficial
        mBinding?.isInBlackList = isInBlackList

        mBinding?.apply {
            llDialogChat.setBackground(
                colorRes = R.color.color_f3f3f4,
                cornerRadius = 12.dpF,
                direction = Direction.LEFT_TOP or Direction.RIGHT_TOP
            )

            tvVisitHomePage.setBackground(
                colorRes = R.color.color_ffffff,
                cornerRadius = 12.dpF,
                direction = Direction.LEFT_TOP or Direction.RIGHT_TOP
            )

            tvVisitHomePage.setOnClickListener {
                onVisitHomePage?.invoke()
                dismiss()
            }
            tvAddToBlacklist.setOnClickListener {
                onAddToBlacklist?.invoke()
                dismiss()
            }
            cancelView.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun initData() {}

    override fun startObserve() {}
}