package com.kotlin.android.ticket.order.component.ui.orderlist.history

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.ticket.order.component.R
import com.kotlin.android.ticket.order.component.databinding.FragHistoryTicketMtimeOrderBinding
import com.kotlin.android.ticket.order.component.ui.orderlist.TicketOrderListViewModel
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import kotlinx.android.synthetic.main.frag_history_ticket_mtime_order.*

/**
 * create by lushan on 2020/10/23
 * description: 三个月前时光网电影票订单列表
 */
class HistoryTicketMtimeOrderFragment : BaseVMFragment<TicketOrderListViewModel, FragHistoryTicketMtimeOrderBinding>() {
    private val mAdapter by lazy {
        createMultiTypeAdapter(orderRv).apply {
            setOnClickListener { view, binder -> }
            setOnLongClickListener { view, binder ->
                if (view.id == R.id.orderRootView) {
//                请求删除订单接口，删除订单
                    binder.notifyAdapterSelfRemoved()
                }
            }
        }
    }
    private var orderBinderList: MutableList<MultiTypeBinder<*>> = mutableListOf()//订单列表

    override fun initVM(): TicketOrderListViewModel {
        return viewModels<TicketOrderListViewModel>().value
    }

    override fun initView() {
        initRefreshLayout()
        stateView?.setMultiStateListener(object : MultiStateView.MultiStateListener {
            override fun onMultiStateChanged(viewState: Int) {
                if (viewState == MultiStateView.VIEW_STATE_NO_NET || viewState == MultiStateView.VIEW_STATE_ERROR) {
                    loadOrderList(false)
                }
            }

        })
    }

    private fun initRefreshLayout() {
        refreshLayout?.apply {
            setEnableLoadMore(true)
            setEnableRefresh(true)
            setOnRefreshListener {
                loadOrderList(false)
            }
            setOnLoadMoreListener {
                loadOrderList(true)
            }

        }
    }

    private fun loadOrderList(loadMore: Boolean) {
        mViewModel?.getOrderListOutThreeMonths(loadMore)
    }


    override fun initData() {
        refreshLayout?.autoRefresh()
    }

    private fun loadComplete() {
        refreshLayout?.finishLoadMore()
        refreshLayout?.finishRefresh()
    }

    private fun setContentState(@MultiStateView.ViewState state: Int) {
        stateView?.setViewState(state)
    }

    override fun startObserve() {
        mViewModel?.orderListOutThreeMonthsState?.observe(this, Observer  {
            it?.apply {
                success?.apply {
                    loadComplete()
                    if (loadMore.not()) {//不是加载更多需要
                        orderBinderList.clear()
                        if (this.list.isEmpty()){
                            setContentState(MultiStateView.VIEW_STATE_EMPTY)
                        }else{
                            setContentState(MultiStateView.VIEW_STATE_CONTENT)
                        }
                    }
                    orderBinderList.addAll(list)
                    if (loadMore) {
                        mAdapter.notifyAdapterAdded(orderBinderList)
                    } else {
                        mAdapter.notifyAdapterDataSetChanged(orderBinderList)
                    }
//                    判断当前加载订单数和totalCount相比，另外如果本次加载的订单为空也表示没有订单（有可能是删除了订单，本地和totalCount不一致，导致多加载了一次）
                    refreshLayout?.setNoMoreData(orderBinderList.size >= totalCount || list.isEmpty())
                }
                netError?.apply {
                    loadComplete()
                    if (orderBinderList.isEmpty()) {
                        setContentState(MultiStateView.VIEW_STATE_NO_NET)
                    }
                    this.showToast()
                }
                error?.apply {
                    loadComplete()
                    if (orderBinderList.isEmpty()) {
                        setContentState(MultiStateView.VIEW_STATE_ERROR)
                    }
                    this.showToast()
                }
            }
        })

    }

    override fun destroyView() {
    }
}