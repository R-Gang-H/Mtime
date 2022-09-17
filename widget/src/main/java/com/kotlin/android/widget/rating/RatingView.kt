package com.kotlin.android.widget.rating

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.IntRange
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.widget.R
import kotlin.math.abs

/**
 * 评级视图：
 * [itemCount] 共是10个等级，可扩展
 *
 * Created on 2020/7/21.
 *
 * @author o.s
 */
class RatingView : View {

    private val itemCount = 10
    private var mWidth = 315.dp
    private var mHeight = 38.dp
    private var itemWith = 27.dp
    private var itemHeight = 38.dp
    private val edge = 5.dp
    private var offsetX = itemWith + edge
    private val cornerRadius = 4.dpF
    private val safeClickEdge = 5.dp

    private val levels = ArrayList<RatingLevel>()

    var ratingChange: ((hasRating: Boolean) -> Unit)? = null

    var action: ((level: Double) -> Unit)? = null

    var level: Double = -1.0
        set(value) {
            if (field != value) {
                field = value
                invalidate()
                ratingChange?.invoke(value > 0)
                action?.invoke(value)
            }
        }

    var supportTouchEvent = true

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun initView() {
        levels.add(RatingLevel(context, 1, R.color.color_ffe219, R.color.color_ffde1a, itemWith, itemHeight, edge, cornerRadius))
        levels.add(RatingLevel(context, 2, R.color.color_ffdd1b, R.color.color_ffda1d, itemWith, itemHeight, edge, cornerRadius))
        levels.add(RatingLevel(context, 3, R.color.color_ffd91d, R.color.color_ffd51e, itemWith, itemHeight, edge, cornerRadius))
        levels.add(RatingLevel(context, 4, R.color.color_ffd31e, R.color.color_ffd020, itemWith, itemHeight, edge, cornerRadius))
        levels.add(RatingLevel(context, 5, R.color.color_ffcf20, R.color.color_ffca21, itemWith, itemHeight, edge, cornerRadius))
        levels.add(RatingLevel(context, 6, R.color.color_ffca22, R.color.color_fec523, itemWith, itemHeight, edge, cornerRadius))
        levels.add(RatingLevel(context, 7, R.color.color_fec423, R.color.color_fec024, itemWith, itemHeight, edge, cornerRadius))
        levels.add(RatingLevel(context, 8, R.color.color_fec025, R.color.color_febb27, itemWith, itemHeight, edge, cornerRadius))
        levels.add(RatingLevel(context, 9, R.color.color_feba26, R.color.color_feb628, itemWith, itemHeight, edge, cornerRadius))
        levels.add(RatingLevel(context, 10, R.color.color_feb629, R.color.color_feb12a, itemWith, itemHeight, edge, cornerRadius))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = measuredWidth
        mHeight = measuredHeight
        itemHeight = mHeight
        itemWith = (mWidth - (edge * (itemCount - 1))) / itemCount
        offsetX = itemWith + edge
        levels.forEach {
            it.width = itemWith
            it.height = itemHeight
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.run {
            save()
            levels.forEach {
                it.apply {
                    if (level.toInt() >= ratingLevel) {
                        highlightDrawable.draw(canvas)
                    } else {
                        normalDrawable.draw(canvas)
                    }
                }
            }
            restore()
        }
    }

    private var downY = 0F
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        if (supportTouchEvent) {
            event?.action?.let {
                when (it) {
                    MotionEvent.ACTION_DOWN -> {
                        downY = event.y
                        parent.requestDisallowInterceptTouchEvent(true)
                    }
                    MotionEvent.ACTION_UP -> {
                        parent.requestDisallowInterceptTouchEvent(false)
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        parent.requestDisallowInterceptTouchEvent(false)
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (downY == 0F) {
                            downY = event.y
                        }
                        val offsetY = event.y - downY
                        if (abs(offsetY) > 100) {
                            parent.requestDisallowInterceptTouchEvent(false)
                        }
                        level = getTouchLevel(event.x)
                    }
                }
            }
        }
        return supportTouchEvent
    }

    private fun getTouchLevel(x: Float): Double {
        val butterX = x - safeClickEdge
        if (butterX < 0) {
            return 1.0
        }
        var touchLevel = (butterX / offsetX).toInt() + 1
        if (touchLevel > itemCount) {
            touchLevel = itemCount
        }
        return touchLevel.toDouble()
    }

    /**
     * 评级等级
     */
    class RatingLevel(
            val context: Context,
            @IntRange(from = 1, to = 10) val ratingLevel: Int,
            @ColorRes val startColor: Int = R.color.color_f2f3f6,
            @ColorRes val endColor: Int = R.color.color_f2f3f6,
            var width: Int = 27.dp,
            var height: Int = 38.dp,
            private val edge: Int = 5.dp,
            private val cornerRadius: Float = 4.dpF
    ) {

        /**
         * 高亮评级
         */
        val highlightDrawable by lazy {
            context.getShapeDrawable(
                    colorRes = startColor,
                    endColorRes = endColor,
                    cornerRadius = cornerRadius,
                    orientation = GradientDrawable.Orientation.LEFT_RIGHT
            ).apply {
                setBounds(rect.left, rect.top, rect.right, rect.bottom)
            }
        }

        /**
         * 默认评级
         */
        val normalDrawable by lazy {
            context.getShapeDrawable(
                    colorRes = R.color.color_e5e5e5,
                    endColorRes = R.color.color_e5e5e5,
                    cornerRadius = cornerRadius,
                    orientation = GradientDrawable.Orientation.LEFT_RIGHT
            ).apply {
                setBounds(rect.left, rect.top, rect.right, rect.bottom)
            }
        }

        /**
         * 布局位置
         */
        val rect by lazy {
            val left = (width + edge) * (ratingLevel - 1)
            val right = left + width
            Rect(left, 0, right, height)
        }

    }

}