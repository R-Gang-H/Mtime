package com.kotlin.android.ugc.detail.component.ui.mediadetail

import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.comment.component.DetailBaseViewModel
import com.kotlin.android.ugc.detail.component.bean.UgcMediaViewBean
import com.kotlin.android.ugc.detail.component.repository.UgcMediaRepository

/**
 * create by lushan on 2022/3/16
 * des:ugc视频详情、音乐详情
 **/
class UgcMediaViewModel: DetailBaseViewModel() {
        private val repo by lazy { UgcMediaRepository() }

    private val detailUIModel:BaseUIModel<UgcMediaViewBean> = BaseUIModel<UgcMediaViewBean>()
    val detailUIState = detailUIModel.uiState

    fun loadDetail(type: Long, contentId: Long, recId:Long){
        call(detailUIModel){
            repo.loadUgcDetail(contentId, type,recId)
        }
    }
}