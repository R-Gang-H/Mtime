package com.kotlin.android.community.repository

import androidx.collection.arrayMapOf
import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.CommHasMoreList
import com.kotlin.android.app.data.entity.community.person.CollectionContentList
import com.kotlin.android.app.data.entity.community.person.ContentList
import com.kotlin.android.app.data.entity.filmlist.MyCreateFilmList
import com.kotlin.android.app.data.entity.mine.CollectionCinema
import com.kotlin.android.app.data.entity.mine.CollectionMovie
import com.kotlin.android.app.data.entity.mine.CollectionPerson
import com.kotlin.android.article.component.item.bean.ArticleItem
import com.kotlin.android.community.card.component.item.bean.CommunityCardItem
import com.kotlin.android.retrofit.getRequestBody
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import okhttp3.RequestBody

/**
 * @author WangWei
 * @date 2020/9/23
 *
 * 社区-个人主页-内容、影评、日志、帖子 数据仓库
 *
 * 社区-用户主页api - 个人中心分页查询用户内容(/content/user_center.api)
 * http://apidocs.mt-dev.com/community-front-api/index.html#api-%E7%A4%BE%E5%8C%BA-%E7%94%A8%E6%88%B7%E4%B8%BB%E9%A1%B5api-queryUserContent
 * 内容类型 必填 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"), ARTICLE(4, "文章"); VIDEO(5, "视频"), AUDIO(6, "音频"),
 */
class PersonContentRepository : BaseRepository() {

    /**
     * 社区内容列表
     */
    suspend fun loadContent(
            type: Long,
            userId: Long,
            nextStamp: String?,
            pageSize: Long
    ): ApiResult<ContentList> {
        return request {
            apiMTime.getCommunityPersonContent(getRequestBody(nextStamp, pageSize, type, userId))
//            apiMTime.getCommunityPersonContentReleased(
//                getRequestBody(
//                    pageIndex,
//                    pageSize,
//                    type,
//                    userId
//                )
//            )
        }
    }

    /**
     * 个人收藏
     */
    suspend fun loadCollectContent(
            type: Long,
            userId: Long,
            nextStamp: String?,
            pageSize: Long
    ): ApiResult<CollectionContentList> {
        return request {
            apiMTime.getPersonCollectContent(type, nextStamp, pageSize)
//            apiMTime.getCommunityPersonContentReleased(
//                getRequestBody(
//                    pageIndex,
//                    pageSize,
//                    type,
//                    userId
//                )
//            )
        }
    }

    /**
     * 收藏电影
     */
    suspend fun loadCollectionMovie(
            nextStamp: String?,
            pageSize: Long
    ): ApiResult<CollectionMovie> {
        return request {
            apiMTime.getCollectionMovie(nextStamp, pageSize)
        }
    }

    /**
     * 收藏影人
     */
    suspend fun loadCollectionPerson(
            nextStamp: String?,
            pageSize: Long
    ): ApiResult<CollectionPerson> {
        return request {
            apiMTime.getCollectionPerson(nextStamp, pageSize)
        }

        /*var datas = arrayListOf<CollectionPerson.Item>()
        var movieItem = CollectionPerson.MovieItem("I love the sun", 1L, "我爱太阳")
        var movies = arrayListOf<CollectionPerson.MovieItem>()
        movies.add(movieItem)
        movies.add(movieItem)
        movies.add(movieItem)
        var person = CollectionPerson.Person(
            birthDate = "2022.4.1",
            id = 1L,
            imgUrl = "",
            nameCn = "冰敦敦",
            nameEn = "bingchewnchewn",
            profession = "演员/制片人/配音/导演",
            rating = "9.0",
            mainMovies = movies
        )
        datas.add(CollectionPerson.Item(obj = person))
        datas.add(CollectionPerson.Item(obj = person))
        return ApiResult.Success(
            CollectionPerson(items = datas)
        )*/
    }

    /**
     * 收藏影院
     */
    suspend fun loadCollectionCinema(
            nextStamp: String?,
            pageSize: Long
    ): ApiResult<CollectionCinema> {
        return request {
            apiMTime.getCollectionCinema(nextStamp, pageSize)
        }
    }

    /**
     * 影单
     */
    suspend fun loadFilmList(
            userId: Long,
            nextStamp: String?,
            pageSize: Long
    ): ApiResult<MyCreateFilmList> {
        return request {
            apiMTime.getMyFilmList(userId, nextStamp, pageSize)
        }
    }


    suspend fun loadDataUnreleased(pageIndex: Long, pageSize: Long, type: Long, userId: Long):
            ApiResult<CommHasMoreList<MultiTypeBinder<*>>> {
        return request(
                converter = {
                    val list = mutableListOf<MultiTypeBinder<*>>()
                    if (type == 4L)//文章
                        it.items?.run {
                            list.addAll(map {
                                ArticleItem.converter2ArticleBinder(it, false, isPublish = false)
                            })
                        }
                    else it.items?.forEach { item ->
                        list.add(
                                CommunityCardItem.converter2Binder(
                                        commContent = item,
                                        isPublish = false
                                )
                        )
                    }
                    CommHasMoreList(hasMore = it.hasNext, list = list)
                },
                api = {
                    apiMTime.getCommunityPersonContentUnreleased(
                            getRequestBody(
                                    pageIndex,
                                    pageSize,
                                    type,
                                    userId
                            )
                    )
                })
    }


    suspend fun loadDataReleased(pageIndex: Long, pageSize: Long, type: Long, userId: Long):
            ApiResult<CommHasMoreList<MultiTypeBinder<*>>> {
        return request(
                converter = {
                    val list = mutableListOf<MultiTypeBinder<*>>()
                    if (type == 4L)//文章
                        it.items?.run {
                            list.addAll(map {
                                ArticleItem.converter2ArticleBinder(it, false, isPublish = true)
                            })
                        }
                    else it.items?.forEach { item ->
                        list.add(
                                CommunityCardItem.converter2Binder(
                                        commContent = item,
                                        isPublish = true
                                )
                        )
                    }
                    CommHasMoreList(hasMore = it.hasNext, list = list)
                },
                api = {
                    apiMTime.getCommunityPersonContentReleased(
                            getRequestBody(
                                    pageIndex,
                                    pageSize,
                                    type,
                                    userId
                            )
                    )
                })
    }

    /**
     * 获取请求体
     */
    private fun getRequestBody(
            nextStamp: String?,
            pageSize: Long,
            type: Long,
            userId: Long
    ): RequestBody {
        val params = arrayMapOf<String, Any>(
                "userId" to userId,
                "type" to type,
                "pageSize" to pageSize
        )
        if (!nextStamp.isNullOrEmpty()){
            params["nextStamp"]=nextStamp
        }
        return getRequestBody(params)
    }
    /**
     * 获取请求体
     */
    private fun getRequestBody(
            pageIndex: Long,
            pageSize: Long,
            type: Long,
            userId: Long
    ): RequestBody {
        val params = arrayMapOf<String, Any>(
                "userId" to userId,
                "type" to type,
                "pageIndex" to pageIndex,
                "pageSize" to pageSize
        )
        return getRequestBody(params)
    }
}