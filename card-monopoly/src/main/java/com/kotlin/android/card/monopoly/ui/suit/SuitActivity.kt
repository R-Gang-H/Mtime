package com.kotlin.android.card.monopoly.ui.suit

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.text.TextUtils
import androidx.activity.viewModels
import androidx.core.graphics.drawable.DrawableCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.data.entity.monopoly.*
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.card_monopoly.ICardMonopolyProvider
import com.kotlin.android.card.monopoly.KEY_CARD_MONOPOLY_USER_INFO
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.databinding.ActSuitBinding
import com.kotlin.android.card.monopoly.ext.showFunctionMenuDialog
import com.kotlin.android.card.monopoly.ext.showSearchCardDialog
import com.kotlin.android.card.monopoly.ext.showSuitDetailDialog
import com.kotlin.android.card.monopoly.ui.CardMonopolyApiViewModel
import com.kotlin.android.card.monopoly.widget.*
import com.kotlin.android.card.monopoly.widget.MenuView.Style
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.core.*
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.user.UserManager
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.titlebar.State
import com.kotlin.android.widget.titlebar.ThemeStyle
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.act_main.*
import kotlinx.android.synthetic.main.act_suit.*
import kotlinx.android.synthetic.main.act_suit.mainLayout
import kotlinx.android.synthetic.main.act_suit.multiStateView
import kotlinx.android.synthetic.main.act_suit.refreshLayout
import kotlinx.android.synthetic.main.act_suit.titleBar
import kotlinx.android.synthetic.main.act_wishing.*
import kotlinx.android.synthetic.main.view_user_label.view.*

/**
 * 卡片大富翁套装列表页面：
 *
 * Created on 2020/9/7.
 *
 * @author o.s
 */
@SuppressLint("UseCompatLoadingForDrawables")
@Route(path = RouterActivityPath.CardMonopoly.PAGER_SUIT)
class SuitActivity : BaseVMActivity<CardMonopolyApiViewModel, ActSuitBinding>(), MultiStateView.MultiStateListener, OnRefreshLoadMoreListener {
    private val mProvider by lazy {
        getProvider(ICardMonopolyProvider::class.java)
    }

    private val firstLabelDrawable by lazy {
        getDrawable(R.drawable.ic_label_normal_card_tips)?.apply {
            setBounds(0, 0, labelWidth, labelHeight)
        }
    }

    private val secondLabelDrawable by lazy {
        getDrawable(R.drawable.ic_label_limit_card_tips)?.apply {
            setBounds(0, 0, labelWidth, labelHeight)
        }
    }

    private val closeDrawable by lazy {
        getDrawable(R.drawable.ic_title_bar_center_end_close)?.apply {
            setBounds(0, 0, closeWidth, closeHeight)
        }
    }

    private val rightArrowDrawable by lazy {
        getDrawable(R.drawable.ic_issued_arrow_right)?.let {
            it.setBounds(0, 0, 18.dp, 18.dp)
            DrawableCompat.wrap(it).apply {
                DrawableCompat.setTintList(this, ColorStateList.valueOf(getColor(R.color.color_1d2736)))
            }
        }
    }

    private val edgeBottom = 10.dp
    private val labelWidth = 13.dp
    private val labelHeight = 14.dp
    private val closeWidth = 32.dp
    private val closeHeight = 32.dp
    private var mUserInfo: UserInfo? = null
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
    private var mSuitCategory: SuitCategory? = null
    private var isInitCenterBar: Boolean = false
    private var mCurrentCategoryId: Long = 1L
    private var pageIndexs: Long = 1L
    private var pageSize: Long = 18L

    private var style: Style = Style.SELF
        set(value) {
            field = value
            notifyChange()
        }

    override fun initVM(): CardMonopolyApiViewModel = viewModels<CardMonopolyApiViewModel>().value

    override fun getIntentData(intent: Intent?) {
        intent?.apply {
            mUserInfo = getParcelableExtra(KEY_CARD_MONOPOLY_USER_INFO)
        }
    }

    override fun initView() {
        immersive()
                .transparentStatusBar()
                .statusBarDarkFont(false)
        window.setBackgroundDrawable(null)
        initTitleView()
        initContentView()
        style = if (isSelf()) {
            Style.SELF
        } else {
            Style.FRIEND
        }
        updateUI()
    }

    override fun initNewData() {
        initData()
    }

    override fun initData() {
        refreshLayout?.resetNoMoreData()
        pageIndexs = 1L
        loadSuitList()
    }

    private fun loadSuitList() {
        mUserInfo?.apply {
            if (userId > 0L) {
                mViewModel?.suitList(
                        userId = userId,
                        categoryId = mCurrentCategoryId,
                        pageIndex = pageIndexs,
                        pageSize = pageSize
                )
            }
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
                    updateMixSuitInfoView()
                    updateSuitsView()
                    updateCenterBarView()
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
                    showSearchView(this)
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
            setTitle(getString(R.string.card_monopoly), alwaysShow = true) {

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
        centerBarView?.apply {
            theme = CenterBarView.Theme.LIGHT
            action = {
                refreshLayout?.resetNoMoreData()
                mSuitCategory = it
                mCurrentCategoryId = it.id
                initData()
            }
//            setCurrentItem(0)
        }
        suitsView?.apply {
            action = { suit ->
                suit?.apply {
                    showSuitDetailDialog(
                        data = suit,
                        action = {
                            val mCardData = CardImageDetailBean(card = it)
                            mProvider?.startImageDetailActivity(this@SuitActivity,null,mCardData)
                        },
                    ) {
                        reloadData()
                    }
                }
            }
        }
        suitDetailView?.apply {
            setCompoundDrawables(null, null, rightArrowDrawable, null)
            setOnClickListener {
                val suitType = if ((mSuitCategory?.id ?: 1L) == 4L) {
                    2L
                } else {
                    1L
                }
                mProvider?.startSuitDetailActivity(suitType)
            }
        }

        firstTipsView?.apply {
            setCompoundDrawables(firstLabelDrawable, null, null, null)
        }
        secondTipsView?.apply {
            setCompoundDrawables(secondLabelDrawable, null, null, null)
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
                centerLayout?.visible()
                initData()
            }
        }

    }

    private fun reloadData() {
        mSearchSuit?.apply {
            loadSearchData()
        } ?: initData()
    }

    private fun updateUI() {
        updateSuitsView()
        updateCenterBarView()
    }

    /**
     * 更新，精、简、终、限
     */
    private fun updateCenterBarView() {
        if (isInitCenterBar) {
            return
        }
        centerBarView?.apply {
            mSuits?.categoryInfos?.apply {
                isInitCenterBar = true
                setCategories(this)
                postDelayed({
                    setCurrentItem(0)
                }, 50)
            }
        }
    }

    /**
     * 更新合成套装统计信息
     */
    private fun updateMixSuitInfoView() {
        firstTipsView?.text = mMixSuitInfo?.commonCountDesc.orEmpty()
        secondTipsView?.text = mMixSuitInfo?.limitCountDesc.orEmpty()
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
            loadSearchData()
            centerLayout?.gone()
            searchInputView?.text = suitName.orEmpty()
            mData.clear()
            mData.add(this)
            updateSuitsView()
            refreshLayout?.finishLoadMoreWithNoMoreData()
        }
    }

    /**
     * 根据搜索套装结果加载用户相关的所有套装信息
     */
    private fun loadSearchData() {
        mSearchSuit?.apply {
            mViewModel?.searchSuitList(userId = mUserInfo?.userId ?: 0L, suitId = suitId)
        }
    }

    /**
     * 显示搜索套装
     */
    private fun showSearchView(suitList: SearchSuitList) {
        suitList.suitList?.apply {
            mData.clear()
            mData.addAll(this)
            updateSuitsView()
            refreshLayout?.finishLoadMoreWithNoMoreData()
        }
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

    private fun notifyChange() {
        when (style) {
            Style.SELF -> {
                selfUI()
            }
            Style.FRIEND -> {
                friendUI()
            }
        }
    }

    private fun selfUI() {
        // 显示套装详情
        showSuitDetailView()
    }

    private fun friendUI() {
        // 隐藏套装详情
        showSuitDetailView(false)
    }

    private fun showSuitDetailView(show: Boolean = true) {
        if (show) {
            suitsView?.edgeBottom = edgeBottom
            suitDetailView?.visible()
        } else {
            suitsView?.edgeBottom = edgeBottom
            suitDetailView?.gone()
        }
    }

    /**
     * 判断是否自己
     */
    private fun isSelf(): Boolean {
        return mUserInfo?.run {
            userId <= 0L || UserManager.instance.userId == userId
        } ?: true
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
                initData()
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {

    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        loadSuitList()
    }
}