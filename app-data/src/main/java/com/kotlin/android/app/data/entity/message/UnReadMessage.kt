package com.kotlin.android.app.data.entity.message

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/10/9
 * description: 获取未读消息统计(用于红点儿等)
 */
data class UnReadMessage(
    var errorCode: Long = 0L,//错误码，-1 表示设备推送编号不存在
    var errorMsg: String? = "",//错误信息
    var notificationContent: String? = "",//最新一条未读PUSH通知消息的内容
    var unreadBroadcastCount: Long = 0L,//Push广播消息未读数
    var unreadNotificationCount: Long = 0L,//PUSH通知消息未读数
    var unreadPraiseCount: Long = 0L,//【用户动态消息】点赞未读数
    var unreadReviewCount: Long = 0L//【用户动态消息】评论回复未读数
): ProguardRule {
    /**
     * 是否有消息
     */
    fun hasMsg(): Boolean {
        return (unreadBroadcastCount
                + unreadNotificationCount
                + unreadPraiseCount
                + unreadReviewCount) > 0L
    }
}
