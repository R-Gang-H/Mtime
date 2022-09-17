package com.kotlin.android.ticket.order.component.binder

import android.app.Activity
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.text.HtmlCompat
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.showSafeDialog
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.mtime.ktx.getServerTime

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.main.IMainProvider
import com.kotlin.android.app.router.provider.ticket.ITicketProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.ticket.order.component.R
import com.kotlin.android.ticket.order.component.TicketCountDownTimer
import com.kotlin.android.ticket.order.component.bean.TicketOrderOldItemViewBean
import com.kotlin.android.ticket.order.component.databinding.ItemTicketOldOrderBinding
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.dialog.BaseDialog

/**
 * create by lushan on 2020/10/16
 * description: 老需求订单类表
 */
class TicketOrderOldBinder(var bean: TicketOrderOldItemViewBean) : MultiTypeBinder<ItemTicketOldOrderBinding>() {
    private val instance: IMainProvider? = getProvider(IMainProvider::class.java)
    private val ticketProvider:ITicketProvider? = getProvider(ITicketProvider::class.java)
    private var mTimer: CountDownTimer? = null
    private var finishDialog:BaseDialog? = null//倒计时结束弹框
    override fun layoutId(): Int = R.layout.item_ticket_old_order

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is TicketOrderOldBinder && other.bean != bean
    }

    override fun onBindViewHolder(binding: ItemTicketOldOrderBinding, position: Int) {
        super.onBindViewHolder(binding, position)
//        是否包含卖品图标背景
        ShapeExt.setShapeCorner2Color2Stroke(binding.containsGoodsTv, corner = 2.dp, strokeColor = R.color.color_f97d3f, strokeWidth = 1.dp)
//        付款按钮背景
        ShapeExt.setShapeColorAndCorner(binding.payBtn, R.color.color_f97d3f, 14.5f.dp)

        //        处理总价样式
        handleTotalPriceTvStyle()

        //        处理倒计时待支付订单倒计时
        handleRemainTime()

//        binding.orderRootView.setOnLongClickListener {
//            handleDeleteOrder()
//            true
//        }


    }


    /**
     * 处理长按事件
     */
    private fun handleDeleteOrder() {
        if (bean.isNotPay) {//未支付订单不可删除
            binding?.orderRootView?.context?.apply {
                BaseDialog.Builder(this).setTitle(R.string.ticket_order_tips).setContent(R.string.ticket_order_cant_cancel_no_pay)
                        .setPositiveButton(R.string.ticket_order_do_sure) { dialog, which -> dialog?.dismiss() }.create().show()
            }
        } else {
            binding?.orderRootView?.context?.apply {
                BaseDialog.Builder(this).setTitle(R.string.ticket_order_tips)
                        .setContent(R.string.ticket_order_delete_order_not_recovery)
                        .setPositiveButton(R.string.ticket_order_do_sure_delete) { dialog, which ->
                            dialog?.dismiss()
                            binding?.orderRootView?.run {
                                super.onLongClick(this)
                            }
                        }.setNegativeButton(R.string.ticket_order_do_cancel) { dialog, which ->
                            dialog?.dismiss()
                        }.create().show()
            }
        }
    }

    /**
     * 处理待支付订单倒计时样式
     */
    private fun handleRemainTime() {
        if (bean.notPay()) {//待支付订单
            val remainTime = if (bean.reSelectSeat) {
                bean.reSelectSeatEndTime  - getServerTime()
            } else {
                bean.payEndTime  - getServerTime()
            }

            if (remainTime <= 0) {
                return
            }

            mTimer?.cancel()
            mTimer = TicketCountDownTimer(remainTime) { min: String, sec: String, isFinish: Boolean ->
                if (isFinish) {//结束需要显示弹框
                    binding?.payBtn?.gone()
                    binding?.remainTimeTv?.gone()
//                    binding?.orderStatusTv?.gone()
                    showOrderFinishDialog()
                } else {
                    setRemainTimeContent(min, sec)
                }

            }
            mTimer?.start()
        }
    }

    private fun showOrderFinishDialog() {
        (binding?.root?.context as? Activity)?.showSafeDialog {
            binding?.root?.context?.apply {
                finishDialog?.dismiss()
                finishDialog = BaseDialog.Builder(this).setContent(com.kotlin.android.mtime.ktx.getString(R.string.ticket_order_count_down_finished)).setPositiveButton(com.kotlin.android.mtime.ktx.getString(R.string.ticket_iknow)) { p0, p1 ->
                    notifyAdapterSelfRemoved()
                    p0?.dismiss()
                }.create()
                finishDialog?.show()
            }
        }
    }

    private fun setRemainTimeContent(min: String, sec: String) {
        val str = ("<font  color=\"#989898\">剩余时间: </font> " + "<font color=\"#F1883D\">"
                + min + "</font> " + "<font color=\"#989898\"> 分</font> "
                + "<font color=\"#F1883D\">" + sec + "</font> "
                + "<font color=\"#989898\"> 秒</font>")
        binding?.remainTimeTv?.text = HtmlCompat.fromHtml(str, HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH)
    }

    /**
     * 处理总价样式
     */
    private fun handleTotalPriceTvStyle() {
        SpannableString(bean.getTotalPriceFormat()).apply {
            val startIndex = 4
            setSpan(ForegroundColorSpan(getColor(R.color.color_4e5e73)), startIndex, this.length, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE)
        }.also {
            binding?.totalPriceTv?.text = it
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.payBtn -> {//未支付
                if (bean.reSelectSeat) {
                    ticketProvider?.startSeatSelectFromOrderListActivity(bean.orderId,true)
                }else{
                    instance?.startOrderDetailActivity(bean.orderId, false, bean.isNotPay)
                }
            }
            R.id.orderRootView -> {//整个订单item
                instance?.startOrderDetailActivity(bean.orderId, false, bean.isNotPay, true)
            }
            else -> {
                super.onClick(view)
            }
        }
    }
}