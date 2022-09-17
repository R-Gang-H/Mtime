package com.kotlin.android.home.repository

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.CommContent
import com.kotlin.android.app.data.entity.common.RegionPublish
import com.kotlin.android.app.data.entity.home.*
import com.kotlin.android.app.data.entity.video.VideoPlayList
import com.kotlin.android.mtime.ktx.GlobalDimensionExt.getCurrentCityId

/**
 *
 * Created on 2020/6/18.
 *
 * @author o.s
 */
class RecommendRepository : BaseRepository() {
    /**
     * 获取banner数据
     */
    suspend fun loadBanner(): ApiResult<RegionPublish> {
        return request {
            apiMTime.getRegionPublishList(CommConstant.RCMD_REGION_HOME_BANNER)
        }
    }

    /**
     * 获取正在热映和即将上映数据
     */
    suspend fun loadShowingComingMovies(): ApiResult<HomeShowingComingMovies> {
        return request {
            apiMTime.getHomeShowingComingMovies(getCurrentCityId())
        }
    }

    /**
     * 获取每日佳片列表
     * 
     * type 必填】 1当前日期推荐，2历史推荐
     */
    suspend fun loadTrailers(type: Long): ApiResult<RcmdTrailerList> {
        return request {
            apiMTime.getHomeRcmdTrailers(type)
        }
        
//        return ApiResult.Success(
//            RcmdTrailerList(
//                items = listOf(
//                    RcmdTrailerList.RcmdTrailer(
//                        videoId = 82657,
//                        bigTitle = "《巴霍巴利王》系列导演新作《RRR》预告",
//                        smallTitle = "印度神片《RRR》预告",
//                        appBigImageUrl = "http://img5.mtime.cn/mg/2022/03/29/080519.38936592.jpg",
//                        appSmallImageUrl = "http://img5.mtime.cn/mg/2022/03/29/080521.27978320.jpg",
//                        useDate = "2022.3.25"
//                    ),
//                    RcmdTrailerList.RcmdTrailer(
//                        videoId = 82639,
//                        bigTitle = "八个女人八台戏 美剧《咆哮》曝中字预告",
//                        smallTitle = "美剧《咆哮》曝中字预告",
//                        appBigImageUrl = "http://img5.mtime.cn/mg/2022/03/25/115312.64403172.jpg",
//                        appSmallImageUrl = "http://img5.mtime.cn/mg/2022/03/25/115310.48680979.jpg",
//                        useDate = "2022.3.25"
//                    ),
//                    RcmdTrailerList.RcmdTrailer(
//                        videoId = 82652,
//                        bigTitle = "休劳瑞执导《悬崖上的谋杀》正式预告",
//                        smallTitle = "《悬崖上的谋杀》正式预告",
//                        appBigImageUrl = "http://img5.mtime.cn/mg/2022/03/26/124011.33273868.jpg",
//                        appSmallImageUrl = "http://img5.mtime.cn/mg/2022/03/26/124018.84720630.jpg",
//                        useDate = "2022.3.25"
//                    ),
//                    RcmdTrailerList.RcmdTrailer(
//                        videoId = 82630,
//                        bigTitle = "讲述《教父》诞生的美剧《参与其中》预告",
//                        smallTitle = "美剧《参与其中》预告",
//                        appBigImageUrl = "http://img5.mtime.cn/mg/2022/03/24/110111.99709458.jpg",
//                        appSmallImageUrl = "http://img5.mtime.cn/mg/2022/03/24/110114.79848787.jpg",
//                        useDate = "2022.3.25"
//                    )
//                )
//            )
//        )
    }

    /**
     * 获取视频播放地址
     */
    suspend fun getPlayUrlList(
        videoId: Long,
        source: Long,
        scheme: String
    ): ApiResult<VideoPlayList> {
        return request {
            apiMTime.getPlayUrl(videoId, source, scheme)
        }
    }

    /**
     * 加载feed流数据
     */
    suspend fun loadRcmdData(nextStamp: String?, pageSize: Long): ApiResult<HomeRcmdContentList> {
        return request {
            apiMTime.getCommunityRcmdAppIndexlList(nextStamp, pageSize)
        }
        
//        return ApiResult.Success(getTestRcmdData())
    }

    private fun getTestRcmdData(): HomeRcmdContentList {
        return HomeRcmdContentList(
            nextStamp = "ddddd",
            hasNext = true,
            items = (0..15).map {
                HomeRcmdContent(
                    itemType = if (it > 1) 1 else 0,
                    adv = getTestAdv(),
                    content = getTestRcmdContent(it)
                )
            }
        )
    }
    
    private fun getTestAdv(): Adv {
        return Adv(
            img = "http://img5.mtime.cn/mg/2020/07/10/110941.31254968_285X160X4.jpg",
            title = "这是广告标题",
            subTitle = "这是广告子标题",
            appLink = "dddddd"
        )
    }
    
    private fun getTestRcmdContent(it: Int): RcmdContent {
        return RcmdContent(
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
}