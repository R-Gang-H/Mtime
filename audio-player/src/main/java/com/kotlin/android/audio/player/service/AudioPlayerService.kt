package com.kotlin.android.audio.player.service

import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.os.Handler
import android.os.IBinder
import com.kotlin.android.audio.floatview.component.aduiofloat.bean.AudioBean
import com.kotlin.android.audio.floatview.component.aduiofloat.constant.*
import com.kotlin.android.audio.floatview.component.aduiofloat.event.EventAudioPlayer
import com.kotlin.android.ktx.ext.orFalse
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.ktx.utils.LogUtils
import tv.danmaku.ijk.media.player.IjkMediaPlayer

/**
 * create by lushan on 2022/3/22
 * des:音频播放服务
 **/
class AudioPlayerService : Service() {

    /**
     * 播放器
     */
    private var mMediaPlayer: IjkMediaPlayer? = null

    /**
     * 播放线程
     */
    private var mPlayerThread: Thread? = null

    /**
     * 是否正在快进
     */
    private var isSeekTo = false
    private var isComplete = false
    private var audioInfoBean: AudioBean? = null
    private var handler = Handler()
    private var timeRunnable: Runnable = Runnable {
        try {
            LogUtils.d("isSeekTo$isSeekTo -- isComplete:$isComplete -- isPlaying:${mMediaPlayer?.isPlaying} ---${isSeekTo.not() && isComplete.not() && mMediaPlayer?.isPlaying == true}")
            if (isSeekTo.not() && isComplete.not() && mMediaPlayer?.isPlaying == true) {
                sendCmd(
                    action = AUDIO_ACTION_SEEK_DURATION,
                    seekToPosition = mMediaPlayer?.currentPosition.orZero(),
                    totalDuration = mMediaPlayer?.duration.orZero()
                )
            }
            timeEmptyMessage()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun timeEmptyMessage() {

        handler.postDelayed(timeRunnable, 1000L)
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        LogUtils.e("开启服务---")
        startObserve()


    }

    private fun startObserve() {
        audioPlayerObserve().observeForever {
            LogUtils.e("音频播放服务接收信令---")
            it?.apply {
                when (it.action) {
                    AUDIO_ACTION_INIT -> {//初始化播放器，重新播放
//                        releasePlayer()
//                        audioInfoBean = audioInfo
//                        playNetMusic(audioInfo?.audioUrl.orEmpty())

                        playMusic(true)
                    }
                    AUDIO_ACTION_PLAY -> {//播放
                        //如果不是当前音频详情，需要重新加载播放，并到上次播放的位置
                        playMusic(false)
                    }

                    AUDIO_ACTION_PAUSE -> {//暂停播放
                        pauseMusic()
                    }

                    AUDIO_ACTION_SEEK -> {//快进、快退
                        seekToMusic(seekToPosition)
                    }
                    AUDIO_ACTION_CLOSE -> {//关闭
                        //关闭后先暂停音频播放
                        pauseMusic()
                    }
                    AUDIO_ACTION_REQUEST_IS_CURRENT_INFO -> {//询问传过来的信息是否是当前播放的音频
                        sendCmd(
                            AUDIO_ACTION_REPLY_IS_CURRENT_INFO,
                            isCurrentInfo = audioInfoBean?.contentId == audioInfo?.contentId,
                            requestContentId = currentRequestContentId
                        )
                    }
                }
            }
        }
    }

    private fun EventAudioPlayer.playMusic(releaseMediaPlayer: Boolean = false) {
        if (mMediaPlayer?.isPlayable.orFalse() && audioInfoBean?.contentId == audioInfo?.contentId && audioInfo?.contentId != 0L) {
            mMediaPlayer?.start()
        } else {
            if (releaseMediaPlayer) {
                releasePlayer()
            }
            audioInfoBean = audioInfo
            playNetMusic(audioInfo?.audioUrl.orEmpty())
            if (seekToPosition != 0L) {
                seekToMusic(seekToPosition)
            }
        }
    }


    /**
     * 快进
     *
     */
    private fun seekToMusic(position: Long) {
        isSeekTo = true
        mMediaPlayer?.seekTo(position)
    }

    private fun setComplete(isComplete: Boolean) {
        this.isComplete = isComplete
        this.isSeekTo = false
    }

    /**
     * 播放网络歌曲
     */
    private fun playNetMusic(url: String) {
        try {
            LogUtils.e("播放音乐：$url")
            mMediaPlayer = IjkMediaPlayer()

            mMediaPlayer?.apply {
                setAudioStreamType(AudioManager.STREAM_MUSIC)
                dataSource = url
                prepareAsync()
                setOnSeekCompleteListener {
                    LogUtils.e("seeke ---setOnSeekCompleteListener")
                    mMediaPlayer?.start()
                    setComplete(false)
                    timeEmptyMessage()
                    sendCmd(
                        action = AUDIO_ACTION_SEEK_DURATION,
                        seekToPosition = mMediaPlayer?.currentPosition.orZero(),
                        totalDuration = mMediaPlayer?.duration.orZero()
                    )
                    isSeekTo = false
                }
                setOnCompletionListener {
                    setComplete(true)
                    sendCmd(action = AUDIO_ACTION_PLAY_COMPLETE)
                    //播放完成
                    LogUtils.e("播放完成")
                }

                setOnErrorListener { mp, what, extra -> //发送播放错误广播
                    LogUtils.e("播放失败，what:$what -- $extra")
                    setComplete(true)
                    sendCmd(action = AUDIO_ACTION_PLAY_ERROR)
                    false
                }
                setOnPreparedListener {
                    setComplete(false)
                    LogUtils.e("播放setOnPreparedListener -- 开始播放")
                    mMediaPlayer?.start()
                    timeEmptyMessage()
                    sendCmd(
                        action = AUDIO_ACTION_SEEK_DURATION,
                        seekToPosition = mMediaPlayer?.currentPosition.orZero(),
                        totalDuration = mMediaPlayer?.duration.orZero()
                    )
                }
                setOnBufferingUpdateListener { iMediaPlayer, i ->
                    sendCmd(action = AUDIO_ACTION_BUFFER_PROGRESS, bufferPercent = i)
                    LogUtils.e("播放加载setOnBufferingUpdateListener -- $i")
                }
                setOnTimedTextListener { iMediaPlayer, ijkTimedText ->
                    LogUtils.e("播放加载setOnTimedTextListener -- ${ijkTimedText.text} -- ${iMediaPlayer.currentPosition}")
                }
            }
            if (mPlayerThread == null) {
                mPlayerThread = Thread()
                mPlayerThread?.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtils.e("播放失败-- ${e.message}")
        }

    }

    private fun sendCmd(
        action: String = "",
        seekToPosition: Long = 0L,
        totalDuration: Long = 0L,
        bufferPercent: Int = 0,
        isCurrentInfo: Boolean = false,
        requestContentId:Long = 0L
    ) {
        EventAudioPlayer(
            action,
            audioInfoBean,
            seekToPosition,
            totalDuration,
            bufferPercent, isCurrentInfo,requestContentId
        ).postEvent()
    }

    /**
     * 暂停播放
     */
    private fun pauseMusic() {
        if (mMediaPlayer?.isPlaying == true) {
            mMediaPlayer?.pause()
        }
    }


    /**
     * 释放播放器
     */
    private fun releasePlayer() {
//        mHPApplication.setPlayStatus(AudioPlayerManager.STOP)
        if (mPlayerThread != null) {
            mPlayerThread = null
        }
        if (mMediaPlayer?.isPlaying == true) {
            mMediaPlayer?.stop()
        }
        //mMediaPlayer.reset();
        mMediaPlayer?.release()
        mMediaPlayer = null
        System.gc()
    }

    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()
    }


}