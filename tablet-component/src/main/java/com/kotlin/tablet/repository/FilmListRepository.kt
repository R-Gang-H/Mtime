package com.kotlin.tablet.repository

import androidx.collection.arrayMapOf
import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.annotation.SEARCH_FILM_LIST
import com.kotlin.android.app.data.annotation.SEARCH_MOVIE
import com.kotlin.android.app.data.entity.common.CollectionResult
import com.kotlin.android.app.data.entity.common.CommonShare
import com.kotlin.android.app.data.entity.filmlist.*
import com.kotlin.android.app.data.entity.monopoly.CommResult
import com.kotlin.android.app.data.entity.monopoly.CommonBizMsgBean
import com.kotlin.android.retrofit.getRequestBody
import com.kotlin.tablet.adapter.FilmListAddBinder
import com.kotlin.tablet.ui.add.FilmCart

/**
 * 创建者: sunhao
 * 创建时间: 2022/3/11
 * 描述:片单 repository
 **/
class FilmListRepository : BaseRepository() {

    /**
     * 创建片单
     * title	String【必填】片单名称/标题
     * synopsis	String【选填】片单简介
     * coverUrl	String 选填】封面图url
     * coverFieldId	String【选填】封面图FiledId
     * privacyStatus  Number【必填】隐私状态：1公开，2私密/仅自己可见，默认公开（app，pc已确认）
     * movieIdsStr	String【选填】电影id集合，id顺序要与页面一致，多个用逗号分割
     */
    suspend fun create(
        title: String,
        synopsis: String,
        coverUrl: String,
        coverFieldId: String,
        privacyStatus: Long
    ): ApiResult<FilmListCreateResult> {
        return request {
            val body = getRequestBody(
                arrayMapOf(
                    "title" to title,
                    "synopsis" to synopsis,
                    "coverUrl" to coverUrl,
                    "coverFieldId" to coverFieldId,
                    "privacyStatus" to privacyStatus
                )
            )
            apiMTime.create(body)
        }
    }

    /**
     * 编辑片单
     *
     * filmListId Long【必填】片单id
     * title	String【必填】片单名称/标题
     * synopsis	String【选填】片单简介
     * coverUrl	String 选填】封面图url
     * coverFieldId	String【选填】封面图FiledId
     * privacyStatus  Number【必填】隐私状态：1公开，2私密/仅自己可见，默认公开（app，pc已确认）
     * movieIdsStr	String【选填】电影id集合，id顺序要与页面一致，多个用逗号分割
     * movieIds Array【选填】电影id集合
     */
    suspend fun edit(
        filmListId: Long,
        title: String,
        synopsis: String,
        coverUrl: String,
        coverFieldId: String,
        privacyStatus: Long,
        movieIds: List<Long>
    ): ApiResult<CommResult> {
        return request {
            val body = getRequestBody(
                arrayMapOf(
                    "filmListId" to filmListId,
                    "title" to title,
                    "synopsis" to synopsis,
                    "coverUrl" to coverUrl,
                    "coverFieldId" to coverFieldId,
                    "privacyStatus" to privacyStatus,
                    "movieIds" to movieIds
                )
            )
            apiMTime.edit(body)
        }
    }

    /**
     * 片单列表页面
     */
    suspend fun list(
        category: Long? = null,
        nextStamp: String? = null,
        pageSize: Long = 20L
    ): ApiResult<FilmListEntity> {
        return request {
            apiMTime.list(category, nextStamp, pageSize)
        }
    }

    /**
     * 片单详情 上半部分
     * filmListId LONG 片单id【必填】
     * source【选填】默认值为0； 0:缓存5分钟，1:缓存1秒-我创建的片单列表用，
     */
    suspend fun details(filmListId: Long, source: Long = 0L): ApiResult<FilmListBasicInfoEntity> {
        return request {
            apiMTime.baseInfo(filmListId, source)
        }
    }

    /**
     * 片单详情  下半部分
     * filmListId LONG 片单id【必填】
     * type LONG 【选填】类型：0全部，1可播放，2未看过
     * nextStamp String【选填】下一页标识，默认或第一页可为null
     * pageSize	Number【选填】每页页面数，默认20
     * source【选填】默认值为0； 0:缓存5分钟，1:缓存1秒-我创建的片单列表用，
     */
    suspend fun pageMovies(
        filmListId: Long,
        type: Long = 0,
        nextStamp: String? = "",
        pageSize: Long = 20,
        source: Long = 0L
    ): ApiResult<FilmListPageMoviesEntity> {
        return request {
            apiMTime.pageMovies(filmListId, type, nextStamp, pageSize, source)
        }
    }

    /**
     * 搜索影片
     */
    suspend fun searchFilm(
        keyWord: String,
        pageIndex: Long,
        pageSize: Long = 20,
    ): ApiResult<List<FilmListAddBinder>> {
        return request(api = {
            apiMTime.unionSearch(
                keyWord,
                searchType = SEARCH_MOVIE,
                pageIndex = pageIndex,
                pageSize = pageSize
            )
        }, converter = { cover ->
            cover.movies?.map {
                FilmCart.instance.getSelectedData().forEach { cache ->
                    if (cache.key == it.movieId) {
                        it.isAdd = true
                        return@forEach
                    }
                }
                FilmListAddBinder(it)
            } ?: listOf()
        })
    }

    /**
     * 分享片单
     * filmListId LONG 片单id【必填】
     */
    suspend fun share(filmListId: Long): ApiResult<FilmListShareEntity> {
        return request {
            apiMTime.share(filmListId)
        }
    }

    /**
     * 投稿当前活动
     */
    suspend fun reqCurrActivities(
        nextStamp: String? = null,
        pageSize: Long = 200000L
    ): ApiResult<CurrActivityInfo> {
        return request {
            apiMTime.currActivity(nextStamp, pageSize)
        }
    }

    /**
     * 投稿历史活动
     */
    suspend fun reqHistoryActivities(
        nextStamp: String? = "",
        pageSize: Long = 20L
    ): ApiResult<HistoryActivityInfo> {
        return request(converter = {
            it.apply {
                activitys?.forEach {
                    it.isHistory = true
                }
            }
        }) { apiMTime.historyActivity(nextStamp, pageSize) }
    }

    /**
     * 我创建的片单-个人中心
     */
    suspend fun reqMyCreates(
        nextStamp: String? = "",
        pageSize: Long = 20L
    ): ApiResult<MyCreateFilmList> {
        return request { apiMTime.myCreates(nextStamp, pageSize) }
    }

    /**
     * 我创建的片单-投稿
     */
    suspend fun reqMyManuscripts(
        nextStamp: String? = "",
        pageSize: Long = 20L,
        activityId: Long?
    ): ApiResult<MyCreateFilmList> {
        return request { apiMTime.myManuscripts(nextStamp, pageSize, activityId) }
    }

    /**
     * 我收藏的片单
     */
    suspend fun reqMyCollection(
        nextStamp: String? = "",
        pageSize: Long = 20L
    ): ApiResult<MyCollectionFilmList> {
        return request { apiMTime.myCollection(nextStamp, pageSize) }
    }

    /**
     * 取消收藏
     * @param filmListId 片单Id
     */
    suspend fun cancelCollect(
        action: Long,
        objType: Long,
        objId: Long,
        position: Int
    ): ApiResult<FilmListCollectExtend<Any>> {
        return request(converter = {
            FilmListCollectExtend(it, position)
        }, api = { apiMTime.collection(action, objType, objId) })
    }

    /**
     * 投稿
     */
    suspend fun reqContribute(
        activityId: Long,
        filmListId: Long
    ): ApiResult<CommResult> {
        return request {
            val body = getRequestBody(
                arrayMapOf(
                    "activityId" to activityId,
                    "filmListId" to filmListId
                )
            )
            apiMTime.reqContribute(body)
        }
    }

    /**
     * 添加电影
     * filmListId	Number【必填】片单id
     * movieIds 电影集合
     */
    suspend fun addMovies(
        movieIds: List<Long>,
        filmListId: Long
    ): ApiResult<CommonBizMsgBean> {
        return request {
            val body = getRequestBody(
                arrayMapOf(
                    "movieIds" to movieIds,
                    "filmListId" to filmListId
                )
            )
            apiMTime.addMovies(body)
        }
    }

    /**
     * 编辑-片单信息
     */
    suspend fun reqFilmListInfo(filmListId: Long): ApiResult<FilmListEditInfo> {
        return request { apiMTime.reqFilmListInfo(filmListId) }
    }

    /**
     * 编辑-已选电影
     */
    suspend fun getModifyMovies(
        filmListId: Long
    ): ApiResult<FilmListModifyMovieInfo> {
        return request { apiMTime.getModifyMovies(filmListId) }
    }

    /**
     * 片单详情 删除片单
     */
    suspend fun delete(filmListId: Long): ApiResult<FilmListCreateResult> {
        return request {
            val body = getRequestBody(
                arrayMapOf(
                    "filmListId" to filmListId
                )
            )
            apiMTime.delete(body)
        }
    }

    /**
     * 片单详情-收藏
     */
    suspend fun collect(action: Long, filmListId: Long): ApiResult<CollectionResult> {
        return request {
            apiMTime.collection(action, 10, filmListId)
        }
    }

    /**
     * 获取分享信息(/utility/share.api)
     *
     * type 1	文章详情页 2	片单详情页 3	视频详情页 4	家族详情页 5	帖子详情页 6	长影评页面 7 短影评页面 8	日志详情页 9	相册详情页 10	卡片大富翁 11	影人详情页 12	影片资料页
     * relateId 分享对象的ID，用于获取该对象的相关内容
     * secondRelateId 分享对象ID2（用于需要多个ID才能获取到分享内容的情况）
     */
    suspend fun getContributeShareInfo(
        type: Long,
        relateId: Long,
        secondRelateId: Long = 0
    ): ApiResult<CommonShare> {
        return request {
            apiMTime.getShareInfo(type, relateId, secondRelateId)
        }
    }

    /**
     * 片单列表搜索
     */
    suspend fun searchList(
        keyWord: String,
        pageIndex: Long = 1,
        pageSize: Long = 20,
        filter: String?
    ): ApiResult<FilmListSearchEntity> {
        return request {
            apiMTime.filmListSearch(
                keyword = keyWord,
                pageIndex = pageIndex,
                pageSize = pageSize,
                sortType = 0,
                filter
                )
        }
    }
}