package com.kotlin.android.card.monopoly.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.kotlin.android.card.monopoly.R

/**
 * @desc 我的卡友列表空位展示view
 * @author zhangjian
 * @date 2020/9/7 15:41
 */

class CardSeatProgressView : View {

    //边框高度
    private var mBorderHeight = 0

    //边框宽度
    private var mBorderWidth = 0

    //边框线的宽度
    private var mBorderLineWidth = 0f

    //边框颜色
    private var mBorderColor = 0

    //边框的弧度
    private var mBorderRadius = 0f

    //边框的画笔
    private var mBorderPaint: Paint? = null

    //距离外边框的缝隙值
    private var mPadding = 0f

    //占位的画笔
    private var mFillPaint: Paint? = null

    //空位的画笔
    private var mEmptyPaint: Paint? = null

    //占位数量
    private var mFillCount: Int = 0

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context, attrs, defStyleAttr)
    }

    private fun initView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val typeArr = context.obtainStyledAttributes(attrs, R.styleable.CardSeatProgressView)
        mBorderColor = typeArr.getColor(R.styleable.CardSeatProgressView_cpv_border_color, Color.parseColor("#20A0DA"))
        mBorderLineWidth = typeArr.getDimension(R.styleable.CardSeatProgressView_cpv_border_width, 2f)
        mBorderRadius = typeArr.getDimension(R.styleable.CardSeatProgressView_cpv_border_radius, 10f)
        mPadding = typeArr.getDimension(R.styleable.CardSeatProgressView_cpv_border_padding, 3f)
        typeArr.recycle()

        //边框
        mBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBorderPaint?.color = mBorderColor
        mBorderPaint?.strokeWidth = mBorderLineWidth
        mBorderPaint?.style = Paint.Style.STROKE
        //占位
        mFillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mFillPaint?.color = mBorderColor
        mFillPaint?.style = Paint.Style.FILL
        //空位
        mEmptyPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mEmptyPaint?.color = Color.parseColor("#E3E5ED")
        mEmptyPaint?.style = Paint.Style.FILL


    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        //测量边框宽度
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        when (widthMode) {
            MeasureSpec.UNSPECIFIED, MeasureSpec.EXACTLY -> {
                mBorderWidth = widthSize
            }
            MeasureSpec.AT_MOST -> {
                mBorderWidth = 112
            }
        }

        //测量边框高度
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        when (heightMode) {
            MeasureSpec.UNSPECIFIED, MeasureSpec.EXACTLY -> {
                mBorderHeight = heightSize
            }
            MeasureSpec.AT_MOST -> {
                mBorderHeight = 30
            }
        }
        setMeasuredDimension(mBorderWidth, mBorderHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //画边框
        val mRadius = 1.0f
        val mBorderRect = RectF(mRadius, mRadius,
                mBorderWidth.toFloat() - mRadius,
                mBorderHeight.toFloat() - mRadius)
        mBorderPaint?.let { canvas?.drawRoundRect(mBorderRect, mBorderRadius, mBorderRadius, it) }
        //位置的宽和高
        val seatWidth = (mBorderWidth - mBorderLineWidth * 7 - mPadding * 6) / 5
        val seatHeight = mBorderHeight - mBorderLineWidth * 2 - mPadding * 2
        //画位置(固定是5个)
        for (i in 0 until 5) {
            val spaceAndSeatWidth = (mPadding + mBorderLineWidth) * (i + 1) + seatWidth * i
            val mRct = RectF(spaceAndSeatWidth + mPadding,
                    mRadius + mPadding + mBorderLineWidth,
                    spaceAndSeatWidth + mPadding + seatWidth,
                    seatHeight + mPadding + mBorderLineWidth - mRadius
            )
            if ((i + 1) <= mFillCount) {
                mFillPaint?.let {
                    canvas?.drawRoundRect(mRct, mBorderRadius, mBorderRadius, it)
                }
            } else {
                mEmptyPaint?.let {
                    canvas?.drawRoundRect(mRct, mBorderRadius, mBorderRadius, it)
                }
            }
        }

    }

    //设置进度
    fun setFillCount(fill: Int, allCount: Int) {
        val fillCount = allCount - fill
        mFillCount = if (allCount > 5) {
            val temp: Double = (fillCount * 1.0 / allCount) * 5
            temp.toInt()
        } else {
            fillCount
        }
        invalidate()
    }


}