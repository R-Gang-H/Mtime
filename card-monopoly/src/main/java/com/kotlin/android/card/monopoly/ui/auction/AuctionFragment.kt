package com.kotlin.android.card.monopoly.ui.auction

import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.adapter.AuctionBinder
import com.kotlin.android.card.monopoly.constants.Constants.KEY_FIX_PRICE
import com.kotlin.android.card.monopoly.constants.Constants.KEY_START_PRICE
import com.kotlin.android.card.monopoly.constants.Constants.KEY_TIME_AUCTION
import com.kotlin.android.card.monopoly.databinding.FragAuctionAuctionBinding
import com.kotlin.android.card.monopoly.ext.showCardMonopolyCommDialog
import com.kotlin.android.card.monopoly.ext.showClearPocketDialog
import com.kotlin.android.card.monopoly.ui.CardMonopolyApiViewModel
import com.kotlin.android.card.monopoly.widget.decoration.TopAndBottomItemDecoration
import com.kotlin.android.card.monopoly.widget.dialog.CommDialog
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.app.data.entity.monopoly.AuctionList
import com.kotlin.android.app.data.entity.monopoly.CardImageDetailBean
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.store.putSpValue
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.app.router.provider.card_monopoly.ICardMonopolyProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.frag_auction_auction.*

/**
 * @desc 拍卖行的拍卖tab
 * @author zhangjian
 * @date 2020/9/14 14:04
 */

class AuctionFragment : BaseVMFragment<CardMonopolyApiViewModel, FragAuctionAuctionBinding>(),
    MultiStateView.MultiStateListener, OnRefreshLoadMoreListener {

    private val mProvider by lazy {
        getProvider(ICardMonopolyProvider::class.java)
    }

    private var mRefresh = true
    private var pageIndexs = 1L
    private var pageSize = 20L
    private lateinit var mAdapter: MultiTypeAdapter
    private var mListData: ArrayList<MultiTypeBinder<*>> = ArrayList()

    companion object {
        fun getInstance(): AuctionFragment {
            val fragment = AuctionFragment()
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
        showProgressDialog()
        rvAuction.addItemDecoration(TopAndBottomItemDecoration(bottom = 15.dp))
        mAdapter = createMultiTypeAdapter(rvAuction, LinearLayoutManager(activity))
        mViewModel?.loadAuctionList(pageIndexs, pageSize)
        setAuctionStyle(tvAuction)
    }

    override fun initData() {
    }

    override fun startObserve() {
        mViewModel?.auctionUiState?.observe(this, {
            dismissProgressDialog()
            it?.apply {
                success?.apply {
                    if (mRefresh) {
                        mListData.clear()
                        mAdapter.notifyAdapterDataSetChanged(mListData)
                        mRefresh = false
                    }
                    pageIndexs++
                    showData(this, this.hasMore)
                }
                if (isEmpty) {
                    mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY)
                }

                error?.apply {
                    if (mListData?.size ?: 0 > 0) {
                        showToast(this)
                        mRefreshLayout.finishLoadMore()
                    } else {
                        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR)
                    }
                }

                netError?.apply {
                    if (mListData?.size ?: 0 > 0) {
                        showToast(this)
                        mRefreshLayout.finishLoadMore()
                    } else {
                        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                    }

                }
            }
        })

        mViewModel?.retrieveCardUiState?.observe(this, {
            it.success?.apply {
                showToast(this.bizMessage)
                refreshData()
            }
            it.error?.apply {
                showToast(getString(R.string.common_request_fail_please_retry))
            }
            it.netError?.apply {
                showToast(getString(R.string.common_no_network_please_retry))
            }
        })


        mViewModel?.addAuctionUiState?.observe(this) {
            it.success?.apply {
                showToast(this.bizMessage)
                refreshData()
            }
            it.error?.apply {
                showToast(getString(R.string.common_request_fail_please_retry))
            }
            it.netError?.apply {
                showToast(getString(R.string.common_no_network_please_retry))
            }
        }

        mViewModel?.cancelAuctionUiState?.observe(this) {
            it.success?.apply {
                if (this.bizCode == 5L) {
                    showClearPocketDialog(bizMessage ?: getString(R.string.pocket_is_full))
                } else {
                    showToast(this.bizMessage)
                }
                refreshData()
            }
            it.error?.apply {
                showToast(this)
            }
            it.netError?.apply {
                showToast(this)
            }

        }

    }

    private fun refreshData() {
        mRefresh = true
        pageIndexs = 1
        mViewModel?.loadAuctionList(pageIndexs, pageSize)
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT)
        mRefreshLayout.setEnableLoadMore(true)
        mRefreshLayout.setNoMoreData(false)
    }

    private fun showData(auctionListBean: AuctionList, hasMore: Boolean) {
        auctionListBean.auctionList?.forEach {
            //拍卖状态：0拍卖进行中，1一口价成交待领取，2流拍待取回，3一口价/竞拍成功已领取，4流拍已取回，6取消拍卖已取回
            mListData.add(AuctionBinder(it, ::retrieveCard, ::jumpMainPage, ::jumpCardDetails))
        }
        mAdapter.notifyAdapterAdded(mListData as List<MultiTypeBinder<*>>) {
            if (hasMore) {
                mRefreshLayout?.finishLoadMore()
            } else {
                mRefreshLayout?.finishLoadMoreWithNoMoreData()
            }
        }
    }

    private fun setAuctionStyle(view: View) {
        ShapeExt.setShapeCorner2ColorWithColor(view, getColor(R.color.color_feb12a), 45)
        //展示拍卖的弹框
        view.onClick { it ->
            view.showCardMonopolyCommDialog(
                CommDialog.Style.AUCTION, CommDialog.Data(
                    message = "口袋。"
                )
            ) { data ->
                data?.apply {
                    setPriceToSP(start = startPrice, fix = fixPrice, time = timeLimited)
                    if (this.toolCard?.toolId != null) {
                        mViewModel?.addAuction(
                            id = this.toolCard?.toolId,
                            type = 2,
                            timeLimited = timeLimited ?: 0L,
                            startPrice = startPrice ?: 0L,
                            fixPrice = fixPrice ?: 0L
                        )
                    } else if (suit != null) {
                        mViewModel?.addAuction(
                            id = suit?.suitId,
                            type = 3,
                            timeLimited = timeLimited ?: 0L,
                            startPrice = startPrice ?: 0L,
                            fixPrice = fixPrice ?: 0L
                        )
                    } else {
                        mViewModel?.addAuction(
                            id = card?.cardId,
                            type = card?.type ?: 1L,
                            timeLimited = timeLimited ?: 0L,
                            startPrice = startPrice ?: 0L,
                            fixPrice = fixPrice ?: 0L
                        )
                    }

                }

            }
        }
    }


    /**
     * 设置价格到sp中
     */
    private fun setPriceToSP(start: Long? = 0L, fix: Long? = 0L, time: Long? = 0) {
        activity?.apply {
            if (start != 0L) {
                this.putSpValue(KEY_START_PRICE, start.toString())
            }
            if (fix != 0L) {
                this.putSpValue(KEY_FIX_PRICE, fix.toString())
            }
            if (time != 0L) {
                this.putSpValue(KEY_TIME_AUCTION, time)
            }

        }
    }

    /**
     * 取回卡片
     */
    private fun retrieveCard(cardId: Long, type: Long) {
        when (type) {
            0L -> {
                mViewModel?.cancelAuction(cardId)
            }
            else -> {
                mViewModel?.retrieveCard(cardId)
            }
        }
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

    override fun destroyView() {
    }

    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                showProgressDialog()
                pageIndexs = 1
                mViewModel?.loadAuctionList(pageIndexs, pageSize)
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {

    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mViewModel?.loadAuctionList(pageIndexs, pageSize)
    }
}