package com.kotlin.chat_component.inner.modules.chat.presenter

import com.hyphenate.chat.EMMessage
import com.kotlin.chat_component.inner.modules.ILoadDataView

interface IHandleMessageView : ILoadDataView {
    /**
     * 生成视频封面失败
     */
    fun createThumbFileFail(message: String?)

    /**
     * 在发送消息前，添加消息属性，如设置ext等
     */
    fun addMsgAttrBeforeSend(message: EMMessage?)

    /**
     * 发送消息失败
     */
    fun sendMessageFail(message: String?)

    /**
     * 完成发送消息动作
     */
    fun sendMessageFinish(message: EMMessage?)

    /**
     * 删除本地消息
     */
    fun deleteLocalMessageSuccess(message: EMMessage?)

    /**
     * 完成撤回消息
     */
    fun recallMessageFinish(message: EMMessage?)

    /**
     * 撤回消息失败
     */
    fun recallMessageFail(code: Int, message: String?)

    /**
     * message send success
     */
    fun onPresenterMessageSuccess(message: EMMessage?)

    /**
     * message send fail
     */
    fun onPresenterMessageError(message: EMMessage?, code: Int, error: String?)

    /**
     * message in sending progress
     */
    fun onPresenterMessageInProgress(message: EMMessage?, progress: Int)
}