package com.kotlin.chat_component.inner.modules.chat

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hyphenate.chat.EMChatRoom
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMConversation.EMConversationType
import com.hyphenate.chat.EMConversation.EMSearchDirection
import com.hyphenate.chat.EMMessage
import com.kotlin.chat_component.R
import com.kotlin.chat_component.inner.adapter.EaseMessageAdapter
import com.kotlin.chat_component.inner.interfaces.MessageListItemClickListener
import com.kotlin.chat_component.inner.interfaces.OnItemClickListener
import com.kotlin.chat_component.inner.manager.EaseMessageTypeSetManager
import com.kotlin.chat_component.inner.manager.EaseThreadManager
import com.kotlin.chat_component.inner.modules.chat.interfaces.IChatMessageItemSet
import com.kotlin.chat_component.inner.modules.chat.interfaces.IChatMessageListLayout
import com.kotlin.chat_component.inner.modules.chat.interfaces.IRecyclerViewHandle
import com.kotlin.chat_component.inner.modules.chat.model.EaseChatItemStyleHelper
import com.kotlin.chat_component.inner.modules.chat.presenter.EaseChatMessagePresenter
import com.kotlin.chat_component.inner.modules.chat.presenter.EaseChatMessagePresenterImpl
import com.kotlin.chat_component.inner.modules.chat.presenter.IChatMessageListView
import com.kotlin.chat_component.inner.utils.EaseCommonUtils

class EaseChatMessageListLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), IChatMessageListView, IRecyclerViewHandle,
    IChatMessageItemSet, IChatMessageListLayout {

    private var presenter: EaseChatMessagePresenter?
    override var messageAdapter: EaseMessageAdapter? = null
        private set
    private var baseAdapter: ConcatAdapter? = null

    /**
     * ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     */
    private var loadDataType: LoadDataType? = null

    /**
     * ??????id??????????????????????????????????????????????????????
     */
    private var msgId: String? = null
    private var pageSize = DEFAULT_PAGE_SIZE
    private var rvList: RecyclerView? = null
    private var srlRefresh: SwipeRefreshLayout? = null
    private var layoutManager: LinearLayoutManager? = null
    private var conversation: EMConversation? = null

    /**
     * ????????????????????????????????????????????????
     */
    private var conType: EMConversationType? = null

    /**
     * ??????????????????id
     */
    private var username: String? = null
    private var canUseRefresh = true
    private var loadMoreStatus: LoadMoreStatus? = null
    private var messageTouchListener: OnMessageTouchListener? = null
    private var errorListener: OnChatErrorListener? = null

    /**
     * ????????????????????????
     */
    private var recyclerViewLastHeight = 0

    /**
     * ?????????????????????????????????
     */
    private var messageListItemClickListener: MessageListItemClickListener? = null
    private val chatSetHelper: EaseChatItemStyleHelper

    companion object {
        private const val DEFAULT_PAGE_SIZE = 10
        private val TAG = EaseChatMessageListLayout::class.java.simpleName

        /**
         * ?????????????????????
         * @param recyclerView
         * @return
         */
        fun isVisibleBottom(recyclerView: RecyclerView): Boolean {
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
            //????????????????????????????????????position
            val lastVisibleItemPosition = layoutManager!!.findLastVisibleItemPosition()
            //????????????????????????????????????
            val visibleItemCount = layoutManager.childCount
            //??????RecyclerView?????????????????????
            val totalItemCount = layoutManager.itemCount
            //RecyclerView???????????????
            val state = recyclerView.scrollState
            return visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1
                    && state == RecyclerView.SCROLL_STATE_IDLE
        }

        /**
         * ??????????????????????????????????????????position
         */
        fun lastVisibleItemPosition(recyclerView: RecyclerView): Int {
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
            return layoutManager!!.findLastVisibleItemPosition()
        }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.ease_chat_message_list, this)
        EaseChatItemStyleHelper.getInstance().clear()
        chatSetHelper = EaseChatItemStyleHelper.getInstance()
        presenter = EaseChatMessagePresenterImpl()
        if (context is AppCompatActivity) {
            presenter?.let {
                context.lifecycle.addObserver(it)
            }
        }
        initAttrs(context, attrs)
        initViews()
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.EaseChatMessageListLayout)
            val textSize = a.getDimension(
                R.styleable.EaseChatMessageListLayout_ease_chat_item_text_size, 0f
            )
            chatSetHelper.setTextSize(textSize.toInt())
            val textColorRes =
                a.getResourceId(R.styleable.EaseChatMessageListLayout_ease_chat_item_text_color, -1)
            val textColor: Int
            textColor = if (textColorRes != -1) {
                ContextCompat.getColor(context, textColorRes)
            } else {
                a.getColor(
                    R.styleable.EaseChatMessageListLayout_ease_chat_item_text_color,
                    0
                )
            }
            chatSetHelper.setTextColor(textColor)
            val itemMinHeight =
                a.getDimension(R.styleable.EaseChatMessageListLayout_ease_chat_item_min_height, 0f)
            chatSetHelper.setItemMinHeight(itemMinHeight.toInt())
            val timeTextSize = a.getDimension(
                R.styleable.EaseChatMessageListLayout_ease_chat_item_time_text_size,
                0f
            )
            chatSetHelper.setTimeTextSize(timeTextSize.toInt())
            val timeTextColorRes = a.getResourceId(
                R.styleable.EaseChatMessageListLayout_ease_chat_item_time_text_color,
                -1
            )
            val timeTextColor: Int
            timeTextColor = if (timeTextColorRes != -1) {
                ContextCompat.getColor(context, textColorRes)
            } else {
                a.getColor(
                    R.styleable.EaseChatMessageListLayout_ease_chat_item_time_text_color,
                    0
                )
            }
            chatSetHelper.setTimeTextColor(timeTextColor)
            chatSetHelper.setTimeBgDrawable(a.getDrawable(R.styleable.EaseChatMessageListLayout_ease_chat_item_time_background))
            val avatarDefaultDrawable =
                a.getDrawable(R.styleable.EaseChatMessageListLayout_ease_chat_item_avatar_default_src)
            val shapeType = a.getInteger(
                R.styleable.EaseChatMessageListLayout_ease_chat_item_avatar_shape_type,
                0
            )
            chatSetHelper.setAvatarDefaultSrc(avatarDefaultDrawable)
            chatSetHelper.setShapeType(shapeType)
            chatSetHelper.setReceiverBgDrawable(a.getDrawable(R.styleable.EaseChatMessageListLayout_ease_chat_item_receiver_background))
            chatSetHelper.setSenderBgDrawable(a.getDrawable(R.styleable.EaseChatMessageListLayout_ease_chat_item_sender_background))

            chatSetHelper.setShowNickname(
                a.getBoolean(
                    R.styleable.EaseChatMessageListLayout_ease_chat_item_show_nickname,
                    false
                )
            )
            chatSetHelper.setItemShowType(
                a.getInteger(
                    R.styleable.EaseChatMessageListLayout_ease_chat_item_show_type,
                    0
                )
            )
            a.recycle()
        }
    }

    private fun initViews() {
        presenter?.attachView(this)
        rvList = findViewById(R.id.message_list)
        srlRefresh = findViewById(R.id.srl_refresh)
        srlRefresh?.isEnabled = canUseRefresh
        layoutManager = LinearLayoutManager(context)
        rvList?.layoutManager = layoutManager
        baseAdapter = ConcatAdapter()
        messageAdapter = EaseMessageAdapter()
        baseAdapter?.addAdapter(messageAdapter!!)
        rvList?.adapter = baseAdapter
        registerChatType()
        initListener()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (conversation != null) {
            conversation?.markAllMessagesAsRead()
        }
        EaseChatItemStyleHelper.getInstance().clear()
        EaseMessageTypeSetManager.getInstance().release()
    }

    private fun registerChatType() {
        try {
            EaseMessageTypeSetManager.getInstance().registerMessageType(messageAdapter)
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    fun init(loadDataType: LoadDataType?, username: String?, chatType: Int) {
        this.username = username
        this.loadDataType = loadDataType
        conType = EaseCommonUtils.getConversationType(chatType)
        conversation = EMClient.getInstance().chatManager().getConversation(username, conType, true)
        if (conType == EMConversationType.ChatRoom) {
            conversation?.clearAllMessages()
        }
        presenter?.setupWithConversation(conversation)
    }

    fun init(username: String?, chatType: Int) {
        init(LoadDataType.LOCAL, username, chatType)
    }

    fun loadDefaultData() {
        loadData(pageSize, null)
    }

    fun loadData(msgId: String?) {
        loadData(pageSize, msgId)
    }

    fun loadData(pageSize: Int, msgId: String?) {
        this.pageSize = pageSize
        this.msgId = msgId
        checkConType()
    }

    private fun checkConType() {
        if (isChatRoomCon) {
            presenter?.joinChatRoom(username)
        } else {
            loadData()
        }
    }

    private fun loadData() {
        if (!isSingleChat) {
            chatSetHelper.setShowNickname(true)
        }
        conversation?.markAllMessagesAsRead()
        when (loadDataType) {
            LoadDataType.ROAM -> {
                presenter?.loadServerMessages(pageSize)
            }
            LoadDataType.HISTORY -> {
                presenter?.loadMoreLocalHistoryMessages(msgId, pageSize, EMSearchDirection.DOWN)
            }
            else -> {
                presenter?.loadLocalMessages(pageSize)
            }
        }
    }

    /**
     * ?????????????????????????????????????????????????????????
     */
    fun loadMorePreviousData() {
        val msgId = listFirstMessageId
        if (loadDataType == LoadDataType.ROAM) {
            presenter?.loadMoreServerMessages(msgId, pageSize)
        } else if (loadDataType == LoadDataType.HISTORY) {
            presenter?.loadMoreLocalHistoryMessages(msgId, pageSize, EMSearchDirection.UP)
        } else {
            presenter?.loadMoreLocalMessages(msgId, pageSize)
        }
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????
     */
    fun loadMoreHistoryData() {
        val msgId = listLastMessageId
        if (loadDataType == LoadDataType.HISTORY) {
            loadMoreStatus = LoadMoreStatus.HAS_MORE
            presenter?.loadMoreLocalHistoryMessages(msgId, pageSize, EMSearchDirection.DOWN)
        }
    }

    /**
     * ???????????????????????????????????????id
     * @return
     */
    private val listFirstMessageId: String?
        private get() {
            var message: EMMessage? = null
            messageAdapter?.data?.let {
                if (it.isNotEmpty()) {
                    message = messageAdapter?.data?.get(0)
                }
            }

            return message?.msgId
        }

    /**
     * ???????????????????????????????????????id
     * @return
     */
    private val listLastMessageId: String?
        private get() {
            var message: EMMessage? = null
            try {
                message = messageAdapter!!.data[messageAdapter!!.data.size - 1]
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return message?.msgId
        }
    val isChatRoomCon: Boolean
        get() = conType == EMConversationType.ChatRoom
    val isGroupChat: Boolean
        get() = conType == EMConversationType.GroupChat
    private val isSingleChat: Boolean
        private get() = conType == EMConversationType.Chat

    private fun initListener() {
        srlRefresh!!.setOnRefreshListener { loadMorePreviousData() }
        rvList!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                //????????????????????? ????????????????????????
                //???????????????????????????????????? ????????????????????????????????????
                if (dy > 0) {
                    seekToLatest()
                } else {
                    super.onScrolled(recyclerView, dx, dy)
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //???????????????????????????????????????
                    if (loadDataType == LoadDataType.HISTORY && loadMoreStatus == LoadMoreStatus.HAS_MORE && layoutManager!!.findLastVisibleItemPosition() != 0 && layoutManager!!.findLastVisibleItemPosition() == layoutManager!!.itemCount - 1) {
                        //????????????
                        loadMoreHistoryData()
                    }
                } else {
                    //if recyclerView not idle should hide keyboard
                    if (messageTouchListener != null) {
                        messageTouchListener!!.onViewDragging()
                    }
                }
            }
        })

        //????????????RecyclerView????????????????????????????????????
        rvList!!.viewTreeObserver.addOnGlobalLayoutListener {
            val height = rvList!!.height
            if (height == 0) {
                return@addOnGlobalLayoutListener
            }
            if (recyclerViewLastHeight == 0) {
                recyclerViewLastHeight = height
            }
            if (recyclerViewLastHeight != height) {
                //RecyclerView?????????????????????????????????
                if (messageAdapter!!.data != null && !messageAdapter!!.data.isEmpty()) {
                    post {
                        smoothSeekToPosition(messageAdapter!!.data.size - 1)
                    }
                }
            }
            recyclerViewLastHeight = height
        }
        messageAdapter!!.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                if (messageTouchListener != null) {
                    messageTouchListener!!.onTouchItemOutside(view, position)
                }
            }
        })
        messageAdapter!!.setListItemClickListener(object : MessageListItemClickListener {
            override fun onBubbleClick(message: EMMessage?): Boolean {
                return if (messageListItemClickListener != null) {
                    messageListItemClickListener!!.onBubbleClick(message)
                } else false
            }

            override fun onResendClick(message: EMMessage?): Boolean {
                return if (messageListItemClickListener != null) {
                    messageListItemClickListener!!.onResendClick(message)
                } else false
            }

            override fun onBubbleLongClick(v: View?, message: EMMessage?): Boolean {
                return if (messageListItemClickListener != null) {
                    messageListItemClickListener!!.onBubbleLongClick(v, message)
                } else false
            }

            override fun onUserAvatarClick(username: String?) {
                if (messageListItemClickListener != null) {
                    messageListItemClickListener!!.onUserAvatarClick(username)
                }
            }

            override fun onUserAvatarLongClick(username: String?) {
                if (messageListItemClickListener != null) {
                    messageListItemClickListener!!.onUserAvatarLongClick(username)
                }
            }

            override fun onMessageCreate(message: EMMessage?) {
                if (messageListItemClickListener != null) {
                    messageListItemClickListener!!.onMessageCreate(message)
                }
            }

            override fun onMessageSuccess(message: EMMessage?) {
                if (messageListItemClickListener != null) {
                    messageListItemClickListener!!.onMessageSuccess(message)
                }
            }

            override fun onMessageError(message: EMMessage?, code: Int, error: String?) {
                if (messageListItemClickListener != null) {
                    messageListItemClickListener!!.onMessageError(message, code, error)
                }
            }

            override fun onMessageInProgress(message: EMMessage?, progress: Int) {
                if (messageListItemClickListener != null) {
                    messageListItemClickListener!!.onMessageInProgress(message, progress)
                }
            }
        })
    }

    /**
     * ??????????????????
     */
    private fun finishRefresh() {
        if (presenter?.isActive == true) {
            runOnUi {
                if (srlRefresh != null) {
                    srlRefresh!!.isRefreshing = false
                }
            }
        }
    }

    private fun notifyDataSetChanged() {
        messageAdapter!!.notifyDataSetChanged()
    }

    /**
     * ????????????
     * @param data
     */
    fun setData(data: List<EMMessage?>) {
        messageAdapter!!.data = data
    }

    /**
     * ????????????
     * @param data
     */
    fun addData(data: List<EMMessage?>) {
        messageAdapter!!.addData(data)
    }

    override fun context(): Context {
        return context
    }

    override val currentConversation: EMConversation?
        get() = conversation

    override fun joinChatRoomSuccess(value: EMChatRoom?) {
        loadData()
    }

    override fun joinChatRoomFail(error: Int, errorMsg: String?) {
        if (presenter?.isActive == true) {
            runOnUi {
                if (errorListener != null) {
                    errorListener!!.onChatError(error, errorMsg)
                }
            }
        }
    }

    override fun loadMsgFail(error: Int, message: String?) {
        finishRefresh()
        if (errorListener != null) {
            errorListener!!.onChatError(error, message)
        }
    }

    override fun loadLocalMsgSuccess(data: List<EMMessage?>?) {
        refreshToLatest()
    }

    override fun loadNoLocalMsg() {}
    override fun loadMoreLocalMsgSuccess(data: List<EMMessage?>?) {
        finishRefresh()
        presenter?.refreshCurrentConversation()
        post { data?.size?.minus(1)?.let { smoothSeekToPosition(it) } }
    }

    override fun loadNoMoreLocalMsg() {
        finishRefresh()
    }

    override fun loadMoreLocalHistoryMsgSuccess(
        data: List<EMMessage?>?,
        direction: EMSearchDirection?
    ) {
        if (direction == EMSearchDirection.UP) {
            finishRefresh()
            messageAdapter!!.addData(0, data)
        } else {
            messageAdapter!!.addData(data)
            loadMoreStatus = if (data?.size!! >= pageSize) {
                LoadMoreStatus.HAS_MORE
            } else {
                LoadMoreStatus.NO_MORE_DATA
            }
        }
    }

    override fun loadNoMoreLocalHistoryMsg() {
        finishRefresh()
    }

    override fun loadServerMsgSuccess(data: List<EMMessage?>?) {
        presenter?.refreshToLatest()
    }

    override fun loadMoreServerMsgSuccess(data: List<EMMessage?>?) {
        finishRefresh()
        presenter?.refreshCurrentConversation()
        post { data?.size?.minus(1)?.let { smoothSeekToPosition(it) } }
    }

    override fun refreshCurrentConSuccess(data: List<EMMessage?>?, toLatest: Boolean) {
        messageAdapter!!.data = data
        if (conType == EMConversationType.ChatRoom) {
            //???????????????????????????????????????????????????????????????position = itemCount-2??????????????????
            if (toLatest && rvList?.let { lastVisibleItemPosition(it) } == rvList?.adapter?.itemCount?.minus(
                    2
                ) ?: 0) {
                data?.size?.minus(1)?.let { seekToPosition(it) }
            }
        } else {
            if (toLatest) {
                seekToPosition(data!!.size - 1)
            }
        }
    }

    override fun canUseDefaultRefresh(canUseRefresh: Boolean) {
        this.canUseRefresh = canUseRefresh
        srlRefresh!!.isEnabled = canUseRefresh
    }

    override fun refreshMessages() {
        presenter?.refreshCurrentConversation()
    }

    override fun refreshToLatest() {
        presenter?.refreshToLatest()
    }

    override fun refreshMessage(message: EMMessage?) {
        val position = messageAdapter?.data?.lastIndexOf(message)
        if (position != -1) {
            runOnUi {
                if (position != null) {
                    messageAdapter?.notifyItemChanged(position)
                }
            }
        }
    }

    override fun removeMessage(message: EMMessage?) {
        if (message == null || messageAdapter!!.data == null) {
            return
        }
        conversation!!.removeMessage(message.msgId)
        runOnUi {
            if (presenter?.isActive == true) {
                val messages = messageAdapter!!.data
                val position = messages.lastIndexOf(message)
                if (position != -1) {
                    //????????????????????????????????????
                    messages.removeAt(position)
                    //???????????????????????????
                    messageAdapter!!.notifyItemRemoved(position)
                    //???????????????????????????
                    messageAdapter!!.notifyItemChanged(position)
                }
            }
        }
    }

    override fun moveToPosition(position: Int) {
        seekToPosition(position)
    }

    override fun lastMsgScrollToBottom(message: EMMessage?) {
    }

    override fun showNickname(showNickname: Boolean) {
        chatSetHelper.setShowNickname(showNickname)
        notifyDataSetChanged()
    }

    override fun setItemSenderBackground(bgDrawable: Drawable?) {
        chatSetHelper.setSenderBgDrawable(bgDrawable)
        notifyDataSetChanged()
    }

    override fun setItemReceiverBackground(bgDrawable: Drawable?) {
        chatSetHelper.setReceiverBgDrawable(bgDrawable)
        notifyDataSetChanged()
    }

    override fun setItemTextSize(textSize: Int) {
        chatSetHelper.setTextSize(textSize)
        notifyDataSetChanged()
    }

    override fun setItemTextColor(textColor: Int) {
        chatSetHelper.setTextColor(textColor)
        notifyDataSetChanged()
    }

    override fun setTimeTextSize(textSize: Int) {
        chatSetHelper.setTimeTextSize(textSize)
        notifyDataSetChanged()
    }

    override fun setTimeTextColor(textColor: Int) {
        chatSetHelper.setTimeTextColor(textColor)
        notifyDataSetChanged()
    }

    override fun setTimeBackground(bgDrawable: Drawable?) {
        chatSetHelper.setTimeBgDrawable(bgDrawable)
        notifyDataSetChanged()
    }

    override fun setItemShowType(type: ShowType?) {
        if (!isSingleChat) {
            chatSetHelper.setItemShowType(type!!.ordinal)
            notifyDataSetChanged()
        }
    }

    override fun setAvatarDefaultSrc(src: Drawable?) {
        chatSetHelper.setAvatarDefaultSrc(src)
        notifyDataSetChanged()
    }

    override fun setAvatarShapeType(shapeType: Int) {
        chatSetHelper.setShapeType(shapeType)
        notifyDataSetChanged()
    }

    override fun addHeaderAdapter(adapter: RecyclerView.Adapter<*>?) {
        baseAdapter!!.addAdapter(0, adapter!!)
    }

    override fun addFooterAdapter(adapter: RecyclerView.Adapter<*>?) {
        baseAdapter!!.addAdapter(adapter!!)
    }

    override fun removeAdapter(adapter: RecyclerView.Adapter<*>?) {
        baseAdapter!!.removeAdapter(adapter!!)
    }

    override fun addRVItemDecoration(decor: RecyclerView.ItemDecoration) {
        rvList!!.addItemDecoration(decor)
    }

    override fun removeRVItemDecoration(decor: RecyclerView.ItemDecoration) {
        rvList!!.removeItemDecoration(decor)
    }

    /**
     * ?????????????????????
     * ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * @return
     */
    fun haveNewMessages(): Boolean {
        return if (messageAdapter == null || messageAdapter!!.data == null || messageAdapter!!.data.isEmpty()
            || conversation == null || conversation!!.lastMessage == null
        ) {
            false
        } else conversation!!.lastMessage.msgTime > messageAdapter!!.data[messageAdapter!!.data.size - 1].msgTime
    }

    //???????????????
    private fun seekToLatest() {
        if (presenter?.isDestroy == true || rvList == null) {
            return
        }
        rvList?.adapter?.itemCount?.minus(1)?.let { seekToPosition(it) }
    }

    /**
     * ?????????????????????
     * @param position
     */
    private fun seekToPosition(position: Int) {
        var position = position
        if (presenter?.isDestroy == true || rvList == null) {
            return
        }
        if (position < 0) {
            position = 0
        }
        val manager = rvList!!.layoutManager
        if (manager is LinearLayoutManager) {
            manager.scrollToPositionWithOffset(position, 10)
        }
    }

    /**
     * ?????????????????????
     * @param position
     */
    private fun smoothSeekToPosition(position: Int) {
        var position = position
        if (presenter?.isDestroy == true || rvList == null) {
            return
        }
        if (position < 0) {
            position = 0
        }
        val manager = rvList!!.layoutManager
        if (manager is LinearLayoutManager) {
            manager.scrollToPositionWithOffset(position, 0)
            //setMoveAnimation(manager, position);
        }
    }

    private fun setMoveAnimation(manager: RecyclerView.LayoutManager, position: Int) {
        val prePosition: Int
        prePosition = if (position > 0) {
            position - 1
        } else {
            position
        }
        val view = manager.findViewByPosition(0)
        val height: Int
        height = view?.height ?: 200
        val animator = ValueAnimator.ofInt(-height, 0)
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            (manager as LinearLayoutManager).scrollToPositionWithOffset(prePosition, value)
        }
        animator.duration = 800
        animator.start()
    }

    override fun setPresenter(presenter: EaseChatMessagePresenter?) {
        this.presenter = presenter
        if (context is AppCompatActivity) {
            presenter?.let { (context as AppCompatActivity).lifecycle.addObserver(it) }
        }
        this.presenter?.attachView(this)
        this.presenter?.setupWithConversation(conversation)
    }

    override fun setOnMessageTouchListener(listener: OnMessageTouchListener?) {
        messageTouchListener = listener
    }

    override fun setOnChatErrorListener(listener: OnChatErrorListener?) {
        errorListener = listener
    }

    override fun setMessageListItemClickListener(listener: MessageListItemClickListener?) {
        messageListItemClickListener = listener
    }

    fun runOnUi(runnable: Runnable?) {
        EaseThreadManager.getInstance().runOnMainThread(runnable)
    }

    /**
     * ??????????????????
     */
    interface OnMessageTouchListener {
        /**
         * touch??????
         * @param v
         * @param position
         */
        fun onTouchItemOutside(v: View?, position: Int)

        /**
         * ?????????????????????
         */
        fun onViewDragging()
    }

    interface OnChatErrorListener {
        /**
         * ?????????????????????
         * @param code
         * @param errorMsg
         */
        fun onChatError(code: Int, errorMsg: String?)
    }

    /**
     * ???????????????????????????local??????????????????????????????Roam????????????????????????History?????????????????????
     */
    enum class LoadDataType {
        LOCAL, ROAM, HISTORY
    }

    /**
     * ?????????????????????
     */
    enum class LoadMoreStatus {
        IS_LOADING, HAS_MORE, NO_MORE_DATA
    }

    /**
     * ?????????????????????
     * normal???????????????????????????
     * left??????????????????????????????
     * right??????????????????????????????
     */
    enum class ShowType {
        NORMAL, LEFT /*, RIGHT*/
    }
}