package com.kotlin.android.community.ui.person.center.content.article

import com.kotlin.android.community.databinding.FragCommunityPersonContentListBinding
import com.kotlin.android.community.ui.person.center.BaseContentFragment
import com.kotlin.android.community.ui.person.center.PersonCenterViewModel
import com.kotlin.android.widget.multistate.MultiStateView

/**
 * @author Wangwei
 * @date 2022/3/27
 */
class CommunityCenterContentFragment :
        BaseContentFragment<PersonCenterViewModel, FragCommunityPersonContentListBinding>(),
        MultiStateView.MultiStateListener {

    override fun startMyPageObserve() {
        if (contentType == 1L)//个人主页
            mViewModel?.mContentUIModel?.uiState?.observe(this) { it.observerMethod() }
        else
            mViewModel?.mCollectionUIModel?.uiState?.observe(this) { it.observerMethod() }
    }

    override fun loadData(isRefresh: Boolean) {
        if (contentType == 1L)//个人主页
            mViewModel?.loadContent(isRefresh, type, userId)
        else
            mViewModel?.loadCollectionContent(isRefresh, type, userId)//收藏

    }
}