package com.kotlin.android.ugc.detail.component.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.SeekBar
import com.kk.taurus.playerbase.utils.TimeUtil
import com.kotlin.android.audio.floatview.component.aduiofloat.bean.AudioBean
import com.kotlin.android.audio.floatview.component.aduiofloat.constant.*
import com.kotlin.android.audio.floatview.component.aduiofloat.event.EventAudioPlayer
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.orFalse
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.ktx.utils.LogUtils
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.ugc.detail.component.R
import com.kotlin.android.ugc.detail.component.bean.UgcAudioViewBean
import com.kotlin.android.ugc.detail.component.databinding.ViewAudioBinding

/**
 * create by lushan on 2022/3/22
 * des:音频详情页
 **/
class AudioView @JvmOverloads constructor(
    var ctx: Context,
    var attrs: AttributeSet? = null,
    var defStr: Int = 0
) : FrameLayout(ctx, attrs, defStr) {
    private var mTimeFormat: String? = null
    var binding: ViewAudioBinding? = null
    private var audioBean: AudioBean? = null

    init {
        removeAllViews()
        binding = ViewAudioBinding.inflate(LayoutInflater.from(ctx))
        addView(
            binding?.root,
            LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        )
        initView()
        startObserve()
    }

    private fun startObserve() {
        audioPlayerObserve().observeForever {
            it?.apply {
                if (it.action == AUDIO_ACTION_REPLY_IS_CURRENT_INFO){
                    if (it.isCurrentAudio.not() && currentRequestContentId == audioBean?.contentId && audioBean?.contentId!=0L){//接收信息后需要进行过滤，可能会存在多个audioView
                        playThisAudio()
                    }
                    return@observeForever
                }
                if (audioBean?.contentId != audioInfo?.contentId && audioBean?.contentId != 0L && audioInfo?.contentId != 0L) {
                    //暂停播放
                    binding?.playIv?.isSelected = true
                    return@apply
                }

                when (it.action) {
                    AUDIO_ACTION_SEEK_DURATION -> {//播放进度
                        binding?.playIv?.isSelected = false
                        if (mTimeFormat == null) {
                            mTimeFormat = TimeUtil.getFormat(totalDuration)
                        }
                        binding?.startDurationTv?.text =
                            TimeUtil.getTime(mTimeFormat, seekToPosition)
                        binding?.totalDurationTv?.text =
                            TimeUtil.getTime(mTimeFormat, totalDuration)
                        binding?.audioSeekBar?.apply {
                            max = totalDuration.toInt()
                            progress = seekToPosition.toInt()
                        }
                        LogUtils.e("播放器--max:${binding?.audioSeekBar?.max} --- progress:${binding?.audioSeekBar?.progress}")

                    }

                    AUDIO_ACTION_BUFFER_PROGRESS -> {//缓存加载进度
                        binding?.audioSeekBar?.secondaryProgress =
                            ((bufferPercent * binding?.audioSeekBar?.max.orZero()) / 100f).toInt()
                    }
                    AUDIO_ACTION_PLAY_COMPLETE -> {//音频播放完成
                        playComplete()
                    }

                    AUDIO_ACTION_PLAY_ERROR -> {//音频播放器播放失败
                        showToast(R.string.ugc_detail_component_play_audio_error)
                        playComplete()
                    }
                    AUDIO_ACTION_CLOSE, AUDIO_ACTION_PAUSE -> {//关闭悬浮框/播放暂停
                        binding?.playIv?.isSelected = true
                    }
                    AUDIO_ACTION_PLAY -> {//播放开始
                        binding?.playIv?.isSelected = false
                    }

                }
            }
        }
    }

    fun isPlaying() = binding?.playIv?.isSelected.orFalse().not()

    private fun EventAudioPlayer.playComplete() {
        binding?.startDurationTv?.text =
            TimeUtil.getTime(mTimeFormat, seekToPosition)
        binding?.playIv?.isSelected = true
        binding?.audioSeekBar?.progress = 0
    }

    private fun sendCmd(
        action: String = "",
        seekToPosition: Long = 0L,
        totalDuration: Long = 0L,
        bufferPercent: Int = 0,
        currentRequestContentId:Long = 0L
    ) {
        EventAudioPlayer(
            action,
            audioBean,
            seekToPosition,
            totalDuration,
            bufferPercent,
            isPlaying = isPlaying(),
            currentRequestContentId = currentRequestContentId
        ).postEvent()
    }

    private fun playThisAudio() {
        binding?.playIv?.isSelected = false
        sendCmd(
            action = AUDIO_ACTION_INIT,
            seekToPosition = binding?.audioSeekBar?.progress.orZero().toLong()
        )
    }

    /**
     * 判断播放的和当前是否是同一个音频
     */
    fun checkAudio(){
        audioBean?:return
        sendCmd(action = AUDIO_ACTION_REQUEST_IS_CURRENT_INFO, currentRequestContentId = audioBean?.contentId?:0)
    }

    /**
     * 在音频页暂停播放，进入其他页面需要同步一次数据，不然悬浮球不显示
     */
    fun updateInfo(){
        sendCmd(
            action = AUDIO_ACTION_UPDATE_INFO,
            seekToPosition = binding?.audioSeekBar?.progress.orZero().toLong(),
            totalDuration = binding?.audioSeekBar?.max.orZero().toLong()
        )
    }

    private fun initView() {
        binding?.playIv?.onClick {//播放按钮
            binding?.playIv?.isSelected = binding?.playIv?.isSelected.orFalse().not()
            sendCmd(
                action = if (binding?.playIv?.isSelected.orFalse()) AUDIO_ACTION_PAUSE else AUDIO_ACTION_PLAY,
                seekToPosition = binding?.audioSeekBar?.progress.orZero().toLong()
            )
        }

        binding?.audioSeekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                LogUtils.e("播放进度改变：progress:$progress -- fromUser:$fromUser")
                if (fromUser) {
                    binding?.startDurationTv?.text =
                        TimeUtil.getTime(mTimeFormat, progress.toLong())
                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

                sendCmd(
                    action = AUDIO_ACTION_SEEK,
                    seekToPosition = seekBar?.progress.orZero().toLong()
                )

            }

        })

    }

    fun setData(bean: UgcAudioViewBean) {

        binding?.bgImg?.loadImage(
            bean.audioImg,
            220.dp,
            220.dp,
            blurRadius = 10f,
            blurSampling = 4f
        )
        binding?.coverIv?.loadImage(bean.audioImg, 220.dp, 220.dp)

        val audioInfo = AudioBean(bean.contentId, bean.audioUrl, bean.des, bean.audioImg)
        audioBean = audioInfo
        EventAudioPlayer(
            action = AUDIO_ACTION_INIT,
            audioInfo = audioInfo
        ).postEvent()
        binding?.playIv?.isSelected = false

    }


}