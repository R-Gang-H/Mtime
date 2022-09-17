package com.kotlin.android.app.api.api

import com.kotlin.android.api.ApiResponse
import com.kotlin.android.app.api.api.ApiFilmList.Path.UNION_SEARCH_2
import com.kotlin.android.app.data.annotation.SEARCH_ALL
import com.kotlin.android.app.data.annotation.SearchType
import com.kotlin.android.app.data.entity.filmlist.*
import com.kotlin.android.app.data.entity.monopoly.CommResult
import com.kotlin.android.app.data.entity.monopoly.CommonBizMsgBean
import com.kotlin.android.app.data.entity.search.ConditionResult
import com.kotlin.android.app.data.entity.search.MovieSearchResult
import com.kotlin.android.app.data.entity.search.UnionSearch
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * 创建者: sunhao
 * 创建时间: 2022/3/11
 * 描述:片单接口Api
 **/
interface ApiFilmList {
    object Path {
        const val GET_FILM_LIST = "/community/film_list/pageRcmdList.api"//推荐列表页面
        const val POST_FILM_LIST_SAVE = "/community/film_list/save.api"//创建片单
        const val POST_FILM_LIST_MODIFY = "/community/film_list/modify.api"//编辑片单
        const val GET_BASE_INFO = "/community/film_list/basicInfo.api"//片单详情上半部分
        const val GET_PAGE_MOVIES = "/community/film_list/pageMovies.api"//片单详情下半部分
        const val GET_FILM_LIST_SHARE = "/community/film_list/share.api"//分享片单
        const val GET_PAGE_CURR_ACTIVITIES =
            "/community/film_list_activity/pageCurrActivities.api"//投稿当前活动
        const val GET_PAGE_HISTORY_ACTIVITIES =
            "/community/film_list_activity/historyActivities.api"//投稿历史活动
        const val GET_PAGE_MY_CREATES = "/community/film_list/pageMyCreates.api"//我创建的片单-个人中心
        const val GET_PAGE_MY_MANUSCRIPTS =
            "/community/film_list/pageMyManuscriptsFilmList.api"//我创建的片单-投稿
        const val GET_PAGE_MY_FAVORITES = "/community/film_list/pageMyFavorites.api"//我收藏的片单
        const val POST_DELETE = "/community/film_list/delete.api"//片单详情删除片单
        const val POST_PAGE_MANUSCRIPTS = "/community/film_list_activity/manuscripts.api"//投稿
        const val POST_PAGE_ADD_MOVIES = "/community/film_list/addMovies.api"//添加电影
        const val POST_FILM_LIST_GET_BY_ID = "/community/film_list/getModifyById.api"//编辑-片单信息
        const val GET_MODIFY_MOVIES = "/community/film_list/getModifyMovies.api"//编辑-已选电影
        const val GET_FIND_MOVIE_CONDITION_LIST = "/library/index/find_movie/condition.api"
        const val GET_FILM_FILTER_RESULT = "/mtime-search/search/filmFilter"
        const val UNION_SEARCH_2 = "/mtime-search/search/filmList" // 片单搜索
    }

    /**
     * 创建片单
     */
    @POST(Path.POST_FILM_LIST_SAVE)
    suspend fun create(
        @Body body: RequestBody
    ): ApiResponse<FilmListCreateResult>

    /**
     * 编辑片单
     */
    @POST(Path.POST_FILM_LIST_MODIFY)
    suspend fun edit(
        @Body body: RequestBody
    ): ApiResponse<CommResult>

    /**
     * category	Number【选填】推荐分类（默认排行第一的分类）
    nextStamp String【选填】下一页标识，默认或第一页可为null
    pageSize	Number【选填】每页页面数，默认20
     */
    @GET(Path.GET_FILM_LIST)
    suspend fun list(
        @Query("category") category: Long? = null,
        @Query("nextStamp") nextStamp: String? = null,
        @Query("pageSize") pageSize: Long = 20L
    ): ApiResponse<FilmListEntity>

    /**
     * filmListId	Number【必填】片单id
     * source【选填】默认值为0； 0:缓存5分钟，1:缓存1秒-我创建的片单列表用，
     */
    @GET(Path.GET_BASE_INFO)
    suspend fun baseInfo(
        @Query("filmListId") filmListId: Long?,
        @Query("source") source: Long?
    ): ApiResponse<FilmListBasicInfoEntity>

    /**
     * filmListId	Number【必填】片单id
     * type	Number【选填】类型：0全部，1可播放，2未看过
     * nextStamp	String【选填】下一页标识，默认或第一页可为null
     * pageSize	Number【选填】每页页面数，默认20
     * source【选填】默认值为0； 0:缓存5分钟，1:缓存1秒-我创建的片单列表用，
     */
    @GET(Path.GET_PAGE_MOVIES)
    suspend fun pageMovies(
        @Query("filmListId") filmListId: Long?,
        @Query("type") type: Long?,
        @Query("nextStamp") nextStamp: String?,
        @Query("pageSize") pageSize: Long?,
        @Query("source") source: Long = 0L
    ): ApiResponse<FilmListPageMoviesEntity>

    /**
     * filmListId	Number【必填】片单id
     * pageSize	Number【选填】每页页面数，默认9
     */
    @GET(Path.GET_FILM_LIST_SHARE)
    suspend fun share(
        @Query("filmListId") filmListId: Long,
        @Query("pageSize") pageSize: Long = 9L,
    ): ApiResponse<FilmListShareEntity>

    /**
     * 当前主题
     */
    @GET(Path.GET_PAGE_CURR_ACTIVITIES)
    suspend fun currActivity(
        @Query("nextStamp") nextStamp: String?,
        @Query("pageSize") pageSize: Long?
    ): ApiResponse<CurrActivityInfo>

    /**
     * 历史主题
     */
    @GET(Path.GET_PAGE_HISTORY_ACTIVITIES)
    suspend fun historyActivity(
        @Query("nextStamp") nextStamp: String?,
        @Query("pageSize") pageSize: Long?
    ): ApiResponse<HistoryActivityInfo>

    /**
     * 我创建的片单-个人中心
     */
    @GET(Path.GET_PAGE_MY_CREATES)
    suspend fun myCreates(
        @Query("nextStamp") nextStamp: String?,
        @Query("pageSize") pageSize: Long?
    ): ApiResponse<MyCreateFilmList>

    /**
     * 我创建的片单-投稿
     */
    @GET(Path.GET_PAGE_MY_MANUSCRIPTS)
    suspend fun myManuscripts(
        @Query("nextStamp") nextStamp: String?,
        @Query("pageSize") pageSize: Long?,
        @Query("activityId") activityId: Long?,
    ): ApiResponse<MyCreateFilmList>

    /**
     * 我收藏的片单
     */
    @GET(Path.GET_PAGE_MY_FAVORITES)
    suspend fun myCollection(
        @Query("nextStamp") nextStamp: String?,
        @Query("pageSize") pageSize: Long?
    ): ApiResponse<MyCollectionFilmList>

    /**
     * 投稿
     * filmListId	Number【必填】片单id
     * activityId 投稿活动Id
     */
    @POST(Path.POST_PAGE_MANUSCRIPTS)
    suspend fun reqContribute(
        @Body body: RequestBody
    ): ApiResponse<CommResult>

    /**
     * 片单详情删除
     * filmListId	Number【必填】片单id
     */
    @POST(Path.POST_DELETE)
    suspend fun delete(@Body body: RequestBody): ApiResponse<FilmListCreateResult>

    /**
     * 添加电影
     */
    @POST(Path.POST_PAGE_ADD_MOVIES)
    suspend fun addMovies(
        @Body body: RequestBody
    ): ApiResponse<CommonBizMsgBean>

    /**
     * 编辑-片单信息
     * filmListId	Number【必填】片单id
     */
    @GET(Path.POST_FILM_LIST_GET_BY_ID)
    suspend fun reqFilmListInfo(
        @Query("filmListId") filmListId: Long
    ): ApiResponse<FilmListEditInfo>

    /**
     * 编辑-已选电影
     * filmListId	Number【必填】片单id
     */
    @GET(Path.GET_MODIFY_MOVIES)
    suspend fun getModifyMovies(
        @Query("filmListId") filmListId: Long
    ): ApiResponse<FilmListModifyMovieInfo>

    /**
     *
     * @param keyword    关键字	string	可选	搜索的关键字
     * @param page        页数	    int	    可选 0
     * @param pageSize    条数	    int	可选	默认20条
     * @param year       年代	int	    可选	默认-1:-1全部，范围筛选 2020:2020
     * @param genreTypes 电影类型	int	    可选	默认0全部，多条以逗号分隔
     * @param area     国别	int	    可选	默认0全部，多条以逗号分隔
     * @param sortType     排序类型	int	    可选	默认0， 时光评分0 音乐1  画面2  导演3  故事4  表演5  近期热度6  上映时间7
     * 找电影-筛选结果
     * http://wiki.inc-mtime.com/pages/viewpage.action?pageId=288478223
     */
    @GET(Path.GET_FIND_MOVIE_CONDITION_LIST)
    suspend fun getFindMovieCondition(@Query("allOptions") allOptions: Boolean = true): ApiResponse<ConditionResult>

    /**
     *
     * @param keyword    关键字	string	可选	搜索的关键字
     * @param page        页数	    int	    可选 0
     * @param pageSize    条数	    int	可选	默认20条
     * @param year       年代	int	    可选	默认-1:-1全部，范围筛选 2020:2020
     * @param genreTypes 电影类型	int	    可选	默认0全部，多条以逗号分隔
     * @param area     国别	int	    可选	默认0全部，多条以逗号分隔
     * @param sortType     排序类型	int	    可选	默认0， 时光评分0 音乐1  画面2  导演3  故事4  表演5  近期热度6  上映时间7
     * 找电影-筛选结果
     * http://wiki.inc-mtime.com/pages/viewpage.action?pageId=288478223
     */
    @GET(Path.GET_FILM_FILTER_RESULT)
    suspend fun loadFilmFilterResult(
        @Query("keyword") category: String? = null,
        @Query("nextStamp") nextStamp: String? = null,
        @Query("page") page: String = "",
        @Query("pageSize") pageSize: Long = 20L,
        @Query("year") year: String = "-1:-1",
        @Query("genreTypes") genreTypes: Long = 0L,
        @Query("area") area: Long = 0L,
        @Query("sortType") sortType: Long = 0L,//
    ): ApiResponse<MovieSearchResult>

    /**
     * 片单搜索
     * keyword      关键字	    string	必填	    搜索的关键字
     * page    页数	        int	    可选     1
     * pageSize	    每页显示条数	int	    可选	    默认20条
     * searchType 	搜索类型	    int	    可选	    0影片、1影院、2影人、3全部 、4 商品、5 文章、6 用户、7 影评、8 帖子、9 日志、10 家族  默认全部
     */
    @GET(UNION_SEARCH_2)
    suspend fun filmListSearch(
        @Query("keyword") keyword: String,
        @Query("page") pageIndex: Long = 1,
        @Query("pageSize") pageSize: Long? = null,
        @Query("sortType") sortType: Long? = null,
        @Query("filter") filter: String? = null,
    ): ApiResponse<FilmListSearchEntity>
}
