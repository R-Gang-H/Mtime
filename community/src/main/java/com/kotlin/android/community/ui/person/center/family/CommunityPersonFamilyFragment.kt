package com.kotlin.android.community.ui.person.center.family

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kotlin.android.community.R
import com.kotlin.android.community.databinding.FragCommunityPersonFamilyListBinding
import com.kotlin.android.community.ui.person.KEY_USER_ID
import com.kotlin.android.community.ui.person.binder.CommunityPersonFamilyBinder
import com.kotlin.android.community.ui.person.center.CommunityPersonContentFragment
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.router.liveevent.LOGIN_STATE
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.ogaclejapan.smarttablayout.utils.v4.Bundler
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.frag_community_person_family_list.mMultiStateView
import kotlinx.android.synthetic.main.frag_community_person_family_list.mRefreshLayout
import kotlinx.android.synthetic.main.frag_community_person_family_list.*

/**
 * @author WangWei
 * @date 2020/9/25
 */
class CommunityPersonFamilyFragment : BaseVMFragment<PersonFamilyViewModel, FragCommunityPersonFamilyListBinding>(),
        OnRefreshLoadMoreListener, MultiStateView.MultiStateListener {

    override fun initVM(): PersonFamilyViewModel = viewModels<PersonFamilyViewModel>().value

    private lateinit var mAdapter: MultiTypeAdapter
    private var mIsInitData = false
    private var userId: Long = 0L

    override fun initView() {
        mRefreshLayout.setOnRefreshLoadMoreListener(this)
        mMultiStateView.setMultiStateListener(this)
        mAdapter = createMultiTypeAdapter(mCommunityFamilyListRv)
        mAdapter.setOnClickListener(::onBinderClick)
    }

    override fun initData() {
        if (arguments != null) {
            userId = arguments?.getLong(KEY_USER_ID, 0)!!
        }
    }

    fun newInstance(userId: Long): CommunityPersonContentFragment? {
        val f = CommunityPersonContentFragment()
        f.arguments = bundler(userId)
        return f
    }

    fun bundler(userId: Long): Bundle {
        return Bundler()
                .putLong(KEY_USER_ID, userId)
                .get()
    }

    override fun onResume() {
        super.onResume()
        //首次加载
        if (!mIsInitData) {
            mIsInitData = true
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT)
            mRefreshLayout.autoRefresh()
        }
    }

    override fun startObserve() {
        LiveEventBus
                .get(LOGIN_STATE, com.kotlin.android.app.router.liveevent.event.LoginState::class.java)
                .observe(this, Observer {
                    mIsInitData = false
                })

        //页面数据回调监听
        registerPageUIStageObserve()
    }

    //页面数据回调监听
    private fun registerPageUIStageObserve() {
        mViewModel?.uiState?.observe(this, Observer {
            it.apply {
                success?.apply {
                    showData(this.list, loadMore, noMoreData)
                }

                if (isEmpty) {
                    if (loadMore) {
                        mRefreshLayout.finishLoadMore(false)
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

//

    override fun destroyView() {

    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mViewModel?.loadData(false, userId)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mViewModel?.loadData(true,userId)
    }

    private fun showData(
            data: List<MultiTypeBinder<*>>,
            isLoadMore: Boolean,
            noMoreData: Boolean
    ) {
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

    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                mRefreshLayout.autoRefresh()
            }
        }
    }

    /**
     * 从MultiTypeBinder回调的一些点击事件
     * 此事件已通过Adapter注册
     */
    private fun onBinderClick(view: View, binder: MultiTypeBinder<*>) {
        when (binder) {
            is CommunityPersonFamilyBinder -> {
                when (view.id) {
                    R.id.mCommunityFamilyBtnFl -> {
                        joinBtnClickListener(binder)
                    }
                }
            }
        }
    }

    //家族item按钮的点击事件
    private fun joinBtnClickListener(binder: CommunityPersonFamilyBinder) {
        binder.apply {

        }
    }
}