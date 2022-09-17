package com.kotlin.android.mine.ui.widgets.dialog

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.mine.R
import com.kotlin.android.mine.bean.GoodsViewBean
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.formatCount
import kotlinx.android.synthetic.main.dialog_m_bean_exchange.*

/**
 * create by lushan on 2020/12/24
 * description:M豆兑换弹框`
 */
class ExchangeDialog() : DialogFragment() {

    var data: GoodsViewBean? = null
        set(value) {
            field = value
            fillData()
        }

    var action: ((GoodsViewBean?) -> Unit)? = null

    private fun fillData() {
        data?.apply {
            titleTv?.text = this.name
            goodsNumTv?.text = "1"//只能兑换一个
            //最大支持9999999的显示，完整显示，超过这个数字，依然显示9999999
            mBeanNumTv?.text = if (mNeedNum >= 9999999) "9999999" else mNeedNum.toString()
            initView()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ImmersiveDialog)
        isCancelable = true

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dialog?.apply {
            window?.apply {
                decorView?.setBackgroundColor(Color.TRANSPARENT)
                setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
                setWindowAnimations(R.style.BottomDialogAnimation)
            }
            isCancelable = true
            setCanceledOnTouchOutside(true)
        }
//        initView()
    }

    private fun initView() {
        cancelTv?.apply {
            ShapeExt.setShapeCorner2Color2Stroke(this, corner = 25, strokeColor = R.color.color_20a0da, strokeWidth = 1.dp)
            onClick {
                dismiss()
            }
        }
        sureTv?.apply {
            ShapeExt.setShapeCorner2Color(this, corner = 25, solidColor = R.color.color_20a0da)
            onClick {
                action?.invoke(data)
            }
        }
        ShapeExt.setShapeColorAndCorner(rootView, R.color.color_ffffff, 5)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_m_bean_exchange, container, false)
    }

}


fun FragmentActivity.showExchangeDialog(bean: GoodsViewBean, listener: ((GoodsViewBean?) -> Unit)? = null): ExchangeDialog {
    return getOrGenerateExchangeDialog().apply {
        action = listener
        data = bean
    }

}

fun FragmentActivity.getOrGenerateExchangeDialog(): ExchangeDialog {
    var fragment = getExchangeDialog()
    if (fragment == null) {
        fragment = ExchangeDialog()
        fragment?.showNow(supportFragmentManager, TAG_FRAGMENT_MEMBER_EXCHANGE_DIALOG)
    }
    return fragment
}

fun FragmentActivity.getExchangeDialog(): ExchangeDialog? {
    return supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_MEMBER_EXCHANGE_DIALOG) as? ExchangeDialog
}

const val TAG_FRAGMENT_MEMBER_EXCHANGE_DIALOG = "tag_fragment_member_exchange_dialog"

fun FragmentActivity.dismissExchangeDialog() {
    getExchangeDialog()?.dismiss()
}