package com.kotlin.android.card.monopoly.ui.auction

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.adapter.AuctionBidBinder
import com.kotlin.android.card.monopoly.databinding.FragAuctionBiddingBinding
import com.kotlin.android.card.monopoly.ui.CardMonopolyApiViewModel
import com.kotlin.android.card.monopoly.widget.decoration.TopAndBottomItemDecoration
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.app.data.entity.monopoly.BidList
import com.kotlin.android.app.data.entity.monopoly.CardImageDetailBean
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast

import com.kotlin.android.app.router.provider.card_monopoly.ICardMonopolyProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.frag_auction_auction.mMultiStateView
import kotlinx.android.synthetic.main.frag_auction_auction.mRefreshLayout
import kotlinx.android.synthetic.main.frag_auction_auction.rvAuction

/**
 * @desc 拍卖行的竞价tab
 * @author zhangjian
 * @date 2020/9/14 14:04
 */

class AuctionBidFragment : BaseVMFragment<CardMonopolyApiViewModel, FragAuctionBiddingBinding>()
    , MultiStateView.MultiStateListener, OnRefreshLoadMoreListener {
    private val mProvider by lazy {
        getProvider(ICardMonopolyProvider::class.java)
    }
    private var mRefresh = true
    private var pageIndexs = 1L
    private var pageSize = 20L
    private var indexManager = 0
    private lateinit var mAdapter: MultiTypeAdapter
    private var mListData: ArrayList<MultiTypeBinder<*>>? = ArrayList()


    companion object {
        fun getInstance(): AuctionBidFragment {
            val fragment = AuctionBidFragment()
            return fragment
        }
    }

    override fun initVM(): CardMonopolyApiViewModel {
        return viewModels<CardMonopolyApiViewModel>().value
    }

    override fun initView() {
        immersive()
                .transparentStatusBar()
                .statusBarDarkFont(false)
        mMultiStateView.setMultiStateListener(this)
        mRefreshLayout.setOnRefreshLoadMoreListener(this)
        rvAuction.addItemDecoration(TopAndBottomItemDecoration(top = 15.dp, lastEdge = 15.dp))
        mAdapter = createMultiTypeAdapter(rvAuction, LinearLayoutManager(activity))
        refreshData()
    }


    override fun initData() {
    }

    override fun startObserve() {
        mViewModel?.bidListUiState?.observe(this, Observer {
            it?.apply {
                success?.apply {
                    dismissProgressDialog()
                    if (pageIndexs == 1L && this.bidList?.isEmpty() == true) {
                        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY)
                    }
                    if (mRefresh) {
                        mListData?.clear()
                        mAdapter.notifyAdapterClear()
                        mRefresh = false
                        if (indexManager!=0 && pageIndexs==1L){
                            rvAuction.scrollToPosition(indexManager)
                        }
                    }
                    pageIndexs++
                    showData(this, this.hasMore)
                }
                error?.apply {
                    dismissProgressDialog()
                    if (mListData?.size ?: 0 > 0) {
                        showToast(this)
                        mRefreshLayout.finishLoadMore()
                    } else {
                        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR)
                    }
                }
                netError?.apply {
                    dismissProgressDialog()
                    if (mListData?.size ?: 0 > 0) {
                        showToast(this)
                        mRefreshLayout.finishLoadMore()
                    } else {
                        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                    }
                }
            }
        })

        mViewModel?.bidUiState?.observe(this) {
            it.success?.apply {
                if (this.bizCode == 1L) {
                    refreshData()
                }
                showToast(this.bizMessage)
            }
            it.error?.apply {
                showToast(getString(R.string.str_fail))
            }
        }

        mViewModel?.bidSuccessPickCardUiState?.observe(this) {
            it.success?.apply {
                refreshData()
                showToast(this.bizMessage)
            }
            it.error?.apply {
                showToast(getString(R.string.str_fail))
            }
        }
    }

    private fun refreshData() {
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT)
        mRefreshLayout.setEnableLoadMore(true)
        mRefreshLayout.setNoMoreData(false)
        mRefresh = true
        pageIndexs = 1
        showProgressDialog()
        mViewModel?.getAuctionBidList(pageIndexs)
    }

    private fun showData(bidList: BidList, hasMore: Boolean) {
        if (bidList.bidList?.isEmpty() == true && pageIndexs == 1L) {
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY)
        }
        bidList.bidList?.forEach {
            //拍卖状态：0拍卖进行中，1一口价成交待领取，2流拍待取回，3一口价/竞拍成功已领取，4流拍已取回，6取消拍卖已取回
            if (it.auctionStatus == 0L || it.auctionStatus == 1L) {
                mListData?.add(AuctionBidBinder(it, activity, ::btnAction, ::jumpMainPage,::jumpCardDetails))
            }
        }

        mAdapter.notifyAdapterAdded(mListData as List<MultiTypeBinder<*>>) {
            if (hasMore) {
                mRefreshLayout.finishLoadMore()
            } else {
                mRefreshLayout.finishLoadMoreWithNoMoreData()
            }
        }
    }

    /**
     * 展示回调
     */
    fun btnAction(type: Int, price: Long, auctionId: Long,position:Int) {
        indexManager = position
        when (type) {
            1, 2 -> {
                //竞价, 一口价
                mViewModel?.bid(auctionId, price)
            }
            3 -> {
                //取回卡片
                mViewModel?.bidSuccessPickCard(auctionId)
            }
        }
    }

    override fun destroyView() {
    }

    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                refreshData()
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {

    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mViewModel?.getAuctionBidList(pageIndexs)
    }

    private fun jumpMainPage(userId: Long) {
        activity?.let {
            mProvider?.startCardMainActivity(it, userId = userId)
        }
    }

    private fun jumpCardDetails(image: View, cardDetails: CardImageDetailBean) {
        activity?.let {
            mProvider?.startImageDetailActivity(requireActivity(), image, cardDetails)
        }
    }
}