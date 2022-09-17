package com.kotlin.android.community.ui.person.center

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kotlin.android.community.R
import com.kotlin.android.community.card.component.item.adapter.CommunityCardBaseBinder
import com.kotlin.android.community.databinding.FragCommunityPersonContentListBinding
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.community.ui.person.KEY_TYPE
import com.kotlin.android.community.ui.person.KEY_USER_ID
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.liveevent.LOGIN_STATE
import com.kotlin.android.user.afterLogin
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.ogaclejapan.smarttablayout.utils.v4.Bundler
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.frag_community_person_content_list.*

/**
 * @author WangWei
 * @date 2020/9/23
 * 社区个人主页： 内容 影评 日志 帖子 容器
 */
class CommunityPersonContentFragment : BaseVMFragment<ContentViewModel, FragCommunityPersonContentListBinding>(),
        OnRefreshLoadMoreListener, MultiStateView.MultiStateListener {

    override fun initVM(): ContentViewModel = viewModels<ContentViewModel>().value

    private lateinit var mAdapter: MultiTypeAdapter
    private var mIsInitData = false

    fun newInstance(userId: Long, type: Long): CommunityPersonContentFragment? {
        val f = CommunityPersonContentFragment()
        f.arguments = bundler(userId, type)
        return f
    }

    fun bundler(userId: Long, type: Long): Bundle {
        return Bundler()
                .putLong(KEY_USER_ID, userId)
                .putLong(KEY_TYPE, type)
                .get()
    }

    override fun initView() {
//        mRefreshLayout.setEnableRefresh(false)
        mRefreshLayout.setOnRefreshLoadMoreListener(this)
        mMultiStateView.setMultiStateListener(this)
        mAdapter = createMultiTypeAdapter(mRecyclerView)
        mAdapter.setOnClickListener(::onBinderClick)
    }

    var userId: Long = 0L
    var type: Long = 0L
    override fun initData() {
        if (arguments != null) {
            type = arguments?.getLong("type", 0)!!
            userId = arguments?.getLong("user_id", 0)!!
        }
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
        mViewModel?.uiState?.observe(this, Observer {
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
        })
    }

    //点赞、取消点赞
    private fun registerPraiseUpObserve() {
        mViewModel?.mCommPraiseUpUIState?.observe(this) {
            it?.apply {
                showOrHideProgressDialog(showLoading)
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
        mViewModel?.loadData(false, type, userId)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mViewModel?.loadData(true, type, userId)
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
        when (binder) {
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