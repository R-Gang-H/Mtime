package com.kotlin.android.card.monopoly.widget.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.util.Range
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.app.data.entity.monopoly.PropCard
import com.kotlin.android.ktx.ext.span.toColor
import com.kotlin.android.ktx.ext.span.toSpan
import com.kotlin.android.mtime.ktx.getColor
import kotlinx.android.synthetic.main.dialog_use_prop.*

/**
 * 卡片大富翁打开宝箱对话框：
 *
 * Created on 2020/9/22.
 *
 * @author o.s
 */
class UsePropDialog : DialogFragment() {

    var dismiss: (() -> Unit)? = null

    var event: (() -> Unit)? = null

    var data: Data? = null
        set(value) {
            field = value
            fillData()
        }

    private fun fillData() {
        data?.apply {
            changeState()
        }
    }

    fun show() {
        dialog?.show()
    }

    fun hide() {
        dialog?.hide()
    }

    override fun setCancelable(cancelable: Boolean) {
        super.setCancelable(cancelable)
        dialog?.setCanceledOnTouchOutside(cancelable)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismiss?.invoke()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.apply {
            decorView.setPadding(0, 0, 0, 0)
            setLayout(WindowManager.LayoutParams.MATCH_PARENT,  WindowManager.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(null)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_use_prop, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        closeView?.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

    private fun changeState() {
        data?.apply {
            if (isSuccess == true) {
                panelView.setBackgroundResource(R.mipmap.ic_dialog_panel_prop_success)
                titleView.setTextColor(getColor(R.color.color_ffffff))
                descView.setTextColor(getColor(R.color.color_ffffff))
                divideView.setBackgroundColor(getColor(R.color.color_ffffff))
            } else {
                panelView.setBackgroundResource(R.mipmap.ic_dialog_panel_prop_failed)
                titleView.setTextColor(getColor(R.color.color_1d2736))
                descView.setTextColor(getColor(R.color.color_1d2736))
                divideView.setBackgroundColor(getColor(R.color.color_1d2736))
            }
            //图片加载
            cardImageView?.card = card ?: Card(cardCover = propCard?.toolCover)
            descView?.text = message ?: ""
            val name = card?.cardName ?: propCard?.toolName ?: ""
            titleView?.text = if (isSuccess == true) {
                getString(R.string.use_prop_success_, name)
                        .toSpan()
                        .toColor(Range(0, name.length), color = getColor(R.color.color_fee62e))
            } else {
                getString(R.string.use_prop_failed_, name)
                        .toSpan()
                        .toColor(Range(0, name.length), color = getColor(R.color.color_20a0da))
            }

        }
    }

    data class Data(
            var propCard: PropCard? = null,
            var card: Card? = null,
            var isSuccess: Boolean? = false,
            var message: String? = null
    )
}