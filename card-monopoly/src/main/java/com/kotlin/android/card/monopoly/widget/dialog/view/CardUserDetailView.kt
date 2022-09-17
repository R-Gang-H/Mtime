package com.kotlin.android.card.monopoly.widget.dialog.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.event.EventSelectedSuit
import com.kotlin.android.card.monopoly.ext.setUserInfo
import com.kotlin.android.card.monopoly.widget.search.SelectSuitView
import com.kotlin.android.app.data.entity.monopoly.Suit
import com.kotlin.android.app.data.entity.monopoly.UserDetail
import com.kotlin.android.ktx.ext.core.*
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.span.toBold
import com.kotlin.android.ktx.ext.span.toColor
import com.kotlin.android.ktx.ext.span.toSpan
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList
import com.kotlin.android.router.bus.ext.observe

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.card_monopoly.ICardMonopolyProvider
import com.kotlin.android.app.router.provider.home.IHomeProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.user.UserManager
import kotlinx.android.synthetic.main.view_card_user_detail.view.*
import kotlinx.android.synthetic.main.view_card_user_detail.view.avatarView
import kotlinx.android.synthetic.main.view_card_user_detail.view.onlineView
import kotlinx.android.synthetic.main.view_card_user_detail.view.signatureView

/**
 * 大富翁用户详细统计信息
 *
 * Created on 2021/5/24.
 *
 * @author o.s
 */
class CardUserDetailView : FrameLayout {

    private val mOnlineViewWidth = 10.dp
    private val mOnlineViewHeight = mOnlineViewWidth
    private val mLabelWith = 16.dp
    private val mLabelHeight = mLabelWith
    private var mCurrentSuit: Suit? = null
    private var mPosition: Int = 0

    private val mProvider by lazy {
        getProvider(ICardMonopolyProvider::class.java)
    }

    private val homeProvider by lazy {
        getProvider(IHomeProvider::class.java)
    }

    private val mOnlineStateDrawable by lazy {
        getDrawableStateList(
            normalRes = R.drawable.ic_user_state_offline_full,
            activatedRes = R.drawable.ic_user_state_online_full
        ).apply {
            setBounds(0, 0, mOnlineViewWidth, mOnlineViewHeight)
        }
    }

    var action: ((ActionEvent) -> Unit)? = null
    var dismiss: (() -> Unit)? = null

    var data: UserDetail? = null
        set(value) {
            field = value
            fillData()
            suitShow = value?.suitShowList
        }

    var suitShow: List<Suit>? = null
        set(value) {
            field = value
            fillSuitShow()
        }

    var onlineState: Boolean = false
        set(value) {
            field = value
            notifyOnlineState()
        }

    @SuppressLint("SetTextI18n")
    private fun fillData() {
        resetState()
        data?.apply {
            // User
            avatarView?.setUserInfo(url = avatarUrl)
            onlineState = data?.isOnline ?: true
            if (isSelf()) {
                onlineState = true
            }
            usernameView?.text = nickName.orEmpty()
            rankInfo?.apply {
                rankingView.visible()
                rankingView?.text = "$rankName/${ranking ?: 0}名"
            } ?: rankingView.gone()
            signatureView?.text = signature.orEmpty()

            // MixSuitCount
            mixSuitCount?.apply {
                val text = "已收集套装".toSpan().append(
                    "$mixCount".toSpan().toColor(color = getColor(R.color.color_feb12a)).toBold()
                ).append(
                    "/".toSpan().toColor(color = getColor(R.color.color_4e5e73))
                ).append(
                    "$totalCount".toSpan().toColor(color = getColor(R.color.color_feb12a)).toBold()
                ).append(
                    "\n最早合成套装".toSpan().toColor(color = getColor(R.color.color_4e5e73))
                ).append(
                    "$earliestTotalCount".toSpan().toColor(color = getColor(R.color.color_feb12a)).toBold()
                ).append(
                    "次（限量".toSpan().toColor(color = getColor(R.color.color_4e5e73))
                ).append(
                    "$earliestLimitCount".toSpan().toColor(color = getColor(R.color.color_feb12a)).toBold()
                ).append(
                    "次、普卡".toSpan().toColor(color = getColor(R.color.color_4e5e73))
                ).append(
                    "$earliestCommonCount".toSpan().toColor(color = getColor(R.color.color_feb12a)).toBold()
                ).append(
                    "次）".toSpan().toColor(color = getColor(R.color.color_4e5e73))
                )
                suitStatisticsView?.text = text
                perfectSuitView?.visibility = if (isGrandSlam) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }

            // StatisticCount
            statisticCount?.apply {
                val text = if (isSelf()) {
                    myStatisticsView?.visible()
                    "累计满足".toSpan().append(
                        "$satisfyCount".toSpan().toColor(color = getColor(R.color.color_feb12a)).toBold()
                    ).append(
                        "位好友愿望（每满足50个得".toSpan().toColor(color = getColor(R.color.color_4e5e73))
                    ).append(
                        "拆套卡".toSpan().toColor(color = getColor(R.color.color_feb12a)).toBold()
                    ).append(
                        "）\n昨日收入".toSpan().toColor(color = getColor(R.color.color_4e5e73))
                    ).append(
                        "$yesterdayGold".toSpan().toColor(color = getColor(R.color.color_feb12a)).toBold()
                    ).append(
                        "金币\n昨日使用道具卡".toSpan().toColor(color = getColor(R.color.color_4e5e73))
                    ).append(
                        "$yesterdayToolCard".toSpan().toColor(color = getColor(R.color.color_feb12a)).toBold()
                    ).append(
                        "次\n昨日收集".toSpan().toColor(color = getColor(R.color.color_4e5e73))
                    ).append(
                        "$yesterdayCard".toSpan().toColor(color = getColor(R.color.color_feb12a)).toBold()
                    ).append(
                        "张\n昨日合成".toSpan().toColor(color = getColor(R.color.color_4e5e73))
                    ).append(
                        "$yesterdaySuit".toSpan().toColor(color = getColor(R.color.color_feb12a)).toBold()
                    ).append(
                        "套\n昨日挖了".toSpan().toColor(color = getColor(R.color.color_4e5e73))
                    ).append(
                        "$yesterdayBox".toSpan().toColor(color = getColor(R.color.color_feb12a)).toBold()
                    ).append(
                        "个宝箱".toSpan().toColor(color = getColor(R.color.color_4e5e73))
                    )
                } else {
                    myStatisticsView?.gone()
                    ""
//                    "累计满足".toSpan().append(
//                        "$satisfyCount".toSpan().toColor(color = getColor(R.color.color_feb12a))
//                    ).append(
//                        "位好友愿望\n昨日收入"
//                    ).append(
//                        "$yesterdayGold".toSpan().toColor(color = getColor(R.color.color_feb12a))
//                    ).append(
//                        "金币"
//                    )
                }
                myStatisticsView?.text = text
            }
        }
    }

    private fun fillSuitShow() {
        val size = suitShow?.size ?: 0
        resetSuitShowView(size)
        if (size > 0) {
            if (size > 0) {
                showSuitView1.suit = suitShow?.get(0)
            }
            if (size > 1) {
                showSuitView2.suit = suitShow?.get(1)
            }
            if (size > 2) {
                showSuitView3.suit = suitShow?.get(2)
            }
            if (size > 3) {
                showSuitView4.suit = suitShow?.get(3)
            }
        }
    }

    private fun resetSuitShowView(index: Int = 4) {
        if (index <= 4) {
            showSuitView4.suit = null
        }
        if (index <= 3) {
            showSuitView3.suit = null
        }
        if (index <= 2) {
            showSuitView2.suit = null
        }
        if (index <= 1) {
            showSuitView1.suit = null
        }
    }

    private val labelRankingDrawable by lazy {
        getDrawable(R.drawable.ic_label_ranking)?.apply {
            setBounds(0, 0, mLabelWith, mLabelHeight)
        }
    }

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        fitsSystemWindows = (parent as ViewGroup).fitsSystemWindows
        children.forEach {
            it.fitsSystemWindows = fitsSystemWindows
        }
    }

    private fun initView() {
        initEvent()
        val view = inflate(context, R.layout.view_card_user_detail, null)
        addView(view)

        onlineView?.apply {
            background = mOnlineStateDrawable
            isActivated = data?.isOnline ?: false
        }

        rankingView?.apply {
            setBackground(
                colorRes = R.color.color_19b3c2,
                cornerRadius = 9.dpF
            )
            setCompoundDrawables(labelRankingDrawable, null, null, null)
            setOnClickListener {
                homeProvider?.startToplistGameDetailActivity(data?.rankInfo?.rankType ?: 1L)
            }
        }

        contentView?.apply {
            setBackground(
                colorRes = R.color.color_ffffff,
                cornerRadius = 15.dpF
            )
        }

        suitStatisticsView?.apply {
            setBackground(
                colorRes = R.color.color_f2f3f6,
                cornerRadius = 10.dpF
            )
        }

        myStatisticsView?.apply {
            setBackground(
                colorRes = R.color.color_f2f3f6,
                cornerRadius = 10.dpF
            )
        }

        showSuitView1?.apply {
            action = {
                mPosition = 1
                mCurrentSuit = suit
                mProvider?.startSuitSelectedActivity(
                    from = 1,
                    userId = data?.userId ?: 0L,
                    suitC = "4",
                )
//                showSearchCardDialog(
//                    dismiss = {
////                        titleBar?.syncStatusBar()
//                    }
//                ) { data ->
//                    this@CardUserDetailView.action?.invoke(ActionEvent(suit, data.suit))
//                }
            }
        }
        showSuitView2?.apply {
            action = {
                mPosition = 2
                mCurrentSuit = suit
                mProvider?.startSuitSelectedActivity(
                    from = 1,
                    userId = data?.userId ?: 0L,
                    suitC = "4",
                )
//                showSearchCardDialog(
//                    dismiss = {
////                        titleBar?.syncStatusBar()
//                    }
//                ) { data ->
//                    this@CardUserDetailView.action?.invoke(ActionEvent(suit, data.suit))
//                }
            }
        }
        showSuitView3?.apply {
            action = {
                mPosition = 3
                mCurrentSuit = suit
                mProvider?.startSuitSelectedActivity(
                    from = 1,
                    userId = data?.userId ?: 0L,
                    suitC = "4",
                )
//                showSearchCardDialog(
//                    dismiss = {
////                        titleBar?.syncStatusBar()
//                    }
//                ) { data ->
//                    this@CardUserDetailView.action?.invoke(ActionEvent(suit, data.suit))
//                }
            }
        }
        showSuitView4?.apply {
            action = {
                mPosition = 4
                mCurrentSuit = suit
                mProvider?.startSuitSelectedActivity(
                    from = 1,
                    userId = data?.userId ?: 0L,
                    suitC = "4",
                )
//                showSearchCardDialog(
//                    dismiss = {
////                        titleBar?.syncStatusBar()
//                    }
//                ) { data ->
//                    this@CardUserDetailView.action?.invoke(ActionEvent(suit, data.suit))
//                }
            }
        }

        closeView?.apply {
            setOnClickListener {
                close()
            }
        }
    }

    private fun initEvent() {
        (context as? AppCompatActivity)?.apply {
            observe(EventSelectedSuit::class.java) {
                if (it.from == 1) {
                    val selectedSuit = it.suit
//                showToast("${mCurrentSuit?.cardUserSuitId} -> ${selectedSuit.suitName}")
                    action?.invoke(ActionEvent(mCurrentSuit, selectedSuit))
                    resetSuit()
                }
            }
        }
    }

    private fun resetSuit() {
        mPosition = 0
        mCurrentSuit = null
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return true
    }

    fun hide() {
        gone()
    }

    fun show() {
        visible()
    }

    private fun close() {
        dismiss?.invoke()
        hide()
    }

    private fun resetState() {
        if (isSelf()) {
            setState(showSuitView1, SelectSuitView.State.SELECT)
            setState(showSuitView2, SelectSuitView.State.SELECT)
            setState(showSuitView3, SelectSuitView.State.SELECT)
            setState(showSuitView4, SelectSuitView.State.SELECT)
        } else {
            setState(showSuitView1, SelectSuitView.State.EMPTY)
            setState(showSuitView2, SelectSuitView.State.EMPTY)
            setState(showSuitView3, SelectSuitView.State.EMPTY)
            setState(showSuitView4, SelectSuitView.State.EMPTY)

        }
    }

    private fun setState(view: SelectSuitView?, state: SelectSuitView.State) {
        view?.state = state
        view?.clickEnable = isSelf()
    }

    /**
     * 判断是否自己
     */
    private fun isSelf(): Boolean {
        return data?.run {
            userId <= 0L || UserManager.instance.userId == userId
        } ?: true
    }

    /**
     * 通知在线状态
     */
    private fun notifyOnlineState() {
        onlineView?.isActivated = onlineState
    }

    data class ActionEvent(
        var suit: Suit?,
        var newSuit: Suit?
    )
}