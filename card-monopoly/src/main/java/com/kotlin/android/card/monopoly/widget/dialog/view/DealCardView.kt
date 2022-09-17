package com.kotlin.android.card.monopoly.widget.dialog.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IntDef
import androidx.core.view.children
import com.kotlin.android.card.monopoly.CARD_HEIGHT
import com.kotlin.android.card.monopoly.CARD_WIDTH
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.widget.card.image.CardImageView
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.app.data.entity.monopoly.ItemData
import com.kotlin.android.app.data.entity.monopoly.UserInfo
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.core.getDrawable
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * 获得奖励视图：
 *
 * Created on 2020/9/25.
 *
 * @author o.s
 */
class DealCardView : FrameLayout {

    @IntDef(EXCHANGE_CARD, EXCHANGE_COIN)
    @Retention(RetentionPolicy.SOURCE)
    annotation class ExchangeType

    private val mIconWidth = CARD_WIDTH.dp
    private val mIconHeight = CARD_HEIGHT.dp
    private val mCenterWidth = 36.dp
    private val mCenterHeight = 21.dp
    private val mCoinWidth = 80.dp
    private val mCoinHeight = 33.dp

    private val mIconView by lazy {
        initCardView(mIconWidth, mIconHeight, Gravity.START)
    }
    private val mIconEndView by lazy {
        initCardView(mIconWidth, mIconHeight, Gravity.END).apply {
            gone()
        }
    }
    private val mCenterView by lazy {
        initIconView(mCenterWidth, mCenterHeight, Gravity.CENTER).apply {
            setImageResource(R.drawable.ic_arrow_right_blue)
        }
    }
    private val mCoinView by lazy { initCoinView() }

    var data: Data? = null
        set(value) {
            field = value
            fillData()
        }

    private fun fillData() {
        data?.apply {
            when (exchangeType) {
                EXCHANGE_CARD -> {
                    exchangeCard()
                }
                EXCHANGE_COIN -> {
                    exchangeCoin()
                }
            }
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
        children.forEach {
            it.fitsSystemWindows = fitsSystemWindows
        }
    }

    private fun initView() {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mIconHeight)

        addView(mCenterView)
        addView(mIconView)
        addView(mIconEndView)
        addView(mCoinView)
    }

    private fun initCardView(width: Int, height: Int, gravity: Int = Gravity.START): CardImageView {
        return CardImageView(context).apply {
            layoutParams = LayoutParams(width, height).apply {
                this.gravity = gravity
            }
        }
    }

    private fun initIconView(width: Int, height: Int, gravity: Int = Gravity.START): ImageView {
        return ImageView(context).apply {
            layoutParams = LayoutParams(width, height).apply {
                this.gravity = gravity
            }
        }
    }

    private fun initCoinView(): TextView {
        return TextView(context).apply {
            layoutParams = LayoutParams(mCoinWidth, mCoinHeight).apply {
                gravity = Gravity.END or Gravity.CENTER_VERTICAL
            }
            gravity = Gravity.CENTER_VERTICAL
            getDrawable(R.drawable.ic_coin_icon)?.apply {
                setBounds(0, 0, mCoinHeight, mCoinHeight)
                setCompoundDrawables(this, null, null, null)
            }
            gone()
        }
    }

    private fun exchangeCard() {
        data?.apply {
            mCoinView.gone()
            if (desCard == null) {
                mIconEndView.gone()
                mCenterView.gone()
            } else {
                mIconEndView.visible()
                mCenterView.visible()
            }

            mIconView.card = srcCard
            mIconEndView.card = desCard
        }
    }

    private fun exchangeCoin() {
        data?.apply {
            mIconEndView.gone()
            mCenterView.visible()
            mCoinView.visible()

            mIconView.card = srcCard
            mCoinView.text = coin?.toString() ?: "0"
        }
    }

    companion object {
        const val EXCHANGE_CARD: Int = 1 // 交换卡
        const val EXCHANGE_COIN: Int = 2 // 交换银币
    }

    data class Data(
            @ExchangeType var exchangeType: Int = EXCHANGE_CARD,
            var userInfo: UserInfo? = null,
            var srcCard: Card? = null,
            var desCard: Card? = null,
            var coin: Long? = 0,
            var itemData: ItemData? = null
    )

}