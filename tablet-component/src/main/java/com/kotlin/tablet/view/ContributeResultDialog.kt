package com.kotlin.tablet.view

import android.view.View
import com.kotlin.android.core.BaseVMDialogFragment
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.dimension.screenWidth
import com.kotlin.tablet.R
import com.kotlin.tablet.databinding.DialogContributeResultBinding
import com.kotlin.tablet.ui.mycreate.MyCreateActivity

const val TAG_FRAGMENT_CONTRIBUTE_RESULT = "tag_fragment_contribute_result"
fun MyCreateActivity.showContributeResultDialog(
    isSuccess: Boolean?,
    bizMsg: String? = "",
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null
): ContributeResultDialog {
    return getOrGenerateContributeResultDialog(isSuccess, bizMsg).apply {
        setCancelable(isCancelable)
        this.dismiss = dismiss
    }
}

fun MyCreateActivity.getOrGenerateContributeResultDialog(
    isSuccess: Boolean?,
    bizMsg: String?
): ContributeResultDialog {
    var fragment = getContributeResultDialog()
    if (fragment == null) {
        fragment = ContributeResultDialog(isSuccess ?: true, bizMsg).apply {
            showNow(supportFragmentManager, TAG_FRAGMENT_CONTRIBUTE_RESULT)
        }
    }
    return fragment
}

fun MyCreateActivity.getContributeResultDialog(): ContributeResultDialog? {
    return supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_CONTRIBUTE_RESULT) as? ContributeResultDialog
}

/**
 * 创建者: SunHao
 * 创建时间: 2022/3/28
 * 描述:投稿结果
 **/
class ContributeResultDialog(val isSuccess: Boolean, val bizMsg: String?) :
    BaseVMDialogFragment<BaseViewModel, DialogContributeResultBinding>() {
    private val dialogWidthRatio = 0.8F
    private var backAction: (() -> Unit)? = null
    private var repeatAction: (() -> Unit)? = null
    private var goOnAction: (() -> Unit)? = null

    override fun initEnv() {
        theme = {
            setStyle(STYLE_NORMAL, R.style.common_dialog)
        }
        window = {
        }
    }

    override fun initView() {
        initUI()
        initEvent()
    }

    private fun initUI() {
        mBinding?.apply {
            mContributeResultLay.layoutParams.width = (dialogWidthRatio * screenWidth).toInt()
            mContributeResultLay.setBackground(
                colorRes = R.color.color_ffffff,
                cornerRadius = 12.dpF
            )
            mContributeSuccessIv.loadImage(
                if (isSuccess) R.drawable.ic_film_list_success_bg else R.drawable.ic_film_list_success_bg,
                width = 56,
                height = 56
            )
            mContributeSuccessTv.text =
                if (isSuccess) getString(R.string.tablet_film_list_contribute_over) else getString(R.string.tablet_film_list_contribute_error)
            mContributeContentTv.text =
                if (isSuccess) {
                    getString(R.string.tablet_film_list_contribute_content_over)
                } else {
                    bizMsg ?: getString(R.string.tablet_film_list_contribute_content_error)
                }
            mRepeatBtn.visibility = if (isSuccess) View.GONE else View.VISIBLE
            mGoOnBtn.visibility = if (isSuccess) View.VISIBLE else View.GONE

            mBackBtn.setBackground(
                strokeColorRes = R.color.color_3d4955,
                strokeWidth = 1,
                cornerRadius = 18.dpF
            )
            mRepeatBtn.setBackground(colorRes = R.color.color_20a0da, cornerRadius = 18.dpF)
            mGoOnBtn.setBackground(colorRes = R.color.color_20a0da, cornerRadius = 18.dpF)
        }
    }

    private fun initEvent() {
        mBinding?.apply {
            mBackBtn.onClick {
                dialog?.dismiss()
                backAction?.invoke()
            }
            mGoOnBtn.onClick {
                dialog?.dismiss()
                goOnAction?.invoke()
            }
            mRepeatBtn.onClick {
                dialog?.dismiss()
                repeatAction?.invoke()
            }
        }
    }

    override fun initData() {

    }

    override fun startObserve() {
    }

    fun back(backAction: () -> Unit) = apply {
        this.backAction = backAction
    }

    fun goOn(goOnAction: () -> Unit) = apply {
        this.goOnAction = goOnAction
    }

    fun repeat(repeatAction: () -> Unit) = apply {
        this.repeatAction = repeatAction
    }
}