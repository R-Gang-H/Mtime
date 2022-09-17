package com.kotlin.android.film.widget.seat

import android.graphics.*
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.dimension.spF
import java.util.ArrayList

/**
 *
 * Created on 2022/2/15.
 *
 * @author o.s
 */
internal class SeatHeader(
    private val seatIcon: SeatIcon, // 座位icon
    var labelVerticalSpacing: Float = 7.dpF, // 标签垂直间隔
    var labelHorizontalSpacing: Float = 20.dpF, // 标签水平间隔
    var areaLabelVerticalSpacing: Float = 6.dpF, // 分区标签垂直间隔
    var areaLabelHorizontalSpacing: Float = 20.dpF, // 分区标签水平间隔
    var labelTextPadding: Float = 5.dpF, // 标签垂直间隔
    var labelMargin: Float = 20.dpF, //
    private val labelTextSize: Float = 14.spF,
) {
    private val labels = ArrayList<Label>() // 标签列表
    private val areaLabels = ArrayList<Label>() // 标签列表
    private val areaLabelMatrix by lazy { Matrix() } // 分区标签矩阵（左右滚动）
    private val areaLabelMatrixValues by lazy { MatrixValues() } // 矩阵值对象
    private val areaLabelM = FloatArray(9)

    private var areaLabelsWidth: Float = 0F

    private var isArea: Boolean = false // 是否分区

    private var _height: Float = 0F
    private var _labelHeight: Float = 0F

    private var seatViewWidth: Float = 0F

    private val paint by lazy {
        Paint().apply {
            style = Paint.Style.FILL
            textSize = labelTextSize
            isAntiAlias = true
        }
    }

    val labelHeight: Float
        get() = _labelHeight

    val height: Float
        get() = _height

    fun init(
        seatManager: SeatManager<*, *>,
        seatViewWidth: Float
    ) {
        this.seatViewWidth = seatViewWidth
        this.isArea = seatManager.isArea()
        if (isArea) {
            initAreaLabel(seatManager)
        } else {
            initLabel()
        }
    }

    fun draw(canvas: Canvas) {
        measure()

        paint.color = Color.WHITE
        //绘制背景
        canvas.drawRect(0F, 0F, seatViewWidth, height, paint)

        labels.forEach {
            it.draw(canvas = canvas)
        }

        areaLabels.forEach {
            it.draw(canvas = canvas)
        }

    }

    /**
     * 触摸滚动分区标签:
     * [dx] x 坐标变化量.
     */
    fun touchAreaLabels(dx: Float): Boolean {
        val l = labelMargin
        val r = seatViewWidth - labelMargin
        if (areaLabelsWidth > r - l) {
            updateAreaLabelMatrixValues()
            val offsetL = areaLabelMatrixValues.dx
            val offsetR = areaLabelsWidth + areaLabelMatrixValues.dx + l
//            LogManager.e(LogManager.NET_TAG, String.format( "l = %f, r = %f, offsetL= %f, " +
//                    "offsetR = %f, dx = %f, areaLabelMatrixValues.dx = %f, areaLabelsWidth = %f",
//                    l, r, offsetL, offsetR, dx, areaLabelMatrixValues.dx, areaLabelsWidth))
            if (dx < 0 && offsetR > r) {
                areaLabelMatrix.postTranslate(dx, 0f)
                return true
            } else if (dx > 0 && offsetL < 0) {
                areaLabelMatrix.postTranslate(dx, 0f)
                return true
            }
        }
        return false
    }

    fun updateAreaLabelMatrixValues(): MatrixValues {
        areaLabelMatrix.getValues(areaLabelM)
        areaLabelMatrixValues.dx = areaLabelM[Matrix.MTRANS_X]
        return areaLabelMatrixValues
    }

    fun reset() {
        areaLabelMatrix.preTranslate(0f, 0f)
        areaLabelMatrix.reset()
    }

    fun clear() {
        areaLabelMatrixValues.clear()
    }

    private fun measure() {

    }

    private fun initLabel() {
        labels.clear()
        labels.add(
            Label(
                areaLabelMatrixValues = areaLabelMatrixValues,
                seatIcon = seatIcon,
                type = SeatType.SEAT_OPTIONAL,
                labelVerticalSpacing = labelVerticalSpacing,
                labelTextPadding = labelTextPadding,
                labelTextSize = labelTextSize,
                isArea = isArea,
                title = "可选",
            )
        )
        labels.add(
            Label(
                areaLabelMatrixValues = areaLabelMatrixValues,
                seatIcon = seatIcon,
                type = SeatType.SEAT_SELECTED,
                labelVerticalSpacing = labelVerticalSpacing,
                labelTextPadding = labelTextPadding,
                labelTextSize = labelTextSize,
                isArea = isArea,
                title = "已选",
            )
        )
        labels.add(
            Label(
                areaLabelMatrixValues = areaLabelMatrixValues,
                seatIcon = seatIcon,
                type = SeatType.SEAT_DISABLED,
                labelVerticalSpacing = labelVerticalSpacing,
                labelTextPadding = labelTextPadding,
                labelTextSize = labelTextSize,
                isArea = isArea,
                title = "已售",
            )
        )
        labels.add(
            Label(
                areaLabelMatrixValues = areaLabelMatrixValues,
                seatIcon = seatIcon,
                type = SeatType.SEAT_COUPLE_OPTIONAL_L,
                labelVerticalSpacing = labelVerticalSpacing,
                labelTextPadding = labelTextPadding,
                labelTextSize = labelTextSize,
                isArea = isArea,
                title = "情侣座",
            )
        )
//        labels.add(
//            Label(
//                areaLabelMatrixValues = areaLabelMatrixValues,
//                seatIcon = seatIcon,
//                type = SeatType.SEAT_DISABILITY_OPTIONAL,
//                labelVerticalSpacing = labelVerticalSpacing,
//                labelTextPadding = labelTextPadding,
//                labelTextSize = labelTextSize,
//                isArea = isArea,
//                title = "残疾人坐席"
//            )
//        )
//        labels.add(
//            Label(
//                areaLabelMatrixValues = areaLabelMatrixValues,
//                seatIcon = seatIcon,
//                type = SeatType.SEAT_REPAIR,
//                labelVerticalSpacing = labelVerticalSpacing,
//                labelTextPadding = labelTextPadding,
//                labelTextSize = labelTextSize,
//                isArea = isArea,
//                title = "待维修座位"
//            )
//        )

        handleLabel()

        _height = labels.last().let {
            it.y + seatIcon.labelHeight + labelVerticalSpacing
        }
        _labelHeight = _height
    }

    private fun initAreaLabel(seatManager: SeatManager<*, *>) {
        areaLabels.clear()
        val areas = seatManager.getArea()
        areas.forEach {
            val type = when (it.areaLevel) {
                AreaLevel.AREA_LEVEL_1 -> SeatType.AREA_OPTIONAL_1
                AreaLevel.AREA_LEVEL_2 -> SeatType.AREA_OPTIONAL_2
                AreaLevel.AREA_LEVEL_3 -> SeatType.AREA_OPTIONAL_3
                AreaLevel.AREA_LEVEL_4 -> SeatType.AREA_OPTIONAL_4
                AreaLevel.AREA_LEVEL_5 -> SeatType.AREA_OPTIONAL_5
            }
            areaLabels.add(
                Label(
                    areaLabelMatrixValues = areaLabelMatrixValues,
                    seatIcon = seatIcon,
                    type = type,
                    areaLabelVerticalSpacing = areaLabelVerticalSpacing,
                    labelTextPadding = labelTextPadding,
                    labelTextSize = labelTextSize,
                    isArea = isArea,
                    title = it.areaPriceFlag,
                )
            )
        }

        handleAreaLabel()

        _height = areaLabels.last().let {
            it.y + seatIcon.labelHeight + areaLabelVerticalSpacing
        }
        _labelHeight = _height
    }

    private fun handleLabel(limit: Int = 4) {
        if (labels.isEmpty() || limit < 1) {
            return
        }

        val size = labels.size
        if (size <= limit) {
            // 第一行
            var totalWidth = 0F
            labels.forEach {
                totalWidth += it.width
            }
            totalWidth += (size - 1) * labelHorizontalSpacing
            val x = (seatViewWidth - totalWidth) / 2

            if (size > 0) {
                val label = labels[0]
                label.x = x
            }
            for (i in 1 until size) {
                handleSameRow(labels[i - 1], labels[i])
            }
        } else {
            // 第一行
            var totalWidth1 = 0F
            (0 until limit).forEach {
                totalWidth1 += labels[it].width
            }
            totalWidth1 += (limit - 1) * labelHorizontalSpacing
            val x1 = (seatViewWidth - totalWidth1) / 2
            if (size > 0) {
                val label = labels[0]
                label.x = x1
            }
            for (i in 1 until limit) {
                handleSameRow(labels[i - 1], labels[i])
            }

            val rowY1 = labels[0].y
            var totalWidth2 = 0F

            // 第二行
            (limit until size).forEach {
                val label = labels[it]
                label.y = rowY1 + seatIcon.labelHeight + labelVerticalSpacing
                totalWidth2 += label.width
            }
            totalWidth2 += (size - limit - 1) * labelHorizontalSpacing
            val x2 = (seatViewWidth - totalWidth2) / 2

            val label = labels[limit]
            label.x = x2
            for (i in limit + 1 until size) {
                handleSameRow(labels[i - 1], labels[i])
            }
        }
    }

    private fun handleAreaLabel() {
        val size = areaLabels.size
        areaLabelsWidth = 0f
        areaLabels.forEachIndexed { index, label ->
            areaLabelsWidth += label.width
            if (index < size - 1) {
                areaLabelsWidth += areaLabelHorizontalSpacing
            }
        }
        var areaLabelMargin = (seatViewWidth - areaLabelsWidth) / 2
        if (areaLabelMargin < labelMargin) {
            areaLabelMargin = labelMargin
        }
        if (size > 0) {
            val label = areaLabels[0]
            label.x = areaLabelMargin
        }
        for (i in 1 until size) {
            handleAreaSameRow(areaLabels[i - 1], areaLabels[i])
        }
    }

    /**
     * 处理同行标签
     */
    private fun handleSameRow(pre: Label, label: Label) {
        label.x = pre.x + pre.width + labelHorizontalSpacing
    }

    /**
     * 处理分区同行标签
     */
    private fun handleAreaSameRow(pre: Label, label: Label) {
        label.x = pre.x + pre.width + areaLabelHorizontalSpacing
    }
}

internal class Label(
    private val areaLabelMatrixValues: MatrixValues, // 矩阵值对象
    private val seatIcon: SeatIcon, // 座位icon
    val type: SeatType,
    val labelVerticalSpacing: Float = 7.dpF, // 标签垂直间隔
    val areaLabelVerticalSpacing: Float = 6.dpF, // 标签垂直间隔
    val labelTextPadding: Float = 5.dpF, // 标签垂直间隔
    private val labelTextSize: Float = 14.spF,
    private val isArea: Boolean, // 是否分区
    val title: String,
) {
    private val paint by lazy {
        Paint().apply {
            style = Paint.Style.FILL
            textSize = labelTextSize
            isAntiAlias = true
        }
    }

    private var areaX: Float = 0F // y 偏移量
    private var baseline: Float = 0F
    private val textMarginStart: Float

    val width: Float

    var x: Float = 0F // x 偏移量

    var y: Float = 0F // y 偏移量
        set(value) {
            field = value
            baseline = paint.getBaseline(top = y, bottom = y + seatIcon.labelHeight)
        }

    init {
        y = if (isArea) {
            areaLabelVerticalSpacing
        } else {
            labelVerticalSpacing
        }

        textMarginStart = if (SeatType.SEAT_COUPLE_OPTIONAL_L == type || SeatType.SEAT_COUPLE_OPTIONAL_R == type ) {
            // 情侣座两个 icon label width
            seatIcon.labelWidth + seatIcon.labelWidth + labelTextPadding
        } else {
            seatIcon.labelWidth + labelTextPadding
        }

        width = textMarginStart + paint.measureText(title)
    }

    fun draw(canvas: Canvas) {
        val x = if (isArea) {
            measure()
            areaX
        } else {
            x
        }

        getBitmap()?.apply {
            canvas.drawBitmap(this, x, y, paint)
        }
        getCoupleBitmapRight()?.apply {
            canvas.drawBitmap(this, x + seatIcon.labelWidth - 1, y, paint)
        }
        canvas.drawText(title, x + textMarginStart, baseline, paint)
    }

    private fun measure() {
        areaX = areaLabelMatrixValues.dx + x
    }

    /**
     * 包含情侣座【左】
     */
    private fun getBitmap(): Bitmap? {
        return when (type) {
            SeatType.SEAT_OPTIONAL -> seatIcon.labelOptional
            SeatType.SEAT_SELECTED -> seatIcon.labelSelected
            SeatType.SEAT_DISABLED -> seatIcon.labelDisabled
            SeatType.SEAT_DISABILITY_OPTIONAL -> seatIcon.labelDisabilityOptional
            SeatType.SEAT_REPAIR -> seatIcon.labelRepair
            SeatType.SEAT_COUPLE_OPTIONAL_L,
            SeatType.SEAT_COUPLE_OPTIONAL_R -> seatIcon.labelCoupleOptionalLeft

            SeatType.AREA_OPTIONAL_1 -> seatIcon.labelOptional1
            SeatType.AREA_OPTIONAL_2 -> seatIcon.labelOptional2
            SeatType.AREA_OPTIONAL_3 -> seatIcon.labelOptional3
            SeatType.AREA_OPTIONAL_4 -> seatIcon.labelOptional4
            SeatType.AREA_OPTIONAL_5 -> seatIcon.labelOptional5
            else -> {
                null
            }
        }
    }

    /**
     * 情侣座【右】只有在情侣座标识时出现
     */
    private fun getCoupleBitmapRight(): Bitmap? {
        return when (type) {
            SeatType.SEAT_COUPLE_OPTIONAL_L,
            SeatType.SEAT_COUPLE_OPTIONAL_R -> seatIcon.labelCoupleOptionalRight
            else -> {
                null
            }
        }
    }
}