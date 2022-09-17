package com.kotlin.android.app.data.entity.monopoly

/**
 * 卡片大富翁api - 游戏信息列表（/gameRecord.api）
 *
 * Created on 2020/9/28.
 *
 * @author o.s
 */
data class RecordList(
        var hasMore: Boolean = false,
        var totalCount: Long = 0,
        var messageList: List<Record>? = null
)