package com.kotlin.android.card.monopoly.widget.lack

import android.content.Context
import android.graphics.Rect
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.entity.LackCardViewBean
import com.kotlin.android.card.monopoly.widget.card.view.LackCardView
import com.kotlin.android.app.data.entity.monopoly.*
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getString
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.span.toBold
import com.kotlin.android.ktx.ext.span.toColor
import com.kotlin.android.ktx.ext.span.toSpan
import java.util.ArrayList

/**
 * 缺失卡片套装视图：
 *
 * Created on 2020/9/14.
 *
 * @author o.s
 */
class LackCardSuitView : LinearLayout {

    private var mTitleHeight = 44.dp

    private val mLabelPaddingTop = 20.dp
    private val mLabelPaddingBottom = 10.dp
    private val mRecyclerPaddingBottom = 10.dp
    private val mTitleTextSize = 15F
    private val mLabelTextSize = 14F

    private var titleView: TextView? = null
    private var dropView: DropView? = null
    private var descView: TextView? = null
    private var lackCardView: LackCardView? = null
    private var descView2: TextView? = null
    private var recyclerView: RecyclerView? = null

    private val mAdapter by lazy {
        LackCardSuitAdapter {
            action?.invoke(it)
        }
    }

    var action: ((data: UserInfo) -> Unit)? = null
    var dropAction: ((suit: Suit) -> Unit)? = null
        set(value) {
            field = value
            dropView?.action = value
        }

    private var mSuitList: List<Suit>? = null
    private var mSelectSuitInfo: Suit? = null
    private var mOwnedCards: List<Card>? = null
    private var mLackCards: List<LackCard>? = null

    var data: MyWish? = null
        set(value) {
            field = value
            mSuitList = value?.suitList
            mSelectSuitInfo = value?.selectSuitInfo
            mOwnedCards = value?.ownedCards
            mLackCards = value?.lackCards
            fillData()
        }

    var myCardsBySuit: MyCardsBySuit? = null
        set(value) {
            field = value
            mSelectSuitInfo = value?.selectSuitInfo
            mOwnedCards = value?.ownedCards
            mLackCards = value?.lackCards
            fillMyCardsBySuit()
        }

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
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
        orientation = VERTICAL

        titleView = addTitleView()
        dropView = addDropView()
        descView = addLabelView()
        lackCardView = addLackCardView()
        descView2 = addLabelView()
        recyclerView = addRecyclerView()
    }

    private fun fillData() {
        data?.apply {
            visible()
            fillDropView()
            fillMyCardsBySuit()
        } ?: gone()
    }

    private fun fillMyCardsBySuit() {
        fillDescView()
        fillOwnedCardsView()
        fillLackCardsView()
    }

    private fun fillDropView() {
        mSuitList?.forEach {
            if (it.suitId == mSelectSuitInfo?.suitId) {
                it.isSelected = true
            }
        }
        dropView?.setData(mSuitList)
    }

    private fun fillDescView() {
        val suitName = mSelectSuitInfo?.suitName ?: ""
        descView?.text = getString(R.string.wish_you_owned_cards_, mOwnedCards?.size ?: 0).toSpan()
                .append(
                        suitName.toSpan().toBold().toColor(color = getColor(R.color.color_feb12a))
                ).append(
                        getString(R.string.wish_card)
                )
        descView2?.text = getString(R.string.wish_you_lack_cards_, mLackCards?.size
                ?: 0).toSpan()
                .append(
                        suitName.toSpan().toBold().toColor(color = getColor(R.color.color_feb12a))
                ).append(
                        if (mSelectSuitInfo?.suitType == 3L) { // 限量卡套装的标志是什么？
                            getString(R.string.wish_paperback_suit_limit)
                        } else {
                            getString(R.string.wish_paperback_suit)
                        }
                )
    }

    private fun fillOwnedCardsView() {
        lackCardView?.data = mOwnedCards
    }

    private fun fillLackCardsView() {
        mLackCards?.apply {
            val list = ArrayList<Card>()
            mOwnedCards?.apply {
                list.addAll(this)
            }
            mLackCards?.forEach {
                list.add(Card(
                        cardId = it.cardId,
                        cardName = it.cardName,
                        cardCover = it.cardCover
                ))
            }
            mAdapter.suit = Suit(
                    suitName = mSelectSuitInfo?.suitName ?: "",
                    cardList = list.sortedBy { it.cardId }
            )
            mAdapter.setData(transformLackCards(mLackCards))
        }
    }

    private fun transformLackCards(lackCards: List<LackCard>?): List<LackCardViewBean>? {
        return lackCards?.run {
            val list = ArrayList<LackCardViewBean>()
            forEach {
                list.add(LackCardViewBean(
                        type = 1,
                        cardId = it.cardId,
                        cardName = it.cardName.orEmpty(),
                        cardCover = it.cardCover
                ))
                it.ownedFriends?.apply {
                    val isOdd = size % 2 == 1
                    val len = if (isOdd) {
                        size / 2
                    } else {
                        size / 2 - 1
                    }
                    (0 .. len).forEach { step ->
                        val index = step * 2

                        val bean = if (step == len) {
                            if (isOdd) {
                                LackCardViewBean(
                                        friend1 = this[index])
                            } else {
                                LackCardViewBean(
                                        friend1 = this[index],
                                        friend2 = this[index + 1])
                            }
                        } else {
                            LackCardViewBean(
                                    friend1 = this[index],
                                    friend2 = this[index + 1])
                        }
                        list.add(bean)
                    }
                }

            }
            list
        }
    }

    private fun addTitleView(): TextView {
        return TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, mTitleHeight)
            gravity = Gravity.CENTER
            typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mTitleTextSize)
            setTextColor(getColor(R.color.color_4e5e73))
            setText(R.string.my_missing_card)
            addView(this)
        }
    }

    private fun addDropView(): DropView {
        return DropView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            stateChange = {
                when (it) {
                    DropView.State.EXPANDING -> {
                        lackCardView?.gone()
                        descView?.gone()
                        descView2?.gone()
                        recyclerView?.gone()
                    }
                    DropView.State.COLLAPSING -> {
                        lackCardView?.visible()
                        descView?.visible()
                        descView2?.visible()
                        recyclerView?.visible()
                    }
                }
            }
            this@LackCardSuitView.addView(this)
        }
    }

    private fun addLackCardView(): LackCardView {
        return LackCardView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            this@LackCardSuitView.addView(this)
        }
    }

    /**
     * 高亮文字颜色 #FEB12A
     */
    private fun addLabelView(): TextView {
        return TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            setPadding(0, mLabelPaddingTop, 0, mLabelPaddingBottom)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mLabelTextSize)
            setTextColor(getColor(R.color.color_8798af))
            addView(this)
        }
    }

    private fun addRecyclerView(): RecyclerView {
        return RecyclerView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            setPadding(0, 0, 0, mRecyclerPaddingBottom)
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(LackCardItemDecoration())
            adapter = mAdapter

            this@LackCardSuitView.addView(this)
        }
    }

}

class LackCardItemDecoration : RecyclerView.ItemDecoration() {
    private val gap = 10.dp

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        if (position > 0) {
            outRect.top = gap
        }
    }

}