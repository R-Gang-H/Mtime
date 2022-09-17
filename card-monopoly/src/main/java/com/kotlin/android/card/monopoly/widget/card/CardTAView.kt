package com.kotlin.android.card.monopoly.widget.card

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.view.children
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.widget.card.view.CheckCardView
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList

/**
 * 卡片视图：
 *
 * Created on 2020/9/1.
 *
 * @author o.s
 */
class CardTAView : LinearLayout {

    private val mFooterHeight = 75.dp
    private val mActionHeight = 35.dp
    private val mMarginTop = 15.dp
    private val mMarginLeft = 10.dp
    private val mActionPadding = 20.dp
    private val mTextSize = 15F

    private var cardStoreView: CheckCardView? = null
    private var composeView: TextView? = null

    var limit: Int? = null
        set(value) {
            field = value
            cardStoreView?.limit = value ?: 12
        }

    var data: List<Card>? = null
        set(value) {
            field = value
            cardStoreView?.data = value
        }

    val selectedCards: ArrayList<Card>?
        get() = cardStoreView?.selectedCards

    var action: ((event: CardView.ActionEvent) -> Unit)? = null

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
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        orientation = VERTICAL

        cardStoreView = CheckCardView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                setMargins(mMarginLeft, mMarginTop, mMarginLeft, 0)
            }
            post {
                limit = 12
                spec = CheckCardView.Spec.CARD
            }
            selectChange = {
                when (it.size) {
                    1 -> {
                        composeView?.isEnabled = true
                    }
                    else -> {
                        composeView?.isEnabled = false
                    }
                }
            }
        }
        addView(cardStoreView)

        val footerView = LinearLayout(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, mFooterHeight).apply {
                gravity = Gravity.CENTER_HORIZONTAL
            }
            orientation = HORIZONTAL
            gravity = Gravity.CENTER

            composeView = initActionView(CardView.ActionType.DEAL)
            addView(composeView)
        }
        addView(footerView)
    }

    private fun initActionView(
            type: CardView.ActionType,
            @ColorRes colorRes: Int = R.color.color_feb12a,
            @ColorRes disableColorRes: Int = R.color.color_66feb12a,
            margin: Int = 0
    ): TextView {
        return TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, mActionHeight).apply {
                gravity = Gravity.CENTER
                marginStart = margin
                marginEnd = margin
            }
            gravity = Gravity.CENTER
            setPadding(mActionPadding, 0, mActionPadding, 0)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize)
            setTextColor(getColor(R.color.color_ffffff))
            setText(R.string.deal)
            background = getDrawableStateList(
                    normal = getShapeDrawable(
                            colorRes = colorRes,
                            cornerRadius = 18.dpF
                    ),
                    disable = getShapeDrawable(
                            colorRes = disableColorRes,
                            cornerRadius = 18.dpF
                    )
            )
            setOnClickListener {
                cardStoreView?.selectedCards?.apply {
                    action?.invoke(CardView.ActionEvent(type, this))
                }
            }
        }
    }

}