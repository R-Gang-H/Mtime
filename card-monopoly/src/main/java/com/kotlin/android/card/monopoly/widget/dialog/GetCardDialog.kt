package com.kotlin.android.card.monopoly.widget.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF

import kotlinx.android.synthetic.main.dialog_get_card.*
import kotlinx.android.synthetic.main.dialog_get_card.actionView
import kotlinx.android.synthetic.main.dialog_get_card.closeView

/**
 * 卡片大富翁获得卡片对话框：
 *
 * Created on 2020/9/25.
 *
 * @author o.s
 */
class GetCardDialog : DialogFragment() {

    var dismiss: (() -> Unit)? = null

    var event: (() -> Unit)? = null

    var data: Data? = null
        set(value) {
            field = value
            fillData()
        }

    private fun fillData() {
        data?.apply {
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
        return inflater.inflate(R.layout.dialog_get_card, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        closeView?.setOnClickListener {
            dismissAllowingStateLoss()
        }
        rewardView?.apply {
//            data = RewardView.Data("", 1, 280)
        }
        selectCardView?.apply {
            data = getData()
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

    private fun getData(): ArrayList<Card> {
        return ArrayList<Card>().apply {
            (0..19).forEach { _ ->
                add(Card(0,0,0,"",""))
            }
        }
    }

    data class Data(
            var cardCover: String? = null,
            var title: String? = null,
            var desc: String? = null,
    )
}