package com.kotlin.android.live.component.viewbean

import com.kotlin.android.app.data.entity.live.LiveDetail
import com.kotlin.android.app.data.ProguardRule

/**
 * 直播详情ViewBean
 */
data class LiveDetailViewBean(
    var title: String = "",         //直播间标题
    var description: String = "",   //直播间描述
    var image: String = "",         //直播间配图
) : ProguardRule {

    companion object {
        fun convert(liveDetail: LiveDetail): LiveDetailViewBean {
            return LiveDetailViewBean(
                title = liveDetail.title.orEmpty(),
                description = liveDetail.description.orEmpty(),
                image = liveDetail.image.orEmpty()
            )
        }
    }

}
