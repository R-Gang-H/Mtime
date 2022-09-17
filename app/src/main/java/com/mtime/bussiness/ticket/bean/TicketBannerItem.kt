package com.mtime.bussiness.ticket.bean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.common.RegionPublish

/**
 * 创建者: vivian.wei
 * 创建时间: 2022/4/13
 * 描述: 购票BannerItem
 */
data class TicketBannerItem(
        var appImage: String = "",
        var appLink: String = ""
): ProguardRule {

    companion object {

        fun converter2BannerItems(data: RegionPublish): List<TicketBannerItem>? {
            data.apply {
                return regionList?.firstOrNull()?.items?.map {
                    TicketBannerItem(
                            appImage = it["appImage"].orEmpty(),
                            appLink = it["appLink"].orEmpty()
                    )
                }
            }
        }

    }

}
