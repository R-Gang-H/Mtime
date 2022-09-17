package com.kotlin.tablet.view

import android.text.Html
import android.text.method.ScrollingMovementMethod
import androidx.fragment.app.FragmentActivity
import com.kotlin.android.core.BaseVMDialogFragment
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.dimension.screenWidth
import com.kotlin.tablet.R
import com.kotlin.tablet.databinding.DialogContributeInfoBinding

const val TAG_FRAGMENT_CONTRIBUTE_INFO = "tag_fragment_contribute_info"

fun FragmentActivity.showContributeDialog(
    title: String?,
    content: String?,
    isCancelable: Boolean = true,
    dismiss: (() -> Unit)? = null
): ContributeInfoDialog {
    return getOrGenerateContributeDialog().apply {
        setCancelable(isCancelable)
        this.title = title
        this.content = content
        this.dismiss = dismiss
        setData()
    }
}

fun FragmentActivity.getOrGenerateContributeDialog(): ContributeInfoDialog {
    var fragment = getContributeDialog()
    if (fragment == null) {
        fragment = ContributeInfoDialog().apply {
            showNow(supportFragmentManager, TAG_FRAGMENT_CONTRIBUTE_INFO)
        }
    }
    return fragment
}

fun FragmentActivity.getContributeDialog(): ContributeInfoDialog? {
    return supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_CONTRIBUTE_INFO) as? ContributeInfoDialog
}

/**
 * 创建者: SunHao
 * 创建时间: 2022/3/25
 * 描述:投稿说明、入围达人说明Dialog
 **/
class ContributeInfoDialog() :
    BaseVMDialogFragment<BaseViewModel, DialogContributeInfoBinding>() {
    private val dialogWidthRatio = 0.87F
    var title: String? = ""
    var content: String? = ""

    override fun initEnv() {
        theme = {
            setStyle(STYLE_NORMAL, R.style.common_dialog)
        }
        window = {
        }
//        immersive = {
//            immersive().transparentStatusBar()
//        }
    }

    override fun initView() {
        mBinding?.apply {
            mHostView.layoutParams.width = (dialogWidthRatio * screenWidth).toInt()
            mContributeContentTv.movementMethod = ScrollingMovementMethod.getInstance()
            mContentLay.setBackground(
                colorRes = R.color.color_f9ffde,
                endColorRes = R.color.color_ffffff,
                cornerRadius = 12.dpF
            )
            mInfoClose.onClick {
                dialog?.dismiss()
            }
        }
    }

    override fun initData() {
    }

    override fun startObserve() {
    }

    fun setData(){
        mBinding?.apply {
            title?.let {
                mContributeTitleTv.text = it
            }
            content?.let {
                mContributeContentTv.text = Html.fromHtml(it)
            }
        }
    }
}