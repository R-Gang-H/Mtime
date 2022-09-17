package com.kotlin.android.card.monopoly.widget.dialog

import androidx.fragment.app.viewModels
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.databinding.DialogSuitComposeBinding
import com.kotlin.android.card.monopoly.ui.CardMonopolyApiViewModel
import com.kotlin.android.card.monopoly.widget.dialog.view.RewardView
import com.kotlin.android.core.BaseVMDialogFragment
import com.kotlin.android.app.data.entity.monopoly.MixSuit
import com.kotlin.android.app.data.entity.monopoly.Suit
import com.kotlin.android.app.data.entity.monopoly.UserInfo
import com.kotlin.android.core.DialogStyle
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.span.toBold
import com.kotlin.android.ktx.ext.span.toColor
import com.kotlin.android.ktx.ext.span.toSpan

import kotlinx.android.synthetic.main.dialog_suit_compose.*
import kotlinx.android.synthetic.main.dialog_suit_compose.closeView

/**
 * 卡片大富翁套装合成成功对话框：
 *
 * Created on 2020/9/25.
 *
 * @author o.s
 */
class SuitComposeDialog : BaseVMDialogFragment<CardMonopolyApiViewModel, DialogSuitComposeBinding>() {

    var close: (() -> Unit)? = null
    var event: (() -> Unit)? = null

    var data: MixSuit? = null
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
            close?.invoke()
            dismissAllowingStateLoss()
        }
        actionView?.apply {
            setBackground(
                    colorRes = R.color.color_feb12a,
                    cornerRadius = 19.dpF
            )
            setOnClickListener {
                event?.invoke()
                dismissAllowingStateLoss()
            }
        }
    }

    override fun initData() {
    }

    override fun startObserve() {

    }

    private fun fillData() {
        data?.apply {
            suitImageView?.apply {
                data = Suit(suitCover = suitCover)
            }
            titleView?.apply {
                text = getString(R.string.title_compose_suit, suitName)
            }
            suitCardView?.apply {
                data = cardList
            }
            rewardView?.apply {
                val mixGold = rewardGoldDetail?.mixGold ?: 0L
                val mammonGold = rewardGoldDetail?.mammonGold ?: 0L
                val slaveGold = rewardGoldDetail?.slaveGold ?: 0L
                val desc = "恭喜你合成套装，奖励金币 ".toSpan().append(
                        "$mixGold".toSpan().toBold().toColor(color = getColor(R.color.color_feb12a))
                )
                if (mammonGold > 0L) {
                    desc.append("，额外奖励金币 ").append(
                            "$mammonGold".toSpan().toBold().toColor(color = getColor(R.color.color_feb12a))
                    ).append("（财神卡效果）")
                }
                if (slaveGold > 0L) {
                    desc.append("，额外扣除金币 ").append(
                            "$slaveGold".toSpan().toBold().toColor(color = getColor(R.color.color_feb12a))
                    ).append("（奴隶卡效果）")
                }

                data = RewardView.Data(rewardToolInfo, rewardGold, desc)
            }
        }
    }

    data class Data(
            val userInfo: UserInfo? = null,
            val mixSuit: MixSuit? = null
    )
}