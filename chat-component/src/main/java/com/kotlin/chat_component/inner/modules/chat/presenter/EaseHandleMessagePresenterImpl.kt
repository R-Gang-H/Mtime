package com.kotlin.chat_component.inner.modules.chat.presenter

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.text.TextUtils
import com.hyphenate.EMCallBack
import com.hyphenate.EMValueCallBack
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMCmdMessageBody
import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMTextMessageBody
import com.hyphenate.exceptions.HyphenateException
import com.hyphenate.util.EMLog
import com.hyphenate.util.PathUtil
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.chat_component.R
import com.kotlin.chat_component.inner.constants.EaseConstant
import com.kotlin.chat_component.inner.manager.EaseAtMessageHelper
import com.kotlin.chat_component.inner.modules.chat.EaseChatLayout
import com.kotlin.chat_component.inner.utils.EaseCommonUtils
import com.kotlin.chat_component.inner.utils.EaseFileUtils
import java.io.File
import java.io.FileOutputStream

/**
 * 处理发送消息及自定义消息
 */
class EaseHandleMessagePresenterImpl : EaseHandleMessagePresenter() {
    override fun sendTextMessage(content: String?) {
        sendTextMessage(content, false)
    }

    override fun sendTextMessage(content: String?, isNeedGroupAck: Boolean) {
        if (EaseAtMessageHelper.get().containsAtUsername(content)) {
            sendAtMessage(content)
            return
        }
        val message = EMMessage.createTxtSendMessage(content, toChatUsername)
        message.setIsNeedGroupAck(isNeedGroupAck)
        sendMessage(message)
    }

    override fun sendAtMessage(content: String?) {
        if (!isGroupChat) {
            EMLog.e(TAG, "only support group chat message")
            if (isActive) {
                runOnUI { mView!!.sendMessageFail("only support group chat message") }
            }
            return
        }
        val message = EMMessage.createTxtSendMessage(content, toChatUsername)
        val group = EMClient.getInstance().groupManager().getGroup(toChatUsername)
        if (EMClient.getInstance().currentUser == group.owner && EaseAtMessageHelper.get()
                .containsAtAll(content)
        ) {
            message.setAttribute(
                EaseConstant.MESSAGE_ATTR_AT_MSG,
                EaseConstant.MESSAGE_ATTR_VALUE_AT_MSG_ALL
            )
        } else {
            message.setAttribute(
                EaseConstant.MESSAGE_ATTR_AT_MSG,
                EaseAtMessageHelper.get()
                    .atListToJsonArray(EaseAtMessageHelper.get().getAtMessageUsernames(content))
            )
        }
        sendMessage(message)
    }

    override fun sendBigExpressionMessage(name: String?, identityCode: String?) {
        val message = EaseCommonUtils.createExpressionMessage(toChatUsername, name, identityCode)
        sendMessage(message)
    }

    override fun sendVoiceMessage(filePath: Uri?, length: Int) {
        val message = EMMessage.createVoiceSendMessage(filePath, length, toChatUsername)
        sendMessage(message)
    }

    override fun sendImageMessage(imageUri: Uri?) {
        sendImageMessage(imageUri, true)
    }

    override fun sendImageMessage(imageUri: Uri?, sendOriginalImage: Boolean) {
        val message = EMMessage.createImageSendMessage(imageUri, sendOriginalImage, toChatUsername)
        sendMessage(message)
    }

    override fun sendLocationMessage(
        latitude: Double,
        longitude: Double,
        locationAddress: String?,
        buildingName: String?
    ) {
        val message = EMMessage.createLocationSendMessage(
            latitude,
            longitude,
            locationAddress,
            buildingName
        )
        EMLog.i(TAG, "current = " + EMClient.getInstance().currentUser + " to = " + toChatUsername)
        val body = message.body
        val msgId = message.msgId
        val from = message.from
        EMLog.i(TAG, "body = $body")
        EMLog.i(TAG, "msgId = $msgId from = $from")
        sendMessage(message)
    }

    override fun sendVideoMessage(videoUri: Uri?, videoLength: Int) {
        val thumbPath = getThumbPath(videoUri)
        val message =
            EMMessage.createVideoSendMessage(videoUri, thumbPath, videoLength, toChatUsername)
        sendMessage(message)
    }

    override fun sendFileMessage(fileUri: Uri?) {
        val message = EMMessage.createFileSendMessage(fileUri, toChatUsername)
        sendMessage(message)
    }

    override fun addMessageAttributes(message: EMMessage?) {
        //可以添加一些自定义属性
        mView!!.addMsgAttrBeforeSend(message)
    }

    override fun sendMessage(message: EMMessage?) {
        if (message == null) {
            if (isActive) {
                runOnUI { mView!!.sendMessageFail("message is null!") }
            }
            return
        }
        addMessageAttributes(message)
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            message.chatType = EMMessage.ChatType.GroupChat
        } else if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
            message.chatType = EMMessage.ChatType.ChatRoom
        }
        message.setMessageStatusCallback(object : EMCallBack {
            override fun onSuccess() {
                if (isActive) {
                    runOnUI { mView!!.onPresenterMessageSuccess(message) }
                }
            }

            override fun onError(code: Int, error: String) {
                if (isActive) {
                    runOnUI { mView!!.onPresenterMessageError(message, code, error) }
                }
            }

            override fun onProgress(progress: Int, status: String) {
                if (isActive) {
                    runOnUI { mView!!.onPresenterMessageInProgress(message, progress) }
                }
            }
        })
        // send message
        EMClient.getInstance().chatManager().sendMessage(message)
        if (isActive) {
            runOnUI { mView!!.sendMessageFinish(message) }
        }
    }

    override fun sendCmdMessage(action: String?) {
        val beginMsg = EMMessage.createSendMessage(EMMessage.Type.CMD)
        val body = EMCmdMessageBody(action)
        // Only deliver this cmd msg to online users
        body.deliverOnlineOnly(true)
        beginMsg.addBody(body)
        beginMsg.to = toChatUsername
        EMClient.getInstance().chatManager().sendMessage(beginMsg)
    }

    override fun resendMessage(message: EMMessage?) {
        message!!.setStatus(EMMessage.Status.CREATE)
        val currentTimeMillis = System.currentTimeMillis()
        message.setLocalTime(currentTimeMillis)
        message.msgTime = currentTimeMillis
        EMClient.getInstance().chatManager().updateMessage(message)
        sendMessage(message)
    }

    override fun deleteMessage(message: EMMessage?) {
        conversation!!.removeMessage(message!!.msgId)
        if (isActive) {
            runOnUI { mView!!.deleteLocalMessageSuccess(message) }
        }
    }

    override fun recallMessage(message: EMMessage?) {
        try {
            val msgNotification = EMMessage.createSendMessage(EMMessage.Type.TXT)
            val txtBody =
                EMTextMessageBody(mView!!.context()!!.resources.getString(R.string.msg_recall_by_self))
            msgNotification.addBody(txtBody)
            msgNotification.to = message!!.to
            msgNotification.msgTime = message.msgTime
            msgNotification.setLocalTime(message.msgTime)
            msgNotification.setAttribute(EaseConstant.MESSAGE_TYPE_RECALL, true)
            msgNotification.setStatus(EMMessage.Status.SUCCESS)
            EMClient.getInstance().chatManager().recallMessage(message)
            EMClient.getInstance().chatManager().saveMessage(msgNotification)
            if (isActive) {
                runOnUI { mView!!.recallMessageFinish(msgNotification) }
            }
        } catch (e: HyphenateException) {
            e.printStackTrace()
            if (isActive) {
                runOnUI { mView!!.recallMessageFail(e.errorCode, e.description) }
            }
        }
    }

//    override fun translateMessage(
//        message: EMMessage?,
//        languageCode: String?,
//        isTranslation: Boolean
//    ) {
//        val body = message!!.body as EMTextMessageBody
//        if (isTranslation) {
//            val result = EMClient.getInstance().translationManager().getTranslationResult(
//                message.msgId
//            )
//            if (result != null) {
//                result.setShowTranslation(true)
//                EMClient.getInstance().translationManager().updateTranslationResult(result)
//                if (isActive) {
//                    runOnUI { mView!!.translateMessageSuccess(message) }
//                }
//                return
//            }
//        }
//        EMClient.getInstance().translationManager().translate(
//            message.msgId,
//            message.conversationId(),
//            body.message,
//            languageCode,
//            object : EMValueCallBack<EMTranslationResult?> {
//                override fun onSuccess(value: EMTranslationResult?) {
//                    if (isActive) {
//                        runOnUI { mView!!.translateMessageSuccess(message) }
//                    }
//                }
//
//                override fun onError(error: Int, errorMsg: String) {
//                    if (isActive) {
//                        runOnUI { mView!!.translateMessageFail(message, error, errorMsg) }
//                    }
//                }
//            })
//    }

//    override fun hideTranslate(message: EMMessage?) {
//        val result = EMClient.getInstance().translationManager().getTranslationResult(
//            message!!.msgId
//        )
//        result.setShowTranslation(false)
//        EMClient.getInstance().translationManager().updateTranslationResult(result)
//    }

    /**
     * 获取视频封面
     *
     * @param videoUri
     * @return
     */
    private fun getThumbPath(videoUri: Uri?): String {
        if (!EaseFileUtils.isFileExistByUri(mView!!.context(), videoUri)) {
            return ""
        }
        val filePath = EaseFileUtils.getFilePath(mView!!.context(), videoUri)
        val file =
            File(PathUtil.getInstance().videoPath, "thvideo" + System.currentTimeMillis() + ".jpeg")
        var createSuccess = true
        if (!TextUtils.isEmpty(filePath) && File(filePath).exists()) {
            try {
                val fos = FileOutputStream(file)
                val ThumbBitmap = ThumbnailUtils.createVideoThumbnail(filePath, 3)
                ThumbBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.close()
            } catch (e: Exception) {
                e.printStackTrace()
                EMLog.e(TAG, e.message)
                if (isActive) {
                    runOnUI { mView!!.createThumbFileFail(e.message) }
                }
                createSuccess = false
            }
        } else {
            try {
                val fos = FileOutputStream(file)
                val media = MediaMetadataRetriever()
                media.setDataSource(mView!!.context(), videoUri)
                val frameAtTime = media.frameAtTime
                frameAtTime!!.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.close()
            } catch (e: Exception) {
                e.printStackTrace()
                EMLog.e(TAG, e.message)
                if (isActive) {
                    runOnUI { mView!!.createThumbFileFail(e.message) }
                }
                createSuccess = false
            }
        }
        return if (createSuccess) file.absolutePath else ""
    }

    companion object {
        private val TAG = EaseChatLayout::class.java.simpleName
    }
}