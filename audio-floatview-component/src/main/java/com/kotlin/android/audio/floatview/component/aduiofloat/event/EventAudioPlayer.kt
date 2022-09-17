package com.kotlin.android.audio.floatview.component.aduiofloat.event

import com.jeremyliao.liveeventbus.core.LiveEvent
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.audio.floatview.component.aduiofloat.bean.AudioBean

/**
 * create by lushan on 2022/3/22
 * des:视频播放器指令
 **/
data class EventAudioPlayer(var action:String = "",//音频播放器指令类型
                            var audioInfo: AudioBean? = null,//音频信息
                            var seekToPosition:Long = 0L,//音频播放到的位置
                            var totalDuration:Long = 0L,//总时长
                            var bufferPercent:Int = 0,//加载缓存进度
                            var isCurrentAudio:Boolean = false,//是否是同一个音频
                            var currentRequestContentId:Long = 0L,//当前请求是否是同一个音频发出信令对应的内容id
                            var isPlaying:Boolean = false//是否是播放中

):LiveEvent,ProguardRule