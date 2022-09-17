package com.kotlin.android.player.receivers

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.CompoundButton
import android.widget.SeekBar
import com.kk.taurus.playerbase.event.BundlePool
import com.kk.taurus.playerbase.event.EventKey
import com.kk.taurus.playerbase.event.OnPlayerEventListener
import com.kk.taurus.playerbase.player.IPlayer
import com.kk.taurus.playerbase.player.OnTimerUpdateListener
import com.kk.taurus.playerbase.receiver.BaseCover
import com.kk.taurus.playerbase.receiver.IReceiverGroup.OnGroupValueUpdateListener
import com.kk.taurus.playerbase.touch.OnTouchGestureListener
import com.kk.taurus.playerbase.utils.TimeUtil
import com.kotlin.android.core.statusbar.StatusBarUtils
import com.kotlin.android.app.data.entity.player.VideoInfoBean
import com.kotlin.android.app.data.entity.video.VideoDetail
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.utils.LogUtils
import com.kotlin.android.player.DataInter
import com.kotlin.android.player.R
import kotlinx.android.synthetic.main.view_player_new_controller_cover.view.*

/**
 * create by lushan on 2020/8/12
 * description:
 */
class NewControllerCover(context: Context) : BaseCover(context), OnTouchGestureListener,
    OnTimerUpdateListener {

    private var mTimerUpdateEnable = true
    private var isControllerEnable = true
    private val PLAY_STATE_ICON_MIN_W_H_DP = 41
    private val PLAY_STATE_ICON_MAX_W_H_DP = 62
    private var isControllerShow = false
    private val MSG_CODE_DELAY_HIDDEN_CONTROLLER = 101
    private var showFullScreen:Boolean = true
    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MSG_CODE_DELAY_HIDDEN_CONTROLLER -> hiddenController()
            }
        }
    }
    private var mOnSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            val duration: Int = getDuration()
            if (p2 && duration > 0) {
                updateUI(p1, duration, -1)
                if (isLocalVideo()) {
                    updateVideoSeekBar(0L)
                }
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
            if (isLocalVideo()) return
            updateVideoSeekBar()

        }

    }

    private fun updateVideoSeekBar(delayMillis: Long = 500L) {
        sendDelayHiddenMessage()
        mSeekTraceProgress = view?.cover_player_controller_seek_bar?.progress ?: 0
        mHandler.removeCallbacks(mDelaySeekRunnable)
        mHandler.postDelayed(mDelaySeekRunnable, delayMillis)
        notifyReceiverPrivateEvent(
            DataInter.ReceiverKey.KEY_LOG_RECEIVER,
            DataInter.ReceiverEvent.EVENT_CODE_SEEK_END_TRACING_TOUCH, null
        )
    }

    private var mTimeFormat: String? = null
    private val onCheckedChangeListener =
        CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            groupValue.putBoolean(
                DataInter.Key.KEY_DANMU_OPEN_STATE,
                isChecked
            )
        }
    private var mSeekTraceProgress = 0
    private var mDelaySeekRunnable = object : Runnable {
        override fun run() {
            if (mSeekTraceProgress < 0 || mSeekTraceProgress > getDuration()) {
                return
            }
            val bundle = BundlePool.obtain()
            bundle.putInt(EventKey.INT_DATA, mSeekTraceProgress)
            if (isAllComplete()){
                mTimerUpdateEnable = true
                groupValue.putBoolean(DataInter.Key.KEY_LIST_COMPLETE, false)
                requestReplay(bundle)
            }else{
            }
                requestSeek(bundle)
        }

    }

    private val onGroupValueUpdateListener: OnGroupValueUpdateListener =
        object : OnGroupValueUpdateListener {
            override fun filterKeys(): Array<String> {
                return arrayOf(
                    DataInter.Key.KEY_IS_FULL_SCREEN,
                    DataInter.Key.KEY_CONTROLLER_TIMER_UPDATE_ENABLE,
                    DataInter.Key.KEY_CONTROLLER_TOP_ENABLE,
                    DataInter.Key.KEY_NEED_RECOMMEND_LIST,
                    DataInter.Key.KEY_NEED_VIDEO_DEFINITION,
                    DataInter.Key.KEY_CONTROLLER_SHARE_ENABLE,
                    DataInter.Key.KEY_CONTROLLER_FULL_ENABLE,
                    DataInter.Key.KEY_DANMU_EDIT_ENABLE,
                    DataInter.Key.KEY_NEED_PLAY_NEXT,
                    DataInter.Key.KEY_USER_GUIDE_STATE,
                    DataInter.Key.KEY_LIST_COMPLETE,
                    DataInter.Key.KEY_CURRENT_DEFINITION
                )
            }

            override fun onValueUpdate(s: String, o: Any) {
                if (DataInter.Key.KEY_IS_FULL_SCREEN == s) {
                    val isFullScreen = o as Boolean
                    if (isFullScreen) {
                        setScreenSwitchIconState(false)
                    } else if (isControllerShow) {
                        setScreenSwitchIconState(true)
                    }
                    setPlayStateSmallIconState(isFullScreen)
                    changePlayStateCircleIcon(isFullScreen)
                    changeReplayIcon(isFullScreen)
                } else if (DataInter.Key.KEY_CONTROLLER_TIMER_UPDATE_ENABLE == s) {
                    mTimerUpdateEnable = o as Boolean
                } else if (DataInter.Key.KEY_CONTROLLER_TOP_ENABLE == s) {
                    val topEnable = o as Boolean
                    if (isControllerShow) {
                        setTopContainerState(topEnable)
                    }
                } else if (DataInter.Key.KEY_NEED_RECOMMEND_LIST == s) {
                    val needRecommendState = o as Boolean
                    setRecommendVideosState(needRecommendState)
                    setPlayNextIconState(needRecommendState)
                } else if (DataInter.Key.KEY_NEED_VIDEO_DEFINITION == s) {
                    setDefinitionState(o as Boolean)
                } else if (DataInter.Key.KEY_CONTROLLER_SHARE_ENABLE == s) {
                    setShareIconState(o as Boolean)
                } else if (DataInter.Key.KEY_CONTROLLER_FULL_ENABLE == s) {
                    showFullScreen = o as Boolean
                    setScreenSwitchIconState(o as Boolean)
                } else if (DataInter.Key.KEY_DANMU_EDIT_ENABLE == s) {
                    val state = o as Boolean
                    if (state && isControllerShow) {
//                    setEditDanmuIconContainerState(true)
                    } else if (!state) {
//                    setEditDanmuIconContainerState(false)
                    }
                } else if (DataInter.Key.KEY_NEED_PLAY_NEXT == s) {
                    setPlayNextIconState(o as Boolean)
                } else if (DataInter.Key.KEY_USER_GUIDE_STATE == s) {
                    val state = o as Boolean
                    isControllerEnable = !state
                    if (!isErrorState() && state) {
                        hiddenController()
                    }
                } else if (DataInter.Key.KEY_LIST_COMPLETE == s) {
                    val state = o as Boolean
//                    LogUtils.e("播放本地视频---播放--$s -- ${state}")
                    setReplayIconContainerState(state)
                    if (state) {
                        notifyReceiverEvent(DataInter.ReceiverEvent.EVENT_VIDEO_COMPLETE, null)
                        showController()
                    }
                } else if (DataInter.Key.KEY_CURRENT_DEFINITION == s) {
                    val definitionName = o as String
                    setDefinitionName(definitionName)
                }
            }
        }

    private fun setShareIconState(state: Boolean) {
        view?.video_layout_player_top_share_iv?.visible(state)
    }

    private fun setPlayNextIconState(state: Boolean) {
        view?.player_next_icon?.visible(state)
    }

    private fun isErrorState(): Boolean {
        return groupValue.getBoolean(DataInter.Key.KEY_ERROR_SHOW_STATE)
    }

    private fun setDefinitionName(definitionName: String) {
        if (!TextUtils.isEmpty(definitionName)) view?.view_player_bottom_definition_tv?.text =
            definitionName
    }

    /**
     * 大小屏切换时，动态调整圆形播放状态按钮的大小
     *
     * @param isFullScreen
     */
    private fun changePlayStateCircleIcon(isFullScreen: Boolean) {
        val layoutParams: ViewGroup.LayoutParams? =
            view?.layout_controller_cover_play_pause_rl?.layoutParams
        var px: Int = PLAY_STATE_ICON_MIN_W_H_DP.dp
        if (isFullScreen) {
            px = PLAY_STATE_ICON_MAX_W_H_DP.dp
        }
        layoutParams?.width = px
        layoutParams?.height = px

        view?.layout_controller_cover_play_pause_rl?.layoutParams = layoutParams
    }

    /**
     * 大小屏切换时，动态调整重播按钮的大小
     *
     * @param isFullScreen
     */
    private fun changeReplayIcon(isFullScreen: Boolean) {
        val layoutParams: ViewGroup.LayoutParams? =
            view?.layout_controller_cover_replay_iv?.layoutParams
        var px: Int = PLAY_STATE_ICON_MIN_W_H_DP.dp
        if (isFullScreen) {
            px = PLAY_STATE_ICON_MAX_W_H_DP.dp
        }
        layoutParams?.width = px
        layoutParams?.height = px
        view?.layout_controller_cover_replay_iv?.layoutParams = layoutParams
    }

    private fun removeDelayHiddenMessage() {
        mHandler.removeMessages(MSG_CODE_DELAY_HIDDEN_CONTROLLER)
    }

    private fun sendDelayHiddenMessage() {
        removeDelayHiddenMessage()
        mHandler.sendEmptyMessageDelayed(MSG_CODE_DELAY_HIDDEN_CONTROLLER, 5000)
    }

    private fun setScreenSwitchIconState(state: Boolean) {
        view?.video_layout_player_screen_switch_iv?.visible(state && showFullScreen)
    }

    private fun setPlayStateSmallIconState(state: Boolean) {
        view?.player_state_icon?.visible(state)
    }

    private fun setBottomBgState(show: Boolean) {
        val drawable = context.getDrawable(R.drawable.ic_player_peogress_bg)
        view?.cover_player_controller_bottom_progress_bar?.background = if (show) drawable else null
    }

    private fun setTopContainerState(state: Boolean) {
        view?.controller_cover_top_container?.visible(state)
    }

    private fun setButtonsContainerState(state: Boolean) {
        view?.rl_buttons_container?.visible(state)
    }

    private fun setBottomContainerState(state: Boolean) {
        view?.rl_bottom_container?.visible(state)
        setScreenSwitchIconState(state)
    }

    private fun setRecommendVideosState(state: Boolean) {
        view?.view_player_bottom_section_tv?.visible(state)
    }

    private fun setDefinitionState(state: Boolean) {
        view?.view_player_bottom_definition_tv?.visible(state)
    }

    private fun setBottomProgressBarState(state: Boolean) {
        view?.cover_player_controller_bottom_progress_bar?.visible(state)
    }

    private fun isFullScreen(): Boolean {
        return groupValue.getBoolean(DataInter.Key.KEY_IS_FULL_SCREEN)
    }

    private fun isNeedBottomProgressBar(): Boolean {
        return groupValue.getBoolean(DataInter.Key.KEY_NEED_BOTTOM_PROGRESS_BAR)
    }

    private fun updateControllerShowState(show: Boolean) {
        groupValue.putBoolean(DataInter.Key.KEY_IS_CONTROLLER_SHOW, show)
    }

    override fun onCoverAttachedToWindow() {
        super.onCoverAttachedToWindow()
        changePlayStateCircleIcon(isFullScreen())
        changeReplayIcon(isFullScreen())
        cleanHandler()
        if (isControllerShow) {
            showControllerAndDelayHidden()
        }
    }


    private fun showControllerAndDelayHidden() {
        showController()
        sendDelayHiddenMessage()
    }

    override fun onReceiverUnBind() {
        super.onReceiverUnBind()
        cleanHandler()
        groupValue.unregisterOnGroupValueUpdateListener(onGroupValueUpdateListener)

    }

    override fun onCoverDetachedToWindow() {
        super.onCoverDetachedToWindow()
        cleanHandler()
    }

    private fun cleanHandler() {
        removeDelayHiddenMessage()
        mHandler.removeCallbacks(mDelaySeekRunnable)
    }

    private fun hiddenController() {
        isControllerShow = false
        setBottomBgState(false)
        //hide
        setTopContainerState(false)
        setButtonsContainerState(false)
        setBottomContainerState(false)
        setScreenSwitchIconState(false)
        setRecommendVideosState(false)
        setDefinitionState(false)

        if (isNeedBottomProgressBar()) {
            setBottomProgressBarState(true)
        }
        //update show state
        updateControllerShowState(false)
        removeDelayHiddenMessage()
    }

    private fun isPreparing(): Boolean {
        val playerStateGetter = playerStateGetter
        if (playerStateGetter != null) {
            val state = playerStateGetter.state
            return state == IPlayer.STATE_INITIALIZED || state == IPlayer.STATE_PREPARED
        }
        return true
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

    private fun getCurrentUrl(): String? {
        return groupValue.getString(DataInter.Key.KEY_CURRENT_URL)
    }

    private fun isLocalVideo(): Boolean {
        return URLUtil.isValidUrl(getCurrentUrl().orEmpty()).not()
    }

    private fun showController() {
        isControllerShow = true
        setBottomBgState(true)
        setScreenSwitchIconState(!isFullScreen())
        if (isPreparing()) {
            setPlayStateSmallIconState(false)
            setTopContainerState(false)
            setBottomContainerState(false)
        } else {
            setTopContainerState(isTopEnable())
            setBottomContainerState(true)
        }
        if (isRecommendListEnable()) {
            setRecommendVideosState(true)
        }
        if (isVideoDefinitionEnable()) {
            setDefinitionState(true)
        }
//        if (isDanmuSwitchEnable()) {
//            setDanmuToggleState(true)
//        }
        setBottomProgressBarState(false)
        showButtons()
        //update show state
        updateControllerShowState(true)
    }


    private fun isAllComplete(): Boolean {
        return groupValue.getBoolean(DataInter.Key.KEY_LIST_COMPLETE)
    }

    private fun setPlayStateBigIconContainerState(state: Boolean) {
        view?.layout_controller_cover_play_pause_rl?.visible(state)
    }

    private fun setReplayIconContainerState(state: Boolean) {
        view?.layout_controller_cover_replay_iv?.visible(state)
    }

    private fun showButtons() {
        setButtonsContainerState(true)
        if (isAllComplete()) {
            if (isLocalVideo()) {
                var state = getDuration() - getCurrentPosition() > 1000 && !isPreparing()
                setPlayStateBigIconContainerState(state)
                setReplayIconContainerState(state.not())
            } else {
                setPlayStateBigIconContainerState(false)
                setReplayIconContainerState(true)
            }

        } else {
            setReplayIconContainerState(false)
            if (!isPreparing()) {
                setPlayStateBigIconContainerState(true)
            }
        }
        //edit danmu icon show state by danmuEditEnable
//        setEditDanmuIconContainerState(isEditDanmuEnable())
    }

    private fun setSeekProgress(curr: Int, duration: Int, bufferPercentage: Int) {
        view?.cover_player_controller_seek_bar?.apply {
            max = duration
            progress = curr
            if (bufferPercentage in 1..100) {
                val secondProgress = (bufferPercentage * 1.0f / 100 * duration).toInt()
                this.secondaryProgress = secondProgress
            }
            if (duration != 0) {
                setReplayIconContainerState(curr.toFloat() / duration >= 1f)
            } else {
                setReplayIconContainerState(false)
            }
        }


    }

    private fun setCurrTime(curr: Int) {
        view?.cover_player_controller_text_view_curr_time?.text =
            TimeUtil.getTime(mTimeFormat, curr.toLong())
    }

    private fun setTotalTime(duration: Int) {
        view?.cover_player_controller_text_view_total_time?.text =
            TimeUtil.getTime(mTimeFormat, duration.toLong())
    }


    private fun updateUI(curr: Int, duration: Int, bufferPercentage: Int) {
        setSeekProgress(curr, duration, bufferPercentage)
        setCurrTime(curr)
        setTotalTime(duration)
        updateBottomProgress(curr, duration, bufferPercentage)
    }


    private fun updateBottomProgress(curr: Int, duration: Int, bufferPercentage: Int) {
        view?.cover_player_controller_bottom_progress_bar?.apply {
            max = duration
            progress = curr
            if (bufferPercentage in 1..100) {
                val secondProgress = (bufferPercentage * 1.0f / 100 * duration).toInt()
                this.secondaryProgress = secondaryProgress
            }
//            LogUtils.e("播放本地视频---updateBottomProgress--$curr")
//            setReplayIconContainerState(curr >= 100)
        }

    }

    private fun getDuration(): Int {
        val playerStateGetter = playerStateGetter
        return playerStateGetter?.duration ?: 0
    }

    private fun getCurrentPosition(): Int {
        val playerStateGetter = playerStateGetter
        return playerStateGetter?.currentPosition ?: 0
    }

    private fun setPlayIconState(isPlaying: Boolean) {
        view?.player_state_icon?.isSelected = !isPlaying
        view?.layout_controller_cover_play_pause_tb?.isSelected = !isPlaying
    }

    override fun onPlayerEvent(eventCode: Int, bundle: Bundle?) {
        when (eventCode) {
            OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START -> {
                if (!isFullScreen() && !isControllerShow) {
                    setScreenSwitchIconState(false)
                }
                setPlayIconState(true)
            }
            OnPlayerEventListener.PLAYER_EVENT_ON_PROVIDER_DATA_START -> hiddenController()
            OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_COMPLETE -> mTimerUpdateEnable = true
            DataInter.ProviderEvent.EVENT_VIDEO_INFO_READY -> if (bundle != null) {
                val bean: VideoDetail? =
                    bundle.getSerializable(EventKey.SERIALIZABLE_DATA) as? VideoDetail
                setTitle(bean?.title.orEmpty())
            }
            OnPlayerEventListener.PLAYER_EVENT_ON_RESUME -> {
                setPlayIconState(true)
                groupValue.putBoolean(DataInter.Key.KEY_IS_USER_PAUSE, false)
            }
            OnPlayerEventListener.PLAYER_EVENT_ON_PAUSE -> setPlayIconState(false)
        }
    }


    private fun setTitle(text: String) {
        if (!TextUtils.isEmpty(text)) {
            view?.player_title?.text = text
        }
    }

    override fun onReceiverEvent(eventCode: Int, bundle: Bundle?) {
        when (eventCode) {
            DataInter.ReceiverEvent.EVENT_CODE_DEFINITION_CHANGE -> if (bundle != null) {
                setDefinitionName(bundle.getString(EventKey.STRING_DATA).orEmpty())
            }
        }
    }

    override fun onErrorEvent(eventCode: Int, bundle: Bundle?) {
    }

    override fun onPrivateEvent(eventCode: Int, bundle: Bundle?): Bundle? {
        when (eventCode) {
            DataInter.ReceiverEvent.EVENT_CODE_UPDATE_SEEK -> if (bundle != null) {
                updateUI(
                    bundle.getInt(EventKey.INT_ARG1),
                    bundle.getInt(EventKey.INT_ARG2),
                    -1
                )
            }
        }
        return super.onPrivateEvent(eventCode, bundle)
    }

    override fun onCreateCoverView(context: Context?): View {
        return View.inflate(context, R.layout.view_player_new_controller_cover, null)
    }

    private fun initClickEvent() {
        view?.apply {
            player_back_icon?.onClick {
                notifyReceiverEvent(DataInter.ReceiverEvent.EVENT_REQUEST_BACK, null)
            }
            layout_controller_cover_edit_barrage_iv?.onClick {
                notifyReceiverEvent(DataInter.ReceiverEvent.EVENT_REQUEST_EDIT_DANMU, null)
            }
            layout_controller_cover_play_pause_tb?.onClick {//右下角播放、暂停按钮
                togglePlayState()
            }
            video_layout_player_control_review_iv?.onClick {//重新播放按钮
                requestReplay(null)
                setReplayIconContainerState(false)
                groupValue.putBoolean(DataInter.Key.KEY_LIST_COMPLETE, false)
            }
            player_state_icon?.onClick {
                togglePlayState()
            }
            player_next_icon?.onClick {
                notifyReceiverEvent(DataInter.ReceiverEvent.EVENT_REQUEST_NEXT_VIDEO, null)
            }

            view_player_bottom_section_tv?.onClick {
                val bundle = BundlePool.obtain()
                bundle.putBoolean(EventKey.BOOL_DATA, true)
                notifyReceiverEvent(
                    DataInter.ReceiverEvent.EVENT_REQUEST_RECOMMEND_LIST_CHANGE,
                    bundle
                )
            }

            view_player_bottom_definition_tv?.onClick {
                notifyReceiverPrivateEvent(
                    DataInter.ReceiverKey.KEY_DEFINITION_COVER,
                    DataInter.ReceiverEvent.EVENT_REQUEST_SHOW_DEFINITION_LIST, null
                )
            }

            video_layout_player_screen_switch_iv?.onClick {
                notifyReceiverEvent(DataInter.ReceiverEvent.EVENT_REQUEST_TOGGLE_SCREEN_STATE, null)
            }
            video_layout_player_top_share_iv?.onClick {
                notifyReceiverEvent(DataInter.ReceiverEvent.EVENT_CODE_SHARE, null)
            }


        }
    }

    override fun onReceiverBind() {
        super.onReceiverBind()
        initClickEvent()
        val statusBarHeight = StatusBarUtils.getStatusBarHeight(context)
        view?.controller_cover_top_container?.apply {
            setPadding(this.paddingLeft, statusBarHeight, this.paddingRight, this.paddingBottom)
        }
        view?.cover_player_controller_seek_bar?.apply {
            setOnSeekBarChangeListener(mOnSeekBarChangeListener)
        }

        view?.video_layout_player_top_barrage_open_state_tb?.setOnCheckedChangeListener(
            onCheckedChangeListener
        )

        groupValue.registerOnGroupValueUpdateListener(onGroupValueUpdateListener)

    }

    private fun togglePlayState() {
        val playerStateGetter = playerStateGetter
        if (playerStateGetter != null) {
            val state = playerStateGetter.state
            if (state == IPlayer.STATE_STARTED) {
                requestPause(null)
                groupValue.putBoolean(DataInter.Key.KEY_IS_USER_PAUSE, true)
                setPlayIconState(false)
            } else if (state == IPlayer.STATE_PAUSED) {
                requestResume(null)
                setPlayIconState(true)
            }
        }
    }

    override fun onEndGesture() {
    }

    private fun isControllerShowEnable(): Boolean {
        return isControllerEnable && !isErrorState()
    }


    private fun toggleControllerState() {
        if (isControllerShow) {
            hiddenController()
        } else {
            showControllerAndDelayHidden()
        }
    }

    override fun onSingleTapConfirmed(event: MotionEvent?) {
        if (isControllerShowEnable()) {
            toggleControllerState()
        } else if (isControllerShow) {
            hiddenController()
        }
    }

    override fun onDown(event: MotionEvent?) {

    }

    override fun onDoubleTap(event: MotionEvent?) {
        togglePlayState()
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float) {
    }

    override fun onLongPress(event: MotionEvent?) {
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