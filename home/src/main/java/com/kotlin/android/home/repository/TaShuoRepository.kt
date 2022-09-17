package com.kotlin.android.home.repository

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.CommContent
import com.kotlin.android.app.data.entity.common.RegionPublish
import com.kotlin.android.app.data.entity.home.RcmdContent
import com.kotlin.android.app.data.entity.home.RcmdContentList
import com.kotlin.android.app.data.entity.home.tashuo.RcmdFollowUser
import com.kotlin.android.app.data.entity.home.tashuo.TaShuoRcmdList

class TaShuoRepository : BaseRepository() {
    
    private fun getTestFollowUserList(): List<RcmdFollowUser> {
        return (0..10).map {
            RcmdFollowUser(
                fansCount = 999,
                latestContent = RcmdFollowUser.LatestContent(
                    contentId = 11,
                    type = 1,
                    title = "2021时光穿梭机六大主题活动·年终大奖大奖·年终大奖大奖·年终大奖大奖"
                ),
                followed = it % 2 == 0,
                userId = 11,
                nikeName = "时光穿梭机",
                avatarUrl = "http://img5.mtime.cn/up/2021/01/05/082019.12085078_128X128.jpg",
                authType = (it % 4) + 1L,
                authRole = "电影人"
            )
        }
    }
    
    private fun getTestContentList(): RcmdContentList {
        return RcmdContentList(
            nextStamp = "ddddd",
            hasNext = true,
            items = (0..20).map {
                RcmdContent(
                    commContent = CommContent(
                        createUser = CommContent.User(
                            avatarUrl = "http://img5.mtime.cn/up/2017/05/31/163840.61835564_128X128.jpg",
                            nikeName = "时光穿梭机",
                            authType = 1,
                            userId = 1
                        ),
                        contentId = 1,
                        type = 4,
                        title = "漫威新剧《月光骑士》洛杉矶首映 奥斯卡·伊萨克&伊桑·霍克亮相",
                        mixWord = "时光网讯　近日，漫威新剧 《月光骑士》在美国洛杉矶举行首映礼，主演 奥斯卡·伊萨克、 伊桑·霍克、 梅·卡拉马维，以及漫威影业总裁 凯文·费奇等出席。该剧将于3月30日上线Disney+。（CFP供图",
                        mixImages = listOf(
                            CommContent.Image(
                                imageUrl = "http://img5.mtime.cn/mg/2022/03/23/144612.34016727.jpg"
                            ),
                            CommContent.Image(
                                imageUrl = "http://img5.mtime.cn/mg/2022/03/23/144629.96309356.jpg"
                            ),
                            CommContent.Image(
                                imageUrl = "http://img5.mtime.cn/mg/2022/03/23/144629.96309356.jpg"
                            )
                        )
                    )
                )
            }
        )
    }
    
    suspend fun loadRcmdData(nextStamp: String?, pageSize: Long): ApiResult<TaShuoRcmdList> {
        return request { apiMTime.getCommunityRcmdTaShuoList(nextStamp, pageSize) }

//        val result = TaShuoRcmdList(
//            rcmdUserList = getTestFollowUserList(),
//            rcmdContentList = getTestContentList()
//        )
//        return ApiResult.Success(result)
    }

    /**
     * 获取banner数据
     */
    suspend fun loadBanner(): ApiResult<RegionPublish> {
        return request {
            apiMTime.getRegionPublishList(CommConstant.RCMD_REGION_TA_SHUO_BANNER)
        }
    }
}