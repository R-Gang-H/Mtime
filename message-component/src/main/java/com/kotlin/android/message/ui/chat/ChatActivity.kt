package com.kotlin.android.message.ui.chat

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.core.ext.showDialogFragment
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.message.MessageConstant
import com.kotlin.android.message.R
import com.kotlin.android.message.databinding.MessageActivityChatBinding
import com.kotlin.android.message.tools.MessageCenterJumper
import com.kotlin.android.message.ui.chat.dialog.ChatBottomDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.widget.titlebar.TitleBar
import com.kotlin.android.widget.titlebar.TitleBarManager
import com.kotlin.android.widget.titlebar.moreH
import com.kotlin.chat_component.ChatFragment

/**
 * Created by zhaoninglongfei on 2022/4/15
 * 私聊页面
 */
@Route(path = RouterActivityPath.MessageCenter.PAGE_CHAT)
class ChatActivity : BaseVMActivity<ChatViewModel, MessageActivityChatBinding>() {

    private var conversationId: String? = null
    private var title: String? = null
    private var mtimeId: Long? = null
    private var isOfficial: Boolean? = false

    private var otherMtimeId: Long? = null
    private var otherNickName: String? = null
    private var otherHead: String? = null
    private var otherAuthType: Long? = null
    private var otherAuthRole: String? = null

    private lateinit var titleBar: TitleBar

    override fun onCreate(savedInstanceState: Bundle?) {
        window.statusBarColor = getColor(R.color.color_ffffff)
        super.onCreate(savedInstanceState)
    }

    override fun getIntentData(intent: Intent?) {
        conversationId = intent?.getStringExtra(MessageConstant.KEY_CHAT_CONVERSATION_ID)
        title = intent?.getStringExtra(MessageConstant.KEY_CHAT_TITLE)
        mtimeId = intent?.getLongExtra(MessageConstant.KEY_CHAT_MTIME_ID, 0L)
        isOfficial = intent?.getBooleanExtra(MessageConstant.KEY_CHAT_IS_OFFICIAL, false)
        otherMtimeId = intent?.getLongExtra(MessageConstant.KEY_CHAT_OTHER_MTIME_ID, 0L)
        otherNickName = intent?.getStringExtra(MessageConstant.KEY_CHAT_OTHER_NICK_NAME)
        otherHead = intent?.getStringExtra(MessageConstant.KEY_CHAT_OTHER_HEAD)
        otherAuthType = intent?.getLongExtra(MessageConstant.KEY_CHAT_OTHER_AUTH_TYPE, 0L)
        otherAuthRole = intent?.getStringExtra(MessageConstant.KEY_CHAT_OTHER_AUTH_ROLE)
    }

    override fun initTheme() {
        super.initTheme()
        immersive()
            .statusBarDarkFont(true)
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        titleBar = TitleBarManager.with(this)
            .setTitle(
                title = title,
                isBold = true,
                gravity = Gravity.CENTER,
                drawablePadding = 5.dp,
            ).addItem(
                isReversed = false,
                drawableRes = R.drawable.ic_title_bar_36_back,
                reverseDrawableRes = R.drawable.ic_title_bar_36_back_reversed,
                click = {
                    this.finish()
                }
            )
    }

    override fun initView() {
        ChatFragment.builder(this)
            .conversationId(conversationId)
            .otherMtimeId(otherMtimeId)
            .otherNickName(otherNickName)
            .otherHead(otherHead)
            .otherAuthType(otherAuthType)
            .otherAuthRole(otherAuthRole)
            .replace(mBinding?.viewFragment?.id).commit()
    }

    override fun initData() {
        if (isOfficial == true) {
            addOfficialTitleItem()
        } else {
            mViewModel?.loadBlockStatus(userId = mtimeId)
        }
    }

    override fun startObserve() {
        mViewModel?.blockStatusUiState?.observe(this) { state ->
            state?.apply {
                if (showLoading) {
                    showProgressDialog()
                }

                success?.let { result ->
                    dismissProgressDialog()
                    titleBar.moreH {
                        showDialogFragment(
                            ChatBottomDialog::class.java, {
                                ChatBottomDialog.newInstance(
                                    isOfficial = result.bizData?.isOfficial ?: true,
                                    isInBlackList = mViewModel?.getBlockStatus().isInBlackList()
                                )
                            }
                        )?.apply {
                            onVisitHomePage = {
                                MessageCenterJumper.jumpToUserHome(this@ChatActivity, mtimeId)
                            }
                            onAddToBlacklist = {
                                mtimeId?.let { id ->
                                    mViewModel?.addOrRemoveBlacklist(
                                        id,
                                        if (mViewModel?.getBlockStatus().isInBlackList()) 2 else 1
                                    )
                                }
                            }
                        }
                    }
                }

                error?.let { error ->
                    dismissProgressDialog()
                    showToast(error)
                    addOfficialTitleItem()
                }

                netError?.let { error ->
                    dismissProgressDialog()
                    showToast(error)
                    addOfficialTitleItem()
                }
            }
        }

        //拉黑/取消拉黑
        mViewModel?.addOrRemoveFromBlacklistUiState?.observe(this) {
            it?.apply {
                if (showLoading) {
                    showProgressDialog()
                }

                success?.let {
                    dismissProgressDialog()
                    showToast(R.string.message_success)
//                    mViewModel?.loadBlockStatus(userId = mtimeId)
                }

                error?.let { error ->
                    dismissProgressDialog()
                    showToast(error)
                }

                netError?.let { error ->
                    dismissProgressDialog()
                    showToast(error)
                }
            }
        }
    }

    private fun addOfficialTitleItem() {
        titleBar.moreH {
            //不展示拉黑
            showDialogFragment(
                ChatBottomDialog::class.java, {
                    ChatBottomDialog.newInstance(isOfficial = true, isInBlackList = false)
                }
            )?.apply {
                onVisitHomePage = {
                    MessageCenterJumper.jumpToUserHome(this@ChatActivity, mtimeId)
                }
            }
        }
    }

    private fun Long?.isInBlackList(): Boolean {
        return when (this) {
            1L -> true
            2L -> false
            else -> false
        }
    }
}