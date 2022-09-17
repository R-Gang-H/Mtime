package com.kotlin.android.chart

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PointF
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.kotlin.android.R
import com.kotlin.android.databinding.PopupEverywhereBinding
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.mtime.ktx.formatCount
import com.kotlin.android.popup.ARROWDIRECTION
import com.kotlin.android.popup.EverywherePopup
import com.kotlin.android.popup.TriangleDrawable
import java.text.DecimalFormat

class LineChart(context: Context, attrs: AttributeSet) : CoordinateTouch(context, attrs) {

    var inited = true // 数据变化后是否已绘制动画
    var progress = 0f // 当前动画绘制进度，取值 (0 - data.size)

    // 图表 x/y 轴选中的值
    var xData: String = ""
    var yData: String = ""
    var descName: String? = null // 弹框说明

    var mEverywherePopup: EverywherePopup? = null
    var everywhereBinding: PopupEverywhereBinding? = null

    init {
        val obtainStyledAttributes = context.obtainStyledAttributes(attrs, R.styleable.Chart)
        animation = obtainStyledAttributes.getBoolean(R.styleable.Chart_animation, false)
        animationDuration = obtainStyledAttributes.getInteger(R.styleable.Chart_animationDuration,
            500).toLong()
        obtainStyledAttributes.recycle()

        everywhereBinding = PopupEverywhereBinding.inflate(LayoutInflater.from(mContext))
        everywhereBinding?.lineData = this
        everywherePopup()
        drawFocusedInfoText(null, -1)
    }

    private fun everywherePopup() {
        if (mEverywherePopup == null) {
            mEverywherePopup = EverywherePopup.create(context)
                .setContentView(everywhereBinding?.root)
                ?.setOnViewListener(object : EverywherePopup.OnViewListener {
                    override fun initViews(view: View?, popup: EverywherePopup?) {
                        everywhereBinding?.apply {
                            vLArrow.background =
                                TriangleDrawable(ARROWDIRECTION.LEFT,
                                    Color.parseColor("#20A0DA"))
                            vRArrow.background =
                                TriangleDrawable(ARROWDIRECTION.RIGHT,
                                    Color.parseColor("#20A0DA"))
                            rlBox.setBackground(colorRes = R.color.color_20a0da, cornerRadius = 8f)
                        }
                    }
                })
                ?.setOnDismissListener {
                    invalidate()
                }
                ?.setNeedReMeasureWH(true)
                ?.setFocusAndOutsideEnable(true)
                ?.apply()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawSeries(canvas)
    }

    /**
     * 绘制坐标系内的bar或line
     */
    fun drawSeries(canvas: Canvas) {
        if (animation) {
            if (!inited) {
                inited = true
                startAnimation()
            }
            drawAnimateLines(canvas, progress)
        }
    }

    private fun drawAnimateLines(canvas: Canvas, progress: Float) {

    }

    private fun startAnimation() {
        ValueAnimator.ofFloat(0f, (XValueList?.size as Int - 1).toFloat()).apply {
            addUpdateListener {
                progress = animatedValue as Float
                invalidate()
            }
            interpolator = AccelerateDecelerateInterpolator()
            duration = animationDuration
        }.start()
    }

    fun setDescName(posDesc: String?): LineChart {
        descName = posDesc
        return this
    }

    private val sCountFormat: DecimalFormat = DecimalFormat(",###.#")

    fun formatNumber(count: Long): String {
        // todo 千分、万、亿 通用规则显示
        return if (count <= 9999) {
            sCountFormat.format(count.toDouble())
        } else {
            formatCount(count)
        }
    }

    /**
     * 绘制说明区域
     */
    override fun drawFocusedInfoText(canvas: Canvas?, focusedDataIndex: Int) {
        everywhereBinding?.let {
            // 说明弹框
            mEverywherePopup?.apply {
                listener = fun(yData: Long?, xData: String?, point: PointF?) {
                    dismiss()
                    if (yData != null) {
                        this@LineChart.yData = formatNumber(yData)
                        it.tvYData.text = String.format("%s：%s", descName, formatNumber(yData))
                    }
                    if (xData != null) {
                        this@LineChart.xData = xData
                        it.tvXData.text = xData
                    }

                    // 判断弹框出现的位置方向，计算偏移量 start  ========
                    val screenWidth: Int = getScreenWidthHeight()[0]
                    val offsetX: Int
                    if (point != null) {
                        // , sumWidth.toInt(), sumHeight.toInt()
                        // 获得当前view在屏幕中的坐标
                        val location = IntArray(2)
                        this@LineChart.getLocationOnScreen(location)
                        // x坐标距离屏幕左边的宽度 > 总控件右边距离
                        if (point.x + location[0] + width > screenWidth - location[0]) {
                            // 图表控件左边距 + 控件宽度 = 控件最右边x坐标
                            val lMarginSumWidth = +sumWidth
                            // 出现在右边👉🏻  (控件最右x坐标 - (圆点到控件右边的距离) -  圆点宽度 - 边距
                            offsetX =
                                (lMarginSumWidth - (lMarginSumWidth - point.x.toInt() - location[0]) -
                                        dotCircleSize.toInt() - dp2px(5f)).toInt()
                            /*// 出现在右边👈🏻  (圆点在控件中的x + y轴值左边距 - 圆点宽度 - 边距)
                            offsetX = point.x.toInt() + valueSpaceYL.toInt() -
                                    dotCircleSize.toInt() - dp2px(5f).toInt()*/
                            it.vRArrow.visible()
                            it.vLArrow.gone()
                        } else {
                            // 出现在左边👈🏻  (圆点在控件中的x + y轴值左边距 + 圆点宽度 + 边距)
                            offsetX = point.x.toInt() + valueSpaceYL.toInt() +
                                    dotCircleSize.toInt() + dp2px(5f).toInt()
                            it.vRArrow.gone()
                            it.vLArrow.visible()
                        }
                        // 判断弹框出现的位置方向，计算偏移量 end  ========

                        showEverywhere(this@LineChart,
                            offsetX,
                            // 圆点在控件中的y + 控件距离屏幕顶部的高度 - 弹框高度/2
                            point.y.toInt() + location[1] - height / 2,
                            point, location)
                    }
                }

            }
        }

    }

    override fun onDataUpdate(yData: ArrayList<Long>?, xData: ArrayList<String>?) {
        super.onDataUpdate(yData, xData)
        if (animation) {
            inited = false
            progress = 0f
        }
    }

}