package com.kotlin.android.home.ui.recommend.adapter

import android.app.Service
import android.graphics.Rect
import android.media.AudioManager
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kk.taurus.playerbase.event.OnErrorEventListener
import com.kk.taurus.playerbase.event.OnPlayerEventListener
import com.kk.taurus.playerbase.player.IPlayer
import com.kk.taurus.playerbase.receiver.GroupValue
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.ext.VariateExt
import com.kotlin.android.app.router.provider.ugc.IUgcProvider
import com.kotlin.android.app.router.provider.video.IVideoProvider
import com.kotlin.android.home.R
import com.kotlin.android.home.databinding.ItemTrailerBinding
import com.kotlin.android.home.databinding.ItemTrailerPlayBinding
import com.kotlin.android.home.ui.RcmdTrailerListActivity
import com.kotlin.android.home.ui.recommend.bean.TrailerItem
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.startActivity
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.orFalse
import com.kotlin.android.ktx.utils.LogUtils
import com.kotlin.android.player.DataInter
import com.kotlin.android.player.ReceiverGroupManager
import com.kotlin.android.player.bean.MTimeVideoData
import com.kotlin.android.player.dataprovider.MTimeDataProvider
import com.kotlin.android.player.receivers.SimpleTouchGestureListener
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/13
 *
 * ????????????-????????????
 */
class TrailerBinder(
    val items: List<TrailerItemBinder>,
    private val lifecycle: Lifecycle,
    private val recyclerView: RecyclerView,
    private val provider: MTimeDataProvider
) : BaseRecommendHeadBinder<ItemTrailerPlayBinding>(),
    OnPlayerEventListener, OnErrorEventListener, LifecycleObserver, SimpleTouchGestureListener {
    private var mCurPlayPosition = -1
    private var mIsVolumeSilent = true //????????????
    private var mIsCompletelyVisible = false //??????????????????
    private var mGroupValue: GroupValue? = null
    private val mVideoProvider by lazy { getProvider(IVideoProvider::class.java) }

    //??????????????????????????????????????????
    private val mOnScrollListener =
        object : RecyclerView.OnScrollListener() {
            private val rect = Rect()

            fun isCompletelyVisible(view: View): Boolean {
                val visible = view.getLocalVisibleRect(rect)
                mIsCompletelyVisible = visible && rect.width() == view.width
                        && rect.height() == view.height
                return mIsCompletelyVisible
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                binding?.run {
                    if (isShowError())
                        return

                    if (isCompletelyVisible(mTrailerVideoView)) {
                        if (mTrailerVideoView.state == IPlayer.STATE_PAUSED) {
                            mTrailerVideoView.resume()
                        }
                    } else {
                        if (mTrailerVideoView.isInPlaybackState
                            && mTrailerVideoView.state == IPlayer.STATE_STARTED
                        ) {
                            mTrailerVideoView.pause()
                        }
                    }
                }
            }
        }

    fun resume() {
        binding?.apply {
            if (mOnScrollListener.isCompletelyVisible(mTrailerVideoView)) {
                if (mTrailerVideoView.state == IPlayer.STATE_PAUSED) {
                    mTrailerVideoView.resume()
                }
            }
        }
    }

    fun pause() {
        binding?.mTrailerVideoView?.pause()
    }

    private fun isShowError(): Boolean {
        return mGroupValue?.getBoolean(DataInter.Key.KEY_ERROR_SHOW_STATE, false).orFalse()
    }

    override fun layoutId(): Int {
        return R.layout.item_trailer_play
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is TrailerBinder && other.items != items
    }

    override fun onBindViewHolder(binding: ItemTrailerPlayBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        mPosition = position
        binding.run {
            mAllTv.onClick {
                it.context.startActivity(clazz = RcmdTrailerListActivity::class.java)
            }
            registerListeners()
            initVideoView()
            setPlayListAdapter()
            initOther()
        }
    }

    private fun initOther() {
        binding?.apply {
            mTrailerVideoVolumeIv.setImageResource(R.drawable.ic_home_volume_off)
            mCurPlayPosition = -1
//          ??????item????????????2(????????????????????????)
            mIsCompletelyVisible = mPosition < 2
            playVideo(0)
        }
    }

    private fun initVideoView() {
        binding?.apply {
            mTrailerVideoView.setOnPlayerEventListener(this@TrailerBinder)
            mTrailerVideoView.setOnErrorEventListener(this@TrailerBinder)
            mTrailerVideoView.setReceiverGroup(
                ReceiverGroupManager.getLessReceiverGroup(root.context, this@TrailerBinder).apply {
                    mGroupValue = groupValue
                })
            mTrailerVideoView.setOnReceiverEventListener { eventCode, bundle ->
                when (eventCode) {
                    DataInter.ReceiverEvent.EVENT_CODE_ERROR_FEED_BACK -> {
                        getProvider(IUgcProvider::class.java) {
                            launchDetail(
                                VariateExt.feedbackPostId,
                                CommConstant.PRAISE_OBJ_TYPE_POST
                            )
                        }
                    }
                }
            }
            mTrailerVideoView.setVolume(0f, 0f) //??????
            mTrailerVideoView.stop()
        }
    }

    //???????????????????????????????????????
    private fun setPlayListAdapter() {
        binding?.run {
            items.let {
                 createMultiTypeAdapter(mTrailerItemRecyclerView).apply {
                    setOnClickListener { view, binder ->
                        when (view.id) {
                            R.id.mItemTrailerRootView -> {
                                //????????????item????????????
                                val position = items.indexOf(binder)
                                if (position != mCurPlayPosition) {
                                    playVideo(position)
                                }
                            }
                            else -> {
                                this@TrailerBinder.mOnClickListener?.invoke(view, binder)
                            }
                        }
                    }
                    notifyAdapterDataSetChanged(items)
                }
            }
        }
    }

    private fun registerListeners() {
        recyclerView.clearOnScrollListeners()
        recyclerView.addOnScrollListener(mOnScrollListener)
        lifecycle.removeObserver(this)
        lifecycle.addObserver(this)
    }

    override fun onViewAttachedToWindow(binding: ItemTrailerPlayBinding, position: Int) {
        registerListeners()
    }

    override fun onViewDetachedFromWindow(binding: ItemTrailerPlayBinding, position: Int) {
        recyclerView.removeOnScrollListener(mOnScrollListener)
        lifecycle.removeObserver(this)
        binding.apply {
            mTrailerVideoView.pause()
        }
    }

    override fun onUnBindViewHolder(binding: ItemTrailerPlayBinding?) {
        binding?.run {
            mTrailerVideoView.stop()
            recyclerView.removeOnScrollListener(mOnScrollListener)
            lifecycle.removeObserver(this@TrailerBinder)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.mTrailerVideoVolumeIv -> {
                onVolumeClick()
            }
            else -> {
                super.onClick(view)
            }
        }
    }

    //????????????
    private fun onVolumeClick() {
        binding?.run {
            if (mIsVolumeSilent) {
                //????????????
                val audioManager =
                    mTrailerVideoVolumeIv.context
                        .getSystemService(Service.AUDIO_SERVICE) as AudioManager
                mTrailerVideoView.setVolume(
                    audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM).toFloat(),
                    audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM).toFloat()
                )
                mTrailerVideoVolumeIv.setImageResource(R.drawable.ic_home_volume_on)
            } else {
                //??????
                mTrailerVideoView.setVolume(0f, 0f)
                mTrailerVideoVolumeIv.setImageResource(R.drawable.ic_home_volume_off)
            }
            mIsVolumeSilent = !mIsVolumeSilent
        }
    }

    //?????????????????????
    override fun onPlayerEvent(eventCode: Int, bundle: Bundle?) {
        when (eventCode) {
            OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START -> {
                if (!mIsCompletelyVisible) {
                    binding?.mTrailerVideoView?.pause()
                }
            }
            OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE -> {
                //????????????,?????????????????????
                playVideo(
                    if (mCurPlayPosition == items.size - 1) {
                        0
                    } else {
                        mCurPlayPosition + 1
                    }
                )
            }
        }
    }

    //???????????????????????????
    override fun onErrorEvent(eventCode: Int, bundle: Bundle?) {

    }

    //??????????????????
    private fun playVideo(position: Int) {
        items.let {
            if (position < it.size && position != mCurPlayPosition) {
                binding?.run {
                    //?????????item???????????????
                    if (mCurPlayPosition >= 0) {
                        it[mCurPlayPosition].isSelected = false
                        it[mCurPlayPosition].notifyAdapterSelfChanged()
                    }
                    mCurPlayPosition = position

                    //??????item????????????
                    it[position].isSelected = true
                    it[position].notifyAdapterSelfChanged()

                    //?????????????????????
                    (mTrailerItemRecyclerView.layoutManager as LinearLayoutManager)
                        .scrollToPositionWithOffset(position, 0)

                    //????????????
                    mTrailerVideoView.run {
                        stop()
                        setDataProvider(provider)
                        setDataSource(
                            MTimeVideoData(
                                videoId = it[position].trailerItem.videoId,
                                source = it[position].trailerItem.videoSource
                            )
                        )
                        start()
                    }
                }
            }
        }
    }

    @OnLifecycleEvent(androidx.lifecycle.Lifecycle.Event.ON_RESUME)
    fun startPlay() {
        if (mIsCompletelyVisible) {
            binding?.mTrailerVideoView?.resume()
        }
    }

    @OnLifecycleEvent(androidx.lifecycle.Lifecycle.Event.ON_PAUSE)
    fun stopPlay() {
        binding?.mTrailerVideoView?.pause()
        "xxxxxxxxxxxxx-OnLifecycleEvent..ON_PAUSE".e()
    }

    @OnLifecycleEvent(androidx.lifecycle.Lifecycle.Event.ON_DESTROY)
    fun releaseVideoView() {
        binding?.mTrailerVideoView?.stop()
        binding?.mTrailerVideoView?.stopPlayback()
        lifecycle.removeObserver(this)
    }

    override fun onSingleTapConfirmed(event: MotionEvent?) {
        //??????????????????????????????????????????
        if (mCurPlayPosition > -1) {
            items.let {
                mVideoProvider?.startPreVideoActivity(
                    it[mCurPlayPosition].trailerItem.videoId
                )
            }
        }
    }
}

class TrailerItemBinder(val trailerItem: TrailerItem) : MultiTypeBinder<ItemTrailerBinding>() {

    var isSelected = false

    override fun layoutId(): Int {
        return R.layout.item_trailer
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is TrailerItemBinder
                && (other.isSelected != this.isSelected)
    }

    override fun onPreBindViewHolder(binding: ItemTrailerBinding, position: Int) {
        binding.apply {
            mTrailerPlayingTagBg.visible(isSelected)
            mTrailerPlayingTagTv.visible(isSelected)
        }
    }
}