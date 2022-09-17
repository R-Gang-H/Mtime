package com.kotlin.android.chart

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import kotlin.math.ceil

/**
 * 直角坐标系下的图表
 */
open class CoordinateSystem(context: Context, attrs: AttributeSet) :
    Chart(context, attrs) {

    var isShadow = false // 默认没有阴影
    var isDash = true // 默认虚线
    var isShowDotValue = false // 是否显示圆点值
    var isAllShowDot = false // 是否显示全部圆点
    var isOneShowDot = true // 触摸显示单个圆点
    var isDotCircle = true // 圆点带圈
    var isFixedAft = false // Y轴固定间隔，适用于图表高度自适应
    var intervalY = 25 // 默认Y每个间隔25
    var intervalX = 28 // 默认X每个间隔28
    var isFixedHeight = false // 图表固定高度，适用于固定格子
    var intervalYCount = 6 // 固定有几个格
    var dotCircleSize = dp2px(4f) // 圆点大小
    var leftWidth = dp2px(41.5f) //曲线距离左边宽度
    var dividerSpaceYL = dp2px(41.5f) // 分割线左边距
    var dividerSpaceYR = dp2px(12f) // 分割线右边距
    var valueSpaceYL = dp2px(12f) // y轴值左边距
    var valueDividerSpace = dp2px(0f) // 值距离分割线上下边距
    var valueTop = dp2px(6f) // 曲线上面值的边距
    var xValueBottom = dp2px(14f) //x轴值距离底部边距

    var animation = false // 是否开启动画
    var animationDuration = 500L // 动画时长

    var focusedDataIndex: Int = -1 // 用户点击的数据下标

    init {
        initPaint()
    }

    /**
     * 初始化画笔
     *
     * @linpaint 线上值画笔
     * @baseShadow 阴影画笔
     */
    fun initPaint() {
        if (isShowDotValue) {
            //线上值的画笔
            linePaint = Paint()
            linePaint?.color = Color.parseColor("#20A0DA")
            linePaint?.isAntiAlias = true
            linePaint?.textSize = dp2px(12f)
            linePaint?.strokeWidth = dp2px(1f)
        }

        //画x/y轴文字的画笔
        textPaint = Paint()
        textPaint?.color = Color.parseColor("#8798AF")
        textPaint?.isAntiAlias = true
        textPaint?.textSize = dp2px(12f)

        if (isAllShowDot || isOneShowDot) { // 显示全部圆点 || 触摸选中当前点
            if (isDotCircle) {
                // 圆点圈画笔
                circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
                circlePaint?.color = Color.WHITE
                circlePaint?.strokeWidth = dp2px(1f)
                circlePaint?.style = Paint.Style.STROKE
            }
            // 圆点画笔
            circlePaint2 = Paint(Paint.ANTI_ALIAS_FLAG)
            circlePaint2?.color = Color.parseColor("#20A0DA")
            circlePaint2?.style = Paint.Style.FILL
        }

        if (isShadow) { // 开启阴影
            baseShadow = Paint()
            baseShadow?.isAntiAlias = true
            baseShadow?.color = Color.WHITE and 0x40FFFFFF or 0x10000000
            baseShadow?.style = Paint.Style.FILL
        }

        buttomHeiht = dp2px(40.5f) //线距离底部高度

        // 曲线画笔
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint?.color = Color.parseColor("#303A47")
        mPaint?.strokeWidth = dp2px(1f)
        mPaint?.style = Paint.Style.STROKE
        mPaint?.strokeCap = Paint.Cap.ROUND

        // x轴横线条
        scorePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        scorePaint?.style = Paint.Style.STROKE
        scorePaint?.strokeCap = Paint.Cap.ROUND
        scorePaint?.color = Color.parseColor("#E7E8ED")
        scorePaint?.strokeWidth = dp2px(1f)

        // y轴横线条
        datePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        datePaint?.style = Paint.Style.STROKE
        datePaint?.strokeCap = Paint.Cap.ROUND
        datePaint?.color = Color.parseColor("#20A0DA")
        datePaint?.strokeWidth = dp2px(1f)

        baseLinePath = Path()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (YValueList == null || XValueList == null) return
        measure()
        toGetXy() //获取x和y的坐标
        if (!animation) {
            toDrawLine(canvas)
        }
    }

    fun measure() {
        val text = "分"
        val rect = Rect()
        textPaint?.getTextBounds(text, 0, text.length, rect)
        oneHeight = (sumHeight - buttomHeiht - 2 * rect.height()) / maxTime.toFloat()
        if (isFixedHeight) {
            oneWidth = sumWidth / intervalX; // x固定间隔
        } else {
            XValueList?.apply {
                oneWidth = px2dp((sumWidth - dividerSpaceYL - dividerSpaceYR -
                        rect.width() * (size + 1)) / size)
            }
        }
    }

    fun toGetXy() {
        xyList = ArrayList()
        for (i in XValueList?.indices as IntRange) {
            val x = oneWidth + i * 4 * oneWidth
            val valueY = YValueList?.get(i) //y轴每点的值
            val y = sumHeight - oneHeight * valueY?.toFloat() as Float
            xyList?.add(PointF(x + leftWidth, y - buttomHeiht))
        }
    }

    /**
     * 画线
     */
    fun toDrawLine(canvas: Canvas) {
        if (xyList == null || xyList?.size == 0) {
            return
        }

        val NewPoints: MutableList<PointF> = ArrayList()
        xyList?.apply {
            NewPoints.addAll(this)
        }

        drawBottomLine(canvas)
        drawLine(canvas)

        var lX = 0f
        var lY = 0f
        baseLinePath?.reset()
        baseLinePath?.moveTo(NewPoints[0].x, NewPoints[0].y)
        for (i in 1 until NewPoints.size) {
            val p = NewPoints[i]
            val firstPointF = NewPoints[i - 1]
            val x1 = firstPointF.x + lX
            val y1 = firstPointF.y + lY
            val secondPointF = NewPoints[if (i + 1 < NewPoints.size) i + 1 else i]
            lX = (secondPointF.x - firstPointF.x) / 2 * smoothness
            lY = (secondPointF.y - firstPointF.y) / 2 * smoothness
            val x2 = p.x - lX
            var y2 = p.y - lY
            if (y1 == p.y) {
                y2 = y1
            }
            baseLinePath?.cubicTo(x1, y1, x2, y2, p.x, p.y)
        }
        baseLinePath?.apply {
            mPaint?.let { canvas.drawPath(this, it) }
        }

        if (isShadow) {
            drawArea(canvas, NewPoints)
        }

        for (i in NewPoints.indices) {
            val x = NewPoints[i].x
            val y = NewPoints[i].y
            if (isShowDotValue) {
                val valueY = YValueList?.get(i)
                val mThumbText = valueY.toString()
                val rect = Rect()
                linePaint?.apply {
                    getTextBounds(mThumbText, 0, mThumbText.length, rect)
                    canvas.drawText(mThumbText,
                        x - rect.width() / 2,
                        y - valueTop,
                        this) //画最上面字体
                }
            }

            if (isAllShowDot || i == focusedDataIndex) { // 显示圆点 || 触摸选中当前点
                if (isDotCircle) {
                    circlePaint?.apply {
                        canvas.drawCircle(x, y, dotCircleSize + 1, this)
                    }
                }
                circlePaint2?.apply {
                    canvas.drawCircle(x, y, dotCircleSize, this)
                }
            }
            XValueList?.apply {
                val rectf = Rect()
                textPaint?.getTextBounds(get(i), 0, get(i).length, rectf)
                textPaint?.apply {
                    canvas.drawText(get(i),
                        x - rectf.width() / 2,
                        sumHeight - xValueBottom,
                        this) //画最下方的字体
                }
            }
        }

    }

    /**
     * 分割线
     *
     * @param canvas
     */
    private fun drawLine(canvas: Canvas) {
        //
        var text: String
        var number: Int
        val rect = Rect()

        if (isFixedAft) { // 固定间隔宽度
            //text 计算间隔数
            intervalYCount = ceil(maxTime / intervalY).toInt() // 向上取整计算，y有几个间隔
        } else { // 默认固定图表宽度
            //text 计算间隔值
            intervalY = ceil(maxTime / intervalYCount).toInt() // 向上取整计算，y格子数
        }
        for (i in 1..intervalYCount) {
            text = (intervalY * i).toString() // 每个y间隔名称
            number = intervalY * i // 每个y间隔值
            var newnumber = ""
            textPaint?.apply {
                if (text.length > 2) {
                    newnumber = (text.substring(0, 2).toInt() + 1).toString() // 取前两位第三位进一
                    (1..(text.length - 2)).forEach {
                        newnumber += "0" // 补0
                    }
                } else {
                    newnumber = text
                }
                val y = sumHeight - oneHeight * number
                canvas.drawText(newnumber,
                    valueSpaceYL,
                    y - buttomHeiht - rect.height() / 2 - valueDividerSpace,
                    this)

                if (isDash) { // 是否虚线
                    //绘制长度为4的实线后再绘制长度为15的空白区域，0位间隔
                    scorePaint?.pathEffect = DashPathEffect(floatArrayOf(15f, 15f), 0f)
                }
                scorePaint?.apply {
                    canvas.drawLine(dividerSpaceYL,
                        y - buttomHeiht,
                        sumWidth - dividerSpaceYR,
                        y - buttomHeiht,
                        this)
                }
            }
        }
        /* //100
        text = "100(分)";
        float y2 = (sumHeight - (oneHeight * 100));
        canvas.drawText(text, 0, y2 - buttomHeiht - rect.height() / 2 - dp2px(4), textPaint);
        canvas.drawLine(0, y2 - buttomHeiht, sumWidth, y2 - buttomHeiht, scorePaint);*/
    }

    /**
     * 底部标线
     *
     * @param canvas
     */
    private fun drawBottomLine(canvas: Canvas) {
        // 0
        val rect = Rect()
        val text = "0"
        scorePaint?.getTextBounds(text, 0, text.length, rect);
        //画底部y轴0
        textPaint?.apply {
            val y2 = sumHeight - oneHeight * 0
            canvas.drawText(text,
                valueSpaceYL,
                y2 - buttomHeiht - rect.height() / 2 - valueDividerSpace,
                this)
            //画底部灰线
            textPaint?.apply {
                canvas.drawLine(dividerSpaceYL,
                    y2 - buttomHeiht,
                    sumWidth - dividerSpaceYR,
                    y2 - buttomHeiht,
                    this)
            }
        }
    }

    /**
     * 阴影层叠
     *
     * @param canvas
     * @param Points
     */
    private fun drawArea(canvas: Canvas, Points: List<PointF>) {
        val mShader = LinearGradient(0f,
            0f,
            0f,
            measuredHeight.toFloat(),
            intArrayOf(Color.parseColor("#BAEFE6"),
                Color.parseColor("#D7F5F0"),
                Color.parseColor("#F9FEFD")),
            floatArrayOf(0.5f, 0.65f, 0.85f),
            Shader.TileMode.REPEAT)
        baseShadow?.shader = mShader
        xyList?.apply {
            if (Points.isNotEmpty() && this.size > 0) {
                baseLinePath?.lineTo(get(Points.size - 1).x, sumHeight - buttomHeiht)
                baseLinePath?.lineTo(get(0).x, sumHeight - buttomHeiht)
                baseLinePath?.close()
                baseLinePath?.apply {
                    baseShadow?.let { canvas.drawPath(this, it) }
                }
            }
        }
    }
}