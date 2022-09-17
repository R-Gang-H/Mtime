package com.kotlin.android.publish.component.widget.article.sytle

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import com.kotlin.android.publish.component.R

/**
 * 文本颜色
 *
 * Created on 2022/3/22.
 *
 * @author o.s
 */
enum class TextColor(val order: Int, val style: String, @ColorInt val color: Int, val rgb: String, @ColorRes val colorRes: Int) {
    BLACK(order = 0, style = "color: #303A47;", color = Color.parseColor("#303A47"), rgb = "color: rgb(48, 58, 71);", colorRes = R.color.color_303a47),
    GRAY(order = 1, style = "color: #8798AF;", color = Color.parseColor("#8798AF"), rgb = "color: rgb(135, 152, 175);", colorRes = R.color.color_8798af),
    ORANGE(order = 2, style = "color: #FF5A36;", color = Color.parseColor("#FF5A36"), rgb = "color: rgb(255, 90, 54);", colorRes = R.color.color_ff5a36),
    YELLOW(order = 3, style = "color: #FEB12A;", color = Color.parseColor("#FEB12A"), rgb = "color: rgb(254, 177, 42);", colorRes = R.color.color_feb12a),
    GREEN(order = 4, style = "color: #91D959;", color = Color.parseColor("#91D959"), rgb = "color: rgb(145, 217, 89);", colorRes = R.color.color_91d959),
    CYAN(order = 5, style = "color: #36C096;", color = Color.parseColor("#36C096"), rgb = "color: rgb(54, 192, 150);", colorRes = R.color.color_36c096),
    BLUE(order = 6, style = "color: #20A0DA;", color = Color.parseColor("#20A0DA"), rgb = "color: rgb(32, 160, 218);", colorRes = R.color.color_20a0da);

    companion object {
        fun obtain(@ColorInt color: Int): TextColor? {
            return when (color) {
                BLACK.color -> BLACK
                GRAY.color -> GRAY
                ORANGE.color -> ORANGE
                YELLOW.color -> YELLOW
                GREEN.color -> GREEN
                CYAN.color -> CYAN
                BLUE.color -> BLUE
                else -> null
            }
        }
    }
}