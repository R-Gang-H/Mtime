package com.kotlin.android.app.api.api

import com.kotlin.android.api.ApiResponse
import com.kotlin.android.app.data.entity.mine.CreatorCenterBean
import com.kotlin.android.app.data.entity.mine.DataCenterBean
import com.kotlin.android.app.data.entity.mine.DataCenterDetailBean
import com.kotlin.android.app.data.entity.mine.HelpInfoList
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 *
 * @ProjectName:    b2c
 * @Package:        com.kotlin.android.app.api.api
 * @ClassName:      ApiCreator
 * @Description:    创作者Api
 * @Author:         haoruigang
 * @CreateDate:     2022/4/1 14:16
 */
interface ApiCreator {

    companion object {
        // 查看创作者中心首页数据
        const val CREATOR = "/community/creator.api"

        // 获取帮助中心-等级相关问题
        const val CREATOR_HELP_LEVE = "/community/creator_help_leve.api"

        // 创作者数据中心-整体概览
        const val CREATOR_STATISTICS = "/community/creator_statistics.api"

        // 创作者数据中心-单篇分析
        const val CREATOR_STATISTICS_DETAILS = "/community/creator_statistics_details.api"

        // 创作者统计内容分页查询
        const val QUERY_STAT_CONTENT = "/community/creator/query_stat_content.api"

    }

    /**
     *  创作者中心 - 创作者中心首页数据
     *  GET(/creator.api)
     */
    @GET(CREATOR)
    suspend fun getCreator(): ApiResponse<CreatorCenterBean>

    /**
     * 创作者中心 - 获取帮助中心-等级相关问题
     * GET (/creator_help_leve.api)
     */
    @GET(CREATOR_HELP_LEVE)
    suspend fun getCreatorHelpLeve(): ApiResponse<HelpInfoList>

    /**
     * 创作者数据中心-整体概览
     * GET (/creator_statistics.api)
     * statisticsType 	Number【必填】 统计类型:0全部(默认),1内容,2视频,3音频
     * timeType 	Number【必填】 统计时间:1近七天(默认),2近半年,3历史累计
     */
    @GET(CREATOR_STATISTICS)
    suspend fun getCreatorStatistics(
        @Query("statisticsType") statisticsType: Long,
        @Query("timeType") timeType: Long,
    ): ApiResponse<DataCenterBean.EarthBean>

    /**
     * 创作者中心-数据中心 - 创作者数据中心-单篇分析
     * GET (/creator_statistics_details.api)
     * type 	Number【必填】 资源类型:1:日志/2:帖子/3:影评/4:文章/5:视频/6:音频
     * contentId 	Number【必填】 内容id
     * timeType 	Number【必填】 统计时间:1近七天(默认),2近半年,3历史累计
     */
    @GET(CREATOR_STATISTICS_DETAILS)
    suspend fun getCreatorStatisticsDetails(
        @Query("type") type: Long,
        @Query("contentId") contentId: Long,
        @Query("timeType") timeType: Long,
    ): ApiResponse<DataCenterDetailBean>

    /**
     * 社区创作者内容api - 创作者统计内容分页查询
     * POST (/creator/query_stat_content.api)
     * type 	Number  内容类型 必填 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"), ARTICLE(4, "文章"); VIDEO(5, "视频"), AUDIO(6, "音频"),
     * nextStamp 	String  下一页标识
     * pageSize 	Number  分页size
     */
    @POST(QUERY_STAT_CONTENT)
    suspend fun queryStatContent(
        @Body body: RequestBody,
    ): ApiResponse<DataCenterBean.SingleAnalysisBean>

}