package com.kotlin.android.home.repository

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.CommContent
import com.kotlin.android.app.data.entity.CommContentList
import com.kotlin.android.app.data.entity.common.RegionPublish
import com.kotlin.android.app.data.entity.home.OriginalRcmdContentList
import com.kotlin.android.app.data.entity.home.RcmdContent
import com.kotlin.android.app.data.entity.home.RcmdContentList

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/8/6
 */
class OriginalRepository : BaseRepository() {

    /**
     * 获取banner数据
     */
    suspend fun loadBanner(): ApiResult<RegionPublish> {
        return request {
            apiMTime.getRegionPublishList(CommConstant.RCMD_REGION_MTIME_ORIGINAL_BANNER)
        }
    }

    /**
     * 内容推荐api - 【2.0】原创内容推荐列表
     * rcmdTagsFilter: 推荐标签，逗号分隔，必传：1-电影，2-电视，3-音乐，4-人物，5-产业，6-全球拾趣，7-时光对话，8-时光策划，9-时光快讯，10-超级英雄，11-吐槽大会，12-时光大赏，13-精选，101-华语，102-欧美，103-日韩，104-其他
     */
    suspend fun loadData(
        rcmdTagsFilter: String,
        nextStamp: String?,
        pageSize: Long
    ): ApiResult<OriginalRcmdContentList> {
        return request {
            apiMTime.getCommunityRcmdOriginalList(
                rcmdTagsFilter,
                nextStamp,
                pageSize
            )
        }
        
//        return ApiResult.Success(getTestData())
    }
    
    private fun getTestData(): OriginalRcmdContentList {
        return OriginalRcmdContentList(
            rcmdContentList = RcmdContentList(
                nextStamp = "ddddd",
                hasNext = true,
                items = (0..20).map {
                    RcmdContent(
                        commContent = CommContent(
                            userCreateTime = CommContent.UserCreateTime(
                                show = "",
                                stamp = 1648093568
                            ),
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
        )
    }
    
}