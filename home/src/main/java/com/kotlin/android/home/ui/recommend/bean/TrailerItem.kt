package com.kotlin.android.home.ui.recommend.bean

import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.home.RcmdTrailerList
import com.kotlin.android.home.ui.recommend.adapter.TrailerBinder
import com.kotlin.android.home.ui.recommend.adapter.TrailerItemBinder
import com.kotlin.android.player.dataprovider.MTimeDataProvider

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/13
 *
 * 每日佳片UI实体
 */
data class TrailerItem(
    var videoId: Long,
    var videoSource: Long,
    var bigPic: String = "",
    var pic: String = "",
    var title: String = "",
    var subTitle: String = "",
    var releaseDate: String? = ""
) : ProguardRule {
    companion object {
        fun converter2UIBean(it: RcmdTrailerList.RcmdTrailer): TrailerItem {
            return TrailerItem(
                videoId = it.videoId ?: 0,
                videoSource = 1,
                title = it.bigTitle.orEmpty(),
                subTitle = it.smallTitle.orEmpty(),
                pic = it.appSmallImageUrl.orEmpty(),
                bigPic = it.appBigImageUrl.orEmpty(),
                releaseDate = it.useDate
            )
        }
        
        fun converter2Binder(
            lifecycle: Lifecycle,
            recyclerView: RecyclerView,
            provider: MTimeDataProvider,
            data: RcmdTrailerList
        ): TrailerBinder? {
            data.items?.apply {
                if (isNotEmpty()) {
                    return TrailerBinder(
                        map {
                            TrailerItemBinder(
                                converter2UIBean(it)
                            )
                        },
                        lifecycle,
                        recyclerView,
                        provider
                    )
                }
            }
            return null
        }
    }
}