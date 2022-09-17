package com.kotlin.android.home.ui.zhongcao

import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.app.data.entity.common.CommSubTypeList
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.home.repository.ZhongCaoRepository
import com.kotlin.android.home.ui.bean.BannerItem

class ZhongCaoViewModel : BaseViewModel() {
    private val repo by lazy { ZhongCaoRepository() }

    private val mBannerModel = BaseUIModel<List<BannerItem>>()
    val bannerUIState = mBannerModel.uiState

    private val mSubTypeModel = BaseUIModel<CommSubTypeList>()
    val subTypeUIState = mSubTypeModel.uiState

    /**
     * 加载banner数据
     */
    fun loadBanner() {
        call(
            uiModel = mBannerModel,
            isShowLoading = false
        ) {
            repo.loadBanner()
        }
    }

    /**
     * 加载子分类列表
     */
    fun loadSubTypes() {
        call(
            uiModel = mSubTypeModel,
            isShowLoading = true
        ) {
            repo.loadSubClass()
        }
    }
}