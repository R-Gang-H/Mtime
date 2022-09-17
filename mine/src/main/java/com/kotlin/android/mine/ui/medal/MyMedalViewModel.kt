package com.kotlin.android.mine.ui.medal

import com.kotlin.android.api.ApiResult
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.app.data.entity.community.medal.MedalData
import com.kotlin.android.app.data.entity.community.medal.MedalDetail
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.mine.binder.MedalBinder
import com.kotlin.android.mine.repoistory.MedalRepository
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

class MyMedalViewModel : BaseViewModel() {

    private val repo by lazy {
        MedalRepository()
    }

    private val medalUIModel: BaseUIModel<MedalData> = BaseUIModel()
    val medalState = medalUIModel.uiState

    private val medalDetailUIModel: BaseUIModel<MedalDetail> = BaseUIModel()
    val medalDetailState = medalDetailUIModel.uiState

    fun getMedalData() {
        call(uiModel = medalUIModel, isEmpty = { it == null }) {
            requestData()
        }
    }

    fun getMedalDetail(medalId: Long) {
        call(uiModel = medalDetailUIModel) {
            repo.getMedalDetail(medalId)
        }
    }

    private suspend fun requestData(): ApiResult<MedalData> {
        return repo.getMedalData()
    }

    fun obtainOngoingMedalInfo(ongoingMedalInfos: List<MedalData.MedalInfos>?): MutableList<MultiTypeBinder<*>> {
        return ongoingMedalInfos?.map { converterMedalInfo(it, true) }?.toMutableList()
            ?: mutableListOf()
    }

    fun obtainCompleteMedalInfo(completeMedalInfos: List<MedalData.MedalInfos>?): MutableList<MultiTypeBinder<*>> {
        return completeMedalInfos?.map { converterMedalInfo(it, false) }?.toMutableList()
            ?: mutableListOf()
    }

    private fun converterMedalInfo(
        bean: MedalData.MedalInfos,
        isAward: Boolean
    ): MultiTypeBinder<*> {
        return MedalBinder(bean,isAward)
    }
}