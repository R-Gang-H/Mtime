package com.kotlin.android.home.ui

import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.callParallel
import com.kotlin.android.app.data.entity.home.RcmdTrailerList
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.home.repository.RecommendRepository
import com.kotlin.android.home.ui.recommend.bean.TrailerItem

class RcmdTrailerListVIewModel : BaseViewModel() {
    private val repo by lazy { RecommendRepository() }
    
    private val mUIModel = BaseUIModel<List<TrailerItem>>()
    val uiState = mUIModel.uiState
    
    //加载每日佳片列表数据
    fun loadData() {
        callParallel(
            { repo.loadTrailers(RcmdTrailerList.TYPE_HISTORY) },
            uiModel = mUIModel,
            isShowLoading = true,
            isEmpty = {
                it.isEmpty()
            }
        ) {
            val result = arrayListOf<TrailerItem>()
            (it[0] as? RcmdTrailerList)?.apply {
                items?.map { 
                    result.add(TrailerItem.converter2UIBean(it))
                }
            }
            result
        }
    }
}