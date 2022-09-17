package com.kotlin.android.app.api.api

import com.kotlin.android.api.ApiResponse
import com.kotlin.android.app.data.entity.film.seat.AutoSeatInfo
import com.kotlin.android.app.data.entity.film.seat.SeatInfo
import retrofit2.http.*

/**
 * cinema服务接口规范
 *
 * Created on 2020/5/13.
 *
 * @author o.s
 */
interface ApiCinema {

    /**
     * cinema服务接口的path集
     */
    object Path {
        // movie
        const val MOVIE_DETAIL = "/movie/movie_detail.api" // 查询电影详情接口
        const val MOVIE_ACTORS = "/movie/movie_actors.api" // 根据movieId获取影片 演/职员 列表
        const val MOVIE_IMAGE = "/movie/image.api" // 根据movieId获取影片图片列表
        const val MOVIE_VIDEO = "/movie/video.api" // 电影详情页-预告片接口
        const val MOVIE_COMMENTS = "/movie/review/getComments.api" // 查询最新与最热评论接口
        const val MOVIE_LAST_COMMENTS = "/movie/review/last_comments.api" // 查询 最新或最热 评论接口 （优先取最热，无的话返回最新评论列表(根据评论是否有点赞数来判断是最新还是最热)）
        const val MOVIE_BRIEF = "/movie/movie_brief.api" // 根据movie ids 返回一组简要信息
        const val MOVIE_SCORE_COMMENT = "/movie/review/score_comment.api" // 电影详情页-评分评论接口
        const val MOVIE_COMMENT_REPLIES = "/movie/review/find_comment_replys.api" // 根据评论id分页查询回复信息
        const val MOVIE_COMMENT_LIKE = "/movie/review/comment_like.api" // 评论点赞
        const val MOVIE_COMMENT_REPLY = "/movie/review/comment_reply.api" // 评论回复
        const val MOVIE_SHARE_PIC = "/movie/share_movie_pic.api" // 获取用户电影详情分享二维码图片接口
        const val MOVIE_SHARE_DETAIL = "/movie/share_movie_detail.api" // 影片详情分享接口
        const val MOVIE_HOT_SHOW_V6_4 = "/movie/hot_show_v6_4.api" // 正在热映(V6.4)
        const val MOVIE_COMING_V6_4 = "/movie/coming_v6_4.api" // 即将上映(V6.4)
        // cinema
        const val CINEMA_BY_LOCATION = "/cinema/by_locationid.api" // 根据城市id 及经纬度 查询附近的影院列表，并按照经纬度的距离排序（由近到远）
        const val CINEMA_BY_LOCATION_V6_4 = "/cinema/by_locationid_v6_4.api" // 影院列表(V6.4)
        const val CINEMA_BY_ID = "/cinema/by_cinemaid.api" // 根据影院ID获取影院详情
        const val CINEMAS_BY_IDS = "/cinema/by_cinemaids.api" // 根据影院IDs获取影院详情列表
        const val CINEMA_FILM_BY_ID = "/cinema/by_filmid_v6_4.api" // 影片可售影院场次列表(V6.4)
        const val CINEMA_CAN_TOP = "/cinema/can_top.api" // 查询用户是否置顶影院接口
        const val CINEMA_TOP = "/cinema/cinema_top.api" // 影院置顶接口
        const val CINEMA_CUSTOMIZED_COMMENTS_INFO = "/movie/review/customized_comments_info.api" // 新版定制评价信息接口
        const val CINEMA_CUSTOMIZED_COMMENTS = "/movie/review/customized_comments.api" // 定制评价接口
        // seat
        const val SEAT_INFO = "/ticket/schedule/realtime_seat/online_seats_by_showtime_id.api" // "/realtime_seat/online_seats_by_showtime_id.api" // 获取实时座位图--支持分区售卖(/realtime_seat/online_seats_by_showtime_id.api)
        const val SEAT_AUTO = "/ticket/schedule/realtime_seat/auto_optimal_seats.api" // 自动计算最优座位(/ realtime_seat / auto_optimal_seats.api)
    }

    /**
     * 查询电影详情接口
     * GET ("/movie/movie_detail.api")
     *
     * movieId  String  必选  电影id
     */
    @GET(Path.MOVIE_DETAIL)
    suspend fun getMovieDetail(
        @Query("movieId") movieId: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 根据movieId获取影片 演/职员 列表
     * GET ("/movie/movie_actors.api")
     *
     * movieId  String  必选  电影id
     */
    @GET(Path.MOVIE_ACTORS)
    suspend fun getMovieActors(
        @Query("movieId") movieId: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 根据movieId获取影片图片列表
     * GET ("/movie/image.api")
     *
     * movieId      String  必选  电影id
     * imageType	int	    可选  图片类型：为空 返回所有 1、海报 2、工作照 3、新闻照 4、封套 5、桌面 6、剧照
     * pageIndex	int	    可选  pageIndex：为空 返回所有
     * pageSize	    int	    可选  pageSize：默认10
     */
    @GET(Path.MOVIE_IMAGE)
    suspend fun getMovieImage(
        @Query("movieId") movieId: String,
        @Query("pageIndex") pageIndex: Int?,
        @Query("pageSize") pageSize: Int?,
        @Query("imageType") imageType: Int? = null,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 电影详情页-预告片接口
     * GET ("/movie/video.api")
     *
     * movieId      Long    必选  电影ID
     * videoType    String  可选  预告片类型（视频类型:为空 返回所有，1.精彩片段 2.花絮 3.影人访谈 4.电影首映 5.mv 6.预告）
     * pageIndex    Float   可选  当前页数（pageIndex:为空 返回所有）
     * pageSize     String  可选  每页的条数（pageSize 默认 10）
     */
    @GET(Path.MOVIE_VIDEO)
    suspend fun getMovieVideo(
        @Query("movieId") movieId: String,
        @Query("pageIndex") pageIndex: Int?,
        @Query("pageSize") pageSize: Int?,
        @Query("videoType") videoType: Int? = null,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 查询最新与最热评论接口
     * GET ("/movie/review/getComments.api")
     *
     * movieId      String  必选  电影id
     * pageIndex	String 	必选  第几页，从1开始
     * pageSize	    String  必选  每页显示条数
     */
    @GET(Path.MOVIE_COMMENTS)
    suspend fun getMovieComments(
        @Query("movieId") movieId: String,
        @Query("pageIndex") pageIndex: Int,
        @Query("pageSize") pageSize: Int,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 查询 最新或最热 评论接口 （优先取最热，无的话返回最新评论列表(根据评论是否有点赞数来判断是最新还是最热)）
     * GET ("/movie/review/last_comments.api")
     *
     * movieId      String  必选  电影id
     * pageIndex	int 	可选  第几页，从1开始
     * pageSize	    int	    可选  每页显示条数
     */
    @GET(Path.MOVIE_LAST_COMMENTS)
    suspend fun getMovieLastComments(
        @Query("movieId") movieId: String,
//        @Query("pageIndex") pageIndex: Int? = null,
//        @Query("pageSize") pageSize: Int? = null,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 根据movie ids 返回一组简要信息
     * GET ("/movie/movie_brief.api")
     *
     * movieIds     String  必选  电影ids,以,分割
     */
    @GET(Path.MOVIE_BRIEF)
    suspend fun getMovieBrief(
        @Query("movieIds") movieIds: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 电影详情页-评分评论接口
     * POST ("/movie/review/score_comment.api")
     *
     * movieId      Long    必选  电影ID
     * movieName    String  必选  电影名称
     * score        Float   必选  电影评分
     * comment      String  必选  评论
     */
    @POST(Path.MOVIE_SCORE_COMMENT)
    @FormUrlEncoded
    suspend fun getMovieScoreComment(
        @Field("movieId") movieId: String,
        @Field("movieName") movieName: String,
        @Field("score") score: Float,
        @Field("comment") comment: String,
        @Field("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 根据评论id分页查询回复信息
     * GET ("/movie/review/find_comment_replys.api")
     *
     * commentIds   String  必选  评论ID
     * movieId	    String	可选  电影ID
     * pageIndex    String  必选  第几页，从1开始
     * pageSize     String  必选  每页显示条数
     */
    @GET(Path.MOVIE_COMMENT_REPLIES)
    suspend fun getMovieCommentReplies(
        @Query("commentIds") commentIds: String,
        @Query("movieId") movieId: String?,
        @Query("pageIndex") pageIndex: Int,
        @Query("pageSize") pageSize: Int,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 评论点赞
     * POST ("/movie/review/comment_like.api")
     *
     * commentId    long    必选  评论ID
     * flag         int     必选  0：取消点赞 1：点赞
     */
    @POST(Path.MOVIE_COMMENT_LIKE)
    @FormUrlEncoded
    suspend fun getMovieCommentLike(
        @Field("commentId") commentId: String,
        @Field("flag") flag: String,
        @Field("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 评论回复
     * POST ("/movie/review/comment_reply.api")
     *
     * commentId    long    必选  评论ID
     * replyContent String  必选  回复
     */
    @POST(Path.MOVIE_COMMENT_REPLY)
    @FormUrlEncoded
    suspend fun getMovieCommentReply(
        @Field("commentId") commentId: String,
        @Field("replyContent") replyContent: String,
        @Field("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 获取用户电影详情分享二维码图片接口
     * GET ("/movie/share_movie_pic.api")
     *
     * movieId	String	可选	分享的影片Id
     */
    @GET(Path.MOVIE_SHARE_PIC)
    suspend fun getMovieSharePic(
        @Query("movieId") movieId: String?,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 影片详情分享接口
     * GET ("/movie/share_movie_detail.api")
     *
     * movieId	String	必选	分享的影片Id
     * cinemaId	String	可选	影院ID
     * cityId	String	必选	城市ID
     */
    @GET(Path.MOVIE_SHARE_DETAIL)
    suspend fun getMovieShareDetail(
        @Query("movieId") movieId: String,
        @Query("cityId") cityId: String,
        @Query("cinemaId") cinemaId: String?,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 正在热映(V6.4)
     * GET ("/movie/hot_show_v6_4.api")
     *
     * cityId   int     必选  城市id
     * day      Integer 必选  日期:0:全部, 1:今天, 2:明天, 3:后天
     */
    @GET(Path.MOVIE_HOT_SHOW_V6_4)
    suspend fun getMovieHotShow(
        @Query("cityId") cityId: String,
        @Query("day") day: Int,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 即将上映(V6.4)
     * GET ("/movie/coming_v6_4.api")
     *
     * cityId   int 必选  城市id
     */
    @GET(Path.MOVIE_COMING_V6_4)
    suspend fun getMovieComing(
        @Query("cityId") cityId: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 根据城市id 及经纬度 查询附近的影院列表，并按照经纬度的距离排序（由近到远）
     * GET ("/cinema/by_locationid.api")
     *
     * locationId	String	必选  城市id
     * lon          String	可选  经度
     * lat          String  可选  纬度
     * coordType    int	    可选  坐标类型:1百度,2腾讯(原来没传默认0就是百度)
     */
    @GET(Path.CINEMA_BY_LOCATION)
    suspend fun getCinemaByLocation(
        @Query("locationId") locationId: String,
        @Query("lon") lon: String?,
        @Query("lat") lat: String?,
        @Query("coordType") coordType: Int = 0,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 影院列表(V6.4)
     * GET ("/cinema/by_locationid_v6_4.api")
     *
     * locationId	String	必选  城市id
     * lon          String	可选  经度
     * lat          String  可选  纬度
     * coordType    int	    可选  坐标类型:1百度,2腾讯(原来没传默认0就是百度)
     */
    @GET(Path.CINEMA_BY_LOCATION_V6_4)
    suspend fun getCinemaByLocationV64(
        @Query("locationId") locationId: String,
        @Query("lon") lon: String?,
        @Query("lat") lat: String?,
        @Query("coordType") coordType: Int = 0,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 根据影院ID获取影院详情
     * GET ("/cinema/by_cinemaid.api")
     *
     * cinemaid String  必选  影院ID
     */
    @GET(Path.CINEMA_BY_ID)
    suspend fun getCinemaById(
        @Query("cinemaid") cinemaId: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 根据影院IDs获取影院详情列表
     * GET ("/cinema/by_cinemaids.api")
     *
     * cinemaids    String  必选  影院ids,以,分割
     */
    @GET(Path.CINEMAS_BY_IDS)
    suspend fun getCinemasByIds(
        @Query("cinemaids") cinemaIds: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 影片可售影院场次列表(V6.4)
     * GET ("/cinema/by_filmid_v6_4.api")
     *
     * locationId	String	必选  城市id
     * filmId	    String	必选  影片ID
     * lon          String	可选  经度
     * lat          String  可选  纬度
     * showType	    String	可选  场次类型（空或0为普通 8.众筹场  9.起跑线）
     * coordType    int	    可选  坐标类型:1百度,2腾讯(原来没传默认0就是百度)
     */
    @GET(Path.CINEMA_FILM_BY_ID)
    suspend fun getCinemaFilmById(
        @Query("filmId") filmId: String,
        @Query("locationId") locationId: String,
        @Query("showType") showType: String?,
        @Query("lon") lon: String?,
        @Query("lat") lat: String?,
        @Query("coordType") coordType: Int = 0,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 查询用户是否置顶影院接口
     * GET ("/cinema/can_top.api")
     *
     * cinemaId	    int	必选  影院ID
     * cityId	    int	必选  城市ID
     */
    @GET(Path.CINEMA_CAN_TOP)
    suspend fun getCinemaCanTop(
        @Query("cinemaId") cinemaId: String,
        @Query("cityId") cityId: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 影院置顶接口
     * GET ("/cinema/cinema_top.api")
     *
     * cinemaId	    int	必选  影院ID
     * cityId	    int	必选  城市ID
     * type	        int	必选  1.置顶  2.取消置顶
     */
    @GET(Path.CINEMA_TOP)
    suspend fun getCinemaTop(
        @Query("cinemaId") cinemaId: String,
        @Query("cityId") cityId: String,
        @Query("type") type: Int,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 新版定制评价信息接口
     * GET ("/movie/review/customized_comments_info.api")
     *
     * orderId	        String	可选  订单号（app6.7.2 及以上版本必传）
     */
    @GET(Path.CINEMA_CUSTOMIZED_COMMENTS_INFO)
    suspend fun getCinemaCustomizedCommentsInfo(
        @Query("orderId") orderId: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 定制评价接口
     * GET ("/movie/review/customized_comments.api")
     *
     * orderId	        String	必选  订单号
     * movieId	        Long	必选  电影ID
     * movieName	    String	可选  电影名称
     * score	        Float	必选  电影评分
     * comment	        String	可选  评论
     * cinemaId         String  必选  影院ID
     * cinemaCommitInfo String  可选  影院评价信息（评价指标code1-评价星级1-星级标签名称1,星级标签名称2|评价指标code2-评价星级2-星级标签名称3,星级标签名称4）
     * cinemaComment	String	可选  影院评论内容
     * appCommitInfo	String	可选  app评价信息（评价指标code1-评价星级1-星级标签名称1,星级标签名称2|评价指标code2-评价星级2-星级标签名称3,星级标签名称4）
     * appComment	    String	可选  app评论内容
     */
    @GET(Path.CINEMA_CUSTOMIZED_COMMENTS)
    suspend fun getCinemaCustomizedComments(
        @Query("orderId") orderId: String,
        @Query("movieId") movieId: String,
        @Query("movieName") movieName: String?,
        @Query("score") score: Float,
        @Query("comment") comment: String?,
        @Query("cinemaId") cinemaId: String,
        @Query("cinemaCommitInfo") cinemaCommitInfo: String?,
        @Query("cinemaComment") cinemaComment: String?,
        @Query("appCommitInfo") appCommitInfo: String?,
        @Query("appComment") appComment: String?,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 获取实时座位图--支持分区售卖(/realtime_seat/online_seats_by_showtime_id.api)
     * POST ("/realtime_seat/online_seats_by_showtime_id.api")
     *
     * dId	    String  场次id,必填
     * mobile	String  手机号，为了满足万达平台选座页需要先输入手机号的规则而增加的参数，非万达平台的场次可以不填
     */
    @POST(Path.SEAT_INFO)
    @FormUrlEncoded
    suspend fun postSeatInfo(
        @Field("dId") dId: String,
        @Field("mobile") mobile: String? = null,
    ): ApiResponse<SeatInfo>

    /**
     * 自动计算最优座位(/ realtime_seat / auto_optimal_seats.api)
     * POST ("/ticket/schedule/realtime_seat/auto_optimal_seats.api")
     *
     * showtimeId	Number      场次ID,必填
     * count	    Number      自动选座个数,必填
     */
    @POST(Path.SEAT_AUTO)
    @FormUrlEncoded
    suspend fun postAutoSeat(
        @Field("showtimeId") showtimeId: Long,
        @Field("count") count: Long,
    ): ApiResponse<AutoSeatInfo>


}