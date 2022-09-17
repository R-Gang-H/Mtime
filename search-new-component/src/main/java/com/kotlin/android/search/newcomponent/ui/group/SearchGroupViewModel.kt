package com.kotlin.android.search.newcomponent.ui.group

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.api.base.checkResult
import com.kotlin.android.app.data.annotation.SEARCH_POST
import com.kotlin.android.app.data.entity.CommHasMoreList
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.mtime.ktx.GlobalDimensionExt
import com.kotlin.android.search.newcomponent.repo.SearchRepository
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import kotlinx.coroutines.launch

/**
 * @des 搜索家族中的贴子
 * @author zhangjian
 * @date 2022/3/25 14:25
 */
class SearchGroupViewModel : BaseViewModel() {

    private val repo by lazy { SearchRepository() }
    // 按分类搜页码
    private var mPageIndex = 1L
    private val PAGE_SIZE = 20L
    private val locationId = GlobalDimensionExt.getDigitsCurrentCityId()
    private val mLongitude = GlobalDimensionExt.getLongitude()
    private val mLatitude = GlobalDimensionExt.getLatitude()

    var uiModel = BaseUIModel<List<MultiTypeBinder<*>>>()
    var uiModelState = uiModel.uiState

    fun searchPost(
        keyWord: String,
        groupId: Long,
        isRefresh: Boolean
    ) {
        viewModelScope.launch(main) {
            if (isRefresh) {
                mPageIndex = 1
            }
            val result = withOnIO {
                repo.unionSearchByGroupId(keyWord,groupId, mPageIndex, PAGE_SIZE, SEARCH_POST,
                    locationId, mLongitude, mLatitude,0L)
            }

            checkResult(result, isEmpty = {
                it.list.isNullOrEmpty()
            }, empty = {
                uiModel.emitUIState(isEmpty = true, isRefresh = isRefresh)
            }, error = {
                uiModel.emitUIState(error = it, isRefresh = isRefresh)
            }, netError = {
                uiModel.emitUIState(netError = it, isRefresh = isRefresh)
            }, needLogin = {
                uiModel.emitUIState(needLogin = true, isRefresh = isRefresh)
            }, success = {
                ++mPageIndex
                uiModel.emitUIState(success = it.list, isRefresh = isRefresh,
                    noMoreData = !it.hasMore
                )
            })
        }
    }

}