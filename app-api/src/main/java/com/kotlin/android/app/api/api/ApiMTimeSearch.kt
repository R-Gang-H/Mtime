package com.kotlin.android.app.api.api

import com.kotlin.android.api.ApiResponse
import com.kotlin.android.app.data.annotation.SEARCH_ALL
import com.kotlin.android.app.data.annotation.SearchType
import com.kotlin.android.app.data.entity.search.HotSearch
import com.kotlin.android.app.data.entity.search.SearchSuggest
import com.kotlin.android.app.data.entity.search.UnionSearch
import retrofit2.http.*

/**
 * 时光搜索
 * http://wiki.inc-mtime.com/display/DsTeam/API
 *
 * MtimeSearchController
 * http://apidocs-bd.wd-dev.com/mtime-search-front-api/index.html#api-MtimeSearchController-unionSearch
 *
 * Created on 2020/10/9.
 *
 * @author o.s
 */
interface ApiMTimeSearch {

    companion object {

        /**
         * 搜索api
         */
        const val SEARCH_UNION = "/mtime-search/search/unionSearch" // MtimeSearchController - unionSearch ｜ 联合搜索/search/unionSearch
        const val UNION_SEARCH_2 = "/mtime-search/search/unionSearch2" // 联合搜索 二期
        const val SEARCH_HOT = "/community/search/hot.api" //搜索热门整合接口
        const val SEARCH_POPULAR_CLICK = "/mtime-search/search/poplarClick" //热门电影 影人点击事件
        const val SEARCH_SUGGEST2 = "/mtime-search/search/suggest2" //智能提示词
    }

    /**
     * 联合搜索/search/unionSearch
     * MtimeSearchController - unionSearch
     * GET ("/search/search/unionSearch")
     *
     * keyword      关键字    string	必填 星际
     * pageIndex    页数      int	可选  1
     * pageSize	             Number
     * searchType 	搜索类型   int	可选  0影片、1影院、2影人、3全部 、4 商品，5 文章 默认全部
     * locationId   城市ID    int	可选  默认0
     * year	        年代      String	可选  影片的时候传
     * genreTypes   电影类型   string 可选  影片的时候传
     * area	        国别      string	可选  影片的时候传
     */
    @GET(SEARCH_UNION)
    suspend fun getSearchUnion(
            @Query("keyword") keyword: String,
            @Query("searchType") @SearchType searchType: Long = SEARCH_ALL,
            @Query("locationId") locationId: Long = 0,
            @Query("year") year: String? = null,
            @Query("area") area: String? = null,
            @Query("genreTypes") genreTypes: String? = null,
            @Query("pageIndex") pageIndex: Long = 1,
            @Query("pageSize") pageSize: Long? = null
    ): ApiResponse<UnionSearch>

    /**
     * 联合搜索 二期
     * POST ("/mtime-search/search/unionSearch2")
     *
     * keyword      关键字	    string	必填	    搜索的关键字
     * pageIndex    页数	        int	    可选     1
     * pageSize	    每页显示条数	int	    可选	    默认20条
     * searchType 	搜索类型	    int	    可选	    0影片、1影院、2影人、3全部 、4 商品、5 文章、6 用户、7 影评、8 帖子、9 日志、10 家族  默认全部
     * locationId   城市ID	    int	    可选	    默认0
     * year	        年代	        string	可选
     * genreTypes   电影类型	    string	可选
     * area	        国别	        string	可选
     * longitude	经度	        double	可选	    传入计算当前坐标位置到影院的距离
     * latitude	    纬度	        double	可选	    传入计算当前坐标位置到影院的距离
     */
    @POST(UNION_SEARCH_2)
    suspend fun unionSearch(
        @Query("keyword") keyword: String,
        @Query("pageIndex") pageIndex: Long = 1,
        @Query("pageSize") pageSize: Long? = null,
        @Query("searchType") @SearchType searchType: Long = SEARCH_ALL,
        @Query("locationId") locationId: Long = 0,
        @Query("longitude") longitude: Double? = null,
        @Query("latitude") latitude: Double? = null,
        @Query("sortType") sortType: Long? = null,
        @Query("familyId") familyId: Long? = 0L
    ): ApiResponse<UnionSearch>

    /**
     * 搜索热门整合接口(/search/hot.api)
     */
    @GET(SEARCH_HOT)
    suspend fun searchHot(): ApiResponse<HotSearch>

    /**
     * 热门电影，影人点击上报
     * @param pType 影片 1 、  影人 2、  文章 3  、用户6、影评7、帖子8、日志9
     * @param sType 影片 0 、 电视剧 1 、 文章-1  、影人 -1、 用户-1、影评-1、帖子-1、日志-1
     * @param keyword 影片id 、影人id、文章标题、用户id、影评id、帖子id、日志id
     */
    @POST(SEARCH_POPULAR_CLICK)
    suspend fun searchPopularClick(
            @Query("pType") pType: Long,
            @Query("sType") sType: Long,
            @Query("keyword") keyword: String
    ): ApiResponse<String>

    /**
     * 搜索智能提示
     * http://wiki.inc-mtime.com/pages/viewpage.action?pageId=286360275
     */
    @POST(SEARCH_SUGGEST2)
    suspend fun searchSuggest2(
            @Query("keyword") keyword: String,
            @Query("locationId") locationId: Long,
            @Query("longitude") longitude: Double,
            @Query("latitude") latitude: Double
    ): ApiResponse<SearchSuggest>
}