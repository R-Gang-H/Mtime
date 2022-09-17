package com.kotlin.android.ktx.ext.dimension

import android.content.res.Resources
import android.util.TypedValue

/**
 * Dimension 相关扩展
 *
 * Created on 2021/3/5.
 *
 * @author o.s
 */

/**
 * 屏幕宽高
 */
inline val screenWidth
    get() = Resources.getSystem().displayMetrics.widthPixels

inline val screenHeight
    get() = Resources.getSystem().displayMetrics.heightPixels

/**
 * 状态栏宽高
 */
inline val statusBarWidth
    get() = Resources.getSystem().getIdentifier("status_bar_width", "dimen", "android").let {
        Resources.getSystem().getDimensionPixelSize(it)
    }

inline val statusBarHeight
    get() = Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android").let {
        var height = Resources.getSystem().getDimensionPixelSize(it)
        if (height <= 0) {
            height = try {
                val clazz = Class.forName("com.android.internal.R\$dimen")
                val obj = clazz.newInstance()
                val h = clazz.getField("status_bar_height")[obj].toString().toInt()
                Resources.getSystem().getDimensionPixelSize(h)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                0
            }
        }
        height
    }

inline val navigationBarHeight
    get() = Resources.getSystem().getIdentifier("navigation_bar_height", "dimen", "android").let {
        if (it > 0) {
            Resources.getSystem().getDimensionPixelSize(it)
        } else {
            0
        }
    }

inline val density
    get() = Resources.getSystem().displayMetrics.density
/**
 * 将 dp, sp, pt, in, mm 转换成 px
 */
inline val Int.dp
    get() = dpF.toInt()

inline val Int.dpF
    get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            toFloat(),
            Resources.getSystem().displayMetrics
    )

inline val Float.dp
    get() = dpF.toInt()

inline val Float.dpF
    get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            Resources.getSystem().displayMetrics
    )

inline val Int.sp
    get() = spF.toInt()

inline val Int.spF
    get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            toFloat(),
            Resources.getSystem().displayMetrics
    )

inline val Float.sp
    get() = spF.toInt()

inline val Float.spF
    get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            toFloat(),
            Resources.getSystem().displayMetrics
    )

inline val Int.mm
    get() = mmF.toInt()

inline val Int.mmF
    get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_MM,
            toFloat(),
            Resources.getSystem().displayMetrics
    )

inline val Float.mm
    get() = mmF.toInt()

inline val Float.mmF
    get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_MM,
            this,
            Resources.getSystem().displayMetrics
    )

/**
 * 将 px 转换成 dp, sp, pt, in, mm
 */
inline val Int.toDP
    get() = toDPF.toInt()

inline val Int.toDPF
    get() = this / Resources.getSystem().displayMetrics.density

inline val Float.toDP
    get() = toDPF.toInt()

inline val Float.toDPF
    get() = this / Resources.getSystem().displayMetrics.density

inline val Int.toSP
    get() = toMMF.toInt()

inline val Int.toSPF
    get() = this / Resources.getSystem().displayMetrics.scaledDensity

inline val Float.toSP
    get() = toSPF.toInt()

inline val Float.toSPF
    get() = this / Resources.getSystem().displayMetrics.scaledDensity

inline val Int.toMM
    get() = toMMF.toInt()

inline val Int.toMMF
    get() = this / Resources.getSystem().displayMetrics.xdpi * (1.0f / 25.4f)

inline val Float.toMM
    get() = toMMF.toInt()

inline val Float.toMMF
    get() = this / Resources.getSystem().displayMetrics.xdpi * (1.0f / 25.4f)
