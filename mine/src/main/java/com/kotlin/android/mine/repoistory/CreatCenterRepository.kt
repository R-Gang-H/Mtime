package com.kotlin.android.mine.repoistory

import androidx.collection.arrayMapOf
import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.common.CommonResult
import com.kotlin.android.app.data.entity.community.content.ArticleUser
import com.kotlin.android.app.data.entity.mine.CreatorCenterBean
import com.kotlin.android.app.data.entity.mine.DataCenterBean
import com.kotlin.android.app.data.entity.mine.DataCenterDetailBean
import com.kotlin.android.app.data.entity.mine.HelpInfoList
import com.kotlin.android.mine.bean.AnalysisViewBean
import com.kotlin.android.retrofit.getRequestBody

/**
 *
 * @ProjectName:    b2c
 * @Package:        com.kotlin.android.mine.repoistory
 * @ClassName:      CreatCenterRepository
 * @Description:    创作者中心
 * @Author:         haoruigang
 * @CreateDate:     2022/4/1 16:19
 */
class CreatCenterRepository : BaseRepository() {

    /**
     *  创作者中心 - 创作者中心首页数据
     *  GET(/creator.api)
     */
    suspend fun getCreator(): ApiResult<CreatorCenterBean> {
        return request(api = { apiMTime.getCreator() })
    }

    /**
     * 获取帮助中心-等级相关问题
     */
    suspend fun getCreatorHelpLeve(): ApiResult<HelpInfoList> {
        return request(api = { apiMTime.getCreatorHelpLeve() })
    }

    /**
     * 数据中心 - 创作者数据中心-整体概览
     */
    suspend fun getCreatorStatistics(
            statisticsType: Long,
            timeType: Long,
    ): ApiResult<DataCenterBean.EarthBean> {
        return request(api = { apiMTime.getCreatorStatistics(statisticsType, timeType) })
    }

    /**
     * 数据中心 - 创作者数据中心-单篇分析
     */
    suspend fun getCreatorStatisticsDetail(
            type: Long,
            contentId: Long,
            timeType: Long,
    ): ApiResult<DataCenterDetailBean> {
        return request(api = { apiMTime.getCreatorStatisticsDetails(type, contentId, timeType) })
    }

    /**
     * 社区创作者内容api - 创作者统计内容分页查询
     */
    suspend fun getQueryStatContent(
            type: Long,
            nextStamp: String?,
            pageSize: Long,
    ): ApiResult<AnalysisViewBean> {
        return request(
                api = {
                    val body = getRequestBody(
                            arrayMapOf(
                                    "type" to type,
                                    "nextStamp" to if (nextStamp.isNullOrEmpty()) "" else nextStamp,
                                    "pageSize" to pageSize
                            )
                    )
                    apiMTime.queryStatContent(body)
                },
                converter = { singleAnalysis ->
                    AnalysisViewBean.converster(type, singleAnalysis)
                })
    }

    /**
     * 查询当前文章用户信息
     */
    suspend fun getQueryArticleUser(): ApiResult<ArticleUser> {
        return request { apiMTime.getQueryArticleUser() }
    }

    /**
     * 判断用户是否在家族内
     */
    suspend fun checkUserInGroup(groupId: Long): ApiResult<CommonResult> {
        return request { apiMTime.checkUserInGroup(groupId) }
    }

}