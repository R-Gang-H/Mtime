package com.kotlin.android.film.widget.seat

import android.graphics.*
import android.os.Handler
import android.os.Looper
import com.kotlin.android.film.R
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.mtime.ktx.getColor

/**
 * 绘制座位图概览
 *
 * Created on 2022/2/10.
 *
 * @author o.s
 */
internal class SeatOverview(
    private val seatIcon: SeatIcon,
    private val seatChart: SeatChart,
    private val seatIndexes: SeatIndexes,
    private val seatHeader: SeatHeader,
    private var overviewScale: Float = 9F, // 概览图的比例
    private var overviewMargin: Float = 5.dpF, // 概览图margin
    private var overviewPadding: Float = 5.dpF,
    private var screenHeight: Float = 18.dpF,
    private var headerHeight: Float = 0F,
    private var overviewBackgroundColor: Int = getColor(R.color.color_7e000000), // 序号／概览图背景颜色
    private var overviewSelectedColor: Int = getColor(R.color.color_004696), // 概览图选中颜色
    private var overviewDisabledColor: Int = getColor(R.color.color_8898c2), // 概览图已售颜色
    private var overviewOptionalColor: Int = getColor(R.color.color_ffffff), // 概览图可选颜色
    private var overviewOptionalCoveredColor: Int = getColor(R.color.color_40ffffff), // 概览图遮盖颜色
    val refresh: () -> Unit,
) {
    private val matrixValues: MatrixValues = seatChart.matrixValues // 矩阵值对象
    private var seatViewWidth: Float = 0F
    private var seatViewHeight: Float = 0F // view宽,高
    private var overviewWidth: Float = 0F
    private var overviewHeight: Float = 0F // 概览图的宽度, 高度
    private var overviewItemWidth: Float = 0F
    private var overviewItemHeight: Float = 0F// 概览图白色方块宽度, 高度
    private var overviewOffsetX: Float = 0F
    private var overviewOffsetY: Float = 0F // 缩略图 X, Y 偏移量
    private var overviewHorizontalSpacing: Float = 0F
    private var overviewVerticalSpacing: Float = 0F // 概览图上方块的水平, 垂直间距

    private var row: Int = 0
    private var column: Int = 0
    private var seatItemWidth: Int = 0
    private var seatItemHeight: Int = 0
    private var horizontalSpacing: Float = 0F
    private var verticalSpacing: Float = 0F
    private var indexesWidth: Float = 0F

    private val canvas by lazy { Canvas() }
    private val overviewRectF by lazy { RectF() }
    private val paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
    }
    private val borderPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 1.dpF
        }
    }

    private var mHandler = Handler(Looper.getMainLooper())

    private val hideOverviewRunnable = Runnable {
        isDrawOverview = false
        refresh()
    }

    private var seatManager: SeatManager<*, *>? = null
    private var overviewBitmap: Bitmap? = null

    var isDrawOverview = false // 标识是否需要绘制概览图
    var isUpdateOverview = true // 标识是否需要更新概览图

    fun actionDown() {
        isDrawOverview = true
        mHandler.removeCallbacks(hideOverviewRunnable)
    }

    fun actionUp() {
        mHandler.postDelayed(hideOverviewRunnable, 1300)
    }

    fun reset() {
        isDrawOverview = false
        isUpdateOverview = true
    }

    fun init(
        seatManager: SeatManager<*, *>,
        seatViewWidth: Float,
        seatViewHeight: Float,
    ) {
        this.seatManager = seatManager
        this.seatViewWidth = seatViewWidth
        this.seatViewHeight = seatViewHeight
        this.headerHeight = seatHeader.height
        this.row = seatManager.getMaxRow()
        this.column = seatManager.getMaxColumn()
        seatItemWidth = seatIcon.width
        seatItemHeight = seatIcon.height
        horizontalSpacing = seatChart.horizontalSpacing
        verticalSpacing = seatChart.verticalSpacing
        indexesWidth = seatIndexes.width

        overviewItemHeight = seatItemHeight / overviewScale - 4
        overviewItemWidth = seatItemWidth / overviewScale - 4
        overviewHorizontalSpacing = horizontalSpacing / overviewScale + 4
        overviewVerticalSpacing = verticalSpacing / overviewScale + 4

        overviewWidth = column * overviewItemWidth + (column + 1) * overviewHorizontalSpacing + overviewPadding * 2
        overviewHeight = row * overviewItemHeight + (row + 1) * overviewVerticalSpacing + overviewPadding * 2
        overviewBitmap = Bitmap.createBitmap(overviewWidth.toInt(), overviewHeight.toInt(), Bitmap.Config.ARGB_8888)

        overviewOffsetX = seatIndexes.edge + indexesWidth + overviewMargin
        overviewOffsetY = headerHeight + screenHeight + overviewMargin
        overviewRectF.right = overviewWidth
        overviewRectF.bottom = overviewHeight
    }

    /**
     * 座位图布局调整
     */
    fun layout(seatViewWidth: Int, seatViewHeight: Int) {
        this.seatViewWidth = seatViewWidth.toFloat()
        this.seatViewHeight = seatViewHeight.toFloat()
    }

    /**
     * 绘制概览
     */
    fun draw(
        canvas: Canvas,
    ) {
        if (isUpdateOverview) {
            isUpdateOverview = false
            createOverviewBitmap()
        }
        if (isDrawOverview) {
            overviewBitmap?.apply {
                canvas.drawBitmap(this, overviewOffsetX, overviewOffsetY, null)
            }
            drawRedBorder(canvas = canvas)
        }

    }

    /**
     * 绘制概览红框
     */
    fun drawRedBorder(
        canvas: Canvas,
    ) {
        //绘制红色框
        var left = -matrixValues.dx
        if (left < 0) {
            left = 0f
        }
        left /= overviewScale
        left /= matrixValues.sx

        var currentWidth = matrixValues.dx + (column * seatItemWidth + horizontalSpacing * (column - 1)) * matrixValues.sx + overviewPadding
        currentWidth = if (currentWidth > seatViewWidth) {
            currentWidth - seatViewWidth
        } else {
            0F
        }
        val right = overviewWidth - overviewPadding * 2 - currentWidth / overviewScale / matrixValues.sx

        var top = -matrixValues.dy + headerHeight
        if (top < 0) {
            top = 0F
        }
        top /= overviewScale
        top /= matrixValues.sy
        if (top > 0) {
            top += overviewVerticalSpacing
        }

        var currentHeight = matrixValues.dy + (row * seatItemHeight + verticalSpacing * (row - 1)) * matrixValues.sy + overviewPadding
        currentHeight = if (currentHeight > seatViewHeight) {
            currentHeight - seatViewHeight
        } else {
            0F
        }
        val bottom = overviewHeight - overviewPadding * 2 - currentHeight / overviewScale / matrixValues.sy

        canvas.drawRect(left + overviewOffsetX + overviewPadding, top + overviewOffsetY + overviewPadding,
            right + overviewOffsetX + overviewPadding, bottom + overviewOffsetY + overviewPadding, borderPaint)
    }

    private fun createOverviewBitmap() {
        seatManager?.apply {
            //绘制透明灰色背景
            paint.color = overviewBackgroundColor
            overviewBitmap?.eraseColor(Color.TRANSPARENT)
            canvas.setBitmap(overviewBitmap)

            canvas.drawRoundRect(overviewRectF, indexesWidth / 2, indexesWidth / 2, paint)

            paint.color = Color.WHITE

            val itemHeight = overviewItemHeight + overviewVerticalSpacing
            val itemWidth = overviewItemWidth + overviewHorizontalSpacing
            var top = -itemHeight + overviewVerticalSpacing + overviewPadding
            for (i in 0 until row) {
                top += itemHeight

                var left = -itemWidth + overviewHorizontalSpacing + overviewPadding
                loop@ for (j in 0 until column) {
                    left += itemWidth

                    when (getSeatType(i, j)) {
                        SeatType.SEAT_BLANK -> continue@loop
                        SeatType.SEAT_COVERED -> paint.color = overviewOptionalCoveredColor
                        SeatType.SEAT_OPTIONAL,
                        SeatType.SEAT_COUPLE_OPTIONAL_L,
                        SeatType.SEAT_COUPLE_OPTIONAL_R,
                        SeatType.SEAT_DISABILITY_OPTIONAL,
                        SeatType.SEAT_REPAIR -> paint.color = overviewOptionalColor
                        SeatType.SEAT_SELECTED,
                        SeatType.SEAT_COUPLE_SELECTED_L,
                        SeatType.SEAT_COUPLE_SELECTED_R,
                        SeatType.SEAT_DISABILITY_SELECTED -> paint.color = overviewSelectedColor
                        SeatType.SEAT_DISABLED,
                        SeatType.SEAT_COUPLE_DISABLED_L,
                        SeatType.SEAT_COUPLE_DISABLED_R -> paint.color = overviewDisabledColor
                        // 分区座位图
                        SeatType.AREA_OPTIONAL_1,
                        SeatType.AREA_OPTIONAL_2,
                        SeatType.AREA_OPTIONAL_3,
                        SeatType.AREA_OPTIONAL_4,
                        SeatType.AREA_OPTIONAL_5,
                        SeatType.AREA_COP_OPTIONAL_1_L,
                        SeatType.AREA_COP_OPTIONAL_2_L,
                        SeatType.AREA_COP_OPTIONAL_3_L,
                        SeatType.AREA_COP_OPTIONAL_4_L,
                        SeatType.AREA_COP_OPTIONAL_5_L,
                        SeatType.AREA_COP_OPTIONAL_1_R,
                        SeatType.AREA_COP_OPTIONAL_2_R,
                        SeatType.AREA_COP_OPTIONAL_3_R,
                        SeatType.AREA_COP_OPTIONAL_4_R,
                        SeatType.AREA_COP_OPTIONAL_5_R,
                        -> paint.color = overviewOptionalColor
                        SeatType.AREA_SELECTED_1,
                        SeatType.AREA_SELECTED_2,
                        SeatType.AREA_SELECTED_3,
                        SeatType.AREA_SELECTED_4,
                        SeatType.AREA_SELECTED_5,
                        SeatType.AREA_COP_SELECTED_1_L,
                        SeatType.AREA_COP_SELECTED_2_L,
                        SeatType.AREA_COP_SELECTED_3_L,
                        SeatType.AREA_COP_SELECTED_4_L,
                        SeatType.AREA_COP_SELECTED_5_L,
                        SeatType.AREA_COP_SELECTED_1_R,
                        SeatType.AREA_COP_SELECTED_2_R,
                        SeatType.AREA_COP_SELECTED_3_R,
                        SeatType.AREA_COP_SELECTED_4_R,
                        SeatType.AREA_COP_SELECTED_5_R,
                        -> paint.color = overviewSelectedColor
                    }
                    canvas.drawRect(left, top, left + overviewItemWidth, top + overviewItemHeight, paint)
                }
            }
        }
    }

    fun recycle() {
        overviewBitmap?.recycle()
    }
}