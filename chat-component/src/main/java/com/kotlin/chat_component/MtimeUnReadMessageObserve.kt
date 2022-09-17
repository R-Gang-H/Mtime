package com.kotlin.chat_component

import com.kotlin.android.app.router.provider.message_center.UnReadMessageObserver

/**
 * Created by zhaoninglongfei on 2022/4/20
 * 未读消息监听
 * 适用于消息中心外部界面、消息小红点数的监听
 * onNotifyMessageCount 包含 长连接cmd消息的未读消息总数（时光业务未读消息数） + 环信的所有未读消息总数
 */
object MtimeUnReadMessageObserve {

    //未读消息观察者列表
    private var unReadMessageObserverList = arrayListOf<UnReadMessageObserver>()

    //添加未读消息观察者
    fun addUnReadMessageObserver(observer: UnReadMessageObserver) {
        unReadMessageObserverList.add(observer)
    }

    fun removeUnReadMessageObserver(observer: UnReadMessageObserver) {
        unReadMessageObserverList.remove(observer)
    }

    private var cmdMessageCount: Long? = null

    fun notice(cmdMessageCount: Long?, huanxinMessageCount: Int) {
        cmdMessageCount?.let {
            this.cmdMessageCount = cmdMessageCount
        }

        unReadMessageObserverList.forEach {
            if (this.cmdMessageCount == null) {
                it.onNotifyMessageCount(huanxinMessageCount.toLong())
            } else {
                it.onNotifyMessageCount(huanxinMessageCount.toLong() + this.cmdMessageCount!!)
            }
        }
    }
}