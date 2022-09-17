package com.kotlin.android.community.family.component.ui.find

import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.app.data.entity.family.FindFamily
import com.kotlin.android.community.family.component.comm.FamilyCommViewModel
import com.kotlin.android.community.family.component.repository.FamilyRepository
import com.kotlin.android.community.family.component.ui.find.binder.FindFamilyRecommendItemBinder

/**
 * @author zhangjian
 * @date 2022/3/16 10:53
 */
class FindFamilyViewModel : FamilyCommViewModel<FindFamilyRecommendItemBinder>() {
    val familyRep = FamilyRepository()

    var findModel = BaseUIModel<FindFamily>()
    var findModelState = findModel.uiState

    /**
     * 找家族
     */
    fun loadFindFamilyData() {
        return call(findModel) {
            familyRep.loadFindData()
        }
    }

}