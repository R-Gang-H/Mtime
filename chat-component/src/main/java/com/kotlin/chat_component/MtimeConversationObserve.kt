package com.kotlin.chat_component

/**
 * Created by zhaoninglongfei on 2022/4/20
 * 会话监听
 */
object MtimeConversationObserve {

    interface MtimeConversationObserver {
        //会话变更回调
        fun onConversationUpdate()
        //收到会话已读回调
        //回调此方法的场景：
        //	 * （1）消息被接收方阅读（发送了会话已读回执）。
        //	 * SDK在接收到此事件时，会将本地数据库中该会话中消息的isAcked属性置为True。
        //	 * （2）多端多设备登录场景下，一端发送会话已读回执（conversation ack）,服务器端会将会话的未读消息数置为0，
        //	 * 同时其他端会回调此方法，并将本地数据库中该会话中消息的isRead属性置为True。
        fun onConversationRead()

    }

    private var mTimeConversationObserverList = arrayListOf<MtimeConversationObserver>()


    fun addMtimeConversationListener(listener: MtimeConversationObserver?) {
        listener?.let { mTimeConversationObserverList.add(it)}
    }

    fun removeMtimeConversationListener(listener: MtimeConversationObserver) {
        mTimeConversationObserverList.remove(listener)
    }

    fun noticeOnConversationUpdate(){
        mTimeConversationObserverList.forEach {
            it.onConversationUpdate()
        }
    }

    fun noticeOnConversationRead(){
        mTimeConversationObserverList.forEach {
            it.onConversationRead()
        }
    }

}
