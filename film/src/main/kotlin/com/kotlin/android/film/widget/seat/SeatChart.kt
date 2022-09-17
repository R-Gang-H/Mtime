package com.kotlin.android.film.widget.seat

import android.graphics.*
import com.kotlin.android.ktx.ext.dimension.dpF

/**
 * 座位绘制
 *
 * Created on 2022/2/10.
 *
 * @author o.s
 */
internal class SeatChart(
//    private val matrixValues: MatrixValues, // 矩阵值对象
    private val seatIcon: SeatIcon, // 座位icon
    var horizontalSpacing: Float = 0F, // 座位列间距
    var verticalSpacing: Float = 4.dpF, // 座位排间距
    var edge: Float = 5.dpF,
) {
    val matrixValues by lazy { MatrixValues() }
    private val mMatrix by lazy { Matrix() } // SeatView 矩阵
    private val m = FloatArray(9)

    private var seatViewWidth: Float = 0F
    private var seatViewHeight: Float = 0F // view宽,高
    private var dx: Float = 0F
    private var dy: Float = 0F
    private var sx: Float = 0F
    private var sy: Float = 0F
    private var hSpacing: Float = 0F
    private var vSpacing: Float = 0F
    private var itemWidth: Float = 0F
    private var itemHeight: Float = 0F
    private var itemVHeight: Float = 0F
    private var itemHWidth: Float = 0F

    private var _row: Int = 0
    private var _col: Int = 0
    private var _centerRow: Int = 0
    private var _centerCol: Int = 0
    private var _seatItemWidth: Int = 0
    private var _seatItemHeight: Int = 0
    private var _seatChartWidth: Float = 0F
    private var _seatChartHeight: Float = 0F
    private var _centerSeatChartWidth: Float = 0F
    private var _centerSeatChartHeight: Float = 0F
    private var _halfSeatChartWidth: Float = 0F
    private var _halfSeatChartHeight: Float = 0F
    private var _betterSeatChartWidth: Float = 0F

    private var isArea: Boolean = false // 是否分区

    private var seatManager: SeatManager<*, *>? = null

//    var resetScale = 0f // 重置座位图缩放比例

    /**
     * 座位图列数
     */
    val col: Int
        get() = _col

    /**
     * 座位图行数（排数）
     */
    val row: Int
        get() = _row

    /**
     * 座位图列中线
     */
    val centerCol: Int
        get() = _centerCol

    /**
     * 座位图行中线
     */
    val centerRow: Int
        get() = _centerRow

    /**
     * 座位图宽
     */
    val width: Float
        get() = _seatChartWidth

    /**
     * 座位图高
     */
    val height: Float
        get() = _seatChartHeight

    /**
     * 整屏显示的宽度
     */
    val betterSeatChartWidth: Float
        get() = _betterSeatChartWidth

    /**
     * 座位图中线宽
     */
    val centerWidth: Float
        get() = _centerSeatChartWidth

    /**
     * 座位图中线高
     */
    val centerHeight: Float
        get() = _centerSeatChartHeight

    /**
     * 座位图 X 中间点
     */
    val centerX: Float
        get() = _halfSeatChartWidth

    /**
     * 座位图 Y 中间点
     */
    val centerY: Float
        get() = _halfSeatChartHeight

    val matrix by lazy { Matrix() } // 座位矩阵
    val paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
            color = Color.WHITE
        }
    }

    init {
        _seatItemWidth = seatIcon.width
        _seatItemHeight = seatIcon.height
    }

    fun reset() {
//        resetScale = 0F
    }

    fun init(
        seatManager: SeatManager<*, *>,
        seatViewWidth: Float,
        seatViewHeight: Float,
        headerHeight: Float,
        screenHeight: Float,
        indexesWidth: Float,
    ) {
        this.seatManager = seatManager
        this.isArea = seatManager.isArea()
        this.seatViewWidth = seatViewWidth
        this.seatViewHeight = seatViewHeight
        _row = seatManager.getMaxRow()
        _col = seatManager.getMaxColumn()
        _centerRow = seatManager.getCenterRow()
        _centerCol = seatManager.getCenterCol()
        _seatChartWidth = _col * _seatItemWidth + (_col - 1) * horizontalSpacing
        _seatChartHeight = _row * _seatItemHeight + (_row - 1) * verticalSpacing
        _centerSeatChartWidth = _centerCol * _seatItemWidth + (_centerCol.toFloat() - 0.5F) * horizontalSpacing
        _centerSeatChartHeight = _centerRow * _seatItemHeight + (_centerRow.toFloat() - 0.5F)* verticalSpacing
        _halfSeatChartWidth = _seatChartWidth / 2
        _halfSeatChartHeight = _seatChartHeight / 2

        // 调整矩阵准备画座位图
        val seatChartX = seatViewWidth / 2 - _halfSeatChartWidth
        val seatChartY = headerHeight + screenHeight + edge // y不变
        _betterSeatChartWidth = seatViewWidth - (edge + indexesWidth + edge) * 2

//        if (resetScale == 0f) {
//            resetScale = _betterSeatChartWidth / _seatChartWidth
//            if (resetScale > 1.2f) {
//                resetScale = 1.2f
//            }
//        }

        updateMatrixValues()
        setMatrixTranslate(seatChartX, seatChartY)
    }

    /**
     * 座位图布局调整
     */
    fun layout(seatViewWidth: Int, seatViewHeight: Int) {
        this.seatViewWidth = seatViewWidth.toFloat()
        this.seatViewHeight = seatViewHeight.toFloat()
    }

    fun draw(
        canvas: Canvas,
    ) {
        seatManager?.apply {
            measure()

            var top = -itemVHeight + dy
            for (i in 0 until _row) {
                top += itemVHeight

                if (top < -itemHeight || top > seatViewHeight) {
                    continue
                }

                var left = -itemHWidth + dx
                loop@ for (j in 0 until _col) {
                    left += itemHWidth

//                seatPlugins?.handleClickDraw(i, j, left, top, matrixValues)

                    if (left < -itemWidth || left > seatViewWidth) {
                        continue
                    }

                    matrix.setTranslate(left, top)
//                    matrix.postScale(xScale, yScale, left, top)
                    matrix.postScale(sx, sy, left, top)
                    when (val seatType = getSeatType(i, j)) {
                        // 公用部分
                        SeatType.SEAT_BLANK -> continue@loop
                        SeatType.SEAT_COVERED -> {}
                        SeatType.SEAT_DISABLED -> drawDisabled(canvas = canvas, row = i, col = j)
                        SeatType.SEAT_COUPLE_OPTIONAL_L -> drawBitmap(canvas, seatIcon.coupleOptionalLeft)
                        SeatType.SEAT_COUPLE_OPTIONAL_R -> drawBitmap(canvas, seatIcon.coupleOptionalRight)
                        SeatType.SEAT_COUPLE_DISABLED_L -> drawBitmap(canvas, seatIcon.coupleDisabledLeft)
                        SeatType.SEAT_COUPLE_DISABLED_R -> drawBitmap(canvas, seatIcon.coupleDisabledRight)
                        SeatType.SEAT_COUPLE_SELECTED_L -> drawBitmap(canvas, seatIcon.coupleSelectedLeft)
                        SeatType.SEAT_COUPLE_SELECTED_R -> drawBitmap(canvas, seatIcon.coupleSelectedRight)
                        SeatType.SEAT_DISABILITY_OPTIONAL -> drawBitmap(canvas, seatIcon.disabilityOptional)
                        SeatType.SEAT_DISABILITY_SELECTED -> drawBitmap(canvas, seatIcon.disabilitySelected)
                        SeatType.SEAT_REPAIR -> drawBitmap(canvas, seatIcon.repair)
                        // 普通/分区（切换可选/选中状态）
                        SeatType.SEAT_OPTIONAL -> drawOptional(canvas, i, j)
                        SeatType.AREA_OPTIONAL_1 -> drawBitmap(canvas, seatIcon.optional1)
                        SeatType.AREA_OPTIONAL_2 -> drawBitmap(canvas, seatIcon.optional2)
                        SeatType.AREA_OPTIONAL_3 -> drawBitmap(canvas, seatIcon.optional3)
                        SeatType.AREA_OPTIONAL_4 -> drawBitmap(canvas, seatIcon.optional4)
                        SeatType.AREA_OPTIONAL_5 -> drawBitmap(canvas, seatIcon.optional5)
                        SeatType.AREA_COP_OPTIONAL_1_L -> drawBitmap(canvas, seatIcon.copOptional1L)
                        SeatType.AREA_COP_OPTIONAL_2_L -> drawBitmap(canvas, seatIcon.copOptional2L)
                        SeatType.AREA_COP_OPTIONAL_3_L -> drawBitmap(canvas, seatIcon.copOptional3L)
                        SeatType.AREA_COP_OPTIONAL_4_L -> drawBitmap(canvas, seatIcon.copOptional4L)
                        SeatType.AREA_COP_OPTIONAL_5_L -> drawBitmap(canvas, seatIcon.copOptional5L)
                        SeatType.AREA_COP_OPTIONAL_1_R -> drawBitmap(canvas, seatIcon.copOptional1R)
                        SeatType.AREA_COP_OPTIONAL_2_R -> drawBitmap(canvas, seatIcon.copOptional2R)
                        SeatType.AREA_COP_OPTIONAL_3_R -> drawBitmap(canvas, seatIcon.copOptional3R)
                        SeatType.AREA_COP_OPTIONAL_4_R -> drawBitmap(canvas, seatIcon.copOptional4R)
                        SeatType.AREA_COP_OPTIONAL_5_R -> drawBitmap(canvas, seatIcon.copOptional5R)
                        SeatType.SEAT_SELECTED -> drawSelected(canvas = canvas, seatType = seatType, row = i, col = j)
                        SeatType.AREA_SELECTED_1 -> drawSelected(canvas = canvas, seatType = seatType, row = i, col = j)
                        SeatType.AREA_SELECTED_2 -> drawSelected(canvas = canvas, seatType = seatType, row = i, col = j)
                        SeatType.AREA_SELECTED_3 -> drawSelected(canvas = canvas, seatType = seatType, row = i, col = j)
                        SeatType.AREA_SELECTED_4 -> drawSelected(canvas = canvas, seatType = seatType, row = i, col = j)
                        SeatType.AREA_SELECTED_5 -> drawSelected(canvas = canvas, seatType = seatType, row = i, col = j)
                        SeatType.AREA_COP_SELECTED_1_L -> drawBitmap(canvas, seatIcon.copSelected1L)
                        SeatType.AREA_COP_SELECTED_2_L -> drawBitmap(canvas, seatIcon.copSelected2L)
                        SeatType.AREA_COP_SELECTED_3_L -> drawBitmap(canvas, seatIcon.copSelected3L)
                        SeatType.AREA_COP_SELECTED_4_L -> drawBitmap(canvas, seatIcon.copSelected4L)
                        SeatType.AREA_COP_SELECTED_5_L -> drawBitmap(canvas, seatIcon.copSelected5L)
                        SeatType.AREA_COP_SELECTED_1_R -> drawBitmap(canvas, seatIcon.copSelected1R)
                        SeatType.AREA_COP_SELECTED_2_R -> drawBitmap(canvas, seatIcon.copSelected2R)
                        SeatType.AREA_COP_SELECTED_3_R -> drawBitmap(canvas, seatIcon.copSelected3R)
                        SeatType.AREA_COP_SELECTED_4_R -> drawBitmap(canvas, seatIcon.copSelected4R)
                        SeatType.AREA_COP_SELECTED_5_R -> drawBitmap(canvas, seatIcon.copSelected5R)
                    }
                }
            }
        }
    }

    val translateX: Float
        get() {
            mMatrix.getValues(m)
            return m[Matrix.MTRANS_X]
        }

    val translateY: Float
        get() {
            mMatrix.getValues(m)
            return m[Matrix.MTRANS_Y]
        }

    val matrixScaleY: Float
        get() {
            mMatrix.getValues(m)
            return m[Matrix.MSCALE_Y]
        }

    val matrixScaleX: Float
        get() {
            mMatrix.getValues(m)
            return m[Matrix.MSCALE_X]
        }

    fun updateMatrixValues(): MatrixValues {
        mMatrix.getValues(m)
        matrixValues.sx = m[Matrix.MSCALE_X]
        matrixValues.sy = m[Matrix.MSCALE_Y]
        matrixValues.dx = m[Matrix.MTRANS_X]
        matrixValues.dy = m[Matrix.MTRANS_Y]
        return matrixValues
    }

    fun setMatrixScale(sx: Float, sy: Float, px: Float, py: Float) {
        mMatrix.setScale(sx, sy, px, py)
    }

    fun setMatrixTranslate(dx: Float, dy: Float) {
        mMatrix.setTranslate(dx, dy)
    }

    fun postMatrixScale(sx: Float, sy: Float, px: Float, py: Float) {
        mMatrix.postScale(sx, sy, px, py)
    }

    fun postMatrixTranslate(dx: Float, dy: Float) {
        mMatrix.postTranslate(dx, dy)
    }

    fun resetMatrix() {
        mMatrix.preTranslate(0F, 0F)
        mMatrix.reset()
        initScale()
    }

    fun initScale() {
        setMatrixScale(1F, 1F, 0F, 0F)
    }

    private fun measure() {
        dx = matrixValues.dx
        dy = matrixValues.dy
        sx = matrixValues.sx
        sy = matrixValues.sy
        itemWidth = seatIcon.width * sx
        itemHeight = seatIcon.height * sy
        hSpacing = horizontalSpacing * sx
        vSpacing = verticalSpacing * sy
        itemHWidth = itemWidth + hSpacing
        itemVHeight = itemHeight + vSpacing
    }

    private fun drawOptional(canvas: Canvas, row: Int, col: Int) {
        seatIcon.getOptionalBitmap(row, col)?.apply {
            drawBitmap(canvas, this)
        } ?: optional(canvas)
    }

    private fun drawSelected(canvas: Canvas, seatType: SeatType, row: Int, col: Int) {
        seatIcon.getSelectedBitmap(row, col)?.apply {
            drawBitmap(canvas, this)
        } ?: selected(canvas, seatType)
    }

    private fun drawDisabled(canvas: Canvas, row: Int, col: Int) {
        seatIcon.getDisabledBitmap(row, col)?.apply {
            drawBitmap(canvas, this)
        } ?: drawBitmap(canvas, seatIcon.disabled)
    }

    private fun optional(canvas: Canvas) {
        if (isArea) {
            drawBitmap(canvas, seatIcon.optional1)
        } else {
            drawBitmap(canvas, seatIcon.optional)
        }
    }

    private fun selected(canvas: Canvas, seatType: SeatType) {
        when (seatType) {
            SeatType.AREA_SELECTED_1 -> drawBitmap(canvas, seatIcon.selected1)
            SeatType.AREA_SELECTED_2 -> drawBitmap(canvas, seatIcon.selected2)
            SeatType.AREA_SELECTED_3 -> drawBitmap(canvas, seatIcon.selected3)
            SeatType.AREA_SELECTED_4 -> drawBitmap(canvas, seatIcon.selected4)
            SeatType.AREA_SELECTED_5 -> drawBitmap(canvas, seatIcon.selected5)
            SeatType.SEAT_SELECTED -> {
                if (isArea) {
                    drawBitmap(canvas, seatIcon.selected1)
                } else {
                    drawBitmap(canvas, seatIcon.selected)
                }
            }
            else -> {}
        }
    }

    private fun drawBitmap(canvas: Canvas, bitmap: Bitmap?) {
        if (bitmap?.isRecycled == false) {
            canvas.drawBitmap(bitmap, matrix, paint)
        }
    }
}