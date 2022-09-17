package com.kotlin.android.ktx.ext.core

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContextWrapper
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.MarginLayoutParamsCompat
import androidx.core.view.children
import com.kotlin.android.ktx.ext.dimension.statusBarHeight
import com.kotlin.android.ktx.ext.statelist.getColorStateList

/**
 * View相关扩展：
 *
 * 创建者: zl
 * 创建时间: 2020/6/8 10:35 AM
 */

inline var View.widthValue: Int
    get() = width
    set(value) {
        layoutParams = layoutParams.apply {
            width = value
        }
    }

inline var View.heightValue: Int
    get() = height
    set(value) {
        layoutParams = layoutParams.apply {
            height = value
        }
    }

inline var View.marginLeft: Int
    get() = (layoutParams as? ViewGroup.MarginLayoutParams)?.leftMargin ?: 0
    set(value) {
        layoutParams = (layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
            leftMargin = value
        }
    }

inline var View.marginTop: Int
    get() = (layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin ?: 0
    set(value) {
        layoutParams = (layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
            topMargin = value
        }
    }

inline var View.marginRight: Int
    get() = (layoutParams as? ViewGroup.MarginLayoutParams)?.rightMargin ?: 0
    set(value) {
        layoutParams = (layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
            rightMargin = value
        }
    }


inline var View.marginBottom: Int
    get() = (layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin ?: 0
    set(value) {
        layoutParams = (layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
            bottomMargin = value
        }
    }

fun View.setMargin(rect: Rect) {
    layoutParams = (layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
        setMargins(rect.left, rect.top, rect.right, rect.bottom)
    }
}

/**
 * @see ViewGroup.MarginLayoutParams
 * @see MarginLayoutParamsCompat.getMarginStart
 */
inline var View.marginStart: Int
    get() {
        val lp = layoutParams
        return if (lp is ViewGroup.MarginLayoutParams) MarginLayoutParamsCompat.getMarginStart(lp) else 0
    }
    set(value) {
        val lp = layoutParams
        layoutParams = (lp as? ViewGroup.MarginLayoutParams)?.apply {
            MarginLayoutParamsCompat.setMarginStart(lp, value)
        }
    }

/**
 * @see ViewGroup.MarginLayoutParams
 * @see MarginLayoutParamsCompat.getMarginEnd
 */
inline var View.marginEnd: Int
    get() {
        val lp = layoutParams
        return if (lp is ViewGroup.MarginLayoutParams) MarginLayoutParamsCompat.getMarginEnd(lp) else 0
    }
    set(value) {
        val lp = layoutParams
        layoutParams = (lp as? ViewGroup.MarginLayoutParams)?.apply {
            MarginLayoutParamsCompat.setMarginEnd(lp, value)
        }
    }

fun View.getDrawable(@DrawableRes id: Int?): Drawable? =
    id?.let { context?.let { context -> ContextCompat.getDrawable(context, it) } }

fun View.getCompoundDrawable(@DrawableRes id: Int?, width: Int? = null, height: Int? = null): Drawable? =
    context?.getCompoundDrawable(id, width, height)

fun View.getString(@StringRes resId: Int, vararg params: Any): String {
    return context.getString(resId, *params)
}

fun View.getColor(@ColorRes resId: Int): Int {
    return ContextCompat.getColor(context, resId)
}

fun View.visible(show: Boolean) {
    this.visibility = if (show) View.VISIBLE else View.GONE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.isShow() = this.visibility == View.VISIBLE

/**
 * View设置选择器，指定圆角 [cornerRadius] (px)，边框 [strokeWidth] (px)
 * 外部调用时指定对应的（dp, dpF）更为直观
 */
fun View.setSelector(
    @ColorRes pressColor: Int,
    @ColorRes normalColor: Int,
    @ColorRes pressStrokeColor: Int? = null,
    @ColorRes normalStrokeColor: Int? = null,
    cornerRadius: Float = 0F,
    strokeWidth: Int = 0
): View {
    val color = getColorStateList(normalColor = getColor(normalColor), pressColor = getColor(pressColor))
    var strokeColor: ColorStateList? = null
    if (normalStrokeColor != null && pressStrokeColor != null) {
        strokeColor =
            getColorStateList(normalColor = getColor(normalStrokeColor), pressColor = getColor(pressStrokeColor))
    } else if (normalStrokeColor != null) {
        strokeColor =
            getColorStateList(normalColor = getColor(normalStrokeColor), pressColor = getColor(pressColor))
    } else if (pressStrokeColor != null) {
        strokeColor =
            getColorStateList(normalColor = getColor(normalColor), pressColor = getColor(pressStrokeColor))
    }
    val gd = GradientDrawable()

    gd.color = color
    if (strokeColor != null) {
        gd.setStroke(strokeWidth, strokeColor)
    }
    if (cornerRadius > 0) {
        gd.cornerRadius = cornerRadius
    }
    background = gd

    return this
}

/**
 * 给 [View] 设置背景：纯色、可渐变、边框、虚线边框、选择器 [GradientDrawable]
 *
 * 1，设置颜色
 *  （1）单独指定 [colorRes]/[color] 则为纯色
 *  （2）指定 [colorRes]/[color]（开始色）和 [endColorRes]/[endColor]（结束色）颜色不同时，则为渐变色
 *  （3）单独指定 [colorStateList] 则为颜色选择器
 *
 * 2，设置边框：当 [strokeWidth] (px) 大于0时，在设置颜色的情况下都可以指定边框
 * （1）[strokeColorRes] 则为纯色边框
 * （2）[strokeColorStateList] 则为颜色选择器边框
 *
 * 3，设置虚线边框：在2中指定边框时，可以通过 [dashWidth] (px) 和 [dashGap] (px) 设定
 *
 * 4，设置圆角，[cornerRadius] (px) 圆角半径，并指定圆角方位 [direction] 默认全方位四角
 *
 * 5，指定渐变色时可以指定渐变方向 [orientation] 默认从上到下 [GradientDrawable.Orientation.TOP_BOTTOM]
 */
fun View.setBackground(
    @ColorRes colorRes: Int = android.R.color.transparent,
    @ColorRes endColorRes: Int? = null,
    @ColorRes strokeColorRes: Int = android.R.color.transparent,
    @ColorInt color: Int? = null,
    @ColorInt endColor: Int? = null,
    @ColorInt strokeColor: Int? = null,
    colorStateList: ColorStateList? = null,
    strokeColorStateList: ColorStateList? = null,
    strokeWidth: Int = 0,
    dashWidth: Float = 0F,
    dashGap: Float = 0F,
    cornerRadius: Float = 0F,
    direction: Int = Direction.ALL,
    orientation: GradientDrawable.Orientation = GradientDrawable.Orientation.TOP_BOTTOM,
): View {
    background = getShapeDrawable(
        colorRes = colorRes,
        endColorRes = endColorRes,
        strokeColorRes = strokeColorRes,
        color = color,
        endColor = endColor,
        strokeColor = strokeColor,
        colorStateList = colorStateList,
        strokeColorStateList = strokeColorStateList,
        strokeWidth = strokeWidth,
        dashWidth = dashWidth,
        dashGap = dashGap,
        cornerRadius = cornerRadius,
        direction = direction,
        orientation = orientation
    )
    return this
}

/**
 * 给 [View] 设置前景：纯色、可渐变、边框、虚线边框、选择器 [GradientDrawable]
 *
 * 1，设置颜色
 * （1）单独指定 [colorRes]/[color] 则为纯色
 * （2）指定 [colorRes]/[color]（开始色）和 [endColorRes]/[endColor]（结束色）颜色不同时，则为渐变色
 * （3）单独指定 [colorStateList] 则为颜色选择器
 *
 * 2，设置边框：当 [strokeWidth] (px) 大于0时，在设置颜色的情况下都可以指定边框
 * （1）[strokeColorRes] 则为纯色边框
 * （2）[strokeColorStateList] 则为颜色选择器边框
 *
 * 3，设置虚线边框：在2中指定边框时，可以通过 [dashWidth] (px) 和 [dashGap] (px) 设定
 *
 * 4，设置圆角，[cornerRadius] (px) 圆角半径，并指定圆角方位 [direction] 默认全方位四角
 *
 * 5，指定渐变色时可以指定渐变方向 [orientation] 默认从上到下 [GradientDrawable.Orientation.TOP_BOTTOM]
 */
fun View.setForeground(
    @ColorRes colorRes: Int = android.R.color.transparent,
    @ColorRes endColorRes: Int? = null,
    @ColorRes strokeColorRes: Int = android.R.color.transparent,
    @ColorInt color: Int? = null,
    @ColorInt endColor: Int? = null,
    @ColorInt strokeColor: Int? = null,
    colorStateList: ColorStateList? = null,
    strokeColorStateList: ColorStateList? = null,
    strokeWidth: Int = 0,
    dashWidth: Float = 0F,
    dashGap: Float = 0F,
    cornerRadius: Float = 0F,
    direction: Int = Direction.ALL,
    orientation: GradientDrawable.Orientation = GradientDrawable.Orientation.TOP_BOTTOM,
): View {
    foreground = getShapeDrawable(
        colorRes = colorRes,
        endColorRes = endColorRes,
        strokeColorRes = strokeColorRes,
        color = color,
        endColor = endColor,
        strokeColor = strokeColor,
        colorStateList = colorStateList,
        strokeColorStateList = strokeColorStateList,
        strokeWidth = strokeWidth,
        dashWidth = dashWidth,
        dashGap = dashGap,
        cornerRadius = cornerRadius,
        direction = direction,
        orientation = orientation
    )
    return this
}

/**
 * 给 [ImageView] 设置 [Drawable]：纯色、可渐变、边框、虚线边框、选择器 [GradientDrawable]
 *
 * 1，设置颜色
 * （1）单独指定 [colorRes]/[color] 则为纯色
 * （2）指定 [colorRes]/[color]（开始色）和 [endColorRes]/[endColor]（结束色）颜色不同时，则为渐变色
 * （3）单独指定 [colorStateList] 则为颜色选择器
 *
 * 2，设置边框：当 [strokeWidth] (px) 大于0时，在设置颜色的情况下都可以指定边框
 * （1）[strokeColorRes] 则为纯色边框
 * （2）[strokeColorStateList] 则为颜色选择器边框
 *
 * 3，设置虚线边框：在2中指定边框时，可以通过 [dashWidth] (px) 和 [dashGap] (px) 设定
 *
 * 4，设置圆角，[cornerRadius] (px) 圆角半径，并指定圆角方位 [direction] 默认全方位四角
 *
 * 5，指定渐变色时可以指定渐变方向 [orientation] 默认从上到下 [GradientDrawable.Orientation.TOP_BOTTOM]
 */
fun ImageView.setImageDrawable(
    @ColorRes colorRes: Int = android.R.color.transparent,
    @ColorRes endColorRes: Int? = null,
    @ColorRes strokeColorRes: Int = android.R.color.transparent,
    @ColorInt color: Int? = null,
    @ColorInt endColor: Int? = null,
    @ColorInt strokeColor: Int? = null,
    colorStateList: ColorStateList? = null,
    strokeColorStateList: ColorStateList? = null,
    strokeWidth: Int = 0,
    dashWidth: Float = 0F,
    dashGap: Float = 0F,
    cornerRadius: Float = 0F,
    direction: Int = Direction.ALL,
    orientation: GradientDrawable.Orientation = GradientDrawable.Orientation.TOP_BOTTOM,
): ImageView {
    setImageDrawable(
        getShapeDrawable(
            colorRes = colorRes,
            endColorRes = endColorRes,
            strokeColorRes = strokeColorRes,
            color = color,
            endColor = endColor,
            strokeColor = strokeColor,
            colorStateList = colorStateList,
            strokeColorStateList = strokeColorStateList,
            strokeWidth = strokeWidth,
            dashWidth = dashWidth,
            dashGap = dashGap,
            cornerRadius = cornerRadius,
            direction = direction,
            orientation = orientation
        )
    )
    return this
}

/**
 * 获取一个纯色、可渐变、边框、虚线边框、选择器 [GradientDrawable]
 *
 * 1，设置颜色
 * （1）单独指定 [colorRes]/[color] 则为纯色
 * （2）指定 [colorRes]/[color]（开始色）和 [endColorRes]/[endColor]（结束色）颜色不同时，则为渐变色
 * （3）单独指定 [colorStateList] 则为颜色选择器
 *
 * 2，设置边框：当 [strokeWidth] (px) 大于0时，在设置颜色的情况下都可以指定边框
 * （1）[strokeColorRes] 则为纯色边框
 * （2）[strokeColorStateList] 则为颜色选择器边框
 *
 * 3，设置虚线边框：在2中指定边框时，可以通过 [dashWidth] (px) 和 [dashGap] (px) 设定
 *
 * 4，设置圆角，[cornerRadius] (px) 圆角半径，并指定圆角方位 [direction] 默认全方位四角
 *
 * 5，指定渐变色时可以指定渐变方向 [orientation] 默认从上到下 [GradientDrawable.Orientation.TOP_BOTTOM]
 */
fun View.getShapeDrawable(
    @ColorRes colorRes: Int = android.R.color.transparent,
    @ColorRes endColorRes: Int? = null,
    @ColorRes strokeColorRes: Int = android.R.color.transparent,
    @ColorInt color: Int? = null,
    @ColorInt endColor: Int? = null,
    @ColorInt strokeColor: Int? = null,
    colorStateList: ColorStateList? = null,
    strokeColorStateList: ColorStateList? = null,
    strokeWidth: Int = 0,
    dashWidth: Float = 0F,
    dashGap: Float = 0F,
    cornerRadius: Float = 0F,
    direction: Int = Direction.ALL,
    orientation: GradientDrawable.Orientation = GradientDrawable.Orientation.TOP_BOTTOM,
): GradientDrawable {
    return getGradientDrawable(
        color = color ?: getColor(colorRes),
        endColor = endColor ?: endColorRes?.let { getColor(it) },
        strokeColor = strokeColor ?: getColor(strokeColorRes),
        colorStateList = colorStateList,
        strokeColorStateList = strokeColorStateList,
        strokeWidth = strokeWidth,
        dashWidth = dashWidth,
        dashGap = dashGap,
        cornerRadius = cornerRadius,
        direction = direction,
        orientation = orientation
    )
}

/**
 * view 转 Bitmap
 */
fun View.toBitmap(): Bitmap {
    val dstHeight = if (this is ScrollView) {
        contentHeight()
    } else {
        height
    }
    val bitmap = Bitmap.createBitmap(width, dstHeight, Bitmap.Config.RGB_565)
    val canvas = Canvas(bitmap)
    canvas.save()
    draw(canvas)
    canvas.restore()
    return bitmap
}

/**
 * 获取 [ScrollView] 内子View的绘制总高度，包括屏幕外不可见的部分
 */
fun ScrollView.contentHeight(): Int {
    var contentHeight = 0
    children.forEach {
        contentHeight += it.height
    }
    return contentHeight
}

/**
 * TextView设置文本颜色和文字，入参为color资源ID
 *
 */
fun TextView.setTextColorRes(@ColorRes resId: Int): TextView {
    setTextColor(getColor(resId))
    return this
}

/**
 * TextView设置文本颜色和文字，入参为color资源ID
 */
fun View.setTextStyle(content:String,@ColorRes resId: Int): View {
    if(this is TextView){
        setTextColor(getColor(resId))
        text = content
    }
    return this
}

/**
 * TextView设置设置四个方向的 [Drawable]
 * [padding] compoundDrawablePadding 单位px
 */
@SuppressLint("ResourceType")
fun TextView.setCompoundDrawablesAndPadding(
    @DrawableRes leftResId: Int = 0,
    @DrawableRes topResId: Int = 0,
    @DrawableRes rightResId: Int = 0,
    @DrawableRes bottomResId: Int = 0,
    padding: Int = 0
): TextView {
    setCompoundDrawablesWithIntrinsicBounds(
        leftResId, topResId, rightResId, bottomResId
    )
    compoundDrawablePadding = if (padding > 0) padding else 0
    return this
}


fun View?.isLocationOnView(x: Float, y: Float): Boolean {
    return this?.run {
        val location = IntArray(2)
        getLocationOnScreen(location)
//        "screen ${location[0]}, ${location[1]} (x, y) -> ($x, $y)".e()
        (x > location[0] && (x < location[0] + width)
                && y > location[1] && y < location[1] + height)
    } ?: false
}

/***
 * 滚动View
 * [increment] 滚动增量。为负值时，反向。
 */
fun ScrollView?.scroll(increment: Int) {
    this?.apply {
        if (childCount > 0) {
            val scrollY = scrollY
            val afterY = scrollY + increment
            val limitHeight = getChildAt(0).height - height
            if (increment > 0) {
                // 向下滚动
                val offsetY = if (afterY <= limitHeight) {
                    afterY
                } else {
                    limitHeight
                }
                smoothScrollTo(0, offsetY)
            } else if (increment < 0) {
                // 向上滚动
                val offsetY = if (afterY >= 0) {
                    afterY
                } else {
                    0
                }
                smoothScrollTo(0, offsetY)
            }
//            "ScrollView >>> scrollY = $scrollY :: increment = $increment :: limitHeight = $limitHeight".w()
        }
    }
}

/**
 * 判断View是否与目标rect部分重叠
 */
fun View?.isInRect(rect: Rect): Boolean {
    return this?.run {
        (top in rect.top .. rect.bottom || bottom in rect.top .. rect.bottom)
                && (left in rect.left .. rect.right || right in rect.left .. rect.right)
    } ?: false
}

/**
 * 从父视图删除View
 */
fun View?.removeFromParent() {
    (this?.parent as? ViewGroup)?.removeView(this)
}

/**
 * 获取文本视图一行文本的高度
 */
fun TextView.getTextHeight(): Int {
    val metricsInt = paint.fontMetricsInt
//    "Text :: textSize = $textSize :: metricsInt $metricsInt".i()
    return metricsInt.descent - metricsInt.ascent
}

/**
 * 获取文本视图指定行数的高度
 */
fun TextView.getTextHeightWithLines(lines: Int = 1): Int {
    return if (lines > 0) {
        paddingTop + lineHeight * lines + paddingBottom
    } else {
        paddingTop + paddingBottom
    }
}

/**
 * 启动一个Activity
 */
fun View.startActivity(clazz: Class<*>) {
    context.startActivity(clazz)
}

/**
 * 兼容 v7 包获取 Activity
 */
fun View?.getActivity(): Activity? {
    return this?.context?.run {
        var context = this
        var activity: Activity? = null
        if (context is ContextWrapper) {
            if (context is Activity) {
                activity = context
            } else {
                context = context.baseContext
                if (context is Activity) {
                    activity = context
                }
            }
        }
        activity
    }
}

/**
 * 设置状态栏高度的上边距
 */
fun View?.marginTopWithStatusBar(){
    this?.marginTop = statusBarHeight
}
