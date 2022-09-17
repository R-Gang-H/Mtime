package com.kotlin.android.live.component.receivers

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import com.kk.taurus.playerbase.receiver.BaseCover
import com.kk.taurus.playerbase.receiver.IReceiverGroup
import com.kotlin.android.app.data.entity.live.DirectorUnits
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.dimension.screenHeight
import com.kotlin.android.ktx.ext.dimension.screenWidth
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.live.component.R
import com.kotlin.android.player.DataInter
import kotlinx.android.synthetic.main.view_live_director_units.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * create by lushan on 2021/11/23
 * des:直播导播台
 **/
class DirectorUnitsCover(context: Context) : BaseCover(context) {
    private var isPortrait = true//默认竖屏
    private var layerImage: DirectorUnits.LayerImage? = null
    private var directorUnits: DirectorUnits? = null
    private var loadedLayerImages: ArrayList<AppCompatImageView> = arrayListOf()//已加载的图层
    var imageViewList = arrayListOf<AppCompatImageView>()

    private val onGroupValueUpdateListener: IReceiverGroup.OnGroupValueUpdateListener =
        object : IReceiverGroup.OnGroupValueUpdateListener {
            override fun filterKeys(): Array<String> {
                return arrayOf(
                    DataInter.Key.KEY_LIVE_DIRECTOR_UNITS,
                    DataInter.Key.KEY_IS_FULL_SCREEN,
                    DataInter.Key.KEY_LIST_COMPLETE
                )
            }

            override fun onValueUpdate(key: String?, value: Any?) {
                when (key) {
                    DataInter.Key.KEY_IS_FULL_SCREEN -> {//全屏按钮
                        //setImageLocation(layerImage)//返回时机不对
                    }
                    DataInter.Key.KEY_LIST_COMPLETE -> {//播放结束

                    }
                    DataInter.Key.KEY_LIVE_DIRECTOR_UNITS -> {//导播台内容信息
                        initDirectorView(value as? DirectorUnits)
                    }
                }
            }
        }

    private fun initDirectorView(directorUnits: DirectorUnits?) {
        this.directorUnits = directorUnits
        directorUnits?.apply {
            caption?.apply {
                view?.directTv?.apply {
                    try {
                        setTextColor(Color.parseColor("#${color.orEmpty()}"))
                        text = value.orEmpty()
                        this.visible()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } ?: hideTextView()

            if (layerImages?.isEmpty() == true) {
                layerImage = null
            }

            //移除已加载的图层
            removeLoadedLayerImages()

            layerImages?.forEach {
                addImageView(it)
            }
        } ?: hideAllView()
    }

    private fun removeLoadedLayerImages() {
        if (loadedLayerImages.isNotEmpty()) {
            loadedLayerImages.forEach {
                view?.rl_live_director?.removeView(it)
            }
            loadedLayerImages = arrayListOf()
        }
    }

    private fun addImageView(layerImage: DirectorUnits.LayerImage) {
        layerImage.apply {
            val imageview: AppCompatImageView = AppCompatImageView(context)
            val layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val sw = screenWidth
            val sh =
                if (isPortrait.not()) screenHeight else if (view.measuredHeight != 0) view.measuredHeight else screenHeight
            layoutParams.let {
                it.leftMargin = (xPos * sw / 1000).toInt()
                it.topMargin = (yPos * sh / 1000).toInt()
                it.width = (proportion * sw / 1000).toInt()
                it.height = if (width == 0L) height.toInt() else (it.width * height / width).toInt()
            }
            imageview.apply {
                postDelayed({
                    if (width != 0 && height != 0) {
                        loadImage(
                            data = layerImage.url.orEmpty(),
                            width = width,
                            height = height)
                    }
                }, 100)
            }

            view?.rl_live_director?.addView(imageview, layoutParams)
            loadedLayerImages.add(imageview)
            imageViewList.add(imageview)
        }
    }

    private fun hideTextView() {
        view?.directTv?.gone()
    }

    private fun hideAllView() {
        hideTextView()
    }

    private fun isFullScreen(): Boolean {
        return groupValue.getBoolean(DataInter.Key.KEY_IS_FULL_SCREEN)
    }

    override fun onReceiverBind() {
        super.onReceiverBind()
        groupValue.registerOnGroupValueUpdateListener(onGroupValueUpdateListener)
    }

    override fun onReceiverUnBind() {
        super.onReceiverUnBind()
        groupValue.unregisterOnGroupValueUpdateListener(onGroupValueUpdateListener)
    }


    override fun onPlayerEvent(eventCode: Int, bundle: Bundle?) {
        when (eventCode) {
            DataInter.ProviderEvent.EVENT_LIVE_CAMERA_LIST_ORIENTATION -> {//横竖屏切换
                //默认是竖屏
                isPortrait =
                    bundle?.getBoolean(DataInter.Key.KEY_LIVE_CAMERA_LIST_ORIENTATION, true) ?: true

                imageViewList.forEach {
                    view?.rl_live_director?.removeView(it)
                }

                //从横屏切换未竖屏 为了保证获取view高度正确，延时计算高度
                CoroutineScope(Dispatchers.Main).launch {
                    delay(100)
                    directorUnits?.layerImages?.forEach {
                        addImageView(it)
                    }
                }
            }
        }
    }

    override fun onErrorEvent(eventCode: Int, bundle: Bundle?) {}

    override fun onReceiverEvent(eventCode: Int, bundle: Bundle?) {}

    override fun onCreateCoverView(context: Context?): View =
        View.inflate(context, R.layout.view_live_director_units, null)

    override fun getCoverLevel(): Int = levelLow(DataInter.CoverLevel.COVER_DIRECTOR_UNITS)
}