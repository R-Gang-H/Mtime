package com.kotlin.android.ticket.order.component.ui.orderlist

import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ticket.order.component.R
import com.kotlin.android.ticket.order.component.databinding.ActivityTicketOrderListBinding
import com.kotlin.android.ticket.order.component.ui.orderlist.history.HistoryOrderListActivity
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.titlebar.CommonTitleBar
import kotlinx.android.synthetic.main.activity_ticket_order_list.*

/**
 * 电影票订单列表
 */
@Route(path = RouterActivityPath.TicketOrder.TICKET_ORDER_LIST_ACTIVITY)
class TicketOrderListActivity : BaseVMActivity<TicketOrderListViewModel, ActivityTicketOrderListBinding>() {
    private val mAdapter by lazy {
        createMultiTypeAdapter(ticketOrderRv, LinearLayoutManager(this)).apply {
            setOnClickListener { view, binder -> }
            setOnLongClickListener { view, binder ->
                if (view.id == R.id.orderRootView) {
//                请求删除订单接口，删除订单 ，删除前需要判断当前是否是最后一个，如果是最后一个需要刷新接口
                    binder.notifyAdapterSelfRemoved()
                }
            }
        }
    }
    private var isInit = true

    override fun initTheme() {
        super.initTheme()
        immersive().statusBarDarkFont(true)
    }

    override fun initVM(): TicketOrderListViewModel = viewModels<TicketOrderListViewModel>().value


    override fun initCommonTitleView() {
        super.initCommonTitleView()
        CommonTitleBar().init(this).setTitle(R.string.ticket_order_title).create()
    }

    override fun initView() {
        initRefreshLayout()
        stateView?.setMultiStateListener(object : MultiStateView.MultiStateListener {
            override fun onMultiStateChanged(viewState: Int) {
                if (viewState == MultiStateView.VIEW_STATE_NO_NET || viewState == MultiStateView.VIEW_STATE_ERROR) {
                    loadOrderList()
                }
            }

        })

        threeMonthAgoCL?.apply {
            ShapeExt.setShapeColorAndCorner(this, R.color.color_f97d3f, 50)
            onClick {
//                跳转到三个月前订单
                startActivity(Intent(this@TicketOrderListActivity, HistoryOrderListActivity::class.java))
            }
        }
    }

    private fun initRefreshLayout() {
        refreshLayout?.apply {
            setEnableLoadMore(false)
            setEnableRefresh(true)
            setOnRefreshListener {
                loadOrderList()
            }

        }
    }


    private fun loadOrderList() {
        mViewModel?.getOrderListInThreeMonths()

    }

    override fun initData() {
        refreshLayout?.autoRefresh()
    }

    override fun onResume() {
        super.onResume()
        if (isInit.not()) {//第一次进入需要自动刷新
            loadOrderList()
        }
        isInit = false
    }

    override fun startObserve() {
        mViewModel?.orderListInThreeMonthsState?.observe(this, Observer {
            it?.apply {
                success?.run {
                    setContentState(MultiStateView.VIEW_STATE_CONTENT)
                    loadComplete()
//                    threeMonthAgoCL?.visible(this.hasOldOrder)
                    if (list.isEmpty()) {
                        setContentState(MultiStateView.VIEW_STATE_EMPTY)
                    }
                    mAdapter.notifyAdapterDataSetChanged(list, false)
                }
                netError?.run {
                    loadComplete()
                    setContentState(MultiStateView.VIEW_STATE_NO_NET)
                }
                error?.run {
                    loadComplete()
                    setContentState(MultiStateView.VIEW_STATE_ERROR)
                }
            }
        })
    }

    private fun loadComplete() {
        refreshLayout?.finishRefresh()
        refreshLayout?.finishLoadMore()
    }

    private fun setContentState(@MultiStateView.ViewState state: Int) {
        stateView?.setViewState(state)
    }
}