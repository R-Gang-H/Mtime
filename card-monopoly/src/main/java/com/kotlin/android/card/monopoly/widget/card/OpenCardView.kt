package com.kotlin.android.card.monopoly.widget.card

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.view.children
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.widget.card.view.CheckCardView
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getDrawable
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList

/**
 * 卡友丢给我的卡片（卡片中转站，弃卡位）
 *
 * Created on 2021/5/12.
 *
 * @author o.s
 */
class OpenCardView : LinearLayout {

    private val mHeaderHeight = 40.dp
    private val mFooterHeight = 75.dp
    private val mActionHeight = 35.dp
    private val mMarginLeft = 10.dp
    private val mActionPadding = 20.dp
    private val mTextSize = 15F
    private val mDiscardTextSize = 13F

    private var checkCardView: CheckCardView? = null
    private var titleView: TextView? = null
    private var pickupView: TextView? = null
    private var discardView: TextView? = null

    var spec: CheckCardView.Spec = CheckCardView.Spec.TRANSFER
        set(value) {
            field = value
            post {
                checkCardView?.spec = value
                checkCardView?.limit = value.spec
            }
        }

    var data: List<Card>? = null
        set(value) {
            field = value
            checkCardView?.data = value
        }

    val selectedCards: ArrayList<Card>?
        get() = checkCardView?.selectedCards

    var action: ((event: ActionEvent) -> Unit)? = null

    var title: CharSequence = ""
        set(value) {
            field = value
            titleView?.text = title
        }

    /**
     * 显示都卡给TA入口
     */
    fun showDiscardActionView(isShow: Boolean = true) {
        discardView?.visibility = if (isShow) View.VISIBLE else View.GONE
    }

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

        val headerView = FrameLayout(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mHeaderHeight).apply {
                setMargins(mMarginLeft, 0, mMarginLeft, 0)
            }

            titleView = initTitleView()
            discardView = initDiscardView()
            addView(titleView)
            addView(discardView)
        }
        addView(headerView)

        checkCardView = CheckCardView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                setMargins(mMarginLeft, 0, mMarginLeft, 0)
            }
//            post {
//                spec = CheckCardView.Spec.TRANSFER
//                limit = spec.spec
//            }
            selectChange = {
                when (it.size) {
                    0 -> {
                        pickupView?.isEnabled = false
                    }
                    else -> {
                        pickupView?.isEnabled = true
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

            pickupView = initActionView(ActionType.PICKUP)
            addView(pickupView)
        }
        addView(footerView)
    }

    private fun initTitleView(): TextView {
        return TextView(context).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER_VERTICAL
            }
            gravity = Gravity.CENTER
            typeface = Typeface.DEFAULT_BOLD
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize)
            setTextColor(getColor(R.color.color_4e5e73))
        }
    }

    private fun initDiscardView(): TextView {
        return TextView(context).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER_VERTICAL or Gravity.END
            }
            gravity = Gravity.CENTER
            typeface = Typeface.DEFAULT_BOLD
            getDrawable(R.drawable.ic_discard_arrow_right)?.apply {
                setBounds(0, 0, 18.dp, 18.dp)
                setCompoundDrawables(null, null, this, null)
            }
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mDiscardTextSize)
            setTextColor(getColor(R.color.color_12c7e9))
            setText(R.string.lose_the_card_to_ta)
            setOnClickListener {
                action?.invoke(ActionEvent(ActionType.DISCARD, ArrayList()))
            }

            gone()
        }
    }

    private fun initActionView(
            type: ActionType,
            @ColorRes colorRes: Int = R.color.color_12c7e9,
            @ColorRes disableColorRes: Int = R.color.color_6612c7e9,
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
                ActionType.PICKUP -> R.string.pickup
                ActionType.DISCARD -> R.string.lose_the_card_to_ta
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
         * 拾取
         */
        PICKUP,

        /**
         * 丢卡
         */
        DISCARD,
    }
}