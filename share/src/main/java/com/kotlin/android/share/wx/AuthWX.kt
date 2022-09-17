package com.kotlin.android.share.wx

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.kotlin.android.share.entity.AuthEntity
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory

/**
 * 微信授权登录：
 *
 * Created on 2020/11/30.
 *
 * @author o.s
 */
class AuthWX(
        private val context: Context,
        private val appId: String,
        private val scope: String,
        private val state: String,
) {

    companion object {
        var authState: ((state: AuthEntity) -> Unit)? = null
    }

    private val api: IWXAPI by lazy {
        WXAPIFactory.createWXAPI(context, appId, true).apply {
            registerApp(appId)
        }
    }

    init {
        //建议动态监听微信启动广播进行注册到微信
        context.run {
            registerReceiver(object : BroadcastReceiver() {

                override fun onReceive(context: Context?, intent: Intent?) {
                    // 将该app注册到微信
                    api.registerApp(appId)
                }

            }, IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP))
        }
    }

    fun auth() {
        val req = SendAuth.Req()
        req.scope = scope
        req.state = state
        api.sendReq(req)
    }
}