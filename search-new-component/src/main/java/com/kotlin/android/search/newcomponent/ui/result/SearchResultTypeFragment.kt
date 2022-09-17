package com.kotlin.android.search.newcomponent.ui.result

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kotlin.android.bonus.scene.component.postJoinFamily
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.core.ext.getSpValue
import com.kotlin.android.app.data.annotation.SEARCH_ALL
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.mtime.ktx.GlobalDimensionExt
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.ext.afterLogin
import com.kotlin.android.router.liveevent.LOGIN_STATE
import com.kotlin.android.router.liveevent.SEARCH_RESULT_TAB_ALL_REFRESH
import com.kotlin.android.router.liveevent.SEARCH_RESULT_TYPE_TAB
import com.kotlin.android.search.newcomponent.R
import com.kotlin.android.search.newcomponent.databinding.FragSearchResultTypeBinding
import com.kotlin.android.search.newcomponent.ui.result.SearchResultConstant.Companion.SEARCH_SORT_COMPREHENSIVE
import com.kotlin.android.search.newcomponent.ui.result.SearchResultConstant.Companion.SP_UNION_SEARCH_KEYWORD
import com.kotlin.android.search.newcomponent.ui.result.adapter.*
import com.kotlin.android.search.newcomponent.ui.result.bean.FamilyItem
import com.kotlin.android.search.newcomponent.ui.result.bean.UserItem
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener

/**
 * 搜索结果页分类Fragment
 */
class SearchResultTypeFragment :
    BaseVMFragment<SearchResultViewModel, FragSearchResultTypeBinding>(),
    OnRefreshLoadMoreListener, MultiStateView.MultiStateListener {

    private val mCityId = GlobalDimensionExt.getDigitsCurrentCityId()
    private val mLongitude = GlobalDimensionExt.getLongitude()
    private val mLatitude = GlobalDimensionExt.getLatitude()

    private var mSearchType = SEARCH_ALL
    private var mSearchKey = ""
    private var mSortType: Long? = null
    private var mAdapter: MultiTypeAdapter? = null
    /**
     * 是否正在自动刷新状态
     * 备注：
     * 这里的刷新有个问题：如果当前是日志tab，重新搜索后，在切换到内容tab，会先调日志的onResume，
     * 但是没有调日志的onRefresh，而是直接调文章的onResume，从文章切换到日志时，不会再走onResume，
     * 导致日志内容显示不出来（可能是刷新组件里面的问题），所以加isAutoRefreshing临时处理一下这个问题
     */
    private var isAutoRefreshing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 搜索类型
        arguments?.let { bundle ->
            mSearchType = bundle.getLong(SearchResultConstant.KEY_UNION_SEARCH_TYPE, SEARCH_ALL)
        }
    }

    override fun initView() {
        mBinding?.mSearchResultTypeRv?.let {
            mAdapter = createMultiTypeAdapter(it)
            mAdapter?.setOnClickListener(::onBinderClick)
        }

        if(mSearchType == SEARCH_ALL) {
            mBinding?.mRefreshLayout?.setEnableLoadMore(false)
            mBinding?.mRefreshLayout?.setOnRefreshListener(this)
        } else {
            mBinding?.mRefreshLayout?.setOnRefreshLoadMoreListener(this)
        }

        mBinding?.mMultiStateView?.setMultiStateListener(this)

    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()

        val searchKey = getSpValue(SP_UNION_SEARCH_KEYWORD, "")
        // 搜索关键词变化，则重新搜索，加载第一页数据
        if (searchKey.isNotEmpty() && mSearchKey != searchKey || isAutoRefreshing) {
            mSearchKey = searchKey
            mSortType = SEARCH_SORT_COMPREHENSIVE
            // 加载第一页数据
            if(!isAutoRefreshing) {
                showProgressDialog()
            }
            loadData()
        }
    }

    /**
     * 加载第一页数据
     */
    private fun loadData() {
        mAdapter?.notifyAdapterClear()
        mBinding?.mFragSearchResultTypeEmptyLayout?.isVisible = false
        mBinding?.mRefreshLayout?.autoRefresh()
        isAutoRefreshing = true
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        isAutoRefreshing = false
        // 加载第一页数据
        if(mSearchType == SEARCH_ALL) {
            // 按全部搜索
            mViewModel?.unionSearchByAll(mSearchKey, mCityId, mLongitude, mLatitude)
        } else {
            // 按具体分类搜索
            searchByType(isLoadMore = false)
        }
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        // 按具体分类搜索
        searchByType(isLoadMore = true)
    }

    /**
     * 按具体分类搜索
     */
    private fun searchByType(isLoadMore: Boolean) {
        mViewModel?.unionSearchByType(
                keyword = mSearchKey,
                searchType = mSearchType,
                locationId = mCityId,
                longitude = mLongitude,
                latitude = mLatitude,
                isLoadMore = isLoadMore,
                sortType = mSortType
        )
    }

    override fun onMultiStateChanged(@MultiStateView.ViewState viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                // 加载第一页数据
                showProgressDialog()
                mBinding?.mRefreshLayout?.autoRefresh()
            }
            else -> {
            }
        }
    }

    override fun startObserve() {
        // 页面数据回调
        if(mSearchType == SEARCH_ALL) {
            allUIStateObserve()
        } else {
            typeUIStateObserve()
        }

        // 关注、取消关注操作结果监听
        followObserve()

        // 加入家族回调监听
        joinFamilyObserve()

        // 退出家族回调监听
        outFamilyObserve()

        // 登录结果监听
        LiveEventBus.get(LOGIN_STATE, com.kotlin.android.app.router.liveevent.event.LoginState::class.java).observe(this) {
            // 加载第一页数据
            loadData()
        }

        // 如果当前页为全部Tab，重新搜索，则不会调SearchResultTypeFragment的onResume，需要手动刷新页面
        LiveEventBus.get(SEARCH_RESULT_TAB_ALL_REFRESH, com.kotlin.android.app.router.liveevent.event.SearchResultTabAllRefreshState::class.java)
            .observe(this) {
                if(mSearchType == SEARCH_ALL) {
                    val searchKey = getSpValue(SP_UNION_SEARCH_KEYWORD, "")
                    mSearchKey = searchKey
                    // 加载第一页数据
                    showProgressDialog()
                    loadData()
                }
            }

        // 点赞、取消点赞操作结果监听
//        likeUpObserve()

        // 点踩、取消点踩操作结果监听
//        likeDownObserve()
    }

    /**
     * 全部tab_页面数据回调监听
     */
    private fun allUIStateObserve() {
        mViewModel?.run {
            searchResultAllUIState.observe(this@SearchResultTypeFragment) {
                it.apply {
                    dismissProgressDialog()

                    success?.apply {
                        showAllData(this)
                    }

                    if (isEmpty) {
                        mBinding?.mRefreshLayout?.finishRefresh()
                        mBinding?.mFragSearchResultTypeEmptyLayout?.isVisible = true
                    }

                    error?.apply {
                        mBinding?.mRefreshLayout?.finishRefresh()
                        mBinding?.mMultiStateView?.setViewState(MultiStateView.VIEW_STATE_ERROR)
                    }

                    netError?.apply {
                        mBinding?.mRefreshLayout?.finishRefresh()
                        mBinding?.mMultiStateView?.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                    }
                }
            }
        }
    }

    /**
     * 全部tab_显示数据
     */
    private fun showAllData(data: List<MultiTypeBinder<*>>) {
        mBinding?.mRefreshLayout?.finishRefresh()
        mAdapter?.notifyAdapterDataSetChanged(data)
    }

    /**
     * 分类tab_页面数据回调监听
     */
    private fun typeUIStateObserve() {
        mViewModel?.run {
            searchResultTypeUIState.observe(this@SearchResultTypeFragment) {
                it.apply {
                    dismissProgressDialog()

                    success?.apply {
                        showTypeData(this, loadMore, noMoreData)
                    }

                    if (isEmpty) {
                        setEmptyOrErrorState(loadMore, MultiStateView.VIEW_STATE_EMPTY)
                    }

                    error?.apply {
                        setEmptyOrErrorState(loadMore, MultiStateView.VIEW_STATE_ERROR)
                    }

                    netError?.apply {
                        setEmptyOrErrorState(loadMore, MultiStateView.VIEW_STATE_NO_NET)
                    }
                }
            }
        }
    }

    /**
     * 分类tab_设置空或错误显示状态
     */
    private fun setEmptyOrErrorState(loadMore: Boolean, @MultiStateView.ViewState state: Int) {
        when {
            loadMore -> {
                mBinding?.mRefreshLayout?.finishLoadMore(false)
            }
            mBinding?.mRefreshLayout?.isRefreshing == true -> {
                // 刷新状态下
                mBinding?.mRefreshLayout?.finishRefreshWithNoMoreData()
                if(state == MultiStateView.VIEW_STATE_EMPTY) {
                    mBinding?.mFragSearchResultTypeEmptyLayout?.isVisible = true
                } else {
                    mBinding?.mMultiStateView?.setViewState(state)
                }
            }
            else -> {
            }
        }
    }

    /**
     * 分类tab_显示数据
     */
    private fun showTypeData(data: List<MultiTypeBinder<*>>, isLoadMore: Boolean, noMoreData: Boolean) {
        if (!isLoadMore) {
            // 第一页
            mAdapter?.notifyAdapterDataSetChanged(data)
            // 刷新组件
            if (noMoreData) {
                mBinding?.mRefreshLayout?.finishRefreshWithNoMoreData()
            } else {
                mBinding?.mRefreshLayout?.finishRefresh()
            }
        } else {
            // 第N页
            mAdapter?.notifyAdapterAdded(data)
            // 加载更多组件
            if (noMoreData) {
                mBinding?.mRefreshLayout?.finishLoadMoreWithNoMoreData()
            } else {
                mBinding?.mRefreshLayout?.finishLoadMore()
            }
        }
    }

    /**
     * 关注、取消关注操作结果监听
     */
    private fun followObserve() {
        mViewModel?.followState?.observe(this) {
            it?.apply {
                showOrHideProgressDialog(showLoading)

                success?.apply {
                    if (result.isSuccess()) {
                        when (extend) {
                            // 影评
                            is SearchResultUserItemBinder -> {
                                (extend as SearchResultUserItemBinder).followChanged()
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

    /**
     * 加入家族回调监听
     */
    private fun joinFamilyObserve() {
        mViewModel?.joinFamilyUISate?.observe(this) {
            it.apply {
                showOrHideProgressDialog(showLoading)

                success?.apply {
                    if(this.result.status == 1L) {
                        (extend as SearchResultFamilyItemBinder).joinChanged(result)
                        postJoinFamily()
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

    /**
     * 退出家族回调监听
     */
    private fun outFamilyObserve() {
        mViewModel?.outFamilyUISate?.observe(this) {
            it.apply {
                showOrHideProgressDialog(showLoading)

                success?.apply {
                    if (result.status == 1L) {
                        (extend as SearchResultFamilyItemBinder).outChanged()
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

    /**
     * 从MultiTypeBinder回调的一些点击事件
     * 此事件已通过Adapter注册
     */
    private fun onBinderClick(view: View, binder: MultiTypeBinder<*>) {
        when(binder) {
            is SearchResultUserItemBinder -> {
                onUserBinderClickListener(view, binder)
            }
            is SearchResultFamilyItemBinder -> {
                onFamilyBinderClickListener(view, binder)
            }
            is SearchResultAllTypeCountBinder -> {
                onTypeCountBinderClickListener(view, binder)
            }
//            is SearchResultFilmCommentItemBinder -> {
//                // 影评相关点击事件
//                onFilmCommentBinderClickListener(view, binder)
//            }
//            is SearchResultPostItemBinder -> {
//                // 帖子、日志相关点击事件
//                onPostBinderClickListener(view, binder)
//            }
        }
    }

    /**
     * 用户相关点击事件
     */
    private fun onUserBinderClickListener(view: View, binder: SearchResultUserItemBinder) {
        when (view.id) {
            R.id.mItemSearchResultUserFollowLayout -> {
                // 关注、取消关注
                afterLogin {
                    mViewModel?.follow(
                        action = if (binder.bean.isFocus == UserItem.FOLLOW)
                            CommConstant.FOLLOW_ACTION_CANCEL else CommConstant.FOLLOW_ACTION_SUPPORT,
                        userId = binder.bean.userId,
                        extend = binder
                    )
                }
            }
        }
    }

    /**
     * 家族相关点击事件
     */
    private fun onFamilyBinderClickListener(view: View, binder: SearchResultFamilyItemBinder) {
        when (view.id) {
            R.id.mItemSearchResultFamilyJoinLayout -> {
                afterLogin {
                    // "加入"按钮操作
                    if (binder.bean.isJoin == FamilyItem.JOIN_STATUS_NOT) {
                        // 加入群组
                        mViewModel?.joinFamily(binder.bean.familyId, binder)
                    } else {
                        // 退出群组
                        mViewModel?.outFamily(binder.bean.familyId, binder)
                    }
                }
            }
        }
    }

    /**
     * 全部tab_各分类结果总数和查看更多点击事件
     */
    private fun onTypeCountBinderClickListener(view: View, binder: SearchResultAllTypeCountBinder) {
        when (view.id) {
            R.id.mItemSearchResultAllTypeTotalCountTv -> {
                // 跳转到相应分类tab下
                LiveEventBus.get(SEARCH_RESULT_TYPE_TAB)
                    .post(
                        com.kotlin.android.app.router.liveevent.event.SearchResultTypeTabState(
                            binder.searchType
                        )
                    )
            }
        }
    }

    override fun destroyView() {

    }

    /**
     * 获取搜索类型
     */
    fun getSearchType(): Long {
        return mSearchType
    }

    /**
     * 获取搜索类型
     */
    fun getSortType(): Long? {
        return mSortType
    }

    /**
     * 按排序搜索
     */
    fun searchBySort(sortType: Long) {
        mSortType = sortType
        loadData()
    }

    /**
     * 点赞、取消点赞操作结果监听
     */
//    private fun likeUpObserve() {
//        mViewModel?.mCommPraiseUpUIState?.observe(this) {
//            it?.apply {
//                showOrHideProgressDialog(showLoading)
//
//                success?.apply {
//                    if (result.isSuccess()) {
//                        when (extend) {
//                            // 影评
//                            is SearchResultFilmCommentItemBinder -> {
//                                (extend as SearchResultFilmCommentItemBinder).praiseUpChanged()
//                            }
//                            // 帖子、日志
//                            is SearchResultPostItemBinder -> {
//                                (extend as SearchResultPostItemBinder).praiseUpChanged()
//                            }
//
//                        }
//                    } else {
//                        showToast(result.bizMsg.orEmpty())
//                    }
//                }
//
//                error?.apply {
//                    showToast(error)
//                }
//
//                netError?.apply {
//                    showToast(netError)
//                }
//            }
//        }
//    }

    /**
     * 点踩、取消点踩操作结果监听
     */
//    private fun likeDownObserve() {
//        mViewModel?.mCommPraiseDownUIState?.observe(this) {
//            it?.apply {
//                showOrHideProgressDialog(showLoading)
//
//                success?.apply {
//                    if (result.isSuccess()) {
//                        when (extend) {
//                            // 影评
//                            is SearchResultFilmCommentItemBinder -> {
//                                (extend as SearchResultFilmCommentItemBinder).praiseDownChanged()
//                            }
//                        }
//                    } else {
//                        showToast(result.bizMsg.orEmpty())
//                    }
//                }
//
//                error?.apply {
//                    showToast(error)
//                }
//
//                netError?.apply {
//                    showToast(netError)
//                }
//            }
//        }
//    }

    /**
     * 影评相关的点击事件
     */
//    private fun onFilmCommentBinderClickListener(view: View, binder: SearchResultFilmCommentItemBinder) {
//        when (view.id) {
//            R.id.mItemSearchResultFilmCommentLikeTv -> {
//                //点赞、取消点赞
//                afterLogin {
//                    mViewModel?.praiseUp(
//                        action = if (binder.bean.isLikeUp) CommConstant.PRAISE_ACTION_CANCEL else CommConstant.PRAISE_ACTION_SUPPORT,
//                        objType = CommConstant.PRAISE_OBJ_TYPE_FILM_COMMENT,
//                        objId = binder.bean.filmCommentId,
//                        extend = binder
//                    )
//                }
//            }
//            R.id.mItemSearchResultFilmCommentDislikeTv -> {
//                //点踩、取消点踩
//                afterLogin {
//                    mViewModel?.praiseDown(
//                        action = if (binder.bean.isLikeDown) CommConstant.PRAISE_ACTION_CANCEL else CommConstant.PRAISE_ACTION_SUPPORT,
//                        objType = CommConstant.PRAISE_OBJ_TYPE_FILM_COMMENT,
//                        objId = binder.bean.filmCommentId,
//                        extend = binder
//                    )
//                }
//            }
//        }
//    }

    /**
     * 帖子、日志相关点击事件
     */
//    private fun onPostBinderClickListener(view: View, binder: SearchResultPostItemBinder) {
//        when (view.id) {
//            R.id.mItemSearchResultPostLikeTv -> {
//                //点赞、取消点赞
//                afterLogin {
//                    mViewModel?.praiseUp(
//                        action = if (binder.bean.isLikeUp) CommConstant.PRAISE_ACTION_CANCEL else CommConstant.PRAISE_ACTION_SUPPORT,
//                        objType = binder.bean.objType,
//                        objId = binder.bean.objId,
//                        extend = binder
//                    )
//                }
//            }
//        }
//    }

}