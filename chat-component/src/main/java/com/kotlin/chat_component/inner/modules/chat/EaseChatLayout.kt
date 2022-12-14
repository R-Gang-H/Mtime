package com.kotlin.chat_component.inner.modules.chat

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.hyphenate.EMMessageListener
import com.hyphenate.chat.*
import com.hyphenate.chat.adapter.EMAChatRoomManagerListener
import com.hyphenate.exceptions.HyphenateException
import com.hyphenate.util.EMLog
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.chat_component.R
import com.kotlin.chat_component.inner.EaseIM
import com.kotlin.chat_component.inner.constants.EaseConstant
import com.kotlin.chat_component.inner.domain.EaseEmojicon
import com.kotlin.chat_component.inner.interfaces.EaseChatRoomListener
import com.kotlin.chat_component.inner.interfaces.EaseGroupListener
import com.kotlin.chat_component.inner.interfaces.MessageListItemClickListener
import com.kotlin.chat_component.inner.manager.EaseAtMessageHelper
import com.kotlin.chat_component.inner.modules.chat.EaseChatMessageListLayout.*
import com.kotlin.chat_component.inner.modules.chat.interfaces.*
import com.kotlin.chat_component.inner.modules.chat.presenter.EaseHandleMessagePresenter
import com.kotlin.chat_component.inner.modules.chat.presenter.EaseHandleMessagePresenterImpl
import com.kotlin.chat_component.inner.modules.chat.presenter.IHandleMessageView
import com.kotlin.chat_component.inner.modules.interfaces.IPopupWindow
import com.kotlin.chat_component.inner.modules.menu.EasePopupWindow.OnPopupWindowItemClickListener
import com.kotlin.chat_component.inner.modules.menu.EasePopupWindowHelper
import com.kotlin.chat_component.inner.modules.menu.MenuItemBean
import com.kotlin.chat_component.inner.utils.EaseCommonUtils
import com.kotlin.chat_component.inner.utils.EaseUserUtils
import com.kotlin.chat_component.inner.widget.EaseAlertDialog
import com.kotlin.chat_component.inner.widget.EaseAlertDialog.AlertDialogUser
import com.kotlin.chat_component.inner.widget.EaseVoiceRecorderView
import org.jetbrains.anko.toast

class EaseChatLayout @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), IChatLayout, IHandleMessageView, IPopupWindow,
    ChatInputMenuListener, EMMessageListener, OnMessageTouchListener, MessageListItemClickListener,
    OnChatErrorListener {

    override var chatMessageListLayout: EaseChatMessageListLayout? = null
        private set
    override var chatInputMenu: EaseChatInputMenu? = null
        private set
    private var voiceRecorder: EaseVoiceRecorderView? = null

    /**
     * ??????id????????????????????????id??????????????????id???????????????id
     */
    private var conversationId: String? = null

    //????????????
    private var chatType = 0

    //???????????????????????????
    private var listener: OnChatLayoutListener? = null

    //???????????????????????????????????????
    private var recordTouchListener: OnChatRecordTouchListener? = null
    private val presenter: EaseHandleMessagePresenter

    //??????????????????????????????
    private var showDefaultMenu = true

    //???????????????????????????
    private var menuHelper: EasePopupWindowHelper? = null
    private var clipboard: ClipboardManager? = null
    private var menuChangeListener: OnMenuChangeListener? = null

    //????????????
    private var recallMessageListener: OnRecallMessageResultListener? = null

    //???????????????
    private var chatRoomListener: ChatRoomListener? = null

    /**
     * ????????????
     */
    private var groupListener: GroupListener? = null

    /**
     * ???????????????????????????????????????
     */
    private var sendMsgEvent: OnAddMsgAttrsBeforeSendEvent? = null

    /**
     * ??????????????????????????????true
     */
    private var isNotFirstSend = false

    private fun initView() {
        chatMessageListLayout = findViewById(R.id.layout_chat_message)
        chatInputMenu = findViewById(R.id.layout_menu)
        voiceRecorder = findViewById(R.id.voice_recorder)
        presenter.attachView(this)
        menuHelper = EasePopupWindowHelper()
        clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    private fun initListener() {
        chatMessageListLayout!!.setOnMessageTouchListener(this)
        chatMessageListLayout!!.setMessageListItemClickListener(this)
        chatMessageListLayout!!.setOnChatErrorListener(this)
        chatInputMenu!!.setChatInputMenuListener(this)
        chatManager.addMessageListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        chatManager.removeMessageListener(this)
        if (chatRoomListener != null) {
            EMClient.getInstance().chatroomManager().removeChatRoomListener(chatRoomListener)
        }
        if (groupListener != null) {
            EMClient.getInstance().groupManager().removeGroupChangeListener(groupListener)
        }
        if (isChatRoomCon) {
            EMClient.getInstance().chatroomManager().leaveChatRoom(conversationId)
        }
        if (isGroupCon) {
            EaseAtMessageHelper.get().removeAtMeGroup(conversationId)
            EaseAtMessageHelper.get().cleanToAtUserList()
        }
    }

    /**
     * ?????????
     * @param username ??????id
     * @param chatType ?????????????????????????????????????????????
     */
    fun init(username: String?, chatType: Int) {
        init(LoadDataType.LOCAL, username, chatType)
    }

    /**
     * ?????????
     * @param loadDataType ??????????????????
     * @param conversationId ??????id????????????????????????id??????????????????id???????????????id
     * @param chatType ?????????????????????????????????????????????
     */
    fun init(loadDataType: LoadDataType?, conversationId: String?, chatType: Int) {
        this.conversationId = conversationId
        this.chatType = chatType
        chatMessageListLayout!!.init(loadDataType, this.conversationId, chatType)
        this.conversationId?.let { presenter.setupWithToUser(chatType, it) }
        if (isChatRoomCon) {
            chatRoomListener = ChatRoomListener()
            EMClient.getInstance().chatroomManager().addChatRoomChangeListener(chatRoomListener)
        } else if (isGroupCon) {
            EaseAtMessageHelper.get().removeAtMeGroup(conversationId)
            groupListener = GroupListener()
            EMClient.getInstance().groupManager().addGroupChangeListener(groupListener)
        }
    }

    /**
     * ?????????????????????????????????
     * @param toChatUsername
     * @param chatType
     */
    fun initHistoryModel(toChatUsername: String?, chatType: Int) {
        init(LoadDataType.HISTORY, toChatUsername, chatType)
    }

    fun loadDefaultData() {
        sendChannelAck()
        chatMessageListLayout!!.loadDefaultData()
    }

    fun loadData(msgId: String?, pageSize: Int) {
        sendChannelAck()
        chatMessageListLayout!!.loadData(pageSize, msgId)
    }

    fun loadData(msgId: String?) {
        sendChannelAck()
        chatMessageListLayout!!.loadData(msgId)
    }

    /**
     * ??????channel ack??????
     * (1)?????????1v1????????????????????????channel ack???????????????????????????[EMConversationListener.onConversationRead],
     * SDK??????????????????????????????????????????isAcked??????true.
     * (2)?????????????????????????????????????????????channel ack????????????SDK???????????????????????????????????????
     */
    private fun sendChannelAck() {
//        if(EaseIM.getInstance().getConfigsManager().enableSendChannelAck()) {
        if (true) {
            val conversation = EMClient.getInstance().chatManager().getConversation(conversationId)
            if (conversation == null || conversation.unreadMsgCount <= 0) {
                return
            }
            try {
                EMClient.getInstance().chatManager().ackConversationRead(conversationId)
            } catch (e: HyphenateException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * ??????????????????
     * @return
     */
    val isChatRoomCon: Boolean
        get() = EaseCommonUtils.getConversationType(chatType) == EMConversation.EMConversationType.ChatRoom

    /**
     * ???????????????
     * @return
     */
    val isGroupCon: Boolean
        get() = EaseCommonUtils.getConversationType(chatType) == EMConversation.EMConversationType.GroupChat
    override val inputContent: String
        get() = chatInputMenu?.primaryMenu?.editTextMessage?.text.toString().trim()

    override fun sendTextMessage(content: String?) {
        judgeContent(content) {
            presenter.sendTextMessage(content)
        }
    }

    /**
     * ????????????????????????????????????
     */
    private fun judgeContent(content: String?, canSend: () -> Unit) {
        if (content.isNullOrBlank()) {
            context.toast(context.getString(R.string.content_cant_be_empty))
            return
        }
        if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
            if (content.length > 20) {
                context.toast(context.getString(R.string.maximum_word_limit_exceeded))
                return
            }
        }
        canSend.invoke()
    }

    override fun sendTextMessage(content: String?, isNeedGroupAck: Boolean) {
        judgeContent(content) {
            presenter.sendTextMessage(content, isNeedGroupAck)
        }
    }

    override fun sendAtMessage(content: String?) {
        presenter.sendAtMessage(content)
    }

    override fun sendBigExpressionMessage(name: String?, identityCode: String?) {
        presenter.sendBigExpressionMessage(name, identityCode)
    }

    override fun sendVoiceMessage(filePath: String?, length: Int) {
        sendVoiceMessage(Uri.parse(filePath), length)
    }

    override fun sendVoiceMessage(filePath: Uri?, length: Int) {
        presenter.sendVoiceMessage(filePath, length)
    }

    override fun sendImageMessage(imageUri: Uri?) {
        presenter.sendImageMessage(imageUri)
    }

    override fun sendImageMessage(imageUri: Uri?, sendOriginalImage: Boolean) {
        presenter.sendImageMessage(imageUri, sendOriginalImage)
    }

    override fun sendLocationMessage(
        latitude: Double,
        longitude: Double,
        locationAddress: String?,
        buildingName: String?
    ) {
        presenter.sendLocationMessage(latitude, longitude, locationAddress, buildingName)
    }

    override fun sendVideoMessage(videoUri: Uri?, videoLength: Int) {
        presenter.sendVideoMessage(videoUri, videoLength)
    }

    override fun sendFileMessage(fileUri: Uri?) {
        presenter.sendFileMessage(fileUri)
    }

    override fun sendMessage(message: EMMessage?) {
        presenter.sendMessage(message)
    }

    override fun resendMessage(message: EMMessage?) {
        EMLog.i(TAG, "resendMessage")
        presenter.resendMessage(message)
    }

    override fun deleteMessage(message: EMMessage?) {
        chatMessageListLayout?.currentConversation?.removeMessage(message!!.msgId)
        chatMessageListLayout?.removeMessage(message)
    }

    override fun recallMessage(message: EMMessage?) {
        presenter.recallMessage(message)
    }

    override fun addMessageAttributes(message: EMMessage?) {
        presenter.addMessageAttributes(message)
    }

    override fun setOnChatLayoutListener(listener: OnChatLayoutListener?) {
        this.listener = listener
    }

    override fun setOnChatRecordTouchListener(recordTouchListener: OnChatRecordTouchListener?) {
        this.recordTouchListener = recordTouchListener
    }

    override fun setOnRecallMessageResultListener(listener: OnRecallMessageResultListener?) {
        recallMessageListener = listener
    }

    override fun setOnAddMsgAttrsBeforeSendEvent(sendMsgEvent: OnAddMsgAttrsBeforeSendEvent?) {
        this.sendMsgEvent = sendMsgEvent
    }

    /**
     * ?????????????????????????????????????????????????????????cmd?????????????????????10s???????????????
     * ????????????????????????10s???????????????????????????
     * @param s
     * @param start
     * @param before
     * @param count
     */
    override fun onTyping(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (listener != null) {
            listener!!.onTextChanged(s, start, before, count)
        }
    }

    override fun onSendMessage(content: String?) {
        judgeContent(content) {
            chatInputMenu?.primaryMenu?.editTextMessage?.setText("")
            presenter.sendTextMessage(content)
        }
    }

    override fun onExpressionClicked(emojicon: Any?) {
        if (emojicon is EaseEmojicon) {
            presenter.sendBigExpressionMessage(emojicon.name, emojicon.identityCode)
        }
    }

    override fun onPressToSpeakBtnTouch(v: View?, event: MotionEvent?): Boolean {
        if (recordTouchListener != null) {
            val onRecordTouch = recordTouchListener!!.onRecordTouch(v, event)
            if (!onRecordTouch) {
                return false
            }
        }
        return voiceRecorder!!.onPressToSpeakBtnTouch(
            v,
            event
        ) { filePath: String?, length: Int -> this.sendVoiceMessage(filePath, length) }
    }

    override fun onChatExtendMenuItemClick(itemId: Int, view: View?) {
        if (listener != null) {
            listener!!.onChatExtendMenuItemClick(view, itemId)
        }
    }

    private val chatManager: EMChatManager
        private get() = EMClient.getInstance().chatManager()

    //????????????
    override fun onMessageReceived(messages: List<EMMessage>) {
        var refresh = false
        for (message in messages) {
            var username: String? = null
            sendGroupReadAck(message)
            sendReadAck(message)
            // group message
            username =
                if (message.chatType == EMMessage.ChatType.GroupChat || message.chatType == EMMessage.ChatType.ChatRoom) {
                    message.to
                } else {
                    // single chat message
                    message.from
                }
            // if the message is for current conversation
            if (username == conversationId || message.to == conversationId || message.conversationId() == conversationId) {
                refresh = true
            }
        }
        if (refresh) {
            chatMessageListLayout!!.refreshToLatest()
        }
    }

    /**
     * ????????????????????????
     * @param message
     */
    private fun sendGroupReadAck(message: EMMessage) {
        if (message.isNeedGroupAck && message.isUnread) {
            try {
                EMClient.getInstance().chatManager()
                    .ackGroupMessageRead(message.to, message.msgId, "")
            } catch (e: HyphenateException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * ????????????????????????
     * @param message
     */
    private fun sendReadAck(message: EMMessage) {
        if (EaseIM.getInstance().configsManager.enableSendChannelAck()) {
            //?????????????????????????????????read ack??????????????????
            if (message.direct() == EMMessage.Direct.RECEIVE && !message.isAcked
                && message.chatType == EMMessage.ChatType.Chat
            ) {
                val type = message.type
                //????????????????????????????????????????????????
                if (type == EMMessage.Type.VIDEO || type == EMMessage.Type.VOICE || type == EMMessage.Type.FILE) {
                    return
                }
                try {
                    EMClient.getInstance().chatManager().ackMessageRead(message.from, message.msgId)
                } catch (e: HyphenateException) {
                    e.printStackTrace()
                }
            }
        }
    }


    /**
     * ?????????????????????????????????????????????
     * ?????????????????????????????????????????????????????????5s????????????????????????????????????????????????????????????
     * @param messages
     */
    //??????????????????
    override fun onCmdMessageReceived(messages: List<EMMessage>) {
        // ???????????????????????????????????????
        for (msg in messages) {
            val body = msg.body as EMCmdMessageBody
            EMLog.i(TAG, "Receive cmd message: " + body.action() + " - " + body.isDeliverOnlineOnly)
        }
    }

    //??????????????????
    override fun onMessageRead(messages: List<EMMessage>) {
        refreshMessages(messages)
    }

    //?????????????????????
    override fun onMessageDelivered(messages: List<EMMessage>) {
        refreshMessages(messages)
    }

    //???????????????
    override fun onMessageRecalled(messages: List<EMMessage>) {
        if (chatMessageListLayout != null) {
            chatMessageListLayout!!.refreshMessages()
        }
    }

    //??????????????????
    override fun onMessageChanged(message: EMMessage, change: Any) {
        refreshMessage(message)
    }

    private fun refreshMessage(message: EMMessage?) {
        if (chatMessageListLayout != null) {
            chatMessageListLayout!!.refreshMessage(message)
        }
    }

    private fun refreshMessages(messages: List<EMMessage>) {
        for (msg in messages) {
            refreshMessage(msg)
        }
    }

    override fun context(): Context {
        return context
    }

    override fun createThumbFileFail(message: String?) {
        if (listener != null) {
            listener!!.onChatError(-1, message)
        }
    }

    override fun addMsgAttrBeforeSend(message: EMMessage?) {
        //???????????????????????????????????????????????????ext
        if (sendMsgEvent != null) {
            sendMsgEvent!!.addMsgAttrsBeforeSend(message)
        }
    }

    override fun sendMessageFail(message: String?) {
        if (listener != null) {
            listener!!.onChatError(-1, message)
        }
    }

    override fun sendMessageFinish(message: EMMessage?) {
        if (chatMessageListLayout != null) {
            chatMessageListLayout!!.refreshToLatest()
        }
    }

    override fun deleteLocalMessageSuccess(message: EMMessage?) {
        chatMessageListLayout!!.removeMessage(message)
    }

    override fun recallMessageFinish(message: EMMessage?) {
        if (recallMessageListener != null) {
            recallMessageListener!!.recallSuccess(message)
        }
        chatMessageListLayout!!.refreshMessages()
    }

    override fun recallMessageFail(code: Int, message: String?) {
        if (recallMessageListener != null) {
            recallMessageListener!!.recallFail(code, message)
        }
        if (listener != null) {
            listener!!.onChatError(code, message)
        }
    }

    override fun onPresenterMessageSuccess(message: EMMessage?) {
        EMLog.i(TAG, "send message onPresenterMessageSuccess")
        if (listener != null) {
            listener!!.onChatSuccess(message)
        }
    }

    override fun onPresenterMessageError(message: EMMessage?, code: Int, error: String?) {
        EMLog.i(TAG, "send message onPresenterMessageError code: $code error: $error")
        //????????????
        refreshMessage(message)
        if (listener != null) {
            listener!!.onChatError(code, error)
        }
    }

    override fun onPresenterMessageInProgress(message: EMMessage?, progress: Int) {
        EMLog.i(TAG, "send message onPresenterMessageInProgress")
    }

    override fun onTouchItemOutside(v: View?, position: Int) {
        chatInputMenu!!.hideSoftKeyboard()
        chatInputMenu!!.showExtendMenu(false)
    }

    override fun onViewDragging() {
        chatInputMenu!!.hideSoftKeyboard()
        chatInputMenu!!.showExtendMenu(false)
    }

    override fun onBubbleClick(message: EMMessage?): Boolean {
        return if (listener != null) {
            listener!!.onBubbleClick(message)
        } else false
    }

    override fun onResendClick(message: EMMessage?): Boolean {
        EMLog.i(TAG, "onResendClick")
        EaseAlertDialog(
            context,
            R.string.resend,
            R.string.confirm_resend,
            null,
            AlertDialogUser { confirmed, bundle ->
                if (!confirmed) {
                    return@AlertDialogUser
                }
                resendMessage(message)
            },
            true
        ).show()
        return true
    }

    override fun onBubbleLongClick(v: View?, message: EMMessage?): Boolean {
        if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
            return false
        }
        if (showDefaultMenu) {
            showDefaultMenu(v, message)
            return if (listener != null) {
                listener!!.onBubbleLongClick(v, message)
            } else true
        }
        return if (listener != null) {
            listener!!.onBubbleLongClick(v, message)
        } else false
    }

    override fun onUserAvatarClick(username: String?) {
        if (listener != null) {
            listener!!.onUserAvatarClick(username)
        }
    }

    override fun onUserAvatarLongClick(username: String?) {
        EMLog.i(TAG, "onUserAvatarLongClick")
        inputAtUsername(username, true)
        if (listener != null) {
            listener!!.onUserAvatarLongClick(username)
        }
    }

    override fun onMessageCreate(message: EMMessage?) {
        //???????????????????????????????????????
        // send message
        EMLog.i(TAG, "onMessageCreate")
        //sendMessage(message);
    }

    override fun onMessageSuccess(message: EMMessage?) {
        EMLog.i(TAG, "send message onMessageSuccess")
        if (listener != null) {
            listener!!.onChatSuccess(message)
        }
    }

    override fun onMessageError(message: EMMessage?, code: Int, error: String?) {
        if (listener != null) {
            listener!!.onChatError(code, error)
        }
    }

    override fun onMessageInProgress(message: EMMessage?, progress: Int) {
        EMLog.i(TAG, "send message on progress: $progress")
    }

    override fun onChatError(code: Int, errorMsg: String?) {
        if (listener != null) {
            listener!!.onChatError(code, errorMsg)
        }
    }

    override fun showItemDefaultMenu(showDefault: Boolean) {
        showDefaultMenu = showDefault
    }

    override fun clearMenu() {
        menuHelper!!.clear()
    }

    override fun addItemMenu(item: MenuItemBean) {
        menuHelper!!.addItemMenu(item)
    }

    override fun addItemMenu(groupId: Int, itemId: Int, order: Int, title: String) {
        menuHelper!!.addItemMenu(groupId, itemId, order, title)
    }

    override fun findItem(id: Int): MenuItemBean {
        return menuHelper!!.findItem(id)
    }

    override fun findItemVisible(id: Int, visible: Boolean) {
        menuHelper!!.findItemVisible(id, visible)
    }

    override fun getMenuHelper(): EasePopupWindowHelper {
        return menuHelper!!
    }

    override fun setOnPopupWindowItemClickListener(listener: OnMenuChangeListener) {
        menuChangeListener = listener
    }

    /**
     * input @
     * only for group chat
     * @param username
     */
    fun inputAtUsername(username: String?, autoAddAtSymbol: Boolean) {
        var username = username
        if (EMClient.getInstance().currentUser == username ||
            !chatMessageListLayout!!.isGroupChat
        ) {
            return
        }
        EaseAtMessageHelper.get().addAtUser(username)
        val user = EaseUserUtils.getUserInfo(username)
        if (user != null) {
            username = user.getNickname()
        }
        val editText: EditText? = chatInputMenu?.primaryMenu?.editTextMessage
        if (autoAddAtSymbol) insertText(editText, AT_PREFIX + username + AT_SUFFIX) else insertText(
            editText,
            username + AT_SUFFIX
        )
    }

    /**
     * insert text to EditText
     * @param edit
     * @param text
     */
    private fun insertText(edit: EditText?, text: String) {
        if (edit == null) {
            return
        }
        if (edit.isFocused) {
            edit.text.insert(edit.selectionStart, text)
        } else {
            edit.text.insert(edit.text.length - 1, text)
        }
    }

    private fun showDefaultMenu(v: View?, message: EMMessage?) {
        menuHelper!!.initMenu(context)
        menuHelper!!.setDefaultMenus()
        menuHelper!!.setOutsideTouchable(true)
        setMenuByMsgType(message)
        if (menuChangeListener != null) {
            menuChangeListener!!.onPreMenu(menuHelper, message)
        }
        menuHelper!!.setOnPopupMenuItemClickListener(OnPopupWindowItemClickListener { item ->
            if (menuChangeListener != null && menuChangeListener!!.onMenuItemClick(item, message)) {
                return@OnPopupWindowItemClickListener true
            }
            if (showDefaultMenu) {
                val itemId = item.itemId
                if (itemId == R.id.action_chat_copy) {
                    clipboard!!.setPrimaryClip(
                        ClipData.newPlainText(
                            null,
                            (message!!.body as EMTextMessageBody).message
                        )
                    )
                    EMLog.i(TAG, "copy success")
                } else if (itemId == R.id.action_chat_delete) {
                    deleteMessage(message)
                    EMLog.i(
                        TAG,
                        "currentMsgId = " + message!!.msgId + " timestamp = " + message.msgTime
                    )
                } else if (itemId == R.id.action_chat_recall) {
                    recallMessage(message)
                }
                return@OnPopupWindowItemClickListener true
            }
            false
        })
        menuHelper!!.setOnPopupMenuDismissListener { menu ->
            if (menuChangeListener != null) {
                menuChangeListener!!.onDismiss(menu)
            }
        }
        menuHelper!!.show(this, v)
    }

    private fun setMenuByMsgType(message: EMMessage?) {
        val type = message!!.type
        menuHelper?.findItemVisible(R.id.action_chat_copy, false)
        menuHelper?.findItemVisible(R.id.action_chat_recall, false)
        menuHelper?.findItem(R.id.action_chat_delete)?.title = context.getString(R.string.action_delete)
        when (type) {
            EMMessage.Type.TXT -> {
                menuHelper?.findItemVisible(R.id.action_chat_copy, true)
                menuHelper?.findItemVisible(R.id.action_chat_recall, true)
            }
            EMMessage.Type.LOCATION, EMMessage.Type.FILE, EMMessage.Type.IMAGE -> menuHelper!!.findItemVisible(
                R.id.action_chat_recall, true
            )
            EMMessage.Type.VOICE -> {
                menuHelper?.findItem(R.id.action_chat_delete)?.title =
                    context.getString(R.string.delete_voice)
                menuHelper?.findItemVisible(R.id.action_chat_recall, true)
            }
            EMMessage.Type.VIDEO -> {
                menuHelper?.findItem(R.id.action_chat_delete)?.title = context.getString(R.string.delete_video)
                menuHelper?.findItemVisible(R.id.action_chat_recall, true)
            }
        }
        if (message.direct() == EMMessage.Direct.RECEIVE) {
            menuHelper?.findItemVisible(R.id.action_chat_recall, false)
        }
    }

    private inner class ChatRoomListener : EaseChatRoomListener() {
        override fun onChatRoomDestroyed(roomId: String, roomName: String) {
            finishCurrent()
        }

        override fun onRemovedFromChatRoom(
            reason: Int,
            roomId: String,
            roomName: String,
            participant: String
        ) {
            if (!TextUtils.equals(roomId, conversationId)) {
                return
            }
            if (reason == EMAChatRoomManagerListener.BE_KICKED) {
                "ChatRoomListener -fun onRemovedFromChatRoom reason: BE_KICKED".e()
            }
        }

        override fun onMemberJoined(roomId: String, participant: String) {}
        override fun onMemberExited(roomId: String, roomName: String, participant: String) {}
    }

    /**
     * group listener
     */
    private inner class GroupListener : EaseGroupListener() {
        override fun onUserRemoved(groupId: String, groupName: String) {
            finishCurrent()
        }

        override fun onGroupDestroyed(groupId: String, groupName: String) {
            finishCurrent()
        }
    }

    /**
     * finish current activity
     */
    private fun finishCurrent() {
        if (context is Activity) {
            (context as Activity).finish()
        }
    }

    companion object {
        private val TAG = EaseChatLayout::class.java.simpleName
        private const val MSG_TYPING_HEARTBEAT = 0
        private const val MSG_TYPING_END = 1
        private const val MSG_OTHER_TYPING_END = 2
        const val ACTION_TYPING_BEGIN = "TypingBegin"
        const val ACTION_TYPING_END = "TypingEnd"
        protected const val TYPING_SHOW_TIME = 10000
        protected const val OTHER_TYPING_SHOW_TIME = 5000
        const val AT_PREFIX = "@"
        const val AT_SUFFIX = " "
    }

    init {
        presenter = EaseHandleMessagePresenterImpl()
        if (context is AppCompatActivity) {
            context.lifecycle.addObserver(presenter)
        }
        LayoutInflater.from(context).inflate(R.layout.ease_layout_chat, this)
        initView()
        initListener()
    }
}