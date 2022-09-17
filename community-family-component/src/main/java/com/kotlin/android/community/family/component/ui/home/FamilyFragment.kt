package com.kotlin.android.community.family.component.ui.home

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kotlin.android.bonus.scene.component.postJoinFamily
import com.kotlin.android.community.family.component.R
import com.kotlin.android.community.family.component.databinding.FragCommunityFamilyListBinding
import com.kotlin.android.community.family.component.ui.clazz.adapter.FamilyItemBinder
import com.kotlin.android.community.family.component.ui.clazz.bean.FamilyItem
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.liveevent.LOGIN_STATE
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.frag_community_family_list.*
import kotlinx.android.synthetic.main.frag_community_family_list.mMultiStateView
import kotlinx.android.synthetic.main.frag_community_family_list.mRefreshLayout

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/17
 */
class FamilyFragment : BaseVMFragment<FamilyViewModel, FragCommunityFamilyListBinding>(),
        OnRefreshLoadMoreListener, MultiStateView.MultiStateListener {

    override fun initVM(): FamilyViewModel = viewModels<FamilyViewModel>().value

    private lateinit var mAdapter: MultiTypeAdapter
    private var mIsInitData = false

    override fun initView() {
        mRefreshLayout.setOnRefreshLoadMoreListener(this)
        mMultiStateView.setMultiStateListener(this)
        mAdapter = createMultiTypeAdapter(mCommunityFamilyListRv)
        mAdapter.setOnClickListener(::onBinderClick)
    }

    override fun initData() {

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
        //注册加入家族回调监听
        registerJoinFamilyObserve()
        //注册退出家族回调监听
        registerOutFamilyObserve()
    }

    //页面数据回调监听
    private fun registerPageUIStageObserve() {
        mViewModel?.uiState?.observe(this, Observer {
            it.apply {
                success?.apply {
                    showData(this, loadMore, noMoreData)
                }

                if (isEmpty) {
                    if (loadMore) {
                        if (noMoreData) {
                            mRefreshLayout.finishLoadMoreWithNoMoreData()
                        } else {
                            mRefreshLayout.finishLoadMore(false)
                        }
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

    //注册加入家族回调监听
    private fun registerJoinFamilyObserve() {
        mViewModel?.mCommJoinFamilyUISate?.observe(this) {
            it.apply {
                if (showLoading) {
                    showProgressDialog()
                } else {
                    dismissProgressDialog()
                }

                success?.apply {
                    extend.joinChanged(result)
                    postJoinFamily()
                }

                error?.apply {
                    showToast(this)
                }

                netError?.apply {
                    showToast(this)
                }
            }
        }
    }

    //注册退出家族回调监听
    private fun registerOutFamilyObserve() {
        mViewModel?.mCommOutFamilyUISate?.observe(this) {
            it.apply {
                if (showLoading) {
                    showProgressDialog()
                } else {
                    dismissProgressDialog()
                }

                success?.apply {
                    if (result.status == 1L) {
                        extend.outChanged()
                    } else {
                        showToast(result.failMsg)
                    }
                }

                error?.apply {
                    showToast(this)
                }

                netError?.apply {
                    showToast(this)
                }
            }
        }
    }

    override fun destroyView() {

    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mViewModel?.loadData(false)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mViewModel?.loadData(true)
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
            is FamilyItemBinder -> {
                when (view.id) {
                    R.id.mCommunityFamilyBtnFl -> {
                        joinBtnClickListener(binder)
                    }
                }
            }
        }
    }

    //家族item按钮的点击事件
    private fun joinBtnClickListener(binder: FamilyItemBinder) {
        binder.apply {
            if (item.joinType == FamilyItem.JOIN_TYPE_NO_JOIN) {
                //未加入
                mViewModel?.joinFamily(item.id, this)
            } else {
                //已加入
                mViewModel?.outFamily(item.id, this)
            }
        }
    }
}