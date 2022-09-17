package com.kotlin.android.player.ad

import com.kotlin.android.app.data.entity.player.VideoAdBean
import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/8/31
 * description:视频广告工具类
 */
class AdHelper {
    companion object {
        private val mAdDataMap: HashMap<String, List<VideoAdBean.AdItem>> = HashMap()


        fun updateAdList(vid: Long, videoType: Long) {
            val video: Video = Video(vid, videoType)
            val key: String = video.key()
            if (AdHelper.mAdDataMap.containsKey(key)) {
                val adItems: List<VideoAdBean.AdItem>? = mAdDataMap[key]
                if (adItems != null) {
                    return
                }
            }
            postLoad(video, null)
        }


        fun postLoad(video: Video, listener: OnAdDataListener?){
//       todo  加载广告信息
        }

        fun destory(){
            mAdDataMap.clear()
        }

        fun getAdList(vid: Long, videoType: Long): List<VideoAdBean.AdItem>? {
            val video: Video = Video(vid, videoType)
            return mAdDataMap[video.key()]
        }
    }



    class Video : ProguardRule {
        var vid: Long = 0L
        var videoType: Long = 0L

        constructor(vid: Long, videoType: Long) {
            this.vid = vid
            this.videoType = videoType
        }


        fun key():String = "$vid$videoType"
    }

    interface OnAdDataListener {
        fun onAdListReady(items: List<VideoAdBean.AdItem?>?)
    }

}