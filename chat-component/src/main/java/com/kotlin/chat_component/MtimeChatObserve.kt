package com.kotlin.chat_component

/**
 * Created by zhaoninglongfei on 2022/4/20
 * 聊天监听
 * 适用于消息中心内部
 */
object MtimeChatObserve {

    //消息收发观察者
    interface ChatMessageObserver {
        //收到新消息
        fun onMessageReceived()

        //发出新消息
        fun onMessageDelivered()
    }

    private var messageDispatchObserverList = arrayListOf<ChatMessageObserver>()


    //添加消息收发观察者
    fun addMessageDispatchObserver(observerChat: ChatMessageObserver) {
        messageDispatchObserverList.add(observerChat)
    }

    fun removeMessageDispatchObserver(observerChat: ChatMessageObserver) {
        messageDispatchObserverList.remove(observerChat)
    }


    fun noticeMessageReceived() {
        messageDispatchObserverList.forEach {
            it.onMessageReceived()
        }
    }
}