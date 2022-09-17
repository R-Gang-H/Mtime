package com.kotlin.android.mtime.ktx.binding

import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.databinding.BindingAdapter
import com.kotlin.android.mtime.ktx.ext.ShapeExt

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/8
 */

@Deprecated(message = "请使用BindingAdapterExt", replaceWith = ReplaceWith("com.kotlin.android.ktx.ext.bind.BindingAdapterExt"))
@BindingAdapter(value = [
    "android:foreground_gradient_start_color",
    "android:foreground_gradient_end_color",
    "android:foreground_gradient_corner"
], requireAll = false)
fun bindGradientForeground(
        view: ImageView,
        @ColorInt startColor: Int,
        endColor: Int,
        corner: Int = 0
) {
    ShapeExt.setForegroundGradientColorWithColor(
            view = view,
            startColor = startColor,
            endColor = endColor,
            corner = corner
    )
}

@Deprecated(message = "请使用BindingAdapterExt", replaceWith = ReplaceWith("com.kotlin.android.ktx.ext.bind.BindingAdapterExt"))
@BindingAdapter(value = [
    "android:bg_gradient_start_color",
    "android:bg_gradient_end_color",
    "android:bg_gradient_corner"
], requireAll = false)
fun bindGradientBg(
        view: View,
        @ColorInt startColor: Int,
        endColor: Int,
        corner: Int = 0
) {
    ShapeExt.setGradientColorWithColor(
            view = view,
            startColor = startColor,
            endColor = endColor,
            corner = corner
    )
}

@Deprecated(message = "请使用BindingAdapterExt", replaceWith = ReplaceWith("com.kotlin.android.ktx.ext.bind.BindingAdapterExt"))
@BindingAdapter(
        value = ["android:bg_shape_color_res", "android:bg_shape_corner"],
        requireAll = false)
fun binShapeCorner2Color(
        view: View,
        @ColorRes solidColorRes: Int = 0,
        corner: Int = 0) {
    ShapeExt.setShapeCorner2Color(view, solidColorRes, corner, false)
}

@Deprecated(message = "请使用BindingAdapterExt", replaceWith = ReplaceWith("com.kotlin.android.ktx.ext.bind.BindingAdapterExt"))
@BindingAdapter(
        value = ["android:bg_shape_color", "android:bg_shape_corner"],
        requireAll = false)
fun binShapeCorner2ColorWithColor(
        view: View,
        @ColorInt solidColor: Int = 0,
        corner: Int = 0) {
    ShapeExt.setShapeCorner2ColorWithColor(view, solidColor, corner, false)
}
