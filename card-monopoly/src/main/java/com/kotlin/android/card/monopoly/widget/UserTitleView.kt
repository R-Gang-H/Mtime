package com.kotlin.android.card.monopoly.widget

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.view.children
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.app.data.entity.monopoly.UserInfo
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getDrawable
import com.kotlin.android.ktx.ext.core.getString
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF

/**
 * 用户头部信息：金币、名称、排名、套装
 *
 * Created on 2020/8/18.
 *
 * @author o.s
 */
class UserTitleView : LinearLayout {

    private val mIconWidth = 25.dp
    private val mIconHeight = 25.dp
    private val mTextHeight = 25.dp
    private val mTextPadding = 10.dp
    private val strokeWidth = 8.dp
    private val cornerRadius = 13.dpF
    private val mTextSize = 11F

    private var coinView: TextView? = null
    private var badgeView: TextView? = null
    private var suitView: TextView? = null

    var action: ((event: ActionEvent) -> Unit)? = null

    var userInfo: UserInfo? = null
        set(value) {
            field = value
            fillData()
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

    /**
     * 150 : 250 : 170
     */
    private fun initView() {
        orientation = HORIZONTAL
        coinView = initView(R.drawable.ic_coin).apply { setOnClickListener { action?.invoke(ActionEvent.COIN) } }
        badgeView = initView(R.mipmap.ic_badge, margin = 1.dp).apply { setOnClickListener { action?.invoke(ActionEvent.BADGE) } }
        suitView = initView(R.drawable.ic_suit, margin = 1.dp).apply { setOnClickListener { action?.invoke(ActionEvent.SUIT) } }
    }

    private fun initView(@DrawableRes iconRes: Int, margin: Int = 0): TextView {
        return TextView(context).apply {
            LayoutParams(LayoutParams.WRAP_CONTENT, mTextHeight).apply {
                gravity = Gravity.CENTER_VERTICAL
                layoutParams = this
                marginStart = margin
            }
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, mTextPadding, 0)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize)
            setTextColor(getColor(R.color.color_ffffff))
            ellipsize = TextUtils.TruncateAt.END
            maxLines = 1
            setBackground(
                    colorRes = R.color.color_29a3d3,
                    strokeColorRes = android.R.color.transparent,
                    strokeWidth = strokeWidth,
                    cornerRadius = cornerRadius,
            )
            getDrawable(iconRes)?.apply {
                setBounds(0, 0, mIconWidth, mIconHeight)
                setCompoundDrawables(this, null, null, null)
            }
            addView(this)
        }
    }

    private fun fillData() {
        userInfo?.apply {
            visible()
            setCoin(cardGoldShow ?: "")
            rankInfo?.apply {
                if (ranking ?: 0L > 0L) {
                    showBadge()
                    setBadge("${rankName ?: getTypeString(rankType)}/${ranking}名")
                } else {
                    showBadge(false)
                }
            } ?: showBadge(false)
            setSuit(getString(R.string.str_suit, suitCount))
        } ?: resetUI()
    }

    private fun setCoin(text: String) {
        coinView?.text = text
    }

    private fun setBadge(text: String) {
        badgeView?.text = text
    }

    private fun setSuit(text: String) {
        suitView?.text = text
    }

    private fun resetUI() {
        gone()
    }

    private fun showBadge(isShow: Boolean = true) {
        if (isShow) {
            badgeView?.visible()
            (suitView?.layoutParams as MarginLayoutParams).apply {
                marginStart = 1.dp
                suitView?.layoutParams = this
            }
        } else {
            badgeView?.gone()
            (suitView?.layoutParams as MarginLayoutParams).apply {
                marginStart = 50.dp
                suitView?.layoutParams = this
            }
        }
    }

    private fun getTypeString(type:Long):String {
        when(type){
            1L->{
                return getString(R.string.str_rank_type_1)
            }
            2L->{
                return getString(R.string.str_rank_type_2)
            }
            3L->{
                return getString(R.string.str_rank_type_3)
            }
            4L->{
                return getString(R.string.str_rank_type_4)
            }
            5L->{
                return getString(R.string.str_rank_type_5)
            }
            6L->{
                return getString(R.string.str_rank_type_6)
            }
        }
        return "-"
    }

    enum class ActionEvent {
        COIN,
        BADGE,
        SUIT
    }
}