package com.kotlin.android.mine.ui.content.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.mine.databinding.FragContentsBinding
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_FILM_COMMENT
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_VIDEO
import com.kotlin.android.app.data.constant.CommConstant.KEY_CONTENT_TYPE
import com.kotlin.android.app.data.constant.CommConstant.MINE_CONTENT_TAB_ALL
import com.kotlin.android.app.data.constant.CommConstant.MINE_CONTENT_TAB_WAIT_REVIEW
import com.kotlin.android.app.data.constant.CommConstant.MINE_CONTENT_TAB_REVIEW_FAIL
import com.kotlin.android.app.data.constant.CommConstant.MINE_CONTENT_TAB_RELEASED
import com.kotlin.android.app.router.provider.publish.IPublishProvider
import com.kotlin.android.app.router.provider.ugc.IUgcProvider
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.core.setTextColorRes
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.mine.BR
import com.kotlin.android.mine.R
import com.kotlin.android.mine.bean.ContentsViewBean
import com.kotlin.android.mine.binder.ContentsBinder
import com.kotlin.android.mine.ui.widgets.dialog.OperationBottomDialog
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView

class ContentsFragment : BaseVMFragment<ContentsViewModel, FragContentsBinding>() {

    companion object {
        fun newInstance(): ContentsFragment = ContentsFragment()
    }

    private var mContentType: Long = 0L
    private var mTabType: Long = MINE_CONTENT_TAB_ALL
    private var mSort: Long = 1L
    private var mAdapter: MultiTypeAdapter? = null
    private var mIsRefresh = true
    private lateinit var deleteBinder: MultiTypeBinder<*>
    private var allIsChecked: Boolean = false
    private var createTimeIsChecked: Boolean = false

    override fun initVM(): ContentsViewModel = viewModels<ContentsViewModel>().value

    override fun initView() {
        mBinding?.apply {
            refreshLayout.setEnableRefresh(false)
            refreshLayout.setOnLoadMoreListener {
                loadContentData()
            }
            allContainer.setBackground(colorRes = R.color.color_f2f3f6, cornerRadius = 14.dpF)
            createTimeContainer.setBackground(
                colorRes = R.color.color_f2f3f6,
                cornerRadius = 14.dpF
            )
            allContainer.setOnClickListener {
                allIsChecked = !allIsChecked
                changeTabState()
                allIma.animate().rotation(if (allIsChecked) 180F else 0F)
            }
            createTimeContainer.setOnClickListener {
                createTimeIsChecked = !createTimeIsChecked
                changeCreateTime()
                createTimeIma.animate().rotation(if (createTimeIsChecked) 180F else 0F)
            }
        }
    }

    private fun loadContentData() {
        mViewModel?.getContent(
            context = activity as Context,
            type = mContentType,
            tab = mTabType,
            sort = mSort,
            isRefresh = mIsRefresh,
            isDrafts = false
        )
    }

    private fun loadComplete() {
        mBinding?.apply {
            refreshLayout?.finishLoadMore()
        }
    }

    override fun startObserve() {
        mViewModel?.contentState?.observe(this) {
            it?.apply {
                success?.run {
                    setContentState(MultiStateView.VIEW_STATE_CONTENT)
                    loadComplete()
                    if (mIsRefresh) {
                        mAdapter?.notifyAdapterDataSetChanged(list)
                        if (list.isEmpty()) {
                            setContentState(MultiStateView.VIEW_STATE_EMPTY)
                        }
                    } else {
                        mAdapter?.notifyAdapterAdded(list)
                    }
                    mBinding?.apply {
                        refreshLayout.setNoMoreData(noMoreData)
                        allContainer.visible()
                        createTimeContainer.visible()
                    }
                    if (mIsRefresh) mIsRefresh = false
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
        getTypeCount()
        deleteContentObserve()
        reviewContentObserve()
    }

    private fun setContentState(@MultiStateView.ViewState state: Int) {
        mBinding?.stateView?.setViewState(state)
    }

    override fun initData() {
        mContentType = arguments?.getLong(KEY_CONTENT_TYPE) ?: 0L
        loadContentData()
        mBinding?.apply {
            setVariable(BR.viewModel, mViewModel)
            mAdapter =
                createMultiTypeAdapter(mBinding?.contentRv!!, LinearLayoutManager(context)).apply {
                    setOnClickListener { view, binder ->
                        when (view.id) {
                            R.id.moreRl -> {
                                OperationBottomDialog(
                                    false,
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
                                    revokeLlCallBack = {
                                        mViewModel?.reviewContent(
                                            binder.bean.id ?: 0L,
                                            mContentType
                                        )
                                    },
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
                            R.id.itemContainer -> {
                                if ((binder as ContentsBinder).bean.creatorContentStatus == ContentsViewBean.CREATOR_CONTENT_STATUS_RELEASED) {
                                    getProvider(IUgcProvider::class.java) {
                                        launchDetail(
                                            contentId = binder.bean.id ?: 0L,
                                            type = mContentType
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            stateView.setMultiStateListener(object : MultiStateView.MultiStateListener {
                override fun onMultiStateChanged(viewState: Int) {
                    if (viewState == MultiStateView.VIEW_STATE_NO_NET || viewState == MultiStateView.VIEW_STATE_ERROR) {
                        loadContentData()
                    }
                }

            })
            allStateTv.setOnClickListener {
                mTabType = MINE_CONTENT_TAB_ALL
                mIsRefresh = true
                loadContentData()
                resetStateColor()
                allStateTv.setTextColorRes(R.color.color_20a0da)
            }
            passedStateTv.setOnClickListener {
                mTabType = MINE_CONTENT_TAB_RELEASED
                mIsRefresh = true
                loadContentData()
                resetStateColor()
                passedStateTv.setTextColorRes(R.color.color_20a0da)
            }
            reviewedStateTv.setOnClickListener {
                mTabType = MINE_CONTENT_TAB_WAIT_REVIEW
                mIsRefresh = true
                loadContentData()
                resetStateColor()
                reviewedStateTv.setTextColorRes(R.color.color_20a0da)
            }
            failStateTv.setOnClickListener {
                mTabType = MINE_CONTENT_TAB_REVIEW_FAIL
                mIsRefresh = true
                loadContentData()
                resetStateColor()
                failStateTv.setTextColorRes(R.color.color_20a0da)
            }
            positiveSequence.setOnClickListener {
                positiveSequence.setTextColorRes(R.color.color_20a0da)
                invertedOrder.setTextColorRes(R.color.color_8798af)
                createTimeLl.gone()
                createTimeIma.animate().rotation(0F)
                createTimeIsChecked = false
                mSort = 8L
                mIsRefresh = true
                loadContentData()
            }
            invertedOrder.setOnClickListener {
                invertedOrder.setTextColorRes(R.color.color_20a0da)
                positiveSequence.setTextColorRes(R.color.color_8798af)
                createTimeLl.gone()
                createTimeIma.animate().rotation(0F)
                createTimeIsChecked = false
                mSort = 1L
                mIsRefresh = true
                loadContentData()
            }
            stateLl.setOnClickListener {
                stateLl.gone()
                allIma.animate().rotation(0F)
                allIsChecked = false
            }
            createTimeLl.setOnClickListener {
                createTimeLl.gone()
                createTimeIma.animate().rotation(0F)
                createTimeIsChecked = false
            }
        }
    }

    fun changeTabState() {
        mBinding?.apply {
            createTimeLl.gone()
            createTimeIma.animate().rotation(0F)
            createTimeIsChecked = false
        }
        mContentType = arguments?.getLong(KEY_CONTENT_TYPE) ?: 0L
        mViewModel?.getTypeCount(mContentType)
    }

    private fun changeCreateTime() {
        mBinding?.apply {
            stateLl.gone()
            allIma.animate().rotation(0F)
            allIsChecked = false
            createTimeLl.visibility =
                if (createTimeLl.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
    }

    private fun resetStateColor() {
        mBinding?.apply {
            allStateTv.setTextColorRes(R.color.color_8798af)
            passedStateTv.setTextColorRes(R.color.color_8798af)
            reviewedStateTv.setTextColorRes(R.color.color_8798af)
            failStateTv.setTextColorRes(R.color.color_8798af)
            stateLl.gone()
            allIma.animate().rotation(0F)
            allIsChecked = false
        }
    }

    @SuppressLint("StringFormatMatches")
    private fun getTypeCount() {
        mViewModel?.typeCountState?.observe(this) {
            it?.apply {
                success?.run {
                    mBinding?.apply {
                        stateLl.visibility =
                            if (stateLl.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                        allStateTv.text = getString(R.string.mine_content_all_num, "$allCount")
                        passedStateTv.text =
                            getString(R.string.mine_content_state_passed, "$releasedCount")
                        reviewedStateTv.text =
                            getString(R.string.mine_content_state_reviewed, "$waitReviewCount")
                        failStateTv.text =
                            getString(R.string.mine_content_state_fail, "$reviewFailCount")
                    }
                }
                netError?.run {

                }
                error?.run {

                }
            }
        }
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

    private fun reviewContentObserve() {
        mViewModel?.reviewContentState?.observe(this) {
            it?.apply {
                success?.run {
                    mIsRefresh = true
                    loadContentData()
                }
                netError?.run {

                }
                error?.run {

                }
            }
        }
    }
}