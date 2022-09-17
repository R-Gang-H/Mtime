package com.kotlin.android.app.data.entity.monopoly

import com.kotlin.android.app.data.ProguardRule

/**
 * @desc 卡片商店宝箱列表bean
 * @author zhangjian
 * @date 2020/9/19 15:21
 */
data class StoreBoxListBean(
        var activityBoxList: List<ActivityBox>? = null,
        var limitBoxList: List<LimitBox>? = null,
        var commonBoxList: List<NormalBox>? = null,
        var boughtBox: BoughtBox? = null
) : ProguardRule {
    data class ActivityBox(
            var activityEndTime: Long = 0, // 1721
            var cardBoxId: Long = 0, // 3853
            var cardBoxName: String? = "", // Px9N1buM
            var cover: String? = "", // 4bslz2a
            var description: String? = "", // 74n6mqD
            var price: Long = 0 // 7557
    ) : ProguardRule

    data class LimitBox(
            var activityEndTime: Long = 0, // 5464
            var cardBoxId: Long = 0, // 498
            var cardBoxName: String? = "", // Xs3ItCa9
            var cover: String? = "", // 1Dl
            var description: String? = "", // RnZxQ
            var price: Long = 0 // 8315
    ) : ProguardRule

    data class NormalBox(
            var activityEndTime: Long = 0, // 1175
            var cardBoxId: Long = 0, // 2368
            var cardBoxName: String? = "", // hQNoacM
            var cover: String? = "", // jNcGO9Iw
            var description: String? = "", // g3bJ
            var price: Long = 0 // 6739
    ) : ProguardRule

    data class BoughtBox(
            var activityEndTime: Long = 0, // 1175
            var cardBoxId: Long = 0, // 2368
            var cardBoxName: String? = "", // hQNoacM
            var cover: String? = "", // jNcGO9Iw
            var description: String? = "", // g3bJ
            var price: Long = 0 // 6739
    ) : ProguardRule
}