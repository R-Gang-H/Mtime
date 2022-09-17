package com.kotlin.android.card.monopoly.ui.suit.detail

import android.content.Intent
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.card.monopoly.KEY_CARD_MONOPOLY_SUIT_TYPE
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.adapter.SuitDetailItemBinder
import com.kotlin.android.card.monopoly.databinding.ActSuitDetailBinding
import com.kotlin.android.card.monopoly.databinding.ItemSuitDetailBinding
import com.kotlin.android.card.monopoly.ext.showFunctionMenuDialog
import com.kotlin.android.card.monopoly.ui.CardMonopolyApiViewModel
import com.kotlin.android.card.monopoly.widget.nav.NavView
import com.kotlin.android.widget.titlebar.State
import com.kotlin.android.widget.titlebar.ThemeStyle
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.app.data.entity.monopoly.CardImageDetailBean
import com.kotlin.android.app.data.entity.monopoly.SuitList
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog

import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.card_monopoly.ICardMonopolyProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.act_suit_detail.*
import kotlinx.android.synthetic.main.act_suit_detail.mMultiStateView
import kotlinx.android.synthetic.main.act_suit_detail.mRefreshLayout
import kotlinx.android.synthetic.main.act_suit_detail.navView
import kotlinx.android.synthetic.main.act_suit_detail.titleBar

/**
 * 卡片大富翁套装详情页面：
 *
 * Created on 2020/9/7.
 *
 * @author o.s
 */
@Route(path = RouterActivityPath.CardMonopoly.PAGER_SUIT_DETAIL)
class SuitDetailActivity : BaseVMActivity<CardMonopolyApiViewModel, ActSuitDetailBinding>(), OnRefreshLoadMoreListener,
        MultiStateView.MultiStateListener {

    private val mProvider by lazy {
        getProvider(ICardMonopolyProvider::class.java)
    }

    private lateinit var mAdapter: MultiTypeAdapter
    private var mListData: ArrayList<MultiTypeBinder<ItemSuitDetailBinding>> = ArrayList()

    private var pageIndexs: Long = 1
    private var pageSize: Long = 10
    private var suitType: Long = 1L

    override fun initVM(): CardMonopolyApiViewModel = viewModels<CardMonopolyApiViewModel>().value

    override fun getIntentData(intent: Intent?) {
        super.getIntentData(intent)
        intent?.apply {
            suitType = getLongExtra(KEY_CARD_MONOPOLY_SUIT_TYPE, 1L)
        }
    }
    override fun initView() {
        immersive()
                .transparentStatusBar()
                .statusBarDarkFont(false)
        window.setBackgroundDrawable(null)
        mMultiStateView.setMultiStateListener(this)
        mRefreshLayout.setOnRefreshLoadMoreListener(this)
        initTitleView()
        initContentView()
    }

    override fun initNewData() {
        initData()
    }

    override fun initData() {
        val position = if (suitType == 2L) {
            1
        } else {
            0
        }
        navView?.selectItem(position)
    }

    override fun startObserve() {
        mViewModel?.allSuitListUiState?.observe(this) {
            it?.apply {
                if (pageIndexs == 1L) {
                    showProgressDialog(showLoading)
                }

                success?.apply {
                    showData(this)
                }

                if (loadMore) {
                    if (pageIndexs == 1L) {
                        mRefreshLayout.resetNoMoreData()
                    }
                    pageIndexs++
                    mRefreshLayout.finishLoadMore(true)
                }

                if (noMoreData) {
                    mRefreshLayout.finishLoadMoreWithNoMoreData()
                }

                if (isEmpty) {
                    mRefreshLayout.finishLoadMore(true)
                    if (pageIndexs == 1L) {
                        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY)
                    }
                }

                error?.apply {
                    mRefreshLayout.finishLoadMore(false)
                    if (pageIndexs == 1L) {
                        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR)
                    }
                }

                netError?.apply {
                    mRefreshLayout.finishLoadMore(false)
                    if (pageIndexs == 1L) {
                        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                    }
                }
            }
        }
    }

    private fun showData(suits: SuitList) {
        suits.suitList?.forEach {
            it.type = suitType
            mListData.add(SuitDetailItemBinder(it,::jumpMainPage,::jumpCardImgDetail,::jumpSuitComment))
        }
        mAdapter.notifyAdapterAdded(mListData as List<MultiTypeBinder<*>>)
    }

    private fun jumpSuitComment(suitId:Long,suitName:String){
        mProvider?.startSuitCommentActivity(11, suitId,"${suitName}套装的评论")
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
            setTitle(getString(R.string.suit_detail), alwaysShow = true) {

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
        }
    }

    private fun initContentView() {
        mainLayout?.background = getShapeDrawable(
            colorRes = R.color.color_a2edff,
            endColorRes = R.color.color_ffffff
        )
        navView?.apply {
            setItems(NavView.Category.SUIT, NavView.Category.LIMIT)
            style = NavView.Style.DOUBLE
            action = {
                suitType = if (it == 0) {
                    1
                } else {
                    2
                }
                pageIndexs = 1
                mAdapter.notifyAdapterClear()
                mListData.clear()
                mViewModel?.allSuitList(suitType, pageIndexs, pageSize)
            }
        }
        mAdapter = createMultiTypeAdapter(rvSuitDetail, LinearLayoutManager(this))
    }

    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                pageIndexs = 1
                mAdapter.notifyAdapterClear()
                mListData.clear()
                mViewModel?.allSuitList(suitType, pageIndexs, pageSize)
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {

    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mViewModel?.allSuitList(suitType, pageIndexs, pageSize)
    }

    private fun jumpMainPage(userId: Long) {
        mProvider?.startCardMainActivity(this, userId = userId)
    }

    private fun jumpCardImgDetail(mData:CardImageDetailBean){
        mProvider?.startImageDetailActivity(mActivity = this,data = mData,image = null)
    }

}