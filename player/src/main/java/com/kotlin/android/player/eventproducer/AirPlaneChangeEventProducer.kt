package com.kotlin.android.player.eventproducer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.kk.taurus.playerbase.extension.BaseEventProducer
import com.kk.taurus.playerbase.receiver.IReceiverGroup.OnReceiverFilter
import com.kotlin.android.player.DataInter
import java.lang.ref.WeakReference

/**
 * create by lushan on 2020/9/1
 * description:
 */
class AirPlaneChangeEventProducer(var ctx: Context) : BaseEventProducer() {
    companion object {
        val MSG_CODE_AIRPLANE_STATE_CHANGE = 10
    }

    private var mAppContext = ctx.applicationContext
    private var mBroadcastReceiver: AirPlaneBroadcastReceiver? = null

    private val onReceiverFilter = OnReceiverFilter { iReceiver -> DataInter.ReceiverKey.KEY_ERROR_COVER == iReceiver.key }
    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MSG_CODE_AIRPLANE_STATE_CHANGE -> {
                    val sender = sender
                    sender?.sendEvent(
                            DataInter.ProducerEvent.EVENT_AIRPLANE_STATE_CHANGE,
                            null,
                            onReceiverFilter)
                }
            }
        }
    }
    override fun onAdded() {
        try {
            mBroadcastReceiver = AirPlaneBroadcastReceiver(mHandler)
            val filter = IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)
            mAppContext.registerReceiver(mBroadcastReceiver, filter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onRemoved() {
        unregisterReceiver()
        cleanHandler()
    }

    private fun unregisterReceiver() {
        try {
            if (mBroadcastReceiver != null) {
                mAppContext.unregisterReceiver(mBroadcastReceiver)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun destroy() {
        unregisterReceiver()
        cleanHandler()
    }

    private fun cleanHandler() {
        mHandler.removeMessages(MSG_CODE_AIRPLANE_STATE_CHANGE)
    }

    class AirPlaneBroadcastReceiver(handler: Handler) : BroadcastReceiver() {
        private val mHandlerRefer: WeakReference<Handler>?
        val handler: Handler?
            get() = mHandlerRefer?.get()

        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (Intent.ACTION_AIRPLANE_MODE_CHANGED == action) {
                val postHandler = handler
                postHandler?.sendEmptyMessage(MSG_CODE_AIRPLANE_STATE_CHANGE)
            }
        }

        init {
            mHandlerRefer = WeakReference(handler)
        }
    }
}