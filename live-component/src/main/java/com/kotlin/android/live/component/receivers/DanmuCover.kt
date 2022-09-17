package com.kotlin.android.live.component.receivers

import android.content.Context
import android.os.Bundle
import android.view.View
import com.kk.taurus.playerbase.receiver.BaseCover
import com.kk.taurus.playerbase.receiver.IReceiverGroup
import com.kotlin.android.app.data.entity.live.DanmuBean
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.live.component.R
import com.kotlin.android.live.component.manager.CameraStandManager
import com.kotlin.android.live.component.ui.widget.BackgroundCacheStuffer
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.player.DataInter
import com.kotlin.android.player.LiveStatus
import com.kotlin.android.player.PlayerConfig
import kotlinx.android.synthetic.main.view_danmu.view.*
import kotlinx.android.synthetic.main.view_living_controller_cover.view.*
import master.flame.danmaku.controller.DrawHandler
import master.flame.danmaku.danmaku.model.BaseDanmaku
import master.flame.danmaku.danmaku.model.DanmakuTimer
import master.flame.danmaku.danmaku.model.IDanmakus
import master.flame.danmaku.danmaku.model.IDisplayer
import master.flame.danmaku.danmaku.model.android.DanmakuContext
import master.flame.danmaku.danmaku.model.android.Danmakus
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser
import java.util.*

/**
 * create by lushan on 2021/3/16
 * description: 弹幕 cover
 */
class DanmuCover(context: Context) : BaseCover(context) {
    private var danmaContext: DanmakuContext? = null
    private val onGroupValueUpdateListener: IReceiverGroup.OnGroupValueUpdateListener = object : IReceiverGroup.OnGroupValueUpdateListener {
        override fun filterKeys(): Array<String> {
            return arrayOf(DataInter.Key.KEY_LIVE_DANMU_CONTENT)
        }

        override fun onValueUpdate(key: String?, value: Any?) {
            if (key == DataInter.Key.KEY_LIVE_DANMU_CONTENT) {
                if (value is Long) {//此时传入集合
                    addAllDanmu()
                } else {
//                    此时传入了单个弹幕
                    val danmuBean = value as? DanmuBean
                    danmuBean?.apply {
                        addDanmu(this, -1)
                    }
                }
            }
        }

    }

    private fun addAllDanmu() {
        CameraStandManager.getDanmuList().forEachIndexed { index, danmuBean ->
            addDanmu(danmuBean, index)
        }
    }

    override fun onReceiverBind() {
        super.onReceiverBind()
        initDanmaView()
        groupValue.registerOnGroupValueUpdateListener(onGroupValueUpdateListener)
    }

    override fun onReceiverUnBind() {
        super.onReceiverUnBind()

        // dont forget release!
        view?.danmakuView?.release()
        groupValue.unregisterOnGroupValueUpdateListener(onGroupValueUpdateListener)

    }

    private fun initDanmaView() {
        // 设置最大显示行数
        val maxLinesPair = HashMap<Int, Int>()
        maxLinesPair[BaseDanmaku.TYPE_SCROLL_RL] = 5 // 滚动弹幕最大显示5行
        // 设置是否禁止重叠
        val overlappingEnablePair = HashMap<Int, Boolean>()
        overlappingEnablePair[BaseDanmaku.TYPE_SCROLL_RL] = true
        overlappingEnablePair[BaseDanmaku.TYPE_FIX_TOP] = true
        danmaContext = DanmakuContext.create()
        danmaContext?.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_SHADOW, 3f)?.setDuplicateMergingEnabled(false)?.setScrollSpeedFactor(1.2f)?.setScaleTextSize(0.8f)?.setL2RDanmakuVisibility(false)?.setCacheStuffer(SpannedCacheStuffer(), null) // 图文混排使用SpannedCacheStuffer
                ?.setCacheStuffer(BackgroundCacheStuffer(), null) // 绘制背景使用BackgroundCacheStuffer
                ?.setMaximumLines(maxLinesPair)
                ?.preventOverlapping(overlappingEnablePair)?.setDanmakuMargin(10)

        view?.danmakuView?.apply {
            setCallback(object : DrawHandler.Callback {
                override fun prepared() {
                    view?.danmakuView?.start()
                }

                override fun updateTimer(timer: DanmakuTimer?) {
                }

                override fun danmakuShown(danmaku: BaseDanmaku?) {
                }

                override fun drawingFinished() {
                }

            })
            prepare(getParser(), danmaContext)
            enableDanmakuDrawingCache(true)
        }


    }

    private fun getParser(): BaseDanmakuParser {
        return object : BaseDanmakuParser() {
            override fun parse(): IDanmakus {
                return Danmakus()
            }

        }
    }

    private fun addDanmu(bean: DanmuBean, index: Int) {
        val danmaku = danmaContext?.mDanmakuFactory?.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL)
        danmaku ?: return
        danmaku.apply {
            text = bean.content
            textColor = getColor(if (bean.isMine) R.color.color_feb12a else R.color.color_ffffff)
            textShadowColor = getColor(R.color.color_4e5e73)
            priority = if (bean.isMine) 1 else 0
            textSize = context.resources.getDimension(R.dimen.offset_20sp)
            isLive = PlayerConfig.liveStatus == LiveStatus.LIVING
            time = (view?.danmakuView?.currentTime ?: 0L) + 1200L + index.toLong() * 100
        }.also {
            view?.danmakuView?.addDanmaku(it)
        }

    }

    private fun setDanmuViewState(state: Boolean) {
        view?.danmakuView?.visible(state)
    }

    override fun onPlayerEvent(eventCode: Int, bundle: Bundle?) {


    }

    override fun onErrorEvent(eventCode: Int, bundle: Bundle?) {
    }

    override fun onReceiverEvent(eventCode: Int, bundle: Bundle?) {
    }

    override fun onPrivateEvent(eventCode: Int, bundle: Bundle?): Bundle? {
        when (eventCode) {
            DataInter.ReceiverEvent.EVENT_LIVE_DANMU -> {//LiveController中开关通知是否显示，关闭弹幕后，只是不再添加新的弹幕，已经发在屏幕上的弹幕需要走完
                val openDanmu = bundle?.getBoolean(DataInter.Key.KEY_DANMU_TOGGLE, false) ?: false
                setDanmuViewState(openDanmu && PlayerConfig.liveStatus == LiveStatus.LIVING)//直播中显示弹幕
                if (!(openDanmu && PlayerConfig.liveStatus == LiveStatus.LIVING)){
                    view?.danmakuView?.clear()
                }
            }
        }
        return super.onPrivateEvent(eventCode, bundle)
    }


    override fun onCreateCoverView(context: Context?): View = View.inflate(context, R.layout.view_danmu, null)

    override fun getCoverLevel(): Int = levelLow(DataInter.CoverLevel.COVER_LEVEL_DANMU)


}