package com.kotlin.android.api.socket

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.NonNull
import com.kotlin.android.core.CoreApp
import com.kotlin.android.retrofit.ssl.SSL
import com.kotlin.android.retrofit.ssl.UnSafeHostnameVerifier
import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket
import io.socket.emitter.Emitter
import io.socket.engineio.client.transports.WebSocket
import okhttp3.*
import java.net.URISyntaxException
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * create by lushan on 2021/2/22
 * description: socket基类
 */
open class SocketManager {

    private var okHttpClient: OkHttpClient? = null
    private var sCookies = ConcurrentHashMap<String, List<Cookie>>()
    var socket: Socket? = null
    private val SEND_MESSAGE_EVENT = "send_message"//发送消息事件
    private val NEW_MESSENGE_EVENT = "new_message"//接收新消息事件

    protected fun init(url: String) {
        if (okHttpClient == null) {
            val builder = OkHttpClient.Builder()
            builder.cookieJar(object : CookieJar {
                private fun url(url: HttpUrl): String {
                    return url.scheme + "://" + url.host + ":" + url.port + url.encodedPath
                }

                override fun saveFromResponse(@NonNull url: HttpUrl, @NonNull cookies: List<Cookie>) {
                    if (cookies.size > 0) {
                        sCookies[url(url).orEmpty()] = cookies
                    }
                }

                override fun loadForRequest(@NonNull url: HttpUrl): List<Cookie> {
                    val cookies: List<Cookie>? = sCookies.get(url(url).orEmpty())
                    return cookies ?: ArrayList()
                }
            })
//            设置ssl
            builder.hostnameVerifier(UnSafeHostnameVerifier())
            SSL.instance.ignoreSSL?.apply {
                builder.sslSocketFactory(sslSocketFactory, trustManager)
            }
            okHttpClient = builder.build()
        }
        val options = IO.Options().apply {
            webSocketFactory = okHttpClient
            callFactory = okHttpClient
            transports = arrayOf(
                    WebSocket.NAME
            )
            reconnection = true
        }

//        创建socket
        try {
            socket = IO.socket(url, options)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    fun connect() {
        socket?.connect()
    }

    open fun onConnected(vararg args: Any?) {

    }

    open fun onDisconnected() {

    }

    /**
     * 长链接链接状态监听
     */
    fun setSocketListener(listener: ISocketListener) {
//        链接成功
        socket?.on(Socket.EVENT_CONNECT) {
            onConnected(it)
            CoreApp.instance.applicationContext.runOnUiThread {
                Log.e("SocketManager", "链接成功")
                it.forEach { any ->
                    Log.e("SocketManager", "链接成功" + any.toString())
                }

                listener.onConnected()
            }

        }
//        链接错误
        socket?.on(Socket.EVENT_CONNECT_ERROR) {
            CoreApp.instance.applicationContext.runOnUiThread {
                it.forEach { any ->
                    Log.e("SocketManager", "链接错误", any as Throwable)
                }
                listener.onConnectError()
            }

        }
//       链接断开
        socket?.on(Socket.EVENT_DISCONNECT) {
            onDisconnected()
            CoreApp.instance.applicationContext.runOnUiThread {
                it.forEach { any ->
                    Log.e("SocketManager", "链接断开" + any.toString())
                }
                listener.onDisconnect()
            }

        }

        socket?.on(Manager.EVENT_OPEN) {
            CoreApp.instance.applicationContext.runOnUiThread {
                it.forEach { any ->
                    Log.e("SocketManager", "链接成功Open" + any.toString())
                }
                listener.onConnected()
            }

        }
    }

    /**
     * 接收消息事件监听
     * @param listener 接收消息监听事件
     * @param event 接收新消息注册事件名称
     */
    fun setEventListener(event: String = NEW_MESSENGE_EVENT, listener: Emitter.Listener) {
        socket?.on(event, listener)
    }

    /**
     * 发送消息
     * @param content 发送消息内容
     * @param event 发送消息注册事件名称
     */
    open fun sendEvent(event: String = SEND_MESSAGE_EVENT, content: String) {
        socket?.emit(event, content)
    }

    fun disconnect() {
        socket?.disconnect()
        socket?.off()
    }

    fun isConnecting():Boolean = socket?.connected()?:true


    interface ISocketListener {
        fun onConnected()
        fun onConnectError()
        fun onDisconnect()
    }

}

fun Context.runOnUiThread(f: Context.() -> Unit) {
    if (Looper.getMainLooper() === Looper.myLooper()) f() else ContextHelper.handler.post { f() }
}

private object ContextHelper {
    val handler = Handler(Looper.getMainLooper())
}