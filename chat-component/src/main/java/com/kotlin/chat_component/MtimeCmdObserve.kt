package com.kotlin.chat_component

import com.kotlin.android.app.data.entity.message.CmdHuanxin

/**
 * Created by zhaoninglongfei on 2022/4/20
 * 长连接 cmd消息监听
 */
object MtimeCmdObserve {

    interface MessageCountDetailObserver {
        //新增未读消息数详情提醒
        fun onNotifyMessageDetail(cmd: CmdHuanxin)
    }

    //消息数详情观察者列表
    private var messageCountDetailObserverList = arrayListOf<MessageCountDetailObserver>()

    //添加消息数详情观察者
    fun addMessageDetailObserver(observer: MessageCountDetailObserver) {
        messageCountDetailObserverList.add(observer)
    }

    fun removeMessageDetailObserver(observer: MessageCountDetailObserver) {
        messageCountDetailObserverList.remove(observer)
    }

    fun notice(cmd: CmdHuanxin) {
        messageCountDetailObserverList.forEach {
            it.onNotifyMessageDetail(cmd)
        }
    }
}