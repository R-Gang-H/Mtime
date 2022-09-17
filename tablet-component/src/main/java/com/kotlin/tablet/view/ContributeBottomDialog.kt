package com.kotlin.tablet.view

import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.android.core.BaseVMDialogFragment
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.image.component.R
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.Direction
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.tablet.adapter.ContributeBottomBinder
import com.kotlin.tablet.databinding.DialogContributeBottomBinding

fun FragmentActivity.showContributeBottomDialog(
    data: MutableList<String>,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
    selectAction: ((Int) -> Unit)? = null
): ContributeBottomDialog {
    return getOrGenerateContributeBottomDialog().apply {
        setCancelable(isCancelable)
        this.selectAction = selectAction
        this.dismiss = dismiss
        setData(data)
    }
}

fun FragmentActivity.getOrGenerateContributeBottomDialog(): ContributeBottomDialog {
    var fragment = getContributeBottomDialog()
    if (fragment == null) {
        fragment = ContributeBottomDialog().apply {
            showNow(supportFragmentManager, TAG_FRAGMENT_CONTRIBUTE_INFO)
        }
    }
    return fragment
}

fun FragmentActivity.getContributeBottomDialog(): ContributeBottomDialog? {
    return supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_CONTRIBUTE_INFO) as? ContributeBottomDialog
}

/**
 * 创建者: SunHao
 * 创建时间: 2022/3/25
 * 描述:投稿底部弹出框
 **/
class ContributeBottomDialog :
    BaseVMDialogFragment<BaseViewModel, DialogContributeBottomBinding>() {
    private lateinit var mAdapter: MultiTypeAdapter
    var selectAction: ((Int) -> Unit)? = null
    override fun initEnv() {
        theme = {
            setStyle(STYLE_NORMAL, R.style.ImmersiveDialog)
        }
        window = {
            setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.BOTTOM)
            setWindowAnimations(R.style.BottomDialogAnimation)
        }
        immersive = {
//            immersive().transparentStatusBar(true,true)
        }
    }

    override fun initView() {
        mBinding?.apply {
            mBottomRv.addItemDecoration(
                VerticalItemDecoration(
                    edge = 1.dpF,
                    edgeColorRes = com.kotlin.tablet.R.color.color_f3f3f4
                )
            )
            mBottomLay.setBackground(
                colorRes = R.color.color_ffffff,
                cornerRadius = 12.dpF,
                direction = Direction.LEFT_TOP or Direction.RIGHT_TOP
            )
            mCancelTv.onClick {
                dismiss()
            }
            mAdapter =
                createMultiTypeAdapter(mBottomRv, LinearLayoutManager(context)).setOnClickListener(
                    ::handClick
                )
        }
    }

    private fun handClick(view: View, multiTypeBinder: MultiTypeBinder<*>) {
        if (multiTypeBinder is ContributeBottomBinder) {
            dialog?.dismiss()
            selectAction?.invoke(multiTypeBinder.mPosition)
        }
    }

    fun setData(data: MutableList<String>) {
        mAdapter.notifyAdapterDataSetChanged(data.map {
            ContributeBottomBinder(it)
        })
    }

    override fun initData() {
    }

    override fun startObserve() {
    }
}
