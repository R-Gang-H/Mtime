package com.kotlin.android.live.component.ui.fragment.list

import android.view.View
import androidx.lifecycle.Observer
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.core.ext.observeLiveData
import com.kotlin.android.live.component.R
import com.kotlin.android.live.component.constant.LIVE_HAD_APPOINT
import com.kotlin.android.live.component.databinding.FragmentLiveListBinding
import com.kotlin.android.live.component.ui.fragment.list.adapter.LiveListItemBinder
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.formatCount
import com.kotlin.android.router.liveevent.LOGIN_STATE
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener

/**
 * create by zsq on 2021/3/2
 * description: 直播列表fragment
 */
class LiveListFragment : BaseVMFragment<LiveListViewModel, FragmentLiveListBinding>(),
    OnRefreshLoadMoreListener, MultiStateView.MultiStateListener {

    private lateinit var mAdapter: MultiTypeAdapter
    private var mIsLoginStateChanged = false

    override fun initView() {
        mBinding?.apply {
            mRefreshLayout.setOnRefreshLoadMoreListener(this@LiveListFragment)
            mMultiStateView.setMultiStateListener(this@LiveListFragment)
            mAdapter = createMultiTypeAdapter(mLiveListRecyclerView)
            mAdapter.setOnClickListener(::onBinderClick)
        }
    }

    /**
     * 从MultiTypeBinder回调的一些点击事件
     * 此事件已通过Adapter注册
     */
    private fun onBinderClick(view: View, binder: MultiTypeBinder<*>) {
        when (binder) {
            is LiveListItemBinder -> {
                when (view.id) {
                    R.id.mLiveAppointBtnTv -> {
                        //直播预约
                        if (!binder.itemData.isAppoint()) {
                            mViewModel?.getLiveAppoint(binder.itemData.liveId, binder)
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (mIsLoginStateChanged) {
            mIsLoginStateChanged = false
            mBinding?.mRefreshLayout?.autoRefresh(300)
        }
    }

    override fun initData() {
        //自动刷新加载最新数据
        mBinding?.mRefreshLayout?.autoRefresh(500)
    }

    override fun startObserve() {
        //登录状态监听
        LiveEventBus
            .get(LOGIN_STATE, com.kotlin.android.app.router.liveevent.event.LoginState::class.java)
            .observe(this, Observer {
                mIsLoginStateChanged = true
            })

        //列表页总接口回调监听
        observeLiveData(mViewModel?.uiState) {
            it.apply {
                success?.apply {
                    showData(this, loadMore, noMoreData)
                }

                if (isEmpty) {
                    mBinding?.mMultiStateView?.setViewState(MultiStateView.VIEW_STATE_EMPTY)
                }

                error?.apply {
                    if (loadMore) {
                        mBinding?.mRefreshLayout?.finishLoadMore(false)
                    } else {
                        mBinding?.mRefreshLayout?.finishRefresh()
                        //没有数据则显示出错误状态页
                        if (mViewModel?.uiState?.value?.success.isNullOrEmpty()) {
                            mBinding?.mMultiStateView?.setViewState(MultiStateView.VIEW_STATE_ERROR)
                        }
                    }
                }

                netError?.apply {
                    if (loadMore) {
                        mBinding?.mRefreshLayout?.finishLoadMore(false)
                    } else {
                        mBinding?.mRefreshLayout?.finishRefresh()
                        //没有数据则显示出错误状态页
                        if (mViewModel?.uiState?.value?.success.isNullOrEmpty()) {
                            mBinding?.mMultiStateView?.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                        }
                    }
                }
            }
        }

        //预约结果
        observeLiveData(mViewModel?.uiLiveAppointState) {
            it.apply {
                if (showLoading) {
                    showProgressDialog()
                } else {
                    dismissProgressDialog()
                }

                success?.apply {
                    showToast(result.dataMsg)
                    extend.itemData.appointStatus = LIVE_HAD_APPOINT
                    extend.itemData.statistic = formatCount(result.appointCount)
                    extend.notifyAdapterSelfChanged()
                }

                error?.apply {
                    showToast(this)
                }

                netError?.apply {
                    showToast(this)
                }
            }
        }
    }

    override fun destroyView() {
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mViewModel?.getLiveList()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mViewModel?.getLiveWonderVodList()
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
     * 显示页面数据
     */
    private fun showData(
        data: List<MultiTypeBinder<*>>,
        isLoadMore: Boolean,
        noMoreData: Boolean
    ) {
        if (!isLoadMore) {
            mAdapter.notifyAdapterDataSetChanged(data)
            if (noMoreData) {
                mBinding?.mRefreshLayout?.finishRefreshWithNoMoreData()
            } else {
                mBinding?.mRefreshLayout?.finishRefresh()
            }
        } else {
            mAdapter.notifyAdapterAdded(data) {
                if (noMoreData) {
                    mBinding?.mRefreshLayout?.finishLoadMoreWithNoMoreData()
                } else {
                    mBinding?.mRefreshLayout?.finishLoadMore()
                }
            }
        }
    }
}