package com.kotlin.android.app.router.provider.message_center

/**
 * Created by zhaoninglongfei on 2022/4/24
 * 未读消息监听  用于消息中心外部消息数的展示
 */
interface UnReadMessageObserver {
    //长连接cmd消息的未读消息总数 + 环信的所有未读消息总数
    fun onNotifyMessageCount(totalCount: Long)
}