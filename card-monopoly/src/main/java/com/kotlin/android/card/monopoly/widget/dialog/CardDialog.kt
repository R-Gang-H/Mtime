package com.kotlin.android.card.monopoly.widget.dialog

import androidx.annotation.IntRange
import androidx.fragment.app.viewModels
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.databinding.DialogCardBinding
import com.kotlin.android.card.monopoly.ext.showCardMonopolyCommDialog
import com.kotlin.android.card.monopoly.ext.showClearPocketDialog
import com.kotlin.android.card.monopoly.ui.CardMonopolyApiViewModel
import com.kotlin.android.card.monopoly.widget.card.view.SelectCardView
import com.kotlin.android.core.BaseVMDialogFragment
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.app.data.entity.monopoly.PocketCards
import com.kotlin.android.core.DialogStyle
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.span.toColor
import com.kotlin.android.ktx.ext.span.toSpan
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import kotlinx.android.synthetic.main.dialog_card.actionView
import kotlinx.android.synthetic.main.dialog_card.closeView
import kotlinx.android.synthetic.main.dialog_card.selectCardView
import kotlinx.android.synthetic.main.dialog_card.titleView
import java.lang.StringBuilder

/**
 * 卡片大富翁卡片对话框：
 *
 * Created on 2020/9/25.
 *
 * @author o.s
 */
class CardDialog : BaseVMDialogFragment<CardMonopolyApiViewModel, DialogCardBinding>() {

    var event: ((pocketCards: PocketCards?) -> Unit)? = null
    var clearMainPocket: (() -> Unit)? = null

    var data: Data? = null
        set(value) {
            field = value
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
                    drawBox(selectedCards)
                }
            }
        }
        selectCardView?.selectModel = SelectCardView.SelectModel.MULTIPART
    }

    override fun initData() {
    }

    override fun startObserve() {
        mViewModel?.drawBoxModelUiState?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    when (bizCode) {
                        1L -> {
                            // 领取成功，回调并关闭对话框
                            event?.invoke(pocketCards)
                            showToast(bizMessage)
                            dismissAllowingStateLoss()
                        }
                        -4L,-6L -> {
                            dismissAllowingStateLoss()
                            showClearPocketDialog(
                                    message = bizMessage ?: getString(R.string.pocket_is_full),
                                    isCardMainPage = clearMainPocket != null
                            ) {
                                clearMainPocket?.invoke()
                            }
                        }
                        else -> {
                            show()
                            showToast(bizMessage)
                        }
                    }
                }

                error?.apply {
                    dismissAllowingStateLoss()
                }

                netError?.apply {
                    dismissAllowingStateLoss()
                }
            }
        }
    }

    private fun fillData() {
        data?.apply {
            selectCardView?.data = cardList
            titleView?.apply {
                val count = "${cardList?.size ?: 0}".toSpan()
                        .toColor(color = getColor(R.color.color_feb12a))
                val coin = "$gold".toSpan()
                        .toColor(color = getColor(R.color.color_feb12a))
                text = "获得卡片 ".toSpan()
                        .append(count)
                        .append(" 张 金币 ")
                        .append(coin)
            }
        }
    }

    /**
     * 领取宝箱
     */
    private fun drawBox(selectedCards: List<Card>) {
        if (data?.cardList?.isNullOrEmpty() != false || !selectedCards.isNullOrEmpty()) {
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
        } else {
            hide()
            showCardMonopolyCommDialog(
                    style = CommDialog.Style.DRAW_BOX,
                    data = CommDialog.Data(message = getString(R.string.warning_not_pick_card)),
                    close = {
                        show()
                    }
            ) {
                data?.position?.apply {
                    mViewModel?.drawBox(this, "")
                }
            }
//            showDialog(
//                    context = context,
//                    content = R.string.warning_not_pick_card,
//                    negativeAction = {
//                        show()
//                    }
//            ) {
////                show()
//                data?.position?.apply {
//                    mViewModel?.drawBox(this, "")
//                }
//            }
        }
    }

    data class Data(
            @IntRange(from = 0, to = 4) var position: Int = 0,
            var gold: Long = 0L,
            var cardList: List<Card>? = null
    )
}