package com.kotlin.android.search.newcomponent.repo

import android.graphics.Rect
import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.annotation.*
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.CommHasMoreList
import com.kotlin.android.app.data.entity.search.*
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.orFalse
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.search.newcomponent.adapter.binder.SearchHintBinder
import com.kotlin.android.search.newcomponent.ui.result.adapter.*
import com.kotlin.android.search.newcomponent.ui.result.bean.*
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * 搜索：
 * [SearchType] 搜索类型：0影片、1影院、2影人、3全部、4商品、5文章、默认全部
 *
 * Created on 2020/10/9.
 *
 * @author o.s
 */
class SearchRepository : BaseRepository() {

    companion object {
        const val UNION_SEARCH_RESULT_ALL_PAGE_INDEX = 1L
        const val UNION_SEARCH_RESULT_ALL_PAGE_SIZE = 3L
        const val UNION_SEARCH_RESULT_ALL_DEFAULT_SHOW_COUNT = 2   // 搜索结果_全部_默认显示2条
        const val UNION_SEARCH_RESULT_ALL_CINEMA_SHOW_COUNT = 3    // 搜索结果_全部_影院显示3条
    }

    /**
     * 联合搜索 二期
     * 直接返回数据，而不是binderList
     * 目前用于发布组件-关联电影、影人搜索
     */
    suspend fun unionSearch(keyword: String, searchType: Long): ApiResult<UnionSearch> {
        return request(
                api = {
                    apiMTime.unionSearch(
                            keyword = keyword,
                            searchType = searchType
                    )
                }
        )
    }

    /**
     * 联合搜索_按全部搜 二期（搜索结果页专用）
     */
    suspend fun unionSearchByAll(
        keyword: String, locationId: Long, longitude: Double?,
        latitude: Double?
    ): ApiResult<CommHasMoreList<MultiTypeBinder<*>>> {
        return request(
            converter = { searchResult ->
                val binderList = mutableListOf<MultiTypeBinder<*>>()
                // 影片
                searchResult.movies?.let { list ->
                    if(list.isNotEmpty()) {
                        binderList.add(SearchResultAllTypeTitleBinder(SEARCH_MOVIE))
                        list.subList(
                                0,
                                list.size.coerceAtMost(UNION_SEARCH_RESULT_ALL_DEFAULT_SHOW_COUNT)
                        )
                                .map {
                                    binderList.add(getMovieItemBinder(keyword, it))
                                }
                        if (searchResult.moviesCount.orZero()
                                        .toInt() > UNION_SEARCH_RESULT_ALL_DEFAULT_SHOW_COUNT
                        ) {
                            binderList.add(
                                    SearchResultAllTypeCountBinder(
                                            SEARCH_MOVIE,
                                            searchResult.moviesCount.orZero()
                                    )
                            )
                        }
                    }
                }
                // 影院
                if (locationId > 0) {
                    // 本地影院，当locationId>0时，有意义
                    searchResult.locationCinemas?.let { list ->
                        if(list.isNotEmpty()) {
                            binderList.add(SearchResultAllTypeTitleBinder(SEARCH_CINEMA))
                            list.subList(
                                    0,
                                    list.size.coerceAtMost(UNION_SEARCH_RESULT_ALL_CINEMA_SHOW_COUNT)
                            )
                                    .map {
                                        binderList.add(getCinemaItemBinder(keyword, it))
                                    }
                            if (searchResult.locationCinemasCount.orZero()
                                            .toInt() > UNION_SEARCH_RESULT_ALL_CINEMA_SHOW_COUNT
                            ) {
                                binderList.add(
                                        SearchResultAllTypeCountBinder(
                                                SEARCH_CINEMA,
                                                searchResult.locationCinemasCount.orZero()
                                        )
                                )
                            }
                        }
                    }
                } else {
                    // 影院列表,当locationID=0时，搜索范围为全国，否则为其他地区的影院
                    searchResult.cinemas?.let { list ->
                        if(list.isNotEmpty()) {
                            binderList.add(SearchResultAllTypeTitleBinder(SEARCH_CINEMA))
                            list.subList(
                                    0,
                                    list.size.coerceAtMost(UNION_SEARCH_RESULT_ALL_CINEMA_SHOW_COUNT)
                            )
                                    .map {
                                        binderList.add(getCinemaItemBinder(keyword, it))
                                    }
                            if (searchResult.cinemaCount.orZero()
                                            .toInt() > UNION_SEARCH_RESULT_ALL_CINEMA_SHOW_COUNT
                            ) {
                                binderList.add(
                                        SearchResultAllTypeCountBinder(
                                                SEARCH_CINEMA,
                                                searchResult.cinemaCount.orZero()
                                        )
                                )
                            }
                        }
                    }
                }
                // 影人
                searchResult.persons.let { list ->
                    if(list.isNotEmpty()) {
                        binderList.add(SearchResultAllTypeTitleBinder(SEARCH_PERSON))
                        list.subList(
                                0,
                                list.size.coerceAtMost(UNION_SEARCH_RESULT_ALL_DEFAULT_SHOW_COUNT)
                        )
                                .map {
                                    binderList.add(getPersonItemBinder(keyword, it))
                                }
                        if (searchResult.personsCount.orZero()
                                        .toInt() > UNION_SEARCH_RESULT_ALL_DEFAULT_SHOW_COUNT
                        ) {
                            binderList.add(
                                    SearchResultAllTypeCountBinder(
                                            SEARCH_PERSON,
                                            searchResult.personsCount.orZero()
                                    )
                            )
                        }
                    }
                }
                // 文章
                searchResult.articles?.let { list ->
                    if(list.isNotEmpty()) {
                        binderList.add(SearchResultAllTypeTitleBinder(SEARCH_ARTICLE))
                        list.subList(
                                0,
                                list.size.coerceAtMost(UNION_SEARCH_RESULT_ALL_DEFAULT_SHOW_COUNT)
                        )
                                .map {
                                    binderList.add(getArticleItemBinder(keyword, it))
                                }
                        if (searchResult.articlesCount.orZero()
                                        .toInt() > UNION_SEARCH_RESULT_ALL_DEFAULT_SHOW_COUNT
                        ) {
                            binderList.add(
                                    SearchResultAllTypeCountBinder(
                                            SEARCH_ARTICLE,
                                            searchResult.articlesCount.orZero()
                                    )
                            )
                        }
                    }
                }
                // 用户
                searchResult.userInfos?.let { list ->
                    if(list.isNotEmpty()) {
                        binderList.add(SearchResultAllTypeTitleBinder(SEARCH_USER))
                        list.subList(
                                0,
                                list.size.coerceAtMost(UNION_SEARCH_RESULT_ALL_DEFAULT_SHOW_COUNT)
                        )
                                .map {
                                    binderList.add(getUserItemBinder(keyword, it))
                                }
                        if (searchResult.userInfosCount.orZero()
                                        .toInt() > UNION_SEARCH_RESULT_ALL_DEFAULT_SHOW_COUNT
                        ) {
                            binderList.add(
                                    SearchResultAllTypeCountBinder(
                                            SEARCH_USER,
                                            searchResult.userInfosCount.orZero()
                                    )
                            )
                        }
                    }
                }
                // 帖子
                searchResult.posts?.let { list ->
                    if(list.isNotEmpty()) {
                        binderList.add(SearchResultAllTypeTitleBinder(SEARCH_POST))
                        list.subList(
                                0,
                                list.size.coerceAtMost(UNION_SEARCH_RESULT_ALL_DEFAULT_SHOW_COUNT)
                        )
                                .map {
                                    binderList.add(getPostItemBinder(keyword, it))
                                }
                        if (searchResult.postsCount.orZero()
                                        .toInt() > UNION_SEARCH_RESULT_ALL_DEFAULT_SHOW_COUNT
                        ) {
                            binderList.add(
                                    SearchResultAllTypeCountBinder(
                                            SEARCH_POST,
                                            searchResult.postsCount.orZero()
                                    )
                            )
                        }
                    }
                }
                // 影评
                searchResult.filmComments?.let { list ->
                    if(list.isNotEmpty()) {
                        binderList.add(SearchResultAllTypeTitleBinder(SEARCH_FILM_COMMENT))
                        list.subList(
                                0,
                                list.size.coerceAtMost(UNION_SEARCH_RESULT_ALL_DEFAULT_SHOW_COUNT)
                        )
                                .map {
                                    binderList.add(getFilmCommentItemBinder(keyword, it))
                                }
                        if (searchResult.filmCommentsCount.orZero() > UNION_SEARCH_RESULT_ALL_DEFAULT_SHOW_COUNT) {
                            binderList.add(
                                    SearchResultAllTypeCountBinder(
                                            SEARCH_FILM_COMMENT,
                                            searchResult.filmCommentsCount.orZero()
                                    )
                            )
                        }
                    }
                }
                // 家族
                searchResult.families?.let { list ->
                    if(list.isNotEmpty()) {
                        binderList.add(SearchResultAllTypeTitleBinder(SEARCH_FAMILY))
                        list.subList(
                                0,
                                list.size.coerceAtMost(UNION_SEARCH_RESULT_ALL_DEFAULT_SHOW_COUNT)
                        )
                                .map {
                                    binderList.add(getFamilyItemBinder(keyword, it))
                                }
                        if (searchResult.familiesCount.orZero()
                                        .toInt() > UNION_SEARCH_RESULT_ALL_DEFAULT_SHOW_COUNT
                        ) {
                            binderList.add(
                                    SearchResultAllTypeCountBinder(
                                            SEARCH_FAMILY,
                                            searchResult.familiesCount.orZero()
                                    )
                            )
                        }
                    }
                }
                // 日志
                searchResult.logs?.let { list ->
                    if(list.isNotEmpty()) {
                        binderList.add(SearchResultAllTypeTitleBinder(SEARCH_LOG))
                        list.subList(
                                0,
                                list.size.coerceAtMost(UNION_SEARCH_RESULT_ALL_DEFAULT_SHOW_COUNT)
                        )
                                .map {
                                    binderList.add(getLogItemBinder(keyword, it))
                                }
                        if (searchResult.logsCount.orZero()
                                        .toInt() > UNION_SEARCH_RESULT_ALL_DEFAULT_SHOW_COUNT
                        ) {
                            binderList.add(
                                    SearchResultAllTypeCountBinder(
                                            SEARCH_LOG,
                                            searchResult.logsCount.orZero()
                                    )
                            )
                        }
                    }
                }
                // 片单
                searchResult.filmList?.let { list ->
                    if(list.isNotEmpty()) {
                        // title
                        binderList.add(SearchResultAllTypeTitleBinder(SEARCH_FILM_LIST))
                        // 列表
                        binderList.addAll(
                            FilmListViewBean.build(
                                searchType = SEARCH_ALL,
                                keyword = keyword,
                                beans = list.subList(
                                        0,
                                        list.size.coerceAtMost(UNION_SEARCH_RESULT_ALL_DEFAULT_SHOW_COUNT)
                                )
                            )
                        )
                        // 查看全部
                        if (searchResult.filmListCount.orZero().toInt() > UNION_SEARCH_RESULT_ALL_DEFAULT_SHOW_COUNT) {
                            binderList.add(
                                SearchResultAllTypeCountBinder(
                                    SEARCH_FILM_LIST,
                                    searchResult.filmListCount.orZero()
                                )
                            )
                        }
                    }
                }
                // 视频
                searchResult.videoList?.let { list ->
                    if(list.isNotEmpty()) {
                        // title
                        binderList.add(SearchResultAllTypeTitleBinder(SEARCH_VIDEO))
                        // 列表
                        binderList.addAll(
                                VideoViewBean.build(
                                        searchType = SEARCH_ALL,
                                        keyword = keyword,
                                        beans = list.subList(
                                                0,
                                                list.size.coerceAtMost(UNION_SEARCH_RESULT_ALL_DEFAULT_SHOW_COUNT)
                                        )
                                )
                        )
                        // 查看全部
                        if (searchResult.videoCount.orZero().toInt() > UNION_SEARCH_RESULT_ALL_DEFAULT_SHOW_COUNT) {
                            binderList.add(
                                    SearchResultAllTypeCountBinder(
                                            SEARCH_VIDEO,
                                            searchResult.videoCount.orZero()
                                    )
                            )
                        }
                    }
                }
                // 播客
                searchResult.audioList?.let { list ->
                    if(list.isNotEmpty()) {
                        // title
                        binderList.add(SearchResultAllTypeTitleBinder(SEARCH_AUDIO))
                        // 列表
                        binderList.addAll(
                                AudioViewBean.build(
                                        searchType = SEARCH_ALL,
                                        keyword = keyword,
                                        beans = list.subList(
                                                0,
                                                list.size.coerceAtMost(UNION_SEARCH_RESULT_ALL_DEFAULT_SHOW_COUNT)
                                        )
                                )
                        )
                        // 查看全部
                        if (searchResult.audioCount.orZero().toInt() > UNION_SEARCH_RESULT_ALL_DEFAULT_SHOW_COUNT) {
                            binderList.add(
                                    SearchResultAllTypeCountBinder(
                                            SEARCH_AUDIO,
                                            searchResult.audioCount.orZero()
                                    )
                            )
                        }
                    }
                }

                CommHasMoreList(hasMore = false, list = binderList)
            },
            api = {
                apiMTime.unionSearch(
                    keyword,
                    UNION_SEARCH_RESULT_ALL_PAGE_INDEX,
                    UNION_SEARCH_RESULT_ALL_PAGE_SIZE,
                    SEARCH_ALL,
                    locationId,
                    longitude,
                    latitude
                )
            })
    }

    /**
     * 联合搜索_按具体分类搜 二期
     */
    suspend fun unionSearchByType(
        keyword: String, pageIndex: Long, pageSize: Long,
        @SearchType searchType: Long, locationId: Long,
        longitude: Double?, latitude: Double?, sortType: Long? = null
    ): ApiResult<CommHasMoreList<MultiTypeBinder<*>>> {
        return request(
            converter = { searchResult ->
                var binderList = mutableListOf<MultiTypeBinder<*>>()
                var totalCount = 0L
                when (searchType) {
                    SEARCH_MOVIE -> {
                        searchResult.movies?.let { list ->
                            list.map {
                                binderList.add(getMovieItemBinder(keyword, it))
                            }
                            totalCount = searchResult.moviesCount.orZero()
                        }
                    }
                    SEARCH_CINEMA -> {
                        if (locationId > 0) {
                            // 本地影院，当locationId>0时，有意义
                            searchResult.locationCinemas?.let { list ->
                                list.map {
                                    binderList.add(getCinemaItemBinder(keyword, it))
                                }
                                totalCount = searchResult.locationCinemasCount.orZero()
                            }
                        } else {
                            // 影院列表,当locationID=0时，搜索范围为全国，否则为其他地区的影院
                            searchResult.cinemas?.let { list ->
                                list.map {
                                    binderList.add(getCinemaItemBinder(keyword, it))
                                }
                                totalCount = searchResult.cinemaCount.orZero()
                            }
                        }
                    }
                    SEARCH_PERSON -> {
                        searchResult.persons.let { list ->
                            list.map {
                                binderList.add(getPersonItemBinder(keyword, it))
                            }
                            totalCount = searchResult.personsCount.orZero()
                        }
                    }
                    SEARCH_USER -> {
                        searchResult.userInfos?.let { list ->
                            list.map {
                                binderList.add(getUserItemBinder(keyword, it))
                            }
                            totalCount = searchResult.userInfosCount.orZero()
                        }
                    }
                    SEARCH_FAMILY -> {
                        searchResult.families?.let { list ->
                            list.map {
                                binderList.add(getFamilyItemBinder(keyword, it))
                            }
                            totalCount = searchResult.familiesCount.orZero()
                        }
                    }
                    SEARCH_FILM_COMMENT -> {
                        searchResult.filmComments?.let { list ->
                            list.map {
                                binderList.add(getFilmCommentItemBinder(keyword, it))
                            }
                            totalCount = searchResult.filmCommentsCount.orZero()
                        }
                    }
                    SEARCH_ARTICLE -> {
                        searchResult.articles?.let { list ->
                            list.map {
                                binderList.add(getArticleItemBinder(keyword, it))
                            }
                            totalCount = searchResult.articlesCount.orZero()
                        }
                    }
                    SEARCH_POST -> {
                        searchResult.posts?.let { list ->
                            list.map {
                                binderList.add(getPostItemBinder(keyword, it))
                            }
                            totalCount = searchResult.postsCount.orZero()
                        }
                    }
                    SEARCH_LOG -> {
                        searchResult.logs?.let { list ->
                            list.map {
                                binderList.add(getLogItemBinder(keyword, it))
                            }
                            totalCount = searchResult.logsCount.orZero()
                        }
                    }
                    SEARCH_FILM_LIST -> {
                        searchResult.filmList?.let { list ->
                            binderList = FilmListViewBean.build(
                                    searchType = searchType,
                                    keyword = keyword,
                                    beans = list
                            )
                            totalCount = searchResult.filmListCount.orZero()
                        }
                    }
                    SEARCH_VIDEO -> {
                        searchResult.videoList?.let { list ->
                            binderList = VideoViewBean.build(
                                    searchType = searchType,
                                    keyword = keyword,
                                    beans = list
                            )
                            totalCount = searchResult.videoCount.orZero()
                        }
                    }
                    SEARCH_AUDIO -> {
                        searchResult.audioList?.let { list ->
                            binderList = AudioViewBean.build(searchType, keyword, list)
                            totalCount = searchResult.audioCount.orZero()
                        }
                    }
                    else -> {
                    }
                }
                val hasMore = totalCount > pageIndex * pageSize
                CommHasMoreList(hasMore = hasMore, list = binderList)
            },
            api = {
                apiMTime.unionSearch(
                    keyword,
                    pageIndex,
                    pageSize,
                    searchType,
                    locationId,
                    longitude,
                    latitude,
                    sortType
                )
            })
    }


    /**
     * 联合搜索_按家族id搜索相关帖子
     */
    suspend fun unionSearchByGroupId(
        keyword: String, groupId:Long,pageIndex: Long, pageSize: Long,
        @SearchType searchType: Long, locationId: Long,
        longitude: Double?, latitude: Double?, sortType: Long? = null
    ): ApiResult<CommHasMoreList<MultiTypeBinder<*>>> {
        return request(
            converter = { searchResult ->
                val binderList = mutableListOf<MultiTypeBinder<*>>()
                var totalCount = 0L
                when (searchType) {
                    SEARCH_POST -> {
                        searchResult.posts?.let { list ->
                            list.map {
                                binderList.add(getPostItemBinder(keyword, it))
                            }
                            totalCount = searchResult.postsCount.orZero()
                        }
                    }
                    else -> {
                    }
                }
                val hasMore = totalCount > pageIndex * pageSize
                CommHasMoreList(hasMore = hasMore, list = binderList)
            },
            api = {
                apiMTime.unionSearch(
                    keyword,
                    pageIndex,
                    pageSize,
                    searchType,
                    locationId,
                    longitude,
                    latitude,
                    sortType,
                    groupId
                )
            })
    }

    /**
     * 搜索结果_影片ItemBinder
     */
    private fun getMovieItemBinder(keyword: String, movie: Movie): SearchResultMovieItemBinder {
        return SearchResultMovieItemBinder(
            keyword = keyword,
            bean = MovieItem.objectToViewBean(movie),
            isResult = true
        )
    }

    /**
     * 搜索结果_影院ItemBinder
     */
    private fun getCinemaItemBinder(keyword: String, cinema: Cinema): SearchResultCinemaItemBinder {
        return SearchResultCinemaItemBinder(
            keyword,
            CinemaItem(
                cinemaId = cinema.cinemaId.orZero(),
                name = cinema.name.orEmpty(),
                address = cinema.address.orEmpty(),
                featureInfos = cinema.featureInfos.orEmpty(),
                distance = cinema.distance.orZero()
            )
        )
    }

    /**
     * 搜索结果_影人ItemBinder
     */
    private fun getPersonItemBinder(keyword: String, person: Person): SearchResultPersonItemBinder {
        return SearchResultPersonItemBinder(
            keyword = keyword,
            bean = PersonItem.objectToViewBean(person),
            isResult = true
        )
    }

    /**
     * 转换影人代表作
     */
    private fun convertPersonMovies(personMovies: List<PersonMovie>?): List<String> {
        return mutableListOf<String>().apply {
            personMovies?.let { list ->
                list.map {
                    it.title?.let { title ->
                        if (title.isNotEmpty()) {
                            this.add(title)
                        }
                    }
                }
            }
        }
    }

    /**
     * 搜索结果_文章ItemBinder
     */
    private fun getArticleItemBinder(
        keyword: String,
        article: Article
    ): SearchResultArticleItemBinder {
        return SearchResultArticleItemBinder(
            keyword,
            ArticleItem(
                articleId = article.articleId.orZero(),
                articleTitle = article.articleTitle.orEmpty(),
                image = article.image.orEmpty(),
                authorNickName = article.author?.nickName.orEmpty(),
                publishTime = article.publishTime.orZero()
            )
        )
    }

    /**
     * 搜索结果_用户ItemBinder
     */
    private fun getUserItemBinder(keyword: String, user: User): SearchResultUserItemBinder {
        return SearchResultUserItemBinder(
            keyword,
            UserItem(
                userId = user.userId.orZero(),
                nickName = user.nickName.orEmpty(),
                headImage = user.headImage.orEmpty(),
                sign = user.sign.orEmpty(),
                isAuth = user.isAuth.orZero(),
                authType = user.authType.orZero(),
                fansCount = user.fansCount.orZero(),
                isFocus = user.isFocus.orZero(),
                showUserFocus = true
            )
        )
    }

    /**
     * 搜索结果_帖子ItemBinder
     */
    private fun getPostItemBinder(keyword: String, post: Post): SearchResultPostItemBinder {
        return SearchResultPostItemBinder(
            keyword,
            PostItem(
                objId = post.postId.orZero(),
                objTitle = post.postTitle.orEmpty(),
                objType = CommConstant.PRAISE_OBJ_TYPE_POST,
                commentCount = post.commentCount.orZero(),
                likeUp = post.likeUp.orZero(),
                isLikeUp = post.isLikeUp.orFalse()
            )
        )
    }

    /**
     * 搜索结果_影评ItemBinder
     */
    private fun getFilmCommentItemBinder(keyword: String, filmComment: FilmComment):
            SearchResultFilmCommentItemBinder {
        return SearchResultFilmCommentItemBinder(
            keyword,
            FilmCommentItem(
                filmCommentId = filmComment.filmCommentId.orZero(),
                filmCommentTitle = filmComment.filmCommentTitle.orEmpty(),
                likeUp = filmComment.likeUp.orZero(),
                likeDown = filmComment.likeDown.orZero(),
                isLikeUp = filmComment.isLikeUp.orFalse(),
                isLikeDown = filmComment.isLikeDown.orFalse()
            )
        )
    }

    /**
     * 搜索结果_家族ItemBinder
     */
    private fun getFamilyItemBinder(keyword: String, family: Family): SearchResultFamilyItemBinder {
        return SearchResultFamilyItemBinder(
            keyword,
            FamilyItem(
                familyId = family.familyId.orZero(),
                name = family.name.orEmpty(),
                imageUrl = family.imageUrl.orEmpty(),
                summary = family.summary.orEmpty(),
                memberNum = family.memberNum.orZero(),
                isJoin = convertIsJoin(family.isJoin.orZero()),
                showIsJoin = true
            )
        )
    }

    /**
     * 当前用户是否加入此家族（页面判断用，与社区家族保持一致）
     * @param isJoin  Long  Family.isJoin 搜索接口返回值
     */
    private fun convertIsJoin(isJoin: Long): Long {
        /**
         * Family用户类型：
         * 已登录 申请者-1、族长1、管理员2、普通成员3、黑名单4、未加入5
         * 未登录 0
         */
        return when (isJoin) {
            Family.IS_JOIN_APPLY -> FamilyItem.JOIN_STATUS_JOINING
            Family.IS_JOIN_CREATOR, Family.IS_JOIN_MANAGER, Family.IS_JOIN_MEMBER -> FamilyItem.JOIN_STATUS_JOINED
            Family.IS_JOIN_BLACK_LIST -> FamilyItem.JOIN_STATUS_BLACK_LIST
            else -> FamilyItem.JOIN_STATUS_NOT
        }
    }

    /**
     * 搜索结果_日志ItemBinder
     */
    private fun getLogItemBinder(keyword: String, log: Log): SearchResultPostItemBinder {
        return SearchResultPostItemBinder(
            keyword,
            PostItem(
                objId = log.logId.orZero(),
                objTitle = log.logTitle.orEmpty(),
                objType = CommConstant.PRAISE_OBJ_TYPE_JOURNAL,
                commentCount = log.commentCount.orZero(),
                likeUp = log.likeUp.orZero(),
                isLikeUp = log.isLikeUp.orFalse()
            )
        )
    }

    /**
     * 搜索热门整合接口(/search/hot.api)
     */
    suspend fun hotSearch(): ApiResult<HotSearch> {
        return request {
            apiMTime.searchHot()
        }
    }

    /**
     * 热门电影，影人点击上报
     * @param pType 影片 1 、  影人 2、  文章 3  、用户6、影评7、帖子8、日志9
     * @param sType 影片 0 、 电视剧 1 、 文章-1  、影人 -1、 用户-1、影评-1、帖子-1、日志-1
     * @param keyword 影片id 、影人id、文章标题、用户id、影评id、帖子id、日志id
     */
    suspend fun searchPopularClick(
        pType: Long,
        sType: Long,
        keyword: String
    ): ApiResult<String> {
        return request {
            apiMTime.searchPopularClick(pType, sType, keyword)
        }
    }

    /**
     * 搜索智能提示
     */
    suspend fun searchSuggest2(
        keyword: String,
        locationId: Long,
        longitude: Double,
        latitude: Double
    ): ApiResult<List<MultiTypeBinder<*>>> {
        return request(
            converter = {
                val list = arrayListOf<MultiTypeBinder<*>>()
                it.suggestions?.forEachIndexed { index, item ->
                    val itemBinder: MultiTypeBinder<*>? = if (index == 0) {
                        convertFullMatchTypeBinder(item, keyword)
                    } else {
                        convertHintBinder(item, keyword)
                    }
                    itemBinder?.apply {
                        list.add(this)
                    }
                }
                list
            },
            api = {
                apiMTime.searchSuggest2(keyword, locationId, longitude, latitude)
            })
    }

    /**
     * 搜索智能提示第一条最佳匹配类型
     */
    private fun convertFullMatchTypeBinder(
        item: SearchSuggestItem,
        keyword: String
    ): MultiTypeBinder<*>? {
        return when (item.type) {
            SEARCH_MOVIE -> {
                SearchResultMovieItemBinder(
                    keyword,
                    MovieItem(
                        movieId = item.movieId.orZero(),
                        name = item.titleCn.orEmpty(),
                        nameEn = item.titleEn.orEmpty(),
                        year = item.year.orEmpty(),
                        img = item.cover.orEmpty(),
                        rating = item.rating.orZero(),
                        movieType = item.movieType.orEmpty(),
                        locationName = item.locationName.orEmpty(),
                        releaseStatus = item.releaseStatus.orZero(),
                        canPlay = item.canPlay.orZero(),
                        saleType = item.saleType.orZero()
                    )
                )
            }
            SEARCH_CINEMA -> {
                SearchResultCinemaItemBinder(
                    keyword,
                    CinemaItem(
                        cinemaId = item.cinemaId.orZero(),
                        name = item.name.orEmpty(),
                        address = item.address.orEmpty(),
                        featureInfos = item.featureInfos.orEmpty(),
                        distance = item.distance.orZero()
                    )
                )
            }
            SEARCH_PERSON -> {
                SearchResultPersonItemBinder(
                    keyword,
                    PersonItem(
                        personId = item.personId.orZero(),
                        name = item.titleCn.orEmpty(),
                        nameEn = item.titleEn.orEmpty(),
                        img = item.cover.orEmpty(),
                        profession = item.profession.orEmpty(),
                        loveDeep = item.loveDeep.orZero(),
                        personMovies = convertPersonMovies(item.personMovies)
                    )
                )
            }
            SEARCH_USER -> {
                SearchResultUserItemBinder(
                    keyword,
                    UserItem(
                        userId = item.userId.orZero(),
                        nickName = item.nickName.orEmpty(),
                        headImage = item.headUrl.orEmpty(),
                        sign = item.sign.orEmpty(),
                        isAuth = item.isAuth.orZero(),
                        authType = item.authType.orZero(),
                        fansCount = item.fansCount.orZero(),
                        isFocus = item.isFocus.orZero(),
                        showUserFocus = false
                    )
                )
            }
            SEARCH_FAMILY -> {
                SearchResultFamilyItemBinder(
                    keyword,
                    FamilyItem(
                        familyId = item.familyId.orZero(),
                        name = item.name.orEmpty(),
                        imageUrl = item.imageUrl.orEmpty(),
                        summary = item.summary.orEmpty(),
                        memberNum = item.memberNum.orZero(),
                        isJoin = 0L,
                        showIsJoin = false
                    )
                )
            }
            SEARCH_FILM_LIST -> {
                SearchResultFilmListItemBinder(
                    keyword = keyword,
                    viewBean = FilmListViewBean(
                        id = item.id.orZero(),
                        title = item.title.orEmpty(),
                        cover = item.cover.orEmpty(),
                        authorImg = item.authorImg.orEmpty(),
                        authorId = item.authorId.orZero(),
                        authorName = item.authorName.orEmpty(),
                        films = item.films.orEmpty(),
                        collectNum = item.collectNum.orZero(),
                        authTag = item.authTag.orZero(),
                    ),
                    firstMarginTop = 0,
                    rootPadding = Rect(15.dp, 7.dp, 15.dp, 8.dp)
                )
            }
            else -> {
                convertHintBinder(item, keyword)
            }
        }
    }

    /**
     * 转换成普通的智能提示binder
     */
    private fun convertHintBinder(item: SearchSuggestItem, keyword: String): MultiTypeBinder<*>? {
        val itemName: String? = when (item.type) {
            SEARCH_MOVIE,
            SEARCH_PERSON -> {
                item.titleCn ?: item.titleEn
            }
            SEARCH_USER -> {
                item.nickName
            }
            SEARCH_CINEMA,
            SEARCH_FAMILY -> {
                item.name
            }
            SEARCH_ARTICLE -> {
                item.articleTitle
            }
            SEARCH_FILM_COMMENT -> {
                item.filmCommentTitle
            }
            SEARCH_POST -> {
                item.postTitle
            }
            SEARCH_LOG -> {
                item.logTitle
            }
            SEARCH_FILM_LIST,
            SEARCH_VIDEO,
            SEARCH_AUDIO -> {
                item.title
            }
            else -> {
                item.name ?: item.title
            }
        }

        return if (itemName.isNullOrEmpty()) {
            null
        } else {
            SearchHintBinder(itemName, keyword)
        }
    }
}