package com.kotlin.android.message.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.databinding.BindingAdapter
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.message.R
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.widget.titlebar.item.getBaseline

/**
 * NotifyView默认只展示一个小红点
 *
 * 如果使用了此属性 notify传 null       会隐藏红点
 *               notify传 0          会展示红点
 *               notify传 小于99的数   会展示原数
 *               notify传 大于99的数   会展示99+
 */
@BindingAdapter("notify")
fun setNoticeText(notifyView: NotifyView, text: Int?) {
    var formatText: String? =
        when {
            text == null -> null
            text == 0 -> ""
            text > 99 -> getString(R.string.message_more_than_99)
            else -> text.toString()
        }

    notifyView.noticeText = formatText
}

/**
 * Created by zhaoninglongfei on 2022/4/1
 * 红点提醒
 */
class NotifyView : View {

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    //提醒文案
    var noticeText: CharSequence? = ""
        set(value) {
            if (value == null) {
                this.visibility = GONE
            } else {
                this.visibility = VISIBLE
            }
            field = value
            invalidate()
        }

    private var borderColor = Color.parseColor("#FEFFFA")
    private var bgColor = Color.parseColor("#FF5A36")
    private var titleColor = Color.parseColor("#FFFFFF")

    private val pointX = 8.dpF
    private val pointY = 8.dpF
    private val mBorderWidth = 1.dpF
    private val mPointRadius = 4.dpF
    private val mPointBorderRadius = mPointRadius + mBorderWidth
    private val mRadius = 6.dpF
    private val mBorderRadius = mRadius + mBorderWidth
    private val mBorderTop = 1.dpF
    private val mBorderBottom = mBorderTop + 2 * mBorderRadius // 15.dpF
    private val mTitlePadding = 4.dpF

    private val paintBorder by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = borderColor
            typeface = Typeface.DEFAULT_BOLD
        }
    }
    private val paintBg by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = bgColor
            textSize = 9.dpF
            typeface = Typeface.DEFAULT_BOLD
        }
    }
    private val paintTitle by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = titleColor
            textSize = 9.dpF
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        }
    }

    private val borderRecF by lazy {
        RectF(0f, 0f, 0f, 0f)
    }

    private val redRectF by lazy {
        RectF(0f, 0f, 0f, 0f)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (noticeText.isNullOrEmpty().not()) {
            val noteText = noticeText.toString()
            if (noteText.length == 1) {
                canvas?.drawCircle(pointX + mTitlePadding, pointY, mBorderRadius, paintBorder)
                canvas?.drawCircle(pointX + mTitlePadding, pointY, mRadius, paintBg)

                val textWidth = paintTitle.measureText(noteText)
                val textLeft = pointX - textWidth / 2 + mTitlePadding

                noticeText?.apply {
                    canvas?.drawText(
                        this,
                        0,
                        length,
                        textLeft,
                        paintTitle.getBaseline(0F, mBorderBottom),
                        paintTitle
                    )
                }
            } else {
                var textWidth = paintTitle.measureText(noteText)
                val textLeft = pointX
                borderRecF.left = textLeft - mTitlePadding
                borderRecF.top = mBorderTop
                borderRecF.right = textLeft + textWidth + mTitlePadding
                borderRecF.bottom = mBorderBottom
                canvas?.drawRoundRect(borderRecF, mBorderRadius, mBorderRadius, paintBorder)

                redRectF.left = borderRecF.left + mBorderWidth
                redRectF.top = borderRecF.top + mBorderWidth
                redRectF.right = borderRecF.right - mBorderWidth
                redRectF.bottom = borderRecF.bottom - mBorderWidth
                canvas?.drawRoundRect(redRectF, mRadius, mRadius, paintBg)
                noticeText?.apply {
                    canvas?.drawText(
                        this,
                        0,
                        length,
                        textLeft,
                        paintTitle.getBaseline(0F, mBorderBottom),
                        paintTitle
                    )
                }
            }
        } else {
            canvas?.drawCircle(pointX + mTitlePadding, pointY, mPointBorderRadius, paintBorder)
            canvas?.drawCircle(pointX + mTitlePadding, pointY, mPointRadius, paintBg)
        }
    }
}