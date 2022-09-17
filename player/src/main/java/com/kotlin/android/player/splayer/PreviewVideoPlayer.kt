package com.kotlin.android.player.splayer

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.FragmentActivity
import com.kk.taurus.playerbase.assist.AssistPlay
import com.kk.taurus.playerbase.assist.OnAssistPlayEventHandler
import com.kk.taurus.playerbase.entity.DataSource
import com.kk.taurus.playerbase.event.EventKey
import com.kk.taurus.playerbase.event.OnPlayerEventListener
import com.kk.taurus.playerbase.receiver.IReceiver
import com.kk.taurus.playerbase.receiver.IReceiverGroup
import com.kk.taurus.playerbase.receiver.IReceiverGroup.OnReceiverFilter
import com.kk.taurus.playerbase.receiver.ReceiverGroup
import com.kotlin.android.app.data.entity.player.VideoInfo
import com.kotlin.android.audio.floatview.component.aduiofloat.constant.sendCloseEvent
import com.kotlin.android.core.CoreApp
import com.kotlin.android.ktx.bean.AppState
import com.kotlin.android.ktx.ext.appState
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.ktx.utils.LogUtils
import com.kotlin.android.player.*
import com.kotlin.android.player.bean.MTimeVideoData
import com.kotlin.android.player.bean.StatisticsInfo
import com.kotlin.android.player.dataprovider.MTimeDataProvider
import com.kotlin.android.player.eventproducer.AirPlaneChangeEventProducer
import com.kotlin.android.player.receivers.UserGuideCover
import com.kotlin.android.player.receivers.VideoDefinitionCover

/**
 * create by lushan on 2020/9/1
 * description: 预告片
 */
class PreviewVideoPlayer : BSPlayer(CoreApp.instance) {
    companion object {
        var instance: PreviewVideoPlayer? = null
        fun get(): PreviewVideoPlayer? {
            if (null == instance) {
                synchronized(PreviewVideoPlayer::class.java) {
                    if (null == instance) {
                        instance = PreviewVideoPlayer()
                    }
                }
            }
            return instance
        }
    }

    var sizeListener:((Int,Int)->Unit)? = null

    private var mHostActivity: FragmentActivity? = null
    private var mStatisticsInfo: StatisticsInfo? = null

    //    private val onNextPlayListener: OnNextPlayListener? = null
    private var onRequestInputDialogListener: (() -> Unit)? = null//显示弹窗
    private var onBackRequestListener: (() -> Unit)? = null//返回

    private val needRecommendList = false
    private var mDataSource: DataSource? = null

    private var mPageRefer: String? = null

    init {
        mRelationAssist.setDataProvider(MTimeDataProvider())
        mRelationAssist.superContainer.setBackgroundColor(Color.BLACK)
        //add air plane setting change event producer
        //add air plane setting change event producer
        mRelationAssist.superContainer.addEventProducer(AirPlaneChangeEventProducer(mAppContext))

        //set event handler
        mRelationAssist.setEventAssistHandler(object : OnAssistPlayEventHandler() {

            override fun requestRetry(assistPlay: AssistPlay, bundle: Bundle?) {
                if (PlayerHelper.isTopActivity(mHostActivity) && appState == AppState.FOREGROUND) {
                    super.requestRetry(assistPlay, bundle)
                }
            }

            override fun onAssistHandle(assist: AssistPlay?, eventCode: Int, bundle: Bundle?) {
                super.onAssistHandle(assist, eventCode, bundle)
//                if (eventCode == DataInter.ProducerEvent.EVENT_AIRPLANE_STATE_CHANGE){
//                    val networkState = NetworkUtils.getNetworkState(mAppContext)
//                    if (networkState> PConst.NETWORK_STATE_WIFI){
//                        requestStop(assist,bundle)
//                    }
//                }
            }
        })
    }

//    fun setDataProvider()

    fun isCurrentTopActivity() = PlayerHelper.isTopActivity(mHostActivity)

    fun setOnRequestInputDialogListener(block: (() -> Unit)?) {
        this.onRequestInputDialogListener = block
    }

    fun setOnBackRequestListener(block: (() -> Unit)?) {
        onBackRequestListener = block
    }

    fun requestRetry() {
        mRelationAssist.play()
    }

    fun requestRetry(msc: Int) {
        mRelationAssist.rePlay(msc)
    }

    fun requestStop() {
        mRelationAssist.stop()
    }

    fun getCurrentVid(): Long {
        val groupValue = getGroupValue()
        if (groupValue != null) {
            val vid = groupValue.getLong(DataInter.Key.KEY_CURRENT_VIDEO_ID)
            return vid
        }
        return -1L
    }

    private fun parseInt(id: String?): Int {
        try {
            if (!TextUtils.isEmpty(id)) return id?.toInt() ?: -1
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return -1
    }

    fun setStatisticsInfo(statisticsInfo: StatisticsInfo?) {
        mStatisticsInfo = statisticsInfo
        updateStatisticsInfo()
    }

    fun updateAutoPlayFlag(auto: Boolean) {
        val flag: String = if (auto) 1.toString() else 0.toString()
        updateGroupValue(DataInter.Key.KEY_AUTO_PLAY_FLAG, flag)
    }

    private fun updateStatisticsInfo() {
        if (mStatisticsInfo != null) {
            updateGroupValue(DataInter.Key.KEY_STATISTICS_INFO, mStatisticsInfo)
        }
    }

    fun attachActivity(activity: FragmentActivity, pageRefer: String) {
        mHostActivity = activity
        mPageRefer = pageRefer
    }

    fun isEqualData(vid: Long): Boolean {
        val currentVid = getCurrentVid()
        return currentVid == vid
    }

    override fun onSetDataSource(dataSource: DataSource?) {
        if (dataSource != null) {
            this.mDataSource = dataSource
        }
        if (dataSource != null) {
            updateVideoInfo(dataSource)
            if (dataSource is MTimeVideoData) {
                dataSource.startPos =
                    MemoryPlayRecorder.getRecordPlayTime((dataSource).videoId.toInt())
            }
        }
        //check list play soon complete
//        if (PlayerConfig.isListMode) {
//            val soonComplete: Boolean = CategoryDataManager.get().isListComplete()
//            updateGroupValue(DataInter.Key.KEY_NEED_PLAY_NEXT, !soonComplete)
//        }
    }


    private fun updateVideoInfo(dataSource: DataSource?) {
        val groupValue = getGroupValue()
        if (groupValue != null && dataSource != null && dataSource is MTimeVideoData) {
            val data: MTimeVideoData = dataSource
            val info: VideoInfo = VideoInfo(data.videoId, data.source)
            groupValue.putObject(DataInter.Key.KEY_VIDEO_INFO, info)
        }
    }

    fun updateLandScapeDanmuEditState(edit: Boolean) {
        updateGroupValue(DataInter.Key.KEY_DANMU_COVER_IN_LANDSCAPE_EDIT_STATE, edit)
    }

    fun logicResourceResume(dataSource: DataSource?) {
        sendCloseEvent()//关闭音频
        if (isInPlaybackState()) {
            logicResume()
        } else if (dataSource != null && dataSource is MTimeVideoData) {
            val data: MTimeVideoData = dataSource
            dataSource.startPos = MemoryPlayRecorder.getRecordPlayTime(data.videoId.toInt())
            play(dataSource, true)
        }
    }

    fun logicPause(isFoucePause: Boolean = true) {
        val groupValue = getGroupValue()
        var needHandlePause = false
        if (groupValue != null) {
            val isErrorState = groupValue.getBoolean(DataInter.Key.KEY_ERROR_SHOW_STATE)
            val isAllComplete = groupValue.getBoolean(DataInter.Key.KEY_LIST_COMPLETE)
            if (!isErrorState && isAllComplete) {
                needHandlePause = true
            }
        } else {
            needHandlePause = true
        }
        if (isFoucePause) {
            pause()
        }
        if (needHandlePause) {
            if (isInPlaybackState()) {
                pause()
            } else {
                stop()
                reset()
            }
        }
    }

    fun logicResume() {
        val groupValue = getGroupValue()
        if (groupValue != null) {
            val isErrorState = groupValue.getBoolean(DataInter.Key.KEY_ERROR_SHOW_STATE)
            val isAllComplete = groupValue.getBoolean(DataInter.Key.KEY_LIST_COMPLETE)
            val isUserPause = groupValue.getBoolean(DataInter.Key.KEY_IS_USER_PAUSE)
            if (!isErrorState && !isAllComplete && !isUserPause) {
                if (isInPlaybackState()) {
                    resume()
                } else {
                    rePlay(MemoryPlayRecorder.getRecordPlayTime(getCurrentVid().toInt()))
                }
            }
        }
    }

    override fun onCallBackPlayerEvent(eventCode: Int, bundle: Bundle?) {
        when (eventCode) {
            OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE ->
                updateAutoPlayFlag(true)
//                if (CategoryDataManager.get().isListComplete()) {
//                    updateGroupValue(DataInter.Key.KEY_LIST_COMPLETE, true)
//                } else {
//                    updateAutoPlayFlag(true)
//                    postNext()
//                }
            OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_SIZE_CHANGE->{
                onCallBackReceiverEvent(DataInter.ReceiverEvent.EVENT_VIDEO_SIZE,bundle)
            }
        }

    }

    fun sendReceiverEvent(eventCode: Int, bundle: Bundle?, onReceiverFilter: OnReceiverFilter?) {
        val receiverGroup = getReceiverGroup()
        receiverGroup?.forEach(
            onReceiverFilter,
            { iReceiver -> iReceiver.onReceiverEvent(eventCode, bundle) })
    }

    fun sendReceiverPrivateEvent(receiverKey: String?, eventCode: Int, bundle: Bundle?) {
        val receiverGroup = getReceiverGroup()
        if (receiverGroup != null) {
            val receiver = receiverGroup.getReceiver<IReceiver>(receiverKey)
            receiver?.onPrivateEvent(eventCode, bundle)
        }
    }

//    private fun postNext() {
//        Handler(Looper.getMainLooper()).post { playNext() }
//    }

//    private fun playNext() {
//        CategoryDataManager.get().next(object : OnNextVideoListener() {
//            fun onNextReady(item: RecommendVideoItem, type: Int, index: Int) {
//                if (item != null) {
//                    if (onNextPlayListener != null) {
//                        onNextPlayListener.onNextPlay(item, type, index)
//                    }
//                    val data: MTimeVideoData = MTimeVideoData(item.getvId().toString(), item.getVideoSource())
//                    play(data, true)
//                }
//            }
//
//            fun onFailure() {
//                //TODO load next video error, maybe do something.
//            }
//        })
//    }

    override fun onCallBackErrorEvent(eventCode: Int, bundle: Bundle?) {
    }

    override fun onCallBackReceiverEvent(eventCode: Int, bundle: Bundle?) {
        when (eventCode) {
            DataInter.ReceiverEvent.EVENT_REQUEST_BACK -> onBackRequestListener?.invoke()
            DataInter.ReceiverEvent.EVENT_REQUEST_TOGGLE_SCREEN_STATE -> mHostActivity?.requestedOrientation =
                if (isLandScape()) ActivityInfo.SCREEN_ORIENTATION_PORTRAIT else ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            DataInter.ReceiverEvent.EVENT_REQUEST_NEXT_VIDEO -> {
                updateAutoPlayFlag(false)
//                postNext()
            }
            DataInter.ReceiverEvent.EVENT_REQUEST_EDIT_DANMU -> if (onRequestInputDialogListener != null && isLandScape()) {
                onRequestInputDialogListener?.invoke()
            }
            DataInter.ReceiverEvent.EVENT_VIDEO_SIZE->{
                val mVideoWidth = bundle?.getInt(EventKey.INT_ARG1).orZero()
                val mVideoHeight = bundle?.getInt(EventKey.INT_ARG2).orZero()
                LogUtils.e("视频播放器","视频预览：视频宽$$mVideoWidth -- 视频高：$mVideoHeight")
                if (mVideoHeight==0 || mVideoWidth==0) return
                sizeListener?.invoke(mVideoWidth,mVideoHeight)
            }
        }
    }

    fun isLandScape(activity: FragmentActivity? = null): Boolean {
        val act = activity ?: mHostActivity
        act ?: return false
        return act?.resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    //    fun setReceiverGroupFullScreenState(context: Context?, recommendListCover: RecommendListCover) {
//        val params = ConfigParams()
//        params.recommendListCover = recommendListCover
//        setReceiverGroupConfigState(context, ISPayer.RECEIVER_GROUP_CONFIG_FULL_SCREEN_STATE, params)
//    }

    fun setReceiverGroupConfigState(
        context: Context?,
        configState: Int,
        group: ReceiverGroup? = null
    ) {
        var receiverGroup = getReceiverGroup()
        if (receiverGroup == null) {
            context?.apply {
                if (group != null) {
                    receiverGroup = group
                } else {
                    receiverGroup = ReceiverGroupManager.getStandardReceiverGroup(context)
                }
            }
            setReceiverGroup(receiverGroup)
            updateStatisticsInfo()
        }else{
            if (group!=null){
                receiverGroup = group
                setReceiverGroup(receiverGroup)
                updateStatisticsInfo()
            }
        }
        updateGroupValue(
            DataInter.Key.KEY_STATISTICS_PAGE_REFER,
            if (mPageRefer == null) "" else mPageRefer
        )
        when (configState) {
            ISPayer.RECEIVER_GROUP_CONFIG_LIST_STATE -> {
                //remove some receivers
                receiverGroup?.removeReceiver(DataInter.ReceiverKey.KEY_DEFINITION_COVER)
                receiverGroup?.removeReceiver(DataInter.ReceiverKey.KEY_GESTURE_COVER)
                receiverGroup?.removeReceiver(DataInter.ReceiverKey.KEY_RECOMMEND_LIST_COVER)
                receiverGroup?.removeReceiver(DataInter.ReceiverKey.KEY_USER_GUIDE_COVER)
                //config params
                updateGroupValue(DataInter.Key.KEY_DANMU_SHOW_STATE, false)
                updateGroupValue(DataInter.Key.KEY_NEED_VIDEO_DEFINITION, false)
                updateGroupValue(DataInter.Key.KEY_NEED_RECOMMEND_LIST, false)
                updateGroupValue(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, false)
                updateGroupValue(DataInter.Key.KEY_NEED_PLAY_NEXT, false)
                updateGroupValue(DataInter.Key.KEY_IS_FULL_SCREEN, false)
                updateGroupValue(DataInter.Key.KEY_DANMU_EDIT_ENABLE, false)
                updateGroupValue(DataInter.Key.KEY_NEED_ERROR_TITLE, false)
            }
            ISPayer.RECEIVER_GROUP_CONFIG_DETAIL_PORTRAIT_STATE -> {
                configDetailProtraitState(receiverGroup, context, true)
            }
            ISPayer.RECEIVER_GROUP_CONFIG_DETAIL_PORTRAIT_WITH_OUT_TITLE_STATE -> {
                configDetailProtraitState(receiverGroup, context, false)
            }
            ISPayer.RECEIVER_GROUP_CONFIG_FULL_SCREEN_STATE -> {
                configDetailLandState(receiverGroup, context, true)
            }
            ISPayer.RECEIVER_GROUP_CONFIG_FULL_SCREEN_WITH_OUT_TITLE_STATE -> {
                configDetailLandState(receiverGroup, context, false)
            }

        }
    }

    private fun configDetailLandState(
        receiverGroup: IReceiverGroup?,
        context: Context?, withTitle: Boolean
    ) {
        //need definition cover
        val fDefinitionReceiver =
            receiverGroup?.getReceiver<IReceiver>(DataInter.ReceiverKey.KEY_DEFINITION_COVER)
        if (fDefinitionReceiver == null) {
            receiverGroup?.addReceiver(
                DataInter.ReceiverKey.KEY_DEFINITION_COVER,
                VideoDefinitionCover(context)
            )
        }
        //need recommend cover
        //                if (needRecommendList && configParams != null && configParams.recommendListCover != null) {
        //                    receiverGroup?.removeReceiver(DataInter.ReceiverKey.KEY_RECOMMEND_LIST_COVER)
        //                    receiverGroup?.addReceiver(DataInter.ReceiverKey.KEY_RECOMMEND_LIST_COVER, configParams.recommendListCover)
        //                }
        //need user guide cover
        if (ReceiverGroupManager.isNeeduserGuideCover()) {
            val fUserGuideReceiver =
                receiverGroup?.getReceiver<IReceiver>(DataInter.ReceiverKey.KEY_USER_GUIDE_COVER)
            if (fUserGuideReceiver == null) {
                context?.apply {
                    receiverGroup?.addReceiver(
                        DataInter.ReceiverKey.KEY_USER_GUIDE_COVER,
                        UserGuideCover(context)
                    )
                }
            }
        }
        //config params
        updateGroupValue(DataInter.Key.KEY_DANMU_SHOW_STATE, true)
        updateGroupValue(DataInter.Key.KEY_DANMU_EDIT_ENABLE, true)
        updateGroupValue(DataInter.Key.KEY_CONTROLLER_DANMU_SWITCH_ENABLE, true)
        updateGroupValue(DataInter.Key.KEY_CONTROLLER_SHARE_ENABLE, true)
        updateGroupValue(DataInter.Key.KEY_NEED_VIDEO_DEFINITION, true)
        updateGroupValue(DataInter.Key.KEY_NEED_RECOMMEND_LIST, needRecommendList)
        updateGroupValue(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, withTitle)
        updateGroupValue(DataInter.Key.KEY_NEED_PLAY_NEXT, needRecommendList)
        updateGroupValue(DataInter.Key.KEY_IS_FULL_SCREEN, true)
        updateGroupValue(DataInter.Key.KEY_NEED_ERROR_TITLE, true)
    }

    fun setShareVisibility(show: Boolean) {
        updateGroupValue(DataInter.Key.KEY_CONTROLLER_SHARE_ENABLE, show)
    }

    fun setFullScreenVisibility(show: Boolean){
        updateGroupValue(DataInter.Key.KEY_CONTROLLER_FULL_ENABLE, show)
    }

    private fun configDetailProtraitState(
        receiverGroup: IReceiverGroup?,
        context: Context?, withTitle: Boolean
    ) {
        //remove some receivers
        receiverGroup?.removeReceiver(DataInter.ReceiverKey.KEY_USER_GUIDE_COVER)
        receiverGroup?.removeReceiver(DataInter.ReceiverKey.KEY_DEFINITION_COVER)
        receiverGroup?.removeReceiver(DataInter.ReceiverKey.KEY_RECOMMEND_LIST_COVER)
        //config params
        updateGroupValue(DataInter.Key.KEY_DANMU_SHOW_STATE, true)
        updateGroupValue(DataInter.Key.KEY_DANMU_EDIT_ENABLE, false)
        updateGroupValue(DataInter.Key.KEY_CONTROLLER_DANMU_SWITCH_ENABLE, true)
        updateGroupValue(DataInter.Key.KEY_CONTROLLER_SHARE_ENABLE, true)
        updateGroupValue(DataInter.Key.KEY_NEED_VIDEO_DEFINITION, false)
        updateGroupValue(DataInter.Key.KEY_NEED_RECOMMEND_LIST, false)
        updateGroupValue(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, withTitle)
        updateGroupValue(DataInter.Key.KEY_NEED_PLAY_NEXT, false)
        updateGroupValue(DataInter.Key.KEY_IS_FULL_SCREEN, false)
        updateGroupValue(DataInter.Key.KEY_NEED_ERROR_TITLE, true)
    }

    fun isEqualDataSource(vid: Long, videoSource: Long): Boolean {
        if (vid == 0L || videoSource == 0L) return false
        if (mDataSource != null && mDataSource is MTimeVideoData) {
            val data: MTimeVideoData = mDataSource as MTimeVideoData
            return vid == data.videoId && videoSource == data.source
        }
        return false
    }

    fun getDataSource(): DataSource? {
        return mDataSource
    }

    override fun stop() {
        pause()
        super.stop()
    }

    override fun destroy() {
        PlayerConfig.isListMode = false
        mHostActivity = null
        instance = null
        super.destroy()
    }

//    class ConfigParams {
//        var recommendListCover: RecommendListCover? = null
//    }


}