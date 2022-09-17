package com.kotlin.android.ticket.order.component.binder

import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import androidx.core.text.HtmlCompat
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_FILM_COMMENT
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.mtime.ktx.getServerTime

import com.kotlin.android.app.router.provider.main.IMainProvider
import com.kotlin.android.app.router.provider.publish.IPublishProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.ticket.order.component.R
import com.kotlin.android.ticket.order.component.TicketCountDownTimer
import com.kotlin.android.ticket.order.component.bean.TicketOrderItemViewBean
import com.kotlin.android.ticket.order.component.databinding.ItemTicketOrderBinding
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.dialog.BaseDialog

/**
 * create by lushan on 2020/9/16
 * description: 电影票订单列表 新电影票订单列表
 */
class TicketOrderBinder(var bean: TicketOrderItemViewBean) : MultiTypeBinder<ItemTicketOrderBinding>() {
    private val instance: IMainProvider? = getProvider(IMainProvider::class.java)

    private var mTimer: CountDownTimer? = null
    override fun layoutId(): Int = R.layout.item_ticket_order

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean = other is TicketOrderBinder && other.bean != bean

    override fun onBindViewHolder(binding: ItemTicketOrderBinding, position: Int) {
        super.onBindViewHolder(binding, position)
//        评分按钮
        ShapeExt.setGradientColor(binding.gotoRatingTv, GradientDrawable.Orientation.BL_TR, R.color.color_20a0da, R.color.color_1bafe0, 25)
//        付款按钮
        ShapeExt.setShapeCorner2Color(binding.payBtn, R.color.color_feb12a, 25)
//        我要评分 处理样式
        handleRatingTvStyle()
//        处理总价样式
        handleTotalPriceTvStyle()
//        处理倒计时待支付订单倒计时
        handleRemainTime()
//        binding.orderCL.setOnLongClickListener {
//            handleDeleteOrder()
//            true
//        }

    }

    /**
     * 处理长按事件
     */
    private fun handleDeleteOrder(){
        if (bean.isNoPay()){//未支付订单不可删除
            binding?.orderCL?.context?.apply {
                BaseDialog.Builder(this).setTitle(R.string.ticket_order_tips).setContent(R.string.ticket_order_cant_cancel_no_pay)
                        .setPositiveButton(R.string.ticket_order_do_sure) { dialog, which -> dialog?.dismiss() }.create().show()
            }
        }else{
            binding?.orderCL?.context?.apply {
                BaseDialog.Builder(this).setTitle(R.string.ticket_order_tips)
                        .setContent(R.string.ticket_order_delete_order_not_recovery)
                        .setPositiveButton(R.string.ticket_order_do_sure_delete){dialog,which->
                            dialog?.dismiss()
                            binding?.orderCL?.run {
                                super.onLongClick(this)
                            }
                        }.setNegativeButton(R.string.ticket_order_do_cancel){
                            dialog,which->
                            dialog?.dismiss()
                        }.create().show()
            }
        }
    }

    /**
     * 处理待支付订单倒计时样式
     */
    private fun handleRemainTime() {
        if (bean.isNoPay()) {//待支付订单
            val remainTime = bean.orderEndTime - getServerTime()
            if (remainTime <= 0) {
                return
            }

            mTimer?.cancel()
            mTimer = TicketCountDownTimer(remainTime) { min: String, sec: String, isFinish: Boolean ->
                if (isFinish) {//结束需要显示弹框
                    binding?.payBtn?.gone()
                    binding?.remainTimeTv?.gone()
//                    binding?.orderStatusTv?.gone()
                } else {
                    setRemainTimeContent(min, sec)
                }

            }
            mTimer?.start()
        }
    }

    private fun setRemainTimeContent(min: String, sec: String) {
        val str = ("<font  color=\"#8798AF\">剩余时间: </font> " + "<font color=\"#FEB12A\">"
                + min + "</font> " + "<font color=\"#8798AF\"> 分</font> "
                + "<font color=\"#FEB12A\">" + sec + "</font> "
                + "<font color=\"#8798AF\"> 秒</font>")
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
            binding?.priceTv?.text = it
        }
    }

    /**
     * 处理评分样式
     */
    private fun handleRatingTvStyle() {
        if (TextUtils.isEmpty(bean.rating).not()) {
            SpannableString(bean.getRatingFormat()).apply {
                val startIndex = 3
                setSpan(StyleSpan(Typeface.BOLD_ITALIC), startIndex, startIndex + bean.rating.length, SpannableString.SPAN_EXCLUSIVE_INCLUSIVE)
            }.also {
                binding?.ratingTv?.text = it
            }
        }
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.orderCL -> {//订单详情
                instance?.startOrderDetailActivity(bean.orderId, false, bean.isNoPay())
            }
            R.id.cinemaTv -> {//影院详情
                instance?.startCinemaViewActivity(bean.cinemaId)
            }
            R.id.gotoRatingTv -> {//评分页面
                getProvider(IPublishProvider::class.java)?.startEditorActivity(
                    type = CONTENT_TYPE_FILM_COMMENT,
                    movieId = bean.movieId,
                    movieName = bean.movieName
                )
            }

            R.id.payBtn -> {//付款按钮
                instance?.startOrderDetailActivity(bean.orderId, false, bean.isNoPay(), true)
            }
            else -> {
                super.onClick(view)
            }
        }
    }

    override fun onUnBindViewHolder(binding: ItemTicketOrderBinding?) {
        super.onUnBindViewHolder(binding)
        mTimer?.cancel()
        mTimer = null
    }
}