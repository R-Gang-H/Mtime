package com.kotlin.android.card.monopoly.ui.deal

import android.app.AlertDialog
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.data.entity.monopoly.ItemData
import com.kotlin.android.app.data.entity.monopoly.RecordList
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.card_monopoly.ICardMonopolyProvider
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.TAB_AUCTION_AUCTION
import com.kotlin.android.card.monopoly.TAB_AUCTION_BID
import com.kotlin.android.card.monopoly.adapter.deal.*
import com.kotlin.android.card.monopoly.constants.Constants
import com.kotlin.android.card.monopoly.databinding.ActGameRecordBinding
import com.kotlin.android.card.monopoly.ext.showCardMonopolyCommDialog
import com.kotlin.android.card.monopoly.ext.showDealAddPriceDialog
import com.kotlin.android.card.monopoly.ext.showFunctionMenuDialog
import com.kotlin.android.card.monopoly.ui.CardMonopolyApiViewModel
import com.kotlin.android.card.monopoly.widget.dialog.CommDialog
import com.kotlin.android.card.monopoly.widget.dialog.view.DealCardView
import com.kotlin.android.card.monopoly.widget.nav.NavView
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.span.toBold
import com.kotlin.android.ktx.ext.span.toColor
import com.kotlin.android.ktx.ext.span.toSpan
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.SafeLinearLayoutManager
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.titlebar.State
import com.kotlin.android.widget.titlebar.ThemeStyle
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.act_game_record.*
import kotlinx.android.synthetic.main.act_game_record.mMultiStateView
import kotlinx.android.synthetic.main.act_game_record.mRefreshLayout
import kotlinx.android.synthetic.main.act_suit_detail.navView
import kotlinx.android.synthetic.main.act_suit_detail.titleBar

/**
 * 卡片大富翁交易信息页面：
 *
 * Created on 2020/9/7.
 *
 * @author o.s
 */
@Route(path = RouterActivityPath.CardMonopoly.PAGER_DEAL_RECORDS)
class DealRecordsActivity : BaseVMActivity<CardMonopolyApiViewModel, ActGameRecordBinding>(),
    MultiStateView.MultiStateListener, OnRefreshLoadMoreListener {

    private val mProvider by lazy {
        getProvider(ICardMonopolyProvider::class.java)
    }

    private lateinit var mAdapter: MultiTypeAdapter
    private var mListData: ArrayList<MultiTypeBinder<*>>? = ArrayList()
    private var mItemData: ItemData = ItemData()

    val timeList = arrayOf("最近一个月", "一个月之前")
    var listType: Long = 1
    var pageSize: Long = 20
    var pageIndexs: Long = 1
    var timeType: Long = 0L
    var deletePosition: Int = -1
    var binderItem: MultiTypeBinder<*>? = null

    private val TYPE_DEAL = 1L
    private val TYPE_PROPS = 2L
    private val TYPE_FRIEND = 3L

    override fun initVM(): CardMonopolyApiViewModel = viewModels<CardMonopolyApiViewModel>().value

    override fun initView() {
        immersive()
            .transparentStatusBar()
            .statusBarDarkFont(false)
        window.setBackgroundDrawable(null)
        mRefreshLayout.setOnRefreshLoadMoreListener(this)
        initTitleView()
        initContentView()
    }

    override fun initNewData() {
        initData()
    }

    override fun initData() {
        navView?.selectItem(0)
    }

    override fun startObserve() {

        mViewModel?.gameUiState?.observe(this) {
            it?.apply {
                success?.apply {
                    if (this.hasMore) {
                        pageIndexs++
                    } else {
                        mRefreshLayout.setNoMoreData(true)
                    }
                    if (!this.hasMore && pageIndexs == 1L && this.messageList?.isEmpty() == true) {
                        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY)
                    } else {
                        showData(this, this.hasMore, noMoreData)
                    }
                }

                error?.apply {
                    mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR)
                }

                netError?.apply {
                    mMultiStateView.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                }
            }
        }

        mViewModel?.deleteRecordUiState?.observe(this) {
            it?.apply {
                success?.apply {
                    showToast(this.bizMessage)
                    if (deletePosition != -1) {
                        mListData?.removeIf { it ->
                            var flag = false
                            if (it is BiddingBinder && it.data.recordDetailId == mItemData.recordDetail) {
                                flag = true
                                it.notifyAdapterSelfRemoved()
                            } else if (it is FriendNormalBinder && it.data.recordDetailId == mItemData.recordDetail) {
                                flag = true
                                it.notifyAdapterSelfRemoved()
                            } else if (it is FriendVisitBinder && it.data.recordDetailId == mItemData.recordDetail) {
                                flag = true
                                it.notifyAdapterSelfRemoved()
                            } else if (it is PropsBinder && it.data.recordDetailId == mItemData.recordDetail) {
                                flag = true
                                it.notifyAdapterSelfRemoved()
                            } else if (it is TransBinder && it.data.recordDetailId == mItemData.recordDetail) {
                                flag = true
                                it.notifyAdapterSelfRemoved()
                            } else if (it is TransResultBinder && it.data.recordDetailId == mItemData.recordDetail) {
                                flag = true
                                it.notifyAdapterSelfRemoved()
                            }
                            flag
                        }
                    }
                }
                error?.apply {
                    showToast(getString(R.string.common_request_fail_please_retry))
                }
            }
        }

        mViewModel?.agreeAddFriendUiState?.observe(this) {
            it?.apply {
                success?.apply {
                    when (bizCode) {
                        -3L -> {
                            showCardMonopolyCommDialog(
                                data = CommDialog.Data(
                                    title = "同意添加好友",
                                    message = bizMessage
                                )
                            )
                        }
                        else -> {
                            showToast(this.bizMessage)
                        }
                    }
                    refreshData()
                }
                error?.apply {
                    showToast(getString(R.string.common_request_fail_please_retry))
                }
            }
        }

        mViewModel?.refuseAddFriendUiState?.observe(this) {
            it?.apply {
                success?.apply {
                    when (bizCode) {
                        -3L -> {
                            showCardMonopolyCommDialog(
                                data = CommDialog.Data(
                                    title = "拒绝添加好友",
                                    message = bizMessage
                                )
                            )
                        }
                        else -> {
                            showToast(this.bizMessage)
                        }
                    }
                    refreshData()
                }
                error?.apply {
                    showToast(getString(R.string.common_request_fail_please_retry))
                }
            }
        }

        mViewModel?.refuseTradeUiState?.observe(this) {
            it?.apply {
                success?.apply {
                    when (bizCode) {
                        -3L -> {
                            showCardMonopolyCommDialog(
                                data = CommDialog.Data(
                                    title = "拒绝交易",
                                    message = bizMessage
                                )
                            )
                        }
                        else -> {
                            showToast(this.bizMessage)
                        }
                    }
                    refreshData()
                }
                error?.apply {
                    showToast(getString(R.string.common_request_fail_please_retry))
                }
            }
        }

        mViewModel?.agreeTradeUiState?.observe(this) {
            it?.apply {
                success?.apply {
                    when (bizCode) {
                        1L -> {
                            val name = mItemData.userName.toSpan()
                                .toColor(color = getColor(R.color.color_1fc4ca))
                            val price = mItemData.price.toString().toSpan()
                                .toColor(color = getColor(R.color.color_feb12a))
                            val msg =
                                "您与".toSpan().append(name).append("已成功交易！对方给你支付了").append(price)
                                    .append("个金币")
                            showCardMonopolyCommDialog(
                                style = CommDialog.Style.DEAL_SUCCESS,
                                data = CommDialog.Data(
                                    message = msg
                                )
                            ) {
                                refreshData()
                            }

                        }
                        -3L -> {
                            showCardMonopolyCommDialog(
                                data = CommDialog.Data(
                                    title = "同意交易",
                                    message = bizMessage
                                )
                            )
                        }
                        else -> {
                            val name = mItemData.userName.toSpan()
                                .toColor(color = getColor(R.color.color_1fc4ca))
                            val msg = "您与".toSpan().append(name).append("交易失败!")
                            showCardMonopolyCommDialog(
                                data = CommDialog.Data(
                                    title = "交易失败",
                                    message = msg
                                )
                            )
                        }
                    }

                }
                error?.apply {
                    showToast(getString(R.string.common_request_fail_please_retry))
                }
            }
        }


    }

    private fun showData(gameDealListBean: RecordList, hasMore: Boolean, noMoreData: Boolean) {
        gameDealListBean.messageList?.forEach {
            when (it.recordType) {
                Constants.GAME_RECORD_BIDDING_FAIL,
                Constants.GAME_RECORD_BIDDING_SUCCESS,
                Constants.GAME_RECORD_AUCTION_SUCCESS,
                Constants.GAME_RECORD_GIFT_CARD,
                Constants.GAME_RECORD_GIFT_GOLD,
                Constants.GAME_RECORD_GIFT_PROP,
                Constants.GAME_RECORD_AUCTION_FAIL -> {
                    mListData?.add(BiddingBinder(it, ::onBtnClick))
                }
                Constants.GAME_RECORD_INIT_TRANS,
                Constants.GAME_RECORD_DISS_PRICE -> {
                    mListData?.add(TransBinder(it, ::onBtnClick, ::jumpMainPage))
                }
                Constants.GAME_RECORD_USE_PROPS -> {
                    mListData?.add(PropsBinder(it, ::onBtnClick))
                }

                Constants.GAME_RECORD_TRANS_SUCCESS,
                Constants.GAME_RECORD_TRANS_FAIL -> {
                    mListData?.add(TransResultBinder(it, ::onBtnClick))
                }

                Constants.GAME_RECORD_DEL_FRIEND,
                Constants.GAME_RECORD_ADD_FRIEND_SUCCESS,
                Constants.GAME_RECORD_ADD_FRIEND,
                Constants.GAME_RECORD_REFUSE_ADD -> {
                    mListData?.add(FriendVisitBinder(it, ::onBtnClick, ::jumpMainPage))
                }
//              Constants.GAME_RECORD_INVITE_FRIENDS,
                Constants.GAME_RECORD_REFUSE_TRANS,
                Constants.GAME_RECORD_REFUSE_DISS -> {
                    mListData?.add(FriendNormalBinder(it, ::onBtnClick))
                }

            }
        }
        mAdapter.notifyAdapterAdded(mListData as List<MultiTypeBinder<*>>) {
            mRefreshLayout.finishLoadMore()
        }
    }

    private fun initTitleView() {
        titleBar?.apply {
            setThemeStyle(ThemeStyle.STANDARD_STATUS_BAR)
            setState(State.REVERSE)
            addItem(
                drawableRes = R.drawable.ic_title_bar_back_light,
                reverseDrawableRes = R.drawable.ic_title_bar_back_dark
            ) {
                finish()
            }
            setTitle(getString(R.string.deal_records), alwaysShow = true) {

            }
            addItem(
                drawableRes = R.drawable.ic_title_bar_more_light,
                reverseDrawableRes = R.drawable.ic_title_bar_more_dark,
                isReversed = true,
            ) {
                showFunctionMenuDialog(
                    dismiss = {
                        syncStatusBar()
                    }
                )
            }
            addItem(
                drawableRes = R.drawable.ic_filter_record,
                reverseDrawableRes = R.drawable.ic_filter_record,
                isReversed = true,
            ) {
                showTimeSelectDialog()
            }
        }
    }

    private fun showTimeSelectDialog() {
        val alert = AlertDialog.Builder(this)
        alert.apply {
            setTitle(getString(R.string.choice_time))
            setSingleChoiceItems(timeList, -1) { _, index ->
                timeType = when (index) {
                    0 -> {
                        0L
                    }
                    else -> {
                        1L
                    }
                }
            }
            setPositiveButton(getString(R.string.ok)) { p0, p1 ->
                refreshData()
                alert.create().dismiss()
            }
            setNegativeButton(getString(R.string.cancel), null)
        }
        alert.create().show()
    }

    private fun initContentView() {
        mainLayout?.background = getShapeDrawable(
            colorRes = R.color.color_a2edff,
            endColorRes = R.color.color_ffffff
        )
        navView?.apply {
            setItems(
                NavView.Category.DEAL_INFO,
                NavView.Category.PROP_CARD_INFO,
                NavView.Category.CARD_FRIEND
            )
            style = NavView.Style.TRIPLE
            action = {
                when (it) {
                    0 -> {
                        listType = TYPE_DEAL
                    }
                    1 -> {
                        listType = TYPE_PROPS
                    }
                    2 -> {
                        listType = TYPE_FRIEND
                    }
                }
                pageIndexs = 1
                timeType = 0L
                mAdapter.notifyAdapterClear()
                mListData?.clear()
                mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT)
                mRefreshLayout.setNoMoreData(false)
                loadData()
            }
        }
        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        com.kotlin.android.mtime.ktx.getDrawable(R.drawable.item_decoration_ver_fff)
            ?.let { divider.setDrawable(it) }
        rvRecord.addItemDecoration(divider)
        mAdapter = createMultiTypeAdapter(rvRecord, SafeLinearLayoutManager(this))
    }

    private fun loadData() {
        mViewModel?.loadGameInfo(listType, pageIndexs, pageSize, timeType)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        loadData()
    }

    private fun refreshData() {
        pageIndexs = 1
        mRefreshLayout.setNoMoreData(false)
        mAdapter.notifyAdapterClear()
        mListData?.clear()
        loadData()
    }

    /**
     * item中的点击事件
     */
    private fun onBtnClick(item: ItemData, binder: MultiTypeBinder<*>?) {
        //删除
        when (item.actionType) {
            Constants.TYPE_DELETE -> {
                mItemData = item
                mViewModel?.deleteRecord(item.recordDetail ?: 0)
                deletePosition = item.position ?: -1
            }
            Constants.TYPE_USE_TOOLS -> {
                //跳转主页
                mProvider?.startCardMainActivity(
                    context = this,
                    userId = item.userInfo?.userId ?: 0L
                )
            }
            Constants.TYPE_ADD_TRADE_PRICE -> {
                //交易加价,弹框
                if (item.recordStatus == 1L) {
                    showCardMonopolyCommDialog(
                        data = CommDialog.Data(
                            title = "加价失败",
                            message = "你已经处理过此条消息。"
                        )
                    )
                } else {
                    val dialogCard = DealCardView.Data(
                        userInfo = item.userInfo,
                        srcCard = item.srcCard,
                        desCard = item.desCard,
                        itemData = item
                    )
                    showDealAddPriceDialog(data = dialogCard) {
                        when (it) {
                            1L -> {
                                refreshData()
                            }
                        }
                    }
                }

//                mViewModel?.addTradePrice()
            }
            Constants.TYPE_REFUSE_FRIEND -> {
                //拒绝添加好友
                val message = "您谢绝与".toSpan()
                    .append(
                        item.userName.toSpan().toBold()
                            .toColor(color = getColor(R.color.color_feb12a))
                    ).append(
                        "加为好友"
                    )
                showCardMonopolyCommDialog(
                    style = CommDialog.Style.REFUSE_TO_ADD_FRIEND, data = CommDialog.Data(
                        message = message
                    )
                ) {
                    mViewModel?.refuseFriend(item.recordDetail ?: 0, it?.postscript ?: "")
                }
            }
            Constants.TYPE_AGREE_FRIEND -> {
                //同意添加卡友
                binderItem = binder
                mViewModel?.agreeFriend(item.recordDetail ?: 0)

            }
            Constants.TYPE_AGREE_TRADE -> {
                //交易同意
                mItemData.userName = item.userName
                mItemData.price = item.price
                mViewModel?.agreeTrade(item.recordDetail ?: 0)
            }
            Constants.TYPE_REFUSE_TRADE -> {
                if (item.recordStatus == 1L) {
                    showCardMonopolyCommDialog(
                        data = CommDialog.Data(
                            title = "拒绝失败",
                            message = "你已经处理过此条消息。"
                        )
                    )
                } else {
                    val message = "您谢绝与".toSpan()
                        .append(
                            item.userName.toSpan().toBold()
                                .toColor(color = getColor(R.color.color_20a0da))
                        ).append(
                            "的"
                        ).append(
                            item.desCard?.cardName.toSpan().toBold()
                                .toColor(color = getColor(R.color.color_feb12a))
                        ).append(
                            "卡片交易"
                        )
                    showCardMonopolyCommDialog(
                        style = CommDialog.Style.NO_DEAL, CommDialog.Data(
                            message = message
                        )
                    ) {
                        mViewModel?.refuseTrade(item.recordDetail ?: 0L, it?.postscript ?: "")
                    }
                }

            }
            //竞拍成功跳转竞价页面
            Constants.GAME_RECORD_BIDDING_SUCCESS.toInt() -> {
                mProvider?.startAuctionActivity(TAB_AUCTION_BID, null)
            }
            //拍卖失败,跳转拍卖页面
            Constants.GAME_RECORD_AUCTION_FAIL.toInt() -> {
                mProvider?.startAuctionActivity(TAB_AUCTION_AUCTION, null)
            }
            else -> {
                mProvider?.startAuctionActivity()
            }
        }
    }

    private fun jumpMainPage(userId: Long) {
        mProvider?.startCardMainActivity(this, userId = userId)
    }


    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                pageIndexs = 1
                mAdapter.notifyAdapterClear()
                mListData?.clear()
                mRefreshLayout.setEnableLoadMore(true)
                mRefreshLayout.setNoMoreData(false)
                loadData()
            }
        }
    }

}