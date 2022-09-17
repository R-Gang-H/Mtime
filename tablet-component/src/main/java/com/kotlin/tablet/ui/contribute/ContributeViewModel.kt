package com.kotlin.tablet.ui.contribute

import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.app.data.constant.CommConstant.SHARE_TYPE_FILM_LIST
import com.kotlin.android.app.data.entity.common.CommonShare
import com.kotlin.android.app.data.entity.filmlist.CurrActivityInfo
import com.kotlin.android.core.BaseViewModel
import com.kotlin.tablet.repository.FilmListRepository

/**
 * 创建者: SunHao
 * 创建时间: 2022/3/25
 * 描述:投稿
 **/
class ContributeViewModel : BaseViewModel() {

    private val repo by lazy { FilmListRepository() }
    private val currActivityUiModel by lazy { BaseUIModel<CurrActivityInfo>() }

    //    分享
    private val shareUIModel = BaseUIModel<CommonShare>()
    val shareUIState = shareUIModel.uiState

    val currActivityUiState by lazy { currActivityUiModel.uiState }

    fun reqCurrActivities() {
        call(
            isRefresh = true,
            uiModel = currActivityUiModel,
            pageStamp = { it.nextStamp })
        {
            repo.reqCurrActivities(
                pageSize = currActivityUiModel.pageSize,
                nextStamp = currActivityUiModel.pageStamp
            )
        }
    }

    /**
     * 片单活动分享
     */
    fun getContributeShareInfo() {
        call(uiModel = shareUIModel) {
            repo.getContributeShareInfo(
                type = SHARE_TYPE_FILM_LIST,
                relateId = 0L
            )
        }
    }
}