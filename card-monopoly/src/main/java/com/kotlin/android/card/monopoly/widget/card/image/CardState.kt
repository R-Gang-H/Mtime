package com.kotlin.android.card.monopoly.widget.card.image

/**
 * 卡片视图状态
 *
 * Created on 2020/11/13.
 *
 * @author o.s
 */
enum class CardState {

    /**
     * 选中卡片
     */
    SELECTED,

    /**
     * 填充卡片
     */
    FILL,

    /**
     * 空位
     */
    EMPTY,

    /**
     * 加锁
     */
    LOCK,

    /**
     * 不显示卡片
     */
    NO_DISPLAY;
}