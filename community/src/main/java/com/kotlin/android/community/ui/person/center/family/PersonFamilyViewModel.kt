package com.kotlin.android.community.ui.person.center.family

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.checkResult
import com.kotlin.android.community.family.component.comm.FamilyCommViewModel
import com.kotlin.android.community.repository.PersonFamilyRepository
import com.kotlin.android.community.ui.person.binder.CommunityPersonFamilyBinder
import com.kotlin.android.app.data.entity.CommHasMoreList
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import kotlinx.coroutines.launch

/**
 * @author WangWei
 * @date 2020/9/25
 */
class PersonFamilyViewModel : FamilyCommViewModel<CommunityPersonFamilyBinder>() {
    private val repo by lazy { PersonFamilyRepository() }

    private val mFamilyUIModel = BaseUIModel<CommHasMoreList<MultiTypeBinder<*>>>()

    val uiState = mFamilyUIModel.uiState

    private var pageIndex = 1L
    private val pageSize = 20L

    fun loadData(isLoadMore: Boolean, userId: Long) {
        viewModelScope.launch(main) {
            if (!isLoadMore) {
                pageIndex = 1L
            }
            val result = withOnIO {
                repo.loadData(pageIndex, pageSize, userId)
            }
            checkResult(result,
                    empty = {
                        mFamilyUIModel.emitUIState(
                                loadMore = isLoadMore,
                                isEmpty = true)
                    }, error = {
                mFamilyUIModel.emitUIState(error = it)
            }, netError = {
                mFamilyUIModel.emitUIState(netError = it)
            }, success = {
                ++pageIndex
                mFamilyUIModel.emitUIState(
                        success = it,
                        loadMore = isLoadMore,
                        noMoreData = !it.hasMore
                )
            })
        }
    }
}