package com.kotlin.android.card.monopoly.ui.suit

import android.annotation.SuppressLint
import android.content.Intent
import android.text.TextUtils
import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.data.entity.monopoly.*
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.card.monopoly.KEY_CARD_MONOPOLY_SUIT_C
import com.kotlin.android.card.monopoly.KEY_CARD_MONOPOLY_SUIT_FROM
import com.kotlin.android.card.monopoly.KEY_CARD_MONOPOLY_USER_ID
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.databinding.ActSuitSelectedBinding
import com.kotlin.android.card.monopoly.event.EventSelectedSuit
import com.kotlin.android.card.monopoly.ext.showFunctionMenuDialog
import com.kotlin.android.card.monopoly.ext.showSearchCardDialog
import com.kotlin.android.card.monopoly.ui.CardMonopolyApiViewModel
import com.kotlin.android.card.monopoly.widget.*
import com.kotlin.android.card.monopoly.widget.nav.NavView
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.core.Direction
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.router.bus.ext.post
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.titlebar.State
import com.kotlin.android.widget.titlebar.ThemeStyle
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.act_suit_selected.*

/**
 * 卡片大富翁套装选择列表页面：
 *
 * Created on 2020/9/7.
 *
 * @author o.s
 */
@SuppressLint("UseCompatLoadingForDrawables")
@Route(path = RouterActivityPath.CardMonopoly.PAGER_SUIT_SELECTED)
class SuitSelectedActivity : BaseVMActivity<CardMonopolyApiViewModel, ActSuitSelectedBinding>(), MultiStateView.MultiStateListener, OnRefreshLoadMoreListener {

    companion object {
        const val SUIT_STATE_C_LIMIT = "14"
        const val SUIT_STATE_LIMIT = "4"
    }
    private var mUserId: Long = 0L
    private var mSuitC: String? = null
    private var mSuitFrom: Int = 0 // 选择套装来源
    private var mMixSuitInfo: MixSuitInfo? = null
    private var mSuits: SuitList? = null
        set(value) {
            field = value
            if (pageIndexs == 1L) {
                mData.clear()
                value?.mixSuitCount?.apply {
                    mMixSuitInfo = this
                }
            }
            value?.suitList?.apply {
                mData.addAll(this)
            }
        }
    private val mData by lazy { ArrayList<Suit>() }
    private var mSearchSuit: Suit? = null
        set(value) {
            field = value
            isSearchLimit = value?.suitType == 3L
        }
    private var mSearchSuitList: SearchSuitList? = null
    private var mCurrentCategoryId: Long = 1L
    private var pageIndexs: Long = 1L
    private var pageSize: Long = 18L
    private var isSearchLimit = false
    private val isCategoryLimit
        get() = mCurrentCategoryId == 4L

    override fun initVM(): CardMonopolyApiViewModel = viewModels<CardMonopolyApiViewModel>().value

    override fun getIntentData(intent: Intent?) {
        intent?.apply {
            mUserId = getLongExtra(KEY_CARD_MONOPOLY_USER_ID, 0L)
            mSuitC = getStringExtra(KEY_CARD_MONOPOLY_SUIT_C)
            mSuitFrom = getIntExtra(KEY_CARD_MONOPOLY_SUIT_FROM, 0)
        }
    }

    override fun initView() {
        immersive()
                .transparentStatusBar()
                .statusBarDarkFont(false)
        window.setBackgroundDrawable(null)
        initTitleView()
        initNavView()
        initContentView()
        updateUI()
    }

    override fun initNewData() {
        loadData()
    }

    override fun initData() {

    }

    private fun loadData() {
        refreshLayout?.resetNoMoreData()
        pageIndexs = 1L
        loadSuitList()
    }

    private fun loadSuitList() {
        if (mUserId > 0L) {
            mViewModel?.suitList(
                userId = mUserId,
                categoryId = mCurrentCategoryId,
                pageIndex = pageIndexs,
                pageSize = pageSize
            )
        }
    }

    override fun startObserve() {
        mViewModel?.suitListUiState?.observe(this) {
            it?.apply {
                if (pageIndexs == 1L) {
                    showProgressDialog(showLoading)
                }

                success?.apply {
                    mSuits = this
                    updateSuitsView()
                    loadFirstPageScrollToTop()
                }

                if (loadMore) {
                    pageIndexs++
                    refreshLayout?.finishLoadMore(true)
                }

                if (noMoreData) {
                    refreshLayout?.finishLoadMoreWithNoMoreData()
                }

                if (isEmpty) {
                    refreshLayout?.finishLoadMore(true)
                    if (pageIndexs == 1L) {
                        multiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY)
                    }
                }

                error?.apply {
                    refreshLayout?.finishLoadMore(false)
                    if (pageIndexs == 1L) {
                        multiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR)
                    }
                }

                netError?.apply {
                    refreshLayout?.finishLoadMore(false)
                    if (pageIndexs == 1L) {
                        multiStateView.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                    }
                }
            }
        }
        mViewModel?.searchSuitListUiState?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    mSearchSuitList = this
                    showSearchView()
                }
            }
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
            setTitle(getString(R.string.chose_suit), alwaysShow = true) {

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

    private fun initNavView() {
        when (mSuitC) {
            SUIT_STATE_C_LIMIT -> {
                cardNavView?.apply {
                    setItems(NavView.Category.CARD_C, NavView.Category.CARD_S)
                    action = {

                        when (it) {
                            0 -> {
                                // 简装
                                mCurrentCategoryId = 1L
                            }
                            1 -> {
                                // 限量
                                mCurrentCategoryId = 4L
                            }
                        }
                        if (mSearchSuit == null) {
                            refreshLayout?.resetNoMoreData()
                            loadData()
                        } else {
                            showSearchView()
                        }
                    }
                    selectItem(0)
                }
            }
            SUIT_STATE_LIMIT -> {
                cardNavView?.apply {
                    setItems(NavView.Category.CARD_S)
                    action = {
                        when (it) {
                            0 -> {
                                // 限量
                                mCurrentCategoryId = 4L
                            }
                        }
                        if (mSearchSuit == null) {
                            refreshLayout?.resetNoMoreData()
                            loadData()
                        } else {
                            showSearchView()
                        }
                    }
                    selectItem(0)
                }
            }
        }
    }

    private fun initContentView() {
        multiStateView?.setMultiStateListener(this)
        refreshLayout?.setOnRefreshLoadMoreListener(this)
        mainLayout?.background = getShapeDrawable(
            colorRes = R.color.color_a2edff,
            endColorRes = R.color.color_ffffff
        )
        suitLayout?.setBackground(
                colorRes = android.R.color.white,
                cornerRadius = 6.dpF,
                direction = Direction.LEFT_TOP or Direction.RIGHT_TOP
        )

        suitsView?.apply {
            action = { suit ->
                suit?.apply {
                    EventSelectedSuit(mSuitFrom, suit).post()
                    finish()
//                    showSuitDetailDialog(
//                        data = suit,
//                        action = {
//                            val mCardData = CardImageDetailBean(card = it)
//                            mProvider?.startImageDetailActivity(this@SuitSelectedActivity,null,mCardData)
//                        },
//                    ) {
//                        reloadData()
//                    }
                }
            }
        }

        searchInputView?.apply {
            setBackground(
                colorRes = R.color.color_f4f6f8,
                cornerRadius = 16.dpF
            )
            setOnClickListener {
                showSearchCardDialog(
                    dismiss = {
                        titleBar?.syncStatusBar()
                    }
                ) { data ->
                    mSearchSuit = data.suit
                    handleSearchData()
                }
            }
        }
        searchCancelView?.setOnClickListener {
            mSearchSuit = null
            val text = searchInputView?.text
            if (!TextUtils.isEmpty(text)) {
                searchInputView?.text = ""
//                centerLayout?.visible()
                loadData()
            }
        }

    }

    private fun reloadData() {
        mSearchSuit?.apply {
            loadSearchData()
        } ?: loadData()
    }

    private fun updateUI() {
        updateSuitsView()
    }

    /**
     * 更新套装列表
     */
    private fun updateSuitsView() {
        suitsView?.apply {
            data = mData
        }
    }

    /**
     * 处理搜索结果
     */
    private fun handleSearchData() {
        mSearchSuit?.apply {
            when (mSuitC) {
                SUIT_STATE_C_LIMIT -> {
                    if (isSearchLimit) {
                        loadSearchData()
                        cardNavView.selectItem(1)
                    } else {
                        loadSearchData()
                        cardNavView.selectItem(0)
                    }
                }
                SUIT_STATE_LIMIT -> {
                    if (isSearchLimit) {
                        loadSearchData()
                        cardNavView.selectItem(0)
                    }
                }
            }

//            loadSearchData()
//            centerLayout?.gone()
            searchInputView?.text = suitName.orEmpty()
            mData.clear()
//            mData.add(this)
            updateSuitsView()
            refreshLayout?.finishLoadMoreWithNoMoreData()
        }
    }

    /**
     * 根据搜索套装结果加载用户相关的所有套装信息
     */
    private fun loadSearchData() {
        mSearchSuit?.apply {
            mViewModel?.searchSuitList(userId = mUserId, suitId = suitId)
        }
    }

    /**
     * 显示搜索套装
     */
    private fun showSearchView() {
        mData.clear()
        when (mSuitC) {
            SUIT_STATE_C_LIMIT -> {
                if (isSearchLimit) {
                    if (isCategoryLimit) {
                        mSearchSuitList?.suitList?.apply {
                            mData.addAll(this)
                        }
                    }
                } else {
                    if (!isCategoryLimit) {
                        mSearchSuitList?.suitList?.forEach {
                            if (it.suitClass.equals("C", true)) {
                                mData.add(it)
                            }
                        }
                    }
                }
            }
            SUIT_STATE_LIMIT -> {
                if (isSearchLimit) {
                    if (isCategoryLimit) {
                        mSearchSuitList?.suitList?.apply {
                            mData.addAll(this)
                        }
                    }
                }
            }
        }

        updateSuitsView()
        refreshLayout?.finishLoadMoreWithNoMoreData()
    }

    /**
     * 加载首页并滚动到顶部
     */
    private fun loadFirstPageScrollToTop() {
        if (pageIndexs == 1L) {
            if (mData.isNotEmpty()) {
                suitsView.scrollToPosition(0)
            }
        }
    }

    /**
     * Callback for when the [ViewState] has changed
     *
     * @param viewState The [ViewState] that was switched to
     */
    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                suitsView?.data = null
                loadData()
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {

    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        loadSuitList()
    }
}