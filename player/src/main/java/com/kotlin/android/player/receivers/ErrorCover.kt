package com.kotlin.android.player.receivers

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.kk.taurus.playerbase.assist.InterKey
import com.kk.taurus.playerbase.config.PConst
import com.kk.taurus.playerbase.entity.DataSource
import com.kk.taurus.playerbase.event.BundlePool
import com.kk.taurus.playerbase.event.EventKey
import com.kk.taurus.playerbase.event.OnErrorEventListener
import com.kk.taurus.playerbase.event.OnPlayerEventListener
import com.kk.taurus.playerbase.provider.IDataProvider
import com.kk.taurus.playerbase.receiver.BaseCover
import com.kk.taurus.playerbase.receiver.IReceiverGroup.OnGroupValueUpdateListener
import com.kk.taurus.playerbase.utils.NetworkUtils
import com.kotlin.android.ktx.bean.AppState
import com.kotlin.android.ktx.ext.appState
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.formatFileSize
import com.kotlin.android.player.DataInter
import com.kotlin.android.player.LiveStatus
import com.kotlin.android.player.PlayerConfig
import com.kotlin.android.player.R
import com.kotlin.android.player.bean.MTimeVideoData
import com.kotlin.android.player.splayer.PreviewVideoPlayer
import kotlinx.android.synthetic.main.view_player_error_cover.view.*

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/15
 */
open class ErrorCover(context: Context) : BaseCover(context), View.OnClickListener {
    private val TAG = "ErrorCover"

    private val ERROR_TYPE_NETWORK_MOBILE = 1
    private val ERROR_TYPE_NET_ERROR = 2
    private val ERROR_TYPE_OTHER = 3
    private val ERROR_TYPE_DATA_SOURCE = 4

    private lateinit var mTips: TextView
    private lateinit var mHandleButton: TextView
    private lateinit var mErrorTitleLayout: View
    private lateinit var mBackIcon: ImageView
    private lateinit var mTitle: TextView

    private var mErrorType = 0

    private var mCurrentPosition = 0
    private var mNeedErrorTitle = false

    private var mDefinitionName: String? = null
    private var mFileSize: Long = 0

    override fun onCreateCoverView(context: Context?): View? {
        return View.inflate(context, R.layout.view_player_error_cover, null)
    }

    override fun onReceiverBind() {
        super.onReceiverBind()

        mTips = findViewById(R.id.video_layout_player_error_prompt_tv)
        mHandleButton = findViewById(R.id.video_layout_player_retry_tv)
        mErrorTitleLayout = findViewById(R.id.video_layout_player_error_title_ll)
        mBackIcon = findViewById(R.id.video_layout_player_error_back_icon)
        mTitle = findViewById(R.id.video_layout_player_error_title_tv)

        mHandleButton.setOnClickListener(this)
        mBackIcon.setOnClickListener(this)

        ShapeExt.setShapeCorner2Color2Stroke(
            mHandleButton,
            R.color.color_00ffffff,
            90,
            R.color.color_ffffff,
            1,
            false
        )

//        val statusBarHeight: Int = PlayerHelper.getStatusBarHeight(context)
        val statusBarHeight: Int = 35.dp
        mErrorTitleLayout.setPadding(
            mErrorTitleLayout.paddingLeft,
            statusBarHeight,
            mErrorTitleLayout.paddingRight,
            mErrorTitleLayout.paddingBottom
        )
        setClickListener()
        groupValue.registerOnGroupValueUpdateListener(onGroupValueUpdateListener)
    }

    open fun setClickListener() {
        view.setOnClickListener { }
    }

    fun setErrorTitleLayoutState(state: Boolean) {
        mErrorTitleLayout.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun setErrorTitle(text: String) {
        if (!TextUtils.isEmpty(text)) mTitle.text = text
    }

    private val onGroupValueUpdateListener: OnGroupValueUpdateListener =
        object : OnGroupValueUpdateListener {
            override fun filterKeys(): Array<String> {
                return arrayOf(
                    DataInter.Key.KEY_NEED_ERROR_TITLE,
                    DataInter.Key.KEY_IS_FULL_SCREEN
                )
            }

            override fun onValueUpdate(key: String, value: Any) {
                if (DataInter.Key.KEY_NEED_ERROR_TITLE == key) {
                    mNeedErrorTitle = value as Boolean
                    if (!mNeedErrorTitle) {
                        setErrorTitleLayoutState(false)
                    } else {
                        checkErrorTitle()
                    }
                } else if (DataInter.Key.KEY_IS_FULL_SCREEN == key) {
                    checkErrorTitle()
                }
            }
        }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.video_layout_player_retry_tv -> errorHandle()
            R.id.video_layout_player_error_back_icon ->
                notifyReceiverEvent(DataInter.ReceiverEvent.EVENT_REQUEST_BACK, null)
        }
    }

    override fun onCoverAttachedToWindow() {
        super.onCoverAttachedToWindow()
        checkNetworkState()
    }

    private fun checkNetworkState() {
        val netConnected = NetworkUtils.isNetConnected(context)
        if (netConnected) {
            val networkState = NetworkUtils.getNetworkState(context)
            if (!PlayerConfig.ignoreMobile && networkState > PConst.NETWORK_STATE_WIFI) {
                showError(ERROR_TYPE_NETWORK_MOBILE)
            }
        } else {
            showError(ERROR_TYPE_NET_ERROR)
        }
    }

    override fun onProducerEvent(eventCode: Int, bundle: Bundle?) {
        super.onProducerEvent(eventCode, bundle)
        when (eventCode) {
            DataInter.ProducerEvent.EVENT_AIRPLANE_STATE_CHANGE -> if (isAirplaneModeOn(context)) {
                showError(ERROR_TYPE_NET_ERROR)
            }
        }
    }

    override fun onProducerData(key: String, data: Any) {
        super.onProducerData(key, data)
        if (InterKey.KEY_NETWORK_STATE == key) {
            val networkState = data as Int
            if (networkState > PConst.NETWORK_STATE_WIFI) {
                if (PlayerConfig.ignoreMobile) {
                    recoveryPlay()
                    hiddenError()
                } else {
                    showError(ERROR_TYPE_NETWORK_MOBILE)
                }
            } else if (networkState < 0) {
                showError(ERROR_TYPE_NET_ERROR)
            } else if (networkState == PConst.NETWORK_STATE_WIFI && isErrorShow()) {
                recoveryPlay()
                hiddenError()
            }
        }
    }

    override fun onPlayerEvent(eventCode: Int, bundle: Bundle?) {
        when (eventCode) {
            OnPlayerEventListener.PLAYER_EVENT_ON_PROVIDER_DATA_START -> {
                checkNetworkState()
            }
            OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET -> {
                val dataSource = bundle?.getSerializable(EventKey.SERIALIZABLE_DATA) as? DataSource?
                if (dataSource != null) {
                    mDefinitionName = dataSource.tag
                }
                if (dataSource is MTimeVideoData) {
                    mFileSize = dataSource.fileSize
                }
            }
            OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START -> {
                hiddenError()
            }
            DataInter.ProviderEvent.EVENT_VIDEO_INFO_READY -> {
                checkNetworkState()
//                bundle?.run {
//                    val bean = getSerializable(EventKey.SERIALIZABLE_DATA) as VideoInfoBean?
//                    if (bean != null) {
//                        setErrorTitle(bean.getTitle())
//                    }
//                }
            }
        }
    }

    override fun onErrorEvent(eventCode: Int, bundle: Bundle?) {
        "ErrorCover-onErrorEvent:$eventCode".e()
        when (eventCode) {
            OnErrorEventListener.ERROR_EVENT_DATA_PROVIDER_ERROR -> if (bundle != null) {
                val code = bundle.getInt(EventKey.INT_DATA)
                if (IDataProvider.PROVIDER_CODE_DATA_PROVIDER_ERROR == code) {
                    showError(ERROR_TYPE_DATA_SOURCE)
                }
            }
            else -> showError(ERROR_TYPE_OTHER)
        }
    }

    override fun onReceiverEvent(eventCode: Int, bundle: Bundle?) {
        when (eventCode) {
            DataInter.ReceiverEvent.EVENT_CODE_DEFINITION_CHANGE -> if (bundle != null) {
                mDefinitionName = bundle.getString(EventKey.STRING_DATA)
                mFileSize = bundle.getLong(EventKey.LONG_DATA)
            }
        }
    }

    fun setTipText(text: String?) {
        if (TextUtils.isEmpty(text)) return
        mTips.text = text
    }

    fun setHandleButtonText(text: String?) {
        if (TextUtils.isEmpty(text)) return
        mHandleButton.text = text
    }

    fun setHandleButtonState(state: Boolean) {
        mHandleButton.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun showErrorNetToast() {
        view.showToast(R.string.player_string_no_network_connected)
    }

    private fun getCurrentPosition(): Int {
        val playerStateGetter = playerStateGetter
        return playerStateGetter?.currentPosition ?: 0
    }

    private fun recoveryPlay() {
        val bundle = BundlePool.obtain()
        bundle.putInt(EventKey.INT_DATA, mCurrentPosition)
        if (PreviewVideoPlayer.get()
                ?.isCurrentTopActivity() == true && appState == AppState.FOREGROUND
        ) {
            if (PlayerConfig.liveStatus == LiveStatus.LIVING) {
                requestReplay(null)
            } else {
                requestRetry(bundle)
            }
//            PreviewVideoPlayer.get()?.requestRetry(mCurrentPosition)
        } else {
             if (PlayerConfig.liveStatus == LiveStatus.LIVING) {
                requestReplay(null)
            } else {
                 PreviewVideoPlayer.get()?.rePlay(mCurrentPosition)
             }
            PreviewVideoPlayer.get()?.pause()
        }
    }

    private fun errorHandle() {
        when (mErrorType) {
            ERROR_TYPE_NETWORK_MOBILE -> {
                PlayerConfig.ignoreMobile = true
                //request go on play
                recoveryPlay()
                hiddenError()
            }
            ERROR_TYPE_NET_ERROR -> showErrorNetToast()
            ERROR_TYPE_OTHER -> notifyReceiverEvent(
                DataInter.ReceiverEvent.EVENT_CODE_ERROR_FEED_BACK,
                null
            )
            ERROR_TYPE_DATA_SOURCE -> {
            }
        }
    }

    private fun getString(id: Int): String? {
        return context.getString(id)
    }

    private fun isErrorShow(): Boolean {
        return groupValue.getBoolean(DataInter.Key.KEY_ERROR_SHOW_STATE)
    }

    open fun showError(errorType: Int) {
        mErrorType = errorType
        var bgColor = Color.BLACK
        setHandleButtonState(true)
        mHandleButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        when (errorType) {
            ERROR_TYPE_NETWORK_MOBILE -> {
//                if (PlayerConfig.liveStatus == LiveStatus.LIVING) {
//                    setHandleButtonState(false)
//                    return
//                }
                setTipText(getString(R.string.player_string_network_no_wifi_tips))
                if (mFileSize <= 0) {
                    setHandleButtonText(context.getString(R.string.player_string_go_on_play))
                } else {
                    val text = context.getString(
                        R.string.player_string_go_on_play_size,
                        mDefinitionName, formatFileSize(mFileSize)
                    )
                    setHandleButtonText(text)
                }
                mHandleButton.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_player_mobile_play_continue,
                    0, 0, 0
                )
                bgColor = Color.parseColor("#66000000")
            }
            ERROR_TYPE_NET_ERROR -> {
                showErrorNetToast()
                setTipText(getString(R.string.player_string_no_network_connected))
                setHandleButtonText(getString(R.string.player_string_retry))
            }
            ERROR_TYPE_OTHER -> {
                setTipText(getString(R.string.player_string_error_other))
                setHandleButtonText(getString(R.string.player_string_error_feed_back))
            }
            ERROR_TYPE_DATA_SOURCE -> {
                setTipText(getString(R.string.player_string_error_license_tip))
                setHandleButtonState(false)
            }
        }
        view.setBackgroundColor(bgColor)
        setCoverVisibility(View.VISIBLE)
        checkCurrentPosition()
        groupValue.putBoolean(DataInter.Key.KEY_ERROR_SHOW_STATE, true)
        //request stop player
        requestStop(null)
        //check whether need error title
        checkErrorTitle()
    }

    fun showErrorBackGround() {
        var bgColor = Color.parseColor("#66000000")
        view.setBackgroundColor(bgColor)
    }

    fun showErrorTitleLayout(isShow: Boolean) {
        view?.video_layout_player_error_title_ll?.visible(false)
    }

    private fun isFullScreen(): Boolean {
        return groupValue.getBoolean(DataInter.Key.KEY_IS_FULL_SCREEN)
    }

    open fun checkErrorTitle() {
        if (mNeedErrorTitle && mErrorType != ERROR_TYPE_NETWORK_MOBILE && isErrorShow() && isFullScreen()) {
            setErrorTitleLayoutState(true)
        } else {
            setErrorTitleLayoutState(false)
        }
    }

    private fun checkCurrentPosition() {
        val currentPosition = getCurrentPosition()
        if (currentPosition > 0) {
            mCurrentPosition = currentPosition
        }
    }

    private fun hiddenError() {
        mErrorType = -1
        setCoverVisibility(View.GONE)
        groupValue.putBoolean(DataInter.Key.KEY_ERROR_SHOW_STATE, false)
    }

    private fun isAirplaneModeOn(context: Context): Boolean {
        return Settings.System.getInt(
            context.contentResolver,
            Settings.Global.AIRPLANE_MODE_ON, 0
        ) != 0
    }

    override fun onReceiverUnBind() {
        super.onReceiverUnBind()
        groupValue.unregisterOnGroupValueUpdateListener(onGroupValueUpdateListener)
    }

    override fun getCoverLevel(): Int {
        return levelHigh(DataInter.CoverLevel.COVER_LEVEL_ERROR)
    }

}