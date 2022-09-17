package com.kotlin.android.community.ui.selection

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kotlin.android.community.R
import com.kotlin.android.community.card.component.item.adapter.CommunityCardBaseBinder
import com.kotlin.android.community.databinding.FragCommunitySelectionListBinding
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.core.ext.observeLiveData
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.liveevent.LOGIN_STATE
import com.kotlin.android.user.afterLogin
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.frag_community_selection_list.*
import kotlinx.android.synthetic.main.frag_community_selection_list.mMultiStateView
import kotlinx.android.synthetic.main.frag_community_selection_list.mRefreshLayout

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/17
 */
class SelectionFragment: BaseVMFragment<SelectionViewModel, FragCommunitySelectionListBinding>(),
        OnRefreshLoadMoreListener, MultiStateView.MultiStateListener {

    override fun initVM(): SelectionViewModel = viewModels<SelectionViewModel>().value

    private lateinit var mAdapter: MultiTypeAdapter
    private var mIsInitData = false

    override fun initView() {
        mRefreshLayout.setOnRefreshLoadMoreListener(this)
        mMultiStateView.setMultiStateListener(this)
        mAdapter = createMultiTypeAdapter(mCommunitySelectionRv)
        mAdapter.setOnClickListener(::onBinderClick)
    }

    override fun initData() {
    }

    override fun onResume() {
        super.onResume()
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

        //页面数据监听
        registerPageUIStateObserve()

        //点赞、取消点赞
        registerPraiseUpObserve()
    }

    //页面数据监听
    private fun registerPageUIStateObserve() {
        observeLiveData(mViewModel?.uiState) {
            it.apply {
                success?.apply {
                    showData(this, loadMore, noMoreData)
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
        }
    }

    //点赞、取消点赞
    private fun registerPraiseUpObserve() {
        observeLiveData(mViewModel?.mCommPraiseUpUIState) {
            it?.apply {
                if (showLoading) {
                    showProgressDialog()
                } else {
                    dismissProgressDialog()
                }

                success?.apply {
                    if (result.isSuccess()) {
                        when (extend) {
                            is CommunityCardBaseBinder -> {
                                (extend as CommunityCardBaseBinder).praiseUpChanged()
                            }
                        }
                    } else {
                        showToast(result.bizMsg.orEmpty())
                    }
                }

                error?.apply {
                    showToast(error)
                }

                netError?.apply {
                    showToast(netError)
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

    /**
     * 从MultiTypeBinder回调的一些点击事件
     * 此事件已通过Adapter注册
     */
    private fun onBinderClick(view: View, binder: MultiTypeBinder<*>) {
        when(binder) {
            is CommunityCardBaseBinder -> {
                //社区帖子相关点击事件
                onCommunityCardBinderClickListener(view, binder)
            }
        }
    }

    //社区帖子相关点击事件
    private fun onCommunityCardBinderClickListener(
            view: View,
            binder: CommunityCardBaseBinder<*>
    ) {
        when (view.id) {
            R.id.mCommunityCardLikeTv -> {
                //帖子内容的点赞、取消点赞
                onPraiseUpBtnClick(
                        isLike = binder.item.isLike,
                        objType = binder.item.praiseObjType,
                        objId = binder.item.id,
                        binder = binder
                )
            }
        }
    }

    /**
     * 点赞、取消点赞
     */
    private fun onPraiseUpBtnClick(
            isLike: Boolean,
            objType: Long,
            objId: Long,
            binder: MultiTypeBinder<*>
    ) {
        afterLogin {
            mViewModel?.praiseUp(
                    action = if (isLike) CommConstant.PRAISE_ACTION_CANCEL else CommConstant.PRAISE_ACTION_SUPPORT,
                    objType = objType,
                    objId = objId,
                    extend = binder
            )
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
}