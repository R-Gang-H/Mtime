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
 * 卡片大富翁我的道具页面：
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
                        val mMessage = "是否配合使用".toSpan()
                            .append(
                                "1".toSpan().toBold().toColor(color = getColor(R.color.color_12c7e9))
                            )
                            .append("张")
                            .append("恶魔卡".toSpan().toColor(color = getColor(R.color.color_12c7e9)))
                        val dialogData = CommDialog.Data(
                            message = mMessage,
                            title = "使用恶魔卡",
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
                    //使用成功后,刷新页面
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
     * 刷新页面(各个道具卡的数量)
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
        //设置背景圆角
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
                //奴隶卡(3)
                mFriend = data?.getParcelableExtra(KEY_FRIENDS)
                hasDemonCard()
            }
            FRIEND_RESULT_CODE_101 -> {
                //打劫卡(9)
                mFriend = data?.getParcelableExtra(KEY_FRIENDS)
                mCard = data?.getParcelableExtra(KEY_CARD_MONOPOLY_CARD_ID)
                useRobCard()
            }
            FRIEND_RESULT_CODE_102 -> {
                //黑客卡(5)
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
     * 更新用户信息
     */
    private fun updateUserView(data: UserInfo?) {
        userView?.userInfo = data
    }

    //是否有恶魔卡
    private fun hasDemonCard() {
        mViewModel?.hasDemonCard()
    }

    override fun usePropCard(data: PropCard) {
        mPropCard = data
        when (data.toolId) {
            Constants.TOOlS_CARD_POCKET -> {
                //新增口袋卡,进行跳转
                //口袋卡跳转我的口袋
                mProvider?.startCardMainActivity(
                    context = this@PropActivity,
                    userId = mUserInfo?.userId,
                    tab = CARD_MONOPOLY_MY_POCKET
                )
            }
            Constants.TOOLS_CARD_BAGPOS,
            Constants.TOOLS_CARD_BAG -> {
                // 空位卡(13)，保险箱卡(14): 不能直接使用，跳转保险箱页面
                mProvider?.startCardMainActivity(
                    context = this@PropActivity,
                    userId = mUserInfo?.userId,
                    tab = CARD_MONOPOLY_MY_COFFER
                )
            }
            //奴隶卡(3)
            Constants.TOOlS_CARD_SLAVE -> {
                mProvider?.startCardFriendForResult(
                    this@PropActivity,
                    FRIEND_RESULT_CODE_100,
                    Constants.CARD_FRIEND_TOOLS_SLAVE
                )
            }
            //打劫限量卡(16)
            Constants.TOOlS_CARD_ROB_LIMIT -> {
                mProvider?.startCardFriendForResult(
                    this@PropActivity,
                    FRIEND_RESULT_CODE_101,
                    Constants.CARD_FRIEND_TOOLS_ROB_LIMIT
                )
            }
            //打劫卡(5)
            Constants.TOOlS_CARD_ROB -> {
                mProvider?.startCardFriendForResult(
                    this@PropActivity,
                    FRIEND_RESULT_CODE_101,
                    Constants.CARD_FRIEND_TOOLS_ROB
                )
            }
            //黑客卡(9)
            Constants.TOOlS_CARD_HACKER -> {
                mProvider?.startCardFriendForResult(
                    this@PropActivity,
                    FRIEND_RESULT_CODE_102,
                    Constants.CARD_FRIEND_TOOLS_HACK
                )
            }
            //拆套卡(15)
            Constants.TOOLS_CARD_DISMANTLE -> {
                showSearchCardDialog(isCancelable = true, dismiss = null, event = {
                    mCard = Card(cardId = it.suit?.suitId ?: 0L)
                    useDismantleCard()
                })
            }
            //复制卡(8)
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
                // 恶魔卡(10): 不能直接使用
            }
            else -> {
                usePropCard()
            }
        }
    }

    /**
     * 使用打劫卡(5): 对好友使用
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
     * 使用奴隶卡(3)/黑客卡(9): 对好友使用
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
     * 使用复制卡(8):
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
     * 使用拆套卡(16):
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
     * 其他对自己使用道具卡，1、2、4、6、7、11、15、18
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
     * 如果是命中黑客卡,直接使用黑客封面
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