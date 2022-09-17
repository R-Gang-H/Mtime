package com.kotlin.android.message.ui.center

import android.view.Gravity
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.data.entity.message.UnreadCountResult
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.message_center.IMessageCenterProvider
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.core.getCompoundDrawable
import com.kotlin.android.ktx.ext.core.marginTop
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.statusBarHeight
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.message.R
import com.kotlin.android.message.databinding.MessageActivityMessageCenterBinding
import com.kotlin.android.message.ui.center.binder.MessageCenterChatListItemBinder
import com.kotlin.android.message.ui.center.binder.MessageCenterHeaderItemBinder
import com.kotlin.android.message.ui.center.binder.MessageCenterMovieNotifyItemBinder
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.dialog.BaseDialog
import com.kotlin.android.widget.refresh.ext.complete
import com.kotlin.android.widget.titlebar.back
import com.kotlin.android.widget.titlebar.fans

/**
 * Created by zhaoninglongfei on 2022/3/4
 * 10.0 新版消息中心页
 */
@Route(path = RouterActivityPath.MessageCenter.PAGE_MESSAGE_CENTER)
class MessageCenterActivity :
    BaseVMActivity<MessageCenterViewModel, MessageActivityMessageCenterBinding>() {

    private lateinit var adapter: MultiTypeAdapter

    override fun initTheme() {
        super.initTheme()
        immersive()
            .transparentStatusBar(isFitsSystemWindows = false)
            .statusBarDarkFont(true)
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        mBinding?.titleBar?.apply {
            marginTop = statusBarHeight
        }?.setTitle(
            title = getString(R.string.message_center),
            isBold = true,
            gravity = Gravity.CENTER,
            drawablePadding = 5.dp,
            endDrawable = getCompoundDrawable(
                id = R.drawable.message_ic_clear_message,
                width = 11.dp,
                height = 13.dp
            ),
            touchClick = {
                BaseDialog.Builder(this)
                    .setContent(getString(R.string.message_make_sure_clear_all_message))
                    .setPositiveButton(R.string.ok) { dialog, _ ->
                        mViewModel?.clearAllUnreadMessages()
                        dialog?.dismiss()
                    }
                    .setNegativeButton(R.string.cancel) { dialog, _ -> dialog?.dismiss() }
                    .create()
                    .show()
            }
        )
            ?.back { this.finish() }
            ?.fans { getProvider(IMessageCenterProvider::class.java)?.startFansActivity(this) }
    }

    override fun initView() {
        mBinding?.apply {
            adapter = createMultiTypeAdapter(rvMessageCenter)
            refreshLayout.setOnRefreshListener {
                //请求未读信息  请求聊天列表
                mViewModel?.loadUnreadCount()
                mViewModel?.loadChatList()
            }
            refreshLayout.setEnableLoadMore(false)
        }
        //先创建一个默认布局
        mViewModel?.apply {
            adapter.notifyAdapterAdded(createDefaultView())
        }
    }

    override fun onResume() {
        //从子页面返回，需要刷新未读消息数
        mBinding?.refreshLayout?.autoRefresh()
        super.onResume()
    }

    override fun initData() {
        mBinding?.refreshLayout?.autoRefresh()
    }

    override fun startObserve() {
        mViewModel?.let { lifecycle.addObserver(it) }
        mViewModel?.unReadUIState?.observe(this) {
            it?.apply {
                mBinding?.refreshLayout.complete(it)
                success?.let { result ->
                    //刷新评论/回复、点赞提醒
                    updateNotifyCount(result)
                    //刷新观影通知
                    updateMovieNotify(result)
                    //刷新新增粉丝数
                    updateUserFollowers(result)
                }
            }
        }
        mViewModel?.chatListUIState?.observe(this) {
            //刷新聊天列表
            adapter.getList().let { list ->
                list.filterIsInstance<MessageCenterChatListItemBinder>()[0].updateChatList(it)
            }
        }
        mViewModel?.clearMessagesUiState?.observe(this) {
            it?.apply {
                if (showLoading) {
                    showProgressDialog()
                }

                success?.let {
                    dismissProgressDialog()
                    mViewModel?.loadUnreadCount()
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

    //刷新新增粉丝数
    private fun updateUserFollowers(result: UnreadCountResult) {
        mBinding?.titleBar?.updateRedPoint(
            isReversed = true,
            isShow = result.userFollow != 0L,
            title = result.userFollow.convertToTitleShow()
        )
    }

    //转化为消息中心标题栏 粉丝通知展示
    private fun Long.convertToTitleShow(): CharSequence? {
        return when (this) {
            0L -> null
            in 1..99L -> this.toString()
            else -> getString(R.string.message_more_than_99)
        }
    }

    //刷新观影通知
    private fun updateMovieNotify(result: UnreadCountResult) {
        adapter.getList().let { list ->
            list.filterIsInstance<MessageCenterMovieNotifyItemBinder>()[0].updateMovieNotify(result)
        }
    }

    //刷新评论/回复、点赞提醒
    private fun updateNotifyCount(result: UnreadCountResult) {
        adapter.getList().let { list ->
            list.filterIsInstance<MessageCenterHeaderItemBinder>()[0].updateNotifyCount(result)
        }
    }
}