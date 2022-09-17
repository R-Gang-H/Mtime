package com.kotlin.chat_component.inner.modules.chat

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import com.hyphenate.EMError
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import com.hyphenate.util.EMLog
import com.hyphenate.util.PathUtil
import com.hyphenate.util.VersionUtils
import com.kotlin.android.app.data.entity.chatroom.Token
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.permission.permissions
import com.kotlin.android.user.UserManager
import com.kotlin.chat_component.R
import com.kotlin.chat_component.inner.constants.EaseConstant
import com.kotlin.chat_component.inner.manager.EaseDingMessageHelper
import com.kotlin.chat_component.inner.modules.chat.interfaces.*
import com.kotlin.chat_component.inner.modules.menu.EasePopupWindowHelper
import com.kotlin.chat_component.inner.modules.menu.MenuItemBean
import com.kotlin.chat_component.inner.ui.base.EaseBaseFragment
import com.kotlin.chat_component.inner.utils.EaseCommonUtils
import com.kotlin.chat_component.inner.utils.EaseCompat
import com.kotlin.chat_component.inner.utils.EaseFileUtils
import com.kotlin.chat_component.manager.ChatUserCacheManager
import com.kotlin.chat_component.manager.HuanxinTokenStore
import com.kotlin.chat_component.model.MtimeUserSimple
import kotlinx.android.synthetic.main.ease_fragment_chat_list.*
import org.jetbrains.anko.toast
import java.io.File
import java.io.IOException

open class EaseChatFragment : EaseBaseFragment(), OnChatLayoutListener, OnMenuChangeListener,
    OnAddMsgAttrsBeforeSendEvent, OnChatRecordTouchListener {

    companion object {
        protected const val REQUEST_CODE_MAP = 1
        protected const val REQUEST_CODE_CAMERA = 2
        protected const val REQUEST_CODE_LOCAL = 3
        protected const val REQUEST_CODE_DING_MSG = 4
        protected const val REQUEST_CODE_SELECT_VIDEO = 11
        protected const val REQUEST_CODE_SELECT_FILE = 12
        private val TAG = EaseChatFragment::class.java.simpleName
    }

    var chatLayout: EaseChatLayout? = null
    var conversationId: String? = null
    var chatType = 0
    var historyMsgId: String? = null
    var isRoam = false
    var isMessageInit = false
    private var listener: OnChatLayoutListener? = null
    private var onMessageSendListener: OnMessageSendListener? = null

    protected var cameraFile: File? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initArguments()
        return inflater.inflate(layoutId, null)
    }

    private val layoutId: Int
        private get() = R.layout.ease_fragment_chat_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
        initData()
    }

    private var otherMtimeId: Long? = null
    private var otherNickName: String? = null
    private var otherHead: String? = null
    private var otherAuthType: Long? = null
    private var otherAuthRole: String? = null

    private fun initArguments() {
        val bundle = arguments
        if (bundle != null) {
            conversationId = bundle.getString(EaseConstant.EXTRA_CONVERSATION_ID)
            chatType = bundle.getInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE)
            historyMsgId = bundle.getString(EaseConstant.HISTORY_MSG_ID)
            isRoam = bundle.getBoolean(EaseConstant.EXTRA_IS_ROAM, false)

            otherMtimeId = bundle.getLong(EaseConstant.KEY_CHAT_OTHER_MTIME_ID)
            otherNickName = bundle.getString(EaseConstant.KEY_CHAT_OTHER_NICK_NAME)
            otherHead = bundle.getString(EaseConstant.KEY_CHAT_OTHER_HEAD)
            otherAuthType = bundle.getLong(EaseConstant.KEY_CHAT_OTHER_AUTH_TYPE, 0L)
            otherAuthRole = bundle.getString(EaseConstant.KEY_CHAT_OTHER_AUTH_ROLE)
        }
    }

    fun initView() {
        chatLayout = findViewById(R.id.layout_chat)
        chatLayout?.chatMessageListLayout?.setItemShowType(EaseChatMessageListLayout.ShowType.NORMAL)
        chatLayout?.chatMessageListLayout?.setBackgroundColor(
            ContextCompat.getColor(mContext, R.color.gray)
        )
        if (chatType == EaseConstant.CHATTYPE_CHATROOM) {
            cl_loading_view.visibility = View.VISIBLE
        }
    }

    fun dismissLoadingView() {
        cl_loading_view.visibility = View.GONE
    }

    fun initListener() {
        chatLayout?.setOnChatLayoutListener(this)
        chatLayout?.setOnPopupWindowItemClickListener(this)
        chatLayout?.setOnAddMsgAttrsBeforeSendEvent(this)
        chatLayout?.setOnChatRecordTouchListener(this)
    }

    open fun initData() {
        if (isMessageInit) return
        if (!TextUtils.isEmpty(historyMsgId)) {
            chatLayout?.init(
                EaseChatMessageListLayout.LoadDataType.HISTORY,
                conversationId,
                chatType
            )
            chatLayout?.loadData(historyMsgId)
        } else {
            if (isRoam) {
                chatLayout?.init(
                    EaseChatMessageListLayout.LoadDataType.ROAM,
                    conversationId,
                    chatType
                )
            } else {
                chatLayout?.init(conversationId, chatType)
            }
            chatLayout?.loadDefaultData()
        }
        isMessageInit = true
    }

    override fun onResume() {
        super.onResume()
        if (isMessageInit) {
            chatLayout?.chatMessageListLayout?.refreshMessages()
        }
    }

    fun setOnChatLayoutListener(listener: OnChatLayoutListener?) {
        this.listener = listener
    }

    override fun onBubbleClick(message: EMMessage?): Boolean {
        return if (listener != null) {
            listener!!.onBubbleClick(message)
        } else false
    }

    override fun onBubbleLongClick(v: View?, message: EMMessage?): Boolean {
        return if (listener != null) {
            listener!!.onBubbleLongClick(v, message)
        } else false
    }

    override fun onUserAvatarClick(username: String?) {
        if (listener != null) {
            listener?.onUserAvatarClick(username)
        }
    }

    override fun onUserAvatarLongClick(username: String?) {
        if (listener != null) {
            listener?.onUserAvatarLongClick(username)
        }
    }

    override fun onChatExtendMenuItemClick(view: View?, itemId: Int) {
        if (itemId == R.id.extend_item_take_picture) {
            selectPicFromCamera()
        } else if (itemId == R.id.extend_item_picture) {
            selectPicFromLocal()
        } else if (itemId == R.id.extend_item_video) {
            selectVideoFromLocal()
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun onChatSuccess(message: EMMessage?) {
        message?.let { onMessageSendListener?.onSendSuccess(it) }
    }

    fun setOnMessageSendListener(listener: OnMessageSendListener) {
        onMessageSendListener = listener
    }

    override fun onChatError(code: Int, errorMsg: String?) {
        "onChatError  code:$code  errorMsg:$errorMsg".e()
        if (code == EMError.USER_MUTED || code == EMError.USER_MUTED_BY_ADMIN) {
            context?.toast(getString(R.string.you_are_muted))
        }
        if (listener != null) {
            listener?.onChatError(code, errorMsg)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            chatLayout?.chatInputMenu?.hideExtendContainer()
            if (requestCode == REQUEST_CODE_CAMERA) { // capture new image
                onActivityResultForCamera(data)
            } else if (requestCode == REQUEST_CODE_LOCAL) { // send local image
                onActivityResultForLocalPhotos(data)
            } else if (requestCode == REQUEST_CODE_DING_MSG) { // To send the ding-type msg.
                onActivityResultForDingMsg(data)
            } else if (requestCode == REQUEST_CODE_SELECT_VIDEO) {
                onActivityResultForLocalVideos(data)
            }
        }
    }

    private fun onActivityResultForLocalVideos(data: Intent?) {
        if (data != null) {
            val uri = data.data

            if (!EaseFileUtils.validVideo(
                    EaseFileUtils.getFileSize(
                        EaseFileUtils.getFileByUri(
                            uri,
                            context
                        )
                    )
                )
            ) {
                context?.toast(getString(R.string.invalid_video))
                return
            }

            val mediaPlayer = MediaPlayer()
            try {
                mediaPlayer.setDataSource(mContext, uri!!)
                mediaPlayer.prepare()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val duration = mediaPlayer.duration
            EMLog.d(TAG, "path = " + uri!!.path + ",duration=" + duration)
            chatLayout?.sendVideoMessage(uri, duration)
        }
    }

    /**
     * select picture from camera
     */
    protected fun selectPicFromCamera() {
        activity?.permissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        ) {
            onGranted { it ->
                if (it.size == 3) {
                    if (!checkSdCardExist()) {
                        return@onGranted
                    }
                    cameraFile = File(
                        PathUtil.getInstance().imagePath, EMClient.getInstance().currentUser
                                + System.currentTimeMillis()
                    )
                    cameraFile?.parentFile?.mkdirs()
                    cameraFile?.let { file ->
                        startActivityForResult(
                            Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
                                MediaStore.EXTRA_OUTPUT, EaseCompat.getUriForFile(
                                    context, file
                                )
                            ), REQUEST_CODE_CAMERA
                        )
                    }

                }
            }
        }
    }

    /**
     * select local image
     */
    protected fun selectPicFromLocal() {
        activity?.permissions(Manifest.permission.READ_EXTERNAL_STORAGE) {
            onGranted {
                EaseCompat.openImage(this@EaseChatFragment, REQUEST_CODE_LOCAL)
            }
        }
    }


    /**
     * select local video
     */
    protected open fun selectVideoFromLocal() {
        val intent = Intent()
        if (VersionUtils.isTargetQ(activity)) {
            intent.action = Intent.ACTION_OPEN_DOCUMENT
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                intent.action = Intent.ACTION_GET_CONTENT
            } else {
                intent.action = Intent.ACTION_OPEN_DOCUMENT
            }
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "video/*"
        startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO)
    }


    /**
     * 相机返回处理结果
     *
     * @param data
     */
    protected fun onActivityResultForCamera(data: Intent?) {
        if (cameraFile != null && cameraFile!!.exists()) {
            if (!EaseFileUtils.validImage(EaseFileUtils.getFileSize(cameraFile))) {
                context?.toast(getString(R.string.invalid_image))
                return
            }
            chatLayout?.sendImageMessage(Uri.parse(cameraFile?.absolutePath))
        }
    }

    /**
     * 选择本地图片处理结果
     *
     * @param data
     */
    protected fun onActivityResultForLocalPhotos(data: Intent?) {
        if (data != null) {
            val selectedImage = data.data
            if (selectedImage != null) {
                val filePath = EaseFileUtils.getFilePath(mContext, selectedImage)
                if (!EaseFileUtils.validImage(
                        EaseFileUtils.getFileSize(
                            EaseFileUtils.getFileByUri(
                                selectedImage,
                                context
                            )
                        )
                    )
                ) {
                    context?.toast(getString(R.string.invalid_image))
                    return
                }
                if (!TextUtils.isEmpty(filePath) && File(filePath).exists()) {
                    chatLayout?.sendImageMessage(Uri.parse(filePath))
                } else {
                    EaseFileUtils.saveUriPermission(mContext, selectedImage, data)
                    chatLayout?.sendImageMessage(selectedImage)
                }
            }
        }
    }

    private fun onActivityResultForDingMsg(data: Intent?) {
        if (data != null) {
            val msgContent = data.getStringExtra("msg")
            EMLog.i(TAG, "To send the ding-type msg, content: $msgContent")
            // Send the ding-type msg.
            val dingMsg = EaseDingMessageHelper.get().createDingMessage(conversationId, msgContent)
            chatLayout?.sendMessage(dingMsg)
        }
    }

    /**
     * 检查sd卡是否挂载
     *
     * @return
     */
    private fun checkSdCardExist(): Boolean {
        return EaseCommonUtils.isSdcardExist()
    }

    override fun onPreMenu(helper: EasePopupWindowHelper?, message: EMMessage?) {}
    override fun onMenuItemClick(item: MenuItemBean?, message: EMMessage?): Boolean {
        return false
    }

    private var token: Token? = null

    override fun addMsgAttrsBeforeSend(message: EMMessage?) {
        if (token == null) {
            token = HuanxinTokenStore.instance.getToken()
        }
        //每一条消息都添加当前时光的用户名和头像
        token?.bizData?.imId?.let {
            message?.setAttribute(ChatUserCacheManager.KEY_IM_ID, it)
            message?.setAttribute(ChatUserCacheManager.KEY_NICKNAME, UserManager.instance.nickname)
            message?.setAttribute(ChatUserCacheManager.KEY_AVATAR, UserManager.instance.userAvatar)
            message?.setAttribute(ChatUserCacheManager.KEY_MTIME_ID, UserManager.instance.userId)
            message?.setAttribute(
                ChatUserCacheManager.KEY_MTIME_AUTH_TYPE,
                UserManager.instance.userAuthType
            )
            message?.setAttribute(
                ChatUserCacheManager.KEY_MTIME_AUTH_ROLE,
                UserManager.instance.userAuthRole
            )

            if (otherMtimeId == null || otherMtimeId == 0L) {
                val mtimeUser = ChatUserCacheManager.getMtimeUserSimple(it)
                mtimeUser?.let { user ->
                    message?.setAttribute(ChatUserCacheManager.KEY_MTIME_OTHER, user.toJsonObject())
                }
            } else {
                //携带的对方信息
                message?.setAttribute(
                    ChatUserCacheManager.KEY_MTIME_OTHER, MtimeUserSimple(
                        imId = this.conversationId,
                        mtimeId = otherMtimeId,
                        nickName = otherNickName,
                        headPic = otherHead,
                        authType = otherAuthType,
                        authRole = otherAuthRole
                    ).toJsonObject()
                )
            }
        }
    }

    /**
     * Set whether can touch voice button
     *
     * @param v
     * @param event
     * @return
     */
    override fun onRecordTouch(v: View?, event: MotionEvent?): Boolean {
        var canTouch = false
        activity?.permissions(Manifest.permission.RECORD_AUDIO) {
            canTouch = true
        }
        return canTouch
    }

    override fun onDismiss(menu: PopupWindow?) {}
}