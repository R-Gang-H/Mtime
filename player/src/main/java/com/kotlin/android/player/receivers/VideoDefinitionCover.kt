package com.kotlin.android.player.receivers

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.kk.taurus.playerbase.event.BundlePool
import com.kk.taurus.playerbase.event.EventKey
import com.kk.taurus.playerbase.receiver.BaseCover
import com.kk.taurus.playerbase.receiver.IReceiverGroup.OnGroupValueUpdateListener
import com.kotlin.android.app.data.entity.player.VideoInfo
import com.kotlin.android.app.data.entity.video.VideoPlayList
import com.kotlin.android.app.data.entity.video.VideoPlayUrl
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.player.DataInter
import com.kotlin.android.player.PlayerHelper
import com.kotlin.android.player.R
import com.kotlin.android.player.bean.MTimeVideoData
import com.kotlin.android.player.widgets.DefinitionItemView
import java.util.*

/**
 * Created by mtime on 2017/10/18.
 */
class VideoDefinitionCover(context: Context?) : BaseCover(context) {
    private var mDefinitionView: LinearLayout? = null
    private var mUrlItems: List<VideoPlayUrl>? = ArrayList<VideoPlayUrl>()
    private val MSG_CODE_DELAY_HIDDEN_RATE_LIST = -123
    private var isListEmpty = false
    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MSG_CODE_DELAY_HIDDEN_RATE_LIST -> setDefinitionState(false)
            }
        }
    }

    override fun onReceiverBind() {
        super.onReceiverBind()
        mDefinitionView = findViewById(R.id.definition_view)
        view.setOnClickListener { setDefinitionState(false) }
        groupValue.registerOnGroupValueUpdateListener(onGroupValueUpdateListener)
    }

    override fun onReceiverUnBind() {
        super.onReceiverUnBind()
        groupValue.unregisterOnGroupValueUpdateListener(onGroupValueUpdateListener)
    }

    private val onGroupValueUpdateListener: OnGroupValueUpdateListener = object : OnGroupValueUpdateListener {
        override fun filterKeys(): Array<String> {
            return arrayOf(DataInter.Key.KEY_VIDEO_RATE_DATA,DataInter.Key.KEY_VIDEO_RATE_NO_DATA)
        }

        override fun onValueUpdate(s: String, o: Any) {
            when(s){
                DataInter.Key.KEY_VIDEO_RATE_DATA->{
                    val urlItems: VideoPlayList = o as VideoPlayList
                    refreshView(urlItems.playUrlList ?: arrayListOf())
                    isListEmpty = false
                }
                DataInter.Key.KEY_VIDEO_RATE_NO_DATA->{
                    isListEmpty = true
                }
            }

        }
    }

    override fun onPlayerEvent(eventCode: Int, bundle: Bundle?) {}
    override fun onErrorEvent(i: Int, bundle: Bundle?) {}
    override fun onReceiverEvent(eventCode: Int, bundle: Bundle?) {}
    override fun onPrivateEvent(eventCode: Int, bundle: Bundle?): Bundle? {
        when (eventCode) {
            DataInter.ReceiverEvent.EVENT_REQUEST_SHOW_DEFINITION_LIST -> setDefinitionState(true)
        }
        return super.onPrivateEvent(eventCode, bundle)
    }

    private fun sendDelayHiddenListMsg() {
        removeDelayHiddenListMsg()
        mHandler.sendEmptyMessageDelayed(MSG_CODE_DELAY_HIDDEN_RATE_LIST, 5000)
    }

    private fun removeDelayHiddenListMsg() {
        mHandler.removeMessages(MSG_CODE_DELAY_HIDDEN_RATE_LIST)
    }

    private fun refreshView(urlItems: ArrayList<VideoPlayUrl>) {
        mUrlItems = urlItems
    }

    private val currentPosition: Int
        private get() {
            val playerStateGetter = playerStateGetter
            return playerStateGetter?.currentPosition ?: 0
        }

    private fun onDefinitionShow() {
        if (mUrlItems == null) {
            mUrlItems = groupValue.get<List<VideoPlayUrl>>(DataInter.Key.KEY_VIDEO_RATE_DATA)
        }
        if (mUrlItems == null) return
        mDefinitionView?.removeAllViews()
        var itemView: DefinitionItemView
        val currentUrl = groupValue.getString(DataInter.Key.KEY_CURRENT_URL)
        mUrlItems?.forEach { item ->
            itemView = DefinitionItemView(context)
            itemView.key = item.url.orEmpty()
            itemView.setCurrentItemKey(currentUrl)
            itemView.text = item.name.orEmpty()
            itemView.setOnClickListener(View.OnClickListener {
                if (currentUrl != item.url) {
                    val bundle = BundlePool.obtain()
                    bundle.putSerializable(EventKey.SERIALIZABLE_DATA, buildDataSource(item))
                    if (item.isLive) {
                        requestReplay(bundle)
                    } else {
                        requestPlayDataSource(bundle)
                    }
                    setDefinitionState(false)
                    if (isListEmpty){
                        return@OnClickListener
                    }
                    PlayerHelper.recordDefinition(item.resolutionType.toString())
                    val obtain = BundlePool.obtain()
                    obtain.putString(EventKey.STRING_DATA, item.name.orEmpty())
                    obtain.putLong(EventKey.LONG_DATA, item.fileSize)
                    notifyReceiverEvent(DataInter.ReceiverEvent.EVENT_CODE_DEFINITION_CHANGE, obtain)
                }
            })
            mDefinitionView?.addView(itemView, params)
        }

    }

    private fun buildDataSource(item: VideoPlayUrl): MTimeVideoData {
        val data = MTimeVideoData(item.url.orEmpty(), 0L, 0, 0L)
        data.startPos = currentPosition
        data.tag = item.name.orEmpty()
        data.isLive = item.isLive
        val info: VideoInfo? = groupValue.get(DataInter.Key.KEY_VIDEO_INFO)
        if (info != null) {
            data.videoId = info.vid
            data.source = info.sourceType
        }
        return data
    }

    private val params: LinearLayout.LayoutParams
        private get() {
            val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val marginValue: Int = 10.dp
            params.setMargins(marginValue, marginValue, marginValue, marginValue)
            return params
        }

    fun setDefinitionState(state: Boolean) {
        setCoverVisibility(if (state) View.VISIBLE else View.GONE)
        if (state) {
            onDefinitionShow()
            sendDelayHiddenListMsg()
        }
    }

    public override fun onCreateCoverView(context: Context): View {
        return View.inflate(context, R.layout.layout_definition_cover, null)
    }

    override fun getCoverLevel(): Int {
        return levelMedium(DataInter.CoverLevel.COVER_LEVEL_DEFINITION)
    }
}