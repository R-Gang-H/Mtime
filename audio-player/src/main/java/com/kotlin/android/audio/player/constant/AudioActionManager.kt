package com.kotlin.android.audio.player.constant

import android.content.Intent
import com.kotlin.android.audio.player.service.AudioPlayerService
import com.kotlin.android.core.CoreApp

/**
 * create by lushan on 2022/3/22
 * des:音频播放器服务开启
 **/

object AudioActionManager {
    fun startAudioService(){
        Intent(CoreApp.instance,AudioPlayerService::class.java).apply {
            CoreApp.instance.startService(this)
        }
    }
}