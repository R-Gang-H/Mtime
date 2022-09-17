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
import com.kotlin.android.app.data.entity.home.zhongcao.ZhongCaoRcmdData

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/8/6
 */
class ZhongCaoSubRepository : BaseRepository() {

    /**
     * 内容推荐api - 【2.0】种草推荐列表
     * subTypeId: 子分类Id，必传
     */
    suspend fun loadData(
        subTypeId: Long,
        nextStamp: String?,
        pageSize: Long
    ): ApiResult<ZhongCaoRcmdData> {
        return request {
            apiMTime.getCommunityRcmdZhongCaolList(
                subTypeId,
                nextStamp,
                pageSize
            )
        }

//        return ApiResult.Success(getTestData())
    }

    private fun getTestData(): RcmdContentList {
        return RcmdContentList(
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
    }

}