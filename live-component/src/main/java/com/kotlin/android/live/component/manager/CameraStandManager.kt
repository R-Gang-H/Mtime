package com.kotlin.android.live.component.manager

import com.kotlin.android.app.data.entity.live.CameraStandList
import com.kotlin.android.app.data.entity.live.DanmuBean
import com.kotlin.android.app.data.entity.live.LiveDetail
import com.kotlin.android.app.data.entity.video.VideoPlayList
import com.kotlin.android.app.data.entity.video.VideoPlayUrl
import com.kotlin.android.ktx.utils.LogUtils
import com.kotlin.android.live.component.ui.adapter.CameraStandBinder
import com.kotlin.android.live.component.viewbean.CameraStandViewBean
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2021/3/9
 * description:
 */
object CameraStandManager {
    //    机位列表
    private var cameraStandList = mutableListOf<CameraStandViewBean>()

    //    直播相关视频列表
    private var videoList = mutableListOf<LiveDetail.Video>()

    //    弹幕数据集合
    private var danmuList = mutableListOf<DanmuBean>()


    /**
     * 获取弹幕集合
     */
    fun getDanmuList() = danmuList
    /**
     * 获取直播相关视频数据
     */
    fun getVideoList() = videoList


    /**
     * 获取机位数据列表
     */
    fun getCameraStandList() = cameraStandList

    fun setVideoList(bean: LiveDetail?) {
        bean ?: return
        bean?.video?.apply {
            videoList.clear()
            videoList.addAll(this)
        }
    }

    /**
     * 获取机位binder列表
     */
    fun getCameraStandBinderList(isPortrait:Boolean): MutableList<CameraStandBinder> {
        return cameraStandList.map { CameraStandBinder(it,isPortrait) }.toMutableList()
    }

    fun getCameraStandBinderList2(): MutableList<CameraStandViewBean> {
        return cameraStandList
    }
    

    /**
     * 获取下一个要播放的视频
     */
    fun getNextPlayVideo(videoId: Long): LiveDetail.Video? {
        var indexOfFirst = videoList.indexOfFirst { it.videoId == videoId }
        return if (indexOfFirst < 0 || indexOfFirst >= videoList.size - 1) {
            null
        } else {
            videoList[indexOfFirst + 1]
        }
    }


    fun getCurrentPlayVideo(videoId: Long):LiveDetail.Video?{
        var indexOfFirst = videoList.indexOfFirst { it.videoId == videoId }
        return if (indexOfFirst < 0 || indexOfFirst > videoList.size - 1) {
            null
        } else {
            videoList[indexOfFirst]
        }
    }

    /**
     * 获取相关视频第一条数据
     */
    fun getFistPlayVideo():LiveDetail.Video?{
        return if (videoList.isNotEmpty()) {
            videoList[0]
        }else{
            null
        }
    }

    private fun setCameraStandList(cameraStandList: MutableList<CameraStandViewBean>) {
        CameraStandManager.cameraStandList.clear()
        CameraStandManager.cameraStandList.addAll(cameraStandList)
    }


    /**
     * 转换机位数据
     */
    fun setCameraData(cameraStandBean: CameraStandList) {
        cameraStandBean.cameraList?.map {
            CameraStandViewBean(
                    cameraId = it.videoId,
                    img = it.img.orEmpty(),
                    title = it.title.orEmpty(),
                    isSelected = false
            )
        }?.toMutableList()?.also {
            setCameraStandList(it)
        }
    }


    /**
     * 获取选中机位后的列表
     */
    fun getCameraStandListWithSelected(selectedBean: CameraStandViewBean,isPortrait: Boolean): MutableList<CameraStandBinder> {
        LogUtils.e("切换机位","切换机位：${selectedBean.cameraId}")
        cameraStandList?.forEach {
            it.isSelected = selectedBean.cameraId == it.cameraId
        }

        return getCameraStandBinderList(isPortrait)
    }

    fun getCameraStandListWithSelected2(selectedBean: CameraStandViewBean): MutableList<CameraStandViewBean> {
        cameraStandList?.forEach {
            it.isSelected = selectedBean.cameraId == it.cameraId
        }
        return cameraStandList
    }

    fun getCameraStandListIndex(selectedBean: CameraStandViewBean):Int{
        return cameraStandList?.indexOfFirst { selectedBean.cameraId == it.cameraId }
    }

    /**
     * 第一个选中的机位index
     */
    fun getCameraStandSelectIndex():Int{
        return cameraStandList.indexOfFirst { it.isSelected }
    }


    fun setDanmuList(list: MutableList<DanmuBean>){
        danmuList.clear()
        danmuList.addAll(list)
    }


    fun release() {
        videoList.clear()
        cameraStandList.clear()
    }

    fun containsVideoId(currentVideoId: Long):Boolean {
        return cameraStandList.any { it.cameraId == currentVideoId }
    }

    /**
     * 更新当前播放的机位id
     */
    fun updateCameraVideoId(videoId: Long) {
        cameraStandList?.forEach {
            it.isSelected = it.cameraId == videoId
        }
    }

    fun getCurrentCameraStandTitle():String?{
        return cameraStandList.firstOrNull { it.isSelected }?.title.orEmpty()
    }

    fun getPlayVideoPlayListByVideoId(videoId: Long): VideoPlayList?{
        val videoList = getVideoList().filter { it.videoId == videoId }
        if (videoList.isEmpty()){
            return null
        }

        val video = videoList[0]
        val playUrlList = arrayListOf<VideoPlayUrl>()
        playUrlList.add(VideoPlayUrl(
                url = video.url.orEmpty(),
                name = "超清",
                resolutionType = 2L
        ))

        playUrlList.add(VideoPlayUrl(
                url = video.hightUrl.orEmpty(),
                name = "高清",
                resolutionType = 1L
        ))
        return VideoPlayList(playUrlList)

    }
}