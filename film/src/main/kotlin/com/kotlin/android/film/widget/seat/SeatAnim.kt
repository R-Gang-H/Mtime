package com.kotlin.android.film.widget.seat

import android.animation.AnimatorSet
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.graphics.Point
import android.view.animation.DecelerateInterpolator
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.log.e

/**
 * 座位图动画
 *
 * Created on 2022/2/17.
 *
 * @author o.s
 */
internal class SeatAnim(
    private val seatChart: SeatChart,
    private var animDuration: Long = 400L,
    private var edge: Float = 5.dpF,
    val refresh: () -> Unit,
) {
    private val matrixValues = seatChart.matrixValues
    private val seatChartWidth: Float
        get() = seatChart.width
    private val seatChartHeight: Float
        get() = seatChart.height
    private val halfSeatChartWidth: Float
        get() = seatChart.centerX
    private val betterSeatChartWidth: Float
        get() = seatChart.betterSeatChartWidth

    private var seatViewWidth: Int = 0
    private var seatViewHeight: Int = 0 // view宽,高
    private var seatViewCenterX: Float = 0F
    private var seatViewY: Float = 0F
    private var indexesWidth: Float = 0F
    private val seatScale = 1F // 座位图的比例
    private val resetScale: Float // 重置座位图缩放比例
        get() {
            var scale = betterSeatChartWidth / seatChartWidth
            if (scale > 1.2f) {
                scale = 1.2f
            }
            return scale
        }
    private val seatScaleDouble = seatScale * 2 // 1.0F; 座位图放大两倍的比例

    private var centerPoint = Point()
    private var start = Point()
    private var end = Point()
    private var point = Point()

    fun init(
        seatViewWidth: Int,
        seatViewHeight: Int,
        seatViewCenterX: Float,
        seatViewY: Float,
        indexesWidth: Float,
    ) {
        this.seatViewWidth = seatViewWidth
        this.seatViewHeight = seatViewHeight
        this.seatViewCenterX = seatViewCenterX
        this.seatViewY = seatViewY
        this.indexesWidth = indexesWidth
    }

    /**
     * 座位图布局调整
     */
    fun layout(seatViewWidth: Int, seatViewHeight: Int) {
        this.seatViewWidth = seatViewWidth
        this.seatViewHeight = seatViewHeight
    }

    fun startFirstAnim() {
        firstAndRestAnim(0.1F, 1F)
    }

    fun startResetAnim() {
        "resetScale=$resetScale".e()
        firstAndRestAnim(0F, resetScale)
    }

    fun autoStartDoubleAnim(x: Float, y: Float) {
        seatChart.updateMatrixValues()

        var targetScale = matrixValues.sx
        if (matrixValues.sx > seatScaleDouble) {
            targetScale = seatScaleDouble
        } else if (matrixValues.sx < resetScale) {
            targetScale = resetScale
        }
        if (matrixValues.sx < seatScale * 1.5f) {
            targetScale = seatScale * 1.8f
        }

        centerPoint.x = x.toInt()
        centerPoint.y = y.toInt()
        start.x = 0
        start.y = 0
        end.x = 0
        end.y = 0

        "autoStartDoubleAnim sx=${matrixValues.sx} targetScale=$targetScale centerPoint=$centerPoint start=$start end=$end".e()
//        if (matrixValues.sx == targetScale) {
//            return
//        }
        anim(matrixValues.sx, targetScale, VertexType.VERTEX_TYPE_DEFAULT)
    }

    /**
     * 座位图自动修正动画
     */
    fun autoStartAnim() {
        seatChart.updateMatrixValues()

        var targetScale = matrixValues.sx
        if (matrixValues.sx > seatScaleDouble) {
            targetScale = seatScaleDouble
        } else if (matrixValues.sx < resetScale) {
            targetScale = resetScale
        }
        centerPoint.x = seatViewCenterX.toInt()
        centerPoint.y = seatViewY.toInt()
        start.x = (matrixValues.dx + halfSeatChartWidth * matrixValues.sx).toInt()
        start.y = matrixValues.dy.toInt()
        end.x = seatViewCenterX.toInt()
        end.y = seatViewY.toInt()

        "autoStartAnim sx=${matrixValues.sx} targetScale=$targetScale centerPoint=$centerPoint start=$start end=$end".e()
        anim(matrixValues.sx, targetScale, autoMoveAnim())
    }

    /**
     * 暂停动画
     */
    fun animPause() {
        if (anim.isRunning && !anim.isPaused) {
            anim.pause()
        }
    }

    /**
     * 取消动画
     */
    fun animCancel() {
        anim.cancel()
    }

    private val mInterpolator by lazy { DecelerateInterpolator() }

    private val evaluator by lazy {
        TypeEvaluator<Point> { fraction, startValue, endValue ->
            val x = (startValue.x + fraction * (endValue.x - startValue.x)).toInt()
            val y = (startValue.y + fraction * (endValue.y - startValue.y)).toInt()
            point.x = x
            point.y = y
            point
        }
    }

    private val scaleAnimUpdateListener by lazy {
        ValueAnimator.AnimatorUpdateListener { animation ->

            if (animation.animatedValue is Float) {
                val value = animation.animatedValue as Float
                val sx = value / seatChart.matrixScaleX // 重新获取, 防止矩阵抖动
                // LogManager.w(LogManager.NET_TAG, "@@@@@@ scale value = " + value + ", sx =" + sx);
                seatChart.postMatrixScale(sx, sx, centerPoint.x.toFloat(), centerPoint.y.toFloat())
                refresh()
            }
        }
    }

    /**
     * 设置顶点类型
     */
    private var type = VertexType.VERTEX_TYPE_CENTER_TOP
    
    private val moveAnimUpdateListener by lazy {
        ValueAnimator.AnimatorUpdateListener { animation ->

            if (animation.animatedValue is Point) {
                val p = animation.animatedValue as Point
                seatChart.updateMatrixValues() // 重新获取矩阵dx，dy 防止矩阵抖动
                val dx: Float // x中点
                val dy: Float // y起点

                when (type) {
                    VertexType.VERTEX_TYPE_LEFT_TOP -> {
                        dx = p.x - matrixValues.dx // x中点
                        dy = p.y - matrixValues.dy // y起点
                    }
                    VertexType.VERTEX_TYPE_RIGHT_TOP -> {
                        dx = p.x - (matrixValues.dx + seatChartWidth * matrixValues.sx) // x中点
                        dy = p.y - matrixValues.dy // y起点
                    }
                    VertexType.VERTEX_TYPE_LEFT_BOTTOM -> {
                        dx = p.x - matrixValues.dx // x中点
                        dy = p.y - (matrixValues.dy + seatChartHeight * matrixValues.sy) // y起点
                    }
                    VertexType.VERTEX_TYPE_RIGHT_BOTTOM -> {
                        dx = p.x - (matrixValues.dx + seatChartWidth * matrixValues.sx) // x中点
                        dy = p.y - (matrixValues.dy + seatChartHeight * matrixValues.sy) // y起点
                    }
                    VertexType.VERTEX_TYPE_LEFT -> {
                        dx = p.x - matrixValues.dx // x中点
                        dy = 0f // y起点
                    }
                    VertexType.VERTEX_TYPE_TOP -> {
                        dx = 0f // x中点
                        dy = p.y - matrixValues.dy // y起点
                    }
                    VertexType.VERTEX_TYPE_RIGHT -> {
                        dx = p.x - (matrixValues.dx + seatChartWidth * matrixValues.sx) // x中点
                        dy = 0f // y起点
                    }
                    VertexType.VERTEX_TYPE_BOTTOM -> {
                        dx = 0f // x中点
                        dy = p.y - (matrixValues.dy + seatChartHeight * matrixValues.sy) // y起点
                    }
                    VertexType.VERTEX_TYPE_CENTER_TOP -> {
                        dx =
                            p.x - (matrixValues.dx + halfSeatChartWidth * matrixValues.sx) // x中点
                        dy = p.y - matrixValues.dy // y起点
                    }
                    VertexType.VERTEX_TYPE_CENTER_BOTTOM -> {
                        dx =
                            p.x - (matrixValues.dx + halfSeatChartWidth * matrixValues.sx) // x中点
                        dy = p.y - (matrixValues.dy + seatChartHeight * matrixValues.sy) // y起点
                    }
                    VertexType.VERTEX_TYPE_CENTER_HORIZONTAL -> {
                        dx =
                            p.x - (matrixValues.dx + halfSeatChartWidth * matrixValues.sx) // x中点
                        dy = 0f // y起点
                    }
                    else -> {
                        dx = 0f // x中点
                        dy = 0f // y起点
                    }
                }
                // LogManager.e(LogManager.NET_TAG, "***** move :: p.x = %d, p.y = %d, dx = %f, dy = %f, " +
                //       "matrixValues.dx = %f, matrixValues.sx = %f, seatChartWidth = %f, seatChartHeight = %f",
                //       p.x, p.y, dx, dy, matrixValues.dx, matrixValues.sx, seatChartWidth, seatChartHeight);
                seatChart.postMatrixTranslate(dx, dy)
                refresh()
            }
        }
    }

    private val scaleAnim by lazy {
        ValueAnimator.ofFloat().apply {
            interpolator = mInterpolator
            addUpdateListener(scaleAnimUpdateListener)
            duration = animDuration
        }
    }

    private val moveAnim by lazy {
        "moveAnim init start=$start end=$end".e()
        ValueAnimator.ofObject(evaluator, start, end).apply {
            interpolator = mInterpolator
            addUpdateListener(moveAnimUpdateListener)
            duration = animDuration
        }
    }

    private val anim by lazy {
        AnimatorSet().apply {
            play(scaleAnim)?.with(moveAnim)
            duration = animDuration
        }
    }

    private fun firstAndRestAnim(fromScale: Float, toScale: Float) {
        seatChart.updateMatrixValues()
        // startScaleAnim
        centerPoint.x = seatViewCenterX.toInt()
        centerPoint.y = seatViewY.toInt()
        val from = if (fromScale == 0f) {
            matrixValues.sx
        } else {
            fromScale
        }
        "firstAndRestAnim from=$from toScale=$toScale".e()
        // startMoveAnim
        start.x = (matrixValues.dx + halfSeatChartWidth * matrixValues.sx).toInt()
        start.y = matrixValues.dy.toInt()
        end.x = seatViewCenterX.toInt()
        end.y = seatViewY.toInt()
        type = VertexType.VERTEX_TYPE_CENTER_TOP
        "firstAndRestAnim (dx, dy, sx, sy):(${matrixValues.dx}, ${matrixValues.dy}, ${matrixValues.sx}, ${matrixValues.sy}) halfW=$halfSeatChartWidth halfWS=${halfSeatChartWidth * matrixValues.sx} seatViewCenterX=$seatViewCenterX seatViewY=$seatViewY".e()
        "moveAnim set start=$start end=$end".e()
//        if (from == toScale && start == end) {
//            return
//        }
        scaleAnim?.setFloatValues(from, toScale)
        moveAnim?.setObjectValues(start, end)

        anim.start()
    }

    private fun anim(fromScale: Float, toScale: Float, type: VertexType) {
        val from = if (fromScale == 0f) {
            matrixValues.sx
        } else {
            fromScale
        }
        "anim from=$from toScale=$toScale".e()
//        if (from == toScale && start == end) {
//            return
//        }
        scaleAnim?.setFloatValues(from, toScale)
        this.type = type

        anim.start()
    }

    private fun autoMoveAnim(): VertexType {
        val currentSeatChartWidth = seatChartWidth * matrixValues.sx
        val currentSeatChartHeight = seatChartHeight * matrixValues.sy
        val width = seatViewWidth - 2 * (edge + indexesWidth + edge)
        val height = seatViewHeight - seatViewY - edge

        val vl = (edge + indexesWidth + edge).toInt()
        val vr = seatViewWidth - vl
        val vt = seatViewY.toInt()
        val vb = (seatViewHeight - edge).toInt()
        val vp1 = Point(vl, vt)
        val vp2 = Point(vr, vt)
        val vp3 = Point(vl, vb)
        val vp4 = Point(vr, vb)


        val l = matrixValues.dx.toInt()
        val t = matrixValues.dy.toInt()
        val r = (matrixValues.dx + seatChartWidth * matrixValues.sx).toInt()
        val b = (matrixValues.dy + seatChartHeight * matrixValues.sy).toInt()
        val p1 = Point(l, t)
        val p2 = Point(r, t)
        val p3 = Point(l, b)
        val p4 = Point(r, b)


        return if (currentSeatChartWidth <= width && currentSeatChartHeight <= height) {
            // 9种矩阵较小的情况下：将矩阵的上中点移动到座位图的上中点。（这是一个不错的主意）
            smallWidthAndHeight(p1, p2, vp1, vp2)
        } else if (currentSeatChartWidth > width && currentSeatChartHeight <= height) {
            // 6种长条矩阵（宽较大高较小）情况：
            bigWidthAndSmallHeight(p1, p2, vp1, vp2)
        } else if (currentSeatChartWidth <= width && currentSeatChartHeight > height) {
            // 6种竖条矩阵（宽较小高较大）情况：
            smallWidthAndBigHeight(p1, p2, p3, p4, vp1, vp2, vp3)
        } else {
            // 9种矩阵较大的情况：
            bigWidthAndHeight(p1, p2, p3, p4, vp1, vp2, vp3, vp4)
        }
    }

    // 9种矩阵较小的情况：将矩阵的上中点移动到座位图的上中点。（这是一个不错的主意）
    private fun smallWidthAndHeight(p1: Point, p2: Point, vp1: Point, vp2: Point): VertexType {
        // 9种，将矩阵的上中点移动到座位图的上中点。（这是一个不错的主意）
        start.x = (p1.x + p2.x) / 2
        start.y = p1.y
        end.x = (vp1.x + vp2.x) / 2
        end.y = vp1.y
        return VertexType.VERTEX_TYPE_CENTER_TOP
    }

    // 6种长条矩阵（宽较大高较小）情况：
    private fun bigWidthAndSmallHeight(p1: Point, p2: Point, vp1: Point, vp2: Point): VertexType {
        when {
            p2.x <= vp2.x -> {
                // 矩阵右点x <= 座位图右点x（往右上点移动，因为高比较小）
                start.x = p2.x
                start.y = p2.y
                end.x = vp2.x
                end.y = vp2.y
                return VertexType.VERTEX_TYPE_RIGHT_TOP
            }
            p1.x >= vp1.x -> {
                // 矩阵左点x >= 座位图左点x（往左上点移动，因为高比较小）
                start.x = p1.x
                start.y = p1.y
                end.x = vp1.x
                end.y = vp1.y
                return VertexType.VERTEX_TYPE_LEFT_TOP
            }
            else -> {
                // 矩阵较长在座位图两边都有不可见部分（矩阵可视部分中点移动到座位图中上点，因为高比较小）
                // TODO 中点问题 start.x
                start.x = (vp1.x + vp2.x) / 2
                start.y = p1.y
                end.x = (vp1.x + vp2.x) / 2
                end.y = vp1.y
                return VertexType.VERTEX_TYPE_TOP
            }
        }
    }

    // 6种竖条矩阵（宽较小高较大）情况：
    private fun smallWidthAndBigHeight(p1: Point, p2: Point, p3: Point, p4: Point,
                                       vp1: Point, vp2: Point, vp3: Point): VertexType {
        when {
            p1.y >= vp1.y -> {
                // 矩阵上点y >= 座位图上点y（往中上点移动，因为宽较小）
                start.x = (p1.x + p2.x) / 2
                start.y = p1.y
                end.x = (vp1.x + vp2.x) / 2
                end.y = vp1.y
                return VertexType.VERTEX_TYPE_CENTER_TOP
            }
            p3.y <= vp3.y -> {
                // 矩阵下点y <= 座位图下点y（往中下点移动，因为宽较小）
                start.x = (p3.x + p4.x) / 2
                start.y = p3.y
                end.x = (vp1.x + vp2.x) / 2
                end.y = vp3.y
                return VertexType.VERTEX_TYPE_CENTER_BOTTOM
            }
            else -> {
                // 矩阵较高在座位图上下部分都有不可见部分（矩阵平移到座位图x中部，因为宽较小）
                // TODO 中点问题 start.y
                start.x = (p1.x + p2.x) / 2
                start.y = (vp1.y + vp3.y) / 2
                end.x = (vp1.x + vp2.x) / 2
                end.y = (vp1.y + vp3.y) / 2
                return VertexType.VERTEX_TYPE_CENTER_HORIZONTAL
            }
        }
    }

    // 9种矩阵较大的情况：
    private fun bigWidthAndHeight(p1: Point, p2: Point, p3: Point, p4: Point,
                                  vp1: Point, vp2: Point, vp3: Point, vp4: Point): VertexType {
        if (p1.x >= vp1.x && p1.y >= vp1.y) {
            // ↖️ 矩阵的左上顶点在座位图左上顶点的右下方（矩阵往座位图左上顶点移动）
            start.x = p1.x
            start.y = p1.y
            end.x = vp1.x
            end.y = vp1.y
            return VertexType.VERTEX_TYPE_LEFT_TOP
        } else if (p2.x <= vp2.x && p2.y >= vp2.y) {
            // ↗️ 矩阵的右上顶点在座位图右上顶点的左下方（矩阵往座位图右上顶点移动）
            start.x = p2.x
            start.y = p2.y
            end.x = vp2.x
            end.y = vp2.y
            return VertexType.VERTEX_TYPE_RIGHT_TOP
        } else if (p3.x >= vp3.x && p3.y <= vp3.y) {
            // ↙️ 矩阵的左下顶点在座位图左下顶点的右上方（矩阵往座位图左下顶点移动）
            start.x = p3.x
            start.y = p3.y
            end.x = vp3.x
            end.y = vp3.y
            return VertexType.VERTEX_TYPE_LEFT_BOTTOM
        } else if (p4.x <= vp4.x && p4.y <= vp4.y) {
            // ↘️ 矩阵的右下顶点在座位图右下顶点的左上方（矩阵往座位图右下顶点移动）
            start.x = p4.x
            start.y = p4.y
            end.x = vp4.x
            end.y = vp4.y
            return VertexType.VERTEX_TYPE_RIGHT_BOTTOM
        } else if (p1.y >= vp1.y) {
            // ⬆️ 矩阵上点在座位图上点下方（矩阵往座位图上中点垂直移动）
            // TODO 中点问题 start.x
            start.x = (vp1.x + vp2.x) / 2
            start.y = p1.y
            end.x = (vp1.x + vp2.x) / 2
            end.y = vp1.y
            return VertexType.VERTEX_TYPE_TOP
        } else if (p2.x <= vp2.x) {
            // ➡️ 矩阵右点在座位图右点左方（矩阵往座位图右点水平移动）
            // TODO 中点问题 start.y
            start.x = p2.x
            start.y = (vp2.y + vp4.y) / 2
            end.x = vp2.x
            end.y = (vp2.y + vp4.y) / 2
            return VertexType.VERTEX_TYPE_RIGHT
        } else if (p3.y <= vp3.y) {
            // ⬇️ 矩阵下点在座位图下点上方（矩阵往座位图下中点垂直移动）
            // TODO 中点问题 start.x
            start.x = (vp3.x + vp4.x) / 2
            start.y = p3.y
            end.x = (vp3.x + vp4.x) / 2
            end.y = vp3.y
            return VertexType.VERTEX_TYPE_BOTTOM
        } else if (p1.x >= vp1.x) {
            // TODO 中点问题 start.y
            // ⬅️️ 矩阵左点在座位图左点右方（矩阵往座位图左点水平移动）
            start.x = p1.x
            start.y = p1.y
            end.x = vp1.x
            end.y = vp1.y
            //            LogManager.e(LogManager.NET_TAG, "start == end :: x = %d, y = %d", p1.x, p1.y);
            return VertexType.VERTEX_TYPE_LEFT
        } else {
            return VertexType.VERTEX_TYPE_DEFAULT
        }
    }
}