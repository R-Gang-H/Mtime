package com.kotlin.android.player.receivers

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import com.kk.taurus.playerbase.event.EventKey
import com.kk.taurus.playerbase.event.OnPlayerEventListener
import com.kk.taurus.playerbase.receiver.BaseReceiver
import com.kotlin.android.app.data.entity.live.DanmuBean
import com.kotlin.android.app.data.entity.live.DirectorUnits
import com.kotlin.android.app.data.entity.player.VideoInfo
import com.kotlin.android.app.data.entity.video.VideoPlayList
import com.kotlin.android.app.data.entity.video.VideoPlayUrl
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.player.DataInter
import com.kotlin.android.player.MemoryPlayRecorder
import com.kotlin.android.player.PlayerConfig
import com.kotlin.android.player.ad.AdHelper
import com.kotlin.android.player.bean.MTimeVideoData

/**
 * create by lushan on 2020/8/31
 * description:
 */
class DataReceiver(var ctx: Context) : BaseReceiver(ctx) {
    private var mCurrVid = 0L//当前视频id
    override fun onPlayerEvent(eventCode: Int, bundle: Bundle?) {
        when (eventCode) {
            OnPlayerEventListener.PLAYER_EVENT_ON_PROVIDER_DATA_START -> groupValue.putBoolean(
                DataInter.Key.KEY_LIST_COMPLETE,
                false
            )
            OnPlayerEventListener.PLAYER_EVENT_ON_PAUSE -> recordPlayPosition()
            OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE -> {
                recordPlayPosition(0)
                if (!PlayerConfig.isListMode) {
                    groupValue.putBoolean(DataInter.Key.KEY_LIST_COMPLETE, true)
                }
            }
            OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET -> if (bundle != null) {
                val data: MTimeVideoData? =
                    bundle.getSerializable(EventKey.SERIALIZABLE_DATA) as? MTimeVideoData?
                if (data != null) {
                    mCurrVid = data.videoId
                    val info: VideoInfo = VideoInfo(data.videoId, data.source)
                    AdHelper.updateAdList(data.videoId, data.source)
                    groupValue.putLong(DataInter.Key.KEY_CURRENT_VIDEO_ID, mCurrVid.orZero())
                    groupValue.putObject(DataInter.Key.KEY_VIDEO_INFO, info)
                    groupValue.putString(DataInter.Key.KEY_CURRENT_URL, data.data.orEmpty())
                    groupValue.putString(DataInter.Key.KEY_CURRENT_DEFINITION, data.tag.orEmpty())
                }
            }
            DataInter.ProviderEvent.EVENT_VIDEO_RATE_DATA -> if (bundle != null) {
                val list: VideoPlayList? =
                    bundle.getSerializable(DataInter.Key.KEY_PROVIDER_PLAY_URL_INFO) as? VideoPlayList
                if (list!=null){
                    groupValue.putObject(DataInter.Key.KEY_VIDEO_RATE_DATA, list)
                }
            }
            DataInter.ProviderEvent.EVENT_VIDEO_NO_RATE_DATA->{
                groupValue.putObject(DataInter.Key.KEY_VIDEO_RATE_NO_DATA, "")
            }
            DataInter.ProviderEvent.EVENT_LIVE_DANMU -> {
                if (bundle != null) {//单个
                    val danmuBean =
                        bundle.getSerializable(DataInter.Key.KEY_LIVE_DANMU_CONTENT) as? DanmuBean
                    danmuBean?.apply {
                        groupValue.putObject(DataInter.Key.KEY_LIVE_DANMU_CONTENT, this)
                    }
                } else {//集合 不能传null，会报空指针
                    groupValue.putObject(DataInter.Key.KEY_LIVE_DANMU_CONTENT, -1L)
                }
            }
            DataInter.ProviderEvent.EVENT_LIVE_DIRECTOR_UNITS->{//导播台
                val directorUnits = bundle?.getSerializable(DataInter.Key.KEY_LIVE_DIRECTOR_UNITS) as? DirectorUnits
                directorUnits?.apply {
                    groupValue.putObject(DataInter.Key.KEY_LIVE_DIRECTOR_UNITS,this)
                }

            }
        }
    }

    private fun recordPlayPosition(msc: Int) {
        if (mCurrVid > 0) MemoryPlayRecorder.recordPlayTime(mCurrVid.toInt(), msc)
    }

    private fun recordPlayPosition() {
        if (getDuration() > 0) recordPlayPosition(getCurrentPosition())
    }

    private fun getCurrentPosition(): Int {
        val playerStateGetter = playerStateGetter
        return playerStateGetter?.currentPosition ?: 0
    }

    private fun getDuration(): Int {
        val playerStateGetter = playerStateGetter
        return playerStateGetter?.duration ?: 0
    }

    private fun parseInt(id: String): Int {
        try {
            if (!TextUtils.isEmpty(id)) return id.toInt()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return -1
    }

    override fun onErrorEvent(eventCode: Int, bundle: Bundle?) {

    }

    override fun onReceiverEvent(eventCode: Int, bundle: Bundle?) {

    }
}