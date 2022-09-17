package com.kotlin.android.message.ui.fans

import android.view.Gravity
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.core.marginTop
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.statusBarHeight
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.message.R
import com.kotlin.android.message.databinding.MessageActivityFansBinding
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.multistate.ext.complete
import com.kotlin.android.widget.refresh.ext.complete
import com.kotlin.android.widget.titlebar.TitleBarManager

/**
 * Created by zhaoninglongfei on 2022/3/15
 * 新消息中心 - 粉丝
 */
@Route(path = RouterActivityPath.MessageCenter.PAGE_FANS)
class FansActivity : BaseVMActivity<FansViewModel, MessageActivityFansBinding>(),
    MultiStateView.MultiStateListener {

    private lateinit var adapter: MultiTypeAdapter

    override fun initTheme() {
        super.initTheme()
        immersive()
            .transparentStatusBar(isFitsSystemWindows = false)
            .statusBarDarkFont(true)
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        TitleBarManager.with(this)
            .apply {
                marginTop = statusBarHeight
            }
            .setTitle(
                title = getString(R.string.message_fans),
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
        mBinding?.apply {
            adapter = createMultiTypeAdapter(rvFans)
            mMultiStateView.setMultiStateListener(this@FansActivity)
            refreshLayout.setOnRefreshListener {
                mViewModel?.loadFansList(true)
            }
            refreshLayout.setOnLoadMoreListener {
                mViewModel?.loadFansList(false)
            }
        }
    }

    override fun initData() {
        mBinding?.refreshLayout?.autoRefresh()
    }

    override fun startObserve() {
        mViewModel?.fansUiState?.observe(this) {
            it?.apply {
                mBinding?.refreshLayout.complete(it)
                mBinding?.mMultiStateView.complete(it) {
                    mViewModel?.loadEmptyView()
                }

                success?.let {
                    binders?.apply {
                        if (isRefresh) {
                            adapter.notifyAdapterRemovedAll {
                                adapter.notifyAdapterAdded(this)
                            }
                        } else {
                            adapter.notifyAdapterAdded(this)
                        }
                    }
                }
            }
        }

        mViewModel?.emptyStateUiState?.observe(this) {
            it?.apply {
                success?.let { binder ->
                    adapter.notifyAdapterRemovedAll {
                        adapter.notifyAdapterAdded(binder)
                    }
                }
            }
        }

        mViewModel?.followUserUiState?.observe(this) {
            it?.apply {
                if (showLoading) {
                    showProgressDialog()
                }

                success?.let {
                    dismissProgressDialog()
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

    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                mBinding?.refreshLayout?.autoRefresh()
            }
        }
    }
}