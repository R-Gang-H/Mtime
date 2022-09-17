package com.kotlin.android.review.component.item.ui.movie

import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.common.CommBizCodeResult
import com.kotlin.android.app.data.entity.review.MovieReview
import com.kotlin.android.app.data.entity.review.MovieReviewList
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.review.component.databinding.ActMovieReviewListBinding
import com.kotlin.android.review.component.databinding.ItemMovieReviewBinding
import com.kotlin.android.review.component.item.ui.movie.adapter.MovieReviewItemBinder
import com.kotlin.android.review.component.item.ui.movie.constant.MovieReviewConstant
import com.kotlin.android.router.liveevent.LOGIN_STATE
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.titlebar.CommonTitleBar
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.act_movie_review_list.*
import kotlin.collections.ArrayList

/**
 * @author vivian.wei
 * @date 2020/12/28
 * @desc 影片长影评列表页
 */
@Route(path = RouterActivityPath.Review.PAGE_MOVIE_REVIEW_LIST_ACTIVITY)
class MovieReviewListActivity : BaseVMActivity<MovieReviewListViewModel, ActMovieReviewListBinding>(),
        MultiStateView.MultiStateListener, OnLoadMoreListener, OnRefreshListener,
        MovieReviewItemBinder.IMovieReviewActionCallBack
{

    private var mMovieId: String = ""
    private var mTitle: String = ""
    private var mPageIndex = 1
    private var mListData: ArrayList<MultiTypeBinder<ItemMovieReviewBinding>> = ArrayList()
    private lateinit var mAdapter: MultiTypeAdapter
    private var totalCount = 0L
    // 点赞点踩操作
    private var mIsPraiseUp = true  // true赞   false踩
    private var mPosition = -1
    private var mAction = CommConstant.PRAISE_ACTION_SUPPORT
    private var mReview: MovieReview ?= null

    override fun initVM(): MovieReviewListViewModel = viewModels<MovieReviewListViewModel>().value

    override fun initVariable() {
        super.initVariable()

        intent?.let {
            mMovieId = it.getStringExtra(MovieReviewConstant.KEY_MOVIE_ID) ?: ""
            mTitle = it.getStringExtra(MovieReviewConstant.KEY_MOVIE_TITLE) ?: ""
        }
    }

    override fun initCommonTitleView() {
        // title
        CommonTitleBar().init(this)
                .setTitle(mTitle)
                .create()
    }

    override fun initView() {
        mAdapter = createMultiTypeAdapter(mMovieReviewListRv, LinearLayoutManager(this))
        // 事件
        initEvent()
    }

    private fun initEvent() {
        mRefreshLayout.setOnRefreshListener(this)
        mRefreshLayout.setOnLoadMoreListener(this)
        mMultiStateView.setMultiStateListener(this)
    }

    override fun initData() {
        if (mMovieId.isNotEmpty()) {
            if(mPageIndex == 1) {
                showProgressDialog()
            }
            // 获取影片长影评列表
            mViewModel?.getReviewList(mMovieId, mPageIndex)
        }
    }

    override fun startObserve() {
        // observe长影评列表
        observeReviewList()
        // observe赞/取消赞
        observePraiseUp()
        // observe踩/取消踩
        observePraiseDown()
        // 登录成功后回来刷新页面
        observeLoginEvent()
    }

    /**
     * observe长影评列表
     */
    private fun observeReviewList() {
        mViewModel?.mReviewListUiState?.observe(this) {
            it.apply {
                dismissProgressDialog()

                if(mRefreshLayout.isRefreshing) {
                    mRefreshLayout.finishRefresh(true)
                }

                success?.apply {
                    showReviewList(this)

                    mRefreshLayout.finishLoadMore()
                    if(this.hasMore == true) {
                        mPageIndex++
                    } else {
                        /*
                         * mRefreshLayout.finishLoadMoreWithNoMoreData()
                         * 没有拖动item时，页脚的提示语会悬浮在item上面
                         */
                        mRefreshLayout.setNoMoreData(true)
                    }
                }

                if(isEmpty) {
                    queryError(MultiStateView.VIEW_STATE_EMPTY)
                }

                error?.apply {
                    queryError(MultiStateView.VIEW_STATE_ERROR)
                }

                netError?.apply {
                    queryError(MultiStateView.VIEW_STATE_NO_NET)
                }
            }
        }
    }

    /**
     * observe赞/取消赞
     */
    private fun observePraiseUp() {
        mViewModel?.mPraiseUpUiState?.observe(this) {
            it.apply {
                showOrHideProgressDialog(showLoading)

                success?.apply {
                    updatePraiseState(this)
                }

                error?.apply {

                }

                netError?.apply {

                }
            }
        }
    }

    /**
     * observe踩/取消踩
     */
    private fun observePraiseDown() {
        mViewModel?.mPraiseDownUiState?.observe(this) {
            it.apply {
                showOrHideProgressDialog(showLoading)

                success?.apply {
                    updatePraiseState(this)
                }

                error?.apply {

                }

                netError?.apply {

                }
            }
        }
    }

    /**
     * 登录成功后回来刷新页面
     */
    private fun observeLoginEvent() {
        LiveEventBus.get(LOGIN_STATE, com.kotlin.android.app.router.liveevent.event.LoginState::class.java).observe(this) {
            if (it?.isLogin == true) {
                //登录成功
                mRefreshLayout.autoRefresh()
            }
        }
    }

    /**
     * 接口错误处理
     */
    private fun queryError(viewState: Int) {
        mRefreshLayout.finishLoadMore()
        if(mPageIndex == 1) {
            mMultiStateView.setViewState(viewState)
        }
    }

    /**
     * 点击页面错误状态"图标/按钮"后处理事件
     */
    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_EMPTY,
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                refreshData(true)
            }
        }
    }

    /**
     * 下拉刷新
     */
    override fun onRefresh(refreshLayout: RefreshLayout) {
        refreshData(false)
    }

    /**
     * 刷新页面数据
     */
    private fun refreshData(showLoading: Boolean) {
        mPageIndex = 1
        mListData.clear()
        mAdapter.notifyAdapterClear()
        if(showLoading) {
            showProgressDialog()
        }
        // 获取影片长影评列表
        mViewModel?.getReviewList(mMovieId, mPageIndex)
    }

    /**
     * 加载更多
     */
    override fun onLoadMore(refreshLayout: RefreshLayout){
        // 获取精选片单列表
        mViewModel?.getReviewList(mMovieId, mPageIndex)
    }

    /**
     * 显示长影评列表
     */
    private fun showReviewList(reviewList: MovieReviewList) {
        reviewList?.let {
            var list = ArrayList<MovieReview>()
            if(mPageIndex == 1) {
                mListData.clear()
                mAdapter.notifyAdapterClear()
                totalCount = it.count ?: 0

                // 待审核长影评放最前面(只显示1条）
                var auditReview = getFirstAuditReview(it.auditingList)
                auditReview?.let { auditReview ->
                    auditReview.isAudit = true
                    list.add(auditReview)
                    totalCount += 1
                }
            }
            it.list?.let { reviews ->
                list.addAll(reviews)
            }
            list?.map{ review ->
                var binder = MovieReviewItemBinder(review, true, totalCount)
                binder.mIMovieReviewActionCallBack = this
                mListData.add(binder)
            }
            if(mListData.isNotEmpty()) {
                mAdapter.notifyAdapterAdded(mListData)
            } else {
                mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY)
            }
        }

    }

    /**
     * 第1条待审核长影评
     */
    private fun getFirstAuditReview(auditingList: List<MovieReview>?): MovieReview? {
        auditingList?.let {
            it?.let { auditingList ->
                return auditingList.firstOrNull()
            }
        }
        return null
    }

    /**
     * 赞/取消赞(踩/取消踩)
     */
    override fun praiseCallBack(bean: MovieReview, position: Int, isPraiseUp: Boolean) {
        bean.commentId?.let {
            mIsPraiseUp = isPraiseUp
            mReview = bean
            mPosition = position
            if(mIsPraiseUp) {
                // 赞/取消赞
                mAction = if (bean.isPraise == true) CommConstant.PRAISE_ACTION_CANCEL
                            else CommConstant.PRAISE_ACTION_SUPPORT
                mViewModel?.praiseUp(
                        mAction,
                        CommConstant.PRAISE_OBJ_TYPE_FILM_COMMENT,
                        it
                )
            } else {
                // 踩/取消踩
                mAction = if (bean.isPraiseDown == true) CommConstant.PRAISE_ACTION_CANCEL
                            else CommConstant.PRAISE_ACTION_SUPPORT
                mViewModel?.praiseDown(
                        mAction,
                        CommConstant.PRAISE_OBJ_TYPE_FILM_COMMENT,
                        it
                )
            }
        }
    }

    /**
     * 更新赞/踩状态和数
     */
    private fun updatePraiseState(result: CommBizCodeResult) {
        if(result.isSuccess()) {
            mReview?.let {
                if (mAction == CommConstant.PRAISE_ACTION_SUPPORT) {
                    // 点赞与点踩互斥
                    if(mIsPraiseUp) {
                        // 点赞
                        it.praiseCount += 1L
                        it.isPraise = true
                        if(it.isPraiseDown == true){
                            // 取消点踩状态
                            cancelPraiseDownState(it)
                        }
                    }
                    else {
                        // 点踩
                        it.praiseDownCount += 1L
                        it.isPraiseDown = true
                        if(it.isPraise == true) {
                            // 取消点赞状态
                            cancelPraiseUpState(it)
                        }
                    }
                } else if(mAction == CommConstant.PRAISE_ACTION_CANCEL) {
                    if(mIsPraiseUp) {
                        // 取消点赞状态
                        cancelPraiseUpState(it)
                    } else {
                        // 取消点踩状态
                        cancelPraiseDownState(it)
                    }
                }
                var binder = MovieReviewItemBinder(it, true, totalCount)
                binder.mIMovieReviewActionCallBack = this
                mListData[mPosition] = binder
                mAdapter.notifyItemChanged(mPosition)
            }
        } else {
            showToast(result.bizMsg)
        }
    }

    /**
     * 取消赞状态
     */
    private fun cancelPraiseUpState(review: MovieReview) {
        review.isPraise = false
        review.praiseCount -= 1L
        if (review.praiseCount < 0L) {
            review.praiseCount = 0L
        }
    }

    /**
     * 取消踩状态
     */
    private fun cancelPraiseDownState(review: MovieReview) {
        review.isPraiseDown = false
        review.praiseDownCount -= 1L
        if (review.praiseDownCount < 0L) {
            review.praiseDownCount = 0L
        }
    }

}