package com.kotlin.android.card.monopoly.widget.dialog

import androidx.annotation.IntRange
import androidx.fragment.app.viewModels
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.constants.Constants
import com.kotlin.android.card.monopoly.databinding.DialogPocketCardBinding
import com.kotlin.android.card.monopoly.ui.CardMonopolyApiViewModel
import com.kotlin.android.card.monopoly.widget.card.view.SelectCardView
import com.kotlin.android.core.BaseVMDialogFragment
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.app.data.entity.monopoly.PropCard
import com.kotlin.android.core.DialogStyle
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.span.toColor
import com.kotlin.android.ktx.ext.span.toSpan
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import kotlinx.android.synthetic.main.dialog_pocket_card.*
import java.lang.StringBuilder

/**
 * 卡片大富翁卡片对话框：
 *
 * Created on 2020/9/25.
 *
 * @author o.s
 */
class PocketCardDialog : BaseVMDialogFragment<CardMonopolyApiViewModel, DialogPocketCardBinding>() {

    private val mCards = ArrayList<Card>()

    private var mCardList: List<Card>? = null
        set(value) {
            field = value
            value?.let {
                mCards.addAll(0, it)
            }
        }

    private var mPropCardList: List<PropCard>? = null
        set(value) {
            field = value
            value?.apply {
                if (Style.USE_PROP_CARD == style) {
                    value.forEach {
                        if (it.useType == 2L) {
                            mCards.add(
                                    Card(
                                            cardId = it.toolId,
                                            cardName = it.toolName,
                                            cardCover = it.toolCover,
                                            isPropCard = true
                                    )
                            )
                        }
                    }
                } else {
                    mCards.addAll(value.map {
                        Card(
                                cardId = it.toolId,
                                cardName = it.toolName,
                                cardCover = it.toolCover,
                                isPropCard = true
                        )
                    })
                }
            }
        }

    /**
     * 样式：卡片、道具卡、卡片和道具卡混合
     */
    var style: Style = Style.CARD
        set(value) {
            field = value
            mCards.clear()
            loadData()
        }

    /**
     * 选择模式
     */
    var selectModel: SelectCardView.SelectModel = SelectCardView.SelectModel.SINGLE
        set(value) {
            field = value
            selectCardView?.selectModel = value
        }

    var event: ((selectedCards: List<Card>?) -> Unit)? = null

    var selectedCards: List<Card>? = null

    var data: Data? = null
        set(value) {
            field = value
            mCardList = value?.cardList
            fillData()
        }

    override fun initEnv() {
        dialogStyle = DialogStyle.CENTER
    }

    override fun initVM(): CardMonopolyApiViewModel = viewModels<CardMonopolyApiViewModel>().value

    override fun initView() {
        closeView?.setOnClickListener {
            dismissAllowingStateLoss()
        }
        actionView?.apply {
            setBackground(
                    colorRes = R.color.color_feb12a,
                    cornerRadius = 19.dpF
            )
            setOnClickListener {
                selectCardView?.apply {
                    this@PocketCardDialog.selectedCards = selectedCards
                    if (!selectedCards.isNullOrEmpty()) {
//                        this@PocketCardDialog.data?.apply {
//                            mViewModel?.discard(selectedCards[0].userCardId ?: 0L, friendId, isRobot)
//                        }
                        event?.invoke(selectedCards)
                        dismissAllowingStateLoss()
                    } else {
                        showToast(R.string.please_choose_a_card)
                    }
                }
            }
        }
    }

    override fun initData() {
    }

    private fun loadData() {
        when (style) {
            Style.CARD -> {
                mViewModel?.myPocketCards()
            }
            Style.TA_CARD -> {
                data?.friendId?.apply {
                    mViewModel?.friendNormalCards(friendId = this)
                }
            }
            Style.TA_LIMIT_CARD -> {
                data?.friendId?.apply {
                    mViewModel?.friendLimitCards(friendId = this)
                }
            }
            Style.PROP_CARD -> {
                mViewModel?.myPropCards()
            }
            Style.USE_PROP_CARD -> {
                mViewModel?.myPropCards()
            }
            Style.CARD_AND_PROP_CARD -> {
                mViewModel?.myPocketCards()
                mViewModel?.myPropCards()
            }
        }
    }

    override fun startObserve() {
        mViewModel?.apply {
            myPocketCardsUiState.observe(this@PocketCardDialog) {
                it?.apply {
                    showProgressDialog(showLoading)

                    success?.apply {
                        mCardList = cardList
                        updateUI()
                    }
                }
            }

            friendNormalCardsUiState.observe(this@PocketCardDialog) {
                it?.apply {
                    showProgressDialog(showLoading)

                    success?.apply {
                        mCardList = this.cardList
                        updateUI()
                    }
                }
            }

            myPropCardsUiState.observe(this@PocketCardDialog) {
                it?.apply {
                    showProgressDialog(showLoading)

                    success?.apply {
                        mPropCardList = if (style == Style.CARD_AND_PROP_CARD) {
                            toolCardList?.filter { propCard ->
                                propCard.toolId == Constants.TOOLS_CARD_BAGPOS || propCard.toolId == Constants.TOOLS_CARD_BAG
                                        || propCard.toolId == Constants.TOOlS_CARD_ROB_LIMIT
                                        || propCard.toolId == Constants.TOOLS_CARD_DISMANTLE
                                        || propCard.toolId == Constants.TOOlS_CARD_HIDE

                            }
                        } else {
                            toolCardList
                        }
                        updateUI()
                    }
                }
            }

//            drawBoxModelUiState.observe(this@PocketCardDialog) {
//                it?.apply {
//                    showProgressDialog(showLoading)
//
//                    success?.apply {
//                        when (bizCode) {
//                            1L -> {
//                                // 领取成功，回调并关闭对话框
//                                event?.invoke(this@PocketCardDialog, pocketCards)
//                                dismissAllowingStateLoss()
//                            }
//                            -4L, -6L -> {
//                                showClearPocketDialog(bizMessage ?: getString(R.string.pocket_is_full))
//                            }
//                            else -> {
//                                showToast(bizMessage)
//                            }
//                        }
//                    }
//                }
//            }
//
//            discardUiState.observe(this@PocketCardDialog) {
//                it?.apply {
//                    showProgressDialog(showLoading)
//
//                    success?.apply {
//                        when (bizCode) {
//                            1L -> {
//                                showToast(bizMessage)
//                                event?.invoke(this@PocketCardDialog, openPocketCards)
//                                dismissAllowingStateLoss()
//                            }
//                            else -> {
//                                showToast(bizMessage)
//                            }
//                        }
//                    }
//                }
//            }
        }

    }

    private fun fillData() {
//        updateTitleUI()
        updateUI()
    }

    private fun updateTitleUI() {
        titleView?.apply {
            val count = "${mCardList?.size ?: 0}".toSpan()
                    .toColor(color = getColor(R.color.color_feb12a))
            val coin = "${data?.gold}".toSpan()
                    .toColor(color = getColor(R.color.color_feb12a))
            text = "获得卡片 ".toSpan()
                    .append(count)
                    .append(" 张 金币 ")
                    .append(coin)
        }
    }

    private fun updateUI() {
        selectCardView?.apply {
            data = mCards
        }
    }

    /**
     * 领取宝箱
     */
    private fun drawBox(selectedCards: List<Card>) {
        val cardIds = StringBuilder()
        selectedCards.forEachIndexed { index, card ->
            if (index > 0) {
                cardIds.append(",")
            }
            cardIds.append(card.cardId)
        }
        data?.position?.apply {
            mViewModel?.drawBox(this, cardIds.toString())
        }
    }

    data class Data(
            @IntRange(from = 0, to = 4) var position: Int = 0,
            var gold: Long = 0L,
            var cardList: List<Card>? = null,
            var card: Card? = null,
            var friendId: Long = 0,
            var isRobot: Boolean = false
    )

    /**
     * 样式：卡片、道具卡、卡片和道具卡混合
     */
    enum class Style {
        /**
         * 卡片
         */
        CARD,

        /**
         * TA的卡片
         */
        TA_CARD,

        /**
         * TA的限量卡片
         */
        TA_LIMIT_CARD,

        /**
         * 道具卡
         */
        PROP_CARD,

        /**
         * 对TA使用的道具列表
         */
        USE_PROP_CARD,

        /**
         * 我的卡片和道具卡
         */
        CARD_AND_PROP_CARD
    }
}