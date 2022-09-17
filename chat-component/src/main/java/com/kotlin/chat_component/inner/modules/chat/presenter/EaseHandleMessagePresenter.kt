package com.kotlin.chat_component.inner.modules.chat.presenter

import android.net.Uri
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMMessage
import com.kotlin.chat_component.inner.constants.EaseConstant
import com.kotlin.chat_component.inner.modules.EaseBasePresenter
import com.kotlin.chat_component.inner.modules.ILoadDataView
import com.kotlin.chat_component.inner.utils.EaseCommonUtils

/**
 * 处理发送消息
 */
abstract class EaseHandleMessagePresenter : EaseBasePresenter() {
    @JvmField
    protected var mView: IHandleMessageView? = null

    @JvmField
    protected var chatType = 0

    @JvmField
    protected var toChatUsername: String? = null

    @JvmField
    protected var conversation: EMConversation? = null
    override fun attachView(view: ILoadDataView?) {
        mView = view as IHandleMessageView?
    }

    override fun detachView() {
        mView = null
    }

    override fun onDestroy() {
        super.onDestroy()
        detachView()
    }

    /**
     * 绑定发送方id
     * @param chatType
     * @param toChatUsername
     */
    fun setupWithToUser(chatType: Int, toChatUsername: String) {
        this.chatType = chatType
        this.toChatUsername = toChatUsername
        conversation = EMClient.getInstance().chatManager()
            .getConversation(toChatUsername, EaseCommonUtils.getConversationType(chatType), true)
    }

    /**
     * 发送文本消息
     * @param content
     */
    abstract fun sendTextMessage(content: String?)

    /**
     * 发送文本消息
     * @param content
     * @param isNeedGroupAck 需要需要群回执
     */
    abstract fun sendTextMessage(content: String?, isNeedGroupAck: Boolean)

    /**
     * 发送@消息
     * @param content
     */
    abstract fun sendAtMessage(content: String?)

    /**
     * 发送大表情消息
     * @param name
     * @param identityCode
     */
    abstract fun sendBigExpressionMessage(name: String?, identityCode: String?)

    /**
     * 发送语音消息
     * @param filePath
     * @param length
     */
    abstract fun sendVoiceMessage(filePath: Uri?, length: Int)

    /**
     * 发送图片消息
     * @param imageUri
     */
    abstract fun sendImageMessage(imageUri: Uri?)

    /**
     * 发送图片消息
     * @param imageUri
     * @param sendOriginalImage
     */
    abstract fun sendImageMessage(imageUri: Uri?, sendOriginalImage: Boolean)

    /**
     * 发送定位消息
     * @param latitude
     * @param longitude
     * @param locationAddress
     */
    abstract fun sendLocationMessage(
        latitude: Double,
        longitude: Double,
        locationAddress: String?,
        buildingName: String?
    )

    /**
     * 发送视频消息
     * @param videoUri
     * @param videoLength
     */
    abstract fun sendVideoMessage(videoUri: Uri?, videoLength: Int)

    /**
     * 发送文件消息
     * @param fileUri
     */
    abstract fun sendFileMessage(fileUri: Uri?)

    /**
     * 为消息添加扩展字段
     * @param message
     */
    abstract fun addMessageAttributes(message: EMMessage?)

    /**
     * 发送消息
     * @param message
     */
    abstract fun sendMessage(message: EMMessage?)

    /**
     * 发送cmd消息
     * @param action
     */
    abstract fun sendCmdMessage(action: String?)

    /**
     * 重新发送消息
     * @param message
     */
    abstract fun resendMessage(message: EMMessage?)

    /**
     * 删除消息
     * @param message
     */
    abstract fun deleteMessage(message: EMMessage?)

    /**
     * 撤回消息
     * @param message
     */
    abstract fun recallMessage(message: EMMessage?)

    /**
     * 是否是群聊
     * @return
     */
    val isGroupChat: Boolean
        get() = chatType == EaseConstant.CHATTYPE_GROUP

//    /**
//     * 翻译消息
//     * @param message
//     * @param languageCode
//     * @param isTranslation
//     */
//    abstract fun translateMessage(
//        message: EMMessage?,
//        languageCode: String?,
//        isTranslation: Boolean
//    )

//    /**
//     * 隐藏翻译
//     * @param message
//     */
//    abstract fun hideTranslate(message: EMMessage?)
}