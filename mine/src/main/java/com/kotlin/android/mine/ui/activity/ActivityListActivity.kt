package com.kotlin.android.mine.ui.activity

import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mine.R
import com.kotlin.android.mine.databinding.ActActivityListBinding
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.multistate.ext.complete
import com.kotlin.android.widget.refresh.ext.complete
import com.kotlin.android.widget.titlebar.ThemeStyle
import com.kotlin.android.widget.titlebar.TitleBarManager
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener

/**
 * 创建者: vivian.wei
 * 创建时间: 2022/3/16
 * 描述: 个人中心-活动列表页
 */
@Route(path = RouterActivityPath.Mine.PAGE_ACTIVITY_LIST)
class ActivityListActivity: BaseVMActivity<ActivityListViewModel, ActActivityListBinding>(),
        OnRefreshLoadMoreListener, MultiStateView.MultiStateListener {

    private var mAdapter: MultiTypeAdapter? = null

    override fun initVM(): ActivityListViewModel= viewModels<ActivityListViewModel>().value

    override fun initTheme() {
        super.initTheme()
        immersive().transparentStatusBar(isFitsSystemWindows = false)
                .statusBarDarkFont(true)
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        TitleBarManager.with(this, ThemeStyle.STANDARD_STATUS_BAR)
                .setTitle(
                        titleRes = R.string.mine_activity_title,
                )
                .addItem(
                        drawableRes = R.drawable.ic_title_bar_36_back
                ) {
                    onBackPressed()
                }
    }

    override fun initView() {
        mBinding?.apply {
            mAdapter = createMultiTypeAdapter(activityRv)
            mRefreshLayout.setOnRefreshListener(this@ActivityListActivity)
            mRefreshLayout.setOnLoadMoreListener(this@ActivityListActivity)
            mMultiStateView.setMultiStateListener(this@ActivityListActivity)
        }
    }

    override fun initData() {
        requestData(true)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        requestData(true)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        requestData(false)
    }

    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                // 请求列表
                mAdapter?.notifyAdapterClear()
                requestData(true)
            }
            else -> {
            }
        }
    }

    /**
     * 请求数据
     */
    private fun requestData(isRefresh: Boolean) {
        mBinding?.mMultiStateView?.setViewState(MultiStateView.VIEW_STATE_CONTENT)
        mViewModel?.getActivityList(
                context = this,
                isRefresh = isRefresh
        )
    }

    override fun startObserve() {
        getActivityListObserve()
    }

    /**
     * 活动列表Observe
     */
    private fun getActivityListObserve() {
        mViewModel?.uIState?.observe(this) {
            it.apply {
                showOrHideProgressDialog(showLoading)
                mBinding?.mRefreshLayout.complete(this)
                mBinding?.mMultiStateView.complete(this)
                success?.apply {
                    if(isRefresh) {
                        mAdapter?.notifyAdapterDataSetChanged(binders)
                    } else {
                        binders?.let { binderList ->
                            mAdapter?.notifyAdapterAdded(binderList)
                        }
                    }
                }
            }
        }
    }
}