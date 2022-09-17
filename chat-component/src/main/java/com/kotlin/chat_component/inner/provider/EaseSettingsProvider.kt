package com.kotlin.chat_component.inner.provider

import com.hyphenate.chat.EMMessage

/**
 * new message options provider
 *
 */
interface EaseSettingsProvider {
    /**
     * 是否允许消息提醒
     * @param message
     * @return
     */
    fun isMsgNotifyAllowed(message: EMMessage?): Boolean

    /**
     * 是否设置声音
     * @param message
     * @return
     */
    fun isMsgSoundAllowed(message: EMMessage?): Boolean

    /**
     * 是否允许震动
     * @param message
     * @return
     */
    fun isMsgVibrateAllowed(message: EMMessage?): Boolean

    /**
     * 是否使用扬声器播放声音
     * @return
     */
    val isSpeakerOpened: Boolean
}