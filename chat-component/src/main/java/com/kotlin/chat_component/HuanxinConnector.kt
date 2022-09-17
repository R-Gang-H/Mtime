package com.kotlin.chat_component

import com.hyphenate.EMCallBack
import com.hyphenate.chat.EMClient
import com.kotlin.android.api.base.checkResult
import com.kotlin.android.app.data.entity.chatroom.Token
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.user.UserManager
import com.kotlin.android.user.isLogin
import com.kotlin.chat_component.manager.ChatUserCacheManager
import com.kotlin.chat_component.manager.HuanxinConnectManager
import com.kotlin.chat_component.manager.HuanxinTokenStore
import com.kotlin.chat_component.model.MtimeUserSimple
import com.kotlin.chat_component.repository.ChatRoomRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * Created by zhaoninglongfei on 2021/11/29.
 * 环信连接器
 */
class HuanxinConnector private constructor() {
    companion object {
        val instance by lazy { HuanxinConnector() }
    }

    private val repo by lazy { ChatRoomRepository() }

    /**
     * 连接环信 内部做了登录判断
     */
    fun connect() {
        "开始连接环信".e()
        if (isConnected()) {
            "环信已连接".e()
            //注册会话监听
            HuanxinConversationManager.registerMtimeConversationListenerToHuanxin()
            //注册消息监听
            HuanxinMessageManager.registerMtimeMessageListenerToHuanxin()
            //collect current user
            val token = HuanxinTokenStore.instance.getToken()
            token?.bizData?.imId?.let { imId ->
                collectCurrentUserInfo(imId)
            }
            return
        }
        if (isLogin()) {
            val token = HuanxinTokenStore.instance.getToken()
            if (token == null) {
                //从业务服务器获取环信token
                getHuanXinTokenFromServer(
                    success = {
                        connectHuanxin(it)
                        //注册连接监听
                        HuanxinConnectManager.registerConnectListener(object :
                            HuanxinConnectManager.DisConnectListener {
                            override fun disConnected() {
                                //todo 账号异常  比如用户被其他设备踢掉
                                //connect()
                            }
                        })
                    }, fail = {
                        "从业务服务器上取环信Token失败".e()
                    })
            } else {
                token.bizData?.imId?.let { imId ->
                    collectCurrentUserInfo(imId)
                }
                connectHuanxin(token)
            }
        }
    }

    /**
     * 是否以连接环信
     */
    fun isConnected(): Boolean {
        return HuanxinConnectManager.isConnected()
    }

    /**
     * 断开环信
     */
    fun disConnect() {
        HuanxinTokenStore.instance.clearToken()
        if (EMClient.getInstance().isLoggedInBefore) {
            //异步退出登录
            EMClient.getInstance().logout(false, object : EMCallBack {
                override fun onSuccess() {
                    "断开环信成功".e()
                }

                override fun onError(p0: Int, p1: String?) {
                }

                override fun onProgress(p0: Int, p1: String?) {
                }
            })
        }
    }

    /**
     * 从业务服务器上取环信Token
     */
    private fun getHuanXinTokenFromServer(
        success: (token: Token) -> Unit,
        fail: (String?) -> Unit
    ) {
        MainScope().launch(Dispatchers.IO) {
            val async = async { repo.getToken() }
            val result = async.await()
            checkResult(
                result = result,
                success = { token ->
                    HuanxinTokenStore.instance.saveToken(token)
                    token.bizData?.imId?.let { imId ->
                        collectCurrentUserInfo(imId)
                        success.invoke(token)
                    }
                },
                error = {
                    MainScope().launch {
                        fail.invoke(it)
                    }
                }
            )
        }
    }

    /**
     * 连接环信服务器
     */
    private fun connectHuanxin(token: Token) {
        connectHuanxin(token, success = {
            "连接环信成功".e()

            //连接成功后注册会话监听
            HuanxinConversationManager.registerMtimeConversationListenerToHuanxin()
            //连接成功后注册消息监听
            HuanxinMessageManager.registerMtimeMessageListenerToHuanxin()
            //首次连接成功 加载一次远程回话列表
            HuanxinConversationManager.loadHuanxinConversations()
        }, fail = {
            "连接环信失败".e()

            //断开链接后注销会话监听
            HuanxinConversationManager.unRegisterMtimeConversationListenerFromHuanxin()
            //断开链接后注销消息监听
            HuanxinMessageManager.unRegisterMtimeMessageListenerToHuanxin()
        })
    }

    /**
     * 连接环信服务器
     */
    private fun connectHuanxin(token: Token, success: () -> Unit, fail: (String?) -> Unit) {
        HuanxinConnectManager.connectWithToken(
            token.bizData?.imId,
            token.bizData?.token, object :
                HuanxinConnectManager.ConnectResult {
                override fun onSuccess() {
                    MainScope().launch {
                        success.invoke()
                    }
                }

                override fun onError(reason: String?) {
                    MainScope().launch {
                        fail.invoke(reason)
                    }
                }
            })
    }

    private fun collectCurrentUserInfo(imId: String) {
        ChatUserCacheManager.put(
            imId,
            MtimeUserSimple(
                imId,
                UserManager.instance.userId,
                UserManager.instance.nickname,
                UserManager.instance.userAvatar,
                UserManager.instance.userAuthType,
                UserManager.instance.userAuthRole
            )
        )
    }
}