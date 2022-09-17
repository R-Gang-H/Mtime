package com.kotlin.android.app.data.entity.monopoly

/**
 * @desc item中传递参数的数据bean
 * @author zhangjian
 * @date 2020/11/12 19:18
 */
data class ItemData(
        var recordDetail: Long? = 0,
        var position: Int? = -1,
        var type: Long? = 0,
        var actionType: Int? = 0,
        var userInfo: UserInfo? = null,
        var srcCard: Card? = null,
        var desCard: Card? = null,
        var price:Long? = 0,
        var recordStatus:Long? = 0,
        var userName: String? = null
)