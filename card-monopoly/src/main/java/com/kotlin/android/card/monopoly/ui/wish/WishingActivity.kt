package com.kotlin.android.card.monopoly.ui.wish

import android.content.Intent
import android.text.TextUtils
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.data.entity.monopoly.*
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.card_monopoly.ICardMonopolyProvider
import com.kotlin.android.card.monopoly.*
import com.kotlin.android.card.monopoly.adapter.MsgBoardAdapter
import com.kotlin.android.card.monopoly.adapter.WishWallAdapter
import com.kotlin.android.card.monopoly.databinding.ActWishingBinding
import com.kotlin.android.card.monopoly.ext.showClearPocketDialog
import com.kotlin.android.card.monopoly.ext.showFunctionMenuDialog
import com.kotlin.android.card.monopoly.ext.showSearchCardDialog
import com.kotlin.android.card.monopoly.provider.startCardMainActivity
import com.kotlin.android.card.monopoly.ui.CardMonopolyApiViewModel
import com.kotlin.android.card.monopoly.widget.MenuView
import com.kotlin.android.card.monopoly.widget.nav.NavView
import com.kotlin.android.card.monopoly.widget.wish.WishTitleView
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.user.UserManager
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.titlebar.State
import com.kotlin.android.widget.titlebar.ThemeStyle
import kotlinx.android.synthetic.main.act_wishing.*

/**
 * 卡片大富翁套装详情页面：
 *
 * Created on 2020/9/7.
 *
 * @author o.s
 */
@Route(path = RouterActivityPath.CardMonopoly.PAGER_WISHING)
class WishingActivity : BaseVMActivity<CardMonopolyApiViewModel, ActWishingBinding>(),
    MultiStateView.MultiStateListener {

    private val mProvider by lazy {
        getProvider(ICardMonopolyProvider::class.java)
    }

    private val mAdapter by lazy {
        MsgBoardAdapter {
            it?.apply {
                startCardMainActivity(this)
            }
        }
    }

    private var mWishWallAdapter: WishWallAdapter? = null
    private val pageSize = 20L
    private var pageIndexs = 1L

    //区分于自己的愿望和列表实现的愿望
    private var wishListFlag = false
    private var wishList: ArrayList<WishInfo> = ArrayList()

    private var mUserInfo: UserInfo? = null
    private var mTab: Int = 0
    private var mMyWish: MyWish? = null
        set(value) {
            field = value
            mWishInfo = value?.wishInfo
        }
    private var mFriendWish: FriendWish? = null
        set(value) {
            field = value
            mWishInfo = value?.wishInfo
        }
    private var mWishInfo: WishInfo? = null
    private var mMyCardsSuit: MyCardsBySuit? = null
    private var mSignatures: SignatureList? = null

    private var mFirstLoadWish: Boolean = true
    private var mFirstLoadMsgBoard: Boolean = true
    private var mFirstLoadWishWall: Boolean = true

    private var wishWallIsEmpty : Boolean = false

    var style: MenuView.Style = MenuView.Style.SELF
        set(value) {
            field = value
            changeStyle()
        }

    override fun initVM(): CardMonopolyApiViewModel = viewModels<CardMonopolyApiViewModel>().value

    override fun getIntentData(intent: Intent?) {
        super.getIntentData(intent)
        intent?.apply {
            mUserInfo = getParcelableExtra(KEY_CARD_MONOPOLY_USER_INFO)
            mTab = getIntExtra(KEY_CARD_MONOPOLY_WISH_TAB, 0)
        }
    }

    override fun initView() {
        window.setBackgroundDrawable(null)
        initTitleView()
        initContentView()
        initWishWallView()
        initMessageBoardView()
        immersive()
            .transparentStatusBar()
            .statusBarDarkFont(false)
    }

    private fun initWishWallView() {
        mWishWallAdapter =
            WishWallAdapter(this@WishingActivity, wishList, ::helpWish, ::jumpPager, ::showImg)
        refreshLayout.setEnableRefresh(true)
        refreshLayout.setEnableLoadMore(true)
        refreshLayout.setOnRefreshListener {
            refreshLayout.finishRefresh()
            mWishWallAdapter?.clearData()
            pageIndexs = 1
            mViewModel?.wishWall(pageIndexs, pageSize)
        }
        refreshLayout.setOnLoadMoreListener {
            refreshLayout.finishLoadMore()
            pageIndexs++
            mViewModel?.wishWall(pageIndexs, pageSize)
        }
        rvWishWall.apply {
            layoutManager = LinearLayoutManager(this@WishingActivity)
            adapter = mWishWallAdapter
        }
    }

    private fun jumpPager(userId: Long) {
        mProvider?.startCardMainActivity(this, userId)
    }

    override fun initNewData() {
        "许愿树 initNewData".e()
        initData()
        loadWish()
        loadWishWallList(true)
    }

    override fun initData() {
        style = if (isSelf()) {
            MenuView.Style.SELF
        } else {
            MenuView.Style.FRIEND
        }
        navView?.selectItem(mTab)
    }

    override fun startObserve() {
        mViewModel?.myWishUiState?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    mMyWish = this
                    updateUI()
                }

                if (isEmpty) {
                    multiStateView?.setViewState(MultiStateView.VIEW_STATE_EMPTY)
                }

                error?.apply {
                    multiStateView?.setViewState(MultiStateView.VIEW_STATE_ERROR)
                }

                netError?.apply {
                    multiStateView?.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                }
            }
        }

        mViewModel?.friendWishUiState?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    mFriendWish = this
                    updateFriendWishTitleView()
                }

                if (isEmpty) {
                    multiStateView?.setViewState(MultiStateView.VIEW_STATE_EMPTY)
                }

                error?.apply {
                    multiStateView?.setViewState(MultiStateView.VIEW_STATE_ERROR)
                }

                netError?.apply {
                    multiStateView?.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                }
            }
        }

        mViewModel?.addOrChangeWishUiState?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    when (bizCode) {
                        1L -> {
                            showToast(bizMessage)
                            loadWish()
                        }
                        else -> {
                            showToast(bizMessage)
                        }
                    }
                }

                error?.apply {
                    showToast(this)
                }

                netError?.apply {
                    showToast(this)
                }
            }
        }

        mViewModel?.pickCardFromWishUiState?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    when (bizCode) {
                        1L -> {
                            showToast(bizMessage)
                            loadWish()
                        }
                        -2L -> {
                            showClearPocketDialog(bizMessage ?: getString(R.string.pocket_is_full))
                        }
                        else -> {
                            showToast(bizMessage)
                        }
                    }
                }
            }
        }

        mViewModel?.wishComeTrueUiState?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    when (bizCode) {
                        1L -> {
                            showToast(bizMessage)
                            if (wishListFlag) {
                                loadWishWallList(true)
                                wishListFlag = false
                            } else {
                                loadWish()
                            }
                        }
                        else -> {
                            showToast(bizMessage)
                        }
                    }
                }
            }
        }

        mViewModel?.myCardsBySuitUiState?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    mMyCardsSuit = this
                    updateMyCardsBySuitView()
                }
            }
        }

        mViewModel?.updateSignatureUiState?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    when (bizCode) {
                        1L -> {
                            showToast(bizMessage)
                            loadSignatureList()
                        }
                        else -> {
                            showToast(bizMessage)
                        }
                    }
                }
            }
        }

        mViewModel?.signatureListUiState?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    mSignatures = this
                    updateMsgBoardView()
                }

                if (isEmpty) {
                    multiStateView?.setViewState(MultiStateView.VIEW_STATE_EMPTY)
                }

                error?.apply {
                    multiStateView?.setViewState(MultiStateView.VIEW_STATE_ERROR)
                }

                netError?.apply {
                    multiStateView?.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                }
            }
        }

        mViewModel?.wishWallUiState?.observe(this) {
            it.apply {
                success?.apply {
                    refreshLayout.setEnableLoadMore(hasMore ?: false)
                    wishWallIsEmpty = false
                    if (wishList?.isNotEmpty() == true) {
                        showWishWall(wishList as ArrayList<WishInfo>)
                    } else {
                        if (pageIndexs == 1L) {
                            setLayoutChange()
                            multiStateView?.setViewState(MultiStateView.VIEW_STATE_EMPTY)
                            wishWallIsEmpty = true
                        } else {
                            --pageIndexs
                        }
                    }
                }
                error?.apply {
                    setLayoutChange()
                    multiStateView?.setViewState(MultiStateView.VIEW_STATE_ERROR)
                }

                netError?.apply {
                    setLayoutChange()
                    multiStateView?.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                }
            }
        }
    }

    private fun showWishWall(list: ArrayList<WishInfo>) {
//        wishList.addAll(list)
        mWishWallAdapter?.setData(list)
    }

    private fun loadWish() {
        when (style) {
            MenuView.Style.SELF -> {
                mViewModel?.myWish()
            }
            MenuView.Style.FRIEND -> {
                mUserInfo?.apply {
                    mViewModel?.friendWish(userId)
                }
            }
        }
    }


    private fun loadSignatureList() {
        mViewModel?.signatureList()
    }

    private fun loadWishWallList(isRefresh: Boolean) {
        if (isRefresh) {
            pageIndexs = 1
            wishList.clear()
        }
        mViewModel?.wishWall(pageIndexs, pageSize)
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
            setTitle(getString(R.string.wishing_tree), alwaysShow = true) {

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
        mainLayout?.background = getShapeDrawable(
            colorRes = R.color.color_a2edff,
            endColorRes = R.color.color_ffffff
        )
        navView?.apply {
            action = {
                when (it) {
                    0 -> {
                        mTab = TAB_MY_WISH
                        setLayoutChange(wish = true)
                        if (mFirstLoadWish) {
                            mFirstLoadWish = false
                            loadWish()
                        }
                    }

                    1 -> {
                        mTab = TAB_WISH_WALL
                        setLayoutChange(wishWall = true)
                        if (mFirstLoadWishWall) {
                            mFirstLoadWishWall = false
                            loadWishWallList(true)
                        }
                    }

                    2 -> {
                        mTab = TAB_MESSAGE_BOARD
                        setLayoutChange(msg = true)
                        if (mFirstLoadMsgBoard) {
                            mFirstLoadMsgBoard = false
                            loadSignatureList()
                        }
                    }
                }
            }
        }

        wishTitleView?.apply {
            action = {
                when (it) {
                    WishTitleView.EventType.SEARCH_CARD -> {
                        showSearchCardDialog(
                            dismiss = {
                                titleBar?.syncStatusBar()
                            }
                        ) { data ->
                            card = data.card
                        }
                    }
                    WishTitleView.EventType.WISH -> {
                        addOrChangeWish()
                    }
                    WishTitleView.EventType.WISHING -> {
                        wishTitleView?.clear()
                        state = WishTitleView.State.CHANGE_WISH
                    }
                    WishTitleView.EventType.CHANGE_WISH -> {
                        addOrChangeWish()
                    }
                    WishTitleView.EventType.WISH_COME_TRUE -> {
                        mViewModel?.pickCardFromWish()
                    }
                    WishTitleView.EventType.HELP_TA -> {
                        mUserInfo?.apply {
                            mViewModel?.wishComeTrue(userId)
                        }
                    }
                }
            }
        }
        lackCardSuitView?.apply {
            action = {
                startCardMainActivity(it)
            }
            dropAction = {
                mViewModel?.myCardBySuit(it.suitId)
            }
        }
    }

    /**
     * 设置布局展示
     */
    private fun setLayoutChange(
        wish: Boolean? = false,
        wishWall: Boolean? = false,
        msg: Boolean? = false
    ) {
        if (wish == true) {
            wishLayout.visible()
        } else {
            wishLayout.gone()
        }
        if (wishWall == true) {
            if(wishWallIsEmpty){
                multiStateView?.setViewState(MultiStateView.VIEW_STATE_EMPTY)
            }else{
                wishWallAllLayout.visible()
            }
        } else {
            wishWallAllLayout.gone()
        }
        if (msg == true) {
            messageBoardLayout.visible()
        } else {
            messageBoardLayout.gone()
        }
    }

    private fun addOrChangeWish() {
        wishTitleView?.apply {
            when {
                !UserManager.instance.hasBindMobile -> {
                    showToast(R.string.hint_please_bind_phone)
                }
                card == null -> {
                    showToast(R.string.hint_please_check_my_card)
                }
                TextUtils.isEmpty(wishText) -> {
                    showToast(R.string.hint_please_input_my_wish)
                }
                else -> {
                    mViewModel?.addOrChangeWish(cardId = card?.cardId ?: 0L, content = wishText)
                }
            }
        }
    }

    private fun initMessageBoardView() {
        msgBoardInputView?.apply {
            setBackground(
                colorRes = R.color.color_f2f3f6,
                cornerRadius = 6.dpF
            )
            addTextChangedListener {
                actionView?.isEnabled = it?.length ?: 0 > 0
            }
        }
        actionView?.apply {
            background = getDrawableStateList(
                normal = getShapeDrawable(
                    colorRes = R.color.color_feb12a,
                    cornerRadius = 18.dpF
                ),
                disable = getShapeDrawable(
                    colorRes = R.color.color_66feb12a,
                    cornerRadius = 18.dpF
                )
            )
            setOnClickListener {
                val message = msgBoardInputView?.text.toString()
                if (!TextUtils.isEmpty(message)) {
                    mViewModel?.updateSignature(message)
                } else {
                    showToast(R.string.hint_please_input_message)
                }
            }
        }
        recyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }
    }

    private fun changeStyle() {
        when (style) {
            MenuView.Style.SELF -> {
                selfStyle()
            }
            MenuView.Style.FRIEND -> {
                friendStyle()
            }
        }
    }

    private fun selfStyle() {
        navView?.apply {
            setItems(
                NavView.Category.MY_WISHING,
                NavView.Category.WISHING,
                NavView.Category.MSG_BOARD
            )
            style = NavView.Style.TRIPLE
        }
    }

    private fun friendStyle() {
        navView?.apply {
            setItems(
                NavView.Category.TA_WISHING,
                NavView.Category.WISHING,
                NavView.Category.MSG_BOARD
            )
            style = NavView.Style.TRIPLE
        }
    }

    private fun updateUI() {
        updateMyWishTitleView()
        updateMyWishView()
    }

    private fun updateMyWishTitleView() {
        wishTitleView?.apply {
            wishInfo = mWishInfo
        }
    }

    private fun updateFriendWishTitleView() {
        wishTitleView?.apply {
            state = WishTitleView.State.HELP_TA
            wishInfo = mWishInfo
        }
        hideMyWishView()
    }

    private fun updateMyWishView() {
        lackCardSuitView?.data = mMyWish
    }

    private fun updateMyCardsBySuitView() {
        lackCardSuitView?.myCardsBySuit = mMyCardsSuit
    }

    private fun hideMyWishView() {
        // 隐藏我缺失的卡片信息视图
        lackCardSuitView?.data = null
    }

    private fun updateMsgBoardView() {
        msgBoardInputView?.apply {
            setText("")
            clearFocus()
        }
        mAdapter.setData(mSignatures?.signatures)
        if (mSignatures?.signatures?.isEmpty() == false) {
            msgBoardLabelView?.visible()
        } else {
            msgBoardLabelView?.gone()
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

    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                when (mTab) {
                    TAB_MY_WISH -> {
                        loadWish()
                        setLayoutChange(wish = true)
                    }
                    TAB_WISH_WALL -> {
                        loadWishWallList(true)
                        setLayoutChange(wishWall = true)
                    }
                    TAB_MESSAGE_BOARD -> {
                        loadSignatureList()
                        setLayoutChange(msg = true)
                    }
                }
            }
        }
    }

    /**
     * 列表页面---实现愿望
     */
    private fun helpWish(friendId: Long) {
        wishListFlag = true
        mViewModel?.wishComeTrue(friendId)
    }

    private fun showImg(data: CardImageDetailBean) {
        mProvider?.startImageDetailActivity(this, null, data)
    }
}