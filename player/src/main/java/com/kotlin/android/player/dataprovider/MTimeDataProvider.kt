package com.kotlin.android.player.dataprovider

import android.os.Bundle
import android.text.TextUtils
import com.kk.taurus.playerbase.config.PConst
import com.kk.taurus.playerbase.entity.DataSource
import com.kk.taurus.playerbase.event.BundlePool
import com.kk.taurus.playerbase.event.EventKey
import com.kk.taurus.playerbase.provider.BaseDataProvider
import com.kk.taurus.playerbase.utils.NetworkUtils
import com.kotlin.android.core.CoreApp
import com.kotlin.android.app.data.entity.live.CameraPlayUrl
import com.kotlin.android.app.data.entity.live.DanmuBean
import com.kotlin.android.app.data.entity.live.DirectorUnits
import com.kotlin.android.app.data.entity.video.VideoDetail
import com.kotlin.android.app.data.entity.video.VideoPlayList
import com.kotlin.android.app.data.entity.video.VideoPlayUrl
import com.kotlin.android.audio.floatview.component.aduiofloat.constant.AUDIO_ACTION_CLOSE
import com.kotlin.android.audio.floatview.component.aduiofloat.constant.postEvent
import com.kotlin.android.audio.floatview.component.aduiofloat.constant.sendCloseEvent
import com.kotlin.android.audio.floatview.component.aduiofloat.event.EventAudioPlayer
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.player.DataInter
import com.kotlin.android.player.PlayerConfig
import com.kotlin.android.player.PlayerHelper
import com.kotlin.android.player.bean.LiveVideoData
import com.kotlin.android.player.bean.MTimeVideoData
import com.kotlin.android.player.splayer.PreviewVideoPlayer

/**
 * create by lushan on 2020/9/3
 * description:视频播放数据provider,提供视频播放地址、清晰度和视频文件大小
 */
class MTimeDataProvider(var listener: ((MTimeVideoData) -> Unit)? = null) : BaseDataProvider() {
    companion object {
        const val KEY_IS_LIVE = "is_live"
    }

    private var mDataSource = MTimeVideoData("", 0L, 0L, 0L)
    override fun handleSourceData(sourceData: DataSource?) {
        sourceData ?: return

        sendCloseEvent()//关闭音频
        "handleSourceData==".e()
        if (!TextUtils.isEmpty(sourceData.data)) {
            val obtain = BundlePool.obtain()
            obtain.putSerializable(EventKey.SERIALIZABLE_DATA, sourceData)
            onProviderMediaDataSuccess(obtain)
            return
        }
        if (sourceData is MTimeVideoData) {
            "handleSourceData==加载视频播放地址".e()
            mDataSource = sourceData
            listener?.invoke(sourceData)
        }
    }

    /**
     * 当前播放视频的视频id
     */
    fun getCurrentVideoId(): Long {
        return mDataSource.videoId
    }

    /**
     * 更新视频id和视频类型
     */
    fun updateSourceData(videoId: Long, sourceType: Long) {
        mDataSource.videoId = videoId
        mDataSource.source = sourceType
    }

    /**
     * 设置视频播放地址
     */
    fun setVideoPlayUrlList(bean: VideoPlayList,notIgnoreMobileNet:Boolean = true) {
        val list = bean.playUrlList ?: arrayListOf()
//        "$list".e()
        if (list.isNotEmpty()) {
            //默认使用最高清晰度起播。
            val recordDefinition: String = PlayerHelper.getHistoryDefinition().orEmpty()
            var playItem: VideoPlayUrl? = PlayerHelper.getHighDefinition(list)
            list.forEach {
                //如果本地有清晰度记录，就以记录清晰度起播
                if (it.resolutionType.toString().equals(recordDefinition, ignoreCase = true)) {
                    playItem = it
                }
            }

            mDataSource.isLive = false

            //处理最终的播放数据
            handleDataSourceReady(mDataSource, playItem)
            val bundle = Bundle()
            bundle.putSerializable(DataInter.Key.KEY_PROVIDER_PLAY_URL_INFO, bean)
            onProviderExtraDataSuccess(DataInter.ProviderEvent.EVENT_VIDEO_RATE_DATA, bundle)
            if (notIgnoreMobileNet){
                stopWithMobileNet()
            }else{
                PlayerConfig.ignoreMobile = true
            }
        }
    }

    private fun stopWithMobileNet() {
        if (NetworkUtils.getNetworkState(CoreApp.instance.applicationContext) > PConst.NETWORK_STATE_WIFI && !PlayerConfig.ignoreMobile) {
            PreviewVideoPlayer.get()?.requestStop()
            onProviderExtraDataSuccess(DataInter.ProviderEvent.EVENT_VIDEO_INFO_READY, null)

        }
    }


    /**
     * 处理直播视频播放地址
     */
    fun setLiveVideoPlayUrlList(bean: CameraPlayUrl?) {
        bean ?: return
        if (bean.hd == null && bean.ori == null && bean.sd == null) {
            return
        }
        val videoPlayerList: ArrayList<VideoPlayUrl> = arrayListOf()
        val recordDefinition: String = PlayerHelper.getHistoryDefinition().orEmpty()
        var playItem: VideoPlayUrl? = null
        var isLive = true
        bean.sd?.apply {
            videoPlayerList.add(
                VideoPlayUrl(
                    url = this.rtmpUrl.orEmpty(),
                    name = "标清",
                    resolutionType = 1L,
                    isLive = isLive
                )
            )
        }
        bean.hd?.apply {
            playItem = VideoPlayUrl(
                url = this.rtmpUrl.orEmpty(),
                name = "高清",
                resolutionType = 2L,
                isLive = isLive
            )
            playItem?.apply {
                videoPlayerList.add(this)
            }
        }
        bean.ori?.apply {
            videoPlayerList.add(
                VideoPlayUrl(
                    url = this.rtmpUrl.orEmpty(),
                    name = "原画",
                    resolutionType = 3L,
                    isLive = isLive
                )
            )
        }
        videoPlayerList.forEach {
            //如果本地有清晰度记录，就以记录清晰度起播
            if (it.resolutionType.toString().equals(recordDefinition, ignoreCase = true)) {
                playItem = it
            }
        }
        mDataSource.isLive = isLive
        //处理最终的播放数据
        handleDataSourceReady(mDataSource, playItem)
        val bundle = Bundle()
        bundle.putSerializable(
            DataInter.Key.KEY_PROVIDER_PLAY_URL_INFO,
            VideoPlayList(videoPlayerList)
        )
        onProviderExtraDataSuccess(DataInter.ProviderEvent.EVENT_VIDEO_RATE_DATA, bundle)
        refreshCameraStandList()
//        LogUtils.e("机位地址获取","setLiveVideoPlayUrlList机位播放地址获取成功判断网络1")
        stopWithMobileNet()
    }


    /**
     * 视频播放地址加载失败
     */
    fun setVideoPlayUrlError() {
        onProviderMediaDataError(Bundle())
        onProviderExtraDataSuccess(DataInter.ProviderEvent.EVENT_VIDEO_NO_RATE_DATA, Bundle())
    }

    private fun handleDataSourceReady(dataSource: MTimeVideoData, playItem: VideoPlayUrl?) {
//        "handleDataSourceReady加载后播放视频:${playItem?.url}".e()
        val mTimeVideoData = MTimeVideoData(
            playItem?.url.orEmpty(), dataSource.videoId, dataSource.source, playItem?.fileSize
                ?: 0L
        )
        mTimeVideoData.isLive = dataSource.isLive
        mTimeVideoData.tag = playItem?.name
        mTimeVideoData.startPos = dataSource.startPos
        onDataSourceReady(mTimeVideoData)
    }

    fun refreshVideoDetail(detail: VideoDetail?) {
        detail?.apply {
            val bundle = Bundle()
            bundle.putSerializable(EventKey.SERIALIZABLE_DATA, this)
            onProviderExtraDataSuccess(DataInter.ProviderEvent.EVENT_VIDEO_INFO_READY, bundle)
        }
    }

    /**
     * 刷新直播视频观看人数、直播标识
     */
    fun refreshLiveVideoDetail(detail: LiveVideoData?) {
        detail?.apply {
            val bundle = Bundle()
            bundle.putSerializable(EventKey.SERIALIZABLE_DATA, this)
            onProviderExtraDataSuccess(DataInter.ProviderEvent.EVENT_VIDEO_INFO_READY, bundle)
        }
    }

    /**
     * 刷新机位列表
     */
    fun refreshCameraStandList() {
        //        显示直播机位列表
        onProviderExtraDataSuccess(DataInter.ProviderEvent.EVENT_LIVE_CAMERA_LIST, null)
    }

    /**
     * 根据横竖屏刷新机位
     */
    fun refreshCameraStandUIByOrientation(isPortrait: Boolean) {
        Bundle().apply {
            putBoolean(DataInter.Key.KEY_LIVE_CAMERA_LIST_ORIENTATION, isPortrait)
        }.also {
            onProviderExtraDataSuccess(
                DataInter.ProviderEvent.EVENT_LIVE_CAMERA_LIST_ORIENTATION,
                it
            )
        }
    }

    private fun onDataSourceReady(data: MTimeVideoData) {
        val bundle = BundlePool.obtain()
        bundle.putSerializable(EventKey.SERIALIZABLE_DATA, data)
        onProviderMediaDataSuccess(bundle)
    }

    /**
     * 更新直播发送消息
     */
    fun updateChatContent(content: String?) {
        Bundle().apply {
            putString(DataInter.Key.KEY_LIVE_SEND_CHAT_CONTENT, content.orEmpty())
        }.also {
            onProviderExtraDataSuccess(DataInter.ProviderEvent.EVENT_LIVE_SEND_CHAT_CONTENT, it)
        }
    }

    /**
     * 更新弹幕集合
     */
    fun updateDanmuList() {
        onProviderExtraDataSuccess(DataInter.ProviderEvent.EVENT_LIVE_DANMU, null)
    }

    /**
     * 更新单个弹幕
     */
    fun updateDanmu(bean: DanmuBean) {
        Bundle().apply {
            putSerializable(DataInter.Key.KEY_LIVE_DANMU_CONTENT, bean)
        }.also {
            onProviderExtraDataSuccess(DataInter.ProviderEvent.EVENT_LIVE_DANMU, it)
        }
    }

    /**
     * 更新导播台
     */
    fun updateDirectorUnits(bean:DirectorUnits){
        Bundle().apply {
            putSerializable(DataInter.Key.KEY_LIVE_DIRECTOR_UNITS, bean)
        }.also {
            onProviderExtraDataSuccess(DataInter.ProviderEvent.EVENT_LIVE_DIRECTOR_UNITS, it)
        }
    }

    override fun cancel() {}

    override fun destroy() {}
}