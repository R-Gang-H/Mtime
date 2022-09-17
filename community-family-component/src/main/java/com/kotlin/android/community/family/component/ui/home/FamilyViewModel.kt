package com.kotlin.android.community.family.component.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.checkResult
import com.kotlin.android.community.family.component.comm.FamilyCommViewModel
import com.kotlin.android.community.family.component.repository.FamilyHomeRepository
import com.kotlin.android.community.family.component.ui.clazz.adapter.FamilyItemBinder
import com.kotlin.android.community.family.component.ui.home.adapter.FamilyClassListItemBinder
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import kotlinx.coroutines.launch

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/27
 */
class FamilyViewModel: FamilyCommViewModel<FamilyItemBinder>() {
    private val repo by lazy { FamilyHomeRepository() }

    private val mFamilyHomeUIModel = BaseUIModel<List<MultiTypeBinder<*>>>()

    val uiState = mFamilyHomeUIModel.uiState

    private var pageIndex = 1L
    private val pageSize = 20L

    fun loadData(isLoadMore: Boolean) {
        viewModelScope.launch(main) {
            val list = mutableListOf<MultiTypeBinder<*>>()

            if (!isLoadMore) {
                pageIndex = 1L
                //家族分类
                val resultFamilyClass = withOnIO {
                    repo.loadFamilyClass()
                }
                checkResult(resultFamilyClass, success = {
                    if (it.isNotEmpty()) {
                        list.add(FamilyClassListItemBinder(it))
                    }
                })
            }

            //热门家族
            val resultFamilyList = withOnIO {
                repo.loadHotFamilyData(pageIndex, pageSize)
            }

            checkResult(resultFamilyList, isEmpty = {
                it.list.isNullOrEmpty() && list.isEmpty()
            }, empty = {
                mFamilyHomeUIModel.emitUIState(
                        loadMore = isLoadMore,
                        noMoreData = false,
                        isEmpty = true)
            }, error = {
                mFamilyHomeUIModel.emitUIState(
                        error = it
                        , loadMore = isLoadMore)
            }, netError = {
                mFamilyHomeUIModel.emitUIState(
                        netError = it
                        , loadMore = isLoadMore)
            }, success = {
                ++pageIndex
                list.addAll(it.list)
                mFamilyHomeUIModel.emitUIState(
                        success = list,
                        loadMore = isLoadMore,
                        noMoreData = !it.hasMore
                )
            })
        }
    }
}