package com.kotlin.android.search.newcomponent.ui.history

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.ApiResult
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.core.ext.getSpValue
import com.kotlin.android.ktx.ext.handleToList
import com.kotlin.android.review.component.item.bean.ReviewItem
import com.kotlin.android.search.newcomponent.Search
import com.kotlin.android.search.newcomponent.adapter.binder.SearchHistoryBinder
import com.kotlin.android.search.newcomponent.adapter.binder.SearchHotSearchBinder
import com.kotlin.android.search.newcomponent.adapter.binder.SearchRcmdReviewBinder
import com.kotlin.android.search.newcomponent.bean.HotSearchBean
import com.kotlin.android.search.newcomponent.repo.SearchRepository
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import kotlinx.coroutines.launch
import retrofit2.http.Query
import java.util.ArrayList

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2021/5/17
 */
class SearchHistoryViewModel : BaseViewModel() {

    private val repo by lazy { SearchRepository() }

    private val uiModel by lazy { BaseUIModel<MutableList<MultiTypeBinder<*>>>() }
    val uiState = uiModel.uiState

    private val uiHotWordModel by lazy { BaseUIModel<List<String>>() }
    val uiHotWordState = uiHotWordModel.uiState

    var searchHistoryData: MutableList<String> = arrayListOf()

    private fun getSearchHistory() {
        val spValue = getSpValue(Search.SP_KEY_SEARCH_HISTORY, "")
        searchHistoryData = handleToList(spValue, String::class.java)?.toMutableList()
                ?: arrayListOf()
    }

    fun loadHotSearch() {
        viewModelScope.launch {
            uiModel.emitUIState(showLoading = true)

            var apiHotWordResult: ApiResult.Success<List<String>>? = null
            val binders: MutableList<MultiTypeBinder<*>> = ArrayList()

            withOnIO {
                getSearchHistory()
                if (!searchHistoryData.isNullOrEmpty()) {
                    binders.add(SearchHistoryBinder(searchHistoryData))
                }

                val apiResult = repo.hotSearch()
                if (apiResult is ApiResult.Success) {
                    apiResult.data.let { hotSearch ->
                        val films = hotSearch.hotMovies?.map {
                            HotSearchBean(
                                    title = it.name.orEmpty(),
                                    score = it.rating.orEmpty(),
                                    hotLevel = getHotLevel(it.compare),
                                    id = it.relateId ?: 0L,
                                    type = HotSearchBean.HOT_TYPE_FILM
                            )
                        }
                        val peoples = hotSearch.hotPersons?.map {
                            HotSearchBean(
                                    title = it.name.orEmpty(),
                                    score = it.rating.orEmpty(),
                                    hotLevel = getHotLevel(it.compare),
                                    id = it.relateId ?: 0L,
                                    type = HotSearchBean.HOT_TYPE_PEOPLE
                            )
                        }
                        val familys = hotSearch.hotGroups?.map {
                            HotSearchBean(
                                    title = it.name.orEmpty(),
                                    score = it.rating.orEmpty(),
                                    hotLevel = getHotLevel(it.compare),
                                    id = it.relateId ?: 0L,
                                    type = HotSearchBean.HOT_TYPE_FAMILY
                            )
                        }

                        val hotWordList = mutableListOf<String>()
                        val hotSearchMap = mutableMapOf<Int, List<HotSearchBean>>()
                        if (!films.isNullOrEmpty()) {
                            hotSearchMap[HotSearchBean.HOT_TYPE_FILM] = films
                            hotWordList.add(films[0].title)
                        }
                        if (!peoples.isNullOrEmpty()) {
                            hotSearchMap[HotSearchBean.HOT_TYPE_PEOPLE] = peoples
                            hotWordList.add(peoples[0].title)
                        }
                        if (!familys.isNullOrEmpty()) {
                            hotSearchMap[HotSearchBean.HOT_TYPE_FAMILY] = familys
                            hotWordList.add(familys[0].title)
                        }
                        if (!hotSearchMap.isNullOrEmpty()) {
                            binders.add(SearchHotSearchBinder(hotSearchMap))
                        }
                        if (hotWordList.isNotEmpty()) {
                            apiHotWordResult = ApiResult.Success(hotWordList)
                        }

                        hotSearch.hotFcRcmds?.let {
                            if (it.isNotEmpty()) {
                                binders.add(SearchRcmdReviewBinder(ReviewItem.converter(it[0])))
                            }
                        }
                    }
                }
            }

            apiHotWordResult?.let {
                uiHotWordModel.checkResultAndEmitUIState(it)
            }

            val apiRe = ApiResult.Success(binders)
            uiModel.checkResultAndEmitUIState(apiRe)
        }
    }

    /**
     * 热门的热度值
     */
    private fun getHotLevel(index: Long?): Int {
        return when {
            null == index -> {
                HotSearchBean.HOT_LEVEL_NOTHING
            }
            index > 0L -> {
                HotSearchBean.HOT_LEVEL_UP
            }
            index < 0L -> {
                HotSearchBean.HOT_LEVEL_DOWN
            }
            else -> {
                HotSearchBean.HOT_LEVEL_FLAT
            }
        }
    }

    /**
     * 热门电影，影人点击上报
     * @param pType 影片 1 、  影人 2、  文章 3  、用户6、影评7、帖子8、日志9
     * @param sType 影片 0 、 电视剧 1 、 文章-1  、影人 -1、 用户-1、影评-1、帖子-1、日志-1
     * @param keyword 影片id 、影人id、文章标题、用户id、影评id、帖子id、日志id
     */
    fun searchPopularClick(pType: Long,
                           sType: Long,
                           keyword: String) {
        viewModelScope.launch {
            withOnIO {
                repo.searchPopularClick(pType, sType, keyword)
            }
        }
    }
}