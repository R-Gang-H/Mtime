package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * 卡片大富翁api - 全部套装列表（/allSuitList.api）
 *
 * Created on 2020/9/27.
 *
 * @author o.s
 */
data class AllSuitList(
        var hasMore: Boolean = false,
        var totalCount: Long = 0,
        var suitList: List<Suit>? = null
) : ProguardRule {
    data class Suit(
            var suitId: Long = 0,
            var suitName: String? = null,
            var suitCover: String? = null,
            var cardCount: Long = 0,
            var description: String? = null,
            var issueTime: Long = 0,
            var issueUserInfo: UserInfo? = null,
            var earliestMixUser: UserInfo? = null,
            var cardList: List<Card>? = null
    ) : ProguardRule
}