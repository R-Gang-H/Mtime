package com.kotlin.android.push.receiver

import android.content.Context
import android.os.Build
import android.text.TextUtils
import cn.jpush.android.api.JPushInterface
import cn.jpush.android.api.NotificationMessage
import cn.jpush.android.service.JPushMessageReceiver
import com.google.gson.Gson
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.main.IMainProvider
import com.kotlin.android.ktx.ext.log.d
import org.json.JSONObject

/**
 * Created by zhaoninglongfei on 2022/2/15
 *
 */
class JPushReceiver : JPushMessageReceiver() {

    override fun onRegister(context: Context?, registrationID: String?) {
        super.onRegister(context, registrationID)
        getProvider(IMainProvider::class.java)
            ?.pushReceiverOnRegister(
                context,
                registrationID
            )
    }

    override fun onNotifyMessageOpened(context: Context?, message: NotificationMessage?) {
        //super不要调用，否则会直接启动Splash页面，导致applink数据跳转
        super.onNotifyMessageOpened(context, message)
        message ?: return
        JPushInterface.reportNotificationOpened(
            context,
            message.msgId,
            getDeviceBrand()
        )
        //JPushInterface.clearNotificationById(context,message.notificationId)

        "JPush onNotifyMessageOpened ".d()

        parseMessage(message)?.apply {
            getProvider(IMainProvider::class.java)?.onNotifyMessageOpened(
                context,
                this
            )
        }
    }

    /**
     * 解析推送消息数据
     */
    fun parseMessage(message: NotificationMessage): String? {
        val extras = message.notificationExtras
        "JPush notificationExtras : $extras".d()
        // 在这里处理点击通知
        if (TextUtils.isEmpty(extras) || "{}" == extras) return ""

        var appLinkData: String? = null

        try {
            val `object` = JSONObject(extras)
            appLinkData = `object`.getString("applinkData")
            "JPush appLinkData : $appLinkData".d()

        } catch (e: Exception) {
            e.d()
        }
        return appLinkData
    }

    override fun onNotifyMessageArrived(context: Context?, message: NotificationMessage?) {
        super.onNotifyMessageArrived(context, message)
        message ?: return
        "JPush onNotifyMessageArrived ".d()
        parseMessage(message)?.apply {
            getProvider(IMainProvider::class.java)?.onNotifyMessageArrived(
                context,
                this
            )
        }
//        getProvider(IMainProvider::class.java)?.onNotifyMessageArrived()
    }

    /**
     * 把设备标识转换为极光的type类型
     * @return deviceBrand
     */
    private fun getDeviceBrand(): Byte {
        return when (Build.BRAND) {
            "Xiaomi" -> 1
            "HUAWEI" -> 2
            "Meizu" -> 3
            "OPPO" -> 4
            "vivo" -> 5
            "FCM" -> 8
            else -> 0
        }
    }
}