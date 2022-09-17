package com.kotlin.android.home.ui.bean

import android.graphics.Rect
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.common.RegionPublish
import com.kotlin.android.home.ui.adapter.BannerBinder
import com.kotlin.android.ktx.ext.dimension.dp

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/10
 *
 * 首页-banner item ui 实体
 */
data class BannerItem(
    var img: String? = "",
    var applinkData: String? = ""
) : ProguardRule {
    companion object {
        fun converter2BannerItems(data: RegionPublish): List<BannerItem>? {
            data.apply {
                return regionList?.firstOrNull()?.items?.map {
                    converter2BannerItem(it)
                }
            }
        }

        private fun converter2BannerItem(it: Map<String, String>): BannerItem {
            val imgUrl = when {
                it.contains("imgApp") -> {
                    it["imgApp"]
                }
                it.contains("appImageUrl") -> {
                    it["appImageUrl"]
                }
                else -> {
                    it["image"]
                }
            }
            val appLink = when {
                it.contains("targetAppLink") -> {
                    it["targetAppLink"]
                }
                it.contains("appLink") -> {
                    it["appLink"]
                }
                else -> {
                    it["applink"]
                }
            }
            return BannerItem(
                img = imgUrl,
                applinkData = appLink
            )
        }
        
        fun converter2Binder(
            data: RegionPublish,
            marginRect: Rect = Rect(7.dp, 0, 7.dp, 0)
        ): BannerBinder? {
            data.apply {
                regionList?.firstOrNull()?.items?.let {
                    if (it.isNullOrEmpty().not()) {
                        return BannerBinder(it.map { gallery ->
                            converter2BannerItem(gallery)
                        }, marginRect)
                    }
                }

            }
            return null
        }
    }
}