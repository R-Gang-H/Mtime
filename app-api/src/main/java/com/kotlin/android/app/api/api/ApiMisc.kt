package com.kotlin.android.app.api.api

import com.kotlin.android.api.ApiResponse
import com.kotlin.android.app.data.entity.SeatBanner
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * misc服务接口规范
 *
 * Created on 2020/5/13.
 *
 * @author o.s
 */
interface ApiMisc {

    /**
     * misc服务接口的path集
     */
    object Path {
        // batch
        const val SEAT_ICONS = "/acm/get_seaticons_list.api" // 座位图url
        const val SERVICE_CALL = "/prompt/service_call.api" // 客服电话url
        const val URL_CONFIG = "/prompt/url_conf.api" // url config
        const val PROMPT_CONFIG = "/prompt/prompt_conf.api" // 确认订单展示文案url
        const val SKINS = "/acm/skins.api" // 换肤
        const val APP_FLASH_AD = "/commend/openapp_flashad.api" // 闪屏广告
        // city
        const val CITY = "/homepage/city.api"
        const val CITY_POINT = "/homepage/city_point.api"
        // banner
        const val MOVIES_BANNER = "/commend/movies_banner.api" // 首页-正在热映banner
        const val COMMON_BANNER = "/commend/common_banner.api" // 通用推荐位接口
        const val MALL_BANNER = "/commend/malltab_mallbanner.api" // 商城首页_banner
        const val SEAT_BANNER = "/commend/selectseat_topad1.api" // 选座页_顶部广告位1
        const val THEATERS_BOX_AD = "/commend/intheaters_boxad.api" // 首页_正在热映-弹窗广告
        const val CARDS_PRESENT_TEXT = "/commend/get_cards_present_text.api" // 卡赠送文案
        // tips
        const val THEATERS_TIPS = "/commend/intheaters_message.api" // APP首页全局通知推荐位接口
        const val PROMPT_TIPS = "/prompt/tips.api" // 获取全局提示信息
        const val SALE_CARD_TIPS = "/commend/get_salecard_tip.api" // 获取影城有可销售卡时的Tip
        //
        const val WE_CHAT_TEMPLATE = "/share/template/get.api" // 卡分享模板api
        const val HOT_NOTE = "/hotnote/info.api" // 获取红点信息
        const val HOT_NOTE_CLICK = "/hotnote/click.api" // 点击红点
        const val APP_VERSION_INFO = "/appversion/info.api" // 获取版本信息
        // 消息中心
        const val NEW_MESSAGE = "/message/new_message.api" // 消息中心 - 新消息
        const val MESSAGE_LIST = "/message/list.api" // 消息中心 - 消息列表
        const val MESSAGE_READ = "/message/read.api" // 消息中心-消息列表中阅读消息

    }

    /**
     * 城市列表
     * GET ("/homepage/city.api")
     */
    @GET(Path.CITY)
    suspend fun getCity(
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 根据经纬度定位城市
     * GET ("/homepage/city_point.api")
     *
     * lon  double  必填  经度
     * lat  double  必填  纬度 double
     * type int     可选	经纬度类型1:百度  2:腾讯(默认为百度)
     */
    @GET(Path.CITY_POINT)
    suspend fun getCityPoint(
        @Query("lon") lon: Double,
        @Query("lat") lat: Double,
        @Query("type") type: Int = 1,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 获取首页banner
     * GET ("/commend/movies_banner.api")
     *
     * cityId   String	必填	城市Id
     * cinemaId String	可选	影院Id
     */
    @GET(Path.MOVIES_BANNER)
    suspend fun getMoviesBanner(
        @Query("cityId") cityId: String,
//        @Query("cinemaId") cinemaId: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 万达6.0APP换肤
     * GET ("/acm/skins.api")
     *
     * pageIndex    string  可选  分页页码
     * pageSize     string  可选  页大小
     */
    @GET(Path.SKINS)
    suspend fun getSkins(
        @Query("pageIndex") pageIndex: Int,
        @Query("pageSize") pageSize: Int,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * APP启动-闪屏广告
     * GET ("/commend/openapp_flashad.api")
     */
    @GET(Path.APP_FLASH_AD)
    suspend fun getAppFlashed(
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 首页_正在热映-弹窗广告
     * GET ("/commend/intheaters_boxad.api")
     *
     * cityId   String	必填	城市Id
     * cinemaId String	可选	影院Id
     */
    @GET(Path.THEATERS_BOX_AD)
    suspend fun getTheatersBoxAd(
        @Query("cityId") cityId: String,
        @Query("cinemaId") cinemaId: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 卡赠送文案
     * GET ("/commend/get_cards_present_text.api")
     */
    @GET(Path.CARDS_PRESENT_TEXT)
    suspend fun getCardsPresentText(
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * APP首页全局通知推荐位接口
     * GET ("/commend/intheaters_message.api")
     */
    @GET(Path.THEATERS_TIPS)
    suspend fun getTheatersTips(
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 通用推荐位接口
     * GET ("/commend/common_banner.api")
     *
     * cityId       String	可选  城市Id
     * pageSymbol   String  必选  页面标志位（具体值由产品提供）
     * areaSymbol   String	必选  区域标志位（具体值由产品提供）
     * cinemaId	    String	可选  影院id
     * filmId	    String	可选  影片id
     */
    @GET(Path.COMMON_BANNER)
    suspend fun getCommonBanner(
        @Query("pageSymbol") pageSymbol: String,
        @Query("areaSymbol") areaSymbol: String,
        @Query("cityId") cityId: String,
        @Query("cinemaId") cinemaId: String,
        @Query("filmId") filmId: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 通过微信分享时，需要配置分享的标题/内容/图标等，此接口用于提供微信分享时所需要的有效模板。 模板分类 非必填 template_type
     * GET ("/share/template/get.api")
     *
     * tId  Integer	必填	模板类型id
     */
    @GET(Path.WE_CHAT_TEMPLATE)
    suspend fun getWeChatTemplate(
        @Query("tId") tId: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 获取影城有可销售卡时的Tip
     * GET ("/commend/get_salecard_tip.api")
     *
     * cityId	String	必填	城市ID
     * cinemaId String	必填	影院id
     */
    @GET(Path.SALE_CARD_TIPS)
    suspend fun getSaleCardTips(
        @Query("cityId") cityId: String,
        @Query("cinemaId") cinemaId: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 商城首页banner
     * GET ("/commend/malltab_mallbanner.api")
     *
     * cityId	String	必填	城市ID
     * cinemaId String	必填	影院id
     */
    @GET(Path.MALL_BANNER)
    suspend fun getMallBanner(
        @Query("cityId") cityId: String,
        @Query("cinemaId") cinemaId: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 获取全局提示信息
     * GET ("/prompt/tips.api")
     *
     * cinemaId String	必填	影院id
     */
    @GET(Path.PROMPT_TIPS)
    suspend fun getPromptTips(
        @Query("cinemaId") cinemaId: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 获取客服电话
     * GET ("/prompt/service_call.api")
     */
    @GET(Path.SERVICE_CALL)
    suspend fun getServiceCall(
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 获取URL相关配置
     * GET ("/prompt/url_conf.api")
     */
    @GET(Path.URL_CONFIG)
    suspend fun getUrlConfig(
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 获取座位ICON配置列表
     * GET ("/acm/get_seaticons_list.api")
     */
    @GET(Path.SEAT_ICONS)
    suspend fun getSeatIcons(
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 选座页_顶部广告位1
     * GET ("/commend/selectseat_topad1.api")
     *
     * movieId	String	可选	影片ID
     */
    @GET(Path.SEAT_BANNER)
    suspend fun getSeatBanner(
        @Query("movieId") movieId: String? = null,
        @Query("json") json: Boolean = true
    ): ApiResponse<SeatBanner>

    /**
     * 获取红点信息
     * GET ("/hotnote/info.api")
     *
     * deviceToken  String  必选  极光token
     */
    @GET(Path.HOT_NOTE)
    suspend fun getHotNote(
        @Query("deviceToken") deviceToken: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 点击红点
     * GET ("/hotnote/click.api")
     *
     * deviceToken  String  必选  极光token
     * type         Integer	必选  红点类型。1：活动红点 2：我的红点 3：券红点 4:消息中心红点 5:智能客服消息
     */
    @GET(Path.HOT_NOTE_CLICK)
    suspend fun getHotNoteClick(
        @Query("deviceToken") deviceToken: String,
        @Query("type") type: Int,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 获取版本信息
     * GET ("/appversion/info.api")
     */
    @GET(Path.APP_VERSION_INFO)
    suspend fun getAppVersionInfo(
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 确认订单页面文案提示
     * GET ("/prompt/prompt_conf.api")
     */
    @GET(Path.PROMPT_CONFIG)
    suspend fun getPromptConfig(
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 消息中心 - 新消息
     * GET ("/message/new_message.api")
     */
    @GET(Path.NEW_MESSAGE)
    suspend fun getNewMessage(
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 消息中心 - 消息列表
     * GET ("/message/list.api")
     *
     * deviceToken  String  必选  极光token
     */
    @GET(Path.MESSAGE_LIST)
    suspend fun getMessageList(
        @Query("deviceToken") deviceToken: String,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

    /**
     * 消息中心 - 消息列表中阅读消息
     * GET ("/message/read.api")
     *
     * deviceToken  String  必选  极光token
     * type	        String	必选  类型：1:系统消息；2:活动推广消息；3:用户消息
     * pageSize	    String	必选  页面数量
     * pageIndex	String	必选  第几页
     */
    @GET(Path.MESSAGE_READ)
    suspend fun getMessageRead(
        @Query("deviceToken") deviceToken: String,
        @Query("type") type: String,
        @Query("pageSize") pageSize: Int,
        @Query("pageIndex") pageIndex: Int,
        @Query("json") json: Boolean = true
    ): ApiResponse<*>

}