package com.kotlin.android.dialog

import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import androidx.core.text.HtmlCompat
import androidx.fragment.app.FragmentActivity
import com.kotlin.android.R
import com.kotlin.android.app.data.entity.mine.HelpInfoList
import com.kotlin.android.core.BaseVMDialogFragment
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.databinding.DialogItemProblemBinding
import com.kotlin.android.databinding.DialogRankProblemBinding
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.dimension.screenWidth

const val TAG_FRAGMENT_CONTRIBUTE_INFO = "tag_activity_rank_problems"

fun FragmentActivity.showRankProblemDialog(
    title: String?,
    content: HelpInfoList?,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
): RankProblemDialog {
    return getOrGenerateContributeDialog().apply {
        setCancelable(isCancelable)
        this.title = title
        this.dismiss = dismiss
        this.content = content
        setData()
    }
}

fun FragmentActivity.getOrGenerateContributeDialog(): RankProblemDialog {
    var fragment = getContributeDialog()
    if (fragment == null) {
        fragment = RankProblemDialog().apply {
            showNow(supportFragmentManager, TAG_FRAGMENT_CONTRIBUTE_INFO)
        }
    }
    return fragment
}

fun FragmentActivity.getContributeDialog(): RankProblemDialog? {
    return supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_CONTRIBUTE_INFO) as? RankProblemDialog
}

/**
 * @Package:        com.kotlin.android.dialog
 * @ClassName:      RankProblemDialog
 * @Description:    等级相关问题 Dialog
 * @Author:         haoruigang
 * @CreateDate:     2022/3/31 15:48
 */
class RankProblemDialog() :
    BaseVMDialogFragment<BaseViewModel, DialogRankProblemBinding>() {
    private val dialogWidthRatio = 0.87F
    var title: String? = ""
    var content: HelpInfoList? = null

    override fun initEnv() {
        theme = {
            setStyle(STYLE_NORMAL, R.style.common_dialog)
        }
        window = {
        }
        /*immersive = {
            immersive().transparentStatusBar()
        }*/
    }

    override fun initView() {
        mBinding?.apply {
            mHostView.layoutParams.width = (dialogWidthRatio * screenWidth).toInt()
            mContentLay.setBackground(
                colorRes = R.color.color_ffffff,
                cornerRadius = 12.dpF)
            FlBg.setBackground(
                colorRes = R.color.color_f9ffde,
                endColorRes = R.color.color_ffffff,
                cornerRadius = 12.dpF
            )
            btnGotit.onClick {
                dialog?.dismiss()
            }
            mInfoClose.onClick {
                dialog?.dismiss()
            }
        }
    }

    override fun initData() {
    }

    override fun startObserve() {
    }

    fun setData() {
        mBinding?.apply {
            title?.let {
                tvRankCorrelator.text = it
            }
            (content as HelpInfoList).let {
                llProblems.removeAllViews()
                it.helpInfos?.forEachIndexed { index, entity ->
                    val view = DialogItemProblemBinding.inflate(LayoutInflater.from(context))
                    if (index % 2 == 0) {
                        view.vRoundDot.setBackground(colorRes = R.color.color_feb12a)
                    } else {
                        view.vRoundDot.setBackground(colorRes = R.color.color_91d959)
                    }
                    /*view.rankTitle.text = "什么是等级？"
                    view.rankContent.text = getString(R.string.rank_problem_content)*/
                    view.rankTitle.text = entity.helpName
                    view.rankContent.text = HtmlCompat.fromHtml(entity.details.toString(),
                        HtmlCompat.FROM_HTML_MODE_COMPACT)
                    view.rankContent.movementMethod = ScrollingMovementMethod.getInstance()
                    llProblems.addView(view.root)
                }
            }
        }
    }
}