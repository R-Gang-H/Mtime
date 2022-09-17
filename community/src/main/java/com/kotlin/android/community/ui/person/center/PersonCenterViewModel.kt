package com.kotlin.android.community.ui.person.center

import android.text.TextUtils
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.BinderUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.app.api.viewmodel.CommViewModel
import com.kotlin.android.app.data.entity.community.person.CollectionContentList
import com.kotlin.android.app.data.entity.community.person.ContentList
import com.kotlin.android.app.data.entity.filmlist.MyCreate
import com.kotlin.android.app.data.entity.filmlist.MyCreateFilmList
import com.kotlin.android.app.data.entity.mine.CollectionCinema
import com.kotlin.android.app.data.entity.mine.CollectionMovie
import com.kotlin.android.app.data.entity.mine.CollectionPerson
import com.kotlin.android.community.R
import com.kotlin.android.community.post.component.item.adapter.CommunityCenterAudioBinder
import com.kotlin.android.community.post.component.item.adapter.CommunityCenterContentBinder
import com.kotlin.android.community.post.component.item.adapter.CommunityCenterFilmCommentBinder
import com.kotlin.android.community.post.component.item.adapter.CommunityCenterVideoBinder
import com.kotlin.android.community.post.component.item.bean.CommunityPostItem
import com.kotlin.android.community.repository.PersonContentRepository
import com.kotlin.android.community.ui.person.*
import com.kotlin.android.community.ui.person.bean.CollectionCinemaViewBean
import com.kotlin.android.community.ui.person.bean.CollectionMovieViewBean
import com.kotlin.android.community.ui.person.bean.CollectionPersonViewBean
import com.kotlin.android.community.ui.person.binder.CollectionCinemaBinder
import com.kotlin.android.community.ui.person.binder.CollectionMovieBinder
import com.kotlin.android.community.ui.person.binder.CollectionPersonBinder
import com.kotlin.android.ktx.ext.time.TimeExt
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.tablet.adapter.MineMyCreateBinder
import java.text.DecimalFormat
import java.util.*

/**
 * @author WangWei
 * @date 2020/9/23
 */
class PersonCenterViewModel : CommViewModel<MultiTypeBinder<*>>() {
    private val repo by lazy { PersonContentRepository() }

    private val mSelectionUIModel = BaseUIModel<List<MultiTypeBinder<*>>>()
    val uiState = mSelectionUIModel.uiState

    val mContentUIModel = BinderUIModel<ContentList, List<MultiTypeBinder<*>>>()
    val mCollectionUIModel = BinderUIModel<CollectionContentList, List<MultiTypeBinder<*>>>()
    val mCinemaUIModel = BinderUIModel<CollectionCinema, List<MultiTypeBinder<*>>>()
    val mPersonUIModel = BinderUIModel<CollectionPerson, List<MultiTypeBinder<*>>>()
    val mMovieUIModel = BinderUIModel<CollectionMovie, List<MultiTypeBinder<*>>>()
    val mFilmListUIModel = BinderUIModel<MyCreateFilmList, List<MultiTypeBinder<*>>>()

    fun laodFilmList(isRefresh: Boolean, userId: Long) {
        call(
            uiModel = mFilmListUIModel,
            isRefresh = isRefresh,
            isShowLoading = false,
            converter = {
                val list = arrayListOf<MultiTypeBinder<*>>()
                it?.myCreates?.forEach {
                    list.add(MineMyCreateBinder(it))
                }
                list
            },
            isEmpty = {
                it.myCreates?.size == 0
            },
            hasMore = {
                it?.hasNext == true
            },
            pageStamp = {
                it?.nextStamp
            },
        ) {
            repo.loadFilmList(userId, mFilmListUIModel.pageStamp, mFilmListUIModel.pageSize)
        }
    }

    //收藏影院
    fun loadCollectionCinema(isRefresh: Boolean) {
        call(
            uiModel = mCinemaUIModel,
            isRefresh = isRefresh,
            isShowLoading = false,
            converter = {
                val list = arrayListOf<MultiTypeBinder<*>>()
                it?.items?.forEach {
                    list.add(converterCollectionCinemaBinder(it))
                }
                list
            },
            isEmpty = {
                it.items?.size == 0
            },
            pageStamp = {
                it?.nextStamp
            },
            hasMore = {
                it?.hasNext
            }
        ) {
            repo.loadCollectionCinema(mCinemaUIModel.pageStamp, mCinemaUIModel.pageSize)
        }
    }

    fun loadCollectionPerson(isRefresh: Boolean) {
        call(
            uiModel = mPersonUIModel,
            isRefresh = isRefresh,
            isShowLoading = false,
            converter = {
                val list = arrayListOf<MultiTypeBinder<*>>()
                it?.items?.forEach {
                    list.add(converterCollectionPersonBinder(it))
                }
                list
            },
            isEmpty = {
                it.items?.size == 0
            },
            hasMore = {
                it?.hasNext
            },
            pageStamp = {
                it?.nextStamp
            }
        ) {
            repo.loadCollectionPerson(mPersonUIModel.pageStamp, mPersonUIModel.pageSize)
        }
    }

    fun loadCollectionMovie(isRefresh: Boolean) {
        call(
            uiModel = mMovieUIModel,
            isRefresh = isRefresh,
            isShowLoading = false,
            converter = {
                val list = arrayListOf<MultiTypeBinder<*>>()
                it?.items?.forEach {
                    list.add(converterCollectionMovieBinder(it))
                }
                list
            },
            isEmpty = {
                it.items?.size == 0
            },
            hasMore = {
                it?.hasNext
            },
            pageStamp = {
                it?.nextStamp
            }
        ) {
            repo.loadCollectionMovie(mMovieUIModel.pageStamp, mMovieUIModel.pageSize)
        }
    }

    //个人主页内容
    fun loadContent(isRefresh: Boolean, type: Long, userId: Long, contentType: Long = 1L) {
        call(
            uiModel = mContentUIModel,
            isRefresh = isRefresh,
            isShowLoading = false,
            converter = {
                val list = arrayListOf<MultiTypeBinder<*>>()
                it.items?.forEach {
                    when (type) {
                        USER_CENTER_TYPE_FILM_COMMENT ->
                            list.add(
                                CommunityCenterFilmCommentBinder(
                                    CommunityPostItem.converter(
                                        it,
                                        binderType = contentType
                                    )
                                )
                            )
                        USER_CENTER_TYPE_FILM_VIDEO ->
                            list.add(
                                CommunityCenterVideoBinder(
                                    CommunityPostItem.converter(
                                        it,
                                        binderType = contentType
                                    )
                                )
                            )
                        USER_CENTER_TYPE_AUDIO ->
                            list.add(
                                CommunityCenterAudioBinder(
                                    CommunityPostItem.converter(
                                        it,
                                        binderType = contentType
                                    )
                                )
                            )
                        else -> list.add(
                            CommunityCenterContentBinder(
                                CommunityPostItem.converter(
                                    it,
                                    binderType = contentType
                                )!!
                            )
                        )
                    }
                }
                list
            },
            isEmpty = {
                it.items?.size == 0
            },
            hasMore = {
                it.hasNext
            },
            pageStamp = {
                it?.nextStamp
            }

        ) {
            //个人主页内容
            repo.loadContent(
                type,
                userId,
                mContentUIModel.pageStamp,
                mContentUIModel.pageSize
            )
        }
    }

    //收藏内容
    fun loadCollectionContent(
        isRefresh: Boolean,
        type: Long,
        userId: Long,
        contentType: Long = 2L
    ) {
        call(
            uiModel = mCollectionUIModel,
            isRefresh = isRefresh,
            isShowLoading = false,
            converter = {
                val list = arrayListOf<MultiTypeBinder<*>>()
                it.items?.forEach {
                    when (type) {
                        USER_CENTER_TYPE_FILM_COMMENT ->
                            list.add(
                                CommunityCenterFilmCommentBinder(
                                    CommunityPostItem.converter(
                                        it.obj,
                                        binderType = contentType
                                    )
                                )
                            )
                        USER_CENTER_TYPE_FILM_VIDEO ->
                            list.add(
                                CommunityCenterVideoBinder(
                                    CommunityPostItem.converter(
                                        it.obj,
                                        binderType = contentType
                                    )
                                )
                            )
                        USER_CENTER_TYPE_AUDIO ->
                            list.add(
                                CommunityCenterAudioBinder(
                                    CommunityPostItem.converter(
                                        it.obj,
                                        binderType = contentType
                                    )
                                )
                            )
                        else -> list.add(
                            CommunityCenterContentBinder(
                                CommunityPostItem.converter(
                                    it.obj,
                                    binderType = contentType
                                )!!
                            )
                        )
                    }
                }
                list
            },
            isEmpty = {
                it.items?.size == 0
            },
            hasMore = {
                it.hasNext
            },
            pageStamp = {
                it?.nextStamp
            }

        ) {
            repo.loadCollectContent(
                type,
                userId,
                mCollectionUIModel.pageStamp,
                mCollectionUIModel.pageSize
            )
        }
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
                nameEn = bean.obj?.nameEn.orEmpty(),
                imageUrl = bean.obj?.imgUrl.orEmpty(),
                profession = bean.obj?.profession.orEmpty(),
                likeRate = getLikeRating(bean.obj?.rating.orEmpty()),
                personInfo = bean.obj?.birthDate.orEmpty(),
                movies = getMovies(bean.obj?.mainMovies)
            )
        )
    }

    private fun getMovies(movieList: ArrayList<CollectionPerson.MovieItem>?): String {
        var movies = ""
        movieList?.forEach {
            movies = movies.plus("《${if (it.name?.isEmpty() == true)
                it.nameEn else it.name}》")

        }
        return movies
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
//            e.printStackTrace()
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
                movieName = if (TextUtils.isEmpty(bean.obj?.name).not())
                    bean.obj?.name.orEmpty() else bean.obj?.nameEn.orEmpty(),
                moviePic = bean.obj?.imgUrl.orEmpty(),
                genreTypes = bean.obj?.genreTypes.orEmpty(),
                actors = getActors(bean.obj?.mainActors),
                directors = getDirectors(bean.obj?.mainDirectors),
                year = bean.obj?.year,
                releaseContent = if(bean.obj?.earliestReleaseDate.orEmpty().isNotEmpty())"${bean.obj?.earliestReleaseDate.orEmpty()}(${bean.obj?.releaseArea.orEmpty()})" else "" /*TimeExt.date2String(
                    Date(
                        bean.collectTime?.stamp
                            ?: 0L
                    ),
                    getString(
                        R.string.collection_movie_date_pattern,
                        bean.obj?.releaseArea.orEmpty()
                    )
                ).orEmpty()*/,
                score = if (TextUtils.isEmpty(bean.obj?.rating)) "" else bean.obj?.rating.orEmpty(),
                nameEn = bean.obj?.nameEn.orEmpty(),
                canPlay = bean.obj?.play == 1L,//播放
                isShowing = bean.obj?.btnShow == 3L//显示想看
            )
        )
    }

    private fun getActors(movieList: ArrayList<CollectionMovie.Person>?): String {
        var actors = ""
        var head = ""
        if (movieList.isNullOrEmpty()) {
            head = ""
        } else {
            head = getString(R.string.actor)
        }

        movieList?.apply {
            this.forEachIndexed { index: Int, it: CollectionMovie.Person ->
                actors = if (index == 0)
                    actors.plus(
                        if (it.personNameCn?.isEmpty() == true)
                            it.personNameEn else it.personNameCn
                    )
                else actors.plus("/ ")
                    .plus(
                        if (it.personNameCn?.isEmpty() == true)
                            it.personNameEn else it.personNameCn
                    )
            }
            actors = head.plus(actors)
        }
        return actors
    }


    private fun getDirectors(mainDirectors: ArrayList<CollectionMovie.Person>?): String {
       var head = ""
        if (mainDirectors.isNullOrEmpty()) {
            head = ""
        } else {
            head = getString(R.string.director)
        }
        var directors = ""
        mainDirectors?.apply {
            this.forEachIndexed { index: Int, it: CollectionMovie.Person ->
                directors = if (index == 0)
                    directors.plus(
                        if (it.personNameCn?.isEmpty() == true)
                            it.personNameEn else it.personNameCn
                    )
                else directors.plus("/ ")
                    .plus(
                        if (it.personNameCn?.isEmpty() == true)
                            it.personNameEn else it.personNameCn
                    )
            }
            directors = head.plus(directors)
        }

        return directors
    }

}