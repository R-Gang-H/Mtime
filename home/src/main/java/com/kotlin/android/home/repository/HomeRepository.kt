package com.kotlin.android.home.repository

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.home.HomeTabNavList
import com.kotlin.android.mtime.ktx.GlobalDimensionExt

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/8/20
 */
class HomeRepository : BaseRepository() {
    suspend fun loadNav(cityId: String): ApiResult<HomeTabNavList> {
        return request {
            apiMTime.getIndexAppNav("1", cityId)
        }
        
//        return ApiResult.Success(getTestNav())
    }
    
//    private fun getTestNav(): HomeTabNavList {
//        return HomeTabNavList(
//            tabList = listOf(
//                HomeTabNavList.TabNav(
//                    name = "推荐",
//                    tips = "测试",
//                    redirectType = 1,
//                    appPageCode = HomeTabNavList.TYPE_RECOMMEND,
//                    h5Url = ""
//                ),
//                HomeTabNavList.TabNav(
//                    name = "时光原创",
//                    tips = "测试",
//                    redirectType = 1,
//                    appPageCode = HomeTabNavList.TYPE_ORIGINAL,
//                    h5Url = ""
//                ),
//                HomeTabNavList.TabNav(
//                    name = "TA说",
//                    tips = "测试",
//                    redirectType = 1,
//                    appPageCode = HomeTabNavList.TYPE_TA_SHUO,
//                    h5Url = ""
//                ),
//                HomeTabNavList.TabNav(
//                    name = "种草",
//                    tips = "测试",
//                    redirectType = 1,
//                    appPageCode = HomeTabNavList.TYPE_ZHONG_CAO,
//                    h5Url = ""
//                ),
//                HomeTabNavList.TabNav(
//                    name = "H5活动",
//                    tips = "测试",
//                    redirectType = 2,
//                    appPageCode = HomeTabNavList.TYPE_H5,
//                    h5Url = "http://www.mtime.com"
//                )
//            )
//        )
//    }

}