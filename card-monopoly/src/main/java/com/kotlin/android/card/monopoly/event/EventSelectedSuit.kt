package com.kotlin.android.card.monopoly.event

import com.jeremyliao.liveeventbus.core.LiveEvent
import com.kotlin.android.app.data.entity.monopoly.Suit
import com.kotlin.android.router.bus.annotation.LiveEventKey

/**
 *
 * Created on 2021/9/26.
 *
 * @author o.s
 */
@LiveEventKey("live_event_key_selected_suit")
data class EventSelectedSuit(
    val from: Int, // 来源类型 1.用户详细页 2.拍卖行/拍卖
    val suit: Suit, // 选中的套装
) : LiveEvent