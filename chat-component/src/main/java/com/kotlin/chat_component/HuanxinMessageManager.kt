package com.kotlin.chat_component

import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by zhaoninglongfei on 2022/4/20
 * 环信消息管理
 */
object HuanxinMessageManager {

    //监听环信的各种消息
    interface HuanxinMessageListener {
        fun onMessageReceived(messages: List<EMMessage>?)//接收到聊天消息
        fun onCmdMessageReceived(messages: List<EMMessage>?)//接收到透传消息
        fun onMessageDelivered(messages: List<EMMessage>?)//消息已送出
    }

    private var huanxinMessageListenerList = arrayListOf<HuanxinMessageListener>()

    //添加环信消息监听
    fun addHuanxinMessageListener(listener: HuanxinMessageListener) {
        huanxinMessageListenerList.add(listener)
    }

    //移除环信消息监听
    fun removeHuanxinMessageListener(listener: HuanxinMessageListener) {
        huanxinMessageListenerList.remove(listener)
    }

    fun registerMtimeMessageListenerToHuanxin() {
        EMClient.getInstance().chatManager().addMessageListener(emMessageListener)
    }

    fun unRegisterMtimeMessageListenerToHuanxin() {
        EMClient.getInstance().chatManager().removeMessageListener(emMessageListener)
    }

    //环信内部消息监听 EMMessageListener
    private var emMessageListener: EMMessageListener = object : EMMessageListener {
        override fun onMessageReceived(messages: List<EMMessage>?) {
            CoroutineScope(Dispatchers.Main).launch {
                huanxinMessageListenerList.forEach {
                    it.onMessageReceived(messages)
                }
            }
        }

        override fun onCmdMessageReceived(messages: List<EMMessage>?) {
            CoroutineScope(Dispatchers.Main).launch {
                huanxinMessageListenerList.forEach {
                    it.onCmdMessageReceived(messages)
                }
            }
        }

        override fun onMessageRead(messages: List<EMMessage>?) {}

        override fun onMessageDelivered(messages: List<EMMessage>?) {
            CoroutineScope(Dispatchers.Main).launch {
                huanxinMessageListenerList.forEach {
                    it.onMessageDelivered(messages)
                }
            }
        }

        override fun onMessageRecalled(p0: List<EMMessage>?) {}

        override fun onMessageChanged(p0: EMMessage?, p1: Any?) {}
    }
}