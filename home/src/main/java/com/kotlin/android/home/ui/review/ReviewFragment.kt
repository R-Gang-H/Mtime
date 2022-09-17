package com.kotlin.android.home.ui.review

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.home.R
import com.kotlin.android.home.databinding.FagReviewBinding
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.review.component.item.adapter.ReviewBinder
import com.kotlin.android.router.liveevent.LOGIN_STATE
import com.kotlin.android.user.afterLogin
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.fag_review.*

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/7
 */
class ReviewFragment : BaseVMFragment<ReviewViewModel, FagReviewBinding>(),
        OnRefreshLoadMoreListener, MultiStateView.MultiStateListener {

    private var mIsInitData: Boolean = false
    private lateinit var mAdapter: MultiTypeAdapter

    override fun initVM(): ReviewViewModel = viewModels<ReviewViewModel>().value

    override fun initView() {
        mAdapter = createMultiTypeAdapter(mReviewRecyclerView)
        mAdapter.setOnClickListener(::onBinderClick)
        mRefreshLayout.setOnRefreshLoadMoreListener(this)
        mMultiStateView.setMultiStateListener(this)
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

        //页面数据
        registerPageUIStateObserve()

        //点赞、取消点赞操作结果监听
        registerPraiseUpObserve()

        //点踩、取消点踩操作结果监听
        registerPraiseDownObserve()
    }

    //页面数据回调监听
    private fun registerPageUIStateObserve() {
        mViewModel?.run {
            uiState.observe(this@ReviewFragment, Observer {
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
    }

    //点赞、取消点赞
    private fun registerPraiseUpObserve() {
        mViewModel?.mCommPraiseUpUIState?.observe(this) {
            it?.apply {
                if (showLoading) {
                    showProgressDialog()
                } else {
                    dismissProgressDialog()
                }

                success?.apply {
                    if (result.isSuccess()) {
                        when (extend) {
                            is ReviewBinder -> {
                                (extend as ReviewBinder).praiseUpChanged()
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

    //点踩、取消点踩操作结果监听
    private fun registerPraiseDownObserve() {
        mViewModel?.mCommPraiseDownUIState?.observe(this) {
            it?.apply {
                if (showLoading) {
                    showProgressDialog()
                } else {
                    dismissProgressDialog()
                }

                success?.apply {
                    if (result.isSuccess()) {
                        when (extend) {
                            is ReviewBinder -> {
                                (extend as ReviewBinder).praiseDownChanged()
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

    /**
     * 从MultiTypeBinder回调的一些点击事件
     * 此事件已通过Adapter注册
     */
    private fun onBinderClick(view: View, binder: MultiTypeBinder<*>) {
        when(binder) {
            is ReviewBinder -> {
                //影评相关的点击事件
                onReviewBinderClickListener(view, binder)
            }
        }
    }

    //影评相关的点击事件
    private fun onReviewBinderClickListener(
            view: View,
            binder: ReviewBinder
    ) {
        when (view.id) {
            R.id.mReviewLikeTv -> {
                //点赞、取消点赞
                onPraiseUpBtnClick(
                        isLike = binder.reviewItem.isLike,
                        objType = CommConstant.PRAISE_OBJ_TYPE_FILM_COMMENT,
                        objId = binder.reviewItem.id,
                        binder = binder
                )
            }
            R.id.mReviewDislikeTv -> {
                //点踩、取消点踩
                onPraiseDownBtnClick(
                        isDislike = binder.reviewItem.isDislike,
                        objType = CommConstant.PRAISE_OBJ_TYPE_FILM_COMMENT,
                        objId = binder.reviewItem.id,
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

    /**
     * 点踩、取消点踩
     */
    private fun onPraiseDownBtnClick(
            isDislike: Boolean,
            objType: Long,
            objId: Long,
            binder: MultiTypeBinder<*>
    ) {
        afterLogin {
            mViewModel?.praiseDown(
                    action = if (isDislike) CommConstant.PRAISE_ACTION_CANCEL else CommConstant.PRAISE_ACTION_SUPPORT,
                    objType = objType,
                    objId = objId,
                    extend = binder
            )
        }
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mViewModel?.loadData(true)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mViewModel?.loadData(false)
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