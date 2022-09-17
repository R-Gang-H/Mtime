package com.kotlin.android.community.ui.person.wantsee

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.BinderUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.api.base.checkResult
import com.kotlin.android.app.data.entity.community.person.WantSeeInfo
import com.kotlin.android.community.repository.PersonWantSeeRepository
import com.kotlin.android.community.ui.person.PERSON_TYPE_HAS_SEEN
import com.kotlin.android.community.ui.person.PERSON_TYPE_WANT_SEE
import com.kotlin.android.community.ui.person.bean.WantSeeHasMoreList
import com.kotlin.android.community.ui.person.bean.WantSeeViewBean
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import kotlinx.coroutines.launch

/**
 * @author WangWei
 * @data 2020/10/13
 *
 */
class CommunityWantSeeViewModel : BaseViewModel() {

    private val repo by lazy { PersonWantSeeRepository() }

    private val mViewModel = BinderUIModel<WantSeeInfo, List<MultiTypeBinder<*>>>()

    val uiState = mViewModel.uiState

    fun loadData(isRefresh: Boolean, userId: Long, type: Long) {
        call(
                uiModel = mViewModel,
                isRefresh = isRefresh,
                isShowLoading = false,
                pageStamp = { it?.nextStamp },
                hasMore = { it?.hasNext },
                isEmpty = {
                    it?.items.isNullOrEmpty()
                },
                converter = {
                    val list = mutableListOf<MultiTypeBinder<*>>()
                    it.items.forEach { item ->
                        when (type) {
                            PERSON_TYPE_WANT_SEE -> list.add(WantSeeViewBean.convertToWantSeeBinder(item, it.count, PERSON_TYPE_WANT_SEE))
                            PERSON_TYPE_HAS_SEEN -> list.add(WantSeeViewBean.convertToWantSeeBinder(item, it.count, PERSON_TYPE_HAS_SEEN))
                            else -> list.add(WantSeeViewBean.convertToWantSeeBinder(item, it.count, PERSON_TYPE_HAS_SEEN))
                        }
                    }
                    list
                }
        ) {
            when (type) {
                PERSON_TYPE_WANT_SEE -> repo.loadCommunityWantSee(userId, mViewModel?.pageStamp, mViewModel?.pageSize)
                PERSON_TYPE_HAS_SEEN -> repo.loadCommunityHasSeen(userId, mViewModel?.pageStamp, mViewModel?.pageSize)
                else -> repo.loadCommunityHasSeen(userId, mViewModel?.pageStamp, mViewModel?.pageSize)
            }
        }
    }
}