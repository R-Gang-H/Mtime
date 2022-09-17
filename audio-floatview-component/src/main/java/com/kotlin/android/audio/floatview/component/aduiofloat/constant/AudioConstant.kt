package com.kotlin.android.audio.floatview.component.aduiofloat.constant

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.jeremyliao.liveeventbus.LiveEventBus
import com.jeremyliao.liveeventbus.core.LiveEvent
import com.jeremyliao.liveeventbus.core.Observable
import com.kotlin.android.audio.floatview.component.aduiofloat.event.EventAudioPlayer

/**
 * create by lushan on 2022/3/26
 * des: 音乐播放器常量
 **/
//音频播放器service使用
const val AUDIO_ACTION_INIT = "audio_action_init" //初始化音频播放器
const val AUDIO_ACTION_PLAY = "audio_action_play"//音频播放器播放
const val AUDIO_ACTION_PAUSE = "audio_action_pause"//音频播放器暂停
const val AUDIO_ACTION_SEEK = "audio_action_seek"//音频播放器快进或快退 播放器接收
const val AUDIO_ACTION_CLOSE = "audio_action_close"//音频播放器关闭
const val AUDIO_ACTION_REPLY_IS_CURRENT_INFO  = "audio_action_reply_is_current_info"//回复是否是同一个音频

//音频详情页使用
const val AUDIO_ACTION_SEEK_DURATION = "audio_action_seek_duration"//音频播放器时间 ，页面进度条接收
const val AUDIO_ACTION_BUFFER_PROGRESS = "audio_action_buffer_progress"//音频播放器缓存进度
const val AUDIO_ACTION_PLAY_COMPLETE = "audio_action_play_complete"//音频播放器播放完成
const val AUDIO_ACTION_PLAY_ERROR = "audio_action_play_error"//音频播放器播放失败
const val AUDIO_ACTION_REQUEST_IS_CURRENT_INFO = "audio_action_request_is_current_info"//判断是否同一个音频
const val AUDIO_ACTION_UPDATE_INFO = "audio_action_update_info"//同步信息到悬浮球


const val LIVE_EVENT_AUDIO_PLAYER = "live_event_audio_player"//发从音频指令

fun EventAudioPlayer.postEvent() {
    LiveEventBus.get(LIVE_EVENT_AUDIO_PLAYER).post(this)
}

fun sendCloseEvent(){
    EventAudioPlayer(AUDIO_ACTION_CLOSE).postEvent()
}


fun audioPlayerObserve() = LiveEventBus.get(LIVE_EVENT_AUDIO_PLAYER, EventAudioPlayer::class.java)