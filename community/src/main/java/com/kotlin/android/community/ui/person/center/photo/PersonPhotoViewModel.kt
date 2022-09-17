package com.kotlin.android.community.ui.person.center.photo

import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.app.api.viewmodel.CommViewModel
import com.kotlin.android.app.data.entity.CommHasNextList
import com.kotlin.android.community.repository.PersonPhotoRepository
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @author WangWei
 * @date 2020/9/25
 */
class PersonPhotoViewModel : CommViewModel<MultiTypeBinder<*>>() {
    private val repo by lazy { PersonPhotoRepository() }

    private val mUIModel = BaseUIModel<CommHasNextList<MultiTypeBinder<*>>>()

    val uiState = mUIModel.uiState

    fun loadData(isLoadMore: Boolean, userId: Long) {
        call(
            uiModel = mUIModel,
            isShowLoading = false,
            isRefresh = isLoadMore.not(),
            isEmpty = {
                (it.items?.isNotEmpty() == true).not()
            },
            pageStamp = {
                it.nextStamp
            },

            hasMore = {
                it.hasNext
            },
            api = {
                repo.loadData(mUIModel.pageStamp, mUIModel.pageSize, userId)
            }

        )
    }
}