package com.kotlin.android.audio.floatview.component.aduiofloat

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.LayoutInflater
import androidx.annotation.LayoutRes
import coil.load
import com.kotlin.android.app.data.ext.VariateExt
import com.kotlin.android.audio.floatview.component.R
import com.kotlin.android.audio.floatview.component.aduiofloat.bean.AudioBean
import com.kotlin.android.audio.floatview.component.aduiofloat.constant.*
import com.kotlin.android.audio.floatview.component.aduiofloat.event.EventAudioPlayer
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.orFalse
import com.kotlin.android.ktx.ext.orZero
import kotlinx.android.synthetic.main.view_float_audio.view.*

class EnFloatingView @JvmOverloads constructor(
    context: Context,
    @LayoutRes resource: Int = R.layout.view_float_audio
) : FloatingMagnetView(context, null) {
    private var mInfoBean: AudioBean? = null


    init {
        removeAllViews()
        val inflate = LayoutInflater.from(context).inflate(resource, null)
        LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.RIGHT
        }.also {
            addView(inflate,it)
        }

        startObserve()
        initListener()
        audioCl?.background = getBg()
    }

    private fun getBg(): Drawable{
        return GradientDrawable().apply {
            setColor(getColor(R.color.color_661d2736))
            val dp30 = 30.dpF
            cornerRadii = floatArrayOf(dp30, dp30, 0f, 0f,0f,0f,dp30, dp30)
        }
    }


    private fun initListener() {
        playBtn?.onClick {
            //暂停播放
            playBtn?.isSelected = playBtn?.isSelected.orFalse().not()
            titleTv?.visible(playBtn?.isSelected.orFalse())
            sendCmd(action = if (playBtn?.isSelected.orFalse()) AUDIO_ACTION_PAUSE else AUDIO_ACTION_PLAY,seekBar?.getProgress().orZero(),seekBar?.getMaxProgress().orZero())
        }
        closeBtn?.onClick {
            //取消悬浮框，停止播放 发送广播
            sendCmd(action = AUDIO_ACTION_CLOSE)
        }
        audioFL?.onClick {//点击封面进行跳转
            dealClickEvent()
        }
    }

    private fun startObserve() {
        audioPlayerObserve().observeForever {
            it?.apply {
                mInfoBean = it.audioInfo
                when (it.action) {
                    AUDIO_ACTION_SEEK_DURATION,//播放进度
                    AUDIO_ACTION_PLAY//aduioView通知播放器开始播放
                    -> {
                        updateInfo(it)

                    }

                    AUDIO_ACTION_PLAY_COMPLETE ,AUDIO_ACTION_PLAY_ERROR-> {//音频播放完成 音频播放失败
                        seekBar?.setProgress(0)
                        playBtn?.isSelected = true
                        titleTv?.visible(true)
                    }
                    AUDIO_ACTION_CLOSE->{
                        FloatingView.get()?.remove()
                    }
                    AUDIO_ACTION_UPDATE_INFO->{//同步数据
                        updateInfo(it)
                        playBtn?.isSelected = it.isPlaying.not()
                        titleTv?.visible(it.isPlaying.not())
                    }

                }
            }

        }
    }

    private fun updateInfo(bean:EventAudioPlayer){
        seekBar?.setProgress(bean.seekToPosition.toInt())
        seekBar?.setMaxProgress(bean.totalDuration.toInt())
        setAudioData(bean.audioInfo)

    }

    private fun setAudioData(bean: AudioBean?) {
        bean ?: return
        audioIv?.load(setProxy(bean.audioImg, 44.dp, 44.dp))
        titleTv?.text = bean.contentDes

    }

    private fun sendCmd(
        action: String = "",
        seekToPosition: Long = 0L,
        totalDuration: Long = 0L,
        bufferPercent: Int = 0
    ) {
        EventAudioPlayer(
            action,
            mInfoBean,
            seekToPosition,
            totalDuration,
            bufferPercent
        ).postEvent()
    }

    private fun setProxy(imgUrl: String, width: Int, height: Int): String {
        return if (imgUrl.contains("aliyuncs.com")) {
            // 具体参数参见：https://help.aliyun.com/document_detail/44705.html
            "$imgUrl?x-oss-process=image/resize,m_fill,w_$width,h_$height/auto-orient,1"
        } else {
            "${VariateExt.imgProxyUrl}$imgUrl&width=$width&height=$height&clipType=4"
        }
    }
}