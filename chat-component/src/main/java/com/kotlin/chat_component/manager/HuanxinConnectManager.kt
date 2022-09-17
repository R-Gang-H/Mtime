package com.kotlin.chat_component.manager

import com.hyphenate.EMCallBack
import com.hyphenate.EMConnectionListener
import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMClient
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * Created by zhaoninglongfei on 2021/11/25.
 * 环信连接管理
 */
object HuanxinConnectManager {


    fun isConnected(): Boolean {
        return EMClient.getInstance().isConnected
    }

    fun connectWithToken(userName: String?, token: String?, result: ConnectResult) {
        if (userName.isNullOrEmpty() || token.isNullOrEmpty()) {
            result.onError("参数有误")
            return
        }

        EMClient.getInstance().loginWithToken(userName, token, object : EMCallBack {
            override fun onSuccess() {
                MainScope().launch {
                    result.onSuccess()
                }
            }

            override fun onError(code: Int, error: String?) {
                MainScope().launch {
                    result.onError(error)
                }
            }

            override fun onProgress(progress: Int, status: String?) {

            }
        })
    }

    /**
     * 连接中断监听
     */
    fun registerConnectListener(disConnectListener: DisConnectListener) {
        EMClient.getInstance().addConnectionListener(object : EMConnectionListener {
            override fun onConnected() {

            }

            override fun onDisconnected(p0: Int) {
                //用来监听账号异常
                disConnectListener.disConnected()
            }
        })
    }

    /**
     * 断开连接监听
     */
    interface DisConnectListener {
        fun disConnected()
    }

    /**
     * 连接状态的回调
     */
    interface ConnectResult {
        fun onSuccess()
        fun onError(reason: String?)
    }
}