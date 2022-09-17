package com.kotlin.android.audio.floatview.component.aduiofloat

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.kotlin.android.audio.floatview.component.R
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.dimension.dpF


/**
 * create by lushan on 2022/3/25
 * des:圆形seekBar
 **/
class CircleSeekBar @JvmOverloads constructor(
    var ctx: Context,
    var attrsSet: AttributeSet? = null,
    var def: Int = 0
) : View(ctx, attrsSet, def) {
    private var mViewWidth: Int = 0
    private var mCurrentProgress: Int = 0
    private var mMaxProgress: Int = 100
    private var mSeekBarCircularWidth: Float = 3.dpF
    private var mRaduis: Int = 0
    private var mBgDefault: Int = getColor(R.color.color_66ffffff)
    private var mBgSeekBar: Int = getColor(R.color.color_ff5a36)
    private var mPaintDefault: Paint? = null
    private var mPaintSeekBar: Paint? = null
    private var rectFProgress: RectF? = null

    init {
        mPaintDefault = Paint().apply {
            strokeWidth = mSeekBarCircularWidth
            isAntiAlias = true
            color = mBgDefault
            style = Paint.Style.STROKE
        }


        mPaintSeekBar = Paint().apply {
            strokeWidth = mSeekBarCircularWidth
            isAntiAlias = true
            color = mBgSeekBar
            style = Paint.Style.STROKE
        }
        rectFProgress = RectF()

    }

    fun setProgress(progress: Int) {
        this.mCurrentProgress = progress
        invalidate()
    }

    fun getProgress(): Long = mCurrentProgress.toLong()

    fun getMaxProgress(): Long = mMaxProgress.toLong()
    fun setMaxProgress(max: Int) {
        this.mMaxProgress = if (max <= 0) 100 else max
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        var cx = (mViewWidth / 2).toFloat()
        mPaintDefault?.apply {
            canvas?.drawCircle(cx, cx, mRaduis.toFloat(), this)
        }
        var sweepAngle = mCurrentProgress * 360 / mMaxProgress
        var left = mSeekBarCircularWidth / 2
        var right = mViewWidth - left
        rectFProgress?.set(left, left, right, right);//减掉画笔的宽度
        rectFProgress?.apply {
            mPaintSeekBar?.let { canvas?.drawArc(this, -90f, sweepAngle.toFloat(), false, it) }
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mViewWidth = MeasureSpec.getSize(widthMeasureSpec)
        mRaduis = ((mViewWidth.toFloat() - mSeekBarCircularWidth) / 2f).toInt()
        setMeasuredDimension(mViewWidth, mViewWidth)
    }
}