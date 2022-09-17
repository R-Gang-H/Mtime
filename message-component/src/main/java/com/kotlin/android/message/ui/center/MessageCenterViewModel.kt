package com.kotlin.android.message.ui.center

import androidx.lifecycle.*
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.api.base.checkResult
import com.kotlin.android.app.data.entity.message.ClearResult
import com.kotlin.android.app.data.entity.message.CmdHuanxin
import com.kotlin.android.app.data.entity.message.UnreadCountResult
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.message.repository.MessageRepository
import com.kotlin.android.message.ui.center.binder.MessageCenterChatListItemBinder
import com.kotlin.android.message.ui.center.binder.MessageCenterHeaderItemBinder
import com.kotlin.android.message.ui.center.binder.MessageCenterMovieNotifyItemBinder
import com.kotlin.android.message.ui.center.viewBean.ChatViewBean.Companion.convertChatViewBeanList
import com.kotlin.android.message.ui.center.viewBean.MessageCenterChatListViewBean
import com.kotlin.android.message.ui.center.viewBean.MessageCenterMovieNotifyViewBean
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.chat_component.HuanxinConversationManager
import com.kotlin.chat_component.MtimeChatObserve
import com.kotlin.chat_component.MtimeCmdObserve
import com.kotlin.chat_component.MtimeConversationObserve
import com.kotlin.chat_component.model.MtimeChatItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by zhaoninglongfei on 2022/3/4
 *
 */
class MessageCenterViewModel : BaseViewModel(), LifecycleObserver {

    private val repo = MessageRepository()

    private val unReadUiModel = BaseUIModel<UnreadCountResult>()
    val unReadUIState = unReadUiModel.uiState

    private val clearMessagesUIModel = BaseUIModel<ClearResult>()
    val clearMessagesUiState = clearMessagesUIModel.uiState

    val chatListUIState = MutableLiveData<MessageCenterChatListViewBean>()

    //环信聊天监听
    private var chatObserverChat: MtimeChatObserve.ChatMessageObserver =
        object : MtimeChatObserve.ChatMessageObserver {

            override fun onMessageReceived() {
                //刷新重新刷新聊天列表
                loadChatList()
            }

            override fun onMessageDelivered() {

            }
        }

    //环信消息监听
    private var messageDetailObserver: MtimeCmdObserve.MessageCountDetailObserver =
        object : MtimeCmdObserve.MessageCountDetailObserver {
            override fun onNotifyMessageDetail(cmd: CmdHuanxin) {
                unReadUiModel.emitUIState(
                    success = UnreadCountResult(
                        commentReply = cmd.data?.commentReply ?: 0L,
                        praise = cmd.data?.praise ?: 0L,
                        userFollow = cmd.data?.userFollow ?: 0L,
                        movieRelease = cmd.data?.movieRelease ?: 0L,
                        movieName = cmd.data?.movieName,
                    )
                )
            }
        }

    //环信会话监听
    private var mtimeConversationObserver: MtimeConversationObserve.MtimeConversationObserver =
        object : MtimeConversationObserve.MtimeConversationObserver {
            override fun onConversationUpdate() {
                loadChatList()
            }

            override fun onConversationRead() {}
        }

    //创建默认布局
    fun createDefaultView(): List<MultiTypeBinder<*>> {
        var messageCenterBinders = arrayListOf<MultiTypeBinder<*>>()
        messageCenterBinders.apply {
            //顺序不能错
            add(MessageCenterHeaderItemBinder())//评论/回复 点赞
            add(MessageCenterMovieNotifyItemBinder(MessageCenterMovieNotifyViewBean()))//观影通知
            add(MessageCenterChatListItemBinder())//聊天列表
        }
        return messageCenterBinders
    }

    fun loadUnreadCount() {
        call(
            uiModel = unReadUiModel,
            isRefresh = true
        ) {
            repo.loadUnreadCount()
        }
    }

    //加载环信聊天列表
    fun loadChatList() {
        CoroutineScope(Dispatchers.Main).launch {
            val result: List<MtimeChatItem> = withOnIO {
                HuanxinConversationManager.loadMtimeChatItems()
            }

            chatListUIState.value = MessageCenterChatListViewBean(
                chatList = convertChatViewBeanList(result),
                chatDelete = {
                    loadChatList()
                }
            )
        }
    }

    // 清空所有未读消息
    fun clearAllUnreadMessages() {
        HuanxinConversationManager.clearUnreadMessages()
        viewModelScope.launch(main) {
            clearMessagesUIModel.emitUIState(showLoading = true)
            val result = withOnIO {
                repo.clearAllUnreadMessages()
            }
            checkResult(result,
                success = { clearMessagesUIModel.emitUIState(success = it) },
                error = { clearMessagesUIModel.emitUIState(error = it) },
                netError = { clearMessagesUIModel.emitUIState(netError = it) })
        }
        loadChatList()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun registerChatObserver() {
        //聊天监听
        MtimeChatObserve.addMessageDispatchObserver(chatObserverChat)

        //会话监听
        MtimeConversationObserve.addMtimeConversationListener(mtimeConversationObserver)

        //cmd消息监听
        MtimeCmdObserve.addMessageDetailObserver(messageDetailObserver)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun unRegisterChatObserver() {
        MtimeChatObserve.removeMessageDispatchObserver(chatObserverChat)
        MtimeCmdObserve.removeMessageDetailObserver(messageDetailObserver)
        MtimeConversationObserve.removeMtimeConversationListener(mtimeConversationObserver)
    }
}