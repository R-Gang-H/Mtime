package com.kotlin.android.home.ui.tashuo

import android.view.View
import com.kotlin.android.community.card.component.item.BaseCardFragment
import com.kotlin.android.core.ext.observeLiveData
import com.kotlin.android.home.R
import com.kotlin.android.home.databinding.FagCommonCardListBinding
import com.kotlin.android.home.ui.tashuo.adapter.RcmdFollowItemBinder
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.ext.afterLogin
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.multistate.ext.complete
import com.kotlin.android.widget.refresh.ext.complete

/**
 * 首页-TA说
 */
class TaShuoFragment : BaseCardFragment<TaShuoViewModel, FagCommonCardListBinding>(),
    MultiStateView.MultiStateListener {
    private var mAdapter: MultiTypeAdapter? = null

    override fun initView() {
        mBinding?.apply {
            mMultiStateView.setMultiStateListener(this@TaShuoFragment)
            mAdapter = createMultiTypeAdapter(mRecyclerView)
                .setOnClickListener(::onBinderClick)
            mRefreshLayout.setOnRefreshListener {
                mViewModel?.loadData()
            }
            mRefreshLayout.setOnLoadMoreListener {
                mViewModel?.loadMoreData()
            }
        }
    }

    override fun onLoginStateChanged() {
        initData()
    }

    override fun startObserve() {
        super.startObserve()

        // 加载数据结果监听
        observeLiveData(mViewModel?.uiState) {
            mBinding?.apply {
                it.apply {
                    mRefreshLayout.complete(this)
                    mMultiStateView.complete(this)

                    success?.items?.apply {
                        if (isRefresh) {
                            mAdapter?.notifyAdapterDataSetChanged(this)
                        } else {
                            mAdapter?.notifyAdapterAdded(this)
                        }
                    }
                }
            }
        }

        // 关注、取消关注操作结果监听
        followObserve()
    }

    /**
     * 关注、取消关注操作结果监听
     */
    private fun followObserve() {
        mViewModel?.followState?.observe(this) {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                showToast(error)
                showToast(netError)

                success?.apply {
                    if (result.isSuccess()) {
                        when (extend) {
                            // 推荐关注
                            is RcmdFollowItemBinder -> {
                                (extend as RcmdFollowItemBinder).followChanged()
                            }
                        }
                    } else {
                        showToast(result.bizMsg.orEmpty())
                    }
                }
            }
        }
    }

    override fun initData() {
        mBinding?.mRefreshLayout?.autoRefresh()
    }

    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                mBinding?.mRefreshLayout?.autoRefresh()
            }
        }
    }

    /**
     * 从MultiTypeBinder回调的一些点击事件
     * 此事件已通过Adapter注册
     */
    override fun onBinderClick(view: View, binder: MultiTypeBinder<*>) {
        when (binder) {
            is RcmdFollowItemBinder -> {
                //推荐关注item点击事件
                onRcmdFollowItemBinderClickListener(view, binder)
            }
            else -> {
                super.onBinderClick(view, binder)
            }
        }
    }

    // 推荐关注item点击事件
    private fun onRcmdFollowItemBinderClickListener(
        view: View,
        binder: RcmdFollowItemBinder
    ) {
        if (view.id == R.id.userFollowFl) {
            // 关注、取消关注
            afterLogin {
                mViewModel?.follow(
                    action = if (binder.item.followed) 2 else 1,
                    userId = binder.item.userId,
                    extend = binder
                )
            }
        }
    }
}