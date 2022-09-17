package com.kotlin.android.live.component.receivers

import android.app.Activity
import android.content.Context
import android.os.*
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.SeekBar
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kk.taurus.playerbase.event.BundlePool
import com.kk.taurus.playerbase.event.EventKey
import com.kk.taurus.playerbase.event.OnPlayerEventListener
import com.kk.taurus.playerbase.player.IPlayer
import com.kk.taurus.playerbase.player.OnTimerUpdateListener
import com.kk.taurus.playerbase.receiver.BaseCover
import com.kk.taurus.playerbase.receiver.IReceiverGroup
import com.kk.taurus.playerbase.touch.OnTouchGestureListener
import com.kk.taurus.playerbase.utils.TimeUtil
import com.kotlin.android.core.ext.getSpValue
import com.kotlin.android.core.ext.putSpValue
import com.kotlin.android.core.statusbar.StatusBarUtils
import com.kotlin.android.app.data.entity.video.VideoDetail
import com.kotlin.android.ktx.bean.AppState
import com.kotlin.android.ktx.ext.appState
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.getActivity
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.live.component.R
import com.kotlin.android.live.component.constant.LIVE_STATUS_LIVING
import com.kotlin.android.live.component.constant.LIVE_STATUS_REVIEW
import com.kotlin.android.live.component.manager.CameraStandManager
import com.kotlin.android.live.component.ui.adapter.CameraStandAdapter
import com.kotlin.android.live.component.ui.widget.SendDanmuDialog
import com.kotlin.android.live.component.ui.widget.dismissDanmuDialog
import com.kotlin.android.live.component.ui.widget.showDanmuDialog
import com.kotlin.android.live.component.viewbean.CameraStandViewBean
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.mtime.ktx.getDrawable
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.player.DataInter
import com.kotlin.android.player.DataInter.Key.Companion.KEY_DANMU_TOGGLE
import com.kotlin.android.player.LiveStatus
import com.kotlin.android.player.PlayerConfig
import com.kotlin.android.player.PlayerHelper
import com.kotlin.android.player.bean.LiveVideoData
import com.kotlin.android.player.splayer.PreviewVideoPlayer
import com.kotlin.android.router.ext.put
import com.kotlin.android.user.isLogin
import com.kotlin.android.user.login.gotoLoginPage
import kotlinx.android.synthetic.main.view_living_controller_cover.view.*

/**
 * create by lushan on 2021/3/8
 * description:直播控制器,直播中，回看
 */
class LiveControllerCover(context: Context) : BaseCover(context), OnTouchGestureListener,
    OnTimerUpdateListener {

    private val MSG_CODE_DELAY_HIDDEN_CONTROLLER = 101
    private var isShowCameraStand = true//是否显示机位列表，由机位显示按钮控制
    private var isPortrait = true//默认竖屏
    private var currentPosition = 0
    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MSG_CODE_DELAY_HIDDEN_CONTROLLER -> hiddenController()
            }
        }
    }
    private var mTimerUpdateEnable = true
    private var mSeekTraceProgress = 0
    private var mDelaySeekRunnable = object : Runnable {
        override fun run() {
            if (mSeekTraceProgress < 0 || mSeekTraceProgress > getDuration()) {
                return
            }
            val bundle = BundlePool.obtain()
            bundle.putInt(EventKey.INT_DATA, mSeekTraceProgress)
            requestSeek(bundle)
            requestResume(null)
        }

    }
    private var mOnSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            val duration: Int = getDuration()
            if (p2 && duration > 0) {
                updateUI(p1, duration, -1)
            }
        }

        override fun onStartTrackingTouch(p0: SeekBar?) {
            mTimerUpdateEnable = false
            removeDelayHiddenMessage()
            notifyReceiverPrivateEvent(
                DataInter.ReceiverKey.KEY_LOG_RECEIVER,
                DataInter.ReceiverEvent.EVENT_CODE_SEEK_START_TRACING_TOUCH, null
            )
        }

        override fun onStopTrackingTouch(p0: SeekBar?) {
            sendDelayHiddenMessage()
            mSeekTraceProgress = p0?.progress ?: 0
            mHandler.removeCallbacks(mDelaySeekRunnable)
            mHandler.postDelayed(mDelaySeekRunnable, 500)
            notifyReceiverPrivateEvent(
                DataInter.ReceiverKey.KEY_LOG_RECEIVER,
                DataInter.ReceiverEvent.EVENT_CODE_SEEK_END_TRACING_TOUCH, null
            )
        }

    }
    private var mTimeFormat: String? = null
    private var isControllerShow = false
    private var isControllerEnable = true

    private var cameraStandAdapter: CameraStandAdapter? = null
    private val onCheckedChangeListener =
        CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            Bundle().apply {
                putBoolean(DataInter.Key.KEY_DANMU_TOGGLE, isChecked)
            }.also {
                notifyReceiverPrivateEvent(
                    DataInter.ReceiverKey.KEY_DANMU_COVER,
                    DataInter.ReceiverEvent.EVENT_LIVE_DANMU, it
                )
            }

            groupValue.putBoolean(DataInter.Key.KEY_DANMU_OPEN_STATE, isChecked)
        }

    private val onGroupValueUpdateListener: IReceiverGroup.OnGroupValueUpdateListener =
        object : IReceiverGroup.OnGroupValueUpdateListener {
            override fun filterKeys(): Array<String> {
                return arrayOf(
                    DataInter.Key.KEY_IS_FULL_SCREEN,
                    DataInter.Key.KEY_CONTROLLER_TIMER_UPDATE_ENABLE,
                    DataInter.Key.KEY_CONTROLLER_TOP_ENABLE,
                    DataInter.Key.KEY_NEED_RECOMMEND_LIST,
                    DataInter.Key.KEY_NEED_VIDEO_DEFINITION,
                    DataInter.Key.KEY_CONTROLLER_SHARE_ENABLE,
                    DataInter.Key.KEY_DANMU_EDIT_ENABLE,
                    DataInter.Key.KEY_NEED_PLAY_NEXT,
                    DataInter.Key.KEY_USER_GUIDE_STATE,
                    DataInter.Key.KEY_LIST_COMPLETE,
                    DataInter.Key.KEY_CURRENT_DEFINITION
                )
            }

            override fun onValueUpdate(s: String, o: Any) {
                if (DataInter.Key.KEY_IS_FULL_SCREEN == s) {//全屏按钮
                    setScreenToggleViewState(isControllerShow)
                    setChatViewState(isControllerShow && isFullScreen() && isLive())
                    updateRightAndPlayMargin()
                    setPlayNextViewState(
                        CameraStandManager.getVideoList()
                            .isNotEmpty() && isFullScreen() && isLive().not()
                    )
                    setAllTopMargin()
                } else if (DataInter.Key.KEY_CONTROLLER_TIMER_UPDATE_ENABLE == s) {
                    mTimerUpdateEnable = o as Boolean
                } else if (DataInter.Key.KEY_CONTROLLER_TOP_ENABLE == s) {
                    val topEnable = o as Boolean
                    if (isControllerShow) {
                        setUpControllerViewState(topEnable)
                    }
                } else if (DataInter.Key.KEY_NEED_VIDEO_DEFINITION == s) {//显示清晰度
                    setDefinitionViewState(
                        o as Boolean && TextUtils.isEmpty(
                            view.definitionTv?.text?.toString().orEmpty()
                        ).not()
                    )
                } else if (DataInter.Key.KEY_CONTROLLER_SHARE_ENABLE == s) {//显示分享
                    setShareViewState(o as Boolean)
                } else if (DataInter.Key.KEY_DANMU_EDIT_ENABLE == s) {
                    val state = o as Boolean
                    if (state && isControllerShow) {
//                    setEditDanmuIconContainerState(true)
                    } else if (!state) {
//                    setEditDanmuIconContainerState(false)
                    }
                } else if (DataInter.Key.KEY_NEED_PLAY_NEXT == s) {//需要播放下一个

                } else if (DataInter.Key.KEY_USER_GUIDE_STATE == s) {//新手引导
                    val state = o as Boolean
                    isControllerEnable = !state
                    if (state) {
                        hiddenController()
                    }
                } else if (DataInter.Key.KEY_LIST_COMPLETE == s) {//列表播放结束
                    val state = o as Boolean
//                重播
//                setReplayIconContainerState(state)
                    if (state) {
                        showController()
                    }
                } else if (DataInter.Key.KEY_CURRENT_DEFINITION == s) {
                    val definitionName = o as? String
                    setDefinitionName(definitionName.orEmpty())
                }
            }
        }

    private fun isFullScreen(): Boolean {
        return groupValue.getBoolean(DataInter.Key.KEY_IS_FULL_SCREEN)
    }

    private fun isPreparing(): Boolean {
        val playerStateGetter = playerStateGetter
        if (playerStateGetter != null) {
            val state = playerStateGetter.state
            return state == IPlayer.STATE_INITIALIZED || state == IPlayer.STATE_PREPARED
        }
        return true
    }


    private fun showController() {
        setAllTopMargin()

        PlayerHelper.setSystemBarVisibility(context as? Activity, true)
        isControllerShow = true
        if (isPreparing()) {
            setUpControllerViewState(false)
//            setTopContainerState(false)
            setDownControllerViewState(false)
        } else {
            if (isTopEnable()) {
                setUpControllerViewState(true)
            }
            setDownControllerViewState(true)
        }
        if (isRecommendListEnable()) {
            setCameraStandViewState(true)
        }
        if (isVideoDefinitionEnable() && TextUtils.isEmpty(
                view.definitionTv?.text?.toString().orEmpty()
            ).not()
        ) {
            setDefinitionViewState(true)
        }
        if (isDanmuSwitchEnable() || PlayerConfig.liveStatus == LiveStatus.LIVING) {
            setDanmuToggleViewState(true)
        }
//        setBottomProgressBarState(false)
        showButtons()
        setCoverVisibility(View.VISIBLE)
        //update show state
        updateControllerShowState(true)
        setShareViewState(true)
    }

    private fun setAllTopMargin() {
        setTopMargin(view.backBtn, if (isFullScreen()) 30.dp else 10.dp)
        setTopMargin(view.liveLabelCL, if (isFullScreen()) 30.dp else 10.dp)
        setTopMargin(view.onLineNumTv, if (isFullScreen()) 32.dp else 11.dp)
        setTopMargin(view.titleTv, if (isFullScreen()) 32.dp else 11.dp)
        setTopMargin(view.shareIv, if (isFullScreen()) 35.dp else 15.dp)
    }

    /**
     * @param portraitLeft 竖屏时左边距
     * @param   landLeft    横屏时左边距
     */
    private fun updateMarginLeft(view: View?, portraitLeft: Int, landLeft: Int) {
        val marginLayoutParams = view?.layoutParams as? ViewGroup.MarginLayoutParams
        marginLayoutParams?.leftMargin = if (isFullScreen()) landLeft.dp else portraitLeft.dp
        view?.layoutParams = marginLayoutParams
    }

    private fun updateMarginRight(view: View?, portraitRight: Int, landRight: Int) {
        val marginLayoutParams = view?.layoutParams as? ViewGroup.MarginLayoutParams
        marginLayoutParams?.rightMargin = if (isFullScreen()) landRight.dp else portraitRight.dp
        view?.layoutParams = marginLayoutParams
    }

    private fun updateRightAndPlayMargin() {
        updateMarginRight(view?.chatTv, 10, 30)
        updateMarginLeft(view?.playerIv, 10, 25)
    }

    private fun showButtons() {
        updateRightAndPlayMargin()
        setScreenToggleViewState(isControllerShow)
        setChatViewState(false)
        setChangeCameraViewState(false)
        setCameraStandViewState(false)
        setCameraTipsViewState(false)
        setLiveLabelClViewState(false)
        setOnLineNumViewState(false)
        setDanmuToggleViewState(false)
        setPlayNextViewState(false)
        toggleState()
        when (PlayerConfig.liveStatus) {
            LiveStatus.LIVING -> {//直播中
                setDanmuToggleViewState(true)
                setLiveLabelClViewState(true)
                setOnLineNumViewState(true)
                setPlayerViewState(true)
                setPlayNextViewState(false)
                setStartDurationViewState(false)
                setRemainDurationViewState(false)
                setSeekBarViewState(false)
                setCameraStandViewState(true)
                setChangeCameraViewState(isShowCameraStand)
                if (isPreparing().not()) {//播放准备中不能显示机位收起提示
                    var hadDisplay = getSpValue(DataInter.Key.KEY_CAMERA_STAND_TIPS, false)
                    setCameraTipsViewState(hadDisplay.not())
                }
                setChatViewState(isControllerShow && isFullScreen() && PlayerConfig.liveStatus == LiveStatus.LIVING)
                view?.playerIv?.background = getDrawable(R.drawable.ic_live_refresh)
            }
            LiveStatus.L_REVIEWER -> {//直播回看
                setPlayerViewState(true)
                setPlayNextViewState(false)
                setLiveLabelClViewState(true)
                setStartDurationViewState(true)
                setRemainDurationViewState(true)
                setSeekBarViewState(true)
                view?.playerIv?.background = getDrawable(R.drawable.ic_selector_live_play_state_icon)
                setPlayNextViewState(
                    CameraStandManager.getVideoList().isNotEmpty() && isFullScreen()
                )
            }
            else -> {
                setPlayerViewState(true)
                setPlayNextViewState(
                    CameraStandManager.getVideoList().isNotEmpty() && isFullScreen()
                )
                setStartDurationViewState(true)
                setRemainDurationViewState(true)
                setSeekBarViewState(true)
                view?.playerIv?.background = getDrawable(R.drawable.ic_selector_live_play_state_icon)
            }
        }
    }

    private fun updateControllerShowState(show: Boolean) {
        groupValue.putBoolean(DataInter.Key.KEY_IS_CONTROLLER_SHOW, show)
    }

    private fun isTopEnable(): Boolean {
        return groupValue.getBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE)
    }

    private fun isRecommendListEnable(): Boolean {
        return groupValue.getBoolean(DataInter.Key.KEY_NEED_RECOMMEND_LIST)
    }

    private fun isVideoDefinitionEnable(): Boolean {
        return groupValue.getBoolean(DataInter.Key.KEY_NEED_VIDEO_DEFINITION)
    }

    private fun isDanmuSwitchEnable(): Boolean {
        return groupValue.getBoolean(DataInter.Key.KEY_CONTROLLER_DANMU_SWITCH_ENABLE)
    }

    private fun setDefinitionName(definitionName: String) {
        if (TextUtils.isEmpty(definitionName)) {
            setDefinitionViewState(false)
        }
        view?.definitionTv?.text = definitionName
    }

    private fun isErrorState(): Boolean {
        return groupValue.getBoolean(DataInter.Key.KEY_ERROR_SHOW_STATE)
    }

    private fun removeDelayHiddenMessage() {
        mHandler.removeMessages(MSG_CODE_DELAY_HIDDEN_CONTROLLER)
    }

    private fun sendDelayHiddenMessage() {
        removeDelayHiddenMessage()
        mHandler.sendEmptyMessageDelayed(MSG_CODE_DELAY_HIDDEN_CONTROLLER, 3500)
    }

    private fun getDuration(): Int {
        val playerStateGetter = playerStateGetter
        return playerStateGetter?.duration ?: 0
    }

    private fun hiddenController() {
        PlayerHelper.setSystemBarVisibility(context as? Activity, isFullScreen().not())
        isControllerShow = false
        setUpControllerViewState(false)
        setDownControllerViewState(false)
        setSeekBarViewState(false)
        setPlayerViewState(false)
        setCameraTipsViewState(false)
    }

    private fun setChatViewState(state: Boolean) {
        setViewState(view?.chatTv, state)
    }

    private fun setUpControllerViewState(state: Boolean) {
        setViewState(view?.upControllerCL, state)
    }

    private fun setDownControllerViewState(state: Boolean) {
        setViewState(view?.bottomCL, state)
    }

    private fun setBackViewState(state: Boolean) {
        setViewState(view?.backBtn, state)
    }

    private fun setLiveLabelClViewState(state: Boolean) {
        setViewState(view?.liveLabelCL, state)
    }

    private fun setOnLineNumViewState(state: Boolean) {
        setViewState(view?.onLineNumTv, state)
    }

    private fun setShareViewState(state: Boolean) {
        setViewState(view?.shareIv, state)
    }

    private fun setChangeCameraViewState(state: Boolean) {
        setViewState(view?.changeCameraStandCL, state)
    }

    private fun setPlayerViewState(state: Boolean) {
        setViewState(view?.playerIv, state)
    }

    private fun setPlayNextViewState(state: Boolean) {
        setViewState(view?.playerNextIv, state)
    }

    private fun setStartDurationViewState(state: Boolean) {
        setViewState(view?.startDurationTv, state)
    }

    private fun setSeekBarViewState(state: Boolean) {
//        setViewState(view?.seekBar, state)
        view?.seekBar?.visibility = if (state) View.VISIBLE else View.INVISIBLE
    }

    private fun setRemainDurationViewState(state: Boolean) {
        setViewState(view?.remainDurationTv, state)
    }

    private fun setRightViewState(state: Boolean) {
        setViewState(view?.rightControllerCl, state)
    }

    private fun setDanmuToggleViewState(state: Boolean) {
        setViewState(view?.danmuToggleIv, state)
    }

    private fun setCameraStandViewState(state: Boolean) {
        setViewState(view?.cameraStandToggleIv, state)
    }

    private fun setDefinitionViewState(state: Boolean) {
        setViewState(view?.definitionTv, state)
    }

    private fun setScreenToggleViewState(state: Boolean) {
        setViewState(view?.screenToggleIv, state)
    }

    private fun setCameraTipsViewState(state: Boolean) {
        setViewState(view?.changeCameraStandTipsTv, state)
    }

    private fun setViewState(view: View?, state: Boolean) {
        view?.visible(state)
    }

    override fun onPlayerEvent(eventCode: Int, bundle: Bundle?) {
        when (eventCode) {
            OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START -> {
//                开始播放视频
                isControllerShow = true
                showControllerAndDelayHidden()//默认显示控制条
                setScreenToggleViewState(isControllerShow)
                setChatViewState(isControllerShow && isFullScreen() && PlayerConfig.liveStatus == LiveStatus.LIVING)
                setPlayIconState(true)
                updateRightAndPlayMargin()
            }
            OnPlayerEventListener.PLAYER_EVENT_ON_PROVIDER_DATA_START -> hiddenController()
            OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_COMPLETE -> mTimerUpdateEnable = true
            DataInter.ProviderEvent.EVENT_VIDEO_INFO_READY -> if (bundle != null) {
//                传递播放视频信息，看是否需要显示title/在线人数、播放标识
                var serializable = bundle.getSerializable(EventKey.SERIALIZABLE_DATA)
                when (serializable) {
                    is VideoDetail -> {//普通视频
                        PlayerConfig.liveStatus = null
                        if (isControllerShow) {
                            showButtons()
                        }
                        view.titleTv?.text = serializable?.title.orEmpty()
                    }
                    is LiveVideoData -> {//直播或回放
                        updateStatusAndOnLineNumber(serializable)
                        view.titleTv?.text = if (isLive().not()) {
                            serializable?.title.orEmpty()
                        } else {
                            ""
                        }
                    }
                }

            }
            DataInter.ProviderEvent.EVENT_LIVE_CAMERA_LIST -> {
                refreshCameraStandList()
            }
            OnPlayerEventListener.PLAYER_EVENT_ON_RESUME -> {
                setPlayIconState(true)
                groupValue.putBoolean(DataInter.Key.KEY_IS_USER_PAUSE, false)
            }
            OnPlayerEventListener.PLAYER_EVENT_ON_PAUSE -> setPlayIconState(false)
            DataInter.ProviderEvent.EVENT_LIVE_SEND_CHAT_CONTENT -> {//发送聊天
                var chatContent =
                    bundle?.getString(DataInter.Key.KEY_LIVE_SEND_CHAT_CONTENT).orEmpty()
                SendDanmuDialog.setInputDanmu("")
                (context as? FragmentActivity)?.dismissDanmuDialog()
            }
            OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE -> {//当前视频播放完成
                playNext(true)
            }
            DataInter.ProviderEvent.EVENT_LIVE_CAMERA_LIST_ORIENTATION -> {//横竖屏切换
//                默认是竖屏
                isPortrait =
                    bundle?.getBoolean(DataInter.Key.KEY_LIVE_CAMERA_LIST_ORIENTATION, true)
                        ?: true
                refreshToggenScreenSrc()
                refreshCameraStandList()
                setTitleViewState(isPortrait.not() && isLive().not())
            }
        }
    }

    private fun setTitleViewState(isShow: Boolean) {
        view.titleTv?.visible(isShow)
    }

    private fun isLive(): Boolean = PlayerConfig.liveStatus == LiveStatus.LIVING

    private fun refreshToggenScreenSrc() {
        view?.screenToggleIv?.setImageResource(if (isPortrait) R.mipmap.ic_screen_toggle_expand else R.mipmap.ic_screen_toggle_packup)
    }

    private fun refreshCameraStandList() {
        cameraStandAdapter?.setData(
            CameraStandManager.getCameraStandBinderList2(),isPortrait
        )

            val cameraStandListIndex = CameraStandManager.getCameraStandSelectIndex()
            if (cameraStandListIndex >= 0) {
                view.cameraStandRv?.scrollToPosition(cameraStandListIndex)
            }
    }


    /**
     * 设置观看人数和直播状态
     */
    private fun updateStatusAndOnLineNumber(liveVideoData: LiveVideoData) {
        setOnLineNumViewState(liveVideoData.onLineNum > 0L && liveVideoData.liveStatus == LIVE_STATUS_LIVING)
        view?.onLineNumTv?.text =
            getString(R.string.live_component_on_line_person_num_format).format(liveVideoData.onLineNum.toString())
        setLiveLabelClViewState(liveVideoData.liveStatus == LIVE_STATUS_LIVING || liveVideoData.liveStatus == LIVE_STATUS_REVIEW)
        when (liveVideoData.liveStatus) {
            LIVE_STATUS_LIVING -> {//直播中
                setLiveLabelData(
                    R.drawable.ic_live,
                    R.string.live_component_live_tag_living,
                    R.color.color_ff5a36
                )
            }
            LIVE_STATUS_REVIEW -> {//直播回放
                setLiveLabelData(
                    R.drawable.ic_live_replay,
                    R.string.live_component_live_tag_replay,
                    R.color.color_8798af
                )
            }
        }
    }

    /**
     * 设置直播中标识样式和内容
     */
    private fun setLiveLabelData(
        @DrawableRes resId: Int,
        @StringRes titleId: Int,
        @ColorRes colorId: Int
    ) {
        getDrawable(resId)?.apply {
            setBounds(0, 0, this.intrinsicWidth, this.intrinsicHeight)
        }?.also {
            view?.liveLabelTv?.apply {
                text = getString(titleId)
                setCompoundDrawables(null, null, it, null)
            }
        }
        view?.liveLabelCL?.apply {
            ShapeExt.setShapeColorAndCorner(this, colorId, 2)
        }
    }

    override fun onErrorEvent(eventCode: Int, bundle: Bundle?) {
    }

    override fun onReceiverEvent(eventCode: Int, bundle: Bundle?) {
        when (eventCode) {
            DataInter.ReceiverEvent.EVENT_CODE_DEFINITION_CHANGE -> if (bundle != null) {
                setDefinitionName(bundle.getString(EventKey.STRING_DATA).orEmpty())
            }
        }
    }

    override fun onPrivateEvent(eventCode: Int, bundle: Bundle?): Bundle? {
        when (eventCode) {
            DataInter.ReceiverEvent.EVENT_CODE_UPDATE_SEEK -> if (bundle != null) {
                updateUI(
                    bundle.getInt(EventKey.INT_ARG1),
                    bundle.getInt(EventKey.INT_ARG2),
                    -1
                )
                requestResume(null)
            }
        }
        return super.onPrivateEvent(eventCode, bundle)

    }

    private fun updateUI(curr: Int, duration: Int, bufferPercentage: Int) {
        this.currentPosition = curr
        setSeekProgress(curr, duration, bufferPercentage)
        setCurrTime(curr)
        setTotalTime(duration)
    }

    private fun setTotalTime(duration: Int) {
        view?.remainDurationTv?.text = TimeUtil.getTime(mTimeFormat, duration.toLong())
    }

    private fun setCurrTime(curr: Int) {
        view?.startDurationTv?.text = TimeUtil.getTime(mTimeFormat, curr.toLong())
    }

    private fun setSeekProgress(curr: Int, duration: Int, bufferPercentage: Int) {
        view?.seekBar?.apply {
            max = duration
            progress = curr
            if (bufferPercentage in 0..100) {
                val secondProgress = (bufferPercentage * 1.0f / 100 * duration).toInt()
                this.secondaryProgress = secondProgress
            }
        }
    }

    override fun onCreateCoverView(context: Context?): View =
        View.inflate(context, R.layout.view_living_controller_cover, null)

    override fun onCoverAttachedToWindow() {
        super.onCoverAttachedToWindow()
//        changePlayStateCircleIcon(isFullScreen())
//        changeReplayIcon(isFullScreen())
        cleanHandler()
        if (isControllerShow) {
            showControllerAndDelayHidden()
        }
    }

    private fun setTopMargin(view: View?, topMargin: Int) {
        (view?.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
            this.topMargin = topMargin
        }?.also {
            view?.layoutParams = it
        }

    }

    override fun onCoverDetachedToWindow() {
        super.onCoverDetachedToWindow()
        cleanHandler()
    }

    override fun onReceiverBind() {
        super.onReceiverBind()
        initClickEvent()
        val statusBarHeight = StatusBarUtils.getStatusBarHeight(context)
        ShapeExt.setShapeColorAndCorner(view?.definitionTv, R.color.color_20ffffff, 7)
        view?.danmuToggleIv?.isSelected = getSpValue(KEY_DANMU_TOGGLE, true)
        view?.cameraStandRv?.apply {
            var layoutManager = LinearLayoutManager(context)
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL
            this.layoutManager = layoutManager
            cameraStandAdapter = CameraStandAdapter(context,clickListener = {
                handleCameraStand(it)
            })
            this.adapter = cameraStandAdapter
            setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    return when (event?.action) {
                        MotionEvent.ACTION_DOWN -> {
                            removeDelayHiddenMessage()
                            onTouchEvent(event)
                        }
                        MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                            sendDelayHiddenMessage()
                            onTouchEvent(event)
                        }
                        else -> onTouchEvent(event)
                    }
                }

            })

        }

        view?.seekBar?.setOnSeekBarChangeListener(mOnSeekBarChangeListener)
//        弹幕开关
        view?.danmuToggleIv?.setOnCheckedChangeListener(onCheckedChangeListener)

        refreshCameraStandList()

        groupValue.registerOnGroupValueUpdateListener(onGroupValueUpdateListener)
        ShapeExt.setShapeColorAndCorner(view?.chatTv, R.color.color_a86e6e6e, 13)
        ShapeExt.setShapeColorAndCorner(view?.changeCameraStandTipsTv, R.color.color_57000000, 8)
        ShapeExt.setGradientColorWithColor(
            view?.buttonsCL,
            startColor = getColor(R.color.transparent),
            endColor = getColor(R.color.color_000000),
            corner = 0
        )

    }

    //    处理选中机位
    private fun handleCameraStand(bean: CameraStandViewBean) {

        cameraStandAdapter?.setData(
            CameraStandManager.getCameraStandListWithSelected2(
                bean
            ),isPortrait
        )
            val cameraStandListIndex = CameraStandManager.getCameraStandListIndex(bean)
            if (cameraStandListIndex >= 0) {
                view.cameraStandRv?.scrollToPosition(cameraStandListIndex)
            }


        Bundle().apply {
            put("videoId", bean.cameraId)
        }.also {
            notifyReceiverEvent(DataInter.ReceiverEvent.EVENT_LIVE_CAMERA_VIDEO, it)
        }

    }

    private fun playNext(auto: Boolean) {
        Bundle().apply {
            putBoolean(DataInter.Key.KEY_VIDEO_PLAY_NEXT_AUTO, auto)
        }.also {
            notifyReceiverEvent(DataInter.ReceiverEvent.EVENT_REQUEST_NEXT_VIDEO, it)
        }
    }

    private fun initClickEvent() {
        view?.apply {
            backBtn?.onClick {
                notifyReceiverEvent(DataInter.ReceiverEvent.EVENT_REQUEST_BACK, null)
            }
            shareIv?.onClick {
                notifyReceiverEvent(DataInter.ReceiverEvent.EVENT_CODE_SHARE, null)
            }
            playerIv?.onClick {//播放按钮
//                如果是播放器播放，代表暂停或继续播放，如果是直播，就是刷新机位数据
                togglePlayState()
            }
            playerNextIv?.onClick {//播放下一个
                playNext(false)
            }
            definitionTv?.onClick {//清晰度
                notifyReceiverPrivateEvent(
                    DataInter.ReceiverKey.KEY_DEFINITION_COVER,
                    DataInter.ReceiverEvent.EVENT_REQUEST_SHOW_DEFINITION_LIST, null
                )
            }

            cameraStandToggleIv?.onClick {//机位切换开关
                var hadDisplay = getSpValue(DataInter.Key.KEY_CAMERA_STAND_TIPS, false)
                if (changeCameraStandCL?.visibility == View.VISIBLE) {
                    putSpValue(DataInter.Key.KEY_CAMERA_STAND_TIPS, true)
                }
                isShowCameraStand = changeCameraStandCL?.visibility != View.VISIBLE
                setChangeCameraViewState(isShowCameraStand)
                setCameraTipsViewState(changeCameraStandCL?.visibility == View.VISIBLE && hadDisplay.not())

            }
            screenToggleIv?.onClick {//全屏开关
                notifyReceiverEvent(DataInter.ReceiverEvent.EVENT_REQUEST_TOGGLE_SCREEN_STATE, null)
            }
            chatTv?.onClick {
                if (isLogin()) {
                    (getActivity() as? FragmentActivity)?.showDanmuDialog {
                        if (TextUtils.isEmpty(it.trim())) {
                            showToast(R.string.live_component_send_empty)
                        } else if (it.trim().length > 20) {
                            showToast(R.string.live_component_out_20)
                        } else {
                            notifyReceiverEvent(
                                DataInter.ReceiverEvent.EVENT_LIVE_CHAT,
                                Bundle().apply {
                                    putString(DataInter.Key.KEY_LIVE_CHAT_CONTENT, it.trim())
                                })
                        }
                    }
                } else {
                    if (isFullScreen()) {
                        notifyReceiverEvent(
                            DataInter.ReceiverEvent.EVENT_REQUEST_TOGGLE_SCREEN_STATE,
                            null
                        )
                        gotoLoginPage()
                    }
                }
            }

        }
    }

    private fun togglePlayState() {
        val playerStateGetter = playerStateGetter
        if (playerStateGetter != null) {
            val state = playerStateGetter.state
//            直播情况下刷新机位
            if (PlayerConfig.liveStatus == LiveStatus.LIVING) {
                requestReplay(null)
                return
            }
            if (state == IPlayer.STATE_STARTED) {
                requestPause(null)
                groupValue.putBoolean(DataInter.Key.KEY_IS_USER_PAUSE, true)
                setPlayIconState(false)
            } else if (state == IPlayer.STATE_PAUSED) {
                requestResume(null)
                setPlayIconState(true)
            } else if (state == IPlayer.STATE_STOPPED) {
                if (PreviewVideoPlayer.get()?.isCurrentTopActivity()==true && appState == AppState.FOREGROUND) {
                    PreviewVideoPlayer.get()?.requestRetry(currentPosition)
                    setPlayIconState(true)
                }
            }
        }
    }

    private fun toggleState(){
        val playerStateGetter = playerStateGetter
        if (playerStateGetter != null) {
            val state = playerStateGetter.state
            setPlayIconState(state == IPlayer.STATE_STARTED)

        }

    }

    private fun setPlayIconState(isSelected: Boolean) {
        view?.playerIv?.isSelected = isSelected
    }


    override fun onReceiverUnBind() {
        super.onReceiverUnBind()
        cleanHandler()
        groupValue.unregisterOnGroupValueUpdateListener(onGroupValueUpdateListener)
    }

    private fun cleanHandler() {
        removeDelayHiddenMessage()
        mHandler.removeCallbacks(mDelaySeekRunnable)
    }

    private fun isControllerShowEnable(): Boolean {
        return isControllerEnable
    }


    private fun toggleControllerState() {
        if (isControllerShow) {
            hiddenController()
        } else {
            showControllerAndDelayHidden()
        }
    }

    private fun showControllerAndDelayHidden() {
        showController()
        sendDelayHiddenMessage()
    }

    override fun onSingleTapConfirmed(event: MotionEvent?) {
        if (isControllerShowEnable()) {
            toggleControllerState()
        } else if (isControllerShow) {
            hiddenController()
        }
    }

    override fun onLongPress(event: MotionEvent?) {
    }

    override fun onDoubleTap(event: MotionEvent?) {
        togglePlayState()
    }

    override fun onDown(event: MotionEvent?) {
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float) {
    }

    override fun onEndGesture() {
    }

    override fun onTimerUpdate(curr: Int, duration: Int, bufferPercentage: Int) {
        if (!mTimerUpdateEnable || duration <= 0) {
            return
        }
        if (mTimeFormat == null) {
            mTimeFormat = TimeUtil.getFormat(duration.toLong())
        }
        updateUI(curr, duration, bufferPercentage)
    }

    override fun getCoverLevel(): Int {
        return levelLow(DataInter.CoverLevel.COVER_LEVEL_CONTROLLER)
    }

}