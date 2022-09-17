package com.kotlin.android.film.widget.seat

import android.graphics.Paint
import com.kotlin.android.film.BuildConfig
import com.kotlin.android.ktx.ext.log.i

/**
 *
 * Created on 2022/2/9.
 *
 * @author o.s
 */

enum class Orientation {
    HORIZONTAL, VERTICAL
}

/**
 * 座位样式
 */
enum class SeatStyle(value: String) {

    NORMAL("normal"),
    AREA("area");

    companion object {
        fun create(index: Int): SeatStyle {
            return if (index == 1) AREA else NORMAL
        }
    }
}

/**
 * 分区等级
 */
enum class AreaLevel(val value: Int) {
    AREA_LEVEL_1(1),
    AREA_LEVEL_2(2),
    AREA_LEVEL_3(3),
    AREA_LEVEL_4(4),
    AREA_LEVEL_5(5)
}

/**
 * 座位类型
 */
enum class SeatType {
    // 一般座位图
    SEAT_BLANK, // 座位不可用，空白区域
    SEAT_COVERED, // 有遮盖效果的
    SEAT_OPTIONAL, // 座位可选
    SEAT_SELECTED, // 座位已选
    SEAT_DISABLED, // 座位已售
    SEAT_COUPLE_OPTIONAL_L, // 情侣座席左可选
    SEAT_COUPLE_OPTIONAL_R, // 情侣座席右可选
    SEAT_COUPLE_SELECTED_L, // 情侣座席左已选
    SEAT_COUPLE_SELECTED_R, // 情侣座席右已选
    SEAT_COUPLE_DISABLED_L, // 情侣座席左已售
    SEAT_COUPLE_DISABLED_R, // 情侣座席右已售
    SEAT_DISABILITY_OPTIONAL, // 残疾人座席可选
    SEAT_DISABILITY_SELECTED, // 残疾人座席已选
    SEAT_REPAIR, // 待修理座席

    // 分区座位图
    AREA_OPTIONAL_1, // y 区域可选座席
    AREA_OPTIONAL_2, // o 区域可选座席
    AREA_OPTIONAL_3, // p 区域可选座席
    AREA_OPTIONAL_4, // b 区域可选座席
    AREA_OPTIONAL_5, // g 区域可选座席

    AREA_SELECTED_1, // y 区域已选座席
    AREA_SELECTED_2, // o 区域已选座席
    AREA_SELECTED_3, // p 区域已选座席
    AREA_SELECTED_4, // b 区域已选座席
    AREA_SELECTED_5, // g 区域已选座席

    // 情侣座席
    AREA_COP_OPTIONAL_1_L, // y 区域可选座席
    AREA_COP_OPTIONAL_2_L, // o 区域可选座席
    AREA_COP_OPTIONAL_3_L, // p 区域可选座席
    AREA_COP_OPTIONAL_4_L, // b 区域可选座席
    AREA_COP_OPTIONAL_5_L, // g 区域可选座席

    AREA_COP_SELECTED_1_L, // y 区域已选座席
    AREA_COP_SELECTED_2_L, // o 区域已选座席
    AREA_COP_SELECTED_3_L, // p 区域已选座席
    AREA_COP_SELECTED_4_L, // b 区域已选座席
    AREA_COP_SELECTED_5_L, // g 区域已选座席

    AREA_COP_OPTIONAL_1_R, // y 区域可选座席
    AREA_COP_OPTIONAL_2_R, // o 区域可选座席
    AREA_COP_OPTIONAL_3_R, // p 区域可选座席
    AREA_COP_OPTIONAL_4_R, // b 区域可选座席
    AREA_COP_OPTIONAL_5_R, // g 区域可选座席

    AREA_COP_SELECTED_1_R, // y 区域已选座席
    AREA_COP_SELECTED_2_R, // o 区域已选座席
    AREA_COP_SELECTED_3_R, // p 区域已选座席
    AREA_COP_SELECTED_4_R, // b 区域已选座席
    AREA_COP_SELECTED_5_R, // g 区域已选座席
}

/**
 * 座位图各个顶点类型
 */
enum class VertexType {
    VERTEX_TYPE_DEFAULT,
    VERTEX_TYPE_LEFT_TOP, // 左上顶点
    VERTEX_TYPE_RIGHT_TOP, // 以此类推...
    VERTEX_TYPE_LEFT_BOTTOM,
    VERTEX_TYPE_RIGHT_BOTTOM,
    VERTEX_TYPE_LEFT,
    VERTEX_TYPE_TOP,
    VERTEX_TYPE_RIGHT,
    VERTEX_TYPE_BOTTOM,
    VERTEX_TYPE_CENTER_TOP,
    VERTEX_TYPE_CENTER_BOTTOM,
    VERTEX_TYPE_CENTER_HORIZONTAL
}

/**
 * 绘制文字基线
 */
fun Paint.getBaseline(top: Float, bottom: Float): Float {
    val fontMetrics = fontMetrics
    val baseline = (bottom + top - fontMetrics.bottom - fontMetrics.top) / 2
//    if (BuildConfig.DEBUG) {
//        String.format("baseline = %f, t = %f, b = %f, ft = %f, fb = %f", baseline, top, bottom, fontMetrics.top, fontMetrics.bottom).i()
//    }
    return baseline
}