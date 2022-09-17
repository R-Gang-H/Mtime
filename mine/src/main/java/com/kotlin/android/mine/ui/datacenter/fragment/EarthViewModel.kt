package com.kotlin.android.mine.ui.datacenter.fragment

import androidx.lifecycle.MutableLiveData
import com.kotlin.android.api.ApiResult
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.app.data.entity.community.content.ArticleUser
import com.kotlin.android.app.data.entity.mine.DataCenterBean
import com.kotlin.android.app.data.entity.mine.DataCenterDetailBean
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.mine.bean.AnalysisViewBean
import com.kotlin.android.mine.bean.DataCenterViewBean
import com.kotlin.android.mine.repoistory.CreatCenterRepository
import com.kotlin.android.mtime.ktx.formatCount
import java.text.DecimalFormat

private val sCountFormat: DecimalFormat = DecimalFormat(",###.#")

fun formatNumber(count: Long): String {
    // todo 千分、万、亿 通用规则显示
    return if (count <= 9999) {
        sCountFormat.format(count.toDouble())
    } else {
        formatCount(count)
    }
}

open class EarthViewModel : BaseViewModel() {

    var cententType: Long = 4

    // 分类切换
    private val uiModelTags by lazy { MutableLiveData<ArrayList<DataCenterViewBean.Tags>>() }
    val tagNames: MutableLiveData<ArrayList<DataCenterViewBean.Tags>>
        get() = uiModelTags

    fun setTagNames(tags: ArrayList<DataCenterViewBean.Tags>) {
        tagNames.value = tags
    }

    var statisticsType: Long = 0

    var position = 0 // 分类选择项
    var posDesc = "阅读量" // 选中项弹框说明

    // 分类说明
    private val uiModelTabs by lazy { MutableLiveData<ArrayList<DataCenterViewBean.Tabs>>() }
    val tabNames: MutableLiveData<ArrayList<DataCenterViewBean.Tabs>>
        get() = uiModelTabs

    fun setTabNames(tabs: ArrayList<DataCenterViewBean.Tabs>) {
        tabNames.value = tabs
    }

    // 整体表现内容
    val expressContent by lazy { MutableLiveData<String>("包括了日志、长影评、文章、帖子、视频、音频六类内容的数据") }
    fun setExpressContent(expressCon: String?) {
        expressContent.value = expressCon
    }

    // 时间筛选集合
    val timeSelec by lazy { MutableLiveData<ArrayList<String>>() }
    fun setTimeNames(times: ArrayList<String>) {
        timeSelec.value = times
    }

    // 时间筛选当前选中项
    val expressTimeFilter by lazy { MutableLiveData<String>("近7天") }
    val expressTimeSelect by lazy { MutableLiveData<Long>(1L) }
    fun setExpressTime(timeName: String, tiemSelect: Long) {
        expressTimeFilter.value = timeName
        expressTimeSelect.value = tiemSelect
    }

    // 比较是否选中时间选项
    fun getTimeSelect(timeIndex: Long): Boolean {
        return expressTimeSelect.value == timeIndex
    }

    var mCannotPublishAudio: Boolean = false //默认不展示音频

    private val repo by lazy {
        CreatCenterRepository()
    }


    var earthBean = DataCenterBean.EarthBean() // 图表信息
    private val dataCenterUiModel by lazy { BaseUIModel<DataCenterBean.EarthBean>() }
    var dataCenterState = dataCenterUiModel.uiState

    fun getStatistics(
            statisticsType: Long,   // 【必填】 统计类型:0全部(默认),1内容,2视频,3音频
            timeType: Long, // 【必填】 统计时间:1近七天(默认),2近半年,3历史累计
    ) {
        call(
                uiModel = dataCenterUiModel,
                api = {
                    getCreatorStatistics(statisticsType, timeType)
                }
        )
    }

    private suspend fun getCreatorStatistics(
            statisticsType: Long,
            timeType: Long,
    ): ApiResult<DataCenterBean.EarthBean> {
        return repo.getCreatorStatistics(statisticsType, timeType)
    }

    private val analysisUIModel by lazy { BaseUIModel<AnalysisViewBean>() }
    val analysisStatic = analysisUIModel.uiState

    fun getAnalysis(
            type: Long,// 内容类型 必填 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"), ARTICLE(4, "文章"); VIDEO(5, "视频"), AUDIO(6, "音频"),
            nextStamp: String?,
            pageSize: Long = 20L,
            isRefresh: Boolean,
    ) {
        call(
                uiModel = analysisUIModel,
                isRefresh = isRefresh,
                hasMore = {
                    it.hasNext && !it.nextStamp.isNullOrBlank()
                },
                pageStamp = {
                    it.nextStamp
                },
                api = {
                    getSingleAnalysis(type, nextStamp, pageSize)
                }
        )
    }

    private suspend fun getSingleAnalysis(
            type: Long,
            nextStamp: String?,
            pageSize: Long,
    ): ApiResult<AnalysisViewBean> {
        return repo.getQueryStatContent(type, nextStamp, pageSize)
    }

    var dataCenterDetailBean = DataCenterDetailBean() // 图表信息
    private val statisticDetailUiModel by lazy { BaseUIModel<DataCenterDetailBean>() }
    val statisticDetailStatic = statisticDetailUiModel.uiState

    fun getStatisticDetail(
            type: Long,     // 【必填】 资源类型:1:日志/2:帖子/3:影评/4:文章/5:视频/6:音频
            contentId: Long,    // 【必填】 内容id
            timeType: Long,     // 【必填】 统计时间:1近七天(默认),2近半年,3历史累计
    ) {
        call(
                uiModel = statisticDetailUiModel,
                isEmpty = {
                    it.statisticsDetailsInfos.isNullOrEmpty()
                },
                api = {
                    getCreatorStatisticsDetail(type, contentId, timeType)
                }
        )
    }

    private suspend fun getCreatorStatisticsDetail(
            type: Long,
            contentId: Long,
            timeType: Long,
    ): ApiResult<DataCenterDetailBean> {
        return repo.getCreatorStatisticsDetail(type, contentId, timeType)
    }

    //查询当前文章用户信息
    private val queryArticleUserUIModel: BaseUIModel<ArticleUser> = BaseUIModel()
    val queryArticleUserState = queryArticleUserUIModel.uiState

    /**
     * 查询当前文章用户信息
     */
    fun getQueryArticleUser() {
        call(
                uiModel = queryArticleUserUIModel
        ) {
            repo.getQueryArticleUser()
        }
    }
}