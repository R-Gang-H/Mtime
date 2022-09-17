package com.kotlin.android.community.repository

import androidx.collection.arrayMapOf
import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.common.CommBizCodeResult
import com.kotlin.android.app.data.entity.community.person.WantSeeInfo
import com.kotlin.android.app.data.entity.mine.CollectionMovie
import com.kotlin.android.community.ui.person.bean.UserHomeViewBean
import com.kotlin.android.retrofit.getRequestBody

/**
 * @author wangwei
 * @date 2020/9/21
 *
 * 用户主页内容
 */
class UserHomeRepository : BaseRepository() {
    /**
     * 加载推荐数据
     */
    suspend fun loadData(userId: Long):
            ApiResult<UserHomeViewBean> {
        return request(
            converter = {
                UserHomeViewBean(
                    it.albumCount,
                    it.articleCount,
                    it.authType,
                    it.avatarUrl,
                    it.fansCount,
                    it.filmCommentCount,
                    it.followCount,
                    it.followed ?: false,
                    it.gender,
                    it.groupCount,
                    it.hasSeenCount,
                    it.info,
                    it.journalCount,
                    it.nikeName,
                    it.postCount,
                    it.registDuration,
                    it.showArticle,
                    it.userId,
                    it.wantSeeCount,
                    it.userLevel,
                    it.status,
                    it.authRole,
                    it.imId,
                    it.backgroundAppUrl,
                    it.videoCount,
                    it.filmListCount,
                    it.audioCount,
                    it.showAudio,
                    it.creator,
                    it.creatorInfo?.praiseCount,
                    it.creatorInfo?.collectCount,
                    it.creatorInfo?.commentCount,
                    it.creatorInfo?.viewsCount,
                    it.creatorInfo?.ongoingMedalInfos?: arrayListOf(),
                    it.creatorInfo?.creatorAppLogoUrl
                )
            },
            api = {
                apiMTime.getUserHomeInfo(userId)
            })
    }

    /**
     * 关注用户
     */
    suspend fun followUser(action: Long, userId: Long): ApiResult<CommBizCodeResult> {
        return request { apiMTime.followUser(action, userId) }
    }


    suspend fun loadTimeLine(nextStamp: String?, pageSize: Long): ApiResult<WantSeeInfo> {
       /* val params = arrayMapOf<String, Any>()
        params["nextStamp"] = nextStamp
        params["pageSize"] = pageSize
        getRequestBody(params)*/
        return request { apiMTime.getTimeLineData(nextStamp,pageSize) }
        /*return ApiResult.Success<WantSeeInfo>(
            getTestData()
        )*/
    }

    private fun getTestData(): WantSeeInfo {
        var mainActors = arrayListOf<CollectionMovie.Person>()
        (0..4).map {
            mainActors.add(
                CollectionMovie.Person(
                    personNameEn = "iacky Wnag",
                    personNameCn = "阿奇马努",
                    personId = 111L
                )
            )
        }

        var mainDirectors = arrayListOf<CollectionMovie.Person>()
        (0..4).map {
            mainDirectors.add(
                CollectionMovie.Person(
                    personNameEn = "iacky Wnag",
                    personNameCn = "阿奇马努",
                    personId = 111L
                )
            )
        }
        fun getTime(index: Int): Long {
            return when (index) {
                0, 1 -> 1649409377000L
                2 -> 1644311777000L
                3 -> 1617873377000L
                4 -> 1620465377000L
                5 -> 1620465377000L
                else -> 1649409377000L
            }
        }
        return WantSeeInfo(
            count = 1111L,
            nextStamp = "ddddd",
            hasNext = true,
            items = (0..20).map {
                WantSeeInfo.Movie(
                    year = "",
                    releaseDate = "1111",
                    minutes = "",
                    rating = "9.2",
                    nameEn = "Month art",
                    imgUrl = "http://img5.mtime.cn/mg/2022/03/23/144612.34016727.jpg",
                    play = 0,
                    playState = 1L,
                    genreTypes = "恐怖/惊悚/故事",
                    name = "埃及月神",
                    id = 11L * (it + 1),
                    releaseArea = "美国",
                    hasTicket = false,
                    mainActors = mainActors,
                    mainDirectors = mainDirectors,
                    enterTime = CollectionMovie.CollectionTime(
                        "2022-22-2",
                        /*getTime(it)*/1649409377000L
                    ),
                    showDate = it % 2 == 0,
                    fcRating = "8.9",
                    shortInteractiveObj = WantSeeInfo.Content(
                        contentId = 111L,
                        type = 1L,
                        title = "ssssss",
                        mixWord = "shortMix",
                        fcType = 1L
                    ),
                    longInteractiveObj = WantSeeInfo.Content(
                        contentId = 111L,
                        type = 1L,
                        title = "aaaaa",
                        mixWord = "longmix",
                        fcType = 1L
                    ),
                    attitude = if (it % 2 == 0) 1L else 3L,
                    timeLineId = 11L
                )
            }
        )
    }
}