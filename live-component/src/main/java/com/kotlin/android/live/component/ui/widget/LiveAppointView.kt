package com.kotlin.android.live.component.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Range
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getDrawable
import com.kotlin.android.ktx.ext.core.getString
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.span.toColor
import com.kotlin.android.ktx.ext.span.toSpan
import com.kotlin.android.live.component.R
import com.kotlin.android.live.component.constant.LIVE_HAD_APPOINT
import com.kotlin.android.live.component.constant.LIVE_STATUS_APPOINT
import com.kotlin.android.live.component.constant.LIVE_STATUS_END
import com.kotlin.android.live.component.viewbean.AppointViewBean
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.formatCount
import kotlinx.android.synthetic.main.view_live_start_and_end.view.*


/**
 * create by lushan on 2021/3/3
 * description:直播前预约和直播结束
 */
class LiveAppointView @JvmOverloads constructor(var ctx: Context, var attrs: AttributeSet? = null, var defStyleAttr: Int = 0) : ConstraintLayout(ctx, attrs, defStyleAttr) {
    init {
        var view = LayoutInflater.from(context).inflate(R.layout.view_live_start_and_end, null)

        addView(view, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
    }

    private var liveStatus: Long = 0L//直播状态
    private var liveAppointViewBean: AppointViewBean? = null//预约信息
    private var appointAction: ((Long) -> Unit)? = null//预约按钮
    private var backAction: ((Long) -> Unit)? = null//返回按钮
    private var shareAction: (() -> Unit)? = null//分享按钮


    fun setAction(appointAction: ((Long) -> Unit)?, backAction: ((Long) -> Unit)?, shareAction: (() -> Unit)?) {
        this.appointAction = appointAction
        this.backAction = backAction
        this.shareAction = shareAction
    }

    fun setLiveStatus(status: Long) {
        this.liveStatus = status
        changeLiveLayout(liveStatus)
    }


    fun updateAppointView(bean: AppointViewBean?) {
        this.liveAppointViewBean = bean
        changeLiveLayout(liveStatus)
    }

    private fun setTopMargin(view:View?){
        (view?.layoutParams as? MarginLayoutParams)?.apply {
            this.topMargin = 10.dp
        }?.also {
            view?.layoutParams = it
        }
    }

    /**
     * 更新预约按钮文案
     */
    private fun updateAppointTv(appointState: Long) {
        liveAppointTv?.text = getString(if (appointState == LIVE_HAD_APPOINT) R.string.live_component_live_appoint_done else R.string.live_component_live_appoint_right_now)
        if (appointState == LIVE_HAD_APPOINT) {//已预约
            liveAppointTv?.run {
                text = getString(R.string.live_component_live_appoint_done)
                setTextColor(getColor(R.color.color_feb12a))
                getDrawable(R.drawable.ic_live_booked).apply {
                    this?.setBounds(0, 0, 20.dp, 18.dp)
                }?.also {
                    setCompoundDrawables(it, null, null, null)
                }

            }
//            按钮背景
            ShapeExt.setShapeCorner2Color2Stroke(liveAppointCL, R.color.transparent, 14, R.color.color_feb12a, 2)
        } else {
            liveAppointTv?.run {
                text = getString(R.string.live_component_live_appoint_right_now)
                setTextColor(getColor(R.color.color_ffffff))
                setCompoundDrawables(null, null, null, null)
            }
            //            按钮背景
            ShapeExt.setShapeCorner2Color2Stroke(liveAppointCL, R.color.color_f5a623, 14, R.color.color_feb12a, 2)
        }
        liveAppointCL?.isEnabled = appointState != LIVE_HAD_APPOINT
    }

    private fun setBackListener() {
        backBtn?.onClick {//返回到上一页
            backAction?.invoke(liveStatus)
        }
    }

    private fun changeLiveLayout(status: Long) {
        setTopMargin(backBtn)
        setTopMargin(shareIv)
        setBackListener()
        setShareListener()
        liveAppointCL?.visible()
        liveStartTimeTv?.visible()
        appointNumTv?.visible()
        when (status) {
            LIVE_STATUS_APPOINT -> {//直播预约状态
                liveStartTimeTv?.text = liveAppointViewBean?.liveDate.orEmpty()
                var appointState = liveAppointViewBean?.appointState ?: 0L
                liveAppointCL?.run {//预约按钮
                    updateAppointTv(appointState)
                    onClick {
                        if (appointState != LIVE_HAD_APPOINT) {//只有预约状态下可以点击
                            appointAction?.invoke(appointState)
                        }
                    }
                }

                updateAppointNumber()
            }

            LIVE_STATUS_END -> {//直播结束状态
                liveStartTimeTv?.text = getString(R.string.live_component_live_end)
                liveAppointCL?.gone()
                appointNumTv?.run {
                    text = getString(R.string.live_component_live_end_tips)
                    setTextColor(getColor(R.color.color_cbd0d7))
                }
            }
        }
    }

    private fun updateAppointNumber() {
        appointNumTv?.run {
            visible(liveAppointViewBean?.appointPersonNum ?: 0L > 0L)
            var formatCount = formatCount(liveAppointViewBean?.appointPersonNum ?: 0L)
            var liveAppointNumFormatString = getString(R.string.live_component_live_appoint_num_format, formatCount)
            var indexOf = liveAppointNumFormatString.indexOf(formatCount)
            setTextColor(getColor(R.color.color_ffffff))
            val endIndex = if (liveAppointViewBean?.appointPersonNum ?: 0L >= 10000L) indexOf + formatCount.length - 1 else indexOf + formatCount.length
            text = liveAppointNumFormatString.toSpan().toColor(Range(indexOf, endIndex), color = getColor(R.color.color_feb12a))
        }
    }

    private fun setShareListener() {
        shareIv?.onClick {
            shareAction?.invoke()
        }
    }

    /**
     * 预约成功
     */
    fun appointSuccess(appointNumber: Long) {
        this.liveAppointViewBean?.appointPersonNum = appointNumber
        this.liveAppointViewBean?.appointState = LIVE_HAD_APPOINT
        updateAppointTv(this.liveAppointViewBean?.appointState ?: 0L)
        updateAppointNumber()
    }


}