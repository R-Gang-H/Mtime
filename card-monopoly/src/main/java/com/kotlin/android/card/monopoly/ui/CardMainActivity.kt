package com.kotlin.android.card.monopoly.ui

import android.content.Intent
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.annotation.IntRange
import androidx.core.view.isVisible
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.bonus.scene.component.postComposedArmor
import com.kotlin.android.card.monopoly.*
import com.kotlin.android.card.monopoly.constants.Constants
import com.kotlin.android.card.monopoly.constants.Constants.TOOlS_CARD_HACKER
import com.kotlin.android.card.monopoly.constants.Constants.TOOlS_CARD_ROB
import com.kotlin.android.card.monopoly.constants.Constants.TOOlS_CARD_ROB_LIMIT
import com.kotlin.android.card.monopoly.constants.Constants.TOOlS_CARD_SLAVE
import com.kotlin.android.card.monopoly.databinding.ActMainBinding
import com.kotlin.android.card.monopoly.ext.*
import com.kotlin.android.card.monopoly.widget.*
import com.kotlin.android.card.monopoly.widget.user.UserView
import com.kotlin.android.card.monopoly.widget.MenuView.Style
import com.kotlin.android.card.monopoly.widget.box.BoxItemView
import com.kotlin.android.card.monopoly.widget.card.CardView
import com.kotlin.android.card.monopoly.widget.card.image.CardState
import com.kotlin.android.card.monopoly.widget.card.view.CheckCardView
import com.kotlin.android.card.monopoly.widget.card.view.SelectCardView
import com.kotlin.android.card.monopoly.widget.coffer.tab.CofferTabItemView
import com.kotlin.android.card.monopoly.widget.dialog.view.DealCardView
import com.kotlin.android.card.monopoly.widget.dialog.view.LinkTextContentView
import com.kotlin.android.card.monopoly.widget.card.OpenCardView
import com.kotlin.android.card.monopoly.widget.card.adapter.CheckCardAdapter
import com.kotlin.android.card.monopoly.widget.dialog.*
import com.kotlin.android.card.monopoly.widget.dialog.view.OpenBoxView
import com.kotlin.android.card.monopoly.widget.nav.NavView
import com.kotlin.android.card.monopoly.widget.wish.HelpWishView
import com.kotlin.android.comment.component.bean.CommentViewBean
import com.kotlin.android.comment.component.bind.CommentView
import com.kotlin.android.comment.component.bind.binder.CommentItemBinder
import com.kotlin.android.comment.component.bind.binder.CommentTitleBinder
import com.kotlin.android.comment.component.bind.viewmodel.CommentViewModel
import com.kotlin.android.widget.titlebar.State
import com.kotlin.android.widget.titlebar.ThemeStyle
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.app.data.annotation.CARD_MONOPOLY_MY_COFFER
import com.kotlin.android.app.data.annotation.CARD_MONOPOLY_MY_POCKET
import com.kotlin.android.app.data.annotation.CARD_MONOPOLY_UNKNOWN
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.comment.CommentAll
import com.kotlin.android.app.data.entity.comment.PostComment
import com.kotlin.android.app.data.entity.community.content.CommentList
import com.kotlin.android.app.data.entity.monopoly.*
import com.kotlin.android.ktx.ext.core.*
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.dimension.statusBarHeight
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.span.toBold
import com.kotlin.android.ktx.ext.span.toColor
import com.kotlin.android.ktx.ext.span.toSpan
import com.kotlin.android.mtime.ktx.ext.progressdialog.ProgressDialogFragment
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast

import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.card_monopoly.ICardMonopolyProvider
import com.kotlin.android.app.router.provider.comment.ICommentProvider
import com.kotlin.android.app.router.provider.community_person.ICommunityPersonProvider
import com.kotlin.android.app.router.provider.mine.IMineProvider
import com.kotlin.android.user.UserManager
import com.kotlin.android.user.isLogin
import kotlinx.android.synthetic.main.act_main.*
import kotlinx.android.synthetic.main.act_main.currentIssuedView
import kotlinx.android.synthetic.main.act_main.titleBar
import kotlinx.android.synthetic.main.act_main.titleLabel
import kotlinx.android.synthetic.main.view_user_label.view.*

/**
 * ???????????????????????????
 * [style]: [Style.SELF], [Style.FRIEND]
 * ????????????????????????TA????????????????????????????????????????????????????????????
 *
 * Created on 2020/8/6.
 *
 * @author o.s
 */
@Route(path = RouterActivityPath.CardMonopoly.PAGER_CARD_MAIN)
class CardMainActivity : BaseVMActivity<CardMonopolyApiViewModel, ActMainBinding>() {
    private val mProvider by lazy {
        getProvider(ICardMonopolyProvider::class.java)
    }
    private val mineProvider by lazy {
        getProvider(IMineProvider::class.java)
    }
    private val personProvider by lazy {
        getProvider(ICommunityPersonProvider::class.java)
    }
    private val commentProvider by lazy {
        getProvider(ICommentProvider::class.java)
    }
    private val commentViewModel by lazy {
        viewModels<CommentViewModel>().value
    }
    private val mData by lazy { ArrayList<CommentViewBean>() }
    private var mTotalCount: Long = 0
    private var mCommentList: CommentList? = null
        set(value) {
            field = value
            value?.items?.apply {
                mTotalCount = value.totalCount.toLong()
                val unReleased: ArrayList<CommentViewBean> = ArrayList()
                forEach {
                    if (it.releasedState) {
                        mData.add(CommentViewBean.obtain(it))
                    } else {
                        unReleased.add(CommentViewBean.obtain(it))
                    }
                }
                if (unReleased.isNotEmpty()) {
                    mData.addAll(0, unReleased)
                }
            }
        }

    private val selfMarginTop = 360.dp// 535.dp // (-15).dp
    private val otherMarginTop = 260.dp // 170.dp // 385.dp // (-164).dp
    private val otherMarginTop2 = 160.dp // 170.dp // 385.dp // (-164).dp
    private var labelWidth = 15.dp
    private var labelHeight = 16.5F.dp
    private var labelDrawablePadding = 5.dp

    private var mFriendPocket: FriendPocket? = null
        set(value) {
            field = value
            mUserInfo = value?.userInfo
            mBufferInfo = value?.bufferInfo
            mPocketCards = value?.pocketCards
            mOpenPocketCards = value?.openPocketCards
            mWishInfo = value?.wishInfo
            nUnreadMsgCount = 0
        }
    private var mPocket: MyPocket? = null
        set(value) {
            field = value
            mUserInfo = value?.userInfo
            mBufferInfo = value?.bufferInfo
            mPocketCards = value?.pocketCards
            mOpenPocketCards = value?.openPocketCards
            mStrongBoxInfo = value?.strongBoxInfo
            nUnreadMsgCount = value?.unreadMsgCount ?: 0
            mWishInfo = null
        }
    private var mUserInfo: UserInfo? = null
    private var mBufferInfo: BufferInfo? = null
    private var mPocketCards: PocketCards? = null
    private var mOpenPocketCards: PocketCards? = null
    private var mStrongBoxInfo: StrongBoxPositionList? = null
    private var nUnreadMsgCount: Long = 0L
    private var mCurrentIssueCardList: CurrentIssueSuitList? = null
    private var mUserDetail: UserDetail? = null
    private var mWishInfo: WishInfo? = null
    private var mSuitShowList: List<Suit>? = null

    private var mOpenBox: Box? = null

    /**
     * ??????
     */
    private var mDiscardCardId: String? = null
    private var mDiscardFriend: Friend? = null

    /**
     * ???TA??????????????????
     */
    private var mPropCard: Card? = null

    private var style: Style = Style.SELF
        set(value) {
            field = value
            notifyChange()
        }

    /**
     * ??????ID
     */
    private var mUserId: Long = 0
    private var mTab: Int = CARD_MONOPOLY_UNKNOWN

    override fun initVM(): CardMonopolyApiViewModel = viewModels<CardMonopolyApiViewModel>().value

    override fun getIntentData(intent: Intent?) {
        super.getIntentData(intent)
        intent?.apply {
            mTab = getIntExtra(KEY_CARD_MONOPOLY_MAIN_TAB, CARD_MONOPOLY_UNKNOWN)
            mUserId = getLongExtra(KEY_CARD_MONOPOLY_USER_ID, 0L)
        }
    }

    override fun initView() {
//        mBinding?.setVariable(BR.vm, mViewModel)
        immersive()
                .transparentStatusBar()
                .statusBarDarkFont(false)
        initTitleView()
        initContentView()
    }

    override fun initNewData() {
        initData()
    }

    override fun initData() {
        style = if (isSelf()) {
            Style.SELF
        } else {
            Style.FRIEND
        }
        scrollToBetter()
    }

    /**
     * ?????????????????????
     */
    private fun scrollToBetter() {
        if (Style.SELF == style) {
            when (mTab) {
                CARD_MONOPOLY_MY_POCKET -> {
                    cardNavView?.selectItem(0)
                }
                CARD_MONOPOLY_MY_COFFER -> {
                    cardNavView?.selectItem(1)
                }
                else -> {
                    cardNavView?.selectItem(0)
                }
            }
        }
    }

    /**
     * ??????????????????????????????
     */
    private fun clearPocket() {
        if (Style.SELF != style) {
            style = Style.SELF
            mUserId = 0
            loadData()
        }
        mTab = CARD_MONOPOLY_MY_POCKET
        scrollToBetter()
    }

    override fun onStart() {
        super.onStart()
        loadData()
    }

    private fun loadData() {
        when (style) {
            Style.SELF -> {
                mViewModel?.myPocket()
            }
            Style.FRIEND -> {
                mViewModel?.friendPocket(friendId = mUserId)
            }
        }
        loadCommentList()
    }

    fun loadCommentList() {
        // ??????????????????
        mData.clear()
        val isNew = commentView?.isNew ?: false
        val log = if(isNew) "??????" else "??????"
        "loadCommentList: $log".e()
        val objId = if (isSelf()) {
            mUserInfo?.userId ?: UserManager.instance.userId
        } else {
            mUserInfo?.userId ?: mUserId
        }
        if (isLogin()) {
            commentViewModel.loadCommentList(
                isNewComment = isNew,
                isReleased = false,
                postComment = PostComment(
                    objType = CommConstant.PRAISE_OBJ_TYPE_CARD_USER,
                    objId = objId,
                    pageIndex = 1L,
                    pageSize = 3L
                )
            )
        }
        commentViewModel.loadCommentList(
            isNewComment = isNew,
            isReleased = true,
            postComment = PostComment(
                objType = CommConstant.PRAISE_OBJ_TYPE_CARD_USER,
                objId = objId,
                pageIndex = 1L,
                pageSize = 3
            )
        )
    }

    override fun startObserve() {
        // ???????????????
        commentViewModel.uiState.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    mCommentList = this
                    updateCommentView()
                }
            }
        }
        commentViewModel.deleteCommentUiState.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    loadCommentList()
                }
            }
        }

        commentViewModel.praiseUpUiState.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)

                success?.apply {
                }
            }
        }

        mViewModel?.apply {
            friendPocketUiState.observe(this@CardMainActivity) {
                it?.apply {
                    showProgressDialog(showLoading)

                    success?.apply {
                        mPocket = null
                        mFriendPocket = this
                        updateUI()
                    }

                    error?.apply {
                        showToast(this)
                    }

                    netError?.apply {
                        showToast(this)
                    }
                }
            }

            myPocketUiState.observe(this@CardMainActivity) {
                it?.apply {
                    showProgressDialog(showLoading)

                    success?.apply {
                        mFriendPocket = null
                        mPocket = this
                        updateUI()
                    }

                    error?.apply {
                        showToast(this)
                    }

                    netError?.apply {
                        showToast(this)
                    }
                }
            }

            wishComeTrueUiState.observe(this@CardMainActivity) {
                it?.apply {
                    showProgressDialog(showLoading)

                    success?.apply {
                        when (bizCode) {
                            1L -> {
                                showToast(bizMessage)
                            }
                            else -> {
                                showToast(bizMessage)
                            }
                        }
                    }
                }
            }

            digBoxUiState.observe(this@CardMainActivity) {
                it?.apply {
                    showProgressDialog(showLoading)

                    success?.apply {
                        when (bizCode) {
                            1L -> {
                                boxView?.apply {
                                    newCardBox?.apply {
//                                        if (cardBoxId == LIMIT_BOX_ID) {
//                                            showDigBoxView(this)
//                                        }
                                        updateCardBox(this)
                                    }
                                }
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

            openBoxUiState.observe(this@CardMainActivity) {
                it?.apply {
                    showProgressDialog(showLoading)

                    success?.apply {
                        when (bizCode) {
                            1L -> {
                                boxView?.apply {
                                    rewardInfo?.apply {
                                        position = mOpenBox?.position ?: 0
                                        if (mOpenBox?.cardBoxId == LIMIT_BOX_ID) {
                                            showOpenBoxAnimView(this)
                                        } else {
                                            openBoxView?.apply {
                                                show()
                                                data = OpenBoxView.Data(box = mOpenBox, info = rewardInfo)
                                            }
                                        }
                                    }
                                }
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

            pickCardFromMeUiState.observe(this@CardMainActivity) {
                it?.apply {
                    showProgressDialog(showLoading)

                    success?.apply {
                        when (bizCode) {
                            1L -> {
                                mPocketCards = pocketCards
                                mOpenPocketCards = openPocketCards
                                updatePocketView()
                                updateOpenPocketView()
                                showToast(bizMessage)
                            }
                            -3L -> {
                                showClearPocketDialog(
                                        message = bizMessage ?: getString(R.string.pocket_is_full),
                                        isCardMainPage = true
                                ) {
                                    clearPocket()
                                }
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

            pickCardFromFriendUiState.observe(this@CardMainActivity) {
                it?.apply {
                    showProgressDialog(showLoading)

                    success?.apply {
                        when (bizCode) {
                            1L -> {
                                mOpenPocketCards = openPocketCards
                                updateOpenPocketView()
                                showToast(bizMessage)
                            }
                            -4L -> {
                                showClearPocketDialog(
                                        message = bizMessage ?: getString(R.string.pocket_is_full),
                                        isCardMainPage = true
                                ) {
                                    clearPocket()
                                }
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

            activeStrongBoxPositionUiState.observe(this@CardMainActivity) {
                it?.apply {
                    showProgressDialog(showLoading)

                    success?.apply {
                        when (bizCode) {
                            1L -> {
                                showToast(bizMessage)
                                loadData()
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

            unlockStrongBoxUiState.observe(this@CardMainActivity) {
                it?.apply {
                    showProgressDialog(showLoading)

                    success?.apply {
                        when (bizCode) {
                            1L -> {
                                showToast(bizMessage)
                                loadData()
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

            mixSuitUiState.observe(this@CardMainActivity) {
                it?.apply {
                    showProgressDialog(isShow = it.showLoading)

                    success?.apply {
                        when (bizCode) {
                            1L -> {
                                showSuitComposeDialog(
                                        data = this,
                                        close = {
                                            loadData()
                                            postComposedArmor()
                                        }
                                ) {
                                    // ????????????????????????
                                    mUserInfo?.apply {
                                        mProvider?.startSuitActivity(this)
                                        postComposedArmor()
                                    }
                                }
                            }
                            -5L -> {
                                showCardMonopolyCommDialog(CommDialog.Style.SYNTHESIS_FAILED, CommDialog.Data(message = getString(R.string.five_cards_can_mix)))
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

            moveCardToStrongBoxUiState.observe(this@CardMainActivity) {
                it?.apply {
                    showProgressDialog(isShow = it.showLoading)

                    success?.apply {
                        when (bizCode) {
                            1L -> {
                                showToast(bizMessage)
                                mPocketCards = pocketCards
                                mStrongBoxInfo = strongBoxInfo
                                updatePocketView()
                                updateStrongBoxView()
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

            moveCardToPocketUiState.observe(this@CardMainActivity) {
                it?.apply {
                    showProgressDialog(isShow = it.showLoading)

                    success?.apply {
                        when (bizCode) {
                            1L -> {
                                showToast(bizMessage)
                                mPocketCards = pocketCards
                                mStrongBoxInfo = strongBoxInfo
                                updatePocketView()
                                updateStrongBoxView()
                            }
                            -3L -> {
                                showClearPocketDialog(
                                        message = bizMessage ?: getString(R.string.pocket_is_full),
                                        isCardMainPage = true
                                ) {
                                    clearPocket()
                                }
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

            discardUiState.observe(this@CardMainActivity) {
                it?.apply {
                    showProgressDialog(isShow = it.showLoading)

                    success?.apply {
                        when (bizCode) {
                            1L -> {
                                val name = if (Style.SELF == style) {
                                    mDiscardFriend?.nickName
                                } else {
                                    mUserInfo?.nickName
                                }
//                                val message = getString(R.string.discard_success_).toSpan()
//                                        .append(
//                                                name.toSpan().toBold().toColor(color = getColor(R.color.color_20a0da))
//                                        ).append(
//                                                getString(R.string._it_pocket)
//                                        )
//                                showCardMonopolyCommDialog(CommDialog.Style.DISCARD_SUCCESS, CommDialog.Data(message = message))
                                showToast(R.string.discard_succcess)
                                if (style == Style.SELF) {
                                    mPocketCards = pocketCards
                                    updatePocketView()
                                } else {
                                    mOpenPocketCards = openPocketCards
                                    updateOpenPocketView()
                                }
                            }
                            -5L -> {
                                val name = if (Style.SELF == style) {
                                    mDiscardFriend?.nickName
                                } else {
                                    mUserInfo?.nickName
                                }
                                val message = getString(R.string.sorry_).toSpan()
                                        .append(
                                                name.toSpan().toBold().toColor(color = getColor(R.color.color_20a0da))
                                        ).append(
                                                getString(R.string._pocket_no_position)
                                        )
                                showCardMonopolyCommDialog(CommDialog.Style.DISCARD_FAILED, CommDialog.Data(message = message))
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

            addFriendUiState.observe(this@CardMainActivity) {
                it?.apply {
                    showProgressDialog(
                            isShow = showLoading,
                            behavior = ProgressDialogFragment.Behavior.SINGLE
                    )

                    success?.apply {
                        when (bizCode) {
                            1L -> {
                                showToast(bizMessage)
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

            demonCardUiState.observe(this@CardMainActivity) {
                it?.apply {
                    showProgressDialog(showLoading)

                    success?.apply {
                        success?.apply {
                            if (result) {
                                val mMessage = "??????????????????".toSpan()
                                        .append("1".toSpan().toBold().toColor(color = getColor(R.color.color_12c7e9)))
                                        .append("???")
                                        .append("?????????".toSpan().toColor(color = getColor(R.color.color_12c7e9)))
                                val dialogData = CommDialog.Data(
                                        message = mMessage,
                                        title = "???????????????",
                                )
                                showCardMonopolyCommDialog(CommDialog.Style.COMMON_CANCEL,dialogData,
                                        close = {useSlaveAndHackerPropCard(false)},
                                        event = {
                                            useSlaveAndHackerPropCard(true)
                                        })
                            } else {
                                useSlaveAndHackerPropCard()
                            }
                        }

                        error?.apply {
                            useSlaveAndHackerPropCard()
                        }

                        netError?.apply {
                            useSlaveAndHackerPropCard()
                        }
                    }
                }
            }

            usePropCardUiState.observe(this@CardMainActivity) {
                it?.apply {
                    showProgressDialog(showLoading)

                    success?.apply {
                        showUsePropDialog(
                                data = UsePropDialog.Data(
                                        card = mPropCard ?: Card(cardCover = toolCover),
                                        isSuccess = bizCode == 1L,
                                        message = bizMessage)
                        )
                        loadData()
                    }

                    error?.apply {
                        showToast(this)
                    }

                    netError?.apply {
                        showToast(this)
                    }
                }
            }

            drawBoxModelUiState.observe(this@CardMainActivity) {
                it?.apply {
                    showProgressDialog(showLoading)

                    success?.apply {
                        when (bizCode) {
                            1L -> {
                                // ???????????????????????????????????????
                                pocketCards?.apply {
                                    mPocketCards = this
                                    updatePocketView()
                                    mViewModel?.myPocket()
                                }
                                showToast(bizMessage)
                                openBoxView?.hide()
                            }
                            -4L,-6L -> {
                                showClearPocketDialog(
                                        message = bizMessage ?: getString(R.string.pocket_is_full),
                                        isCardMainPage = true
                                ) {
                                    openBoxView?.hide()
                                    clearPocket()
                                }
                            }
                            else -> {
                                showToast(bizMessage)
                            }
                        }
                    }

                    error?.apply {
                        openBoxView?.hide()
                    }

                    netError?.apply {
                        openBoxView?.hide()
                    }
                }
            }

            userDetailUiState.observe(this@CardMainActivity) {
                it?.apply {
                    showProgressDialog(showLoading)

                    success?.apply {
                        mUserDetail = this
                        showCardUserDetailView()
                    }
                }
            }

            currentIssueSuitListUiState.observe(this@CardMainActivity) {
                it?.apply {
                    showProgressDialog(showLoading)

                    success?.apply {
                        mCurrentIssueCardList = this
                        showCurrentIssueView()
                    }
                }
            }

            suitShowUiState.observe(this@CardMainActivity) {
                it?.apply {
                    showProgressDialog(showLoading)

                    success?.apply {
                        showToast(bizMessage)
                        when (bizCode) {
                            1L -> {
                                mSuitShowList = suitShowList
                                updateSuitShowView()
                            }
                            else -> {

                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * ????????????????????????
     */
    private fun updateSuitShowView() {
        cardUserDetailView?.suitShow = mSuitShowList
    }

    override fun onDestroy() {
        super.onDestroy()
        boxView?.cancel()
        openBoxAnimaView?.recycle()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == FRIEND_RESULT_CODE) {
            data?.getParcelableExtra<Friend>(KEY_FRIENDS)?.apply {
                mDiscardFriend = this
                mDiscardCardId?.apply {
                    mViewModel?.discard(this, friendId = userId, isRobot)
                }
            }
        }
    }

    private fun initTitleView() {
        titleBar?.apply {
            setThemeStyle(ThemeStyle.IMMERSIVE)
            setState(State.REVERSE)
            target(scrollView)
            addItem(
                drawableRes = R.drawable.ic_title_bar_back_light,
                reverseDrawableRes = R.drawable.ic_title_bar_back_dark
            ) {
                finish()
            }
            setTitle(getString(R.string.card_monopoly)) {

            }
            addItem(
                drawableRes = R.drawable.ic_title_bar_more_light,
                reverseDrawableRes = R.drawable.ic_title_bar_more_dark,
                isReversed = true
            ) {
                showFunctionMenuDialog(
                        dismiss = {
                            syncStatusBar()
                        }
                )
            }
            addItem(
                drawableRes = R.drawable.ic_title_bar_message_light,
                reverseDrawableRes = R.drawable.ic_title_bar_message_dark,
                isReversed = true
            ) {
                mProvider?.startDealRecordsActivity()
            }
        }
    }

    private fun initContentView() {
        mainLayout?.background = getGradientDrawable(
                color = getColor(R.color.color_a2edff),
                endColor = getColor(R.color.color_ffffff)
        )
        titleLabel?.apply {
            (layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = statusBarHeight
        }
        helpWishView?.apply {
            isVisible = false
            action = {
                when (it) {
                    HelpWishView.ActionEvent.WISH -> {
                        mProvider?.startWishingActivity(mUserInfo)
                    }
                    HelpWishView.ActionEvent.HELP -> {
                        mProvider?.startWishingActivity(mUserInfo)
//                        mUserInfo?.apply {
//                            mViewModel?.wishComeTrue(userId)
//                        }
                    }
                }
            }
        }
        userView?.apply {
            action = { event ->
                when (event) {
                    UserView.ActionEvent.AVATAR -> {
                        mViewModel?.userDetail(mUserInfo?.userId ?: mUserId)
                    }
                    UserView.ActionEvent.COIN -> {
                        mineProvider?.startMemberCenterActivity(context)
                    }
                    UserView.ActionEvent.SUIT -> {
                        mUserInfo?.apply {
                            mProvider?.startSuitActivity(this) // UserInfo(userId = 100049)
                        }
                    }
                    UserView.ActionEvent.FRIEND -> {
                        mProvider?.startCardFriend(Constants.CARD_FRIEND_CARD_MAIN)
                    }
                    UserView.ActionEvent.ADD_FRIEND -> {
                        addFriend()
                    }
                    UserView.ActionEvent.PROPS -> {
                        mProvider?.startPropActivity(0)
                    }
                    UserView.ActionEvent.USE_PROPS -> {
                        usePropsOnTA()
                    }
                    UserView.ActionEvent.BUFF -> {
                        showBuffDetailView()
                    }
                    UserView.ActionEvent.CURRENT_ISSUE -> {
                        mViewModel?.currentIssueSuitList()
                    }
                }
            }
        }
        cardStoreLayout?.setBackground(
                colorRes = android.R.color.white,
                cornerRadius = 6.dpF,
                direction = Direction.LEFT_TOP or Direction.RIGHT_TOP
        )
        cardTALayout?.setBackground(
                colorRes = android.R.color.white,
                cornerRadius = 6.dpF,
                direction = Direction.LEFT_TOP or Direction.RIGHT_TOP
        )
        boxView?.apply {
            emptyState = {
//                digBoxState = if (it) {
//                    DigBoxState.EMPTY_POSITION
//                } else {
//                    DigBoxState.FULL
//                }
            }
            action = {
                when (it.state) {
                    BoxItemView.State.DIG_BOX -> {
                        mViewModel?.digBox()
                    }
                    BoxItemView.State.READY -> {
                        mOpenBox = it.box
                        mOpenBox?.position?.toLong()?.apply {
                            mViewModel?.openBox(position = this)
                        }
                    }
                    BoxItemView.State.COUNT_DOWN -> {
                        // ??????????????????
                        playScanAnim(it.position)
                        // ????????????
                        openNowBox(it.box)
                    }
                    BoxItemView.State.EMPTY -> {
                        // nothing
                    }
                }
            }
        }
        openCardView?.apply {
            spec = CheckCardView.Spec.TRANSFER
            action = {
                when (it.type) {
                    OpenCardView.ActionType.PICKUP -> {
                        // ??????/??????????????????????????????
                        pickCard(it.selectedCards)
                    }
                    OpenCardView.ActionType.DISCARD -> {
                        // ?????????TA
                        discard()
                    }
                }
            }
        }
        cardView?.apply {
            actionItem = {
                // ????????????
                unlockPocket(it)
            }
            action = {
                it.apply {
                    when (type) {
                        CardView.ActionType.COMPOSE -> {
                            // ??????
                            mixSuit(selectedCards)
                        }
                        CardView.ActionType.DISCARD -> {
                            mDiscardCardId = selectedCards.userCardIds()
                            if (!mDiscardCardId.isNullOrEmpty()) {
                                // ??????????????????????????????????????????????????????
                                mProvider?.startCardFriendForResult(this@CardMainActivity, FRIEND_RESULT_CODE, Constants.CARD_FRIEND_CARD_DISCARD)
                            }
                        }
                        CardView.ActionType.SAVE_COFFER -> {
                            mViewModel?.moveCardToStrongBox(selectedCards.userCardIds())
                        }
                        else -> {
                        }
                    }
                }
            }
        }
        cardTAView?.apply {
            action = {
                it.apply {
                    when (type) {
                        CardView.ActionType.DEAL -> {
                            dealCard(selectedCards)
                        }
                        else -> {
                        }
                    }
                }
            }
        }
        cofferView?.apply {
            action = {
                mViewModel?.moveCardToPocket(it.userCardIds())
            }
            actionTab = {
                if (CofferTabItemView.ActionType.OPEN_NOW == it.type) {
                    val position = it.position
                    val message = "??????".toSpan().append(
                            "${getUnlockPositionCardCount(position)}".toSpan().toBold().toColor(color = getColor(R.color.color_feb12a))
                    ).append(
                            "???"
                    ).append(
                            "?????????".toSpan().toBold().toColor(color = getColor(R.color.color_feb12a))
                    ).append(
                            "??????????????????"
                    )
                    showCardMonopolyCommDialog(
                            style = CommDialog.Style.UNLOCK_COFFER_POSITION,
                            data = CommDialog.Data(message = message)
                    ) {
                        mViewModel?.activeStrongBoxPosition(position)
                    }
                }
            }
            actionItem = {
                // ???????????????
                unlockStrongBox(it)
            }
        }

        openBoxView?.apply {
            action = {
                val ids = it?.ids { cardId }.orEmpty()
                if (ids.isNotEmpty()) {
                    data?.box?.apply {
                        mViewModel?.drawBox(position, ids)
                    }
                } else {
                    showCardMonopolyCommDialog(
                            style = CommDialog.Style.DRAW_BOX,
                            data = CommDialog.Data(message = getString(R.string.warning_not_pick_card)),
                            close = {
                                show()
                            }
                    ) {
                        data?.box?.apply {
                            mViewModel?.drawBox(position, "")
                        }
                    }
                }
            }
        }

        commentView?.apply {
            actionTitle = {
                when (it.view) {
                    it.holder.binding.newTv -> {
                        newComment(commentView = this, holder = it.holder)
                    }
                    it.holder.binding.hotTv -> {
                        hotComment(commentView = this, holder = it.holder)
                    }
                    else -> {}
                }
            }
            actionItem = {
                when (it.view) {
                    it.holder.binding.userPicIv,
                    it.holder.binding.userNameTv,
                    it.holder.binding.firstUserNameTv,
                    it.holder.binding.secondUserNameTv -> {
                        gotoUserPage(it.holder)
                    }
                    it.holder.binding.commentRootCL -> {
                        gotoCommentDetailPage(it.holder)
                    }
                    it.holder.binding.likeTv -> {
                        praiseUp(it.holder)
                    }
                    it.holder.binding.deleteTv -> {
                        deleteComment(commentView = this, holder = it.holder)
                    }
                    else -> {}
                }
            }
        }
    }

    /**
     * ????????????
     */
    private fun dealCard(selectedCards: ArrayList<Card>) {
        showPocketCardDialog { cards ->
            cards?.firstOrNull()?.apply {
                selectedCards.firstOrNull()?.let { target ->
                    showDealDialog(
                        DealCardView.Data(
                            exchangeType = DealCardView.EXCHANGE_CARD,
                            userInfo = mUserInfo,
                            srcCard = this,
                            desCard = target,
                        )
                    ) { bizCode ->
                        if (bizCode == 1L) {
                            val message = "???????????????".toSpan()
                                .append(
                                    mUserInfo?.nickName.orEmpty().toSpan()
                                        .toBold()
                                        .toColor(color = com.kotlin.android.mtime.ktx.getColor(R.color.color_20a0da))
                                ).append(
                                    "??????????????????"
                                )
                            showCardMonopolyCommDialog(
                                style = CommDialog.Style.LAUNCH_DEAL_SUCCESS,
                                data = CommDialog.Data(
                                    message = message
                                )
                            ) { data ->
                                data?.actionEvent?.apply {
                                    when (this) {
                                        LinkTextContentView.ActionEvent.MY_POCKET -> {
                                            clearPocket()
//                                            style = Style.SELF
//                                            mUserId = 0
//                                            cardNavView?.selectItem(0)
//                                            loadData()
                                        }
                                        else -> {
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * ????????????
     */
    private fun mixSuit(selectedCards: ArrayList<Card>) {
        if (selectedCards.isNotEmpty()) {
            val ids = selectedCards.ids { userCardId ?: 0 }
            mViewModel?.mixSuit(ids)
        }
    }

    /**
     * ????????????
     */
    private fun discard() {
        showPocketCardDialog(
            data = PocketCardDialog.Data(
                friendId = mUserId,
                isRobot = false
            ),
            selectModel = SelectCardView.SelectModel.MULTIPART
        ) { cards ->
            cards?.apply {
                mViewModel?.discard(userCardIds(), mUserId, false)
            }
        }
    }

    /**
     * ????????????
     */
    private fun pickCard(selectedCards: ArrayList<Card>) {
        val ids = selectedCards.ids { userCardId ?: 0 }
        when (style) {
            Style.SELF -> {
                mViewModel?.batchPickCardFromMe(ids)
            }
            Style.FRIEND -> {
                mViewModel?.batchPickCardFromFriend(ids, mUserId, false)
            }
        }
    }

    /**
     * ??????????????????
     */
    private fun openNowBox(box: Box?) {
        val gold = box?.openGold?.toString().toSpan()
            .toColor(color = getColor(R.color.color_feb12a))
        val message = "?????? ".toSpan()
            .append(gold)
            .append(" ????????????????????????")
        showCardMonopolyCommDialog(
            style = CommDialog.Style.COMMON_CANCEL,
            data = CommDialog.Data(title = "????????????", message = message),
            close = {
                box?.apply {
                    boxView?.playScanAnim(position = position, hide = true)
                }
            },
        ) {
            mOpenBox = box
            mOpenBox?.position?.toLong()?.apply {
                mViewModel?.openBox(position = this, openWithGold = true)
            }
        }
    }

    private fun notifyChange() {
        updateUI()
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
        titleBar?.updateVisibility(index = 0, isReversed = true, isShow = true)
        helpWishView?.gone()
        boxView?.visible()
        selfLabelUI()
        cardLayout.marginTop = selfMarginTop
        cardStoreLayout?.visible()
        cardTALayout?.gone()
        cardNavView?.apply {
            setItems(NavView.Category.CARD, NavView.Category.COFFER)
            action = {
                when (it) {
                    0 -> {
                        // ??????
                        cardView?.visible()
                        cofferView?.gone()
                    }
                    1 -> {
                        // ?????????
                        cardView?.gone()
                        cofferView?.visible()
                    }
                }
            }
        }
        openCardView?.apply {
            title = getString(R.string.a_card_from_my_friend)
            showDiscardActionView(false)
        }
    }

    private fun friendUI() {
        titleBar?.updateVisibility(index = 0, isReversed = true, isShow = false)
        helpWishView?.visible()
        boxView?.gone()
        taLabelUI()
        cardLayout.marginTop = otherMarginTop2
        cardStoreLayout?.gone()
        cardTANavView?.apply {
            setItems(NavView.Category.TA_CARD)
            selectItem(0)
        }
        cardTALayout?.visible()
        openCardView?.apply {
            title = getString(R.string.card_lost_to_ta_by_card_mate)
            showDiscardActionView()
        }
    }

    private fun selfLabelUI() {
        selfLabelLayout?.visible()
        cardStoreLabelView?.apply {
            setBackground(
                    colorRes = R.color.color_ffffff,
                    cornerRadius = 8.dpF
            )
            compoundDrawablePadding = labelDrawablePadding
            setDrawable(DrawableTextView.LEFT, getDrawable(R.drawable.ic_label_card_shop), labelWidth, labelHeight)
            setOnClickListener {
                mProvider?.startStoreActivity()
            }
        }
        auctionLabelView?.apply {
            setBackground(
                    colorRes = R.color.color_ffffff,
                    cornerRadius = 8.dpF
            )
            compoundDrawablePadding = labelDrawablePadding
            setDrawable(DrawableTextView.LEFT, getDrawable(R.drawable.ic_label_main_auction), labelWidth, labelHeight)
            setOnClickListener {
                mProvider?.startAuctionActivity()
            }
        }
        wishTreeLabelView?.apply {
            setBackground(
                    colorRes = R.color.color_ffffff,
                    cornerRadius = 8.dpF
            )
            compoundDrawablePadding = labelDrawablePadding
            setDrawable(DrawableTextView.LEFT, getDrawable(R.drawable.ic_label_wish_tree), labelWidth, labelHeight)
            setOnClickListener {
                mProvider?.startWishingActivity()
            }
        }
    }

    private fun taLabelUI() {
        selfLabelLayout?.gone()
    }

    /**
     * ??????UI
     */
    private fun updateUI() {
        updateTitleBar()
        updateUserView()
        updateWishView()
        updateBoxView()
        updatePocketView()
        updateStrongBoxView()
        updateOpenPocketView()
        updateCommentView()
    }

    /**
     * ??????????????????
     */
    private fun updateWishView() {
        if (!isSelf()) {
            helpWishView?.apply {
                if (mWishInfo == null) {
                    isVisible = false
                    cardLayout.marginTop = otherMarginTop2
                } else {
                    isVisible = true
                    cardLayout.marginTop = otherMarginTop
                }
                data = mWishInfo
            }
        }
    }

    /**
     * ??????TitleBar????????????
     */
    private fun updateTitleBar() {
        titleBar?.updateRedPoint(index = 0, isReversed = true, isShow = nUnreadMsgCount > 0)
    }

    /**
     * ??????????????????
     */
    private fun updateUserView() {
        userView?.apply {
            userInfo = mUserInfo
            bufferInfo = mBufferInfo
            if (style == Style.FRIEND) {
                friendRelation = mFriendPocket?.friendRelation
            }
        }
        mFriendPocket?.apply {
            if (style == Style.FRIEND && scampGold > 0) {
                showCardMonopolyCommDialog(
                        data = CommDialog.Data(
                                title = "???????????????",
                                message = "???????????????".toSpan()
                                        .append(
                                                scampGold.toString().toSpan().toColor(color = getColor(R.color.color_feb12a))
                                        ).append(
                                                "??????"
                                        )
                        )
                )
            }
        }
    }

    /**
     * ??????????????????
     */
    private fun updateCommentView() {
        commentView?.apply {
            val data = mData.filterIndexed { index, _ -> index < 3 }
            setTitle(mTotalCount)
            setAll(
                CommentAll(
                    objId = mUserInfo?.userId ?: mUserId,
                    objTitle = "${mUserInfo?.nickName}????????????",
                    type = CommConstant.PRAISE_OBJ_TYPE_CARD_USER
                )
            )
            setData(data)
        }
    }

    /**
     * ??????????????????????????????????????????
     */
    private fun updateBoxView() {
        boxView?.apply {
            data = mPocket?.cardBoxList
        }
    }

    /**
     * ????????????
     */
    private fun updatePocketView() {
        cardView?.apply {
            limit = mPocketCards?.pocketCount?.toInt()
            data = mPocketCards?.cardList
        }
        cardTAView?.apply {
            limit = mPocketCards?.pocketCount?.toInt()
            data = mPocketCards?.cardList
        }
    }

    /**
     * ???????????????
     */
    private fun updateStrongBoxView() {
        cofferView?.apply {
            mStrongBoxInfo?.apply {
                data = this
            }
        }
    }

    /**
     * ??????????????????
     */
    private fun updateOpenPocketView() {
        openCardView?.apply {
            data = mOpenPocketCards?.cardList
        }
    }

    /**
     * ????????????????????????
     * ???????????????????????????
     */
    private fun showDigBoxView(box: Box) {
        digLimitBoxView?.apply {
            visible()
            data = box
        }
    }

    private fun showCurrentIssueView() {
        currentIssuedView?.apply {
            visible()
            data = mCurrentIssueCardList
        }
    }

    private fun showBuffDetailView() {
        buffDetailView?.apply {
            data = mBufferInfo
            showAt(userView?.bufferView)
            visible()
        }
    }

    private fun showCardUserDetailView() {
        cardUserDetailView?.apply {
            show()
            data = mUserDetail
            action = {
                mViewModel?.suitShow(
                    cardUserSuitId = it.suit?.cardUserSuitId ?: 0L,
                    suitId = it.newSuit?.suitId ?: 0L
                )
            }
        }
    }

    /**
     * ????????????
     */
    private fun addFriend() {
        mUserInfo?.apply {
            showCardMonopolyCommDialog(
                    style = CommDialog.Style.ADD_FRIEND,
                    data = CommDialog.Data(
                            avatarUrl = avatarUrl,
                            nickName = nickName
                    )
            ) {
                it?.apply {
                    mViewModel?.addFriend(userId, postscript.toString())
                }
            }
        }
    }

    /**
     * ???TA????????????
     */
    private fun usePropsOnTA() {
        showPocketCardDialog(
                style = PocketCardDialog.Style.USE_PROP_CARD
        ) { cards ->
            mPropCard = cards?.firstOrNull()
            usePropCard()
        }
    }

    /**
     * ???TA???????????????
     */
    private fun usePropCard() {
        mPropCard?.apply {
            when (cardId) {
                TOOlS_CARD_ROB -> {
                    userView?.postDelayed({
                        showPocketCardDialog(
                                data = PocketCardDialog.Data(friendId = mUserInfo?.userId
                                        ?: mUserId),
                                style = PocketCardDialog.Style.TA_CARD
                        ) { cards ->
                            cards?.firstOrNull()?.let {
                                useRobCard(it)
                            }
                        }
                    }, 450)
                }
                TOOlS_CARD_SLAVE,
                TOOlS_CARD_HACKER -> {
                    mViewModel?.hasDemonCard()
                }
                TOOlS_CARD_ROB_LIMIT -> {
                    userView?.postDelayed({
                        showPocketCardDialog(
                            data = PocketCardDialog.Data(friendId = mUserInfo?.userId
                                ?: mUserId),
                            style = PocketCardDialog.Style.TA_LIMIT_CARD
                        ) { cards ->
                            cards?.firstOrNull()?.let {
                                useRobCard(it)
                            }
                        }
                    }, 450)
                }
                else -> {
                }
            }
        }
    }

    /**
     * ???????????????(5): ???????????????
     */
    private fun useRobCard(card: Card) {
        mPropCard?.apply {
            mViewModel?.usePropCard(
                    cardToolId = cardId,
                    targetUserId = mUserInfo?.userId ?: mUserId,
                    targetCardId = card.cardId
            )
        }
    }

    /**
     * ???????????????(3)/?????????(9): ???????????????
     */
    private fun useSlaveAndHackerPropCard(isDemon: Boolean = false) {
        mPropCard?.apply {
            mViewModel?.usePropCard(
                    cardToolId = cardId,
                    targetUserId = mUserInfo?.userId ?: mUserId,
                    useDemonCard = isDemon
            )
        }
    }

    /**
     * ??????????????????
     */
    private fun isSelf(): Boolean {
        return mUserId <= 0L || UserManager.instance.userId == mUserId
    }

    /**
     * ????????????
     */
    private fun newComment(commentView: CommentView, holder: CommentTitleBinder.Holder) {
        holder.apply {
            data?.isNew = true
            commentView.isNew = true
            notifyItemChanged()
        }
        loadCommentList()
    }

    /**
     * ????????????
     */
    private fun hotComment(commentView: CommentView, holder: CommentTitleBinder.Holder) {
        holder.apply {
            data?.isNew = false
            commentView.isNew = false
            notifyItemChanged()
        }
        loadCommentList()
    }

    /**
     * ??????????????????
     */
    private fun gotoUserPage(holder: CommentItemBinder.Holder) {
        holder.data?.apply {
            personProvider?.startPerson(userId = userId)
        }
    }

    /**
     * ??????????????????
     */
    private fun gotoCommentDetailPage(holder: CommentItemBinder.Holder) {
        holder.data?.apply {
            commentProvider?.startComment(
                objType = type,
                commentId = commentId,
            )
        }
    }

    /**
     * ??????/????????????
     */
    private fun praiseUp(holder: CommentItemBinder.Holder) {
        holder.apply {
            data?.apply {
                // action ?????? 1.?????? 2.????????????
                val action = if (userPraised == 1L) {
                    userPraised = null
                    likeCount--
                    2L
                } else {
                    userPraised = 1L
                    likeCount++
                    1L
                }
                commentViewModel.praiseUp(
                    action = action,
                    objType = CommConstant.getPraiseUpType(type, CommConstant.COMMENT_PRAISE_ACTION_COMMENT),
                    objId = commentId
                )
            }
            notifyItemChanged()
        }
    }

    /**
     * ????????????
     */
    private fun deleteComment(commentView: CommentView, holder: CommentItemBinder.Holder) {
        holder.apply {
            com.kotlin.android.widget.dialog.showDialog(
                context = this@CardMainActivity,
                content = com.kotlin.android.comment.component.R.string.comment_is_delete_comment
            ) {
                data?.apply {
                    commentViewModel.deleteComment(
                        objType = type,
                        commentId = commentId,
                    )
                    val position = holder.mPosition
                    if (position > -1) {
                        commentView.notifyItemRemoved(position)
                    }
                }
            }
        }
    }

    /**
     * ????????????
     */
    private fun unlockPocket(event: CheckCardAdapter.ActionEvent) {
        if (!isSelf()) {
            return
        }
        when (event.state) {
            CardState.LOCK -> {
                val message = "??????".toSpan().append(
                    "1".toSpan().toBold().toColor(color = getColor(R.color.color_feb12a))
                ).append(
                    "???"
                ).append(
                    "?????????".toSpan().toBold().toColor(color = getColor(R.color.color_feb12a))
                ).append(
                    "??????????????????"
                )
                showCardMonopolyCommDialog(
                    style = CommDialog.Style.COMMON_CANCEL,
                    data = CommDialog.Data(
                        title = "????????????",
                        message = message
                    )
                ) {
                    mViewModel?.usePropCard(
                        cardToolId = 17L, // ?????????ID = 17
                        targetUserId = mUserInfo?.userId ?: mUserId,
                    )
                }
            }
            else -> {
            }
        }
    }

    /**
     * ???????????????
     */
    private fun unlockStrongBox(event: CheckCardAdapter.ActionEvent) {
        when (event.state) {
            CardState.LOCK -> {
                val position = event.position
                val message = "??????".toSpan().append(
                    "1".toSpan().toBold().toColor(color = getColor(R.color.color_feb12a))
                ).append(
                    "???"
                ).append(
                    "????????????".toSpan().toBold().toColor(color = getColor(R.color.color_feb12a))
                ).append(
                    "??????????????????"
                )
                showCardMonopolyCommDialog(
                    style = CommDialog.Style.UNLOCK_COFFER,
                    data = CommDialog.Data(message = message)
                ) {
                    mViewModel?.unlockStrongBox(position)
                }
            }
            else -> {
            }
        }
    }

    /**
     * ????????????????????????
     */
    fun showOpenBoxAnimView(info: RewardInfo) {
        openBoxAnimaView?.apply {
            complete = {
                openBoxView?.apply {
                    show()
                    data = it
                }
            }
            data = OpenBoxView.Data(box = mOpenBox, info = info)
        }
    }

    /**
     * ??????????????????
     */
    private fun getUnlockPositionCardCount(@IntRange(from = 1, to = 10) position: Int): Int {
        return when (position) {
            1 -> 0
            2 -> 5
            3 -> 10
            4 -> 20
            5 -> 40
            6 -> 80
            7 -> 150
            8 -> 200
            9 -> 250
            10 -> 300
            else -> 0
        }
    }
}