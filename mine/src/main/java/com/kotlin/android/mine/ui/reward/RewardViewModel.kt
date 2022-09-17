package com.kotlin.android.mine.ui.reward

import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.app.data.entity.mine.RewardEntity
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.mine.repoistory.CreatorRepository

/**
 * 权益说明viewmodel
 */
class RewardViewModel : BaseViewModel() {
    private val repo by lazy {
        CreatorRepository()
    }
    private val rewardUiModel by lazy { BaseUIModel<RewardEntity>() }
    var rewardState = rewardUiModel.uiState

    /**
     * 权益说明数据
     */
    fun loadData() {
        call(
            uiModel = rewardUiModel
        ) {
            repo.getCreatorReward()
        }
    }

}