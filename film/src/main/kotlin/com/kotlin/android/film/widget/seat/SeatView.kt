package com.kotlin.android.film.widget.seat

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.log.e
import kotlin.math.abs

/**
 * 座位图
 *
 * Created on 2022/2/9.
 *
 * @author o.s
 */
class SeatView : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    private var edge: Float = 5.dpF

    private val indexes by lazy { ArrayList<String>() }

    private val seatIcon by lazy { SeatIcon() }
    private val seatHeader by lazy {
        SeatHeader(
            seatIcon = seatIcon,
        )
    }
    private val seatChart by lazy {
        SeatChart(
            seatIcon = seatIcon,
            horizontalSpacing = 0F,
            verticalSpacing = 4.dpF,
            edge = edge
        )
    }
    private val seatCenterLineH by lazy {
        SeatCenterLine(
            seatChart = seatChart,
            orientation = Orientation.HORIZONTAL,
        )
    }
    private val seatCenterLineV by lazy {
        SeatCenterLine(
            seatChart = seatChart,
            orientation = Orientation.VERTICAL,
        )
    }
    private val seatIndexes by lazy {
        SeatIndexes(
            seatIcon = seatIcon,
            seatChart = seatChart,
            edge = edge
        )
    }
    private val seatScreen by lazy {
        SeatScreen(
            seatChart = seatChart,
            seatHeader = seatHeader,
        )
    }
    private val seatOverview by lazy {
        SeatOverview(
            seatIcon = seatIcon,
            seatChart = seatChart,
            seatIndexes = seatIndexes,
            seatHeader = seatHeader,
        ) {
            invalidate()
        }
    }

    private val seatScale by lazy {
        SeatScale(
            context = context,
            seatChart = seatChart,
        ) {
            invalidate()
        }
    }

    private val seatAnim by lazy {
        SeatAnim(
            seatChart = seatChart,
        ) {
            invalidate()
        }
    }

    private val seatClick by lazy {
        SeatClick(
            context = context,
            seatIcon = seatIcon,
            seatChart = seatChart,
            seatOverview = seatOverview,
            seatAnim = seatAnim,
        ) {
            invalidate()
        }
    }

    private var pointer: Boolean = false // 是否多个落点
    private var downX: Float = 0.toFloat()
    private var downY: Float = 0.toFloat() // 按下x，y坐标
    private var lastX: Float = 0.toFloat()
    private var lastY: Float = 0.toFloat() // 移动手势最新的 x, y 坐标

    private var seatManager: SeatManager<*, *>? = null

    private var seatViewWidth: Int = 0
    private var seatViewHeight: Int = 0 // view宽,高
    private var seatViewCenterX: Float = 0F
    private var seatViewY: Float = 0F
    private var row = 0
    private var col = 0
    private var isArea = false

    /**
     * 座位图标题，影厅名称
     */
    var title: String = ""
        set(value) {
            field = value
            seatScreen.title = value
        }

    /**
     * 选中的座位列表
     */
    val selectSeats: ArrayList<Int>
        get() = seatClick.selects

    /**
     * 设置座位图管理器
     */
    fun setSeatManager(seatManager: SeatManager<*, *>) {
        this.seatManager = seatManager
        seatManager.setSeatView(this)
    }

    fun notifyDataChanged() {
        post {
            init()
            refreshView()
        }
    }

    private fun refreshView() {
        reset()

        seatAnim.startFirstAnim()
    }

    private fun init() {
        seatManager?.apply {
            row = getMaxRow()
            col = getMaxColumn()
            isArea = isArea()

            seatChart.initScale()
            initIndexes()

            seatIndexes.init(
                indexes = indexes
            )
            seatHeader.init(
                seatManager = this,
                seatViewWidth = seatViewWidth.toFloat()
            )
            seatScreen.init(
                seatManager = this,
            )
            seatChart.init(
                seatManager = this,
                seatViewWidth = seatViewWidth.toFloat(),
                seatViewHeight = seatViewHeight.toFloat(),
                headerHeight = seatHeader.height,
                screenHeight = seatScreen.height,
                indexesWidth = seatIndexes.width,
            )
            seatOverview.init(
                seatManager = this,
                seatViewWidth = seatViewWidth.toFloat(),
                seatViewHeight = seatViewHeight.toFloat(),
            )

            seatClick.init(
                seatManager = this
            )

            seatViewY = seatHeader.height + seatScreen.height + edge

            seatAnim.init(
                seatViewWidth = seatViewWidth,
                seatViewHeight = seatViewHeight,
                seatViewCenterX = seatViewCenterX,
                seatViewY = seatViewY,
                indexesWidth = seatIndexes.width,
            )
        }
    }

    fun checkedSeat(i: Int, j: Int) {
        seatClick.checkedSeat(i, j)
    }

    fun unCheckedSeat(i: Int, j: Int) {
        seatClick.unCheckedSeat(i, j)
    }

    /**
     * 自动选座时开启重置座位动画
     */
    fun startResetAnim() {
        seatAnim.startResetAnim()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        seatViewWidth = measuredWidth
        seatViewHeight = measuredHeight

        seatViewCenterX = seatViewWidth / 2F
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        seatChart.layout(seatViewWidth, seatViewHeight)
        seatOverview.layout(seatViewWidth, seatViewHeight)
        seatAnim.layout(seatViewWidth, seatViewHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            if (row <= 1 || col == 1) {
                return
            }
            if (isArea) {
                seatHeader.updateAreaLabelMatrixValues()
            }
            seatChart.updateMatrixValues()
            seatCenterLineH.draw(canvas = this)
            seatCenterLineV.draw(canvas = this)
            seatChart.draw(canvas = this)
            seatIndexes.draw(canvas = this)
            seatScreen.draw(canvas = this)
            seatHeader.draw(canvas = this)
            seatOverview.draw(canvas = this)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        if (event != null) {
            val y = event.y
            val x = event.x

            seatScale.gestureDetector.onTouchEvent(event)
            seatClick.gestureDetector.onTouchEvent(event)

            val pointerCount = event.pointerCount
            if (pointerCount > 1) {
                pointer = true
            }

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 暂停动画
                    seatAnim.animPause()

                    pointer = false
                    downX = x
                    downY = y
                    seatOverview.actionDown()
                    invalidate()
                }
                MotionEvent.ACTION_MOVE -> if (!seatScale.isScaling && !seatClick.isOnClick) {
                    val downDX = abs(x - downX)
                    val downDY = abs(y - downY)
                    if ((downDX > 10 || downDY > 10) && !pointer) {
                        val dx = x - lastX
                        val dy = y - lastY
                        if (downY >= seatHeader.height) {
                            seatChart.postMatrixTranslate(dx, dy)
                            invalidate()
                        } else if (downY < seatHeader.height && downY > 0) { // seatHeader.labelHeight
                            if (seatHeader.touchAreaLabels(dx)) {
                                invalidate()
                            }
                        }
                    }
                }
                MotionEvent.ACTION_UP -> {
                    seatOverview.actionUp()
                    "ACTION_UP".e()
                    seatAnim.autoStartAnim()
                }
            }
            seatClick.isOnClick = false
            lastX = x
            lastY = y
        }

        return true
    }

    private fun reset() {
        seatChart.reset()
        seatOverview.reset()
        seatScale.reset()
        seatClick.reset()
        seatHeader.reset()
    }

    fun recycle() {
        seatIcon.recycle()
        seatOverview.recycle()
    }

    private fun initIndexes() {
        indexes.clear()
        seatManager?.getIndexes()?.forEach {
            indexes.add(it)
        }
    }
}

