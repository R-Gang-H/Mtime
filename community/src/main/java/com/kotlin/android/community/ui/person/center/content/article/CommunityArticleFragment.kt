package com.kotlin.android.community.ui.person.center.content.article

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.kotlin.android.community.databinding.FragCommunityArticleBinding
import com.kotlin.android.community.ui.person.KEY_TYPE
import com.kotlin.android.community.ui.person.KEY_USER_ID
import com.kotlin.android.community.ui.person.center.ContentViewModel
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.ogaclejapan.smarttablayout.utils.v4.Bundler
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.frag_community_article.*
import kotlinx.android.synthetic.main.frag_community_article.mMultiStateView
import kotlinx.android.synthetic.main.frag_community_article.mRefreshLayout

/**
 * @author Wangwei
 * :com.kotlin.android.home.ui.original.OriginalFragment
 * @date 2020/10/20
 */
class CommunityArticleFragment : BaseVMFragment<ContentViewModel, FragCommunityArticleBinding>(),
        OnRefreshLoadMoreListener, MultiStateView.MultiStateListener {

    private var mIsInitData: Boolean = false
    private lateinit var mAdapter: MultiTypeAdapter
    var userId: Long = 0L
    var type: Long = 0L

    override fun initVM(): ContentViewModel = viewModels<ContentViewModel>().value

    override fun initView() {
        mRefreshLayout.setOnRefreshLoadMoreListener(this)
        mMultiStateView.setMultiStateListener(this)
        mAdapter = createMultiTypeAdapter(mArticleRecyclerView)
    }

    override fun initData() {
        if (arguments != null) {
            type = arguments?.getLong(KEY_TYPE, 0)!!
            userId = arguments?.getLong(KEY_USER_ID, 0)!!
        }
    }

    fun newInstance(userId: Long, type: Long): CommunityArticleFragment? {
        val f = CommunityArticleFragment()
        f.arguments = bundler(userId, type)
        return f
    }

    fun bundler(userId: Long, type: Long): Bundle {
        return Bundler()
                .putLong(KEY_USER_ID, userId)
                .putLong(KEY_TYPE, type)
                .get()
    }
    override fun startObserve() {
        mViewModel?.run {
            uiState.observe(this@CommunityArticleFragment, Observer {
                it.apply {
                    success?.apply {
                        showData(this, loadMore, noMoreData)
                    }

                    if (isEmpty) {
                        if (loadMore) {
                            mRefreshLayout.finishLoadMore(true)
                        } else {
                            mRefreshLayout.finishRefresh()
                            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY)
                        }
                    }

                    error?.apply {
                        if (loadMore) {
                            mRefreshLayout.finishLoadMore(false)
                        } else {
                            mRefreshLayout.finishRefresh()
                            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR)
                        }
                    }

                    netError?.apply {
                        if (loadMore) {
                            mRefreshLayout.finishLoadMore(false)
                        } else {
                            mRefreshLayout.finishRefresh()
                            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                        }
                    }
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        if (!mIsInitData) {
            mRefreshLayout.autoRefresh()
        }
    }

    private fun showData(
            data: List<MultiTypeBinder<*>>,
            isLoadMore: Boolean,
            noMoreData: Boolean
    ) {
        mIsInitData = true
        if (!isLoadMore) {
            mAdapter.notifyAdapterDataSetChanged(data) {
                if (noMoreData) {
                    mRefreshLayout.finishRefreshWithNoMoreData()
                } else {
                    mRefreshLayout.finishRefresh()
                }
            }
        } else {
            mAdapter.notifyAdapterAdded(data) {
                if (noMoreData) {
                    mRefreshLayout.finishLoadMoreWithNoMoreData()
                } else {
                    mRefreshLayout.finishLoadMore()
                }
            }
        }
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
//        mViewModel?.loadData(true,type,userId)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
//        mViewModel?.loadData(false,type,userId)
    }

    override fun destroyView() {

    }

    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                mRefreshLayout.autoRefresh()
            }
        }
    }
}