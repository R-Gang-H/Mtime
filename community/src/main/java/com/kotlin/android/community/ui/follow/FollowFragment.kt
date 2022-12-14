package com.kotlin.android.community.ui.follow

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kotlin.android.community.R
import com.kotlin.android.community.databinding.FragCommunityFollowListBinding
import com.kotlin.android.community.post.component.item.adapter.CommunityPostBinder
import com.kotlin.android.community.ui.follow.widget.DividerItemDecoration
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.liveevent.LOGIN_STATE
import com.kotlin.android.share.entity.ShareEntity
import com.kotlin.android.share.ext.showShareDialog
import com.kotlin.android.user.afterLogin
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.frag_community_follow_list.*
import kotlinx.android.synthetic.main.frag_community_follow_list.mMultiStateView
import kotlinx.android.synthetic.main.frag_community_follow_list.mRefreshLayout

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/17
 */
class FollowFragment: BaseVMFragment<FollowViewModel, FragCommunityFollowListBinding>(),
        OnRefreshLoadMoreListener, MultiStateView.MultiStateListener {

    override fun initVM(): FollowViewModel {
        return viewModels<FollowViewModel>().value
    }

    private lateinit var mAdapter: MultiTypeAdapter
    private var mIsInitData = false

    override fun initView() {
        val dividerItemDecoration = DividerItemDecoration(1, R.color.color_0f303a47)
        dividerItemDecoration.offsetStart = 15
        dividerItemDecoration.offsetEnd = 15
        mCommunityFollowRv.addItemDecoration(dividerItemDecoration)

        mRefreshLayout.setOnRefreshLoadMoreListener(this)
        mMultiStateView.setMultiStateListener(this)
        mAdapter = createMultiTypeAdapter(mCommunityFollowRv)
        mAdapter.setOnClickListener(::onBinderClick)
    }

    override fun initData() {
    }

    override fun onResume() {
        super.onResume()
        //????????????
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

        //??????????????????
        registerPageUIStateObserve()

        //????????????????????????????????????
        registerWantToSeeObserve()

        //???????????????????????????????????????
        registerPraiseUpObserve()

        //???????????????????????????????????????
        registerPraiseDownObserve()

        //??????pk????????????????????????
        registerVoteObserve()

        //??????????????????????????????
        registerShareObserve()
    }

    //??????????????????
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
                        mRefreshLayout.finishRefreshWithNoMoreData()
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

    //????????????????????????????????????
    private fun registerWantToSeeObserve() {
        mViewModel?.mCommWantToSeeUIState?.observe(this) {
            it.apply {
                if (showLoading) {
                    showProgressDialog()
                } else {
                    dismissProgressDialog()
                }

                success?.apply {
                    if (result.isSuccess()) {
                        extend.wantToSeeChanged()
                    } else {
                        showToast(result.statusMsg)
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

    //?????????????????????
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
                        if (addedValue > 100L) {
                            //?????????????????????
                            extend.commentPraiseUpChanged()
                        } else {
                            extend.praiseUpChanged()
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

    //???????????????????????????????????????
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
                        if (addedValue > 100L) {
                            //?????????????????????
                            extend.commentPraiseDownChanged()
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

    //??????pk????????????????????????
    private fun registerVoteObserve() {
        mViewModel?.mCommVoteUIState?.observe(this) {
            it.apply {
                if (showLoading) {
                    showProgressDialog()
                } else {
                    dismissProgressDialog()
                }

                success?.apply {
                    if (result.isSuccess()) {
                        extend.voteChanged(addedValue)
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

    //??????????????????????????????
    private fun registerShareObserve() {
        mViewModel?.mCommShareUIState?.observe(this) {
            it.apply {
                if (showLoading) {
                    showProgressDialog()
                } else {
                    dismissProgressDialog()
                }

                success?.apply {
                    showShareDialog(ShareEntity.build(this))
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
                    mRefreshLayout.resetNoMoreData()
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
     * ???MultiTypeBinder???????????????????????????
     * ??????????????????Adapter??????
     */
    private fun onBinderClick(view: View, binder: MultiTypeBinder<*>) {
        when(binder) {
            is CommunityPostBinder -> {
                //??????????????????????????????
                onCommunityPostBinderClickListener(view, binder)
            }
        }
    }

    //??????????????????????????????
    private fun onCommunityPostBinderClickListener(
            view: View,
            binder: CommunityPostBinder
    ) {
        when (view.id) {
            R.id.mCommunityPostLikeTv -> {
                //????????????????????????????????????
                onPraiseUpBtnClick(
                        isLike = binder.post.isLike,
                        objType = binder.post.praiseObjType,
                        objId = binder.post.id,
                        binder = binder
                )
            }
            R.id.mCommunityPostMovieBtnFl -> {
                //????????? //??????
                onWanToSeeBtnClick(
                        movieId = binder.post.movieId,
                        btnState = binder.post.movieBtnState,
                        binder = binder
                )
            }
            R.id.mCommunityPostCommentLikeTv -> {
                //??????????????????????????????????????????
                binder.mCurClickComment?.apply {
                    onPraiseUpBtnClick(
                            isLike = isLike,
                            objType = getPraiseType(binder.post.type),
                            objId = id,
                            binder = binder
                    )
                }
            }
            R.id.mCommunityPostCommentDislikeTv -> {
                //??????????????????????????????????????????
                binder.mCurClickComment?.apply {
                    onPraiseDownBtnClick(
                            isDislike = isDislike,
                            objType = getPraiseType(binder.post.type),
                            objId = id,
                            binder = binder
                    )
                }
            }
            R.id.mCommunityPostPkPositiveBtn -> {
                //??????PK????????????
                onVoteClick(
                        objId = binder.post.id,
                        voteId = binder.post.opinions?.get(0)?.id ?: 0,
                        binder = binder
                )
            }
            R.id.mCommunityPostPkNegativeBtn -> {
                //??????PK????????????
                onVoteClick(
                        objId = binder.post.id,
                        voteId = binder.post.opinions?.get(1)?.id ?: 0,
                        binder = binder
                )
            }
            R.id.mCommunityPostShareIv -> {
                binder.post.let {
                    mViewModel?.getShareInfo(
                            type = it.getShareType(),
                            relateId = it.id
                    )
                }
            }
        }
    }

    /**
     * ??????PK????????????
     */
    private fun onVoteClick(
            objId: Long,
            voteId: Long,
            binder: CommunityPostBinder) {
        afterLogin {
            mViewModel?.communityVote(
                    objType = 1L,
                    objId = objId,
                    voteId = voteId,
                    extend = binder
            )
        }
    }

    /**
     * ?????????????????????
     */
    private fun onPraiseDownBtnClick(
            isDislike: Boolean,
            objType: Long,
            objId: Long,
            binder: CommunityPostBinder
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

    /**
     * ?????????????????????
     */
    private fun onPraiseUpBtnClick(
            isLike: Boolean,
            objType: Long,
            objId: Long,
            binder: CommunityPostBinder
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
     * ?????????/???????????????????????????
     */
    private fun onWanToSeeBtnClick(
            movieId: Long,
            btnState: Long,
            binder: CommunityPostBinder
    ) {
        afterLogin {
            mViewModel?.getMovieWantToSee(
                    movieId = movieId,
                    flag = if (btnState == CommConstant.MOVIE_BTN_STATE_WANT_SEE)
                        CommConstant.MOVIE_WANT_SEE_FLAG else CommConstant.MOVIE_CANCEL_WANT_SEE_FLAG,
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