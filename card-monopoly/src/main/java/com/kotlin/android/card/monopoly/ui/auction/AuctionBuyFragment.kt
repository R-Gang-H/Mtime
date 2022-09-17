package com.kotlin.android.card.monopoly.ui.auction

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.kotlin.android.app.data.entity.monopoly.BuyList
import com.kotlin.android.app.data.entity.monopoly.CardImageDetailBean
import com.kotlin.android.app.data.entity.monopoly.Suit
import com.kotlin.android.app.router.provider.card_monopoly.ICardMonopolyProvider
import com.kotlin.android.card.monopoly.KEY_CARD_MONOPOLY_AUCTION_TYPE
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.adapter.AuctionBuyBinder
import com.kotlin.android.card.monopoly.constants.Constants
import com.kotlin.android.card.monopoly.databinding.FragAuctionBuyBinding
import com.kotlin.android.card.monopoly.databinding.ItemAuctionBuyBinding
import com.kotlin.android.card.monopoly.ui.CardMonopolyApiViewModel
import com.kotlin.android.card.monopoly.widget.AuctionFilterView
import com.kotlin.android.card.monopoly.widget.card.view.SelectCardView
import com.kotlin.android.card.monopoly.widget.decoration.TopAndBottomItemDecoration
import com.kotlin.android.card.monopoly.widget.search.SearchCardSuitView
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.frag_auction_buy.*


/**
 * @desc 拍卖行的购买tab
 * @author zhangjian
 * @date 2020/9/14 14:04
 */

class AuctionBuyFragment : BaseVMFragment<CardMonopolyApiViewModel, FragAuctionBuyBinding>(),
    MultiStateView.MultiStateListener,
    OnRefreshLoadMoreListener {
    private val mProvider by lazy {
        getProvider(ICardMonopolyProvider::class.java)
    }
    private var mRefresh = false
    private var orderType = Constants.ORDER_TIME_ASE
    private var cardType = Constants.ORDER_CARD
    private var searchCardId = 0L
    private var mSuitId = 0L
    private var mCardId = 0L
    private var pageIndexs = 1L
    private var pageSize = 50L
    private var mSuit: Suit? = null
    private lateinit var mAdapter: MultiTypeAdapter
    private var mListData: ArrayList<MultiTypeBinder<ItemAuctionBuyBinding>>? = ArrayList()
    private var mClearSuitListener: IClearSuitData? = null
    private var indexManager:Int = 0

    companion object {
        fun getInstance(mSuit: Suit?): AuctionBuyFragment {
            val fragment = AuctionBuyFragment()
            val bundle = Bundle()
            bundle.putParcelable(KEY_CARD_MONOPOLY_AUCTION_TYPE, mSuit)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun initVM(): CardMonopolyApiViewModel {
        return viewModels<CardMonopolyApiViewModel>().value
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mClearSuitListener = activity as IClearSuitData
    }

    override fun initView() {
        immersive()
            .transparentStatusBar()
            .statusBarDarkFont(false)
        mMultiStateView.setMultiStateListener(this)
        mRefreshLayout.setOnRefreshLoadMoreListener(this)
        mSuit = arguments?.getParcelable(KEY_CARD_MONOPOLY_AUCTION_TYPE)
        searchCardSuitView.apply {
            stateChange = {
                when (it) {
                    SearchCardSuitView.State.EXPANDING -> {
                        selectCardView?.gone()
                        llFilter?.gone()
                        llResult?.gone()
                        setAppBarScrollState(false)
                    }
                    SearchCardSuitView.State.COLLAPSING -> {
                        selectCardView?.visible()
                        llFilter?.visible()
                        llResult?.visible()
                        setAppBarScrollState(true)
                    }
                }
            }

            searchAction = {
                if (it.event == 1) {
                    mViewModel?.querySuitByCard(it.keyword, isShowLoading = true)
                } else {
                    mViewModel?.querySuitByCard(it.keyword, isShowLoading = false)
                }
            }

            action = {
                mViewModel?.suitCards(it.suitId)
            }

            cancel = {
                selectCardView?.gone()
                searchCardId = 0L
                mSuitId = 0L
                mCardId = 0L
                refreshData()
                mClearSuitListener?.onClearSuit()
            }
        }
        selectCardView?.apply {
            selectModel = SelectCardView.SelectModel.SINGLE_NOT_CANCEL
            action = {
                it?.apply {
                    mCardId = cardId
                    //点击卡的时候切换类型
                    typeFilterView.setState(Constants.TYPE_CARD,false)
                    cardType = Constants.TYPE_CARD
                    refreshData()
                }
            }
        }
//        filterView.setTextValue(getString(R.string.reset_time),getString(R.string.reset_time),getString(R.string.reset_time))
        filterView.onClickFilter = object : AuctionFilterView.OnClickFilterListener {
            override fun clickView(order: Int) {
                showProgressDialog()
                orderType = order
                refreshData()
            }

        }

        typeFilterView.action = {
            cardType = it
            refreshData()
        }

        rvAuctionBuy.addItemDecoration(TopAndBottomItemDecoration(bottom = 15.dp))
        mAdapter = createMultiTypeAdapter(rvAuctionBuy, LinearLayoutManager(activity))
        showProgressDialog()
        if (mSuit != null) {
            mSuit?.cardList?.forEach {
                if (it.isSelected) {
                    mCardId = it.cardId
                }
            }
            selectCardView?.selectModel = SelectCardView.SelectModel.SINGLE_NOT_CANCEL
            selectCardView?.data = mSuit?.cardList
            selectCardView?.visible()
            searchCardSuitView?.text = mSuit?.suitName.orEmpty()
            loadDataByParam()
        } else {
            loadDataByParam()
        }
    }

    /**
     * 是否可以滑动
     * @param flag true --可滑动  false -不可滑动
     */
    private fun setAppBarScrollState(flag: Boolean) {
        val mAppBarChildAt: View = mAppBarLayout.getChildAt(0)
        val mAppBarParams = mAppBarChildAt.layoutParams as AppBarLayout.LayoutParams
        if (flag) {
            mAppBarParams.scrollFlags =
                AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
            mAppBarChildAt.layoutParams = mAppBarParams
        } else {
            mAppBarParams.scrollFlags = 0
        }

    }

    override fun initData() {
    }


    //更新数据
    private fun refreshData() {
        mRefresh = true
        pageIndexs = 1L
        mRefreshLayout.setEnableLoadMore(true)
        mRefreshLayout.setNoMoreData(false)
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT)
        loadDataByParam()
    }


    private fun loadDataByParam() {
        //1.没有搜索
        if(mCardId == 0L){
            searchCardId = 0L
        }else{
            //2.有searchCardId搜索
            searchCardId = when(cardType){
                //1.搜索点击的是卡
                Constants.ORDER_CARD->{
                    mCardId
                }
                //2.搜索点击的是套装
                Constants.ORDER_SUIT->{
                    mSuitId
                }
                //3.搜索点击的是道具
                else->{
                    0L
                }
            }
        }
        mViewModel?.loadAuctionBuyList(
            orderType.toLong(),
            cardType,
            searchCardId,
            pageIndexs,
            pageSize
        )

    }

    override fun startObserve() {
        mViewModel?.auctionBuyUiState?.observe(this, {
            it?.apply {
                success?.apply {
                    if (mRefresh) {
                        mRefresh = false
                        mListData?.clear()
                        mAdapter.notifyAdapterClear()
                        if (indexManager!=0 && pageIndexs==1L){
                            rvAuctionBuy.scrollToPosition(indexManager)
                        }
                    }
                    if (pageIndexs == 1L && this.buyList?.isEmpty() == true) {
                        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY)
                    } else {
                        pageIndexs++
                        showData(this, this.hasMore)
                    }
                }
                if (isEmpty) {
                    mRefresh = false
                    mListData?.clear()
                    mAdapter.notifyAdapterClear()
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
                dismissProgressDialog()
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
            it.netError?.apply {
                showToast(this)
            }
        }
        mViewModel?.bidSuccessPickCardUiState?.observe(this) {
            it.success?.apply {
                if (this.bizCode == 1L) {
                    refreshData()
                } else {
                    showToast(this.bizMessage)
                }
            }
            it.error?.apply {
                showToast(getString(R.string.str_fail))
            }
            it.netError?.apply {
                showToast(this)
            }
        }

        mViewModel?.querySuitByCardUiState?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    searchCardSuitView.data = suitList
                }
            }
        }

        mViewModel?.suitCardsUiState?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    mSuitId = suitInfo?.suitId ?: 0L
                    cardList?.forEach { list ->
                        list.cardSuitId = this.suitInfo?.suitId
                    }
                    selectCardView?.data = cardList
                }
            }
        }
    }

    private fun showData(mListBean: BuyList, hasMore: Boolean) {
        mListBean.buyList?.forEach {
            mListData?.add(
                AuctionBuyBinder(
                    it,
                    activity,
                    ::getItemBid,
                    ::jumpMainPage,
                    ::jumpCardDetails
                )
            )
        }
        if (hasMore) {
            mRefreshLayout.finishLoadMore()
        } else {
            mRefreshLayout.finishLoadMoreWithNoMoreData()
        }
        mAdapter.notifyAdapterAdded(mListData as List<MultiTypeBinder<*>>)
    }


    override fun destroyView() {
    }

    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                showProgressDialog()
                loadDataByParam()
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {

    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        loadDataByParam()
    }

    /**
     * 发起竞价
     * @param type 1-竞价,2-一口价
     * @param price  竞价价格
     * @param auctionId 竞价id
     */
    private fun getItemBid(type: Int, price: Long, auctionId: Long, index:Int) {
        indexManager = index
        if (type == 1) {
            mViewModel?.bid(auctionId, price)
        } else {
//            mViewModel?.bidSuccessPickCard(auctionId)
            mViewModel?.bid(auctionId, price)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        searchCardId = 0L
        selectCardView?.data = null
        selectCardView?.visibility = View.GONE
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

    /**
     * 清空mSuit对象
     */
    interface IClearSuitData {
        fun onClearSuit()
    }
}