package com.kotlin.android.player

import android.content.Context
import com.kk.taurus.playerbase.receiver.ReceiverGroup
import com.kk.taurus.playerbase.touch.OnTouchGestureListener
import com.kotlin.android.core.ext.getSpValue
import com.kotlin.android.core.ext.putSpValue
import com.kotlin.android.player.receivers.*

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/15
 *
 * 播放器UI组件拼装
 */
object ReceiverGroupManager {
    const val FIRST_ENTRY_FULL_SCREEN = "first_entry_full_screen"
    /**
     * 获取极简UI组件
     */
    fun getLessReceiverGroup(context: Context, listener: OnTouchGestureListener): ReceiverGroup {
        return ReceiverGroup().apply {
            addReceiver(DataInter.ReceiverKey.KEY_TOUCH_GESTURE_COVER, TouchGestureListenerReceiver(context, listener))
            addReceiver(DataInter.ReceiverKey.KEY_LOADING_COVER, LoadingCover(context))
            addReceiver(DataInter.ReceiverKey.KEY_ERROR_COVER, ErrorCover(context))
        }
    }

    fun getNormalReceiverGroup(context: Context):ReceiverGroup{
        return ReceiverGroup().apply {
            addReceiver(DataInter.ReceiverKey.KEY_LOADING_COVER, LoadingCover(context))
            addReceiver(DataInter.ReceiverKey.KEY_ERROR_COVER, ErrorCover(context))
            addReceiver(DataInter.ReceiverKey.KEY_CONTROLLER_COVER, NewControllerCover(context))
        }
    }

    /**
     * 获取基础视频控制控件
     */
    fun getBaseReceiverGroup(context: Context):ReceiverGroup{
        return ReceiverGroup().apply {
            addReceiver(DataInter.ReceiverKey.KEY_DATA_RECEIVER, DataReceiver(context))
//        日志数据上报
//        receiverGroup.addReceiver(DataInter.ReceiverKey.KEY_LOG_RECEIVER, LogReceiver(context))
            addReceiver(DataInter.ReceiverKey.KEY_LOADING_COVER, LoadingCover(context))
            addReceiver(DataInter.ReceiverKey.KEY_CONTROLLER_COVER, NewControllerCover(context))
            addReceiver(DataInter.ReceiverKey.KEY_ERROR_COVER, ErrorCover(context))
            addReceiver(DataInter.ReceiverKey.KEY_PAUSE_AD_COVER, PauseAdCover(context))
        }

    }

    /**
     * 获取标准视频控件
     */
    fun getStandardReceiverGroup(context: Context):ReceiverGroup{
        return getBaseReceiverGroup(context).apply {
            addReceiver(DataInter.ReceiverKey.KEY_DEFINITION_COVER, VideoDefinitionCover(context))
            addReceiver(DataInter.ReceiverKey.KEY_GESTURE_COVER, PlayerGestureCover(context))
            if (isNeeduserGuideCover()) addReceiver(DataInter.ReceiverKey.KEY_USER_GUIDE_COVER, UserGuideCover(context))
        }
    }

    fun getStandardWithoutGestureReceiverGroup(context: Context):ReceiverGroup{
        return getBaseReceiverGroup(context).apply {
            addReceiver(DataInter.ReceiverKey.KEY_DEFINITION_COVER, VideoDefinitionCover(context))
            if (isNeeduserGuideCover()) addReceiver(DataInter.ReceiverKey.KEY_USER_GUIDE_COVER, UserGuideCover(context))
        }
    }

    /**
     * 是否需要新手引导
     */
    fun isNeeduserGuideCover() = getSpValue(FIRST_ENTRY_FULL_SCREEN, true).not()

    fun updateUserGuideIKnow() {
        putSpValue(FIRST_ENTRY_FULL_SCREEN, true)
    }
}