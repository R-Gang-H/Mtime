package com.kotlin.chat_component.inner.interfaces

import android.view.View
import com.hyphenate.chat.EMMessage

/**
 * 消息列表中的点击事件
 */
interface MessageListItemClickListener {
    /**
     * there is default handling when bubble is clicked, if you want handle it, return true
     * another way is you implement in onBubbleClick() of chat row
     * @param message
     * @return
     */
    fun onBubbleClick(message: EMMessage?): Boolean

    /**
     * click resend view
     * @param message
     * @return
     */
    fun onResendClick(message: EMMessage?): Boolean

    /**
     * on long click for bubble
     * @param v
     * @param message
     */
    fun onBubbleLongClick(v: View?, message: EMMessage?): Boolean

    /**
     * click the user avatar
     * @param username
     */
    fun onUserAvatarClick(username: String?)

    /**
     * long click for user avatar
     * @param username
     */
    fun onUserAvatarLongClick(username: String?)

    /**
     * message is create status
     * @param message
     */
    fun onMessageCreate(message: EMMessage?)

    /**
     * message send success
     * @param message
     */
    fun onMessageSuccess(message: EMMessage?)

    /**
     * message send fail
     * @param message
     * @param code
     * @param error
     */
    fun onMessageError(message: EMMessage?, code: Int, error: String?)

    /**
     * message in sending progress
     * @param message
     * @param progress
     */
    fun onMessageInProgress(message: EMMessage?, progress: Int)
}