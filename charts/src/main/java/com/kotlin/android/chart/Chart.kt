package com.kotlin.android.chart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View

/**
 * @ProjectName: CoordinateTouch
 * @ClassName: Chart
 * @Description: 图表
 * @Author: haoruigang
 * @CreateDate: 2022/3/21 16:31
 */
abstract class Chart(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    open val LOG_TAG = "Chart"
    val mContext = context

    var sumHeight //总控件的高度
            = 0f
    var sumWidth //总控件的宽度
            = 0f
    var maxTime //最大的时间 用来划y轴单位的 最小就是20 X1.2是为了给上方和下方预留空间
            = 0.0
    var linePaint //线上值的画笔
            : Paint? = null
    var mPaint //曲线画笔
            : Paint? = null
    var circlePaint //圆点圈画笔
            : Paint? = null
    var circlePaint2 //圆点画笔
            : Paint? = null
    var scorePaint //图表中x轴横线
            : Paint? = null
    var datePaint //图表中y轴竖线
            : Paint? = null
    var textPaint //文字的画笔
            : Paint? = null
    var YValueList //Y轴值集合
            : ArrayList<Long>? = null
    var XValueList //X轴值集合
            : ArrayList<String>? = null
    var oneHeight //每一个小段所要分成的高
            = 0f
    var oneWidth //每一个小段所要分成的宽
            = 0f
    var buttomHeiht //给底部一排日期预留出的空间
            = 0f
    var baseLinePath //折线路径
            : Path? = null
    val smoothness = 0.36f //折线的弯曲率
    var baseShadow //折线下的阴影的画笔
            : Paint? = null
    var xyList //储存定好的坐标点的集合
            : ArrayList<PointF>? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (YValueList == null || XValueList == null) return
        maxTime = getMaxTime(YValueList)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        sumHeight = measuredHeight.toFloat()
        sumWidth = measuredWidth.toFloat()
    }

    /**
     * 更新阅读时间
     */
    open fun updateTime(
        yValueList: ArrayList<Long>?,
        xValueList: ArrayList<String>?,
    ):Chart {
        YValueList = yValueList
        XValueList = xValueList
        if (YValueList != null && YValueList?.size as Int > 0 && XValueList != null
            && XValueList?.size as Int > 0
        ) {
            onDataUpdate(yValueList, xValueList)
            invalidate()
        }
        return this
    }

    /**
     * 数据更新后的回调，主要用于重置动画参数
     */
    open fun onDataUpdate(yData: ArrayList<Long>?, xData: ArrayList<String>?) {

    }

    /**
     * 取出时间里面的最大的一个用来计算总长度
     *
     * @param YValueList
     * @return
     */
    fun getMaxTime(YValueList: ArrayList<Long>?): Double {
        maxTime = 0.0
        for (i in YValueList?.indices as IntRange) {
            if (maxTime < YValueList[i]) {
                maxTime = YValueList[i].toDouble()
            }
        }
        if (maxTime <= 10) {
            maxTime = 10.0
        }
        maxTime *= 1.2
        return maxTime
    }

    fun dp2px(dp: Float): Float {
        val scale = this.resources.displayMetrics.density
        return dp * scale + 0.5f
    }

    fun sp2px(sp: Float): Float {
        val scale = this.resources.displayMetrics.scaledDensity
        return sp * scale
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    fun px2dp(pxValue: Float): Float {
        val scale = this.resources.displayMetrics.density
        return pxValue / scale + 0.5f
    }

}