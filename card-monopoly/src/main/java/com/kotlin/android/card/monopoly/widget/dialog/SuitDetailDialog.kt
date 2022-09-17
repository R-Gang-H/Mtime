package com.kotlin.android.card.monopoly.widget.dialog

import androidx.fragment.app.viewModels
import com.kotlin.android.card.monopoly.BuildConfig
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.constants.Constants
import com.kotlin.android.card.monopoly.databinding.DialogSuitDetailBinding
import com.kotlin.android.card.monopoly.ext.showSuitUpgradeDialog
import com.kotlin.android.card.monopoly.ui.CardMonopolyApiViewModel
import com.kotlin.android.core.BaseVMDialogFragment
import com.kotlin.android.app.data.entity.monopoly.*
import com.kotlin.android.core.DialogStyle
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.span.toColor
import com.kotlin.android.ktx.ext.span.toSpan
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.getColor
import kotlinx.android.synthetic.main.dialog_suit_detail.*

/**
 * 卡片大富翁套装详情对话框：
 *
 * Created on 2020/9/25.
 *
 * @author o.s
 */
class SuitDetailDialog : BaseVMDialogFragment<CardMonopolyApiViewModel, DialogSuitDetailBinding>() {

    private var mSuitDetail: SuitDetail? = null
    private var mUpgradeSuit: UpgradeSuit? = null

    var event: ((data: UpgradeSuit) -> Unit)? = null

    var action: ((card: List<Card>) -> Unit)? = null

    var data: Suit? = null
        set(value) {
            field = value
            fillData()
            updateSuitView()
        }

    override fun initEnv() {
        dialogStyle = DialogStyle.CENTER
    }

    override fun initVM(): CardMonopolyApiViewModel = viewModels<CardMonopolyApiViewModel>().value

    override fun initView() {
        closeView?.setOnClickListener {
            mUpgradeSuit?.apply {
                event?.invoke(this)
            }
            dismissAllowingStateLoss()
        }
        actionView?.apply {
            isEnabled = false
            background = getDrawableStateList(
                normal = getShapeDrawable(
                    colorRes = R.color.color_feb12a,
                    cornerRadius = 19.dpF
                ),
                disable = getShapeDrawable(
                    colorRes = R.color.color_66feb12a,
                    cornerRadius = 19.dpF
                )
            )
            setOnClickListener {
                data?.apply {
                    mViewModel?.upgradeSuit(suitId, suitClass ?: "c")
                }
            }
        }
        suitCountView?.apply {
            setBackground(
                colorRes = R.color.color_f2f3f6,
                cornerRadius = 19.dpF
            )
            text = getString(R.string._suit_count, 0)
        }
    }

    override fun initData() {
    }

    override fun startObserve() {
        mViewModel?.suitDetailUiState?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)
                success?.apply {
                    mSuitDetail = this
                    updateUI()
                }
            }
        }

        mViewModel?.upgradeSuitUiState?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    when (bizCode) {
                        1L -> {
                            mUpgradeSuit = this
                            suitDetailLayerUpdateInfo?.apply {
                                updateUpgradeSuitView(suitCount, canUpgrade)
                            }
//                            hide()
//                            showSuitUpgradeDialog(
//                                data = this,
//                                dismiss = {
//                                    show()
//                                }
//                            )
                            if (BuildConfig.DEBUG) {
                                if (this.rewardToolInfo?.toolId == Constants.TOOlS_CARD_ROB_LIMIT) {
                                    showToast("打劫卡(限量)")
                                } else if (this.rewardToolInfo?.toolId == Constants.TOOlS_CARD_HIDE) {
                                    showToast("隐身卡")
                                }
                            } else {
                                hide()
                                showSuitUpgradeDialog(
                                    data = this,
                                    dismiss = {
                                        show()
                                    }
                                )
                            }
                        }
                        else -> {
                            showToast(bizMessage)
                        }
                    }
                }
            }
        }
    }

    private fun fillData() {
        data?.apply {
            mViewModel?.suitDetail(suitId, suitClass ?: "c", suitUserId ?: 0L)
        }
    }

    private fun updateUI() {
        updateSuitDetailView()
    }

    private fun updateSuitView() {
        suitImageView?.data = data
    }

    private fun updateUpgradeSuitView(suitCount: Long, canUpgrade: Boolean) {
        suitCountView?.text = getString(R.string._suit_count, suitCount)
        actionView?.isEnabled = canUpgrade
    }

    private fun updateSuitDetailView() {
        mSuitDetail?.apply {
            labelView?.text = getString(R.string.five_unit, cardCount)
            titleView?.text = suitName.orEmpty()
            subTitleView?.text = suitClassText.orEmpty()
            coinView?.text = getString(R.string.reward_gold_coin).toSpan()
                .append(
                    rewardGold.toString().toSpan()
                        .toColor(color = getColor(R.color.color_feb12a))
                )
            propView?.text = getString(R.string.reward_prop).toSpan()
                .append(
                    "x$rewardToolCount".toSpan()
                        .toColor(color = getColor(R.color.color_feb12a))
                )
            suitCardView?.apply {
                data = cardList
                action = {
                    val cardDetails = CardImageDetailBean()
                    cardDetails.card = cardList
                    if (cardList?.isNotEmpty() == true) {
                        this@SuitDetailDialog.action?.invoke(cardList!!)
                    }
                }
            }
            updateUpgradeSuitView(suitCount, canUpgrade)
        }
    }
}