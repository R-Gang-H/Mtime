package com.kotlin.android.community.family.component.ui.manage

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.community.family.component.repository.FamilyManageRepository
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.entity.community.group.GroupCategoryList
import kotlinx.coroutines.launch

/**
 * @author vivian.wei
 * @date 2020/8/10
 * @desc 家族群组分类页ViewModel
 */
class FamilyCategoryViewModel: BaseViewModel() {

    private val repo by lazy { FamilyManageRepository() }

    private val mGroupCategoryUIModel = BaseUIModel<GroupCategoryList>()
    val uiState = mGroupCategoryUIModel.uiState

    /**
     * 群组一级分类列表
     */
    fun getCommunityGroupCategory() {
        viewModelScope.launch {
            mGroupCategoryUIModel.emitUIState(showLoading = true)

            val result = withOnIO {
                repo.getCommunityGroupCategory()
            }

            mGroupCategoryUIModel.checkResultAndEmitUIState(result)
        }
    }

}