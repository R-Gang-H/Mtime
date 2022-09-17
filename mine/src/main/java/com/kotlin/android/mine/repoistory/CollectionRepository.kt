package com.kotlin.android.mine.repoistory

import android.text.TextUtils
import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.article.component.item.adapter.CollectionArticleBinder
import com.kotlin.android.article.component.item.bean.CollectionArticleViewBean
import com.kotlin.android.community.post.component.item.adapter.CollectionPostBinder
import com.kotlin.android.community.post.component.item.bean.CollectionPostViewBean
import com.kotlin.android.app.data.entity.mine.*
import com.kotlin.android.ktx.ext.time.TimeExt
import com.kotlin.android.mine.R
import com.kotlin.android.mine.bean.*
import com.kotlin.android.mine.binder.CollectionCinemaBinder
import com.kotlin.android.mine.binder.CollectionMovieBinder
import com.kotlin.android.mine.binder.CollectionPersonBinder
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import java.text.DecimalFormat
import java.util.*

/**
 * create by lushan on 2020/9/14
 * description:我的收藏repository  影人、影院、电影、 文章、帖子
 */
class CollectionRepository : BaseRepository() {
    private val START_PAGEINDEX = 1L
    private var pageIndexs: Long = START_PAGEINDEX// 我的收藏中每次只会有一种类型请求，故此处只用了一个pageIndexs
    private val PAGESIZE = 20L

    /**
     * 分页查询收藏的电影
     */
    suspend fun getCollectionMovie(isMore: Boolean): ApiResult<CollectionViewBean> {
        resetPageIndex(isMore)
        return request(
            api = { apiMTime.getMovieCollection(pageIndexs, PAGESIZE) },
            converter = { collectionMovie ->
                pageIndexs++
                CollectionViewBean(
                    collectionMovie.hasNext,
                    collectionMovie.totalCount,
                    collectionMovie.items?.map { converterCollectionMovieBinder(it) }
                        ?.toMutableList()
                        ?: mutableListOf<MultiTypeBinder<*>>()
                )
            })
    }

    /**
     * 分页查询收藏的影人
     */
    suspend fun getCollectionPerson(isMore: Boolean): ApiResult<CollectionViewBean> {
        resetPageIndex(isMore)
        return request(
            api = { apiMTime.getPersonCollection(pageIndexs, PAGESIZE) },
            converter = { collectionPerson ->
                pageIndexs++
                CollectionViewBean(
                    collectionPerson.hasNext,
                    collectionPerson.totalCount,
                    collectionPerson.items?.map { converterCollectionPersonBinder(it) }
                        ?.toMutableList()
                        ?: mutableListOf()
                )

            })
    }

    /**
     * 分页查询收藏的影院
     */
    suspend fun getCollectionCinema(isMore: Boolean): ApiResult<CollectionViewBean> {
        resetPageIndex(isMore)
        return request(
            api = { apiMTime.getCinemaCollection(pageIndexs, PAGESIZE) },
            converter = { collectionCinema ->
                pageIndexs++
                CollectionViewBean(
                    collectionCinema.hasNext,
                    collectionCinema.totalCount,
                    collectionCinema.items?.map { converterCollectionCinemaBinder(it) }
                        ?.toMutableList()
                        ?: mutableListOf()
                )
            })
    }


    /**
     * 分页查询收藏的文章
     */
    suspend fun getCollectionArticle(isMore: Boolean): ApiResult<CollectionViewBean> {
        resetPageIndex(isMore)
        return request(
            api = { apiMTime.getArticleCollection(pageIndexs, PAGESIZE) },
            converter = { collectionArticle ->
                pageIndexs++
                CollectionViewBean(
                    collectionArticle.hasNext,
                    collectionArticle.totalCount,
                    collectionArticle.items?.map { convertCollectionArticleBinder(it) }
                        ?.toMutableList()
                        ?: mutableListOf()
                )
            })
    }


    /**
     * 分页查询收藏的帖子
     */
    suspend fun getCollectionPost(isMore: Boolean): ApiResult<CollectionViewBean> {
        resetPageIndex(isMore)
        return request(
            api = { apiMTime.getPostCollection(pageIndexs, PAGESIZE) },
            converter = { collectionPost ->
                pageIndexs++
                CollectionViewBean(
                    collectionPost.hasNext,
                    collectionPost.totalCount,
                    collectionPost.items?.map { covertCollectionPostBinder(it) }?.toMutableList()
                        ?: mutableListOf()
                )

            })
    }

    /**
     * 刷新重置pageIndexs
     */
    private fun resetPageIndex(isMore: Boolean) {
        if (isMore.not()) pageIndexs = START_PAGEINDEX
    }

    /**
     * 获取收藏帖子binder
     */
    private fun covertCollectionPostBinder(bean: CollectionPost.Item): MultiTypeBinder<*> {
        return CollectionPostBinder(
            CollectionPostViewBean(
                postId = bean.obj?.contentId ?: 0L,
                postTitle = bean.obj?.title.orEmpty(),
                likeCount = bean.obj?.interactive?.praiseUpCount ?: 0L,
                replyCount = bean.obj?.interactive?.commentCount ?: 0L,
                isPkPost = bean.obj?.interactive?.voteStat != null
            )
        )
    }

    /**
     * 获取收藏文章binder
     */
    private fun convertCollectionArticleBinder(bean: CollectionArticle.Item): MultiTypeBinder<*> {
        val nickName = bean.obj?.createUser?.nikeName.orEmpty()
        return CollectionArticleBinder(
            CollectionArticleViewBean(
                articleId = bean.obj?.contentId ?: 0L,
                articleTitle = bean.obj?.title.orEmpty(),
                articlePic = if (bean.obj?.images?.isNotEmpty() == true) bean.obj?.images?.get(0)?.imageUrl.orEmpty() else "",
                articleAuth = if (TextUtils.isEmpty(nickName)) "" else getString(R.string.mine_collection_article_auther_format).format(
                    nickName
                )
            )
        )
    }

    /**
     * 获取收藏影院binder
     */
    private fun converterCollectionCinemaBinder(bean: CollectionCinema.Item): MultiTypeBinder<*> {
        return CollectionCinemaBinder(
            CollectionCinemaViewBean(
                cinemaId = bean.obj?.id ?: 0L,
                cinemaName = bean.obj?.name.orEmpty(),
                cinemaAddress = bean.obj?.address.orEmpty()
            )
        )
    }


    /**
     * 获取收藏影人binder
     */
    private fun converterCollectionPersonBinder(bean: CollectionPerson.Item): MultiTypeBinder<*> {
//        如果只有英文名，没有中文名，英文名需要显示在中文名位置，英文名不显示

        return CollectionPersonBinder(
            CollectionPersonViewBean(
                personId = bean.obj?.id ?: 0L,
                name = if (TextUtils.isEmpty(bean.obj?.nameCn.orEmpty())) bean.obj?.nameEn.orEmpty() else bean.obj?.nameCn.orEmpty(),
                nameEn = if (TextUtils.isEmpty(bean.obj?.nameCn.orEmpty())) "" else bean.obj?.nameEn.orEmpty(),
                imageUrl = bean.obj?.imgUrl.orEmpty(),
                profession = bean.obj?.profession.orEmpty(),
                likeRate = getLikeRating(bean.obj?.rating.orEmpty()),
                personInfo = bean.obj?.birthDate.orEmpty()
            )
        )
    }

    private fun getLikeRating(rating: String): String {
        if (TextUtils.isEmpty(rating)) {
            return ""
        }
        val likeRatingDouble = getLikeRatingDouble(rating)
        if (likeRatingDouble <= 0.0) {
            return ""
        }
        val df = DecimalFormat("#0")
        return "${df.format(10 * likeRatingDouble)}%"

    }

    private fun getLikeRatingDouble(rating: String): Double {
        if (TextUtils.isEmpty(rating)) {
            return 0.0
        }
        return try {
            rating.toDouble()
        } catch (e: Exception) {
            e.printStackTrace()
            0.0
        }
    }

    /**
     * 获取收藏影片binder
     */
    private fun converterCollectionMovieBinder(bean: CollectionMovie.Item): MultiTypeBinder<*> {
        val scoreDouble = getLikeRatingDouble(bean.obj?.rating.orEmpty())

        return CollectionMovieBinder(
            CollectionMovieViewBean(
                movieId = bean.obj?.id ?: 0L,
                movieName = if (TextUtils.isEmpty(bean.obj?.name)
                        .not()
                ) bean.obj?.name.orEmpty() else bean.obj?.nameEn.orEmpty(),
                moviePic = bean.obj?.imgUrl.orEmpty(),
                genreTypes = bean.obj?.genreTypes.orEmpty(),
                actors = "",
                releaseContent = TimeExt.date2String(
                    Date(
                        bean.collectTime?.stamp
                            ?: 0L
                    ), getString(R.string.collection_movie_date_pattern)
                ).orEmpty(),
                score = if (scoreDouble <= 0) "" else bean.obj?.rating.orEmpty()
            )
        )
    }
}