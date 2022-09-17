package com.kotlin.android.card.monopoly.widget.wish

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.addTextChangedListener
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.ext.setUserInfo
import com.kotlin.android.card.monopoly.provider.startCardMainActivity
import com.kotlin.android.card.monopoly.widget.card.image.CardState
import com.kotlin.android.card.monopoly.widget.search.SelectCardView
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.app.data.entity.monopoly.WishInfo
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.core.getString
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList
import kotlinx.android.synthetic.main.view_wish_help_user.view.*

/**
 * 愿望头部视图：状态查看 [State] 许愿(发起许愿), 许愿中, 愿望实现(取回卡片), 帮助TA
 *
 * Created on 2020/9/14.
 *
 * @author o.s
 */
class WishTitleView : LinearLayout {

    private val mIconWidth = 66.dp
    private val mIconHeight = 97.dp
    private val mActionHeight = 35.dp
    private val mInputMarginLeft = 76.dp
    private val mInputPaddingLeft = 7.dp
    private val mInputPaddingTop = 5.dp
    private val mActionMarginTop = 15.dp
    private val mActionMarginBottom = 10.dp
    private val mActionPaddingLeft = 25.dp

    private val mInputTextSize = 14F // sp
    private val mActionTextSize = 15F // sp

    private var iconLayout: FrameLayout? = null
    private var selectCardView: SelectCardView? = null
    private var inputView: EditText? = null
    private var descView: TextView? = null
    private var helpUserLayout: View? = null
    private var actionView: TextView? = null

    private var mHasCard: Boolean = false
    private var mHasInput: Boolean = false

    var action: ((type: EventType) -> Unit)? = null

    var state: State = State.WISH
        set(value) {
            field = value
            notifyChange()
        }

    var card: Card? = null
        set(value) {
            field = value
            selectCardView?.card = value
        }

    var wishInfo: WishInfo? = null
        set(value) {
            field = value
            fillData()
        }

    val wishText: String
        get() {
            return inputView?.text.toString()
        }

    fun clear() {
        card = null
        inputView?.setText("")
    }

    private fun fillData() {
        wishInfo?.apply {
            iconLayout?.visible()
            card = cardInfo
            descView?.text = content ?: ""

            userInfo?.apply {
                helpUserLayout?.visible()
                avatarView?.setUserInfo(userInfo)
                nickNameView?.let {
                    it.text = nickName ?: ""
//                    it.isOnline = isOnline
                }
                labelView?.text = if (state == State.HELP_TA) {
                        getString(R.string.help_ta_realize_wish)
                    } else {
                        getString(R.string.help_you_realize_wish)
                    }
            } ?: helpUserLayout?.gone()
            when (status) {
                1L -> { // 已发布：（更换/帮TA实现）
                    actionView?.visible()
                    if (state != State.HELP_TA) {
                        state = State.WISHING
                    }
                }
                2L -> { // 已实现：（取回卡片/隐藏）
                    if (state == State.HELP_TA) {
                        actionView?.gone()
                    } else {
                        actionView?.visible()
                        state = State.WISH_COME_TRUE
                    }
                }
                else -> {
                    resetUI()
                }
            }
        } ?: resetUI()
    }

    private fun resetUI() {
        if (state == State.HELP_TA) {
            iconLayout?.gone()
            helpUserLayout?.gone()
            actionView?.gone()
        } else {
            state = State.WISH
        }
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
    }

    private fun initView() {
        orientation = VERTICAL

        addIconLayout()

        helpUserLayout = View.inflate(context, R.layout.view_wish_help_user, null).apply {
            avatarView?.setOnClickListener {
                startCardMainActivity(wishInfo?.userInfo)
            }
            nickNameView?.setOnClickListener {
                startCardMainActivity(wishInfo?.userInfo)
            }
        }
        addView(helpUserLayout)

        actionView = addActionView()

        state = State.WISH
    }

    private fun addIconLayout(): FrameLayout {
        return FrameLayout(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mIconHeight)

            this@WishTitleView.addView(this)
            iconLayout = this

            selectCardView = addSelectCardView()
            inputView = addInputView()
            descView = addDescView()
        }
    }

    private fun addSelectCardView(): SelectCardView {
        return SelectCardView(context).apply {
            layoutParams = LayoutParams(mIconWidth, mIconHeight)
            action = {
                when (state) {
                    State.WISH,
                    State.CHANGE_WISH -> {
                        this@WishTitleView.action?.invoke(EventType.SEARCH_CARD)
                    }
                    else -> { }
                }
            }
            stateChange = {
                when (it) {
                    CardState.FILL -> {
                        mHasCard = true
                    }
                    CardState.EMPTY -> {
                        mHasCard = false
                    }
                    else -> {}
                }
                notifyActionState()
            }

            iconLayout?.addView(this)
        }
    }

    private fun addInputView(): EditText {
        return EditText(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mIconHeight).apply {
                marginStart = mInputMarginLeft
            }
            setBackground(
                    colorRes = R.color.color_f2f3f6,
                    cornerRadius = 6.dpF
            )
            gravity = Gravity.START or Gravity.TOP
            setPadding(mInputPaddingLeft, mInputPaddingTop, mInputPaddingLeft, mInputPaddingTop)
            setTextColor(getColor(R.color.color_1d2736))
            setHintTextColor(getColor(R.color.color_8798af))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mInputTextSize)
            setHint(R.string.hint_please_input_my_wish)
            addTextChangedListener {
                mHasInput = it?.length ?: 0 > 0
                notifyActionState()
            }

            iconLayout?.addView(this)
        }
    }

    private fun addDescView(): TextView {
        return TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mIconHeight).apply {
                marginStart = mInputMarginLeft
            }
            setPadding(mInputPaddingLeft, mInputPaddingTop, mInputPaddingLeft, mInputPaddingTop)
            setTextColor(getColor(R.color.color_1d2736))
            setHintTextColor(getColor(R.color.color_8798af))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mInputTextSize)

            iconLayout?.addView(this)
        }
    }

    private fun addActionView(): TextView {
        return TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, mActionHeight).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                topMargin = mActionMarginTop
                bottomMargin = mActionMarginBottom
            }
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
            gravity = Gravity.CENTER
            setPadding(mActionPaddingLeft, 0, mActionPaddingLeft, 0)
            typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            setTextColor(getColor(R.color.color_ffffff))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mActionTextSize)

            setOnClickListener {
                val type = when (state) {
                    State.WISH -> EventType.WISH
                    State.WISHING -> EventType.WISHING
                    State.CHANGE_WISH -> EventType.CHANGE_WISH
                    State.WISH_COME_TRUE -> EventType.WISH_COME_TRUE
                    State.HELP_TA -> EventType.HELP_TA
                }
                action?.invoke(type)
            }

            addView(this)
        }
    }

    private fun notifyChange() {
        when (state) {
            State.WISH -> {
                wishState()
            }
            State.WISHING -> {
                wishingState()
            }
            State.CHANGE_WISH -> {
                changeWishState()
            }
            State.WISH_COME_TRUE -> {
                wishComeTrueState()
            }
            State.HELP_TA -> {
                helpTaState()
            }
        }
        notifyActionState()
    }

    private fun wishState() {
        inputView?.visible()
        descView?.gone()
        helpUserLayout?.gone()
        //重置下卡片
        selectCardView?.card = null
        actionView?.setText(R.string.wish)
    }

    private fun changeWishState() {
        inputView?.visible()
        descView?.gone()
        helpUserLayout?.gone()
        actionView?.setText(R.string.change_wish)
    }

    private fun wishingState() {
        inputView?.gone()
        descView?.visible()
        helpUserLayout?.gone()

        actionView?.setText(R.string.change_wish)
    }

    private fun wishComeTrueState() {
        inputView?.gone()
        descView?.visible()
//        helpUserLayout?.visible()

        actionView?.setText(R.string.get_back_the_card)
    }

    private fun helpTaState() {
        inputView?.gone()
        descView?.visible()
//        helpUserLayout?.gone()

        actionView?.setText(R.string.help_ta_realize)
    }

    private fun notifyActionState() {
        actionView?.apply {
            isEnabled = when (state) {
                State.WISH -> {
                    mHasCard && mHasInput
                }
                State.WISHING -> {
                    true
                }
                State.CHANGE_WISH -> {
                    mHasCard && mHasInput
                }
                State.WISH_COME_TRUE -> {
                    true
                }
                State.HELP_TA -> {
                    true
                }
            }
        }
    }

    /**
     * 我的愿望状态
     */
    enum class State {

        /**
         * 许愿(发起许愿)
         */
        WISH,

        /**
         * 许愿中（更换）
         */
        WISHING,

        /**
         * 更换（编辑）
         */
        CHANGE_WISH,

        /**
         * 愿望实现(取回卡片)
         */
        WISH_COME_TRUE,

        /**
         * 帮助TA
         */
        HELP_TA
    }

    /**
     * 点击事件类型
     */
    enum class EventType {

        /**
         * 查找卡片
         */
        SEARCH_CARD,

        /**
         * 许愿(发起许愿)
         */
        WISH,

        /**
         * 许愿中（更换）
         */
        WISHING,

        /**
         * 更换（编辑）
         */
        CHANGE_WISH,

        /**
         * 愿望实现(取回卡片)
         */
        WISH_COME_TRUE,

        /**
         * 帮助TA
         */
        HELP_TA
    }
}