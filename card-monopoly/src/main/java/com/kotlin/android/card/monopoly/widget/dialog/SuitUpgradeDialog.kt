package com.kotlin.android.card.monopoly.widget.dialog

import android.view.WindowManager
import androidx.fragment.app.viewModels
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.databinding.DialogSuitUpgradeBinding
import com.kotlin.android.card.monopoly.ui.CardMonopolyApiViewModel
import com.kotlin.android.card.monopoly.widget.dialog.view.RewardView
import com.kotlin.android.core.BaseVMDialogFragment
import com.kotlin.android.app.data.entity.monopoly.Suit
import com.kotlin.android.app.data.entity.monopoly.UpgradeSuit
import com.kotlin.android.core.DialogStyle
import kotlinx.android.synthetic.main.dialog_suit_upgrade.*

/**
 * 卡片大富翁套装升级成功对话框：
 *
 * Created on 2020/9/25.
 *
 * @author o.s
 */
class SuitUpgradeDialog : BaseVMDialogFragment<CardMonopolyApiViewModel, DialogSuitUpgradeBinding>() {

    var event: (() -> Unit)? = null

    var data: UpgradeSuit? = null
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
        rewardView?.apply {
            data?.apply {

            }
        }
    }

    override fun initData() {
    }

    override fun startObserve() {
    }

    private fun fillData() {
        data?.apply {
            suitImageView?.data = Suit(suitCover = suitCover)
            titleView?.text = suitName.orEmpty()
            subTitleView?.text = getString(R.string.upgrade_suit_, suitClassText.orEmpty())
            rewardView?.apply {
                data = RewardView.Data(rewardToolInfo, rewardGold)
            }
        }
    }
}