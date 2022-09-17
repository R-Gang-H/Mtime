package com.kotlin.android.card.monopoly.widget.dialog.view

/**
 * 宝箱类型
 *
 * Created on 2021/5/19.
 *
 * @author o.s
*/
enum class BoxType(val id: Long) {
    COPPER(1), // 铜宝箱
    SILVER(2), // 银宝箱
    GOLD(3), // 金宝箱
    PLATINUM(4), // 铂金宝箱
    DIAMONDS(5), // 钻石宝箱
    LIMIT(6), // 限量宝箱
    ACTIVITY(7); // 活动宝箱

    companion object {
        fun obtain(id: Long): BoxType {
            return when (id) {
                1L -> {
                    COPPER
                }
                2L -> {
                    SILVER
                }
                3L -> {
                    GOLD
                }
                4L -> {
                    PLATINUM
                }
                5L -> {
                    DIAMONDS
                }
                6L -> {
                    LIMIT
                }
                7L -> {
                    ACTIVITY
                }
                else -> {
                    COPPER
                }
            }
        }
    }
}