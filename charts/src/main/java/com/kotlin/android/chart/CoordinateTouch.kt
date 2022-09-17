package com.kotlin.android.chart

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent

/**
 * @Package:        com.kotlin.android.chart
 * @ClassName:      CoordinateTouch
 * @Description:    触摸显示坐标系
 * @Author:         haoruigang
 * @CreateDate:     2022/3/23 13:5
 */
open class CoordinateTouch(context: Context, attrs: AttributeSet) :
    CoordinateSystem(context, attrs) {

    var origin: PointF = PointF(0f, 0f) // 坐标系原点坐标
    var xEndPoint: PointF = PointF(0f, 0f) // x轴终点坐标
    var yEndPoint: PointF = PointF(0f, 0f) // y轴终点坐标
    var xWidthUnit = 0f //x轴单位刻度的宽度，指一个bar在x轴上占用的总宽度（包含了bar两边的空白区域）

    var listener: ((Long?, String?, PointF?) -> Unit)? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (YValueList == null || XValueList == null) return

        origin.x = dividerSpaceYL - paddingStart.toFloat()
        origin.y = sumHeight - buttomHeiht
        xEndPoint.x = sumWidth - dividerSpaceYR
        xEndPoint.y = sumHeight - buttomHeiht
        yEndPoint.x = dividerSpaceYL - paddingStart.toFloat()
        yEndPoint.y = paddingTop.toFloat()

        XValueList?.apply {
            xWidthUnit = if (isEmpty()) {
                (xEndPoint.x - origin.x)
            } else {
                ((xEndPoint.x - origin.x) / size)
            }
        }

        if (focusedDataIndex != -1) {
            drawFocusedInfo(canvas)
        }

    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (XValueList?.isEmpty() == true || YValueList?.isEmpty() == true) {
            return false
        }
        if (event != null) {
            onTouchEvent(event)
        }
        return true
    }

    /**
     * 触摸事件反馈
     */
    fun onFocused(yData: Long?, xData: String?, point: PointF?) {
        listener?.invoke(yData, xData, point)
    }

    // 处理触控事件冲突 https://blog.csdn.net/qq_38547512/article/details/90479862
    fun attemptClaimDrag() {
        this.parent?.requestDisallowInterceptTouchEvent(true)
    }

    /**
     * 绘制描述触摸区域的描述信息
     */
    private fun drawFocusedInfo(canvas: Canvas) {
        // canvas.saveLayerAlpha(0f, 0f, width.toFloat(), height.toFloat(), 0x88) // 增加透明度
        if (isDash) { // 是否虚线
            //绘制长度为4的实线后再绘制长度为20的空白区域，0位间隔
            datePaint?.pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
        }
        // 绘制y轴触摸点的标尺线
        xyList?.apply {
            datePaint?.let {
                canvas.drawLine(
                    this[focusedDataIndex].x,
                    origin.y,
                    this[focusedDataIndex].x,
                    yEndPoint.y, it)
            }
        }

        //绘制说明区域
        drawFocusedInfoText(canvas, focusedDataIndex)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d(LOG_TAG, event?.action.toString())
        Log.d(LOG_TAG, event?.x.toString())
        Log.d(LOG_TAG, event?.y.toString())
        if (event != null) {
            if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                attemptClaimDrag()
                // 点击点在坐标系内部，确定点击的是哪一个柱体
                if (event.x > origin.x && event.x < xEndPoint.x && event.y < origin.y && event.y > yEndPoint.y) {
                    val dataIndex = ((event.x - origin.x) / xWidthUnit).toInt()
                    val yData = YValueList?.get(dataIndex)
                    val xData = XValueList?.get(dataIndex)
                    val point = xyList?.get(dataIndex)
                    Log.d(LOG_TAG, yData.toString() + "==" + xData.toString() +
                            ":" + point?.y.toString() + "==" + point?.x.toString())
                    if (focusedDataIndex == -1) {
                        focusedDataIndex = dataIndex
                        onFocused(yData, xData, point)
                        invalidate()
                        return true
                    } else if (dataIndex != focusedDataIndex) {
                        focusedDataIndex = dataIndex
                        onFocused(yData, xData, point)
                        invalidate()
                        return true
                    }
                } else {
                    Log.d(LOG_TAG, "触点不在坐标系内！")
                }
            } else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_OUTSIDE) {
                focusedDataIndex = -1
                //onFocused(null, null, null)
                //invalidate()
                return true
            }
        }
        performClick()
        return super.onTouchEvent(event)
    }

    /**
     * 绘制说明区域
     */
    open fun drawFocusedInfoText(canvas: Canvas?, focusedDataIndex: Int) {
        return
    }


}