package com.kotlin.android.widget.titlebar.item

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.getDrawable
import com.kotlin.android.ktx.ext.core.getString
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.log.i
import com.kotlin.android.widget.BuildConfig
import com.kotlin.android.widget.R
import com.kotlin.android.widget.titlebar.IButtonItem
import com.kotlin.android.widget.titlebar.State
import com.kotlin.android.widget.titlebar.TitleBar

/**
 * 标题栏 TextView Item 实现
 *
 * Created on 2021/12/13.
 *
 * @author o.s
 */
class ButtonItem : AppCompatTextView, IButtonItem {

    private var state: State = State.NORMAL

    @DrawableRes
    private var resId: Int = 0

    @DrawableRes
    private var reverseResId: Int = 0

    @ColorRes
    private var colorResId: Int = 0

    @ColorRes
    private var colorReverseResId: Int = 0

    private var colorState: ColorStateList? = null
    private var reverseColorState: ColorStateList? = null

    private var startDrawable: Drawable? = null
    private var reverseStartDrawable: Drawable? = null
    private var endDrawable: Drawable? = null
    private var reverseEndDrawable: Drawable? = null

    private var bgDrawable: Drawable? = null
    private var reverseBgDrawable: Drawable? = null

    private var borderColor = Color.parseColor("#FEFFFA")
    private var bgColor = Color.parseColor("#FF5A36")
    private var titleColor = Color.parseColor("#FFFFFF")

    private val mItemWidth = 36.dpF
    private val pointX = 28.dpF
    private val pointY = 8.dpF
    private val mBorderWidth = 1.dpF
    private val mPointRadius = 4.dpF
    private val mPointBorderRadius = mPointRadius + mBorderWidth
    private val mRadius = 6.dpF
    private val mBorderRadius = mRadius + mBorderWidth
    private val mBorderTop = 1.dpF
    private val mBorderBottom = mBorderTop + 2 * mBorderRadius // 15.dpF
    private val mTitlePadding = 4.dpF
    private val mMaxNoteWidth = mItemWidth - mTitlePadding * 2

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

    override val view: View
        get() = this

    /**
     * 是否显示红点
     */
    var showRedPoint = false
        set(value) {
            field = value
            invalidate()
        }
    var noteTitle: CharSequence? = null

    override fun showRedPoint(isShow: Boolean, title: CharSequence?) {
        noteTitle = title
        showRedPoint = isShow
    }

    override fun setState(state: State) {
        if (this.state == state) {
            return
        }
        this.state = state
        refreshImage()
        refreshTextColor()
        refreshBgDrawable()
        refreshCompoundDrawables()
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        fitsSystemWindows = (parent as ViewGroup).fitsSystemWindows
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (showRedPoint) {
            if (noteTitle.isNullOrEmpty().not()) {
                val noteText = noteTitle.toString()
                if (noteText.length == 1) {
                    canvas?.drawCircle(pointX, pointY, mBorderRadius, paintBorder)
                    canvas?.drawCircle(pointX, pointY, mRadius, paintBg)

                    val textWidth = paintTitle.measureText(noteText)
                    val textLeft = pointX - textWidth / 2

                    noteTitle?.apply {
                        canvas?.drawText(this, 0, length, textLeft, paintTitle.getBaseline(0F, mBorderBottom), paintTitle)
                    }
                } else {
                    var textWidth = paintTitle.measureText(noteText)
                    if (textWidth > mMaxNoteWidth) {
                        textWidth = mMaxNoteWidth
                    }
                    val textLeft = mItemWidth - textWidth - mTitlePadding
                    borderRecF.left = textLeft - mTitlePadding
                    borderRecF.top = mBorderTop
                    borderRecF.right = mItemWidth
                    borderRecF.bottom = mBorderBottom
                    canvas?.drawRoundRect(borderRecF, mBorderRadius, mBorderRadius, paintBorder)

                    redRectF.left = borderRecF.left + mBorderWidth
                    redRectF.top = borderRecF.top + mBorderWidth
                    redRectF.right = borderRecF.right - mBorderWidth
                    redRectF.bottom = borderRecF.bottom - mBorderWidth
                    canvas?.drawRoundRect(redRectF, mRadius, mRadius, paintBg)
                    noteTitle?.apply {
                        canvas?.drawText(this, 0, length, textLeft, paintTitle.getBaseline(0F, mBorderBottom), paintTitle)
                    }
                }
            } else {
                canvas?.drawCircle(pointX, pointY, mPointBorderRadius, paintBorder)
                canvas?.drawCircle(pointX, pointY, mPointRadius, paintBg)
            }
        }
    }

    fun update(
        @DrawableRes drawableRes: Int? = null,
        @DrawableRes reverseDrawableRes: Int? = null,
        title: CharSequence? = null,
        @StringRes titleRes: Int? = null,
        @ColorRes colorRes: Int = R.color.color_30333b,
        @ColorRes reverseColorRes: Int = R.color.color_ffffff,
        colorState: ColorStateList? = null,
        reverseColorState: ColorStateList? = null,
        textSize: Float = 15F, // sp
        isBold: Boolean = false,
        isSingleLine: Boolean = true,
        isEllipsizeEnd: Boolean = false,
        gravity: Int = Gravity.CENTER,

        maxWidth: Int = 0,
        titleWidth: Int = 0,
        titleHeight: Int = TitleBar.iconWidth,
        titleMarginStart: Int = 0,
        titleMarginEnd: Int = 0,
        titlePaddingStart: Int = 0,
        titlePaddingEnd: Int = 0,
        titlePaddingBottom: Int = 0,
        titlePaddingTop: Int = 0,
        drawablePadding: Int = 0,

        startDrawable: Drawable? = null,
        reverseStartDrawable: Drawable? = null,
        endDrawable: Drawable? = null,
        reverseEndDrawable: Drawable? = null,
        bgDrawable: Drawable? = null,
        reverseBgDrawable: Drawable? = null,

        click: ((v: View) -> Unit)? = null
    ): ButtonItem {
        var iHeight = titleHeight
        val iWith = when {
            drawableRes != null -> {
                iHeight = TitleBar.iconWidth
                TitleBar.iconWidth
            }
            titleWidth > 0 -> {
                titleWidth
            }
            titleWidth == -1 -> {
                LinearLayout.LayoutParams.MATCH_PARENT
            }
            else -> {
                LinearLayout.LayoutParams.WRAP_CONTENT
            }
        }
        layoutParams = LinearLayout.LayoutParams(iWith, iHeight).apply {
            marginStart = titleMarginStart
            marginEnd = titleMarginEnd
        }
        if (drawableRes != null || reverseDrawableRes != null) {
            setImageResource(drawableRes ?: reverseDrawableRes!!, reverseDrawableRes ?: drawableRes!!)
        } else {
            if (maxWidth > 0) {
                this.maxWidth = maxWidth
            }
            setPadding(titlePaddingStart, titlePaddingTop, titlePaddingEnd, titlePaddingBottom)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
            setTextColor(colorRes, reverseColorRes)
            setTextColorState(colorState, reverseColorState)
            setBgDrawable(bgDrawable, reverseBgDrawable)
            setStartDrawable(startDrawable ?: reverseStartDrawable, reverseStartDrawable ?: startDrawable)
            setEndDrawable(endDrawable ?: reverseEndDrawable, reverseEndDrawable ?: endDrawable)
            if (isSingleLine) {
                setSingleLine()
            }
            if (isEllipsizeEnd) {
                ellipsize = TextUtils.TruncateAt.END
            }
            if (drawablePadding > 0) {
                compoundDrawablePadding = drawablePadding
            }
            typeface = if (isBold) {
                Typeface.defaultFromStyle(Typeface.BOLD)
            } else {
                Typeface.defaultFromStyle(Typeface.NORMAL)
            }
            this.text = title ?: if (titleRes != null) getString(titleRes) else ""
        }
        this.gravity = gravity
        onClick {
            click?.invoke(it)
        }

        return this
    }

    /**
     * 只更新文案
     */
    fun updateTitle(
        title: CharSequence? = null,
        @StringRes titleRes: Int? = null,
    ): ButtonItem {
        this.text = title ?: if (titleRes != null) getString(titleRes) else ""
        return this
    }

    fun setTextColor(@ColorRes resId: Int, @ColorRes reverseResId: Int) {
        this.colorResId = resId
        this.colorReverseResId = reverseResId
        refreshTextColor()
    }

    private fun setTextColorState(colorState: ColorStateList?, reverseColorState: ColorStateList?) {
        this.colorState = colorState
        this.reverseColorState = reverseColorState
//        if (colorState != null || reverseColorState != null) {
//        }
        refreshTextColor()
    }

    private fun setBgDrawable(bgDrawable: Drawable?, reverseBgDrawable: Drawable?) {
        this.bgDrawable = bgDrawable
        this.reverseBgDrawable = reverseBgDrawable
//        if (bgDrawable != null || reverseBgDrawable != null) {
//        }
        refreshBgDrawable()
    }

    private fun setStartDrawable(startDrawable: Drawable?, reverseStartDrawable: Drawable?) {
        this.startDrawable = startDrawable
        this.reverseStartDrawable = reverseStartDrawable
//        if (startDrawable != null || reverseStartDrawable != null) {
//        }
        refreshCompoundDrawables()
    }

    private fun setEndDrawable(endDrawable: Drawable?, reverseEndDrawable: Drawable?) {
        this.endDrawable = endDrawable
        this.reverseEndDrawable = reverseEndDrawable
//        if (endDrawable != null || reverseEndDrawable != null) {
//        }
        refreshCompoundDrawables()
    }

    fun setImageResource(@DrawableRes resId: Int, @DrawableRes reverseResId: Int) {
        this.resId = resId
        this.reverseResId = reverseResId
        refreshImage()
    }

    @SuppressLint("ResourceType")
    private fun refreshImage() {
        when (state) {
            State.REVERSE -> {
                if (resId > 0) {
                    background = if (reverseResId > 0) {
                        getDrawable(reverseResId)
                    } else {
                        getDrawable(resId)
                    }
                }
            }
            State.NORMAL -> {
                if (resId > 0) {
                    background = getDrawable(resId)
                }
            }
        }
    }

    @SuppressLint("ResourceType")
    private fun refreshTextColor() {
        when (state) {
            State.REVERSE -> {
                if (reverseColorState != null) {
                    setTextColor(reverseColorState)
                    return
                }
                if (colorResId > 0) {
                    if (colorReverseResId > 0) {
                        setTextColor(ContextCompat.getColor(context, colorReverseResId))
                    } else {
                        setTextColor(ContextCompat.getColor(context, colorResId))
                    }
                }
            }
            State.NORMAL -> {
                if (colorState != null) {
                    setTextColor(colorState)
                    return
                }
                if (colorResId > 0) {
                    setTextColor(ContextCompat.getColor(context, colorResId))
                }
            }
        }
    }

    /**
     * 刷新背景。一般用于文字按钮的背景（边框等）。
     */
    private fun refreshBgDrawable() {
        when (state) {
            State.REVERSE -> {
                if (reverseBgDrawable != null) {
                    setBackgroundDrawable(reverseBgDrawable)
                }
            }
            State.NORMAL -> {
                if (bgDrawable != null) {
                    setBackgroundDrawable(bgDrawable)
                }
            }
        }
    }

    private fun refreshCompoundDrawables() {
        when (state) {
            State.REVERSE -> {
                setCompoundDrawables(reverseStartDrawable, null, reverseEndDrawable, null)
            }
            State.NORMAL -> {
                setCompoundDrawables(startDrawable, null, endDrawable, null)
            }
        }
    }
}

/**
 * 绘制文字基线
 */
fun Paint.getBaseline(top: Float, bottom: Float): Float {
    val fontMetrics = fontMetrics
    val baseline = (bottom + top - fontMetrics.bottom - fontMetrics.top) / 2F
    if (BuildConfig.DEBUG) {
        String.format("baseline = %f, t = %f, b = %f, ft = %f, fb = %f", baseline, top, bottom, fontMetrics.top, fontMetrics.bottom).i()
    }
    return baseline
}