package com.kotlin.android.community.ui.person.center.filmlist

import android.view.View
import com.kotlin.android.app.router.provider.tablet.ITabletProvider
import com.kotlin.android.community.databinding.FragCommunityPersonContentListBinding
import com.kotlin.android.community.ui.person.center.BaseContentFragment
import com.kotlin.android.community.ui.person.center.PersonCenterViewModel
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.tablet.R
import com.kotlin.tablet.adapter.MineMyCreateBinder

/**
 * @author Wangwei
 * @date 2022/4/24
 * @des  片单
 */
class CommunityCenterFilmListFragment :
    BaseContentFragment<PersonCenterViewModel, FragCommunityPersonContentListBinding>(),
    MultiStateView.MultiStateListener {

    override fun startMyPageObserve() {
        mViewModel?.mFilmListUIModel?.uiState?.observe(this) { it.observerMethod() }
    }

    override fun loadData(isRefresh: Boolean) {
        mViewModel?.laodFilmList(isRefresh, userId)
    }

    override fun onBinderClick(view: View, binder: MultiTypeBinder<*>) {
        super.onBinderClick(view, binder)
        if (binder is MineMyCreateBinder) {
            when (view.id) {
                R.id.mMyCreateHostLay -> {
                    getProvider(ITabletProvider::class.java)?.startFilmListDetailsActivity(
                        1,
                        filmListId = binder.bean.filmListId ?: 0L
                    )
                }
            }
        }
    }
}