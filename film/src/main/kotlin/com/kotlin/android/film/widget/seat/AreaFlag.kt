package com.kotlin.android.film.widget.seat

import com.kotlin.android.app.data.entity.film.seat.Area
import com.kotlin.android.mtime.ktx.PriceUtils

/**
 *
 * Created on 2022/2/9.
 *
 * @author o.s
 */
class AreaFlag {
    /**
     * "areaId":"01",
     * "areaPrice":{
     * "areaCode":"",
     * "areaName":"",
     * "cinemaFee":0,
     * "cinemaPrice":0,
     * "extPlatformFee":0,
     * "channelFee":0,
     * "salesPrice":0,
     * "settlePrice":0
     * },
     * "maxcolcnt":17,
     * "maxrowcnt":13,
     */
    var areaId: String = "" // areaCode
//    var areaCode: String = ""
    var areaName: String = ""
    var areaPriceFlag: String = ""
    var salesPrice: Int = 0
    var ticketPrice: Int = 0
    var totalFee: Int = 0
    var cinemaFee: Int = 0
    var cinemaPrice: Int = 0
    var extPlatformFee: Int = 0
    var channelFee: Int = 0
    var settlePrice: Int = 0
    var maxColIndex: Int = 0
    var maxRowIndex: Int = 0
    var activityCode: String = "" // 活动code
    var activityName: String = ""
    var activityPrice: Int = 0
    var userLimitNum: Int = 0
    var areaLevel: AreaLevel = AreaLevel.AREA_LEVEL_1

    companion object {
        fun create(area: Area?): AreaFlag {
            val flag = AreaFlag()
            if (area != null) {
                flag.areaId = area.areaCode.orEmpty()
                flag.areaName = area.areaName.orEmpty()
                val priceBean = area.areaPrice
                flag.salesPrice = priceBean?.salesPrice ?: 0
                flag.ticketPrice = priceBean?.ticketPrice ?: 0
                flag.totalFee = priceBean?.totalFee ?: 0

                val len = flag.areaName.length
                val areaNameSub = if (len > 5) {
                    flag.areaName.substring(0, 5)
                } else {
                    flag.areaName
                }
                flag.areaPriceFlag = areaNameSub + " ¥ " + PriceUtils.formatPrice(flag.salesPrice.toDouble())
            }
            return flag
        }
    }
}