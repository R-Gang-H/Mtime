package com.kotlin.android.community.ui.person.collect

import com.kotlin.android.community.databinding.FragCommunityPersonContentListBinding
import com.kotlin.android.community.ui.person.center.BaseContentFragment
import com.kotlin.android.community.ui.person.center.PersonCenterViewModel
import com.kotlin.android.widget.multistate.MultiStateView

/**
 * 收藏-影人
 * @author Wangwei
 * @date 2022/4/1
 */
class CollectionPersonFragment :
    BaseContentFragment<PersonCenterViewModel, FragCommunityPersonContentListBinding>(),
    MultiStateView.MultiStateListener {

    override fun startMyPageObserve() {
        mViewModel?.mPersonUIModel?.uiState?.observe(this) { it.observerMethod() }
    }

    override fun loadData(isRefresh: Boolean) {
        mViewModel?.loadCollectionPerson(isRefresh)
    }
}