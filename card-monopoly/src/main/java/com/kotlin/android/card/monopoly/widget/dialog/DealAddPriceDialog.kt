package com.kotlin.android.card.monopoly.widget.dialog

import android.text.InputType
import androidx.fragment.app.viewModels
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.databinding.DialogDealAddPriceBinding
import com.kotlin.android.card.monopoly.ext.showCardMonopolyCommDialog
import com.kotlin.android.card.monopoly.ui.CardMonopolyApiViewModel
import com.kotlin.android.card.monopoly.widget.dialog.view.DealCardView
import com.kotlin.android.core.BaseVMDialogFragment
import com.kotlin.android.core.DialogStyle
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import kotlinx.android.synthetic.main.dialog_deal_add_price.*

/**
 * 卡片大富翁交易对话框：
 *
 * Created on 2020/9/25.
 *
 * @author o.s
 */
class DealAddPriceDialog : BaseVMDialogFragment<CardMonopolyApiViewModel, DialogDealAddPriceBinding>() {

    var event: ((bizCode: Long) -> Unit)? = null

    var data: DealCardView.Data? = null
        set(value) {
            field = value
            fillData()
        }

    private fun fillData() {
    }

    override fun initEnv() {
        dialogStyle = DialogStyle.CENTER
    }

    override fun initVM(): CardMonopolyApiViewModel = viewModels<CardMonopolyApiViewModel>().value

    override fun initView() {
        closeView?.setOnClickListener {
            dismissAllowingStateLoss()
        }
        postscriptView?.apply {
            setBackground(
                    colorRes = R.color.color_ffffff,
                    strokeColorRes = R.color.color_36c096,
                    strokeWidth = 1.dp,
                    cornerRadius = 19.dpF
            )
            hint = getString(R.string.postscript)
            numProgressView?.limit = maxLength
            inputChange = {
                numProgressView.progress = it
            }
        }
        coinView?.apply {
            setBackground(
                    colorRes = R.color.color_ffffff,
                    strokeColorRes = R.color.color_36c096,
                    strokeWidth = 1.dp,
                    cornerRadius = 19.dpF
            )
            maxLength = 11
            hint = getString(R.string.add_price)
            unit = getString(R.string.g)
            inputType = InputType.TYPE_CLASS_NUMBER
        }

        actionView?.apply {
            setBackground(
                    colorRes = R.color.color_feb12a,
                    cornerRadius = 19.dpF
            )
            setOnClickListener {
                addTradePrice()
            }
        }
    }

    override fun initData() {
    }

    override fun startObserve() {
        mViewModel?.tradeRaisePriceUiState?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    when (bizCode) {
                        -3L -> {
                            event?.invoke(bizCode)
                            dismissAllowingStateLoss()
                            showCardMonopolyCommDialog(
                                    data = CommDialog.Data(
                                            title = "交易加价",
                                            message = bizMessage
                                    )
                            )
                        }
                        else -> {
                            showToast(bizMessage)
                            event?.invoke(bizCode)
                            dismissAllowingStateLoss()
                        }
                    }
                }

                error?.apply {
                    showToast(this)
                }

                netError?.apply {
                    showToast(this)
                }
            }
        }
    }

    /**
     * 交易加价
     */
    private fun addTradePrice() {
        data?.itemData?.apply {
            val coinStr = coinView?.text
            val coin = if (!coinStr.isNullOrEmpty()) {
                coinStr.toLong()
            } else {
                0L
            }
            mViewModel?.addTradePrice(
                    recordDetailId = recordDetail ?: 0L,
                    raiseGolds = coin,
                    message = postscriptView?.text ?: ""
            )
        }
    }
}