package com.kotlin.android.community.family.component.ui.clazz

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.checkResult
import com.kotlin.android.community.family.component.comm.FamilyCommViewModel
import com.kotlin.android.community.family.component.repository.FamilyClassListRepository
import com.kotlin.android.community.family.component.ui.clazz.adapter.FamilyItemBinder
import com.kotlin.android.community.family.component.ui.clazz.bean.FamilyClassItem
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import kotlinx.coroutines.launch

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/28
 */
class FamilyClassListViewModel: FamilyCommViewModel<FamilyItemBinder>() {

    private val repo by lazy { FamilyClassListRepository() }

    private val mFamilyClassUIModel = BaseUIModel<List<FamilyClassItem>>()

    val uiClassState = mFamilyClassUIModel.uiState

    private val mFamilyListUIModel = BaseUIModel<List<MultiTypeBinder<*>>>()

    val uiListState = mFamilyListUIModel.uiState

    private var pageIndex = 1L
    private var pageSize = 20L

    fun loadClassData() {
        viewModelScope.launch(main) {
            mFamilyClassUIModel.emitUIState(showLoading = true)

            val result = withOnIO {
                repo.loadClassData()
            }

            checkResult(result, isEmpty = {
                it.isNullOrEmpty()
            }, empty = {
                mFamilyClassUIModel.emitUIState(isEmpty = true)
            }, error = {
                mFamilyClassUIModel.emitUIState(error = it)
            }, netError = {
                mFamilyClassUIModel.emitUIState(netError = it)
            }, needLogin = {
                mFamilyClassUIModel.emitUIState(needLogin = true)
            }, success = {
                mFamilyClassUIModel.emitUIState(success = it)
            })
        }
    }

    fun loadFamilyList(isLoadMore: Boolean, categoryId: Long) {
        viewModelScope.launch(main) {
            if (!isLoadMore) {
                mFamilyListUIModel.emitUIState(showLoading = true)
                pageIndex = 1L
            }

            val result = withOnIO {
                repo.loadFamilyList(categoryId, pageIndex, pageSize)
            }

            checkResult(result, empty = {
                mFamilyListUIModel.emitUIState(
                        isEmpty = true
                        , loadMore = isLoadMore)
            } ,error = {
                mFamilyListUIModel.emitUIState(
                        error = it,
                        loadMore = isLoadMore)
            }, netError = {
                mFamilyListUIModel.emitUIState(
                        netError = it,
                        loadMore = isLoadMore)
            }, needLogin = {
                mFamilyListUIModel.emitUIState(
                        needLogin = true,
                        loadMore = isLoadMore)
            }, success = {
                pageIndex++
                mFamilyListUIModel.emitUIState(
                        success = it.list,
                        loadMore = isLoadMore,
                        noMoreData = !it.hasMore)
            })
        }
    }
}