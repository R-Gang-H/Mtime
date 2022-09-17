package com.kotlin.android.mtime.ktx.ext

import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.statelist.getColorStateList
import com.kotlin.android.mtime.ktx.R

/**
 * 创建者: zl
 * 创建时间: 2020/6/8 11:02 AM
 * 描述:
 * 该扩展类用于修改shape的圆角半径、背景色和描边色
 * 使用前提：必须先设置background drawable
 * 基础shape文件
 * bg_rect.xml
 * bg_rect_corner.xml
 * 以后无需在定义新的shape，使用如上文件即可
 *
 * @Deprecated
 * @see com.kotlin.android.ktx.ext.core.setBackground
 * @see com.kotlin.android.ktx.ext.core.setForeground
 * @see com.kotlin.android.ktx.ext.core.getShapeDrawable
 * @see com.kotlin.android.ktx.ext.core.getGradientDrawable
 * @see com.kotlin.android.ktx.ext.core.getCompoundDrawable
 * @see com.kotlin.android.ktx.ext.statelist.getColorStateList
 * @see com.kotlin.android.ktx.ext.statelist.getDrawableStateList
 * */
@Deprecated(message = "替代", replaceWith = ReplaceWith("com.kotlin.android.ktx.ext.core.setBackground"))
object ShapeExt {

    /**
     * 渐变方式
    enum class Orientation {
    /** draw the gradient from the top to the bottom  */
    TOP_BOTTOM,
    /** draw the gradient from the top-right to the bottom-left  */
    TR_BL,
    /** draw the gradient from the right to the left  */
    RIGHT_LEFT,
    /** draw the gradient from the bottom-right to the top-left  */
    BR_TL,
    /** draw the gradient from the bottom to the top  */
    BOTTOM_TOP,
    /** draw the gradient from the bottom-left to the top-right  */
    BL_TR,
    /** draw the gradient from the left to the right  */
    LEFT_RIGHT,
    /** draw the gradient from the top-left to the bottom-right  */
    TL_BR
    }
     */

    /**
     * 设置渐变色
     * @param view
     * @param orientation 渐变方式，参考上面定义
     * @param startColor 开始颜色
     * @param endColor 结束颜色
     * @param corner 圆角半径
     */
    fun setGradientColor(
            view: View?,
            orientation: GradientDrawable.Orientation = GradientDrawable.Orientation.TOP_BOTTOM,
            @ColorRes startColor: Int,
            @ColorRes endColor: Int,
            corner: Int = 15
    ): ShapeExt {
        view?.apply {
            val gradientDrawable =
                    GradientDrawable(
                            orientation,
                            intArrayOf(getColor(startColor), getColor(endColor))
                    )
            gradientDrawable.cornerRadius = corner.dpF
            background = gradientDrawable
        }
        return this
    }

    /**
     * 设置渐变色
     * @param view
     * @param orientation 渐变方式，参考上面定义
     * @param startColor 开始颜色
     * @param endColor 结束颜色
     * @param corner 圆角半径
     */
    fun setGradientColorWithColor(
            view: View?,
            orientation: GradientDrawable.Orientation = GradientDrawable.Orientation.TOP_BOTTOM,
            @ColorInt startColor: Int,
            @ColorInt endColor: Int,
            corner: Int = 15
    ): ShapeExt {
        view?.apply {
            val gradientDrawable =
                    GradientDrawable(
                            orientation,
                            intArrayOf(startColor, endColor)
                    )
            gradientDrawable.cornerRadius = corner.dpF
            background = gradientDrawable
        }
        return this
    }

    /**
     * 设置ImageView前景渐变色
     * @param view
     * @param orientation 渐变方式，参考上面定义
     * @param startColor 开始颜色
     * @param endColor 结束颜色
     * @param corner 圆角半径
     */
    fun setForegroundGradientColorWithColor(
            view: ImageView?,
            orientation: GradientDrawable.Orientation = GradientDrawable.Orientation.TOP_BOTTOM,
            @ColorInt startColor: Int,
            @ColorInt endColor: Int,
            corner: Int = 15
    ): ShapeExt {
        view?.apply {
            val gradientDrawable =
                    GradientDrawable(
                            orientation,
                            intArrayOf(startColor, endColor)
                    )
            gradientDrawable.cornerRadius = corner.dpF
            foreground = gradientDrawable
        }
        return this
    }

    /**
     * 设置shape背景色和圆角半径
     * @param view
     * @param solidColor
     * @param corner dp
     */
    fun setShapeColorAndCorner(view: View?, @ColorRes solidColor: Int, corner: Int = 3): ShapeExt {
        view?.apply {
            val gradientDrawable = GradientDrawable()
            gradientDrawable.setColor(getColor(solidColor))
            gradientDrawable.cornerRadius = corner.dpF
            view.background = gradientDrawable
        }
        return this
    }

    /**
     * 设置shape圆角半径和背景色
     * @param view
     * @param solidColor
     * @param corner dp
     */
    fun setShapeCorner2Color(
            view: View?,
            @ColorRes solidColor: Int,
            corner: Int = 3,
            isNormalPadding: Boolean = true): ShapeExt {
        view?.apply {
            setShapeColor(
                    this,
                    solidColor,
                    isNormalPadding
            )
            val gradientDrawable = background as GradientDrawable
            gradientDrawable.cornerRadius = corner.dpF
        }
        return this
    }

    /**
     * 设置shape圆角半径和背景色
     * @param view
     * @param solidColor
     * @param corner dp
     */
    fun setShapeCorner2ColorWithColor(
            view: View?,
            @ColorInt solidColor: Int,
            corner: Int = 3,
            isNormalPadding: Boolean = true): ShapeExt {
        view?.apply {
            setShapeColorWithColor(
                    this,
                    solidColor,
                    isNormalPadding
            )
            val gradientDrawable = background as GradientDrawable
            gradientDrawable.cornerRadius = corner.dpF
        }
        return this
    }


    /**
     * 设置shape圆角半径 背景色 和描边颜色和大小
     * @param view
     * @param solidColor
     * @param corner dp
     * @param solidColor
     */
    fun setShapeCorner2Color2Stroke(
            view: View,
            @ColorRes solidColor: Int = R.color.color_ffffff,
            corner: Int = 3,
            @ColorRes strokeColor: Int,
            strokeWidth: Int = 1,
            isNormalPadding: Boolean = true
    ): ShapeExt {
        view.apply {
            setShapeCorner2Color(
                    this,
                    solidColor,
                    corner,
                    isNormalPadding
            )
            val gradientDrawable = background as GradientDrawable
            gradientDrawable.setStroke(strokeWidth, getColor(strokeColor))
        }
        return this
    }


    /**
     * 设置 shape 的颜色
     * @param view
     * @param solidColor
     */
    fun setShapeColor(
            view: View,
            @ColorRes solidColor: Int,
            isNormalPadding: Boolean = true): ShapeExt {
        setShapeColorWithColor(view, view.getColor(solidColor), isNormalPadding)
        return this
    }

    /**
     * 设置 shape 的颜色
     * @param view
     * @param solidColor
     */
    fun setShapeColorWithColor(
            view: View?,
            @ColorInt solidColor: Int,
            isNormalPadding: Boolean = true): ShapeExt {
        view?.apply {
            if (isNormalPadding) {
                setNormalPadding(this)
            }
            val gradientDrawable = GradientDrawable()
            gradientDrawable.setColor(solidColor)
            gradientDrawable.cornerRadius = 0f
            background = gradientDrawable
        }
        return this
    }

    /**
     * 设置默认的内边距，一般不需要改动
     */
    private fun setNormalPadding(
            view: View?,
            left: Int = 2,
            top: Int = 1,
            right: Int = 2,
            bottom: Int = 1
    ) {
        view?.apply {
            setPadding(left.dp, top.dp, right.dp, bottom.dp)
        }
    }

    /**
     * 按钮设置style
     * 圆角、背景色、按下色
     */
    fun setViewSelector(
            view: View?,
            @ColorRes pressColor: Int = R.color.color_dbb177,
            @ColorRes normalColor: Int = R.color.color_d1a568,
            corner: Int = 30,
            strokeWidth: Int = 1
    ): ShapeExt {
        return setViewSelector(
                view,
                pressColor,
                pressColor,
                normalColor,
                normalColor,
                corner,
                strokeWidth
        )
    }

    /**
     * 针对有按下状态的view进行颜色处理
     * 设置shape圆角半径 背景色 和描边颜色和大小
     * @param view
     * @param pressColor view背景色
     * @param pressStrokeColor view描边颜色
     * @param normalColor view背景色
     * @param normalStrokeColor view描边颜色
     * @param corner 圆角半径
     * @param strokeWidth 描边大小
     */
    fun setViewSelector(
            view: View?,
            @ColorRes pressColor: Int,
            @ColorRes pressStrokeColor: Int,
            @ColorRes normalColor: Int,
            @ColorRes normalStrokeColor: Int,
            corner: Int,
            strokeWidth: Int = 1
    ): ShapeExt {
        view?.apply {
            val pDrawable =
                    ContextCompat.getDrawable(context, R.drawable.bg_rect_corner) as GradientDrawable
            pDrawable.setColor(getColor(pressColor))
            pDrawable.cornerRadius = corner.dpF
            pDrawable.setStroke(strokeWidth, getColor(pressStrokeColor))

            val nDrawable =
                    ContextCompat.getDrawable(context, R.drawable.bg_rect) as GradientDrawable
            nDrawable.setColor(getColor(normalColor))
            nDrawable.cornerRadius = corner.dpF
            nDrawable.setStroke(strokeWidth, getColor(normalStrokeColor))
            val drawable = StateListDrawable()
            drawable.addState(intArrayOf(android.R.attr.state_pressed), pDrawable)
            drawable.addState(IntArray(0), nDrawable)
            background = drawable
        }
        return this
    }

    /**
     * 设置不同圆角角度的drawable
     */
    fun setShapeCornerWithColor(view:View?, @ColorRes solideColor:Int, leftTopCorner:Int, rightTopCorner:Int, rightBottomCorner:Int, leftBottomCorner:Int):ShapeExt{
        view?.apply {
            val gradientDrawable = GradientDrawable()
            gradientDrawable.setColor(getColor(solideColor))
            gradientDrawable.cornerRadii = floatArrayOf(leftTopCorner.dpF,leftTopCorner.dpF,rightTopCorner.dpF,rightTopCorner.dpF,rightBottomCorner.dpF,rightBottomCorner.dpF,leftBottomCorner.dpF,leftBottomCorner.dpF)
            view.background = gradientDrawable
        }

        return this
    }

}
