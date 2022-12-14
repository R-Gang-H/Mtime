package com.kotlin.android.card.monopoly.ui.prop

import android.content.Intent
import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.data.annotation.CARD_MONOPOLY_MY_COFFER
import com.kotlin.android.app.data.annotation.CARD_MONOPOLY_MY_POCKET
import com.kotlin.android.app.data.entity.monopoly.*
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.card_monopoly.ICardMonopolyProvider
import com.kotlin.android.card.monopoly.*
import com.kotlin.android.card.monopoly.adapter.MyPropItemBinder
import com.kotlin.android.card.monopoly.constants.Constants
import com.kotlin.android.card.monopoly.constants.Constants.PROPS_USE_TYPE_FROM
import com.kotlin.android.card.monopoly.databinding.ActPropBinding
import com.kotlin.android.card.monopoly.ext.*
import com.kotlin.android.card.monopoly.ui.CardMonopolyApiViewModel
import com.kotlin.android.card.monopoly.widget.card.view.SelectCardView
import com.kotlin.android.card.monopoly.widget.dialog.CommDialog
import com.kotlin.android.card.monopoly.widget.dialog.PocketCardDialog
import com.kotlin.android.card.monopoly.widget.dialog.UsePropDialog
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.core.Direction
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.span.toBold
import com.kotlin.android.ktx.ext.span.toColor
import com.kotlin.android.ktx.ext.span.toSpan
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.SafeLinearLayoutManager
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.notifyBinderChanged
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.titlebar.State
import com.kotlin.android.widget.titlebar.ThemeStyle
import kotlinx.android.synthetic.main.act_prop.*

/**
 * ????????????????????????????????????
 *
 * Created on 2020/9/7.
 *
 * @author o.s
 */
@Route(path = RouterActivityPath.CardMonopoly.PAGER_PROP)
class PropActivity : BaseVMActivity<CardMonopolyApiViewModel, ActPropBinding>(), MultiStateView.MultiStateListener,
    MyPropItemBinder.IUseProps {
    private val mProvider by lazy {
        getProvider(ICardMonopolyProvider::class.java)
    }

    private val mAdapter: MultiTypeAdapter by lazy {
        createMultiTypeAdapter(recyclerView, SafeLinearLayoutManager(this))
    }
    private var mListData: ArrayList<MyPropItemBinder> = ArrayList()
    private var showDataType = Constants.PROPS_USE_TYPE_0
    private var mPropCards: MyPropCards? = null
        set(value) {
            field = value
            mUserInfo = value?.userInfo
            mPropCardList = value?.toolCardList
        }
    private var mUserInfo: UserInfo? = null
    private var mPropCardList: List<PropCard>? = null
    private var mPropCard: PropCard? = null
    private var mFriend: Friend? = null
    private var mCard: Card? = null

    override fun initVM(): CardMonopolyApiViewModel = viewModels<CardMonopolyApiViewModel>().value

    override fun getIntentData(intent: Intent?) {
        super.getIntentData(intent)
        intent?.apply {
            showDataType = getLongExtra(PROPS_USE_TYPE_FROM, 0L)
        }
    }

    override fun initView() {
        immersive()
            .transparentStatusBar()
            .statusBarDarkFont(false)
        window.setBackgroundDrawable(null)
        mMultiStateView.setMultiStateListener(this)
        initTitleView()
        initContentView()
    }

    override fun initNewData() {
        initData()
    }

    override fun initData() {
        refreshData()
    }

    override fun startObserve() {
        mViewModel?.myPropCardsUiState?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    mPropCards = this
                    showData()
                }

                if (isEmpty) {
                    mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY)
                }
                error?.apply {
                    mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR)
                }
                netError?.apply {
                    mMultiStateView.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                }
            }
        }

        mViewModel?.demonCardUiState?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    if (result) {
                        val mMessage = "??????????????????".toSpan()
                            .append(
                                "1".toSpan().toBold().toColor(color = getColor(R.color.color_12c7e9))
                            )
                            .append("???")
                            .append("?????????".toSpan().toColor(color = getColor(R.color.color_12c7e9)))
                        val dialogData = CommDialog.Data(
                            message = mMessage,
                            title = "???????????????",
                        )
                        showCardMonopolyCommDialog(CommDialog.Style.COMMON_CANCEL, dialogData,
                            close = { useSlaveAndHackerPropCard(false) },
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

        mViewModel?.usePropCardUiState?.observe(this) {
            it?.apply {
//                showProgressDialog(showLoading)

                success?.apply {
                    //???????????????,????????????
                    refreshData()
                    showUsePropDialog(
                        data = UsePropDialog.Data(
                            propCard = mPropCard,
                            isSuccess = bizCode == 1L,
                            message = bizMessage
                        )
                    )
                }
                error?.apply {
                    showToast(this)
                }
                netError?.apply {
                    showToast(this)
                }
            }
        }
    }

    /**
     * ????????????(????????????????????????)
     */
    private fun refreshData() {
        mViewModel?.myPropCards()
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
        userView?.hideBubble()
        mainLayout?.background = getShapeDrawable(
            colorRes = R.color.color_a2edff,
            endColorRes = R.color.color_ffffff
        )
        //??????????????????
        llContainer?.setBackground(
            colorRes = android.R.color.white,
            cornerRadius = 6.dpF,
            direction = Direction.LEFT_TOP or Direction.RIGHT_TOP
        )
    }

    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                refreshData()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            FRIEND_RESULT_CODE_100 -> {
                //?????????(3)
                mFriend = data?.getParcelableExtra(KEY_FRIENDS)
                hasDemonCard()
            }
            FRIEND_RESULT_CODE_101 -> {
                //?????????(9)
                mFriend = data?.getParcelableExtra(KEY_FRIENDS)
                mCard = data?.getParcelableExtra(KEY_CARD_MONOPOLY_CARD_ID)
                useRobCard()
            }
            FRIEND_RESULT_CODE_102 -> {
                //?????????(5)
                mFriend = data?.getParcelableExtra(KEY_FRIENDS)
                hasDemonCard()
            }
        }
    }

    private fun showData() {
        updateUserView(mPropCards?.userInfo)
        mPropCards?.userInfo?.apply {
            notifyBinderChanged(
                adapter = mAdapter,
                binderList = mListData,
                dataList = mPropCards?.toolCardList,
                isSame = { binder, data ->
                    binder.data.toolId == data.toolId
                },
                syncDataAndNotify = { binder, data ->
                    val notify = binder.data.remainCount != data.remainCount
                    if (notify) {
                        binder.data.remainCount = data.remainCount
                    }
                    notify
                },
                createBinder = {
                    MyPropItemBinder(it, this).apply {
                        usePropsListener = this@PropActivity
                    }
                }
            )
        }
    }

    /**
     * ??????????????????
     */
    private fun updateUserView(data: UserInfo?) {
        userView?.userInfo = data
    }

    //??????????????????
    private fun hasDemonCard() {
        mViewModel?.hasDemonCard()
    }

    override fun usePropCard(data: PropCard) {
        mPropCard = data
        when (data.toolId) {
            Constants.TOOlS_CARD_POCKET -> {
                //???????????????,????????????
                //???????????????????????????
                mProvider?.startCardMainActivity(
                    context = this@PropActivity,
                    userId = mUserInfo?.userId,
                    tab = CARD_MONOPOLY_MY_POCKET
                )
            }
            Constants.TOOLS_CARD_BAGPOS,
            Constants.TOOLS_CARD_BAG -> {
                // ?????????(13)???????????????(14): ??????????????????????????????????????????
                mProvider?.startCardMainActivity(
                    context = this@PropActivity,
                    userId = mUserInfo?.userId,
                    tab = CARD_MONOPOLY_MY_COFFER
                )
            }
            //?????????(3)
            Constants.TOOlS_CARD_SLAVE -> {
                mProvider?.startCardFriendForResult(
                    this@PropActivity,
                    FRIEND_RESULT_CODE_100,
                    Constants.CARD_FRIEND_TOOLS_SLAVE
                )
            }
            //???????????????(16)
            Constants.TOOlS_CARD_ROB_LIMIT -> {
                mProvider?.startCardFriendForResult(
                    this@PropActivity,
                    FRIEND_RESULT_CODE_101,
                    Constants.CARD_FRIEND_TOOLS_ROB_LIMIT
                )
            }
            //?????????(5)
            Constants.TOOlS_CARD_ROB -> {
                mProvider?.startCardFriendForResult(
                    this@PropActivity,
                    FRIEND_RESULT_CODE_101,
                    Constants.CARD_FRIEND_TOOLS_ROB
                )
            }
            //?????????(9)
            Constants.TOOlS_CARD_HACKER -> {
                mProvider?.startCardFriendForResult(
                    this@PropActivity,
                    FRIEND_RESULT_CODE_102,
                    Constants.CARD_FRIEND_TOOLS_HACK
                )
            }
            //?????????(15)
            Constants.TOOLS_CARD_DISMANTLE -> {
                showSearchCardDialog(isCancelable = true, dismiss = null, event = {
                    mCard = Card(cardId = it.suit?.suitId ?: 0L)
                    useDismantleCard()
                })
            }
            //?????????(8)
            Constants.TOOlS_CARD_COPY -> {
                showPocketCardDialog(
                    style = PocketCardDialog.Style.PROP_CARD,
                    selectModel = SelectCardView.SelectModel.SINGLE,
                    event = {
                        mCard = it?.firstOrNull()
                        useCopyCard()
                    })
            }
            Constants.TOOlS_CARD_DEMON -> {
                // ?????????(10): ??????????????????
            }
            else -> {
                usePropCard()
            }
        }
    }

    /**
     * ???????????????(5): ???????????????
     */
    private fun useRobCard() {
        mPropCard?.apply {
            mFriend?.apply {
                mViewModel?.usePropCard(
                    cardToolId = mPropCard?.toolId ?: 0L,
                    targetUserId = userId,
                    targetCardId = mCard?.cardId
                )
            }
        }
    }

    /**
     * ???????????????(3)/?????????(9): ???????????????
     */
    private fun useSlaveAndHackerPropCard(isDemon: Boolean = false) {
        mPropCard?.apply {
            mFriend?.apply {
                mViewModel?.usePropCard(
                    cardToolId = mPropCard?.toolId ?: 0L,
                    targetUserId = userId,
                    useDemonCard = isDemon
                )
            }
        }
    }

    /**
     * ???????????????(8):
     */
    private fun useCopyCard() {
        mPropCard?.apply {
            mViewModel?.usePropCard(
                cardToolId = mPropCard?.toolId ?: 0L,
                targetUserId = mUserInfo?.userId ?: 0L,
                targetToolId = mCard?.cardId
            )
        }
    }

    /**
     * ???????????????(16):
     */
    private fun useDismantleCard() {
        mPropCard?.apply {
            mViewModel?.usePropCard(
                cardToolId = mPropCard?.toolId ?: 0L,
                targetUserId = mUserInfo?.userId ?: 0L,
                targetCardId = mCard?.cardId
            )
        }
    }

    /**
     * ?????????????????????????????????1???2???4???6???7???11???15???18
     */
    private fun usePropCard() {
        mPropCard?.apply {
            mViewModel?.usePropCard(
                cardToolId = mPropCard?.toolId ?: 0L,
                targetUserId = mUserInfo?.userId ?: 0L,
            )
        }
    }

    /**
     * ????????????????????????,????????????????????????
     */
    private fun convertHintCover(myPocketCards: MyPocketCards): ArrayList<Card> {
        if (myPocketCards.hitToolCard?.isHitHackerCard == true) {
            myPocketCards.cardList?.forEach {
                it.cardCover = myPocketCards.hitToolCard?.hackerCardCover
            }
        }
        return myPocketCards.cardList ?: arrayListOf()
    }
}