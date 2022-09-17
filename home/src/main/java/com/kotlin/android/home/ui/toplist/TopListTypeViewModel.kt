package com.kotlin.android.home.ui.toplist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.entity.toplist.*
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.home.repository.TopListRepository
import com.kotlin.android.home.ui.toplist.constant.TopListConstant
import kotlinx.coroutines.launch

/**
 * @author vivian.wei
 * @date 2020/7/10
 * @desc 电影/电视剧/影人榜单列表共用
 */
class TopListTypeViewModel : BaseViewModel() {

    companion object {
        const val PAGE_SIZE = 10L
    }

    data class TopListUIModel(
            var firstCategoryTopList: TopListInfo ?= null,
            var otherCategoryTopLists: List<TopListInfo> ?= null,
            var yearlyTopLists: List<TopListInfo> ?= null,
            var year: Long ?= 0,
            var cateogryVisible: Boolean ?= false,
            var yearlyVisible: Boolean ?= false
    ): ProguardRule

    private val repo by lazy { TopListRepository() }

    // 首页榜单推荐列表
    private val mIndexAppTopListUIModel = BaseUIModel<IndexAppTopList>()
    val recommendUiState = mIndexAppTopListUIModel.uiState

    // 分类榜单查询
    private val mIndexTopListQueryUIModel = BaseUIModel<IndexTopListQuery>()
    val queryUiState = mIndexTopListQueryUIModel.uiState

    // 页面显示UIModel
    private val _topListUIModel = MutableLiveData<TopListUIModel>()
    val topListUIModel: LiveData<TopListUIModel> = _topListUIModel

    /**
     * App榜单列表（推荐位数据）
     */
    fun getIndexTopList() {
        viewModelScope.launch {

            val result = withOnIO {
                repo.getIndexTopList()
            }

            mIndexAppTopListUIModel.checkResultAndEmitUIState(result)
        }
    }

    /**
     * 分类查询（精选片单）
     */
    fun getTopListQuery(type: Long, pageIndex: Int) {
        viewModelScope.launch {

            val result = withOnIO {
                repo.getTopListQuery(type, pageIndex.toLong(), PAGE_SIZE)
            }
            mIndexTopListQueryUIModel.checkResultAndEmitUIState(result)
        }
    }

    /**
     * 获取分类数据
     */
    fun getTypeData(toplistType: Long, indexAppTopList: IndexAppTopList) {
        var uiModel = TopListUIModel()
        // 分类模块最小显示数
        var categoryMinCount = if(toplistType == TopListConstant.TOPLIST_TYPE_MOVIE)
            TopListConstant.MOVIE_TOPLIST_CATEGORY_SHOWCOUNT else
            TopListConstant.PERSON_TOPLIST_CATEGORY_SHOWCOUNT

        uiModel.cateogryVisible = false
        uiModel.yearlyVisible = false
        when(toplistType) {
            TopListConstant.TOPLIST_TYPE_MOVIE -> {
                // 分类榜
                indexAppTopList.movieTopList?.topListInfos?.let {
                    if(it.size >= categoryMinCount) {
                        uiModel.cateogryVisible = true
                        uiModel.firstCategoryTopList = it[0]
                        uiModel.otherCategoryTopLists = it.slice(1..categoryMinCount.coerceAtMost(it.size - 1))
                    }
                }
                // 年度
                indexAppTopList.movieTopListYearly?.topListInfosYearly?.let {
                    if (it.isNotEmpty()) {
                        uiModel.yearlyVisible = true
                        // 第一个年份的榜单
                        uiModel.year = it[0].year
                        it[0].topListInfos?.let {
                            // 最多显示4个
                            uiModel.yearlyTopLists = it.slice(0..TopListConstant.MOVIE_TOPLIST_YEAR_SHOWCOUNT.coerceAtMost(it.size - 1))
                        }
                    }
                }
            }
            TopListConstant.TOPLIST_TYPE_TV -> {
                // 分类榜
                indexAppTopList.tvTopList?.topListInfos?.let {
                    if(it.size >= categoryMinCount) {
                        uiModel.cateogryVisible = true
                        uiModel.firstCategoryTopList = it[0]
                        uiModel.otherCategoryTopLists = it.slice(1..categoryMinCount.coerceAtMost(it.size - 1))
                    }
                }
            }
            TopListConstant.TOPLIST_TYPE_PERSON -> {
                // 分类榜
                indexAppTopList.personTopList?.topListInfos?.let {
                    if(it.size >= categoryMinCount) {
                        uiModel.cateogryVisible = true
                        uiModel.firstCategoryTopList = it[0]
                        uiModel.otherCategoryTopLists = it.slice(1..categoryMinCount.coerceAtMost(it.size - 1))
                    }
                }
            }
            else -> {
            }
        }
        _topListUIModel.value = uiModel
    }

    /**
     * 获取指定年度的榜单列表
     */
    fun getYearlyTopLists(year: Long, list: List<TopListInfos>): List<TopListInfo>? {
        list.forEach {
            if(it.year == year) {
                return it.topListInfos
            }
        }
        return null
    }

}