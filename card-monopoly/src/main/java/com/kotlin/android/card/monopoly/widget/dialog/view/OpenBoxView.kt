package com.kotlin.android.card.monopoly.widget.dialog.view

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.children
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.widget.card.image.CardState
import com.kotlin.android.card.monopoly.widget.card.item.SelectCardItemView
import com.kotlin.android.card.monopoly.widget.card.view.SelectCardView
import com.kotlin.android.app.data.entity.monopoly.Box
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.app.data.entity.monopoly.RewardInfo
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.span.toColor
import com.kotlin.android.ktx.ext.span.toSpan
import kotlinx.android.synthetic.main.view_open_box.view.*

/**
 * 开宝箱视图
 *
 * Created on 2021/5/19.
 *
 * @author o.s
 */
class OpenBoxView : FrameLayout {

    /**
     * 选择的卡片列表
     */
    private var selectedLimitCards: ArrayList<Card> = ArrayList()

    var action: ((List<Card>?) -> Unit)? = null
    var dismiss: (() -> Unit)? = null
//    var event: ((pocketCards: PocketCards?) -> Unit)? = null

    var data: Data? = null // RewardInfo?
        set(value) {
            field = value
            fillData()
        }

    private fun fillData() {
        clear()
        data?.info?.apply {
            openBoxCardView?.data = commonCardList
            val commonSize = commonCardList?.size ?: 0
            val limitSize = limitCardList?.size ?: 0
            val totalCount = commonSize + limitSize
            titleView?.apply {
                val count = "$totalCount".toSpan()
                        .toColor(color = getColor(R.color.color_feb12a))
                val coin = "$gold".toSpan()
                        .toColor(color = getColor(R.color.color_feb12a))
                text = "获得卡片 ".toSpan()
                        .append(count)
                        .append(" 张 金币 ")
                        .append(coin)
            }
            resetLimitView()
            if (limitSize >= 1) {
                limitCardBg1?.visible()
                limitCard1?.card = limitCardList?.get(0)
            }
            if (limitSize >= 2) {
                limitCardBg2?.visible()
                limitCard2?.card = limitCardList?.get(1)
            }
        }
        boxImageView?.data = data?.box
    }

    private fun resetLimitView() {
        limitCardBg1?.gone()
        limitCardBg2?.gone()
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
        val view = inflate(context, R.layout.view_open_box, null)
        addView(view)

        background = getShapeDrawable(
                colorRes = R.color.color_bb45717c,
                endColorRes = R.color.color_ee155f81
        )

        view?.openBoxCardView?.selectModel = SelectCardView.SelectModel.MULTIPART
        view?.actionView?.apply {
            setBackground(
                    colorRes = R.color.color_feb12a,
                    cornerRadius = 19.dpF
            )
            setOnClickListener {
                val cards = ArrayList<Card>()
                cards.addAll(selectedLimitCards)
                view.openBoxCardView?.selectedCards?.apply {
                    cards.addAll(this)
                }
                action?.invoke(cards)
            }
        }
        view?.closeView?.apply {
            setBackground(
                    colorRes = R.color.color_12c7e9,
                    cornerRadius = 19.dpF
            )
            setOnClickListener {
                dismiss?.invoke()
                hide()
            }
        }

        animaView1?.apply {
            imageAssetsFolder = "images/supreme"
            repeatCount = ValueAnimator.INFINITE
            playAnimation()
        }
        animaView2?.apply {
            imageAssetsFolder = "images/supreme"
            repeatCount = ValueAnimator.INFINITE
            playAnimation()
        }

        setupClick(view?.limitCard1)
        setupClick(view?.limitCard2)
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

    private fun setupClick(itemView: SelectCardItemView?) {
        itemView?.apply {
            setOnClickListener {
                card?.apply {
                    if (state == CardState.FILL) {
                        state = CardState.SELECTED
                        isSelected = true
                        selectedLimitCards.add(this)
                    } else if (state == CardState.SELECTED) {
                        state = CardState.FILL
                        isSelected = false
                        selectedLimitCards.remove(this)
                    }
                }
            }
        }
    }

    private fun clear() {
        selectedLimitCards.clear()
    }

    data class Data(
        var box: Box? = null,
        var info: RewardInfo? = null
//            @IntRange(from = 0, to = 4) var position: Int = 0,
//            var gold: Long = 0L,
//            var gold: Long = 0L,
//            var commonCardList: List<Card>? = null,
//            var limitCardList: List<Card>? = null,
    )
}