package com.kotlin.android.mine.ui.drafts.fragment

import android.content.Context
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.mine.BR
import com.kotlin.android.mine.R
import com.kotlin.android.app.data.constant.CommConstant.MINE_CONTENT_TAB_DRAFTS
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_FILM_COMMENT
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_VIDEO
import com.kotlin.android.app.router.provider.publish.IPublishProvider
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.mine.binder.ContentsBinder
import com.kotlin.android.mine.databinding.FragMyDraftsBinding
import com.kotlin.android.mine.ui.content.fragment.ContentsViewModel
import com.kotlin.android.mine.ui.widgets.dialog.OperationBottomDialog
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView

class MyDraftsFragment : BaseVMFragment<ContentsViewModel, FragMyDraftsBinding>() {

    companion object {
        fun newInstance(): MyDraftsFragment = MyDraftsFragment()
    }

    private var mContentType: Long = 0L
    private var mAdapter: MultiTypeAdapter? = null
    private var mIsRefresh = true
    private lateinit var deleteBinder: MultiTypeBinder<*>

    override fun initVM(): ContentsViewModel = viewModels<ContentsViewModel>().value

    override fun initView() {
        mBinding?.apply {
            refreshLayout?.setOnLoadMoreListener {
                mIsRefresh = false
                loadContentData()
            }
            refreshLayout?.setOnRefreshListener {
                mIsRefresh = true
                loadContentData()
            }
        }
    }

    private fun loadContentData() {
        mViewModel?.getContent(
            context = activity as Context,
            type = mContentType,
            tab = MINE_CONTENT_TAB_DRAFTS,
            sort = 1L,
            isRefresh = mIsRefresh,
            isDrafts = true
        )
    }

    private fun loadComplete() {
        mBinding?.apply {
            refreshLayout?.finishRefresh()
            refreshLayout?.finishLoadMore()
        }
    }

    override fun startObserve() {
        mViewModel?.draftsState?.observe(this) {
            it?.apply {
                success?.run {
                    setContentState(MultiStateView.VIEW_STATE_CONTENT)
                    loadComplete()
                    if (isRefresh) {
                        mAdapter?.notifyAdapterDataSetChanged(list)
                        if (list.isEmpty()) {
                            setContentState(MultiStateView.VIEW_STATE_EMPTY)
                        }
                    } else {
                        mAdapter?.notifyAdapterAdded(list)
                    }
                    mBinding?.refreshLayout?.setNoMoreData(noMoreData)
                }
                netError?.run {
                    loadComplete()
                    if (mAdapter?.itemCount == 0) {//如果没有数据是显示加载失败重试页面
                        setContentState(MultiStateView.VIEW_STATE_NO_NET)
                    }
                }
                error?.run {
                    loadComplete()
                    if (mAdapter?.itemCount == 0) {//如果页面上没有显示数据，需要显示加载失败重试页面
                        setContentState(MultiStateView.VIEW_STATE_ERROR)
                    }
                }
            }
        }
        deleteContentObserve()
    }

    private fun setContentState(@MultiStateView.ViewState state: Int) {
        mBinding?.stateView?.setViewState(state)
    }

    override fun onResume() {
        super.onResume()
        mBinding?.refreshLayout?.autoRefresh()
    }

    override fun initData() {
        mContentType = arguments?.getLong(CommConstant.KEY_CONTENT_TYPE) ?: 0L
        mBinding?.setVariable(BR.viewModel, mViewModel)
        mAdapter =
            createMultiTypeAdapter(mBinding?.contentRv!!, LinearLayoutManager(context)).apply {
                setOnClickListener { view, binder ->
                    when (view.id) {
                        R.id.moreRl -> {
                            OperationBottomDialog(
                                true,
                                (binder as ContentsBinder).bean.creatorContentStatus,
                                mContentType,
                                editCallBack = {
                                    if (mContentType == CONTENT_TYPE_VIDEO) {
                                        getProvider(IPublishProvider::class.java) {
                                            startVideoPublishActivity(
                                                contentId = binder.bean.id.orZero(),
                                                recId = binder.bean.item.creatorAuthority?.btnEdit?.recId
                                                    ?: 0L
                                            )
                                        }
                                    } else {
                                        val nameCN = binder.bean.item.fcMovie?.name
                                        val nameEN = binder.bean.item.fcMovie?.nameEn
                                        val name = if (nameCN.isNullOrEmpty()) {
                                            nameEN
                                        } else {
                                            nameCN
                                        }
                                        getProvider(IPublishProvider::class.java)
                                            ?.startEditorActivity(
                                                type = mContentType,
                                                contentId = binder.bean.item.creatorAuthority?.btnEdit?.contentId,
                                                recordId = binder.bean.item.creatorAuthority?.btnEdit?.recId,
                                                movieId = binder.bean.item.fcMovie?.id,
                                                movieName = name,
                                                isLongComment = mContentType == CONTENT_TYPE_FILM_COMMENT
                                            )
                                    }
                                },
                                revokeLlCallBack = {},
                                deleteCallBack = {
                                    deleteBinder = binder
                                    mViewModel?.deleteContent(
                                        binder.bean.id,
                                        mContentType
                                    )
                                }).show(
                                childFragmentManager,
                                OperationBottomDialog.TAG_OPERATION_BOTTOM_DIALOG_FRAGMENT
                            )
                        }
                    }
                }
            }
        mBinding?.stateView?.setMultiStateListener(object : MultiStateView.MultiStateListener {
            override fun onMultiStateChanged(viewState: Int) {
                if (viewState == MultiStateView.VIEW_STATE_NO_NET || viewState == MultiStateView.VIEW_STATE_ERROR) {
                    loadContentData()
                }
            }

        })
    }

    private fun deleteContentObserve() {
        mViewModel?.deleteContentState?.observe(this) {
            it?.apply {
                success?.run {
                    mIsRefresh = true
                    mAdapter?.notifyAdapterRemoved(deleteBinder)
                }
                netError?.run {

                }
                error?.run {

                }
            }
        }
    }
}