package com.kotlin.android.publish.component.ui.family

import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.entity.community.group.MyGroupList
import com.kotlin.android.app.data.entity.community.publish.Footmarks
import com.kotlin.android.publish.component.repo.FamilyListRepository

/**
 *
 * Created on 2020/10/14.
 *
 * @author o.s
 */
class FamilyListViewModel : BaseViewModel() {

    private val repo by lazy { FamilyListRepository() }

    private val uiModel by lazy { BaseUIModel<ArrayList<MyGroupList.Group>>() }
    private val footmarksUIModel by lazy { BaseUIModel<Footmarks>() }
    val uiState = uiModel.uiState
    val footmarksUIState = footmarksUIModel.uiState

    fun loadFamilyList() {
        launchOnUI {
            uiModel.emitUIState(showLoading = true)
            val result = withOnIO {
                repo.loadFamilyList()
            }
            uiModel.checkResultAndEmitUIState(result)
        }
    }

    /**
     * 种草家族列表
     */
    fun getGroupFootmarks() {
        call(
            uiModel = footmarksUIModel,
        ) {
            repo.getGroupFootmarks()
        }
    }
}