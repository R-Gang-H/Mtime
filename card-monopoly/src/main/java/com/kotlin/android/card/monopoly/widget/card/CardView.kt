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
import com.kotlin.android.card.monopoly.widget.card.adapter.CheckCardAdapter
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
class CardView : LinearLayout {

    private val mFooterHeight = 75.dp
    private val mActionHeight = 35.dp
    private val mMargin = 15.dp
    private val mMarginTop = 15.dp
    private val mMarginLeft = 10.dp
    private val mActionPadding = 20.dp
    private val mTextSize = 15F

    private var checkCardView: CheckCardView? = null
    private var composeView: TextView? = null
    private var discardView: TextView? = null
    private var saveCofferView: TextView? = null

    var limit: Int? = null
        set(value) {
            field = value
            checkCardView?.limit = value ?: 12
        }

    var data: List<Card>? = null
        set(value) {
            field = value
            checkCardView?.data = value
        }

    val selectedCards: ArrayList<Card>?
        get() = checkCardView?.selectedCards

    var action: ((event: ActionEvent) -> Unit)? = null

    var actionItem: ((event: CheckCardAdapter.ActionEvent) -> Unit)? = null

//    var changeState: ((count: Int) -> Unit)? = null
//        set(value) {
//            field = value
//            cardStoreView?.changeState = value
//        }

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

        checkCardView = CheckCardView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                setMargins(mMarginLeft, mMarginTop, mMarginLeft, 0)
            }
            post {
                limit = 12
                spec = CheckCardView.Spec.CARD
            }
            action = {
                actionItem?.invoke(it)
            }
            selectChange = {
                when (it.size) {
                    0 -> {
                        composeView?.isEnabled = false
                        discardView?.isEnabled = false
                        saveCofferView?.isEnabled = false
                    }
                    1 -> {
                        composeView?.isEnabled = false
                        discardView?.isEnabled = true
                        saveCofferView?.isEnabled = true
                    }
                    5 -> {
                        composeView?.isEnabled = true
                        discardView?.isEnabled = true
                        saveCofferView?.isEnabled = true
                    }
                    else -> {
                        composeView?.isEnabled = false
                        discardView?.isEnabled = true
                        saveCofferView?.isEnabled = true
                    }
                }
            }
        }
        addView(checkCardView)

        val footerView = LinearLayout(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, mFooterHeight).apply {
                gravity = Gravity.CENTER_HORIZONTAL
            }
            orientation = HORIZONTAL
            gravity = Gravity.CENTER

            composeView = initActionView(ActionType.COMPOSE)
            discardView = initActionView(ActionType.DISCARD, margin = mMargin)
            saveCofferView = initActionView(ActionType.SAVE_COFFER, R.color.color_12c7e9, R.color.color_6612c7e9)
            addView(composeView)
            addView(discardView)
            addView(saveCofferView)
        }
        addView(footerView)
    }

    private fun initActionView(
            type: ActionType,
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
            val res = when (type) {
                ActionType.COMPOSE -> R.string.compose
                ActionType.DISCARD -> R.string.discard
                ActionType.SAVE_COFFER -> R.string.save_coffer
                ActionType.DEAL -> R.string.deal
            }
            setText(res)
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
                checkCardView?.selectedCards?.apply {
                    action?.invoke(ActionEvent(type, this))
                }
            }
        }
    }

    data class ActionEvent (
            var type: ActionType,
            var selectedCards: ArrayList<Card>
    )

    enum class ActionType {
        /**
         * 合成
         */
        COMPOSE,

        /**
         * 丢弃
         */
        DISCARD,

        /**
         * 存保险箱
         */
        SAVE_COFFER,

        /**
         * 交易
         */
        DEAL;
    }
}