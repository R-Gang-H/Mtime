package com.kotlin.android.splash.repository

import android.text.TextUtils
import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.ktx.ext.dimension.screenHeight
import com.kotlin.android.ktx.ext.dimension.screenWidth
import com.kotlin.android.splash.bean.SplashAd

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/11/23
 */
class SplashRepository : BaseRepository() {
    /**
     * 获取启动页广告数据
     */
    suspend fun loadSplashAd(): ApiResult<SplashAd> {
        return request(
            converter = {
                val items = it.regionList?.firstOrNull()?.items
                if (items.isNullOrEmpty()) {
                    null
                } else {
                    items[0].let { map ->
                        val pic = if (screenHeight.toFloat() / screenWidth.toFloat() < 1.9F)
                            map["img"]
                        else
                            map["img2"]
                        val count = map["countDown"]?.toInt()
                        val gif = TextUtils.equals("2", map["srcType"])

                        SplashAd(
                            appLink = map["appLink"] ?: "",
                            img = pic ?: "",
                            countDown = count ?: 3,
                            isGif = gif
                        )
                    }
                }
            },
            api = {
                apiMTime.getRegionPublishList(CommConstant.RCMD_REGION_SPLASH_AD)
            })
    }
}