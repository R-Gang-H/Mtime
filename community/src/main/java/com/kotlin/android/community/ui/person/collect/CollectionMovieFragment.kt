package com.kotlin.android.community.ui.person.collect

import android.view.View
import com.kotlin.android.community.R
import com.kotlin.android.community.databinding.FragCommunityPersonContentListBinding
import com.kotlin.android.community.ui.person.binder.CollectionMovieBinder
import com.kotlin.android.community.ui.person.center.BaseContentFragment
import com.kotlin.android.community.ui.person.center.PersonCenterViewModel
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.multistate.MultiStateView

/**
 *  * 收藏-电影

 * @author Wangwei
 * @date 2022/4/1
 */
class CollectionMovieFragment :
    BaseContentFragment<PersonCenterViewModel, FragCommunityPersonContentListBinding>(),
    MultiStateView.MultiStateListener {

    override fun startMyPageObserve() {
        mViewModel?.mMovieUIModel?.uiState?.observe(this) { it.observerMethod() }
        mViewModel?.mCommWantToSeeUIState?.observe(this) {
            it.apply {
                success?.apply {
                    if (this.result.isSuccess()) {
                        if (this.extend is CollectionMovieBinder) {
                            (this.extend as CollectionMovieBinder).notifyWantSeeStatus()
                        }
                        showToast(this.result.statusMsg)
                    }
                }
            }
        }
    }

    override fun loadData(isRefresh: Boolean) {
        mViewModel?.loadCollectionMovie(isRefresh)
    }

    override fun onBinderClick(view: View, binder: MultiTypeBinder<*>) {
        super.onBinderClick(view, binder)
        if (view.id == R.id.tv_want_see) {
            if (binder is CollectionMovieBinder) {//操作想看
                mViewModel?.getMovieWantToSee(binder.bean.movieId, 1, extend = binder)
            }
        }
    }
}